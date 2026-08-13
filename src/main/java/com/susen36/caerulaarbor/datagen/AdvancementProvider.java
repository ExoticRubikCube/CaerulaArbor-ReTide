package com.susen36.caerulaarbor.datagen;

import com.google.gson.JsonObject;
import com.susen36.caerulaarbor.CaerulaArbor;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 生成进度数据
 */
@SuppressWarnings("SameParameterValue")
public class AdvancementProvider implements AdvancementSubProvider {

    public static ArrayList<AdvancementHolder> advancements = new ArrayList<>();

    /**
     * 创建进度显示信息
     *
     * @param icon           图标物品 ID
     * @param title          标题翻译键
     * @param description    描述翻译键
     * @param background     背景贴图 ID，可为 null
     * @param frame          进度边框类型
     * @param showToast      是否显示 toast
     * @param announceToChat 是否发送聊天栏公告
     * @param hidden         是否隐藏
     * @return 显示信息
     */
    private static DisplayInfo display(String icon, String title, String description, String background, AdvancementType frame, boolean showToast, boolean announceToChat, boolean hidden) {
        return new DisplayInfo(
                item(icon),
                Component.translatable(title),
                Component.translatable(description),
                background == null ? Optional.empty() : Optional.of(resLoc(background)),
                frame,
                showToast,
                announceToChat,
                hidden
        );
    }

    /**
     * 创建无法自动完成的条件
     *
     * @return impossible 条件
     */
    private static Criterion impossible() {
        var triggerId = ResourceLocation.withDefaultNamespace("impossible");
        return new Criterion<>(lookupTrigger(triggerId), new ImpossibleTrigger.TriggerInstance());
    }

    /**
     * 创建物品数量变化条件
     *
     * @param item 物品 ID
     * @param min  最小数量
     * @param max  最大数量
     * @return inventory_changed 条件
     */
    private static Criterion inventoryChanged(String item, int min, int max) {
        var itemId = ResourceLocation.parse(item);
        var itemValue = BuiltInRegistries.ITEM.getOptional(itemId)
                .orElseThrow(() -> new IllegalStateException("Unknown item: " + item));
        var predicate = ItemPredicate.Builder.item()
                .of(itemValue)
                .withCount(MinMaxBounds.Ints.between(min, max))
                .build();
        return InventoryChangeTrigger.TriggerInstance.hasItems(predicate);
    }

