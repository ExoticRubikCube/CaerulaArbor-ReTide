package com.susen36.caerulaarbor.datagen.tags;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CACollectible;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 生成物品标签数据，包括 caerula_arbor、forge 和 minecraft 命名空间
 */
public class ItemTagsProvider extends TagsProvider.RegistryTagsProvider<Item> {
    private static final TagKey<Item> ANIMUS = caItemTag("animus");
    private static final TagKey<Item> ANY_CORAL = caItemTag("any_coral");
    private static final TagKey<Item> ARCHFIEND_RELICS = caItemTag("archfiend_relics");
    private static final TagKey<Item> COMMON_RELICS = caItemTag("common_relics");
    private static final TagKey<Item> CREAM_SWORD = caItemTag("cream_sword");
    private static final TagKey<Item> CURSED = caItemTag("cursed");
    private static final TagKey<Item> ENCHANTABLE_NETHERSEA_WALKER = caItemTag("enchantable/nethersea_walker");
    private static final TagKey<Item> ENCHANTABLE_SANITY = caItemTag("enchantable/sanity");
    private static final TagKey<Item> ENCHANTABLE_SANITY_DEFEND = caItemTag("enchantable/sanity_defend");
    private static final TagKey<Item> ENCHANTABLE_SEABORN_KILLER = caItemTag("enchantable/seaborn_killer");
    private static final TagKey<Item> ENDSPEAKER_CHAPTER = caItemTag("endspeaker_chapter");
    private static final TagKey<Item> FISH_FOOD = caItemTag("fish_food");
    private static final TagKey<Item> GENE = caItemTag("gene");
    private static final TagKey<Item> HAND_RELICS = caItemTag("hand_relics");
    private static final TagKey<Item> KING_RELICS = caItemTag("king_relics");
    private static final TagKey<Item> KNIGHT_EQUIPMENT = caItemTag("knight_equipment");
    private static final TagKey<Item> MOIST_ITEM = caItemTag("moist_item");
    private static final TagKey<Item> NETHERSEA_LOGS = caItemTag("nethersea_logs");
    private static final TagKey<Item> NETHERSEA_PROTECTIVE = caItemTag("nethersea_protective");
    private static final TagKey<Item> RELIC_GENERIC = caItemTag("relic_generic");
    private static final TagKey<Item> SEABORN_LOOTS = caItemTag("seaborn_loots");
    private static final TagKey<Item> SELF_MENDABLE = caItemTag("self_mendable");

    private static final TagKey<Item> FORGE_ARMOR_BOOTS = forgeItemTag("armor/boots");
    private static final TagKey<Item> FORGE_ARMOR_CHESTPLATES = forgeItemTag("armor/chestplates");
    private static final TagKey<Item> FORGE_ARMOR_HELMETS = forgeItemTag("armor/helmets");
    private static final TagKey<Item> FORGE_ARMOR_LEGGINGS = forgeItemTag("armor/leggings");
    private static final TagKey<Item> FORGE_ARMORS = forgeItemTag("armors");
    private static final TagKey<Item> FORGE_FENCES = forgeItemTag("fences");
    private static final TagKey<Item> FORGE_FENCES_WOODEN = forgeItemTag("fences/wooden");
    private static final TagKey<Item> FORGE_INGOTS = forgeItemTag("ingots");
    private static final TagKey<Item> FORGE_MEATS = forgeItemTag("meats");
    private static final TagKey<Item> FORGE_STORAGE_BLOCKS = forgeItemTag("storage_blocks");
    private static final TagKey<Item> FORGE_STORAGE_BLOCKS_COMPLEX_CHITIN = forgeItemTag("storage_blocks/complex_chitin");
    private static final TagKey<Item> FORGE_STORAGE_BLOCKS_HETEROPIC = forgeItemTag("storage_blocks/heteropic");
    private static final TagKey<Item> FORGE_STORAGE_BLOCKS_OCEAN_CHITIN = forgeItemTag("storage_blocks/ocean_chitin");
    private static final TagKey<Item> FORGE_STORAGE_BLOCKS_OCEAN_CRYSTAL = forgeItemTag("storage_blocks/ocean_crystal");
    private static final TagKey<Item> FORGE_STORAGE_BLOCKS_TRAILRITE = forgeItemTag("storage_blocks/trailrite");
    private static final TagKey<Item> FORGE_TOOLS_BOWS = forgeItemTag("tools/bows");
    private static final TagKey<Item> FORGE_TOOLS_CROSSBOWS = forgeItemTag("tools/crossbows");
    private static final TagKey<Item> FORGE_TOOLS_HOES = forgeItemTag("tools/hoes");

