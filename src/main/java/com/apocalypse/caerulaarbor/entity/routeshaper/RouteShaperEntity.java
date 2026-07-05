package com.apocalypse.caerulaarbor.entity.routeshaper;

import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class RouteShaperEntity extends AbstractPathshaperEntity {
	public RouteShaperEntity(Level world) {
		this(CAEntities.ROUTE_SHAPER.get(), world);
	}

	public RouteShaperEntity(EntityType<RouteShaperEntity> type, Level world) {
		super(type, world);
		bossInfo.setColor(ServerBossEvent.BossBarColor.BLUE);
		xpReward = 32;
	}

	@Override
	protected EntityType<?> getSummonedFractalType() {
		return CAEntities.ROUTE_FRACTAL.get();
	}

	@Override
	protected int getHurtSummonThreshold() {
		return 10;
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
		if (this.isDeadOrDying()) {
			LevelAccessor world = this.level();
			final Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
			List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(64 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
			for (Entity entityiterator : entfound) {
				if (entityiterator instanceof RouteFractalEntity) {
					entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.FELL_OUT_OF_WORLD)), 999999);
				}
			}
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
		builder = builder.add(Attributes.MAX_HEALTH, 140);
		builder = builder.add(Attributes.ARMOR, 8);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 9);
		builder = builder.add(Attributes.FOLLOW_RANGE, 48);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
		builder = builder.add(CAAttributes.MAGIC_RESISTANCE.get(), 24);
		return builder;
	}
}

