package com.susen36.caerulaarbor.datagen.tags;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.init.CABlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 生成方块标签数据，覆盖 caerula_arbor、forge 和 minecraft 命名空间
 */
public class BlockTagsProvider extends TagsProvider.RegistryTagsProvider<Block> {
    private static final TagKey<Block> BLOW_UP = caBlockTag("blow_up");
    private static final TagKey<Block> CANNOT_COVER = caBlockTag("cannot_cover");
    private static final TagKey<Block> ERRODABLE = caBlockTag("errodable");
    private static final TagKey<Block> FLAMARINE_DESTROYABLE = caBlockTag("flamarine_destroyable");
    private static final TagKey<Block> HEAT = caBlockTag("heat");
    private static final TagKey<Block> NETHERSEA_WALKER_FUNCTIONS = caBlockTag("nethersea_walker_functions");
    private static final TagKey<Block> ORGANIC = caBlockTag("organic");
    private static final TagKey<Block> SEA_TRAIL = caBlockTag("sea_trail");
    private static final TagKey<Block> TRAIL = caBlockTag("trail");
    private static final TagKey<Block> TRAIL_EXISTABLE = caBlockTag("trail_existable");

    private static final TagKey<Block> FORGE_FENCES = forgeBlockTag("fences");
    private static final TagKey<Block> FORGE_WOODEN_FENCES = forgeBlockTag("fences/wooden");
    private static final TagKey<Block> FORGE_STORAGE_BLOCKS = forgeBlockTag("storage_blocks");
    private static final TagKey<Block> FORGE_STORAGE_BLOCKS_COMPLEX_CHITIN = forgeBlockTag("storage_blocks/complex_chitin");
    private static final TagKey<Block> FORGE_STORAGE_BLOCKS_HETEROPIC = forgeBlockTag("storage_blocks/heteropic");
    private static final TagKey<Block> FORGE_STORAGE_BLOCKS_OCEAN_CHITIN = forgeBlockTag("storage_blocks/ocean_chitin");
    private static final TagKey<Block> FORGE_STORAGE_BLOCKS_OCEAN_CRYSTAL = forgeBlockTag("storage_blocks/ocean_crystal");
    private static final TagKey<Block> FORGE_STORAGE_BLOCKS_TRAILRITE = forgeBlockTag("storage_blocks/trailrite");

