package com.susen36.caerulaarbor.init;

import com.susen36.babel.collectible.*;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.item.*;
import com.susen36.caerulaarbor.item.relic.BooleanCollectibleItem;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;

public class CACollectible {
    public static CollectibleBuilder REGISTRY;

    public static Holder<Item> LEGEND_CHITIN;
    public static Holder<Item> UTIL_ALLAY;
    public static Holder<Item> DISO;
    public static Holder<Item> DISO_FLESH;
    public static Holder<Item> DISO_BLOOD;
    public static Holder<Item> DISO_NEURO;
    public static Holder<Item> DISO_ATTENTION;
    public static Holder<Item> AHND_SWIPE;
    public static Holder<Item> HANSHAND_SPIKE;
    public static Holder<Item> SARKAZ_KING_RYLFATE;
    public static Holder<Item> VAMPIRES_BED;
    public static Holder<Item> UTIL_SCORE;
    public static Holder<Item> UTIL_RESCISSION;
    public static Holder<Item> UTIL_OMNIKEY;
    public static Holder<Item> UTIL_STARE;
    public static Holder<Item> DURIN_OVERGROUND_ODYSSEY;
    public static Holder<Item> CURSED_HEART;
    public static Holder<Item> HAND_FERTILITY;
    public static Holder<Item> HAND_THORNS;
    public static Holder<Item> KING_ARMOR;
    public static Holder<Item> KING_SPEAR;
    public static Holder<Item> KING_EXTENSION;
    public static Holder<Item> KING_CROWN;
    public static Holder<Item> KING_CRYSTAL;
    public static Holder<Item> HAND_STRANGLE;
    public static Holder<Item> HAND_SPEED;
    public static Holder<Item> HAND_FIREWORK;
    public static Holder<Item> HAND_OF_PULVERIZATION;
    public static Holder<Item> HAND_SWIPE;
    public static Holder<Item> SARKAZ_KING_FLAG;
    public static Holder<Item> SARKAZ_KING_BED;
    public static Holder<Item> SARKAZ_KING_ARTIFACT;
    public static Holder<Item> TREATY;
    public static Holder<Item> HEMOST;
    public static Holder<Item> YEARNING;
    public static Holder<Item> CURSED_EMELIGHT;
    public static Holder<Item> CURSED_GLOWBODY;
    public static Holder<Item> CURSED_RESEARCH;
    public static Holder<Item> RELIC_CROWN;
    public static Holder<Item> CAERULA_HEART;
    public static Holder<Item> FEATURED_CANNED_MEAT;
    public static Holder<Item> HAND_OF_BARREN;
    public static Holder<Item> BAT_BED;
    public static Holder<Item> PIGLIN_DIARY;
    public static Holder<Item> CHITIN_KNIFE;
    public static Holder<Item> COFFEE_CANDY;
    public static Holder<Item> RAINBOW_CANDY;
    public static Holder<Item> HAND_OF_ENGRAVE;
    public static Holder<Item> SURVIVOR_CONTRACT;
    public static Holder<Item> ROYAL_FATE;
    public static Holder<Item> BOWL_SEAGRASS;
    public static Holder<Item> GOLDEN_STORM;
    public static Holder<Item> CANNED_CHERRY;
    public static Holder<Item> SOLO_MUSIC_BOX;
    public static Holder<Item> REDSTONE_IRIS_FLOWER;
    public static Holder<Item> ODD_FLUTE;
    public static Holder<Item> VOYAGE_OF_GOLD;
    public static Holder<Item> TOPONYM_TEXTOLOGY;
    public static Holder<Item> KETTLE;
    public static Holder<Item> PROOF_OF_LONGEVITY;
    public static Holder<Item> TULIP_MEDCINE;
    public static Holder<Item> NURTURE_GENE_SET;
    public static Holder<Item> GOLDEN_CHALISE;
    public static Holder<Item> OIL_AND_CREAM;
    public static Holder<Item> HAND_SWORD;

