package com.susen36.caerulaarbor.capability;

import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

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

    public int get(Entity player) {
        return get(ModCapabilities.getPlayerVariables(player));
    }

    public int get(PlayerVariable variables) {
        return switch (this) {
            case FEATURED_CANNED_MEAT -> variables.relic_util_MEATCAN ? 1 : 0;
            case SEAWEED_SALAD -> variables.relic_util_SEAGRASS ? 1 : 0;
            case ORANGE_STORM -> variables.relic_util_ORANGE ? 1 : 0;
            case COFFEE_PLAINS_COFFEE_CANDY -> variables.relic_util_COFFEE ? 1 : 0;
            case PITTS_ASSORTED_FRUITS -> variables.relic_util_BERRIES ? 1 : 0;
            case CURSED_EMELIGHT -> variables.relic_cursed_EMELIGHT ? 1 : 0;
            case CURSED_GLOWBODY -> variables.relic_cursed_GLOWBODY ? 1 : 0;
            case CURSED_RESEARCH -> variables.relic_cursed_RESEARCH ? 1 : 0;
            case KING_CROWN -> variables.relic_king_CROWN ? 1 : 0;
            case KING_ARMOR -> variables.relic_king_ARMOR ? 1 : 0;
            case KING_SPEAR -> variables.relic_king_SPEAR ? 1 : 0;
            case KING_EXTENSION -> variables.relic_king_EXTENSION ? 1 : 0;
            case KING_CRYSTAL -> variables.relic_king_CRYSTAL ? 1 : 0;
            case HAND_THORNS -> variables.relic_hand_THORNS ? 1 : 0;
            case HAND_STRANGLE -> variables.relic_hand_STRANGLE ? 1 : 0;
            case HAND_FERTILITY -> variables.relic_hand_FERTILITY ? 1 : 0;
            case HAND_SPEED -> variables.relic_hand_SPEED ? 1 : 0;
            case HAND_OF_PULVERIZATION -> variables.relic_hand_BARREN ? 1 : 0;
            case HAND_SWIPE -> variables.relic_hand_SWIPE ? 1 : 0;
            case SARKAZ_KING_ARTIFACT -> variables.relic_archfi_ARTIFACT ? 1 : 0;
            case HAND_FIREWORK -> variables.relic_hand_FIREWORK ? 1 : 0;
            case SARKAZ_KING_FLAG -> variables.relic_archfi_FLAG ? 1 : 0;
            case HAND_ENGRAVE -> (int) variables.relic_hand_ENGRAVE;
            case SARKAZ_KING_BED -> variables.relic_archfi_BED ? 1 : 0;
            case SURVIVOR_CONTRACT -> (int) variables.relic_SURVIVOR;
            case TREATY -> variables.relic_TREATY ? 1 : 0;
            case SARKAZ_KING_RYLFATE -> variables.relic_archifi_RYLFATE ? 1 : 0;
            case UTIL_MUSICBOX -> variables.relic_util_MUSICBOX ? 1 : 0;
            case UTIL_IRIS -> variables.relic_util_IRIS ? 1 : 0;
            case WEIRD_FLUTE -> variables.relic_util_FLUTE ? 1 : 0;
            case PURE_GOLD_EXPEDITION -> variables.relic_util_VOYGOLD ? 1 : 0;
            case DURIN_OVERGROUND_ODYSSEY -> variables.relic_util_DURIN ? 1 : 0;
            case UTIL_TOPONYM -> variables.relic_util_TOPONYM ? 1 : 0;
            case HOT_WATER_KETTLE -> variables.relic_util_KETTLE ? 1 : 0;
            case LEGEND_CHITIN -> variables.relic_legend_CHITIN ? 1 : 0;
            case UTIL_ALLEY -> variables.relic_util_ALLEY ? 1 : 0;
            case VAMPIRES_BED -> variables.relic_util_BATBED ? 1 : 0;
            case PROOF_OF_LONGEVITY -> variables.relic_util_LONGEVITY ? 1 : 0;
            case UTIL_OMNIKEY -> variables.relic_util_OMNIKEY ? 1 : 0;
            case UTIL_SCORE -> variables.relic_util_score ? 1 : 0;
            case UTIL_RESCISSION -> variables.relic_util_RESCISSION ? 1 : 0;
            case UTIL_STARE -> variables.relic_util_STARE ? 1 : 0;
            case HAND_SWORD -> variables.relic_hand_SWORD ? 1 : 0;
            case UTIL_ALLAY -> variables.relic_util_ALLAY ? 1 : 0;
            case UTIL_RAINBOW -> variables.relic_util_RAINBOW ? 1 : 0;
            case DISO -> variables.relic_diso ? 1 : 0;
            case DISO_FLESH -> variables.relic_diso_FLESH ? 1 : 0;
            case DISO_BLOOD -> variables.relic_diso_BLOOD ? 1 : 0;
            case DISO_NEURO -> variables.relic_diso_NEURO ? 1 : 0;
            case AHND_SWIPE -> variables.relic_ahnd_SWIPE ? 1 : 0;
            case DISO_ATTENTION -> variables.relic_diso_ATTENTION ? 1 : 0;
            case HANSHAND_SPIKE -> variables.relic_hanshand_SPIKE ? 1 : 0;
            case ROYALFATE -> variables.relic_royalfate ? 1 : 0;
            case CURSED_HEART -> variables.relic_cursed_HEART ? 1 : 0;
            case HEMOST -> variables.relic_HEMOST ? 1 : 0;
            case YEARNING -> variables.relic_YEARNING ? 1 : 0;
        };
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
        switch (this) {
            case FEATURED_CANNED_MEAT -> variables.relic_util_MEATCAN = clampedLevel > 0;
            case SEAWEED_SALAD -> variables.relic_util_SEAGRASS = clampedLevel > 0;
            case ORANGE_STORM -> variables.relic_util_ORANGE = clampedLevel > 0;
            case COFFEE_PLAINS_COFFEE_CANDY -> variables.relic_util_COFFEE = clampedLevel > 0;
            case PITTS_ASSORTED_FRUITS -> variables.relic_util_BERRIES = clampedLevel > 0;
            case CURSED_EMELIGHT -> variables.relic_cursed_EMELIGHT = clampedLevel > 0;
            case CURSED_GLOWBODY -> variables.relic_cursed_GLOWBODY = clampedLevel > 0;
            case CURSED_RESEARCH -> variables.relic_cursed_RESEARCH = clampedLevel > 0;
            case KING_CROWN -> variables.relic_king_CROWN = clampedLevel > 0;
            case KING_ARMOR -> variables.relic_king_ARMOR = clampedLevel > 0;
            case KING_SPEAR -> variables.relic_king_SPEAR = clampedLevel > 0;
            case KING_EXTENSION -> variables.relic_king_EXTENSION = clampedLevel > 0;
            case KING_CRYSTAL -> variables.relic_king_CRYSTAL = clampedLevel > 0;
            case HAND_THORNS -> variables.relic_hand_THORNS = clampedLevel > 0;
            case HAND_STRANGLE -> variables.relic_hand_STRANGLE = clampedLevel > 0;
            case HAND_FERTILITY -> variables.relic_hand_FERTILITY = clampedLevel > 0;
            case HAND_SPEED -> variables.relic_hand_SPEED = clampedLevel > 0;
            case HAND_OF_PULVERIZATION -> variables.relic_hand_BARREN = clampedLevel > 0;
            case HAND_SWIPE -> variables.relic_hand_SWIPE = clampedLevel > 0;
            case SARKAZ_KING_ARTIFACT -> variables.relic_archfi_ARTIFACT = clampedLevel > 0;
            case HAND_FIREWORK -> variables.relic_hand_FIREWORK = clampedLevel > 0;
            case SARKAZ_KING_FLAG -> variables.relic_archfi_FLAG = clampedLevel > 0;
            case HAND_ENGRAVE -> variables.relic_hand_ENGRAVE = clampedLevel;
            case SARKAZ_KING_BED -> variables.relic_archfi_BED = clampedLevel > 0;
            case SURVIVOR_CONTRACT -> variables.relic_SURVIVOR = clampedLevel;
            case TREATY -> variables.relic_TREATY = clampedLevel > 0;
            case SARKAZ_KING_RYLFATE -> variables.relic_archifi_RYLFATE = clampedLevel > 0;
            case UTIL_MUSICBOX -> variables.relic_util_MUSICBOX = clampedLevel > 0;
            case UTIL_IRIS -> variables.relic_util_IRIS = clampedLevel > 0;
            case WEIRD_FLUTE -> variables.relic_util_FLUTE = clampedLevel > 0;
            case PURE_GOLD_EXPEDITION -> variables.relic_util_VOYGOLD = clampedLevel > 0;
            case DURIN_OVERGROUND_ODYSSEY -> variables.relic_util_DURIN = clampedLevel > 0;
            case UTIL_TOPONYM -> variables.relic_util_TOPONYM = clampedLevel > 0;
            case HOT_WATER_KETTLE -> variables.relic_util_KETTLE = clampedLevel > 0;
            case LEGEND_CHITIN -> variables.relic_legend_CHITIN = clampedLevel > 0;
            case UTIL_ALLEY -> variables.relic_util_ALLEY = clampedLevel > 0;
            case VAMPIRES_BED -> variables.relic_util_BATBED = clampedLevel > 0;
            case PROOF_OF_LONGEVITY -> variables.relic_util_LONGEVITY = clampedLevel > 0;
            case UTIL_OMNIKEY -> variables.relic_util_OMNIKEY = clampedLevel > 0;
            case UTIL_SCORE -> variables.relic_util_score = clampedLevel > 0;
            case UTIL_RESCISSION -> variables.relic_util_RESCISSION = clampedLevel > 0;
            case UTIL_STARE -> variables.relic_util_STARE = clampedLevel > 0;
            case HAND_SWORD -> variables.relic_hand_SWORD = clampedLevel > 0;
            case UTIL_ALLAY -> variables.relic_util_ALLAY = clampedLevel > 0;
            case UTIL_RAINBOW -> variables.relic_util_RAINBOW = clampedLevel > 0;
            case DISO -> variables.relic_diso = clampedLevel > 0;
            case DISO_FLESH -> variables.relic_diso_FLESH = clampedLevel > 0;
            case DISO_BLOOD -> variables.relic_diso_BLOOD = clampedLevel > 0;
            case DISO_NEURO -> variables.relic_diso_NEURO = clampedLevel > 0;
            case AHND_SWIPE -> variables.relic_ahnd_SWIPE = clampedLevel > 0;
            case DISO_ATTENTION -> variables.relic_diso_ATTENTION = clampedLevel > 0;
            case HANSHAND_SPIKE -> variables.relic_hanshand_SPIKE = clampedLevel > 0;
            case ROYALFATE -> variables.relic_royalfate = clampedLevel > 0;
            case CURSED_HEART -> variables.relic_cursed_HEART = clampedLevel > 0;
            case HEMOST -> variables.relic_HEMOST = clampedLevel > 0;
            case YEARNING -> variables.relic_YEARNING = clampedLevel > 0;
        }
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