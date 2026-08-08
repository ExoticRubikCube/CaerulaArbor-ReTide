package com.susen36.caerulaarbor.capability;

import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CARelics;
import com.susen36.caerulaarbor.relic.RelicType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.util.EnumMap;
import java.util.function.Consumer;

/**
 * Relic 枚举作为「编译期常量门面层」保留（共 56 个遗物）。
 * <p>
 * 设计要点：
 * <ol>
 *   <li>minLevel / maxLevel / defaultLevel 仍保留在枚举构造器内（供 Relic.modify 级别的 clamp 前置判定使用；PlayerVariable.setRelic 会做最终二次 clamp）</li>
 *   <li>底层存储已全部迁入 PlayerVariable.relicLevels（Map&lt;ResourceKey&lt;RelicType&gt;, Integer&gt;），通过 BY_ENUM 静态表把 56 个枚举常量一一绑定到 CARelics 注册表 Key</li>
 *   <li>对外 API（get / gained / reset / set / gain / modify 等方法签名）完全不变，59 个调用方无需改动</li>
 * </ol>
 */
public enum Relic {
    FEATURED_CANNED_MEAT,
    SEAWEED_SALAD,
    ORANGE_STORM,
    COFFEE_PLAINS_COFFEE_CANDY,
    PITTS_ASSORTED_FRUITS,

    CURSED_EMELIGHT,
    CURSED_GLOWBODY,
    CURSED_RESEARCH,
    KING_CROWN,
    KING_ARMOR,
    KING_SPEAR,
    KING_EXTENSION,
    KING_CRYSTAL,
    HAND_THORNS,
    HAND_STRANGLE,
    HAND_FERTILITY,
    HAND_SPEED,
    HAND_OF_PULVERIZATION,
    HAND_SWIPE,
    SARKAZ_KING_ARTIFACT,
    HAND_FIREWORK,
    SARKAZ_KING_FLAG,
    HAND_ENGRAVE(-1, 99, -1),
    SARKAZ_KING_BED,
    SURVIVOR_CONTRACT(-1, 32, -1),
    TREATY,
    SARKAZ_KING_RYLFATE,
    UTIL_MUSICBOX,
    UTIL_IRIS,
    WEIRD_FLUTE,
    PURE_GOLD_EXPEDITION,
    DURIN_OVERGROUND_ODYSSEY,
    UTIL_TOPONYM,
    HOT_WATER_KETTLE,
    LEGEND_CHITIN,
    UTIL_ALLEY,
    VAMPIRES_BED,
    PROOF_OF_LONGEVITY,
    UTIL_OMNIKEY,
    UTIL_SCORE,
    UTIL_RESCISSION,
    UTIL_STARE,
    HAND_SWORD,
    UTIL_ALLAY,
    UTIL_RAINBOW,
    DISO,
    DISO_FLESH,
    DISO_BLOOD,
    DISO_NEURO,
    AHND_SWIPE,
    DISO_ATTENTION,
    HANSHAND_SPIKE,
    ROYALFATE,
    CURSED_HEART,
    HEMOST,
    YEARNING;

    public final int minLevel;
    public final int maxLevel;
    public final int defaultLevel;

    Relic() {
        this(0, 1, 0);
    }

    Relic(int minLevel, int maxLevel, int defaultLevel) {
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.defaultLevel = defaultLevel;
    }

    /**
     * 56 个枚举常量 → 注册制 ResourceKey 的绑定表。
     * 放在独立 static{} 块里避免枚举构造器阶段跨类引用静态字段的顺序风险。
     */
    private static final EnumMap<Relic, ResourceKey<RelicType>> BY_ENUM = new EnumMap<>(Relic.class);

