package com.susen36.caerulaarbor.init;

import com.susen36.caerulaarbor.network.receive.PlayerVariablesSyncMessage;
import com.susen36.caerulaarbor.network.receive.SavedDataSyncMessage;
import com.susen36.caerulaarbor.network.send.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class CANetwork {
	private CANetwork() {
	}

	@SubscribeEvent
	public static void register(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1");
		registrar.playToClient(SavedDataSyncMessage.TYPE, SavedDataSyncMessage.STREAM_CODEC, SavedDataSyncMessage::handle);
		registrar.playToClient(PlayerVariablesSyncMessage.TYPE, PlayerVariablesSyncMessage.STREAM_CODEC, PlayerVariablesSyncMessage::handle);
		registrar.playToServer(CaerulaRecordGUIButtonMessage.TYPE, CaerulaRecordGUIButtonMessage.STREAM_CODEC, CaerulaRecordGUIButtonMessage::handle);
		registrar.playToServer(CentrifugerSelectButtonMessage.TYPE, CentrifugerSelectButtonMessage.STREAM_CODEC, CentrifugerSelectButtonMessage::handle);
		registrar.playToServer(EvoTreeButtonMessage.TYPE, EvoTreeButtonMessage.STREAM_CODEC, EvoTreeButtonMessage::handle);
		registrar.playToServer(InfoStrategyNavigationButtonMessage.TYPE, InfoStrategyNavigationButtonMessage.STREAM_CODEC, InfoStrategyNavigationButtonMessage::handle);
		registrar.playToServer(InfoStrategyReturnButtonMessage.TYPE, InfoStrategyReturnButtonMessage.STREAM_CODEC, InfoStrategyReturnButtonMessage::handle);
		registrar.playToServer(PlayerEvoButtonMessage.TYPE, PlayerEvoButtonMessage.STREAM_CODEC, PlayerEvoButtonMessage::handle);
		registrar.playToServer(RelicShowcaseButtonMessage.TYPE, RelicShowcaseButtonMessage.STREAM_CODEC, RelicShowcaseButtonMessage::handle);
	}
}