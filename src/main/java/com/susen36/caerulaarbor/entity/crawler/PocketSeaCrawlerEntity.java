package com.susen36.caerulaarbor.entity.crawler;


import com.susen36.caerulaarbor.entity.ai.MoveToTargetGoal;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import java.util.EnumSet;

public class PocketSeaCrawlerEntity extends AbstractPocketSeaCrawlerEntity {
    private int pendingExplosions;

	public PocketSeaCrawlerEntity(Level world) {
		this(CAEntities.POCKET_SEA_CRAWLER.get(), world);
	}

	public PocketSeaCrawlerEntity(EntityType<PocketSeaCrawlerEntity> type, Level world) {
		super(type, world);
		maxSwell = 10;
	}

	public PocketSeaCrawlerEntity(EntityType<PocketSeaCrawlerEntity> type, Level world, boolean fromDoll) {
		super(type, world);
		maxSwell = 10;
		if (fromDoll) {
			this.getAttribute(Attributes.SCALE).setBaseValue(0.8F);
		}
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new MoveToTargetGoal(this, 1, true));
		this.goalSelector.addGoal(2, new CrawlerExplodeGoal(this));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (this.charged() && (source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE)))
			return false;
		float healthBeforeDamage = this.getHealth();
		boolean damaged = super.hurt(source, amount);
		float actualDamage = healthBeforeDamage - this.getHealth();
		if (damaged && actualDamage > 0.0F) {
			float damageThreshold = this.getMaxHealth() * 0.15F;
			float accumulatedDamage = this.getEntityData().get(DATA_DEAL) + actualDamage;
			int newExplosions = Mth.floor(accumulatedDamage / damageThreshold);
			this.getEntityData().set(DATA_DEAL, accumulatedDamage - newExplosions * damageThreshold);
			if (newExplosions > 0) {
				this.pendingExplosions += newExplosions;
				this.setSwellDir(1);
			}
		}
		return damaged;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("PendingExplosions", this.pendingExplosions);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("PendingExplosions")) {
			this.pendingExplosions = compound.getInt("PendingExplosions");
			if (this.pendingExplosions > 0) {
				this.setSwellDir(1);
			}
		}
	}

	public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
		event.register(CAEntities.POCKET_SEA_CRAWLER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return WorldUtils.canRareSeabornSpawn(world, x, y, z);
		}, RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = AbstractPocketSeaCrawlerEntity.createAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.225);
		builder = builder.add(Attributes.MAX_HEALTH, 85);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 4);
		builder = builder.add(Attributes.FOLLOW_RANGE, 24);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
		return builder;
	}

	static class CrawlerExplodeGoal extends Goal {
		private final PocketSeaCrawlerEntity crawler;
		private int swell;
		private int oldSwell;

		public CrawlerExplodeGoal(PocketSeaCrawlerEntity crawler) {
			this.crawler = crawler;
			this.setFlags(EnumSet.of(Flag.MOVE));
		}

		@Override
		public boolean canUse() {
			return this.crawler.pendingExplosions > 0;
		}

		@Override
		public void start() {
			this.crawler.getNavigation().stop();
			this.crawler.setAggressive(true);
		}

		@Override
		public void stop() {
			this.crawler.setSwellDir(-1);
			this.swell = 0;
			this.oldSwell = 0;
			this.crawler.getEntityData().set(PocketSeaCrawlerEntity.DATA_SWELL, 0);
			this.crawler.setAggressive(false);
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			this.oldSwell = this.swell;
			int swellDir = this.crawler.getSwellDir();

			if (swellDir > 0 && this.swell == 0) {
				this.crawler.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
			}

			this.swell += swellDir;
			if (this.swell < 0) {
				this.swell = 0;
			}
			this.crawler.getEntityData().set(PocketSeaCrawlerEntity.DATA_SWELL, this.swell);

			if (this.swell >= this.crawler.maxSwell) {
				while (this.crawler.pendingExplosions > 0) {
					this.crawler.explode(false);
					this.crawler.pendingExplosions--;
				}
				this.swell = 0;
				this.oldSwell = 0;
				this.crawler.getEntityData().set(PocketSeaCrawlerEntity.DATA_SWELL, 0);
				this.crawler.setSwellDir(-1);
			}
		}
	}
}