    static {
        BY_ENUM.put(FEATURED_CANNED_MEAT, CARelics.FEATURED_CANNED_MEAT.getKey());
        BY_ENUM.put(SEAWEED_SALAD, CARelics.SEAWEED_SALAD.getKey());
        BY_ENUM.put(ORANGE_STORM, CARelics.ORANGE_STORM.getKey());
        BY_ENUM.put(COFFEE_PLAINS_COFFEE_CANDY, CARelics.COFFEE_PLAINS_COFFEE_CANDY.getKey());
        BY_ENUM.put(PITTS_ASSORTED_FRUITS, CARelics.PITTS_ASSORTED_FRUITS.getKey());
        BY_ENUM.put(CURSED_EMELIGHT, CARelics.CURSED_EMELIGHT.getKey());
        BY_ENUM.put(CURSED_GLOWBODY, CARelics.CURSED_GLOWBODY.getKey());
        BY_ENUM.put(CURSED_RESEARCH, CARelics.CURSED_RESEARCH.getKey());
        BY_ENUM.put(CURSED_HEART, CARelics.CURSED_HEART.getKey());
        BY_ENUM.put(KING_CROWN, CARelics.KING_CROWN.getKey());
        BY_ENUM.put(KING_ARMOR, CARelics.KING_ARMOR.getKey());
        BY_ENUM.put(KING_SPEAR, CARelics.KING_SPEAR.getKey());
        BY_ENUM.put(KING_EXTENSION, CARelics.KING_EXTENSION.getKey());
        BY_ENUM.put(KING_CRYSTAL, CARelics.KING_CRYSTAL.getKey());
        BY_ENUM.put(ROYALFATE, CARelics.ROYALFATE.getKey());
        BY_ENUM.put(HAND_THORNS, CARelics.HAND_THORNS.getKey());
        BY_ENUM.put(HAND_STRANGLE, CARelics.HAND_STRANGLE.getKey());
        BY_ENUM.put(HAND_FERTILITY, CARelics.HAND_FERTILITY.getKey());
        BY_ENUM.put(HAND_SPEED, CARelics.HAND_SPEED.getKey());
        BY_ENUM.put(HAND_OF_PULVERIZATION, CARelics.HAND_OF_PULVERIZATION.getKey());
        BY_ENUM.put(HAND_SWIPE, CARelics.HAND_SWIPE.getKey());
        BY_ENUM.put(SARKAZ_KING_ARTIFACT, CARelics.SARKAZ_KING_ARTIFACT.getKey());
        BY_ENUM.put(HAND_FIREWORK, CARelics.HAND_FIREWORK.getKey());
        BY_ENUM.put(SARKAZ_KING_FLAG, CARelics.SARKAZ_KING_FLAG.getKey());
        BY_ENUM.put(HAND_ENGRAVE, CARelics.HAND_ENGRAVE.getKey());
        BY_ENUM.put(SARKAZ_KING_BED, CARelics.SARKAZ_KING_BED.getKey());
        BY_ENUM.put(SURVIVOR_CONTRACT, CARelics.SURVIVOR_CONTRACT.getKey());
        BY_ENUM.put(TREATY, CARelics.TREATY.getKey());
        BY_ENUM.put(SARKAZ_KING_RYLFATE, CARelics.SARKAZ_KING_RYLFATE.getKey());
        BY_ENUM.put(UTIL_MUSICBOX, CARelics.UTIL_MUSICBOX.getKey());
        BY_ENUM.put(UTIL_IRIS, CARelics.UTIL_IRIS.getKey());
        BY_ENUM.put(WEIRD_FLUTE, CARelics.WEIRD_FLUTE.getKey());
        BY_ENUM.put(PURE_GOLD_EXPEDITION, CARelics.PURE_GOLD_EXPEDITION.getKey());
        BY_ENUM.put(DURIN_OVERGROUND_ODYSSEY, CARelics.DURIN_OVERGROUND_ODYSSEY.getKey());
        BY_ENUM.put(UTIL_TOPONYM, CARelics.UTIL_TOPONYM.getKey());
        BY_ENUM.put(HOT_WATER_KETTLE, CARelics.HOT_WATER_KETTLE.getKey());
        BY_ENUM.put(LEGEND_CHITIN, CARelics.LEGEND_CHITIN.getKey());
        BY_ENUM.put(UTIL_ALLEY, CARelics.UTIL_ALLEY.getKey());
        BY_ENUM.put(VAMPIRES_BED, CARelics.VAMPIRES_BED.getKey());
        BY_ENUM.put(PROOF_OF_LONGEVITY, CARelics.PROOF_OF_LONGEVITY.getKey());
        BY_ENUM.put(UTIL_OMNIKEY, CARelics.UTIL_OMNIKEY.getKey());
        BY_ENUM.put(UTIL_SCORE, CARelics.UTIL_SCORE.getKey());
        BY_ENUM.put(UTIL_RESCISSION, CARelics.UTIL_RESCISSION.getKey());
        BY_ENUM.put(UTIL_STARE, CARelics.UTIL_STARE.getKey());
        BY_ENUM.put(HAND_SWORD, CARelics.HAND_SWORD.getKey());
        BY_ENUM.put(UTIL_ALLAY, CARelics.UTIL_ALLAY.getKey());
        BY_ENUM.put(UTIL_RAINBOW, CARelics.UTIL_RAINBOW.getKey());
        BY_ENUM.put(DISO, CARelics.DISO.getKey());
        BY_ENUM.put(DISO_FLESH, CARelics.DISO_FLESH.getKey());
        BY_ENUM.put(DISO_BLOOD, CARelics.DISO_BLOOD.getKey());
        BY_ENUM.put(DISO_NEURO, CARelics.DISO_NEURO.getKey());
        BY_ENUM.put(DISO_ATTENTION, CARelics.DISO_ATTENTION.getKey());
        BY_ENUM.put(AHND_SWIPE, CARelics.AHND_SWIPE.getKey());
        BY_ENUM.put(HANSHAND_SPIKE, CARelics.HANSHAND_SPIKE.getKey());
        BY_ENUM.put(HEMOST, CARelics.HEMOST.getKey());
        BY_ENUM.put(YEARNING, CARelics.YEARNING.getKey());
    }

