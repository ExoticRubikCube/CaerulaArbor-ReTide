package com.apocalypse.caerulaarbor.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
    public static final ResourceKey<DamageType> ANCHOR_SMASH = com.apocalypse.caerulaarbor.datagen.DamageTypes.ANCHOR_SMASH;
    public static final ResourceKey<DamageType> AXE_CLEAVE = com.apocalypse.caerulaarbor.datagen.DamageTypes.AXE_CLEAVE;
    public static final ResourceKey<DamageType> BOIL_WATER = com.apocalypse.caerulaarbor.datagen.DamageTypes.BOIL_WATER;
    public static final ResourceKey<DamageType> BRAND_BOMB = com.apocalypse.caerulaarbor.datagen.DamageTypes.BRAND_BOMB;
    public static final ResourceKey<DamageType> CHEST_ATTACK = com.apocalypse.caerulaarbor.datagen.DamageTypes.CHEST_ATTACK;
    public static final ResourceKey<DamageType> CHEST_SPIKE = com.apocalypse.caerulaarbor.datagen.DamageTypes.CHEST_SPIKE;
    public static final ResourceKey<DamageType> CLEAVER_MIX = com.apocalypse.caerulaarbor.datagen.DamageTypes.CLEAVER_MIX;
    public static final ResourceKey<DamageType> ENDSPEAKER_ATTACK = com.apocalypse.caerulaarbor.datagen.DamageTypes.ENDSPEAKER_ATTACK;
    public static final ResourceKey<DamageType> EXTRACTOR_DAMAGE = com.apocalypse.caerulaarbor.datagen.DamageTypes.EXTRACTOR_DAMAGE;
    public static final ResourceKey<DamageType> GENERIC_SEABORN_ATTACK = com.apocalypse.caerulaarbor.datagen.DamageTypes.GENERIC_SEABORN_ATTACK;
    public static final ResourceKey<DamageType> GENERIC_WARRIOR_ATTACK = com.apocalypse.caerulaarbor.datagen.DamageTypes.GENERIC_WARRIOR_ATTACK;
    public static final ResourceKey<DamageType> GLADIIA_MAGIC = com.apocalypse.caerulaarbor.datagen.DamageTypes.GLADIIA_MAGIC;
    public static final ResourceKey<DamageType> GOLEM_ATTACK = com.apocalypse.caerulaarbor.datagen.DamageTypes.GOLEM_ATTACK;
    public static final ResourceKey<DamageType> GUNMU_DAMAGE = com.apocalypse.caerulaarbor.datagen.DamageTypes.GUNMU_DAMAGE;
    public static final ResourceKey<DamageType> HAND_FIREWORK = com.apocalypse.caerulaarbor.datagen.DamageTypes.HAND_FIREWORK;
    public static final ResourceKey<DamageType> HAND_OF_CHOKER = com.apocalypse.caerulaarbor.datagen.DamageTypes.HAND_OF_CHOKER;
    public static final ResourceKey<DamageType> HAND_SPIKE = com.apocalypse.caerulaarbor.datagen.DamageTypes.HAND_SPIKE;
    public static final ResourceKey<DamageType> HIGHMORE_ATTACK = com.apocalypse.caerulaarbor.datagen.DamageTypes.HIGHMORE_ATTACK;
    public static final ResourceKey<DamageType> HUNTER_ATTACK = com.apocalypse.caerulaarbor.datagen.DamageTypes.HUNTER_ATTACK;
    public static final ResourceKey<DamageType> IMMORTAL_PUNISHMENT = com.apocalypse.caerulaarbor.datagen.DamageTypes.IMMORTAL_PUNISHMENT;
    public static final ResourceKey<DamageType> INV_KILLER = com.apocalypse.caerulaarbor.datagen.DamageTypes.INV_KILLER;
    public static final ResourceKey<DamageType> ISHARMLA_ATTACK = com.apocalypse.caerulaarbor.datagen.DamageTypes.ISHARMLA_ATTACK;
    public static final ResourceKey<DamageType> ISHARMLA_CURSED = com.apocalypse.caerulaarbor.datagen.DamageTypes.ISHARMLA_CURSED;
    public static final ResourceKey<DamageType> IZUMIK_NORMAL_ATTACK = com.apocalypse.caerulaarbor.datagen.DamageTypes.IZUMIK_NORMAL_ATTACK;
    public static final ResourceKey<DamageType> IZUMIK_SKILL = com.apocalypse.caerulaarbor.datagen.DamageTypes.IZUMIK_SKILL;
    public static final ResourceKey<DamageType> LAST_KNIGHT_ATTACK = com.apocalypse.caerulaarbor.datagen.DamageTypes.LAST_KNIGHT_ATTACK;
    public static final ResourceKey<DamageType> OCEANIZE_DAMAGE = com.apocalypse.caerulaarbor.datagen.DamageTypes.OCEANIZE_DAMAGE;
    public static final ResourceKey<DamageType> OCEANKILLER_DAMAGE = com.apocalypse.caerulaarbor.datagen.DamageTypes.OCEANKILLER_DAMAGE;
    public static final ResourceKey<DamageType> OCEAN_COUNTER = com.apocalypse.caerulaarbor.datagen.DamageTypes.OCEAN_COUNTER;
    public static final ResourceKey<DamageType> OCEAN_MAGIC = com.apocalypse.caerulaarbor.datagen.DamageTypes.OCEAN_MAGIC;
    public static final ResourceKey<DamageType> OCEAN_WITHER = com.apocalypse.caerulaarbor.datagen.DamageTypes.OCEAN_WITHER;
    public static final ResourceKey<DamageType> PUNCTURE_ATTACK = com.apocalypse.caerulaarbor.datagen.DamageTypes.PUNCTURE_ATTACK;
    public static final ResourceKey<DamageType> REPELLER_ATTACK = com.apocalypse.caerulaarbor.datagen.DamageTypes.REPELLER_ATTACK;
    public static final ResourceKey<DamageType> SANITY_BREAK = com.apocalypse.caerulaarbor.datagen.DamageTypes.SANITY_BREAK;
    public static final ResourceKey<DamageType> SAW_CUT = com.apocalypse.caerulaarbor.datagen.DamageTypes.SAW_CUT;
    public static final ResourceKey<DamageType> SUPER_CAT_ATTACK = com.apocalypse.caerulaarbor.datagen.DamageTypes.SUPER_CAT_ATTACK;
    public static final ResourceKey<DamageType> TRAIL_DAMAGE = com.apocalypse.caerulaarbor.datagen.DamageTypes.TRAIL_DAMAGE;
    public static final ResourceKey<DamageType> WARDEN_ATTACK = com.apocalypse.caerulaarbor.datagen.DamageTypes.WARDEN_ATTACK;
    public static final ResourceKey<DamageType> WARDEN_SONIC = com.apocalypse.caerulaarbor.datagen.DamageTypes.WARDEN_SONIC;
    public static final ResourceKey<DamageType> WIPE_MAGIC = com.apocalypse.caerulaarbor.datagen.DamageTypes.WIPE_MAGIC;

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

}
