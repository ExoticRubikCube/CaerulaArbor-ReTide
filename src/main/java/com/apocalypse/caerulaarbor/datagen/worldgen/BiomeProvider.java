package com.apocalypse.caerulaarbor.datagen.worldgen;

import com.apocalypse.caerulaarbor.init.CABiomes;
import com.apocalypse.caerulaarbor.init.CASounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.CavePlacements;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Objects;

/**
 * 生成 biome 注册表数据
 *
 * <p>新增 biome 时，先在 {@link #bootstrap(BootstapContext)} 获取所需注册表查询器，再用
 * {@code context.register} 绑定 biome key，具体天气、颜色、音效、生成设置放入 {@code brandedLand(...)} 和 {@code generationSettings(...)}
 * <p>示例：
 * <pre>{@code
 * public static void bootstrap(BootstapContext<Biome> context) {
 *     // 查询 placed feature 注册表，用于 biome 生成设置引用 feature
 *     HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
 *     // 查询 carver 注册表，用于 biome 生成设置引用洞穴和峡谷生成器
 *     HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);
 *     // 查询 sound event 注册表，用于背景音乐等音效引用
 *     HolderGetter<SoundEvent> soundEvents = context.lookup(Registries.SOUND_EVENT);
 *
 *     // 注册 biome key，并调用 exampleBiome(...) 构建 biome 实例
 *     context.register(
 *             CABiomes.BRANDED_LAND,
 *             exampleBiome(placedFeatures, carvers, soundEvents)
 *     );
 * }
 *
 * private static Biome exampleBiome(
 *         HolderGetter<PlacedFeature> placedFeatures,
 *         HolderGetter<ConfiguredWorldCarver<?>> carvers,
 *         HolderGetter<SoundEvent> soundEvents
 * ) {
 *     // 使用 BiomeBuilder 逐项配置天气、温度、视觉效果、刷怪和生成设置
 *     return new Biome.BiomeBuilder()
 *             // 设置 biome 是否有降水
 *             .hasPrecipitation(true)
 *             // 设置温度，影响雨雪等表现
 *             .temperature(0.7F)
 *             // 设置降水量
 *             .downfall(0.5F)
 *             // 设置天空、水体、雾效、草色、树叶色和音乐
 *             .specialEffects(new BiomeSpecialEffects.Builder().build())
 *             // 设置实体生成规则
 *             .mobSpawnSettings(new MobSpawnSettings.Builder().build())
 *             // 设置地物、矿物、植被、carver 等生成内容
 *             .generationSettings(generationSettings(placedFeatures, carvers))
 *             // 构建最终 biome 实例
 *             .build();
 * }
 * }</pre>
 */
public class BiomeProvider {
    /**
     * 工具类，不允许实例化
     */
    private BiomeProvider() {
    }

    /**
     * 注册生物群系
     *
     * @param context Mojang 提供的注册表 bootstrap 上下文
     */
    public static void bootstrap(BootstapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);
        HolderGetter<SoundEvent> soundEvents = context.lookup(Registries.SOUND_EVENT);
        context.register(CABiomes.BRANDED_LAND, brandedLand(placedFeatures, carvers, soundEvents));
    }

    /**
     * 构建 branded_land 生物群系
     *
     * @param placedFeatures placed feature 查询器
     * @param carvers        carver 查询器
     * @param soundEvents    音效查询器
     * @return 生物群系定义
     */
    private static Biome brandedLand(
            HolderGetter<PlacedFeature> placedFeatures,
            HolderGetter<ConfiguredWorldCarver<?>> carvers,
            HolderGetter<SoundEvent> soundEvents
    ) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.7F)
                .downfall(0.5F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .backgroundMusic(new Music(shallowSea(soundEvents), 12000, 24000, true))
                        .foliageColorOverride(rgb("#54827A"))
                        .grassColorOverride(rgb("#4F6A63"))
                        .skyColor(rgb("#576D89"))
                        .fogColor(rgb("#9EA2A6"))
                        .waterColor(rgb("#40709F"))
                        .waterFogColor(rgb("#395D7F"))
                        .build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(generationSettings(placedFeatures, carvers))
                .build();
    }

    /**
     * 构建 branded_land 的地形生成设置
     *
     * @param placedFeatures placed feature 查询器
     * @param carvers        carver 查询器
     * @return 生物群系生成设置
     */
    private static BiomeGenerationSettings generationSettings(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        var generation = new BiomeGenerationSettings.Builder(placedFeatures, carvers);
        generation.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE);
        generation.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND);
        generation.addCarver(GenerationStep.Carving.AIR, Carvers.CANYON);

        generation.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND);

        addOreFeatures(generation);
        addVegetationFeatures(generation);

        generation.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, MiscOverworldPlacements.FREEZE_TOP_LAYER);
        return generation.build();
    }

    /**
     * 添加矿物与地下装饰 feature
     *
     * @param generation 生物群系生成设置 builder
     */
    private static void addOreFeatures(BiomeGenerationSettings.Builder generation) {
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_COAL_UPPER);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_COAL_LOWER);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_IRON_UPPER);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_IRON_MIDDLE);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_IRON_SMALL);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GOLD);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GOLD_LOWER);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_REDSTONE);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_REDSTONE_LOWER);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIAMOND);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIAMOND_LARGE);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIAMOND_BURIED);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_LAPIS);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_LAPIS_BURIED);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_COPPER);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, CavePlacements.UNDERWATER_MAGMA);
    }

    /**
     * 添加植被 feature
     *
     * @param generation 生物群系生成设置 builder
     */
    private static void addVegetationFeatures(BiomeGenerationSettings.Builder generation) {
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldgenKeys.PlacedFeatures.BRANDED_LAND_TREE);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_TALL_GRASS);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_TAIGA_2);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_TAIGA);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_TAIGA);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
    }

    /**
     * 查询 CASounds.SHALLOW_SEA 背景音乐音效
     *
     * @param soundEvents 音效查询器
     * @return 音效 holder
     */
    private static Holder<SoundEvent> shallowSea(HolderGetter<SoundEvent> soundEvents) {
        return soundEvents.getOrThrow(ResourceKey.create(Registries.SOUND_EVENT, Objects.requireNonNull(CASounds.SHALLOW_SEA.getId())));
    }

    /**
     * 将 #RRGGBB 或 #AARRGGBB 转为 ARGB 整数
     *
     * @param hex 颜色字符串
     * @return ARGB 整数
     */
    private static int rgb(String hex) {
        if (!hex.startsWith("#") || (hex.length() != 7 && hex.length() != 9)) {
            throw new IllegalArgumentException("hex need #RRGGBB or #AARRGGBB");
        }

        String h = hex.substring(1).toUpperCase();
        if (h.length() == 6) {
            h = "FF" + h; // 补 Alpha
        }

        int result = 0;
        for (int i = 0; i < 8; i++) {
            char c = h.charAt(i);
            int val = Character.digit(c, 16);
            if (val == -1) {
                throw new IllegalArgumentException("invalid hex char: " + c);
            }
            result = (result << 4) | val;
        }
        return result;
    }
}
