package com.susen36.caerulaarbor.datagen.worldgen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.List;
import java.util.function.Function;

/**
 * 生成 structure template pool 注册表数据
 *
 * <p>新增 template pool 时，在 {@link #bootstrap(BootstapContext)} 中指定 fallback pool、投影方式和加权元素，
 * 元素通常通过 {@link #single(String, Holder, int)} 绑定 nbt 结构位置、processor 列表和权重
 * <p>示例：
 * <pre>{@code
 * public static void bootstrap(BootstapContext<StructureTemplatePool> context) {
 *     // 查询 template pool 注册表，用于解析 fallback pool
 *     HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);
 *     // 调用 register(...) 注册模板池，并传入模板池 key、fallback key、投影方式和元素列表
 *     register(context, pools, WorldgenKeys.TemplatePools.ABYSSAL_LAB, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
 *             // 用 single(...) 添加一个结构 nbt，并用 processors(...) 忽略结构方块和空气
 *             single("caerula_arbor:example_structure", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
 *     ));
 * }
 *
 * private static Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer> single(
 *         String location,
 *         Holder<StructureProcessorList> processors,
 *         int weight
 * ) {
 *     // 创建单个 nbt 模板元素，并把它与权重打包成 Pair
 *     return Pair.of(StructurePoolElement.single(location, processors), weight);
 * }
 * }</pre>
 */
public final class TemplatePoolProvider {
    private static final Holder<StructureProcessorList> EMPTY_PROCESSORS = Holder.direct(new StructureProcessorList(List.of()));

    /**
     * 工具类不实例化
     */
    private TemplatePoolProvider() {
    }