    public ResourceKey<RelicType> getRegistryKey() {
        return BY_ENUM.get(this);
    }

    public int get(Entity player) {
        return get(ModCapabilities.getPlayerVariables(player));
    }

    public int get(PlayerVariable variables) {
        return variables.getRelic(getRegistryKey());
    }

    public boolean gained(Entity player) {
        return gained(ModCapabilities.getPlayerVariables(player));
    }

    public boolean gained(PlayerVariable variables) {
        return get(variables) != defaultLevel;
    }

    public void reset(Entity player) {
        reset(ModCapabilities.getPlayerVariables(player));
    }

    public void reset(PlayerVariable variables) {
        set(variables, defaultLevel);
    }

    public void set(Entity player, int level) {
        set(ModCapabilities.getPlayerVariables(player), level);
    }

    public void set(PlayerVariable variables, int level) {
        int clampedLevel = Mth.clamp(level, minLevel, maxLevel);
        variables.setRelic(getRegistryKey(), clampedLevel);
    }

    public void gain(Entity player) {
        set(player, 1);
    }

    public static void modify(Entity player, Consumer<PlayerVariable> operation) {
        PlayerVariable cap = ModCapabilities.getPlayerVariables(player);
        modify(cap, player, operation);
    }

    public static void modify(PlayerVariable cap, Entity player, Consumer<PlayerVariable> operation) {
        operation.accept(cap);
        cap.syncPlayerVariables(player);
    }

    public void modify(Entity player, int value) {
        PlayerVariable cap = ModCapabilities.getPlayerVariables(player);
        modify(cap, player, value);
    }

    public void modify(PlayerVariable cap, Entity player, int value) {
        set(cap, value);
        cap.syncPlayerVariables(player);
    }

    public void gainAndSync(Entity player) {
        PlayerVariable cap = ModCapabilities.getPlayerVariables(player);
        gainAndSync(cap, player);
    }

    public void gainAndSync(PlayerVariable cap, Entity player) {
        set(cap, 1);
        cap.syncPlayerVariables(player);
    }
}
