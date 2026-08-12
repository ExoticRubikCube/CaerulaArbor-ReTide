package com.susen36.caerulaarbor.relic;

import com.susen36.caerulaarbor.api.event.RelicEvent;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAConfigs;
import com.susen36.caerulaarbor.init.CARelics;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;

/**
 * 遗物注册类型。每个遗物对应一个 RelicType 实例，由 CARelics 通过 DeferredRegister 注册。
 * 持有 minLevel / maxLevel / defaultLevel 三元组，由 PlayerVariable.getRelic/setRelic 统一 clamp。
 * <p>
 * 该接口同时承载原 Relic 枚举的所有对外操作 API（get / gained / reset / set / gain / remove / modify / gainAndSync），
 * 通过 {@link #registryKey()} 从注册表反查自身 {@link ResourceKey}，调用侧以 {@code CARelics.XXX.get().method(...)} 使用。
 */
public sealed interface RelicType permits RelicType.BooleanRelicType, RelicType.NumericRelicType {

    int minLevel();

    int maxLevel();

    int defaultLevel();

    /**
     * 从注册表反查本遗物对应的 ResourceKey。
     * 所有 RelicType 均由 CARelics 通过 DeferredRegister 创建并注册，运行时必然存在。
     */
    default ResourceKey<RelicType> registryKey() {
        return CARelics.RELICS_REGISTRY.getResourceKey(this).orElseThrow();
    }

    default int get(Entity player) {
        return get(ModCapabilities.getPlayerVariables(player));
    }

    /**
     * 读取遗物能力值。
     * <p>
     * 若该遗物是「高级遗物」且 {@link CAConfigs#RELIC_BAN} 开关已开启，
     * 则无论 capability 中实际存储了什么值，都会直接返回 {@link #defaultLevel()}（未获得态）。
     * 这样在 API 读取层就直接让能力"看起来从未获得"，不需要对能力值或物品做任何轮询清理。
     */
    default int get(PlayerVariable variables) {
        int stored = variables.getRelic(registryKey());
        if (tier() == RelicTier.ADVANCED && CAConfigs.RELIC_BAN.get()) {
            return defaultLevel();
        }
        return stored;
    }

    default boolean gained(Entity player) {
        return gained(ModCapabilities.getPlayerVariables(player));
    }

    default boolean gained(PlayerVariable variables) {
        return get(variables) != defaultLevel();
    }

    default void reset(Entity player) {
        reset(ModCapabilities.getPlayerVariables(player));
    }

    default void reset(PlayerVariable variables) {
        set(variables, defaultLevel());
    }

    default void set(Entity player, int level) {
        if (level == defaultLevel()) {
            remove(player);
        } else {
            set(ModCapabilities.getPlayerVariables(player), level);
            NeoForge.EVENT_BUS.post(new RelicEvent.Update(player, this));
        }
    }

    default void set(PlayerVariable variables, int level) {
        int clampedLevel = Mth.clamp(level, minLevel(), maxLevel());
        variables.setRelic(registryKey(), clampedLevel);
    }

    default void gain(Entity player) {
        set(player, 1);
        NeoForge.EVENT_BUS.post(new RelicEvent.Gain(player, this));
    }

    default void remove(Entity player) {
        remove(ModCapabilities.getPlayerVariables(player));
        NeoForge.EVENT_BUS.post(new RelicEvent.Remove(player, this));
    }

    default void remove(PlayerVariable variables) {
        variables.setRelic(registryKey(), defaultLevel());
    }

    default void modify(Entity player, int value) {
        PlayerVariable cap = ModCapabilities.getPlayerVariables(player);
        modify(cap, player, value);
    }

    default void modify(PlayerVariable cap, Entity player, int value) {
        set(cap, value);
        cap.syncPlayerVariables(player);
    }

    default void gainAndSync(Entity player) {
        PlayerVariable cap = ModCapabilities.getPlayerVariables(player);
        gainAndSync(cap, player);
    }

    default void gainAndSync(PlayerVariable cap, Entity player) {
        set(cap, 1);
        cap.syncPlayerVariables(player);
    }

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
