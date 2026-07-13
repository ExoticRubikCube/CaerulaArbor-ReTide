package com.apocalypse.caerulaarbor.datagen.worldgen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * 生成 structure template pool 注册表数据
 */
public final class TemplatePoolProvider {
    private static final Holder<StructureProcessorList> EMPTY_PROCESSORS = Holder.direct(new StructureProcessorList(List.of()));

    private static HolderGetter<Block> blockGetter;

    private TemplatePoolProvider() {
    }

    /**
     * 注册结构使用的模板池
     *
     * @param context Mojang 提供的注册表 bootstrap 上下文
     */
    public static void bootstrap(BootstapContext<StructureTemplatePool> context) {
        HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);
        blockGetter = context.lookup(Registries.BLOCK);
        register(context, pools, "abyssal_lab", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:abyssal_lab_2", processors("minecraft:structure_block", "minecraft:cherry_planks"), 1)
        ));
        register(context, pools, "aegir_lab", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:aegir_lab", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "air_base", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:air_base_looted", processors("minecraft:structure_block", "minecraft:white_wool", "minecraft:air"), 1)
        ));
        register(context, pools, "aircraft", "minecraft:empty", StructureTemplatePool.Projection.TERRAIN_MATCHING, List.of(
                single("caerula_arbor:spacecraft", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "anchor_ruin", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:anchor_statue", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "believer_home", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:believer_home", processors("minecraft:structure_block"), 1)
        ));
        register(context, pools, "bishop_cave", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:bishop_cave_looted", processors("minecraft:structure_block", "minecraft:polished_diorite"), 1)
        ));
        register(context, pools, "brand_palace", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:brand_palace_looted", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "brand_portal", "minecraft:empty", StructureTemplatePool.Projection.TERRAIN_MATCHING, List.of(
                single("caerula_arbor:brand_portal_looted", processors("minecraft:structure_block", "minecraft:white_wool", "minecraft:air"), 1)
        ));
        register(context, pools, "branded_town", "minecraft:empty", StructureTemplatePool.Projection.TERRAIN_MATCHING, List.of(
                single("caerula_arbor:town_final", processors("minecraft:structure_block", "minecraft:white_wool", "minecraft:air", "minecraft:dirt"), 1)
        ));
        register(context, pools, "chest_museum", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:museum_relooted", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "chitin_factory", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:factory_looted_booked", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "church", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:church", processors("minecraft:structure_block", "minecraft:polished_diorite", "minecraft:structure_void"), 1)
        ));
        register(context, pools, "cloister", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:cloister", processors("minecraft:structure_block"), 1)
        ));
        register(context, pools, "containment_cave", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:containment_cave", processors("minecraft:structure_block"), 1)
        ));
        register(context, pools, "coral_crown", "minecraft:empty", StructureTemplatePool.Projection.TERRAIN_MATCHING, List.of(
                single("caerula_arbor:coral_crown", processors("minecraft:structure_block", "minecraft:structure_void", "minecraft:air"), 1)
        ));
        register(context, pools, "deep_reef", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:reef", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "flourish", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:flourish_he_xin", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "flourish_bao_xiang", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:flourish_r_mi", EMPTY_PROCESSORS, 2),
                single("caerula_arbor:flourish_r_zhen", EMPTY_PROCESSORS, 1),
                single("caerula_arbor:flourish_r_jia", EMPTY_PROCESSORS, 1),
                single("caerula_arbor:flourish_r_ci", EMPTY_PROCESSORS, 1)
        ));
        register(context, pools, "flourish_ding_fallback", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:flourish_he_ding", EMPTY_PROCESSORS, 1)
        ));
        register(context, pools, "flourish_flourish_0", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:flourish_he_xin", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "flourish_qiao", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:flourish_b_bo", EMPTY_PROCESSORS, 1),
                single("caerula_arbor:flourish_b_dou", EMPTY_PROCESSORS, 1),
                single("caerula_arbor:flourish_b_ping", EMPTY_PROCESSORS, 1),
                single("caerula_arbor:flourish_b_wang", EMPTY_PROCESSORS, 2)
        ));
        register(context, pools, "flourish_zhong", "caerula_arbor:flourish_ding_fallback", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:flourish_he_zhong", EMPTY_PROCESSORS, 1),
                single("caerula_arbor:flourish_he_ding", EMPTY_PROCESSORS, 4)
        ));
        register(context, pools, "golden_age", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:ship_wreck_bossed", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "haunted_house", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:haunted_house", processors("minecraft:structure_block", "minecraft:dirt"), 1)
        ));
        register(context, pools, "iberia_eye", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:iberiaeye_looted_1", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "inquisition_outpost", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:inquisition_home", processors("minecraft:structure_block"), 1)
        ));
        register(context, pools, "isharmlacemetry", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:isharmla_cemetery", processors("minecraft:structure_block", "minecraft:light_blue_wool"), 1)
        ));
        register(context, pools, "izumik_island", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:izumik_island", processors("minecraft:structure_block"), 1)
        ));
        register(context, pools, "lamp", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:lamp", processors("minecraft:structure_block", "minecraft:white_wool", "minecraft:air"), 1)
        ));
        register(context, pools, "lighthouse", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:lighthouse_coraled", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "oddfactory", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:odd_factory_looted", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "rhodes_site", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:rhodes_site_looted", processors("minecraft:structure_block", "minecraft:structure_void", "minecraft:air", "minecraft:cherry_planks"), 1)
        ));
        register(context, pools, "sadness_church", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:sadness_church", processors("minecraft:structure_block"), 1)
        ));
        register(context, pools, "safe_house", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:safe_house3", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "sink_field", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:sink_field", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "sink_garden", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:sink_garden", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "sink_hall", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:sink_hall", processors("minecraft:structure_block", "minecraft:white_wool"), 1)
        ));
        register(context, pools, "sink_remains", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:sink_wreck", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "slider_statu", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:slider_flower", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "submarine", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:submarine_looted", processors("minecraft:structure_block", "minecraft:polished_diorite", "minecraft:air"), 1)
        ));
        register(context, pools, "tide_station", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:tide_station_2", processors("minecraft:structure_block", "minecraft:air"), 1)
        ));
        register(context, pools, "tide_station_aaa", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:abyssal_lab_2", EMPTY_PROCESSORS, 1)
        ));
        register(context, pools, "trader_cave", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:trader_cave", processors("minecraft:structure_block", "minecraft:white_wool"), 1)
        ));
        register(context, pools, "trader_end", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:trader_end", processors("minecraft:structure_block", "minecraft:white_wool"), 1)
        ));
        register(context, pools, "trader_oak", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:trader_oak", processors("minecraft:structure_block", "minecraft:white_wool"), 1)
        ));
        register(context, pools, "trader_sand", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:trader_sand", processors("minecraft:structure_block", "minecraft:white_wool"), 1)
        ));
        register(context, pools, "trader_sky", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:trader_sky", processors("minecraft:structure_block", "minecraft:white_wool"), 1)
        ));
        register(context, pools, "trader_tnt", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:trader_tnt", processors("minecraft:structure_block", "minecraft:white_wool"), 1)
        ));
        register(context, pools, "watchtower", "minecraft:empty", StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:watchtower", processors("minecraft:structure_block", "minecraft:structure_void", "minecraft:air"), 1)
        ));
    }

    private static void register(BootstapContext<StructureTemplatePool> context, HolderGetter<StructureTemplatePool> pools, String name, String fallback, StructureTemplatePool.Projection projection, List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> elements) {
        context.register(WorldgenProvider.modKey(Registries.TEMPLATE_POOL, name), new StructureTemplatePool(pools.getOrThrow(WorldgenProvider.key(Registries.TEMPLATE_POOL, fallback)), elements, projection));
    }

    private static Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer> single(String location, Holder<StructureProcessorList> processors, int weight) {
        return Pair.of(StructurePoolElement.single(location, processors), weight);
    }

    private static Holder<StructureProcessorList> processors(String... blocks) {
        return Holder.direct(new StructureProcessorList(List.of(new BlockIgnoreProcessor(Arrays.stream(blocks).map(TemplatePoolProvider::block).toList()))));
    }

    private static Block block(String id) {
        return blockGetter.getOrThrow(ResourceKey.create(Registries.BLOCK, WorldgenProvider.location(id))).value();
    }
}
