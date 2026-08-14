package com.susen36.caerulaarbor.init;

import com.susen36.babel.collectible.*;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.item.*;
import com.susen36.caerulaarbor.item.relic.BooleanCollectibleItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CACollectible {
    public static final CollectibleBuilder COLLECTIBLE = CollectibleBuilder.create(CaerulaArbor.MODID);

    public static final DeferredHolder<Item, ? extends Item> LEGEND_CHITIN = COLLECTIBLE.registerCollectible("legend_chitin", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final DeferredHolder<Item, ? extends Item> UTIL_ALLAY = COLLECTIBLE.registerCollectible("util_allay", AlleySculptureItem::new);
    public static final DeferredHolder<Item, ? extends Item> DISO = COLLECTIBLE.registerCollectible("diso", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final DeferredHolder<Item, ? extends Item> DISO_FLESH = COLLECTIBLE.registerCollectible("diso_flesh", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final DeferredHolder<Item, ? extends Item> DISO_BLOOD = COLLECTIBLE.registerCollectible("diso_blood", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final DeferredHolder<Item, ? extends Item> DISO_NEURO = COLLECTIBLE.registerCollectible("diso_neuro", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final DeferredHolder<Item, ? extends Item> DISO_ATTENTION = COLLECTIBLE.registerCollectible("diso_attention", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final DeferredHolder<Item, ? extends Item> AHND_SWIPE = COLLECTIBLE.registerCollectible("ahnd_swipe", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final DeferredHolder<Item, ? extends Item> HANSHAND_SPIKE = COLLECTIBLE.registerCollectible("hanshand_spike", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final DeferredHolder<Item, ? extends Item> SARKAZ_KING_RYLFATE = COLLECTIBLE.registerCollectible("sarkaz_king_rylfate", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final DeferredHolder<Item, ? extends Item> VAMPIRES_BED = COLLECTIBLE.registerCollectible("vampires_bed", ArchfiendsBedItem::new);
    public static final DeferredHolder<Item, ? extends Item> UTIL_SCORE = COLLECTIBLE.registerCollectible("util_score", ScoreItem::new);
    public static final DeferredHolder<Item, ? extends Item> UTIL_RESCISSION = COLLECTIBLE.registerCollectible("util_rescission", RescissionItem::new);
    public static final DeferredHolder<Item, ? extends Item> UTIL_OMNIKEY = COLLECTIBLE.registerCollectible("util_omnikey", OmniKeyItem::new);
    public static final DeferredHolder<Item, ? extends Item> UTIL_STARE = COLLECTIBLE.registerCollectible("util_stare", GuardianStareItem::new);
    public static final DeferredHolder<Item, ? extends Item> DURIN_OVERGROUND_ODYSSEY = COLLECTIBLE.registerCollectible("durin_overground_odyssey", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final DeferredHolder<Item, ? extends Item> CURSED_HEART = COLLECTIBLE.registerCollectible("cursed_heart", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final DeferredHolder<Item, ? extends Item> HAND_FERTILITY = COLLECTIBLE.registerCollectible("hand_fertility", HandOfFertiliyItem::new);
    public static final DeferredHolder<Item, ? extends Item> HAND_THORNS = COLLECTIBLE.registerCollectible("hand_thorns", HandOfThornsItem::new);
    public static final DeferredHolder<Item, ? extends Item> KING_ARMOR = COLLECTIBLE.registerCollectible("king_armor", KingsArmourItem::new);
    public static final DeferredHolder<Item, ? extends Item> KING_SPEAR = COLLECTIBLE.registerCollectible("king_spear", KingsSpearItem::new);
    public static final DeferredHolder<Item, ? extends Item> KING_EXTENSION = COLLECTIBLE.registerCollectible("king_extension", KingsExtensionItem::new);
    public static final DeferredHolder<Item, ? extends Item> KING_CROWN = COLLECTIBLE.registerCollectible("king_crown", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final DeferredHolder<Item, ? extends Item> KING_CRYSTAL = COLLECTIBLE.registerCollectible("king_crystal", KingsCrystalItem::new);
    public static final DeferredHolder<Item, ? extends Item> HAND_STRANGLE = COLLECTIBLE.registerCollectible("hand_strangle", HandOfStrangleItem::new);
    public static final DeferredHolder<Item, ? extends Item> HAND_SPEED = COLLECTIBLE.registerCollectible("hand_speed", HandOfSpeedItem::new);
    public static final DeferredHolder<Item, ? extends Item> HAND_FIREWORK = COLLECTIBLE.registerCollectible("hand_firework", HandOfFireworkItem::new);
    public static final DeferredHolder<Item, ? extends Item> HAND_OF_PULVERIZATION = COLLECTIBLE.registerCollectible("hand_of_pulverization", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final DeferredHolder<Item, ? extends Item> HAND_SWIPE = COLLECTIBLE.registerCollectible("hand_swipe", HandOfSwipeItem::new);
    public static final DeferredHolder<Item, ? extends Item> SARKAZ_KING_FLAG = COLLECTIBLE.registerCollectible("sarkaz_king_flag", ArchfiendsFlagItem::new);
    public static final DeferredHolder<Item, ? extends Item> SARKAZ_KING_BED = COLLECTIBLE.registerCollectible("sarkaz_king_bed", () -> new BooleanCollectibleItem(Rarity.UNCOMMON, BooleanCollectibleItem.standardActivation()));
    public static final DeferredHolder<Item, ? extends Item> SARKAZ_KING_ARTIFACT = COLLECTIBLE.registerCollectible("sarkaz_king_artifact", ArchfiendsArtifactItem::new);
    public static final DeferredHolder<Item, ? extends Item> TREATY = COLLECTIBLE.registerCollectible("treaty", CrimsonTreatyItem::new);
    public static final DeferredHolder<Item, ? extends Item> HEMOST = COLLECTIBLE.registerCollectible("hemost", SmellyHemostaticItem::new);
    public static final DeferredHolder<Item, ? extends Item> YEARNING = COLLECTIBLE.registerCollectible("yearning", UnripeYearningItem::new);
    public static final DeferredHolder<Item, ? extends Item> CURSED_EMELIGHT = COLLECTIBLE.registerCollectible("cursed_emelight", CollectibleCurseEMELIGHTItem::new);
    public static final DeferredHolder<Item, ? extends Item> CURSED_GLOWBODY = COLLECTIBLE.registerCollectible("cursed_glowbody", CollectibleCursedGLOWBODYItem::new);
    public static final DeferredHolder<Item, ? extends Item> CURSED_RESEARCH = COLLECTIBLE.registerCollectible("cursed_research", CollectibleCursedRESEARCHItem::new);
    public static final DeferredHolder<Item, ? extends Item> RELIC_CROWN = COLLECTIBLE.registerCollectible("relic_crown", CollectibleCROWNItem::new);
    public static final DeferredHolder<Item, ? extends Item> CAERULA_HEART = COLLECTIBLE.registerCollectible("caerula_heart", CaerulaHeartItem::new);
    public static final DeferredHolder<Item, ? extends Item> FEATURED_CANNED_MEAT = COLLECTIBLE.registerCollectible("featured_canned_meat", MeatCanItem::new);
    public static final DeferredHolder<Item, ? extends Item> HAND_OF_BARREN = COLLECTIBLE.registerCollectible("hand_of_barren", HandOfBarrenItem::new);
    public static final DeferredHolder<Item, ? extends Item> BAT_BED = COLLECTIBLE.registerCollectible("bat_bed", BatBedItem::new);
    public static final DeferredHolder<Item, ? extends Item> PIGLIN_DIARY = COLLECTIBLE.registerCollectible("piglin_diary", PiglinDiaryItem::new);
    public static final DeferredHolder<Item, ? extends Item> CHITIN_KNIFE = COLLECTIBLE.registerCollectible("chitin_knife", ChitinKnifeItem::new);
    public static final DeferredHolder<Item, ? extends Item> COFFEE_CANDY = COLLECTIBLE.registerCollectible("coffee_candy", CoffeeCandyItem::new);
    public static final DeferredHolder<Item, ? extends Item> RAINBOW_CANDY = COLLECTIBLE.registerCollectible("rainbow_candy", RainbowCandyItem::new);
    public static final DeferredHolder<Item, ? extends Item> HAND_OF_ENGRAVE = COLLECTIBLE.registerCollectible("hand_of_engrave", false, 25, false,
            CollectibleTiers.ADVANCED, new CollectibleItem.Levels(0, 99, 0),
            CollectibleActivation.builder().sound(SoundEvents.PLAYER_LEVELUP, 2F, 1F).particle(ParticleTypes.CLOUD, 72).showOverlay(true).build(),
            (stack, level, player, self) -> player.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(stack.getItem(), 0));
    public static final DeferredHolder<Item, ? extends Item> SURVIVOR_CONTRACT = COLLECTIBLE.registerCollectible("survivor_contract",
            false, 25, false,
            CollectibleTiers.ADVANCED, new CollectibleItem.Levels(0, 32, 0),
            CollectibleActivation.builder().sound(SoundEvents.BEACON_ACTIVATE, 3.2F, 1F).particle(ParticleTypes.GLOW, 72).showOverlay(true).build(),
            (stack, level, player, self) -> player.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(stack.getItem(), 0));
    public static final DeferredHolder<Item, ? extends Item> ROYAL_FATE = COLLECTIBLE.registerCollectible("royal_fate", RoyalFateItem::new);
    public static final DeferredHolder<Item, ? extends Item> BOWL_SEAGRASS = COLLECTIBLE.registerCollectible("bowl_seagrass", BowlSeagrassItem::new);
    public static final DeferredHolder<Item, ? extends Item> GOLDEN_STORM = COLLECTIBLE.registerCollectible("golden_storm", GoldenStormItem::new);
    public static final DeferredHolder<Item, ? extends Item> CANNED_CHERRY = COLLECTIBLE.registerCollectible("canned_cherry", CannedCherryItem::new);
    public static final DeferredHolder<Item, ? extends Item> SOLO_MUSIC_BOX = COLLECTIBLE.registerCollectible("solo_music_box", SoloMusicBoxItem::new);
    public static final DeferredHolder<Item, ? extends Item> REDSTONE_IRIS_FLOWER = COLLECTIBLE.registerCollectible("redstone_iris_flower", RedstoneIrisFlowerItem::new);
    public static final DeferredHolder<Item, ? extends Item> ODD_FLUTE = COLLECTIBLE.registerCollectible("odd_flute", OddFluteItem::new);
    public static final DeferredHolder<Item, ? extends Item> VOYAGE_OF_GOLD = COLLECTIBLE.registerCollectible("voyage_of_gold", VoyageOfGoldItem::new);
    public static final DeferredHolder<Item, ? extends Item> TOPONYM_TEXTOLOGY = COLLECTIBLE.registerCollectible("toponym_textology", ToponymTextologyItem::new);
    public static final DeferredHolder<Item, ? extends Item> KETTLE = COLLECTIBLE.registerCollectible("kettle", KettleItem::new);
    public static final DeferredHolder<Item, ? extends Item> PROOF_OF_LONGEVITY = COLLECTIBLE.registerCollectible("proof_of_longevity", ProofOfLongevityItem::new);
    public static final DeferredHolder<Item, ? extends Item> TULIP_MEDCINE = COLLECTIBLE.registerCollectible("tulip_medcine", TulipMedcineItem::new);
    public static final DeferredHolder<Item, ? extends Item> NURTURE_GENE_SET = COLLECTIBLE.registerCollectible("nurture_gene_set", NurtureGeneSetItem::new);
    public static final DeferredHolder<Item, ? extends Item> GOLDEN_CHALISE = COLLECTIBLE.registerCollectible("golden_chalise", GoldenChaliseItem::new);
    public static final DeferredHolder<Item, ? extends Item> OIL_AND_CREAM = COLLECTIBLE.registerCollectible("oil_and_cream", OilAndCreamItem::new);
    public static final DeferredHolder<Item, ? extends Item> HAND_SWORD = COLLECTIBLE.registerCollectible("hand_sword", () -> new BooleanCollectibleItem(Rarity.EPIC, CollectibleActivation.builder().sound(SoundEvents.PLAYER_LEVELUP, 2F, 1F).particle(ParticleTypes.CLOUD, 72).showOverlay(true).build()));
}
