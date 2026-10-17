package net.mcreator.manfromthefogport.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;

import net.mcreator.manfromthefogport.network.ManFromTheFogPortModVariables;

public class TheManOnInitialEntitySpawnProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		ManFromTheFogPortModVariables.MapVariables.get(world).amount = 1;
		ManFromTheFogPortModVariables.MapVariables.get(world).sound = Mth.nextInt(RandomSource.create(), 1, 5);
		ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
		if (ManFromTheFogPortModVariables.MapVariables.get(world).sound == 1) {
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:spawn1")), SoundSource.MASTER, 10, 1);
				} else {
					_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:spawn1")), SoundSource.MASTER, 10, 1, false);
				}
			}
		}
		if (ManFromTheFogPortModVariables.MapVariables.get(world).sound == 2) {
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:spawn2")), SoundSource.MASTER, 10, 1);
				} else {
					_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:spawn2")), SoundSource.MASTER, 10, 1, false);
				}
			}
		}
		if (ManFromTheFogPortModVariables.MapVariables.get(world).sound == 3) {
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:spawn3")), SoundSource.MASTER, 10, 1);
				} else {
					_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:spawn3")), SoundSource.MASTER, 10, 1, false);
				}
			}
		}
		if (ManFromTheFogPortModVariables.MapVariables.get(world).sound == 4) {
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:spawn4")), SoundSource.MASTER, 10, 1);
				} else {
					_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:spawn4")), SoundSource.MASTER, 10, 1, false);
				}
			}
		}
		if (ManFromTheFogPortModVariables.MapVariables.get(world).sound == 5) {
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:spawn5")), SoundSource.MASTER, 10, 1);
				} else {
					_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:spawn5")), SoundSource.MASTER, 10, 1, false);
				}
			}
		}
		ManFromTheFogPortModVariables.MapVariables.get(world).despawntimer = Mth.nextDouble(RandomSource.create(), 1, 2000);
		ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
	}
}