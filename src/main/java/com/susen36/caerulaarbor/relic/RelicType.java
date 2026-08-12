package com.susen36.caerulaarbor.relic;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * 遗物注册类型。每个遗物对应一个 RelicType 实例，由 CARelics 通过 DeferredRegister 注册。
 * 持有 minLevel / maxLevel / defaultLevel 三元组，由 PlayerVariable.getRelic/setRelic 统一 clamp。
 */
public sealed interface RelicType permits RelicType.BooleanRelicType, RelicType.NumericRelicType {

    int minLevel();

    int maxLevel();

    int defaultLevel();

    /**
     * 该遗物所属的稀有度等级（诅咒/普通/稀有/高级）。
     * 在 CARelics 注册 RelicType 时设置，默认 {@link RelicTier#NORMAL}（普通）。
     */
    default RelicTier tier() {
        return RelicTier.NORMAL;
    }

    /**
     * 是否为"获得型"遗物（绝大多数遗物：等级只有 0 未获得 / 1 已获得）。
     * true 对应旧体系 boolean 字段遗物；false 对应数值型（如 HAND_ENGRAVE、SURVIVOR_CONTRACT）。
     */
    default boolean isBooleanType() {
        return maxLevel() == 1 && minLevel() == 0 && defaultLevel() == 0;
    }

    static ResourceKey<Registry<RelicType>> createRegistryKey(String modId, String path) {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(modId, path));
    }

    /**
     * 标准布尔型遗物：min=0/max=1/default=0，clampedLevel > 0 即视为已获得。
     * 无参构造默认 {@link RelicTier#NORMAL}，带参构造可指定等级。
     */
    record BooleanRelicType(RelicTier tier) implements RelicType {
        public BooleanRelicType() {
            this(RelicTier.NORMAL);
        }

        public BooleanRelicType {
            if (tier == null) {
                tier = RelicTier.NORMAL;
            }
        }

        @Override
        public int minLevel() { return 0; }

        @Override
        public int maxLevel() { return 1; }

        @Override
        public int defaultLevel() { return 0; }
    }

    /**
     * 数值型遗物：由调用方指定 [min, max, default]，典型值如 HAND_ENGRAVE(-1..99 default=-1)、SURVIVOR_CONTRACT(-1..32 default=-1)。
     * 三参构造默认 {@link RelicTier#NORMAL}，四参构造可指定等级。
     */
    record NumericRelicType(int minLevel, int maxLevel, int defaultLevel, RelicTier tier) implements RelicType {
        public NumericRelicType(int minLevel, int maxLevel, int defaultLevel) {
            this(minLevel, maxLevel, defaultLevel, RelicTier.NORMAL);
        }

        public NumericRelicType {
            if (minLevel > maxLevel) {
                throw new IllegalArgumentException(
                    "NumericRelicType: minLevel " + minLevel + " > maxLevel " + maxLevel);
            }
            if (defaultLevel < minLevel || defaultLevel > maxLevel) {
                throw new IllegalArgumentException(
                    "NumericRelicType: defaultLevel " + defaultLevel + " not in [" + minLevel + "," + maxLevel + "]");
            }
            if (tier == null) {
                tier = RelicTier.NORMAL;
            }
        }
    }
}