    public BlockTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, Registries.BLOCK, lookupProvider, existingFileHelper);
    }

    /**
     * 创建 caerula_arbor 命名空间的方块标签 key
     *
     * @param path 标签路径
     * @return 方块标签 key
     */
    private static TagKey<Block> caBlockTag(String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, path));
    }

    /**
     * 创建 forge 命名空间的方块标签 key
     *
     * @param path 标签路径
     * @return 方块标签 key
     */
    private static TagKey<Block> forgeBlockTag(String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", path));
    }

    /**
     * 从注册对象创建方块 key
     *
     * @param block 方块注册对象
     * @return 方块 key
     */
    private static ResourceKey<Block> blockKey(RegistryObject<? extends Block> block) {
        return ResourceKey.create(Registries.BLOCK, Objects.requireNonNull(block.getId()));
    }

    /**
     * 从原版方块实例创建方块 key
     *
     * @param block 方块实例
     * @return 方块 key
     */
    private static ResourceKey<Block> blockKey(Block block) {
        return ResourceKey.create(Registries.BLOCK, Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)));
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        addBlocksToTag(BLOW_UP, CABlocks.SEA_TRAIL_INIT, CABlocks.SEA_TRAIL_GROWING, CABlocks.SEA_TRAIL_GROWN, CABlocks.SEA_TRAIL_SOLID, CABlocks.OCEAN_OVARY, CABlocks.TRAIL_LEAVE, CABlocks.SEA_TRAIL_STOP);
        addBlocksToTag(CANNOT_COVER, CABlocks.TRAIL_MUSHROOM, CABlocks.VIVIPAROUS_LILY, CABlocks.HUGE_LILY, CABlocks.DEEP_SEAGRASS, CABlocks.SEA_TRAIL_STOP, CABlocks.SEA_TRAIL_INIT, CABlocks.SEA_TRAIL_GROWING, CABlocks.SEA_TRAIL_GROWN, CABlocks.REDSTONE_IRIS, CABlocks.REDSTONEIRIS_SEEDING, CABlocks.OCEAN_FARMLAND, CABlocks.SEA_TRAIL_SOLID, CABlocks.TRAIL_BRICK, CABlocks.TRAIL_SLAB, CABlocks.TRAIL_STAIR, CABlocks.TRAIL_PRESSURE_PLATE, CABlocks.TRAIL_TILE, CABlocks.TRAIL_PUMPKING, CABlocks.TRAIL_DEBRIS, CABlocks.TRAIL_LOG, CABlocks.TRAIL_LEAVE, CABlocks.TRAIL_PLANK, CABlocks.TRAIL_PLANKS_FENCE, CABlocks.TRAIL_PLANK_FENCEDOOR, CABlocks.TRAIL_PLANK_SLAB, CABlocks.TRAIL_PLANK_STAIR, CABlocks.STRIPPED_TRAIL_LOG, CABlocks.SEA_TRAIL_BURNT, CABlocks.SEA_TRAIL_BURNT_SOLID, CABlocks.CRACKED_TRAIL_BRICK, CABlocks.WHITE_CHITIN_BLOCK, CABlocks.TRAIL_STONE, CABlocks.TRAIL_PULSE, CABlocks.MIZUKI_STATUE, CABlocks.NETHERSEA_SOUL_SAND, CABlocks.STRIPPED_NETHERSEA_WOOD, CABlocks.DRAGON_BRAND, CABlocks.ENDERINA_CORE, CABlocks.NETHERSEA_SAMPLING);
        addBlocksToTag(ERRODABLE, Blocks.COBBLED_DEEPSLATE, Blocks.END_STONE, Blocks.INFESTED_COBBLESTONE, Blocks.INFESTED_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.HAY_BLOCK, Blocks.PRISMARINE, Blocks.DEAD_TUBE_CORAL_BLOCK, Blocks.DEAD_BRAIN_CORAL_BLOCK, Blocks.DEAD_BUBBLE_CORAL_BLOCK, Blocks.DEAD_FIRE_CORAL_BLOCK, Blocks.DEAD_HORN_CORAL_BLOCK, Blocks.BEE_NEST, Blocks.HONEYCOMB_BLOCK, Blocks.POLISHED_BASALT, Blocks.SMOOTH_BASALT, Blocks.OCHRE_FROGLIGHT, Blocks.VERDANT_FROGLIGHT, Blocks.PEARLESCENT_FROGLIGHT, Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE, Blocks.SAND, Blocks.RED_SAND, Blocks.SOUL_SAND);
        addTagsToTag(ERRODABLE, BlockTags.WOOL, BlockTags.DIRT, BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.MINEABLE_WITH_HOE);
        addBlocksToTag(FLAMARINE_DESTROYABLE, Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.DARK_PRISMARINE, Blocks.PRISMARINE_STAIRS, Blocks.PRISMARINE_BRICK_STAIRS, Blocks.DARK_PRISMARINE_STAIRS, Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE_BRICK_SLAB, Blocks.DARK_PRISMARINE_SLAB, Blocks.PRISMARINE_WALL, Blocks.LANTERN, Blocks.SOUL_LANTERN, Blocks.JACK_O_LANTERN, Blocks.SEA_LANTERN);
        addBlocksToTag(HEAT, Blocks.LAVA, Blocks.MAGMA_BLOCK, Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE, Blocks.SMOKER);
        addBlocksToTag(NETHERSEA_WALKER_FUNCTIONS, CABlocks.TRAIL_BRICK, CABlocks.CRACKED_TRAIL_BRICK, CABlocks.TRAIL_CAKE, CABlocks.TRAIL_SLAB, CABlocks.TRAIL_STAIR, CABlocks.TRAIL_TILE, CABlocks.TRAIL_WALL, CABlocks.TRAIL_LOG, CABlocks.TRAIL_PLANK, CABlocks.TRAIL_PLANK_SLAB, CABlocks.TRAIL_PLANK_STAIR, CABlocks.STRIPPED_TRAIL_LOG, CABlocks.SEA_TRAIL_BURNT, CABlocks.SEA_TRAIL_BURNT_SOLID, CABlocks.TRAIL_STONE, CABlocks.NETHERSEA_SOUL_SAND);
        addBlocksToTag(ORGANIC, Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK, Blocks.TUBE_CORAL_BLOCK, Blocks.BRAIN_CORAL_BLOCK, Blocks.BUBBLE_CORAL_BLOCK, Blocks.FIRE_CORAL_BLOCK, Blocks.HORN_CORAL_BLOCK, Blocks.BEE_NEST, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.MUSHROOM_STEM, Blocks.MOSS_BLOCK);
        addTagsToTag(ORGANIC, BlockTags.BEEHIVES, BlockTags.WART_BLOCKS, BlockTags.COMPLETES_FIND_TREE_TUTORIAL, BlockTags.CORAL_BLOCKS);
        addBlocksToTag(SEA_TRAIL, CABlocks.SEA_TRAIL_GROWN, CABlocks.OCEAN_FARMLAND, CABlocks.SEA_TRAIL_SOLID, CABlocks.TRAIL_PULSE, CABlocks.SEA_TRAIL_STOP);
        addBlocksToTag(TRAIL, CABlocks.SEA_TRAIL_INIT, CABlocks.SEA_TRAIL_GROWING, CABlocks.SEA_TRAIL_GROWN, CABlocks.SEA_TRAIL_SOLID, CABlocks.TRAIL_PULSE, CABlocks.SEA_TRAIL_STOP);
        addTagsToTag(TRAIL_EXISTABLE, BlockTags.LEAVES);

        addBlocksToTag(FORGE_FENCES, CABlocks.TRAIL_PLANKS_FENCE);
        addBlocksToTag(FORGE_WOODEN_FENCES, CABlocks.TRAIL_PLANKS_FENCE);
        addBlocksToTag(FORGE_STORAGE_BLOCKS, CABlocks.CHITIN_BLOCK, CABlocks.OCEAN_CRYSTAL_BLOCK, CABlocks.COMPLEX_CHITIN_BLOCK, CABlocks.TRAILRITE_BLOCK, CABlocks.HETEROPIC_BLOCK);
        addBlocksToTag(FORGE_STORAGE_BLOCKS_COMPLEX_CHITIN, CABlocks.COMPLEX_CHITIN_BLOCK);
        addBlocksToTag(FORGE_STORAGE_BLOCKS_HETEROPIC, CABlocks.HETEROPIC_BLOCK);
        addBlocksToTag(FORGE_STORAGE_BLOCKS_OCEAN_CHITIN, CABlocks.CHITIN_BLOCK);
        addBlocksToTag(FORGE_STORAGE_BLOCKS_OCEAN_CRYSTAL, CABlocks.OCEAN_CRYSTAL_BLOCK);
        addBlocksToTag(FORGE_STORAGE_BLOCKS_TRAILRITE, CABlocks.TRAILRITE_BLOCK);

        addBlocksToTag(BlockTags.BEACON_BASE_BLOCKS, CABlocks.CHITIN_BLOCK, CABlocks.OCEAN_CRYSTAL_BLOCK, CABlocks.COMPLEX_CHITIN_BLOCK, CABlocks.TRAILRITE_BLOCK, CABlocks.HETEROPIC_BLOCK);
        addBlocksToTag(BlockTags.BUTTONS, CABlocks.TRAIL_BUTTON, CABlocks.TRAIL_PLANK_BUTTON);
        tag(BlockTags.DIRT);
        addBlocksToTag(BlockTags.FENCES, CABlocks.TRAIL_PLANKS_FENCE);
        addBlocksToTag(BlockTags.LEAVES, CABlocks.TRAIL_LEAVE);
        addBlocksToTag(BlockTags.LOGS, CABlocks.TRAIL_LOG, CABlocks.STRIPPED_TRAIL_LOG, CABlocks.NETHERSEA_WOOD, CABlocks.STRIPPED_NETHERSEA_WOOD);
        addBlocksToTag(BlockTags.MINEABLE_WITH_AXE, CABlocks.STRIPPED_NETHERSEA_WOOD, CABlocks.NETHERSEA_WOOD, CABlocks.HUGE_LILY, CABlocks.STRIPPED_TRAIL_LOG, CABlocks.TRAIL_PLANK_STAIR, CABlocks.TRAIL_PLANK_SLAB, CABlocks.TRAIL_PLANK_FENCEDOOR, CABlocks.TRAIL_PLANKS_FENCE, CABlocks.TRAIL_PLANK, CABlocks.TRAIL_LOG, CABlocks.TRAIL_PUMPKING, CABlocks.BATBED_UPPER, CABlocks.BLOCK_BATBED);
        addBlocksToTag(BlockTags.MINEABLE_WITH_HOE, CABlocks.SEA_TRAIL_STOP, CABlocks.TRAIL_PULSE, CABlocks.SEA_TRAIL_BURNT_SOLID, CABlocks.SEA_TRAIL_BURNT, CABlocks.RED_OVARY, CABlocks.OCEAN_OVARY, CABlocks.SEA_TRAIL_SOLID, CABlocks.SEA_TRAIL_GROWN, CABlocks.SEA_TRAIL_GROWING, CABlocks.SEA_TRAIL_INIT);
        addBlocksToTag(BlockTags.MINEABLE_WITH_PICKAXE, CABlocks.ENDERINA_CORE, CABlocks.DRAGON_BRAND, CABlocks.ISHARMLA_WALL_GILDED, CABlocks.ISHARMLA_WALL_CHIESLED, CABlocks.ISHARMLA_WALL, CABlocks.ISHARMLA_STAIR, CABlocks.ISHARMLA_SLAB, CABlocks.ISHARMLA_BRICK_GILDED, CABlocks.ISHARMLA_BRICK_CHIESLED, CABlocks.ISHARMLA_BRICK_PILLAR, CABlocks.ISHARMLA_BRICK, CABlocks.LIVING_ARMORSTAND, CABlocks.SALTWIND_SMOOTH_STAIR, CABlocks.AEGIR_GLASS_ARCH, CABlocks.AEGIR_GLASS_BAR, CABlocks.AEGIR_GLASS_DECO, CABlocks.INJECTOR, CABlocks.CENTRIFUGER, CABlocks.OPERATION_TABLE, CABlocks.ABANDONED_SULPTURE, CABlocks.EMERGENCY_AID_BUILDING_SALVIENTO, CABlocks.EMERGENCY_AID_BUILDING, CABlocks.GOLDEN_CHALISE, CABlocks.FAX, CABlocks.CRISIS_TABLE, CABlocks.UNDERTIDE_TABLE, CABlocks.BOMB_COPPER, CABlocks.TRAIL_STONE, CABlocks.SALTWIND_STAIR, CABlocks.SALTWIND_SMOOTH_SLAB, CABlocks.SALTWIND_BRICK_SLAB, CABlocks.SALTWIND_SMOOTH_BRICK, CABlocks.SALTWIND_BRICK, CABlocks.WHITE_CHITIN_BLOCK, CABlocks.CRACKED_TRAIL_BRICK, CABlocks.SMOOTH_SALTWIND_SAND_WALL, CABlocks.CHIESELED_SALTWIND_SAND_WALL, CABlocks.SALTWIND_SAND_WALL, CABlocks.SMOOTH_SALTWIND_SAND_SLAB, CABlocks.SMOOTH_SALTWIND_SAND_STAIR, CABlocks.SALTWIND_SAND_SLAB, CABlocks.SALTWIND_SAND_STAIR, CABlocks.SMOOTH_SALTWIND_SANDATONE, CABlocks.CHISELED_SALTWIND_SANDSTONE, CABlocks.SALTWIND_SANDSTONE, CABlocks.TRAIL_WALL, CABlocks.TRAILRITE_BLOCK, CABlocks.TRAIL_DEBRIS, CABlocks.TRAIL_TILE, CABlocks.TRAIL_PRESSURE_PLATE, CABlocks.TRAIL_STAIR, CABlocks.TRAIL_SLAB, CABlocks.TRAIL_BRICK, CABlocks.BLOCK_RECORDER, CABlocks.TIDE_OBSERVATION, CABlocks.COMPLEX_CHITIN_BLOCK, CABlocks.OCEAN_CRYSTAL_BLOCK, CABlocks.ALLAY_BLOCK, CABlocks.CHITIN_BLOCK, CABlocks.BOMB_TRAILER, CABlocks.BLOCK_FATE, CABlocks.BLOCK_CRYSTAL, CABlocks.BLOCK_EXTENSION, CABlocks.BLOCK_SPEAR, CABlocks.BLOCK_CROWN, CABlocks.KINGS_ARMOR, CABlocks.EMERGENCY_LIGHT);
        addBlocksToTag(BlockTags.MINEABLE_WITH_SHOVEL, CABlocks.NETHERSEA_SOUL_SAND, CABlocks.SALTSAND, CABlocks.OCEAN_FARMLAND);
        addBlocksToTag(BlockTags.NEEDS_DIAMOND_TOOL, CABlocks.INJECTOR, CABlocks.CENTRIFUGER, CABlocks.OPERATION_TABLE, CABlocks.UNDERTIDE_TABLE, CABlocks.TRAIL_DEBRIS);
        addBlocksToTag(BlockTags.NEEDS_STONE_TOOL, CABlocks.ISHARMLA_WALL_GILDED, CABlocks.ISHARMLA_WALL, CABlocks.ISHARMLA_STAIR, CABlocks.ISHARMLA_SLAB, CABlocks.ISHARMLA_BRICK_CHIESLED, CABlocks.ISHARMLA_BRICK_PILLAR, CABlocks.ISHARMLA_BRICK, CABlocks.ABANDONED_SULPTURE);
        addBlocksToTag(BlockTags.PLANKS, CABlocks.TRAIL_PLANK);
        addBlocksToTag(BlockTags.SAPLINGS, CABlocks.NETHERSEA_SAMPLING);
        addBlocksToTag(BlockTags.SLABS, CABlocks.ISHARMLA_SLAB, CABlocks.SALTWIND_SMOOTH_SLAB, CABlocks.SALTWIND_BRICK_SLAB, CABlocks.TRAIL_PLANK_SLAB, CABlocks.SMOOTH_SALTWIND_SAND_SLAB, CABlocks.SALTWIND_SAND_SLAB, CABlocks.TRAIL_SLAB);
        addBlocksToTag(BlockTags.SOUL_FIRE_BASE_BLOCKS, CABlocks.NETHERSEA_SOUL_SAND);
        addBlocksToTag(BlockTags.SOUL_SPEED_BLOCKS, CABlocks.NETHERSEA_SOUL_SAND);
        addBlocksToTag(BlockTags.STAIRS, CABlocks.ISHARMLA_STAIR, CABlocks.SALTWIND_SMOOTH_STAIR, CABlocks.SALTWIND_STAIR, CABlocks.TRAIL_PLANK_STAIR, CABlocks.SMOOTH_SALTWIND_SAND_STAIR, CABlocks.SALTWIND_SAND_STAIR, CABlocks.TRAIL_STAIR);
        addBlocksToTag(BlockTags.STONE_BUTTONS, CABlocks.TRAIL_BUTTON);
        addBlocksToTag(BlockTags.WALLS, CABlocks.ISHARMLA_WALL_GILDED, CABlocks.ISHARMLA_WALL_CHIESLED, CABlocks.ISHARMLA_WALL, CABlocks.SMOOTH_SALTWIND_SAND_WALL, CABlocks.CHIESELED_SALTWIND_SAND_WALL, CABlocks.SALTWIND_SAND_WALL, CABlocks.TRAIL_WALL);
        addBlocksToTag(BlockTags.WITHER_IMMUNE, CABlocks.COMPLEX_CHITIN_BLOCK, CABlocks.OCEAN_GLASS, CABlocks.OCEAN_GLASSPANE, CABlocks.TIDE_OBSERVATION, CABlocks.ANCHOR_LOWER, CABlocks.ANCHOR_MEDIUM, CABlocks.ANCHOR_UPPER, CABlocks.TRAIL_TILE, CABlocks.TRAIL_DEBRIS, CABlocks.TIDE_BISHOP_CORE, CABlocks.TIDE_BISHOP_CORE_EMPTY, CABlocks.HETEROPIC_BLOCK, CABlocks.CHESTMEGA_SPAWNER, CABlocks.BLOCK_CHESTFISH, CABlocks.UNDERTIDE_TABLE, CABlocks.UNDERTIDE_SPAWN, CABlocks.HIGHMORE_SPAWNBLOCK, CABlocks.CRISIS_TABLE, CABlocks.HIGHMORE_SPAWNING_BLOCK, CABlocks.GOLDEN_CHALISE, CABlocks.EMERGENCY_AID_BUILDING, CABlocks.EMERGENCY_AID_BUILDING_SALVIENTO, CABlocks.MIZUKI_STATUE, CABlocks.NETHERSEA_SOUL_SAND, CABlocks.ABANDONED_SULPTURE, CABlocks.ENDSPEAKER_NEST, CABlocks.OPERATION_TABLE, CABlocks.CENTRIFUGER, CABlocks.INJECTOR, CABlocks.ISHARMLA_REMAIN, CABlocks.AEGIR_GLASS_DECO, CABlocks.AEGIR_GLASS_BAR, CABlocks.AEGIR_GLASS_ARCH, CABlocks.ENDERINA_CORE);
        addBlocksToTag(BlockTags.WOODEN_BUTTONS, CABlocks.TRAIL_PLANK_BUTTON);
        addBlocksToTag(BlockTags.WOODEN_FENCES, CABlocks.TRAIL_PLANKS_FENCE);
        addBlocksToTag(BlockTags.WOODEN_PRESSURE_PLATES, CABlocks.TRAIL_PLANK_PRESSURE_PLATE);
        addBlocksToTag(BlockTags.WOODEN_SLABS, CABlocks.TRAIL_PLANK_SLAB);
        addBlocksToTag(BlockTags.WOODEN_STAIRS, CABlocks.TRAIL_PLANK_STAIR);
    }

    /**
     * 向目标标签加入注册方块
     *
     * @param targetTag 目标标签
     * @param blocks    要加入的方块注册对象
     */
    @SafeVarargs
    private void addBlocksToTag(TagKey<Block> targetTag, RegistryObject<? extends Block>... blocks) {
        var appender = tag(targetTag);
        for (var block : blocks) {
            appender.add(blockKey(block));
        }
    }

    /**
     * 向目标标签加入原版方块
     *
     * @param targetTag 目标标签
     * @param blocks    要加入的方块
     */
    private void addBlocksToTag(TagKey<Block> targetTag, Block... blocks) {
        var appender = tag(targetTag);
        for (var block : blocks) {
            appender.add(blockKey(block));
        }
    }
}
