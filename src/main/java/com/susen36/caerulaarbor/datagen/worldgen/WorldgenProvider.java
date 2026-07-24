package com.susen36.caerulaarbor.datagen.worldgen;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

/**
 * worldgen 注册表数据入口，只负责把各子 provider 挂到外部 RegistrySetBuilder
 *
 * <p>新增 worldgen 注册表数据时，先创建对应子 provider 的 {@code bootstrap} 方法，再在
 * {@link #init(RegistrySetBuilder)} 中追加 {@code builder.add}
 * <p>示例：
 * <pre>{@code
 * public static void init(RegistrySetBuilder builder) {
 *     // 注册 configured feature bootstrap，先生成 feature 配置
 *     builder.add(Registries.CONFIGURED_FEATURE, ConfiguredFeatureProvider::bootstrap)
 *             // 注册 placed feature bootstrap，引用 configured feature 并配置放置规则
 *             .add(Registries.PLACED_FEATURE, PlacedFeatureProvider::bootstrap)
 *             // 注册 biome bootstrap，引用 placed feature、carver 和 sound event
 *             .add(Registries.BIOME, BiomeProvider::bootstrap);
 * }
 *
 * // 在 WorldgenKeys 中声明 ResourceKey 字段
 * ResourceKey<PlacedFeature> key = WorldgenKeys.PlacedFeatures.BRANDED_LAND_TREE;
 *
 * // provider 直接将字段传给 context.register 或 HolderGetter.getOrThrow
 * context.register(key, placedFeature);
 * }
 * }</pre>
 */
public final class WorldgenProvider {
    /**
     * 工具类不实例化
     */
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

}
