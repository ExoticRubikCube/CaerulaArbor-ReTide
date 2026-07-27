package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAParticles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.LevelAccessor;

public class ImmortalMobEffect extends MobEffect {
    public ImmortalMobEffect() {
        super(MobEffectCategory.NEUTRAL, -4648944);
        this.addAttributeModifier(CAAttributes.SANITY_RESISTANCE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "immortal_sanity_resistance"), 100, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "immortal_knockback_resistance"), 10, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public void addAttributeModifiers(AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(attributeMap, amplifier);
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        super.onEffectAdded(entity, amplifier);
        entity.getPersistentData().putBoolean("immortalTriggered", false);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        if (entity == null)
             return true;
        double ang;
        double phase = 0;
        if ((Entity) entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
            livingEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20, 0, false, false));
        if ((Entity) entity instanceof LivingEntity livingEntity)
            livingEntity.removeEffect(MobEffects.POISON);
        if ((Entity) entity instanceof LivingEntity livingEntity)
            livingEntity.removeEffect(MobEffects.WITHER);
        entity.invulnerableTime = 10;
        ang = Mth.nextDouble(RandomSource.create(), 0, 6.283);
        if (world instanceof ServerLevel level)
            level.sendParticles(CAParticles.IMMORTAL_PTC.get(), (entity.getX() + 1.5 * Math.sin(ang)), (entity.getY() + 1.25), (entity.getZ() + 1.5 * Math.cos(ang)), 1, 0.1, 2, 0.1, 0.2);
        return true;
    }

    @Override
    public void onMobRemoved(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        super.onMobRemoved(entity, amplifier, reason);
        LevelAccessor world = entity.level();
        if (entity == null)
            return;
        if (entity.getPersistentData().getBoolean("immortalTriggered")) {
            ((Entity) entity).hurt(CADamageTypes.source(world, CADamageTypes.IMMORTAL_PUNISHMENT), 114514);
        }
        entity.getPersistentData().putBoolean("immortalTriggered", false);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}