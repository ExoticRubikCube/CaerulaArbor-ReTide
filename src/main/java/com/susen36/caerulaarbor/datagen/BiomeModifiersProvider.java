package com.susen36.caerulaarbor.datagen;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.susen36.caerulaarbor.CaerulaArbor;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 生成 Forge biome modifier 数据
 */
public class BiomeModifiersProvider implements DataProvider {

    private final PackOutput output;

    /**
     * 创建 biome modifier provider
     *
     * @param output datagen 输出位置
     */
    public BiomeModifiersProvider(PackOutput output) {
        this.output = output;
    }

    /**
     * 写出所有 biome modifier JSON
     *
     * @param cache datagen 缓存输出
     * @return 所有写文件任务的组合 future
     */
    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        Map<String, JsonObject> modifiers = new LinkedHashMap<>();
        addSpawn(modifiers, "accumulator_prokaryote_biome_modifier", anyBiome(), "caerula_arbor:accumulator_prokaryote", 60, 1, 3);
        addSpawn(modifiers, "apostle_prokaryote_biome_modifier", anyBiome(), "caerula_arbor:apostle_prokaryote", 45, 1, 2);
        addSpawn(modifiers, "baselayer_abyssal_biome_modifier", anyBiome(), "caerula_arbor:baselayer_abyssal", 45, 1, 3);
        addSpawn(modifiers, "bone_fish_biome_modifier", anyBiome(), "caerula_arbor:bone_fish", 60, 4, 6);
        addFeature(modifiers, "burnt_trails_biome_modifier", biomes("caerula_arbor:branded_land"), features("caerula_arbor:burnt_trails"), "surface_structures");
        addSpawn(modifiers, "chest_fish_biome_modifier", biomes("birch_forest", "flower_forest", "forest", "old_growth_birch_forest", "beach", "snowy_beach", "stony_shore", "caerula_arbor:branded_land"), "caerula_arbor:chest_fish", 3, 1, 1);
        addSpawn(modifiers, "ocean_stonecutte_biome_modifier", anyBiome(), "caerula_arbor:ocean_stonecutte", 45, 1, 4);
        addSpawn(modifiers, "collector_prokaryote_biome_modifier", anyBiome(), "caerula_arbor:collector_prokaryote", 60, 2, 4);
        addSpawn(modifiers, "cracker_abyssal_biome_modifier", anyBiome(), "caerula_arbor:cracker_abyssal", 25, 1, 1);
        addSpawn(modifiers, "pocket_sea_creeper_biome_modifier", anyBiome(), "caerula_arbor:pocket_sea_creeper", 40, 1, 1);
        addSpawn(modifiers, "depositer_prokaryote_biome_modifier", biomes("cold_ocean", "deep_cold_ocean", "deep_frozen_ocean", "deep_lukewarm_ocean", "deep_ocean", "frozen_ocean", "lukewarm_ocean", "ocean", "warm_ocean"), "caerula_arbor:depositer_prokaryote", 45, 1, 3);
        addSpawn(modifiers, "feeder_prokaryote_biome_modifier", anyBiome(), "caerula_arbor:feeder_prokaryote", 45, 1, 2);
        addSpawn(modifiers, "first_to_talk_biome_modifier", anyBiome(), "caerula_arbor:first_to_talk", 1, 1, 1);
        addSpawn(modifiers, "skimming_sea_drifter_biome_modifier", anyBiome(), "caerula_arbor:skimming_sea_drifter", 35, 1, 2);
        addSpawn(modifiers, "floater_prokaryote_biome_modifier", anyBiome(), "caerula_arbor:floater_prokaryote", 50, 1, 2);
        addSpawn(modifiers, "floating_sea_drifter_biome_modifier", anyBiome(), "caerula_arbor:floating_sea_drifter", 50, 2, 4);
        addSpawn(modifiers, "guide_abyssal_biome_modifier", anyBiome(), "caerula_arbor:guide_abyssal", 35, 1, 1);
        addFeature(modifiers, "iris_distribute_biome_modifier", biomes("plains", "sunflower_plains", "flower_forest"), features("caerula_arbor:iris_distribute"), "surface_structures");
        addSpawn(modifiers, "izumik_offspring_biome_modifier", biomes("cold_ocean", "deep_cold_ocean", "deep_frozen_ocean", "deep_lukewarm_ocean", "deep_ocean", "frozen_ocean", "lukewarm_ocean", "ocean", "warm_ocean"), "caerula_arbor:izumik_offspring", 2, 1, 2);
        addSpawn(modifiers, "nautilus_headhunter_biome_modifier", anyBiome(), "caerula_arbor:nautilus_headhunter", 25, 1, 1);
        addSpawn(modifiers, "nucleic_maleficent_biome_modifier", anyBiome(), "caerula_arbor:nucleic_maleficent", 40, 1, 2);
        addSpawn(modifiers, "predator_abyssal_biome_modifier", anyBiome(), "caerula_arbor:predator_abyssal", 45, 1, 4);
        addSpawn(modifiers, "pregnant_fish_biome_modifier", anyBiome(), "caerula_arbor:pregnant_fish", 40, 1, 2);
        addSpawn(modifiers, "puncture_fish_biome_modifier", anyBiome(), "caerula_arbor:puncture_fish", 40, 1, 1);
        addSpawn(modifiers, "reaper_fish_biome_modifier", anyBiome(), "caerula_arbor:reaper_fish", 50, 1, 2);
        addFeature(modifiers, "originium_iris_seeding_biome_modifier", biomes("snowy_plains", "plains", "flower_forest"), features("caerula_arbor:originium_iris_seeding"), "vegetal_decoration");
        addSpawn(modifiers, "shell_sea_runner_biome_modifier", anyBiome(), "caerula_arbor:shell_sea_runner", 50, 2, 4);
        addSpawn(modifiers, "shooter_fish_biome_modifier", anyBiome(), "caerula_arbor:shooter_fish", 50, 2, 3);
        addSpawn(modifiers, "slider_fish_biome_modifier", anyBiome(), "caerula_arbor:slider_fish", 50, 2, 5);
        addFeature(modifiers, "slider_flower_biome_modifier", biomes("caerula_arbor:branded_land"), features("caerula_arbor:slider_flower"), "surface_structures");
        addSpawn(modifiers, "spike_chest_biome_modifier", biomes("snowy_beach", "snowy_plains", "caerula_arbor:branded_land"), "caerula_arbor:spike_chest", 1, 1, 1);
        addSpawn(modifiers, "splasher_abyssal_biome_modifier", anyBiome(), "caerula_arbor:splasher_abyssal", 50, 2, 3);
        addFeature(modifiers, "trail_mushroom_biome_modifier", biomes("caerula_arbor:branded_land"), features("caerula_arbor:trail_mushroom"), "vegetal_decoration");
        addSpawn(modifiers, "umbrella_abyssal_biome_modifier", anyBiome(), "caerula_arbor:umbrella_abyssal", 45, 1, 2);