    /**
     * 注册结构使用的模板池
     *
     * @param context Mojang 提供的注册表 bootstrap 上下文
     */
    public static void bootstrap(BootstapContext<StructureTemplatePool> context) {
        HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);
        register(context, pools, WorldgenKeys.TemplatePools.ABYSSAL_LAB, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:abyssal_lab_2", processors(Blocks.STRUCTURE_BLOCK, Blocks.CHERRY_PLANKS), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.AEGIR_LAB, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:aegir_lab", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.AIR_BASE, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:air_base_looted", processors(Blocks.STRUCTURE_BLOCK, Blocks.WHITE_WOOL, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.AIRCRAFT, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.TERRAIN_MATCHING, List.of(
                single("caerula_arbor:spacecraft", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.ANCHOR_RUIN, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:anchor_statue", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.BELIEVER_HOME, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:believer_home", processors(Blocks.STRUCTURE_BLOCK), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.BISHOP_CAVE, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:bishop_cave_looted", processors(Blocks.STRUCTURE_BLOCK, Blocks.POLISHED_DIORITE), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.BRAND_PALACE, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:brand_palace_looted", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.BRAND_PORTAL, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.TERRAIN_MATCHING, List.of(
                single("caerula_arbor:brand_portal_looted", processors(Blocks.STRUCTURE_BLOCK, Blocks.WHITE_WOOL, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.BRANDED_TOWN, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.TERRAIN_MATCHING, List.of(
                single("caerula_arbor:town_final", processors(Blocks.STRUCTURE_BLOCK, Blocks.WHITE_WOOL, Blocks.AIR, Blocks.DIRT), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.CHEST_MUSEUM, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:museum_relooted", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.CHITIN_FACTORY, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:factory_looted_booked", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.CHURCH, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:church", processors(Blocks.STRUCTURE_BLOCK, Blocks.POLISHED_DIORITE, Blocks.STRUCTURE_VOID), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.CLOISTER, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:cloister", processors(Blocks.STRUCTURE_BLOCK), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.CONTAINMENT_CAVE, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:containment_cave", processors(Blocks.STRUCTURE_BLOCK), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.CORAL_CROWN, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.TERRAIN_MATCHING, List.of(
                single("caerula_arbor:coral_crown", processors(Blocks.STRUCTURE_BLOCK, Blocks.STRUCTURE_VOID, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.DEEP_REEF, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:reef", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.FLOURISH, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:flourish_he_xin", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.FLOURISH_BAO_XIANG, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:flourish_r_mi", EMPTY_PROCESSORS, 2),
                single("caerula_arbor:flourish_r_zhen", EMPTY_PROCESSORS, 1),
                single("caerula_arbor:flourish_r_jia", EMPTY_PROCESSORS, 1),
                single("caerula_arbor:flourish_r_ci", EMPTY_PROCESSORS, 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.FLOURISH_DING_FALLBACK, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:flourish_he_ding", EMPTY_PROCESSORS, 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.FLOURISH_FLOURISH_0, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:flourish_he_xin", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.FLOURISH_QIAO, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:flourish_b_bo", EMPTY_PROCESSORS, 1),
                single("caerula_arbor:flourish_b_dou", EMPTY_PROCESSORS, 1),
                single("caerula_arbor:flourish_b_ping", EMPTY_PROCESSORS, 1),
                single("caerula_arbor:flourish_b_wang", EMPTY_PROCESSORS, 2)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.FLOURISH_ZHONG, WorldgenKeys.TemplatePools.FLOURISH_DING_FALLBACK, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:flourish_he_zhong", EMPTY_PROCESSORS, 1),
                single("caerula_arbor:flourish_he_ding", EMPTY_PROCESSORS, 4)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.GOLDEN_AGE, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:ship_wreck_bossed", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.HAUNTED_HOUSE, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:haunted_house", processors(Blocks.STRUCTURE_BLOCK, Blocks.DIRT), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.IBERIA_EYE, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:iberiaeye_looted_1", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.INQUISITION_OUTPOST, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:inquisition_home", processors(Blocks.STRUCTURE_BLOCK), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.ISHARMLACEMETRY, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:isharmla_cemetery", processors(Blocks.STRUCTURE_BLOCK, Blocks.LIGHT_BLUE_WOOL), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.IZUMIK_ISLAND, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:izumik_island", processors(Blocks.STRUCTURE_BLOCK), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.LAMP, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:lamp", processors(Blocks.STRUCTURE_BLOCK, Blocks.WHITE_WOOL, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.LIGHTHOUSE, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:lighthouse_coraled", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.ODDFACTORY, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:odd_factory_looted", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.RHODES_SITE, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:rhodes_site_looted", processors(Blocks.STRUCTURE_BLOCK, Blocks.STRUCTURE_VOID, Blocks.AIR, Blocks.CHERRY_PLANKS), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.SADNESS_CHURCH, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:sadness_church", processors(Blocks.STRUCTURE_BLOCK), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.SAFE_HOUSE, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:safe_house3", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.SINK_FIELD, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:sink_field", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.SINK_GARDEN, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:sink_garden", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.SINK_HALL, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:sink_hall", processors(Blocks.STRUCTURE_BLOCK, Blocks.WHITE_WOOL), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.SINK_REMAINS, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:sink_wreck", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.SLIDER_STATU, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:slider_flower", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.SUBMARINE, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:submarine_looted", processors(Blocks.STRUCTURE_BLOCK, Blocks.POLISHED_DIORITE, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.TIDE_STATION, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:tide_station_2", processors(Blocks.STRUCTURE_BLOCK, Blocks.AIR), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.TIDE_STATION_AAA, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:abyssal_lab_2", EMPTY_PROCESSORS, 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.TRADER_CAVE, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:trader_cave", processors(Blocks.STRUCTURE_BLOCK, Blocks.WHITE_WOOL), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.TRADER_END, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:trader_end", processors(Blocks.STRUCTURE_BLOCK, Blocks.WHITE_WOOL), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.TRADER_OAK, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:trader_oak", processors(Blocks.STRUCTURE_BLOCK, Blocks.WHITE_WOOL), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.TRADER_SAND, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:trader_sand", processors(Blocks.STRUCTURE_BLOCK, Blocks.WHITE_WOOL), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.TRADER_SKY, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:trader_sky", processors(Blocks.STRUCTURE_BLOCK, Blocks.WHITE_WOOL), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.TRADER_TNT, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:trader_tnt", processors(Blocks.STRUCTURE_BLOCK, Blocks.WHITE_WOOL), 1)
        ));
        register(context, pools, WorldgenKeys.TemplatePools.WATCHTOWER, WorldgenKeys.TemplatePools.MINECRAFT_EMPTY, StructureTemplatePool.Projection.RIGID, List.of(
                single("caerula_arbor:watchtower", processors(Blocks.STRUCTURE_BLOCK, Blocks.STRUCTURE_VOID, Blocks.AIR), 1)
        ));
    }

    /**
     * 注册结构模板池
     *
     * @param context    注册表 bootstrap 上下文
     * @param pools      template pool 查询器
     * @param key        template pool 注册 key
     * @param fallback   fallback pool 注册 key
     * @param projection 模板投影方式
     * @param elements   加权模板元素
     */
    private static void register(BootstapContext<StructureTemplatePool> context, HolderGetter<StructureTemplatePool> pools, ResourceKey<StructureTemplatePool> key, ResourceKey<StructureTemplatePool> fallback, StructureTemplatePool.Projection projection, List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> elements) {
        context.register(key, new StructureTemplatePool(pools.getOrThrow(fallback), elements, projection));
    }

    /**
     * 创建单个结构模板元素
     *
     * @param location   nbt 结构位置
     * @param processors processor 列表 holder
     * @param weight     元素权重
     * @return 加权结构模板元素
     */
    private static Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer> single(String location, Holder<StructureProcessorList> processors, int weight) {
        return Pair.of(StructurePoolElement.single(location, processors), weight);
    }

    /**
     * 创建忽略指定方块的 processor 列表
     *
     * @param blocks 要忽略的方块字段
     * @return processor 列表 holder
     */
    private static Holder<StructureProcessorList> processors(Block... blocks) {
        return Holder.direct(new StructureProcessorList(List.of(new BlockIgnoreProcessor(List.of(blocks)))));
    }
}
