package net.mcreator.manfromthefogport.procedures;

import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import net.mcreator.manfromthefogport.network.ManFromTheFogPortModVariables;
import net.mcreator.manfromthefogport.entity.ManAgressiveEntity;
import net.mcreator.manfromthefogport.ManFromTheFogPortMod;

import javax.annotation.Nullable;

import java.util.Comparator;

@EventBusSubscriber
public class ManKillsPlayerProcedure {
	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity());
		}
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player && !world.getEntitiesOfClass(ManAgressiveEntity.class, new AABB(Vec3.ZERO, Vec3.ZERO).move(new Vec3(x, y, z)).inflate(60 / 2d), e -> true).isEmpty()) {
			ManFromTheFogPortModVariables.MapVariables.get(world).despawn = 1;
			ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
			if (!(findEntityInWorldRange(world, ManAgressiveEntity.class, x, y, z, 60)).level().isClientSide())
				(findEntityInWorldRange(world, ManAgressiveEntity.class, x, y, z, 60)).discard();
			ManFromTheFogPortModVariables.MapVariables.get(world).amount = 0;
			ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
			{
				Entity _ent = entity;
				if (!_ent.level().isClientSide() && _ent.level().getServer() != null) {
					_ent.level().getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, _ent.position(), _ent.getRotationVector(), _ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null,
							LevelBasedPermissionSet.OWNER, _ent.getName().getString(), _ent.getDisplayName(), _ent.level().getServer(), _ent), "stopsound @a master man_from_the_fog_port:chasedv2");
				}
			}
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:jumpscare")), SoundSource.MASTER, 10, 1);
				} else {
					_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("man_from_the_fog_port:jumpscare")), SoundSource.MASTER, 10, 1, false);
				}
			}
			ManFromTheFogPortModVariables.MapVariables.get(world).jumpscare = 1;
			ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
			ManFromTheFogPortMod.queueServerWork(20, () -> {
				ManFromTheFogPortModVariables.MapVariables.get(world).jumpscare = 0;
				ManFromTheFogPortModVariables.MapVariables.get(world).markSyncDirty();
			});
		}
	}

	private static Entity findEntityInWorldRange(LevelAccessor world, Class<? extends Entity> clazz, double x, double y, double z, double range) {
		return (Entity) world.getEntitiesOfClass(clazz, AABB.ofSize(new Vec3(x, y, z), range, range, range), e -> true).stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(x, y, z))).findFirst().orElse(null);
	}
}