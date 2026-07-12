package com.apocalypse.caerulaarbor.datagen.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

public final class StructureSetProvider {
    private StructureSetProvider() {
    }

    public static void bootstrap(BootstapContext<StructureSet> context) {
        HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);
        register(context, structures, "abyssal_lab", "caerula_arbor:abyssal_lab", 1, 56, 28, 983379551);
        register(context, structures, "aegir_lab", "caerula_arbor:aegir_lab", 1, 64, 32, 1001069449);
        register(context, structures, "air_base", "caerula_arbor:air_base", 1, 58, 50, 254265812);
        register(context, structures, "aircraft", "caerula_arbor:aircraft", 1, 59, 55, 1199381489);
        register(context, structures, "anchor_ruin", "caerula_arbor:anchor_ruin", 1, 52, 45, 568563000);
        register(context, structures, "believer_home", "caerula_arbor:believer_home", 1, 64, 48, 2018597269);
        register(context, structures, "bishop_cave", "caerula_arbor:bishop_cave", 1, 48, 24, 1714840136);
        register(context, structures, "brand_palace", "caerula_arbor:brand_palace", 1, 36, 18, 513801667);
        register(context, structures, "brand_portal", "caerula_arbor:brand_portal", 1, 40, 16, 436443816);
        register(context, structures, "branded_town", "caerula_arbor:branded_town", 1, 55, 25, 1577871037);
        register(context, structures, "chest_museum", "caerula_arbor:chest_museum", 1, 56, 47, 195846613);
        register(context, structures, "chitin_factory", "caerula_arbor:chitin_factory", 1, 58, 52, 940612460);
        register(context, structures, "church", "caerula_arbor:church", 1, 42, 21, 1559158881);
        register(context, structures, "cloister", "caerula_arbor:cloister", 1, 68, 24, 732155636);
        register(context, structures, "containment_cave", "caerula_arbor:containment_cave", 1, 36, 12, 282080457);
        register(context, structures, "coral_crown", "caerula_arbor:coral_crown", 1, 36, 20, 173104000);
        register(context, structures, "deep_reef", "caerula_arbor:deep_reef", 1, 50, 47, 2042074128);
        register(context, structures, "flourish", "caerula_arbor:flourish", 1, 36, 18, 676384344);
        register(context, structures, "golden_age", "caerula_arbor:golden_age", 1, 75, 63, 357548055);
        register(context, structures, "haunted_house", "caerula_arbor:haunted_house", 1, 59, 58, 963002566);
        register(context, structures, "iberia_eye", "caerula_arbor:iberia_eye", 1, 56, 24, 13751763);
        register(context, structures, "inquisition_outpost", "caerula_arbor:inquisition_outpost", 1, 50, 39, 1901042701);
        register(context, structures, "isharmlacemetry", "caerula_arbor:isharmlacemetry", 1, 62, 35, 768718172);
        register(context, structures, "izumik_island", "caerula_arbor:izumik_island", 1, 64, 63, 197302117);
        register(context, structures, "lamp", "caerula_arbor:lamp", 1, 30, 20, 1378960);
        register(context, structures, "lighthouse", "caerula_arbor:lighthouse", 1, 39, 12, 1463095153);
        register(context, structures, "oddfactory", "caerula_arbor:oddfactory", 1, 58, 55, 1650518623);
        register(context, structures, "rhodes_site", "caerula_arbor:rhodes_site", 1, 47, 45, 1366962780);
        register(context, structures, "sadness_church", "caerula_arbor:sadness_church", 1, 68, 32, 1995257809);
        register(context, structures, "safe_house", "caerula_arbor:safe_house", 1, 56, 21, 1115414938);
        register(context, structures, "sink_field", "caerula_arbor:sink_field", 1, 58, 45, 668416290);
        register(context, structures, "sink_garden", "caerula_arbor:sink_garden", 1, 50, 32, 2090523120);
        register(context, structures, "sink_hall", "caerula_arbor:sink_hall", 1, 60, 50, 969067352);
        register(context, structures, "sink_remains", "caerula_arbor:sink_remains", 1, 60, 55, 594832990);
        register(context, structures, "slider_statu", "caerula_arbor:slider_statu", 1, 66, 32, 890563194);
        register(context, structures, "submarine", "caerula_arbor:submarine", 1, 55, 47, 1895955932);
        register(context, structures, "tide_station", "caerula_arbor:tide_station", 1, 55, 45, 1973698396);
        register(context, structures, "trader_cave", "caerula_arbor:trader_cave", 1, 10, 8, 184761123);
        register(context, structures, "trader_end", "caerula_arbor:trader_end", 1, 79, 64, 73580191);
        register(context, structures, "trader_oak", "caerula_arbor:trader_oak", 1, 40, 38, 231342564);
        register(context, structures, "trader_sand", "caerula_arbor:trader_sand", 1, 64, 48, 1261542217);
        register(context, structures, "trader_sky", "caerula_arbor:trader_sky", 1, 33, 19, 1011613333);
        register(context, structures, "trader_tnt", "caerula_arbor:trader_tnt", 1, 32, 28, 852904412);
        register(context, structures, "watchtower", "caerula_arbor:watchtower", 1, 46, 16, 1358541846);
    }

    @SuppressWarnings("SameParameterValue")
    private static void register(BootstapContext<StructureSet> context, HolderGetter<Structure> structures, String name, String structure, int weight, int spacing, int separation, int salt) {
        context.register(WorldgenProvider.modKey(Registries.STRUCTURE_SET, name), new StructureSet(java.util.List.of(new StructureSet.StructureSelectionEntry(structures.getOrThrow(WorldgenProvider.key(Registries.STRUCTURE, structure)), weight)), new RandomSpreadStructurePlacement(spacing, separation, RandomSpreadType.LINEAR, salt)));
    }
}
