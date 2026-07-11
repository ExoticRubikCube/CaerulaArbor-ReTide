package com.apocalypse.caerulaarbor.datagen;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.datagen.tags.CaeDamageTypeTags;
import com.apocalypse.caerulaarbor.util.DatagenUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;


public class DamageTypeTagsProvider extends TagsProvider<DamageType> {

    protected DamageTypeTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, Registries.DAMAGE_TYPE, future, CaerulaArborMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        addDamagesToTag(CaeDamageTypeTags.BYPASSES_DEFENSE,
                DamageTypes.OCEANIZE_DAMAGE,
                DamageTypes.SANITY_BREAK,
                DamageTypes.INV_KILLER,
                DamageTypes.HAND_OF_CHOKER,
                DamageTypes.GUNMU_DAMAGE,
                DamageTypes.WARDEN_SONIC,
                DamageTypes.SUPER_CAT_ATTACK,
                DamageTypes.OCEAN_WITHER,
                DamageTypes.IMMORTAL_PUNISHMENT,
                DamageTypes.ISHARMLA_ATTACK,
                DamageTypes.ISHARMLA_CURSED,
                DatagenUtils.MinecraftDamageTypes.GENERIC_KILL,
                DatagenUtils.MinecraftDamageTypes.FELL_OUT_OF_WORLD,
                DatagenUtils.MinecraftDamageTypes.OUTSIDE_BORDER,
                DatagenUtils.MinecraftDamageTypes.SONIC_BOOM,
                DatagenUtils.MinecraftDamageTypes.WITHER
        );
        addTagsToTag(CaeDamageTypeTags.BYPASSES_DEFENSE,
                DamageTypeTags.BYPASSES_ARMOR,
                DatagenUtils.ForgeDamageTypes.IS_MAGIC
        );

        addDamagesToTag(CaeDamageTypeTags.BYPASSES_MISS,
                DamageTypes.HAND_SPIKE,
                DamageTypes.OCEANIZE_DAMAGE,
                DamageTypes.SANITY_BREAK,
                DamageTypes.HAND_FIREWORK,
                DamageTypes.BRAND_BOMB,
                DamageTypes.ANCHOR_SMASH,
                DamageTypes.IZUMIK_SKILL,
                DamageTypes.INV_KILLER,
                DamageTypes.HAND_OF_CHOKER,
                DamageTypes.GUNMU_DAMAGE,
                DamageTypes.WARDEN_SONIC,
                DamageTypes.OCEAN_WITHER,
                DamageTypes.IMMORTAL_PUNISHMENT,
                DamageTypes.ISHARMLA_CURSED
        );
        addTagsToTag(CaeDamageTypeTags.BYPASSES_MISS,
                DamageTypeTags.BYPASSES_EFFECTS
        );
    }


    @SafeVarargs
    @SuppressWarnings("SameParameterValue")
    private void addTagsToTag(TagKey<DamageType> targetTag, TagKey<DamageType>... tags) {
        for (TagKey<DamageType> tag : tags) {
            tag(tag).addTag(targetTag);
        }
    }

    @SafeVarargs
    @SuppressWarnings("SameParameterValue")
    private void addDamagesToTag(TagKey<DamageType> tag, ResourceKey<DamageType>... types) {
        for (ResourceKey<DamageType> type : types) {
            tag(tag).add(type);
        }
    }
}