    private static final TagKey<Item> MINECRAFT_AXES = minecraftItemTag("axes");
    private static final TagKey<Item> MINECRAFT_BASE_STONE_OVERWORLD = minecraftItemTag("base_stone_overworld");
    private static final TagKey<Item> MINECRAFT_BOOKSHELF_BOOKS = minecraftItemTag("bookshelf_books");
    private static final TagKey<Item> MINECRAFT_BUTTONS = minecraftItemTag("buttons");
    private static final TagKey<Item> MINECRAFT_ENCHANTABLE_SHARP_WEAPON = minecraftItemTag("enchantable/sharp_weapon");
    private static final TagKey<Item> MINECRAFT_ENCHANTABLE_SWORD = minecraftItemTag("enchantable/sword");
    private static final TagKey<Item> MINECRAFT_ENCHANTABLE_WEAPON = minecraftItemTag("enchantable/weapon");
    private static final TagKey<Item> MINECRAFT_FENCES = minecraftItemTag("fences");
    private static final TagKey<Item> MINECRAFT_FLOWERS = minecraftItemTag("flowers");
    private static final TagKey<Item> MINECRAFT_FOX_FOOD = minecraftItemTag("fox_food");
    private static final TagKey<Item> MINECRAFT_FRUITS = minecraftItemTag("fruits");
    private static final TagKey<Item> MINECRAFT_HOES = minecraftItemTag("hoes");
    private static final TagKey<Item> MINECRAFT_LOGS = minecraftItemTag("logs");
    private static final TagKey<Item> MINECRAFT_MEAT = minecraftItemTag("meat");
    private static final TagKey<Item> MINECRAFT_MUSIC_DISCS = minecraftItemTag("music_discs");
    private static final TagKey<Item> MINECRAFT_PICKAXES = minecraftItemTag("pickaxes");
    private static final TagKey<Item> MINECRAFT_PIGLIN_LOVED = minecraftItemTag("piglin_loved");
    private static final TagKey<Item> MINECRAFT_PLANKS = minecraftItemTag("planks");
    private static final TagKey<Item> MINECRAFT_SAL_VIENTO_DECO = minecraftItemTag("sal_viento_deco");
    private static final TagKey<Item> MINECRAFT_SHOVELS = minecraftItemTag("shovels");
    private static final TagKey<Item> MINECRAFT_SMALL_FLOWERS = minecraftItemTag("small_flowers");
    private static final TagKey<Item> MINECRAFT_STONE_BUTTONS = minecraftItemTag("stone_buttons");
    private static final TagKey<Item> MINECRAFT_SWORDS = minecraftItemTag("swords");
    private static final TagKey<Item> MINECRAFT_TOOLS = minecraftItemTag("tools");
    private static final TagKey<Item> MINECRAFT_TRIM_TEMPLATES = minecraftItemTag("trim_templates");
    private static final TagKey<Item> MINECRAFT_TRIMMABLE_ARMOR = minecraftItemTag("trimmable_armor");
    private static final TagKey<Item> MINECRAFT_TULIP = minecraftItemTag("tulip");
    private static final TagKey<Item> MINECRAFT_WOODEN_BUTTONS = minecraftItemTag("wooden_buttons");
    private static final TagKey<Item> MINECRAFT_WOODEN_FENCES = minecraftItemTag("wooden_fences");
    private static final TagKey<Item> MINECRAFT_WOODEN_PRESSURE_PLATES = minecraftItemTag("wooden_pressure_plates");
    private static final TagKey<Item> MINECRAFT_WOODEN_SLABS = minecraftItemTag("wooden_slabs");
    private static final TagKey<Item> MINECRAFT_WOODEN_STAIRS = minecraftItemTag("wooden_stairs");

