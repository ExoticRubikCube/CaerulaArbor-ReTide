package com.apocalypse.caerulaarbor.entity.routeshaper;

import com.apocalypse.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

import java.util.Comparator;
import java.util.List;

public class LingeringFractalEntity extends AbstractFractalEntity {
	public LingeringFractalEntity(PlayMessages.SpawnEntity packet, Level world) {
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
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		if (tickCount > 1200 && tickCount % 20 == 7) {
			if (world.getEntitiesOfClass(LineringPathshaperEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).isEmpty()) {
				final Vec3 _center = new Vec3(x, y, z);
				List<LingeringFractalEntity> _entfound = world.getEntitiesOfClass(LingeringFractalEntity.class, new AABB(_center, _center).inflate(64 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
				for (LingeringFractalEntity fractal : _entfound) {
					fractal.discard();
				}
				if (world instanceof ServerLevel level) {
					Entity entityToSpawn = CAEntities.LINGERING_PATHSHAPER.get().spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
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
		builder = builder.add(Attributes.ATTACK_DAMAGE, 7);
		builder = builder.add(Attributes.FOLLOW_RANGE, 32);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
		return builder;
	}
}
