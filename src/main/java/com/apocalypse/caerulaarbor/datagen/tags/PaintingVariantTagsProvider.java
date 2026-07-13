package com.apocalypse.caerulaarbor.datagen.tags;

import com.apocalypse.caerulaarbor.init.CAPaintings;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 生成画作变体标签数据
 */
public class PaintingVariantTagsProvider extends TagsProvider.RegistryTagsProvider<PaintingVariant> {
    protected PaintingVariantTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                          @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.PAINTING_VARIANT, lookupProvider, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        var placeable = tag(PaintingVariantTags.PLACEABLE);
        placeable.add(paintingVariantKey(CAPaintings.PRESIOUS_DAYS));
        placeable.add(paintingVariantKey(CAPaintings.AGE_OF_SILENCE));
        placeable.add(paintingVariantKey(CAPaintings.PRICE_OF_PIECE));
        placeable.add(paintingVariantKey(CAPaintings.CAERULA_STELLA));
    }

    private static ResourceKey<PaintingVariant> paintingVariantKey(RegistryObject<PaintingVariant> variant) {
        return ResourceKey.create(Registries.PAINTING_VARIANT, Objects.requireNonNull(variant.getId()));
    }
}
