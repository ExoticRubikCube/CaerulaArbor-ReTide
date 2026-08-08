package com.susen36.caerulaarbor.entity.routeshaper;

import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class LingeringFractalEntity extends AbstractFractalEntity {
	public LingeringFractalEntity(Level world) {
		this(CAEntities.LINGERING_FRACTAL.get(), world);
	}

	public LingeringFractalEntity(EntityType<LingeringFractalEntity> type, Level world) {
		super(type, world);
		xpReward = 9;
	}

	@Override
	protected EntityType<?> getSummonedFractalType() {
		return CAEntities.LINGERING_FRACTAL.get();
	}

	@Override
	public void baseTick() {
		super.baseTick();
		Level world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		if (world instanceof ServerLevel level && tickCount > 1200 && tickCount % 20 == 7) {
			UUID ownerUUID = this.getOwnerUUID();
			Entity owner = ownerUUID == null ? null : level.getEntity(ownerUUID);
			if (owner instanceof LineringPathshaperEntity lingeringPathshaper && lingeringPathshaper.isDeadOrDying()) {
				final Vec3 center = new Vec3(x, y, z);
				List<LingeringFractalEntity> entfound = world.getEntitiesOfClass(LingeringFractalEntity.class, new AABB(center, center).inflate(64 / 2d), fractal -> fractal.hasOwner(ownerUUID)).stream().sorted(Comparator.comparing(Entity::getUUID)).toList();
				if (entfound.size() > 3 && entfound.getFirst() == this) {
					for (LingeringFractalEntity fractal : entfound) {
						fractal.discard();
					}
					LineringPathshaperEntity entityToSpawn = CAEntities.LINGERING_PATHSHAPER.get().spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			}
		}
		this.refreshDimensions();
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.5);
		builder = builder.add(Attributes.MAX_HEALTH, 95);
		builder = builder.add(Attributes.ARMOR, 3);
		builder = builder.add(Attributes.ATTACK_SPEED, 1.68);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 7);
		builder = builder.add(Attributes.FOLLOW_RANGE, 32);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
		return builder;
	}
}
