package com.susen36.caerulaarbor.network.receive;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.network.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlayerVariablesSyncMessage(PlayerVariable data) implements CustomPacketPayload {
	public static final Type<PlayerVariablesSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "player_variables_sync"));
	public static final StreamCodec<FriendlyByteBuf, PlayerVariablesSyncMessage> STREAM_CODEC = StreamCodec.of(
			PlayerVariablesSyncMessage::encode,
			PlayerVariablesSyncMessage::decode
	);

	public static PlayerVariablesSyncMessage decode(FriendlyByteBuf buffer) {
		PlayerVariable data = new PlayerVariable();
		data.readNBT(buffer.readNbt());
		return new PlayerVariablesSyncMessage(data);
	}

	public static void encode(FriendlyByteBuf buffer, PlayerVariablesSyncMessage message) {
		buffer.writeNbt(message.data.writeNBT());
	}

	public static void handle(PlayerVariablesSyncMessage message, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.flow().isClientbound()) {
				ClientPacketHandler.handlePlayerVariablesSync(message, context);
			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}