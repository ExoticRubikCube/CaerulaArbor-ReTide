package com.susen36.caerulaarbor.entity.routeshaper;

import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class LineringPathshaperEntity extends AbstractPathshaperEntity {
	public LineringPathshaperEntity(Level world) {
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
		return 5;
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
		builder = builder.add(CAAttributes.MAGIC_RESISTANCE.get(), 24);
		builder = builder.add(CAAttributes.MAX_SANITY.get(), 2000);
		return builder;
	}
}

