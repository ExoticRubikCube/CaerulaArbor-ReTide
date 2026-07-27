package com.susen36.caerulaarbor.datagen.worldgen;

import com.susen36.caerulaarbor.CaerulaArborMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

/**
 * 保存 worldgen 注册 key，供 provider 直接引用
 */
public final class WorldgenKeys {
    /**
     * 工具类不实例化
     */
    private WorldgenKeys() {
    }

    /**
     * configured feature 注册 key
     */
    public static final class ConfiguredFeatures {
        public static final ResourceKey<ConfiguredFeature<?, ?>> BRANDED_LAND_TREE = modKey(Registries.CONFIGURED_FEATURE, "branded_land_tree");
        public static final ResourceKey<ConfiguredFeature<?, ?>> BURNT_TRAILS = modKey(Registries.CONFIGURED_FEATURE, "burnt_trails");
        public static final ResourceKey<ConfiguredFeature<?, ?>> IRIS_DISTRIBUTE = modKey(Registries.CONFIGURED_FEATURE, "iris_distribute");
        public static final ResourceKey<ConfiguredFeature<?, ?>> NETHERSEA_TREE = modKey(Registries.CONFIGURED_FEATURE, "nethersea_tree");
        public static final ResourceKey<ConfiguredFeature<?, ?>> REDSTONEIRIS_SEEDING = modKey(Registries.CONFIGURED_FEATURE, "redstoneiris_seeding");
        public static final ResourceKey<ConfiguredFeature<?, ?>> SLIDER_FLOWER = modKey(Registries.CONFIGURED_FEATURE, "slider_flower");
        public static final ResourceKey<ConfiguredFeature<?, ?>> TRAIL_MUSHROOM = modKey(Registries.CONFIGURED_FEATURE, "trail_mushroom");

        /**
         * 字段容器不实例化
         */
        private ConfiguredFeatures() {
        }
    }

    /**
     * placed feature 注册 key
     */
    public static final class PlacedFeatures {
        public static final ResourceKey<PlacedFeature> BRANDED_LAND_TREE = modKey(Registries.PLACED_FEATURE, "branded_land_tree");
        public static final ResourceKey<PlacedFeature> BURNT_TRAILS = modKey(Registries.PLACED_FEATURE, "burnt_trails");
        public static final ResourceKey<PlacedFeature> IRIS_DISTRIBUTE = modKey(Registries.PLACED_FEATURE, "iris_distribute");
        public static final ResourceKey<PlacedFeature> REDSTONEIRIS_SEEDING = modKey(Registries.PLACED_FEATURE, "redstoneiris_seeding");
        public static final ResourceKey<PlacedFeature> SLIDER_FLOWER = modKey(Registries.PLACED_FEATURE, "slider_flower");
        public static final ResourceKey<PlacedFeature> TRAIL_MUSHROOM = modKey(Registries.PLACED_FEATURE, "trail_mushroom");

        /**
         * 字段容器不实例化
         */
        private PlacedFeatures() {
        }
    }

