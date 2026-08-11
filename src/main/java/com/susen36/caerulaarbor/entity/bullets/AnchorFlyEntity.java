package com.susen36.caerulaarbor.entity.bullets;

import com.susen36.babel.init.BabelMobEffects;
import com.susen36.caerulaarbor.entity.base.BaseProjectile;
import com.susen36.caerulaarbor.init.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Comparator;
import java.util.List;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class AnchorFlyEntity extends BaseProjectile implements ItemSupplier {
	public static final ItemStack PROJECTILE_ITEM = new ItemStack(CAItems.UNAMBIGUOUS_DIRECTION.get());
	public AnchorFlyEntity(Level world) {
		super(CAEntities.ANCHOR_FLY.get(), world);
	}

	public AnchorFlyEntity(EntityType<? extends AnchorFlyEntity> type, Level world) {
		super(type, world);
	}

	public AnchorFlyEntity(EntityType<? extends AnchorFlyEntity> type, double x, double y, double z, Level world) {
		super(type, world);
		moveTo(x, y, z);
	}

	public AnchorFlyEntity(EntityType<? extends AnchorFlyEntity> type, LivingEntity entity, Level world) {
		super(type, entity, world);
	}


	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getItem() {
		return PROJECTILE_ITEM;
	}

	@Override
	public void onHitEntity(EntityHitResult entityHitResult) {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity entity = entityHitResult.getEntity();
        Entity sourceentity = this.getOwner();
        if (sourceentity == null)
            return;
        double perc;
        if (!(entity == sourceentity)) {
            perc = (sourceentity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) / (sourceentity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1);
            if (sourceentity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                livingEntity.addEffect(new MobEffectInstance(CAMobEffects.PATH_TO_UNCOVER, 500, 0, false, false));
            if (sourceentity instanceof LivingEntity livingEntity)
                livingEntity.setHealth((float) ((sourceentity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * perc));
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 72, 3, 3, 3, 0.5);
            {
                final Vec3 center = new Vec3(x, y, z);
                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(12 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                for (Entity entityiterator : entfound) {
                    if (entityiterator == sourceentity) {
                        continue;
                    }
                    if ((entityiterator instanceof TamableAnimal tamEnt ? (Entity) tamEnt.getOwner() : null) == sourceentity) {
                        continue;
                    }
                    if (!(entityiterator instanceof Mob)) {
                        continue;
                    }
                    if (new Vec3((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ())).distanceTo(new Vec3(x, y, z)) <= 6) {
                        entityiterator.hurt(
                                CADamageTypes.source(world, CADamageTypes.ANCHOR_SMASH, sourceentity), (float) ((sourceentity instanceof LivingEntity livingEntity16 && livingEntity16.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity16.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1.5));
                        if (entityiterator instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                            livingEntity.addEffect(new MobEffectInstance(BabelMobEffects.STUN, 120, 0, false, false));
                    }
                }
            }
            sourceentity.teleportTo(x, y, z);
            if (sourceentity instanceof ServerPlayer serverPlayer)
                serverPlayer.connection.teleport(x, y, z, sourceentity.getYRot(), sourceentity.getXRot());
            if (sourceentity instanceof LivingEntity livingEntity)
                livingEntity.removeEffect(BabelMobEffects.STUN);
            if (sourceentity instanceof LivingEntity livingEntity)
                livingEntity.removeEffect(MobEffects.DIG_SLOWDOWN);
            if (sourceentity instanceof LivingEntity livingEntity)
                livingEntity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), CASounds.ANCHOR_SKILL.get(), SoundSource.PLAYERS, 3, 1);
            }
            if (!level().isClientSide())
                discard();
        }
    }

	@Override
	public void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        LevelAccessor world = this.level();
        double x = blockHitResult.getBlockPos().getX();
        double y = blockHitResult.getBlockPos().getY();
        double z = blockHitResult.getBlockPos().getZ();
        Entity entity = this.getOwner();
        if (entity == null)
            return;
        double perc;
        perc = (entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) / (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1);
        if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
            livingEntity.addEffect(new MobEffectInstance(CAMobEffects.PATH_TO_UNCOVER, 500, 0, false, false));
        if (entity instanceof LivingEntity livingEntity)
            livingEntity.setHealth((float) (livingEntity.getMaxHealth() * perc));
        if (world instanceof ServerLevel level)
            level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 72, 3, 3, 3, 0.5);
        {
            final Vec3 center = new Vec3(x, y, z);
            List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(12 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
            for (Entity entityiterator : entfound) {
                if (entityiterator == entity) {
                    continue;
                }
                if ((entityiterator instanceof TamableAnimal tamEnt ? (Entity) tamEnt.getOwner() : null) == entity) {
                    continue;
                }
                if (!(entityiterator instanceof Mob)) {
                    continue;
                }
                if (new Vec3((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ())).distanceTo(new Vec3(x, y, z)) <= 6) {
                    entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.ANCHOR_SMASH, entity), (float) ((entity instanceof LivingEntity livingEntity15 && livingEntity15.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity15.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1.5));
                    if (entityiterator instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                        livingEntity.addEffect(new MobEffectInstance(BabelMobEffects.STUN, 120, 0, false, false));
                }
            }
        }
        entity.teleportTo((getX()), (getY()), (getZ()));
        if (entity instanceof ServerPlayer serverPlayer)
            serverPlayer.connection.teleport((getX()), (getY()), (getZ()), entity.getYRot(), entity.getXRot());
        if (entity instanceof LivingEntity living) {
            living.removeEffect(BabelMobEffects.STUN);
            living.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            living.removeEffect(MobEffects.DIG_SLOWDOWN);
        }
        if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), CASounds.ANCHOR_SKILL.get(), SoundSource.PLAYERS, 3, 1);
        }
    }

	@Override
	public void tick() {
		super.tick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity entity = this.getOwner();
        if (entity != null) {
            double perc;
            if (tickCount >= 160) {
                perc = (entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) / (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1);
                if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                    livingEntity.addEffect(new MobEffectInstance(CAMobEffects.PATH_TO_UNCOVER, 500, 0, false, false));
                if (entity instanceof LivingEntity livingEntity)
                    livingEntity.setHealth((float) (livingEntity.getMaxHealth() * perc));
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 72, 3, 3, 3, 0.5);
                {
                    final Vec3 center = new Vec3(x, y, z);
                    List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(12 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                    for (Entity entityiterator : entfound) {
                        if (entityiterator == entity) {
                            continue;
                        }
                        if ((entityiterator instanceof TamableAnimal tamEnt ? (Entity) tamEnt.getOwner() : null) == entity) {
                            continue;
                        }
                        if (!(entityiterator instanceof Mob)) {
                            continue;
                        }
                        if (entityiterator instanceof Player) {
                            continue;
                        }
                        if (new Vec3((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ())).distanceTo(new Vec3(x, y, z)) <= 6) {
                            entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.ANCHOR_SMASH, entity), (float) ((entity instanceof LivingEntity livingEntity16 && livingEntity16.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity16.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1.5));
                            if (entityiterator instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                                livingEntity.addEffect(new MobEffectInstance(BabelMobEffects.STUN, 120, 0, false, false));
                        }
                    }
                }
        if (entity instanceof LivingEntity livingEntity)
            livingEntity.removeEffect(BabelMobEffects.STUN);
        if (entity instanceof LivingEntity livingEntity)
            livingEntity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        if (entity instanceof LivingEntity livingEntity)
            livingEntity.removeEffect(MobEffects.DIG_SLOWDOWN);
        if (!level().isClientSide())
                    discard();
                if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.ANCHOR_SKILL.get(), SoundSource.PLAYERS, (float) 2.5, 1);
                }
            }
        }
        if (this.inGround)
			this.discard();
	}

	public static AnchorFlyEntity shoot(Level world, LivingEntity entity, RandomSource source) {
		return shoot(world, entity, source, 2f, 10, 0);
	}

	public static AnchorFlyEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
		return shoot(world, entity, source, pullingPower * 2f, 10, 0);
	}

	public static AnchorFlyEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
		AnchorFlyEntity entityarrow = new AnchorFlyEntity(CAEntities.ANCHOR_FLY.get(), entity, world);
		entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
		entityarrow.setSilent(true);
		entityarrow.setCritArrow(false);
		entityarrow.setBaseDamage(damage);
		entityarrow.setKnockback(knockback);
		world.addFreshEntity(entityarrow);
		world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), CASounds.ANCHOR_THROW.get(), SoundSource.PLAYERS, 1, 1f / (random.nextFloat() * 0.5f + 1) + (power / 2));
		return entityarrow;
	}

	public static AnchorFlyEntity shoot(LivingEntity entity, LivingEntity target) {
		AnchorFlyEntity entityarrow = new AnchorFlyEntity(CAEntities.ANCHOR_FLY.get(), entity, entity.level());
		double dx = target.getX() - entity.getX();
		double dy = target.getY() + target.getEyeHeight() - 1.1;
		double dz = target.getZ() - entity.getZ();
		entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, 2f * 2, 12.0F);
		entityarrow.setSilent(true);
		entityarrow.setBaseDamage(10);
		entity.level().addFreshEntity(entityarrow);
		entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), CASounds.ANCHOR_THROW.get(), SoundSource.PLAYERS, 1,
				1f / (RandomSource.create().nextFloat() * 0.5f + 1));
		return entityarrow;
	}
}