    public ItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.ITEM, lookupProvider, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        addItemsToTag(ANIMUS, CACollectible.CAERULA_HEART, CAItems.INCANDESCENT_ANIMA, CAItems.LEVIATHAN_ANIMUS, CAItems.MOIST_DRAGON_HEART);
        addItemsToTag(ANY_CORAL, Items.DEAD_TUBE_CORAL_BLOCK, Items.DEAD_BRAIN_CORAL_BLOCK, Items.DEAD_BUBBLE_CORAL_BLOCK, Items.DEAD_FIRE_CORAL_BLOCK, Items.DEAD_HORN_CORAL_BLOCK, Items.TUBE_CORAL_BLOCK, Items.BRAIN_CORAL_BLOCK, Items.BUBBLE_CORAL_BLOCK, Items.FIRE_CORAL_BLOCK, Items.HORN_CORAL_BLOCK, Items.DEAD_TUBE_CORAL, Items.DEAD_BRAIN_CORAL, Items.DEAD_BUBBLE_CORAL, Items.DEAD_FIRE_CORAL, Items.DEAD_HORN_CORAL, Items.TUBE_CORAL, Items.BRAIN_CORAL, Items.BUBBLE_CORAL, Items.FIRE_CORAL, Items.HORN_CORAL, Items.DEAD_TUBE_CORAL_FAN, Items.DEAD_BRAIN_CORAL_FAN, Items.DEAD_BUBBLE_CORAL_FAN, Items.DEAD_FIRE_CORAL_FAN, Items.DEAD_HORN_CORAL_FAN, Items.TUBE_CORAL_FAN, Items.BRAIN_CORAL_FAN, Items.BUBBLE_CORAL_FAN, Items.FIRE_CORAL_FAN, Items.HORN_CORAL_FAN);
        addItemsToTag(ARCHFIEND_RELICS, CACollectible.SARKAZ_KING_ARTIFACT, CACollectible.SARKAZ_KING_FLAG, CACollectible.VAMPIRES_BED, CACollectible.ROYAL_FATE);
        addItemsToTag(COMMON_RELICS, CACollectible.AROMATIC_COFFEE, CACollectible.SOLO_MUSIC_BOX, CACollectible.REDSTONE_IRIS_FLOWER, CACollectible.ODD_FLUTE, CACollectible.VOYAGE_OF_GOLD, CACollectible.PIGLIN_DIARY, CACollectible.TOPONYM_TEXTOLOGY, CACollectible.KETTLE, CACollectible.UTIL_ALLAY, CACollectible.BAT_BED, CACollectible.UTIL_OMNIKEY, CACollectible.UTIL_SCORE, CACollectible.UTIL_RESCISSION, CACollectible.HEMOST);
        addItemsToTag(CREAM_SWORD, CAItems.TRAILED_WOODEN_SWORD, CAItems.TRAILED_STONE_SWORD, CAItems.TRAILED_IRON_SWORD, CAItems.TRAILED_DIAMOND_SWORD, CAItems.TRAILED_NETHERITE_SWORD, CAItems.TRAILED_GOLDEN_SWORD);
        addItemsToTag(CURSED, CACollectible.CURSED_EMELIGHT, CACollectible.CURSED_GLOWBODY, CACollectible.CURSED_RESEARCH, CACollectible.CAERULA_HEART);
        addTagsToTag(ENCHANTABLE_NETHERSEA_WALKER, FORGE_ARMOR_BOOTS);
        addTagsToTag(ENCHANTABLE_SANITY, MINECRAFT_ENCHANTABLE_WEAPON, MINECRAFT_SWORDS, MINECRAFT_AXES);
        addTagsToTag(ENCHANTABLE_SANITY_DEFEND, MINECRAFT_TRIMMABLE_ARMOR, FORGE_ARMORS, FORGE_ARMOR_LEGGINGS, FORGE_ARMOR_BOOTS, FORGE_ARMOR_HELMETS, FORGE_ARMOR_CHESTPLATES);
        addItemsToTag(ENCHANTABLE_SEABORN_KILLER, CAItems.UNAMBIGUOUS_DIRECTION, CAItems.UNFINISHED_BEAUTY, CAItems.WAVECLEAVER);
        addTagsToTag(ENCHANTABLE_SEABORN_KILLER, MINECRAFT_AXES, MINECRAFT_SWORDS, MINECRAFT_ENCHANTABLE_WEAPON);
        addItemsToTag(ENDSPEAKER_CHAPTER, CAItems.DICTATIONLESS_CHAPTER, CAItems.DICTATION_CHAPTER);
        addItemsToTag(FISH_FOOD, CAItems.SEA_TRAIL_MOR, CAItems.OCEAN_FIBRE, CAItems.COOKED_MOR, CAItems.BROKEN_CELL_CLUSTER, CAItems.CELL_CLUSTER, CAItems.CARAMEL_MOR, CAItems.OCEAN_PEDUNCLE, CAItems.ELITE_PEDUNCLE, CAItems.COOKED_FIBRE, CAItems.COOKED_BROKEN_CELL_CLUSTER, CAItems.COOKED_CELL_CLUSTER, CAItems.COOKED_PEDUNCLE, CAItems.FAKE_EGG, CAItems.REAL_EGG, CAItems.COOKED_FAKEEGG, CAItems.COLLECTOR_MEAT, CAItems.COOKED_COLLECTOR, CAItems.CLAW, CAItems.COOKED_CLAW);
        addItemsToTag(GENE, CAItems.DNA_REAPER, CAItems.HUNTER_GENE, CAItems.DNA_HORSE, CAItems.ROCINANTE_INJECTOR, CAItems.HUNTER_GENE_SKADI, CAItems.HUNTER_GENE_ULPIANS, CAItems.HUNTER_GENE_GLADIIA, CAItems.HUNTER_GENE_SPECTER, CACollectible.NURTURE_GENE_SET, CAItems.GENE_SAMPLE_NORMAL, CAItems.GENE_SAMPLE_UPGRADED, CAItems.GENE_SAMPLE_SUPERB);
        addItemsToTag(HAND_RELICS, CACollectible.HAND_THORNS, CACollectible.HAND_STRANGLE, CACollectible.HAND_FERTILITY, CACollectible.HAND_SPEED, CACollectible.HAND_OF_BARREN, CACollectible.HAND_SWIPE, CACollectible.HAND_FIREWORK, CACollectible.HAND_OF_ENGRAVE, CACollectible.HAND_SWORD);
        addItemsToTag(KING_RELICS, CACollectible.RELIC_CROWN, CACollectible.KING_ARMOR, CACollectible.KING_SPEAR, CACollectible.KING_EXTENSION, CACollectible.KING_CRYSTAL);
        addItemsToTag(KNIGHT_EQUIPMENT, CAItems.IRON_SWORD_OF_KNIGHT_CORPUS, CAItems.LONG_SWORD_OF_KNIGHT_CORPUS, CAItems.KNIGHT_IRON_HELMET, CAItems.KNIGHT_IRON_CHESTPLATE, CAItems.KNIGHT_IRON_LEGGINGS, CAItems.KNIGHT_IRON_BOOTS);
        addItemsToTag(MOIST_ITEM, CAItems.MOIST_ECHO_SHARD, CAItems.MOIST_STAR, CAItems.WATER_LOGGED_PEARL, CAItems.MOIST_CRYSTAL_ITEM, CAItems.MOIST_DRAGON_HEART);
        addItemsToTag(NETHERSEA_LOGS, CAItems.NETHERSEA_WOOD, CAItems.STRIPPED_NETHERSEA_WOOD, CAItems.TRAIL_LOG, CAItems.STRIPPED_TRAIL_LOG);
        addItemsToTag(NETHERSEA_PROTECTIVE, CAItems.TRAILRITE_AXE, CAItems.TRAILRITE_SWORD, CAItems.TRAILRITE_PICKAXE, CAItems.TRAILRITE_HOE, CAItems.TRAILRITE_SHOVEL, CAItems.TRAIL_MOP);
        addItemsToTag(RELIC_GENERIC, CACollectible.SURVIVOR_CONTRACT, CACollectible.TREATY, CACollectible.BOWL_SEAGRASS, CACollectible.GOLDEN_STORM, CACollectible.COFFEE_CANDY, CACollectible.CANNED_CHERRY, CACollectible.RAINBOW_CANDY, CACollectible.AROMATIC_COFFEE, CACollectible.SOLO_MUSIC_BOX, CACollectible.REDSTONE_IRIS_FLOWER, CACollectible.ODD_FLUTE, CACollectible.VOYAGE_OF_GOLD, CACollectible.PIGLIN_DIARY, CACollectible.TOPONYM_TEXTOLOGY, CACollectible.KETTLE, CACollectible.CHITIN_KNIFE, CACollectible.UTIL_ALLAY, CACollectible.BAT_BED, CACollectible.PROOF_OF_LONGEVITY, CACollectible.UTIL_OMNIKEY, CACollectible.UTIL_SCORE, CACollectible.UTIL_RESCISSION, CACollectible.UTIL_STARE, CACollectible.YEARNING, CACollectible.FEATURED_CANNED_MEAT, CACollectible.HEMOST);
        addTagsToTag(RELIC_GENERIC, HAND_RELICS, ARCHFIEND_RELICS, KING_RELICS);
        addItemsToTag(SEABORN_LOOTS, CAItems.OCEAN_PHLOEM, CAItems.OCEAN_FIBRE, CAItems.OCEAN_EYE, CAItems.OCEAN_CRYSTAL, CAItems.OCEAN_CUTIN, CAItems.OCEAN_CHITIN, CAItems.HETEROPIC_PIECE, CAItems.BROKEN_OCEAN_CELL, CAItems.BROKEN_CELL_CLUSTER, CAItems.OCEAN_CELL, CAItems.CELL_CLUSTER);
        addTagsToTag(SEABORN_LOOTS, FISH_FOOD, MOIST_ITEM);
        addItemsToTag(SELF_MENDABLE, CAItems.COMPLEX_CHITIN_SWORD, CAItems.COMPLEX_CHITIN_PICKAXE, CAItems.COMPLEX_CHITIN_AXE, CAItems.COMPLEX_CHITIN_SHOVEL, CAItems.COMPLEX_CHITIN_HOE, CAItems.LEGENDARY_SPEAR, CAItems.TRAILED_WOODEN_SWORD, CAItems.TRAILED_STONE_SWORD, CAItems.TRAILED_IRON_SWORD, CAItems.TRAILED_DIAMOND_SWORD, CAItems.TRAILED_NETHERITE_SWORD, CAItems.TRAILED_GOLDEN_SWORD, CAItems.PHLOEM_BOW, CAItems.TRAILRITE_AXE, CAItems.TRAILRITE_SWORD, CAItems.PATH_INAUGURATOR, CAItems.COMPLEX_CHITIN_BOW);
        addItemsToTag(FORGE_ARMOR_BOOTS, CAItems.CHITIN_ARMOR_BOOTS, CAItems.SEALEATHER_BOOTS, CAItems.COMPLEXCHITIN_ARMOR_BOOTS, Items.LEATHER_BOOTS, Items.CHAINMAIL_BOOTS, Items.IRON_BOOTS, Items.GOLDEN_BOOTS, Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS, CAItems.TRAILRITE_ARMOR_BOOTS, CAItems.SEALEATHER_CHITIN_BOOTS, CAItems.KNIGHT_IRON_BOOTS);
        addItemsToTag(FORGE_ARMOR_CHESTPLATES, CAItems.CHITIN_ARMOR_CHESTPLATE, CAItems.SEALEATHER_CHESTPLATE, CAItems.COMPLEXCHITIN_ARMOR_CHESTPLATE, CAItems.WEARABLE_CHEST_CHESTPLATE, Items.LEATHER_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE, CAItems.SEALEATHER_CHITIN_CHESTPLATE, CAItems.TRAILRITE_ARMOR_CHESTPLATE, CAItems.KNIGHT_IRON_CHESTPLATE);
        addItemsToTag(FORGE_ARMOR_HELMETS, CAItems.CHITIN_ARMOR_HELMET, CAItems.SEALEATHER_HELMET, CAItems.COMPLEXCHITIN_ARMOR_HELMET, CAItems.WEARABLE_CROWN_HELMET, Items.LEATHER_HELMET, Items.CHAINMAIL_HELMET, Items.TURTLE_HELMET, Items.IRON_HELMET, Items.GOLDEN_HELMET, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET, CAItems.SEALEATHER_CHITIN_HELMET, CAItems.TRAILRITE_ARMOR_HELMET, CAItems.KNIGHT_IRON_HELMET);
        addItemsToTag(FORGE_ARMOR_LEGGINGS, CAItems.CHITIN_ARMOR_LEGGINGS, CAItems.SEALEATHER_LEGGINGS, CAItems.COMPLEXCHITIN_ARMOR_LEGGINGS, Items.LEATHER_LEGGINGS, Items.CHAINMAIL_LEGGINGS, Items.IRON_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS, CAItems.SEALEATHER_CHITIN_LEGGINGS, CAItems.TRAILRITE_ARMOR_LEGGINGS, CAItems.KNIGHT_IRON_LEGGINGS);
        addItemsToTag(FORGE_ARMORS, CAItems.CHITIN_ARMOR_HELMET, CAItems.CHITIN_ARMOR_CHESTPLATE, CAItems.CHITIN_ARMOR_LEGGINGS, CAItems.CHITIN_ARMOR_BOOTS, CAItems.SEALEATHER_HELMET, CAItems.SEALEATHER_CHESTPLATE, CAItems.SEALEATHER_LEGGINGS, CAItems.SEALEATHER_BOOTS, CAItems.COMPLEXCHITIN_ARMOR_HELMET, CAItems.COMPLEXCHITIN_ARMOR_CHESTPLATE, CAItems.COMPLEXCHITIN_ARMOR_LEGGINGS, CAItems.COMPLEXCHITIN_ARMOR_BOOTS, Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS, CAItems.TRAILRITE_ARMOR_HELMET, CAItems.TRAILRITE_ARMOR_CHESTPLATE, CAItems.TRAILRITE_ARMOR_LEGGINGS, CAItems.TRAILRITE_ARMOR_BOOTS, CAItems.KNIGHT_IRON_HELMET, CAItems.KNIGHT_IRON_CHESTPLATE, CAItems.KNIGHT_IRON_LEGGINGS, CAItems.KNIGHT_IRON_BOOTS);
        addTagsToTag(FORGE_ARMORS, FORGE_ARMOR_LEGGINGS, FORGE_ARMOR_CHESTPLATES, FORGE_ARMOR_BOOTS, FORGE_ARMOR_HELMETS);
        addItemsToTag(FORGE_FENCES, CAItems.TRAIL_PLANKS_FENCE);
        addItemsToTag(FORGE_FENCES_WOODEN, CAItems.TRAIL_PLANKS_FENCE);
        addItemsToTag(FORGE_INGOTS, CAItems.REDSTONE_INGOT, CAItems.TRAILRITE);
        addItemsToTag(FORGE_MEATS, CAItems.OCEAN_FIBRE, CAItems.COOKED_FIBRE, CAItems.OCEAN_PEDUNCLE, CAItems.ELITE_PEDUNCLE, CAItems.COOKED_PEDUNCLE, CAItems.COLLECTOR_MEAT, CAItems.COOKED_COLLECTOR);
        addTagsToTag(FORGE_MEATS, MINECRAFT_MEAT);
        addItemsToTag(MINECRAFT_MEAT, CAItems.BROKEN_CELL_CLUSTER, CAItems.CELL_CLUSTER, CAItems.COLLECTOR_MEAT, CAItems.KEBAB_RAW, CAItems.KEBAB_COOKED, CACollectible.FEATURED_CANNED_MEAT, CAItems.NETHERSEA_EGG_CUSTARD, CAItems.OCEAN_EYE, CAItems.OCEAN_PEDUNCLE, CAItems.TRANSFORM_CELL);
        addItemsToTag(FORGE_STORAGE_BLOCKS, CAItems.CHITIN_BLOCK, CAItems.OCEAN_CRYSTAL_BLOCK, CAItems.COMPLEX_CHITIN_BLOCK, CAItems.TRAILRITE_BLOCK, CAItems.HETEROPIC_BLOCK);
        addItemsToTag(FORGE_STORAGE_BLOCKS_COMPLEX_CHITIN, CAItems.COMPLEX_CHITIN_BLOCK);
        addItemsToTag(FORGE_STORAGE_BLOCKS_HETEROPIC, CAItems.HETEROPIC_BLOCK);
        addItemsToTag(FORGE_STORAGE_BLOCKS_OCEAN_CHITIN, CAItems.CHITIN_BLOCK);
        addItemsToTag(FORGE_STORAGE_BLOCKS_OCEAN_CRYSTAL, CAItems.OCEAN_CRYSTAL_BLOCK);
        addItemsToTag(FORGE_STORAGE_BLOCKS_TRAILRITE, CAItems.TRAILRITE_BLOCK);
        addItemsToTag(FORGE_TOOLS_BOWS, CAItems.PHLOEM_BOW, CAItems.TIDE_WAND, CAItems.CHITIN_BOW, CAItems.TIDELINKED_WAND, CAItems.DRAGON_WAND, CAItems.COMPLEX_CHITIN_BOW, CAItems.TRAILRITE_BOW);
        addItemsToTag(FORGE_TOOLS_CROSSBOWS, CAItems.DRAGON_WAND);
        addItemsToTag(FORGE_TOOLS_HOES, CAItems.CHITIN_HOE, CAItems.HOE_OCEAN_CRYSTAL, CAItems.COMPLEX_CHITIN_HOE, CAItems.PATH_INAUGURATOR, CAItems.TRAILRITE_HOE, Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.GOLDEN_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE);

