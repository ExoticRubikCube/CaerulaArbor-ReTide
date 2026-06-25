/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.client.gui.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CaerulaArborModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(CaerulaArborModMenus.CAERULA_RECORD_GUI.get(), CaerulaRecordGUIScreen::new);
			MenuScreens.register(CaerulaArborModMenus.RELIC_SHOWCASE.get(), RelicShowcaseScreen::new);
			MenuScreens.register(CaerulaArborModMenus.INFO_STRATEGY_SUBSIS.get(), InfoStrategySubsisScreen::new);
			MenuScreens.register(CaerulaArborModMenus.INFO_STRATEGY_BREED.get(), InfoStrategyBreedScreen::new);
			MenuScreens.register(CaerulaArborModMenus.INFO_STRATEGY_MIGRATION.get(), InfoStrategyMigrationScreen::new);
			MenuScreens.register(CaerulaArborModMenus.INFO_STRATEGY_GROW.get(), InfoStrategyGrowScreen::new);
			MenuScreens.register(CaerulaArborModMenus.INFO_STRATEGY_ALL.get(), InfoStrategyAllScreen::new);
			MenuScreens.register(CaerulaArborModMenus.EVO_TREE.get(), EvoTreeScreen::new);
			MenuScreens.register(CaerulaArborModMenus.CENTRIFUGER_SELECT.get(), CentrifugerSelectScreen::new);
			MenuScreens.register(CaerulaArborModMenus.PLAYER_EVO.get(), PlayerEvoScreen::new);
		});
	}
}
