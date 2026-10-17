package net.mcreator.manfromthefogport.client;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.manfromthefogport.init.ManFromTheFogPortModEntities;
import net.mcreator.manfromthefogport.client.renderer.TheManRenderer;
import net.mcreator.manfromthefogport.client.renderer.ManAgressiveRenderer;

@EventBusSubscriber(Dist.CLIENT)
public class AnimatedEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ManFromTheFogPortModEntities.THE_MAN.get(), TheManRenderer::new);
		event.registerEntityRenderer(ManFromTheFogPortModEntities.MAN_AGRESSIVE.get(), ManAgressiveRenderer::new);
	}
}