package com.susen36.caerulaarbor.init;

import com.susen36.caerulaarbor.datagen.DamageTypes;
import com.susen36.caerulaarbor.util.DatagenUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
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
    public static final ResourceKey<DamageType> ANCHOR_SMASH = DamageTypes.ANCHOR_SMASH;
    public static final ResourceKey<DamageType> AXE_CLEAVE = DamageTypes.AXE_CLEAVE;
    public static final ResourceKey<DamageType> BOIL_WATER = DamageTypes.BOIL_WATER;
    public static final ResourceKey<DamageType> BRAND_BOMB = DamageTypes.BRAND_BOMB;
    public static final ResourceKey<DamageType> CHEST_ATTACK = DamageTypes.CHEST_ATTACK;
    public static final ResourceKey<DamageType> CHEST_SPIKE = DamageTypes.CHEST_SPIKE;
    public static final ResourceKey<DamageType> CLEAVER_MIX = DamageTypes.CLEAVER_MIX;
    public static final ResourceKey<DamageType> ENDSPEAKER_ATTACK = DamageTypes.ENDSPEAKER_ATTACK;
    public static final ResourceKey<DamageType> EXTRACTOR_DAMAGE = DamageTypes.EXTRACTOR_DAMAGE;
    public static final ResourceKey<DamageType> GENERIC_SEABORN_ATTACK = DamageTypes.GENERIC_SEABORN_ATTACK;
    public static final ResourceKey<DamageType> GENERIC_WARRIOR_ATTACK = DamageTypes.GENERIC_WARRIOR_ATTACK;
    public static final ResourceKey<DamageType> GLADIIA_MAGIC = DamageTypes.GLADIIA_MAGIC;
    public static final ResourceKey<DamageType> GOLEM_ATTACK = DamageTypes.GOLEM_ATTACK;
    public static final ResourceKey<DamageType> GUNMU_DAMAGE = DamageTypes.GUNMU_DAMAGE;
    public static final ResourceKey<DamageType> HAND_FIREWORK = DamageTypes.HAND_FIREWORK;
    public static final ResourceKey<DamageType> HAND_OF_CHOKER = DamageTypes.HAND_OF_CHOKER;
    public static final ResourceKey<DamageType> HAND_SPIKE = DamageTypes.HAND_SPIKE;
    public static final ResourceKey<DamageType> HIGHMORE_ATTACK = DamageTypes.HIGHMORE_ATTACK;
    public static final ResourceKey<DamageType> HUNTER_ATTACK = DamageTypes.HUNTER_ATTACK;
    public static final ResourceKey<DamageType> IMMORTAL_PUNISHMENT = DamageTypes.IMMORTAL_PUNISHMENT;
    public static final ResourceKey<DamageType> INV_KILLER = DamageTypes.INV_KILLER;
    public static final ResourceKey<DamageType> ISHARMLA_ATTACK = DamageTypes.ISHARMLA_ATTACK;
    public static final ResourceKey<DamageType> ISHARMLA_CURSED = DamageTypes.ISHARMLA_CURSED;
    public static final ResourceKey<DamageType> IZUMIK_NORMAL_ATTACK = DamageTypes.IZUMIK_NORMAL_ATTACK;
    public static final ResourceKey<DamageType> IZUMIK_SKILL = DamageTypes.IZUMIK_SKILL;
    public static final ResourceKey<DamageType> LAST_KNIGHT_ATTACK = DamageTypes.LAST_KNIGHT_ATTACK;
    public static final ResourceKey<DamageType> OCEANIZE_DAMAGE = DamageTypes.OCEANIZE_DAMAGE;
    public static final ResourceKey<DamageType> OCEANKILLER_DAMAGE = DamageTypes.OCEANKILLER_DAMAGE;
    public static final ResourceKey<DamageType> OCEAN_COUNTER = DamageTypes.OCEAN_COUNTER;
    public static final ResourceKey<DamageType> OCEAN_MAGIC = DamageTypes.OCEAN_MAGIC;
    public static final ResourceKey<DamageType> OCEAN_WITHER = DamageTypes.OCEAN_WITHER;
    public static final ResourceKey<DamageType> OCEAN_REAL = DamageTypes.OCEAN_REAL;
    public static final ResourceKey<DamageType> PUNCTURE_ATTACK = DamageTypes.PUNCTURE_ATTACK;
    public static final ResourceKey<DamageType> REPELLER_ATTACK = DamageTypes.REPELLER_ATTACK;
    public static final ResourceKey<DamageType> SANITY_BREAK = DamageTypes.SANITY_BREAK;
    public static final ResourceKey<DamageType> SAW_CUT = DamageTypes.SAW_CUT;
    public static final ResourceKey<DamageType> SUPER_CAT_ATTACK = DamageTypes.SUPER_CAT_ATTACK;
    public static final ResourceKey<DamageType> TRAIL_DAMAGE = DamageTypes.TRAIL_DAMAGE;
    public static final ResourceKey<DamageType> WARDEN_ATTACK = DamageTypes.WARDEN_ATTACK;
    public static final ResourceKey<DamageType> WARDEN_SONIC = DamageTypes.WARDEN_SONIC;
    public static final ResourceKey<DamageType> WIPE_MAGIC = DamageTypes.WIPE_MAGIC;

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
        return source(level, DatagenUtils.MinecraftDamageTypes.PLAYER_ATTACK, attacker);
    }

    public static DamageSource wardenSonic(LevelReader level, Entity attacker) {
        return source(level, WARDEN_SONIC, attacker);
    }

}