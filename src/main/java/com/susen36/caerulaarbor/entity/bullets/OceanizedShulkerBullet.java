package com.susen36.caerulaarbor.entity.bullets;

import com.google.common.base.MoreObjects;
import com.susen36.babel.effect.LessArmorMobEffect;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class OceanizedShulkerBullet extends ShulkerBullet {
    public OceanizedShulkerBullet(Level level, LivingEntity owner, Entity target, Direction.Axis axis) {
        super(level, owner, target, axis);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity target = hitResult.getEntity();
        if (target instanceof LivingEntity living && !living.level().isClientSide()) {
            LessArmorMobEffect.apply(living);
            living.addEffect(new MobEffectInstance(CAMobEffects.MORE_FALL_DAMAGE, 300, 0));
        }
        Entity owner = this.getOwner();
        LivingEntity ownerLiving = owner instanceof LivingEntity ? (LivingEntity) owner : null;
        double damage = (ownerLiving != null && ownerLiving.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                ? ownerLiving.getAttributeValue(Attributes.ATTACK_DAMAGE)
                : 0) * (5.0 / 14.0);
        DamageSource damagesource = this.damageSources().mobProjectile(this, ownerLiving);
        if (target.hurt(damagesource, (float) damage)) {
            if (this.level() instanceof ServerLevel serverlevel) {
                EnchantmentHelper.doPostAttackEffects(serverlevel, target, damagesource);
            }
            if (target instanceof LivingEntity livingentity1) {
                livingentity1.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200), MoreObjects.firstNonNull(owner, this));
            }
        }
    }
}
