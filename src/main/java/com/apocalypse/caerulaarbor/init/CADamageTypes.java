package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;

import javax.annotation.Nullable;

/**
 * 自定义伤害类型常量与伤害来源工厂。
 *
 * <p>这个类负责提供 {@link ResourceKey} 和 {@link DamageSource} 创建入口，
 * 用于表达“这次伤害是什么类型”。如果只是判断一个已有的伤害来源是否属于某类标签，
 * 应使用 {@link CADamageTags} 配合 {@link DamageSource#is}，而不是调用这里的工厂方法。
 */
public class CADamageTypes {
    public static final ResourceKey<DamageType> ANCHOR_SMASH = create("anchor_smash");
    public static final ResourceKey<DamageType> AXE_CLEAVE = create("axe_cleave");
    public static final ResourceKey<DamageType> BOIL_WATER = create("boil_water");
    public static final ResourceKey<DamageType> BRAND_BOMB = create("brand_bomb");
    public static final ResourceKey<DamageType> CHEST_ATTACK = create("chest_attack");
    public static final ResourceKey<DamageType> CHEST_SPIKE = create("chest_spike");
    public static final ResourceKey<DamageType> CLEAVER_MIX = create("cleaver_mix");
    public static final ResourceKey<DamageType> ENDSPEAKER_ATTACK = create("endspeaker_attack");
    public static final ResourceKey<DamageType> EXTRACTOR_DAMAGE = create("extractor_damage");
    public static final ResourceKey<DamageType> GENERAL_SEABORN_ATTACK = create("general_seaborn_attack");
    public static final ResourceKey<DamageType> GENERIC_WARRIOR_ATTACK = create("generic_warrior_attack");
    public static final ResourceKey<DamageType> GLADIIA_MAGIC = create("gladiia_magic");
    public static final ResourceKey<DamageType> GOLEM_ATTACK = create("golem_attack");
    public static final ResourceKey<DamageType> GUNMU_DAMAGE = create("gunmu_damage");
    public static final ResourceKey<DamageType> HAND_FIREWORK = create("hand_firework");
    public static final ResourceKey<DamageType> HAND_OF_CHOKER = create("hand_of_choker");
    public static final ResourceKey<DamageType> HAND_SPIKE = create("hand_spike");
    public static final ResourceKey<DamageType> HIGHMORE_ATTACK = create("highmore_attack");
    public static final ResourceKey<DamageType> HUNTER_ATTACK = create("hunter_attack");
    public static final ResourceKey<DamageType> IMMORTAL_PUNISHMENT = create("immortal_punishment");
    public static final ResourceKey<DamageType> INV_KILLER = create("inv_killer");
    public static final ResourceKey<DamageType> ISHARMLA_ATTACK = create("isharmla_attack");
    public static final ResourceKey<DamageType> ISHARMLA_CURSED = create("isharmla_cursed");
    public static final ResourceKey<DamageType> IZUMIK_NORMAL_ATTACK = create("izumik_normal_attack");
    public static final ResourceKey<DamageType> IZUMIK_SKILL = create("izumik_skill");
    public static final ResourceKey<DamageType> LAST_KNIGHT_ATTACK = create("last_knight_attack");
    public static final ResourceKey<DamageType> OCEANIZE_DAMAGE = create("oceanize_damage");
    public static final ResourceKey<DamageType> OCEANKILLER_DAMAGE = create("oceankiller_damage");
    public static final ResourceKey<DamageType> OCEAN_COUNTER = create("ocean_counter");
    public static final ResourceKey<DamageType> OCEAN_MAGIC = create("ocean_magic");
    public static final ResourceKey<DamageType> OCEAN_WITHER = create("ocean_wither");
    public static final ResourceKey<DamageType> PUNCTURE_ATTACK = create("puncture_attack");
    public static final ResourceKey<DamageType> REPELLER_ATTACK = create("repeller_attack");
    public static final ResourceKey<DamageType> SANITY_BREAK = create("sanity_break");
    public static final ResourceKey<DamageType> SAW_CUT = create("saw_cut");
    public static final ResourceKey<DamageType> SUPER_CAT_ATTACK = create("super_cat_attack");
    public static final ResourceKey<DamageType> TRAIL_DAMAGE = create("trail_damage");
    public static final ResourceKey<DamageType> WARDEN_ATTACK = create("warden_attack");
    public static final ResourceKey<DamageType> WARDEN_SONIC = create("warden_sonic");
    public static final ResourceKey<DamageType> WIPE_MAGIC = create("wipe_magic");

    private CADamageTypes() {
    }

    private static Registry<DamageType> damageTypes(LevelReader level) {
        return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
    }

    /**
     * 创建一个不带实体上下文的伤害来源。
     */
    public static DamageSource source(LevelReader level, ResourceKey<DamageType> damageType) {
        return new DamageSource(damageTypes(level).getHolderOrThrow(damageType));
    }

    /**
     * 创建一个只带直接来源实体的伤害来源。
     */
    public static DamageSource source(LevelReader level, ResourceKey<DamageType> damageType, @Nullable Entity directEntity) {
        return new DamageSource(damageTypes(level).getHolderOrThrow(damageType), directEntity);
    }

    /**
     * 创建一个同时带直接来源与施害者实体的伤害来源。
     */
    public static DamageSource source(LevelReader level, ResourceKey<DamageType> damageType, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        return new DamageSource(damageTypes(level).getHolderOrThrow(damageType), directEntity, causingEntity);
    }

    public static DamageSource wardenAttack(LevelReader level, Entity attacker) {
        return source(level, WARDEN_ATTACK, attacker);
    }

    public static DamageSource playerAttack(LevelReader level, Entity attacker) {
        return source(level, DamageTypes.PLAYER_ATTACK, attacker);
    }

    public static DamageSource wardenSonic(LevelReader level, Entity attacker) {
        return source(level, WARDEN_SONIC, attacker);
    }

    public static ResourceKey<DamageType> create(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, name));
    }
}
