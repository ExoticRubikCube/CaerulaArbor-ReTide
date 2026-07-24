package com.susen36.caerulaarbor.network.receive;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.capability.world.WorldVariables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SavedDataSyncMessage implements CustomPacketPayload {
	public static final Type<SavedDataSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "saved_data_sync"));
	public static final StreamCodec<FriendlyByteBuf, SavedDataSyncMessage> STREAM_CODEC = StreamCodec.of(
			SavedDataSyncMessage::encode,
			SavedDataSyncMessage::decode
	);

	private final int type;
	private final SavedData data;

	public SavedDataSyncMessage(int type, SavedData data) {
		this.type = type;
		this.data = data;
	}

	public static SavedDataSyncMessage decode(FriendlyByteBuf buffer) {
		int type = buffer.readInt();
		CompoundTag nbt = buffer.readNbt();
		SavedData data = null;
		if (nbt != null) {
			data = type == 0 ? new MapVariables() : new WorldVariables();
			if (data instanceof MapVariables mapVariables) {
				mapVariables.read(nbt);
			} else if (data instanceof WorldVariables worldVariables) {
				worldVariables.read(nbt);
			}
		}
		return new SavedDataSyncMessage(type, data);
	}

	public static void encode(FriendlyByteBuf buffer, SavedDataSyncMessage message) {
		buffer.writeInt(message.type);
		if (message.data != null) {
			buffer.writeNbt(message.data.save(new CompoundTag()));
		}
	}

	public static void handle(SavedDataSyncMessage message, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.flow().isClientbound() && message.data != null) {
				if (message.type == 0) {
					MapVariables.clientSide = (MapVariables) message.data;
				} else {
					WorldVariables.clientSide = (WorldVariables) message.data;
				}
			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}