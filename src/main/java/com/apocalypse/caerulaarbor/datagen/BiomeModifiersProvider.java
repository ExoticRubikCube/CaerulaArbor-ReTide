package com.apocalypse.caerulaarbor.datagen;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BiomeModifiersProvider implements DataProvider {

    private final PackOutput output;

    public BiomeModifiersProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        Map<String, JsonObject> modifiers = new LinkedHashMap<>();
        addSpawn(modifiers, "accumulator_prokaryote_biome_modifier", anyBiome(), "caerula_arbor:accumulator_prokaryote", 60, 1, 3);
        addSpawn(modifiers, "apostle_prokaryote_biome_modifier", anyBiome(), "caerula_arbor:apostle_prokaryote", 45, 1, 2);
        addSpawn(modifiers, "baselayer_abyssal_biome_modifier", anyBiome(), "caerula_arbor:baselayer_abyssal", 45, 1, 3);
        addSpawn(modifiers, "bone_fish_biome_modifier", anyBiome(), "caerula_arbor:bone_fish", 60, 4, 6);
        addFeature(modifiers, "burnt_trails_biome_modifier", biomes("caerula_arbor:branded_land"), features("caerula_arbor:burnt_trails"), "surface_structures");
        addSpawn(modifiers, "chest_fish_biome_modifier", biomes("birch_forest", "flower_forest", "forest", "old_growth_birch_forest", "beach", "snowy_beach", "stony_shore", "caerula_arbor:branded_land"), "caerula_arbor:chest_fish", 3, 1, 1);
        addSpawn(modifiers, "chiseler_fish_biome_modifier", anyBiome(), "caerula_arbor:chiseler_fish", 45, 1, 4);
        addSpawn(modifiers, "collector_prokaryote_biome_modifier", anyBiome(), "caerula_arbor:collector_prokaryote", 60, 2, 4);
        addSpawn(modifiers, "cracker_abyssal_biome_modifier", anyBiome(), "caerula_arbor:cracker_abyssal", 25, 1, 1);
        addSpawn(modifiers, "creeper_fish_biome_modifier", anyBiome(), "caerula_arbor:creeper_fish", 40, 1, 1);
        addSpawn(modifiers, "depositer_prokaryote_biome_modifier", biomes("cold_ocean", "deep_cold_ocean", "deep_frozen_ocean", "deep_lukewarm_ocean", "deep_ocean", "frozen_ocean", "lukewarm_ocean", "ocean", "warm_ocean"), "caerula_arbor:depositer_prokaryote", 45, 1, 3);
        addSpawn(modifiers, "feeder_prokaryote_biome_modifier", anyBiome(), "caerula_arbor:feeder_prokaryote", 45, 1, 2);
        addSpawn(modifiers, "first_to_talk_biome_modifier", anyBiome(), "caerula_arbor:first_to_talk", 1, 1, 1);
        addSpawn(modifiers, "flee_fish_biome_modifier", anyBiome(), "caerula_arbor:flee_fish", 35, 1, 2);
        addSpawn(modifiers, "floater_prokaryote_biome_modifier", anyBiome(), "caerula_arbor:floater_prokaryote", 50, 1, 2);
        addSpawn(modifiers, "fly_fish_biome_modifier", anyBiome(), "caerula_arbor:fly_fish", 50, 2, 4);
        addSpawn(modifiers, "guide_abyssal_biome_modifier", anyBiome(), "caerula_arbor:guide_abyssal", 35, 1, 1);
        addFeature(modifiers, "iris_distribute_biome_modifier", biomes("plains", "sunflower_plains", "flower_forest"), features("caerula_arbor:iris_distribute"), "surface_structures");
        addSpawn(modifiers, "izumik_offspring_biome_modifier", biomes("cold_ocean", "deep_cold_ocean", "deep_frozen_ocean", "deep_lukewarm_ocean", "deep_ocean", "frozen_ocean", "lukewarm_ocean", "ocean", "warm_ocean"), "caerula_arbor:izumik_offspring", 2, 1, 2);
        addSpawn(modifiers, "nautilus_headhunter_biome_modifier", anyBiome(), "caerula_arbor:nautilus_headhunter", 25, 1, 1);
        addSpawn(modifiers, "nucleic_maleficent_biome_modifier", anyBiome(), "caerula_arbor:nucleic_maleficent", 40, 1, 2);
        addSpawn(modifiers, "predator_abyssal_biome_modifier", anyBiome(), "caerula_arbor:predator_abyssal", 45, 1, 4);
        addSpawn(modifiers, "pregnant_fish_biome_modifier", anyBiome(), "caerula_arbor:pregnant_fish", 40, 1, 2);
        addSpawn(modifiers, "puncture_fish_biome_modifier", anyBiome(), "caerula_arbor:puncture_fish", 40, 1, 1);
        addSpawn(modifiers, "reaper_fish_biome_modifier", anyBiome(), "caerula_arbor:reaper_fish", 50, 1, 2);
        addFeature(modifiers, "redstoneiris_seeding_biome_modifier", biomes("snowy_plains", "plains", "flower_forest"), features("caerula_arbor:redstoneiris_seeding"), "vegetal_decoration");
        addSpawn(modifiers, "run_fish_biome_modifier", anyBiome(), "caerula_arbor:run_fish", 50, 2, 4);
        addSpawn(modifiers, "shooter_fish_biome_modifier", anyBiome(), "caerula_arbor:shooter_fish", 50, 2, 3);
        addSpawn(modifiers, "slider_fish_biome_modifier", anyBiome(), "caerula_arbor:slider_fish", 50, 2, 5);
        addFeature(modifiers, "slider_flower_biome_modifier", biomes("caerula_arbor:branded_land"), features("caerula_arbor:slider_flower"), "surface_structures");
        addSpawn(modifiers, "spike_chest_biome_modifier", biomes("snowy_beach", "snowy_plains", "caerula_arbor:branded_land"), "caerula_arbor:spike_chest", 1, 1, 1);
        addSpawn(modifiers, "splasher_abyssal_biome_modifier", anyBiome(), "caerula_arbor:splasher_abyssal", 50, 2, 3);
        addFeature(modifiers, "trail_mushroom_biome_modifier", biomes("caerula_arbor:branded_land"), features("caerula_arbor:trail_mushroom"), "vegetal_decoration");
        addSpawn(modifiers, "umbrella_abyssal_biome_modifier", anyBiome(), "caerula_arbor:umbrella_abyssal", 45, 1, 2);

        Path root = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(CaerulaArborMod.MODID)
                .resolve("forge")
                .resolve("biome_modifier");
        var futures = ImmutableList.<CompletableFuture<?>>builder();
        modifiers.forEach((name, json) -> futures.add(DataProvider.saveStable(cache, json, root.resolve(name + ".json"))));
        return CompletableFuture.allOf(futures.build().toArray(CompletableFuture[]::new));
    }

    private static void addSpawn(Map<String, JsonObject> modifiers, String name, JsonElement biomes, String entityType, int weight, int minCount, int maxCount) {
        var spawner = new JsonObject();
        spawner.addProperty("type", entityType);
        spawner.addProperty("weight", weight);
        spawner.addProperty("minCount", minCount);
        spawner.addProperty("maxCount", maxCount);

        var modifier = baseModifier("forge:add_spawns", biomes);
        modifier.add("spawners", spawner);
        modifiers.put(name, modifier);
    }

    private static void addFeature(Map<String, JsonObject> modifiers, String name, JsonElement biomes, JsonElement features, String step) {
        var modifier = baseModifier("forge:add_features", biomes);
        modifier.add("features", features);
        modifier.addProperty("step", step);
        modifiers.put(name, modifier);
    }

    private static JsonObject baseModifier(String type, JsonElement biomes) {
        var modifier = new JsonObject();
        modifier.addProperty("type", type);
        modifier.add("biomes", biomes);
        return modifier;
    }

    private static JsonObject anyBiome() {
        var biomes = new JsonObject();
        biomes.addProperty("type", "forge:any");
        return biomes;
    }

    private static JsonElement biomes(String... biomes) {
        return stringOrArray(biomes);
    }

    private static JsonElement features(String... features) {
        return stringOrArray(features);
    }

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

    @Override
    public @NotNull String getName() {
        return "Biome Modifiers: " + CaerulaArborMod.MODID;
    }
}