    /**
     * template pool 注册 key
     */
    public static final class TemplatePools {
        public static final ResourceKey<StructureTemplatePool> ABYSSAL_LAB = modKey(Registries.TEMPLATE_POOL, "abyssal_lab");
        public static final ResourceKey<StructureTemplatePool> AEGIR_LAB = modKey(Registries.TEMPLATE_POOL, "aegir_lab");
        public static final ResourceKey<StructureTemplatePool> AIR_BASE = modKey(Registries.TEMPLATE_POOL, "air_base");
        public static final ResourceKey<StructureTemplatePool> AIRCRAFT = modKey(Registries.TEMPLATE_POOL, "aircraft");
        public static final ResourceKey<StructureTemplatePool> ANCHOR_RUIN = modKey(Registries.TEMPLATE_POOL, "anchor_ruin");
        public static final ResourceKey<StructureTemplatePool> BELIEVER_HOME = modKey(Registries.TEMPLATE_POOL, "believer_home");
        public static final ResourceKey<StructureTemplatePool> BISHOP_CAVE = modKey(Registries.TEMPLATE_POOL, "bishop_cave");
        public static final ResourceKey<StructureTemplatePool> BRAND_PALACE = modKey(Registries.TEMPLATE_POOL, "brand_palace");
        public static final ResourceKey<StructureTemplatePool> BRAND_PORTAL = modKey(Registries.TEMPLATE_POOL, "brand_portal");
        public static final ResourceKey<StructureTemplatePool> BRANDED_TOWN = modKey(Registries.TEMPLATE_POOL, "branded_town");
        public static final ResourceKey<StructureTemplatePool> CHEST_MUSEUM = modKey(Registries.TEMPLATE_POOL, "chest_museum");
        public static final ResourceKey<StructureTemplatePool> CHITIN_FACTORY = modKey(Registries.TEMPLATE_POOL, "chitin_factory");
        public static final ResourceKey<StructureTemplatePool> CHURCH = modKey(Registries.TEMPLATE_POOL, "church");
        public static final ResourceKey<StructureTemplatePool> CLOISTER = modKey(Registries.TEMPLATE_POOL, "cloister");
        public static final ResourceKey<StructureTemplatePool> CONTAINMENT_CAVE = modKey(Registries.TEMPLATE_POOL, "containment_cave");
        public static final ResourceKey<StructureTemplatePool> CORAL_CROWN = modKey(Registries.TEMPLATE_POOL, "coral_crown");
        public static final ResourceKey<StructureTemplatePool> DEEP_REEF = modKey(Registries.TEMPLATE_POOL, "deep_reef");
        public static final ResourceKey<StructureTemplatePool> FLOURISH = modKey(Registries.TEMPLATE_POOL, "flourish");
        public static final ResourceKey<StructureTemplatePool> FLOURISH_BAO_XIANG = modKey(Registries.TEMPLATE_POOL, "flourish_bao_xiang");
        public static final ResourceKey<StructureTemplatePool> FLOURISH_DING_FALLBACK = modKey(Registries.TEMPLATE_POOL, "flourish_ding_fallback");
        public static final ResourceKey<StructureTemplatePool> FLOURISH_FLOURISH_0 = modKey(Registries.TEMPLATE_POOL, "flourish_flourish_0");
        public static final ResourceKey<StructureTemplatePool> FLOURISH_QIAO = modKey(Registries.TEMPLATE_POOL, "flourish_qiao");
        public static final ResourceKey<StructureTemplatePool> FLOURISH_ZHONG = modKey(Registries.TEMPLATE_POOL, "flourish_zhong");
        public static final ResourceKey<StructureTemplatePool> GOLDEN_AGE = modKey(Registries.TEMPLATE_POOL, "golden_age");
        public static final ResourceKey<StructureTemplatePool> HAUNTED_HOUSE = modKey(Registries.TEMPLATE_POOL, "haunted_house");
        public static final ResourceKey<StructureTemplatePool> IBERIA_EYE = modKey(Registries.TEMPLATE_POOL, "iberia_eye");
        public static final ResourceKey<StructureTemplatePool> INQUISITION_OUTPOST = modKey(Registries.TEMPLATE_POOL, "inquisition_outpost");
        public static final ResourceKey<StructureTemplatePool> ISHARMLACEMETRY = modKey(Registries.TEMPLATE_POOL, "isharmlacemetry");
        public static final ResourceKey<StructureTemplatePool> IZUMIK_ISLAND = modKey(Registries.TEMPLATE_POOL, "izumik_island");
        public static final ResourceKey<StructureTemplatePool> LAMP = modKey(Registries.TEMPLATE_POOL, "lamp");
        public static final ResourceKey<StructureTemplatePool> LIGHTHOUSE = modKey(Registries.TEMPLATE_POOL, "lighthouse");
        public static final ResourceKey<StructureTemplatePool> ODDFACTORY = modKey(Registries.TEMPLATE_POOL, "oddfactory");
        public static final ResourceKey<StructureTemplatePool> RHODES_SITE = modKey(Registries.TEMPLATE_POOL, "rhodes_site");
        public static final ResourceKey<StructureTemplatePool> SADNESS_CHURCH = modKey(Registries.TEMPLATE_POOL, "sadness_church");
        public static final ResourceKey<StructureTemplatePool> SAFE_HOUSE = modKey(Registries.TEMPLATE_POOL, "safe_house");
        public static final ResourceKey<StructureTemplatePool> SINK_FIELD = modKey(Registries.TEMPLATE_POOL, "sink_field");
        public static final ResourceKey<StructureTemplatePool> SINK_GARDEN = modKey(Registries.TEMPLATE_POOL, "sink_garden");
        public static final ResourceKey<StructureTemplatePool> SINK_HALL = modKey(Registries.TEMPLATE_POOL, "sink_hall");
        public static final ResourceKey<StructureTemplatePool> SINK_REMAINS = modKey(Registries.TEMPLATE_POOL, "sink_remains");
        public static final ResourceKey<StructureTemplatePool> SLIDER_STATU = modKey(Registries.TEMPLATE_POOL, "slider_statu");
        public static final ResourceKey<StructureTemplatePool> SUBMARINE = modKey(Registries.TEMPLATE_POOL, "submarine");
        public static final ResourceKey<StructureTemplatePool> TIDE_STATION = modKey(Registries.TEMPLATE_POOL, "tide_station");
        public static final ResourceKey<StructureTemplatePool> TIDE_STATION_AAA = modKey(Registries.TEMPLATE_POOL, "tide_station_aaa");
        public static final ResourceKey<StructureTemplatePool> TRADER_CAVE = modKey(Registries.TEMPLATE_POOL, "trader_cave");
        public static final ResourceKey<StructureTemplatePool> TRADER_END = modKey(Registries.TEMPLATE_POOL, "trader_end");
        public static final ResourceKey<StructureTemplatePool> TRADER_OAK = modKey(Registries.TEMPLATE_POOL, "trader_oak");
        public static final ResourceKey<StructureTemplatePool> TRADER_SAND = modKey(Registries.TEMPLATE_POOL, "trader_sand");
        public static final ResourceKey<StructureTemplatePool> TRADER_SKY = modKey(Registries.TEMPLATE_POOL, "trader_sky");
        public static final ResourceKey<StructureTemplatePool> TRADER_TNT = modKey(Registries.TEMPLATE_POOL, "trader_tnt");
        public static final ResourceKey<StructureTemplatePool> WATCHTOWER = modKey(Registries.TEMPLATE_POOL, "watchtower");
        public static final ResourceKey<StructureTemplatePool> MINECRAFT_EMPTY = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.withDefaultNamespace("empty"));

