package com.apocalypse.caerulaarbor.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.common.data.ExistingFileHelper;
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
        addMinecraftValues("placeable", "caerula_arbor:presious_days", "caerula_arbor:age_of_silence", "caerula_arbor:price_of_piece", "caerula_arbor:caerula_stella");
    }
}
