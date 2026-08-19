package com.susen36.caerulaarbor.datagen;

import com.susen36.caerulaarbor.CaerulaArbor;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

import java.util.ArrayList;

/**
 * 自定义 DamageType 的注册表数据定义
 */
public class DamageTypes {
    public static ArrayList<ResKeyAndType> DamageResKeysAndTypes = new ArrayList<>();
    public static final ResourceKey<DamageType> ANCHOR_SMASH = create(
            "anchor_smash",
            "anchor_smash",
            DamageScaling.NEVER,
            0.25f
    );
    public static final ResourceKey<DamageType> AXE_CLEAVE = create(
            "axe_cleave",
            "axe_cleave",
            DamageScaling.NEVER,
            2f
    );

    public static final ResourceKey<DamageType> BOIL_WATER = create(
            "boil_water",
            "boil_water",
            DamageScaling.NEVER,
            0,
            DamageEffects.DROWNING
    );

    public static final ResourceKey<DamageType> BRAND_BOMB = create(
            "brand_bomb",
            "brand_bomb",
            DamageScaling.NEVER,
            0.1f
    );

    public static final ResourceKey<DamageType> CHEST_ATTACK = create(
            "chest_attack",
            "chest_attack",
            DamageScaling.ALWAYS,
            0.2f
    );

    public static final ResourceKey<DamageType> CHEST_SPIKE = create(
            "chest_spike",
            "chest_spike",
            DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            0.1f,
            DamageEffects.THORNS
    );

    public static final ResourceKey<DamageType> CLEAVER_MIX = create(
            "cleaver_mix",
            "cleaver_mix",
            DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            0.35f
    );

    public static final ResourceKey<DamageType> ENDSPEAKER_ATTACK = create(
            "endspeaker_attack",
            "endspeaker_attack",
            DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            0.25f
    );

    public static final ResourceKey<DamageType> EXTRACTOR_DAMAGE = create(
            "extractor_damage",
            "extractor_damage",
            DamageScaling.NEVER,
            0f
    );

    public static final ResourceKey<DamageType> GENERIC_SEABORN_ATTACK = create(
            "generic_seaborn_attack",
            "generic_seaborn_attack",
            DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            0.25f
    );

    public static final ResourceKey<DamageType> GENERIC_WARRIOR_ATTACK = create(
            "generic_warrior_attack",
            "generic_warrior_attack",
            DamageScaling.NEVER,
            0.1f
    );

    public static final ResourceKey<DamageType> GLADIIA_MAGIC = create(
            "gladiia_magic",
            "gladiia_magic",
            DamageScaling.NEVER,
            0.1f
    );

    public static final ResourceKey<DamageType> GOLEM_ATTACK = create(
            "golem_attack",
            "golem_attack",
            DamageScaling.NEVER,
            0.1f
    );

    public static final ResourceKey<DamageType> GUNMU_DAMAGE = create(
            "gunmu_damage",
            "gunmu_damage",
            DamageScaling.NEVER,
            0.1f
    );

    public static final ResourceKey<DamageType> HAND_FIREWORK = create(
            "hand_firework",
            "hand_firework",
            DamageScaling.NEVER,
            0.1f
    );

    public static final ResourceKey<DamageType> HAND_OF_CHOKER = create(
            "hand_of_choker",
            "hand_of_choker",
            DamageScaling.NEVER,
            0f
    );

    public static final ResourceKey<DamageType> HAND_SPIKE = create(
            "hand_spike",
            "hand_spike",
            DamageScaling.NEVER,
            0.1f,DamageEffects.THORNS
    );

    public static final ResourceKey<DamageType> HIGHMORE_ATTACK = create(
            "highmore_attack",
            "highmore_attack",
            DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            0.1f
    );

    public static final ResourceKey<DamageType> HUNTER_ATTACK = create(
            "hunter_attack",
            "hunter_attack",
            DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            0.1f
    );

    public static final ResourceKey<DamageType> IMMORTAL_PUNISHMENT = create(
            "immortal_punishment",
            "immortal_punishment",
            DamageScaling.NEVER,
            10f
    );

    public static final ResourceKey<DamageType> INV_KILLER = create(
            "inv_killer",
            "inv_killer",
            DamageScaling.NEVER,
            0f
    );

    public static final ResourceKey<DamageType> ISHARMLA_ATTACK = create(
            "isharmla_attack",
            "isharmla_attack",
            DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            0.1f
    );

    public static final ResourceKey<DamageType> ISHARMLA_CURSED = create(
            "isharmla_cursed",
            "isharmla_cursed",
            DamageScaling.NEVER,
            0.25f
    );

    public static final ResourceKey<DamageType> IZUMIK_NORMAL_ATTACK = create(
            "izumik_normal_attack",
            "izumik_normal_attack",
            DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            1f
    );

