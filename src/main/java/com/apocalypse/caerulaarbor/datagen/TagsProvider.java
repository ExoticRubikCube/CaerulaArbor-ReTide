package com.apocalypse.caerulaarbor.datagen;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class TagsProvider {

    private TagsProvider() {
    }

    public static void addProviders(DataGenerator generator, boolean includeServer, PackOutput output,
                                    CompletableFuture<HolderLookup.Provider> lookupProvider,
                                    @Nullable ExistingFileHelper existingFileHelper) {
        generator.addProvider(includeServer, new BlockProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(includeServer, new EntityTypeProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(includeServer, new ItemProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(includeServer, new BiomeProvider(output, lookupProvider, existingFileHelper));
    }

    private abstract static class RegistryTagsProvider<T> extends net.minecraft.data.tags.TagsProvider<T> {
        private final ResourceKey<? extends Registry<T>> registryKey;

        protected RegistryTagsProvider(PackOutput output, ResourceKey<? extends Registry<T>> registryKey,
                                       CompletableFuture<HolderLookup.Provider> lookupProvider,
                                       @Nullable ExistingFileHelper existingFileHelper) {
            super(output, registryKey, lookupProvider, CaerulaArborMod.MODID, existingFileHelper);
            this.registryKey = registryKey;
        }

        private static ResourceLocation modLocation(String path) {
            return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, path);
        }

        private static ResourceLocation entryLocation(String id) {
            int separator = id.indexOf(':');
            if (separator >= 0) {
                return ResourceLocation.fromNamespaceAndPath(id.substring(0, separator), id.substring(separator + 1));
            }
            return ResourceLocation.fromNamespaceAndPath("minecraft", id);
        }

        protected void addValues(String tagPath, String... values) {
            var appender = tag(TagKey.create(registryKey, modLocation(tagPath)));
            for (var value : values) {
                if (value.startsWith("#")) {
                    appender.addTag(TagKey.create(registryKey, entryLocation(value.substring(1))));
                } else {
                    appender.add(ResourceKey.create(registryKey, entryLocation(value)));
                }
            }
        }
    }

    private static final class BlockProvider extends RegistryTagsProvider<Block> {
        private BlockProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
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

    private static final class EntityTypeProvider extends RegistryTagsProvider<EntityType<?>> {
        private EntityTypeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                   @Nullable ExistingFileHelper existingFileHelper) {
            super(output, Registries.ENTITY_TYPE, lookupProvider, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            addValues("bossoffspring", "caerula_arbor:super_slider", "caerula_arbor:route_shaper", "caerula_arbor:bishop_fish", "caerula_arbor:tide_bishop", "caerula_arbor:tide_deathrepeller", "caerula_arbor:mega_chest", "caerula_arbor:first_to_talk", "caerula_arbor:highmore", "caerula_arbor:oceanized_brute", "caerula_arbor:izumik", "caerula_arbor:martus", "caerula_arbor:oceanized_warden", "caerula_arbor:super_big_cat", "caerula_arbor:oceanized_wardenis", "caerula_arbor:oceanized_wither", "caerula_arbor:oceanized_witheria", "caerula_arbor:endspeaker", "caerula_arbor:lingering_pathshaper", "caerula_arbor:tide_chimera", "caerula_arbor:skadi_corrupted", "caerula_arbor:oceanized_illusioner", "caerula_arbor:isharmla", "caerula_arbor:compassion_prayer", "caerula_arbor:oceanized_enderina", "caerula_arbor:thirster");
            addValues("cannot_transform", "iron_golem", "#caerula_arbor:oceanoffspring", "caerula_arbor:chitin_golem", "snow_golem", "#caerula_arbor:golems", "slime", "magma_cube", "strider", "#caerula_arbor:hunters", "#caerula_arbor:sea_friend", "vex");
            addValues("endspeaker_edible", "caerula_arbor:baselayer_abyssal", "caerula_arbor:cracker_abyssal", "caerula_arbor:guide_abyssal", "caerula_arbor:predator_abyssal", "caerula_arbor:splasher_abyssal", "caerula_arbor:umbrella_abyssal");
            addValues("golems", "caerula_arbor:chitin_golem", "caerula_arbor:spike_chest", "caerula_arbor:complex_chitin_golem", "caerula_arbor:last_knight_and_horse", "caerula_arbor:the_last_knight", "caerula_arbor:flamarine_statue", "caerula_arbor:flamarine_golem", "caerula_arbor:moist_dragon_breath");
            addValues("homo_sapiens", "#caerula_arbor:inquisition", "caerula_arbor:apocata", "caerula_arbor:gunmu");
            addValues("hunters", "caerula_arbor:skadi", "caerula_arbor:ulpians", "caerula_arbor:gladiia", "caerula_arbor:specter", "caerula_arbor:specter_doll");
            addValues("ignore_migration", "caerula_arbor:oceanized_warden", "caerula_arbor:oceanized_wardenis", "caerula_arbor:izumik", "caerula_arbor:izumik_offspring", "caerula_arbor:oceanized_wither", "caerula_arbor:martus", "caerula_arbor:tide_chimera", "caerula_arbor:moist_dragon_breath", "caerula_arbor:moist_ender_crystal");
            addValues("immue_to_inst_sanity", "caerula_arbor:oceanized_witch");
            addValues("immue_to_nethersea_brand", "#caerula_arbor:oceanoffspring", "#caerula_arbor:golems", "caerula_arbor:little_helper", "caerula_arbor:al_1_s_helper", "caerula_arbor:flamarine_statue", "caerula_arbor:flamarine_golem", "caerula_arbor:qunyou_wanted_isharmla");
            addValues("inquisition", "#caerula_arbor:warriors", "#caerula_arbor:phalax", "caerula_arbor:tribunal_healer", "caerula_arbor:irene", "caerula_arbor:saint_carmen");
            addValues("is_humanside", "#caerula_arbor:hunters", "#caerula_arbor:inquisition", "caerula_arbor:gladiia_whirl", "caerula_arbor:chitin_golem", "caerula_arbor:complex_chitin_golem", "iron_golem", "snow_golem", "villager", "caerula_arbor:last_knight_and_horse", "caerula_arbor:the_last_knight", "#caerula_arbor:oceanpet");
            addValues("izumik_discovers", "caerula_arbor:bone_fish", "caerula_arbor:cracker_abyssal", "zombie", "zombie_villager", "zombified_piglin", "drowned", "enderman", "silverfish", "elder_guardian", "guardian", "caerula_arbor:flee_fish", "blaze", "creeper", "caerula_arbor:first_to_talk", "phantom", "cave_spider", "spider", "llama", "shulker", "skeleton", "wither_skeleton", "magma_cube", "caerula_arbor:route_fractal", "caerula_arbor:oceanized_cow", "caerula_arbor:oceanized_dog", "caerula_arbor:oceanized_horse", "caerula_arbor:oceanized_pig", "caerula_arbor:oceanized_piglin", "caerula_arbor:oceanized_pillager", "caerula_arbor:oceanized_ravager", "caerula_arbor:oceanized_sheep", "caerula_arbor:oceanized_villager", "caerula_arbor:oceanized_vindicator", "caerula_arbor:oceanized_wolf", "caerula_arbor:oceanized_witch", "caerula_arbor:oceanized_enderman", "caerula_arbor:oceanized_spider", "caerula_arbor:oceanized_brute", "caerula_arbor:oceanized_cat", "caerula_arbor:oceanized_evoker", "caerula_arbor:oceanized_wardenis", "caerula_arbor:oceanized_fox", "caerula_arbor:oceanized_polar_bear", "caerula_arbor:izumik_offspring", "caerula_arbor:lingering_fractal", "caerula_arbor:oceanize_rabbit", "caerula_arbor:compassion_prayer", "caerula_arbor:thirster", "caerula_arbor:nethersea_slime", "caerula_arbor:oceanized_chicken");
            addValues("marinemobs", "caerula_arbor:collector_prokaryote", "caerula_arbor:bone_fish", "caerula_arbor:apostle_prokaryote", "caerula_arbor:floater_prokaryote", "caerula_arbor:accumulator_prokaryote", "caerula_arbor:accumulator_clone", "caerula_arbor:feeder_prokaryote", "caerula_arbor:nucleic_maleficent", "caerula_arbor:depositer_prokaryote", "caerula_arbor:nautilus_headhunter");
            addValues("no_join_whirl", "glow_item_frame", "item_display", "item_frame", "zombie", "boat", "chest_boat", "minecart", "chest_minecart", "command_block_minecart", "furnace_minecart", "hopper_minecart", "spawner_minecart", "tnt_minecart", "caerula_arbor:gladiia_whirl", "caerula_arbor:al_1_s_helper", "caerula_arbor:little_helper", "caerula_arbor:isharmla_tear", "caerula_arbor:qunyou_wanted_isharmla");
            addValues("oceanelite", "caerula_arbor:baselayer_abyssal", "caerula_arbor:cracker_abyssal", "caerula_arbor:creeper_fish", "caerula_arbor:first_to_talk", "caerula_arbor:flee_fish", "caerula_arbor:guide_abyssal", "caerula_arbor:pregnant_fish", "caerula_arbor:puncture_fish", "caerula_arbor:reaper_fish", "caerula_arbor:umbrella_abyssal", "caerula_arbor:mega_chest", "caerula_arbor:apostle_prokaryote", "caerula_arbor:chest_fish", "caerula_arbor:oceanized_vindicator", "caerula_arbor:oceanized_enderman", "caerula_arbor:oceanized_ravager", "caerula_arbor:izumik_offspring", "caerula_arbor:oceanized_evoker", "caerula_arbor:the_abandoned", "caerula_arbor:nucleic_maleficent", "caerula_arbor:tidutant_rock_spider", "caerula_arbor:scream_chest_fish", "caerula_arbor:oceanized_shulker");
            addValues("oceanoffspring", "caerula_arbor:baselayer_abyssal", "caerula_arbor:creeper_fish", "caerula_arbor:fly_fish", "caerula_arbor:puncture_fish", "caerula_arbor:reaper_fish", "caerula_arbor:run_fish", "caerula_arbor:shooter_fish", "caerula_arbor:slider_fish", "caerula_arbor:super_slider", "caerula_arbor:predator_abyssal", "caerula_arbor:guide_abyssal", "caerula_arbor:splasher_abyssal", "caerula_arbor:umbrella_abyssal", "caerula_arbor:cracker_abyssal", "caerula_arbor:collector_prokaryote", "caerula_arbor:fake_offspring", "caerula_arbor:flee_fish", "caerula_arbor:pregnant_fish", "caerula_arbor:route_shaper", "caerula_arbor:route_fractal", "caerula_arbor:first_to_talk", "caerula_arbor:chiseler_fish", "caerula_arbor:bone_fish", "caerula_arbor:reaper_pet", "caerula_arbor:bishop_fish", "caerula_arbor:sons", "caerula_arbor:tide_bishop", "caerula_arbor:tide_deathrepeller", "caerula_arbor:mega_chest", "caerula_arbor:floater_prokaryote", "caerula_arbor:apostle_prokaryote", "caerula_arbor:highmore", "caerula_arbor:accumulator_prokaryote", "caerula_arbor:feeder_prokaryote", "caerula_arbor:chest_fish", "caerula_arbor:oceanized_villager", "caerula_arbor:oceanized_vindicator", "caerula_arbor:oceanized_pillager", "caerula_arbor:depositer_prokaryote", "caerula_arbor:oceanized_pig", "caerula_arbor:accumulator_clone", "caerula_arbor:oceanized_sheep", "caerula_arbor:oceanized_cow", "caerula_arbor:oceanized_horse", "caerula_arbor:oceanized_piglin", "caerula_arbor:oceanized_brute", "caerula_arbor:oceanized_spider", "caerula_arbor:oceanized_enderman", "caerula_arbor:oceanized_dog", "caerula_arbor:oceanized_wolf", "caerula_arbor:oceanized_ravager", "caerula_arbor:oceanized_witch", "caerula_arbor:izumik_offspring", "caerula_arbor:izumik", "caerula_arbor:oceanized_evoker", "caerula_arbor:the_abandoned", "caerula_arbor:martus", "caerula_arbor:oceanized_warden", "caerula_arbor:divicellular_go", "caerula_arbor:oceanized_cat", "caerula_arbor:super_big_cat", "caerula_arbor:oceanized_wardenis", "caerula_arbor:nucleic_maleficent", "caerula_arbor:oceanized_wither", "caerula_arbor:oceanized_witheria", "caerula_arbor:rocinante", "caerula_arbor:oceanized_fox", "caerula_arbor:tidutant_excrescence", "caerula_arbor:oceanized_polar_bear", "caerula_arbor:tidutant_rock_spider", "caerula_arbor:endspeaker", "caerula_arbor:lingering_pathshaper", "caerula_arbor:lingering_fractal", "caerula_arbor:tide_chimera", "caerula_arbor:skadi_corrupted", "caerula_arbor:oceanize_rabbit", "caerula_arbor:oceanized_illusioner", "caerula_arbor:ocean_illusion", "caerula_arbor:nautilus_headhunter", "caerula_arbor:oceanized_vex", "caerula_arbor:isharmla", "caerula_arbor:isharmla_tear", "caerula_arbor:compassion_prayer", "caerula_arbor:oceanized_enderina", "caerula_arbor:moist_ender_crystal", "caerula_arbor:thirster", "caerula_arbor:absorber_limb", "caerula_arbor:scream_chest_fish", "caerula_arbor:oceanized_chicken", "caerula_arbor:nethersea_slime", "caerula_arbor:oceanized_shulker");
            addValues("oceanpet", "caerula_arbor:reaper_pet", "caerula_arbor:oceanized_dog", "caerula_arbor:rocinante");
            addValues("oceanspawn", "caerula_arbor:fake_offspring", "caerula_arbor:route_fractal", "caerula_arbor:sons", "caerula_arbor:accumulator_clone", "caerula_arbor:chest_fish", "caerula_arbor:oceanized_villager", "caerula_arbor:oceanized_vindicator", "caerula_arbor:oceanized_pillager", "caerula_arbor:oceanized_pig", "caerula_arbor:oceanized_sheep", "caerula_arbor:oceanized_cow", "caerula_arbor:oceanized_horse", "caerula_arbor:oceanized_piglin", "caerula_arbor:oceanized_spider", "caerula_arbor:oceanized_enderman", "caerula_arbor:oceanized_ravager", "caerula_arbor:oceanized_witch", "caerula_arbor:izumik_offspring", "caerula_arbor:oceanized_evoker", "caerula_arbor:the_abandoned", "caerula_arbor:divicellular_go", "caerula_arbor:oceanized_cat", "caerula_arbor:oceanized_fox", "caerula_arbor:tidutant_excrescence", "caerula_arbor:oceanized_polar_bear", "caerula_arbor:tidutant_rock_spider", "caerula_arbor:lingering_fractal", "caerula_arbor:oceanize_rabbit", "caerula_arbor:oceanized_illusioner", "caerula_arbor:ocean_illusion", "caerula_arbor:nautilus_headhunter", "caerula_arbor:oceanized_vex", "caerula_arbor:isharmla_tear", "caerula_arbor:moist_ender_crystal", "caerula_arbor:absorber_limb", "caerula_arbor:scream_chest_fish", "caerula_arbor:oceanized_chicken", "caerula_arbor:nethersea_slime", "caerula_arbor:oceanized_shulker");
            addValues("phalax", "caerula_arbor:correctional_phalax_vanguard", "caerula_arbor:correctional_phalanxy_infantry");
            addValues("portable", "caerula_arbor:the_abandoned", "#caerula_arbor:homo_sapiens", "#caerula_arbor:hunters");
            addValues("sea_friend", "glow_squid", "squid", "elder_guardian", "guardian", "axolotl", "panda", "dolphin", "tadpole", "pufferfish", "creeper", "ghast", "turtle", "tropical_fish", "salmon", "cod", "caerula_arbor:apocata", "caerula_arbor:little_helper", "caerula_arbor:al_1_s_helper", "armor_stand", "caerula_arbor:moist_dragon_breath", "caerula_arbor:spike_chest");
            addValues("skip_migration", "caerula_arbor:oceanized_wither", "caerula_arbor:tide_chimera", "caerula_arbor:moist_dragon_breath", "caerula_arbor:moist_ender_crystal");
            addValues("warriors", "caerula_arbor:junior_warrior_priest", "caerula_arbor:warrior_priest");
            addValues("with_low_sanity_modifier", "#caerula_arbor:inquisition");
            addValues("with_lower_sanity_modifier", "#caerula_arbor:oceanoffspring");
            addValues("with_lowest_sanity_modifier", "#caerula_arbor:oceanelite", "warden");
            addValues("with_lowest_smaller_sanity_modifier", "#forge:bosses", "#caerula_arbor:hunters");
            addValues("with_lowest_smallest_sanity_modifier", "iron_golem", "caerula_arbor:chitin_golem", "caerula_arbor:the_last_knight", "caerula_arbor:last_knight_and_horse", "caerula_arbor:complex_chitin_golem", "caerula_arbor:flamarine_statue", "caerula_arbor:flamarine_golem");
            addValues("with_zero_sanity_modifier", "caerula_arbor:gunmu", "caerula_arbor:al_1_s_helper", "caerula_arbor:little_helper", "caerula_arbor:izumik", "caerula_arbor:qunyou_wanted_isharmla", "caerula_arbor:isharmla_tear");
        }
    }

    private static final class ItemProvider extends RegistryTagsProvider<Item> {
        private ItemProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                             @Nullable ExistingFileHelper existingFileHelper) {
            super(output, Registries.ITEM, lookupProvider, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            addValues("animus", "caerula_arbor:caerula_heart", "caerula_arbor:incandescent_anima", "caerula_arbor:leviathan_animus", "caerula_arbor:moist_dragon_heart");
            addValues("any_coral", "minecraft:dead_tube_coral_block", "minecraft:dead_brain_coral_block", "minecraft:dead_bubble_coral_block", "minecraft:dead_fire_coral_block", "minecraft:dead_horn_coral_block", "minecraft:tube_coral_block", "minecraft:brain_coral_block", "minecraft:bubble_coral_block", "minecraft:fire_coral_block", "minecraft:horn_coral_block", "minecraft:dead_tube_coral", "minecraft:dead_brain_coral", "minecraft:dead_bubble_coral", "minecraft:dead_fire_coral", "minecraft:dead_horn_coral", "minecraft:tube_coral", "minecraft:brain_coral", "minecraft:bubble_coral", "minecraft:fire_coral", "minecraft:horn_coral", "minecraft:dead_tube_coral_fan", "minecraft:dead_brain_coral_fan", "minecraft:dead_bubble_coral_fan", "minecraft:dead_fire_coral_fan", "minecraft:dead_horn_coral_fan", "minecraft:tube_coral_fan", "minecraft:brain_coral_fan", "minecraft:bubble_coral_fan", "minecraft:fire_coral_fan", "minecraft:horn_coral_fan");
            addValues("archfiend_relics", "caerula_arbor:archfiends_artifact", "caerula_arbor:archfiends_flag", "caerula_arbor:archfiends_bed", "caerula_arbor:royal_fate");
            addValues("common_relics", "caerula_arbor:aromatic_coffee", "caerula_arbor:solo_music_box", "caerula_arbor:redstone_iris_flower", "caerula_arbor:odd_flute", "caerula_arbor:voyage_of_gold", "caerula_arbor:piglin_diary", "caerula_arbor:toponym_textology", "caerula_arbor:kettle", "caerula_arbor:allay_sculpture", "caerula_arbor:bat_bed", "caerula_arbor:omni_key", "caerula_arbor:score", "caerula_arbor:rescission", "caerula_arbor:smelly_hemostatic");
            addValues("cream_sword", "caerula_arbor:trailed_wooden_sword", "caerula_arbor:trailed_stone_sword", "caerula_arbor:trailed_iron_sword", "caerula_arbor:trailed_diamond_sword", "caerula_arbor:trailed_netherite_sword", "caerula_arbor:trailed_golden_sword");
            addValues("cursed", "caerula_arbor:relic_curse_emelight", "caerula_arbor:relic_cursed_glowbody", "caerula_arbor:relic_cursed_research", "caerula_arbor:caerula_heart");
            addValues("enchantable/nethersea_walker", "#forge:armor/boots");
            addValues("enchantable/sanity", "#minecraft:enchantable/weapon", "#minecraft:swords", "#minecraft:axes");
            addValues("enchantable/sanity_defend", "#minecraft:trimmable_armor", "#forge:armors", "#forge:armor/leggings", "#forge:armor/boots", "#forge:armor/helmets", "#forge:armor/chestplates");
            addValues("enchantable/seaborn_killer", "#minecraft:axes", "#minecraft:swords", "caerula_arbor:unambiguous_direction", "caerula_arbor:unfinished_beauty", "caerula_arbor:wavecleaver", "#minecraft:enchantable/weapon");
            addValues("endspeaker_chapter", "caerula_arbor:dictationless_chapter", "caerula_arbor:dictation_chapter");
            addValues("fish_food", "caerula_arbor:sea_trail_mor", "caerula_arbor:ocean_fibre", "caerula_arbor:cooked_mor", "caerula_arbor:broken_cell_cluster", "caerula_arbor:cell_cluster", "caerula_arbor:caramel_mor", "caerula_arbor:ocean_peduncle", "caerula_arbor:elite_peduncle", "caerula_arbor:cooked_fibre", "caerula_arbor:cooked_broken_cell_cluster", "caerula_arbor:cooked_cell_cluster", "caerula_arbor:cooked_peduncle", "caerula_arbor:fake_egg", "caerula_arbor:real_egg", "caerula_arbor:cooked_fakeegg", "caerula_arbor:collector_meat", "caerula_arbor:cooked_collector", "caerula_arbor:claw", "caerula_arbor:cooked_claw");
            addValues("gene", "caerula_arbor:dna_reaper", "caerula_arbor:hunter_gene", "caerula_arbor:dna_horse", "caerula_arbor:rocinante_injector", "caerula_arbor:hunter_gene_skadi", "caerula_arbor:hunter_gene_ulpians", "caerula_arbor:hunter_gene_gladiia", "caerula_arbor:hunter_gene_specter", "caerula_arbor:nurture_gene_set", "caerula_arbor:gene_sample_normal", "caerula_arbor:gene_sample_upgraded", "caerula_arbor:gene_sample_superb");
            addValues("hand_relics", "caerula_arbor:hand_of_thorns", "caerula_arbor:hand_of_strangle", "caerula_arbor:hand_of_fertiliy", "caerula_arbor:hand_of_speed", "caerula_arbor:hand_of_barren", "caerula_arbor:hand_of_spotless", "caerula_arbor:hand_of_firework", "caerula_arbor:hand_of_engrave", "caerula_arbor:hand_sword");
            addValues("king_relics", "caerula_arbor:relic_crown", "caerula_arbor:kings_armour", "caerula_arbor:kings_spear", "caerula_arbor:kings_extension", "caerula_arbor:kings_crystal");
            addValues("knight_equipment", "caerula_arbor:iron_sword_of_knight_corpus", "caerula_arbor:long_sword_of_knight_corpus", "caerula_arbor:knight_iron_helmet", "caerula_arbor:knight_iron_chestplate", "caerula_arbor:knight_iron_leggings", "caerula_arbor:knight_iron_boots");
            addValues("moist_item", "caerula_arbor:moist_echo_shard", "caerula_arbor:moist_star", "caerula_arbor:water_logged_pearl", "caerula_arbor:moist_crystal_item", "caerula_arbor:moist_dragon_heart");
            addValues("nethersea_logs", "caerula_arbor:nethersea_wood", "caerula_arbor:stripped_nethersea_wood", "caerula_arbor:trail_log", "caerula_arbor:stripped_trail_log");
            addValues("nethersea_protective", "caerula_arbor:trailrite_axe", "caerula_arbor:trailrite_sword", "caerula_arbor:trailrite_pickaxe", "caerula_arbor:trailrite_hoe", "caerula_arbor:trailrite_shovel", "caerula_arbor:trail_mop");
            addValues("relic_advanced", "#caerula_arbor:king_relics", "#caerula_arbor:archfiend_relics", "caerula_arbor:survivor_contract", "caerula_arbor:chitin_knife", "caerula_arbor:unripe_yearning", "caerula_arbor:crimson_treaty", "#caerula_arbor:hand_relics");
            addValues("relic_generic", "#caerula_arbor:hand_relics", "#caerula_arbor:archfiend_relics", "#caerula_arbor:king_relics", "caerula_arbor:survivor_contract", "caerula_arbor:crimson_treaty", "caerula_arbor:bowl_seagrass", "caerula_arbor:golden_storm", "caerula_arbor:coffee_candy", "caerula_arbor:canned_cherry", "caerula_arbor:rainbow_candy", "caerula_arbor:aromatic_coffee", "caerula_arbor:solo_music_box", "caerula_arbor:redstone_iris_flower", "caerula_arbor:odd_flute", "caerula_arbor:voyage_of_gold", "caerula_arbor:piglin_diary", "caerula_arbor:toponym_textology", "caerula_arbor:kettle", "caerula_arbor:chitin_knife", "caerula_arbor:allay_sculpture", "caerula_arbor:bat_bed", "caerula_arbor:proof_of_longevity", "caerula_arbor:omni_key", "caerula_arbor:score", "caerula_arbor:rescission", "caerula_arbor:guardian_stare", "caerula_arbor:unripe_yearning", "caerula_arbor:meat_can", "caerula_arbor:smelly_hemostatic");
            addValues("seaborn_loots", "caerula_arbor:ocean_phloem", "caerula_arbor:ocean_fibre", "caerula_arbor:ocean_eye", "caerula_arbor:ocean_crystal", "caerula_arbor:ocean_cutin", "caerula_arbor:ocean_chitin", "#caerula_arbor:fish_food", "#caerula_arbor:moist_item", "caerula_arbor:heteropic_piece", "caerula_arbor:broken_ocean_cell", "caerula_arbor:broken_cell_cluster", "caerula_arbor:ocean_cell", "caerula_arbor:cell_cluster");
            addValues("self_mendable", "caerula_arbor:complex_chitin_sword", "caerula_arbor:complex_chitin_pickaxe", "caerula_arbor:complex_chitin_axe", "caerula_arbor:complex_chitin_shovel", "caerula_arbor:complex_chitin_hoe", "caerula_arbor:legendary_spear", "caerula_arbor:trailed_wooden_sword", "caerula_arbor:trailed_stone_sword", "caerula_arbor:trailed_iron_sword", "caerula_arbor:trailed_diamond_sword", "caerula_arbor:trailed_netherite_sword", "caerula_arbor:trailed_golden_sword", "caerula_arbor:phloem_bow", "caerula_arbor:trailrite_axe", "caerula_arbor:trailrite_sword", "caerula_arbor:path_inaugurator", "caerula_arbor:complex_chitin_bow");
        }
    }

    private static final class BiomeProvider extends RegistryTagsProvider<Biome> {
        private BiomeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              @Nullable ExistingFileHelper existingFileHelper) {
            super(output, Registries.BIOME, lookupProvider, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            addValues("common_spawn_biome", "caerula_arbor:branded_land", "birch_forest", "flower_forest", "forest", "old_growth_birch_forest", "plains", "snowy_plains", "sunflower_plains", "meadow", "stony_shore", "beach", "snowy_beach", "#caerula_arbor:underwater_spawn_biome", "#minecraft:is_beach", "#minecraft:is_forest");
            addValues("danger_spawn_biome", "caerula_arbor:branded_land", "beach", "snowy_beach", "stony_shore", "#minecraft:is_beach", "#caerula_arbor:marine_spawn_biome");
            addValues("deepmarine_spawn_biome", "deep_cold_ocean", "deep_frozen_ocean", "deep_lukewarm_ocean", "deep_ocean", "#minecraft:is_deep_ocean", "caerula_arbor:branded_land");
            addValues("marine_spawn_biome", "cold_ocean", "deep_cold_ocean", "deep_frozen_ocean", "deep_lukewarm_ocean", "deep_ocean", "frozen_ocean", "lukewarm_ocean", "ocean", "warm_ocean", "#minecraft:is_ocean", "caerula_arbor:branded_land");
            addValues("rare_spawn_biome", "caerula_arbor:branded_land", "snowy_plains", "sunflower_plains", "beach", "snowy_beach", "stony_shore", "#caerula_arbor:marine_spawn_biome", "#minecraft:is_forest");
            addValues("underwater_spawn_biome", "cold_ocean", "deep_cold_ocean", "deep_frozen_ocean", "deep_lukewarm_ocean", "deep_ocean", "frozen_ocean", "lukewarm_ocean", "ocean", "warm_ocean", "frozen_river", "river", "#minecraft:is_river", "#minecraft:is_ocean", "#minecraft:is_deep_ocean", "caerula_arbor:branded_land");
        }
    }
}
