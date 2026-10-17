
package net.mcreator.manfromthefogport.entity;

import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import net.minecraft.world.entity.ai.goal.FloatGoal;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.*;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.SimpleParticleType;

import net.mcreator.manfromthefogport.procedures.TheManEntityDiesProcedure;
import net.mcreator.manfromthefogport.procedures.ManAgressiveOnInitialEntitySpawnProcedure;
import net.mcreator.manfromthefogport.procedures.ManAgressiveOnEntityTickUpdateProcedure;
import net.mcreator.manfromthefogport.init.ManFromTheFogPortModItems;
import net.mcreator.manfromthefogport.ManFromTheFogPortMod;

import javax.annotation.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

import com.geckolib.util.GeckoLibUtil;
import com.geckolib.cache.animation.keyframeevent.ParticleKeyframeData;
import com.geckolib.animation.state.KeyFrameEvent;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.state.AnimationPoint;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.object.LoopType;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.AnimationController;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.GeoEntity;

public class ManAgressiveEntity extends Monster implements GeoEntity {

    // =========================================================
    // CLIMBING SYSTEM - ported from the original 1.21.4 entity
    // =========================================================

    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID =
            SynchedEntityData.defineId(
                    ManAgressiveEntity.class,
                    EntityDataSerializers.BYTE
            );

