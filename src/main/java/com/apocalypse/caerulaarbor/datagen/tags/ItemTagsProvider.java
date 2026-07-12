package com.apocalypse.caerulaarbor.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ItemTagsProvider extends TagsProvider.RegistryTagsProvider<Item> {
    public ItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
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
