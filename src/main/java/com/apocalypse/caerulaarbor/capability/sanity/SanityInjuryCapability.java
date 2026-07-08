package com.apocalypse.caerulaarbor.capability.sanity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.init.CAConfigs;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CADamageTypes;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;

import java.util.Optional;

public class SanityInjuryCapability implements ISanityInjuryCapability {
    public static final ResourceLocation ID = new ResourceLocation(CaerulaArborMod.MODID, "sanity_injury");

    private final LivingEntity owner;
    private double value;
    private boolean recovering;
    private boolean locked;

    public SanityInjuryCapability(LivingEntity owner) {
        this(owner, 1000);
    }

    public SanityInjuryCapability(LivingEntity owner, double value) {
        this.owner = owner;
        this.value = Math.max(0, Math.min(1000, value));
        this.recovering = false;
        this.locked = false;
    }

    @Override
    public boolean hurt(double damage) {
        if (locked || recovering || damage <= 0) {
            return false;
        }
        double sanityResistance = Optional.ofNullable(owner.getAttribute(CAAttributes.SANITY_RESISTANCE.get()))
                .map(AttributeInstance::getValue)
                .orElse(0D);
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
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            value = Math.min(value + event.getAmount(), 1000);
        }
    }

    @Override
    public void tick() {
        if (recovering) {
            boolean fast = owner.hasEffect(CAMobEffects.ESSENCE_RESISTANCE.get());
            double step = 1000.0 / (fast ? 100.0 : 200.0);
            value = Math.min(1000.0, value + step);
            if (value >= 1000.0) {
                value = 1000.0;
                recovering = false;
                ModCapabilities.getApoptosisInjury(owner).unlock();
            }
        }
    }

    public double getValue() {
        return value;
    }

    public void lockToMax() {
        value = 1000;
        locked = true;
    }

    public void unlock() {
        locked = false;
    }

    private void sanityBreak() {
        SanityEvent.Break event = new SanityEvent.Break(owner);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return;
        }
        if (owner.level().isClientSide()) {
            owner.level().playLocalSound(owner.getX(), owner.getY(), owner.getZ(), SoundEvents.ELDER_GUARDIAN_CURSE,
                    owner.getSoundSource(), 2.2f, 1, false);
            return;
        }

        float baseDamage = CAConfigs.SANITY_BREAK.get().floatValue();
        DamageSource sanityBreakDamage = CADamageTypes.source(owner.level(), CADamageTypes.SANITY_BREAK);

        owner.addEffect(new MobEffectInstance(CAMobEffects.UNDER_BREAK.get(), 200, 0, false, false, true));
        if (owner instanceof Player player) {
            //TODO 抵抗效果已通过Mixin适配所有debuff，此处重复
//            int dizzyDuration = 200;
//            MobEffectInstance essenceResistance = player.getEffect(CAMobEffects.ESSENCE_RESISTANCE.get());
//            if (essenceResistance != null) {
//                int level = Math.min(5, essenceResistance.getAmplifier() + 1);
//                dizzyDuration = Math.max(1, (int) Math.ceil(dizzyDuration * (1.0 - level * 0.10)));
//            }
            player.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 200, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0, false, true));
            player.hurt(sanityBreakDamage, baseDamage);
        } else {
            if (owner.getAttributes().hasAttribute(CAAttributes.NUMB.get())) {
                owner.getAttribute(CAAttributes.NUMB.get()).setBaseValue(Math.max(
                        owner.getAttribute(CAAttributes.NUMB.get()).getBaseValue(), 3));
            }
            owner.hurt(sanityBreakDamage, (float) Math.min(Math.max(owner.getMaxHealth() * 0.8F, baseDamage), baseDamage * 6));
        }

        owner.level().playSound(owner instanceof Player player ? player : null,
                owner.getX(), owner.getY(), owner.getZ(),
                SoundEvents.ELDER_GUARDIAN_CURSE, owner.getSoundSource(), 2.2f, 1);
        ModCapabilities.getApoptosisInjury(owner).lockToMax();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("SanityInjury", value);
        tag.putBoolean("SanityRecovering", recovering);
        tag.putBoolean("SanityLocked", locked);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        value = nbt.getDouble("SanityInjury");
        recovering = nbt.getBoolean("SanityRecovering");
        locked = nbt.getBoolean("SanityLocked");
    }
}
