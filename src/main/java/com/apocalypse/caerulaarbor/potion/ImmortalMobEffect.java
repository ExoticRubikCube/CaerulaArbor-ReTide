package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CADamageTypes;
import com.apocalypse.caerulaarbor.init.CAParticles;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;
import java.util.List;

public class ImmortalMobEffect extends MobEffect {
    public ImmortalMobEffect() {
        super(MobEffectCategory.NEUTRAL, -4648944);
        this.addAttributeModifier(CAAttributes.SANITY_RESISTANCE.get(), "bcdd0e0f-6ece-3c18-9266-a804849cd8fc", 100, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "7626f02f-92bf-3575-935f-e06e87d78bd1", 10, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        if (entity == null)
            return;
        entity.getPersistentData().putBoolean("immortalTriggered", false);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        if (entity == null)
            return;
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
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        LevelAccessor world = entity.level();
        if (entity == null)
            return;
        if (entity.getPersistentData().getBoolean("immortalTriggered")) {
            ((Entity) entity).hurt(CADamageTypes.source(world, CADamageTypes.IMMORTAL_PUNISHMENT), 114514);
        }
        entity.getPersistentData().putBoolean("immortalTriggered", false);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