    public static final ResourceKey<DamageType> IZUMIK_SKILL = create(
            "izumik_skill",
            "izumik_skill",
            DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            1.5f
    );

    public static final ResourceKey<DamageType> LAST_KNIGHT_ATTACK = create(
            "last_knight_attack",
            "last_knight_attack",
            DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            0.1f
    );

    public static final ResourceKey<DamageType> OCEANIZE_DAMAGE = create(
            "oceanize_damage",
            "oceanize_damage",
            DamageScaling.NEVER,
            0.1f,
            DamageEffects.DROWNING
    );

    public static final ResourceKey<DamageType> OCEANKILLER_DAMAGE = create(
            "oceankiller_damage",
            "oceankiller_damage",
            DamageScaling.NEVER,
            0f
    );

    public static final ResourceKey<DamageType> OCEAN_COUNTER = create(
            "ocean_counter",
            "ocean_counter",
            DamageScaling.NEVER,
            0f,
            DamageEffects.THORNS
    );

    public static final ResourceKey<DamageType> OCEAN_MAGIC = create(
            "ocean_magic",
            "ocean_magic",
            DamageScaling.ALWAYS,
            0f
    );

    public static final ResourceKey<DamageType> OCEAN_WITHER = create(
            "ocean_wither",
            "ocean_wither",
            DamageScaling.NEVER,
            0.5f
    );

    public static final ResourceKey<DamageType> OCEAN_REAL = create(
            "ocean_real",
            "ocean_real",
            DamageScaling.NEVER,
            0f
    );

    public static final ResourceKey<DamageType> PUNCTURE_ATTACK = create(
            "puncture_attack",
            "puncture_attack",
            DamageScaling.ALWAYS,
            0.1f
    );

    public static final ResourceKey<DamageType> REPELLER_ATTACK = create(
            "repeller_attack",
            "repeller_attack",
            DamageScaling.ALWAYS,
            0.25f
    );

    public static final ResourceKey<DamageType> SANITY_BREAK = create(
            "sanity_break",
            "sanity_break",
            DamageScaling.NEVER,
            2f
    );

    public static final ResourceKey<DamageType> SAW_CUT = create(
            "saw_cut",
            "saw_cut",
            DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            0.1f,
            DamageEffects.THORNS
    );

    public static final ResourceKey<DamageType> SUPER_CAT_ATTACK = create(
            "super_cat_attack",
            "super_cat_attack",
            DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            0f
    );

    public static final ResourceKey<DamageType> TRAIL_DAMAGE = create(
            "trail_damage",
            "trail_damage",
            DamageScaling.NEVER,
            0f
    );

    public static final ResourceKey<DamageType> WARDEN_ATTACK = create(
            "warden_attack",
            "warden_attack",
            DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            0.25f
    );

    public static final ResourceKey<DamageType> WIPE_MAGIC = create(
            "wipe_magic",
            "wipe_magic",
            DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            0.1f
    );

    private static ResourceKey<DamageType> create(String damageName, String pMsgId, DamageScaling pScaling, float pExhaustion) {
        var resourceKey = ResourceKey.create(Registries.DAMAGE_TYPE, CaerulaArbor.ModLoc(damageName));
        var damageType = new DamageType(pMsgId, pScaling, pExhaustion);
        if (DamageResKeysAndTypes == null) DamageResKeysAndTypes = new ArrayList<>();
        DamageResKeysAndTypes.add(new ResKeyAndType(resourceKey, damageType));
        return resourceKey;
    }

    private static ResourceKey<DamageType> create(String damageName, String pMsgId, DamageScaling pScaling, float pExhaustion, DamageEffects damageEffects) {
        var resourceKey = ResourceKey.create(Registries.DAMAGE_TYPE, CaerulaArbor.ModLoc(damageName));
        var damageType = new DamageType(pMsgId, pScaling, pExhaustion,damageEffects);
        if (DamageResKeysAndTypes == null) DamageResKeysAndTypes = new ArrayList<>();
        DamageResKeysAndTypes.add(new ResKeyAndType(resourceKey, damageType));
        return resourceKey;
    }

    /**
     * 向 damage_type 注册表写入所有自定义伤害类型
     *
     * @param context Mojang 提供的注册表 bootstrap 上下文
     */
    public static void bootstrap(BootstrapContext<DamageType> context) {
        for (var resKeyAndTypes : DamageResKeysAndTypes) {
            context.register(resKeyAndTypes.key(), resKeyAndTypes.type());
        }
    }

    /**
     * 伤害类型的注册 key 与数据对象组合
     *
     * @param key  注册表 key
     * @param type 伤害类型数据
     */
    public record ResKeyAndType(ResourceKey<DamageType> key, DamageType type) {
    }
}