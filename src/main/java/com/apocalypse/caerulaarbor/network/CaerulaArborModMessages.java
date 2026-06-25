package com.apocalypse.caerulaarbor.network;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

public class CaerulaArborModMessages {

    private CaerulaArborModMessages() {
    }

    public static void register() {
        CaerulaArborMod.addNetworkMessage(CaerulaArborModVariables.SavedDataSyncMessage.class, CaerulaArborModVariables.SavedDataSyncMessage::buffer,
                CaerulaArborModVariables.SavedDataSyncMessage::new, CaerulaArborModVariables.SavedDataSyncMessage::handler);
        CaerulaArborMod.addNetworkMessage(CaerulaArborModVariables.PlayerVariablesSyncMessage.class, CaerulaArborModVariables.PlayerVariablesSyncMessage::buffer,
                CaerulaArborModVariables.PlayerVariablesSyncMessage::new, CaerulaArborModVariables.PlayerVariablesSyncMessage::handler);
        CaerulaArborMod.addNetworkMessage(CaerulaRecordGUIButtonMessage.class, CaerulaRecordGUIButtonMessage::buffer, CaerulaRecordGUIButtonMessage::new,
                CaerulaRecordGUIButtonMessage::handler);
        CaerulaArborMod.addNetworkMessage(CentrifugerSelectButtonMessage.class, CentrifugerSelectButtonMessage::buffer, CentrifugerSelectButtonMessage::new,
                CentrifugerSelectButtonMessage::handler);
        CaerulaArborMod.addNetworkMessage(EvoTreeButtonMessage.class, EvoTreeButtonMessage::buffer, EvoTreeButtonMessage::new, EvoTreeButtonMessage::handler);
        CaerulaArborMod.addNetworkMessage(InfoStrategyNavigationButtonMessage.class, InfoStrategyNavigationButtonMessage::buffer,
                InfoStrategyNavigationButtonMessage::new, InfoStrategyNavigationButtonMessage::handler);
        CaerulaArborMod.addNetworkMessage(InfoStrategyReturnButtonMessage.class, InfoStrategyReturnButtonMessage::buffer,
                InfoStrategyReturnButtonMessage::new, InfoStrategyReturnButtonMessage::handler);
        CaerulaArborMod.addNetworkMessage(PlayerEvoButtonMessage.class, PlayerEvoButtonMessage::buffer, PlayerEvoButtonMessage::new, PlayerEvoButtonMessage::handler);
        CaerulaArborMod.addNetworkMessage(RelicShowcaseButtonMessage.class, RelicShowcaseButtonMessage::buffer, RelicShowcaseButtonMessage::new,
                RelicShowcaseButtonMessage::handler);
    }
}
