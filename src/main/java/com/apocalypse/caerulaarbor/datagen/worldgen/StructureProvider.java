package com.apocalypse.caerulaarbor.datagen.worldgen;

import com.apocalypse.caerulaarbor.init.CABiomes;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
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

/**
 * 生成 structure 注册表数据
 *
 * <p>新增 structure 时，在 {@link #bootstrap(BootstapContext)} 中创建 {@link JigsawStructure}，
 * 用 {@code settings(...)} 指定可生成 biome、生成阶段和地形调整，
 * 再绑定 template pool、起始高度和搜索深度
 * <p>示例：
 * <pre>{@code
 * public static void bootstrap(BootstapContext<Structure> context) {
 *     // 查询 biome 注册表，用于 settings(...) 解析结构可生成 biome
 *     HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
 *     // 查询 template pool 注册表，用于 JigsawStructure 选择起始模板池
 *     HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);
 *
 *     // 调用 register(...) 写入 structure，并创建 JigsawStructure 定义
 *     register(context, WorldgenKeys.Structures.ABYSSAL_LAB, new JigsawStructure(
 *             // 用 settings(...) 设置可生成 biome、生成阶段和地形调整
 *             settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN, Biomes.PLAINS, CABiomes.BRANDED_LAND),
 *             // 直接引用 template pool key 查找起始模板池
 *             pools.getOrThrow(WorldgenKeys.TemplatePools.ABYSSAL_LAB),
 *             // 不指定起始 jigsaw 名称
 *             Optional.empty(),
 *             // 设置 jigsaw 递归深度
 *             1,
 *             // 设置结构起始高度
 *             ConstantHeight.of(VerticalAnchor.absolute(0)),
 *             // 不使用扩大包围盒适配地形
 *             false,
 *             // 不指定最大距离投影
 *             Optional.empty(),
 *             // 设置最大结构搜索距离
 *             64
 *     ));
 * }
 * }</pre>
 */
public final class StructureProvider {
    /**
     * 工具类不实例化
     */
    private StructureProvider() {
    }

