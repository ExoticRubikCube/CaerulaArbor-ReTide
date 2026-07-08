package com.apocalypse.caerulaarbor.entity.bullets;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CADamageTypes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import java.util.Comparator;
import java.util.List;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class AbandonedShootEntity extends AbstractArrow implements ItemSupplier {
	public static final ItemStack PROJECTILE_ITEM = new ItemStack(Blocks.BLUE_CANDLE);

	public AbandonedShootEntity(Level world) {
		super(CAEntities.ABANDONED_SHOOT.get(), world);
	}

	public AbandonedShootEntity(EntityType<? extends AbandonedShootEntity> type, Level world) {
		super(type, world);
	}

	public AbandonedShootEntity(EntityType<? extends AbandonedShootEntity> type, double x, double y, double z, Level world) {
		super(type, x, y, z, world);
	}

	public AbandonedShootEntity(EntityType<? extends AbandonedShootEntity> type, LivingEntity entity, Level world) {
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
        entity.invulnerableTime = 0;
        if (world instanceof ServerLevel level)
            level.sendParticles(ParticleTypes.ENCHANTED_HIT, x, y, z, 32, 4, 4, 4, 0.15);
        {
            final Vec3 center = new Vec3(x, y, z);
            List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
            for (Entity entityiterator : entfound) {
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                    continue;
                }
                if (!(entityiterator instanceof LivingEntity)) {
                    continue;
                }
                entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.OCEAN_MAGIC),
                        (float) ((sourceentity instanceof LivingEntity livingEntity4 && livingEntity4.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity4.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1.25));
            }
        }
        if (!level().isClientSide())
            discard();
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
        if (world instanceof ServerLevel level)
            level.sendParticles(ParticleTypes.ENCHANTED_HIT, (x + 0.5), (y + 0.5), (z + 0.5), 32, 4, 4, 4, 0.15);
        {
            final Vec3 center = new Vec3((x + 0.5), (y + 0.5), (z + 0.5));
            List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
            for (Entity entityiterator : entfound) {
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                    continue;
                }
                if (!(entityiterator instanceof LivingEntity)) {
                    continue;
                }
                entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.OCEAN_MAGIC),
                        (float) ((entity instanceof LivingEntity livingEntity3 && livingEntity3.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity3.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1.25));
            }
        }
    }

	@Override
	public void tick() {
		super.tick();
        this.level().addParticle(CAParticles.SEA_SPLASH.get(), this.getX(), this.getY(), this.getZ(), ((-0.05) * getDeltaMovement().x()), ((-0.05) * getDeltaMovement().y()),
                ((-0.05) * getDeltaMovement().z()));
        if (tickCount >= 200) {
            if (!level().isClientSide())
                discard();
        }
        if (this.inGround)
			this.discard();
	}

	public static AbandonedShootEntity shoot(Level world, LivingEntity entity, RandomSource source) {
		return shoot(world, entity, source, 1.5f, 3, 0);
	}

	public static AbandonedShootEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
		return shoot(world, entity, source, pullingPower * 1.5f, 3, 0);
	}

	public static AbandonedShootEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
		AbandonedShootEntity entityarrow = new AbandonedShootEntity(CAEntities.ABANDONED_SHOOT.get(), entity, world);
		entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
		entityarrow.setSilent(true);
		entityarrow.setCritArrow(false);
		entityarrow.setBaseDamage(damage);
		entityarrow.setKnockback(knockback);
		world.addFreshEntity(entityarrow);
		world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.SHULKER_SHOOT, SoundSource.PLAYERS, 1, 1f / (random.nextFloat() * 0.5f + 1) + (power / 2));
		return entityarrow;
	}

	public static AbandonedShootEntity shoot(LivingEntity entity, LivingEntity target) {
		AbandonedShootEntity entityarrow = new AbandonedShootEntity(CAEntities.ABANDONED_SHOOT.get(), entity, entity.level());
		double dx = target.getX() - entity.getX();
		double dy = target.getY() + target.getEyeHeight() - 1.1;
		double dz = target.getZ() - entity.getZ();
		entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, 1.5f * 2, 12.0F);
		entityarrow.setSilent(true);
		entityarrow.setBaseDamage(3);
		entityarrow.setKnockback(0);
		entityarrow.setCritArrow(false);
		entity.level().addFreshEntity(entityarrow);
		entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.SHULKER_SHOOT, SoundSource.PLAYERS, 1, 1f / (RandomSource.create().nextFloat() * 0.5f + 1));
		return entityarrow;
	}
}
