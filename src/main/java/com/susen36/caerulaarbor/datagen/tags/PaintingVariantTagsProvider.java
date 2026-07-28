package com.susen36.caerulaarbor.datagen.tags;

import com.susen36.caerulaarbor.init.CAPaintings;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        placeable.addOptional(paintingVariantLocation(CAPaintings.PRESIOUS_DAYS));
        placeable.addOptional(paintingVariantLocation(CAPaintings.AGE_OF_SILENCE));
        placeable.addOptional(paintingVariantLocation(CAPaintings.PRICE_OF_PIECE));
        placeable.addOptional(paintingVariantLocation(CAPaintings.CAERULA_STELLA));
    }

    /**
     * 从注册对象创建画作变体 ID
     *
     * @param variant 注册对象
     * @return 画作变体 location
     */
    private static ResourceLocation paintingVariantLocation(DeferredHolder<PaintingVariant, ?> variant) {
        return variant.getKey().location();
    }
}