    /**
     * 注册结构定义
     *
     * @param context Mojang 提供的注册表 bootstrap 上下文
     */
    public static void bootstrap(BootstapContext<Structure> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);
        register(context, WorldgenKeys.Structures.ABYSSAL_LAB, new JigsawStructure(settings(biomes, GenerationStep.Decoration.STRONGHOLDS, TerrainAdjustment.NONE, Biomes.SNOWY_PLAINS, Biomes.SNOWY_TAIGA), pools.getOrThrow(WorldgenKeys.TemplatePools.ABYSSAL_LAB), Optional.empty(), 1, UniformHeight.of(VerticalAnchor.absolute(-36), VerticalAnchor.absolute(-8)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.AEGIR_LAB, new JigsawStructure(settings(biomes, GenerationStep.Decoration.FLUID_SPRINGS, TerrainAdjustment.NONE, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_OCEAN), pools.getOrThrow(WorldgenKeys.TemplatePools.AEGIR_LAB), Optional.empty(), 1, UniformHeight.of(VerticalAnchor.absolute(53), VerticalAnchor.absolute(54)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.AIR_BASE, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE, CABiomes.BRANDED_LAND, Biomes.SNOWY_PLAINS, Biomes.STONY_SHORE), pools.getOrThrow(WorldgenKeys.TemplatePools.AIR_BASE), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.AIRCRAFT, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BURY, Biomes.BEACH, Biomes.SNOWY_BEACH, Biomes.DESERT), pools.getOrThrow(WorldgenKeys.TemplatePools.AIRCRAFT), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.ANCHOR_RUIN, new JigsawStructure(settings(biomes, GenerationStep.Decoration.FLUID_SPRINGS, TerrainAdjustment.BEARD_THIN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.WARM_OCEAN), pools.getOrThrow(WorldgenKeys.TemplatePools.ANCHOR_RUIN), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.BELIEVER_HOME, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN, CABiomes.BRANDED_LAND, Biomes.BEACH, Biomes.SNOWY_BEACH, Biomes.STONY_SHORE, Biomes.FLOWER_FOREST, Biomes.FOREST), pools.getOrThrow(WorldgenKeys.TemplatePools.BELIEVER_HOME), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.BISHOP_CAVE, new JigsawStructure(settings(biomes, GenerationStep.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_OCEAN), pools.getOrThrow(WorldgenKeys.TemplatePools.BISHOP_CAVE), Optional.empty(), 1, TrapezoidHeight.of(VerticalAnchor.absolute(-32), VerticalAnchor.absolute(-8)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.BRAND_PALACE, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN, CABiomes.BRANDED_LAND), pools.getOrThrow(WorldgenKeys.TemplatePools.BRAND_PALACE), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.BRAND_PORTAL, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE, CABiomes.BRANDED_LAND), pools.getOrThrow(WorldgenKeys.TemplatePools.BRAND_PORTAL), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.BRANDED_TOWN, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN, CABiomes.BRANDED_LAND, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.BEACH, Biomes.SNOWY_BEACH), pools.getOrThrow(WorldgenKeys.TemplatePools.BRANDED_TOWN), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.CHEST_MUSEUM, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN, Biomes.DARK_FOREST, Biomes.JUNGLE), pools.getOrThrow(WorldgenKeys.TemplatePools.CHEST_MUSEUM), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.CHITIN_FACTORY, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN, Biomes.DESERT), pools.getOrThrow(WorldgenKeys.TemplatePools.CHITIN_FACTORY), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.CHURCH, new JigsawStructure(settings(biomes, GenerationStep.Decoration.STRONGHOLDS, TerrainAdjustment.BURY, Biomes.PLAINS, Biomes.SNOWY_PLAINS), pools.getOrThrow(WorldgenKeys.TemplatePools.CHURCH), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.CLOISTER, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN, Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.STONY_SHORE, Biomes.MEADOW), pools.getOrThrow(WorldgenKeys.TemplatePools.CLOISTER), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.CONTAINMENT_CAVE, new JigsawStructure(settings(biomes, GenerationStep.Decoration.STRONGHOLDS, TerrainAdjustment.BEARD_THIN, Biomes.NETHER_WASTES, Biomes.WARPED_FOREST), pools.getOrThrow(WorldgenKeys.TemplatePools.CONTAINMENT_CAVE), Optional.empty(), 1, UniformHeight.of(VerticalAnchor.absolute(24), VerticalAnchor.absolute(75)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.CORAL_CROWN, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE, Biomes.BEACH, Biomes.SNOWY_BEACH), pools.getOrThrow(WorldgenKeys.TemplatePools.CORAL_CROWN), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.DEEP_REEF, new JigsawStructure(settings(biomes, GenerationStep.Decoration.FLUID_SPRINGS, TerrainAdjustment.BEARD_THIN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_OCEAN), pools.getOrThrow(WorldgenKeys.TemplatePools.DEEP_REEF), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.FLOURISH, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE, CABiomes.BRANDED_LAND), pools.getOrThrow(WorldgenKeys.TemplatePools.FLOURISH), Optional.empty(), 7, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.GOLDEN_AGE, new JigsawStructure(settings(biomes, GenerationStep.Decoration.FLUID_SPRINGS, TerrainAdjustment.NONE, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_OCEAN), pools.getOrThrow(WorldgenKeys.TemplatePools.GOLDEN_AGE), Optional.empty(), 1, UniformHeight.of(VerticalAnchor.absolute(56), VerticalAnchor.absolute(59)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.HAUNTED_HOUSE, new JigsawStructure(settings(biomes, GenerationStep.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE, Biomes.DARK_FOREST, Biomes.SWAMP, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_SAVANNA), pools.getOrThrow(WorldgenKeys.TemplatePools.HAUNTED_HOUSE), Optional.empty(), 1, UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.absolute(16)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.IBERIA_EYE, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_BOX, CABiomes.BRANDED_LAND), pools.getOrThrow(WorldgenKeys.TemplatePools.IBERIA_EYE), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.INQUISITION_OUTPOST, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN, Biomes.MEADOW, Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS), pools.getOrThrow(WorldgenKeys.TemplatePools.INQUISITION_OUTPOST), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.ISHARMLACEMETRY, new JigsawStructure(settings(biomes, GenerationStep.Decoration.FLUID_SPRINGS, TerrainAdjustment.NONE, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN), pools.getOrThrow(WorldgenKeys.TemplatePools.ISHARMLACEMETRY), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.IZUMIK_ISLAND, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE, Biomes.END_HIGHLANDS), pools.getOrThrow(WorldgenKeys.TemplatePools.IZUMIK_ISLAND), Optional.empty(), 1, UniformHeight.of(VerticalAnchor.absolute(72), VerticalAnchor.absolute(96)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.LAMP, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE, Biomes.BIRCH_FOREST, Biomes.DARK_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.WINDSWEPT_FOREST, CABiomes.BRANDED_LAND), pools.getOrThrow(WorldgenKeys.TemplatePools.LAMP), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.LIGHTHOUSE, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN, Biomes.BEACH, Biomes.SNOWY_BEACH, Biomes.STONY_SHORE), pools.getOrThrow(WorldgenKeys.TemplatePools.LIGHTHOUSE), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.ODDFACTORY, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS), pools.getOrThrow(WorldgenKeys.TemplatePools.ODDFACTORY), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.RHODES_SITE, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.SNOWY_TAIGA, Biomes.TAIGA), pools.getOrThrow(WorldgenKeys.TemplatePools.RHODES_SITE), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.SADNESS_CHURCH, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN, CABiomes.BRANDED_LAND, Biomes.SNOWY_PLAINS, Biomes.BIRCH_FOREST, Biomes.DARK_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.WINDSWEPT_FOREST), pools.getOrThrow(WorldgenKeys.TemplatePools.SADNESS_CHURCH), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.SAFE_HOUSE, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN, Biomes.SNOWY_PLAINS, Biomes.MEADOW, CABiomes.BRANDED_LAND, Biomes.BADLANDS, Biomes.DESERT), pools.getOrThrow(WorldgenKeys.TemplatePools.SAFE_HOUSE), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.SINK_FIELD, new JigsawStructure(settings(biomes, GenerationStep.Decoration.FLUID_SPRINGS, TerrainAdjustment.BEARD_THIN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.FROZEN_OCEAN), pools.getOrThrow(WorldgenKeys.TemplatePools.SINK_FIELD), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.SINK_GARDEN, new JigsawStructure(settings(biomes, GenerationStep.Decoration.FLUID_SPRINGS, TerrainAdjustment.BEARD_BOX, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.WARM_OCEAN), pools.getOrThrow(WorldgenKeys.TemplatePools.SINK_GARDEN), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.SINK_HALL, new JigsawStructure(settings(biomes, GenerationStep.Decoration.FLUID_SPRINGS, TerrainAdjustment.BURY, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN), pools.getOrThrow(WorldgenKeys.TemplatePools.SINK_HALL), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.SINK_REMAINS, new JigsawStructure(settings(biomes, GenerationStep.Decoration.FLUID_SPRINGS, TerrainAdjustment.BEARD_THIN, Biomes.COLD_OCEAN, Biomes.FROZEN_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.WARM_OCEAN), pools.getOrThrow(WorldgenKeys.TemplatePools.SINK_REMAINS), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.SLIDER_STATU, new JigsawStructure(settings(biomes, GenerationStep.Decoration.FLUID_SPRINGS, TerrainAdjustment.BEARD_THIN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_OCEAN), pools.getOrThrow(WorldgenKeys.TemplatePools.SLIDER_STATU), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.SUBMARINE, new JigsawStructure(settings(biomes, GenerationStep.Decoration.FLUID_SPRINGS, TerrainAdjustment.BEARD_THIN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN), pools.getOrThrow(WorldgenKeys.TemplatePools.SUBMARINE), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.TIDE_STATION, new JigsawStructure(settings(biomes, GenerationStep.Decoration.FLUID_SPRINGS, TerrainAdjustment.BEARD_THIN, Biomes.LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.WARM_OCEAN), pools.getOrThrow(WorldgenKeys.TemplatePools.TIDE_STATION), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.TRADER_CAVE, new JigsawStructure(settings(biomes, GenerationStep.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.NONE, Biomes.LUSH_CAVES), pools.getOrThrow(WorldgenKeys.TemplatePools.TRADER_CAVE), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.TRADER_END, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE, Biomes.END_MIDLANDS), pools.getOrThrow(WorldgenKeys.TemplatePools.TRADER_END), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.TRADER_OAK, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE, Biomes.FLOWER_FOREST, Biomes.FOREST), pools.getOrThrow(WorldgenKeys.TemplatePools.TRADER_OAK), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.TRADER_SAND, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE, Biomes.DESERT), pools.getOrThrow(WorldgenKeys.TemplatePools.TRADER_SAND), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.TRADER_SKY, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE, Biomes.STONY_PEAKS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_HILLS), pools.getOrThrow(WorldgenKeys.TemplatePools.TRADER_SKY), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.TRADER_TNT, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE, Biomes.BADLANDS), pools.getOrThrow(WorldgenKeys.TemplatePools.TRADER_TNT), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
        register(context, WorldgenKeys.Structures.WATCHTOWER, new JigsawStructure(settings(biomes, GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN, Biomes.STONY_SHORE, Biomes.BEACH, Biomes.SNOWY_BEACH, Biomes.PLAINS, Biomes.SNOWY_PLAINS), pools.getOrThrow(WorldgenKeys.TemplatePools.WATCHTOWER), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.empty(), 64));
    }

    /**
     * 注册结构定义
     *
     * @param context   注册表 bootstrap 上下文
     * @param key       structure 注册 key
     * @param structure 结构实例
     */
    private static void register(BootstapContext<Structure> context, ResourceKey<Structure> key, Structure structure) {
        context.register(key, structure);
    }

    /**
     * 构建结构生成设置
     *
     * @param biomes   biome 查询器
     * @param step      生成阶段
     * @param terrain   地形调整
     * @param biomeKeys 可生成 biome key
     * @return 结构生成设置
     */
    @SafeVarargs
    private static Structure.StructureSettings settings(HolderGetter<Biome> biomes, GenerationStep.Decoration step, TerrainAdjustment terrain, ResourceKey<Biome>... biomeKeys) {
        return new Structure.StructureSettings(HolderSet.direct(Arrays.stream(biomeKeys).map(biomes::getOrThrow).toList()), Map.of(), step, terrain);
    }
}
