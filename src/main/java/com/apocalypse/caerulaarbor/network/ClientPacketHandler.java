package com.apocalypse.caerulaarbor.network;

import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.network.message.receive.PlayerVariablesSyncMessage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientPacketHandler {

	private ClientPacketHandler() {
	}

	public static void handlePlayerVariablesSync(PlayerVariablesSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		if (context.getDirection().getReceptionSide() != LogicalSide.CLIENT) {
			return;
		}

		if (Minecraft.getInstance().player == null) {
			return;
		}

		Minecraft.getInstance().player.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(variables -> variables.readNBT(message.data().writeNBT()));
	}
}
