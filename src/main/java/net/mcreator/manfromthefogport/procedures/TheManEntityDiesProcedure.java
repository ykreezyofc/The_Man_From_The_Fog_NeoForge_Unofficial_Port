package net.mcreator.manfromthefogport.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.manfromthefogport.network.ManFromTheFogPortModVariables;

public class TheManEntityDiesProcedure {
	public static void execute(LevelAccessor world) {
		ManFromTheFogPortModVariables.MapVariables.get(world).amount = 0;
		ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
	}
}