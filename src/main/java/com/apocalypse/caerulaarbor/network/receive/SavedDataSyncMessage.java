package com.apocalypse.caerulaarbor.network.receive;

import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import com.apocalypse.caerulaarbor.capability.world.WorldVariables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SavedDataSyncMessage {
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

	public static void encode(SavedDataSyncMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.type);
		if (message.data != null) {
			buffer.writeNbt(message.data.save(new CompoundTag()));
		}
	}

	public static void handler(SavedDataSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			if (context.getDirection().getReceptionSide().isClient() && message.data != null) {
				if (message.type == 0) {
					MapVariables.clientSide = (MapVariables) message.data;
				} else {
					WorldVariables.clientSide = (WorldVariables) message.data;
				}
			}
		});
		context.setPacketHandled(true);
	}
}
