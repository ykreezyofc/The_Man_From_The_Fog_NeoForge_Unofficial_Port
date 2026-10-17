package net.mcreator.manfromthefogport.init;

import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.manfromthefogport.entity.TheManEntity;
import net.mcreator.manfromthefogport.entity.ManAgressiveEntity;

@EventBusSubscriber(value = Dist.CLIENT)
public class EntityAnimationFactory {
	@SubscribeEvent
	public static void onEntityTick(EntityTickEvent.Pre event) {
		if (event == null || event.getEntity() == null)
			return;
		if (event.getEntity() instanceof TheManEntity syncable) {
			String animation = syncable.getSyncedAnimation();
			if (!animation.equals("undefined")) {
				syncable.getEntityData().set(TheManEntity.ANIMATION, "undefined");
				syncable.animationprocedure = animation;
			}
			syncable.applySyncedControllerAnimations();
		}
		if (event.getEntity() instanceof ManAgressiveEntity syncable) {
			String animation = syncable.getSyncedAnimation();
			if (!animation.equals("undefined")) {
				syncable.getEntityData().set(ManAgressiveEntity.ANIMATION, "undefined");
				syncable.animationprocedure = animation;
			}
			syncable.applySyncedControllerAnimations();
		}
	}
}