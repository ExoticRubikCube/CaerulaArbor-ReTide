package com.apocalypse.caerulaarbor.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 生成方块标签数据，包括本模组、forge 和 minecraft 命名空间
 */
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

        addForgeValues("fences", "caerula_arbor:trail_planks_fence");
        addForgeValues("fences/wooden", "caerula_arbor:trail_planks_fence");
        addForgeValues("storage_blocks", "caerula_arbor:chitin_block", "caerula_arbor:ocean_crystal_block", "caerula_arbor:complex_chitin_block", "caerula_arbor:trailrite_block", "caerula_arbor:heteropic_block");
        addForgeValues("storage_blocks/complex_chitin", "caerula_arbor:complex_chitin_block");
        addForgeValues("storage_blocks/heteropic", "caerula_arbor:heteropic_block");
        addForgeValues("storage_blocks/ocean_chitin", "caerula_arbor:chitin_block");
        addForgeValues("storage_blocks/ocean_crystal", "caerula_arbor:ocean_crystal_block");
        addForgeValues("storage_blocks/trailrite", "caerula_arbor:trailrite_block");

        addMinecraftValues("beacon_base_blocks", "caerula_arbor:chitin_block", "caerula_arbor:ocean_crystal_block", "caerula_arbor:complex_chitin_block", "caerula_arbor:trailrite_block", "caerula_arbor:heteropic_block");
        addMinecraftValues("buttons", "caerula_arbor:trail_button", "caerula_arbor:trail_plank_button");
        addMinecraftValues("dirt");
        addMinecraftValues("fences", "caerula_arbor:trail_planks_fence");
        addMinecraftValues("leaves", "caerula_arbor:trail_leave");
        addMinecraftValues("logs", "caerula_arbor:trail_log", "caerula_arbor:stripped_trail_log", "caerula_arbor:nethersea_wood", "caerula_arbor:stripped_nethersea_wood");
        addMinecraftValues("mineable/axe", "caerula_arbor:stripped_nethersea_wood", "caerula_arbor:nethersea_wood", "caerula_arbor:huge_lily", "caerula_arbor:stripped_trail_log", "caerula_arbor:trail_plank_stair", "caerula_arbor:trail_plank_slab", "caerula_arbor:trail_plank_fencedoor", "caerula_arbor:trail_planks_fence", "caerula_arbor:trail_plank", "caerula_arbor:trail_log", "caerula_arbor:trail_pumpking", "caerula_arbor:batbed_upper", "caerula_arbor:block_batbed");
        addMinecraftValues("mineable/hoe", "caerula_arbor:sea_trail_stop", "caerula_arbor:trail_pulse", "caerula_arbor:sea_trail_burnt_solid", "caerula_arbor:sea_trail_burnt", "caerula_arbor:red_ovary", "caerula_arbor:ocean_ovary", "caerula_arbor:sea_trail_solid", "caerula_arbor:sea_trail_grown", "caerula_arbor:sea_trail_growing", "caerula_arbor:sea_trail_init");
        addMinecraftValues("mineable/pickaxe", "caerula_arbor:enderina_core", "caerula_arbor:dragon_brand", "caerula_arbor:isharmla_wall_gilded", "caerula_arbor:isharmla_wall_chiesled", "caerula_arbor:isharmla_wall", "caerula_arbor:isharmla_stair", "caerula_arbor:isharmla_slab", "caerula_arbor:isharmla_brick_gilded", "caerula_arbor:isharmla_brick_chiesled", "caerula_arbor:isharmla_brick_pillar", "caerula_arbor:isharmla_brick", "caerula_arbor:living_armorstand", "caerula_arbor:saltwind_smooth_stair", "caerula_arbor:aegir_glass_arch", "caerula_arbor:aegir_glass_bar", "caerula_arbor:aegir_glass_deco", "caerula_arbor:injector", "caerula_arbor:centrifuger", "caerula_arbor:operation_table", "caerula_arbor:abandoned_sulpture", "caerula_arbor:emergency_aid_building_salviento", "caerula_arbor:emergency_aid_building", "caerula_arbor:golden_chalise", "caerula_arbor:fax", "caerula_arbor:crisis_table", "caerula_arbor:undertide_table", "caerula_arbor:bomb_copper", "caerula_arbor:trail_stone", "caerula_arbor:saltwind_stair", "caerula_arbor:saltwind_smooth_slab", "caerula_arbor:saltwind_brick_slab", "caerula_arbor:saltwind_smooth_brick", "caerula_arbor:saltwind_brick", "caerula_arbor:white_chitin_block", "caerula_arbor:cracked_trail_brick", "caerula_arbor:smooth_saltwind_sand_wall", "caerula_arbor:chieseled_saltwind_sand_wall", "caerula_arbor:saltwind_sand_wall", "caerula_arbor:smooth_saltwind_sand_slab", "caerula_arbor:smooth_saltwind_sand_stair", "caerula_arbor:saltwind_sand_slab", "caerula_arbor:saltwind_sand_stair", "caerula_arbor:smooth_saltwind_sandatone", "caerula_arbor:chiseled_saltwind_sandstone", "caerula_arbor:saltwind_sandstone", "caerula_arbor:trail_wall", "caerula_arbor:trailrite_block", "caerula_arbor:trail_debris", "caerula_arbor:trail_tile", "caerula_arbor:trail_pressure_plate", "caerula_arbor:trail_stair", "caerula_arbor:trail_slab", "caerula_arbor:trail_brick", "caerula_arbor:block_recorder", "caerula_arbor:tide_observation", "caerula_arbor:complex_chitin_block", "caerula_arbor:ocean_crystal_block", "caerula_arbor:allay_block", "caerula_arbor:chitin_block", "caerula_arbor:bomb_trailer", "caerula_arbor:block_fate", "caerula_arbor:block_crystal", "caerula_arbor:block_extension", "caerula_arbor:block_spear", "caerula_arbor:block_crown", "caerula_arbor:kings_armor", "caerula_arbor:emergency_light");
        addMinecraftValues("mineable/shovel", "caerula_arbor:nethersea_soul_sand", "caerula_arbor:saltsand", "caerula_arbor:ocean_farmland");
        addMinecraftValues("needs_diamond_tool", "caerula_arbor:injector", "caerula_arbor:centrifuger", "caerula_arbor:operation_table", "caerula_arbor:undertide_table", "caerula_arbor:trail_debris");
        addMinecraftValues("needs_stone_tool", "caerula_arbor:isharmla_wall_gilded", "caerula_arbor:isharmla_wall", "caerula_arbor:isharmla_stair", "caerula_arbor:isharmla_slab", "caerula_arbor:isharmla_brick_chiesled", "caerula_arbor:isharmla_brick_pillar", "caerula_arbor:isharmla_brick", "caerula_arbor:abandoned_sulpture");
        addMinecraftValues("planks", "caerula_arbor:trail_plank");
        addMinecraftValues("saplings", "caerula_arbor:nethersea_sampling");
        addMinecraftValues("slabs", "caerula_arbor:isharmla_slab", "caerula_arbor:saltwind_smooth_slab", "caerula_arbor:saltwind_brick_slab", "caerula_arbor:trail_plank_slab", "caerula_arbor:smooth_saltwind_sand_slab", "caerula_arbor:saltwind_sand_slab", "caerula_arbor:trail_slab");
        addMinecraftValues("soul_fire_base_blocks", "caerula_arbor:nethersea_soul_sand");
        addMinecraftValues("soul_speed_blocks", "caerula_arbor:nethersea_soul_sand");
        addMinecraftValues("stairs", "caerula_arbor:isharmla_stair", "caerula_arbor:saltwind_smooth_stair", "caerula_arbor:saltwind_stair", "caerula_arbor:trail_plank_stair", "caerula_arbor:smooth_saltwind_sand_stair", "caerula_arbor:saltwind_sand_stair", "caerula_arbor:trail_stair");
        addMinecraftValues("stone_buttons", "caerula_arbor:trail_button");
        addMinecraftValues("walls", "caerula_arbor:isharmla_wall_gilded", "caerula_arbor:isharmla_wall_chiesled", "caerula_arbor:isharmla_wall", "caerula_arbor:smooth_saltwind_sand_wall", "caerula_arbor:chieseled_saltwind_sand_wall", "caerula_arbor:saltwind_sand_wall", "caerula_arbor:trail_wall");
        addMinecraftValues("wither_immune", "caerula_arbor:complex_chitin_block", "caerula_arbor:ocean_glass", "caerula_arbor:ocean_glasspane", "caerula_arbor:tide_observation", "caerula_arbor:anchor_lower", "caerula_arbor:anchor_medium", "caerula_arbor:anchor_upper", "caerula_arbor:trail_tile", "caerula_arbor:trail_debris", "caerula_arbor:tide_bishop_core", "caerula_arbor:heteropic_block", "caerula_arbor:chestmega_spawner", "caerula_arbor:block_chestfish", "caerula_arbor:undertide_table", "caerula_arbor:undertide_spawn", "caerula_arbor:highmore_spawnblock", "caerula_arbor:crisis_table", "caerula_arbor:highmore_spawning_block", "caerula_arbor:golden_chalise", "caerula_arbor:emergency_aid_building", "caerula_arbor:emergency_aid_building_salviento", "caerula_arbor:mizuki_statue", "caerula_arbor:nethersea_soul_sand", "caerula_arbor:abandoned_sulpture", "caerula_arbor:endspeaker_nest", "caerula_arbor:operation_table", "caerula_arbor:centrifuger", "caerula_arbor:injector", "caerula_arbor:isharmla_remain", "caerula_arbor:aegir_glass_deco", "caerula_arbor:aegir_glass_bar", "caerula_arbor:aegir_glass_arch", "caerula_arbor:enderina_core");
        addMinecraftValues("wooden_buttons", "caerula_arbor:trail_plank_button");
        addMinecraftValues("wooden_fences", "caerula_arbor:trail_planks_fence");
        addMinecraftValues("wooden_pressure_plates", "caerula_arbor:trail_plank_pressure_plate");
        addMinecraftValues("wooden_slabs", "caerula_arbor:trail_plank_slab");
        addMinecraftValues("wooden_stairs", "caerula_arbor:trail_plank_stair");
    }
}
