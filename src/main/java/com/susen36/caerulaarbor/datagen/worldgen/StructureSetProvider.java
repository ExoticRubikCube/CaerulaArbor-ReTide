package com.susen36.caerulaarbor.datagen.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

/**
 * 生成 structure_set 注册表数据
 *
 * <p>新增 structure set 时，在 {@link #bootstrap(BootstapContext)} 中引用已注册的 structure，
 * 配置权重、间距、最小分离距离和随机 salt，再写入 {@link StructureSet}
 * <p>示例：
 * <pre>{@code
 * public static void bootstrap(BootstapContext<StructureSet> context) {
 *     // 查询 structure 注册表，用于 structure set 引用已注册结构
 *     HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);
 *
 *     // 调用 register(...) 注册结构放置集合
 *     register(
 *             // 传入 structure_set bootstrap 上下文
 *             context,
 *             // 传入 structure 查询器，用于查找目标结构
 *             structures,
 *             // 传入 structure_set 注册 key
 *             WorldgenKeys.StructureSets.ABYSSAL_LAB,
 *             // 传入被放置的 structure 注册 key
 *             WorldgenKeys.Structures.ABYSSAL_LAB,
 *             // 设置结构选择权重
 *             1,
 *             // 设置结构平均间距
 *             48,
 *             // 设置结构最小分离距离
 *             24,
 *             // 设置随机 salt，避免不同结构共用分布噪声
 *             123456789
 *     );
 * }
 * }</pre>
 */
public final class StructureSetProvider {
    /**
     * 工具类不实例化
     */
    private StructureSetProvider() {
    }

