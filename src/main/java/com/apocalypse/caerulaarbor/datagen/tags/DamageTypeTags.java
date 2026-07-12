package com.apocalypse.caerulaarbor.datagen.tags;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public interface DamageTypeTags {
    TagKey<DamageType> BYPASSES_DEFENSE = create("bypasses_defense");
    TagKey<DamageType> BYPASSES_MISS = create("bypasses_miss");
    TagKey<DamageType> BYPASSES_PROTECTION = create("bypasses_protection");
    TagKey<DamageType> BYPASSES_ENDERMAN = create("bypasses_enderman");
    TagKey<DamageType> BYPASSES_EVOLUTION = create("bypasses_evolution");
    TagKey<DamageType> BYPASSES_MIGRATION = create("bypasses_migration");
    TagKey<DamageType> CAN_TRIGGER_OCEANIZTION = create("can_trigger_oceanization");
    TagKey<DamageType> HORROR = create("horror");
    TagKey<DamageType> IS_MAGIC = create("is_magic");
    TagKey<DamageType> RARE = create("rare");
    TagKey<DamageType> NO_BEAT_BACK = create("no_beat_back");
    TagKey<DamageType> NEVER_TRIGGER_BOSS_PROTECTION = create("never_trigger_boss_protection");

    private static TagKey<DamageType> create(String pName) {
        return TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, pName));
    }
}