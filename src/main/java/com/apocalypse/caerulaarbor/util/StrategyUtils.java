package com.apocalypse.caerulaarbor.util;

import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;

public class StrategyUtils {

	private StrategyUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static boolean isSilence(LevelAccessor world) {
		return MapVariables.get(world).strategy_silence > 0;
	}

	//可疑，需要解释
	public static boolean canEnableSilence(LevelAccessor world) {
		return MapVariables.get(world).strategy_grow >= 4 && MapVariables.get(world).strategy_subsisting >= 4 && MapVariables.get(world).strategy_breed >= 4
				&& MapVariables.get(world).strategy_migration >= 4 && MapVariables.get(world).silence_enabled;
	}

	public static double getStraSilence(LevelAccessor world) {
		return MapVariables.get(world).strategy_silence;
	}

	public static double getStraSubsis(LevelAccessor world) {
		return MapVariables.get(world).strategy_subsisting;
	}

	public static double getStraBreed(LevelAccessor world) {
		return MapVariables.get(world).strategy_breed;
	}

	public static double getStraGrow(LevelAccessor world) {
		return MapVariables.get(world).strategy_grow;
	}

	public static double getStraMigration(LevelAccessor world) {
		return MapVariables.get(world).strategy_migration;
	}

	public static String getSilenceMigration(LevelAccessor world) {
		return Component.translatable("item.caerula_arbor.sample_migration.description_" + Math.round(MapVariables.get(world).strategy_silence + 5)).getString();
	}

	public static String getSilenceSubsis(LevelAccessor world) {
		return Component.translatable("item.caerula_arbor.sample_subsisting.description_" + Math.round(MapVariables.get(world).strategy_silence + 5)).getString();
	}

	public static String getSilenceBreed(LevelAccessor world) {
		return Component.translatable("item.caerula_arbor.sample_breed.description_" + Math.round(MapVariables.get(world).strategy_silence + 5)).getString();
	}

	public static String getSilenceGrow(LevelAccessor world) {
		return Component.translatable("item.caerula_arbor.sample_grow.description_" + Math.round(MapVariables.get(world).strategy_silence + 5)).getString();
	}

	public static String getDescrSubsis(LevelAccessor world) {
		return Component.translatable("item.caerula_arbor.sample_subsisting.description_" + Math.round(MapVariables.get(world).strategy_subsisting)).getString();
	}

	public static String getDescrBreed(LevelAccessor world) {
		return Component.translatable("item.caerula_arbor.sample_breed.description_" + Math.round(MapVariables.get(world).strategy_breed)).getString();
	}

	public static String getDescrGrow(LevelAccessor world) {
		return Component.translatable("item.caerula_arbor.sample_grow.description_" + Math.round(MapVariables.get(world).strategy_grow)).getString();
	}

	public static String getDescrMigra(LevelAccessor world) {
		return Component.translatable("item.caerula_arbor.sample_migration.description_" + Math.round(MapVariables.get(world).strategy_migration)).getString();
	}

	public static boolean isSublimation(LevelAccessor world) {
		return MapVariables.get(world).strategy_sublimation > 0;
	}

	public static double getStraSublimation(LevelAccessor world) {
		return MapVariables.get(world).strategy_sublimation;
	}

	public static String getDescrSublimation(LevelAccessor world) {
		return Component.translatable("item.caerula_arbor.sample_sublimation.description_" + Math.round(MapVariables.get(world).strategy_sublimation)).getString();
	}

	public static String getSublimationSubsis(LevelAccessor world) {
		double lvl = Math.min(MapVariables.get(world).strategy_subsisting, MapVariables.get(world).strategy_sublimation);
		if (lvl < 1) {
			return "";
		}
		return Component.translatable("evolution.caerula_aerbor.sublimation.subsis." + (int) lvl).getString();
	}

	public static String getSublimationSubsis2(LevelAccessor world) {
		double lvl = Math.min(MapVariables.get(world).strategy_subsisting, MapVariables.get(world).strategy_sublimation);
		if (lvl < 1) {
			return "";
		}
		String key = "evolution.caerula_aerbor.sublimation.subsis." + (int) lvl + "_1";
		String desc = Component.translatable(key).getString();
		if (desc.equals(key)) {
			return "";
		}
		return desc;
	}

	public static String getSublimationBreed(LevelAccessor world) {
		double lvl = Math.min(MapVariables.get(world).strategy_breed, MapVariables.get(world).strategy_sublimation);
		if (lvl < 1) {
			return "";
		}
		return Component.translatable("evolution.caerula_aerbor.sublimation.breed." + (int) lvl).getString();
	}

	public static String getSublimationBreed1(LevelAccessor world) {
		double lvl = Math.min(MapVariables.get(world).strategy_breed, MapVariables.get(world).strategy_sublimation);
		if (lvl < 1) {
			return "";
		}
		String key = "evolution.caerula_aerbor.sublimation.breed." + (int) lvl + "_1";
		String desc = Component.translatable(key).getString();
		if (desc.equals(key)) {
			return "";
		}
		return desc;
	}

	public static String getSublimationGrow(LevelAccessor world) {
		double lvl = Math.min(MapVariables.get(world).strategy_grow, MapVariables.get(world).strategy_sublimation);
		if (lvl < 1) {
			return "";
		}
		return Component.translatable("evolution.caerula_aerbor.sublimation.grow." + (int) lvl).getString();
	}

	public static String getSublimationGrow2(LevelAccessor world) {
		double lvl = Math.min(MapVariables.get(world).strategy_grow, MapVariables.get(world).strategy_sublimation);
		if (lvl < 1) {
			return "";
		}
		String key = "evolution.caerula_aerbor.sublimation.grow." + (int) lvl + "_1";
		String desc = Component.translatable(key).getString();
		if (desc.equals(key)) {
			return "";
		}
		return desc;
	}

	public static String getSublimationMig(LevelAccessor world) {
		double lvl = Math.min(MapVariables.get(world).strategy_migration, MapVariables.get(world).strategy_sublimation);
		if (lvl < 1) {
			return "";
		}
		return Component.translatable("evolution.caerula_aerbor.sublimation.migration." + (int) lvl).getString();
	}

	public static String getSublimationMig2(LevelAccessor world) {
		double lvl = Math.min(MapVariables.get(world).strategy_migration, MapVariables.get(world).strategy_sublimation);
		if (lvl < 1) {
			return "";
		}
		String key = "evolution.caerula_aerbor.sublimation.migration." + (int) lvl + "_1";
		String desc = Component.translatable(key).getString();
		if (desc.equals(key)) {
			return "";
		}
		return desc;
	}
}
