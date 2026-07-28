package com.susen36.caerulaarbor.capability.sanity;

import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAConfigs;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;

public class SanityInjuryCapability implements ISanityInjuryCapability {
    private static final double DEFAULT_MAX_SANITY = 1000.0;

    private final LivingEntity owner;
    private double value;
    private boolean recovering;
    private boolean locked;

    public SanityInjuryCapability(LivingEntity owner) {
        this.owner = owner;
        this.value = getMaxValue();
        this.recovering = false;
        this.locked = false;
    }

    @Override
    public boolean hurt(double damage) {
        if (locked || recovering || damage <= 0) {
            return false;
        }

        if (owner instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return false;
        }

        AttributeInstance sanityResistanceAttribute = owner.getAttribute(CAAttributes.SANITY_RESISTANCE);
        double sanityResistance = sanityResistanceAttribute == null ? 0.0 : sanityResistanceAttribute.getValue();
        damage *= 1 - sanityResistance / 100;
        if (damage <= 0) {
            return false;
        }

        value -= damage;
        if (value <= 0) {
            sanityBreak();
            value = 0;
            recovering = true;
        }
        return true;
    }

    @Override
    public void heal(double amount) {
        if (locked || recovering || amount <= 0) {
            return;
        }
        SanityEvent.Heal event = new SanityEvent.Heal(owner, amount);
        if (!NeoForge.EVENT_BUS.post(event).isCanceled()) {
            value = Math.min(value + event.getAmount(), getMaxValue());
        }
    }

    @Override
    public void tick() {
        if (recovering) {
            boolean fast = owner.hasEffect(CAMobEffects.ESSENCE_RESISTANCE);
            double maxValue = getMaxValue();
            double step = maxValue / (fast ? 100.0 : 200.0);
            value = Math.min(maxValue, value + step);
            if (value >= maxValue) {
                value = maxValue;
                recovering = false;
                ModCapabilities.getApoptosisInjury(owner).unlock();
            }
        }
    }

    public double getValue() {
        value = Math.min(value, getMaxValue());
        return value;
    }

    public double getMaxValue() {
        AttributeInstance maxSanityAttribute = owner.getAttribute(CAAttributes.MAX_SANITY);
        return Math.max(1.0, maxSanityAttribute == null ? DEFAULT_MAX_SANITY : maxSanityAttribute.getValue());
    }

    public void lockToMax() {
        value = getMaxValue();
        locked = true;
    }

    public void unlock() {
        locked = false;
    }

    private void sanityBreak() {
        SanityEvent.Break event = new SanityEvent.Break(owner);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return;
        }
        if (owner.level().isClientSide()) {
            owner.level().playLocalSound(owner.getX(), owner.getY(), owner.getZ(), SoundEvents.ELDER_GUARDIAN_CURSE,
                    owner.getSoundSource(), 2.2f, 1, false);
            return;
        }

        float baseDamage = CAConfigs.SANITY_BREAK.get().floatValue();
        DamageSource sanityBreakDamage = CADamageTypes.source(owner.level(), CADamageTypes.SANITY_BREAK);

        owner.addEffect(new MobEffectInstance(CAMobEffects.UNDER_BREAK, 200, 0, false, false, true));
        if (owner instanceof Player player) {
            player.addEffect(new MobEffectInstance(CAMobEffects.DIZZY, 200, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0, false, true));
            player.hurt(sanityBreakDamage, baseDamage);
        } else {
            AttributeInstance numbAttribute = owner.getAttribute(CAAttributes.NUMB);
            if (numbAttribute != null) {
                numbAttribute.setBaseValue(Math.max(numbAttribute.getBaseValue(), 3));
            }
            owner.hurt(sanityBreakDamage, Math.min(Math.max(owner.getMaxHealth() * 0.8F, baseDamage), baseDamage * 6));
        }

        owner.level().playSound(owner instanceof Player player ? player : null,
                owner.getX(), owner.getY(), owner.getZ(),
                SoundEvents.ELDER_GUARDIAN_CURSE, owner.getSoundSource(), 2.2f, 1);
        ModCapabilities.getApoptosisInjury(owner).lockToMax();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("SanityInjury", value);
        tag.putBoolean("SanityRecovering", recovering);
        tag.putBoolean("SanityLocked", locked);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        value = Math.max(0, Math.min(getMaxValue(), nbt.getDouble("SanityInjury")));
        recovering = nbt.getBoolean("SanityRecovering");
        locked = nbt.getBoolean("SanityLocked");
    }
}