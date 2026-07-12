package com.apocalypse.caerulaarbor.datagen.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public final class StructureProvider {
    private StructureProvider() {
    }

    public static void bootstrap(BootstapContext<Structure> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);
        register(context, "abyssal_lab", new JigsawStructure(settings(biomes, "strongholds", "none", "snowy_plains", "snowy_taiga"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:abyssal_lab")), Optional.empty(), 1, UniformHeight.of(VerticalAnchor.absolute(-36), VerticalAnchor.absolute(-8)), false, Optional.empty(), 64));
        register(context, "aegir_lab", new JigsawStructure(settings(biomes, "fluid_springs", "none", "deep_cold_ocean", "deep_frozen_ocean", "deep_lukewarm_ocean", "deep_ocean"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:aegir_lab")), Optional.empty(), 1, UniformHeight.of(VerticalAnchor.absolute(53), VerticalAnchor.absolute(54)), false, Optional.empty(), 64));
        register(context, "air_base", new JigsawStructure(settings(biomes, "surface_structures", "none", "caerula_arbor:branded_land", "snowy_plains", "stony_shore"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:air_base")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "aircraft", new JigsawStructure(settings(biomes, "surface_structures", "bury", "beach", "snowy_beach", "desert"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:aircraft")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "anchor_ruin", new JigsawStructure(settings(biomes, "fluid_springs", "beard_thin", "cold_ocean", "deep_cold_ocean", "deep_lukewarm_ocean", "deep_ocean", "lukewarm_ocean", "ocean", "warm_ocean"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:anchor_ruin")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "believer_home", new JigsawStructure(settings(biomes, "surface_structures", "beard_thin", "caerula_arbor:branded_land", "beach", "snowy_beach", "stony_shore", "flower_forest", "forest"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:believer_home")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "bishop_cave", new JigsawStructure(settings(biomes, "underground_structures", "none", "deep_cold_ocean", "deep_lukewarm_ocean", "deep_ocean"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:bishop_cave")), Optional.empty(), 1, TrapezoidHeight.of(VerticalAnchor.absolute(-32), VerticalAnchor.absolute(-8)), false, Optional.empty(), 64));
        register(context, "brand_palace", new JigsawStructure(settings(biomes, "surface_structures", "beard_thin", "caerula_arbor:branded_land"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:brand_palace")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "brand_portal", new JigsawStructure(settings(biomes, "surface_structures", "none", "caerula_arbor:branded_land"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:brand_portal")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "branded_town", new JigsawStructure(settings(biomes, "surface_structures", "beard_thin", "caerula_arbor:branded_land", "birch_forest", "old_growth_birch_forest", "beach", "snowy_beach"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:branded_town")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "chest_museum", new JigsawStructure(settings(biomes, "surface_structures", "beard_thin", "dark_forest", "jungle"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:chest_museum")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "chitin_factory", new JigsawStructure(settings(biomes, "surface_structures", "beard_thin", "desert"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:chitin_factory")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "church", new JigsawStructure(settings(biomes, "strongholds", "bury", "plains", "snowy_plains"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:church")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "cloister", new JigsawStructure(settings(biomes, "surface_structures", "beard_thin", "plains", "sunflower_plains", "stony_shore", "meadow"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:cloister")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "containment_cave", new JigsawStructure(settings(biomes, "strongholds", "beard_thin", "nether_wastes", "warped_forest"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:containment_cave")), Optional.empty(), 1, UniformHeight.of(VerticalAnchor.absolute(24), VerticalAnchor.absolute(75)), false, Optional.empty(), 64));
        register(context, "coral_crown", new JigsawStructure(settings(biomes, "surface_structures", "none", "beach", "snowy_beach"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:coral_crown")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "deep_reef", new JigsawStructure(settings(biomes, "fluid_springs", "beard_thin", "deep_cold_ocean", "deep_frozen_ocean", "deep_lukewarm_ocean", "deep_ocean"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:deep_reef")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "flourish", new JigsawStructure(settings(biomes, "surface_structures", "none", "caerula_arbor:branded_land"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:flourish")), Optional.empty(), 7, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "golden_age", new JigsawStructure(settings(biomes, "fluid_springs", "none", "deep_cold_ocean", "deep_frozen_ocean", "deep_ocean"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:golden_age")), Optional.empty(), 1, UniformHeight.of(VerticalAnchor.absolute(56), VerticalAnchor.absolute(59)), false, Optional.empty(), 64));
        register(context, "haunted_house", new JigsawStructure(settings(biomes, "underground_structures", "none", "dark_forest", "swamp", "windswept_forest", "windswept_savanna"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:haunted_house")), Optional.empty(), 1, UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.absolute(16)), false, Optional.empty(), 64));
        register(context, "iberia_eye", new JigsawStructure(settings(biomes, "surface_structures", "beard_box", "caerula_arbor:branded_land"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:iberia_eye")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "inquisition_outpost", new JigsawStructure(settings(biomes, "surface_structures", "beard_thin", "meadow", "plains", "sunflower_plains"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:inquisition_outpost")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "isharmlacemetry", new JigsawStructure(settings(biomes, "fluid_springs", "none", "deep_cold_ocean", "deep_frozen_ocean"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:isharmlacemetry")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "izumik_island", new JigsawStructure(settings(biomes, "surface_structures", "none", "end_highlands"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:izumik_island")), Optional.empty(), 1, UniformHeight.of(VerticalAnchor.absolute(72), VerticalAnchor.absolute(96)), false, Optional.empty(), 64));
        register(context, "lamp", new JigsawStructure(settings(biomes, "surface_structures", "none", "birch_forest", "dark_forest", "old_growth_birch_forest", "windswept_forest", "caerula_arbor:branded_land"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:lamp")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "lighthouse", new JigsawStructure(settings(biomes, "surface_structures", "beard_thin", "beach", "snowy_beach", "stony_shore"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:lighthouse")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "oddfactory", new JigsawStructure(settings(biomes, "surface_structures", "beard_thin", "badlands", "eroded_badlands", "wooded_badlands"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:oddfactory")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "rhodes_site", new JigsawStructure(settings(biomes, "surface_structures", "none", "old_growth_spruce_taiga", "snowy_taiga", "taiga"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:rhodes_site")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "sadness_church", new JigsawStructure(settings(biomes, "surface_structures", "beard_thin", "caerula_arbor:branded_land", "snowy_plains", "birch_forest", "dark_forest", "old_growth_birch_forest", "windswept_forest"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:sadness_church")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "safe_house", new JigsawStructure(settings(biomes, "surface_structures", "beard_thin", "snowy_plains", "meadow", "caerula_arbor:branded_land", "badlands", "desert"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:safe_house")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "sink_field", new JigsawStructure(settings(biomes, "fluid_springs", "beard_thin", "cold_ocean", "deep_cold_ocean", "deep_frozen_ocean", "frozen_ocean"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:sink_field")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "sink_garden", new JigsawStructure(settings(biomes, "fluid_springs", "beard_box", "deep_lukewarm_ocean", "deep_ocean", "lukewarm_ocean", "ocean", "warm_ocean"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:sink_garden")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "sink_hall", new JigsawStructure(settings(biomes, "fluid_springs", "bury", "deep_cold_ocean", "deep_lukewarm_ocean"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:sink_hall")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "sink_remains", new JigsawStructure(settings(biomes, "fluid_springs", "beard_thin", "cold_ocean", "frozen_ocean", "lukewarm_ocean", "ocean", "warm_ocean"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:sink_remains")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "slider_statu", new JigsawStructure(settings(biomes, "fluid_springs", "beard_thin", "deep_cold_ocean", "deep_frozen_ocean", "deep_ocean"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:slider_statu")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "submarine", new JigsawStructure(settings(biomes, "fluid_springs", "beard_thin", "deep_cold_ocean", "deep_frozen_ocean", "deep_ocean", "deep_lukewarm_ocean"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:submarine")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "tide_station", new JigsawStructure(settings(biomes, "fluid_springs", "beard_thin", "lukewarm_ocean", "ocean", "warm_ocean"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:tide_station")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "trader_cave", new JigsawStructure(settings(biomes, "underground_decoration", "none", "lush_caves"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:trader_cave")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "trader_end", new JigsawStructure(settings(biomes, "surface_structures", "none", "end_midlands"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:trader_end")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "trader_oak", new JigsawStructure(settings(biomes, "surface_structures", "none", "flower_forest", "forest"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:trader_oak")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "trader_sand", new JigsawStructure(settings(biomes, "surface_structures", "none", "desert"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:trader_sand")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "trader_sky", new JigsawStructure(settings(biomes, "surface_structures", "none", "stony_peaks", "windswept_gravelly_hills", "windswept_hills"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:trader_sky")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "trader_tnt", new JigsawStructure(settings(biomes, "surface_structures", "none", "badlands"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:trader_tnt")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, "watchtower", new JigsawStructure(settings(biomes, "surface_structures", "beard_thin", "stony_shore", "beach", "snowy_beach", "plains", "snowy_plains"), pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, "caerula_arbor:watchtower")), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
    }

    private static void register(BootstapContext<Structure> context, String name, Structure structure) {
        context.register(WorldgenProvider.modKey(Registries.STRUCTURE, name), structure);
    }

    private static Structure.StructureSettings settings(HolderGetter<Biome> biomes, String step, String terrain, String... biomeIds) {
        return new Structure.StructureSettings(HolderSet.direct(Arrays.stream(biomeIds).map(id -> biome(biomes, id)).toList()), Map.of(), decoration(step), terrain(terrain));
    }

    private static Holder<Biome> biome(HolderGetter<Biome> biomes, String id) {
        return biomes.getOrThrow(WorldgenProvider.key(Registries.BIOME, id));
    }

    private static GenerationStep.Decoration decoration(String step) {
        return switch (step) {
            case "fluid_springs" -> GenerationStep.Decoration.FLUID_SPRINGS;
            case "strongholds" -> GenerationStep.Decoration.STRONGHOLDS;
            case "surface_structures" -> GenerationStep.Decoration.SURFACE_STRUCTURES;
            case "underground_decoration" -> GenerationStep.Decoration.UNDERGROUND_DECORATION;
            case "underground_structures" -> GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
            default -> throw new IllegalArgumentException("Unsupported structure step: " + step);
        };
    }

    private static TerrainAdjustment terrain(String terrain) {
        return switch (terrain) {
            case "beard_box" -> TerrainAdjustment.BEARD_BOX;
            case "beard_thin" -> TerrainAdjustment.BEARD_THIN;
            case "bury" -> TerrainAdjustment.BURY;
            case "none" -> TerrainAdjustment.NONE;
            default -> throw new IllegalArgumentException("Unsupported terrain adjustment: " + terrain);
        };
    }
}