        /**
         * 字段容器不实例化
         */
        private TemplatePools() {
        }
    }

    /**
     * structure 注册 key
     */
    public static final class Structures {
        public static final ResourceKey<Structure> ABYSSAL_LAB = modKey(Registries.STRUCTURE, "abyssal_lab");
        public static final ResourceKey<Structure> AEGIR_LAB = modKey(Registries.STRUCTURE, "aegir_lab");
        public static final ResourceKey<Structure> AIR_BASE = modKey(Registries.STRUCTURE, "air_base");
        public static final ResourceKey<Structure> AIRCRAFT = modKey(Registries.STRUCTURE, "aircraft");
        public static final ResourceKey<Structure> ANCHOR_RUIN = modKey(Registries.STRUCTURE, "anchor_ruin");
        public static final ResourceKey<Structure> BELIEVER_HOME = modKey(Registries.STRUCTURE, "believer_home");
        public static final ResourceKey<Structure> BISHOP_CAVE = modKey(Registries.STRUCTURE, "bishop_cave");
        public static final ResourceKey<Structure> BRAND_PALACE = modKey(Registries.STRUCTURE, "brand_palace");
        public static final ResourceKey<Structure> BRAND_PORTAL = modKey(Registries.STRUCTURE, "brand_portal");
        public static final ResourceKey<Structure> BRANDED_TOWN = modKey(Registries.STRUCTURE, "branded_town");
        public static final ResourceKey<Structure> CHEST_MUSEUM = modKey(Registries.STRUCTURE, "chest_museum");
        public static final ResourceKey<Structure> CHITIN_FACTORY = modKey(Registries.STRUCTURE, "chitin_factory");
        public static final ResourceKey<Structure> CHURCH = modKey(Registries.STRUCTURE, "church");
        public static final ResourceKey<Structure> CLOISTER = modKey(Registries.STRUCTURE, "cloister");
        public static final ResourceKey<Structure> CONTAINMENT_CAVE = modKey(Registries.STRUCTURE, "containment_cave");
        public static final ResourceKey<Structure> CORAL_CROWN = modKey(Registries.STRUCTURE, "coral_crown");
        public static final ResourceKey<Structure> DEEP_REEF = modKey(Registries.STRUCTURE, "deep_reef");
        public static final ResourceKey<Structure> FLOURISH = modKey(Registries.STRUCTURE, "flourish");
        public static final ResourceKey<Structure> GOLDEN_AGE = modKey(Registries.STRUCTURE, "golden_age");
        public static final ResourceKey<Structure> HAUNTED_HOUSE = modKey(Registries.STRUCTURE, "haunted_house");
        public static final ResourceKey<Structure> IBERIA_EYE = modKey(Registries.STRUCTURE, "iberia_eye");
        public static final ResourceKey<Structure> INQUISITION_OUTPOST = modKey(Registries.STRUCTURE, "inquisition_outpost");
        public static final ResourceKey<Structure> ISHARMLACEMETRY = modKey(Registries.STRUCTURE, "isharmlacemetry");
        public static final ResourceKey<Structure> IZUMIK_ISLAND = modKey(Registries.STRUCTURE, "izumik_island");
        public static final ResourceKey<Structure> LAMP = modKey(Registries.STRUCTURE, "lamp");
        public static final ResourceKey<Structure> LIGHTHOUSE = modKey(Registries.STRUCTURE, "lighthouse");
        public static final ResourceKey<Structure> ODDFACTORY = modKey(Registries.STRUCTURE, "oddfactory");
        public static final ResourceKey<Structure> RHODES_SITE = modKey(Registries.STRUCTURE, "rhodes_site");
        public static final ResourceKey<Structure> SADNESS_CHURCH = modKey(Registries.STRUCTURE, "sadness_church");
        public static final ResourceKey<Structure> SAFE_HOUSE = modKey(Registries.STRUCTURE, "safe_house");
        public static final ResourceKey<Structure> SINK_FIELD = modKey(Registries.STRUCTURE, "sink_field");
        public static final ResourceKey<Structure> SINK_GARDEN = modKey(Registries.STRUCTURE, "sink_garden");
        public static final ResourceKey<Structure> SINK_HALL = modKey(Registries.STRUCTURE, "sink_hall");
        public static final ResourceKey<Structure> SINK_REMAINS = modKey(Registries.STRUCTURE, "sink_remains");
        public static final ResourceKey<Structure> SLIDER_STATU = modKey(Registries.STRUCTURE, "slider_statu");
        public static final ResourceKey<Structure> SUBMARINE = modKey(Registries.STRUCTURE, "submarine");
        public static final ResourceKey<Structure> TIDE_STATION = modKey(Registries.STRUCTURE, "tide_station");
        public static final ResourceKey<Structure> TRADER_CAVE = modKey(Registries.STRUCTURE, "trader_cave");
        public static final ResourceKey<Structure> TRADER_END = modKey(Registries.STRUCTURE, "trader_end");
        public static final ResourceKey<Structure> TRADER_OAK = modKey(Registries.STRUCTURE, "trader_oak");
        public static final ResourceKey<Structure> TRADER_SAND = modKey(Registries.STRUCTURE, "trader_sand");
        public static final ResourceKey<Structure> TRADER_SKY = modKey(Registries.STRUCTURE, "trader_sky");
        public static final ResourceKey<Structure> TRADER_TNT = modKey(Registries.STRUCTURE, "trader_tnt");
        public static final ResourceKey<Structure> WATCHTOWER = modKey(Registries.STRUCTURE, "watchtower");

        /**
         * 字段容器不实例化
         */
        private Structures() {
        }
    }

    /**
     * structure set 注册 key
     */
    public static final class StructureSets {
        public static final ResourceKey<StructureSet> ABYSSAL_LAB = modKey(Registries.STRUCTURE_SET, "abyssal_lab");
        public static final ResourceKey<StructureSet> AEGIR_LAB = modKey(Registries.STRUCTURE_SET, "aegir_lab");
        public static final ResourceKey<StructureSet> AIR_BASE = modKey(Registries.STRUCTURE_SET, "air_base");
        public static final ResourceKey<StructureSet> AIRCRAFT = modKey(Registries.STRUCTURE_SET, "aircraft");
        public static final ResourceKey<StructureSet> ANCHOR_RUIN = modKey(Registries.STRUCTURE_SET, "anchor_ruin");
        public static final ResourceKey<StructureSet> BELIEVER_HOME = modKey(Registries.STRUCTURE_SET, "believer_home");
        public static final ResourceKey<StructureSet> BISHOP_CAVE = modKey(Registries.STRUCTURE_SET, "bishop_cave");
        public static final ResourceKey<StructureSet> BRAND_PALACE = modKey(Registries.STRUCTURE_SET, "brand_palace");
        public static final ResourceKey<StructureSet> BRAND_PORTAL = modKey(Registries.STRUCTURE_SET, "brand_portal");
        public static final ResourceKey<StructureSet> BRANDED_TOWN = modKey(Registries.STRUCTURE_SET, "branded_town");
        public static final ResourceKey<StructureSet> CHEST_MUSEUM = modKey(Registries.STRUCTURE_SET, "chest_museum");
        public static final ResourceKey<StructureSet> CHITIN_FACTORY = modKey(Registries.STRUCTURE_SET, "chitin_factory");
        public static final ResourceKey<StructureSet> CHURCH = modKey(Registries.STRUCTURE_SET, "church");
        public static final ResourceKey<StructureSet> CLOISTER = modKey(Registries.STRUCTURE_SET, "cloister");
        public static final ResourceKey<StructureSet> CONTAINMENT_CAVE = modKey(Registries.STRUCTURE_SET, "containment_cave");
        public static final ResourceKey<StructureSet> CORAL_CROWN = modKey(Registries.STRUCTURE_SET, "coral_crown");
        public static final ResourceKey<StructureSet> DEEP_REEF = modKey(Registries.STRUCTURE_SET, "deep_reef");
        public static final ResourceKey<StructureSet> FLOURISH = modKey(Registries.STRUCTURE_SET, "flourish");
        public static final ResourceKey<StructureSet> GOLDEN_AGE = modKey(Registries.STRUCTURE_SET, "golden_age");
        public static final ResourceKey<StructureSet> HAUNTED_HOUSE = modKey(Registries.STRUCTURE_SET, "haunted_house");
        public static final ResourceKey<StructureSet> IBERIA_EYE = modKey(Registries.STRUCTURE_SET, "iberia_eye");
        public static final ResourceKey<StructureSet> INQUISITION_OUTPOST = modKey(Registries.STRUCTURE_SET, "inquisition_outpost");
        public static final ResourceKey<StructureSet> ISHARMLACEMETRY = modKey(Registries.STRUCTURE_SET, "isharmlacemetry");
        public static final ResourceKey<StructureSet> IZUMIK_ISLAND = modKey(Registries.STRUCTURE_SET, "izumik_island");
        public static final ResourceKey<StructureSet> LAMP = modKey(Registries.STRUCTURE_SET, "lamp");
        public static final ResourceKey<StructureSet> LIGHTHOUSE = modKey(Registries.STRUCTURE_SET, "lighthouse");
        public static final ResourceKey<StructureSet> ODDFACTORY = modKey(Registries.STRUCTURE_SET, "oddfactory");
        public static final ResourceKey<StructureSet> RHODES_SITE = modKey(Registries.STRUCTURE_SET, "rhodes_site");
        public static final ResourceKey<StructureSet> SADNESS_CHURCH = modKey(Registries.STRUCTURE_SET, "sadness_church");
        public static final ResourceKey<StructureSet> SAFE_HOUSE = modKey(Registries.STRUCTURE_SET, "safe_house");
        public static final ResourceKey<StructureSet> SINK_FIELD = modKey(Registries.STRUCTURE_SET, "sink_field");
        public static final ResourceKey<StructureSet> SINK_GARDEN = modKey(Registries.STRUCTURE_SET, "sink_garden");
        public static final ResourceKey<StructureSet> SINK_HALL = modKey(Registries.STRUCTURE_SET, "sink_hall");
        public static final ResourceKey<StructureSet> SINK_REMAINS = modKey(Registries.STRUCTURE_SET, "sink_remains");
        public static final ResourceKey<StructureSet> SLIDER_STATU = modKey(Registries.STRUCTURE_SET, "slider_statu");
        public static final ResourceKey<StructureSet> SUBMARINE = modKey(Registries.STRUCTURE_SET, "submarine");
        public static final ResourceKey<StructureSet> TIDE_STATION = modKey(Registries.STRUCTURE_SET, "tide_station");
        public static final ResourceKey<StructureSet> TRADER_CAVE = modKey(Registries.STRUCTURE_SET, "trader_cave");
        public static final ResourceKey<StructureSet> TRADER_END = modKey(Registries.STRUCTURE_SET, "trader_end");
        public static final ResourceKey<StructureSet> TRADER_OAK = modKey(Registries.STRUCTURE_SET, "trader_oak");
        public static final ResourceKey<StructureSet> TRADER_SAND = modKey(Registries.STRUCTURE_SET, "trader_sand");
        public static final ResourceKey<StructureSet> TRADER_SKY = modKey(Registries.STRUCTURE_SET, "trader_sky");
        public static final ResourceKey<StructureSet> TRADER_TNT = modKey(Registries.STRUCTURE_SET, "trader_tnt");
        public static final ResourceKey<StructureSet> WATCHTOWER = modKey(Registries.STRUCTURE_SET, "watchtower");

        /**
         * 字段容器不实例化
         */
        private StructureSets() {
        }
    }

    /**
     * 创建 Caerula Arbor 命名空间下的注册 key
     *
     * @param registry 目标注册表 key
     * @param path     资源路径
     * @param <T>      注册值类型
     * @return 注册 key
     */
    private static <T> ResourceKey<T> modKey(ResourceKey<? extends Registry<T>> registry, String path) {
        return ResourceKey.create(registry, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, path));
    }
}