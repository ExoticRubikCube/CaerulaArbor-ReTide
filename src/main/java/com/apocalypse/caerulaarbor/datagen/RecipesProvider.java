package com.apocalypse.caerulaarbor.datagen;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class RecipesProvider extends RecipeProvider {

    public RecipesProvider(PackOutput output) {
        super(output);
    }

    private static void shaped(Consumer<FinishedRecipe> writer, String id, String category, @Nullable String group, JsonObject result, String[] pattern, KeyEntry... keys) {
        var recipe = baseRecipe("minecraft:crafting_shaped");
        recipe.addProperty("category", category);
        addGroup(recipe, group);
        var patternArray = new JsonArray();
        for (var line : pattern) {
            patternArray.add(line);
        }
        recipe.add("pattern", patternArray);

        var key = new JsonObject();
        for (var entry : keys) {
            key.add(String.valueOf(entry.key()), entry.ingredient().toJson());
        }
        recipe.add("key", key);
        recipe.add("result", result);
        save(writer, id, recipe);
    }

    private static void shapeless(Consumer<FinishedRecipe> writer, String id, String category, @Nullable String group, JsonObject result, IngredientEntry[] ingredients) {
        var recipe = baseRecipe("minecraft:crafting_shapeless");
        recipe.addProperty("category", category);
        addGroup(recipe, group);
        recipe.add("ingredients", ingredientsToJson(ingredients));
        recipe.add("result", result);
        save(writer, id, recipe);
    }

    private static void cooking(Consumer<FinishedRecipe> writer, String id, String type, String category, @Nullable String group, IngredientEntry ingredient, String result, float experience, int cookingTime) {
        var recipe = baseRecipe(type);
        recipe.addProperty("category", category);
        addGroup(recipe, group);
        recipe.add("ingredient", ingredient.toJson());
        recipe.addProperty("result", result);
        recipe.addProperty("experience", experience);
        recipe.addProperty("cookingtime", cookingTime);
        save(writer, id, recipe);
    }

    private static void stonecutting(Consumer<FinishedRecipe> writer, String id, IngredientEntry ingredient, String result, int count) {
        var recipe = baseRecipe("minecraft:stonecutting");
        recipe.add("ingredient", ingredient.toJson());
        recipe.addProperty("result", result);
        recipe.addProperty("count", count);
        save(writer, id, recipe);
    }

    private static void smithingTransform(Consumer<FinishedRecipe> writer, String id, IngredientEntry template, IngredientEntry base, IngredientEntry addition, JsonObject result) {
        var recipe = baseRecipe("minecraft:smithing_transform");
        recipe.add("template", template.toJson());
        recipe.add("base", base.toJson());
        recipe.add("addition", addition.toJson());
        recipe.add("result", result);
        save(writer, id, recipe);
    }

    @SuppressWarnings("SameParameterValue")
    private static void patchouliBook(Consumer<FinishedRecipe> writer, String id, String book, IngredientEntry[] ingredients) {
        var recipe = baseRecipe("patchouli:shapeless_book_recipe");
        recipe.add("ingredients", ingredientsToJson(ingredients));
        recipe.addProperty("book", book);
        save(writer, id, recipe);
    }

    private static JsonObject baseRecipe(String type) {
        var recipe = new JsonObject();
        recipe.addProperty("type", type);
        return recipe;
    }

    private static void addGroup(JsonObject recipe, @Nullable String group) {
        if (group != null) {
            recipe.addProperty("group", group);
        }
    }

    private static JsonArray ingredientsToJson(IngredientEntry[] ingredients) {
        var array = new JsonArray();
        for (var ingredient : ingredients) {
            array.add(ingredient.toJson());
        }
        return array;
    }

    private static void save(Consumer<FinishedRecipe> writer, String id, JsonObject recipe) {
        writer.accept(new JsonFinishedRecipe(modLoc(id), recipe));
    }

    private static String[] pattern(String... pattern) {
        return pattern;
    }

    private static IngredientEntry[] ingredients(IngredientEntry... ingredients) {
        return ingredients;
    }

    private static KeyEntry key(char key, IngredientEntry ingredient) {
        return new KeyEntry(key, ingredient);
    }

    private static IngredientEntry item(String item) {
        return new IngredientEntry("item", item);
    }

    private static IngredientEntry tag(String tag) {
        return new IngredientEntry("tag", tag);
    }

    private static JsonObject result(String item) {
        var result = new JsonObject();
        result.addProperty("item", item);
        return result;
    }

    private static JsonObject result(String item, int count) {
        var result = result(item);
        result.addProperty("count", count);
        return result;
    }

    private static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, path);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> writer) {
        shaped(
                writer,
                "aegir_glass_arch",
                "building",
                null,
                result("caerula_arbor:aegir_glass_arch", 3),
                pattern(
                        "aa",
                        "a "
                ),
                key('a', item("caerula_arbor:aegir_glass_deco"))
        );
        shaped(
                writer,
                "aegir_glass_deco",
                "building",
                null,
                result("caerula_arbor:aegir_glass_deco", 4),
                pattern(
                        "aba",
                        "bcb",
                        "aba"
                ),
                key('a', item("caerula_arbor:trail_powder")),
                key('b', item("minecraft:glass")),
                key('c', item("caerula_arbor:ocean_crystal"))
        );
        shaped(
                writer,
                "all_smooth_brick",
                "building",
                null,
                result("caerula_arbor:saltwind_smooth_brick", 4),
                pattern(
                        "aa",
                        "aa"
                ),
                key('a', item("caerula_arbor:smooth_saltwind_sandatone"))
        );
        shaped(
                writer,
                "all_smooth_slab",
                "building",
                null,
                result("caerula_arbor:saltwind_smooth_slab", 6),
                pattern(
                        "aaa"
                ),
                key('a', item("caerula_arbor:saltwind_smooth_brick"))
        );
        shaped(
                writer,
                "all_smooth_stair",
                "building",
                null,
                result("caerula_arbor:saltwind_smooth_stair", 4),
                pattern(
                        "a  ",
                        "aa ",
                        "aaa"
                ),
                key('a', item("caerula_arbor:saltwind_smooth_brick"))
        );
        shapeless(
                writer,
                "another_flamarine_copy",
                "misc",
                null,
                result("caerula_arbor:flamarine_upgrade_template", 2),
                ingredients(item("caerula_arbor:flamarine_upgrade_template"), item("minecraft:netherite_upgrade_smithing_template"), item("caerula_arbor:trailrite"))
        );
        shapeless(
                writer,
                "assemble_cake_trail",
                "misc",
                null,
                result("caerula_arbor:trail_cake", 1),
                ingredients(item("caerula_arbor:trail_cake_piece"), item("caerula_arbor:trail_cake_piece"), item("caerula_arbor:trail_cake_piece"), item("caerula_arbor:trail_cake_piece"))
        );
        shapeless(
                writer,
                "assemble_caramelcake",
                "misc",
                null,
                result("caerula_arbor:caramel_cake", 1),
                ingredients(item("caerula_arbor:caramel_cake_piece"), item("caerula_arbor:caramel_cake_piece"), item("caerula_arbor:caramel_cake_piece"), item("caerula_arbor:caramel_cake_piece"))
        );
        shaped(
                writer,
                "assemble_cooked_fibre",
                "misc",
                null,
                result("caerula_arbor:cooked_fibre_block", 1),
                pattern(
                        "aaa",
                        "aaa",
                        "aaa"
                ),
                key('a', item("caerula_arbor:cooked_fibre"))
        );
        shaped(
                writer,
                "assemble_fibre",
                "misc",
                null,
                result("caerula_arbor:fibre_block", 1),
                pattern(
                        "aaa",
                        "aaa",
                        "aaa"
                ),
                key('a', item("caerula_arbor:ocean_fibre"))
        );
        shaped(
                writer,
                "assemble_phloem",
                "misc",
                null,
                result("caerula_arbor:phloem_block", 1),
                pattern(
                        "aaa",
                        "aaa",
                        "aaa"
                ),
                key('a', item("caerula_arbor:ocean_phloem"))
        );
        shaped(
                writer,
                "assemble_trail_ingot",
                "misc",
                null,
                result("caerula_arbor:trailrite", 1),
                pattern(
                        "aaa",
                        "aaa",
                        "aaa"
                ),
                key('a', item("caerula_arbor:trailrite_nugget"))
        );
        shaped(
                writer,
                "assemble_trail_slab",
                "misc",
                null,
                result("caerula_arbor:trail_brick", 1),
                pattern(
                        "a",
                        "a"
                ),
                key('a', item("caerula_arbor:trail_slab"))
        );
        shaped(
                writer,
                "assemble_trailrite",
                "misc",
                null,
                result("caerula_arbor:trailrite_block", 1),
                pattern(
                        "aaa",
                        "aaa",
                        "aaa"
                ),
                key('a', item("caerula_arbor:trailrite"))
        );
        cooking(
                writer,
                "bake_echo_shard",
                "minecraft:smelting",
                "misc",
                null,
                item("caerula_arbor:moist_echo_shard"),
                "minecraft:echo_shard",
                12f,
                300
        );
        shapeless(
                writer,
                "block_to_item_rec",
                "misc",
                null,
                result("caerula_arbor:caerula_recorder", 1),
                ingredients(item("caerula_arbor:block_recorder"))
        );
        shaped(
                writer,
                "blocked_oceancry",
                "misc",
                null,
                result("caerula_arbor:ocean_crystal_block", 1),
                pattern(
                        "aaa",
                        "aaa",
                        "aaa"
                ),
                key('a', item("caerula_arbor:ocean_crystal"))
        );
        shapeless(
                writer,
                "brand_apple",
                "misc",
                null,
                result("caerula_arbor:trail_apple", 4),
                ingredients(item("caerula_arbor:sea_trail_solid"), item("minecraft:apple"), item("minecraft:apple"), item("caerula_arbor:sea_trail_solid"), item("minecraft:apple"), item("minecraft:apple"))
        );
        cooking(
                writer,
                "burn_chitin",
                "minecraft:blasting",
                "misc",
                null,
                item("caerula_arbor:white_chitin"),
                "caerula_arbor:ocean_chitin",
                0f,
                180
        );
        cooking(
                writer,
                "burn_pearl",
                "minecraft:smelting",
                "misc",
                null,
                item("caerula_arbor:water_logged_pearl"),
                "minecraft:ender_pearl",
                4f,
                200
        );
        cooking(
                writer,
                "burn_shard",
                "minecraft:blasting",
                "misc",
                null,
                item("caerula_arbor:trail_debris"),
                "caerula_arbor:trail_shard",
                6f,
                140
        );
        shapeless(
                writer,
                "button_made",
                "misc",
                null,
                result("caerula_arbor:trail_plank_button", 1),
                ingredients(item("caerula_arbor:trail_plank"))
        );
        shaped(
                writer,
                "caerula_rec",
                "misc",
                null,
                result("caerula_arbor:caerula_recorder", 1),
                pattern(
                        "aba",
                        "bcb",
                        "dbd"
                ),
                key('a', item("minecraft:redstone")),
                key('b', item("minecraft:iron_ingot")),
                key('c', item("caerula_arbor:ocean_glasspane")),
                key('d', item("minecraft:lapis_lazuli"))
        );
        cooking(
                writer,
                "camp_egg",
                "minecraft:campfire_cooking",
                "misc",
                null,
                item("caerula_arbor:real_egg"),
                "caerula_arbor:fried_egg",
                8f,
                400
        );
        cooking(
                writer,
                "camp_kebab",
                "minecraft:campfire_cooking",
                "food",
                "food",
                item("caerula_arbor:kebab_raw"),
                "caerula_arbor:kebab_cooked",
                2f,
                400
        );
        cooking(
                writer,
                "campfire_block_fibre",
                "minecraft:campfire_cooking",
                "food",
                null,
                item("caerula_arbor:fibre_block"),
                "caerula_arbor:cooked_fibre_block",
                13.5f,
                640
        );
        cooking(
                writer,
                "campfire_cell",
                "minecraft:campfire_cooking",
                "food",
                null,
                item("caerula_arbor:broken_cell_cluster"),
                "caerula_arbor:cooked_broken_cell_cluster",
                4f,
                320
        );
        cooking(
                writer,
                "campfire_celll",
                "minecraft:campfire_cooking",
                "food",
                null,
                item("caerula_arbor:cell_cluster"),
                "caerula_arbor:cooked_cell_cluster",
                6f,
                400
        );
        cooking(
                writer,
                "campfire_claw",
                "minecraft:campfire_cooking",
                "food",
                null,
                item("caerula_arbor:claw"),
                "caerula_arbor:cooked_claw",
                1f,
                400
        );
        cooking(
                writer,
                "campfire_fakeegg",
                "minecraft:campfire_cooking",
                "food",
                null,
                item("caerula_arbor:fake_egg"),
                "caerula_arbor:cooked_fakeegg",
                0f,
                240
        );
        cooking(
                writer,
                "campfire_fish",
                "minecraft:campfire_cooking",
                "food",
                null,
                item("caerula_arbor:collector_meat"),
                "caerula_arbor:cooked_collector",
                1f,
                400
        );
        cooking(
                writer,
                "campfire_mor",
                "minecraft:campfire_cooking",
                "food",
                null,
                item("caerula_arbor:sea_trail_mor"),
                "caerula_arbor:cooked_mor",
                2f,
                360
        );
        cooking(
                writer,
                "campfire_oceanfibre",
                "minecraft:campfire_cooking",
                "food",
                null,
                item("caerula_arbor:ocean_fibre"),
                "caerula_arbor:cooked_fibre",
                1.5f,
                320
        );
        cooking(
                writer,
                "campfire_peduncle",
                "minecraft:campfire_cooking",
                "food",
                null,
                item("caerula_arbor:ocean_peduncle"),
                "caerula_arbor:cooked_peduncle",
                1.5f,
                480
        );
        shaped(
                writer,
                "ceaft_tidelinked_wand",
                "misc",
                null,
                result("caerula_arbor:tidelinked_wand", 1),
                pattern(
                        " ab",
                        "aca",
                        "da "
                ),
                key('a', item("caerula_arbor:repeller_shell")),
                key('b', item("caerula_arbor:ocean_chitin")),
                key('c', item("caerula_arbor:tide_wand")),
                key('d', item("minecraft:diamond"))
        );
        shaped(
                writer,
                "change_cell",
                "misc",
                null,
                result("caerula_arbor:transform_cell", 1),
                pattern(
                        "aba",
                        "bcb",
                        "aba"
                ),
                key('a', tag("caerula_arbor:gene")),
                key('b', item("caerula_arbor:cell_cluster")),
                key('c', item("caerula_arbor:base_egg"))
        );
        stonecutting(
                writer,
                "chiesle_isharmla_brick",
                item("caerula_arbor:isharmla_brick"),
                "caerula_arbor:isharmla_brick_chiesled",
                1
        );
        shaped(
                writer,
                "chitin_bow",
                "equipment",
                null,
                result("caerula_arbor:chitin_bow", 1),
                pattern(
                        " ab",
                        "c d",
                        " ab"
                ),
                key('a', item("caerula_arbor:ocean_chitin")),
                key('b', item("minecraft:string")),
                key('c', item("caerula_arbor:chitin_ingot")),
                key('d', item("caerula_arbor:ocean_cutin"))
        );
        shaped(
                writer,
                "chitin_phloem_chestplate",
                "equipment",
                null,
                result("caerula_arbor:sealeather_chitin_chestplate", 1),
                pattern(
                        "a a",
                        "bcb",
                        "d d"
                ),
                key('a', item("caerula_arbor:ocean_chitin")),
                key('b', item("caerula_arbor:chitin_ingot")),
                key('c', item("caerula_arbor:sealeather_chestplate")),
                key('d', item("caerula_arbor:ocean_phloem"))
        );
        shaped(
                writer,
                "cleaner_bot",
                "misc",
                null,
                result("caerula_arbor:item_helper", 1),
                pattern(
                        "aba",
                        "cdc",
                        "efe"
                ),
                key('a', item("minecraft:gold_ingot")),
                key('b', item("caerula_arbor:heteropic_piece")),
                key('c', item("minecraft:copper_block")),
                key('d', item("minecraft:netherite_ingot")),
                key('e', item("caerula_arbor:ocean_phloem")),
                key('f', item("caerula_arbor:ocean_machine"))
        );
        shaped(
                writer,
                "cluster_brocell",
                "misc",
                null,
                result("caerula_arbor:broken_cell_cluster", 1),
                pattern(
                        "aaa",
                        "aaa",
                        "aaa"
                ),
                key('a', item("caerula_arbor:broken_ocean_cell"))
        );
        shaped(
                writer,
                "cluster_cells",
                "misc",
                null,
                result("caerula_arbor:cell_cluster", 1),
                pattern(
                        "aaa",
                        "aaa",
                        "aaa"
                ),
                key('a', item("caerula_arbor:ocean_cell"))
        );
        shaped(
                writer,
                "comple_block",
                "misc",
                null,
                result("caerula_arbor:complex_chitin_block", 1),
                pattern(
                        "aaa",
                        "aaa",
                        "aaa"
                ),
                key('a', item("caerula_arbor:complex_chitin"))
        );
        cooking(
                writer,
                "cook_block_fibre",
                "minecraft:smelting",
                "food",
                null,
                item("caerula_arbor:fibre_block"),
                "caerula_arbor:cooked_fibre_block",
                13.5f,
                320
        );
        cooking(
                writer,
                "cook_cell",
                "minecraft:smelting",
                "food",
                null,
                item("caerula_arbor:broken_cell_cluster"),
                "caerula_arbor:cooked_broken_cell_cluster",
                4f,
                160
        );
        cooking(
                writer,
                "cook_celll",
                "minecraft:smelting",
                "misc",
                null,
                item("caerula_arbor:cell_cluster"),
                "caerula_arbor:cooked_cell_cluster",
                6f,
                200
        );
        cooking(
                writer,
                "cook_claw",
                "minecraft:smelting",
                "food",
                null,
                item("caerula_arbor:claw"),
                "caerula_arbor:cooked_claw",
                1f,
                200
        );
        cooking(
                writer,
                "cook_fish",
                "minecraft:smelting",
                "food",
                null,
                item("caerula_arbor:collector_meat"),
                "caerula_arbor:cooked_collector",
                1f,
                200
        );
        cooking(
                writer,
                "cook_oceanfibre",
                "minecraft:smelting",
                "food",
                null,
                item("caerula_arbor:ocean_fibre"),
                "caerula_arbor:cooked_fibre",
                1.5f,
                160
        );
        cooking(
                writer,
                "cook_peduncle",
                "minecraft:smelting",
                "food",
                null,
                item("caerula_arbor:ocean_peduncle"),
                "caerula_arbor:cooked_peduncle",
                1.5f,
                240
        );
        cooking(
                writer,
                "cooked_kebab",
                "minecraft:smelting",
                "food",
                "food",
                item("caerula_arbor:kebab_raw"),
                "caerula_arbor:kebab_cooked",
                2f,
                300
        );
        cooking(
                writer,
                "cookfakeegg",
                "minecraft:smelting",
                "food",
                null,
                item("caerula_arbor:fake_egg"),
                "caerula_arbor:cooked_fakeegg",
                0f,
                120
        );
        shapeless(
                writer,
                "copy_fate",
                "misc",
                null,
                result("caerula_arbor:royal_fate", 2),
                ingredients(item("caerula_arbor:royal_fate"), tag("caerula_arbor:archfiend_relics"))
        );
        shaped(
                writer,
                "copy_flamarine_temp",
                "misc",
                null,
                result("caerula_arbor:flamarine_upgrade_template", 2),
                pattern(
                        "aba",
                        "aca",
                        "aaa"
                ),
                key('a', item("caerula_arbor:trail_powder")),
                key('b', item("caerula_arbor:flamarine_upgrade_template")),
                key('c', item("caerula_arbor:trailrite"))
        );
        shapeless(
                writer,
                "copy_horse_gene",
                "misc",
                null,
                result("caerula_arbor:dna_horse", 2),
                ingredients(item("caerula_arbor:dna_horse"), item("caerula_arbor:gene_sample_normal"), item("caerula_arbor:gene_sample_normal"), item("caerula_arbor:gene_sample_normal"), item("caerula_arbor:gene_sample_normal"))
        );
        shapeless(
                writer,
                "copy_hunter_gene",
                "misc",
                null,
                result("caerula_arbor:hunter_gene", 2),
                ingredients(item("caerula_arbor:hunter_gene"), item("caerula_arbor:gene_sample_upgraded"), item("caerula_arbor:gene_sample_upgraded"), item("caerula_arbor:gene_sample_upgraded"), item("caerula_arbor:gene_sample_upgraded"))
        );
        shapeless(
                writer,
                "copy_reaper_gene",
                "misc",
                null,
                result("caerula_arbor:dna_reaper", 2),
                ingredients(item("caerula_arbor:dna_reaper"), item("caerula_arbor:gene_sample_normal"), item("caerula_arbor:gene_sample_normal"), item("caerula_arbor:gene_sample_normal"), item("caerula_arbor:gene_sample_normal"))
        );
        shapeless(
                writer,
                "copy_target_base",
                "misc",
                null,
                result("caerula_arbor:targeted_base", 2),
                ingredients(item("caerula_arbor:targeted_base"), item("caerula_arbor:gene_sample_superb"), item("caerula_arbor:gene_sample_superb"), item("caerula_arbor:gene_sample_superb"), item("caerula_arbor:gene_sample_superb"))
        );
        shaped(
                writer,
                "copy_templates",
                "misc",
                null,
                result("caerula_arbor:ocean_trim_template", 2),
                pattern(
                        "aba",
                        "aca",
                        "aaa"
                ),
                key('a', item("caerula_arbor:ocean_cutin")),
                key('b', item("caerula_arbor:ocean_trim_template")),
                key('c', item("caerula_arbor:fake_egg"))
        );
        shapeless(
                writer,
                "copyfate_1",
                "misc",
                null,
                result("caerula_arbor:royal_fate", 2),
                ingredients(item("caerula_arbor:royal_fate"), tag("caerula_arbor:king_relics"))
        );
        shaped(
                writer,
                "core_made",
                "misc",
                null,
                result("caerula_arbor:trail_powder_core", 4),
                pattern(
                        "aaa",
                        "aba",
                        "aaa"
                ),
                key('a', item("caerula_arbor:trail_powder")),
                key('b', item("caerula_arbor:moist_star"))
        );
        shaped(
                writer,
                "craft_aegir_glass_bar",
                "building",
                null,
                result("caerula_arbor:aegir_glass_bar", 3),
                pattern(
                        "a",
                        "a",
                        "a"
                ),
                key('a', item("caerula_arbor:aegir_glass_deco"))
        );
        shaped(
                writer,
                "craft_aegir_sword",
                "equipment",
                null,
                result("caerula_arbor:aegir_sword", 1),
                pattern(
                        " a ",
                        " a ",
                        "bcb"
                ),
                key('a', item("minecraft:iron_ingot")),
                key('b', item("minecraft:copper_ingot")),
                key('c', item("caerula_arbor:trail_powder_core"))
        );
        shapeless(
                writer,
                "craft_al_1s",
                "misc",
                null,
                result("caerula_arbor:item_helper_al_1s", 1),
                ingredients(item("minecraft:gray_dye"), item("minecraft:lapis_lazuli"), item("minecraft:diamond"), item("caerula_arbor:item_helper"))
        );
        shaped(
                writer,
                "craft_anchor_ingot",
                "misc",
                null,
                result("caerula_arbor:anchor_forge_ingot", 1),
                pattern(
                        "aaa",
                        "aba",
                        "aca"
                ),
                key('a', item("caerula_arbor:anchor_shard")),
                key('b', item("caerula_arbor:water_logged_pearl")),
                key('c', item("caerula_arbor:targeted_transmitter_ulpians"))
        );
        shapeless(
                writer,
                "craft_apocata_sword",
                "equipment",
                null,
                result("caerula_arbor:apocata_sword", 1),
                ingredients(item("caerula_arbor:trailrite_sword"), item("caerula_arbor:complex_chitin_sword"), item("caerula_arbor:incandescent_anima"), item("caerula_arbor:leviathan_animus"), item("caerula_arbor:mizuki_determination"), item("caerula_arbor:moist_echo_shard"), item("caerula_arbor:moist_star"), item("caerula_arbor:apocalypse"), item("caerula_arbor:banned_item"))
        );
        shapeless(
                writer,
                "craft_apple_juice",
                "misc",
                null,
                result("caerula_arbor:colourful_apple_juice", 1),
                ingredients(item("caerula_arbor:a_cup_of_water"), item("caerula_arbor:colourfull_jelly"), item("caerula_arbor:colourfull_jelly"), item("caerula_arbor:trail_apple"), item("minecraft:apple"))
        );
        shaped(
                writer,
                "craft_base_egg",
                "misc",
                null,
                result("caerula_arbor:base_egg", 1),
                pattern(
                        "aba",
                        "bcb",
                        "aba"
                ),
                key('a', tag("caerula_arbor:fish_food")),
                key('b', item("caerula_arbor:cell_cluster")),
                key('c', item("caerula_arbor:real_egg"))
        );
        shaped(
                writer,
                "craft_base_kit",
                "misc",
                null,
                result("caerula_arbor:base_operation_kit", 1),
                pattern(
                        "aaa",
                        "bcb",
                        "ddd"
                ),
                key('a', item("minecraft:leather")),
                key('b', item("caerula_arbor:ocean_crystal")),
                key('c', item("minecraft:iron_ingot")),
                key('d', item("caerula_arbor:ocean_phloem"))
        );
        shapeless(
                writer,
                "craft_blue_dye",
                "misc",
                null,
                result("minecraft:blue_dye", 1),
                ingredients(item("caerula_arbor:ocean_phloem"))
        );
        shaped(
                writer,
                "craft_book_shelf",
                "misc",
                null,
                result("caerula_arbor:caerula_book_shelf", 1),
                pattern(
                        "aaa",
                        "bcb",
                        "aaa"
                ),
                key('a', item("caerula_arbor:trail_plank")),
                key('b', item("caerula_arbor:heteropic_piece")),
                key('c', item("minecraft:bookshelf"))
        );
        shapeless(
                writer,
                "craft_bowl_seagrass",
                "misc",
                null,
                result("caerula_arbor:bowl_seagrass", 1),
                ingredients(item("minecraft:seagrass"), item("minecraft:seagrass"), item("minecraft:seagrass"), item("minecraft:bowl"))
        );
        shaped(
                writer,
                "craft_can",
                "misc",
                null,
                result("caerula_arbor:empty_can", 3),
                pattern("a a", "bab"),
                key('a', item("minecraft:iron_ingot")),
                key('b', tag("forge:dyes"))
        );
        shapeless(
                writer,
                "craft_capsule",
                "misc",
                null,
                result("caerula_arbor:mutagenisis_capsule", 1),
                ingredients(item("caerula_arbor:trail_golden_apple"), item("caerula_arbor:immunosuppressor"), item("minecraft:sugar"), item("caerula_arbor:fermented_ocean_eye"), item("caerula_arbor:transform_cell"), item("caerula_arbor:fermented_ocean_eye"), item("caerula_arbor:caffeine"), item("caerula_arbor:immunosuppressor"), item("caerula_arbor:colourfull_jelly"))
        );
        shapeless(
                writer,
                "craft_caramel_seagrs_juice",
                "misc",
                null,
                result("caerula_arbor:caramel_seagrass_juice", 1),
                ingredients(item("caerula_arbor:a_cup_of_water"), item("caerula_arbor:deep_seagrass"), item("caerula_arbor:deep_seagrass"), item("caerula_arbor:deep_seagrass"), item("caerula_arbor:caramel_mor"), item("caerula_arbor:caramel_mor"))
        );
        shapeless(
                writer,
                "craft_catalyst",
                "misc",
                null,
                result("caerula_arbor:oceanize_catalyst", 4),
                ingredients(item("minecraft:sculk_catalyst"), item("caerula_arbor:transform_cell"), item("minecraft:sculk_catalyst"), item("caerula_arbor:transform_cell"), item("caerula_arbor:trail_golden_apple"), item("caerula_arbor:transform_cell"), item("minecraft:sculk_catalyst"), item("caerula_arbor:transform_cell"), item("minecraft:sculk_catalyst"))
        );
        shapeless(
                writer,
                "craft_cavair",
                "misc",
                null,
                result("caerula_arbor:ocean_cavair", 1),
                ingredients(item("caerula_arbor:canned_water"), item("caerula_arbor:cell_cluster"), item("caerula_arbor:cell_cluster"), item("caerula_arbor:cell_cluster"), item("minecraft:kelp"), item("minecraft:kelp"))
        );
        shaped(
                writer,
                "craft_chitin_axe",
                "equipment",
                null,
                result("caerula_arbor:chitin_axe", 1),
                pattern(
                        "ab",
                        "bc",
                        " c"
                ),
                key('a', item("caerula_arbor:ocean_chitin")),
                key('b', item("caerula_arbor:chitin_ingot")),
                key('c', item("caerula_arbor:cutin_stick"))
        );
        shaped(
                writer,
                "craft_chitin_bts",
                "equipment",
                null,
                result("caerula_arbor:chitin_armor_boots", 1),
                pattern(
                        "a a",
                        "b b"
                ),
                key('a', item("caerula_arbor:ocean_chitin")),
                key('b', item("caerula_arbor:chitin_ingot"))
        );
        shaped(
                writer,
                "craft_chitin_chest",
                "equipment",
                null,
                result("caerula_arbor:chitin_armor_chestplate", 1),
                pattern(
                        "a a",
                        "aba",
                        "bab"
                ),
                key('a', item("caerula_arbor:ocean_chitin")),
                key('b', item("caerula_arbor:chitin_ingot"))
        );
        shaped(
                writer,
                "craft_chitin_hel",
                "equipment",
                null,
                result("caerula_arbor:chitin_armor_helmet", 1),
                pattern(
                        "aba",
                        "b b"
                ),
                key('a', item("caerula_arbor:ocean_chitin")),
                key('b', item("caerula_arbor:chitin_ingot"))
        );
        shaped(
                writer,
                "craft_chitin_hoe",
                "equipment",
                null,
                result("caerula_arbor:chitin_hoe", 1),
                pattern(
                        "ab",
                        " c",
                        " c"
                ),
                key('a', item("caerula_arbor:ocean_chitin")),
                key('b', item("caerula_arbor:chitin_ingot")),
                key('c', item("caerula_arbor:cutin_stick"))
        );
        shapeless(
                writer,
                "craft_chitin_ingot",
                "misc",
                null,
                result("caerula_arbor:chitin_ingot", 1),
                ingredients(item("caerula_arbor:ocean_chitin"), item("caerula_arbor:ocean_chitin"), item("minecraft:iron_nugget"), item("minecraft:gold_nugget"))
        );
        shaped(
                writer,
                "craft_chitin_leg",
                "equipment",
                null,
                result("caerula_arbor:chitin_armor_leggings", 1),
                pattern(
                        "aba",
                        "a a",
                        "a a"
                ),
                key('a', item("caerula_arbor:ocean_chitin")),
                key('b', item("caerula_arbor:chitin_ingot"))
        );
        shaped(
                writer,
                "craft_chitin_phloem_boots",
                "equipment",
                null,
                result("caerula_arbor:sealeather_chitin_boots", 1),
                pattern(
                        "aba",
                        "c c"
                ),
                key('a', item("caerula_arbor:ocean_chitin")),
                key('b', item("caerula_arbor:sealeather_boots")),
                key('c', item("caerula_arbor:chitin_ingot"))
        );
        shaped(
                writer,
                "craft_chitin_phloem_helmet",
                "equipment",
                null,
                result("caerula_arbor:sealeather_chitin_helmet", 1),
                pattern(
                        "aba",
                        "bcb"
                ),
                key('a', item("caerula_arbor:ocean_chitin")),
                key('b', item("caerula_arbor:chitin_ingot")),
                key('c', item("caerula_arbor:sealeather_helmet"))
        );
        shaped(
                writer,
                "craft_chitin_phloem_leggings",
                "equipment",
                null,
                result("caerula_arbor:sealeather_chitin_leggings", 1),
                pattern(
                        "a a",
                        "bcb",
                        "a a"
                ),
                key('a', item("caerula_arbor:ocean_chitin")),
                key('b', item("caerula_arbor:chitin_ingot")),
                key('c', item("caerula_arbor:sealeather_leggings"))
        );
        shaped(
                writer,
                "craft_chitin_pick",
                "equipment",
                null,
                result("caerula_arbor:chitin_pickaxe", 1),
                pattern("aba", " c ", " c "),
                key('a', item("caerula_arbor:ocean_chitin")),
                key('b', item("caerula_arbor:chitin_ingot")),
                key('c', item("caerula_arbor:cutin_stick"))
        );
        shaped(
                writer,
                "craft_chitin_shield",
                "misc",
                null,
                result("caerula_arbor:chitin_shield", 1),
                pattern("aba", "bcb", "aba"),
                key('a', item("caerula_arbor:ocean_phloem")),
                key('b', item("caerula_arbor:ocean_chitin")),
                key('c', item("caerula_arbor:chitin_ingot"))
        );
        shaped(
                writer,
                "craft_chitin_shovel",
                "equipment",
                null,
                result("caerula_arbor:chitin_shovel", 1),
                pattern("a", "b", "b"),
                key('a', item("caerula_arbor:chitin_ingot")),
                key('b', item("caerula_arbor:cutin_stick"))
        );
        shaped(
                writer,
                "craft_chitin_swd",
                "equipment",
                null,
                result("caerula_arbor:chitin_sword", 1),
                pattern("a", "a", "b"),
                key('a', item("caerula_arbor:chitin_ingot")),
                key('b', item("caerula_arbor:cutin_stick"))
        );
        shaped(
                writer,
                "craft_chitinblock",
                "misc",
                null,
                result("caerula_arbor:chitin_block", 1),
                pattern("aaa", "aaa", "aaa"),
                key('a', item("caerula_arbor:ocean_chitin"))
        );
        shaped(
                writer,
                "craft_circular_saw",
                "misc",
                null,
                result("caerula_arbor:circular_saw", 1),
                pattern(" ab", "cda", "de "),
                key('a', item("caerula_arbor:ocean_crystal")),
                key('b', item("caerula_arbor:trail_powder_core")),
                key('c', item("minecraft:leather")),
                key('d', item("caerula_arbor:cutin_stick")),
                key('e', item("caerula_arbor:ocean_phloem"))
        );
        shapeless(
                writer,
                "craft_coffee_candy",
                "misc",
                null,
                result("caerula_arbor:coffee_candy", 3),
                ingredients(item("caerula_arbor:caffeine"), item("minecraft:sugar"), item("minecraft:honeycomb"), item("caerula_arbor:paper_bag"), item("caerula_arbor:paper_bag"), item("caerula_arbor:paper_bag"))
        );
        smithingTransform(
                writer,
                "craft_complex_chitin_shield",
                item("caerula_arbor:ocean_trim_template"),
                item("caerula_arbor:chitin_shield"),
                item("caerula_arbor:complex_chitin"),
                result("caerula_arbor:complex_chitin_shield")
        );
        shaped(
                writer,
                "craft_complexchitin",
                "misc",
                null,
                result("caerula_arbor:complex_chitin", 2),
                pattern("abc", "bdb", "ebf"),
                key('a', item("caerula_arbor:sea_trail_mor")),
                key('b', item("caerula_arbor:chitin_ingot")),
                key('c', item("caerula_arbor:ocean_crystal")),
                key('d', item("caerula_arbor:ocean_eye")),
                key('e', item("caerula_arbor:cell_cluster")),
                key('f', item("caerula_arbor:ocean_cutin"))
        );
        shapeless(
                writer,
                "craft_cooked_kebab",
                "misc",
                "food",
                result("caerula_arbor:kebab_cooked", 1),
                ingredients(item("minecraft:stick"), item("caerula_arbor:cooked_fibre"), item("caerula_arbor:cooked_peduncle"))
        );
        shaped(
                writer,
                "craft_copper_bomb",
                "misc",
                null,
                result("caerula_arbor:bomb_copper", 1),
                pattern("aba", "cdc", "aba"),
                key('a', item("minecraft:copper_ingot")),
                key('b', item("minecraft:gunpowder")),
                key('c', item("caerula_arbor:trail_powder")),
                key('d', item("caerula_arbor:bomb_trailer"))
        );
        shaped(
                writer,
                "craft_cream",
                "misc",
                null,
                result("caerula_arbor:trail_cream", 1),
                pattern("aaa", "aba", "aaa"),
                key('a', item("caerula_arbor:sea_trail_mor")),
                key('b', item("minecraft:slime_ball"))
        );
        shaped(
                writer,
                "craft_cryst_axe",
                "equipment",
                null,
                result("caerula_arbor:axe_ocean_crystal", 1),
                pattern("aa", "ab", " b"),
                key('a', item("caerula_arbor:ocean_crystal")),
                key('b', item("caerula_arbor:cutin_stick"))
        );
        shaped(
                writer,
                "craft_cryst_hoe",
                "equipment",
                null,
                result("caerula_arbor:hoe_ocean_crystal", 1),
                pattern("aa", " b", " b"),
                key('a', item("caerula_arbor:ocean_crystal")),
                key('b', item("caerula_arbor:cutin_stick"))
        );
        shaped(
                writer,
                "craft_cryst_pick",
                "equipment",
                null,
                result("caerula_arbor:pickaxe_ocean_crystal", 1),
                pattern("aaa", " b ", " b "),
                key('a', item("caerula_arbor:ocean_crystal")),
                key('b', item("caerula_arbor:cutin_stick"))
        );
        shaped(
                writer,
                "craft_cryst_shovel",
                "equipment",
                null,
                result("caerula_arbor:shovel_ocean_crystal", 1),
                pattern("a", "b", "b"),
                key('a', item("caerula_arbor:ocean_crystal")),
                key('b', item("caerula_arbor:cutin_stick"))
        );
        shaped(
                writer,
                "craft_crystal_sword",
                "equipment",
                null,
                result("caerula_arbor:sword_ocean_crystal", 1),
                pattern("a", "a", "b"),
                key('a', item("caerula_arbor:ocean_crystal")),
                key('b', item("caerula_arbor:cutin_stick"))
        );
        shaped(
                writer,
                "craft_cutin_pane",
                "misc",
                null,
                result("caerula_arbor:ocean_glasspane", 2),
                pattern("aaa"),
                key('a', item("caerula_arbor:ocean_cutin"))
        );
        stonecutting(
                writer,
                "craft_cutinstk",
                item("caerula_arbor:ocean_cutin"),
                "caerula_arbor:cutin_stick",
                4
        );
        shapeless(
                writer,
                "craft_deep_seagrass_juice",
                "misc",
                null,
                result("caerula_arbor:deep_seagrass_juice", 1),
                ingredients(item("caerula_arbor:a_cup_of_water"), item("caerula_arbor:deep_seagrass"), item("caerula_arbor:deep_seagrass"), item("caerula_arbor:deep_seagrass"), item("minecraft:seagrass"))
        );
        shaped(
                writer,
                "craft_diorite_sculpture",
                "misc",
                null,
                result("caerula_arbor:diorite_sculpture", 1),
                pattern("aba", "cdc", "aea"),
                key('a', item("minecraft:diorite")),
                key('b', item("caerula_arbor:chitin_block")),
                key('c', item("minecraft:netherite_ingot")),
                key('d', item("caerula_arbor:heteropic_block")),
                key('e', item("caerula_arbor:targeted_transmitter_specter"))
        );
        shaped(
                writer,
                "craft_echo_jelly",
                "misc",
                null,
                result("caerula_arbor:echo_jelly", 7),
                pattern("aba", "aca", "aaa"),
                key('a', item("caerula_arbor:fruit_jelly")),
                key('b', item("caerula_arbor:sea_trail_mor")),
                key('c', item("caerula_arbor:moist_echo_shard"))
        );
        shapeless(
                writer,
                "craft_elite_cavair",
                "misc",
                null,
                result("caerula_arbor:elite_cavair", 1),
                ingredients(item("caerula_arbor:ocean_cavair"), item("caerula_arbor:cell_cluster"), item("caerula_arbor:fermented_ocean_eye"), item("caerula_arbor:ocean_peduncle"), item("caerula_arbor:deep_seagrass"), item("minecraft:blaze_powder"), item("caerula_arbor:cooked_fakeegg"), item("minecraft:gold_nugget"))
        );
        shaped(
                writer,
                "craft_eme_aid_build",
                "misc",
                null,
                result("caerula_arbor:emergency_aid_building", 2),
                pattern("aba", "cdc", "aea"),
                key('a', tag("forge:sandstone")),
                key('b', item("minecraft:prismarine_shard")),
                key('c', item("minecraft:prismarine_crystals")),
                key('d', item("minecraft:nether_star")),
                key('e', item("minecraft:redstone"))
        );
        shaped(
                writer,
                "craft_empty_treaty",
                "misc",
                null,
                result("caerula_arbor:treaty_empty", 1),
                pattern("aaa", "bcb", "aaa"),
                key('a', item("minecraft:paper")),
                key('b', item("minecraft:ink_sac")),
                key('c', tag("forge:dyes"))
        );
        shaped(
                writer,
                "craft_enderina_core",
                "misc",
                null,
                result("caerula_arbor:enderina_core", 1),
                pattern("aba", "aca", "ada"),
                key('a', item("caerula_arbor:dragon_brand")),
                key('b', item("caerula_arbor:heteropic_piece")),
                key('c', tag("caerula_arbor:moist_item")),
                key('d', item("caerula_arbor:ocean_machine"))
        );
        shaped(
                writer,
                "craft_extractor",
                "misc",
                null,
                result("caerula_arbor:ocean_extractor", 1),
                pattern("aa ", "abc", " cd"),
                key('a', item("caerula_arbor:cutin_stick")),
                key('b', item("caerula_arbor:ocean_glass")),
                key('c', item("caerula_arbor:ocean_crystal")),
                key('d', item("caerula_arbor:complex_chitin"))
        );
        shaped(
                writer,
                "craft_fax",
                "misc",
                null,
                result("caerula_arbor:fax", 1),
                pattern("aba", "acd", "efe"),
                key('a', item("minecraft:iron_ingot")),
                key('b', item("minecraft:iron_bars")),
                key('c', item("minecraft:ink_sac")),
                key('d', item("caerula_arbor:ocean_glasspane")),
                key('e', item("caerula_arbor:ocean_crystal")),
                key('f', item("minecraft:redstone"))
        );
        shapeless(
                writer,
                "craft_fluore_icecream",
                "misc",
                null,
                result("caerula_arbor:fluore_icecream", 1),
                ingredients(item("caerula_arbor:shell_of_stonecutter"), item("caerula_arbor:fluore_berries"), item("minecraft:snowball"), item("minecraft:snowball"), item("minecraft:sugar"), item("minecraft:sugar"), item("minecraft:glow_berries"))
        );
        shapeless(
                writer,
                "craft_fluore_juice",
                "misc",
                null,
                result("caerula_arbor:fluore_berry_juice", 1),
                ingredients(item("caerula_arbor:a_cup_of_water"), item("caerula_arbor:fluore_berries"), item("caerula_arbor:fluore_berries"), item("minecraft:sweet_berries"), item("minecraft:glow_berries"))
        );
        shapeless(
                writer,
                "craft_fluro_berry",
                "misc",
                null,
                result("caerula_arbor:fluore_berries", 1),
                ingredients(item("minecraft:glowstone_dust"), item("minecraft:glowstone_dust"), item("minecraft:glowstone_dust"), item("minecraft:glowstone_dust"), item("minecraft:glow_berries"), item("minecraft:glowstone_dust"), item("minecraft:glowstone_dust"), item("minecraft:glowstone_dust"), item("minecraft:glowstone_dust"))
        );
        shapeless(
                writer,
                "craft_gladiia_kit",
                "misc",
                null,
                result("caerula_arbor:operation_kit_gladiia", 1),
                ingredients(item("caerula_arbor:base_operation_kit"), item("caerula_arbor:targeted_transmitter_gladiia"), item("caerula_arbor:targeted_transmitter_gladiia"), item("caerula_arbor:aegir_lancet"))
        );
        shaped(
                writer,
                "craft_gold_pendant",
                "misc",
                null,
                result("caerula_arbor:pale_gold_pendant", 1),
                pattern("aba", "cdc", "aea"),
                key('a', item("minecraft:gold_ingot")),
                key('b', item("minecraft:lapis_lazuli")),
                key('c', item("minecraft:copper_ingot")),
                key('d', item("minecraft:diamond")),
                key('e', item("caerula_arbor:targeted_transmitter_gladiia"))
        );
        shaped(
                writer,
                "craft_golden_chalise",
                "misc",
                null,
                result("caerula_arbor:golden_chalise", 1),
                pattern("aba", "aca", " d "),
                key('a', item("minecraft:gold_ingot")),
                key('b', tag("caerula_arbor:relic_generic")),
                key('c', item("minecraft:ender_chest")),
                key('d', item("minecraft:gold_block"))
        );
        shaped(
                writer,
                "craft_goldenapple",
                "misc",
                null,
                result("caerula_arbor:trail_golden_apple", 1),
                pattern("aaa", "aba", "aaa"),
                key('a', item("minecraft:gold_ingot")),
                key('b', item("caerula_arbor:trail_apple"))
        );
        shapeless(
                writer,
                "craft_gunpowder",
                "misc",
                null,
                result("minecraft:gunpowder", 8),
                ingredients(item("minecraft:gunpowder"), item("minecraft:gunpowder"), item("caerula_arbor:trail_powder"), item("minecraft:gunpowder"), item("minecraft:gunpowder"), item("caerula_arbor:trail_powder"))
        );
        shaped(
                writer,
                "craft_hand_anchor",
                "equipment",
                null,
                result("caerula_arbor:hand_anchor", 1),
                pattern("abc", " db", "e a"),
                key('a', item("minecraft:iron_ingot")),
                key('b', item("minecraft:iron_block")),
                key('c', item("caerula_arbor:trail_powder_core")),
                key('d', item("minecraft:deepslate")),
                key('e', item("caerula_arbor:cutin_stick"))
        );
        shapeless(
                writer,
                "craft_immunosupp",
                "misc",
                null,
                result("caerula_arbor:immunosuppressor", 2),
                ingredients(item("caerula_arbor:trail_mushroom"), item("minecraft:sugar"), item("minecraft:sugar"), item("caerula_arbor:caffeine"), item("caerula_arbor:cell_cluster"), item("minecraft:sugar"), item("caerula_arbor:caffeine"), item("caerula_arbor:caffeine"), item("caerula_arbor:trail_mushroom"))
        );
        shaped(
                writer,
                "craft_interphone",
                "misc",
                null,
                result("caerula_arbor:interphone", 1),
                pattern(" ab", "aca", "da "),
                key('a', item("minecraft:iron_ingot")),
                key('b', item("minecraft:lightning_rod")),
                key('c', item("minecraft:emerald")),
                key('d', item("minecraft:redstone"))
        );
        shapeless(
                writer,
                "craft_iris",
                "misc",
                null,
                result("caerula_arbor:redstone_iris_flower", 1),
                ingredients(item("caerula_arbor:redstone_iris"))
        );
        shaped(
                writer,
                "craft_isharmla_brick",
                "building",
                null,
                result("caerula_arbor:isharmla_brick", 1),
                pattern("aa", "aa"),
                key('a', item("caerula_arbor:isharmla_scute"))
        );
        shapeless(
                writer,
                "craft_kebab",
                "misc",
                "food",
                result("caerula_arbor:kebab_raw", 1),
                ingredients(item("minecraft:stick"), item("caerula_arbor:ocean_fibre"), item("caerula_arbor:ocean_peduncle"))
        );
        shapeless(
                writer,
                "craft_lanc_xiao",
                "equipment",
                null,
                result("caerula_arbor:lanc_xiao", 1),
                ingredients(item("caerula_arbor:sword_ocean_crystal"), tag("caerula_arbor:endspeaker_chapter"), item("minecraft:obsidian"), item("caerula_arbor:heteropic_piece"))
        );
        shaped(
                writer,
                "craft_lancet",
                "equipment",
                null,
                result("caerula_arbor:aegir_lancet", 1),
                pattern(" ab", "cda", "dc "),
                key('a', item("minecraft:copper_ingot")),
                key('b', item("caerula_arbor:trail_powder_core")),
                key('c', item("minecraft:iron_ingot")),
                key('d', item("caerula_arbor:cutin_stick"))
        );
        shaped(
                writer,
                "craft_lantern",
                "misc",
                null,
                result("caerula_arbor:lantern_judgement", 1),
                pattern(" a ", "bcb", " d "),
                key('a', item("caerula_arbor:heteropic_piece")),
                key('b', item("minecraft:amethyst_shard")),
                key('c', item("minecraft:lantern")),
                key('d', item("caerula_arbor:trail_powder"))
        );
        shaped(
                writer,
                "craft_leath_boots",
                "equipment",
                null,
                result("caerula_arbor:sealeather_boots", 1),
                pattern("a a", "a a"),
                key('a', item("caerula_arbor:ocean_phloem"))
        );
        shaped(
                writer,
                "craft_leath_chets",
                "equipment",
                null,
                result("caerula_arbor:sealeather_chestplate", 1),
                pattern("a a", "aaa", "aaa"),
                key('a', item("caerula_arbor:ocean_phloem"))
        );
        shaped(
                writer,
                "craft_leath_hel",
                "equipment",
                null,
                result("caerula_arbor:sealeather_helmet", 1),
                pattern("aaa", "a a"),
                key('a', item("caerula_arbor:ocean_phloem"))
        );
        shaped(
                writer,
                "craft_leath_legg",
                "equipment",
                null,
                result("caerula_arbor:sealeather_leggings", 1),
                pattern("aaa", "a a", "a a"),
                key('a', item("caerula_arbor:ocean_phloem"))
        );
        shapeless(
                writer,
                "craft_meat_can",
                "misc",
                null,
                result("caerula_arbor:meat_can", 1),
                ingredients(tag("forge:meats"), tag("forge:meats"), item("caerula_arbor:empty_can"))
        );
        shaped(
                writer,
                "craft_moist_bag",
                "misc",
                null,
                result("caerula_arbor:moist_bag", 1),
                pattern("aba", "aca", "bdb"),
                key('a', item("caerula_arbor:ocean_phloem")),
                key('b', item("minecraft:leather")),
                key('c', tag("caerula_arbor:any_coral")),
                key('d', item("caerula_arbor:targeted_transmitter_skadi"))
        );
        shaped(
                writer,
                "craft_mop",
                "misc",
                null,
                result("caerula_arbor:trail_mop", 1),
                pattern(" ab", " ca", "c  "),
                key('a', tag("minecraft:wool")),
                key('b', item("caerula_arbor:trail_cream")),
                key('c', item("minecraft:stick"))
        );
        shapeless(
                writer,
                "craft_nethersea_coffee",
                "misc",
                null,
                result("caerula_arbor:nethersea_coffee", 1),
                ingredients(item("caerula_arbor:a_cup_of_water"), item("minecraft:cocoa_beans"), item("minecraft:cocoa_beans"), item("caerula_arbor:trail_leave"), item("caerula_arbor:trail_leave"), item("caerula_arbor:trail_leave"), item("minecraft:sugar"), item("minecraft:sugar"))
        );
        shapeless(
                writer,
                "craft_nethersea_icecream",
                "misc",
                null,
                result("caerula_arbor:nethersea_icecream", 1),
                ingredients(item("caerula_arbor:shell_of_stonecutter"), item("minecraft:snowball"), item("minecraft:snowball"), item("minecraft:sugar"), item("minecraft:sugar"), item("caerula_arbor:sea_trail_mor"), item("caerula_arbor:sea_trail_mor"), item("caerula_arbor:sea_trail_mor"), item("caerula_arbor:sea_trail_mor"))
        );
        shaped(
                writer,
                "craft_nethersea_pir",
                "misc",
                "food",
                result("caerula_arbor:nethersea_pumpkin_pie", 1),
                pattern("abc"),
                key('a', item("caerula_arbor:trail_pumpking")),
                key('b', item("minecraft:egg")),
                key('c', item("minecraft:sugar"))
        );
        shapeless(
                writer,
                "craft_nethersea_stew",
                "misc",
                "food",
                result("caerula_arbor:nethersea_stew", 1),
                ingredients(item("minecraft:bowl"), item("caerula_arbor:trail_mushroom"), item("caerula_arbor:trail_mushroom"))
        );
        shapeless(
                writer,
                "craft_nethersea_stimutant",
                "misc",
                null,
                result("caerula_arbor:nethersea_stimutant", 1),
                ingredients(item("caerula_arbor:nethersea_coffee"), item("caerula_arbor:caffeine"), item("caerula_arbor:caffeine"), item("minecraft:sugar"), item("minecraft:sugar"), item("caerula_arbor:trail_leave"), item("caerula_arbor:trail_leave"))
        );
        shaped(
                writer,
                "craft_noodle",
                "misc",
                null,
                result("caerula_arbor:instant_noodle", 1),
                pattern("aaa", "aba", "aaa"),
                key('a', item("minecraft:wheat")),
                key('b', item("minecraft:carrot"))
        );
        shapeless(
                writer,
                "craft_nurture_gene",
                "misc",
                null,
                result("caerula_arbor:nurture_gene_set", 1),
                ingredients(item("caerula_arbor:archive_sal_viento"), item("caerula_arbor:archive_of_tidelink"), item("caerula_arbor:archive_of_martus"), item("caerula_arbor:archive_of_raider"), tag("caerula_arbor:gene"), tag("caerula_arbor:cursed"), item("caerula_arbor:ocean_cutin"), item("caerula_arbor:cell_cluster"))
        );
        shaped(
                writer,
                "craft_ocarino",
                "misc",
                null,
                result("caerula_arbor:ocarina", 1),
                pattern(" a ", "aba"),
                key('a', item("caerula_arbor:ocean_chitin")),
                key('b', item("caerula_arbor:ocean_eye"))
        );
        shaped(
                writer,
                "craft_ocean_arrow",
                "misc",
                null,
                result("caerula_arbor:ocean_arrow", 4),
                pattern("a", "b", "c"),
                key('a', item("caerula_arbor:bone_shard")),
                key('b', item("caerula_arbor:cutin_stick")),
                key('c', item("minecraft:feather"))
        );
        shaped(
                writer,
                "craft_oceanglass_cup",
                "misc",
                null,
                result("caerula_arbor:oceanglass_cup", 2),
                pattern("a a", " a "),
                key('a', item("caerula_arbor:ocean_glasspane"))
        );
        shapeless(
                writer,
                "craft_oil",
                "misc",
                null,
                result("caerula_arbor:oil_and_cream", 1),
                ingredients(item("minecraft:blaze_powder"), item("minecraft:sugar"), item("minecraft:blaze_powder"), item("minecraft:magma_cream"), item("caerula_arbor:empty_can"), item("minecraft:stick"), item("minecraft:magma_cream"), item("minecraft:sugar"), item("minecraft:stick"))
        );
        shapeless(
                writer,
                "craft_orangestorm",
                "misc",
                null,
                result("caerula_arbor:golden_storm", 3),
                ingredients(item("minecraft:apple"), item("minecraft:sugar"), item("minecraft:yellow_dye"), item("caerula_arbor:paper_bag"), item("caerula_arbor:paper_bag"), item("caerula_arbor:paper_bag"))
        );
        patchouliBook(
                writer,
                "craft_patchouli_book",
                "caerula_arbor:caerula_illustration",
                ingredients(tag("minecraft:bookshelf_books"), tag("caerula_arbor:seaborn_loots"))
        );
        shaped(
                writer,
                "craft_pocket_doll",
                "misc",
                null,
                result("caerula_arbor:pocket_sea_doll", 1),
                pattern(" a ", "aba", "cdc"),
                key('a', item("caerula_arbor:ocean_cutin")),
                key('b', tag("minecraft:wool")),
                key('c', item("caerula_arbor:ocean_crystal")),
                key('d', item("caerula_arbor:phloem_block"))
        );
        shapeless(
                writer,
                "craft_preserved_egg",
                "misc",
                null,
                result("caerula_arbor:nethersea_preserved_egg", 2),
                ingredients(item("caerula_arbor:nethersea_chicken_egg"), item("caerula_arbor:nethersea_chicken_egg"), item("caerula_arbor:trail_powder"), item("caerula_arbor:trail_powder"), item("caerula_arbor:trail_powder"), tag("caerula_arbor:fish_food"))
        );
        shaped(
                writer,
                "craft_radiant_berry",
                "misc",
                null,
                result("caerula_arbor:radiant_berries", 3),
                pattern("aba", "ccc", "bab"),
                key('a', item("minecraft:blaze_powder")),
                key('b', item("minecraft:prismarine_crystals")),
                key('c', item("caerula_arbor:fluore_berries"))
        );
        shapeless(
                writer,
                "craft_rainbow_candy",
                "misc",
                null,
                result("caerula_arbor:rainbow_candy", 3),
                ingredients(tag("minecraft:fruits"), tag("minecraft:fruits"), tag("minecraft:fruits"), item("caerula_arbor:paper_bag"), item("caerula_arbor:paper_bag"), item("caerula_arbor:paper_bag"), item("minecraft:gold_nugget"), item("minecraft:gold_nugget"), item("minecraft:gold_nugget"))
        );
        shaped(
                writer,
                "craft_rocinante_injector",
                "misc",
                null,
                result("caerula_arbor:rocinante_injector", 1),
                pattern(" a ", "bcb", " a "),
                key('a', item("caerula_arbor:tide_hunet_template")),
                key('b', item("caerula_arbor:cell_cluster")),
                key('c', item("caerula_arbor:dna_horse"))
        );
        shaped(
                writer,
                "craft_sal_eme_build",
                "building",
                null,
                result("caerula_arbor:emergency_aid_building_salviento", 2),
                pattern("aba", "cdc", "aea"),
                key('a', tag("minecraft:sal_viento_deco")),
                key('b', item("minecraft:prismarine_shard")),
                key('c', item("minecraft:prismarine_crystals")),
                key('d', item("minecraft:nether_star")),
                key('e', item("minecraft:redstone"))
        );
        shapeless(
                writer,
                "craft_skadi_kit",
                "misc",
                null,
                result("caerula_arbor:operation_kit_skadi", 1),
                ingredients(item("caerula_arbor:base_operation_kit"), item("caerula_arbor:targeted_transmitter_skadi"), item("caerula_arbor:targeted_transmitter_skadi"), item("caerula_arbor:aegir_sword"))
        );
        shaped(
                writer,
                "craft_spear",
                "equipment",
                null,
                result("caerula_arbor:the_spear", 1),
                pattern("  a", " b ", "b  "),
                key('a', item("caerula_arbor:kings_spear")),
                key('b', item("minecraft:bone"))
        );
        shapeless(
                writer,
                "craft_specter_kit",
                "misc",
                null,
                result("caerula_arbor:operation_kit_specter", 1),
                ingredients(item("caerula_arbor:base_operation_kit"), item("caerula_arbor:targeted_transmitter_specter"), item("caerula_arbor:targeted_transmitter_specter"), item("caerula_arbor:circular_saw"))
        );
        shaped(
                writer,
                "craft_stonecutter_doll",
                "misc",
                null,
                result("caerula_arbor:stonecutter_doll", 1),
                pattern(" a ", " b ", "cdc"),
                key('a', item("caerula_arbor:shell_of_stonecutter")),
                key('b', tag("minecraft:wool")),
                key('c', item("caerula_arbor:claw")),
                key('d', item("caerula_arbor:phloem_block"))
        );
        shaped(
                writer,
                "craft_swarmcaller_doll",
                "misc",
                null,
                result("caerula_arbor:swarmcaller_doll", 1),
                pattern(" a ", "bcb", "ded"),
                key('a', item("minecraft:tube_coral_fan")),
                key('b', item("caerula_arbor:sea_trail_mor")),
                key('c', tag("minecraft:wool")),
                key('d', item("caerula_arbor:ocean_chitin")),
                key('e', item("caerula_arbor:phloem_block"))
        );
        shaped(
                writer,
                "craft_tide_hunt_template",
                "equipment",
                null,
                result("caerula_arbor:tide_hunet_template", 2),
                pattern("aba", "aca", "aaa"),
                key('a', item("caerula_arbor:ocean_crystal")),
                key('b', item("caerula_arbor:tide_hunet_template")),
                key('c', item("caerula_arbor:knight_corpse"))
        );
        shaped(
                writer,
                "craft_tide_observe",
                "misc",
                null,
                result("caerula_arbor:tide_observation", 1),
                pattern("aba", "cdc", "aca"),
                key('a', item("minecraft:obsidian")),
                key('b', item("minecraft:diamond")),
                key('c', item("minecraft:deepslate")),
                key('d', item("minecraft:heart_of_the_sea"))
        );
        shaped(
                writer,
                "craft_tidelinked_shield",
                "equipment",
                null,
                result("caerula_arbor:tidelinked_shield", 1),
                pattern("aba", "bab", " b "),
                key('a', item("caerula_arbor:repeller_shell")),
                key('b', item("caerula_arbor:ocean_chitin"))
        );
        shaped(
                writer,
                "craft_tile",
                "misc",
                null,
                result("caerula_arbor:trail_tile", 4),
                pattern("aa", "aa"),
                key('a', item("caerula_arbor:trail_brick"))
        );
        shapeless(
                writer,
                "craft_trageted_base",
                "misc",
                null,
                result("caerula_arbor:targeted_base", 3),
                ingredients(item("caerula_arbor:immunosuppressor"), item("caerula_arbor:base_egg"), item("caerula_arbor:immunosuppressor"), tag("caerula_arbor:gene"), tag("caerula_arbor:moist_item"), tag("caerula_arbor:gene"), item("caerula_arbor:targeted_base"), item("caerula_arbor:cell_cluster"), item("caerula_arbor:targeted_base"))
        );
        smithingTransform(
                writer,
                "craft_trail_axe",
                item("caerula_arbor:flamarine_upgrade_template"),
                item("minecraft:netherite_axe"),
                item("caerula_arbor:trailrite"),
                result("caerula_arbor:trailrite_axe")
        );
        shaped(
                writer,
                "craft_trail_brick",
                "misc",
                null,
                result("caerula_arbor:trail_brick", 2),
                pattern("aa", "aa"),
                key('a', item("caerula_arbor:sea_trail_solid"))
        );
        shaped(
                writer,
                "craft_trail_button",
                "redstone",
                null,
                result("caerula_arbor:trail_button", 1),
                pattern("a"),
                key('a', item("caerula_arbor:sea_trail_solid"))
        );
        shaped(
                writer,
                "craft_trail_plate",
                "redstone",
                null,
                result("caerula_arbor:trail_pressure_plate", 1),
                pattern("aa"),
                key('a', item("caerula_arbor:trail_brick"))
        );
        shaped(
                writer,
                "craft_trail_slab",
                "misc",
                null,
                result("caerula_arbor:trail_slab", 6),
                pattern("aaa"),
                key('a', item("caerula_arbor:trail_brick"))
        );
        shaped(
                writer,
                "craft_trail_stair",
                "misc",
                null,
                result("caerula_arbor:trail_stair", 4),
                pattern("a  ", "aa ", "aaa"),
                key('a', item("caerula_arbor:trail_brick"))
        );
        smithingTransform(
                writer,
                "craft_trail_sword",
                item("caerula_arbor:flamarine_upgrade_template"),
                item("minecraft:netherite_sword"),
                item("caerula_arbor:trailrite"),
                result("caerula_arbor:trailrite_sword")
        );
        shapeless(
                writer,
                "craft_trailrite",
                "misc",
                null,
                result("caerula_arbor:trailrite", 1),
                ingredients(item("caerula_arbor:trail_shard"), item("caerula_arbor:trail_shard"), item("caerula_arbor:trail_shard"), item("caerula_arbor:trail_shard"), item("caerula_arbor:trail_powder_core"), item("minecraft:diamond"), item("minecraft:diamond"), item("minecraft:diamond"), item("minecraft:diamond"))
        );
        shaped(
                writer,
                "craft_trailrite_arrow",
                "misc",
                null,
                result("caerula_arbor:trailrite_arrow", 4),
                pattern("a", "b", "c"),
                key('a', item("caerula_arbor:trailrite_nugget")),
                key('b', item("caerula_arbor:cutin_stick")),
                key('c', item("minecraft:feather"))
        );
        shaped(
                writer,
                "craft_trial_wall",
                "misc",
                null,
                result("caerula_arbor:trail_wall", 6),
                pattern("aaa", "aaa"),
                key('a', item("caerula_arbor:trail_brick"))
        );
        shapeless(
                writer,
                "craft_ulpians_kit",
                "misc",
                null,
                result("caerula_arbor:operation_kit_ulpians", 1),
                ingredients(item("caerula_arbor:base_operation_kit"), item("caerula_arbor:targeted_transmitter_ulpians"), item("caerula_arbor:targeted_transmitter_ulpians"), item("caerula_arbor:hand_anchor"))
        );
        smithingTransform(
                writer,
                "craft_unfinished_beauty",
                item("caerula_arbor:hunter_gene_specter"),
                item("caerula_arbor:circular_saw"),
                item("caerula_arbor:diorite_sculpture"),
                result("caerula_arbor:unfinished_beauty")
        );
        shapeless(
                writer,
                "craft_wand",
                "equipment",
                null,
                result("caerula_arbor:dragon_wand", 1),
                ingredients(item("caerula_arbor:tidelinked_wand"), item("caerula_arbor:moist_dragon_heart"), item("minecraft:obsidian"), item("minecraft:obsidian"), item("minecraft:dragon_breath"))
        );
        shaped(
                writer,
                "cream_diamond",
                "equipment",
                null,
                result("caerula_arbor:trailed_diamond_sword", 1),
                pattern("ab"),
                key('a', item("minecraft:diamond_sword")),
                key('b', item("caerula_arbor:trail_cream"))
        );
        shaped(
                writer,
                "cream_gold",
                "equipment",
                null,
                result("caerula_arbor:trailed_golden_sword", 1),
                pattern("ab"),
                key('a', item("minecraft:golden_sword")),
                key('b', item("caerula_arbor:trail_cream"))
        );
        shaped(
                writer,
                "cream_iron",
                "equipment",
                null,
                result("caerula_arbor:trailed_iron_sword", 1),
                pattern("ab"),
                key('a', item("minecraft:iron_sword")),
                key('b', item("caerula_arbor:trail_cream"))
        );
        shaped(
                writer,
                "cream_netherite",
                "equipment",
                null,
                result("caerula_arbor:trailed_netherite_sword", 1),
                pattern("ab"),
                key('a', item("minecraft:netherite_sword")),
                key('b', item("caerula_arbor:trail_cream"))
        );
        smithingTransform(
                writer,
                "cream_smithed",
                item("minecraft:netherite_upgrade_smithing_template"),
                item("caerula_arbor:trailed_diamond_sword"),
                item("minecraft:netherite_ingot"),
                result("caerula_arbor:trailed_netherite_sword")
        );
        shaped(
                writer,
                "cream_stone",
                "equipment",
                null,
                result("caerula_arbor:trailed_stone_sword", 1),
                pattern("ab"),
                key('a', item("minecraft:stone_sword")),
                key('b', item("caerula_arbor:trail_cream"))
        );
        shaped(
                writer,
                "cream_wood",
                "equipment",
                null,
                result("caerula_arbor:trailed_wooden_sword", 1),
                pattern("ab"),
                key('a', item("minecraft:wooden_sword")),
                key('b', item("caerula_arbor:trail_cream"))
        );
        stonecutting(
                writer,
                "cur_trail_wall",
                item("caerula_arbor:trail_brick"),
                "caerula_arbor:trail_wall",
                1
        );
        stonecutting(
                writer,
                "cut_tile",
                item("caerula_arbor:trail_brick"),
                "caerula_arbor:trail_tile",
                1
        );
        stonecutting(
                writer,
                "cut_trail_brick",
                item("caerula_arbor:sea_trail_solid"),
                "caerula_arbor:trail_brick",
                1
        );
        stonecutting(
                writer,
                "cut_trail_slab",
                item("caerula_arbor:trail_brick"),
                "caerula_arbor:trail_slab",
                2
        );
        stonecutting(
                writer,
                "cut_trail_stair",
                item("caerula_arbor:trail_brick"),
                "caerula_arbor:trail_stair",
                1
        );
        shapeless(
                writer,
                "decluster_brocell",
                "misc",
                null,
                result("caerula_arbor:broken_ocean_cell", 9),
                ingredients(item("caerula_arbor:broken_cell_cluster"))
        );
        shapeless(
                writer,
                "decluster_cells",
                "misc",
                null,
                result("caerula_arbor:ocean_cell", 9),
                ingredients(item("caerula_arbor:cell_cluster"))
        );
        shapeless(
                writer,
                "deglow_ink_sac",
                "misc",
                null,
                result("minecraft:ink_sac", 4),
                ingredients(item("minecraft:glow_ink_sac"), item("caerula_arbor:broken_ocean_cell"), item("minecraft:glow_ink_sac"), item("minecraft:glow_ink_sac"))
        );
        shapeless(
                writer,
                "dessembe_comple",
                "misc",
                null,
                result("caerula_arbor:complex_chitin", 9),
                ingredients(item("caerula_arbor:complex_chitin_block"))
        );
        shapeless(
                writer,
                "dessemble_blockcry",
                "misc",
                null,
                result("caerula_arbor:ocean_crystal", 9),
                ingredients(item("caerula_arbor:ocean_crystal_block"))
        );
        shapeless(
                writer,
                "dessemble_chitinblock",
                "misc",
                null,
                result("caerula_arbor:ocean_chitin", 9),
                ingredients(item("caerula_arbor:chitin_block"))
        );
        shapeless(
                writer,
                "dessemble_cooked_fibre",
                "misc",
                null,
                result("caerula_arbor:cooked_fibre", 9),
                ingredients(item("caerula_arbor:cooked_fibre_block"))
        );
        shapeless(
                writer,
                "dessemble_fibre",
                "misc",
                null,
                result("caerula_arbor:ocean_fibre", 9),
                ingredients(item("caerula_arbor:fibre_block"))
        );
        shapeless(
                writer,
                "dessemble_het_block",
                "misc",
                null,
                result("caerula_arbor:heteropic_piece", 9),
                ingredients(item("caerula_arbor:heteropic_block"))
        );
        shapeless(
                writer,
                "dessemble_phloem",
                "misc",
                null,
                result("caerula_arbor:ocean_phloem", 9),
                ingredients(item("caerula_arbor:phloem_block"))
        );
        shapeless(
                writer,
                "dessemble_redstonium",
                "redstone",
                null,
                result("caerula_arbor:redstone_ingot", 9),
                ingredients(item("caerula_arbor:redstonium"))
        );
        shapeless(
                writer,
                "dessemble_trail_ingot",
                "misc",
                null,
                result("caerula_arbor:trailrite_nugget", 9),
                ingredients(item("caerula_arbor:trailrite"))
        );
        shapeless(
                writer,
                "dessemble_trailrite",
                "misc",
                null,
                result("caerula_arbor:trailrite", 9),
                ingredients(item("caerula_arbor:trailrite_block"))
        );
        shapeless(
                writer,
                "dictationless_chapter",
                "misc",
                null,
                result("caerula_arbor:dictationless_chapter", 1),
                ingredients(item("caerula_arbor:dictation_chapter"), item("minecraft:ink_sac"), item("minecraft:ink_sac"), item("minecraft:glow_ink_sac"), item("minecraft:glow_ink_sac"), item("caerula_arbor:ocean_phloem"), item("caerula_arbor:whirl_eye"))
        );
        shapeless(
                writer,
                "egg_custard",
                "misc",
                null,
                result("caerula_arbor:nethersea_egg_custard", 1),
                ingredients(item("minecraft:bowl"), item("caerula_arbor:nethersea_chicken_egg"), item("caerula_arbor:nethersea_chicken_egg"), item("minecraft:blaze_powder"))
        );
        cooking(
                writer,
                "extract_caffeine",
                "minecraft:smelting",
                "misc",
                null,
                item("minecraft:cocoa_beans"),
                "caerula_arbor:caffeine",
                1f,
                100
        );
        shapeless(
                writer,
                "fermen_eye",
                "misc",
                null,
                result("caerula_arbor:fermented_ocean_eye", 1),
                ingredients(item("caerula_arbor:ocean_eye"), item("caerula_arbor:cell_cluster"), item("caerula_arbor:caramel_mor"))
        );
        shaped(
                writer,
                "fix_cell",
                "misc",
                null,
                result("caerula_arbor:ocean_cell", 2),
                pattern("aaa", "aba", "aaa"),
                key('a', item("caerula_arbor:sea_trail_mor")),
                key('b', item("caerula_arbor:broken_ocean_cell"))
        );
        shapeless(
                writer,
                "fix_cell_block",
                "misc",
                null,
                result("caerula_arbor:cell_cluster", 2),
                ingredients(item("caerula_arbor:sea_trail_solid"), item("caerula_arbor:sea_trail_solid"), item("caerula_arbor:sea_trail_solid"), item("caerula_arbor:sea_trail_solid"), item("caerula_arbor:broken_cell_cluster"), item("caerula_arbor:sea_trail_solid"), item("caerula_arbor:sea_trail_solid"), item("caerula_arbor:sea_trail_solid"), item("caerula_arbor:sea_trail_solid"))
        );
        cooking(
                writer,
                "fry_egg",
                "minecraft:smelting",
                "misc",
                null,
                item("caerula_arbor:real_egg"),
                "caerula_arbor:fried_egg",
                8f,
                200
        );
        shaped(
                writer,
                "generate_trail",
                "misc",
                null,
                result("caerula_arbor:sea_trail_grown", 1),
                pattern("aaa", "aaa"),
                key('a', item("caerula_arbor:sea_trail_mor"))
        );
        shapeless(
                writer,
                "gild_isharmla_brick",
                "building",
                null,
                result("caerula_arbor:isharmla_brick_gilded", 1),
                ingredients(item("caerula_arbor:isharmla_brick_chiesled"), item("caerula_arbor:tear_isharmla"), item("minecraft:gold_nugget"), item("minecraft:gold_nugget"), item("minecraft:gold_nugget"))
        );
        shapeless(
                writer,
                "glow_ink_sac",
                "misc",
                null,
                result("minecraft:glow_ink_sac", 6),
                ingredients(item("minecraft:ink_sac"), item("caerula_arbor:heteropic_piece"), item("minecraft:ink_sac"), item("minecraft:ink_sac"), item("minecraft:ink_sac"), item("minecraft:ink_sac"))
        );
        shaped(
                writer,
                "het_block",
                "misc",
                null,
                result("caerula_arbor:heteropic_block", 1),
                pattern("aaa", "aaa", "aaa"),
                key('a', item("caerula_arbor:heteropic_piece"))
        );
        shaped(
                writer,
                "isharlma_top",
                "misc",
                null,
                result("caerula_arbor:anchor_upper", 1),
                pattern("aba", "aca", "ada"),
                key('a', item("caerula_arbor:isharmla_scute")),
                key('b', item("caerula_arbor:heteropic_piece")),
                key('c', item("caerula_arbor:heteropic_block")),
                key('d', item("minecraft:diamond"))
        );
        shaped(
                writer,
                "isharmla_bottom",
                "misc",
                null,
                result("caerula_arbor:anchor_lower", 1),
                pattern("aba", "aca", "ada"),
                key('a', item("caerula_arbor:isharmla_scute")),
                key('b', item("caerula_arbor:tear_isharmla")),
                key('c', item("caerula_arbor:moist_echo_shard")),
                key('d', item("caerula_arbor:heteropic_piece"))
        );
        shaped(
                writer,
                "isharmla_core",
                "misc",
                null,
                result("caerula_arbor:anchor_medium", 1),
                pattern("aba", "aca", "ada"),
                key('a', item("caerula_arbor:isharmla_scute")),
                key('b', item("caerula_arbor:ocean_machine")),
                key('c', tag("caerula_arbor:animus")),
                key('d', item("minecraft:redstone"))
        );
        shapeless(
                writer,
                "item_to_block_rec",
                "misc",
                null,
                result("caerula_arbor:block_recorder", 1),
                ingredients(item("caerula_arbor:caerula_recorder"))
        );
        shaped(
                writer,
                "log_to_wood",
                "building",
                null,
                result("caerula_arbor:nethersea_wood", 3),
                pattern("aa", "aa"),
                key('a', item("caerula_arbor:trail_log"))
        );
        shaped(
                writer,
                "made_oceanmachine",
                "misc",
                null,
                result("caerula_arbor:ocean_machine", 1),
                pattern("aba", "cdc", "aba"),
                key('a', item("caerula_arbor:ocean_crystal")),
                key('b', item("minecraft:redstone")),
                key('c', item("caerula_arbor:chitin_ingot")),
                key('d', item("caerula_arbor:ocean_eye"))
        );
        shaped(
                writer,
                "made_redstonium",
                "redstone",
                null,
                result("caerula_arbor:redstonium", 1),
                pattern("aaa", "aaa", "aaa"),
                key('a', item("caerula_arbor:redstone_ingot"))
        );
        shaped(
                writer,
                "make_bone",
                "misc",
                null,
                result("minecraft:bone", 1),
                pattern("aa", "aa"),
                key('a', item("caerula_arbor:bone_shard"))
        );
        shaped(
                writer,
                "make_cake_caramel",
                "misc",
                null,
                result("caerula_arbor:caramel_cake", 1),
                pattern("aaa", "bcb", "ddd"),
                key('a', item("caerula_arbor:caramel_mor")),
                key('b', item("caerula_arbor:cooked_mor")),
                key('c', item("caerula_arbor:real_egg")),
                key('d', item("minecraft:wheat"))
        );
        shaped(
                writer,
                "make_cake_trail",
                "misc",
                null,
                result("caerula_arbor:trail_cake", 1),
                pattern("aaa", "bcb", "ddd"),
                key('a', item("caerula_arbor:sea_trail_mor")),
                key('b', item("minecraft:sugar")),
                key('c', item("caerula_arbor:real_egg")),
                key('d', item("minecraft:wheat"))
        );
        shapeless(
                writer,
                "make_caramel_mor",
                "misc",
                null,
                result("caerula_arbor:caramel_mor", 2),
                ingredients(item("caerula_arbor:cooked_mor"), item("minecraft:sugar"), item("minecraft:sugar"), item("caerula_arbor:cooked_mor"))
        );
        shaped(
                writer,
                "make_door",
                "misc",
                null,
                result("caerula_arbor:trail_plank_door", 3),
                pattern("aa", "aa", "aa"),
                key('a', item("caerula_arbor:trail_plank"))
        );
        shapeless(
                writer,
                "make_fruit_jelly",
                "misc",
                null,
                result("caerula_arbor:fruit_jelly", 1),
                ingredients(item("caerula_arbor:colourfull_jelly"), item("caerula_arbor:colourfull_jelly"), item("minecraft:sugar"), item("caerula_arbor:fluore_berries"))
        );
        shaped(
                writer,
                "make_glasspane",
                "misc",
                null,
                result("caerula_arbor:ocean_glasspane", 16),
                pattern("aaa", "aaa"),
                key('a', item("caerula_arbor:ocean_glass"))
        );
        shaped(
                writer,
                "make_obisidian",
                "building",
                null,
                result("minecraft:obsidian", 1),
                pattern("aa", "aa"),
                key('a', item("caerula_arbor:obisidian_ball"))
        );
        shaped(
                writer,
                "make_ocean_glass",
                "misc",
                null,
                result("caerula_arbor:ocean_glass", 3),
                pattern("aa", "aa"),
                key('a', item("caerula_arbor:ocean_cutin"))
        );
        shaped(
                writer,
                "make_paperbag",
                "misc",
                null,
                result("caerula_arbor:paper_bag", 4),
                pattern("a a", "aba", "aaa"),
                key('a', item("minecraft:paper")),
                key('b', item("minecraft:leather"))
        );
        shapeless(
                writer,
                "make_reaper_egg",
                "misc",
                null,
                result("caerula_arbor:reaper_egg", 1),
                ingredients(item("caerula_arbor:base_egg"), item("caerula_arbor:dna_reaper"))
        );
        shapeless(
                writer,
                "make_soup",
                "misc",
                null,
                result("caerula_arbor:seaborn_soup", 1),
                ingredients(item("minecraft:bowl"), tag("caerula_arbor:fish_food"), tag("caerula_arbor:fish_food"), item("caerula_arbor:cooked_fakeegg"), item("minecraft:carrot"), item("minecraft:potato"))
        );
        shaped(
                writer,
                "moist_crystal",
                "misc",
                null,
                result("caerula_arbor:moist_crystal_item", 1),
                pattern("aba", "bcb", "aba"),
                key('a', item("caerula_arbor:trail_powder")),
                key('b', item("caerula_arbor:dragon_brand")),
                key('c', item("minecraft:end_crystal"))
        );
        shaped(
                writer,
                "new_tide_core",
                "misc",
                null,
                result("minecraft:conduit", 1),
                pattern("aaa", "aba", "aaa"),
                key('a', item("caerula_arbor:shell_of_stonecutter")),
                key('b', item("minecraft:heart_of_the_sea"))
        );
        shaped(
                writer,
                "nourished_apple_pie_craft",
                "misc",
                null,
                result("caerula_arbor:nourished_apple_pie", 2),
                pattern("aaa", "bcb", "ded"),
                key('a', item("minecraft:wheat")),
                key('b', item("caerula_arbor:trail_apple")),
                key('c', item("caerula_arbor:trail_golden_apple")),
                key('d', item("minecraft:sugar")),
                key('e', item("minecraft:shulker_shell"))
        );
        shaped(
                writer,
                "pholem_bow_made",
                "equipment",
                null,
                result("caerula_arbor:phloem_bow", 1),
                pattern(" ab", "acb", " ab"),
                key('a', item("caerula_arbor:phloem_block")),
                key('b', item("caerula_arbor:ocean_fibre")),
                key('c', item("caerula_arbor:ocean_eye"))
        );
        stonecutting(
                writer,
                "pillar_isharmla_brick",
                item("caerula_arbor:isharmla_brick"),
                "caerula_arbor:isharmla_brick_pillar",
                1
        );
        shaped(
                writer,
                "plank_fence",
                "misc",
                null,
                result("caerula_arbor:trail_planks_fence", 1),
                pattern("aba", "aba"),
                key('a', item("caerula_arbor:trail_plank")),
                key('b', item("minecraft:stick"))
        );
        shaped(
                writer,
                "plank_fencegate",
                "misc",
                null,
                result("caerula_arbor:trail_plank_fencedoor", 1),
                pattern("aba", "aba"),
                key('a', item("minecraft:stick")),
                key('b', item("caerula_arbor:trail_plank"))
        );
        shapeless(
                writer,
                "plank_made",
                "misc",
                null,
                result("caerula_arbor:trail_plank", 4),
                ingredients(tag("caerula_arbor:nethersea_logs"))
        );
        shaped(
                writer,
                "plank_slab",
                "misc",
                null,
                result("caerula_arbor:trail_plank_slab", 6),
                pattern("aaa"),
                key('a', item("caerula_arbor:trail_plank"))
        );
        shaped(
                writer,
                "plank_stair",
                "misc",
                null,
                result("caerula_arbor:trail_plank_stair", 4),
                pattern("a  ", "aa ", "aaa"),
                key('a', item("caerula_arbor:trail_plank"))
        );
        shaped(
                writer,
                "pressure_plate_made",
                "misc",
                null,
                result("caerula_arbor:trail_plank_pressure_plate", 1),
                pattern("aa"),
                key('a', item("caerula_arbor:trail_plank"))
        );
        shapeless(
                writer,
                "reverse_caerula_heart",
                "misc",
                null,
                result("caerula_arbor:caerula_heart", 1),
                ingredients(item("caerula_arbor:leviathan_animus"), item("caerula_arbor:tear_isharmla"), item("caerula_arbor:tear_isharmla"), item("caerula_arbor:tear_isharmla"), item("caerula_arbor:tear_isharmla"))
        );
        shapeless(
                writer,
                "reverse_emelight",
                "building",
                null,
                result("caerula_arbor:relic_curse_emelight", 1),
                ingredients(item("caerula_arbor:emergency_light"))
        );
        shaped(
                writer,
                "sdst_brick",
                "building",
                null,
                result("caerula_arbor:saltwind_sandstone", 1),
                pattern("aa", "aa"),
                key('a', item("caerula_arbor:saltsand"))
        );
        shaped(
                writer,
                "sdst_chiseled_made",
                "building",
                null,
                result("caerula_arbor:chiseled_saltwind_sandstone", 4),
                pattern("aa", "aa"),
                key('a', item("caerula_arbor:smooth_saltwind_sandatone"))
        );
        shaped(
                writer,
                "sdst_slab",
                "building",
                null,
                result("caerula_arbor:saltwind_sand_slab", 6),
                pattern("aaa"),
                key('a', item("caerula_arbor:saltwind_sandstone"))
        );
        shaped(
                writer,
                "sdst_smooth_made",
                "building",
                null,
                result("caerula_arbor:smooth_saltwind_sandatone", 4),
                pattern("aa", "aa"),
                key('a', item("caerula_arbor:saltwind_sandstone"))
        );
        shaped(
                writer,
                "sdst_smth_slab",
                "building",
                null,
                result("caerula_arbor:smooth_saltwind_sand_slab", 6),
                pattern("aaa"),
                key('a', item("caerula_arbor:smooth_saltwind_sandatone"))
        );
        shaped(
                writer,
                "sdst_smth_stair",
                "building",
                null,
                result("caerula_arbor:smooth_saltwind_sand_stair", 4),
                pattern("a  ", "aa ", "aaa"),
                key('a', item("caerula_arbor:smooth_saltwind_sandatone"))
        );
        shaped(
                writer,
                "sdst_smth_wall",
                "building",
                null,
                result("caerula_arbor:smooth_saltwind_sand_wall", 6),
                pattern("aaa", "aaa"),
                key('a', item("caerula_arbor:smooth_saltwind_sandatone"))
        );
        shaped(
                writer,
                "sdst_stair",
                "building",
                null,
                result("caerula_arbor:saltwind_sand_stair", 4),
                pattern("a  ", "aa ", "aaa"),
                key('a', item("caerula_arbor:saltwind_sandstone"))
        );
        shaped(
                writer,
                "sdst_wall",
                "building",
                null,
                result("caerula_arbor:saltwind_sand_wall", 6),
                pattern("aaa", "aaa"),
                key('a', item("caerula_arbor:saltwind_sandstone"))
        );
        shapeless(
                writer,
                "sea_copper_treaty",
                "misc",
                null,
                result("caerula_arbor:treaty_copper", 1),
                ingredients(item("minecraft:copper_ingot"), item("caerula_arbor:treaty_empty"), item("caerula_arbor:redstone_ingot"), item("caerula_arbor:redstone_ingot"), item("caerula_arbor:redstone_ingot"), item("caerula_arbor:redstone_ingot"), item("caerula_arbor:redstone_ingot"))
        );
        shapeless(
                writer,
                "seal_diamond_treaty",
                "misc",
                null,
                result("caerula_arbor:treaty_diamond", 1),
                ingredients(item("minecraft:diamond"), item("caerula_arbor:treaty_empty"), item("caerula_arbor:trail_powder"), item("caerula_arbor:redstone_ingot"), item("caerula_arbor:redstone_ingot"), item("caerula_arbor:redstonium"))
        );
        shapeless(
                writer,
                "seal_emerald_treaty",
                "misc",
                null,
                result("caerula_arbor:emerald_treaty", 1),
                ingredients(item("minecraft:emerald"), item("caerula_arbor:treaty_empty"), item("minecraft:amethyst_shard"), item("caerula_arbor:redstonium"))
        );
        shapeless(
                writer,
                "seal_gold_treaty",
                "misc",
                null,
                result("caerula_arbor:treaty_gold", 1),
                ingredients(item("minecraft:gold_ingot"), item("caerula_arbor:treaty_empty"), item("caerula_arbor:redstonium"))
        );
        shapeless(
                writer,
                "seal_iron_treaty",
                "misc",
                null,
                result("caerula_arbor:treaty_iron", 1),
                ingredients(item("minecraft:iron_ingot"), item("caerula_arbor:treaty_copper"), item("minecraft:white_tulip"), item("caerula_arbor:redstone_ingot"), item("caerula_arbor:redstone_ingot"))
        );
        shapeless(
                writer,
                "seal_netherite_treaty",
                "misc",
                null,
                result("caerula_arbor:treaty_netherite", 1),
                ingredients(item("minecraft:netherite_ingot"), item("caerula_arbor:treaty_diamond"), item("caerula_arbor:redstonium"), item("caerula_arbor:redstonium"), item("caerula_arbor:banned_item"))
        );
        stonecutting(
                writer,
                "slab_isharmla_brick",
                item("caerula_arbor:isharmla_brick"),
                "caerula_arbor:isharmla_slab",
                2
        );
        stonecutting(
                writer,
                "smash_bone",
                item("minecraft:bone"),
                "caerula_arbor:bone_shard",
                4
        );
        cooking(
                writer,
                "smelt_mor",
                "minecraft:smelting",
                "food",
                null,
                item("caerula_arbor:sea_trail_mor"),
                "caerula_arbor:cooked_mor",
                2f,
                180
        );
        cooking(
                writer,
                "smelt_redstine_ingot",
                "minecraft:blasting",
                "misc",
                null,
                item("caerula_arbor:redstone_ingot"),
                "minecraft:redstone",
                4f,
                160
        );
        cooking(
                writer,
                "smelt_redstoninium",
                "minecraft:blasting",
                "misc",
                null,
                item("caerula_arbor:redstonium"),
                "minecraft:redstone_block",
                42f,
                200
        );
        smithingTransform(
                writer,
                "smith_anchor",
                item("caerula_arbor:hunter_gene_ulpians"),
                item("caerula_arbor:hand_anchor"),
                item("caerula_arbor:anchor_forge_ingot"),
                result("caerula_arbor:unambiguous_direction")
        );
        smithingTransform(
                writer,
                "smith_comp_chitin_bow",
                item("caerula_arbor:ocean_trim_template"),
                item("caerula_arbor:chitin_bow"),
                item("caerula_arbor:complex_chitin"),
                result("caerula_arbor:complex_chitin_bow")
        );
        smithingTransform(
                writer,
                "smith_compc_helm",
                item("caerula_arbor:ocean_trim_template"),
                item("caerula_arbor:chitin_armor_helmet"),
                item("caerula_arbor:complex_chitin"),
                result("caerula_arbor:complexchitin_armor_helmet")
        );
        smithingTransform(
                writer,
                "smith_compchi_axe",
                item("caerula_arbor:ocean_trim_template"),
                item("caerula_arbor:chitin_axe"),
                item("caerula_arbor:complex_chitin"),
                result("caerula_arbor:complex_chitin_axe")
        );
        smithingTransform(
                writer,
                "smith_compchi_hoe",
                item("caerula_arbor:ocean_trim_template"),
                item("caerula_arbor:chitin_hoe"),
                item("caerula_arbor:complex_chitin"),
                result("caerula_arbor:complex_chitin_hoe")
        );
        smithingTransform(
                writer,
                "smith_compchi_shovel",
                item("caerula_arbor:ocean_trim_template"),
                item("caerula_arbor:chitin_shovel"),
                item("caerula_arbor:complex_chitin"),
                result("caerula_arbor:complex_chitin_shovel")
        );
        smithingTransform(
                writer,
                "smith_compchit_pick",
                item("caerula_arbor:ocean_trim_template"),
                item("caerula_arbor:chitin_pickaxe"),
                item("caerula_arbor:complex_chitin"),
                result("caerula_arbor:complex_chitin_pickaxe")
        );
        smithingTransform(
                writer,
                "smith_compchitin_sword",
                item("caerula_arbor:ocean_trim_template"),
                item("caerula_arbor:chitin_sword"),
                item("caerula_arbor:complex_chitin"),
                result("caerula_arbor:complex_chitin_sword")
        );
        smithingTransform(
                writer,
                "smith_complexc_boot",
                item("caerula_arbor:ocean_trim_template"),
                item("caerula_arbor:chitin_armor_boots"),
                item("caerula_arbor:complex_chitin"),
                result("caerula_arbor:complexchitin_armor_boots")
        );
        smithingTransform(
                writer,
                "smith_complexc_chest",
                item("caerula_arbor:ocean_trim_template"),
                item("caerula_arbor:chitin_armor_chestplate"),
                item("caerula_arbor:complex_chitin"),
                result("caerula_arbor:complexchitin_armor_chestplate")
        );
        smithingTransform(
                writer,
                "smith_complexc_leg",
                item("caerula_arbor:ocean_trim_template"),
                item("caerula_arbor:chitin_armor_leggings"),
                item("caerula_arbor:complex_chitin"),
                result("caerula_arbor:complexchitin_armor_leggings")
        );
        smithingTransform(
                writer,
                "smith_gladiia_weapon",
                item("caerula_arbor:hunter_gene_gladiia"),
                item("caerula_arbor:aegir_lancet"),
                item("caerula_arbor:pale_gold_pendant"),
                result("caerula_arbor:broken_sea")
        );
        smithingTransform(
                writer,
                "smith_knight_boots",
                item("caerula_arbor:tide_hunet_template"),
                item("minecraft:iron_boots"),
                item("caerula_arbor:knight_corpse"),
                result("caerula_arbor:knight_iron_boots")
        );
        smithingTransform(
                writer,
                "smith_knight_chest",
                item("caerula_arbor:tide_hunet_template"),
                item("minecraft:iron_chestplate"),
                item("caerula_arbor:knight_corpse"),
                result("caerula_arbor:knight_iron_chestplate")
        );
        smithingTransform(
                writer,
                "smith_knight_helm",
                item("caerula_arbor:tide_hunet_template"),
                item("minecraft:iron_helmet"),
                item("caerula_arbor:knight_corpse"),
                result("caerula_arbor:knight_iron_helmet")
        );
        smithingTransform(
                writer,
                "smith_knight_leggings",
                item("caerula_arbor:tide_hunet_template"),
                item("minecraft:iron_leggings"),
                item("caerula_arbor:knight_corpse"),
                result("caerula_arbor:knight_iron_leggings")
        );
        smithingTransform(
                writer,
                "smith_long_knight_sword",
                item("caerula_arbor:tide_hunet_template"),
                item("caerula_arbor:iron_sword_of_knight_corpus"),
                item("caerula_arbor:knight_corpse"),
                result("caerula_arbor:long_sword_of_knight_corpus")
        );
        smithingTransform(
                writer,
                "smith_skadi_sword",
                item("caerula_arbor:hunter_gene_skadi"),
                item("caerula_arbor:aegir_sword"),
                item("caerula_arbor:moist_bag"),
                result("caerula_arbor:skadi_sword")
        );
        smithingTransform(
                writer,
                "smith_trailrite_boots",
                item("caerula_arbor:flamarine_upgrade_template"),
                item("minecraft:netherite_boots"),
                item("caerula_arbor:trailrite"),
                result("caerula_arbor:trailrite_armor_boots")
        );
        smithingTransform(
                writer,
                "smith_trailrite_bow",
                item("caerula_arbor:flamarine_upgrade_template"),
                item("caerula_arbor:phloem_bow"),
                item("caerula_arbor:trailrite"),
                result("caerula_arbor:trailrite_bow")
        );
        smithingTransform(
                writer,
                "smith_trailrite_chest",
                item("caerula_arbor:flamarine_upgrade_template"),
                item("minecraft:netherite_chestplate"),
                item("caerula_arbor:trailrite"),
                result("caerula_arbor:trailrite_armor_chestplate")
        );
        smithingTransform(
                writer,
                "smith_trailrite_helm",
                item("caerula_arbor:flamarine_upgrade_template"),
                item("minecraft:netherite_helmet"),
                item("caerula_arbor:trailrite"),
                result("caerula_arbor:trailrite_armor_helmet")
        );
        smithingTransform(
                writer,
                "smith_trailrite_hoe",
                item("caerula_arbor:flamarine_upgrade_template"),
                item("minecraft:netherite_hoe"),
                item("caerula_arbor:trailrite"),
                result("caerula_arbor:trailrite_hoe")
        );
        smithingTransform(
                writer,
                "smith_trailrite_leg",
                item("caerula_arbor:flamarine_upgrade_template"),
                item("minecraft:netherite_leggings"),
                item("caerula_arbor:trailrite"),
                result("caerula_arbor:trailrite_armor_leggings")
        );
        smithingTransform(
                writer,
                "smith_trailrite_pick",
                item("caerula_arbor:flamarine_upgrade_template"),
                item("minecraft:netherite_pickaxe"),
                item("caerula_arbor:trailrite"),
                result("caerula_arbor:trailrite_pickaxe")
        );
        smithingTransform(
                writer,
                "smith_trailrite_shovel",
                item("caerula_arbor:flamarine_upgrade_template"),
                item("minecraft:netherite_shovel"),
                item("caerula_arbor:trailrite"),
                result("caerula_arbor:trailrite_shovel")
        );
        smithingTransform(
                writer,
                "smith_trident",
                item("caerula_arbor:ocean_trim_template"),
                item("caerula_arbor:complex_chitin_sword"),
                item("caerula_arbor:chitin_knife"),
                result("caerula_arbor:legendary_spear")
        );
        cooking(
                writer,
                "smoke_block_fibre",
                "minecraft:smoking",
                "food",
                null,
                item("caerula_arbor:fibre_block"),
                "caerula_arbor:cooked_fibre_block",
                13.5f,
                160
        );
        cooking(
                writer,
                "smoke_cell",
                "minecraft:smoking",
                "food",
                null,
                item("caerula_arbor:broken_cell_cluster"),
                "caerula_arbor:cooked_broken_cell_cluster",
                4f,
                80
        );
        cooking(
                writer,
                "smoke_celll",
                "minecraft:smoking",
                "food",
                null,
                item("caerula_arbor:cell_cluster"),
                "caerula_arbor:cooked_cell_cluster",
                6f,
                100
        );
        cooking(
                writer,
                "smoke_claw",
                "minecraft:smoking",
                "food",
                null,
                item("caerula_arbor:claw"),
                "caerula_arbor:cooked_claw",
                1f,
                100
        );
        cooking(
                writer,
                "smoke_egg",
                "minecraft:smoking",
                "misc",
                null,
                item("caerula_arbor:real_egg"),
                "caerula_arbor:fried_egg",
                8f,
                100
        );
        cooking(
                writer,
                "smoke_fish",
                "minecraft:smoking",
                "food",
                null,
                item("caerula_arbor:collector_meat"),
                "caerula_arbor:cooked_collector",
                1f,
                100
        );
        cooking(
                writer,
                "smoke_kebab",
                "minecraft:smoking",
                "food",
                "food",
                item("caerula_arbor:kebab_raw"),
                "caerula_arbor:kebab_cooked",
                2f,
                150
        );
        cooking(
                writer,
                "smoke_mor",
                "minecraft:smoking",
                "food",
                null,
                item("caerula_arbor:sea_trail_mor"),
                "caerula_arbor:cooked_mor",
                2f,
                90
        );
        cooking(
                writer,
                "smoke_oceanfibre",
                "minecraft:smoking",
                "food",
                null,
                item("caerula_arbor:ocean_fibre"),
                "caerula_arbor:cooked_fibre",
                1.5f,
                80
        );
        cooking(
                writer,
                "smoke_peduncle",
                "minecraft:smoking",
                "food",
                null,
                item("caerula_arbor:ocean_peduncle"),
                "caerula_arbor:cooked_peduncle",
                1.5f,
                120
        );
        cooking(
                writer,
                "smokefakegg",
                "minecraft:smoking",
                "food",
                null,
                item("caerula_arbor:fake_egg"),
                "caerula_arbor:cooked_fakeegg",
                0f,
                60
        );
        shaped(
                writer,
                "solided_trail",
                "misc",
                null,
                result("caerula_arbor:sea_trail_solid", 1),
                pattern("aaa", "aba", "aaa"),
                key('a', item("caerula_arbor:sea_trail_mor")),
                key('b', item("caerula_arbor:sea_trail_grown"))
        );
        stonecutting(
                writer,
                "stair_isharmla_brick",
                item("caerula_arbor:isharmla_brick"),
                "caerula_arbor:isharmla_stair",
                1
        );
        stonecutting(
                writer,
                "stone_chisel",
                item("caerula_arbor:saltwind_sandstone"),
                "caerula_arbor:chiseled_saltwind_sandstone",
                1
        );
        stonecutting(
                writer,
                "stone_cur_smthwall",
                item("caerula_arbor:saltwind_sandstone"),
                "caerula_arbor:smooth_saltwind_sand_wall",
                1
        );
        stonecutting(
                writer,
                "stone_cut_chwall",
                item("caerula_arbor:saltwind_sandstone"),
                "caerula_arbor:chieseled_saltwind_sand_wall",
                1
        );
        stonecutting(
                writer,
                "stone_cut_smooth",
                item("caerula_arbor:saltwind_sandstone"),
                "caerula_arbor:smooth_saltwind_sandatone",
                1
        );
        stonecutting(
                writer,
                "stone_cut_smthslab",
                item("caerula_arbor:saltwind_sandstone"),
                "caerula_arbor:smooth_saltwind_sand_slab",
                2
        );
        stonecutting(
                writer,
                "stone_cut_smthstair",
                item("caerula_arbor:saltwind_sandstone"),
                "caerula_arbor:smooth_saltwind_sand_stair",
                1
        );
        stonecutting(
                writer,
                "stone_cut_stair",
                item("caerula_arbor:saltwind_sandstone"),
                "caerula_arbor:saltwind_sand_stair",
                1
        );
        stonecutting(
                writer,
                "stone_cut_wall",
                item("caerula_arbor:saltwind_sandstone"),
                "caerula_arbor:saltwind_sand_wall",
                1
        );
        stonecutting(
                writer,
                "stonec_cut_slab",
                item("caerula_arbor:saltwind_sandstone"),
                "caerula_arbor:saltwind_sand_slab",
                2
        );
        stonecutting(
                writer,
                "stonecut_shell",
                item("caerula_arbor:shell_of_stonecutter"),
                "caerula_arbor:ocean_chitin",
                2
        );
        shaped(
                writer,
                "strip_log_to_wood",
                "building",
                null,
                result("caerula_arbor:stripped_nethersea_wood", 3),
                pattern("aa", "aa"),
                key('a', item("caerula_arbor:stripped_trail_log"))
        );
        shapeless(
                writer,
                "stripped_log_plank",
                "misc",
                null,
                result("caerula_arbor:trail_plank", 4),
                ingredients(item("caerula_arbor:stripped_trail_log"))
        );
        shaped(
                writer,
                "trail_bomb",
                "misc",
                null,
                result("caerula_arbor:bomb_trailer", 4),
                pattern("aba", "bcb", "aba"),
                key('a', item("minecraft:iron_block")),
                key('b', item("minecraft:gunpowder")),
                key('c', item("caerula_arbor:ocean_machine"))
        );
        shapeless(
                writer,
                "tulip_medic",
                "misc",
                null,
                result("caerula_arbor:tulip_medcine", 1),
                ingredients(item("caerula_arbor:trail_golden_apple"), item("caerula_arbor:ocean_phloem"), item("minecraft:golden_carrot"), tag("minecraft:tulip"), item("caerula_arbor:immunosuppressor"), tag("minecraft:tulip"), item("minecraft:magma_cream"), item("minecraft:blaze_powder"), item("caerula_arbor:viviparous_lily"))
        );
        shaped(
                writer,
                "vraft_breath_tide",
                "misc",
                null,
                result("caerula_arbor:breath_of_tide", 1),
                pattern("abc", "ddd"),
                key('a', item("minecraft:conduit")),
                key('b', item("caerula_arbor:whirl_eye")),
                key('c', item("caerula_arbor:heteropic_piece")),
                key('d', item("caerula_arbor:ocean_cutin"))
        );
        stonecutting(
                writer,
                "wall_isahrmla_brick",
                item("caerula_arbor:isharmla_brick"),
                "caerula_arbor:isharmla_wall",
                1
        );
        stonecutting(
                writer,
                "wall_isharmla_chiesle",
                item("caerula_arbor:isharmla_brick_chiesled"),
                "caerula_arbor:isharmla_wall_chiesled",
                1
        );
        stonecutting(
                writer,
                "wall_isharmla_gilded",
                item("caerula_arbor:isharmla_brick_gilded"),
                "caerula_arbor:isharmla_wall_gilded",
                1
        );
    }

    private record KeyEntry(char key, IngredientEntry ingredient) {
    }

    private record IngredientEntry(String type, String value) {
        private JsonObject toJson() {
            var json = new JsonObject();
            json.addProperty(type, value);
            return json;
        }
    }

    private record JsonFinishedRecipe(ResourceLocation id, JsonObject recipe) implements FinishedRecipe {
        @Override
        public void serializeRecipeData(@NotNull JsonObject json) {
            for (var entry : recipe.entrySet()) {
                if (!entry.getKey().equals("type")) {
                    json.add(entry.getKey(), entry.getValue().deepCopy());
                }
            }
        }

        @Override
        public @NotNull JsonObject serializeRecipe() {
            return recipe.deepCopy();
        }

        @Override
        public @NotNull ResourceLocation getId() {
            return id;
        }

        @Override
        public @NotNull RecipeSerializer<?> getType() {
            return RecipeSerializer.SHAPELESS_RECIPE;
        }

        @Override
        public @Nullable JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public @Nullable ResourceLocation getAdvancementId() {
            return null;
        }
    }
}