    public static void register(IEventBus bus) {
        REGISTRY = CollectibleBuilder.create(CaerulaArbor.MODID, bus);
        LEGEND_CHITIN = REGISTRY.registerCollectible("legend_chitin", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
        UTIL_ALLAY = REGISTRY.registerCollectible("util_allay", AlleySculptureItem::new);
        DISO = REGISTRY.registerCollectible("diso", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
        DISO_FLESH = REGISTRY.registerCollectible("diso_flesh", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
        DISO_BLOOD = REGISTRY.registerCollectible("diso_blood", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
        DISO_NEURO = REGISTRY.registerCollectible("diso_neuro", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
        DISO_ATTENTION = REGISTRY.registerCollectible("diso_attention", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
        AHND_SWIPE = REGISTRY.registerCollectible("ahnd_swipe", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
        HANSHAND_SPIKE = REGISTRY.registerCollectible("hanshand_spike", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
        SARKAZ_KING_RYLFATE = REGISTRY.registerCollectible("sarkaz_king_rylfate", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
        VAMPIRES_BED = REGISTRY.registerCollectible("vampires_bed", ArchfiendsBedItem::new);
        UTIL_SCORE = REGISTRY.registerCollectible("util_score", ScoreItem::new);
        UTIL_RESCISSION = REGISTRY.registerCollectible("util_rescission", RescissionItem::new);
        UTIL_OMNIKEY = REGISTRY.registerCollectible("util_omnikey", OmniKeyItem::new);
        UTIL_STARE = REGISTRY.registerCollectible("util_stare", GuardianStareItem::new);
        DURIN_OVERGROUND_ODYSSEY = REGISTRY.registerCollectible("durin_overground_odyssey", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
        CURSED_HEART = REGISTRY.registerCollectible("cursed_heart", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
        HAND_FERTILITY = REGISTRY.registerCollectible("hand_fertility", HandOfFertiliyItem::new);
        HAND_THORNS = REGISTRY.registerCollectible("hand_thorns", HandOfThornsItem::new);
        KING_ARMOR = REGISTRY.registerCollectible("king_armor", KingsArmourItem::new);
        KING_SPEAR = REGISTRY.registerCollectible("king_spear", KingsSpearItem::new);
        KING_EXTENSION = REGISTRY.registerCollectible("king_extension", KingsExtensionItem::new);
        KING_CROWN = REGISTRY.registerCollectible("king_crown", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
        KING_CRYSTAL = REGISTRY.registerCollectible("king_crystal", KingsCrystalItem::new);
        HAND_STRANGLE = REGISTRY.registerCollectible("hand_strangle", HandOfStrangleItem::new);
        HAND_SPEED = REGISTRY.registerCollectible("hand_speed", HandOfSpeedItem::new);
        HAND_FIREWORK = REGISTRY.registerCollectible("hand_firework", HandOfFireworkItem::new);
        HAND_OF_PULVERIZATION = REGISTRY.registerCollectible("hand_of_pulverization", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
        HAND_SWIPE = REGISTRY.registerCollectible("hand_swipe", HandOfSwipeItem::new);
        SARKAZ_KING_FLAG = REGISTRY.registerCollectible("sarkaz_king_flag", ArchfiendsFlagItem::new);
        SARKAZ_KING_BED = REGISTRY.registerCollectible("sarkaz_king_bed", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
        SARKAZ_KING_ARTIFACT = REGISTRY.registerCollectible("sarkaz_king_artifact", ArchfiendsArtifactItem::new);
        TREATY = REGISTRY.registerCollectible("treaty", CrimsonTreatyItem::new);
        HEMOST = REGISTRY.registerCollectible("hemost", SmellyHemostaticItem::new);
        YEARNING = REGISTRY.registerCollectible("yearning", UnripeYearningItem::new);
        CURSED_EMELIGHT = REGISTRY.registerCollectible("cursed_emelight", CollectibleCurseEMELIGHTItem::new);
        CURSED_GLOWBODY = REGISTRY.registerCollectible("cursed_glowbody", CollectibleCursedGLOWBODYItem::new);
        CURSED_RESEARCH = REGISTRY.registerCollectible("cursed_research", CollectibleCursedRESEARCHItem::new);
        RELIC_CROWN = REGISTRY.registerCollectible("relic_crown", CollectibleCROWNItem::new);
        CAERULA_HEART = REGISTRY.registerCollectible("caerula_heart", CaerulaHeartItem::new);
        FEATURED_CANNED_MEAT = REGISTRY.registerCollectible("featured_canned_meat", MeatCanItem::new);
        HAND_OF_BARREN = REGISTRY.registerCollectible("hand_of_barren", HandOfBarrenItem::new);
        BAT_BED = REGISTRY.registerCollectible("bat_bed", BatBedItem::new);
        PIGLIN_DIARY = REGISTRY.registerCollectible("piglin_diary", PiglinDiaryItem::new);
        CHITIN_KNIFE = REGISTRY.registerCollectible("chitin_knife", ChitinKnifeItem::new);
        COFFEE_CANDY = REGISTRY.registerCollectible("coffee_candy", CoffeeCandyItem::new);
        RAINBOW_CANDY = REGISTRY.registerCollectible("rainbow_candy", RainbowCandyItem::new);
        HAND_OF_ENGRAVE = REGISTRY.registerCollectible("hand_of_engrave", false, 25, false,
                CollectibleTiers.ADVANCED, new CollectibleItem.Levels(0, 99, 0),
                CollectibleActivation.builder().sound(SoundEvents.PLAYER_LEVELUP, 2F, 1F).particle(ParticleTypes.CLOUD, 72).showOverlay(true).build(),
                (stack, level, player, self) -> player.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(stack.getItem(), 0));
        SURVIVOR_CONTRACT = REGISTRY.registerCollectible("survivor_contract",
                false, 25, false,
                CollectibleTiers.ADVANCED, new CollectibleItem.Levels(0, 32, 0),
                CollectibleActivation.builder().sound(SoundEvents.BEACON_ACTIVATE, 3.2F, 1F).particle(ParticleTypes.GLOW, 72).showOverlay(true).build(),
                (stack, level, player, self) -> player.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(stack.getItem(), 0));
        ROYAL_FATE = REGISTRY.registerCollectible("royal_fate", RoyalFateItem::new);
        BOWL_SEAGRASS = REGISTRY.registerCollectible("bowl_seagrass", BowlSeagrassItem::new);
        GOLDEN_STORM = REGISTRY.registerCollectible("golden_storm", GoldenStormItem::new);
        CANNED_CHERRY = REGISTRY.registerCollectible("canned_cherry", CannedCherryItem::new);
        SOLO_MUSIC_BOX = REGISTRY.registerCollectible("solo_music_box", SoloMusicBoxItem::new);
        REDSTONE_IRIS_FLOWER = REGISTRY.registerCollectible("redstone_iris_flower", RedstoneIrisFlowerItem::new);
        ODD_FLUTE = REGISTRY.registerCollectible("odd_flute", OddFluteItem::new);
        VOYAGE_OF_GOLD = REGISTRY.registerCollectible("voyage_of_gold", VoyageOfGoldItem::new);
        TOPONYM_TEXTOLOGY = REGISTRY.registerCollectible("toponym_textology", ToponymTextologyItem::new);
        KETTLE = REGISTRY.registerCollectible("kettle", KettleItem::new);
        PROOF_OF_LONGEVITY = REGISTRY.registerCollectible("proof_of_longevity", ProofOfLongevityItem::new);
        TULIP_MEDCINE = REGISTRY.registerCollectible("tulip_medcine", TulipMedcineItem::new);
        NURTURE_GENE_SET = REGISTRY.registerCollectible("nurture_gene_set", NurtureGeneSetItem::new);
        GOLDEN_CHALISE = REGISTRY.registerCollectible("golden_chalise", GoldenChaliseItem::new);
        OIL_AND_CREAM = REGISTRY.registerCollectible("oil_and_cream", OilAndCreamItem::new);
        HAND_SWORD = REGISTRY.registerCollectible("hand_sword", () -> new BooleanCollectibleItem(Rarity.EPIC, CollectibleActivation.builder().sound(SoundEvents.PLAYER_LEVELUP, 2F, 1F).particle(ParticleTypes.CLOUD, 72).showOverlay(true).build()));
    }
}