    private int climbTime = 0;
    private int stuckTime = 0;

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new WallClimberNavigation(this, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            boolean hasTarget = this.getTarget() != null;

            if (hasTarget) {
                double dist = this.distanceTo(this.getTarget());

                boolean noPath = this.getNavigation().isDone();
                boolean stuck = this.horizontalCollision;

                if (noPath && stuck && dist > 3.0) {
                    this.stuckTime++;
                } else {
                    this.stuckTime = 0;
                }

                if (this.stuckTime > 5) {
                    this.climbTime = 10;
                }
            } else {
                this.stuckTime = 0;
            }

            if (this.climbTime > 0) {
                this.climbTime--;
                this.setClimbing(true);
            } else {
                this.setClimbing(false);
            }

            if (this.isClimbing()) {
                double speed = 0.40;

                if (this.horizontalCollision
                        && this.getDeltaMovement().y < 0.45) {
                    this.setDeltaMovement(
                            0,
                            this.getDeltaMovement().y + speed,
                            0
                    );
                }

                // Original ledge boost
                if (this.horizontalCollision
                        && this.getTarget() != null
                        && this.getTarget().getY() > this.getY()) {
                    this.setDeltaMovement(
                            this.getDeltaMovement().add(0, 0.02, 0)
                    );
                }
            }
        }
    }

    public boolean onClimbable() {
        return this.isClimbing();
    }

    public boolean isClimbing() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setClimbing(boolean climbing) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);

        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }

        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    // =========================================================
    // EXISTING ENTITY DATA AND GECKOLIB FIELDS
    // =========================================================

    public static final EntityDataAccessor<Boolean> SHOOT =
            SynchedEntityData.defineId(
                    ManAgressiveEntity.class,
                    EntityDataSerializers.BOOLEAN
            );

    public static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(
                    ManAgressiveEntity.class,
                    EntityDataSerializers.STRING
            );

    public static final EntityDataAccessor<String> TEXTURE =
            SynchedEntityData.defineId(
                    ManAgressiveEntity.class,
                    EntityDataSerializers.STRING
            );

    private final AnimatableInstanceCache cache =
            GeckoLibUtil.createInstanceCache(this);

    private boolean swinging;
    private boolean lastloop;
    private long lastSwing;

    public String animationprocedure = "empty";
    private String procedureControllerRequester = "";

    public ManAgressiveEntity(EntityType<ManAgressiveEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        // Climbing flag
        builder.define(DATA_FLAGS_ID, (byte) 0);

        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, "stalker");
    }

    public void setTexture(String texture) {
        this.entityData.set(TEXTURE, texture);
    }

    public String getTexture() {
        return this.entityData.get(TEXTURE);
    }

    // =========================================================
    // EXISTING AI
    // =========================================================

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true) {
            @Override
            protected boolean canPerformAttack(LivingEntity entity) {
                return this.isTimeToAttack()
                        && this.mob.distanceToSqr(entity)
                        < (this.mob.getBbWidth() * this.mob.getBbWidth()
                        + entity.getBbWidth())
                        && this.mob.getSensing().hasLineOfSight(entity);
            }
        });

        this.targetSelector.addGoal(
                2,
                new HurtByTargetGoal(this).setAlertOthers()
        );

        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(
                5,
                new NearestAttackableTargetGoal<>(
                        this,
                        Player.class,
                        false,
                        false
                )
        );
        this.goalSelector.addGoal(6, new FloatGoal(this));

    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    // =========================================================
    // EXISTING LOOT, SOUNDS AND DAMAGE
    // =========================================================

    @Override
    protected void dropCustomDeathLoot(
            ServerLevel serverLevel,
            DamageSource source,
            boolean recentlyHitIn
    ) {
        super.dropCustomDeathLoot(serverLevel, source, recentlyHitIn);

        this.spawnAtLocation(
                serverLevel,
                new ItemStack(ManFromTheFogPortModItems.TEAR_OF_THE_MAN.get())
        );
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return BuiltInRegistries.SOUND_EVENT.getValue(
                Identifier.parse("entity.generic.hurt")
        );
    }

    @Override
    public SoundEvent getDeathSound() {
        return BuiltInRegistries.SOUND_EVENT.getValue(
                Identifier.parse("entity.generic.death")
        );
    }

    @Override
    public boolean hurtServer(
            ServerLevel level,
            DamageSource damagesource,
            float amount
    ) {
        if (damagesource.is(DamageTypes.IN_FIRE))
            return false;

        if (damagesource.is(DamageTypes.LIGHTNING_BOLT))
            return false;

        return super.hurtServer(level, damagesource, amount);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        TheManEntityDiesProcedure.execute(this.level());
    }

    // =========================================================
    // EXISTING SPAWN AND SAVE DATA
    // =========================================================

    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor world,
            DifficultyInstance difficulty,
            EntitySpawnReason reason,
            @Nullable SpawnGroupData livingdata
    ) {
        SpawnGroupData retval =
                super.finalizeSpawn(world, difficulty, reason, livingdata);

        ManAgressiveOnInitialEntitySpawnProcedure.execute(
                world,
                this.getX(),
                this.getY(),
                this.getZ(),
                this
        );

        return retval;
    }

    @Override
    public void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);

        valueOutput.putString("Texture", this.getTexture());
        valueOutput.putString(
                "AnimationProcedure",
                this.entityData.get(ANIMATION)
        );
    }

    @Override
    public void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);

        this.setTexture(valueInput.getStringOr("Texture", "stalker"));

        this.entityData.set(
                ANIMATION,
                valueInput.getStringOr("AnimationProcedure", "undefined")
        );
    }

    @Override
    public void baseTick() {
        super.baseTick();

        ManAgressiveOnEntityTickUpdateProcedure.execute(this.level(), this);

        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        return super.getDefaultDimensions(pose).scale(1f);
    }

    public static void init(RegisterSpawnPlacementsEvent event) {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();

        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.5);
        builder = builder.add(Attributes.MAX_HEALTH, 300);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 14);
        builder = builder.add(Attributes.FOLLOW_RANGE, 300);
        builder = builder.add(Attributes.STEP_HEIGHT, 0.6);

        return builder;
    }

    // =========================================================
    // GECKOLIB MOVEMENT ANIMATION
    // =========================================================

    private PlayState movementPredicate(AnimationTest<ManAgressiveEntity> event) {

        // Climbing animation from the original 1.21.4 entity
        if (this.isClimbing()) {
            return event.setAndContinue(
                    RawAnimation.begin().thenLoop("climb")
            );
        }

        if (this.animationprocedure.equals("empty")) {
            if (event.isMoving() && !this.isSprinting()) {
                return event.setAndContinue(
                        RawAnimation.begin().thenLoop("crawl_2")
                );
            }

            if (this.isInWater()) {
                return event.setAndContinue(
                        RawAnimation.begin().thenLoop("swim")
                );
            }

            if (this.isSprinting()) {
                return event.setAndContinue(
                        RawAnimation.begin().thenLoop("crawl_2")
                );
            }

            return event.setAndContinue(
                    RawAnimation.begin().thenLoop("stare")
            );
        }

        return PlayState.STOP;
    }

    // =========================================================
    // EXISTING PROCEDURE ANIMATION
    // =========================================================

    String prevAnim = "empty";

    private PlayState procedurePredicate(AnimationTest<ManAgressiveEntity> event) {
        if ((!animationprocedure.equals("empty")
                && hasPlayOnceAnimationFinished(event.controller()))
                || (!this.animationprocedure.equals(prevAnim)
                && !this.animationprocedure.equals("empty"))) {

            if (!this.animationprocedure.equals(prevAnim))
                event.controller().reset();

            event.controller().setAnimation(
                    RawAnimation.begin().thenPlay(this.animationprocedure)
            );

            if (hasPlayOnceAnimationFinished(event.controller())) {
                this.animationprocedure = "empty";
                event.controller().reset();
            }

        } else if (animationprocedure.equals("empty")) {
            prevAnim = "empty";
            return PlayState.STOP;
        }

        prevAnim = this.animationprocedure;
        return PlayState.CONTINUE;
    }

    private static boolean hasPlayOnceAnimationFinished(
            AnimationController<?> controller
    ) {
        if (!controller.hasAnimationFinished())
            return false;

        AnimationPoint point = controller.getCurrentAnimationPoint();

        if (point == null)
            return true;

        LoopType loopType =
                point.loopType() == LoopType.DEFAULT
                        ? point.animation().loopType()
                        : point.loopType();

        return loopType != LoopType.HOLD_ON_LAST_FRAME
                && loopType != LoopType.LOOP;
    }

    // =========================================================
    // EXISTING DEATH ANIMATION
    // =========================================================

    @Override
    protected void tickDeath() {
        ++this.deathTime;

        if (this.deathTime == 20) {
            this.remove(ManAgressiveEntity.RemovalReason.KILLED);
        }
    }

    // =========================================================
    // EXISTING ANIMATION API
    // =========================================================

    public String getSyncedAnimation() {
        return this.entityData.get(ANIMATION);
    }

    public void setAnimation(String animation) {
        this.entityData.set(ANIMATION, animation);
        this.animationprocedure = animation;
        this.procedureControllerRequester = "";
    }

    public void applySyncedControllerAnimations() {
    }

    public void setControllerAnimation(String controller, String animation) {
        if (controller == null || controller.isBlank()) {
            this.setAnimation(animation);
            return;
        }

        switch (controller) {
            default -> {
                if ("empty".equals(animation)
                        && !controller.equals(this.procedureControllerRequester))
                    return;

                this.setAnimation(animation);
                this.procedureControllerRequester = controller;
            }
        }
    }

    public String getControllerAnimation(String controller) {
        if (controller == null || controller.isBlank())
            return this.animationprocedure;

        return switch (controller) {
            default -> this.animationprocedure;
        };
    }

    // =========================================================
    // GECKOLIB CONTROLLERS AND PARTICLES
    // =========================================================

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(
                new AnimationController<>(
                        "movement",
                        4,
                        this::movementPredicate
                ).setParticleKeyframeHandler(this::spawnKeyframeParticle)
        );

        data.add(
                new AnimationController<>(
                        "procedure",
                        4,
                        this::procedurePredicate
                ).setParticleKeyframeHandler(this::spawnKeyframeParticle)
        );
    }

    private static final Set<String> UNKNOWN_PARTICLE_EFFECTS =
            ConcurrentHashMap.newKeySet();

    private void spawnKeyframeParticle(
            KeyFrameEvent<ManAgressiveEntity, ParticleKeyframeData> event
    ) {
        String effect = event.keyframeData().getEffect();
        Identifier id = Identifier.tryParse(effect);

        if (id != null
                && BuiltInRegistries.PARTICLE_TYPE.getValue(id)
                instanceof SimpleParticleType particle) {

            this.level().addParticle(
                    particle,
                    this.getX(),
                    this.getY() + this.getBbHeight() * 0.5,
                    this.getZ(),
                    0,
                    0,
                    0
            );

        } else if (UNKNOWN_PARTICLE_EFFECTS.add(effect)) {
            ManFromTheFogPortMod.LOGGER.warn(
                    "Particle keyframe effect \"{}\" in ManAgressive animations "
                            + "is not a registered particle id, so nothing is spawned",
                    effect
            );
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
