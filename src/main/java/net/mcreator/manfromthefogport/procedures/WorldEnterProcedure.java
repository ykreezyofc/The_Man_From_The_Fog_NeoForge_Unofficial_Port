package net.mcreator.manfromthefogport.procedures;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.manfromthefogport.network.ManFromTheFogPortModVariables;
import net.mcreator.manfromthefogport.ManFromTheFogPortMod;

import javax.annotation.Nullable;

@EventBusSubscriber
public class WorldEnterProcedure {
	@SubscribeEvent
	public static void onWorldLoad(net.neoforged.neoforge.event.level.LevelEvent.Load event) {
		execute(event, event.getLevel());
	}

	public static void execute(LevelAccessor world) {
		execute(null, world);
	}

	private static void execute(@Nullable Event event, LevelAccessor world) {
		ManFromTheFogPortModVariables.MapVariables.get(world).amount = 0;
		ManFromTheFogPortModVariables.MapVariables.get(world).worldload = 1;
		ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
		ManFromTheFogPortMod.queueServerWork(20, () -> {
			ManFromTheFogPortModVariables.MapVariables.get(world).worldload = 0;
			ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
		});
	}
}