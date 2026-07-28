package com.susen36.caerulaarbor.network;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
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

		PlayerVariable variables = ModCapabilities.getPlayerVariables(Minecraft.getInstance().player);
		variables.readNBT(message.data().writeNBT());
	}
}