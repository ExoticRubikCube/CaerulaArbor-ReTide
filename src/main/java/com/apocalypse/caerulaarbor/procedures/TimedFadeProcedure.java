package com.apocalypse.caerulaarbor.procedures;

import net.minecraft.world.entity.Entity;

public class TimedFadeProcedure {
	public static void execute(Entity immediatesourceentity) {
		if (immediatesourceentity == null)
			return;
		if (immediatesourceentity.tickCount >= 200) {
			if (!immediatesourceentity.level().isClientSide())
				immediatesourceentity.discard();
		}
	}
}

// TODO: 调用次数 = 4，副作用密集（实体删除），保持原样不重构
