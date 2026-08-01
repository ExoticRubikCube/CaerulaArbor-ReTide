package com.susen36.caerulaarbor.entity.base;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

@EventBusSubscriber
public abstract class SeaMonster extends Monster implements GeoEntity, SyncedAnimationEntity {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	protected SeaMonster(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
	}

	@SubscribeEvent
	public static void onLivingBreathe(LivingBreatheEvent event) {
		if (event.getEntity() instanceof SeaMonster) {
			event.setCanBreathe(true);
		}
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	public void baseTick() {
		super.baseTick();
		if (!this.level().isClientSide()) {
			if (this instanceof PolarMountRider rider) {
				rider.tickMountBehavior();
			}
		}
	}

}