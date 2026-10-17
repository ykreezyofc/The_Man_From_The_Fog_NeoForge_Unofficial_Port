package net.mcreator.manfromthefogport.entity;

import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import net.minecraft.world.entity.ai.goal.FloatGoal;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
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

import net.mcreator.manfromthefogport.procedures.TheManOnInitialEntitySpawnProcedure;
import net.mcreator.manfromthefogport.procedures.TheManOnEntityTickUpdateProcedure;
import net.mcreator.manfromthefogport.procedures.TheManEntityDiesProcedure;
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

public class TheManEntity extends Monster implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(TheManEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(TheManEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(TheManEntity.class, EntityDataSerializers.STRING);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";
	private String procedureControllerRequester = "";

	public TheManEntity(EntityType<TheManEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
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

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, (float) 300));
		this.goalSelector.addGoal(2, new FloatGoal(this));
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.generic.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.generic.death"));
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource damagesource, float amount) {
		if (damagesource.is(DamageTypes.LIGHTNING_BOLT))
			return false;
		return super.hurtServer(level, damagesource, amount);
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
		TheManEntityDiesProcedure.execute(this.level());
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData livingdata) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
		TheManOnInitialEntitySpawnProcedure.execute(world, this.getX(), this.getY(), this.getZ());
		return retval;
	}

	@Override
	public void addAdditionalSaveData(ValueOutput valueOutput) {
		super.addAdditionalSaveData(valueOutput);
		valueOutput.putString("Texture", this.getTexture());
		valueOutput.putString("AnimationProcedure", this.entityData.get(ANIMATION));
	}

	@Override
	public void readAdditionalSaveData(ValueInput valueInput) {
		super.readAdditionalSaveData(valueInput);
		this.setTexture(valueInput.getStringOr("Texture", "stalker"));
		this.entityData.set(ANIMATION, valueInput.getStringOr("AnimationProcedure", "undefined"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
		TheManOnEntityTickUpdateProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
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
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0);
		builder = builder.add(Attributes.MAX_HEALTH, 300);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 10);
		builder = builder.add(Attributes.FOLLOW_RANGE, 300);
		builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
		return builder;
	}

	private PlayState movementPredicate(AnimationTest<TheManEntity> event) {
		if (this.animationprocedure.equals("empty")) {
			if (event.isMoving()

					&& !this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("crawl_2"));
			}
			if (this.isInWater()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("swim"));
			}
			if (this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("crawl_2"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("stare"));
		}
		return PlayState.STOP;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationTest<TheManEntity> event) {
		if (!animationprocedure.equals("empty") && hasPlayOnceAnimationFinished(event.controller()) || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
			if (!this.animationprocedure.equals(prevAnim))
				event.controller().reset();
			event.controller().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
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

	private static boolean hasPlayOnceAnimationFinished(AnimationController<?> controller) {
		if (!controller.hasAnimationFinished())
			return false;
		AnimationPoint point = controller.getCurrentAnimationPoint();
		if (point == null)
			return true;
		LoopType loopType = point.loopType() == LoopType.DEFAULT ? point.animation().loopType() : point.loopType();
		return loopType != LoopType.HOLD_ON_LAST_FRAME && loopType != LoopType.LOOP;
	}

	/**
	 * Custom death animation length. XP/loot are already dropped by LivingEntity#die
	 * via dropAllDeathLoot -> dropExperience(ServerLevel, Entity); do not re-drop here.
	 */
	@Override
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 20) {
			this.remove(TheManEntity.RemovalReason.KILLED);
		}
	}

	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
		this.animationprocedure = animation;
		this.procedureControllerRequester = "";
	}

	/**
	 * Moves whatever procedures wrote into the synched controller slots over to the
	 * fields the AnimationControllers read, the same way EntityAnimationFactory
	 * handles the built-in "procedure" controller.
	 */
	public void applySyncedControllerAnimations() {
	}

	/**
	 * Plays an animation on a named controller. A blank or unknown controller name
	 * falls back to the built-in "procedure" controller, so procedures written
	 * before custom controllers existed keep behaving exactly as before.
	 *
	 * <p>Stopping ("empty") an unknown controller name only stops the "procedure"
	 * controller if that same name started what it is playing. Otherwise a tick procedure
	 * that keeps stopping a name nothing else uses would also cancel every other
	 * procedure animation, such as spawn or right-click ones.
	 */
	public void setControllerAnimation(String controller, String animation) {
		if (controller == null || controller.isBlank()) {
			this.setAnimation(animation);
			return;
		}
		switch (controller) {
			default -> {
				if ("empty".equals(animation) && !controller.equals(this.procedureControllerRequester))
					return;
				this.setAnimation(animation);
				this.procedureControllerRequester = controller;
			}
		}
	}

	/** Currently playing animation on a named controller, or "empty" if idle. */
	public String getControllerAnimation(String controller) {
		if (controller == null || controller.isBlank())
			return this.animationprocedure;
		return switch (controller) {
			default -> this.animationprocedure;
		};
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>("movement", 4, this::movementPredicate).setParticleKeyframeHandler(this::spawnKeyframeParticle));
		data.add(new AnimationController<>("procedure", 4, this::procedurePredicate).setParticleKeyframeHandler(this::spawnKeyframeParticle));
	}

	private static final Set<String> UNKNOWN_PARTICLE_EFFECTS = ConcurrentHashMap.newKeySet();

	private void spawnKeyframeParticle(KeyFrameEvent<TheManEntity, ParticleKeyframeData> event) {
		String effect = event.keyframeData().getEffect();
		Identifier id = Identifier.tryParse(effect);
		if (id != null && BuiltInRegistries.PARTICLE_TYPE.getValue(id) instanceof SimpleParticleType particle)
			this.level().addParticle(particle, this.getX(), this.getY() + this.getBbHeight() * 0.5, this.getZ(), 0, 0, 0);
		else if (UNKNOWN_PARTICLE_EFFECTS.add(effect))
			ManFromTheFogPortMod.LOGGER.warn("Particle keyframe effect \"{}\" in TheMan animations is not a registered particle id, so nothing is spawned", effect);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}