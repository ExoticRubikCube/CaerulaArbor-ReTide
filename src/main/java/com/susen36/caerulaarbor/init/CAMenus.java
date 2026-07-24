package com.susen36.caerulaarbor.init;


import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.menu.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CAMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.MENU, CaerulaArborMod.MODID);
	public static final DeferredHolder<MenuType<CaerulaRecordGUIMenu>, ? extends MenuType<CaerulaRecordGUIMenu>> CAERULA_RECORD_GUI = REGISTRY.register("caerula_record_gui", () -> IForgeMenuType.create(CaerulaRecordGUIMenu::new));
	public static final DeferredHolder<MenuType<RelicShowcaseMenu>, ? extends MenuType<RelicShowcaseMenu>> RELIC_SHOWCASE = REGISTRY.register("relic_showcase", () -> IForgeMenuType.create(RelicShowcaseMenu::new));
	public static final DeferredHolder<MenuType<InfoStrategySubsisMenu>, ? extends MenuType<InfoStrategySubsisMenu>> INFO_STRATEGY_SUBSIS = REGISTRY.register("info_strategy_subsis", () -> IForgeMenuType.create(InfoStrategySubsisMenu::new));
	public static final DeferredHolder<MenuType<InfoStrategyBreedMenu>, ? extends MenuType<InfoStrategyBreedMenu>> INFO_STRATEGY_BREED = REGISTRY.register("info_strategy_breed", () -> IForgeMenuType.create(InfoStrategyBreedMenu::new));
	public static final DeferredHolder<MenuType<InfoStrategyMigrationMenu>, ? extends MenuType<InfoStrategyMigrationMenu>> INFO_STRATEGY_MIGRATION = REGISTRY.register("info_strategy_migration", () -> IForgeMenuType.create(InfoStrategyMigrationMenu::new));
	public static final DeferredHolder<MenuType<InfoStrategyGrowMenu>, ? extends MenuType<InfoStrategyGrowMenu>> INFO_STRATEGY_GROW = REGISTRY.register("info_strategy_grow", () -> IForgeMenuType.create(InfoStrategyGrowMenu::new));
	public static final DeferredHolder<MenuType<InfoStrategyAllMenu>, ? extends MenuType<InfoStrategyAllMenu>> INFO_STRATEGY_ALL = REGISTRY.register("info_strategy_all", () -> IForgeMenuType.create(InfoStrategyAllMenu::new));
	public static final DeferredHolder<MenuType<EvoTreeMenu>, ? extends MenuType<EvoTreeMenu>> EVO_TREE = REGISTRY.register("evo_tree", () -> IForgeMenuType.create(EvoTreeMenu::new));
	public static final DeferredHolder<MenuType<CentrifugerSelectMenu>, ? extends MenuType<CentrifugerSelectMenu>> CENTRIFUGER_SELECT = REGISTRY.register("centrifuger_select", () -> IForgeMenuType.create(CentrifugerSelectMenu::new));
	public static final DeferredHolder<MenuType<PlayerEvoMenu>, ? extends MenuType<PlayerEvoMenu>> PLAYER_EVO = REGISTRY.register("player_evo", () -> IForgeMenuType.create(PlayerEvoMenu::new));
}
