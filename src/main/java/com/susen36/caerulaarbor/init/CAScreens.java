package com.susen36.caerulaarbor.init;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.client.gui.screen.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class CAScreens {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(CAMenus.CAERULA_RECORD_GUI.get(), (container, inventory, text) -> {
            String themeName = ModCapabilities.getPlayerVariables(container.entity).current_theme;
            if ("DEEPBLUE".equals(themeName)) {
                return new RecordCaerulaScreen(container, inventory, text);
            }
            return new RecordCommonScreen(container, inventory, text);
        });
        event.register(CAMenus.RELIC_SHOWCASE.get(), RelicShowcaseScreen::new);
        event.register(CAMenus.INFO_STRATEGY_SUBSIS.get(), InfoStrategySubsisScreen::new);
        event.register(CAMenus.INFO_STRATEGY_BREED.get(), InfoStrategyBreedScreen::new);
        event.register(CAMenus.INFO_STRATEGY_MIGRATION.get(), InfoStrategyMigrationScreen::new);
        event.register(CAMenus.INFO_STRATEGY_GROW.get(), InfoStrategyGrowScreen::new);
        event.register(CAMenus.INFO_STRATEGY_ALL.get(), InfoStrategyAllScreen::new);
        event.register(CAMenus.EVO_TREE.get(), EvoTreeScreen::new);
        event.register(CAMenus.CENTRIFUGER_SELECT.get(), CentrifugerSelectScreen::new);
    }
}