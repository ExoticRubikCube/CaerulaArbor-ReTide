package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.util.MathUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class WipeDustsMobEffect extends MobEffect {
    public WipeDustsMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -6684724);
    }


    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        if ((Entity) entity instanceof LivingEntity livEnt0 && livEnt0.hasEffect(MobEffects.REGENERATION) && ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(Items.BRUSH)))) {
            {
                final Vec3 center = new Vec3(entity.getX(), entity.getY(), entity.getZ());
                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(10 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                for (Entity entityiterator : entfound) {
                    if (entityiterator instanceof Monster) {
                        entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.WIPE_MAGIC, entity), (float) (5 * (1 + ((Entity) entity instanceof LivingEntity livEnt && livEnt.hasEffect(MobEffects.REGENERATION) ? livEnt.getEffect(MobEffects.REGENERATION).getAmplifier() : 0))));
                        if (world instanceof ServerLevel level)
                            level.sendParticles(ParticleTypes.WAX_OFF, (entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), 48, 0.8, 1, 0.8, 0.1);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 20);
    }

}