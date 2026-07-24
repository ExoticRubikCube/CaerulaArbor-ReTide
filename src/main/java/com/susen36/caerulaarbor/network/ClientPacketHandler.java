package com.susen36.caerulaarbor.network;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.network.receive.PlayerVariablesSyncMessage;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPacketHandler {

	private ClientPacketHandler() {
	}

	public static void handlePlayerVariablesSync(PlayerVariablesSyncMessage message, IPayloadContext context) {
		if (!context.flow().isClientbound()) {
			return;
		}

		if (Minecraft.getInstance().player == null) {
			return;
		}

		Minecraft.getInstance().player.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(variables -> variables.readNBT(message.data().writeNBT()));
	}
}