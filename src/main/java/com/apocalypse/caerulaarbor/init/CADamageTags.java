package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

/**
 * 自定义伤害类型标签常量。
 *
 * <p>这个接口只负责提供 {@link TagKey}，用于
 * {@code damagesource.is(CADamageTags.XXX)} 这类判断，
 * 表达“已有伤害来源属于哪一类”。它不负责创建 {@link net.minecraft.world.damagesource.DamageSource}；
 * 创建逻辑统一放在 {@link CADamageTypes}。
 */
public interface CADamageTags {
    TagKey<DamageType> BYPASS_PROTECTION = create("bypass_protection");
    TagKey<DamageType> IS_MAGIC = create("is_magic");
    TagKey<DamageType> BYPASS_DEFENSE = create("bypass_defense");
    TagKey<DamageType> BYPASS_MISS = create("bypass_miss");
    TagKey<DamageType> SKIP_SOURCE_CHECK = create("skip_source_check");
    TagKey<DamageType> NEVER_TRIGGER_BOSS_PROTECTION = create("never_trigger_boss_protection");
    TagKey<DamageType> BYPASSES_MIGRATION = create("bypasses_migration");
    TagKey<DamageType> BYPASSES_EVOLUTION = create("bypasses_evolution");
    TagKey<DamageType> BYPASSES_ENDERMAN = create("bypasses_enderman");
    TagKey<DamageType> RARE = create("rare");
    TagKey<DamageType> HORROR = create("horror");
    TagKey<DamageType> CAN_TRIGGER_OCEANIZATION = create("can_trigger_oceanization");

    private static TagKey<DamageType> create(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, name));
    }
}
