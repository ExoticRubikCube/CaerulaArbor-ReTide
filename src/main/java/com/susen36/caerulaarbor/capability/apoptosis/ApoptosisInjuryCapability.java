package com.susen36.caerulaarbor.capability.apoptosis;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class ApoptosisInjuryCapability implements IApoptosisInjuryCapability {

    private final LivingEntity owner;
    private double value;
    private boolean recovering;
    private boolean locked;

    public ApoptosisInjuryCapability(LivingEntity owner) {
        this(owner, 1000);
    }

    public ApoptosisInjuryCapability(LivingEntity owner, double value) {
        this.owner = owner;
        this.value = Math.clamp(value, 0, 1000);
        this.recovering = false;
        this.locked = false;
    }

    public double getValue() {
        return value;
    }

    public boolean isLocked() {
        return locked;
    }

    public void lockToMax() {
        value = 1000;
        locked = true;
    }

    public void unlock() {
        locked = false;
    }

    @Override
    public boolean hurt(double amount) {
        if (locked || recovering || amount <= 0) {
            return false;
        }
        value -= amount;
        if (value <= 0) {
            apoptosisBreak();
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
        value = Math.min(value + amount, 1000);
    }

    @Override
    public void tick() {
        if (recovering) {
            boolean fast = owner.hasEffect(CAMobEffects.ESSENCE_RESISTANCE);
            double step = 1000.0 / (fast ? 100.0 : 200.0);
            value = Math.min(1000.0, value + step);
            if (value >= 1000.0) {
                value = 1000.0;
                recovering = false;
                ModCapabilities.getSanityInjury(owner).unlock();
            }
        }
    }

    private void apoptosisBreak() {
        ModCapabilities.getSanityInjury(owner).lockToMax();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("ApoptosisInjury", value);
        tag.putBoolean("ApoptosisRecovering", recovering);
        tag.putBoolean("ApoptosisLocked", locked);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        value = nbt.getDouble("ApoptosisInjury");
        recovering = nbt.getBoolean("ApoptosisRecovering");
        locked = nbt.getBoolean("ApoptosisLocked");
    }
}