package com.apocalypse.caerulaarbor.datagen.tags;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class TagsProvider {

    private TagsProvider() {
    }

    public static void addProviders(DataGenerator generator, boolean includeServer, PackOutput output,
                                    CompletableFuture<HolderLookup.Provider> lookupProvider,
                                    @Nullable ExistingFileHelper existingFileHelper) {
        generator.addProvider(includeServer, new DamageTypeTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(includeServer, new BlockTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(includeServer, new EntityTypeTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(includeServer, new ItemTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(includeServer, new BiomeTagsProvider(output, lookupProvider, existingFileHelper));
    }

    protected static abstract class RegistryTagsProvider<T> extends net.minecraft.data.tags.TagsProvider<T> {
        private final ResourceKey<? extends Registry<T>> registryKey;

        protected RegistryTagsProvider(PackOutput output, ResourceKey<? extends Registry<T>> registryKey,
                                       CompletableFuture<HolderLookup.Provider> lookupProvider,
                                       @Nullable ExistingFileHelper existingFileHelper) {
            super(output, registryKey, lookupProvider, CaerulaArborMod.MODID, existingFileHelper);
            this.registryKey = registryKey;
        }

        private static ResourceLocation modLocation(String path) {
            return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, path);
        }

        private static ResourceLocation entryLocation(String id) {
            int separator = id.indexOf(':');
            if (separator >= 0) {
                return ResourceLocation.fromNamespaceAndPath(id.substring(0, separator), id.substring(separator + 1));
            }
            return ResourceLocation.withDefaultNamespace(id);
        }

        protected void addValues(String tagPath, String... values) {
            var appender = tag(TagKey.create(registryKey, modLocation(tagPath)));
            for (var value : values) {
                if (value.startsWith("#")) {
                    appender.addTag(TagKey.create(registryKey, entryLocation(value.substring(1))));
                } else {
                    appender.add(ResourceKey.create(registryKey, entryLocation(value)));
                }
            }
        }
    }
}
