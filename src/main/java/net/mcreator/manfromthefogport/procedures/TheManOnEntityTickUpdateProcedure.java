package net.mcreator.manfromthefogport.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import net.mcreator.manfromthefogport.network.ManFromTheFogPortModVariables;
import net.mcreator.manfromthefogport.init.ManFromTheFogPortModEntities;
import net.mcreator.manfromthefogport.ManFromTheFogPortMod;

import java.util.Comparator;

public class TheManOnEntityTickUpdateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (ManFromTheFogPortModVariables.MapVariables.get(world).strike == 1) {
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = ManFromTheFogPortModEntities.MAN_AGRESSIVE.get().spawn(_level, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), EntitySpawnReason.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
			if (world instanceof ServerLevel _level)
				_level.getServer().getCommands().performPrefixedCommand(
						new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, _level, LevelBasedPermissionSet.OWNER, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
						"effect give @a minecraft:darkness 5");
			if (!entity.level().isClientSide())
				entity.discard();
			ManFromTheFogPortModVariables.MapVariables.get(world).strike = 0;
			ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
		}
		if ((findEntityInWorldRange(world, Player.class, x, y, z, 20)) instanceof Player _plr7 && _plr7.gameMode() == GameType.SURVIVAL) {
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:reversed-version-2")), SoundSource.MASTER, 10, 1);
				} else {
					_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:reversed-version-2")), SoundSource.MASTER, 10, 1, false);
				}
			}
			if (world instanceof ServerLevel _level) {
				Entity lightning = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse("minecraft:lightning_bolt")).create(_level, EntitySpawnReason.TRIGGERED);
				if (lightning != null) {
					lightning.snapTo(Vec3.atBottomCenterOf(BlockPos.containing(x, y, z)));
					_level.addFreshEntity(lightning);
				}
			}
			if (!entity.level().isClientSide())
				entity.discard();
			ManFromTheFogPortModVariables.MapVariables.get(world).amount = 0;
			ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
		}
		if (!world.getEntitiesOfClass(Player.class, new AABB(Vec3.ZERO, Vec3.ZERO).move(new Vec3(x, y, z)).inflate(60 / 2d), e -> true).isEmpty()) {
			ManFromTheFogPortModVariables.MapVariables.get(world).despawnScheduled = 0;
			ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
		} else {
			if (ManFromTheFogPortModVariables.MapVariables.get(world).despawnScheduled == 0) {
				ManFromTheFogPortModVariables.MapVariables.get(world).despawnScheduled = 1;
				ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
				ManFromTheFogPortMod.queueServerWork((int) ManFromTheFogPortModVariables.MapVariables.get(world).despawntimer, () -> {
					if (ManFromTheFogPortModVariables.MapVariables.get(world).despawnScheduled == 1) {
						if (!entity.level().isClientSide())
							entity.discard();
						ManFromTheFogPortModVariables.MapVariables.get(world).amount = 0;
						ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
					}
				});
			}
		}
		if (ManFromTheFogPortModVariables.MapVariables.get(world).worldload == 1) {
			if (!entity.level().isClientSide())
				entity.discard();
		}
	}

	private static Entity findEntityInWorldRange(LevelAccessor world, Class<? extends Entity> clazz, double x, double y, double z, double range) {
		return (Entity) world.getEntitiesOfClass(clazz, AABB.ofSize(new Vec3(x, y, z), range, range, range), e -> true).stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(x, y, z))).findFirst().orElse(null);
	}
}