    /**
     * 创建放置方块条件
     *
     * @param block 方块 ID
     * @return placed_block 条件
     */
    private static Criterion placedBlock(String block) {
        var blockId = ResourceLocation.parse(block);
        var blockValue = BuiltInRegistries.BLOCK.getOptional(blockId)
                .orElseThrow(() -> new IllegalStateException("Unknown block: " + block));
        return ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blockValue);
    }

    /**
     * 创建玩家受到实体伤害条件
     *
     * @param sourceEntity 伤害来源实体 ID
     * @param minTaken     最小伤害
     * @param maxTaken     最大伤害
     * @param blocked      是否要求被盾牌阻挡
     * @return entity_hurt_player 条件
     */
    private static Criterion entityHurtPlayer(String sourceEntity, int minTaken, int maxTaken, boolean blocked) {
        var entityId = ResourceLocation.parse(sourceEntity);
        var entityValue = BuiltInRegistries.ENTITY_TYPE.getOptional(entityId)
                .orElseThrow(() -> new IllegalStateException("Unknown entity type: " + sourceEntity));
        var sourceEntityPred = EntityPredicate.Builder.entity()
                .entityType(EntityTypePredicate.of(entityValue))
                .build();
        var damageBuilder = DamagePredicate.Builder.damageInstance()
                .takenDamage(MinMaxBounds.Doubles.between(minTaken, maxTaken))
                .sourceEntity(sourceEntityPred)
                .blocked(blocked);
        return EntityHurtPlayerTrigger.TriggerInstance.entityHurtPlayer(damageBuilder);
    }

    /**
     * 创建指定 trigger 的进度条件
     *
     * @param trigger    trigger ID
     * @param conditions 条件 JSON
     * @return 进度条件
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Criterion<?> criterion(String trigger, JsonObject conditions) {
        var triggerId = ResourceLocation.parse(trigger);
        var triggerObj = lookupTrigger(triggerId);
        var instance = new JsonCriterionTriggerInstance(triggerId, conditions);
        return new Criterion(triggerObj, instance);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static CriterionTrigger lookupTrigger(ResourceLocation triggerId) {
        var triggerRegistryName = ResourceLocation.withDefaultNamespace("trigger_type");
        var triggerRegistry = (net.minecraft.core.Registry<CriterionTrigger>) BuiltInRegistries.REGISTRY.get(triggerRegistryName);
        if (triggerRegistry == null) {
            throw new IllegalStateException("Trigger registry not found for: " + triggerId + " (registry name: " + triggerRegistryName + ")");
        }
        var triggerObj = triggerRegistry.get(triggerId);
        if (triggerObj == null) {
            throw new IllegalStateException("Unknown trigger: " + triggerId);
        }
        return triggerObj;
    }

    /**
     * 创建只解锁配方的奖励
     *
     * @param recipes 配方 ID 列表
     * @return 奖励 builder
     */
    private static AdvancementRewards.Builder recipeRewards(String... recipes) {
        return rewards(0, new String[0], recipes);
    }

    /**
     * 创建经验与配方奖励
     *
     * @param experience 经验值
     * @param recipes    配方 ID 列表
     * @return 奖励 builder
     */
    private static AdvancementRewards.Builder experienceRewards(int experience, String... recipes) {
        return rewards(experience, new String[0], recipes);
    }

    /**
     * 创建经验、战利品表与配方奖励
     *
     * @param experience 经验值
     * @param lootTables 战利品表 ID 列表
     * @param recipes    配方 ID 列表
     * @return 奖励 builder
     */
    private static AdvancementRewards.Builder lootRewards(int experience, String[] lootTables, String... recipes) {
        return rewards(experience, lootTables, recipes);
    }

    /**
     * 创建进度奖励
     *
     * @param experience 经验值
     * @param lootTables 战利品表 ID 列表
     * @param recipes    配方 ID 列表
     * @return 奖励 builder
     */
    private static AdvancementRewards.Builder rewards(int experience, String[] lootTables, String... recipes) {
        var builder = new AdvancementRewards.Builder();
        builder.addExperience(experience);
        for (var lootTable : lootTables) {
            builder.addLootTable(ResourceKey.create(Registries.LOOT_TABLE, resLoc(lootTable)));
        }
        for (var recipe : recipes) {
            builder.addRecipe(resLoc(recipe));
        }
        return builder;
    }

    /**
     * 创建战利品表数组
     *
     * @param lootTables 战利品表 ID 列表
     * @return 战利品表数组
     */
    private static String[] loot(String... lootTables) {
        return lootTables;
    }

    /**
     * 创建进度图标物品栈
     *
     * @param id 物品 ID
     * @return 图标物品栈
     */
    private static ItemStack item(String id) {
        var item = BuiltInRegistries.ITEM.get(resLoc(id));
        return new ItemStack(Objects.requireNonNull(item, "Missing advancement icon item: " + id));
    }

    /**
     * 创建 caerula_arbor 命名空间资源 ID 字符串
     *
     * @param path 资源路径
     * @return 资源 ID 字符串（格式：namespace:path）
     */
    private static String modLoc(String path) {
        return CaerulaArbor.MODID + ":" + path;
    }

    /**
     * 解析资源 ID，省略命名空间时按 minecraft 处理
     *
     * @param id 资源 ID
     * @return 资源 ID
     */
    private static ResourceLocation resLoc(String id) {
        var separator = id.indexOf(':');
        if (separator >= 0) {
            String namespace = id.substring(0, separator);
            String path = id.substring(separator + 1);
            if (ResourceLocation.DEFAULT_NAMESPACE.equals(namespace)) {
                return ResourceLocation.withDefaultNamespace(path);
            }
            return ResourceLocation.fromNamespaceAndPath(namespace, path);
        }
        return ResourceLocation.withDefaultNamespace(id);
    }

    /**
     * 写出全部进度定义
     *
     * @param registries         注册表查询 provider
     * @param saver              进度输出回调
     */
    @Override
    public void generate(HolderLookup.@NotNull Provider registries, @NotNull Consumer<AdvancementHolder> saver) {
        advancements.clear();

        /* encounter_from_the_ocean (root) */
        var encounterFromTheOcean = Advancement.Builder.advancement()
                .display(display(
                        "caerula_arbor:bucket_floater",
                        "advancements.encounter_from_the_ocean.title",
                        "advancements.encounter_from_the_ocean.descr",
                        "caerula_arbor:textures/screen/adv_bg.png",
                        AdvancementType.TASK,
                        true,
                        false,
                        false
                ))
                .addCriterion("encounter_from_the_ocean_0", impossible())
                .rewards(recipeRewards(
                        "caerula_arbor:craft_meat_can",
                        "caerula_arbor:craft_can",
                        "caerula_arbor:craft_bowl_seagrass",
                        "caerula_arbor:extract_caffeine",
                        "caerula_arbor:craft_coffee_candy",
                        "caerula_arbor:make_berry",
                        "caerula_arbor:craft_fluro_berry",
                        "caerula_arbor:craft_radiant_berry",
                        "caerula_arbor:craft_rainbow_candy",
                        "caerula_arbor:make_bone",
                        "caerula_arbor:cook_oceanfibre",
                        "caerula_arbor:smoke_oceanfibre",
                        "caerula_arbor:campfire_oceanfibre",
                        "caerula_arbor:craft_crystal_sword",
                        "caerula_arbor:craft_cryst_pick",
                        "caerula_arbor:craft_cryst_hoe",
                        "caerula_arbor:craft_cryst_shovel",
                        "caerula_arbor:craft_cryst_axe",
                        "caerula_arbor:make_obisidian",
                        "caerula_arbor:craft_noodle",
                        "caerula_arbor:cook_cell",
                        "caerula_arbor:cook_celll",
                        "caerula_arbor:smoke_cell",
                        "caerula_arbor:smoke_celll",
                        "caerula_arbor:campfire_cell",
                        "caerula_arbor:campfire_celll",
                        "caerula_arbor:fry_egg",
                        "caerula_arbor:smoke_egg",
                        "caerula_arbor:camp_egg",
                        "caerula_arbor:cook_peduncle",
                        "caerula_arbor:smoke_peduncle",
                        "caerula_arbor:campfire_peduncle",
                        "caerula_arbor:cookfakeegg",
                        "caerula_arbor:smokefakegg",
                        "caerula_arbor:campfire_fakeegg",
                        "caerula_arbor:cook_fish",
                        "caerula_arbor:smoke_fish",
                        "caerula_arbor:campfire_fish",
                        "caerula_arbor:cook_claw",
                        "caerula_arbor:smoke_claw",
                        "caerula_arbor:campfire_claw",
                        "caerula_arbor:make_soup",
                        "caerula_arbor:sdst_slab",
                        "caerula_arbor:sdst_stair",
                        "caerula_arbor:sdst_wall",
                        "caerula_arbor:sdst_smth_slab",
                        "caerula_arbor:sdst_smth_stair",
                        "caerula_arbor:sdst_smth_wall",
                        "caerula_arbor:stone_cut_smooth",
                        "caerula_arbor:stonec_cut_slab",
                        "caerula_arbor:stone_cut_stair",
                        "caerula_arbor:stone_cut_smthstair",
                        "caerula_arbor:stone_cut_smthslab",
                        "caerula_arbor:stone_chisel",
                        "caerula_arbor:stone_cut_wall",
                        "caerula_arbor:stone_cur_smthwall",
                        "caerula_arbor:stone_cut_chwall",
                        "caerula_arbor:plank_made",
                        "caerula_arbor:plank_slab",
                        "caerula_arbor:plank_stair",
                        "caerula_arbor:plank_fence",
                        "caerula_arbor:plank_fencegate",
                        "caerula_arbor:button_made",
                        "caerula_arbor:pressure_plate_made",
                        "caerula_arbor:stripped_log_plank",
                        "caerula_arbor:made_redstonium",
                        "caerula_arbor:dessemble_redstonium",
                        "caerula_arbor:assemble_phloem",
                        "caerula_arbor:dessemble_phloem",
                        "caerula_arbor:assemble_fibre",
                        "caerula_arbor:dessemble_fibre",
                        "caerula_arbor:assemble_cooked_fibre",
                        "caerula_arbor:dessemble_cooked_fibre",
                        "caerula_arbor:brand_apple",
                        "caerula_arbor:cook_block_fibre",
                        "caerula_arbor:craft_cooked_kebab",
                        "caerula_arbor:cooked_kebab",
                        "caerula_arbor:campfire_mor",
                        "caerula_arbor:campfire_block_fibre",
                        "caerula_arbor:camp_kebab",
                        "caerula_arbor:reverse_emelight",
                        "caerula_arbor:smelt_redstine_ingot",
                        "caerula_arbor:smelt_redstoninium",
                        "caerula_arbor:stonecut_shell",
                        "caerula_arbor:brew_fast_swim",
                        "caerula_arbor:lengthen_fast_swim",
                        "caerula_arbor:upgrade_fast_swim",
                        "caerula_arbor:new_tide_core",
                        "caerula_arbor:craft_swarmcaller_doll",
                        "caerula_arbor:craft_stonecutter_doll",
                        "caerula_arbor:craft_nethersea_icecream",
                        "caerula_arbor:craft_fluore_icecream",
                        "caerula_arbor:aegir_glass_deco",
                        "caerula_arbor:craft_aegir_glass_bar",
                        "caerula_arbor:aegir_glass_arch",
                        "caerula_arbor:brew_ins_sanity",
                        "caerula_arbor:brew_sanity_cure",
                        "caerula_arbor:brew_inv_sanity",
                        "caerula_arbor:brew_perc_regene",
                        "caerula_arbor:craft_patchouli_book"
                ))
                .save(saver, modLoc("encounter_from_the_ocean"));
        advancements.add(encounterFromTheOcean);

        /* another_breath (caerula_arbor:encounter_from_the_ocean) */
        var anotherBreath = Advancement.Builder.advancement()
                .parent(encounterFromTheOcean)
                .display(display(
                        "caerula_arbor:nourished_apple_pie",
                        "advancements.another_breath.title",
                        "advancements.another_breath.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("another_breath_0", impossible())
                .rewards(recipeRewards(
                        "caerula_arbor:seal_iron_treaty",
                        "caerula_arbor:seal_gold_treaty",
                        "caerula_arbor:seal_diamond_treaty",
                        "caerula_arbor:seal_netherite_treaty",
                        "caerula_arbor:seal_emerald_treaty",
                        "caerula_arbor:craft_golden_chalise",
                        "caerula_arbor:nourished_apple_pie_craft"
                ))
                .save(saver, modLoc("another_breath"));
        advancements.add(anotherBreath);

        /* start_of_calamity (caerula_arbor:encounter_from_the_ocean) */
        var startOfCalamity = Advancement.Builder.advancement()
                .parent(encounterFromTheOcean)
                .display(display(
                        "caerula_arbor:sea_trail_init",
                        "advancements.start_of_calamity.title",
                        "advancements.start_of_calamity.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("start_of_calamity_0", placedBlock("caerula_arbor:sea_trail_grown"))
                .rewards(recipeRewards(
                        "caerula_arbor:smelt_mor",
                        "caerula_arbor:smoke_mor",
                        "caerula_arbor:campfire_mor",
                        "caerula_arbor:make_caramel_mor",
                        "caerula_arbor:assemble_cake_trail",
                        "caerula_arbor:make_cake_trail",
                        "caerula_arbor:generate_trail",
                        "caerula_arbor:trail_bomb",
                        "caerula_arbor:solided_trail",
                        "caerula_arbor:craft_trail_brick",
                        "caerula_arbor:cut_trail_brick",
                        "caerula_arbor:craft_trail_slab",
                        "caerula_arbor:assemble_trail_slab",
                        "caerula_arbor:craft_trail_stair",
                        "caerula_arbor:cut_trail_stair",
                        "caerula_arbor:craft_trail_plate",
                        "caerula_arbor:craft_trail_button",
                        "caerula_arbor:cut_trail_slab",
                        "caerula_arbor:craft_trail_axe",
                        "caerula_arbor:craft_trail_sword",
                        "caerula_arbor:craft_trailrite",
                        "caerula_arbor:assemble_trailrite",
                        "caerula_arbor:dessemble_trailrite",
                        "caerula_arbor:cur_trail_wall",
                        "caerula_arbor:stripped_log_plank",
                        "caerula_arbor:log_to_wood",
                        "caerula_arbor:strip_log_to_wood",
                        "caerula_arbor:new_tide_core",
                        "caerula_arbor:craft_enderina_core",
                        "caerula_arbor:moist_crystal"
                ))
                .save(saver, modLoc("start_of_calamity"));
        advancements.add(startOfCalamity);

        /* another_start (caerula_arbor:start_of_calamity) */
        var anotherStart = Advancement.Builder.advancement()
                .parent(startOfCalamity)
                .display(display(
                        "caerula_arbor:moist_crystal_item",
                        "advancements.another_start.title",
                        "advancements.another_start.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("another_start_0", placedBlock("caerula_arbor:enderina_core"))
                .save(saver, modLoc("another_start"));
        advancements.add(anotherStart);

        /* tranquil_heights (caerula_arbor:start_of_calamity) */
        var tranquilHeights = Advancement.Builder.advancement()
                .parent(startOfCalamity)
                .display(display(
                        "caerula_arbor:nethersea_soul_sand",
                        "advancements.tranquil_heights.title",
                        "advancements.tranquil_heights.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("tranquil_heights_0", impossible())
                .save(saver, modLoc("tranquil_heights"));
        advancements.add(tranquilHeights);

        /* withered_tranquiliy (caerula_arbor:tranquil_heights) */
        var witheredTranquiliy = Advancement.Builder.advancement()
                .parent(tranquilHeights)
                .display(display(
                        "caerula_arbor:moist_star",
                        "advancements.withered_tranquiliy.title",
                        "advancements.withered_tranquiliy.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                ))
                .addCriterion("withered_tranquiliy_0", inventoryChanged("caerula_arbor:moist_star", 1, 999))
                .rewards(experienceRewards(
                        32,
                        "caerula_arbor:craft_trailrite",
                        "caerula_arbor:assemble_trailrite",
                        "caerula_arbor:dessemble_trailrite",
                        "caerula_arbor:core_made",
                        "caerula_arbor:new_tide_core",
                        "caerula_arbor:cleaner_bot",
                        "caerula_arbor:craft_al_1s",
                        "caerula_arbor:craft_hand_anchor",
                        "caerula_arbor:craft_circular_saw",
                        "caerula_arbor:craft_lancet"
                ))
                .save(saver, modLoc("withered_tranquiliy"));
        advancements.add(witheredTranquiliy);

        /* aurgelmir (caerula_arbor:withered_tranquiliy) */
        var aurgelmir = Advancement.Builder.advancement()
                .parent(witheredTranquiliy)
                .display(display(
                        "caerula_arbor:flamarine_upgrade_template",
                        "advancements.aurgelmir.title",
                        "advancements.aurgelmir.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("aurgelmir_0", inventoryChanged("caerula_arbor:flamarine_upgrade_template", 1, 999))
                .rewards(recipeRewards(
                        "caerula_arbor:craft_trailrite",
                        "caerula_arbor:assemble_trailrite",
                        "caerula_arbor:dessemble_trailrite",
                        "caerula_arbor:smith_trailrite_helm",
                        "caerula_arbor:smith_trailrite_chest",
                        "caerula_arbor:smith_trailrite_leg",
                        "caerula_arbor:smith_trailrite_boots",
                        "caerula_arbor:smith_trailrite_pick",
                        "caerula_arbor:smith_trailrite_hoe",
                        "caerula_arbor:smith_trailrite_shovel",
                        "caerula_arbor:craft_trail_axe",
                        "caerula_arbor:craft_trail_sword"
                ))
                .save(saver, modLoc("aurgelmir"));
        advancements.add(aurgelmir);

        /* ban_relic_notice (caerula_arbor:encounter_from_the_ocean) */
        var banRelicNotice = Advancement.Builder.advancement()
                .parent(encounterFromTheOcean)
                .addCriterion("ban_relic_notice_0", impossible())
                .save(saver, modLoc("ban_relic_notice"));
        advancements.add(banRelicNotice);

        /* extension_of_calamity (caerula_arbor:start_of_calamity) */
        var extensionOfCalamity = Advancement.Builder.advancement()
                .parent(startOfCalamity)
                .display(display(
                        "caerula_arbor:ocean_ovary",
                        "advancements.extension_of_calamity.title",
                        "advancements.extension_of_calamity.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("extension_of_calamity_0", placedBlock("caerula_arbor:ocean_ovary"))
                .addCriterion("extension_of_calamity_1", placedBlock("caerula_arbor:red_ovary"))
                .rewards(recipeRewards(
                        "caerula_arbor:craft_ocarino",
                        "caerula_arbor:fermen_eye",
                        "caerula_arbor:craft_anchor_ingot",
                        "caerula_arbor:smith_anchor",
                        "caerula_arbor:craft_unfinished_beauty"
                ))
                .save(saver, modLoc("extension_of_calamity"));
        advancements.add(extensionOfCalamity);

        /* whirling_whisper (caerula_arbor:extension_of_calamity) */
        var whirlingWhisper = Advancement.Builder.advancement()
                .parent(extensionOfCalamity)
                .display(display(
                        "caerula_arbor:whirl_eye",
                        "advancements.whirling_whisper.title",
                        "advancements.whirling_whisper.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("whirling_whisper_0", inventoryChanged("caerula_arbor:whirl_eye", 1, 999))
                .rewards(recipeRewards(
                        "caerula_arbor:craft_ocarino",
                        "caerula_arbor:craft_nurture_gene"
                ))
                .save(saver, modLoc("whirling_whisper"));
        advancements.add(whirlingWhisper);

        /* musician_we_many (caerula_arbor:whirling_whisper) */
        var musicianWeMany = Advancement.Builder.advancement()
                .parent(whirlingWhisper)
                .display(display(
                        "caerula_arbor:ocarina",
                        "advancements.musician_we_many.title",
                        "advancements.musician_we_many.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("musician_we_many_0", inventoryChanged("caerula_arbor:ocarina", 1, 999))
                .save(saver, modLoc("musician_we_many"));
        advancements.add(musicianWeMany);

        /* boiling_sea (caerula_arbor:musician_we_many) */
        var boilingSea = Advancement.Builder.advancement()
                .parent(musicianWeMany)
                .display(display(
                        "caerula_arbor:mizuki_statue",
                        "advancements.boiling_sea.title",
                        "advancements.boiling_sea.descr",
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        true
                ))
                .addCriterion("boiling_sea_0", entityHurtPlayer("caerula_arbor:izumik", 0, 9999, true))
                .save(saver, modLoc("boiling_sea"));
        advancements.add(boilingSea);

        /* to_we_many (caerula_arbor:encounter_from_the_ocean) */
        var toWeMany = Advancement.Builder.advancement()
                .parent(encounterFromTheOcean)
                .display(display(
                        "caerula_arbor:oil_and_cream",
                        "advancements.to_we_many.title",
                        "advancements.to_we_many.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("to_we_many_0", impossible())
                .save(saver, modLoc("to_we_many"));
        advancements.add(toWeMany);

        /* but_i_refuse (caerula_arbor:to_we_many) */
        var butIRefuse = Advancement.Builder.advancement()
                .parent(toWeMany)
                .display(display(
                        "caerula_arbor:tulip_medcine",
                        "advancements.but_i_refuse.title",
                        "advancements.but_i_refuse.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("but_i_refuse_0", impossible())
                .save(saver, modLoc("but_i_refuse"));
        advancements.add(butIRefuse);

        /* sinking_love (caerula_arbor:withered_tranquiliy) */
        var sinkingLove = Advancement.Builder.advancement()
                .parent(witheredTranquiliy)
                .display(display(
                        "caerula_arbor:trailrite",
                        "advancements.sinking_love.title",
                        "advancements.sinking_love.descr",
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                ))
                .addCriterion("sinking_love_0", inventoryChanged("caerula_arbor:trailrite", 1, 999))
                .rewards(recipeRewards(
                        "caerula_arbor:craft_trailrite",
                        "caerula_arbor:assemble_trailrite",
                        "caerula_arbor:dessemble_trailrite",
                        "caerula_arbor:smith_trailrite_helm",
                        "caerula_arbor:smith_trailrite_chest",
                        "caerula_arbor:smith_trailrite_leg",
                        "caerula_arbor:smith_trailrite_boots",
                        "caerula_arbor:smith_trailrite_pick",
                        "caerula_arbor:smith_trailrite_hoe",
                        "caerula_arbor:smith_trailrite_shovel",
                        "caerula_arbor:smith_trailrite_bow",
                        "caerula_arbor:dessemble_trail_ingot",
                        "caerula_arbor:assemble_trail_ingot"
                ))
                .save(saver, modLoc("sinking_love"));
        advancements.add(sinkingLove);

        /* combination_of_paradox (caerula_arbor:sinking_love) */
        var combinationOfParadox = Advancement.Builder.advancement()
                .parent(sinkingLove)
                .display(display(
                        "caerula_arbor:trailrite_armor_chestplate",
                        "advancements.combination_of_paradox.title",
                        "advancements.combination_of_paradox.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                ))
                .addCriterion("combination_of_paradox_0", inventoryChanged("caerula_arbor:trailrite_armor_helmet", 1, 999))
                .addCriterion("combination_of_paradox_1", inventoryChanged("caerula_arbor:trailrite_armor_chestplate", 1, 999))
                .addCriterion("combination_of_paradox_2", inventoryChanged("caerula_arbor:trailrite_armor_leggings", 1, 999))
                .addCriterion("combination_of_paradox_3", inventoryChanged("caerula_arbor:trailrite_armor_boots", 1, 999))
                .rewards(experienceRewards(
                        32
                ))
                .save(saver, modLoc("combination_of_paradox"));
        advancements.add(combinationOfParadox);

        /* to_burden_catastrophy (caerula_arbor:encounter_from_the_ocean) */
        var toBurdenCatastrophy = Advancement.Builder.advancement()
                .parent(encounterFromTheOcean)
                .display(display(
                        "caerula_arbor:ocean_chitin",
                        "advancements.to_burden_catastrophy.title",
                        "advancements.to_burden_catastrophy.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("to_burden_catastrophy_0", inventoryChanged("caerula_arbor:ocean_chitin", 1, 999))
                .rewards(recipeRewards(
                        "caerula_arbor:craft_chitinblock",
                        "caerula_arbor:craft_chitin_hel",
                        "caerula_arbor:craft_chitin_chest",
                        "caerula_arbor:craft_chitin_leg",
                        "caerula_arbor:craft_chitin_bts",
                        "caerula_arbor:craft_chitin_pick",
                        "caerula_arbor:craft_chitin_hoe",
                        "caerula_arbor:craft_chitin_axe",
                        "caerula_arbor:craft_chitin_swd",
                        "caerula_arbor:craft_chitin_shovel",
                        "caerula_arbor:craft_complexchitin",
                        "caerula_arbor:smith_compchitin_sword",
                        "caerula_arbor:dessemble_chitinblock",
                        "caerula_arbor:burn_chitin",
                        "caerula_arbor:craft_chitin_shield",
                        "caerula_arbor:craft_complex_chitin_shield",
                        "caerula_arbor:craft_chitin_ingot",
                        "caerula_arbor:chitin_bow"
                ))
                .save(saver, modLoc("to_burden_catastrophy"));
        advancements.add(toBurdenCatastrophy);

        /* im_watching_you (caerula_arbor:to_burden_catastrophy) */
        var imWatchingYou = Advancement.Builder.advancement()
                .parent(toBurdenCatastrophy)
                .display(display(
                        "caerula_arbor:ocean_eye",
                        "advancements.im_watching_you.title",
                        "advancements.im_watching_you.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("im_watching_you_0", inventoryChanged("caerula_arbor:ocean_eye", 1, 999))
                .rewards(recipeRewards(
                        "caerula_arbor:fermen_eye",
                        "caerula_arbor:made_oceanmachine",
                        "caerula_arbor:trail_bomb",
                        "caerula_arbor:craft_copper_bomb"
                ))
                .save(saver, modLoc("im_watching_you"));
        advancements.add(imWatchingYou);

        /* enthusiast_of_chitin (caerula_arbor:im_watching_you) */
        var enthusiastOfChitin = Advancement.Builder.advancement()
                .parent(imWatchingYou)
                .display(display(
                        "caerula_arbor:complex_chitin",
                        "advancements.enthusiast_of_chitin.title",
                        "advancements.enthusiast_of_chitin.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("enthusiast_of_chitin_0", inventoryChanged("caerula_arbor:complex_chitin", 1, 999))
                .rewards(recipeRewards(
                        "caerula_arbor:craft_complexchitin",
                        "caerula_arbor:smith_compchitin_sword",
                        "caerula_arbor:smith_compchit_pick",
                        "caerula_arbor:smith_compchi_axe",
                        "caerula_arbor:smith_compchi_shovel",
                        "caerula_arbor:smith_compchi_hoe",
                        "caerula_arbor:comple_block",
                        "caerula_arbor:dessembe_comple",
                        "caerula_arbor:smith_compc_helm",
                        "caerula_arbor:smith_complexc_chest",
                        "caerula_arbor:smith_complexc_leg",
                        "caerula_arbor:smith_complexc_boot",
                        "caerula_arbor:craft_complex_chitin_shield"
                ))
                .save(saver, modLoc("enthusiast_of_chitin"));
        advancements.add(enthusiastOfChitin);

        /* construction (caerula_arbor:enthusiast_of_chitin) */
        var construction = Advancement.Builder.advancement()
                .parent(enthusiastOfChitin)
                .display(display(
                        "minecraft:white_shulker_box",
                        "advancements.construction.title",
                        "advancements.construction.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("construction_0", impossible())
                .save(saver, modLoc("construction"));
        advancements.add(construction);

        /* treasures (caerula_arbor:encounter_from_the_ocean) */
        var treasures = Advancement.Builder.advancement()
                .parent(encounterFromTheOcean)
                .display(display(
                        "minecraft:chest",
                        "advancements.treasures.title",
                        "advancements.treasures.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("treasures_0", impossible())
                .save(saver, modLoc("treasures"));
        advancements.add(treasures);

        /* costly_treasures (caerula_arbor:treasures) */
        var costlyTreasures = Advancement.Builder.advancement()
                .parent(treasures)
                .display(display(
                        "minecraft:ender_chest",
                        "advancements.costly_treasures.title",
                        "advancements.costly_treasures.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("costly_treasures_0", impossible())
                .save(saver, modLoc("costly_treasures"));
        advancements.add(costlyTreasures);

        /* embrace_the_sea (caerula_arbor:enthusiast_of_chitin) */
        var embraceTheSea = Advancement.Builder.advancement()
                .parent(enthusiastOfChitin)
                .display(display(
                        "caerula_arbor:complexchitin_armor_chestplate",
                        "advancements.embrace_the_sea.title",
                        "advancements.embrace_the_sea.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                ))
                .addCriterion("embrace_the_sea_0", inventoryChanged("caerula_arbor:complexchitin_armor_helmet", 1, 999))
                .addCriterion("embrace_the_sea_1", inventoryChanged("caerula_arbor:complexchitin_armor_chestplate", 1, 999))
                .addCriterion("embrace_the_sea_2", inventoryChanged("caerula_arbor:complexchitin_armor_leggings", 1, 999))
                .addCriterion("embrace_the_sea_3", inventoryChanged("caerula_arbor:complexchitin_armor_boots", 1, 999))
                .save(saver, modLoc("embrace_the_sea"));
        advancements.add(embraceTheSea);

        /* infinite_growth (caerula_arbor:encounter_from_the_ocean) */
        var infiniteGrowth = Advancement.Builder.advancement()
                .parent(encounterFromTheOcean)
                .display(display(
                        "caerula_arbor:broken_ocean_cell",
                        "advancements.infinite_growth.title",
                        "advancements.infinite_growth.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("infinite_growth_0", inventoryChanged("caerula_arbor:cell_cluster", 1, 999))
                .rewards(recipeRewards(
                        "caerula_arbor:craft_base_egg",
                        "caerula_arbor:craft_trageted_base",
                        "caerula_arbor:cluster_cells",
                        "caerula_arbor:cluster_brocell",
                        "caerula_arbor:decluster_cells",
                        "caerula_arbor:decluster_brocell",
                        "caerula_arbor:cook_cell",
                        "caerula_arbor:cook_celll",
                        "caerula_arbor:smoke_cell",
                        "caerula_arbor:smoke_celll",
                        "caerula_arbor:campfire_cell",
                        "caerula_arbor:campfire_celll",
                        "caerula_arbor:fix_cell",
                        "caerula_arbor:fix_cell_block",
                        "caerula_arbor:change_cell",
                        "caerula_arbor:dictationless_chapter"
                ))
                .save(saver, modLoc("infinite_growth"));
        advancements.add(infiniteGrowth);

        /* they_shall_welcome (caerula_arbor:infinite_growth) */
        var theyShallWelcome = Advancement.Builder.advancement()
                .parent(infiniteGrowth)
                .display(display(
                        "caerula_arbor:transform_cell",
                        "advancements.they_shall_welcome.title",
                        "advancements.they_shall_welcome.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("they_shall_welcome_0", impossible())
                .rewards(recipeRewards(
                        "caerula_arbor:cluster_cells",
                        "caerula_arbor:cluster_brocell",
                        "caerula_arbor:decluster_cells",
                        "caerula_arbor:decluster_brocell",
                        "caerula_arbor:cook_cell",
                        "caerula_arbor:cook_celll",
                        "caerula_arbor:smoke_cell",
                        "caerula_arbor:smoke_celll",
                        "caerula_arbor:campfire_cell",
                        "caerula_arbor:campfire_celll",
                        "caerula_arbor:fix_cell",
                        "caerula_arbor:fix_cell_block",
                        "caerula_arbor:change_cell",
                        "caerula_arbor:craft_base_egg",
                        "caerula_arbor:make_reaper_egg",
                        "caerula_arbor:fry_egg",
                        "caerula_arbor:smoke_egg",
                        "caerula_arbor:camp_egg",
                        "caerula_arbor:craft_leath_legg",
                        "caerula_arbor:cookfakeegg",
                        "caerula_arbor:smokefakegg",
                        "caerula_arbor:campfire_fakeegg",
                        "caerula_arbor:tulip_medic",
                        "caerula_arbor:craft_oil",
                        "caerula_arbor:craft_immunosupp"
                ))
                .save(saver, modLoc("they_shall_welcome"));
        advancements.add(theyShallWelcome);

        /* they_shall_pay (caerula_arbor:they_shall_welcome) */
        var theyShallPay = Advancement.Builder.advancement()
                .parent(theyShallWelcome)
                .display(display(
                        "caerula_arbor:transform_cell",
                        "advancements.they_shall_pay.title",
                        "advancements.they_shall_pay.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                ))
                .addCriterion("they_shall_pay_0", impossible())
                .rewards(lootRewards(
                        32,
                        loot(
                                "caerula_arbor:entities/chest_fish"
                        ),
                        "caerula_arbor:craft_capsule",
                        "caerula_arbor:craft_catalyst"
                ))
                .save(saver, modLoc("they_shall_pay"));
        advancements.add(theyShallPay);

        /* start_player_evo (caerula_arbor:they_shall_pay) */
        var startPlayerEvo = Advancement.Builder.advancement()
                .parent(theyShallPay)
                .display(display(
                        "caerula_arbor:gene_sample_normal",
                        "advancements.start_player_evo.title",
                        "advancements.start_player_evo.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("start_player_evo_0", inventoryChanged("caerula_arbor:nurture_gene_set", 1, 999))
                .save(saver, modLoc("start_player_evo"));
        advancements.add(startPlayerEvo);

        /* end_player_evo (caerula_arbor:start_player_evo) */
        var endPlayerEvo = Advancement.Builder.advancement()
                .parent(startPlayerEvo)
                .display(display(
                        "caerula_arbor:gene_sample_superb",
                        "advancements.end_player_evo.title",
                        "advancements.end_player_evo.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                ))
                .addCriterion("end_player_evo_0", impossible())
                .rewards(experienceRewards(
                        8
                ))
                .save(saver, modLoc("end_player_evo"));
        advancements.add(endPlayerEvo);

        /* eternal_anger (caerula_arbor:encounter_from_the_ocean) */
        var eternalAnger = Advancement.Builder.advancement()
                .parent(encounterFromTheOcean)
                .display(display(
                        "caerula_arbor:carmen_treaty",
                        "advancements.eternal_anger.title",
                        "advancements.eternal_anger.descr",
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                ))
                .addCriterion("eternal_anger_0", inventoryChanged("caerula_arbor:carmen_treaty", 1, 999))
                .rewards(experienceRewards(
                        8
                ))
                .save(saver, modLoc("eternal_anger"));
        advancements.add(eternalAnger);

        /* flamarine_dedication (caerula_arbor:sinking_love) */
        var flamarineDedication = Advancement.Builder.advancement()
                .parent(sinkingLove)
                .display(display(
                        "caerula_arbor:trailrite_hoe",
                        "advancements.flamarine_dedication.title",
                        "advancements.flamarine_dedication.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                ))
                .addCriterion("flamarine_dedication_0", inventoryChanged("caerula_arbor:trailrite_hoe", 1, 999))
                .save(saver, modLoc("flamarine_dedication"));
        advancements.add(flamarineDedication);

        /* forced_welcome (caerula_arbor:infinite_growth) */
        var forcedWelcome = Advancement.Builder.advancement()
                .parent(infiniteGrowth)
                .display(display(
                        "caerula_arbor:mutagenisis_capsule",
                        "advancements.forced_welcome.title",
                        "advancements.forced_welcome.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("forced_welcome_0", inventoryChanged("caerula_arbor:mutagenisis_capsule", 1, 999))
                .save(saver, modLoc("forced_welcome"));
        advancements.add(forcedWelcome);

        /* gain_hunter_gene (caerula_arbor:encounter_from_the_ocean) */
        var gainHunterGene = Advancement.Builder.advancement()
                .parent(encounterFromTheOcean)
                .display(display(
                        "caerula_arbor:hunter_gene",
                        "advancements.gain_hunter_gene.title",
                        "advancements.gain_hunter_gene.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("gain_hunter_gene_0", inventoryChanged("caerula_arbor:hunter_gene", 1, 999))
                .rewards(recipeRewards(
                        "caerula_arbor:craft_skadi_kit",
                        "caerula_arbor:craft_ulpians_kit",
                        "caerula_arbor:craft_gladiia_kit",
                        "caerula_arbor:craft_trageted_base",
                        "caerula_arbor:craft_base_kit",
                        "caerula_arbor:craft_lancet",
                        "caerula_arbor:smith_anchor",
                        "caerula_arbor:smith_gladiia_weapon",
                        "caerula_arbor:craft_unfinished_beauty",
                        "caerula_arbor:craft_aegir_sword",
                        "caerula_arbor:smith_skadi_sword",
                        "caerula_arbor:craft_moist_bag"
                ))
                .save(saver, modLoc("gain_hunter_gene"));
        advancements.add(gainHunterGene);

        /* sparkling_shell (caerula_arbor:encounter_from_the_ocean) */
        var sparklingShell = Advancement.Builder.advancement()
                .parent(encounterFromTheOcean)
                .display(display(
                        "caerula_arbor:ocean_cutin",
                        "advancements.sparkling_shell.title",
                        "advancements.sparkling_shell.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("sparkling_shell_0", inventoryChanged("caerula_arbor:ocean_cutin", 1, 999))
                .rewards(recipeRewards(
                        "caerula_arbor:item_to_block_rec",
                        "caerula_arbor:block_to_item_rec",
                        "caerula_arbor:caerula_rec",
                        "caerula_arbor:craft_circular_saw",
                        "caerula_arbor:craft_hand_anchor",
                        "caerula_arbor:craft_pocket_doll",
                        "caerula_arbor:craft_oceanglass_cup",
                        "caerula_arbor:craft_deep_seagrass_juice",
                        "caerula_arbor:craft_caramel_seagrs_juice",
                        "caerula_arbor:craft_apple_juice",
                        "caerula_arbor:craft_nethersea_coffee",
                        "caerula_arbor:craft_nethersea_stimutant",
                        "caerula_arbor:craft_fluore_juice"
                ))
                .save(saver, modLoc("sparkling_shell"));
        advancements.add(sparklingShell);

        /* to_witness_the_tide (caerula_arbor:sparkling_shell) */
        var toWitnessTheTide = Advancement.Builder.advancement()
                .parent(sparklingShell)
                .display(display(
                        "caerula_arbor:caerula_recorder",
                        "advancements.to_witness_the_tide.title",
                        "advancements.to_witness_the_tide.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("to_witness_the_tide_0", inventoryChanged("caerula_arbor:caerula_recorder", 1, 999))
                .rewards(lootRewards(
                        0,
                        loot(
                                "caerula_arbor:gameplay/table_of_hands"
                        ),
                        "caerula_arbor:craft_crystal_sword",
                        "caerula_arbor:craft_cryst_pick",
                        "caerula_arbor:craft_cryst_hoe",
                        "caerula_arbor:craft_cryst_shovel",
                        "caerula_arbor:craft_cryst_axe",
                        "caerula_arbor:blocked_oceancry",
                        "caerula_arbor:dessemble_blockcry",
                        "caerula_arbor:craft_tide_observe",
                        "caerula_arbor:craft_anchor_ingot",
                        "caerula_arbor:smith_anchor",
                        "caerula_arbor:craft_hand_anchor",
                        "caerula_arbor:craft_tidelinked_shield"
                ))
                .save(saver, modLoc("to_witness_the_tide"));
        advancements.add(toWitnessTheTide);

        /* to_experience_evolution (caerula_arbor:to_witness_the_tide) */
        var toExperienceEvolution = Advancement.Builder.advancement()
                .parent(toWitnessTheTide)
                .display(display(
                        "caerula_arbor:sample_subsisting",
                        "advancements.to_experience_evolution.title",
                        "advancements.to_experience_evolution.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("to_experience_evolution_0", impossible())
                .save(saver, modLoc("to_experience_evolution"));
        advancements.add(toExperienceEvolution);

        /* to_terminate_evolution (caerula_arbor:to_experience_evolution) */
        var toTerminateEvolution = Advancement.Builder.advancement()
                .parent(toExperienceEvolution)
                .display(display(
                        "caerula_arbor:sample_subsisting",
                        "advancements.to_terminate_evolution.title",
                        "advancements.to_terminate_evolution.descr",
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                ))
                .addCriterion("to_terminate_evolution_0", impossible())
                .rewards(lootRewards(
                        32,
                        loot(
                                "caerula_arbor:gameplay/terminal_relics"
                        ),
                        "caerula_arbor:smith_trident",
                        "caerula_arbor:cream_smithed"
                ))
                .save(saver, modLoc("to_terminate_evolution"));
        advancements.add(toTerminateEvolution);

        /* she_coming (caerula_arbor:to_terminate_evolution) */
        var sheComing = Advancement.Builder.advancement()
                .parent(toTerminateEvolution)
                .display(display(
                        "caerula_arbor:relic_cursed_research",
                        "advancements.she_coming.title",
                        "advancements.she_coming.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("she_coming_0", impossible())
                .rewards(recipeRewards(
                        "caerula_arbor:craft_spear",
                        "caerula_arbor:craft_meat_can",
                        "caerula_arbor:craft_can",
                        "caerula_arbor:craft_bowl_seagrass",
                        "caerula_arbor:craft_orangestorm",
                        "caerula_arbor:make_paperbag",
                        "caerula_arbor:extract_caffeine",
                        "caerula_arbor:craft_coffee_candy",
                        "caerula_arbor:make_berry",
                        "caerula_arbor:craft_fluro_berry",
                        "caerula_arbor:craft_radiant_berry",
                        "caerula_arbor:craft_rainbow_candy",
                        "caerula_arbor:make_bone",
                        "caerula_arbor:cook_oceanfibre",
                        "caerula_arbor:smoke_oceanfibre",
                        "caerula_arbor:campfire_oceanfibre",
                        "caerula_arbor:make_ocean_glass",
                        "caerula_arbor:make_glasspane",
                        "caerula_arbor:craft_chitinblock",
                        "caerula_arbor:smelt_mor",
                        "caerula_arbor:smoke_mor",
                        "caerula_arbor:campfire_mor",
                        "caerula_arbor:craft_crystal_sword",
                        "caerula_arbor:craft_chitin_hel",
                        "caerula_arbor:craft_chitin_chest",
                        "caerula_arbor:craft_chitin_leg",
                        "caerula_arbor:craft_chitin_bts",
                        "caerula_arbor:craft_cutinstk",
                        "caerula_arbor:craft_chitin_pick",
                        "caerula_arbor:craft_chitin_hoe",
                        "caerula_arbor:craft_chitin_axe",
                        "caerula_arbor:craft_chitin_swd",
                        "caerula_arbor:craft_chitin_shovel",
                        "caerula_arbor:craft_cryst_pick",
                        "caerula_arbor:craft_cryst_hoe",
                        "caerula_arbor:craft_cryst_shovel",
                        "caerula_arbor:craft_cryst_axe",
                        "caerula_arbor:make_obisidian",
                        "caerula_arbor:craft_complexchitin",
                        "caerula_arbor:smith_compchitin_sword",
                        "caerula_arbor:copy_templates",
                        "caerula_arbor:craft_noodle",
                        "caerula_arbor:smith_compchit_pick",
                        "caerula_arbor:smith_compchi_axe",
                        "caerula_arbor:smith_compchi_shovel",
                        "caerula_arbor:smith_compchi_hoe",
                        "caerula_arbor:pholem_bow_made",
                        "caerula_arbor:smith_trident",
                        "caerula_arbor:assemble_cake_trail",
                        "caerula_arbor:make_cake_trail",
                        "caerula_arbor:make_caramel_mor",
                        "caerula_arbor:make_cake_caramel",
                        "caerula_arbor:assemble_caramelcake",
                        "caerula_arbor:brew_ins_sanity",
                        "caerula_arbor:brew_sanity_cure",
                        "caerula_arbor:upgrade_inst_sanity",
                        "caerula_arbor:upgrade_sanity_cure",
                        "caerula_arbor:fermen_eye",
                        "caerula_arbor:generate_trail",
                        "caerula_arbor:made_oceanmachine",
                        "caerula_arbor:trail_bomb",
                        "caerula_arbor:blocked_oceancry",
                        "caerula_arbor:dessemble_blockcry",
                        "caerula_arbor:dessemble_chitinblock",
                        "caerula_arbor:comple_block",
                        "caerula_arbor:dessembe_comple",
                        "caerula_arbor:craft_iris",
                        "caerula_arbor:cream_smithed",
                        "caerula_arbor:craft_cream",
                        "caerula_arbor:cream_wood",
                        "caerula_arbor:cream_stone",
                        "caerula_arbor:cream_iron",
                        "caerula_arbor:cream_diamond",
                        "caerula_arbor:cream_netherite",
                        "caerula_arbor:cream_gold",
                        "caerula_arbor:cluster_cells",
                        "caerula_arbor:cluster_brocell",
                        "caerula_arbor:decluster_cells",
                        "caerula_arbor:decluster_brocell",
                        "caerula_arbor:cook_cell",
                        "caerula_arbor:cook_celll",
                        "caerula_arbor:smoke_cell",
                        "caerula_arbor:smoke_celll",
                        "caerula_arbor:campfire_cell",
                        "caerula_arbor:campfire_celll",
                        "caerula_arbor:item_to_block_rec",
                        "caerula_arbor:block_to_item_rec",
                        "caerula_arbor:caerula_rec",
                        "caerula_arbor:solided_trail",
                        "caerula_arbor:craft_trail_brick",
                        "caerula_arbor:cut_trail_brick",
                        "caerula_arbor:craft_trail_slab",
                        "caerula_arbor:assemble_trail_slab",
                        "caerula_arbor:craft_trail_stair",
                        "caerula_arbor:cut_trail_stair",
                        "caerula_arbor:craft_trail_plate",
                        "caerula_arbor:craft_trail_button",
                        "caerula_arbor:cut_trail_slab",
                        "caerula_arbor:craft_tile",
                        "caerula_arbor:cut_tile",
                        "caerula_arbor:craft_tide_observe",
                        "caerula_arbor:craft_cutin_pane",
                        "caerula_arbor:craft_ocean_arrow",
                        "caerula_arbor:smash_bone",
                        "caerula_arbor:make_reaper_egg",
                        "caerula_arbor:craft_base_egg",
                        "caerula_arbor:fix_cell",
                        "caerula_arbor:fix_cell_block",
                        "caerula_arbor:change_cell",
                        "caerula_arbor:craft_extractor",
                        "caerula_arbor:craft_ocarino",
                        "caerula_arbor:fry_egg",
                        "caerula_arbor:smoke_egg",
                        "caerula_arbor:camp_egg",
                        "caerula_arbor:cook_peduncle",
                        "caerula_arbor:smoke_peduncle",
                        "caerula_arbor:campfire_peduncle",
                        "caerula_arbor:craft_leath_hel",
                        "caerula_arbor:craft_leath_chets",
                        "caerula_arbor:craft_leath_legg",
                        "caerula_arbor:craft_leath_boots",
                        "caerula_arbor:brew_inv_sanity",
                        "caerula_arbor:burn_shard",
                        "caerula_arbor:craft_trail_axe",
                        "caerula_arbor:craft_trail_sword",
                        "caerula_arbor:craft_trailrite",
                        "caerula_arbor:assemble_trailrite",
                        "caerula_arbor:dessemble_trailrite",
                        "caerula_arbor:craft_trial_wall",
                        "caerula_arbor:cur_trail_wall",
                        "caerula_arbor:cookfakeegg",
                        "caerula_arbor:smokefakegg",
                        "caerula_arbor:campfire_fakeegg",
                        "caerula_arbor:cook_fish",
                        "caerula_arbor:smoke_fish",
                        "caerula_arbor:campfire_fish",
                        "caerula_arbor:cook_claw",
                        "caerula_arbor:smoke_claw",
                        "caerula_arbor:campfire_claw",
                        "caerula_arbor:make_soup",
                        "caerula_arbor:sdst_slab",
                        "caerula_arbor:sdst_stair",
                        "caerula_arbor:sdst_wall",
                        "caerula_arbor:sdst_smth_slab",
                        "caerula_arbor:sdst_smth_stair",
                        "caerula_arbor:sdst_smth_wall",
                        "caerula_arbor:stone_cut_smooth",
                        "caerula_arbor:stonec_cut_slab",
                        "caerula_arbor:stone_cut_stair",
                        "caerula_arbor:stone_cut_smthstair",
                        "caerula_arbor:stone_cut_smthslab",
                        "caerula_arbor:stone_chisel",
                        "caerula_arbor:stone_cut_wall",
                        "caerula_arbor:stone_cur_smthwall",
                        "caerula_arbor:stone_cut_chwall",
                        "caerula_arbor:het_block",
                        "caerula_arbor:dessemble_het_block",
                        "caerula_arbor:isharmla_core",
                        "caerula_arbor:isharlma_top",
                        "caerula_arbor:isharmla_bottom",
                        "caerula_arbor:plank_made",
                        "caerula_arbor:plank_slab",
                        "caerula_arbor:plank_stair",
                        "caerula_arbor:plank_fence",
                        "caerula_arbor:plank_fencegate",
                        "caerula_arbor:button_made",
                        "caerula_arbor:pressure_plate_made",
                        "caerula_arbor:stripped_log_plank",
                        "caerula_arbor:make_door",
                        "caerula_arbor:craft_mop",
                        "caerula_arbor:sdst_brick",
                        "caerula_arbor:sdst_smooth_made",
                        "caerula_arbor:sdst_chiseled_made",
                        "caerula_arbor:made_redstonium",
                        "caerula_arbor:dessemble_redstonium",
                        "caerula_arbor:core_made",
                        "caerula_arbor:craft_lantern",
                        "caerula_arbor:burn_chitin",
                        "caerula_arbor:smith_compc_helm",
                        "caerula_arbor:smith_complexc_chest",
                        "caerula_arbor:smith_complexc_leg",
                        "caerula_arbor:smith_complexc_boot",
                        "caerula_arbor:craft_copper_bomb",
                        "caerula_arbor:craft_gunpowder",
                        "caerula_arbor:assemble_phloem",
                        "caerula_arbor:dessemble_phloem",
                        "caerula_arbor:assemble_fibre",
                        "caerula_arbor:dessemble_fibre",
                        "caerula_arbor:assemble_cooked_fibre",
                        "caerula_arbor:dessemble_cooked_fibre",
                        "caerula_arbor:cook_block_fibre",
                        "caerula_arbor:smoke_block_fibre",
                        "caerula_arbor:campfire_block_fibre",
                        "caerula_arbor:craft_goldenapple",
                        "caerula_arbor:brand_apple",
                        "caerula_arbor:tulip_medic",
                        "caerula_arbor:craft_immunosupp",
                        "caerula_arbor:craft_oil",
                        "caerula_arbor:copy_fate",
                        "caerula_arbor:copyfate_1",
                        "caerula_arbor:burn_pearl",
                        "caerula_arbor:craft_chitin_shield",
                        "caerula_arbor:craft_complex_chitin_shield",
                        "caerula_arbor:craft_circular_saw",
                        "caerula_arbor:make_fruit_jelly",
                        "caerula_arbor:craft_fax",
                        "caerula_arbor:craft_empty_treaty",
                        "caerula_arbor:sea_copper_treaty",
                        "caerula_arbor:seal_iron_treaty",
                        "caerula_arbor:seal_gold_treaty",
                        "caerula_arbor:seal_diamond_treaty",
                        "caerula_arbor:seal_netherite_treaty",
                        "caerula_arbor:seal_emerald_treaty",
                        "caerula_arbor:craft_interphone",
                        "caerula_arbor:craft_cavair",
                        "caerula_arbor:craft_elite_cavair",
                        "caerula_arbor:craft_blue_dye",
                        "caerula_arbor:deglow_ink_sac",
                        "caerula_arbor:glow_ink_sac",
                        "caerula_arbor:craft_kebab",
                        "caerula_arbor:craft_cooked_kebab",
                        "caerula_arbor:cooked_kebab",
                        "caerula_arbor:smoke_kebab",
                        "caerula_arbor:camp_kebab",
                        "caerula_arbor:craft_nethersea_pir",
                        "caerula_arbor:craft_nethersea_stew",
                        "caerula_arbor:craft_unfinished_beauty",
                        "caerula_arbor:bake_echo_shard",
                        "caerula_arbor:craft_capsule",
                        "caerula_arbor:craft_echo_jelly",
                        "caerula_arbor:reverse_emelight",
                        "caerula_arbor:craft_catalyst",
                        "caerula_arbor:craft_anchor_ingot",
                        "caerula_arbor:smith_anchor",
                        "caerula_arbor:craft_hand_anchor",
                        "caerula_arbor:smelt_redstine_ingot",
                        "caerula_arbor:smelt_redstoninium"
                ))
                .save(saver, modLoc("she_coming"));
        advancements.add(sheComing);

        /* hymn_of_land (caerula_arbor:she_coming) */
        var hymnOfLand = Advancement.Builder.advancement()
                .parent(sheComing)
                .display(display(
                        "caerula_arbor:caerula_heart",
                        "advancements.hymn_of_land.title",
                        "advancements.hymn_of_land.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                ))
                .addCriterion("hymn_of_land_0", impossible())
                .rewards(experienceRewards(
                        32
                ))
                .save(saver, modLoc("hymn_of_land"));
        advancements.add(hymnOfLand);

        /* i_scream (caerula_arbor:treasures) */
        var iScream = Advancement.Builder.advancement()
                .parent(treasures)
                .display(display(
                        "minecraft:trapped_chest",
                        "advancements.i_scream.title",
                        "advancements.i_scream.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("i_scream_0", impossible())
                .save(saver, modLoc("i_scream"));
        advancements.add(iScream);

        /* its_deal (caerula_arbor:to_witness_the_tide) */
        var itsDeal = Advancement.Builder.advancement()
                .parent(toWitnessTheTide)
                .display(display(
                        "caerula_arbor:block_recorder",
                        "advancements.its_deal.title",
                        "advancements.its_deal.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("its_deal_0", placedBlock("caerula_arbor:block_recorder"))
                .rewards(recipeRewards(
                        "caerula_arbor:made_redstonium",
                        "caerula_arbor:dessemble_redstonium"
                ))
                .save(saver, modLoc("its_deal"));
        advancements.add(itsDeal);

        /* kill_brute (caerula_arbor:forced_welcome) */
        var killBrute = Advancement.Builder.advancement()
                .parent(forcedWelcome)
                .display(display(
                        "caerula_arbor:crimson_treaty",
                        "advancements.kill_brute.title",
                        "advancements.kill_brute.descr",
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        true
                ))
                .addCriterion("kill_brute_0", impossible())
                .rewards(experienceRewards(
                        4
                ))
                .save(saver, modLoc("kill_brute"));
        advancements.add(killBrute);

        /* kill_knight (caerula_arbor:encounter_from_the_ocean) */
        var killKnight = Advancement.Builder.advancement()
                .parent(encounterFromTheOcean)
                .display(display(
                        "caerula_arbor:knight_corpse",
                        "advancements.kill_knight.title",
                        "advancements.kill_knight.descr",
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                ))
                .addCriterion("kill_knight_0", inventoryChanged("caerula_arbor:knight_corpse", 1, 999))
                .rewards(recipeRewards(
                        "caerula_arbor:smith_long_knight_sword",
                        "caerula_arbor:craft_rocinante_injector",
                        "caerula_arbor:craft_tide_hunt_template"
                ))
                .save(saver, modLoc("kill_knight"));
        advancements.add(killKnight);

        /* kill_knight_and_horse (caerula_arbor:kill_knight) */
        var killKnightAndHorse = Advancement.Builder.advancement()
                .parent(killKnight)
                .display(display(
                        "caerula_arbor:tide_hunet_template",
                        "advancements.kill_knight_and_horse.title",
                        "advancements.kill_knight_and_horse.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                ))
                .addCriterion("kill_knight_and_horse_0", impossible())
                .rewards(lootRewards(
                        32,
                        loot(
                                "caerula_arbor:gameplay/mere_geen_sample"
                        )
                ))
                .save(saver, modLoc("kill_knight_and_horse"));
        advancements.add(killKnightAndHorse);

        /* little_by_little (caerula_arbor:enthusiast_of_chitin) */
        var littleByLittle = Advancement.Builder.advancement()
                .parent(enthusiastOfChitin)
                .display(display(
                        "caerula_arbor:ocean_extractor",
                        "advancements.little_by_little.title",
                        "advancements.little_by_little.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        true
                ))
                .addCriterion("little_by_little_0", impossible())
                .rewards(recipeRewards(
                        "caerula_arbor:make_reaper_egg",
                        "caerula_arbor:craft_base_egg",
                        "caerula_arbor:fry_egg",
                        "caerula_arbor:smoke_egg",
                        "caerula_arbor:camp_egg",
                        "caerula_arbor:craft_leath_legg",
                        "caerula_arbor:cookfakeegg",
                        "caerula_arbor:smokefakegg",
                        "caerula_arbor:campfire_fakeegg"
                ))
                .save(saver, modLoc("little_by_little"));
        advancements.add(littleByLittle);

        /* marine_dedication (caerula_arbor:enthusiast_of_chitin) */
        var marineDedication = Advancement.Builder.advancement()
                .parent(enthusiastOfChitin)
                .display(display(
                        "caerula_arbor:complex_chitin_hoe",
                        "advancements.marine_dedication.title",
                        "advancements.marine_dedication.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        true
                ))
                .addCriterion("marine_dedication_0", inventoryChanged("caerula_arbor:complex_chitin_hoe", 1, 999))
                .rewards(experienceRewards(
                        32
                ))
                .save(saver, modLoc("marine_dedication"));
        advancements.add(marineDedication);

        /* operation_deepness (caerula_arbor:encounter_from_the_ocean) */
        var operationDeepness = Advancement.Builder.advancement()
                .parent(encounterFromTheOcean)
                .display(display(
                        "caerula_arbor:crisis_table",
                        "advancements.operation_deepness.title",
                        "advancements.operation_deepness.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("operation_deepness_0", impossible())
                .rewards(lootRewards(
                        0,
                        loot(
                                "caerula_arbor:gameplay/trigger_crisis_table"
                        )
                ))
                .save(saver, modLoc("operation_deepness"));
        advancements.add(operationDeepness);

        /* pave_the_way (caerula_arbor:musician_we_many) */
        var paveTheWay = Advancement.Builder.advancement()
                .parent(musicianWeMany)
                .display(display(
                        "caerula_arbor:path_inaugurator",
                        "advancements.pave_the_way.title",
                        "advancements.pave_the_way.descr",
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                ))
                .addCriterion("pave_the_way_0", inventoryChanged("caerula_arbor:path_inaugurator", 1, 9999))
                .save(saver, modLoc("pave_the_way"));
        advancements.add(paveTheWay);

        /* speechless_break (caerula_arbor:musician_we_many) */
        var speechlessBreak = Advancement.Builder.advancement()
                .parent(musicianWeMany)
                .display(display(
                        "caerula_arbor:highmore_spawnblock",
                        "advancements.speechless_break.title",
                        "advancements.speechless_break.descr",
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        true
                ))
                .addCriterion("speechless_break_0", entityHurtPlayer("caerula_arbor:highmore", 0, 9999, true))
                .save(saver, modLoc("speechless_break"));
        advancements.add(speechlessBreak);

        /* precious_days (caerula_arbor:speechless_break) */
        var preciousDays = Advancement.Builder.advancement()
                .parent(speechlessBreak)
                .display(display(
                        "caerula_arbor:highmore_scythe",
                        "advancements.precious_days.title",
                        "advancements.precious_days.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        true
                ))
                .addCriterion("precious_days_0", impossible())
                .rewards(lootRewards(
                        16,
                        loot(
                                "caerula_arbor:gameplay/mere_geen_sample"
                        )
                ))
                .save(saver, modLoc("precious_days"));
        advancements.add(preciousDays);

        /* unlock_calamity (caerula_arbor:whirling_whisper) */
        var unlockCalamity = Advancement.Builder.advancement()
                .parent(whirlingWhisper)
                .display(display(
                        "caerula_arbor:record_undertides",
                        "advancements.unlock_calamity.title",
                        "advancements.unlock_calamity.descr",
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                ))
                .addCriterion("unlock_calamity_0", impossible())
                .save(saver, modLoc("unlock_calamity"));
        advancements.add(unlockCalamity);

        /* we_many_orienting (caerula_arbor:unlock_calamity) */
        var weManyOrienting = Advancement.Builder.advancement()
                .parent(unlockCalamity)
                .display(display(
                        "caerula_arbor:record_isharmla",
                        "advancements.we_many_orienting.title",
                        "advancements.we_many_orienting.descr",
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                ))
                .addCriterion("we_many_orienting_0", impossible())
                .save(saver, modLoc("we_many_orienting"));
        advancements.add(weManyOrienting);

        /* price_of_peace (caerula_arbor:we_many_orienting) */
        var priceOfPeace = Advancement.Builder.advancement()
                .parent(weManyOrienting)
                .display(display(
                        "caerula_arbor:leviathan_animus",
                        "advancements.price_of_peace.title",
                        "advancements.price_of_peace.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                ))
                .addCriterion("price_of_peace_0", inventoryChanged("caerula_arbor:leviathan_animus", 1, 999))
                .rewards(lootRewards(
                        0,
                        loot(
                                "caerula_arbor:gameplay/mere_geen_sample"
                        ),
                        "caerula_arbor:reverse_caerula_heart",
                        "caerula_arbor:brew_perc_regene"
                ))
                .save(saver, modLoc("price_of_peace"));
        advancements.add(priceOfPeace);

        /* shining_pieces (caerula_arbor:encounter_from_the_ocean) */
        var shiningPieces = Advancement.Builder.advancement()
                .parent(encounterFromTheOcean)
                .display(display(
                        "caerula_arbor:heteropic_piece",
                        "advancements.shining_pieces.title",
                        "advancements.shining_pieces.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("shining_pieces_0", inventoryChanged("caerula_arbor:heteropic_piece", 1, 999))
                .rewards(recipeRewards(
                        "caerula_arbor:isharmla_core",
                        "caerula_arbor:isharlma_top",
                        "caerula_arbor:isharmla_bottom",
                        "caerula_arbor:craft_lantern",
                        "caerula_arbor:craft_book_shelf",
                        "caerula_arbor:vraft_breath_tide"
                ))
                .save(saver, modLoc("shining_pieces"));
        advancements.add(shiningPieces);

        /* silent_interruption (caerula_arbor:musician_we_many) */
        var silentInterruption = Advancement.Builder.advancement()
                .parent(musicianWeMany)
                .display(display(
                        "caerula_arbor:dictation_chapter",
                        "advancements.silent_interruption.title",
                        "advancements.silent_interruption.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                ))
                .addCriterion("silent_interruption_0", impossible())
                .rewards(recipeRewards(
                        "caerula_arbor:craft_lanc_xiao"
                ))
                .save(saver, modLoc("silent_interruption"));
        advancements.add(silentInterruption);

        /* stella_caerula (caerula_arbor:boiling_sea) */
        var stellaCaerula = Advancement.Builder.advancement()
                .parent(boilingSea)
                .display(display(
                        "caerula_arbor:mizuki_determination",
                        "advancements.stella_caerula.title",
                        "advancements.stella_caerula.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        true
                ))
                .addCriterion("stella_caerula_0", impossible())
                .rewards(lootRewards(
                        256,
                        loot(
                                "caerula_arbor:gameplay/mere_geen_sample"
                        )
                ))
                .save(saver, modLoc("stella_caerula"));
        advancements.add(stellaCaerula);

        /* surging_waves_notice (caerula_arbor:encounter_from_the_ocean) */
        var surgingWavesNotice = Advancement.Builder.advancement()
                .parent(encounterFromTheOcean)
                .addCriterion("surging_waves_notice_0", impossible())
                .save(saver, modLoc("surging_waves_notice"));
        advancements.add(surgingWavesNotice);

        /* take_her_eye (caerula_arbor:forced_welcome) */
        var takeHerEye = Advancement.Builder.advancement()
                .parent(forcedWelcome)
                .display(display(
                        "caerula_arbor:moist_echo_shard",
                        "advancements.take_her_eye.title",
                        "advancements.take_her_eye.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                ))
                .addCriterion("take_her_eye_0", inventoryChanged("caerula_arbor:moist_echo_shard", 1, 999))
                .rewards(experienceRewards(
                        32
                ))
                .save(saver, modLoc("take_her_eye"));
        advancements.add(takeHerEye);

        /* terror_of_knowing (caerula_arbor:encounter_from_the_ocean) */
        var terrorOfKnowing = Advancement.Builder.advancement()
                .parent(encounterFromTheOcean)
                .display(display(
                        "caerula_arbor:adv_item",
                        "advancements.terror_of_knowing.title",
                        "advancements.terror_of_knowing.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("terror_of_knowing_0", impossible())
                .rewards(recipeRewards(
                        "caerula_arbor:brew_ins_sanity",
                        "caerula_arbor:brew_sanity_cure",
                        "caerula_arbor:upgrade_inst_sanity",
                        "caerula_arbor:upgrade_sanity_cure",
                        "caerula_arbor:brew_inv_sanity",
                        "caerula_arbor:craft_eme_aid_build",
                        "caerula_arbor:craft_tidelinked_shield",
                        "caerula_arbor:ceaft_tidelinked_wand",
                        "caerula_arbor:craft_golden_chalise",
                        "caerula_arbor:craft_sal_eme_build"
                ))
                .save(saver, modLoc("terror_of_knowing"));
        advancements.add(terrorOfKnowing);

        /* terror_of_collapsing (caerula_arbor:terror_of_knowing) */
        var terrorOfCollapsing = Advancement.Builder.advancement()
                .parent(terrorOfKnowing)
                .display(display(
                        "caerula_arbor:adv_item",
                        "advancements.terror_of_collapsing.title",
                        "advancements.terror_of_collapsing.descr",
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                ))
                .addCriterion("terror_of_collapsing_0", impossible())
                .rewards(experienceRewards(
                        8,
                        "caerula_arbor:craft_goldenapple",
                        "caerula_arbor:brand_apple"
                ))
                .save(saver, modLoc("terror_of_collapsing"));
        advancements.add(terrorOfCollapsing);

        /* tidelinked_life (caerula_arbor:musician_we_many) */
        var tidelinkedLife = Advancement.Builder.advancement()
                .parent(musicianWeMany)
                .display(display(
                        "caerula_arbor:tide_wand",
                        "advancements.tidelinked_life.title",
                        "advancements.tidelinked_life.descr",
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                ))
                .addCriterion("tidelinked_life_0", inventoryChanged("caerula_arbor:tide_wand", 1, 999))
                .addCriterion("tidelinked_life_1", inventoryChanged("caerula_arbor:repeller_shell", 1, 999))
                .save(saver, modLoc("tidelinked_life"));
        advancements.add(tidelinkedLife);

        /* voyage_across_the_end (caerula_arbor:another_start) */
        var voyageAcrossTheEnd = Advancement.Builder.advancement()
                .parent(anotherStart)
                .display(display(
                        "caerula_arbor:moist_dragon_heart",
                        "advancements.voyage_across_the_end.title",
                        "advancements.voyage_across_the_end.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                ))
                .addCriterion("voyage_across_the_end_0", inventoryChanged("caerula_arbor:moist_dragon_heart", 1, 999))
                .rewards(recipeRewards(
                        "caerula_arbor:craft_wand"
                ))
                .save(saver, modLoc("voyage_across_the_end"));
        advancements.add(voyageAcrossTheEnd);

        /* to_listen_dragon_breath (caerula_arbor:voyage_across_the_end) */
        var toListenDragonBreath = Advancement.Builder.advancement()
                .parent(voyageAcrossTheEnd)
                .display(display(
                        "caerula_arbor:dragon_wand",
                        "advancements.to_listen_dragon_breath.title",
                        "advancements.to_listen_dragon_breath.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("to_listen_dragon_breath_0", inventoryChanged("caerula_arbor:dragon_wand", 1, 999))
                .save(saver, modLoc("to_listen_dragon_breath"));
        advancements.add(toListenDragonBreath);

        /* to_observe_evolution (caerula_arbor:to_experience_evolution) */
        var toObserveEvolution = Advancement.Builder.advancement()
                .parent(toExperienceEvolution)
                .display(display(
                        "caerula_arbor:tide_observation",
                        "advancements.to_observe_evolution.title",
                        "advancements.to_observe_evolution.descr",
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .addCriterion("to_observe_evolution_0", placedBlock("caerula_arbor:tide_observation"))
                .rewards(recipeRewards(
                        "caerula_arbor:smith_trident",
                        "caerula_arbor:cream_smithed"
                ))
                .save(saver, modLoc("to_observe_evolution"));
        advancements.add(toObserveEvolution);

        /* to_slain_the_sea (caerula_arbor:gain_hunter_gene) */
        var toSlainTheSea = Advancement.Builder.advancement()
                .parent(gainHunterGene)
                .display(display(
                        "caerula_arbor:base_operation_kit",
                        "advancements.to_slain_the_sea.title",
                        "advancements.to_slain_the_sea.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                ))
                .addCriterion("to_slain_the_sea_0", impossible())
                .rewards(experienceRewards(
                        32
                ))
                .save(saver, modLoc("to_slain_the_sea"));
        advancements.add(toSlainTheSea);

        /* trail_of_degeneration (caerula_arbor:unlock_calamity) */
        var trailOfDegeneration = Advancement.Builder.advancement()
                .parent(unlockCalamity)
                .display(display(
                        "caerula_arbor:incandescent_anima",
                        "advancements.trail_of_degeneration.title",
                        "advancements.trail_of_degeneration.descr",
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                ))
                .addCriterion("trail_of_degeneration_0", inventoryChanged("caerula_arbor:incandescent_anima", 1, 999))
                .rewards(lootRewards(
                        8,
                        loot(
                                "caerula_arbor:gameplay/mere_geen_sample"
                        )
                ))
                .save(saver, modLoc("trail_of_degeneration"));
        advancements.add(trailOfDegeneration);

        /* fifth_touch */
        var fifthTouch = Advancement.Builder.advancement()
                .display(display(
                        "minecraft:heart_of_the_sea",
                        "advancements.fifth_touch.title",
                        "advancements.fifth_touch.descr",
                        null,
                        AdvancementType.TASK,
                        false,
                        false,
                        true
                ))
                .addCriterion("impossible", impossible())
                .save(saver, modLoc("fifth_touch"));
        advancements.add(fifthTouch);

        /* absurd_of_evolution */
        var absurdOfEvolution = Advancement.Builder.advancement()
                .display(display(
                        "minecraft:heart_of_the_sea",
                        "advancements.absurd_of_evolution.title",
                        "advancements.absurd_of_evolution.descr",
                        null,
                        AdvancementType.TASK,
                        false,
                        false,
                        true
                ))
                .addCriterion("impossible", impossible())
                .save(saver, modLoc("absurd_of_evolution"));
        advancements.add(absurdOfEvolution);

    }

    private record JsonCriterionTriggerInstance(ResourceLocation trigger,
                                                JsonObject conditions) implements CriterionTriggerInstance {

        @Override
        public void validate(@NotNull CriterionValidator validator) {
        }
    }
}