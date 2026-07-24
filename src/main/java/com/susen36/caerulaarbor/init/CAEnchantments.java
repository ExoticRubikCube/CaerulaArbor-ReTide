package com.susen36.caerulaarbor.init;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.enchantment.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CAEnchantments {
    public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT, CaerulaArborMod.MODID);
    public static final DeferredHolder<Enchantment, ? extends Enchantment> OCEANOSPR_KILLER = REGISTRY.register("oceanospr_killer", OceanosprKillerEnchantment::new);
    public static final DeferredHolder<Enchantment, ? extends Enchantment> SANITY_REAPER = REGISTRY.register("sanity_reaper", SanityReaperEnchantment::new);
    public static final DeferredHolder<Enchantment, ? extends Enchantment> SANITY_DEFEND = REGISTRY.register("sanity_defend", SanityDefendEnchantment::new);
    public static final DeferredHolder<Enchantment, ? extends Enchantment> REFLECTION = REGISTRY.register("reflection", ReflectionEnchantment::new);
    public static final DeferredHolder<Enchantment, ? extends Enchantment> SYNESTHESIA = REGISTRY.register("synesthesia", SynesthesiaEnchantment::new);
    public static final DeferredHolder<Enchantment, ? extends Enchantment> METABOLISM = REGISTRY.register("metabolism", MetabolismEnchantment::new);
    public static final DeferredHolder<Enchantment, ? extends Enchantment> MUTE_ATTACK = REGISTRY.register("mute_attack", MuteAttackEnchantment::new);
    public static final DeferredHolder<Enchantment, ? extends Enchantment> NETHERSEA_WALKER = REGISTRY.register("nethersea_walker", NetherseaWalkerEnchantment::new);
    public static final DeferredHolder<Enchantment, ? extends Enchantment> FLEXIBILITY = REGISTRY.register("flexibility", FlexibilityEnchantment::new);
    public static final DeferredHolder<Enchantment, ? extends Enchantment> MAGIC_TOLERANCE = REGISTRY.register("magic_tolerance", MagicToleranceEnchantment::new);
    public static final DeferredHolder<Enchantment, ? extends Enchantment> HAZARD_PROTECTION = REGISTRY.register("hazard_protection", HazardProtectionEnchantment::new);
    public static final DeferredHolder<Enchantment, ? extends Enchantment> SANITY_INJURY_CURSE = REGISTRY.register("sanity_injury_curse", SanityInjuryCurseEnchantment::new);
    public static final DeferredHolder<Enchantment, ? extends Enchantment> REJECTION_CURSE = REGISTRY.register("rejection_curse", RejectionCurseEnchantment::new);
}
