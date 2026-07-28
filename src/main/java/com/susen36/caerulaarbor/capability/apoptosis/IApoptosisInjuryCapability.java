package com.susen36.caerulaarbor.capability.apoptosis;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface IApoptosisInjuryCapability extends INBTSerializable<CompoundTag> {
    boolean hurt(double amount);

    void heal(double amount);

    void tick();
}