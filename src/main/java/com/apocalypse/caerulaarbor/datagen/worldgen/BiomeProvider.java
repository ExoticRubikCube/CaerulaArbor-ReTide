package com.apocalypse.caerulaarbor.datagen.worldgen;

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

public class BiomeProvider {
    private BiomeProvider() {
    }

    public static void bootstrap(BootstapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);
        HolderGetter<SoundEvent> soundEvents = context.lookup(Registries.SOUND_EVENT);
        context.register(WorldgenProvider.modKey(Registries.BIOME, "branded_land"), brandedLand(placedFeatures, carvers, soundEvents));
    }

    private static Biome brandedLand(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> carvers, HolderGetter<SoundEvent> soundEvents) {
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

    private static void addVegetationFeatures(BiomeGenerationSettings.Builder generation) {
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, placedFeature("branded_land_tree"));
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_TALL_GRASS);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_TAIGA_2);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_TAIGA);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_TAIGA);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
    }

    @SuppressWarnings("SameParameterValue")
    private static ResourceKey<PlacedFeature> placedFeature(String name) {
        return WorldgenProvider.modKey(Registries.PLACED_FEATURE, name);
    }

    private static Holder<SoundEvent> shallowSea(HolderGetter<SoundEvent> soundEvents) {
        return soundEvents.getOrThrow(WorldgenProvider.modKey(Registries.SOUND_EVENT, "shallow_sea"));
    }

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
