package com.apocalypse.caerulaarbor.util;

import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import net.minecraft.world.level.LevelAccessor;

public class StrategyUtils {

	private StrategyUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static boolean isSilence(LevelAccessor world) {
		return MapVariables.get(world).strategy_silence > 0;
	}

	public static boolean canEnableSilence(LevelAccessor world) {
		return MapVariables.get(world).strategy_grow >= 4 && MapVariables.get(world).strategy_subsisting >= 4 && MapVariables.get(world).strategy_breed >= 4
				&& MapVariables.get(world).strategy_migration >= 4 && MapVariables.get(world).silence_enabled;
	}
}