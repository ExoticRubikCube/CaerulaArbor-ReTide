package com.susen36.caerulaarbor.entity.base;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class SeaMonster extends Monster implements GeoEntity, SyncedAnimationEntity {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	protected SeaMonster(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	public MobType getMobType() {
		return MobType.WATER;
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
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