    /**
     * 注册结构放置集合
     *
     * @param context Mojang 提供的注册表 bootstrap 上下文
     */
    public static void bootstrap(BootstapContext<StructureSet> context) {
        HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);
        register(context, structures, WorldgenKeys.StructureSets.ABYSSAL_LAB, WorldgenKeys.Structures.ABYSSAL_LAB, 1, 56, 28, 983379551);
        register(context, structures, WorldgenKeys.StructureSets.AEGIR_LAB, WorldgenKeys.Structures.AEGIR_LAB, 1, 64, 32, 1001069449);
        register(context, structures, WorldgenKeys.StructureSets.AIR_BASE, WorldgenKeys.Structures.AIR_BASE, 1, 58, 50, 254265812);
        register(context, structures, WorldgenKeys.StructureSets.AIRCRAFT, WorldgenKeys.Structures.AIRCRAFT, 1, 59, 55, 1199381489);
        register(context, structures, WorldgenKeys.StructureSets.ANCHOR_RUIN, WorldgenKeys.Structures.ANCHOR_RUIN, 1, 52, 45, 568563000);
        register(context, structures, WorldgenKeys.StructureSets.BELIEVER_HOME, WorldgenKeys.Structures.BELIEVER_HOME, 1, 64, 48, 2018597269);
        register(context, structures, WorldgenKeys.StructureSets.BISHOP_CAVE, WorldgenKeys.Structures.BISHOP_CAVE, 1, 48, 24, 1714840136);
        register(context, structures, WorldgenKeys.StructureSets.BRAND_PALACE, WorldgenKeys.Structures.BRAND_PALACE, 1, 36, 18, 513801667);
        register(context, structures, WorldgenKeys.StructureSets.BRAND_PORTAL, WorldgenKeys.Structures.BRAND_PORTAL, 1, 40, 16, 436443816);
        register(context, structures, WorldgenKeys.StructureSets.BRANDED_TOWN, WorldgenKeys.Structures.BRANDED_TOWN, 1, 55, 25, 1577871037);
        register(context, structures, WorldgenKeys.StructureSets.CHEST_MUSEUM, WorldgenKeys.Structures.CHEST_MUSEUM, 1, 56, 47, 195846613);
        register(context, structures, WorldgenKeys.StructureSets.CHITIN_FACTORY, WorldgenKeys.Structures.CHITIN_FACTORY, 1, 58, 52, 940612460);
        register(context, structures, WorldgenKeys.StructureSets.CHURCH, WorldgenKeys.Structures.CHURCH, 1, 42, 21, 1559158881);
        register(context, structures, WorldgenKeys.StructureSets.CLOISTER, WorldgenKeys.Structures.CLOISTER, 1, 68, 24, 732155636);
        register(context, structures, WorldgenKeys.StructureSets.CONTAINMENT_CAVE, WorldgenKeys.Structures.CONTAINMENT_CAVE, 1, 36, 12, 282080457);
        register(context, structures, WorldgenKeys.StructureSets.CORAL_CROWN, WorldgenKeys.Structures.CORAL_CROWN, 1, 36, 20, 173104000);
        register(context, structures, WorldgenKeys.StructureSets.DEEP_REEF, WorldgenKeys.Structures.DEEP_REEF, 1, 50, 47, 2042074128);
        register(context, structures, WorldgenKeys.StructureSets.FLOURISH, WorldgenKeys.Structures.FLOURISH, 1, 36, 18, 676384344);
        register(context, structures, WorldgenKeys.StructureSets.GOLDEN_AGE, WorldgenKeys.Structures.GOLDEN_AGE, 1, 75, 63, 357548055);
        register(context, structures, WorldgenKeys.StructureSets.HAUNTED_HOUSE, WorldgenKeys.Structures.HAUNTED_HOUSE, 1, 59, 58, 963002566);
        register(context, structures, WorldgenKeys.StructureSets.IBERIA_EYE, WorldgenKeys.Structures.IBERIA_EYE, 1, 56, 24, 13751763);
        register(context, structures, WorldgenKeys.StructureSets.INQUISITION_OUTPOST, WorldgenKeys.Structures.INQUISITION_OUTPOST, 1, 50, 39, 1901042701);
        register(context, structures, WorldgenKeys.StructureSets.ISHARMLACEMETRY, WorldgenKeys.Structures.ISHARMLACEMETRY, 1, 62, 35, 768718172);
        register(context, structures, WorldgenKeys.StructureSets.IZUMIK_ISLAND, WorldgenKeys.Structures.IZUMIK_ISLAND, 1, 64, 63, 197302117);
        register(context, structures, WorldgenKeys.StructureSets.LAMP, WorldgenKeys.Structures.LAMP, 1, 30, 20, 1378960);
        register(context, structures, WorldgenKeys.StructureSets.LIGHTHOUSE, WorldgenKeys.Structures.LIGHTHOUSE, 1, 39, 12, 1463095153);
        register(context, structures, WorldgenKeys.StructureSets.ODDFACTORY, WorldgenKeys.Structures.ODDFACTORY, 1, 58, 55, 1650518623);
        register(context, structures, WorldgenKeys.StructureSets.RHODES_SITE, WorldgenKeys.Structures.RHODES_SITE, 1, 47, 45, 1366962780);
        register(context, structures, WorldgenKeys.StructureSets.SADNESS_CHURCH, WorldgenKeys.Structures.SADNESS_CHURCH, 1, 68, 32, 1995257809);
        register(context, structures, WorldgenKeys.StructureSets.SAFE_HOUSE, WorldgenKeys.Structures.SAFE_HOUSE, 1, 56, 21, 1115414938);
        register(context, structures, WorldgenKeys.StructureSets.SINK_FIELD, WorldgenKeys.Structures.SINK_FIELD, 1, 58, 45, 668416290);
        register(context, structures, WorldgenKeys.StructureSets.SINK_GARDEN, WorldgenKeys.Structures.SINK_GARDEN, 1, 50, 32, 2090523120);
        register(context, structures, WorldgenKeys.StructureSets.SINK_HALL, WorldgenKeys.Structures.SINK_HALL, 1, 60, 50, 969067352);
        register(context, structures, WorldgenKeys.StructureSets.SINK_REMAINS, WorldgenKeys.Structures.SINK_REMAINS, 1, 60, 55, 594832990);
        register(context, structures, WorldgenKeys.StructureSets.SLIDER_STATU, WorldgenKeys.Structures.SLIDER_STATU, 1, 66, 32, 890563194);
        register(context, structures, WorldgenKeys.StructureSets.SUBMARINE, WorldgenKeys.Structures.SUBMARINE, 1, 55, 47, 1895955932);
        register(context, structures, WorldgenKeys.StructureSets.TIDE_STATION, WorldgenKeys.Structures.TIDE_STATION, 1, 55, 45, 1973698396);
        register(context, structures, WorldgenKeys.StructureSets.TRADER_CAVE, WorldgenKeys.Structures.TRADER_CAVE, 1, 10, 8, 184761123);
        register(context, structures, WorldgenKeys.StructureSets.TRADER_END, WorldgenKeys.Structures.TRADER_END, 1, 79, 64, 73580191);
        register(context, structures, WorldgenKeys.StructureSets.TRADER_OAK, WorldgenKeys.Structures.TRADER_OAK, 1, 40, 38, 231342564);
        register(context, structures, WorldgenKeys.StructureSets.TRADER_SAND, WorldgenKeys.Structures.TRADER_SAND, 1, 64, 48, 1261542217);
        register(context, structures, WorldgenKeys.StructureSets.TRADER_SKY, WorldgenKeys.Structures.TRADER_SKY, 1, 33, 19, 1011613333);
        register(context, structures, WorldgenKeys.StructureSets.TRADER_TNT, WorldgenKeys.Structures.TRADER_TNT, 1, 32, 28, 852904412);
        register(context, structures, WorldgenKeys.StructureSets.WATCHTOWER, WorldgenKeys.Structures.WATCHTOWER, 1, 46, 16, 1358541846);
    }

    /**
     * 注册结构放置集合
     *
     * @param context    注册表 bootstrap 上下文
     * @param structures structure 查询器
     * @param key        structure_set 注册 key
     * @param structureKey structure 注册 key
     * @param weight     结构权重
     * @param spacing    区块间距
     * @param separation 最小分离距离
     * @param salt       随机 salt
     */
    @SuppressWarnings("SameParameterValue")
    private static void register(BootstapContext<StructureSet> context, HolderGetter<Structure> structures, ResourceKey<StructureSet> key, ResourceKey<Structure> structureKey, int weight, int spacing, int separation, int salt) {
        context.register(key, new StructureSet(java.util.List.of(new StructureSet.StructureSelectionEntry(structures.getOrThrow(structureKey), weight)), new RandomSpreadStructurePlacement(spacing, separation, RandomSpreadType.LINEAR, salt)));
    }
}
