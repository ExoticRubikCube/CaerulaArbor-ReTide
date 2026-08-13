
package com.susen36.caerulaarbor.potion;

import com.susen36.babel.util.EPUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class PetReapMobEffect extends MobEffect {
    public PetReapMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -6710785);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        double angle;
        if (entity instanceof Mob mobEnt0 && mobEnt0.isAggressive() && entity.isAlive()) {
            for (int index0 = 0; index0 < 120; index0++) {
                angle = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.ELECTRIC_SPARK, (x + 2.5 * Math.sin(angle)), y, (z + 2.5 * Math.cos(angle)), 8, 0.1, 0.1, 0.1, 0.2);
            }
            final Vec3 center = new Vec3(x, y, z);
            List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
            for (LivingEntity entityiterator : entfound) {
                if (entityiterator instanceof Monster) {
                    if (!(entityiterator == entity)) {
                        EPUtils.causeSanityInjury(entityiterator,
                                entity,
                                (entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 12);
                    }
                }
                if (((Entity) entity instanceof TamableAnimal tamEnt ? (Entity) tamEnt.getOwner() : null) == entityiterator && !entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 0));
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return (double) duration % (double) 20 == 0;
    }
}