package com.apocalypse.caerulaarbor.entity.bullets;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class AnchorFlyEntity extends AbstractArrow implements ItemSupplier {
	public static final ItemStack PROJECTILE_ITEM = new ItemStack(CAItems.UNAMBIGUOUS_DIRECTION.get());

	public AnchorFlyEntity(PlayMessages.SpawnEntity packet, Level world) {
		super(CAEntities.ANCHOR_FLY.get(), world);
	}

	public AnchorFlyEntity(EntityType<? extends AnchorFlyEntity> type, Level world) {
		super(type, world);
	}

	public AnchorFlyEntity(EntityType<? extends AnchorFlyEntity> type, double x, double y, double z, Level world) {
		super(type, x, y, z, world);
	}

	public AnchorFlyEntity(EntityType<? extends AnchorFlyEntity> type, LivingEntity entity, Level world) {
		super(type, entity, world);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getItem() {
		return PROJECTILE_ITEM;
	}

	@Override
	protected ItemStack getPickupItem() {
		return PROJECTILE_ITEM;
	}

	@Override
	protected void doPostHurtEffects(LivingEntity entity) {
		super.doPostHurtEffects(entity);
		entity.setArrowCount(entity.getArrowCount() - 1);
	}

	@Override
	public void onHitEntity(EntityHitResult entityHitResult) {
		super.onHitEntity(entityHitResult);
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
            perc = (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) / (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1);
            if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CAMobEffects.PATH_TO_UNCOVER.get(), 500, 0, false, false));
            if (sourceentity instanceof LivingEntity _entity)
                _entity.setHealth((float) ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * perc));
            if (world instanceof ServerLevel _level)
                _level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 72, 3, 3, 3, 0.5);
            {
                final Vec3 _center = new Vec3(x, y, z);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(12 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (entityiterator == sourceentity) {
                        continue;
                    }
                    if ((entityiterator instanceof TamableAnimal _tamEnt ? (Entity) _tamEnt.getOwner() : null) == sourceentity) {
                        continue;
                    }
                    if (!(entityiterator instanceof Mob)) {
                        continue;
                    }
                    if (new Vec3((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ())).distanceTo(new Vec3(x, y, z)) <= 6) {
                        entityiterator.hurt(
                                new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "anchor_smash"))), sourceentity),
                                (float) ((sourceentity instanceof LivingEntity _livingEntity16 && _livingEntity16.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity16.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1.5));
                        if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
                            _entity.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 120, 0, false, false));
                    }
                }
            }
            sourceentity.teleportTo(x, y, z);
            if (sourceentity instanceof ServerPlayer _serverPlayer)
                _serverPlayer.connection.teleport(x, y, z, sourceentity.getYRot(), sourceentity.getXRot());
            if (sourceentity instanceof LivingEntity _entity)
                _entity.removeEffect(CAMobEffects.DIZZY.get());
            if (sourceentity instanceof LivingEntity _entity)
                _entity.removeEffect(MobEffects.DIG_SLOWDOWN);
            if (sourceentity instanceof LivingEntity _entity)
                _entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            if (world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "anchor_skill")), SoundSource.PLAYERS, 3, 1);
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
        perc = (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) / (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1);
        if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
            _entity.addEffect(new MobEffectInstance(CAMobEffects.PATH_TO_UNCOVER.get(), 500, 0, false, false));
        if (entity instanceof LivingEntity _entity)
            _entity.setHealth((float) ((entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * perc));
        if (world instanceof ServerLevel _level)
            _level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 72, 3, 3, 3, 0.5);
        {
            final Vec3 _center = new Vec3(x, y, z);
            List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(12 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
            for (Entity entityiterator : _entfound) {
                if (entityiterator == entity) {
                    continue;
                }
                if ((entityiterator instanceof TamableAnimal _tamEnt ? (Entity) _tamEnt.getOwner() : null) == entity) {
                    continue;
                }
                if (!(entityiterator instanceof Mob)) {
                    continue;
                }
                if (entityiterator instanceof Player) {
                    continue;
                }
                if (new Vec3((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ())).distanceTo(new Vec3(x, y, z)) <= 6) {
                    entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "anchor_smash"))), entity),
                            (float) ((entity instanceof LivingEntity _livingEntity15 && _livingEntity15.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity15.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1.5));
                    if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 120, 0, false, false));
                }
            }
        }
        entity.teleportTo((getX()), (getY()), (getZ()));
        if (entity instanceof ServerPlayer _serverPlayer)
            _serverPlayer.connection.teleport((getX()), (getY()), (getZ()), entity.getYRot(), entity.getXRot());
        if (entity instanceof LivingEntity _entity)
            _entity.removeEffect(CAMobEffects.DIZZY.get());
        if (entity instanceof LivingEntity _entity)
            _entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        if (entity instanceof LivingEntity _entity)
            _entity.removeEffect(MobEffects.DIG_SLOWDOWN);
        if (world instanceof Level _level) {
                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "anchor_skill")), SoundSource.PLAYERS, 3, 1);
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
                perc = (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) / (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1);
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CAMobEffects.PATH_TO_UNCOVER.get(), 500, 0, false, false));
                if (entity instanceof LivingEntity _entity)
                    _entity.setHealth((float) ((entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * perc));
                if (world instanceof ServerLevel _level)
                    _level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 72, 3, 3, 3, 0.5);
                {
                    final Vec3 _center = new Vec3(x, y, z);
                    List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(12 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                    for (Entity entityiterator : _entfound) {
                        if (entityiterator == entity) {
                            continue;
                        }
                        if ((entityiterator instanceof TamableAnimal _tamEnt ? (Entity) _tamEnt.getOwner() : null) == entity) {
                            continue;
                        }
                        if (!(entityiterator instanceof Mob)) {
                            continue;
                        }
                        if (entityiterator instanceof Player) {
                            continue;
                        }
                        if (new Vec3((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ())).distanceTo(new Vec3(x, y, z)) <= 6) {
                            entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "anchor_smash"))), entity),
                                    (float) ((entity instanceof LivingEntity _livingEntity16 && _livingEntity16.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity16.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1.5));
                            if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
                                _entity.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 120, 0, false, false));
                        }
                    }
                }
                if (entity instanceof LivingEntity _entity)
                    _entity.removeEffect(CAMobEffects.DIZZY.get());
                if (entity instanceof LivingEntity _entity)
                    _entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                if (entity instanceof LivingEntity _entity)
                    _entity.removeEffect(MobEffects.DIG_SLOWDOWN);
                if (!level().isClientSide())
                    discard();
                if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "anchor_skill")), SoundSource.PLAYERS, (float) 2.5, 1);
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
		world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "anchor_throw")), SoundSource.PLAYERS, 1, 1f / (random.nextFloat() * 0.5f + 1) + (power / 2));
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
		entityarrow.setKnockback(0);
		entityarrow.setCritArrow(false);
		entity.level().addFreshEntity(entityarrow);
		entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "anchor_throw")), SoundSource.PLAYERS, 1,
				1f / (RandomSource.create().nextFloat() * 0.5f + 1));
		return entityarrow;
	}
}
