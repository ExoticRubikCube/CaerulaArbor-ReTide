package com.susen36.caerulaarbor.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

public class DatagenUtils {
    public interface MinecraftDamageTypeTags extends DamageTypeTags {
    }

    public static class MinecraftDamageTypes implements DamageTypes {
    }

    public static class ForgeDamageTypes {
        public static final TagKey<DamageType> IS_MAGIC = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("forge", "is_magic"));
    }

}