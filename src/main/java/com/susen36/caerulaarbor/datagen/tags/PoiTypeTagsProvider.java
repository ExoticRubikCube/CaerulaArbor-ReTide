package com.susen36.caerulaarbor.datagen.tags;

import com.susen36.caerulaarbor.init.CAVillagerProfessions;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 生成兴趣点类型标签数据
 */
public class PoiTypeTagsProvider extends TagsProvider.RegistryTagsProvider<PoiType> {
    protected PoiTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                  @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.POINT_OF_INTEREST_TYPE, lookupProvider, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(PoiTypeTags.ACQUIRABLE_JOB_SITE).add(CAVillagerProfessions.CANNOT_GOODENOUGH_POI);
    }
}
