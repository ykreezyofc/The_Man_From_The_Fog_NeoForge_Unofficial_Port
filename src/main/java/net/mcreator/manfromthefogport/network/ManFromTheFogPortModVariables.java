package net.mcreator.manfromthefogport.network;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;

import net.mcreator.manfromthefogport.ManFromTheFogPortMod;

@EventBusSubscriber
public class ManFromTheFogPortModVariables {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ManFromTheFogPortMod.MODID);

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		ManFromTheFogPortMod.addNetworkMessage(SavedDataSyncMessage.TYPE, SavedDataSyncMessage.STREAM_CODEC, SavedDataSyncMessage::handleData);
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			SavedData mapdata = MapVariables.get(player.level());
			SavedData worlddata = WorldVariables.get(player.level());
			if (mapdata != null)
				PacketDistributor.sendToPlayer(player, new SavedDataSyncMessage(0, mapdata));
			if (worlddata != null)
				PacketDistributor.sendToPlayer(player, new SavedDataSyncMessage(1, worlddata));
		}
	}

	@SubscribeEvent
	public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			SavedData worlddata = WorldVariables.get(player.level());
			if (worlddata != null)
				PacketDistributor.sendToPlayer(player, new SavedDataSyncMessage(1, worlddata));
		}
	}

	@SubscribeEvent
	public static void onWorldTick(LevelTickEvent.Post event) {
		if (event.getLevel() instanceof ServerLevel level) {
			WorldVariables worldVariables = WorldVariables.get(level);
			if (worldVariables._syncDirty) {
				PacketDistributor.sendToPlayersInDimension(level, new SavedDataSyncMessage(1, worldVariables));
				worldVariables._syncDirty = false;
			}
			MapVariables mapVariables = MapVariables.get(level);
			if (mapVariables._syncDirty) {
				PacketDistributor.sendToAllPlayers(new SavedDataSyncMessage(0, mapVariables));
				mapVariables._syncDirty = false;
			}
		}
	}

	public static class WorldVariables extends SavedData {
		public static final SavedDataType<WorldVariables> TYPE = new SavedDataType<>(Identifier.parse("man_from_the_fog_port:worldvars"), level -> new WorldVariables(), level -> CompoundTag.CODEC.xmap(tag -> {
			WorldVariables instance = new WorldVariables();
			instance.read(tag, level.registryAccess());
			return instance;
		}, instance -> instance.save(new CompoundTag(), level.registryAccess())));
		boolean _syncDirty = false;

		public void read(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
		}

		public CompoundTag save(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
			return nbt;
		}

		public void markSyncDirty() {
			this.setDirty();
			this._syncDirty = true;
		}

		static WorldVariables clientSide = new WorldVariables();

		public static WorldVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel level) {
				return level.getDataStorage().computeIfAbsent(WorldVariables.TYPE);
			} else {
				return clientSide;
			}
		}
	}

	public static class MapVariables extends SavedData {
		public static final SavedDataType<MapVariables> TYPE = new SavedDataType<>(Identifier.parse("man_from_the_fog_port:mapvars"), level -> new MapVariables(), level -> CompoundTag.CODEC.xmap(tag -> {
			MapVariables instance = new MapVariables();
			instance.read(tag, level.registryAccess());
			return instance;
		}, instance -> instance.save(new CompoundTag(), level.registryAccess())));
		boolean _syncDirty = false;
		public double amount = 0;
		public double sound = 0;
		public double strike = 0;
		public double chance = 0;
		public double distance = 0;
		public double distancez = 0;
		public double positionx = 0;
		public double positiony = 0;
		public double jumpscare = 0;
		public double music = 0.0;
		public double despawn = 0;
		public double despawntimer = 0;
		public double despawnScheduled = 0;
		public double worldload = 0;

		public void read(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
			amount = nbt.getDoubleOr("amount", 0);
			sound = nbt.getDoubleOr("sound", 0);
			strike = nbt.getDoubleOr("strike", 0);
			chance = nbt.getDoubleOr("chance", 0);
			distance = nbt.getDoubleOr("distance", 0);
			distancez = nbt.getDoubleOr("distancez", 0);
			positionx = nbt.getDoubleOr("positionx", 0);
			positiony = nbt.getDoubleOr("positiony", 0);
			jumpscare = nbt.getDoubleOr("jumpscare", 0);
			music = nbt.getDoubleOr("music", 0);
			despawn = nbt.getDoubleOr("despawn", 0);
			despawntimer = nbt.getDoubleOr("despawntimer", 0);
			despawnScheduled = nbt.getDoubleOr("despawnScheduled", 0);
			worldload = nbt.getDoubleOr("worldload", 0);
		}

		public CompoundTag save(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
			nbt.putDouble("amount", amount);
			nbt.putDouble("sound", sound);
			nbt.putDouble("strike", strike);
			nbt.putDouble("chance", chance);
			nbt.putDouble("distance", distance);
			nbt.putDouble("distancez", distancez);
			nbt.putDouble("positionx", positionx);
			nbt.putDouble("positiony", positiony);
			nbt.putDouble("jumpscare", jumpscare);
			nbt.putDouble("music", music);
			nbt.putDouble("despawn", despawn);
			nbt.putDouble("despawntimer", despawntimer);
			nbt.putDouble("despawnScheduled", despawnScheduled);
			nbt.putDouble("worldload", worldload);
			return nbt;
		}

		public void markSyncDirty() {
			this.setDirty();
			this._syncDirty = true;
		}

		static MapVariables clientSide = new MapVariables();

		public static MapVariables get(LevelAccessor world) {
			if (world instanceof ServerLevelAccessor serverLevelAccessor) {
				return serverLevelAccessor.getLevel().getServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(MapVariables.TYPE);
			} else {
				return clientSide;
			}
		}
	}

	public record SavedDataSyncMessage(int dataType, SavedData data) implements CustomPacketPayload {
		public static final Type<SavedDataSyncMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ManFromTheFogPortMod.MODID, "saved_data_sync"));
		public static final StreamCodec<RegistryFriendlyByteBuf, SavedDataSyncMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, SavedDataSyncMessage message) -> {
			buffer.writeInt(message.dataType);
			if (message.data instanceof MapVariables mapVariables)
				buffer.writeNbt(mapVariables.save(new CompoundTag(), buffer.registryAccess()));
			else if (message.data instanceof WorldVariables worldVariables)
				buffer.writeNbt(worldVariables.save(new CompoundTag(), buffer.registryAccess()));
		}, (RegistryFriendlyByteBuf buffer) -> {
			int dataType = buffer.readInt();
			CompoundTag nbt = buffer.readNbt();
			SavedData data = null;
			if (nbt != null) {
				data = dataType == 0 ? new MapVariables() : new WorldVariables();
				if (data instanceof MapVariables mapVariables)
					mapVariables.read(nbt, buffer.registryAccess());
				else if (data instanceof WorldVariables worldVariables)
					worldVariables.read(nbt, buffer.registryAccess());
			}
			return new SavedDataSyncMessage(dataType, data);
		});

		@Override
		public Type<SavedDataSyncMessage> type() {
			return TYPE;
		}

		public static void handleData(final SavedDataSyncMessage message, final IPayloadContext context) {
			if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
				context.enqueueWork(() -> {
					if (message.dataType == 0)
						MapVariables.clientSide.read(((MapVariables) message.data).save(new CompoundTag(), context.player().registryAccess()), context.player().registryAccess());
					else
						WorldVariables.clientSide.read(((WorldVariables) message.data).save(new CompoundTag(), context.player().registryAccess()), context.player().registryAccess());
				}).exceptionally(e -> {
					context.connection().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}
	}
}