/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.enchantment.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CaerulaArborModEnchantments {
	public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, CaerulaArborMod.MODID);
	public static final RegistryObject<Enchantment> OCEANOSPR_KILLER = REGISTRY.register("oceanospr_killer", () -> new OceanosprKillerEnchantment());
	public static final RegistryObject<Enchantment> SANITY_REAPER = REGISTRY.register("sanity_reaper", () -> new SanityReaperEnchantment());
	public static final RegistryObject<Enchantment> SANITY_DEFEND = REGISTRY.register("sanity_defend", () -> new SanityDefendEnchantment());
	public static final RegistryObject<Enchantment> REFLECTION = REGISTRY.register("reflection", () -> new ReflectionEnchantment());
	public static final RegistryObject<Enchantment> SYNESTHESIA = REGISTRY.register("synesthesia", () -> new SynesthesiaEnchantment());
	public static final RegistryObject<Enchantment> METABOLISM = REGISTRY.register("metabolism", () -> new MetabolismEnchantment());
	public static final RegistryObject<Enchantment> MUTE_ATTACK = REGISTRY.register("mute_attack", () -> new MuteAttackEnchantment());
	public static final RegistryObject<Enchantment> NETHERSEA_WALKER = REGISTRY.register("nethersea_walker", () -> new NetherseaWalkerEnchantment());
	public static final RegistryObject<Enchantment> FLEXIBILITY = REGISTRY.register("flexibility", () -> new FlexibilityEnchantment());
	public static final RegistryObject<Enchantment> MAGIC_TOLERANCE = REGISTRY.register("magic_tolerance", () -> new MagicToleranceEnchantment());
	public static final RegistryObject<Enchantment> HAZARD_PROTECTION = REGISTRY.register("hazard_protection", () -> new HazardProtectionEnchantment());
	public static final RegistryObject<Enchantment> SANITY_INJURY_CURSE = REGISTRY.register("sanity_injury_curse", () -> new SanityInjuryCurseEnchantment());
	public static final RegistryObject<Enchantment> REJECTION_CURSE = REGISTRY.register("rejection_curse", () -> new RejectionCurseEnchantment());
}
