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

public class CACollectible {
    public static final CollectibleBuilder REGISTRY = CollectibleBuilder.create(CaerulaArbor.MODID);

    public static final Holder<Item> LEGEND_CHITIN = REGISTRY.registerCollectible("legend_chitin", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final Holder<Item> UTIL_ALLAY = REGISTRY.registerCollectible("util_allay", AlleySculptureItem::new);
    public static final Holder<Item> DISO = REGISTRY.registerCollectible("diso", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final Holder<Item> DISO_FLESH = REGISTRY.registerCollectible("diso_flesh", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final Holder<Item> DISO_BLOOD = REGISTRY.registerCollectible("diso_blood", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final Holder<Item> DISO_NEURO = REGISTRY.registerCollectible("diso_neuro", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final Holder<Item> DISO_ATTENTION = REGISTRY.registerCollectible("diso_attention", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final Holder<Item> AHND_SWIPE = REGISTRY.registerCollectible("ahnd_swipe", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final Holder<Item> HANSHAND_SPIKE = REGISTRY.registerCollectible("hanshand_spike", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final Holder<Item> SARKAZ_KING_RYLFATE = REGISTRY.registerCollectible("sarkaz_king_rylfate", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final Holder<Item> VAMPIRES_BED = REGISTRY.registerCollectible("vampires_bed", ArchfiendsBedItem::new);
    public static final Holder<Item> UTIL_SCORE = REGISTRY.registerCollectible("util_score", ScoreItem::new);
    public static final Holder<Item> UTIL_RESCISSION = REGISTRY.registerCollectible("util_rescission", RescissionItem::new);
    public static final Holder<Item> UTIL_OMNIKEY = REGISTRY.registerCollectible("util_omnikey", OmniKeyItem::new);
    public static final Holder<Item> UTIL_STARE = REGISTRY.registerCollectible("util_stare", GuardianStareItem::new);
    public static final Holder<Item> DURIN_OVERGROUND_ODYSSEY = REGISTRY.registerCollectible("durin_overground_odyssey", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final Holder<Item> CURSED_HEART = REGISTRY.registerCollectible("cursed_heart", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final Holder<Item> HAND_FERTILITY = REGISTRY.registerCollectible("hand_fertility", HandOfFertiliyItem::new);
    public static final Holder<Item> HAND_THORNS = REGISTRY.registerCollectible("hand_thorns", HandOfThornsItem::new);
    public static final Holder<Item> KING_ARMOR = REGISTRY.registerCollectible("king_armor", KingsArmourItem::new);
    public static final Holder<Item> KING_SPEAR = REGISTRY.registerCollectible("king_spear", KingsSpearItem::new);
    public static final Holder<Item> KING_EXTENSION = REGISTRY.registerCollectible("king_extension", KingsExtensionItem::new);
    public static final Holder<Item> KING_CROWN = REGISTRY.registerCollectible("king_crown", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final Holder<Item> KING_CRYSTAL = REGISTRY.registerCollectible("king_crystal", KingsCrystalItem::new);
    public static final Holder<Item> HAND_STRANGLE = REGISTRY.registerCollectible("hand_strangle", HandOfStrangleItem::new);
    public static final Holder<Item> HAND_SPEED = REGISTRY.registerCollectible("hand_speed", HandOfSpeedItem::new);
    public static final Holder<Item> HAND_FIREWORK = REGISTRY.registerCollectible("hand_firework", HandOfFireworkItem::new);
    public static final Holder<Item> HAND_OF_PULVERIZATION = REGISTRY.registerCollectible("hand_of_pulverization", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final Holder<Item> HAND_SWIPE = REGISTRY.registerCollectible("hand_swipe", HandOfSwipeItem::new);
    public static final Holder<Item> SARKAZ_KING_FLAG = REGISTRY.registerCollectible("sarkaz_king_flag", ArchfiendsFlagItem::new);
    public static final Holder<Item> SARKAZ_KING_BED = REGISTRY.registerCollectible("sarkaz_king_bed", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final Holder<Item> SARKAZ_KING_ARTIFACT = REGISTRY.registerCollectible("sarkaz_king_artifact", ArchfiendsArtifactItem::new);
    public static final Holder<Item> TREATY = REGISTRY.registerCollectible("treaty", CrimsonTreatyItem::new);
    public static final Holder<Item> HEMOST = REGISTRY.registerCollectible("hemost", SmellyHemostaticItem::new);
    public static final Holder<Item> YEARNING = REGISTRY.registerCollectible("yearning", UnripeYearningItem::new);
    public static final Holder<Item> CURSED_EMELIGHT = REGISTRY.registerCollectible("cursed_emelight", CollectibleCurseEMELIGHTItem::new);
    public static final Holder<Item> CURSED_GLOWBODY = REGISTRY.registerCollectible("cursed_glowbody", CollectibleCursedGLOWBODYItem::new);
    public static final Holder<Item> CURSED_RESEARCH = REGISTRY.registerCollectible("cursed_research", CollectibleCursedRESEARCHItem::new);
    public static final Holder<Item> RELIC_CROWN = REGISTRY.registerCollectible("relic_crown", CollectibleCROWNItem::new);
    public static final Holder<Item> CAERULA_HEART = REGISTRY.registerCollectible("caerula_heart", CaerulaHeartItem::new);
    public static final Holder<Item> FEATURED_CANNED_MEAT = REGISTRY.registerCollectible("featured_canned_meat", MeatCanItem::new);
    public static final Holder<Item> HAND_OF_BARREN = REGISTRY.registerCollectible("hand_of_barren", HandOfBarrenItem::new);
    public static final Holder<Item> BAT_BED = REGISTRY.registerCollectible("bat_bed", BatBedItem::new);
    public static final Holder<Item> PIGLIN_DIARY = REGISTRY.registerCollectible("piglin_diary", PiglinDiaryItem::new);
    public static final Holder<Item> CHITIN_KNIFE = REGISTRY.registerCollectible("chitin_knife", ChitinKnifeItem::new);
    public static final Holder<Item> COFFEE_CANDY = REGISTRY.registerCollectible("coffee_candy", CoffeeCandyItem::new);
    public static final Holder<Item> RAINBOW_CANDY = REGISTRY.registerCollectible("rainbow_candy", RainbowCandyItem::new);
    public static final Holder<Item> HAND_OF_ENGRAVE = REGISTRY.registerCollectible("hand_of_engrave", false, 25, false,
            CollectibleTiers.ADVANCED, new CollectibleItem.Levels(0, 99, 0),
            CollectibleActivation.builder().sound(SoundEvents.PLAYER_LEVELUP, 2F, 1F).particle(ParticleTypes.CLOUD, 72).showOverlay(true).build(),
            (stack, level, player, self) -> player.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(stack.getItem(), 0));
    public static final Holder<Item> SURVIVOR_CONTRACT = REGISTRY.registerCollectible("survivor_contract",
            false, 25, false,
            CollectibleTiers.ADVANCED, new CollectibleItem.Levels(0, 32, 0),
            CollectibleActivation.builder().sound(SoundEvents.BEACON_ACTIVATE, 3.2F, 1F).particle(ParticleTypes.GLOW, 72).showOverlay(true).build(),
            (stack, level, player, self) -> player.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(stack.getItem(), 0));
    public static final Holder<Item> ROYAL_FATE = REGISTRY.registerCollectible("royal_fate", RoyalFateItem::new);
    public static final Holder<Item> BOWL_SEAGRASS = REGISTRY.registerCollectible("bowl_seagrass", BowlSeagrassItem::new);
    public static final Holder<Item> GOLDEN_STORM = REGISTRY.registerCollectible("golden_storm", GoldenStormItem::new);
    public static final Holder<Item> CANNED_CHERRY = REGISTRY.registerCollectible("canned_cherry", CannedCherryItem::new);
    public static final Holder<Item> SOLO_MUSIC_BOX = REGISTRY.registerCollectible("solo_music_box", SoloMusicBoxItem::new);
    public static final Holder<Item> REDSTONE_IRIS_FLOWER = REGISTRY.registerCollectible("redstone_iris_flower", RedstoneIrisFlowerItem::new);
    public static final Holder<Item> ODD_FLUTE = REGISTRY.registerCollectible("odd_flute", OddFluteItem::new);
    public static final Holder<Item> VOYAGE_OF_GOLD = REGISTRY.registerCollectible("voyage_of_gold", VoyageOfGoldItem::new);
    public static final Holder<Item> TOPONYM_TEXTOLOGY = REGISTRY.registerCollectible("toponym_textology", ToponymTextologyItem::new);
    public static final Holder<Item> KETTLE = REGISTRY.registerCollectible("kettle", KettleItem::new);
    public static final Holder<Item> PROOF_OF_LONGEVITY = REGISTRY.registerCollectible("proof_of_longevity", ProofOfLongevityItem::new);
    public static final Holder<Item> TULIP_MEDCINE = REGISTRY.registerCollectible("tulip_medcine", TulipMedcineItem::new);
    public static final Holder<Item> NURTURE_GENE_SET = REGISTRY.registerCollectible("nurture_gene_set", NurtureGeneSetItem::new);
    public static final Holder<Item> GOLDEN_CHALISE = REGISTRY.registerCollectible("golden_chalise", GoldenChaliseItem::new);
    public static final Holder<Item> OIL_AND_CREAM = REGISTRY.registerCollectible("oil_and_cream", OilAndCreamItem::new);
    public static final Holder<Item> HAND_SWORD = REGISTRY.registerCollectible("hand_sword", () -> new BooleanCollectibleItem(Rarity.EPIC, CollectibleActivation.builder().sound(SoundEvents.PLAYER_LEVELUP, 2F, 1F).particle(ParticleTypes.CLOUD, 72).showOverlay(true).build()));
}
