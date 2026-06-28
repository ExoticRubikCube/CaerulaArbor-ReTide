package com.apocalypse.caerulaarbor.network;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.network.message.receive.PlayerVariablesSyncMessage;
import com.apocalypse.caerulaarbor.network.message.receive.SavedDataSyncMessage;
import com.apocalypse.caerulaarbor.network.message.send.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CaerulaArborModNetwork {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(CaerulaArborMod.MODID, CaerulaArborMod.MODID), () -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private static int messageID = 0;

	private CaerulaArborModNetwork() {
	}

	public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder,
			BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
		PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
		messageID++;
	}

	public static void register() {
		addNetworkMessage(SavedDataSyncMessage.class, SavedDataSyncMessage::encode, SavedDataSyncMessage::decode, SavedDataSyncMessage::handler);
		addNetworkMessage(PlayerVariablesSyncMessage.class, PlayerVariablesSyncMessage::encode, PlayerVariablesSyncMessage::decode, PlayerVariablesSyncMessage::handler);
		addNetworkMessage(CaerulaRecordGUIButtonMessage.class, CaerulaRecordGUIButtonMessage::buffer, CaerulaRecordGUIButtonMessage::new, CaerulaRecordGUIButtonMessage::handler);
		addNetworkMessage(CentrifugerSelectButtonMessage.class, CentrifugerSelectButtonMessage::buffer, CentrifugerSelectButtonMessage::new, CentrifugerSelectButtonMessage::handler);
		addNetworkMessage(EvoTreeButtonMessage.class, EvoTreeButtonMessage::buffer, EvoTreeButtonMessage::new, EvoTreeButtonMessage::handler);
		addNetworkMessage(InfoStrategyNavigationButtonMessage.class, InfoStrategyNavigationButtonMessage::buffer, InfoStrategyNavigationButtonMessage::new, InfoStrategyNavigationButtonMessage::handler);
		addNetworkMessage(InfoStrategyReturnButtonMessage.class, InfoStrategyReturnButtonMessage::buffer, InfoStrategyReturnButtonMessage::new, InfoStrategyReturnButtonMessage::handler);
		addNetworkMessage(PlayerEvoButtonMessage.class, PlayerEvoButtonMessage::buffer, PlayerEvoButtonMessage::new, PlayerEvoButtonMessage::handler);
		addNetworkMessage(RelicShowcaseButtonMessage.class, RelicShowcaseButtonMessage::buffer, RelicShowcaseButtonMessage::new, RelicShowcaseButtonMessage::handler);
	}
}
