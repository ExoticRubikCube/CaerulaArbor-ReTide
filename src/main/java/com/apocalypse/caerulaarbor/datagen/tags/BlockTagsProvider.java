package com.apocalypse.caerulaarbor.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BlockTagsProvider extends TagsProvider.RegistryTagsProvider<Block> {
    public BlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                             @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.BLOCK, lookupProvider, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        addValues("blow_up", "caerula_arbor:sea_trail_init", "caerula_arbor:sea_trail_growing", "caerula_arbor:sea_trail_grown", "caerula_arbor:sea_trail_solid", "caerula_arbor:ocean_ovary", "caerula_arbor:trail_leave", "caerula_arbor:sea_trail_stop");
        addValues("cannot_cover", "caerula_arbor:trail_mushroom", "caerula_arbor:viviparous_lily", "caerula_arbor:huge_lily", "caerula_arbor:deep_seagrass", "caerula_arbor:sea_trail_stop", "caerula_arbor:sea_trail_init", "caerula_arbor:sea_trail_growing", "caerula_arbor:sea_trail_grown", "caerula_arbor:redstone_iris", "caerula_arbor:redstoneiris_seeding", "caerula_arbor:ocean_farmland", "caerula_arbor:sea_trail_solid", "caerula_arbor:trail_brick", "caerula_arbor:trail_slab", "caerula_arbor:trail_stair", "caerula_arbor:trail_pressure_plate", "caerula_arbor:trail_tile", "caerula_arbor:trail_pumpking", "caerula_arbor:trail_debris", "caerula_arbor:trail_log", "caerula_arbor:trail_leave", "caerula_arbor:trail_plank", "caerula_arbor:trail_planks_fence", "caerula_arbor:trail_plank_fencedoor", "caerula_arbor:trail_plank_slab", "caerula_arbor:trail_plank_stair", "caerula_arbor:stripped_trail_log", "caerula_arbor:sea_trail_burnt", "caerula_arbor:sea_trail_burnt_solid", "caerula_arbor:cracked_trail_brick", "caerula_arbor:white_chitin_block", "caerula_arbor:trail_stone", "caerula_arbor:trail_pulse", "caerula_arbor:mizuki_statue", "caerula_arbor:nethersea_soul_sand", "caerula_arbor:stripped_nethersea_wood", "caerula_arbor:dragon_brand", "caerula_arbor:enderina_core", "caerula_arbor:nethersea_sampling");
        addValues("errodable", "minecraft:cobbled_deepslate", "minecraft:end_stone", "minecraft:infested_cobblestone", "minecraft:infested_stone_bricks", "minecraft:infested_mossy_stone_bricks", "minecraft:infested_cracked_stone_bricks", "minecraft:infested_chiseled_stone_bricks", "minecraft:hay_block", "minecraft:prismarine", "minecraft:dead_tube_coral_block", "minecraft:dead_brain_coral_block", "minecraft:dead_bubble_coral_block", "minecraft:dead_fire_coral_block", "minecraft:dead_horn_coral_block", "minecraft:bee_nest", "minecraft:honeycomb_block", "minecraft:polished_basalt", "minecraft:smooth_basalt", "minecraft:ochre_froglight", "minecraft:verdant_froglight", "minecraft:pearlescent_froglight", "minecraft:cobblestone", "minecraft:mossy_cobblestone", "#minecraft:wool", "#minecraft:dirt", "minecraft:sand", "minecraft:red_sand", "minecraft:soul_sand", "#minecraft:mineable/shovel", "#minecraft:mineable/hoe");
        addValues("flamarine_destroyable", "minecraft:prismarine", "minecraft:prismarine_bricks", "minecraft:dark_prismarine", "minecraft:prismarine_stairs", "minecraft:prismarine_brick_stairs", "minecraft:dark_prismarine_stairs", "minecraft:prismarine_slab", "minecraft:prismarine_brick_slab", "minecraft:dark_prismarine_slab", "minecraft:prismarine_wall", "minecraft:lantern", "minecraft:soul_lantern", "minecraft:jack_o_lantern", "minecraft:sea_lantern");
        addValues("heat", "minecraft:lava", "minecraft:magma_block", "minecraft:fire", "minecraft:soul_fire", "minecraft:campfire", "minecraft:soul_campfire", "minecraft:smoker");
        addValues("nethersea_walker_functions", "caerula_arbor:trail_brick", "caerula_arbor:cracked_trail_brick", "caerula_arbor:trail_cake", "caerula_arbor:trail_slab", "caerula_arbor:trail_stair", "caerula_arbor:trail_tile", "caerula_arbor:trail_wall", "caerula_arbor:trail_log", "caerula_arbor:trail_plank", "caerula_arbor:trail_plank_slab", "caerula_arbor:trail_plank_stair", "caerula_arbor:stripped_trail_log", "caerula_arbor:sea_trail_burnt", "caerula_arbor:sea_trail_burnt_solid", "caerula_arbor:trail_stone", "caerula_arbor:nethersea_soul_sand");
        addValues("organic", "minecraft:bamboo_planks", "minecraft:bamboo_mosaic", "minecraft:bamboo_block", "minecraft:stripped_bamboo_block", "minecraft:tube_coral_block", "minecraft:brain_coral_block", "minecraft:bubble_coral_block", "minecraft:fire_coral_block", "minecraft:horn_coral_block", "minecraft:bee_nest", "#minecraft:beehives", "#minecraft:wart_blocks", "#minecraft:completes_find_tree_tutorial", "#minecraft:coral_blocks", "minecraft:brown_mushroom_block", "minecraft:red_mushroom_block", "minecraft:mushroom_stem", "minecraft:moss_block");
        addValues("sea_trail", "caerula_arbor:sea_trail_grown", "caerula_arbor:ocean_farmland", "caerula_arbor:sea_trail_solid", "caerula_arbor:trail_pulse", "caerula_arbor:sea_trail_stop");
        addValues("trail", "caerula_arbor:sea_trail_init", "caerula_arbor:sea_trail_growing", "caerula_arbor:sea_trail_grown", "caerula_arbor:sea_trail_solid", "caerula_arbor:trail_pulse", "caerula_arbor:sea_trail_stop");
        addValues("trail_existable", "#minecraft:leaves");
    }
}
