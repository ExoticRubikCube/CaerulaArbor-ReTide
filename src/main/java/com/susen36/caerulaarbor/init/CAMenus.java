package com.susen36.caerulaarbor.init;


import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.menu.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CAMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.MENU, CaerulaArbor.MODID);
	public static final DeferredHolder<MenuType<?>, MenuType<CaerulaRecordGUIMenu>> CAERULA_RECORD_GUI = REGISTRY.register("caerula_record_gui", () -> IMenuTypeExtension.create(CaerulaRecordGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<RelicShowcaseMenu>> RELIC_SHOWCASE = REGISTRY.register("relic_showcase", () -> IMenuTypeExtension.create(RelicShowcaseMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<InfoStrategySubsisMenu>> INFO_STRATEGY_SUBSIS = REGISTRY.register("info_strategy_subsis", () -> IMenuTypeExtension.create(InfoStrategySubsisMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<InfoStrategyBreedMenu>> INFO_STRATEGY_BREED = REGISTRY.register("info_strategy_breed", () -> IMenuTypeExtension.create(InfoStrategyBreedMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<InfoStrategyMigrationMenu>> INFO_STRATEGY_MIGRATION = REGISTRY.register("info_strategy_migration", () -> IMenuTypeExtension.create(InfoStrategyMigrationMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<InfoStrategyGrowMenu>> INFO_STRATEGY_GROW = REGISTRY.register("info_strategy_grow", () -> IMenuTypeExtension.create(InfoStrategyGrowMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<InfoStrategyAllMenu>> INFO_STRATEGY_ALL = REGISTRY.register("info_strategy_all", () -> IMenuTypeExtension.create(InfoStrategyAllMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<EvoTreeMenu>> EVO_TREE = REGISTRY.register("evo_tree", () -> IMenuTypeExtension.create(EvoTreeMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<CentrifugerSelectMenu>> CENTRIFUGER_SELECT = REGISTRY.register("centrifuger_select", () -> IMenuTypeExtension.create(CentrifugerSelectMenu::new));
}