package com.apocalypse.caerulaarbor.network.receive;

import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.network.ClientPacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PlayerVariablesSyncMessage(PlayerVariable data) {

	public static PlayerVariablesSyncMessage decode(FriendlyByteBuf buffer) {
		PlayerVariable data = new PlayerVariable();
		data.readNBT(buffer.readNbt());
		return new PlayerVariablesSyncMessage(data);
	}

	public static void encode(PlayerVariablesSyncMessage message, FriendlyByteBuf buffer) {
		buffer.writeNbt((CompoundTag) message.data.writeNBT());
	}

	public static void handler(PlayerVariablesSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handlePlayerVariablesSync(message, contextSupplier)));
		context.setPacketHandled(true);
	}
}
