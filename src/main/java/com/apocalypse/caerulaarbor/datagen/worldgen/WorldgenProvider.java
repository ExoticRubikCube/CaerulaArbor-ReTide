package com.apocalypse.caerulaarbor.datagen.worldgen;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * worldgen 注册表数据入口，只负责把各子 provider 挂到外部 RegistrySetBuilder
 */
public final class WorldgenProvider {
    private WorldgenProvider() {
    }

    /**
     * 注册所有 worldgen bootstrap
     *
     * @param builder 来自 RegistryDataProvider 的共享构建器
     */
    public static void init(RegistrySetBuilder builder) {
        builder.add(Registries.CONFIGURED_FEATURE, ConfiguredFeatureProvider::bootstrap)
                .add(Registries.PLACED_FEATURE, PlacedFeatureProvider::bootstrap)
                .add(Registries.BIOME, BiomeProvider::bootstrap)
                .add(Registries.TEMPLATE_POOL, TemplatePoolProvider::bootstrap)
                .add(Registries.STRUCTURE, StructureProvider::bootstrap)
                .add(Registries.STRUCTURE_SET, StructureSetProvider::bootstrap);
    }

    static ResourceLocation location(String id) {
        int separator = id.indexOf(':');
        if (separator < 0) {
            return ResourceLocation.withDefaultNamespace(id);
        }
        String namespace = id.substring(0, separator);
        String path = id.substring(separator + 1);
        if ("minecraft".equals(namespace)) {
            return ResourceLocation.withDefaultNamespace(path);
        }
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    static ResourceLocation modLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, path);
    }

    static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> registry, String id) {
        return ResourceKey.create(registry, location(id));
    }

    static <T> ResourceKey<T> modKey(ResourceKey<? extends Registry<T>> registry, String path) {
        return ResourceKey.create(registry, modLocation(path));
    }
}