        addItemsToTag(MINECRAFT_AXES, CAItems.TRAILRITE_AXE, CAItems.COMPLEX_CHITIN_AXE, CAItems.AXE_OCEAN_CRYSTAL, CAItems.CHITIN_AXE, CAItems.PATH_INAUGURATOR, CAItems.CIRCULAR_SAW, CAItems.UNFINISHED_BEAUTY);
        addItemsToTag(MINECRAFT_BASE_STONE_OVERWORLD, Items.STONE, Items.GRANITE, Items.DIORITE, Items.ANDESITE);
        addItemsToTag(MINECRAFT_BOOKSHELF_BOOKS, CACollectible.SURVIVOR_CONTRACT, CACollectible.PIGLIN_DIARY, CACollectible.TOPONYM_TEXTOLOGY, CAItems.MARTUS_BOOK, CAItems.DICTATIONLESS_CHAPTER, CACollectible.CURSED_RESEARCH, CAItems.DICTATION_CHAPTER);
        addItemsToTag(MINECRAFT_BUTTONS, CAItems.TRAIL_BUTTON, CAItems.TRAIL_PLANK_BUTTON);
        addItemsToTag(MINECRAFT_ENCHANTABLE_SHARP_WEAPON, CAItems.LEGENDARY_SPEAR, CAItems.WAVECLEAVER, CAItems.CHITIN_SHOVEL, CAItems.BLOCK_SPEAR, CAItems.SWORD_OCEAN_CRYSTAL, CAItems.CHITIN_AXE, CAItems.CHITIN_SWORD, CAItems.AXE_OCEAN_CRYSTAL, CAItems.COMPLEX_CHITIN_SWORD, CAItems.COMPLEX_CHITIN_AXE, CAItems.TRAILED_WOODEN_SWORD, CAItems.TRAILED_STONE_SWORD, CAItems.TRAILED_IRON_SWORD, CAItems.TRAILED_DIAMOND_SWORD, CAItems.TRAILED_NETHERITE_SWORD, CAItems.TRAILED_GOLDEN_SWORD, CAItems.TRAILRITE_AXE, CAItems.TRAILRITE_SWORD);
        addTagsToTag(MINECRAFT_ENCHANTABLE_SHARP_WEAPON, MINECRAFT_SWORDS, MINECRAFT_AXES);
        addItemsToTag(MINECRAFT_ENCHANTABLE_SWORD, CAItems.LEGENDARY_SPEAR, CAItems.WAVECLEAVER);
        addTagsToTag(MINECRAFT_ENCHANTABLE_SWORD, MINECRAFT_SWORDS);
        addItemsToTag(MINECRAFT_ENCHANTABLE_WEAPON, CAItems.THE_SPEAR, CAItems.SWORD_OCEAN_CRYSTAL, CAItems.CHITIN_SWORD, CAItems.COMPLEX_CHITIN_SWORD, CAItems.LEGENDARY_SPEAR, CAItems.WAVECLEAVER);
        addItemsToTag(MINECRAFT_FENCES, CAItems.TRAIL_PLANKS_FENCE);
        addItemsToTag(MINECRAFT_FLOWERS, CAItems.TRAIL_MUSHROOM);
        addItemsToTag(MINECRAFT_FOX_FOOD, CAItems.FLUORE_BERRIES, CAItems.RADIANT_BERRIES, CACollectible.CANNED_CHERRY);
        addItemsToTag(MINECRAFT_FRUITS, Items.APPLE, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.MELON_SLICE, Items.SWEET_BERRIES, Items.GLOW_BERRIES, Items.GLISTERING_MELON_SLICE);
        addItemsToTag(MINECRAFT_HOES, CAItems.TRAILRITE_HOE, CAItems.COMPLEX_CHITIN_HOE, CAItems.HOE_OCEAN_CRYSTAL, CAItems.CHITIN_HOE, CAItems.PATH_INAUGURATOR);
        addItemsToTag(MINECRAFT_LOGS, CAItems.TRAIL_LOG, CAItems.STRIPPED_TRAIL_LOG, CAItems.NETHERSEA_WOOD, CAItems.STRIPPED_NETHERSEA_WOOD);
        addItemsToTag(MINECRAFT_MEAT, Items.COOKED_PORKCHOP, Items.COOKED_BEEF, Items.COOKED_CHICKEN, Items.COOKED_RABBIT, Items.COOKED_MUTTON, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.COOKED_COD, Items.COOKED_SALMON);
        addItemsToTag(MINECRAFT_MUSIC_DISCS, CAItems.RECORD_ISHARMLA, CAItems.RECORD_DEEPNESS, CAItems.RECORD_UNDERTIDES, CAItems.RECORD_PATH_AHEAD, CAItems.RECORD_ENDOSPORE, CAItems.RECORD_WHISPER, CAItems.BLOODY_RECORD, CACollectible.UTIL_SCORE, CAItems.RECORD_OCEANWISH, CAItems.RECORD_UNDERDAWN, CAItems.RECORD_MARE_NATUS);
        addItemsToTag(MINECRAFT_PICKAXES, CAItems.TRAILRITE_PICKAXE, CAItems.HAND_ANCHOR, CAItems.TRAIL_MOP, CAItems.COMPLEX_CHITIN_PICKAXE, CAItems.PICKAXE_OCEAN_CRYSTAL, CAItems.CHITIN_PICKAXE);
        addItemsToTag(MINECRAFT_PIGLIN_LOVED, CAItems.TRAILED_GOLDEN_SWORD, CAItems.TRAIL_GOLDEN_APPLE, CACollectible.VOYAGE_OF_GOLD);
        addItemsToTag(MINECRAFT_PLANKS, CAItems.TRAIL_PLANK);
        addItemsToTag(MINECRAFT_SAL_VIENTO_DECO, CAItems.SALTWIND_SANDSTONE, CAItems.CHISELED_SALTWIND_SANDSTONE, CAItems.SMOOTH_SALTWIND_SANDATONE, CAItems.SALTWIND_COLUMN, CAItems.SALTWIND_BRICK, CAItems.SALTWIND_SMOOTH_BRICK);
        addItemsToTag(MINECRAFT_SHOVELS, CAItems.TRAILRITE_SHOVEL, CAItems.COMPLEX_CHITIN_SHOVEL, CAItems.SHOVEL_OCEAN_CRYSTAL, CAItems.CHITIN_SHOVEL);
        addItemsToTag(MINECRAFT_SMALL_FLOWERS, CAItems.TRAIL_MUSHROOM);
        addItemsToTag(MINECRAFT_STONE_BUTTONS, CAItems.TRAIL_BUTTON);
        addItemsToTag(MINECRAFT_SWORDS, CAItems.SKADI_SWORD, CAItems.AEGIR_SWORD, CAItems.BROKEN_SEA, CAItems.AEGIR_LANCET, CAItems.APOCATA_SWORD, CAItems.LANC_XIAO, CAItems.LONG_SWORD_OF_KNIGHT_CORPUS, CAItems.IRON_SWORD_OF_KNIGHT_CORPUS, CAItems.TRAILRITE_SWORD, CAItems.TRAILED_GOLDEN_SWORD, CAItems.TRAILED_NETHERITE_SWORD, CAItems.TRAILED_DIAMOND_SWORD, CAItems.TRAILED_IRON_SWORD, CAItems.TRAILED_STONE_SWORD, CAItems.TRAILED_WOODEN_SWORD, CAItems.COMPLEX_CHITIN_SWORD, CAItems.CHITIN_SWORD, CAItems.SWORD_OCEAN_CRYSTAL, CAItems.THE_SPEAR, CAItems.LEGENDARY_SPEAR);
        addItemsToTag(MINECRAFT_TOOLS, CAItems.THE_SPEAR, CAItems.SWORD_OCEAN_CRYSTAL, CAItems.CHITIN_PICKAXE, CAItems.PICKAXE_OCEAN_CRYSTAL, CAItems.COMPLEX_CHITIN_PICKAXE, CAItems.TRAILRITE_PICKAXE, CAItems.CHITIN_AXE, CAItems.AXE_OCEAN_CRYSTAL, CAItems.COMPLEX_CHITIN_AXE, CAItems.TRAILRITE_AXE, CAItems.CHITIN_SWORD, CAItems.COMPLEX_CHITIN_SWORD, CAItems.TRAILED_WOODEN_SWORD, CAItems.TRAILED_STONE_SWORD, CAItems.TRAILED_IRON_SWORD, CAItems.TRAILED_DIAMOND_SWORD, CAItems.TRAILED_NETHERITE_SWORD, CAItems.TRAILED_GOLDEN_SWORD, CAItems.TRAILRITE_SWORD, CAItems.IRON_SWORD_OF_KNIGHT_CORPUS, CAItems.LONG_SWORD_OF_KNIGHT_CORPUS, CAItems.APOCATA_SWORD, CAItems.AEGIR_SWORD, CAItems.SKADI_SWORD, CAItems.CHITIN_SHOVEL, CAItems.SHOVEL_OCEAN_CRYSTAL, CAItems.COMPLEX_CHITIN_SHOVEL, CAItems.TRAILRITE_SHOVEL, CAItems.CHITIN_HOE, CAItems.HOE_OCEAN_CRYSTAL, CAItems.COMPLEX_CHITIN_HOE, CAItems.TRAILRITE_HOE, CAItems.TIDE_WAND, CAItems.TIDELINKED_WAND, CAItems.HIGHMORE_SCYTHE, CAItems.CIRCULAR_SAW, CAItems.AEGIR_LANCET, CAItems.UNAMBIGUOUS_DIRECTION, CAItems.BROKEN_SEA, CAItems.WAVECLEAVER, CAItems.UNFINISHED_BEAUTY);
        addItemsToTag(MINECRAFT_TRIM_TEMPLATES, CAItems.OCEAN_TRIM_TEMPLATE, CAItems.HUNTER_GENE, CAItems.HUNTER_GENE_SKADI, CAItems.HUNTER_GENE_ULPIANS, CAItems.HUNTER_GENE_GLADIIA, CAItems.HUNTER_GENE_SPECTER, CAItems.TIDE_HUNET_TEMPLATE, CAItems.FLAMARINE_UPGRADE_TEMPLATE);
        addItemsToTag(MINECRAFT_TRIMMABLE_ARMOR, CAItems.CHITIN_ARMOR_HELMET, CAItems.CHITIN_ARMOR_CHESTPLATE, CAItems.CHITIN_ARMOR_LEGGINGS, CAItems.CHITIN_ARMOR_BOOTS);
        addItemsToTag(MINECRAFT_TULIP, Items.RED_TULIP, Items.ORANGE_TULIP, Items.WHITE_TULIP, Items.PINK_TULIP);
        addItemsToTag(MINECRAFT_WOODEN_BUTTONS, CAItems.TRAIL_PLANK_BUTTON);
        addItemsToTag(MINECRAFT_WOODEN_FENCES, CAItems.TRAIL_PLANKS_FENCE);
        addItemsToTag(MINECRAFT_WOODEN_PRESSURE_PLATES, CAItems.TRAIL_PLANK_PRESSURE_PLATE);
        addItemsToTag(MINECRAFT_WOODEN_SLABS, CAItems.TRAIL_PLANK_SLAB);
        addItemsToTag(MINECRAFT_WOODEN_STAIRS, CAItems.TRAIL_PLANK_STAIR);
    }

    /**
     * 创建 caerula_arbor 命名空间的物品标签 key
     *
     * @param path 标签路径
     * @return 物品标签 key
     */
    private static TagKey<Item> caItemTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, path));
    }

    /**
     * 创建 forge 命名空间的物品标签 key
     *
     * @param path 标签路径
     * @return 物品标签 key
     */
    private static TagKey<Item> forgeItemTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("forge", path));
    }

    /**
     * 创建 minecraft 命名空间的物品标签 key
     *
     * @param path 标签路径
     * @return 物品标签 key
     */
    private static TagKey<Item> minecraftItemTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace(path));
    }

    /**
     * 向目标标签加入物品字段
     *
     * @param targetTag 目标标签
     * @param items     要加入的物品字段
     */
    private void addItemsToTag(TagKey<Item> targetTag, Object... items) {
        var appender = tag(targetTag);
        for (var item : items) {
            appender.add(itemKey(item));
        }
    }

    /**
     * 从注册对象或原版物品创建物品 key
     *
     * @param item 物品字段
     * @return 物品 key
     */
    private static ResourceKey<Item> itemKey(Object item) {
        if (item instanceof DeferredHolder<?,?> registryObject) {
            return ResourceKey.create(Registries.ITEM, Objects.requireNonNull(registryObject.getId()));
        }
        if (item instanceof Item itemValue) {
            return ResourceKey.create(Registries.ITEM, Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(itemValue)));
        }
        throw new IllegalArgumentException("Unsupported item field: " + item);
    }
}