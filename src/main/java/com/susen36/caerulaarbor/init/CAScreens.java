package com.susen36.caerulaarbor.init;

import com.susen36.caerulaarbor.client.gui.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CAScreens {
    @SubscribeEvent
    public static void clientLoad(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(CAMenus.CAERULA_RECORD_GUI.get(), CaerulaRecordGUIScreen::new);
            MenuScreens.register(CAMenus.RELIC_SHOWCASE.get(), RelicShowcaseScreen::new);
            MenuScreens.register(CAMenus.INFO_STRATEGY_SUBSIS.get(), InfoStrategySubsisScreen::new);
            MenuScreens.register(CAMenus.INFO_STRATEGY_BREED.get(), InfoStrategyBreedScreen::new);
            MenuScreens.register(CAMenus.INFO_STRATEGY_MIGRATION.get(), InfoStrategyMigrationScreen::new);
            MenuScreens.register(CAMenus.INFO_STRATEGY_GROW.get(), InfoStrategyGrowScreen::new);
            MenuScreens.register(CAMenus.INFO_STRATEGY_ALL.get(), InfoStrategyAllScreen::new);
            MenuScreens.register(CAMenus.EVO_TREE.get(), EvoTreeScreen::new);
            MenuScreens.register(CAMenus.CENTRIFUGER_SELECT.get(), CentrifugerSelectScreen::new);
            MenuScreens.register(CAMenus.PLAYER_EVO.get(), PlayerEvoScreen::new);
        });
    }
}
