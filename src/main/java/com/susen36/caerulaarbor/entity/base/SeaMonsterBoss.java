package com.susen36.caerulaarbor.entity.base;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public abstract class SeaMonsterBoss extends SeaMonster {
	protected ServerBossEvent bossInfo;

	protected SeaMonsterBoss(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		if (this.bossInfo != null) {
			this.bossInfo.addPlayer(player);
		}
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		if (this.bossInfo != null) {
			this.bossInfo.removePlayer(player);
		}
	}

	@Override
	public void customServerAiStep() {
		super.customServerAiStep();
		if (this.bossInfo != null) {
			this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
		}
	}
}