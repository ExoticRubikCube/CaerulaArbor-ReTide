package com.apocalypse.caerulaarbor.util;

import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import net.minecraft.world.level.LevelAccessor;

public class StrategyUtils {

	private StrategyUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static boolean isSilence(LevelAccessor world) {
		return CaerulaArborModVariables.MapVariables.get(world).strategy_silence > 0;
	}

	public static boolean canEnableSilence(LevelAccessor world) {
		return CaerulaArborModVariables.MapVariables.get(world).strategy_grow >= 4 && CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting >= 4 && CaerulaArborModVariables.MapVariables.get(world).strategy_breed >= 4
				&& CaerulaArborModVariables.MapVariables.get(world).strategy_migration >= 4 && CaerulaArborModVariables.MapVariables.get(world).silence_enabled;
	}
}