package com.apocalypse.caerulaarbor.network.message.receive;

import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
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
			data = type == 0 ? new CaerulaArborModVariables.MapVariables() : new CaerulaArborModVariables.WorldVariables();
			if (data instanceof CaerulaArborModVariables.MapVariables mapVariables) {
				mapVariables.read(nbt);
			} else if (data instanceof CaerulaArborModVariables.WorldVariables worldVariables) {
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
					CaerulaArborModVariables.MapVariables.clientSide = (CaerulaArborModVariables.MapVariables) message.data;
				} else {
					CaerulaArborModVariables.WorldVariables.clientSide = (CaerulaArborModVariables.WorldVariables) message.data;
				}
			}
		});
		context.setPacketHandled(true);
	}
}
