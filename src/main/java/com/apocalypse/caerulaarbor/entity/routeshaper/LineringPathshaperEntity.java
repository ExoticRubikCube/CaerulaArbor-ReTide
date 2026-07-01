package com.apocalypse.caerulaarbor.entity.routeshaper;

import com.apocalypse.caerulaarbor.init.CAEntities;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;

public class LineringPathshaperEntity extends AbstractPathshaperEntity {
	public LineringPathshaperEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CAEntities.LINGERING_PATHSHAPER.get(), world);
	}

	public LineringPathshaperEntity(EntityType<LineringPathshaperEntity> type, Level world) {
		super(type, world);
		this.bossInfo.setColor(ServerBossEvent.BossBarColor.GREEN);
		xpReward = 32;
	}

	@Override
	protected EntityType<?> getSummonedFractalType() {
		return CAEntities.LINGERING_FRACTAL.get();
	}

	@Override
	protected int getHurtSummonThreshold() {
		return 6;
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
		builder = builder.add(Attributes.MAX_HEALTH, 280);
		builder = builder.add(Attributes.ATTACK_SPEED, 1.68);
		builder = builder.add(Attributes.ARMOR, 9);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 10);
		builder = builder.add(Attributes.FOLLOW_RANGE, 48);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
		return builder;
	}
}