        Path root = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(CaerulaArbor.MODID)
                .resolve("neoforge")
                .resolve("biome_modifier");
        var futures = ImmutableList.<CompletableFuture<?>>builder();
        modifiers.forEach((name, json) -> futures.add(DataProvider.saveStable(cache, json, root.resolve(name + ".json"))));
        return CompletableFuture.allOf(futures.build().toArray(CompletableFuture[]::new));
    }

    /**
     * 添加实体生成 modifier
     *
     * @param modifiers modifier 输出集合
     * @param name      modifier 文件名
     * @param biomes    目标 biome 条件
     * @param entityType 实体类型 ID
     * @param weight    生成权重
     * @param minCount  最小生成数量
     * @param maxCount  最大生成数量
     */
    private static void addSpawn(Map<String, JsonObject> modifiers, String name, JsonElement biomes, String entityType, int weight, int minCount, int maxCount) {
        var spawner = new JsonObject();
        spawner.addProperty("type", entityType);
        spawner.addProperty("weight", weight);
        spawner.addProperty("minCount", minCount);
        spawner.addProperty("maxCount", maxCount);

        var modifier = baseModifier("neoforge:add_spawns", biomes);
        modifier.add("spawners", spawner);
        modifiers.put(name, modifier);
    }

    /**
     * 添加 feature 注入 modifier
     *
     * @param modifiers modifier 输出集合
     * @param name      modifier 文件名
     * @param biomes    目标 biome 条件
     * @param features  要注入的 placed feature
     * @param step      生成阶段
     */
    private static void addFeature(Map<String, JsonObject> modifiers, String name, JsonElement biomes, JsonElement features, String step) {
        var modifier = baseModifier("neoforge:add_features", biomes);
        modifier.add("features", features);
        modifier.addProperty("step", step);
        modifiers.put(name, modifier);
    }

    /**
     * 创建 Forge biome modifier 基础 JSON
     *
     * @param type   modifier 类型
     * @param biomes 目标 biome 条件
     * @return modifier JSON
     */
    private static JsonObject baseModifier(String type, JsonElement biomes) {
        var modifier = new JsonObject();
        modifier.addProperty("type", type);
        modifier.add("biomes", biomes);
        return modifier;
    }

    /**
     * 创建匹配任意 biome 的条件
     *
     * @return 任意 biome 条件 JSON
     */
    private static JsonObject anyBiome() {
        var biomes = new JsonObject();
        biomes.addProperty("type", "neoforge:any");
        return biomes;
    }

    /**
     * 创建 biome 条件值
     *
     * @param biomes biome ID 列表
     * @return 单值或数组 JSON
     */
    private static JsonElement biomes(String... biomes) {
        return stringOrArray(biomes);
    }

    /**
     * 创建 feature 条件值
     *
     * @param features placed feature ID 列表
     * @return 单值或数组 JSON
     */
    private static JsonElement features(String... features) {
        return stringOrArray(features);
    }

    /**
     * 单个值写为字符串，多个值写为数组
     *
     * @param values 字符串值
     * @return 字符串或数组 JSON
     */
    private static JsonElement stringOrArray(String... values) {
        if (values.length == 1) {
            return new JsonPrimitive(values[0]);
        }
        var array = new JsonArray();
        for (var value : values) {
            array.add(value);
        }
        return array;
    }

    /**
     * 返回 provider 在 datagen 日志中的显示名称
     */
    @Override
    public @NotNull String getName() {
        return "Biome Modifiers: " + CaerulaArbor.MODID;
    }
}