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

    /**
     * 解析 worldgen 数据中的资源 ID，省略命名空间时按 minecraft 处理
     *
     * @param id 资源 ID
     * @return ResourceLocation
     */
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

    /**
     * 创建 caerula_arbor 命名空间的资源 ID
     *
     * @param path 资源路径
     * @return ResourceLocation
     */
    static ResourceLocation modLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, path);
    }

    /**
     * 创建指定注册表的资源 key
     *
     * @param registry 注册表 key
     * @param id       资源 ID
     * @param <T>      注册表元素类型
     * @return ResourceKey
     */
    static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> registry, String id) {
        return ResourceKey.create(registry, location(id));
    }

    /**
     * 创建 caerula_arbor 命名空间下指定注册表的资源 key
     *
     * @param registry 注册表 key
     * @param path     资源路径
     * @param <T>      注册表元素类型
     * @return ResourceKey
     */
    static <T> ResourceKey<T> modKey(ResourceKey<? extends Registry<T>> registry, String path) {
        return ResourceKey.create(registry, modLocation(path));
    }
}
