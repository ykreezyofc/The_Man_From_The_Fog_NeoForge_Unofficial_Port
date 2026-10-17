package net.mcreator.manfromthefogport.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.manfromthefogport.network.ManFromTheFogPortModVariables;

public class JumpscareDisplayOverlayIngameProcedure {
	public static boolean execute(LevelAccessor world) {
		if (ManFromTheFogPortModVariables.MapVariables.get(world).jumpscare == 1) {
			return true;
		}
		return false;
	}
}