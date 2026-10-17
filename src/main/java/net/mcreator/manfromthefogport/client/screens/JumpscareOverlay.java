package net.mcreator.manfromthefogport.client.screens;

import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.Minecraft;

import net.mcreator.manfromthefogport.procedures.JumpscareDisplayOverlayIngameProcedure;

@EventBusSubscriber(Dist.CLIENT)
public class JumpscareOverlay {
	private static final Identifier BACKGROUND = Identifier.parse("man_from_the_fog_port:textures/screens/overlay.png");

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getGuiGraphics().guiWidth();
		int h = event.getGuiGraphics().guiHeight();
		Level world = null;
		double x = 0;
		double y = 0;
		double z = 0;
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
			world = entity.level();
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
		}
		if (JumpscareDisplayOverlayIngameProcedure.execute(world)) {
			event.getGuiGraphics().blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, 0, 0, 0, 0, w, h, w, h);
		}
	}
}