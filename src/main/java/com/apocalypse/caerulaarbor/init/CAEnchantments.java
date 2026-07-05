/*
 *	MCreator 注：此文件会在每次构建时重新生成。
 */
package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.enchantment.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CAEnchantments {
	public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, CaerulaArborMod.MODID);
	public static final RegistryObject<Enchantment> OCEANOSPR_KILLER = REGISTRY.register("oceanospr_killer", OceanosprKillerEnchantment::new);
	public static final RegistryObject<Enchantment> SANITY_REAPER = REGISTRY.register("sanity_reaper", SanityReaperEnchantment::new);
	public static final RegistryObject<Enchantment> SANITY_DEFEND = REGISTRY.register("sanity_defend", SanityDefendEnchantment::new);
	public static final RegistryObject<Enchantment> REFLECTION = REGISTRY.register("reflection", ReflectionEnchantment::new);
	public static final RegistryObject<Enchantment> SYNESTHESIA = REGISTRY.register("synesthesia", SynesthesiaEnchantment::new);
	public static final RegistryObject<Enchantment> METABOLISM = REGISTRY.register("metabolism", MetabolismEnchantment::new);
	public static final RegistryObject<Enchantment> MUTE_ATTACK = REGISTRY.register("mute_attack", MuteAttackEnchantment::new);
	public static final RegistryObject<Enchantment> NETHERSEA_WALKER = REGISTRY.register("nethersea_walker", NetherseaWalkerEnchantment::new);
	public static final RegistryObject<Enchantment> FLEXIBILITY = REGISTRY.register("flexibility", FlexibilityEnchantment::new);
	public static final RegistryObject<Enchantment> MAGIC_TOLERANCE = REGISTRY.register("magic_tolerance", MagicToleranceEnchantment::new);
	public static final RegistryObject<Enchantment> HAZARD_PROTECTION = REGISTRY.register("hazard_protection", HazardProtectionEnchantment::new);
	public static final RegistryObject<Enchantment> SANITY_INJURY_CURSE = REGISTRY.register("sanity_injury_curse", SanityInjuryCurseEnchantment::new);
	public static final RegistryObject<Enchantment> REJECTION_CURSE = REGISTRY.register("rejection_curse", RejectionCurseEnchantment::new);
}
