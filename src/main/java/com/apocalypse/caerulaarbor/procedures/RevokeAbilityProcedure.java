package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.world.level.LevelAccessor;

public class RevokeAbilityProcedure {
	public static void execute(LevelAccessor world, double index) {
		if (EntityUtils.inquirybility(world, index)) {
			CaerulaArborModVariables.MapVariables.get(world).endspeaker_abolities =(double) ((int)CaerulaArborModVariables.MapVariables.get(world).endspeaker_abolities - (int)Math.pow(2, index));
			CaerulaArborModVariables.MapVariables.get(world).syncData(world);
		}
	}
}

// TODO: 调用次数 = 4，但副作用密集（修改全局变量、同步数据），保持原样不重构
