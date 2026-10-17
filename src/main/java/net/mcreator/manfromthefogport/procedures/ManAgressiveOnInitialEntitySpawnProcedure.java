package net.mcreator.manfromthefogport.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import net.mcreator.manfromthefogport.network.ManFromTheFogPortModVariables;
import net.mcreator.manfromthefogport.ManFromTheFogPortMod;

public class ManAgressiveOnInitialEntitySpawnProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		ManFromTheFogPortModVariables.MapVariables.get(world).music = 0;
		ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
		if (world instanceof ServerLevel _level) {
			Entity lightning = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse("minecraft:lightning_bolt")).create(_level, EntitySpawnReason.TRIGGERED);
			if (lightning != null) {
				lightning.snapTo(Vec3.atBottomCenterOf(BlockPos.containing(x, y, z)));
				_level.addFreshEntity(lightning);
			}
		}
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:spotted")), SoundSource.MASTER, 10, 1);
			} else {
				_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:spotted")), SoundSource.MASTER, 10, 1, false);
			}
		}
		ManFromTheFogPortModVariables.MapVariables.get(world).despawn = 0;
		ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
		ManFromTheFogPortMod.queueServerWork((int) Mth.nextDouble(RandomSource.create(), 400, 1000), () -> {
			ManFromTheFogPortModVariables.MapVariables.get(world).amount = 0;
			ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
			if (ManFromTheFogPortModVariables.MapVariables.get(world).despawn == 0) {
				{
					Entity _ent = entity;
					if (!_ent.level().isClientSide() && _ent.level().getServer() != null) {
						_ent.level().getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, _ent.position(), _ent.getRotationVector(), _ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null,
								LevelBasedPermissionSet.OWNER, _ent.getName().getString(), _ent.getDisplayName(), _ent.level().getServer(), _ent), "stopsound @a master man_from_the_fog_port:chasedv2");
					}
				}
				if (world instanceof ServerLevel _level) {
					Entity lightning = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse("minecraft:lightning_bolt")).create(_level, EntitySpawnReason.TRIGGERED);
					if (lightning != null) {
						lightning.snapTo(Vec3.atBottomCenterOf(BlockPos.containing(entity.getX(), entity.getY(), entity.getZ())));
						_level.addFreshEntity(lightning);
					}
				}
				if (!entity.level().isClientSide())
					entity.discard();
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:reversed-version-2")), SoundSource.MASTER, 30, 1);
					} else {
						_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:reversed-version-2")), SoundSource.MASTER, 30, 1, false);
					}
				}
			}
		});
	}
}