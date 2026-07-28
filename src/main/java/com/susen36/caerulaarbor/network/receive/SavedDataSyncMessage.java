package com.susen36.caerulaarbor.network.receive;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.capability.world.WorldVariables;
import net.minecraft.core.HolderLookup;
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
	private final CompoundTag data;

	public SavedDataSyncMessage(int type, SavedData savedData, HolderLookup.Provider provider) {
		this.type = type;
		this.data = savedData.save(new CompoundTag(), provider);
	}

	private SavedDataSyncMessage(int type, CompoundTag data) {
		this.type = type;
		this.data = data;
	}

	public static SavedDataSyncMessage decode(FriendlyByteBuf buffer) {
		int type = buffer.readInt();
		CompoundTag nbt = buffer.readNbt();
		return new SavedDataSyncMessage(type, nbt);
	}

	public static void encode(FriendlyByteBuf buffer, SavedDataSyncMessage message) {
		buffer.writeInt(message.type);
		buffer.writeNbt(message.data);
	}

	public static void handle(SavedDataSyncMessage message, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.flow().isClientbound() && message.data != null) {
				if (message.type == 0) {
					MapVariables.clientSide.read(message.data);
				} else {
					WorldVariables.clientSide.read(message.data);
				}
			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
