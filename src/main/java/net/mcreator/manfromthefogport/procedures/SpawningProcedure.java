package net.mcreator.manfromthefogport.procedures;

import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import net.mcreator.manfromthefogport.network.ManFromTheFogPortModVariables;
import net.mcreator.manfromthefogport.init.ManFromTheFogPortModEntities;

import javax.annotation.Nullable;

@EventBusSubscriber
public class SpawningProcedure {
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		execute(event, event.getEntity().level(), event.getEntity());
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (!(world instanceof Level _lvl0 && _lvl0.isBrightOutside()) && ManFromTheFogPortModVariables.MapVariables.get(world).amount == 0) {
			ManFromTheFogPortModVariables.MapVariables.get(world).chance = Mth.nextInt(RandomSource.create(), 1, 5000);
			ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
			if (ManFromTheFogPortModVariables.MapVariables.get(world).chance == 1) {
				ManFromTheFogPortModVariables.MapVariables.get(world).positionx = Mth.nextInt(RandomSource.create(), 1, 64);
				ManFromTheFogPortModVariables.MapVariables.get(world).amount = Mth.nextInt(RandomSource.create(), 1, 64);
				ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
				if (Mth.nextInt(RandomSource.create(), 0, 1) == 0) {
					ManFromTheFogPortModVariables.MapVariables.get(world).distance = 1;
					ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
				} else {
					ManFromTheFogPortModVariables.MapVariables.get(world).distance = -1;
					ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
				}
				if (Mth.nextInt(RandomSource.create(), 0, 1) == 0) {
					ManFromTheFogPortModVariables.MapVariables.get(world).distancez = 1;
					ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
				} else {
					ManFromTheFogPortModVariables.MapVariables.get(world).distancez = -1;
					ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
				}
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = ManFromTheFogPortModEntities.THE_MAN.get().spawn(_level,
							BlockPos.containing(entity.getX() + ManFromTheFogPortModVariables.MapVariables.get(world).positionx * ManFromTheFogPortModVariables.MapVariables.get(world).distance,
									world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (entity.getX() + ManFromTheFogPortModVariables.MapVariables.get(world).positionx * ManFromTheFogPortModVariables.MapVariables.get(world).distance),
											(int) (entity.getZ() + ManFromTheFogPortModVariables.MapVariables.get(world).amount * ManFromTheFogPortModVariables.MapVariables.get(world).distancez)),
									entity.getZ() + ManFromTheFogPortModVariables.MapVariables.get(world).amount * ManFromTheFogPortModVariables.MapVariables.get(world).distancez),
							EntitySpawnReason.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			}
		}
	}
}