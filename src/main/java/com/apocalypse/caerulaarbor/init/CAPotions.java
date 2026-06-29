/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CAPotions {
	public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(ForgeRegistries.POTIONS, CaerulaArborMod.MODID);
	public static final RegistryObject<Potion> SANITY_IMMUE_POTION = REGISTRY.register("sanity_immue_potion", () -> new Potion(new MobEffectInstance(CAMobEffects.SANITY_IMMUE.get(), 400, 0, false, true)));
	public static final RegistryObject<Potion> INST_SANITY = REGISTRY.register("inst_sanity", () -> new Potion(new MobEffectInstance(CAMobEffects.INSTANT_SANITY.get(), 1, 0, false, true)));
	public static final RegistryObject<Potion> SANITY_CURE = REGISTRY.register("sanity_cure", () -> new Potion(new MobEffectInstance(CAMobEffects.SANITY_HEAL.get(), 1, 0, false, true)));
	public static final RegistryObject<Potion> INST_SANITY_II = REGISTRY.register("inst_sanity_ii", () -> new Potion(new MobEffectInstance(CAMobEffects.INSTANT_SANITY.get(), 1, 1, false, true)));
	public static final RegistryObject<Potion> SANITY_CURE_II = REGISTRY.register("sanity_cure_ii", () -> new Potion(new MobEffectInstance(CAMobEffects.SANITY_HEAL.get(), 1, 1, false, true)));
	public static final RegistryObject<Potion> FAST_SWIM_POTION = REGISTRY.register("fast_swim_potion", () -> new Potion(new MobEffectInstance(CAMobEffects.FAST_SWIM.get(), 1200, 0, false, true)));
	public static final RegistryObject<Potion> FAST_SWIM_POTION_II = REGISTRY.register("fast_swim_potion_ii", () -> new Potion(new MobEffectInstance(CAMobEffects.FAST_SWIM.get(), 800, 1, false, true)));
	public static final RegistryObject<Potion> FAST_SWIM_POTION_LONG = REGISTRY.register("fast_swim_potion_long", () -> new Potion(new MobEffectInstance(CAMobEffects.FAST_SWIM.get(), 2400, 0, false, true)));
	public static final RegistryObject<Potion> FAST_SWIM_POTION_III = REGISTRY.register("fast_swim_potion_iii", () -> new Potion(new MobEffectInstance(CAMobEffects.FAST_SWIM.get(), 720, 2, false, true)));
	public static final RegistryObject<Potion> PERCENTAGE_REGENERATION = REGISTRY.register("percentage_regeneration", () -> new Potion(new MobEffectInstance(CAMobEffects.REGENERATION_PERCLY.get(), 480, 0, false, true)));
	public static final RegistryObject<Potion> PERCENTAGE_REGENERATION_II = REGISTRY.register("percentage_regeneration_ii", () -> new Potion(new MobEffectInstance(CAMobEffects.REGENERATION_PERCLY.get(), 400, 1, false, true)));
	public static final RegistryObject<Potion> LONG_SNT_IMMUE = REGISTRY.register("long_snt_immue", () -> new Potion(new MobEffectInstance(CAMobEffects.SANITY_IMMUE.get(), 2400, 0, false, true)));
}
