package com.susen36.caerulaarbor.entity.crawler;


import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class PocketSeaCreeperEntity extends AbstractPocketSeaCrawlerEntity {

	public PocketSeaCreeperEntity(Level world) {
		this(CAEntities.POCKET_SEA_CREEPER.get(), world);
	}

	public PocketSeaCreeperEntity(EntityType<PocketSeaCreeperEntity> type, Level world) {
		super(type, world);

	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new SelfDestructGoal(this));
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (itemstack.is(ItemTags.CREEPER_IGNITERS)) {
			SoundEvent soundevent = itemstack.is(Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
			this.level().playSound(player, this.getX(), this.getY(), this.getZ(), soundevent, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
			if (!this.level().isClientSide) {
				this.setSwellDir(1);
				if (!itemstack.isDamageableItem()) {
					itemstack.shrink(1);
				} else {
					itemstack.hurtAndBreak(1, player, getSlotForHand(hand));
				}
			}

			return InteractionResult.sidedSuccess(this.level().isClientSide);
		} else {
			return super.mobInteract(player, hand);
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
		builder = builder.add(Attributes.MAX_HEALTH, 65);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 4);
		builder = builder.add(Attributes.FOLLOW_RANGE, 36);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.75);
		return builder;
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}

	static class SelfDestructGoal extends Goal {
		private final PocketSeaCreeperEntity creeper;
		private int swell;
		private int oldSwell;
		private int maxSwell = 30;
		private boolean forcedExplode;
		private static final double TRIGGER_DISTANCE_SQR = 9.0D;
		private static final double MAX_TRACK_DISTANCE_SQR = 49.0D;

		public SelfDestructGoal(PocketSeaCreeperEntity creeper) {
			this.creeper = creeper;
			this.setFlags(EnumSet.of(Flag.MOVE));
		}

		@Override
		public boolean canUse() {
			LivingEntity target = this.creeper.getTarget();
			if (this.creeper.getSwellDir() > 0) {
				this.forcedExplode = true;
				return true;
			}
			if (target == null || !target.isAlive()) {
				return false;
			}
			return this.creeper.distanceToSqr(target.getX(), target.getY(), target.getZ()) < TRIGGER_DISTANCE_SQR;
		}

		@Override
		public void start() {
			this.creeper.getNavigation().stop();
			this.creeper.setAggressive(true);
		}

		@Override
		public void stop() {
			this.creeper.setSwellDir(-1);
			this.swell = 0;
			this.oldSwell = 0;
			this.forcedExplode = false;
			this.creeper.setAggressive(false);
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			this.oldSwell = this.swell;
			LivingEntity target = this.creeper.getTarget();

			if (this.forcedExplode) {
				this.creeper.setSwellDir(1);
			} else if (target == null) {
				this.creeper.setSwellDir(-1);
			} else if (this.creeper.distanceToSqr(target.getX(), target.getY(), target.getZ()) > MAX_TRACK_DISTANCE_SQR) {
				this.creeper.setSwellDir(-1);
			} else if (!this.creeper.getSensing().hasLineOfSight(target)) {
				this.creeper.setSwellDir(-1);
			} else {
				this.creeper.setSwellDir(1);
			}

			int swellDir = this.creeper.getSwellDir();
			if (swellDir > 0 && this.swell == 0) {
				this.creeper.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
			}

			this.swell += swellDir;
			if (this.swell < 0) {
				this.swell = 0;
			}
			this.creeper.getEntityData().set(PocketSeaCreeperEntity.DATA_SWELL, this.swell);

			if (this.swell >= this.maxSwell) {
				this.swell = this.maxSwell;
				this.creeper.explode(true);
				this.creeper.setSwellDir(-1);
				this.swell = 0;
				this.oldSwell = 0;
				this.creeper.getEntityData().set(PocketSeaCreeperEntity.DATA_SWELL, 0);
			}
		}
	}
}
