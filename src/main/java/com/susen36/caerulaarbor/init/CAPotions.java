package com.susen36.caerulaarbor.init;

import com.susen36.caerulaarbor.CaerulaArborMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CAPotions {
    public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(BuiltInRegistries.POTION, CaerulaArborMod.MODID);

    public static final DeferredHolder<Potion, ? extends Potion> SANITY_IMMUE_POTION = REGISTRY.register("sanity_immue_potion", () -> new Potion(new MobEffectInstance(CAMobEffects.SANITY_IMMUE.get(), 400, 0, false, true)));
    public static final DeferredHolder<Potion, ? extends Potion> INST_SANITY = REGISTRY.register("inst_sanity", () -> new Potion(new MobEffectInstance(CAMobEffects.INSTANT_SANITY.get(), 1, 0, false, true)));
    public static final DeferredHolder<Potion, ? extends Potion> SANITY_CURE = REGISTRY.register("sanity_cure", () -> new Potion(new MobEffectInstance(CAMobEffects.SANITY_HEAL.get(), 1, 0, false, true)));
    public static final DeferredHolder<Potion, ? extends Potion> INST_SANITY_II = REGISTRY.register("inst_sanity_ii", () -> new Potion(new MobEffectInstance(CAMobEffects.INSTANT_SANITY.get(), 1, 1, false, true)));
    public static final DeferredHolder<Potion, ? extends Potion> SANITY_CURE_II = REGISTRY.register("sanity_cure_ii", () -> new Potion(new MobEffectInstance(CAMobEffects.SANITY_HEAL.get(), 1, 1, false, true)));
    public static final DeferredHolder<Potion, ? extends Potion> FAST_SWIM_POTION = REGISTRY.register("fast_swim_potion", () -> new Potion(new MobEffectInstance(CAMobEffects.FAST_SWIM.get(), 1200, 0, false, true)));
    public static final DeferredHolder<Potion, ? extends Potion> FAST_SWIM_POTION_II = REGISTRY.register("fast_swim_potion_ii", () -> new Potion(new MobEffectInstance(CAMobEffects.FAST_SWIM.get(), 800, 1, false, true)));
    public static final DeferredHolder<Potion, ? extends Potion> FAST_SWIM_POTION_LONG = REGISTRY.register("fast_swim_potion_long", () -> new Potion(new MobEffectInstance(CAMobEffects.FAST_SWIM.get(), 2400, 0, false, true)));
    public static final DeferredHolder<Potion, ? extends Potion> FAST_SWIM_POTION_III = REGISTRY.register("fast_swim_potion_iii", () -> new Potion(new MobEffectInstance(CAMobEffects.FAST_SWIM.get(), 720, 2, false, true)));
    public static final DeferredHolder<Potion, ? extends Potion> PERCENTAGE_REGENERATION = REGISTRY.register("percentage_regeneration", () -> new Potion(new MobEffectInstance(CAMobEffects.REGENERATION_PERCLY.get(), 480, 0, false, true)));
    public static final DeferredHolder<Potion, ? extends Potion> PERCENTAGE_REGENERATION_II = REGISTRY.register("percentage_regeneration_ii", () -> new Potion(new MobEffectInstance(CAMobEffects.REGENERATION_PERCLY.get(), 400, 1, false, true)));
    public static final DeferredHolder<Potion, ? extends Potion> LONG_SNT_IMMUE = REGISTRY.register("long_snt_immue", () -> new Potion(new MobEffectInstance(CAMobEffects.SANITY_IMMUE.get(), 2400, 0, false, true)));
}
