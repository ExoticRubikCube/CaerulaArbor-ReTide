package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.block.item.*;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.item.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "SameParameterValue"})
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CAItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, CaerulaArborMod.MODID);
    public static final RegistryObject<Item> CAERULA_RECORDER = REGISTRY.register("caerula_recorder", CaerulaRecorderItem::new);
    public static final RegistryObject<Item> RELIC_CURSE_EMELIGHT = REGISTRY.register("relic_curse_emelight", RelicCurseEMELIGHTItem::new);
    public static final RegistryObject<Item> EMERGENCY_LIGHT = block(CABlocks.EMERGENCY_LIGHT);
    public static final RegistryObject<Item> RELIC_CURSED_GLOWBODY = REGISTRY.register("relic_cursed_glowbody", RelicCursedGLOWBODYItem::new);
    public static final RegistryObject<Item> RELIC_CURSED_RESEARCH = REGISTRY.register("relic_cursed_research", RelicCursedRESEARCHItem::new);
    public static final RegistryObject<Item> RELIC_CROWN = REGISTRY.register("relic_crown", RelicCROWNItem::new);
    public static final RegistryObject<Item> KINGS_ARMOUR = REGISTRY.register("kings_armour", KingsArmourItem::new);
    public static final RegistryObject<Item> KINGS_ARMOR = block(CABlocks.KINGS_ARMOR);
    public static final RegistryObject<Item> BLOCK_CROWN = block(CABlocks.BLOCK_CROWN);
    public static final RegistryObject<Item> KINGS_SPEAR = REGISTRY.register("kings_spear", KingsSpearItem::new);
    public static final RegistryObject<Item> BLOCK_SPEAR = block(CABlocks.BLOCK_SPEAR);
    public static final RegistryObject<Item> KINGS_EXTENSION = REGISTRY.register("kings_extension", KingsExtensionItem::new);
    public static final RegistryObject<Item> BLOCK_EXTENSION = block(CABlocks.BLOCK_EXTENSION);
    public static final RegistryObject<Item> KINGS_CRYSTAL = REGISTRY.register("kings_crystal", KingsCrystalItem::new);
    public static final RegistryObject<Item> BLOCK_CRYSTAL = block(CABlocks.BLOCK_CRYSTAL);
    public static final RegistryObject<Item> REDSTONE_INGOT = REGISTRY.register("redstone_ingot", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> THE_SPEAR = REGISTRY.register("the_spear", TheSpearItem::new);
    public static final RegistryObject<Item> WEARABLE_CHEST_CHESTPLATE = REGISTRY.register("wearable_chest_chestplate", WearableChestItem.Chestplate::new);
    public static final RegistryObject<WearableCrownItem> WEARABLE_CROWN_HELMET = REGISTRY.register("wearable_crown_helmet", () -> new WearableCrownItem(ArmorItem.Type.HELMET, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> HAND_OF_THORNS = REGISTRY.register("hand_of_thorns", HandOfThornsItem::new);
    public static final RegistryObject<Item> HAND_OF_STRANGLE = REGISTRY.register("hand_of_strangle", HandOfStrangleItem::new);
    public static final RegistryObject<Item> HAND_OF_FERTILIY = REGISTRY.register("hand_of_fertiliy", HandOfFertiliyItem::new);
    public static final RegistryObject<Item> HAND_OF_SPEED = REGISTRY.register("hand_of_speed", HandOfSpeedItem::new);
    public static final RegistryObject<Item> HAND_OF_BARREN = REGISTRY.register("hand_of_barren", HandOfBarrenItem::new);
    public static final RegistryObject<Item> FLUORE_BERRIES = REGISTRY.register("fluore_berries", FluoreBerriesItem::new);
    public static final RegistryObject<Item> RADIANT_BERRIES = REGISTRY.register("radiant_berries", RadiantBerriesItem::new);
    public static final RegistryObject<Item> HAND_OF_SPOTLESS = REGISTRY.register("hand_of_spotless", HandOfSwipeItem::new);
    public static final RegistryObject<Item> ARCHFIENDS_ARTIFACT = REGISTRY.register("archfiends_artifact", ArchfiendsArtifactItem::new);
    public static final RegistryObject<Item> HAND_OF_FIREWORK = REGISTRY.register("hand_of_firework", HandOfFireworkItem::new);
    public static final RegistryObject<Item> ARCHFIENDS_FLAG = REGISTRY.register("archfiends_flag", ArchfiendsFlagItem::new);
    public static final RegistryObject<Item> HAND_OF_ENGRAVE = REGISTRY.register("hand_of_engrave", HandOfEngraveItem::new);
    public static final RegistryObject<Item> ARCHFIENDS_BED = REGISTRY.register("archfiends_bed", ArchfiendsBedItem::new);
    public static final RegistryObject<Item> SURVIVOR_CONTRACT = REGISTRY.register("survivor_contract", SurvivorContractItem::new);
    public static final RegistryObject<Item> ROYAL_FATE = REGISTRY.register("royal_fate", RoyalFateItem::new);
    public static final RegistryObject<Item> BLOCK_FATE = block(CABlocks.BLOCK_FATE);
    public static final RegistryObject<Item> CRIMSON_TREATY = REGISTRY.register("crimson_treaty", CrimsonTreatyItem::new);
    public static final RegistryObject<Item> MEAT_CAN = REGISTRY.register("meat_can", MeatCanItem::new);
    public static final RegistryObject<Item> EMPTY_CAN = REGISTRY.register("empty_can", EmptyCanItem::new);
    public static final RegistryObject<Item> BOWL_SEAGRASS = REGISTRY.register("bowl_seagrass", BowlSeagrassItem::new);
    public static final RegistryObject<Item> GOLDEN_STORM = REGISTRY.register("golden_storm", GoldenStormItem::new);
    public static final RegistryObject<Item> PAPER_BAG = REGISTRY.register("paper_bag", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> COFFEE_CANDY = REGISTRY.register("coffee_candy", CoffeeCandyItem::new);
    public static final RegistryObject<Item> CANNED_CHERRY = REGISTRY.register("canned_cherry", CannedCherryItem::new);
    public static final RegistryObject<Item> BERRY_CAN = block(CABlocks.BERRY_CAN);
    public static final RegistryObject<Item> RAINBOW_CANDY = REGISTRY.register("rainbow_candy", RainbowCandyItem::new);
    public static final RegistryObject<Item> AROMATIC_COFFEE = REGISTRY.register("aromatic_coffee", AromaticCoffeeItem::new);
    public static final RegistryObject<Item> SOLO_MUSIC_BOX = REGISTRY.register("solo_music_box", SoloMusicBoxItem::new);
    public static final RegistryObject<Item> MUSIC_BOX_FIXED = REGISTRY.register("music_box_fixed", MusicBoxFixedItem::new);
    public static final RegistryObject<Item> REDSTONE_IRIS = block(CABlocks.REDSTONE_IRIS);
    public static final RegistryObject<Item> REDSTONE_IRIS_FLOWER = REGISTRY.register("redstone_iris_flower", RedstoneIrisFlowerItem::new);
    public static final RegistryObject<Item> REDSTONEIRIS_SEEDING = block(CABlocks.REDSTONEIRIS_SEEDING);
    public static final RegistryObject<Item> ODD_FLUTE = REGISTRY.register("odd_flute", OddFluteItem::new);
    public static final RegistryObject<Item> VOYAGE_OF_GOLD = REGISTRY.register("voyage_of_gold", VoyageOfGoldItem::new);
    public static final RegistryObject<Item> THERMOGRAPH = REGISTRY.register("thermograph", ThermographItem::new);
    public static final RegistryObject<Item> PIGLIN_DIARY = REGISTRY.register("piglin_diary", PiglinDiaryItem::new);
    public static final RegistryObject<Item> TOPONYM_TEXTOLOGY = REGISTRY.register("toponym_textology", ToponymTextologyItem::new);
    public static final RegistryObject<Item> RUN_FISH_SPAWN_EGG = REGISTRY.register("run_fish_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.RUN_FISH, -16777012, -3355444, new Item.Properties()));
    public static final RegistryObject<Item> SLIDER_FISH_SPAWN_EGG = REGISTRY.register("slider_fish_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.SLIDER_FISH, -13421569, -3355444, new Item.Properties()));
    public static final RegistryObject<Item> SUPER_SLIDER_SPAWN_EGG = REGISTRY.register("super_slider_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.SUPER_SLIDER, -1, -1, new Item.Properties()));
    public static final RegistryObject<Item> SHOOTER_FISH_SPAWN_EGG = REGISTRY.register("shooter_fish_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.SHOOTER_FISH, -13434676, -3355444, new Item.Properties()));
    public static final RegistryObject<Item> FLY_FISH_SPAWN_EGG = REGISTRY.register("fly_fish_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.FLY_FISH, -16737895, -3355444, new Item.Properties()));
    public static final RegistryObject<Item> REAPER_FISH_SPAWN_EGG = REGISTRY.register("reaper_fish_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.REAPER_FISH, -6710785, -3355444, new Item.Properties()));
    public static final RegistryObject<Item> POCKET_SEA_CREEPER_SPAWN_EGG = REGISTRY.register("pocket_sea_creeper_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.POCKET_SEA_CREEPER, -10092442, -3342337, new Item.Properties()));
    public static final RegistryObject<Item> PUNCTURE_FISH_SPAWN_EGG = REGISTRY.register("puncture_fish_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.PUNCTURE_FISH, -16750900, -1, new Item.Properties()));
    public static final RegistryObject<Item> SEA_TRAIL_INIT = block(CABlocks.SEA_TRAIL_INIT);
    public static final RegistryObject<Item> SEA_TRAIL_GROWING = block(CABlocks.SEA_TRAIL_GROWING);
    public static final RegistryObject<Item> SEA_TRAIL_GROWN = block(CABlocks.SEA_TRAIL_GROWN);
    public static final RegistryObject<Item> SEA_TRAIL_MOR = REGISTRY.register("sea_trail_mor", SeaTrailMorItem::new);
    public static final RegistryObject<Item> BOMB_TRAILER = block(CABlocks.BOMB_TRAILER);
    public static final RegistryObject<Item> CANNED_WATER = REGISTRY.register("canned_water", CannedWaterItem::new);
    public static final RegistryObject<Item> CANNED_LAVA = REGISTRY.register("canned_lava", CannedLavaItem::new);
    public static final RegistryObject<Item> OCEAN_EYE = REGISTRY.register("ocean_eye", OceanEyeItem::new);
    public static final RegistryObject<Item> COOKED_FIBRE = REGISTRY.register("cooked_fibre", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(6).saturationMod(0.5f).meat().build())));
    public static final RegistryObject<Item> BASELAYER_ABYSSAL_SPAWN_EGG = REGISTRY.register("baselayer_abyssal_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.BASELAYER_ABYSSAL, -16776961, -2056595, new Item.Properties()));
    public static final RegistryObject<Item> OCEAN_GLASS = block(CABlocks.OCEAN_GLASS);
    public static final RegistryObject<Item> OCEAN_GLASSPANE = block(CABlocks.OCEAN_GLASSPANE);
    public static final RegistryObject<Item> CHITIN_BLOCK = block(CABlocks.CHITIN_BLOCK);
    public static final RegistryObject<Item> COOKED_MOR = REGISTRY.register("cooked_mor", CookedMorItem::new);
    public static final RegistryObject<Item> SWORD_OCEAN_CRYSTAL = REGISTRY.register("sword_ocean_crystal", SwordOceanCrystalItem::new);
    public static final RegistryObject<Item> CHITIN_ARMOR_HELMET = REGISTRY.register("chitin_armor_helmet", ChitinArmorItem.Helmet::new);
    public static final RegistryObject<Item> CHITIN_ARMOR_CHESTPLATE = REGISTRY.register("chitin_armor_chestplate", ChitinArmorItem.Chestplate::new);
    public static final RegistryObject<Item> CHITIN_ARMOR_LEGGINGS = REGISTRY.register("chitin_armor_leggings", ChitinArmorItem.Leggings::new);
    public static final RegistryObject<Item> CHITIN_ARMOR_BOOTS = REGISTRY.register("chitin_armor_boots", ChitinArmorItem.Boots::new);
    public static final RegistryObject<Item> CHITIN_PICKAXE = REGISTRY.register("chitin_pickaxe", ChitinPickaxeItem::new);
    public static final RegistryObject<Item> CHITIN_AXE = REGISTRY.register("chitin_axe", ChitinAxeItem::new);
    public static final RegistryObject<Item> CHITIN_SHOVEL = REGISTRY.register("chitin_shovel", ChitinShovelItem::new);
    public static final RegistryObject<Item> CHITIN_HOE = REGISTRY.register("chitin_hoe", ChitinHoeItem::new);
    public static final RegistryObject<Item> CHITIN_SWORD = REGISTRY.register("chitin_sword", ChitinSwordItem::new);
    public static final RegistryObject<Item> PICKAXE_OCEAN_CRYSTAL = REGISTRY.register("pickaxe_ocean_crystal", OceanthornPickaxeItem::new);
    public static final RegistryObject<Item> AXE_OCEAN_CRYSTAL = REGISTRY.register("axe_ocean_crystal", OceanthornAxeItem::new);
    public static final RegistryObject<Item> SHOVEL_OCEAN_CRYSTAL = REGISTRY.register("shovel_ocean_crystal", OceanthornShovelItem::new);
    public static final RegistryObject<Item> HOE_OCEAN_CRYSTAL = REGISTRY.register("hoe_ocean_crystal", OceanthornHoeItem::new);
    public static final RegistryObject<Item> CUTIN_STICK = REGISTRY.register("cutin_stick", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> PREDATOR_ABYSSAL_SPAWN_EGG = REGISTRY.register("predator_abyssal_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.PREDATOR_ABYSSAL, -16777063, -2056595, new Item.Properties()));
    public static final RegistryObject<Item> KETTLE = REGISTRY.register("kettle", KettleItem::new);
    public static final RegistryObject<Item> BLOCK_KETTLE = block(CABlocks.BLOCK_KETTLE);
    public static final RegistryObject<Item> INSTANT_NOODLE = REGISTRY.register("instant_noodle", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(2).saturationMod(0.25f).build())));
    public static final RegistryObject<Item> CANNED_NOODLE = REGISTRY.register("canned_noodle", CannedNoodleItem::new);
    public static final RegistryObject<Item> CANNED_BOILED_WATER = REGISTRY.register("canned_boiled_water", CannedBoiledWaterItem::new);
    public static final RegistryObject<Item> OBISIDIAN_BALL = REGISTRY.register("obisidian_ball", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> CHITIN_KNIFE = REGISTRY.register("chitin_knife", ChitinKnifeItem::new);
    public static final RegistryObject<Item> COMPLEX_CHITIN_SWORD = REGISTRY.register("complex_chitin_sword", ComplexChitinSwordItem::new);
    public static final RegistryObject<Item> COMPLEX_CHITIN_PICKAXE = REGISTRY.register("complex_chitin_pickaxe", ComplexChitinPickaxeItem::new);
    public static final RegistryObject<Item> COMPLEX_CHITIN_AXE = REGISTRY.register("complex_chitin_axe", ComplexChitinAxeItem::new);
    public static final RegistryObject<Item> COMPLEX_CHITIN_SHOVEL = REGISTRY.register("complex_chitin_shovel", ComplexChitinShovelItem::new);
    public static final RegistryObject<Item> COMPLEX_CHITIN_HOE = REGISTRY.register("complex_chitin_hoe", ComplexChitinHoeItem::new);
    public static final RegistryObject<Item> OCEAN_FARMLAND = block(CABlocks.OCEAN_FARMLAND);
    public static final RegistryObject<Item> PHLOEM_BOW = REGISTRY.register("phloem_bow", PhloemBowItem::new);
    public static final RegistryObject<Item> LEGENDARY_SPEAR = REGISTRY.register("legendary_spear", LegendarySpearItem::new);
    public static final RegistryObject<Item> ALLAY_SCULPTURE = REGISTRY.register("allay_sculpture", AlleySculptureItem::new);
    public static final RegistryObject<Item> ALLAY_BLOCK = block(CABlocks.ALLAY_BLOCK);
    public static final RegistryObject<Item> BAT_BED = REGISTRY.register("bat_bed", BatBedItem::new);
    public static final RegistryObject<Item> BLOCK_BATBED = block(CABlocks.BLOCK_BATBED);
    public static final RegistryObject<Item> BATBED_UPPER = block(CABlocks.BATBED_UPPER);
    public static final RegistryObject<Item> PROOF_OF_LONGEVITY = REGISTRY.register("proof_of_longevity", ProofOfLongevityItem::new);
    public static final RegistryObject<Item> OMNI_KEY = REGISTRY.register("omni_key", OmniKeyItem::new);
    public static final RegistryObject<Item> SCORE = REGISTRY.register("score", ScoreItem::new);
    public static final RegistryObject<Item> RESCISSION = REGISTRY.register("rescission", RescissionItem::new);
    public static final RegistryObject<Item> TRAIL_CAKE = block(CABlocks.TRAIL_CAKE);
    public static final RegistryObject<Item> TRAIL_CAKE_PIECE = REGISTRY.register("trail_cake_piece", TrailCakePieceItem::new);
    public static final RegistryObject<Item> CARAMEL_CAKE = block(CABlocks.CARAMEL_CAKE);
    public static final RegistryObject<Item> CARAMEL_CAKE_PIECE = REGISTRY.register("caramel_cake_piece", CaramelCakePieceItem::new);
    public static final RegistryObject<Item> CARAMEL_MOR = REGISTRY.register("caramel_mor", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(3).saturationMod(1f).build())) {
        @Override
        public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemstack, net.minecraft.world.level.@NotNull Level world, net.minecraft.world.entity.@NotNull LivingEntity entity) {
            ItemStack retval = super.finishUsingItem(itemstack, world, entity);
            ModCapabilities.getSanityInjury(entity).heal(15);
            return retval;
        }
    });
    public static final RegistryObject<Item> GUIDE_ABYSSAL_SPAWN_EGG = REGISTRY.register("guide_abyssal_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.GUIDE_ABYSSAL, -13434778, -2056595, new Item.Properties()));
    public static final RegistryObject<Item> SPLASHER_ABYSSAL_SPAWN_EGG = REGISTRY.register("splasher_abyssal_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.SPLASHER_ABYSSAL, -16777012, -2056595, new Item.Properties()));
    public static final RegistryObject<Item> FAKE_EGG = REGISTRY.register("fake_egg", FakeEggItem::new);
    public static final RegistryObject<Item> REAL_EGG = REGISTRY.register("real_egg", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(4).saturationMod(0.5f).meat().build())));
    public static final RegistryObject<Item> FERMENTED_OCEAN_EYE = REGISTRY.register("fermented_ocean_eye", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)) {
        @Override
        public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemstack) {
            return UseAnim.EAT;
        }
    });
    public static final RegistryObject<Item> OCEAN_MACHINE = REGISTRY.register("ocean_machine", () -> new Item(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> OCEAN_CRYSTAL_BLOCK = block(CABlocks.OCEAN_CRYSTAL_BLOCK);
    public static final RegistryObject<Item> COMPLEX_CHITIN_BLOCK = block(CABlocks.COMPLEX_CHITIN_BLOCK);
    public static final RegistryObject<Item> GUARDIAN_STARE = REGISTRY.register("guardian_stare", GuardianStareItem::new);
    public static final RegistryObject<Item> HAND_SWORD = REGISTRY.register("hand_sword", HandSwordItem::new);
    public static final RegistryObject<Item> TIDE_OBSERVATION = block(CABlocks.TIDE_OBSERVATION);
    public static final RegistryObject<Item> SAMPLE_BREED = REGISTRY.register("sample_breed", SampleBreedItem::new);
    public static final RegistryObject<Item> ANCHOR_LOWER = block(CABlocks.ANCHOR_LOWER);
    public static final RegistryObject<Item> ANCHOR_MEDIUM = block(CABlocks.ANCHOR_MEDIUM);
    public static final RegistryObject<Item> ANCHOR_UPPER = block(CABlocks.ANCHOR_UPPER);
    public static final RegistryObject<Item> UMBRELLA_ABYSSAL_SPAWN_EGG = REGISTRY.register("umbrella_abyssal_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.UMBRELLA_ABYSSAL, -10066177, -2056595, new Item.Properties()));
    public static final RegistryObject<Item> TRAILED_WOODEN_SWORD = REGISTRY.register("trailed_wooden_sword", TrailedWoodenSwordItem::new);
    public static final RegistryObject<Item> TRAILED_STONE_SWORD = REGISTRY.register("trailed_stone_sword", TrailedStoneSwordItem::new);
    public static final RegistryObject<Item> TRAILED_IRON_SWORD = REGISTRY.register("trailed_iron_sword", TrailedIronSwordItem::new);
    public static final RegistryObject<Item> TRAILED_DIAMOND_SWORD = REGISTRY.register("trailed_diamond_sword", TrailedDiamondSwordItem::new);
    public static final RegistryObject<Item> TRAILED_NETHERITE_SWORD = REGISTRY.register("trailed_netherite_sword", TrailedNetheriteSwordItem::new);
    public static final RegistryObject<Item> TRAILED_GOLDEN_SWORD = REGISTRY.register("trailed_golden_sword", TrailedGoldenSwordItem::new);
    public static final RegistryObject<Item> TRAIL_CREAM = REGISTRY.register("trail_cream", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> CRACKER_ABYSSAL_SPAWN_EGG = REGISTRY.register("cracker_abyssal_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.CRACKER_ABYSSAL, -16436298, -2056595, new Item.Properties()));
    public static final RegistryObject<Item> DISPATCH_STICK = REGISTRY.register("dispatch_stick", DispatchStickItem::new);
    public static final RegistryObject<Item> COLLECTOR_PROKARYOTE_SPAWN_EGG = REGISTRY.register("collector_prokaryote_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.COLLECTOR_PROKARYOTE, -16776961, -16316408, new Item.Properties()));
    public static final RegistryObject<Item> BROKEN_CELL_CLUSTER = REGISTRY.register("broken_cell_cluster", BrokenCellClusterItem::new);
    public static final RegistryObject<Item> CELL_CLUSTER = REGISTRY.register("cell_cluster", CellClusterItem::new);
    public static final RegistryObject<Item> COOKED_BROKEN_CELL_CLUSTER = REGISTRY.register("cooked_broken_cell_cluster", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(6).saturationMod(0.25f).meat().build())));
    public static final RegistryObject<Item> COOKED_CELL_CLUSTER = REGISTRY.register("cooked_cell_cluster", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(8).saturationMod(0.3f).meat().build())));
    public static final RegistryObject<Item> TRANSFORM_CELL = REGISTRY.register("transform_cell", TransformCellItem::new);
    public static final RegistryObject<Item> BONE_FISH_SPAWN_EGG = REGISTRY.register("bone_fish_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.BONE_FISH, -10066177, -1, new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_RECORDER = block(CABlocks.BLOCK_RECORDER);
    public static final RegistryObject<Item> CHISELER_FISH_SPAWN_EGG = REGISTRY.register("chiseler_fish_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.CHISELER_FISH, -10053121, -1, new Item.Properties()));
    public static final RegistryObject<Item> PREGNANT_FISH_SPAWN_EGG = REGISTRY.register("pregnant_fish_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.PREGNANT_FISH, -10066177, -404566, new Item.Properties()));
    public static final RegistryObject<Item> FAKE_OFFSPRING_SPAWN_EGG = REGISTRY.register("fake_offspring_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.FAKE_OFFSPRING, -856596, -6711916, new Item.Properties()));
    public static final RegistryObject<Item> FLEE_FISH_SPAWN_EGG = REGISTRY.register("flee_fish_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.FLEE_FISH, -16764007, -29083, new Item.Properties()));
    public static final RegistryObject<Item> ROUTE_FRACTAL_SPAWN_EGG = REGISTRY.register("route_fractal_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.ROUTE_FRACTAL, -16711423, -15260869, new Item.Properties()));
    public static final RegistryObject<Item> SEA_TRAIL_SOLID = block(CABlocks.SEA_TRAIL_SOLID);
    public static final RegistryObject<Item> TRAIL_BRICK = block(CABlocks.TRAIL_BRICK);
    public static final RegistryObject<Item> TRAIL_SLAB = block(CABlocks.TRAIL_SLAB);
    public static final RegistryObject<Item> TRAIL_STAIR = block(CABlocks.TRAIL_STAIR);
    public static final RegistryObject<Item> TRAIL_BUTTON = block(CABlocks.TRAIL_BUTTON);
    public static final RegistryObject<Item> TRAIL_PRESSURE_PLATE = block(CABlocks.TRAIL_PRESSURE_PLATE);
    public static final RegistryObject<Item> TRAIL_TILE = block(CABlocks.TRAIL_TILE);
    public static final RegistryObject<Item> CAERULA_HEART = REGISTRY.register("caerula_heart", CaerulaHeartItem::new);
    public static final RegistryObject<Item> COIN_OF_TRADE = REGISTRY.register("coin_of_trade", CoinOfTradeItem::new);
    public static final RegistryObject<Item> OCEAN_PEDUNCLE = REGISTRY.register("ocean_peduncle", OceanPeduncleItem::new);
    public static final RegistryObject<Item> ELITE_PEDUNCLE = REGISTRY.register("elite_peduncle", ElitePeduncleItem::new);
    public static final RegistryObject<Item> COOKED_PEDUNCLE = REGISTRY.register("cooked_peduncle", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(6).saturationMod(1f).meat().build())) {
        @Override
        public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemstack, net.minecraft.world.level.@NotNull Level world, net.minecraft.world.entity.@NotNull LivingEntity entity) {
            ItemStack retval = super.finishUsingItem(itemstack, world, entity);
            ModCapabilities.getSanityInjury(entity).heal(15);
            return retval;
        }
    });
    public static final RegistryObject<Item> OCEAN_ARROW = REGISTRY.register("ocean_arrow", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> FIRST_TO_TALK_SPAWN_EGG = REGISTRY.register("first_to_talk_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.FIRST_TO_TALK, -1, -1, new Item.Properties()));
    public static final RegistryObject<Item> REAPER_PET_SPAWN_EGG = REGISTRY.register("reaper_pet_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.REAPER_PET, -6750157, -3355444, new Item.Properties()));
    public static final RegistryObject<Item> OCEAN_EXTRACTOR = REGISTRY.register("ocean_extractor", OceanExtractorItem::new);
    public static final RegistryObject<Item> REAPER_EGG = block(CABlocks.REAPER_EGG);
    public static final RegistryObject<Item> OCEAN_OVARY = block(CABlocks.OCEAN_OVARY);
    public static final RegistryObject<Item> OCARINA = REGISTRY.register("ocarina", OcarinaItem::new);
    public static final RegistryObject<Item> BOILED_EGG = REGISTRY.register("boiled_egg", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(6).saturationMod(0.5f).alwaysEat().build())) {
        @Override
        public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemstack, net.minecraft.world.level.@NotNull Level world, net.minecraft.world.entity.@NotNull LivingEntity entity) {
            ItemStack retval = super.finishUsingItem(itemstack, world, entity);
            ModCapabilities.getSanityInjury(entity).heal(125);
            return retval;
        }
    });
    public static final RegistryObject<Item> FRIED_EGG = REGISTRY.register("fried_egg", FriedEggItem::new);
    public static final RegistryObject<Item> SEALEATHER_HELMET = REGISTRY.register("sealeather_helmet", SealeatherItem.Helmet::new);
    public static final RegistryObject<Item> SEALEATHER_CHESTPLATE = REGISTRY.register("sealeather_chestplate", SealeatherItem.Chestplate::new);
    public static final RegistryObject<Item> SEALEATHER_LEGGINGS = REGISTRY.register("sealeather_leggings", SealeatherItem.Leggings::new);
    public static final RegistryObject<Item> SEALEATHER_BOOTS = REGISTRY.register("sealeather_boots", SealeatherItem.Boots::new);
    public static final RegistryObject<Item> SONS_SPAWN_EGG = REGISTRY.register("sons_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.SONS, -16764058, -1, new Item.Properties()));
    public static final RegistryObject<Item> FLOATER_PROKARYOTE_SPAWN_EGG = REGISTRY.register("floater_prokaryote_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.FLOATER_PROKARYOTE, -16777012, -8483352, new Item.Properties()));
    public static final RegistryObject<Item> CHITIN_GOLEM_SPAWN_EGG = REGISTRY.register("chitin_golem_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.CHITIN_GOLEM, -3629966, -12951147, new Item.Properties()));
    public static final RegistryObject<Item> TRAIL_PUMPKING = block(CABlocks.TRAIL_PUMPKING);
    public static final RegistryObject<Item> MEGA_CHEST_SPAWN_EGG = REGISTRY.register("mega_chest_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.MEGA_CHEST, -1, -1, new Item.Properties()));
    public static final RegistryObject<Item> TRAILRITE_AXE = REGISTRY.register("trailrite_axe", TrailriteAxeItem::new);
    public static final RegistryObject<Item> BUCKET_RUNFISH = REGISTRY.register("bucket_runfish",
            () -> new MobBucketItem(CAEntities.RUN_FISH, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> TRAILRITE_SWORD = REGISTRY.register("trailrite_sword", TrailriteSwordItem::new);
    public static final RegistryObject<Item> BUCKET_SLIDER = REGISTRY.register("bucket_slider",
            () -> new MobBucketItem(CAEntities.SLIDER_FISH, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> TRAIL_DEBRIS = block(CABlocks.TRAIL_DEBRIS);
    public static final RegistryObject<Item> TRAILRITE_BLOCK = block(CABlocks.TRAILRITE_BLOCK);
    public static final RegistryObject<Item> APOSTLE_PROKARYOTE_SPAWN_EGG = REGISTRY.register("apostle_prokaryote_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.APOSTLE_PROKARYOTE, -15191057, -3933705, new Item.Properties()));
    public static final RegistryObject<Item> BUCKET_CHISELER = REGISTRY.register("bucket_chiseler",
            () -> new MobBucketItem(CAEntities.CHISELER_FISH, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> BUCKET_FLOATER = REGISTRY.register("bucket_floater",
            () -> new MobBucketItem(CAEntities.FLOATER_PROKARYOTE, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> BUCKET_BONEFISH = REGISTRY.register("bucket_bonefish",
            () -> new MobBucketItem(CAEntities.BONE_FISH, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> BUCKET_COLLECTOR = REGISTRY.register("bucket_collector",
            () -> new MobBucketItem(CAEntities.COLLECTOR_PROKARYOTE, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> WAVECLEAVER = REGISTRY.register("wavecleaver", WavecleaverItem::new);
    public static final RegistryObject<Item> TRAIL_WALL = block(CABlocks.TRAIL_WALL);
    public static final RegistryObject<Item> RED_OVARY = block(CABlocks.RED_OVARY);
    public static final RegistryObject<Item> ACCUMULATOR_PROKARYOTE_SPAWN_EGG = REGISTRY.register("accumulator_prokaryote_spawn_egg",
            () -> new ForgeSpawnEggItem(CAEntities.ACCUMULATOR_PROKARYOTE, -14863367, -15721602, new Item.Properties()));
    public static final RegistryObject<Item> TIDEWAY_CRADLE = REGISTRY.register(Objects.requireNonNull(CABlocks.TIDEWAY_CRADLE.getId()).getPath(), () -> new TidewayCradleDisplayItem(CABlocks.TIDEWAY_CRADLE.get(), new Item.Properties()));
    public static final RegistryObject<Item> SMELLY_HEMOSTATIC = REGISTRY.register("smelly_hemostatic", SmellyHemostaticItem::new);
    public static final RegistryObject<Item> PATH_INAUGURATOR = REGISTRY.register("path_inaugurator", PathInauguratorItem::new);
    public static final RegistryObject<Item> COOKED_FAKEEGG = REGISTRY.register("cooked_fakeegg", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(4).saturationMod(0.7f).alwaysEat().build())));
    public static final RegistryObject<Item> COLLECTOR_MEAT = REGISTRY.register("collector_meat", CollectorMeatItem::new);
    public static final RegistryObject<Item> COOKED_COLLECTOR = REGISTRY.register("cooked_collector", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(6).saturationMod(0.5f).meat().build())));
    public static final RegistryObject<Item> CLAW = REGISTRY.register("claw", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(2).saturationMod(0f).build())));
    public static final RegistryObject<Item> COOKED_CLAW = REGISTRY.register("cooked_claw", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food(new FoodProperties.Builder().nutrition(7).saturationMod(0.5f).meat().effect(() -> new MobEffectInstance(CAMobEffects.ADD_ATTACK_PERCLY.get(), 600, 1), 1.0F).build())));
    public static final RegistryObject<Item> SEABORN_SOUP = REGISTRY.register("seaborn_soup", () -> new BowlFoodItem(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON).food(new FoodProperties.Builder().nutrition(12).saturationMod(0.75f).effect(() -> new MobEffectInstance(CAMobEffects.SANITY_IMMUE.get(), 400, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 200, 1), 1.0F).build())));
    public static final RegistryObject<Item> REPELLER_SHELL = REGISTRY.register("repeller_shell",
            () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> FEEDER_PROKARYOTE_SPAWN_EGG = REGISTRY.register("feeder_prokaryote_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.FEEDER_PROKARYOTE, -12698628, -6754314, new Item.Properties()));
    public static final RegistryObject<Item> CHEST_FISH_SPAWN_EGG = REGISTRY.register("chest_fish_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.CHEST_FISH, -8882056, -858389, new Item.Properties()));
    public static final RegistryObject<Item> SPIKE_CHEST_SPAWN_EGG = REGISTRY.register("spike_chest_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.SPIKE_CHEST, -9082259, -533095, new Item.Properties()));
    public static final RegistryObject<Item> HIGHMORE_SCYTHE = REGISTRY.register("highmore_scythe", HighmoreScytheItem::new);
    public static final RegistryObject<Item> SALTWIND_SANDSTONE = block(CABlocks.SALTWIND_SANDSTONE);
    public static final RegistryObject<Item> CHISELED_SALTWIND_SANDSTONE = block(CABlocks.CHISELED_SALTWIND_SANDSTONE);
    public static final RegistryObject<Item> SMOOTH_SALTWIND_SANDATONE = block(CABlocks.SMOOTH_SALTWIND_SANDATONE);
    public static final RegistryObject<Item> SALTWIND_SAND_STAIR = block(CABlocks.SALTWIND_SAND_STAIR);
    public static final RegistryObject<Item> SALTWIND_SAND_SLAB = block(CABlocks.SALTWIND_SAND_SLAB);
    public static final RegistryObject<Item> SMOOTH_SALTWIND_SAND_STAIR = block(CABlocks.SMOOTH_SALTWIND_SAND_STAIR);
    public static final RegistryObject<Item> SMOOTH_SALTWIND_SAND_SLAB = block(CABlocks.SMOOTH_SALTWIND_SAND_SLAB);
    public static final RegistryObject<Item> SALTWIND_SAND_WALL = block(CABlocks.SALTWIND_SAND_WALL);
    public static final RegistryObject<Item> CHIESELED_SALTWIND_SAND_WALL = block(CABlocks.CHIESELED_SALTWIND_SAND_WALL);
    public static final RegistryObject<Item> SMOOTH_SALTWIND_SAND_WALL = block(CABlocks.SMOOTH_SALTWIND_SAND_WALL);
    public static final RegistryObject<Item> TIDE_BISHOP_CORE = block(CABlocks.TIDE_BISHOP_CORE);
    public static final RegistryObject<Item> ADV_ITEM = REGISTRY.register("adv_item", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> HETEROPIC_BLOCK = block(CABlocks.HETEROPIC_BLOCK);
    public static final RegistryObject<Item> TRAIL_LOG = block(CABlocks.TRAIL_LOG);
    public static final RegistryObject<Item> TRAIL_LEAVE = block(CABlocks.TRAIL_LEAVE);
    public static final RegistryObject<Item> TRAIL_PLANK = block(CABlocks.TRAIL_PLANK);
    public static final RegistryObject<Item> TRAIL_PLANKS_FENCE = block(CABlocks.TRAIL_PLANKS_FENCE);
    public static final RegistryObject<Item> TRAIL_PLANK_FENCEDOOR = block(CABlocks.TRAIL_PLANK_FENCEDOOR);
    public static final RegistryObject<Item> TRAIL_PLANK_SLAB = block(CABlocks.TRAIL_PLANK_SLAB);
    public static final RegistryObject<Item> TRAIL_PLANK_STAIR = block(CABlocks.TRAIL_PLANK_STAIR);
    public static final RegistryObject<Item> TRAIL_PLANK_BUTTON = block(CABlocks.TRAIL_PLANK_BUTTON);
    public static final RegistryObject<Item> TRAIL_PLANK_PRESSURE_PLATE = block(CABlocks.TRAIL_PLANK_PRESSURE_PLATE);
    public static final RegistryObject<Item> STRIPPED_TRAIL_LOG = block(CABlocks.STRIPPED_TRAIL_LOG);
    public static final RegistryObject<Item> TIDE_WAND = REGISTRY.register("tide_wand", TideWandItem::new);
    public static final RegistryObject<Item> TRAIL_PLANK_DOOR = doubleBlock(CABlocks.TRAIL_PLANK_DOOR);
    public static final RegistryObject<Item> TRAIL_MOP = REGISTRY.register("trail_mop", TrailMopItem::new);
    public static final RegistryObject<Item> BLOODY_RECORD = REGISTRY.register("bloody_record", () -> new RecordItem(9, CASounds.BLOODY_WOLF, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 3080));
    public static final RegistryObject<Item> SALTSAND = block(CABlocks.SALTSAND);
    public static final RegistryObject<Item> SKADI_SPAWN_EGG = REGISTRY.register("skadi_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.SKADI, -13289929, -4141862, new Item.Properties()));
    public static final RegistryObject<Item> SEA_TRAIL_BURNT = block(CABlocks.SEA_TRAIL_BURNT);
    public static final RegistryObject<Item> SEA_TRAIL_BURNT_SOLID = block(CABlocks.SEA_TRAIL_BURNT_SOLID);
    public static final RegistryObject<Item> REDSTONIUM = REGISTRY.register("redstonium", () -> new Item(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> CHESTMEGA_SPAWNER = REGISTRY.register(Objects.requireNonNull(CABlocks.CHESTMEGA_SPAWNER.getId()).getPath(),
            () -> new ChestmegaSpawnerDisplayItem(CABlocks.CHESTMEGA_SPAWNER.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRACKED_TRAIL_BRICK = block(CABlocks.CRACKED_TRAIL_BRICK);
    public static final RegistryObject<Item> SALTWIND_COLUMN = block(CABlocks.SALTWIND_COLUMN);
    public static final RegistryObject<Item> BLOCK_CHESTFISH = block(CABlocks.BLOCK_CHESTFISH);
    public static final RegistryObject<Item> LANTERN_JUDGEMENT = REGISTRY.register("lantern_judgement", LanternJudgementItem::new);
    public static final RegistryObject<Item> WHITE_CHITIN_BLOCK = block(CABlocks.WHITE_CHITIN_BLOCK);
    public static final RegistryObject<Item> DEPOSITER_PROKARYOTE_SPAWN_EGG = REGISTRY.register("depositer_prokaryote_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.DEPOSITER_PROKARYOTE, -1381662, -10523951, new Item.Properties()));
    public static final RegistryObject<Item> SALTWIND_BRICK = block(CABlocks.SALTWIND_BRICK);
    public static final RegistryObject<Item> SALTWIND_SMOOTH_BRICK = block(CABlocks.SALTWIND_SMOOTH_BRICK);
    public static final RegistryObject<Item> SALTWIND_BRICK_SLAB = block(CABlocks.SALTWIND_BRICK_SLAB);
    public static final RegistryObject<Item> SALTWIND_SMOOTH_SLAB = block(CABlocks.SALTWIND_SMOOTH_SLAB);
    public static final RegistryObject<Item> SALTWIND_STAIR = block(CABlocks.SALTWIND_STAIR);
    public static final RegistryObject<ChitinComplexItem> COMPLEXCHITIN_ARMOR_HELMET = REGISTRY.register("complexchitin_armor_helmet", () -> new ChitinComplexItem(ArmorItem.Type.HELMET, new Item.Properties().fireResistant()));
    public static final RegistryObject<ChitinComplexItem> COMPLEXCHITIN_ARMOR_CHESTPLATE = REGISTRY.register("complexchitin_armor_chestplate", () -> new ChitinComplexItem(ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant()));
    public static final RegistryObject<ChitinComplexItem> COMPLEXCHITIN_ARMOR_LEGGINGS = REGISTRY.register("complexchitin_armor_leggings", () -> new ChitinComplexItem(ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant()));
    public static final RegistryObject<ChitinComplexItem> COMPLEXCHITIN_ARMOR_BOOTS = REGISTRY.register("complexchitin_armor_boots", () -> new ChitinComplexItem(ArmorItem.Type.BOOTS, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> TRAIL_WOOD_TRAPDOOR = block(CABlocks.TRAIL_WOOD_TRAPDOOR);
    public static final RegistryObject<Item> TRAIL_STONE = block(CABlocks.TRAIL_STONE);
    public static final RegistryObject<Item> TRAIL_PULSE = block(CABlocks.TRAIL_PULSE);
    public static final RegistryObject<Item> OCEANIZED_VILLAGER_SPAWN_EGG = REGISTRY.register("oceanized_villager_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_VILLAGER, -12431264, -5598838, new Item.Properties()));
    public static final RegistryObject<Item> OCEANIZED_VINDICATOR_SPAWN_EGG = REGISTRY.register("oceanized_vindicator_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_VINDICATOR, -6907484, -16632259, new Item.Properties()));
    public static final RegistryObject<Item> OCEANIZED_PILLAGER_SPAWN_EGG = REGISTRY.register("oceanized_pillager_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_PILLAGER, -15913406, -6907486, new Item.Properties()));
    public static final RegistryObject<Item> UNAMBIGUOUS_DIRECTION = REGISTRY.register("unambiguous_direction", UnambiguousDirectionItem::new);
    public static final RegistryObject<Item> OCEANIZED_PIG_SPAWN_EGG = REGISTRY.register("oceanized_pig_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_PIG, -4088402, -6327133, new Item.Properties()));
    public static final RegistryObject<Item> TRAIL_MUSHROOM = block(CABlocks.TRAIL_MUSHROOM);
    public static final RegistryObject<Item> OCEANIZED_COW_SPAWN_EGG = REGISTRY.register("oceanized_cow_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_COW, -13158343, -6970193, new Item.Properties()));
    public static final RegistryObject<Item> TRAIL_APPLE = REGISTRY.register("trail_apple", TrailAppleItem::new);
    public static final RegistryObject<Item> OCEANIZED_SHEEP_SPAWN_EGG = REGISTRY.register("oceanized_sheep_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_SHEEP, -2432793, -3028258, new Item.Properties()));
    public static final RegistryObject<Item> VIVIPAROUS_LILY = REGISTRY.register(Objects.requireNonNull(CABlocks.VIVIPAROUS_LILY.getId()).getPath(), () -> new ViviparousLilyDisplayItem(CABlocks.VIVIPAROUS_LILY.get(), new Item.Properties()));
    public static final RegistryObject<Item> HUGE_LILY = REGISTRY.register(Objects.requireNonNull(CABlocks.HUGE_LILY.getId()).getPath(), () -> new HugeLilyDisplayItem(CABlocks.HUGE_LILY.get(), new Item.Properties()));
    public static final RegistryObject<Item> BOMB_COPPER = block(CABlocks.BOMB_COPPER);
    public static final RegistryObject<Item> OCEANIZED_HORSE_SPAWN_EGG = REGISTRY.register("oceanized_horse_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_HORSE, -10197897, -8355686, new Item.Properties()));
    public static final RegistryObject<Item> PHLOEM_BLOCK = block(CABlocks.PHLOEM_BLOCK);
    public static final RegistryObject<Item> FIBRE_BLOCK = block(CABlocks.FIBRE_BLOCK);
    public static final RegistryObject<Item> COOKED_FIBRE_BLOCK = block(CABlocks.COOKED_FIBRE_BLOCK);
    public static final RegistryObject<Item> UNRIPE_YEARNING = REGISTRY.register("unripe_yearning", UnripeYearningItem::new);
    public static final RegistryObject<Item> CIRCULAR_SAW = REGISTRY.register("circular_saw", CircularSawItem::new);
    public static final RegistryObject<Item> OCEANIZED_PIGLIN_SPAWN_EGG = REGISTRY.register("oceanized_piglin_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_PIGLIN, -10136240, -3168112, new Item.Properties()));
    public static final RegistryObject<Item> OCEANIZED_BRUTE_SPAWN_EGG = REGISTRY.register("oceanized_brute_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_BRUTE, -1, -1, new Item.Properties()));
    public static final RegistryObject<Item> UNDERTIDE_TABLE = block(CABlocks.UNDERTIDE_TABLE);
    public static final RegistryObject<Item> UNDERTIDE_SPAWN = block(CABlocks.UNDERTIDE_SPAWN);
    public static final RegistryObject<Item> OCEANIZED_SPIDER_SPAWN_EGG = REGISTRY.register("oceanized_spider_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_SPIDER, -13552063, -16239700, new Item.Properties()));
    public static final RegistryObject<Item> TRAIL_GOLDEN_APPLE = REGISTRY.register("trail_golden_apple", TrailGoldenAppleItem::new);
    public static final RegistryObject<Item> ENCHANTED_TRAIL_GOLDEN_APPLE = REGISTRY.register("enchanted_trail_golden_apple", EnchantedTrailGoldenAppleItem::new);
    public static final RegistryObject<Item> RULER = REGISTRY.register("ruler", RulerItem::new);
    public static final RegistryObject<Item> OCEANIZED_ENDERMAN_SPAWN_EGG = REGISTRY.register("oceanized_enderman_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_ENDERMAN, -13878972, -10763556, new Item.Properties()));
    public static final RegistryObject<Item> OCEANIZED_WOLF_SPAWN_EGG = REGISTRY.register("oceanized_wolf_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_WOLF, -8420214, -11250088, new Item.Properties()));
    public static final RegistryObject<Item> OCEANIZED_DOG_SPAWN_EGG = REGISTRY.register("oceanized_dog_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_DOG, -8420214, -11250088, new Item.Properties()));
    public static final RegistryObject<Item> TULIP_MEDCINE = REGISTRY.register("tulip_medcine", TulipMedcineItem::new);
    public static final RegistryObject<Item> IMMUNOSUPPRESSOR = REGISTRY.register("immunosuppressor", ImmunosuppressorItem::new);
    public static final RegistryObject<Item> OIL_AND_CREAM = REGISTRY.register("oil_and_cream", OilAndCreamItem::new);
    public static final RegistryObject<Item> OCEANIZED_RAVAGER_SPAWN_EGG = REGISTRY.register("oceanized_ravager_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_RAVAGER, -10919578, -6771789, new Item.Properties()));
    public static final RegistryObject<Item> OCEANIZED_WITCH_SPAWN_EGG = REGISTRY.register("oceanized_witch_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_WITCH, -16176318, -12024617, new Item.Properties()));
    public static final RegistryObject<Item> IZUMIK_OFFSPRING_SPAWN_EGG = REGISTRY.register("izumik_offspring_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.IZUMIK_OFFSPRING, -592138, -405009, new Item.Properties()));
    public static final RegistryObject<Item> DEEP_SEAGRASS = block(CABlocks.DEEP_SEAGRASS);
    public static final RegistryObject<Item> SEA_TRAIL_STOP = block(CABlocks.SEA_TRAIL_STOP);
    public static final RegistryObject<Item> QUINTUS_SPAWNEGG = REGISTRY.register("quintus_spawnegg", () -> new ForgeSpawnEggItem(CAEntities.BISHOP_FISH, -1, -1, new Item.Properties().stacksTo(64).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> PATH_SHAPER_SPAWNEGG = REGISTRY.register("path_shaper_spawnegg", PathShaperSpawneggItem::new);
    public static final RegistryObject<Item> TIDE_BISHOP_SPAWNEGG = REGISTRY.register("tide_bishop_spawnegg", TideBishopSpawneggItem::new);
    public static final RegistryObject<Item> DEATHREPELLER_SPAWNEGG = REGISTRY.register("deathrepeller_spawnegg", () -> new ForgeSpawnEggItem(CAEntities.TIDE_DEATHREPELLER, -1, -1, new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> HIGHMORE_SPAWNEGG = REGISTRY.register("highmore_spawnegg", () -> new ForgeSpawnEggItem(CAEntities.HIGHMORE, -1, -1, new Item.Properties().stacksTo(64).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> COLOURFULL_JELLY = REGISTRY.register("colourfull_jelly", ColourfullJellyItem::new);
    public static final RegistryObject<Item> CHITIN_SHIELD = REGISTRY.register("chitin_shield", ChitinShieldItem::new);
    public static final RegistryObject<Item> COMPLEX_CHITIN_SHIELD = REGISTRY.register("complex_chitin_shield", ComplexChitinShieldItem::new);
    public static final RegistryObject<Item> TRAILRITE_SHIELD = REGISTRY.register("trailrite_shield", TrailriteShieldItem::new);
    public static final RegistryObject<Item> HIGHMORE_SPAWNBLOCK = REGISTRY.register(Objects.requireNonNull(CABlocks.HIGHMORE_SPAWNBLOCK.getId()).getPath(),
            () -> new HighmoreSpawnblockDisplayItem(CABlocks.HIGHMORE_SPAWNBLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRISIS_TABLE = REGISTRY.register(Objects.requireNonNull(CABlocks.CRISIS_TABLE.getId()).getPath(), () -> new CrisisTableDisplayItem(CABlocks.CRISIS_TABLE.get(), new Item.Properties()));
    public static final RegistryObject<Item> OCEANIZED_EVOKER_SPAWN_EGG = REGISTRY.register("oceanized_evoker_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_EVOKER, -6907486, -10896428, new Item.Properties()));
    public static final RegistryObject<Item> IDV_IZUMIK = REGISTRY.register("idv_izumik", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> FRUIT_JELLY = REGISTRY.register("fruit_jelly", FruitJellyItem::new);
    public static final RegistryObject<Item> JUNIOR_WARRIOR_PRIEST_SPAWN_EGG = REGISTRY.register("junior_warrior_priest_spawn_egg",
            () -> new ForgeSpawnEggItem(CAEntities.JUNIOR_WARRIOR_PRIEST, -11776947, -13620166, new Item.Properties()));
    public static final RegistryObject<Item> WARRIOR_PRIEST_SPAWN_EGG = REGISTRY.register("warrior_priest_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.WARRIOR_PRIEST, -11776947, -7122553, new Item.Properties()));
    public static final RegistryObject<Item> CORRECTIONAL_PHALANXY_INFANTRY_SPAWN_EGG = REGISTRY.register("correctional_phalanxy_infantry_spawn_egg",
            () -> new ForgeSpawnEggItem(CAEntities.CORRECTIONAL_PHALANXY_INFANTRY, -9473918, -3028012, new Item.Properties()));
    public static final RegistryObject<Item> CORRECTIONAL_PHALAX_VANGUARD_SPAWN_EGG = REGISTRY.register("correctional_phalax_vanguard_spawn_egg",
            () -> new ForgeSpawnEggItem(CAEntities.CORRECTIONAL_PHALAX_VANGUARD, -9473918, -7405225, new Item.Properties()));
    public static final RegistryObject<Item> FAX = block(CABlocks.FAX);
    public static final RegistryObject<Item> TRIBUNAL_HEALER_SPAWN_EGG = REGISTRY.register("tribunal_healer_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.TRIBUNAL_HEALER, -11776947, -5232043, new Item.Properties()));
    public static final RegistryObject<Item> INTERPHONE = REGISTRY.register("interphone", InterphoneItem::new);
    public static final RegistryObject<Item> BANNED_ITEM = REGISTRY.register("banned_item", BannedItemItem::new);
    public static final RegistryObject<Item> THE_ABANDONED_SPAWN_EGG = REGISTRY.register("the_abandoned_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.THE_ABANDONED, -15836717, -12171706, new Item.Properties()));
    public static final RegistryObject<Item> UNFINISHED_BEAUTY = REGISTRY.register("unfinished_beauty", UninishedBeautyItem::new);
    public static final RegistryObject<Item> OCEAN_CAVAIR = REGISTRY.register("ocean_cavair", OceanCavairItem::new);
    public static final RegistryObject<Item> ELITE_CAVAIR = REGISTRY.register("elite_cavair", EliteCavairItem::new);
    public static final RegistryObject<Item> MARTUS_SPAWNEGG = REGISTRY.register("martus_spawnegg", () -> new ForgeSpawnEggItem(CAEntities.MARTUS, -1, -1, new Item.Properties().stacksTo(64).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> KEBAB_RAW = REGISTRY.register("kebab_raw", KebabRawItem::new);
    public static final RegistryObject<Item> KEBAB_COOKED = REGISTRY.register("kebab_cooked", KebabCookedItem::new);
    public static final RegistryObject<Item> GUNMU_SPAWN_EGG = REGISTRY.register("gunmu_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.GUNMU, -1, -1, new Item.Properties()));
    public static final RegistryObject<Item> NETHERSEA_PUMPKIN_PIE = REGISTRY.register("nethersea_pumpkin_pie", NetherseaPumpkinPieItem::new);
    public static final RegistryObject<Item> NETHERSEA_STEW = REGISTRY.register("nethersea_stew", NetherseaStewItem::new);
    public static final RegistryObject<Item> RECORD_WHISPER = REGISTRY.register("record_whisper", () -> new RecordItem(6, CASounds.WHISPER_ILLUSION, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 4480));
    public static final RegistryObject<Item> OCEANIZED_WARDEN_SPAWNEGG = REGISTRY.register("oceanized_warden_spawnegg", OceanizedWardenSpawneggItem::new);
    public static final RegistryObject<Item> MUTAGENISIS_CAPSULE = REGISTRY.register("mutagenisis_capsule", MutagenisisCapsuleItem::new);
    public static final RegistryObject<Item> ECHO_JELLY = REGISTRY.register("echo_jelly", EchoJellyItem::new);
    public static final RegistryObject<Item> APOCALYPSE = REGISTRY.register("apocalypse", ApocalypseItem::new);
    public static final RegistryObject<Item> OCEANIZED_CAT_SPAWN_EGG = REGISTRY.register("oceanized_cat_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_CAT, -3491156, -7897500, new Item.Properties()));
    public static final RegistryObject<Item> SUPER_BIG_CAT_SPAWN_EGG = REGISTRY.register("super_big_cat_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.SUPER_BIG_CAT, -1, -1, new Item.Properties()));
    public static final RegistryObject<Item> HAND_ANCHOR = REGISTRY.register("hand_anchor", HandAnchorItem::new);
    public static final RegistryObject<Item> COMPLEX_CHITIN_GOLEM_SPAWN_EGG = REGISTRY.register("complex_chitin_golem_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.COMPLEX_CHITIN_GOLEM, -2631220, -9079451, new Item.Properties()));
    public static final RegistryObject<Item> HIGHMORE_SPAWNING_BLOCK = REGISTRY.register(Objects.requireNonNull(CABlocks.HIGHMORE_SPAWNING_BLOCK.getId()).getPath(),
            () -> new HighmoreSpawningBlockDisplayItem(CABlocks.HIGHMORE_SPAWNING_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> TIDELINKED_SHIELD = REGISTRY.register("tidelinked_shield", TidelinkedShieldItem::new);
    public static final RegistryObject<Item> TIDELINKED_WAND = REGISTRY.register("tidelinked_wand", TidelinkedWandItem::new);
    public static final RegistryObject<Item> GOLDEN_CHALISE = block(CABlocks.GOLDEN_CHALISE);
    public static final RegistryObject<Item> EMERGENCY_AID_BUILDING = block(CABlocks.EMERGENCY_AID_BUILDING);
    public static final RegistryObject<Item> EMERGENCY_AID_BUILDING_SALVIENTO = block(CABlocks.EMERGENCY_AID_BUILDING_SALVIENTO);
    public static final RegistryObject<Item> MIZUKI_STATUE = REGISTRY.register(Objects.requireNonNull(CABlocks.MIZUKI_STATUE.getId()).getPath(), () -> new MizukiStatueDisplayItem(CABlocks.MIZUKI_STATUE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RECORD_ENDOSPORE = REGISTRY.register("record_endospore", () -> new RecordItem(0, CASounds.ENDOSPORE, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 3940));
    public static final RegistryObject<Item> NUCLEIC_MALEFICENT_SPAWN_EGG = REGISTRY.register("nucleic_maleficent_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.NUCLEIC_MALEFICENT, -15062866, -1, new Item.Properties()));
    public static final RegistryObject<Item> SHELL_OF_STONECUTTER = REGISTRY.register("shell_of_stonecutter", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> POCKET_SEA_DOLL = REGISTRY.register(Objects.requireNonNull(CABlocks.POCKET_SEA_DOLL.getId()).getPath(), () -> new PocketSeaDollDisplayItem(CABlocks.POCKET_SEA_DOLL.get(), new Item.Properties()));
    public static final RegistryObject<Item> SWARMCALLER_DOLL = REGISTRY.register(Objects.requireNonNull(CABlocks.SWARMCALLER_DOLL.getId()).getPath(), () -> new SwarmcallerDollDisplayItem(CABlocks.SWARMCALLER_DOLL.get(), new Item.Properties()));
    public static final RegistryObject<Item> STONECUTTER_DOLL = REGISTRY.register(Objects.requireNonNull(CABlocks.STONECUTTER_DOLL.getId()).getPath(), () -> new StonecutterDollDisplayItem(CABlocks.STONECUTTER_DOLL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MOIST_STAR = REGISTRY.register("moist_star", MoistStarItem::new);
    public static final RegistryObject<Item> NETHERSEA_SOUL_SAND = block(CABlocks.NETHERSEA_SOUL_SAND);
    public static final RegistryObject<Item> OCEANIZED_WITHER_SPAWNEGG = REGISTRY.register("oceanized_wither_spawnegg", OceanziedWitherSpawneggItem::new);
    public static final RegistryObject<Item> CAERULA_BOOK_SHELF = block(CABlocks.CAERULA_BOOK_SHELF);
    public static final RegistryObject<Item> RECORD_PATH_AHEAD = REGISTRY.register("record_path_ahead", () -> new RecordItem(6, CASounds.ONLY_PATH_AHEAD, new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE), 3040));
    public static final RegistryObject<Item> ABANDONED_SULPTURE = REGISTRY.register(Objects.requireNonNull(CABlocks.ABANDONED_SULPTURE.getId()).getPath(),
            () -> new AbandonedSulptureDisplayItem(CABlocks.ABANDONED_SULPTURE.get(), new Item.Properties()));
    public static final RegistryObject<Item> NETHERSEA_ICECREAM = REGISTRY.register("nethersea_icecream", NetherseaIcecreamItem::new);
    public static final RegistryObject<Item> GOODSQUIDS_SERVERER = REGISTRY.register("goodsquids_serverer", GoodsquidsServererItem::new);
    public static final RegistryObject<Item> NOURISHED_APPLE_PIE = REGISTRY.register("nourished_apple_pie", NourishedApplePieItem::new);
    public static final RegistryObject<Item> LAST_KNIGHT_SPAWNER = REGISTRY.register("last_knight_spawner", LastKnightSpawnerItem::new);
    public static final RegistryObject<Item> ROCINANTE_SPAWN_EGG = REGISTRY.register("rocinante_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.ROCINANTE, -13027013, -9471010, new Item.Properties()));
    public static final RegistryObject<Item> APOCATA_SPAWN_EGG = REGISTRY.register("apocata_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.APOCATA, -26164, -13421773, new Item.Properties()));
    public static final RegistryObject<Item> IRON_SWORD_OF_KNIGHT_CORPUS = REGISTRY.register("iron_sword_of_knight_corpus", SwordOfLastKnightCorpusItem::new);
    public static final RegistryObject<Item> LONG_SWORD_OF_KNIGHT_CORPUS = REGISTRY.register("long_sword_of_knight_corpus", LongSwordOfKnightCorpusItem::new);
    public static final RegistryObject<Item> MARTUS_BOOK = REGISTRY.register("martus_book", MartusBookItem::new);
    public static final RegistryObject<Item> OCEANIZED_FOX_SPAWN_EGG = REGISTRY.register("oceanized_fox_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_FOX, -1968903, -6433566, new Item.Properties()));
    public static final RegistryObject<Item> TIDUTANT_EXCRESCENCE_SPAWN_EGG = REGISTRY.register("tidutant_excrescence_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.TIDUTANT_EXCRESCENCE, -10392431, -3177597, new Item.Properties()));
    public static final RegistryObject<Item> BREATH_OF_TIDE = REGISTRY.register("breath_of_tide", BreathOfTideItem::new);
    public static final RegistryObject<Item> OCEANIZED_POLAR_BEAR_SPAWN_EGG = REGISTRY.register("oceanized_polar_bear_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_POLAR_BEAR, -3614740, -12494209, new Item.Properties()));
    public static final RegistryObject<Item> FLUORE_ICECREAM = REGISTRY.register("fluore_icecream", FloureIcecreamItem::new);
    public static final RegistryObject<Item> OCEANGLASS_CUP = REGISTRY.register("oceanglass_cup", OceanglassCupItem::new);
    public static final RegistryObject<Item> A_CUP_OF_WATER = REGISTRY.register("a_cup_of_water", ACupOfWaterItem::new);
    public static final RegistryObject<Item> DEEP_SEAGRASS_JUICE = REGISTRY.register("deep_seagrass_juice", DeepSeagrassJuiceItem::new);
    public static final RegistryObject<Item> CARAMEL_SEAGRASS_JUICE = REGISTRY.register("caramel_seagrass_juice", CaramelSeagrassJuiceItem::new);
    public static final RegistryObject<Item> COLOURFUL_APPLE_JUICE = REGISTRY.register("colourful_apple_juice", ColourfulAppleJuiceItem::new);
    public static final RegistryObject<Item> NETHERSEA_COFFEE = REGISTRY.register("nethersea_coffee", NetherseaCoffeItem::new);
    public static final RegistryObject<Item> NETHERSEA_STIMUTANT = REGISTRY.register("nethersea_stimutant", NetherseaStimutantItem::new);
    public static final RegistryObject<Item> FLUORE_BERRY_JUICE = REGISTRY.register("fluore_berry_juice", FluoreBerryJuiceItem::new);
    public static final RegistryObject<Item> TIDUTANT_ROCK_SPIDER_SPAWN_EGG = REGISTRY.register("tidutant_rock_spider_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.TIDUTANT_ROCK_SPIDER, -10267053, -9455873, new Item.Properties()));
    public static final RegistryObject<Item> ENDSPEAKER_SPAWNEGG = REGISTRY.register("endspeaker_spawnegg", EndspeakerSpawneggItem::new);
    public static final RegistryObject<Item> LINGERING_FRACTAL_SPAWN_EGG = REGISTRY.register("lingering_fractal_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.LINGERING_FRACTAL, -15986675, -14861545, new Item.Properties()));
    public static final RegistryObject<Item> ENDSPEAKER_NEST = block(CABlocks.ENDSPEAKER_NEST);
    public static final RegistryObject<Item> ITEM_HELPER = REGISTRY.register("item_helper", LittleHelperItem::new);
    public static final RegistryObject<Item> ITEM_HELPER_AL_1S = REGISTRY.register("item_helper_al_1s", ItemHelperAl1sItem::new);
    public static final RegistryObject<Item> DICTATIONLESS_CHAPTER = REGISTRY.register("dictationless_chapter", DictationlessChapterItem::new);
    public static final RegistryObject<Item> LANC_XIAO = REGISTRY.register("lanc_xiao", LancXiaoItem::new);
    public static final RegistryObject<Item> APOCATA_SWORD = REGISTRY.register("apocata_sword", ApocataSwordItem::new);
    public static final RegistryObject<Item> CHITIN_INGOT = REGISTRY.register("chitin_ingot", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> ULPIANS_SPAWN_EGG = REGISTRY.register("ulpians_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.ULPIANS, -12958887, -1121571, new Item.Properties()));
    public static final RegistryObject<Item> GLADIIA_SPAWN_EGG = REGISTRY.register("gladiia_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.GLADIIA, -13419440, -9077307, new Item.Properties()));
    public static final RegistryObject<Item> OPERATION_TABLE = block(CABlocks.OPERATION_TABLE);
    public static final RegistryObject<Item> CENTRIFUGER = REGISTRY.register(Objects.requireNonNull(CABlocks.CENTRIFUGER.getId()).getPath(), () -> new CentrifugerDisplayItem(CABlocks.CENTRIFUGER.get(), new Item.Properties()));
    public static final RegistryObject<Item> PERSONNEL_TRANSPORTER = REGISTRY.register("personnel_transporter", PersonnelTransporterItem::new);
    public static final RegistryObject<Item> INJECTOR = block(CABlocks.INJECTOR);
    public static final RegistryObject<Item> DICTATION_CHAPTER = REGISTRY.register("dictation_chapter", DictationChapterItem::new);
    public static final RegistryObject<Item> SEALEATHER_CHITIN_HELMET = REGISTRY.register("sealeather_chitin_helmet", SealeatherChitinItem.Helmet::new);
    public static final RegistryObject<Item> SEALEATHER_CHITIN_CHESTPLATE = REGISTRY.register("sealeather_chitin_chestplate", SealeatherChitinItem.Chestplate::new);
    public static final RegistryObject<Item> SEALEATHER_CHITIN_LEGGINGS = REGISTRY.register("sealeather_chitin_leggings", SealeatherChitinItem.Leggings::new);
    public static final RegistryObject<Item> SEALEATHER_CHITIN_BOOTS = REGISTRY.register("sealeather_chitin_boots", SealeatherChitinItem.Boots::new);
    public static final RegistryObject<Item> NETHERSEA_WOOD = block(CABlocks.NETHERSEA_WOOD);
    public static final RegistryObject<Item> STRIPPED_NETHERSEA_WOOD = block(CABlocks.STRIPPED_NETHERSEA_WOOD);
    public static final RegistryObject<Item> SPECTER_SPAWN_EGG = REGISTRY.register("specter_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.SPECTER, -9340526, -5326397, new Item.Properties()));
    public static final RegistryObject<Item> AEGIR_LANCET = REGISTRY.register("aegir_lancet", AegirLancetItem::new);
    public static final RegistryObject<Item> BROKEN_SEA = REGISTRY.register("broken_sea", BrokenSeaItem::new);
    public static final RegistryObject<Item> IRENE_SPAWN_EGG = REGISTRY.register("irene_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.IRENE, -5065798, -4494458, new Item.Properties()));
    public static final RegistryObject<Item> TIDE_CHIMERA_SPAWN_EGG = REGISTRY.register("tide_chimera_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.TIDE_CHIMERA, -16762940, -10496001, new Item.Properties()));
    public static final RegistryObject<Item> AEGIR_SWORD = REGISTRY.register("aegir_sword", AegirSwordItem::new);
    public static final RegistryObject<Item> SKADI_SWORD = REGISTRY.register("skadi_sword", SkadiSwordItem::new);
    public static final RegistryObject<Item> CORRUPTED_HEART_SPAWNER = REGISTRY.register("corrupted_heart_spawner", CorruptedHeartSpawnerItem::new);
    public static final RegistryObject<Item> INCANDESCENT_ANIMA = REGISTRY.register("incandescent_anima", IncandescentAnimaItem::new);
    public static final RegistryObject<Item> ISHARMLA_REMAIN = block(CABlocks.ISHARMLA_REMAIN);
    public static final RegistryObject<Item> RECORD_UNDERTIDES = REGISTRY.register("record_undertides", () -> new RecordItem(0, CASounds.UNDERTIDES, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 4620));
    public static final RegistryObject<Item> AEGIR_GLASS_DECO = block(CABlocks.AEGIR_GLASS_DECO);
    public static final RegistryObject<Item> AEGIR_GLASS_BAR = block(CABlocks.AEGIR_GLASS_BAR);
    public static final RegistryObject<Item> AEGIR_GLASS_ARCH = block(CABlocks.AEGIR_GLASS_ARCH);
    public static final RegistryObject<Item> RECORD_DEEPNESS = REGISTRY.register("record_deepness", () -> new RecordItem(6, CASounds.OPERATION_DEEPNESS, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 4420));
    public static final RegistryObject<Item> OCEANIZE_RABBIT_SPAWN_EGG = REGISTRY.register("oceanize_rabbit_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZE_RABBIT, -1802567, -4241774, new Item.Properties()));
    public static final RegistryObject<Item> CHITIN_BOW = REGISTRY.register("chitin_bow", ChitinBowItem::new);
    public static final RegistryObject<Item> CORAL_FEET = REGISTRY.register("coral_feet", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> SAINT_CARMEN_SPAWN_EGG = REGISTRY.register("saint_carmen_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.SAINT_CARMEN, -1, -1, new Item.Properties()));
    public static final RegistryObject<Item> SALTWIND_SMOOTH_STAIR = block(CABlocks.SALTWIND_SMOOTH_STAIR);
    public static final RegistryObject<Item> CARMEN_TREATY = REGISTRY.register("carmen_treaty", CarmenTreatyItem::new);
    public static final RegistryObject<Item> OCEANIZED_ILLUSIONER_SPAWNER = REGISTRY.register("oceanized_illusioner_spawner", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_ILLUSIONER, -1, -1, new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> OCEAN_ILLUSION_SPAWN_EGG = REGISTRY.register("ocean_illusion_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEAN_ILLUSION, -13609646, -14469579, new Item.Properties()));
    public static final RegistryObject<TrailriteArmorItem> TRAILRITE_ARMOR_HELMET = REGISTRY.register("trailrite_armor_helmet", () -> new TrailriteArmorItem(ArmorItem.Type.HELMET, new Item.Properties().fireResistant()));
    public static final RegistryObject<TrailriteArmorItem> TRAILRITE_ARMOR_CHESTPLATE = REGISTRY.register("trailrite_armor_chestplate", () -> new TrailriteArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant()));
    public static final RegistryObject<TrailriteArmorItem> TRAILRITE_ARMOR_LEGGINGS = REGISTRY.register("trailrite_armor_leggings", () -> new TrailriteArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant()));
    public static final RegistryObject<TrailriteArmorItem> TRAILRITE_ARMOR_BOOTS = REGISTRY.register("trailrite_armor_boots", () -> new TrailriteArmorItem(ArmorItem.Type.BOOTS, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> ILLUSIONER_BANNER = REGISTRY.register(Objects.requireNonNull(CABlocks.ILLUSIONER_BANNER.getId()).getPath(),
            () -> new IllusionerBannerDisplayItem(CABlocks.ILLUSIONER_BANNER.get(), new Item.Properties()));
    public static final RegistryObject<Item> FLAMARINE_STATUE_SPAWN_EGG = REGISTRY.register("flamarine_statue_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.FLAMARINE_STATUE, -12687711, -341741, new Item.Properties()));
    public static final RegistryObject<Item> LIVING_ARMORSTAND = REGISTRY.register(Objects.requireNonNull(CABlocks.LIVING_ARMORSTAND.getId()).getPath(),
            () -> new LivingArmorstandDisplayItem(CABlocks.LIVING_ARMORSTAND.get(), new Item.Properties()));
    public static final RegistryObject<Item> NAUTILUS_HEADHUNTER_SPAWN_EGG = REGISTRY.register("nautilus_headhunter_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.NAUTILUS_HEADHUNTER, -30360, -3593216, new Item.Properties()));
    public static final RegistryObject<Item> XANTIS_SPAWN_EGG = REGISTRY.register("xantis_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.XANTIS, -10063429, -7364642, new Item.Properties()));
    public static final RegistryObject<Item> FLAMARINE_GOLEM_SPAWN_EGG = REGISTRY.register("flamarine_golem_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.FLAMARINE_GOLEM, -1, -1, new Item.Properties()));
    public static final RegistryObject<Item> TRAILRITE_ARMORSTAND = REGISTRY.register(Objects.requireNonNull(CABlocks.TRAILRITE_ARMORSTAND.getId()).getPath(),
            () -> new TrailriteArmorstandDisplayItem(CABlocks.TRAILRITE_ARMORSTAND.get(), new Item.Properties()));
    public static final RegistryObject<Item> TRAILRITE_PICKAXE = REGISTRY.register("trailrite_pickaxe", TrailritePickaxeItem::new);
    public static final RegistryObject<Item> TRAILRITE_HOE = REGISTRY.register("trailrite_hoe", TrailriteHoeItem::new);
    public static final RegistryObject<Item> TRAILRITE_SHOVEL = REGISTRY.register("trailrite_shovel", TrailriteShovelItem::new);
    public static final RegistryObject<Item> OCEANIZED_VEX_SPAWN_EGG = REGISTRY.register("oceanized_vex_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_VEX, -11178618, -8543810, new Item.Properties()));
    public static final RegistryObject<KnightIronItem> KNIGHT_IRON_HELMET = REGISTRY.register("knight_iron_helmet", () -> new KnightIronItem(ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<KnightIronItem> KNIGHT_IRON_CHESTPLATE = REGISTRY.register("knight_iron_chestplate", () -> new KnightIronItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<KnightIronItem> KNIGHT_IRON_LEGGINGS = REGISTRY.register("knight_iron_leggings", () -> new KnightIronItem(ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<KnightIronItem> KNIGHT_IRON_BOOTS = REGISTRY.register("knight_iron_boots", () -> new KnightIronItem(ArmorItem.Type.BOOTS, new Item.Properties()));
    public static final RegistryObject<Item> QUNYOU_WANTED_ISHARMLA_SPAWN_EGG = REGISTRY.register("qunyou_wanted_isharmla_spawn_egg",
            () -> new ForgeSpawnEggItem(CAEntities.QUNYOU_WANTED_ISHARMLA, -2499866, -10115, new Item.Properties()));
    public static final RegistryObject<Item> ISHARMLA_TEAR_SPAWN_EGG = REGISTRY.register("isharmla_tear_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.ISHARMLA_TEAR, -3034976, -328990, new Item.Properties()));
    public static final RegistryObject<Item> LEVIATHAN_ANIMUS = REGISTRY.register("leviathan_animus", LeviathanAnimusItem::new);
    public static final RegistryObject<Item> RECORD_ISHARMLA = REGISTRY.register("record_isharmla", () -> new RecordItem(4, CASounds.CONQUERER, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 4020));
    public static final RegistryObject<Item> TEAR_ISHARMLA = REGISTRY.register("tear_isharmla", TearIsharmlaItem::new);
    public static final RegistryObject<Item> ISHARMLA_SCUTE = REGISTRY.register("isharmla_scute", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ISHARMLA_BRICK = block(CABlocks.ISHARMLA_BRICK);
    public static final RegistryObject<Item> ISHARMLA_BRICK_PILLAR = block(CABlocks.ISHARMLA_BRICK_PILLAR);
    public static final RegistryObject<Item> ISHARMLA_BRICK_CHIESLED = block(CABlocks.ISHARMLA_BRICK_CHIESLED);
    public static final RegistryObject<Item> ISHARMLA_BRICK_GILDED = block(CABlocks.ISHARMLA_BRICK_GILDED);
    public static final RegistryObject<Item> ISHARMLA_SLAB = block(CABlocks.ISHARMLA_SLAB);
    public static final RegistryObject<Item> ISHARMLA_STAIR = block(CABlocks.ISHARMLA_STAIR);
    public static final RegistryObject<Item> ISHARMLA_WALL = block(CABlocks.ISHARMLA_WALL);
    public static final RegistryObject<Item> ISHARMLA_WALL_CHIESLED = block(CABlocks.ISHARMLA_WALL_CHIESLED);
    public static final RegistryObject<Item> ISHARMLA_WALL_GILDED = block(CABlocks.ISHARMLA_WALL_GILDED);
    public static final RegistryObject<Item> COMPASSION_PRAYER_SPAWN_EGG = REGISTRY.register("compassion_prayer_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.COMPASSION_PRAYER, -11571331, -2371137, new Item.Properties()));
    public static final RegistryObject<Item> MOIST_ENDER_CRYSTAL_SPAWN_EGG = REGISTRY.register("moist_ender_crystal_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.MOIST_ENDER_CRYSTAL, -1, -1, new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_BRAND = block(CABlocks.DRAGON_BRAND);
    public static final RegistryObject<Item> MOIST_CRYSTAL_ITEM = REGISTRY.register("moist_crystal_item", MoistCrystalItemItem::new);
    public static final RegistryObject<Item> ENDERINA_CORE = block(CABlocks.ENDERINA_CORE);
    public static final RegistryObject<Item> DRAGON_WAND = REGISTRY.register("dragon_wand", DragonWandItem::new);
    public static final RegistryObject<Item> CAERULA_BOOK_HOLDER = REGISTRY.register("caerula_book_holder", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> THIRSTER_SPAWN_EGG = REGISTRY.register("thirster_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.THIRSTER, -1, -1, new Item.Properties()));
    public static final RegistryObject<Item> ABSORBER_LIMB_SPAWN_EGG = REGISTRY.register("absorber_limb_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.ABSORBER_LIMB, -16117736, -13220008, new Item.Properties()));
    public static final RegistryObject<Item> NERVOUS_REGENERATION = REGISTRY.register("nervous_regeneration", NervousRegenerationItem::new);
    public static final RegistryObject<Item> THIRST_CORAL = block(CABlocks.THIRST_CORAL);
    public static final RegistryObject<Item> COMPLEX_CHITIN_BOW = REGISTRY.register("complex_chitin_bow", ComplexChitinBowItem::new);
    public static final RegistryObject<Item> TRAILRITE_BOW = REGISTRY.register("trailrite_bow", TrailriteBowItem::new);
    public static final RegistryObject<Item> TRAILRITE_ARROW = REGISTRY.register("trailrite_arrow", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> TRAILRITE_NUGGET = REGISTRY.register("trailrite_nugget", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> SCREAM_CHEST_FISH_SPAWN_EGG = REGISTRY.register("scream_chest_fish_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.SCREAM_CHEST_FISH, -9079426, -12746559, new Item.Properties()));
    public static final RegistryObject<Item> OCEANIZED_CHICKEN_SPAWN_EGG = REGISTRY.register("oceanized_chicken_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_CHICKEN, -9467992, -16762940, new Item.Properties()));
    public static final RegistryObject<Item> NETHERSEA_CHICKEN_EGG = REGISTRY.register("nethersea_chicken_egg", NetherseaChickenEggItem::new);
    public static final RegistryObject<Item> NETHERSEA_EGG_CUSTARD = REGISTRY.register("nethersea_egg_custard", NetherseaEggCustardItem::new);
    public static final RegistryObject<Item> NETHERSEA_SLIME_SPAWN_EGG = REGISTRY.register("nethersea_slime_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.NETHERSEA_SLIME, -10983573, -12426093, new Item.Properties()));
    public static final RegistryObject<Item> OCEANIZED_SHULKER_SPAWN_EGG = REGISTRY.register("oceanized_shulker_spawn_egg", () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_SHULKER, -5999788, -13398106, new Item.Properties()));
    public static final RegistryObject<Item> NURTURE_GENE_SET = REGISTRY.register("nurture_gene_set", NurtureGeneSetItem::new);
    public static final RegistryObject<Item> GENE_SAMPLE_NORMAL = REGISTRY.register("gene_sample_normal", GeneSampleNormalItem::new);
    public static final RegistryObject<Item> GENE_SAMPLE_UPGRADED = REGISTRY.register("gene_sample_upgraded", GeneSampleUpgradedItem::new);
    public static final RegistryObject<Item> GENE_SAMPLE_SUPERB = REGISTRY.register("gene_sample_superb", GeneSampleSuperbItem::new);
    public static final RegistryObject<Item> NETHERSEA_SAMPLING = block(CABlocks.NETHERSEA_SAMPLING);
    public static final RegistryObject<Item> ARCHIVE_SAL_VIENTO = REGISTRY.register("archive_sal_viento", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ARCHIVE_OF_TIDELINK = REGISTRY.register("archive_of_tidelink", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ARCHIVE_OF_MARTUS = REGISTRY.register("archive_of_martus", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ARCHIVE_OF_RAIDER = REGISTRY.register("archive_of_raider", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)));
    private static final Map<ResourceLocation, Integer> TOOLTIP_COUNTS = new HashMap<>();
    public static final RegistryObject<Item> CAFFEINE = tooltipItem("caffeine", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 1);
    public static final RegistryObject<Item> BONE_SHARD = tooltipItem("bone_shard", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 1);
    public static final RegistryObject<Item> OCEAN_PHLOEM = tooltipItem("ocean_phloem", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 1);
    public static final RegistryObject<Item> OCEAN_FIBRE = tooltipItem("ocean_fibre",
            () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(2).saturationMod(0.2f).meat().build())), 1);
    public static final RegistryObject<Item> CHITIN_COOKIE_RAW = REGISTRY.register("chitin_cookie_raw", ChitinCookieRawItem::new);
    public static final RegistryObject<Item> CHITIN_COOKIE_DONE = REGISTRY.register("chitin_cookie_done", ChitinCookieDoneItem::new);
    public static final RegistryObject<Item> OCEAN_CRYSTAL = tooltipItem("ocean_crystal", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 1);
    public static final RegistryObject<Item> OCEAN_CUTIN = tooltipItem("ocean_cutin", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 1);
    public static final RegistryObject<Item> OCEAN_CHITIN = tooltipItem("ocean_chitin", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 1);
    public static final RegistryObject<Item> COMPLEX_CHITIN = tooltipItem("complex_chitin", () -> new Item(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.UNCOMMON)), 1);
    public static final RegistryObject<Item> OCEAN_TRIM_TEMPLATE = tooltipItem("ocean_trim_template", () -> new Item(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.COMMON)), 7);
    public static final RegistryObject<Item> LANGUAGE_KEY = tooltipItem("language_key", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 17);
    public static final RegistryObject<Item> SAMPLE_SUBSISTING = tooltipItem("sample_subsisting", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 10);
    public static final RegistryObject<Item> SAMPLE_GROW = tooltipItem("sample_grow", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 10);
    public static final RegistryObject<Item> SAMPLE_MIGRATION = tooltipItem("sample_migration", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 10);
    public static final RegistryObject<Item> BROKEN_OCEAN_CELL = tooltipItem("broken_ocean_cell", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 1);
    public static final RegistryObject<Item> OCEAN_CELL = tooltipItem("ocean_cell", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 1);
    public static final RegistryObject<Item> DNA_REAPER = tooltipItem("dna_reaper", () -> new Item(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)), 1);
    public static final RegistryObject<Item> BASE_EGG = tooltipItem("base_egg", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)), 1);
    public static final RegistryObject<Item> WHIRL_EYE = tooltipItem("whirl_eye", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)), 1);
    public static final RegistryObject<Item> A_SECOND_KEY = tooltipItem("a_second_key", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 4);
    public static final RegistryObject<Item> TRAILRITE = tooltipItem("trailrite", () -> new Item(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.RARE)), 1);
    public static final RegistryObject<Item> TRAIL_SHARD = tooltipItem("trail_shard", () -> new Item(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.UNCOMMON)), 1);
    public static final RegistryObject<Item> HETEROPIC_PIECE = tooltipItem("heteropic_piece", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 1);
    public static final RegistryObject<Item> TRAIL_POWDER = tooltipItem("trail_powder", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 1);
    public static final RegistryObject<Item> TRAIL_POWDER_CORE = tooltipItem("trail_powder_core", () -> new Item(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.COMMON)), 1);
    public static final RegistryObject<Item> HUNTER_GENE = tooltipItem("hunter_gene", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)), 1);
    public static final RegistryObject<Item> WHITE_CHITIN = tooltipItem("white_chitin", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 1);
    public static final RegistryObject<Item> REJECTION_KEY = tooltipItem("rejection_key", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 5);
    public static final RegistryObject<Item> WATER_LOGGED_PEARL = tooltipItem("water_logged_pearl", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 1);
    public static final RegistryObject<Item> IZUMIK_SPAWNEGG = tooltipItem("izumik_spawnegg",
            () -> new ForgeSpawnEggItem(CAEntities.IZUMIK, -1, -1, new Item.Properties().stacksTo(64).rarity(Rarity.EPIC)), 1);
    public static final RegistryObject<Item> TREATY_COPPER = tooltipItem("treaty_copper", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 3);
    public static final RegistryObject<Item> TREATY_EMPTY = tooltipItem("treaty_empty", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 2);
    public static final RegistryObject<Item> TREATY_IRON = tooltipItem("treaty_iron", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)), 3);
    public static final RegistryObject<Item> TREATY_GOLD = tooltipItem("treaty_gold", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)), 3);
    public static final RegistryObject<Item> TREATY_DIAMOND = tooltipItem("treaty_diamond", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)), 3);
    public static final RegistryObject<Item> TREATY_NETHERITE = tooltipItem("treaty_netherite", () -> new Item(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.RARE)), 3);
    public static final RegistryObject<Item> EMERALD_TREATY = tooltipItem("emerald_treaty", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)), 3);
    public static final RegistryObject<Item> MOIST_ECHO_SHARD = tooltipItem("moist_echo_shard", () -> new Item(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.UNCOMMON)), 1);
    public static final RegistryObject<Item> OCEANIZE_CATALYST = tooltipItem("oceanize_catalyst", OceanizeCatalystItem::new, 2);
    public static final RegistryObject<Item> ANCHOR_SHARD = tooltipItem("anchor_shard", () -> new Item(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.UNCOMMON)), 2);
    public static final RegistryObject<Item> ANCHOR_FORGE_INGOT = tooltipItem("anchor_forge_ingot", () -> new Item(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.RARE)), 2);
    public static final RegistryObject<Item> DIORITE_SCULPTURE = tooltipItem("diorite_sculpture", () -> new Item(new Item.Properties().stacksTo(8).fireResistant().rarity(Rarity.UNCOMMON)), 1);
    public static final RegistryObject<Item> KNIGHT_CORPSE = tooltipItem("knight_corpse", () -> new Item(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.UNCOMMON)), 3);
    public static final RegistryObject<Item> TIDE_HUNET_TEMPLATE = tooltipItem("tide_hunet_template", () -> new Item(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.COMMON)), 7);
    public static final RegistryObject<Item> DNA_HORSE = tooltipItem("dna_horse", () -> new Item(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)), 1);
    public static final RegistryObject<Item> ROCINANTE_INJECTOR = tooltipItem("rocinante_injector", RocinanteInjectorItem::new, 1);
    public static final RegistryObject<Item> TARGETED_BASE = tooltipItem("targeted_base", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)), 1);
    public static final RegistryObject<Item> HUNTER_GENE_SKADI = tooltipItem("hunter_gene_skadi", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)), 2);
    public static final RegistryObject<Item> HUNTER_GENE_ULPIANS = tooltipItem("hunter_gene_ulpians", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)), 2);
    public static final RegistryObject<Item> HUNTER_GENE_GLADIIA = tooltipItem("hunter_gene_gladiia", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)), 2);
    public static final RegistryObject<Item> TARGETED_TRANSMITTER_SKADI = tooltipItem("targeted_transmitter_skadi", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.RARE)), 2);
    public static final RegistryObject<Item> TARGETED_TRANSMITTER_ULPIANS = tooltipItem("targeted_transmitter_ulpians", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.RARE)), 2);
    public static final RegistryObject<Item> TARGETED_TRANSMITTER_GLADIIA = tooltipItem("targeted_transmitter_gladiia", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.RARE)), 2);
    public static final RegistryObject<Item> BASE_OPERATION_KIT = tooltipItem("base_operation_kit", () -> new Item(new Item.Properties().stacksTo(8).rarity(Rarity.COMMON)), 1);
    public static final RegistryObject<Item> OPERATION_KIT_SKADI = tooltipItem("operation_kit_skadi", () -> new Item(new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON)), 2);
    public static final RegistryObject<Item> OPERATION_KIT_ULPIANS = tooltipItem("operation_kit_ulpians", () -> new Item(new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON)), 2);
    public static final RegistryObject<Item> OPERATION_KIT_GLADIIA = tooltipItem("operation_kit_gladiia", () -> new Item(new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON)), 2);
    public static final RegistryObject<Item> HUNTER_GENE_SPECTER = tooltipItem("hunter_gene_specter", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)), 2);
    public static final RegistryObject<Item> OPERATION_KIT_SPECTER = tooltipItem("operation_kit_specter", () -> new Item(new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON)), 2);
    public static final RegistryObject<Item> TARGETED_TRANSMITTER_SPECTER = tooltipItem("targeted_transmitter_specter", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.RARE)), 2);
    public static final RegistryObject<Item> PALE_GOLD_PENDANT = tooltipItem("pale_gold_pendant", () -> new Item(new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON)), 1);
    public static final RegistryObject<Item> MOIST_BAG = tooltipItem("moist_bag", () -> new Item(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON)), 1);
    public static final RegistryObject<Item> FLAMARINE_UPGRADE_TEMPLATE = tooltipItem("flamarine_upgrade_template",
            () -> new Item(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.UNCOMMON)), 8);
    public static final RegistryObject<Item> ISHARMLA_SPAWNER = tooltipItem("isharmla_spawner",
            () -> new ForgeSpawnEggItem(CAEntities.ISHARMLA, -1, -1, new Item.Properties().stacksTo(64).rarity(Rarity.EPIC)), 1);
    public static final RegistryObject<Item> MIZUKI_DETERMINATION = tooltipItem("mizuki_determination", () -> new Item(new Item.Properties().stacksTo(16).fireResistant().rarity(Rarity.RARE)), 2);
    public static final RegistryObject<Item> ENDERINA_SPAWNER = tooltipItem("enderina_spawner",
            () -> new ForgeSpawnEggItem(CAEntities.OCEANIZED_ENDERINA, -1, -1, new Item.Properties().stacksTo(64).rarity(Rarity.EPIC)), 1);
    public static final RegistryObject<Item> MOIST_DRAGON_HEART = tooltipItem("moist_dragon_heart", () -> new Item(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.RARE)), 1);
    public static final RegistryObject<Item> NETHERSEA_PRESERVED_EGG = tooltipItem("nethersea_preserved_egg", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4f).alwaysEat().effect(() -> new MobEffectInstance(CAMobEffects.ESSENCE_RESISTANCE.get(), 400, 1, false, true), 1.0F).effect(() -> new MobEffectInstance(CAMobEffects.DEDUCT_ONE_SANITY.get(), 60, 0, false, false), 1.0F).build())), 1);
    public static final RegistryObject<Item> SEA_PRAIRIE_BOMB = block(CABlocks.SEA_PRAIRIE_BOMB);
    public static final RegistryObject<Item> CHEST_FISH_FOOD = block(CABlocks.CHEST_FISH_FOOD);

    private static <T extends Item> RegistryObject<T> tooltipItem(String name, Supplier<T> factory, int tooltipCount) {
        TOOLTIP_COUNTS.put(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, name), tooltipCount);
        return REGISTRY.register(name, factory);
    }

    private static RegistryObject<Item> block(RegistryObject<Block> block) {
        return REGISTRY.register(Objects.requireNonNull(block.getId()).getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static RegistryObject<Item> doubleBlock(RegistryObject<Block> block) {
        return REGISTRY.register(Objects.requireNonNull(block.getId()).getPath(), () -> new DoubleHighBlockItem(block.get(), new Item.Properties()));
    }

    @SubscribeEvent
    public static void clientLoad(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(MUSIC_BOX_FIXED.get(), ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "music_box_fixed_playing"),
                    (itemStackToRender, clientWorld, living, itemEntityId) -> living instanceof Player player
                            && player.getCooldowns().isOnCooldown(itemStackToRender.getItem()) ? 1.0F : 0.0F);
            ItemProperties.register(CHITIN_SHIELD.get(), ResourceLocation.parse("blocking"), Objects.requireNonNull(ItemProperties.getProperty(Items.SHIELD, ResourceLocation.parse("blocking"))));
            ItemProperties.register(COMPLEX_CHITIN_SHIELD.get(), ResourceLocation.parse("blocking"), Objects.requireNonNull(ItemProperties.getProperty(Items.SHIELD, ResourceLocation.parse("blocking"))));
            ItemProperties.register(TIDELINKED_SHIELD.get(), ResourceLocation.parse("blocking"), Objects.requireNonNull(ItemProperties.getProperty(Items.SHIELD, ResourceLocation.parse("blocking"))));
            ItemProperties.register(TRAILRITE_SHIELD.get(), ResourceLocation.parse("blocking"), Objects.requireNonNull(ItemProperties.getProperty(Items.SHIELD, ResourceLocation.parse("blocking"))));
            ItemProperties.register(CHITIN_BOW.get(), ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "chitin_bow_pulling"),
                    (itemStackToRender, clientWorld, living, itemEntityId) -> living != null && living.isUsingItem()
                            && living.getUseItem().is(itemStackToRender.getItem()) ? living.getTicksUsingItem() : 0.0F);
            ItemProperties.register(COMPLEX_CHITIN_BOW.get(), ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "complex_chitin_bow_pulling"),
                    (itemStackToRender, clientWorld, living, itemEntityId) -> living != null && living.isUsingItem()
                            && living.getUseItem().is(itemStackToRender.getItem()) ? living.getTicksUsingItem() : 0.0F);
            ItemProperties.register(TRAILRITE_BOW.get(), ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "trailrite_bow_pulling"),
                    (itemStackToRender, clientWorld, living, itemEntityId) -> living != null && living.isUsingItem()
                            && living.getUseItem().is(itemStackToRender.getItem()) ? living.getTicksUsingItem() : 0.0F);
        });
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void appendRegisteredTooltips(ItemTooltipEvent event) {
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
            if (itemId == null) {
                return;
            }

            Integer tooltipCount = TOOLTIP_COUNTS.get(itemId);
            if (tooltipCount != null && tooltipCount > 0) {
                List<Component> tooltip = event.getToolTip();
                String baseKey = "item." + itemId.getNamespace() + "." + itemId.getPath() + ".description_";
                for (int i = 0; i < tooltipCount; i++) {
                    tooltip.add(Component.translatable(baseKey + i));
                }
            }
        }
    }
}
