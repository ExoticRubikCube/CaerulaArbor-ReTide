package com.apocalypse.caerulaarbor.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class DatagenUtils {
    public static interface MinecraftDamageTypeTags extends net.minecraft.tags.DamageTypeTags {
    }

    public static class MinecraftDamageTypes implements net.minecraft.world.damagesource.DamageTypes {
    }

    public static class ForgeDamageTypes {
        public static final TagKey<DamageType> IS_MAGIC = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("forge", "is_magic"));
    }

}
