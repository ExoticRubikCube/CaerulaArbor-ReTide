package com.susen36.caerulaarbor.datagen;

import com.susen36.caerulaarbor.CaerulaArborMod;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * 战利品表 datagen 聚合器，集中创建各类 LootTableSubProvider
 */
public final class LootTableProviders {
    private LootTableProviders() {
    }

    /**
     * 创建完整的战利品表 provider
     *
     * @param output datagen 输出位置
     * @return 已注册所有子 provider 的战利品表 provider
     */
    public static net.minecraft.data.loot.LootTableProvider create(PackOutput output) {
        return new net.minecraft.data.loot.LootTableProvider(
                output,
                Set.of(),
                List.of(
                        new SubProviderEntry(BlockTables::new, LootContextParamSets.BLOCK),
                        new SubProviderEntry(ChestTables::new, LootContextParamSets.CHEST),
                        new SubProviderEntry(EntityTables::new, LootContextParamSets.ENTITY),
                        new SubProviderEntry(GameplayTables::new, LootContextParamSets.ALL_PARAMS)
                )
        );
    }

    private static LootTable.Builder table(TableDef definition) {
        LootTable.Builder builder = LootTable.lootTable();
        for (PoolDef pool : definition.pools()) {
            builder.withPool(pool(pool));
        }
        return builder;
    }

    private static LootPool.Builder pool(PoolDef definition) {
        LootPool.Builder builder = LootPool.lootPool().setRolls(number(definition.rolls()));
        if (definition.bonusRolls() != null) {
            builder.setBonusRolls(number(definition.bonusRolls()));
        }
        for (CondDef condition : definition.conditions()) {
            builder.when(condition(condition));
        }
        for (EntryDef entry : definition.entries()) {
            builder.add(entry(entry));
        }
        return builder;
    }

    private static LootPoolSingletonContainer.Builder<?> entry(EntryDef definition) {
        LootPoolSingletonContainer.Builder<?> builder = LootItem.lootTableItem(item(definition.item()));
        if (definition.weight() != 1) {
            builder.setWeight(definition.weight());
        }
        for (CondDef condition : definition.conditions()) {
            builder.when(condition(condition));
        }
        for (FuncDef function : definition.functions()) {
            builder.apply(function(function));
        }
        return builder;
    }

    private static LootItemFunction.Builder function(FuncDef definition) {
        LootItemConditionalFunction.Builder<?> builder = switch (definition.type()) {
            case "set_count" -> SetItemCountFunction.setCount(number(definition.number()));
            case "explosion_decay" -> ApplyExplosionDecay.explosionDecay();
            case "ore_bonus" -> ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE);
            case "enchant_with_levels" -> {
                EnchantWithLevelsFunction.Builder enchantBuilder = EnchantWithLevelsFunction.enchantWithLevels(number(definition.number()));
                if (definition.treasure()) {
                    enchantBuilder.allowTreasure();
                }
                yield enchantBuilder;
            }
            default -> throw new IllegalStateException("Unsupported loot function: " + definition.type());
        };
        for (CondDef condition : definition.conditions()) {
            builder.when(condition(condition));
        }
        return builder;
    }

    private static LootItemCondition.Builder condition(CondDef definition) {
        return switch (definition.type()) {
            case "survives_explosion" -> ExplosionCondition.survivesExplosion();
            case "silk_touch" -> MatchTool.toolMatches(ItemPredicate.Builder.item()
                    .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
            case "inverted" -> InvertedLootItemCondition.invert(condition(Objects.requireNonNull(definition.term())));
            case "block_state_property" -> blockStateProperty(definition);
            default -> throw new IllegalStateException("Unsupported loot condition: " + definition.type());
        };
    }

    @SuppressWarnings({"rawtypes"})
    private static LootItemCondition.Builder blockStateProperty(CondDef definition) {
        Block block = block(Objects.requireNonNull(definition.block()));
        Property property = block.getStateDefinition().getProperty(Objects.requireNonNull(definition.property()));
        if (property == null) {
            throw new IllegalStateException("Unknown block state property " + definition.property() + " for " + definition.block());
        }
        return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, Objects.requireNonNull(definition.value())));
    }

    private static NumberProvider number(NumberDef definition) {
        return definition.uniform() ? UniformGenerator.between(definition.min(), definition.max()) : ConstantValue.exactly(definition.min());
    }

    private static Item item(String id) {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(id)), "Unknown loot item: " + id);
    }

    private static Block block(String id) {
        return Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(id)), "Unknown loot block: " + id);
    }

    private static TableDef table(String path, PoolDef... pools) {
        return new TableDef(path, pools);
    }

    private static PoolDef pool(NumberDef rolls, NumberDef bonusRolls, CondDef[] conditions, EntryDef... entries) {
        return new PoolDef(rolls, bonusRolls, conditions, entries);
    }

    private static EntryDef entry(String item, int weight, CondDef[] conditions, FuncDef... functions) {
        return new EntryDef(item, weight, conditions, functions);
    }

    private static FuncDef setCount(NumberDef count, CondDef... conditions) {
        return new FuncDef("set_count", count, false, conditions);
    }

    private static FuncDef explosionDecay(CondDef... conditions) {
        return new FuncDef("explosion_decay", null, false, conditions);
    }

    private static FuncDef oreBonus(CondDef... conditions) {
        return new FuncDef("ore_bonus", null, false, conditions);
    }

    private static FuncDef enchantWithLevels(NumberDef levels, boolean treasure, CondDef... conditions) {
        return new FuncDef("enchant_with_levels", levels, treasure, conditions);
    }

    private static CondDef[] cond(CondDef... conditions) {
        return conditions;
    }

    private static CondDef survivesExplosion() {
        return new CondDef("survives_explosion", null, null, null, null);
    }

    private static CondDef silkTouch() {
        return new CondDef("silk_touch", null, null, null, null);
    }

    private static CondDef inverted(CondDef term) {
        return new CondDef("inverted", null, null, null, term);
    }

    private static CondDef blockState(String block, String property, String value) {
        return new CondDef("block_state_property", block, property, value, null);
    }

    private static NumberDef number(float value) {
        return new NumberDef(value, value, false);
    }

    private static NumberDef u(float min, float max) {
        return new NumberDef(min, max, true);
    }

    /**
     * 将表定义列表写出为实际 loot table
     */
    private abstract static class GeneratedLootTableProvider implements LootTableSubProvider {
        private final List<TableDef> tables;

        private GeneratedLootTableProvider(List<TableDef> tables) {
            this.tables = tables;
        }

        /**
         * 写出当前子 provider 持有的所有表定义
         *
         * @param output 战利品表输出回调
         */
        @Override
        public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> output) {
            for (TableDef table : tables) {
                output.accept(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, table.path()), table(table));
            }
        }
    }

    /**
     * 生成方块战利品表
     */
    public static final class BlockTables extends GeneratedLootTableProvider {
        /**
         * 创建方块战利品表子 provider
         */
        public BlockTables() {
            super(List.of(
                    babandonedSulpture(),
                    baegirGlassArch(),
                    baegirGlassBar(),
                    baegirGlassDeco(),
                    ballayBlock(),
                    banchorLower(),
                    banchorMedium(),
                    banchorUpper(),
                    bbatbedUpper(),
                    bberryCanumber(),
                    bblockBatbed(),
                    bblockChestfish(),
                    bchestFishFood(),
                    bblockCrownumber(),
                    bblockCrystal(),
                    bblockExtension(),
                    bblockFate(),
                    bblockKettle(),
                    bblockRecorder(),
                    bblockSpear(),
                    bbombCopper(),
                    bbombTrailer(),
                    bcaerulaBookShelf(),
                    bcaramelCake(),
                    bchieseledSaltwindSandWall(),
                    bchiseledSaltwindSandstone(),
                    bchitinBlock(),
                    bcomplexChitinBlock(),
                    bcookedFibreBlock(),
                    bcrackedTrailBrick(),
                    bdeepSeagrass(),
                    bdragonBrand(),
                    bemergencyAidBuildingSalviento(),
                    bemergencyAidBuilding(),
                    bemergencyLight(),
                    benderinaCore(),
                    bendspeakerNest(),
                    bfax(),
                    bfibreBlock(),
                    bgoldenChalise(),
                    bheteropicBlock(),
                    binjector(),
                    bisharmlaBrickChiesled(),
                    bisharmlaBrickGilded(),
                    bisharmlaBrickPillar(),
                    bisharmlaBrick(),
                    bisharmlaRemainumber(),
                    bisharmlaSlab(),
                    bisharmlaStair(),
                    bisharmlaWallChiesled(),
                    bisharmlaWallGilded(),
                    bisharmlaWall(),
                    bkingsArmor(),
                    bnetherseaSampling(),
                    bnetherseaSoulSand(),
                    bnetherseaWood(),
                    boceanCrystalBlock(),
                    boceanFarmland(),
                    boceanGlass(),
                    boceanGlasspane(),
                    boceanOvary(),
                    boperationTable(),
                    bphloemBlock(),
                    breaperEgg(),
                    bredOvary(),
                    bredstoneIris(),
                    bredstoneirisSeeding(),
                    bsaltsand(),
                    bsaltwindBrickSlab(),
                    bsaltwindBrick(),
                    bsaltwindColumnumber(),
                    bsaltwindSandSlab(),
                    bsaltwindSandStair(),
                    bsaltwindSandWall(),
                    bsaltwindSandstone(),
                    bsaltwindSmoothBrick(),
                    bsaltwindSmoothSlab(),
                    bsaltwindSmoothStair(),
                    bsaltwindStair(),
                    bseaPrairieBomb(),
                    bseaTrailBurntSolid(),
                    bseaTrailBurnt(),
                    bseaTrailGrowing(),
                    bseaTrailGrownumber(),
                    bseaTrailInit(),
                    bseaTrailSolid(),
                    bseaTrailStop(),
                    bsmoothSaltwindSandSlab(),
                    bsmoothSaltwindSandStair(),
                    bsmoothSaltwindSandWall(),
                    bsmoothSaltwindSandatone(),
                    bstrippedNetherseaWood(),
                    bstrippedTrailLog(),
                    bthirstCoral(),
                    btideBishopCore(),
                    btidebishopCoreEmpty(),
                    btideObservation(),
                    btrailBrick(),
                    btrailButton(),
                    btrailCake(),
                    btrailDebris(),
                    btrailLeave(),
                    btrailLog(),
                    btrailMushroom(),
                    btrailPlankButton(),
                    btrailPlankDoor(),
                    btrailPlankFencedoor(),
                    btrailPlankPressurePlate(),
                    btrailPlankSlab(),
                    btrailPlankStair(),
                    btrailPlank(),
                    btrailPlanksFence(),
                    btrailPressurePlate(),
                    btrailPumpking(),
                    btrailSlab(),
                    btrailStair(),
                    btrailStone(),
                    btrailTile(),
                    btrailWall(),
                    btrailWoodTrapdoor(),
                    btrailriteBlock(),
                    bundertideSpawnumber(),
                    bundertideTable(),
                    bwhiteChitinBlock()
            ));
        }

        private static TableDef babandonedSulpture() {
            return table("blocks/abandoned_sulpture",
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:cobbled_deepslate", 99, cond(), setCount(u(1.0F, 3.0F)))));
        }

        private static TableDef baegirGlassArch() {
            return table("blocks/aegir_glass_arch",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:aegir_glass_arch", 1, cond())));
        }

        private static TableDef baegirGlassBar() {
            return table("blocks/aegir_glass_bar",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:aegir_glass_bar", 1, cond())));
        }

        private static TableDef baegirGlassDeco() {
            return table("blocks/aegir_glass_deco",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:aegir_glass_deco", 1, cond())));
        }

        private static TableDef ballayBlock() {
            return table("blocks/allay_block",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:allay_block", 1, cond())));
        }

        private static TableDef banchorLower() {
            return table("blocks/anchor_lower",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:anchor_lower", 1, cond())));
        }

        private static TableDef banchorMedium() {
            return table("blocks/anchor_medium",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:anchor_medium", 1, cond())));
        }

        private static TableDef banchorUpper() {
            return table("blocks/anchor_upper",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:anchor_upper", 1, cond())));
        }

        private static TableDef bbatbedUpper() {
            return table("blocks/batbed_upper");
        }

        private static TableDef bberryCanumber() {
            return table("blocks/berry_can",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:canned_cherry", 1, cond())));
        }

        private static TableDef bblockBatbed() {
            return table("blocks/block_batbed",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:block_batbed", 1, cond())));
        }

        private static TableDef bblockChestfish() {
            return table("blocks/block_chestfish",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:block_chestfish", 1, cond())));
        }

        private static TableDef bchestFishFood() {
            return table("blocks/chest_fish_food");
        }

        private static TableDef bblockCrownumber() {
            return table("blocks/block_crown",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:block_crown", 1, cond())));
        }

        private static TableDef bblockCrystal() {
            return table("blocks/block_crystal",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:block_crystal", 1, cond())));
        }

        private static TableDef bblockExtension() {
            return table("blocks/block_extension",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:block_extension", 1, cond())));
        }

        private static TableDef bblockFate() {
            return table("blocks/block_fate",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:block_fate", 1, cond())));
        }

        private static TableDef bblockKettle() {
            return table("blocks/block_kettle",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:block_kettle", 1, cond())));
        }

        private static TableDef bblockRecorder() {
            return table("blocks/block_recorder",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:block_recorder", 1, cond())));
        }

        private static TableDef bblockSpear() {
            return table("blocks/block_spear",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:block_spear", 1, cond())));
        }

        private static TableDef bbombCopper() {
            return table("blocks/bomb_copper",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:bomb_copper", 1, cond())));
        }

        private static TableDef bbombTrailer() {
            return table("blocks/bomb_trailer",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:bomb_trailer", 1, cond())));
        }

        private static TableDef bcaerulaBookShelf() {
            return table("blocks/caerula_book_shelf",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:caerula_book_shelf", 1, cond())));
        }

        private static TableDef bcaramelCake() {
            return table("blocks/caramel_cake");
        }

        private static TableDef bchieseledSaltwindSandWall() {
            return table("blocks/chieseled_saltwind_sand_wall",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:chieseled_saltwind_sand_wall", 1, cond())));
        }

        private static TableDef bchiseledSaltwindSandstone() {
            return table("blocks/chiseled_saltwind_sandstone",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:chiseled_saltwind_sandstone", 1, cond())));
        }

        private static TableDef bchitinBlock() {
            return table("blocks/chitin_block",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:chitin_block", 1, cond())));
        }

        private static TableDef bcomplexChitinBlock() {
            return table("blocks/complex_chitin_block",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:complex_chitin_block", 1, cond())));
        }

        private static TableDef bcookedFibreBlock() {
            return table("blocks/cooked_fibre_block",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:cooked_fibre_block", 1, cond())));
        }

        private static TableDef bcrackedTrailBrick() {
            return table("blocks/cracked_trail_brick",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:cracked_trail_brick", 1, cond())));
        }

        private static TableDef bdeepSeagrass() {
            return table("blocks/deep_seagrass",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:deep_seagrass", 1, cond())));
        }

        private static TableDef bdragonBrand() {
            return table("blocks/dragon_brand",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:dragon_brand", 1, cond())));
        }

        private static TableDef bemergencyAidBuildingSalviento() {
            return table("blocks/emergency_aid_building_salviento",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:emergency_aid_building_salviento", 1, cond())));
        }

        private static TableDef bemergencyAidBuilding() {
            return table("blocks/emergency_aid_building",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:emergency_aid_building", 1, cond())));
        }

        private static TableDef bemergencyLight() {
            return table("blocks/emergency_light",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:emergency_light", 1, cond())));
        }

        private static TableDef benderinaCore() {
            return table("blocks/enderina_core",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:enderina_core", 1, cond())));
        }

        private static TableDef bendspeakerNest() {
            return table("blocks/endspeaker_nest",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:endspeaker_nest", 1, cond())));
        }

        private static TableDef bfax() {
            return table("blocks/fax",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:fax", 1, cond())));
        }

        private static TableDef bfibreBlock() {
            return table("blocks/fibre_block",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:fibre_block", 1, cond())));
        }

        private static TableDef bgoldenChalise() {
            return table("blocks/golden_chalise",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:golden_chalise", 1, cond())));
        }

        private static TableDef bheteropicBlock() {
            return table("blocks/heteropic_block",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:heteropic_block", 1, cond())));
        }

        private static TableDef binjector() {
            return table("blocks/injector",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:injector", 1, cond())));
        }

        private static TableDef bisharmlaBrickChiesled() {
            return table("blocks/isharmla_brick_chiesled",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:isharmla_brick_chiesled", 1, cond())));
        }

        private static TableDef bisharmlaBrickGilded() {
            return table("blocks/isharmla_brick_gilded",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:isharmla_brick_gilded", 1, cond())));
        }

        private static TableDef bisharmlaBrickPillar() {
            return table("blocks/isharmla_brick_pillar",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:isharmla_brick_pillar", 1, cond())));
        }

        private static TableDef bisharmlaBrick() {
            return table("blocks/isharmla_brick",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:isharmla_brick", 1, cond())));
        }

        private static TableDef bisharmlaRemainumber() {
            return table("blocks/isharmla_remain",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:isharmla_remain", 1, cond())));
        }

        private static TableDef bisharmlaSlab() {
            return table("blocks/isharmla_slab",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:isharmla_slab", 1, cond(), setCount(number(2.0F), blockState("caerula_arbor:isharmla_slab", "type", "double")), explosionDecay())));
        }

        private static TableDef bisharmlaStair() {
            return table("blocks/isharmla_stair",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:isharmla_stair", 1, cond())));
        }

        private static TableDef bisharmlaWallChiesled() {
            return table("blocks/isharmla_wall_chiesled",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:isharmla_wall_chiesled", 1, cond())));
        }

        private static TableDef bisharmlaWallGilded() {
            return table("blocks/isharmla_wall_gilded",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:isharmla_wall_gilded", 1, cond())));
        }

        private static TableDef bisharmlaWall() {
            return table("blocks/isharmla_wall",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:isharmla_wall", 1, cond())));
        }

        private static TableDef bkingsArmor() {
            return table("blocks/kings_armor",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:kings_armor", 1, cond())));
        }

        private static TableDef bnetherseaSampling() {
            return table("blocks/nethersea_sampling",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:nethersea_sampling", 1, cond())));
        }

        private static TableDef bnetherseaSoulSand() {
            return table("blocks/nethersea_soul_sand",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:nethersea_soul_sand", 1, cond())));
        }

        private static TableDef bnetherseaWood() {
            return table("blocks/nethersea_wood",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:nethersea_wood", 1, cond())));
        }

        private static TableDef boceanCrystalBlock() {
            return table("blocks/ocean_crystal_block",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:ocean_crystal_block", 1, cond())));
        }

        private static TableDef boceanFarmland() {
            return table("blocks/ocean_farmland",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("minecraft:dirt", 1, cond())));
        }

        private static TableDef boceanGlass() {
            return table("blocks/ocean_glass",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:ocean_glass", 1, cond())));
        }

        private static TableDef boceanGlasspane() {
            return table("blocks/ocean_glasspane",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:ocean_glasspane", 1, cond())));
        }

        private static TableDef boceanOvary() {
            return table("blocks/ocean_ovary",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:ocean_ovary", 100, cond(silkTouch()), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 65, cond(inverted(silkTouch())), setCount(u(5.0F, 7.0F))),
                            entry("caerula_arbor:whirl_eye", 35, cond(inverted(silkTouch())), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef boperationTable() {
            return table("blocks/operation_table",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:operation_table", 1, cond())));
        }

        private static TableDef bphloemBlock() {
            return table("blocks/phloem_block",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:phloem_block", 1, cond())));
        }

        private static TableDef breaperEgg() {
            return table("blocks/reaper_egg",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:reaper_egg", 1, cond())));
        }

        private static TableDef bredOvary() {
            return table("blocks/red_ovary",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 30, cond(inverted(silkTouch())), setCount(u(6.0F, 9.0F))),
                            entry("caerula_arbor:ocean_eye", 10, cond(inverted(silkTouch())), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_cell", 20, cond(inverted(silkTouch())), setCount(u(7.0F, 12.0F))),
                            entry("caerula_arbor:whirl_eye", 40, cond(inverted(silkTouch())), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:red_ovary", 100, cond(silkTouch()), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef bredstoneIris() {
            return table("blocks/redstone_iris",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:redstone_iris", 1, cond())));
        }

        private static TableDef bredstoneirisSeeding() {
            return table("blocks/redstoneiris_seeding",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:redstoneiris_seeding", 1, cond())));
        }

        private static TableDef bsaltsand() {
            return table("blocks/saltsand",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:saltsand", 1, cond())));
        }

        private static TableDef bsaltwindBrickSlab() {
            return table("blocks/saltwind_brick_slab",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:saltwind_brick_slab", 1, cond(), setCount(number(2.0F), blockState("caerula_arbor:saltwind_brick_slab", "type", "double")), explosionDecay())));
        }

        private static TableDef bsaltwindBrick() {
            return table("blocks/saltwind_brick",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:saltwind_brick", 1, cond())));
        }

        private static TableDef bsaltwindColumnumber() {
            return table("blocks/saltwind_column",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:saltwind_column", 1, cond())));
        }

        private static TableDef bsaltwindSandSlab() {
            return table("blocks/saltwind_sand_slab",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:saltwind_sand_slab", 1, cond(), setCount(number(2.0F), blockState("caerula_arbor:saltwind_sand_slab", "type", "double")), explosionDecay())));
        }

        private static TableDef bsaltwindSandStair() {
            return table("blocks/saltwind_sand_stair",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:saltwind_sand_stair", 1, cond())));
        }

        private static TableDef bsaltwindSandWall() {
            return table("blocks/saltwind_sand_wall",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:saltwind_sand_wall", 1, cond())));
        }

        private static TableDef bsaltwindSandstone() {
            return table("blocks/saltwind_sandstone",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:saltwind_sandstone", 1, cond())));
        }

        private static TableDef bsaltwindSmoothBrick() {
            return table("blocks/saltwind_smooth_brick",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:saltwind_smooth_brick", 1, cond())));
        }

        private static TableDef bsaltwindSmoothSlab() {
            return table("blocks/saltwind_smooth_slab",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:saltwind_smooth_slab", 1, cond(), setCount(number(2.0F), blockState("caerula_arbor:saltwind_smooth_slab", "type", "double")), explosionDecay())));
        }

        private static TableDef bsaltwindSmoothStair() {
            return table("blocks/saltwind_smooth_stair",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:saltwind_smooth_stair", 1, cond())));
        }

        private static TableDef bsaltwindStair() {
            return table("blocks/saltwind_stair",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:saltwind_stair", 1, cond())));
        }

        private static TableDef bseaPrairieBomb() {
            return table("blocks/sea_prairie_bomb",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:sea_prairie_bomb", 1, cond())));
        }

        private static TableDef bseaTrailBurntSolid() {
            return table("blocks/sea_trail_burnt_solid",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:trail_powder", 1, cond(), setCount(number(3.0F)), explosionDecay())));
        }

        private static TableDef bseaTrailBurnt() {
            return table("blocks/sea_trail_burnt",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_powder", 1, cond())));
        }

        private static TableDef bseaTrailGrowing() {
            return table("blocks/sea_trail_growing",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 100, cond(), setCount(u(1.0F, 2.0F)), explosionDecay())));
        }

        private static TableDef bseaTrailGrownumber() {
            return table("blocks/sea_trail_grown",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 100, cond(), setCount(u(2.0F, 3.0F)), explosionDecay())));
        }

        private static TableDef bseaTrailInit() {
            return table("blocks/sea_trail_init");
        }

        private static TableDef bseaTrailSolid() {
            return table("blocks/sea_trail_solid",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 100, cond(inverted(silkTouch())), setCount(u(6.0F, 9.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_solid", 100, cond(silkTouch()), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef bseaTrailStop() {
            return table("blocks/sea_trail_stop",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 100, cond(), setCount(u(2.0F, 3.0F)))));
        }

        private static TableDef bsmoothSaltwindSandSlab() {
            return table("blocks/smooth_saltwind_sand_slab",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:smooth_saltwind_sand_slab", 1, cond(), setCount(number(2.0F), blockState("caerula_arbor:smooth_saltwind_sand_slab", "type", "double")), explosionDecay())));
        }

        private static TableDef bsmoothSaltwindSandStair() {
            return table("blocks/smooth_saltwind_sand_stair",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:smooth_saltwind_sand_stair", 1, cond())));
        }

        private static TableDef bsmoothSaltwindSandWall() {
            return table("blocks/smooth_saltwind_sand_wall",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:smooth_saltwind_sand_wall", 1, cond())));
        }

        private static TableDef bsmoothSaltwindSandatone() {
            return table("blocks/smooth_saltwind_sandatone",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:smooth_saltwind_sandatone", 1, cond())));
        }

        private static TableDef bstrippedNetherseaWood() {
            return table("blocks/stripped_nethersea_wood",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:stripped_nethersea_wood", 1, cond())));
        }

        private static TableDef bstrippedTrailLog() {
            return table("blocks/stripped_trail_log",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:stripped_trail_log", 1, cond())));
        }

        private static TableDef bthirstCoral() {
            return table("blocks/thirst_coral",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:thirst_coral", 1, cond())));
        }

        private static TableDef btideBishopCore() {
            return table("blocks/tide_bishop_core",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:tide_bishop_core", 1, cond())));
        }

        private static TableDef btidebishopCoreEmpty() {
            return table("blocks/tidebishop_core_empty",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:tidebishop_core_empty", 1, cond())));
        }

        private static TableDef btideObservation() {
            return table("blocks/tide_observation",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:tide_observation", 1, cond())));
        }

        private static TableDef btrailBrick() {
            return table("blocks/trail_brick",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_brick", 1, cond())));
        }

        private static TableDef btrailButton() {
            return table("blocks/trail_button",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_button", 1, cond())));
        }

        private static TableDef btrailCake() {
            return table("blocks/trail_cake");
        }

        private static TableDef btrailDebris() {
            return table("blocks/trail_debris",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_debris", 1, cond())));
        }

        private static TableDef btrailLeave() {
            return table("blocks/trail_leave",
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 85, cond(inverted(silkTouch())), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:stick", 10, cond(inverted(silkTouch())), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:trail_apple", 5, cond(inverted(silkTouch())), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:nethersea_sampling", 15, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:trail_leave", 100, cond(silkTouch()), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef btrailLog() {
            return table("blocks/trail_log",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_log", 1, cond())));
        }

        private static TableDef btrailMushroom() {
            return table("blocks/trail_mushroom",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_mushroom", 1, cond())));
        }

        private static TableDef btrailPlankButton() {
            return table("blocks/trail_plank_button",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_plank_button", 1, cond())));
        }

        private static TableDef btrailPlankDoor() {
            return table("blocks/trail_plank_door",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_plank_door", 1, cond(blockState("caerula_arbor:trail_plank_door", "half", "lower")))));
        }

        private static TableDef btrailPlankFencedoor() {
            return table("blocks/trail_plank_fencedoor",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_plank_fencedoor", 1, cond())));
        }

        private static TableDef btrailPlankPressurePlate() {
            return table("blocks/trail_plank_pressure_plate",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_plank_pressure_plate", 1, cond())));
        }

        private static TableDef btrailPlankSlab() {
            return table("blocks/trail_plank_slab",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:trail_plank_slab", 1, cond(), setCount(number(2.0F), blockState("caerula_arbor:trail_plank_slab", "type", "double")), explosionDecay())));
        }

        private static TableDef btrailPlankStair() {
            return table("blocks/trail_plank_stair",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_plank_stair", 1, cond())));
        }

        private static TableDef btrailPlank() {
            return table("blocks/trail_plank",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_plank", 1, cond())));
        }

        private static TableDef btrailPlanksFence() {
            return table("blocks/trail_planks_fence",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_planks_fence", 1, cond())));
        }

        private static TableDef btrailPressurePlate() {
            return table("blocks/trail_pressure_plate",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_pressure_plate", 1, cond())));
        }

        private static TableDef btrailPumpking() {
            return table("blocks/trail_pumpking",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_pumpking", 1, cond())));
        }

        private static TableDef btrailSlab() {
            return table("blocks/trail_slab",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:trail_slab", 1, cond(), setCount(number(2.0F), blockState("caerula_arbor:trail_slab", "type", "double")), explosionDecay())));
        }

        private static TableDef btrailStair() {
            return table("blocks/trail_stair",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_stair", 1, cond())));
        }

        private static TableDef btrailStone() {
            return table("blocks/trail_stone",
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:cobblestone", 100, cond(inverted(silkTouch())), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 100, cond(inverted(silkTouch())), setCount(u(1.0F, 2.0F)), oreBonus())),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:trail_stone", 100, cond(silkTouch()), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef btrailTile() {
            return table("blocks/trail_tile",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_tile", 1, cond())));
        }

        private static TableDef btrailWall() {
            return table("blocks/trail_wall",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_wall", 1, cond())));
        }

        private static TableDef btrailWoodTrapdoor() {
            return table("blocks/trail_wood_trapdoor",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trail_wood_trapdoor", 1, cond())));
        }

        private static TableDef btrailriteBlock() {
            return table("blocks/trailrite_block",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:trailrite_block", 1, cond())));
        }

        private static TableDef bundertideSpawnumber() {
            return table("blocks/undertide_spawn",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:undertide_spawn", 1, cond())));
        }

        private static TableDef bundertideTable() {
            return table("blocks/undertide_table",
                    pool(number(1.0F), null, cond(survivesExplosion()),
                            entry("caerula_arbor:undertide_table", 1, cond())));
        }

        private static TableDef bwhiteChitinBlock() {
            return table("blocks/white_chitin_block",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:white_chitin", 100, cond(inverted(silkTouch())), setCount(u(3.0F, 5.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:white_chitin_block", 100, cond(silkTouch()), setCount(u(1.0F, 1.0F)))));
        }
    }

    /**
     * 生成箱子战利品表
     */
    public static final class ChestTables extends GeneratedLootTableProvider {
        /**
         * 创建箱子战利品表子 provider
         */
        public ChestTables() {
            super(List.of(
                    caegirGene(),
                    caegirLife(),
                    caegirStore(),
                    canchorStatue(),
                    cbelieverHomeBarrel(),
                    cbishopChest(),
                    cbishopTreasure(),
                    cbrandPortal(),
                    cchurchBarrelBooks(),
                    cchurchBarrelBrand(),
                    cchurchBarrelChitinumber(),
                    cchurchBarrelEquip(),
                    cchurchBarrelWeapon(),
                    cchurchHallway(),
                    cchurchKitchenumber(),
                    cchurchLeftChest(),
                    cchurchMidChest(),
                    cchurchRoomGround(),
                    ccombatBarrelA1(),
                    ccombatBarrelA2(),
                    ccombatBarrelBChannel(),
                    ccombatBarrelB(),
                    ccombatBarrel(),
                    ceyeBackroom(),
                    ceyeBarrel(),
                    ceyeCommon(),
                    ceyeLight(),
                    ceyeSide(),
                    ceyeTop(),
                    cfactoryFruits(),
                    cfactoryGem(),
                    cfactoryPoor(),
                    cfactoryStones(),
                    cfactoryTop(),
                    cflourishBarrel(),
                    cinquiFrontBed(),
                    cinquiFrontFood(),
                    cinquiFrontSpec(),
                    cinquiFrontUnder(),
                    cinquiHomeChurch(),
                    cinquiHomeCommon(),
                    cinquiHomeStore(),
                    clabAward(),
                    clabBarrel(),
                    clabBedroom(),
                    clabBossHallway(),
                    clabClassroom(),
                    clabTreasure(),
                    clighthouseBarrel(),
                    clighthouseBed(),
                    cmuseumLoot(),
                    coddChitinumber(),
                    coddCol(),
                    coddReaperroom(),
                    coddToilet(),
                    cpalaceBrew(),
                    cpalaceFood(),
                    cpalaceTreasure(),
                    credstones(),
                    csadChurchBarrel(),
                    cshipFood(),
                    cshipHead(),
                    cshipRock(),
                    cshipSeat(),
                    cshipTreasure(),
                    cshipwreckMap(),
                    csinkWreckBarrel(),
                    cspawnBonusAppendix(),
                    csubmarineContents(),
                    csubmarineGene(),
                    ctideStationChest(),
                    ctideStationTop(),
                    ctownPossesion(),
                    cwatchtowerBomb()
            ));
        }

        private static TableDef caegirGene() {
            return table("chests/aegir_gene",
                    pool(u(2.0F, 5.0F), null, cond(),
                            entry("minecraft:iron_ingot", 25, cond(), setCount(u(12.0F, 18.0F))),
                            entry("caerula_arbor:item_helper", 1, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:redstone", 25, cond(), setCount(u(7.0F, 9.0F))),
                            entry("caerula_arbor:redstone_ingot", 15, cond(), setCount(u(2.0F, 5.0F))),
                            entry("minecraft:paper", 35, cond(), setCount(u(8.0F, 12.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:personnel_transporter", 1, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("caerula_arbor:hunter_gene", 2, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_extractor", 3, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_cutin", 15, cond(), setCount(u(2.0F, 6.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:targeted_base", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:broken_ocean_cell", 25, cond(), setCount(u(8.0F, 16.0F)))));
        }

        private static TableDef caegirLife() {
            return table("chests/aegir_life",
                    pool(u(3.0F, 6.0F), null, cond(),
                            entry("caerula_arbor:heteropic_piece", 5, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:copper_ingot", 25, cond(), setCount(u(5.0F, 12.0F))),
                            entry("caerula_arbor:oceanglass_cup", 6, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:a_cup_of_water", 3, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(3.0F, 6.0F), null, cond(),
                            entry("caerula_arbor:canned_water", 15, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:book", 25, cond(), setCount(u(4.0F, 8.0F))),
                            entry("caerula_arbor:treaty_empty", 5, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:canned_noodle", 5, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:kettle", 5, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef caegirStore() {
            return table("chests/aegir_store",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:smelly_hemostatic", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:apple", 25, cond(), setCount(u(3.0F, 5.0F))),
                            entry("caerula_arbor:trail_apple", 15, cond(), setCount(u(2.0F, 4.0F)))),
                    pool(u(3.0F, 7.0F), null, cond(),
                            entry("caerula_arbor:ocean_eye", 3, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:netherite_scrap", 1, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_crystal", 25, cond(), setCount(u(7.0F, 9.0F))),
                            entry("caerula_arbor:chitin_ingot", 12, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:iron_ingot", 32, cond(), setCount(u(4.0F, 9.0F))),
                            entry("minecraft:gold_ingot", 32, cond(), setCount(u(5.0F, 9.0F)))));
        }

        private static TableDef canchorStatue() {
            return table("chests/anchor_statue",
                    pool(u(3.0F, 7.0F), null, cond(),
                            entry("minecraft:deepslate", 50, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:deepslate_tiles", 50, cond(), setCount(u(2.0F, 4.0F)))),
                    pool(u(2.0F, 5.0F), null, cond(),
                            entry("minecraft:gravel", 55, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:sea_pickle", 45, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:iron_ingot", 35, cond(), setCount(u(3.0F, 9.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:anchor_shard", 100, cond(), setCount(u(2.0F, 4.0F)))));
        }

        private static TableDef cbelieverHomeBarrel() {
            return table("chests/believer_home_barrel",
                    pool(u(2.0F, 6.0F), null, cond(),
                            entry("caerula_arbor:broken_cell_cluster", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:broken_ocean_cell", 67, cond(), setCount(u(3.0F, 6.0F)))),
                    pool(u(4.0F, 7.0F), null, cond(),
                            entry("caerula_arbor:ocean_fibre", 22, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:fake_egg", 12, cond(), setCount(u(3.0F, 3.0F))),
                            entry("caerula_arbor:deep_seagrass", 33, cond(), setCount(u(3.0F, 6.0F))),
                            entry("caerula_arbor:deep_seagrass_juice", 23, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef cbishopChest() {
            return table("chests/bishop_chest",
                    pool(u(2.0F, 3.0F), null, cond(),
                            entry("minecraft:deepslate", 44, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:cobbled_deepslate", 44, cond(), setCount(u(1.0F, 3.0F)))),
                    pool(u(1.0F, 3.0F), null, cond(),
                            entry("caerula_arbor:ocean_peduncle", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:ocean_phloem", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:ocean_fibre", 33, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:fluore_berries", 1, cond(), setCount(u(0.0F, 3.0F)))));
        }

        private static TableDef cbishopTreasure() {
            return table("chests/bishop_treasure",
                    pool(number(2.0F), null, cond(),
                            entry("caerula_arbor:ocean_eye", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:trail_powder", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:trail_cream", 44, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:ocean_extractor", 1, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef cbrandPortal() {
            return table("chests/brand_portal",
                    pool(u(4.0F, 6.0F), null, cond(),
                            entry("minecraft:gold_ingot", 45, cond(), setCount(u(3.0F, 4.0F))),
                            entry("minecraft:gold_block", 32, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:experience_bottle", 45, cond(), setCount(u(3.0F, 6.0F))),
                            entry("caerula_arbor:sea_trail_mor", 32, cond(), setCount(u(6.0F, 8.0F))),
                            entry("caerula_arbor:trail_powder", 24, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("minecraft:golden_helmet", 25, cond(), setCount(u(1.0F, 2.0F)), enchantWithLevels(u(1.0F, 1.0F), true)),
                            entry("minecraft:golden_chestplate", 25, cond(), setCount(u(1.0F, 2.0F)), enchantWithLevels(u(1.0F, 1.0F), true)),
                            entry("minecraft:golden_leggings", 25, cond(), setCount(u(1.0F, 2.0F)), enchantWithLevels(u(1.0F, 1.0F), true)),
                            entry("minecraft:golden_boots", 25, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(1.0F, 4.0F), null, cond(),
                            entry("minecraft:obsidian", 33, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:crying_obsidian", 33, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef cchurchBarrelBooks() {
            return table("chests/church_barrel_books",
                    pool(u(2.0F, 3.0F), number(1.0F), cond(),
                            entry("minecraft:book", 99, cond(), setCount(u(2.0F, 4.0F)))));
        }

        private static TableDef cchurchBarrelBrand() {
            return table("chests/church_barrel_brand",
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 99, cond(), setCount(u(3.0F, 6.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:bone_shard", 1, cond(), setCount(u(2.0F, 6.0F)))),
                    pool(u(1.0F, 3.0F), number(1.0F), cond(),
                            entry("minecraft:bow", 33, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(2.0F, 3.0F), true)),
                            entry("caerula_arbor:meat_can", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:obisidian_ball", 22, cond(), setCount(u(1.0F, 3.0F)))));
        }

        private static TableDef cchurchBarrelChitinumber() {
            return table("chests/church_barrel_chitin",
                    pool(u(3.0F, 4.0F), number(1.0F), cond(),
                            entry("caerula_arbor:ocean_chitin", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("minecraft:iron_ingot", 44, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:ocean_crystal", 33, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("minecraft:iron_sword", 44, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(1.0F, 3.0F), true)),
                            entry("caerula_arbor:chitin_pickaxe", 33, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(1.0F, 2.0F), true)),
                            entry("caerula_arbor:chitin_axe", 33, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(1.0F, 3.0F), true))));
        }

        private static TableDef cchurchBarrelEquip() {
            return table("chests/church_barrel_equip",
                    pool(u(1.0F, 3.0F), number(1.0F), cond(),
                            entry("minecraft:leather_helmet", 25, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:leather_chestplate", 25, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:leather_leggings", 25, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:leather_boots", 25, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(0.0F, 2.0F), u(0.0F, 1.0F), cond(),
                            entry("minecraft:chainmail_helmet", 25, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(0.0F, 1.0F), true)),
                            entry("minecraft:chainmail_chestplate", 25, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(0.0F, 1.0F), true)),
                            entry("minecraft:chainmail_leggings", 25, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(0.0F, 1.0F), true)),
                            entry("minecraft:chainmail_boots", 25, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(0.0F, 1.0F), true))),
                    pool(u(0.0F, 1.0F), null, cond(),
                            entry("caerula_arbor:sealeather_helmet", 25, cond(), setCount(u(0.0F, 1.0F)), enchantWithLevels(u(0.0F, 2.0F), true)),
                            entry("caerula_arbor:sealeather_chestplate", 25, cond(), setCount(u(0.0F, 1.0F)), enchantWithLevels(u(0.0F, 2.0F), true)),
                            entry("caerula_arbor:sealeather_leggings", 25, cond(), setCount(u(0.0F, 1.0F)), enchantWithLevels(u(0.0F, 2.0F), true)),
                            entry("caerula_arbor:sealeather_boots", 25, cond(), setCount(u(0.0F, 1.0F)), enchantWithLevels(u(0.0F, 2.0F), true))));
        }

        private static TableDef cchurchBarrelWeapon() {
            return table("chests/church_barrel_weapon",
                    pool(u(1.0F, 3.0F), number(1.0F), cond(),
                            entry("minecraft:iron_axe", 33, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(2.0F, 4.0F), true)),
                            entry("caerula_arbor:trailed_stone_sword", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:trail_mop", 22, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(0.0F, 1.0F), true))),
                    pool(u(3.0F, 5.0F), null, cond(),
                            entry("caerula_arbor:bone_shard", 33, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:iron_nugget", 44, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:ocean_cutin", 44, cond(), setCount(u(3.0F, 3.0F)))));
        }

        private static TableDef cchurchHallway() {
            return table("chests/church_hallway",
                    pool(u(3.0F, 5.0F), u(1.0F, 2.0F), cond(),
                            entry("caerula_arbor:ocean_fibre", 60, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:ocean_arrow", 40, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("minecraft:shield", 50, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:iron_sword", 50, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(2.0F, 4.0F), true)),
                            entry("minecraft:bow", 45, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(0.0F, 3.0F), true))),
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:golden_apple", 99, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef cchurchKitchenumber() {
            return table("chests/church_kitchen",
                    pool(u(3.0F, 6.0F), u(1.0F, 2.0F), cond(),
                            entry("caerula_arbor:ocean_fibre", 44, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:cooked_peduncle", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:claw", 22, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:caramel_mor", 17, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:cooked_fibre", 33, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("minecraft:golden_apple", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:fluore_berries", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:bowl_seagrass", 33, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef cchurchLeftChest() {
            return table("chests/church_left_chest",
                    pool(u(2.0F, 5.0F), number(1.0F), cond(),
                            entry("minecraft:string", 50, cond(), setCount(u(2.0F, 6.0F))),
                            entry("minecraft:torch", 50, cond(), setCount(u(1.0F, 3.0F)))),
                    pool(u(2.0F, 3.0F), number(1.0F), cond(),
                            entry("minecraft:iron_ingot", 50, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_chitin", 50, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("minecraft:golden_apple", 1, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef cchurchMidChest() {
            return table("chests/church_mid_chest",
                    pool(u(2.0F, 4.0F), number(1.0F), cond(),
                            entry("minecraft:string", 55, cond(), setCount(u(2.0F, 5.0F))),
                            entry("minecraft:torch", 45, cond(), setCount(u(1.0F, 3.0F)))),
                    pool(u(2.0F, 3.0F), null, cond(),
                            entry("minecraft:iron_ingot", 99, cond(), setCount(u(2.0F, 5.0F)))));
        }

        private static TableDef cchurchRoomGround() {
            return table("chests/church_room_ground",
                    pool(u(2.0F, 5.0F), u(1.0F, 2.0F), cond(),
                            entry("caerula_arbor:ocean_crystal", 45, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:ocean_cutin", 55, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(2.0F, 3.0F), u(0.0F, 1.0F), cond(),
                            entry("minecraft:golden_apple", 33, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:voyage_of_gold", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_arrow", 44, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("minecraft:crossbow", 50, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(2.0F, 3.0F), true)),
                            entry("minecraft:iron_axe", 50, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(3.0F, 4.0F), true)),
                            entry("caerula_arbor:trailed_iron_sword", 30, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(3.0F, 5.0F), true))));
        }

        private static TableDef ccombatBarrelA1() {
            return table("chests/combat_barrel_a_1",
                    pool(u(2.0F, 3.0F), null, cond(),
                            entry("minecraft:pufferfish", 33, cond(), setCount(u(3.0F, 4.0F))),
                            entry("minecraft:stick", 22, cond(), setCount(u(2.0F, 3.0F))),
                            entry("minecraft:sand", 33, cond(), setCount(u(2.0F, 4.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("minecraft:glass_bottle", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("minecraft:pufferfish_bucket", 11, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef ccombatBarrelA2() {
            return table("chests/combat_barrel_a_2",
                    pool(u(2.0F, 3.0F), null, cond(),
                            entry("minecraft:iron_ingot", 33, cond(), setCount(u(4.0F, 8.0F))),
                            entry("caerula_arbor:ocean_crystal", 22, cond(), setCount(u(3.0F, 7.0F))),
                            entry("minecraft:iron_nugget", 33, cond(), setCount(u(3.0F, 12.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:pickaxe_ocean_crystal", 50, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(0.0F, 1.0F), true)),
                            entry("caerula_arbor:sword_ocean_crystal", 50, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(0.0F, 2.0F), true))));
        }

        private static TableDef ccombatBarrelBChannel() {
            return table("chests/combat_barrel_b_channel",
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("minecraft:pufferfish", 22, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:salmon", 55, cond(), setCount(u(3.0F, 6.0F))),
                            entry("minecraft:cooked_cod", 55, cond(), setCount(u(3.0F, 6.0F))),
                            entry("minecraft:tropical_fish", 22, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:golden_chalise", 2, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:ocean_ovary", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:red_ovary", 11, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:sea_trail_mor", 66, cond(), setCount(u(3.0F, 8.0F)))));
        }

        private static TableDef ccombatBarrelB() {
            return table("chests/combat_barrel_b",
                    pool(u(3.0F, 5.0F), null, cond(),
                            entry("caerula_arbor:ocean_chitin", 33, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:ocean_crystal", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:sea_trail_mor", 44, cond(), setCount(u(3.0F, 7.0F))),
                            entry("caerula_arbor:ocean_fibre", 44, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:ocean_phloem", 44, cond(), setCount(u(3.0F, 6.0F)))),
                    pool(u(4.0F, 8.0F), null, cond(),
                            entry("minecraft:iron_ingot", 33, cond(), setCount(u(6.0F, 10.0F))),
                            entry("minecraft:raw_iron", 22, cond(), setCount(u(5.0F, 9.0F))),
                            entry("minecraft:copper_ingot", 33, cond(), setCount(u(4.0F, 8.0F))),
                            entry("minecraft:raw_copper", 22, cond(), setCount(u(5.0F, 9.0F))),
                            entry("minecraft:gold_ingot", 22, cond(), setCount(u(2.0F, 5.0F))),
                            entry("minecraft:raw_gold", 11, cond(), setCount(u(3.0F, 5.0F)))));
        }

        private static TableDef ccombatBarrel() {
            return table("chests/combat_barrel",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:kettle", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:score", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:allay_sculpture", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:bowl_seagrass", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:relic_curse_emelight", 11, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:canned_cherry", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:odd_flute", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:royal_fate", 1, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(3.0F, 7.0F), null, cond(),
                            entry("caerula_arbor:ocean_phloem", 33, cond(), setCount(u(4.0F, 12.0F))),
                            entry("minecraft:gold_ingot", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:diamond", 22, cond(), setCount(u(1.0F, 4.0F))),
                            entry("minecraft:copper_ingot", 33, cond(), setCount(u(6.0F, 8.0F))),
                            entry("minecraft:iron_ingot", 33, cond(), setCount(u(6.0F, 9.0F))),
                            entry("caerula_arbor:shell_of_stonecutter", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:bone_shard", 22, cond(), setCount(u(1.0F, 4.0F)))));
        }

        private static TableDef ceyeBackroom() {
            return table("chests/eye_backroom",
                    pool(u(4.0F, 6.0F), null, cond(),
                            entry("caerula_arbor:ocean_crystal", 33, cond(), setCount(u(3.0F, 8.0F))),
                            entry("caerula_arbor:ocean_eye", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:fermented_ocean_eye", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:bomb_copper", 22, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:cell_cluster", 22, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(u(1.0F, 3.0F), null, cond(),
                            entry("caerula_arbor:immunosuppressor", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:heteropic_piece", 33, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:trail_powder", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:redstonium", 22, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef ceyeBarrel() {
            return table("chests/eye_barrel",
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:apple", 44, cond(), setCount(u(3.0F, 6.0F))),
                            entry("caerula_arbor:trail_apple", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:torch", 33, cond(), setCount(u(3.0F, 6.0F))),
                            entry("minecraft:iron_ingot", 22, cond(), setCount(u(2.0F, 3.0F))),
                            entry("minecraft:water_bucket", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:bread", 33, cond(), setCount(u(4.0F, 7.0F)))));
        }

        private static TableDef ceyeCommon() {
            return table("chests/eye_common",
                    pool(u(4.0F, 8.0F), null, cond(),
                            entry("caerula_arbor:ocean_fibre", 33, cond(), setCount(u(4.0F, 8.0F))),
                            entry("caerula_arbor:ocean_chitin", 33, cond(), setCount(u(3.0F, 6.0F))),
                            entry("caerula_arbor:ocean_phloem", 33, cond(), setCount(u(3.0F, 4.0F))),
                            entry("caerula_arbor:ocean_cutin", 33, cond(), setCount(u(3.0F, 4.0F))),
                            entry("minecraft:bone", 22, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:fake_egg", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_cell", 22, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:ocean_eye", 12, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:trail_mushroom", 25, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:trail_apple", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("minecraft:gunpowder", 33, cond(), setCount(u(1.0F, 3.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:redstone_ingot", 25, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:trail_brick", 30, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:cracked_trail_brick", 30, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:redstone", 25, cond(), setCount(u(4.0F, 5.0F))),
                            entry("minecraft:leather", 30, cond(), setCount(u(1.0F, 3.0F)))));
        }

        private static TableDef ceyeLight() {
            return table("chests/eye_light",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:lantern_judgement", 100, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(0.0F, 1.0F), true))),
                    pool(u(3.0F, 6.0F), null, cond(),
                            entry("minecraft:iron_sword", 44, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(1.0F, 2.0F), true)),
                            entry("caerula_arbor:phloem_bow", 33, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(1.0F, 2.0F), true)),
                            entry("caerula_arbor:chitin_sword", 33, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(1.0F, 2.0F), true)),
                            entry("caerula_arbor:chitin_shield", 33, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(1.0F, 2.0F), true)),
                            entry("caerula_arbor:trailed_golden_sword", 33, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(1.0F, 2.0F), true)),
                            entry("caerula_arbor:trailed_iron_sword", 33, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(1.0F, 2.0F), true)),
                            entry("caerula_arbor:smelly_hemostatic", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:dna_reaper", 22, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef ceyeSide() {
            return table("chests/eye_side",
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:score", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:paper", 55, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:paper_bag", 44, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:string", 33, cond(), setCount(u(3.0F, 5.0F)))),
                    pool(u(3.0F, 6.0F), null, cond(),
                            entry("caerula_arbor:ocean_eye", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:ender_pearl", 22, cond(), setCount(u(1.0F, 4.0F))),
                            entry("caerula_arbor:ocean_fibre", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:ocean_chitin", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:broken_ocean_cell", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:bone_shard", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:viviparous_lily", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:enchanted_trail_golden_apple", 12, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef ceyeTop() {
            return table("chests/eye_top",
                    pool(u(6.0F, 9.0F), null, cond(),
                            entry("caerula_arbor:trail_powder_core", 50, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_eye", 33, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:real_egg", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:ocean_fibre", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:ocean_phloem", 33, cond(), setCount(u(3.0F, 4.0F))),
                            entry("caerula_arbor:ocean_chitin", 33, cond(), setCount(u(2.0F, 5.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:voyage_of_gold", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:gold_ingot", 33, cond(), setCount(u(3.0F, 6.0F))),
                            entry("caerula_arbor:redstone_ingot", 33, cond(), setCount(u(4.0F, 8.0F)))));
        }

        private static TableDef cfactoryFruits() {
            return table("chests/factory_fruits",
                    pool(u(4.0F, 7.0F), null, cond(),
                            entry("caerula_arbor:fluore_berries", 44, cond(), setCount(u(2.0F, 3.0F))),
                            entry("minecraft:ink_sac", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("minecraft:glow_ink_sac", 33, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:raw_gold_block", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:golden_carrot", 22, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:diamond", 12, cond(), setCount(u(1.0F, 4.0F)))));
        }

        private static TableDef cfactoryGem() {
            return table("chests/factory_gem",
                    pool(u(4.0F, 7.0F), null, cond(),
                            entry("minecraft:iron_ingot", 33, cond(), setCount(u(3.0F, 5.0F))),
                            entry("minecraft:raw_gold", 22, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:gold_ingot", 22, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:raw_iron", 33, cond(), setCount(u(3.0F, 5.0F))),
                            entry("minecraft:emerald", 11, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:golden_apple", 11, cond(), setCount(u(2.0F, 3.0F)))));
        }

        private static TableDef cfactoryPoor() {
            return table("chests/factory_poor",
                    pool(u(3.0F, 5.0F), null, cond(),
                            entry("minecraft:iron_nugget", 44, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:iron_ingot", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:coal", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:charcoal", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:raw_gold", 22, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:spider_eye", 22, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:firework_star", 99, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef cfactoryStones() {
            return table("chests/factory_stones",
                    pool(u(3.0F, 6.0F), null, cond(),
                            entry("minecraft:andesite", 55, cond(), setCount(u(3.0F, 8.0F))),
                            entry("minecraft:dried_kelp_block", 22, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("minecraft:carved_pumpkin", 99, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef cfactoryTop() {
            return table("chests/factory_top",
                    pool(u(2.0F, 7.0F), null, cond(),
                            entry("minecraft:iron_ingot", 50, cond(), setCount(u(2.0F, 3.0F))),
                            entry("minecraft:copper_ingot", 50, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:lapis_lazuli", 33, cond(), setCount(u(3.0F, 8.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:netherite_scrap", 50, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:diamond", 50, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef cflourishBarrel() {
            return table("chests/flourish_barrel",
                    pool(u(2.0F, 6.0F), null, cond(),
                            entry("minecraft:iron_ingot", 25, cond(), setCount(u(2.0F, 6.0F))),
                            entry("minecraft:raw_iron", 25, cond(), setCount(u(3.0F, 5.0F))),
                            entry("minecraft:copper_ingot", 25, cond(), setCount(u(2.0F, 8.0F))),
                            entry("minecraft:raw_copper", 25, cond(), setCount(u(3.0F, 5.0F))),
                            entry("minecraft:gold_ingot", 15, cond(), setCount(u(2.0F, 9.0F))),
                            entry("minecraft:raw_gold", 15, cond(), setCount(u(3.0F, 9.0F))),
                            entry("minecraft:diamond", 15, cond(), setCount(u(2.0F, 6.0F)))),
                    pool(u(4.0F, 7.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 25, cond(), setCount(u(3.0F, 9.0F))),
                            entry("caerula_arbor:ocean_phloem", 25, cond(), setCount(u(4.0F, 5.0F))),
                            entry("caerula_arbor:broken_ocean_cell", 20, cond(), setCount(u(6.0F, 12.0F))),
                            entry("caerula_arbor:ocean_cell", 20, cond(), setCount(u(4.0F, 9.0F))),
                            entry("caerula_arbor:broken_cell_cluster", 15, cond(), setCount(u(2.0F, 5.0F))),
                            entry("caerula_arbor:nethersea_chicken_egg", 15, cond(), setCount(u(2.0F, 5.0F))),
                            entry("caerula_arbor:fake_egg", 15, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:real_egg", 15, cond(), setCount(u(1.0F, 4.0F)))),
                    pool(u(3.0F, 5.0F), null, cond(),
                            entry("caerula_arbor:chitin_ingot", 10, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_chitin", 25, cond(), setCount(u(2.0F, 6.0F))),
                            entry("caerula_arbor:ocean_cutin", 25, cond(), setCount(u(3.0F, 8.0F))),
                            entry("caerula_arbor:ocean_crystal", 15, cond(), setCount(u(3.0F, 5.0F))),
                            entry("caerula_arbor:viviparous_lily", 20, cond(), setCount(u(2.0F, 4.0F)))),
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("caerula_arbor:radiant_berries", 10, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:fluore_berries", 25, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:bowl_seagrass", 25, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:rainbow_candy", 15, cond(), setCount(u(1.0F, 4.0F))),
                            entry("caerula_arbor:nethersea_coffee", 15, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef cinquiFrontBed() {
            return table("chests/inqui_front_bed",
                    pool(u(3.0F, 6.0F), null, cond(),
                            entry("minecraft:iron_ingot", 30, cond(), setCount(u(4.0F, 7.0F))),
                            entry("minecraft:iron_nugget", 25, cond(), setCount(u(6.0F, 9.0F))),
                            entry("minecraft:copper_ingot", 30, cond(), setCount(u(4.0F, 6.0F))),
                            entry("minecraft:gold_nugget", 15, cond(), setCount(u(3.0F, 5.0F))),
                            entry("caerula_arbor:redstone_ingot", 15, cond(), setCount(u(5.0F, 7.0F)))),
                    pool(u(3.0F, 6.0F), null, cond(),
                            entry("minecraft:amethyst_shard", 50, cond(), setCount(u(3.0F, 6.0F))),
                            entry("minecraft:cake", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:trail_powder", 15, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:white_tulip", 15, cond(), setCount(u(2.0F, 6.0F)))));
        }

        private static TableDef cinquiFrontFood() {
            return table("chests/inqui_front_food",
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("minecraft:glow_berries", 30, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:fluore_berries", 10, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:sweet_berries", 50, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(4.0F, 7.0F), null, cond(),
                            entry("caerula_arbor:instant_noodle", 30, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:wheat", 60, cond(), setCount(u(3.0F, 8.0F))),
                            entry("minecraft:potato", 50, cond(), setCount(u(2.0F, 6.0F)))),
                    pool(u(4.0F, 7.0F), null, cond(),
                            entry("minecraft:apple", 30, cond(), setCount(u(3.0F, 5.0F))),
                            entry("minecraft:golden_apple", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:beetroot", 40, cond(), setCount(u(3.0F, 5.0F))),
                            entry("minecraft:carrot", 40, cond(), setCount(u(3.0F, 6.0F))),
                            entry("minecraft:cod", 35, cond(), setCount(u(2.0F, 3.0F))),
                            entry("minecraft:beef", 35, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(u(3.0F, 5.0F), null, cond(),
                            entry("minecraft:wheat_seeds", 25, cond(), setCount(u(4.0F, 9.0F))),
                            entry("minecraft:pumpkin_seeds", 15, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:beetroot_seeds", 25, cond(), setCount(u(3.0F, 6.0F)))));
        }

        private static TableDef cinquiFrontSpec() {
            return table("chests/inqui_front_spec",
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:amethyst_shard", 35, cond(), setCount(u(3.0F, 4.0F))),
                            entry("minecraft:spyglass", 25, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:interphone", 25, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:obisidian_ball", 24, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:lantern_judgement", 3, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:trail_cream", 8, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:painting", 32, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(u(1.0F, 3.0F), null, cond(),
                            entry("caerula_arbor:trailed_iron_sword", 50, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(1.0F, 2.0F), true)),
                            entry("minecraft:iron_sword", 50, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(1.0F, 2.0F), true)),
                            entry("minecraft:crossbow", 35, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(0.0F, 1.0F), true))));
        }

        private static TableDef cinquiFrontUnder() {
            return table("chests/inqui_front_under",
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("caerula_arbor:ocean_eye", 15, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:whirl_eye", 2, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_fibre", 35, cond(), setCount(u(4.0F, 6.0F))),
                            entry("caerula_arbor:fibre_block", 25, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(u(4.0F, 7.0F), null, cond(),
                            entry("caerula_arbor:ocean_crystal", 33, cond(), setCount(u(3.0F, 6.0F))),
                            entry("caerula_arbor:ocean_arrow", 22, cond(), setCount(u(2.0F, 12.0F))),
                            entry("caerula_arbor:white_chitin", 22, cond(), setCount(u(3.0F, 8.0F))),
                            entry("caerula_arbor:shell_of_stonecutter", 11, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef cinquiHomeChurch() {
            return table("chests/inqui_home_church",
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("minecraft:amethyst_shard", 10, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:purple_dye", 30, cond(), setCount(u(2.0F, 3.0F))),
                            entry("minecraft:purple_candle", 30, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("caerula_arbor:redstone_ingot", 99, cond(), setCount(u(2.0F, 4.0F)))));
        }

        private static TableDef cinquiHomeCommon() {
            return table("chests/inqui_home_common",
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("minecraft:amethyst_shard", 25, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:pitcher_plant", 25, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:allium", 45, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:pink_tulip", 45, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(5.0F, 8.0F), null, cond(),
                            entry("minecraft:paper", 33, cond(), setCount(u(3.0F, 7.0F))),
                            entry("minecraft:glass_bottle", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:oceanglass_cup", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:instant_noodle", 22, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:canned_noodle", 11, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:book", 33, cond(), setCount(u(2.0F, 5.0F)))));
        }

        private static TableDef cinquiHomeStore() {
            return table("chests/inqui_home_store",
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("minecraft:iron_nugget", 50, cond(), setCount(u(4.0F, 9.0F))),
                            entry("minecraft:iron_ingot", 33, cond(), setCount(u(3.0F, 5.0F))),
                            entry("caerula_arbor:treaty_iron", 15, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:raw_iron", 33, cond(), setCount(u(3.0F, 5.0F)))),
                    pool(u(2.0F, 3.0F), null, cond(),
                            entry("minecraft:gold_nugget", 50, cond(), setCount(u(5.0F, 12.0F))),
                            entry("minecraft:gold_ingot", 33, cond(), setCount(u(4.0F, 6.0F))),
                            entry("caerula_arbor:treaty_gold", 15, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:raw_gold", 33, cond(), setCount(u(3.0F, 6.0F))),
                            entry("caerula_arbor:voyage_of_gold", 5, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(5.0F, 8.0F), null, cond(),
                            entry("minecraft:cookie", 33, cond(), setCount(u(3.0F, 6.0F))),
                            entry("minecraft:cake", 11, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:carrot", 44, cond(), setCount(u(3.0F, 8.0F))),
                            entry("minecraft:wheat", 44, cond(), setCount(u(4.0F, 8.0F))),
                            entry("minecraft:potato", 44, cond(), setCount(u(4.0F, 8.0F)))));
        }

        private static TableDef clabAward() {
            return table("chests/lab_award",
                    pool(u(4.0F, 6.0F), null, cond(),
                            entry("caerula_arbor:complex_chitin", 4, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:phloem_block", 33, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:redstoneiris_seeding", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:cooked_peduncle", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:caramel_cake_piece", 22, cond(), setCount(u(3.0F, 6.0F))),
                            entry("caerula_arbor:trail_cake_piece", 33, cond(), setCount(u(5.0F, 9.0F))),
                            entry("caerula_arbor:ocean_chitin", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:ocean_crystal", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:berry_can", 22, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef clabBarrel() {
            return table("chests/lab_barrel",
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("caerula_arbor:ocean_trim_template", 3, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_eye", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_cutin", 33, cond(), setCount(u(4.0F, 5.0F))),
                            entry("caerula_arbor:ocean_crystal", 33, cond(), setCount(u(4.0F, 5.0F))),
                            entry("caerula_arbor:ocean_fibre", 33, cond(), setCount(u(4.0F, 5.0F))),
                            entry("caerula_arbor:caffeine", 22, cond(), setCount(u(2.0F, 3.0F)))));
        }

        private static TableDef clabBedroom() {
            return table("chests/lab_bedroom",
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("minecraft:gold_ingot", 1, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:sticky_piston", 1, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:redstone", 1, cond(), setCount(u(4.0F, 6.0F))),
                            entry("minecraft:blaze_powder", 1, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(2.0F, 3.0F), null, cond(),
                            entry("minecraft:cooked_porkchop", 33, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:sugar", 33, cond(), setCount(u(1.0F, 4.0F))),
                            entry("minecraft:wheat", 33, cond(), setCount(u(1.0F, 6.0F)))));
        }

        private static TableDef clabBossHallway() {
            return table("chests/lab_boss_hallway",
                    pool(u(3.0F, 5.0F), null, cond(),
                            entry("minecraft:amethyst_cluster", 66, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:pufferfish", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:diamond", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:golden_apple", 33, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef clabClassroom() {
            return table("chests/lab_classroom",
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:ocean_cell", 33, cond(), setCount(u(4.0F, 12.0F))),
                            entry("caerula_arbor:broken_cell_cluster", 33, cond(), setCount(u(3.0F, 4.0F)))));
        }

        private static TableDef clabTreasure() {
            return table("chests/lab_treasure",
                    pool(u(4.0F, 9.0F), null, cond(),
                            entry("minecraft:iron_ingot", 44, cond(), setCount(u(32.0F, 32.0F))),
                            entry("caerula_arbor:trail_shard", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:ancient_debris", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:echo_shard", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:diamond", 44, cond(), setCount(u(3.0F, 9.0F))),
                            entry("minecraft:gold_ingot", 44, cond(), setCount(u(8.0F, 16.0F))),
                            entry("minecraft:experience_bottle", 44, cond(), setCount(u(36.0F, 48.0F)))));
        }

        private static TableDef clighthouseBarrel() {
            return table("chests/lighthouse_barrel",
                    pool(u(3.0F, 6.0F), null, cond(),
                            entry("minecraft:iron_ingot", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("minecraft:potato", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:cooked_mor", 33, cond(), setCount(u(2.0F, 4.0F)))));
        }

        private static TableDef clighthouseBed() {
            return table("chests/lighthouse_bed",
                    pool(u(3.0F, 5.0F), null, cond(),
                            entry("caerula_arbor:ocean_fibre", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:ocean_cutin", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:ocean_phloem", 33, cond(), setCount(u(3.0F, 4.0F))),
                            entry("caerula_arbor:ocean_chitin", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_arrow", 22, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:fluore_berries", 22, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:trailed_iron_sword", 22, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef cmuseumLoot() {
            return table("chests/museum_loot",
                    pool(u(4.0F, 12.0F), null, cond(),
                            entry("minecraft:iron_ingot", 50, cond(), setCount(u(8.0F, 12.0F))),
                            entry("minecraft:gold_ingot", 40, cond(), setCount(u(5.0F, 9.0F))),
                            entry("minecraft:leather", 30, cond(), setCount(u(6.0F, 11.0F))),
                            entry("caerula_arbor:enchanted_trail_golden_apple", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:trail_golden_apple", 30, cond(), setCount(u(1.0F, 3.0F)))));
        }

        private static TableDef coddChitinumber() {
            return table("chests/odd_chitin",
                    pool(u(3.0F, 6.0F), null, cond(),
                            entry("caerula_arbor:ocean_chitin", 50, cond(), setCount(u(6.0F, 16.0F))),
                            entry("caerula_arbor:chitin_block", 11, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:white_chitin", 22, cond(), setCount(u(2.0F, 9.0F))),
                            entry("minecraft:iron_ingot", 22, cond(), setCount(u(1.0F, 3.0F)))),
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("caerula_arbor:ocean_phloem", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:ocean_crystal", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:ocean_eye", 11, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:fermented_ocean_eye", 8, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_cutin", 33, cond(), setCount(u(3.0F, 6.0F))),
                            entry("caerula_arbor:ocean_peduncle", 33, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:ocean_fibre", 33, cond(), setCount(u(3.0F, 7.0F))),
                            entry("minecraft:bone_meal", 33, cond(), setCount(u(1.0F, 4.0F)))),
                    pool(u(2.0F, 6.0F), null, cond(),
                            entry("minecraft:oak_boat", 11, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:birch_boat", 11, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:cherry_boat", 11, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:repeater", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:netherite_scrap", 2, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:scute", 22, cond(), setCount(u(3.0F, 6.0F))),
                            entry("minecraft:bone", 22, cond(), setCount(u(1.0F, 3.0F)))));
        }

        private static TableDef coddCol() {
            return table("chests/odd_col",
                    pool(u(4.0F, 7.0F), null, cond(),
                            entry("minecraft:iron_ingot", 33, cond(), setCount(u(3.0F, 8.0F))),
                            entry("minecraft:gold_ingot", 33, cond(), setCount(u(3.0F, 6.0F))),
                            entry("minecraft:diamond", 16, cond(), setCount(u(2.0F, 3.0F))),
                            entry("minecraft:iron_block", 12, cond(), setCount(u(2.0F, 3.0F))),
                            entry("minecraft:gold_block", 12, cond(), setCount(u(2.0F, 2.0F)))),
                    pool(u(3.0F, 6.0F), null, cond(),
                            entry("minecraft:lapis_lazuli", 33, cond(), setCount(u(4.0F, 8.0F))),
                            entry("minecraft:lapis_block", 22, cond(), setCount(u(2.0F, 5.0F))),
                            entry("caerula_arbor:caramel_cake_piece", 22, cond(), setCount(u(3.0F, 7.0F))),
                            entry("caerula_arbor:caramel_cake", 11, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:emerald", 22, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:emerald_block", 11, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef coddReaperroom() {
            return table("chests/odd_reaperroom",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:odd_flute", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:copper_ingot", 66, cond(), setCount(u(4.0F, 8.0F)))),
                    pool(u(4.0F, 7.0F), null, cond(),
                            entry("minecraft:diamond", 33, cond(), setCount(u(2.0F, 5.0F))),
                            entry("minecraft:iron_ingot", 44, cond(), setCount(u(3.0F, 7.0F))),
                            entry("caerula_arbor:trail_golden_apple", 44, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:golden_apple", 44, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef coddToilet() {
            return table("chests/odd_toilet",
                    pool(u(3.0F, 6.0F), null, cond(),
                            entry("minecraft:gold_ingot", 333, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:raw_gold", 33, cond(), setCount(u(2.0F, 4.0F)))),
                    pool(u(2.0F, 5.0F), null, cond(),
                            entry("minecraft:lantern", 44, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:oak_planks", 33, cond(), setCount(u(3.0F, 6.0F))),
                            entry("minecraft:oak_log", 33, cond(), setCount(u(3.0F, 9.0F)))));
        }

        private static TableDef cpalaceBrew() {
            return table("chests/palace_brew",
                    pool(u(4.0F, 7.0F), null, cond(),
                            entry("caerula_arbor:fermented_ocean_eye", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:spider_eye", 44, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:fermented_spider_eye", 44, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:blaze_powder", 44, cond(), setCount(u(4.0F, 9.0F))),
                            entry("minecraft:golden_apple", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:redstone", 44, cond(), setCount(u(4.0F, 11.0F)))));
        }

        private static TableDef cpalaceFood() {
            return table("chests/palace_food",
                    pool(u(6.0F, 8.0F), null, cond(),
                            entry("minecraft:potato", 23, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:wheat", 32, cond(), setCount(u(3.0F, 4.0F))),
                            entry("minecraft:carrot", 23, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:glow_berries", 12, cond(), setCount(u(3.0F, 4.0F))),
                            entry("minecraft:pumpkin_pie", 12, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:cod", 23, cond(), setCount(u(2.0F, 5.0F)))),
                    pool(u(0.0F, 2.0F), null, cond(),
                            entry("minecraft:golden_apple", 50, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:golden_carrot", 25, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef cpalaceTreasure() {
            return table("chests/palace_treasure",
                    pool(u(2.0F, 3.0F), null, cond(),
                            entry("minecraft:totem_of_undying", 1, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:enchanted_golden_apple", 1, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(3.0F, 8.0F), null, cond(),
                            entry("minecraft:golden_apple", 45, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:baked_potato", 45, cond(), setCount(u(4.0F, 8.0F))),
                            entry("caerula_arbor:redstonium", 25, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef credstones() {
            return table("chests/redstones",
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("minecraft:repeater", 33, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:comparator", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:redstone", 33, cond(), setCount(u(31.0F, 31.0F))),
                            entry("minecraft:air", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:slime_ball", 22, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:oak_boat", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:dark_oak_boat", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:cherry_chest_boat", 22, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef csadChurchBarrel() {
            return table("chests/sad_church_barrel",
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:whirl_eye", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_fibre", 65, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(3.0F, 9.0F), null, cond(),
                            entry("caerula_arbor:saltsand", 45, cond(), setCount(u(4.0F, 8.0F))),
                            entry("minecraft:sand", 25, cond(), setCount(u(2.0F, 6.0F))),
                            entry("caerula_arbor:trail_plank", 25, cond(), setCount(u(2.0F, 4.0F)))),
                    pool(u(2.0F, 6.0F), null, cond(),
                            entry("caerula_arbor:obisidian_ball", 35, cond(), setCount(u(3.0F, 6.0F))),
                            entry("caerula_arbor:ocean_eye", 25, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:viviparous_lily", 15, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:heteropic_piece", 25, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:coral_feet", 10, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(3.0F, 5.0F), null, cond(),
                            entry("minecraft:redstone", 25, cond(), setCount(u(3.0F, 5.0F))),
                            entry("minecraft:glowstone_dust", 25, cond(), setCount(u(2.0F, 5.0F))),
                            entry("caerula_arbor:caffeine", 15, cond(), setCount(u(1.0F, 4.0F))),
                            entry("caerula_arbor:immunosuppressor", 5, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef cshipFood() {
            return table("chests/ship_food",
                    pool(u(2.0F, 3.0F), null, cond(),
                            entry("caerula_arbor:trail_apple", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:apple", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:golden_apple", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:trail_golden_apple", 11, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(4.0F, 6.0F), null, cond(),
                            entry("minecraft:potato", 33, cond(), setCount(u(3.0F, 6.0F))),
                            entry("minecraft:bread", 33, cond(), setCount(u(3.0F, 8.0F))),
                            entry("minecraft:pumpkin_pie", 33, cond(), setCount(u(2.0F, 5.0F))),
                            entry("caerula_arbor:trail_cake_piece", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:instant_noodle", 22, cond(), setCount(u(1.0F, 3.0F)))),
                    pool(u(6.0F, 12.0F), null, cond(),
                            entry("minecraft:salmon", 33, cond(), setCount(u(4.0F, 6.0F))),
                            entry("minecraft:tropical_fish", 22, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:pufferfish", 22, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:cod", 33, cond(), setCount(u(4.0F, 6.0F)))));
        }

        private static TableDef cshipHead() {
            return table("chests/ship_head",
                    pool(u(5.0F, 8.0F), null, cond(),
                            entry("minecraft:redstone", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:repeater", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:comparator", 33, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:gunpowder", 1, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:trail_powder", 1, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:tnt", 1, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(2.0F, 3.0F), null, cond(),
                            entry("caerula_arbor:treaty_empty", 11, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:paper", 44, cond(), setCount(u(3.0F, 5.0F))),
                            entry("minecraft:map", 22, cond(), setCount(u(2.0F, 3.0F)))));
        }

        private static TableDef cshipRock() {
            return table("chests/ship_rock",
                    pool(u(4.0F, 8.0F), null, cond(),
                            entry("minecraft:oak_log", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:spruce_log", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:jungle_log", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:mangrove_log", 33, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(u(6.0F, 9.0F), null, cond(),
                            entry("caerula_arbor:trail_plank", 33, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:trail_log", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:diorite", 33, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:trail_stone", 22, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:saltwind_sandstone", 33, cond(), setCount(u(1.0F, 3.0F)))));
        }

        private static TableDef cshipSeat() {
            return table("chests/ship_seat",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:whirl_eye", 1, cond(), setCount(u(1.0F, 3.0F)))),
                    pool(u(4.0F, 7.0F), null, cond(),
                            entry("caerula_arbor:ocean_chitin", 33, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:ocean_fibre", 33, cond(), setCount(u(3.0F, 5.0F))),
                            entry("caerula_arbor:fibre_block", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_eye", 11, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:diamond", 11, cond(), setCount(u(3.0F, 12.0F))),
                            entry("minecraft:gold_ingot", 16, cond(), setCount(u(8.0F, 32.0F)))));
        }

        private static TableDef cshipTreasure() {
            return table("chests/ship_treasure",
                    pool(u(4.0F, 6.0F), null, cond(),
                            entry("minecraft:iron_ingot", 33, cond(), setCount(u(3.0F, 6.0F))),
                            entry("caerula_arbor:treaty_iron", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:gold_ingot", 33, cond(), setCount(u(3.0F, 4.0F))),
                            entry("minecraft:diamond", 22, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:emerald", 22, cond(), setCount(u(2.0F, 3.0F))),
                            entry("minecraft:netherite_scrap", 5, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:heteropic_piece", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:amethyst_shard", 33, cond(), setCount(u(5.0F, 9.0F)))),
                    pool(u(3.0F, 6.0F), null, cond(),
                            entry("minecraft:iron_block", 33, cond(), setCount(u(2.0F, 5.0F))),
                            entry("minecraft:gold_block", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:treaty_gold", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:diamond_block", 12, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:netherite_ingot", 4, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:chitin_block", 16, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:emerald_block", 11, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:echo_shard", 12, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef cshipwreckMap() {
            return table("chests/shipwreck_map",
                    pool(u(0.0F, 1.0F), null, cond(),
                            entry("caerula_arbor:ocean_trim_template", 100, cond(), setCount(u(0.0F, 1.0F)))));
        }

        private static TableDef csinkWreckBarrel() {
            return table("chests/sink_wreck_barrel",
                    pool(u(3.0F, 7.0F), null, cond(),
                            entry("caerula_arbor:saltsand", 22, cond(), setCount(u(3.0F, 4.0F))),
                            entry("minecraft:bone", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:bone_shard", 22, cond(), setCount(u(3.0F, 6.0F))),
                            entry("minecraft:sand", 22, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(3.0F, 7.0F), null, cond(),
                            entry("caerula_arbor:deep_seagrass", 50, cond(), setCount(u(4.0F, 6.0F))),
                            entry("caerula_arbor:guardian_stare", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:prismarine_shard", 25, cond(), setCount(u(5.0F, 9.0F))),
                            entry("minecraft:prismarine_crystals", 25, cond(), setCount(u(2.0F, 6.0F))),
                            entry("caerula_arbor:whirl_eye", 2, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef cspawnBonusAppendix() {
            return table("chests/spawn_bonus_appendix",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:kettle", 99, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:instant_noodle", 99, cond(), setCount(u(2.0F, 4.0F)))));
        }

        private static TableDef csubmarineContents() {
            return table("chests/submarine_contents",
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("caerula_arbor:redstone_ingot", 33, cond(), setCount(u(8.0F, 16.0F))),
                            entry("minecraft:redstone", 33, cond(), setCount(u(12.0F, 24.0F))),
                            entry("minecraft:repeater", 22, cond(), setCount(u(8.0F, 10.0F)))),
                    pool(u(3.0F, 6.0F), null, cond(),
                            entry("caerula_arbor:enchanted_trail_golden_apple", 11, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:trail_golden_apple", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:sea_pickle", 44, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:heteropic_piece", 44, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:cooked_fakeegg", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:nethersea_pumpkin_pie", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:compass", 11, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:quartz", 44, cond(), setCount(u(4.0F, 8.0F))),
                            entry("caerula_arbor:ocean_trim_template", 22, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef csubmarineGene() {
            return table("chests/submarine_gene",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:hunter_gene", 99, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("minecraft:netherite_scrap", 50, cond(), setCount(u(1.0F, 4.0F))),
                            entry("caerula_arbor:mutagenisis_capsule", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:transform_cell", 7, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef ctideStationChest() {
            return table("chests/tide_station_chest",
                    pool(u(4.0F, 6.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 60, cond(), setCount(u(8.0F, 12.0F))),
                            entry("caerula_arbor:sea_trail_solid", 40, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(u(3.0F, 6.0F), null, cond(),
                            entry("minecraft:raw_gold", 25, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:raw_iron", 30, cond(), setCount(u(4.0F, 8.0F))),
                            entry("minecraft:raw_copper", 35, cond(), setCount(u(4.0F, 7.0F)))),
                    pool(number(2.0F), null, cond(),
                            entry("caerula_arbor:chitin_sword", 35, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:odd_flute", 10, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_chitin", 35, cond(), setCount(u(3.0F, 7.0F))),
                            entry("caerula_arbor:meat_can", 15, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:nautilus_shell", 25, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef ctideStationTop() {
            return table("chests/tide_station_top",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:trailed_netherite_sword", 100, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(1.0F, 2.0F), true))),
                    pool(u(4.0F, 8.0F), null, cond(),
                            entry("caerula_arbor:trail_cream", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:book", 15, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:string", 12, cond(), setCount(u(2.0F, 6.0F))),
                            entry("minecraft:paper", 18, cond(), setCount(u(3.0F, 8.0F)))));
        }

        private static TableDef ctownPossesion() {
            return table("chests/town_possesion",
                    pool(u(4.0F, 6.0F), number(1.0F), cond(),
                            entry("minecraft:dried_kelp", 25, cond(), setCount(u(4.0F, 5.0F))),
                            entry("minecraft:birch_sapling", 35, cond(), setCount(u(3.0F, 5.0F))),
                            entry("minecraft:string", 35, cond(), setCount(u(3.0F, 5.0F))),
                            entry("minecraft:emerald", 15, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(u(3.0F, 4.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:ocean_eye", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_phloem", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:ocean_cutin", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_crystal", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:bowl_seagrass", 22, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef cwatchtowerBomb() {
            return table("chests/watchtower_bomb",
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("caerula_arbor:bomb_trailer", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:gunpowder", 44, cond(), setCount(u(3.0F, 8.0F))),
                            entry("caerula_arbor:ocean_crystal", 22, cond(), setCount(u(1.0F, 3.0F)))));
        }
    }

    /**
     * 生成实体战利品表
     */
    public static final class EntityTables extends GeneratedLootTableProvider {
        /**
         * 创建实体战利品表子 provider
         */
        public EntityTables() {
            super(List.of(
                    eaccumulatorProkaryote(),
                    eapostleProkaryote(),
                    ebaselayerAbyssal(),
                    ebishopFish(),
                    echestFish(),
                    echiselerFish(),
                    echitinGolem(),
                    ecomplexChitinGolem(),
                    ecrackerAbyssal(),
                    ePocketSeaCreeper(),
                    edepositerProkaryote(),
                    eendspeaker(),
                    efeederProkaryote(),
                    efirstTeller(),
                    eflamarineStatue(),
                    efleeFish(),
                    efloaterProkaryote(),
                    eguideAbyssal(),
                    ehighmore(),
                    eisharmla(),
                    eizumikOffspring(),
                    eizumik(),
                    elastKnightAndHorse(),
                    elingeringPathshaper(),
                    emartus(),
                    emegaChest(),
                    enucleicMaleficent(),
                    eoceanizeRabbit(),
                    eoceanizedBrute(),
                    eoceanizedCow(),
                    eoceanizedEnderina(),
                    eoceanizedEndermanumber(),
                    eoceanizedEvoker(),
                    eoceanizedHorse(),
                    eoceanizedIllusioner(),
                    eoceanizedPig(),
                    eoceanizedPiglinumber(),
                    eoceanizedPillager(),
                    eoceanizedPolarBear(),
                    eoceanizedSheep(),
                    eoceanizedShulker(),
                    eoceanizedSpider(),
                    eoceanizedVindicator(),
                    eoceanizedWardenumber(),
                    eoceanizedWardenis(),
                    eoceanizedWither(),
                    eoceanziedWitch(),
                    epredatorAbyssal(),
                    epregnantFish(),
                    epunctureFish(),
                    ereaperFish(),
                    erouteShaper(),
                    escreamChestFish(),
                    esliderFish(),
                    espikeChest(),
                    esplasherAbyssal(),
                    ethirster(),
                    etideBishop(),
                    etideDeathrepeller(),
                    etidutantRockSpider(),
                    eumbrellaAbyssal()
            ));
        }

        private static TableDef eaccumulatorProkaryote() {
            return table("entities/accumulator_prokaryote",
                    pool(u(1.0F, 2.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:broken_ocean_cell", 60, cond(), setCount(u(3.0F, 4.0F))),
                            entry("caerula_arbor:ocean_cell", 40, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:prismarine_crystals", 20, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef eapostleProkaryote() {
            return table("entities/apostle_prokaryote",
                    pool(u(1.0F, 2.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:broken_ocean_cell", 55, cond(), setCount(u(18.0F, 21.0F))),
                            entry("caerula_arbor:broken_cell_cluster", 55, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:ocean_cell", 44, cond(), setCount(u(7.0F, 12.0F))),
                            entry("caerula_arbor:cell_cluster", 44, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:heteropic_piece", 33, cond(), setCount(u(2.0F, 5.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 100, cond(), setCount(u(0.0F, 7.0F))),
                            entry("minecraft:nautilus_shell", 50, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef ebaselayerAbyssal() {
            return table("entities/baselayer_abyssal",
                    pool(u(1.0F, 2.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:ocean_phloem", 50, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_chitin", 50, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(u(0.0F, 1.0F), null, cond(),
                            entry("minecraft:lapis_lazuli", 99, cond(), setCount(u(0.0F, 1.0F)))));
        }

        private static TableDef ebishopFish() {
            return table("entities/bishop_fish",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:ocean_eye", 100, cond(), setCount(u(2.0F, 2.0F)))),
                    pool(u(2.0F, 4.0F), u(0.0F, 2.0F), cond(),
                            entry("caerula_arbor:ocean_fibre", 66, cond(), setCount(u(16.0F, 48.0F))),
                            entry("caerula_arbor:ocean_phloem", 55, cond(), setCount(u(16.0F, 32.0F))),
                            entry("caerula_arbor:ocean_cutin", 44, cond(), setCount(u(8.0F, 16.0F))),
                            entry("caerula_arbor:coin_of_trade", 8, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:tube_coral_block", 22, cond(), setCount(u(3.0F, 4.0F))),
                            entry("caerula_arbor:ocean_peduncle", 44, cond(), setCount(u(6.0F, 12.0F))),
                            entry("caerula_arbor:elite_peduncle", 22, cond(), setCount(u(3.0F, 6.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:archive_sal_viento", 32, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_phloem", 48, cond(), setCount(u(2.0F, 4.0F)))));
        }

        private static TableDef echestFish() {
            return table("entities/chest_fish",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:redstone_ingot", 100, cond(), setCount(u(9.0F, 12.0F)))),
                    pool(u(2.0F, 3.0F), null, cond(),
                            entry("minecraft:emerald", 20, cond(), setCount(u(3.0F, 5.0F))),
                            entry("minecraft:diamond", 20, cond(), setCount(u(4.0F, 6.0F))),
                            entry("minecraft:gold_nugget", 30, cond(), setCount(u(16.0F, 24.0F))),
                            entry("minecraft:iron_nugget", 40, cond(), setCount(u(32.0F, 48.0F))),
                            entry("caerula_arbor:obisidian_ball", 30, cond(), setCount(u(3.0F, 6.0F)))));
        }

        private static TableDef echiselerFish() {
            return table("entities/chiseler_fish",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:shell_of_stonecutter", 25, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:claw", 45, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:bone_shard", 35, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef echitinGolem() {
            return table("entities/chitin_golem",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:ocean_chitin", 100, cond(), setCount(u(11.0F, 19.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 100, cond(), setCount(u(0.0F, 6.0F)))));
        }

        private static TableDef ecomplexChitinGolem() {
            return table("entities/complex_chitin_golem",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:complex_chitin", 1, cond(), setCount(u(3.0F, 6.0F)))),
                    pool(u(3.0F, 5.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 22, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:ocean_crystal", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_cutin", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_chitin", 33, cond(), setCount(u(1.0F, 3.0F)))));
        }

        private static TableDef ecrackerAbyssal() {
            return table("entities/cracker_abyssal",
                    pool(u(1.0F, 2.0F), u(0.0F, 2.0F), cond(),
                            entry("caerula_arbor:ocean_fibre", 55, cond(), setCount(u(3.0F, 5.0F))),
                            entry("caerula_arbor:ocean_crystal", 44, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:ocean_chitin", 44, cond(), setCount(u(2.0F, 2.0F)))),
                    pool(u(0.0F, 1.0F), null, cond(),
                            entry("minecraft:tube_coral", 50, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:sea_trail_mor", 30, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:netherite_scrap", 10, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef ePocketSeaCreeper() {
            return table("entities/pocket_sea_creeper",
                    pool(u(1.0F, 2.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:ocean_crystal", 32, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_cutin", 48, cond(), setCount(u(3.0F, 5.0F)))),
                    pool(u(0.0F, 1.0F), null, cond(),
                            entry("minecraft:gunpowder", 1, cond(), setCount(u(2.0F, 3.0F)))));
        }

        private static TableDef edepositerProkaryote() {
            return table("entities/depositer_prokaryote",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:heteropic_piece", 1, cond(), setCount(u(1.0F, 3.0F)))));
        }

        private static TableDef eendspeaker() {
            return table("entities/endspeaker",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:dictation_chapter", 100, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(8.0F, 12.0F), null, cond(),
                            entry("caerula_arbor:ocean_fibre", 33, cond(), setCount(u(3.0F, 6.0F))),
                            entry("minecraft:lapis_lazuli", 22, cond(), setCount(u(5.0F, 11.0F))),
                            entry("caerula_arbor:sea_trail_mor", 33, cond(), setCount(u(3.0F, 9.0F))),
                            entry("caerula_arbor:ocean_crystal", 22, cond(), setCount(u(3.0F, 4.0F))),
                            entry("caerula_arbor:ocean_cutin", 22, cond(), setCount(u(4.0F, 8.0F)))));
        }

        private static TableDef efeederProkaryote() {
            return table("entities/feeder_prokaryote",
                    pool(u(1.0F, 2.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:ocean_cell", 50, cond(), setCount(u(6.0F, 11.0F))),
                            entry("caerula_arbor:cell_cluster", 40, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:heteropic_piece", 30, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:ocean_cutin", 40, cond(), setCount(u(4.0F, 5.0F))),
                            entry("caerula_arbor:ocean_crystal", 15, cond(), setCount(u(2.0F, 3.0F)))));
        }

        private static TableDef efirstTeller() {
            return table("entities/first_teller",
                    pool(u(0.0F, 2.0F), null, cond(),
                            entry("minecraft:phantom_membrane", 45, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:allay_sculpture", 15, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:complex_chitin", 3, cond(), setCount(u(0.0F, 1.0F))),
                            entry("caerula_arbor:elite_peduncle", 8, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_phloem", 15, cond(), setCount(u(8.0F, 16.0F))),
                            entry("caerula_arbor:obisidian_ball", 15, cond(), setCount(u(3.0F, 7.0F)))),
                    pool(u(1.0F, 2.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:ocean_crystal", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:sea_trail_mor", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:ocean_fibre", 55, cond(), setCount(u(3.0F, 6.0F))),
                            entry("caerula_arbor:whirl_eye", 11, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef eflamarineStatue() {
            return table("entities/flamarine_statue",
                    pool(number(2.0F), null, cond(),
                            entry("caerula_arbor:trail_debris", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:ancient_debris", 15, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:netherite_upgrade_smithing_template", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:diamond", 45, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:gold_nugget", 75, cond(), setCount(u(6.0F, 9.0F))),
                            entry("minecraft:copper_ingot", 75, cond(), setCount(u(4.0F, 8.0F))),
                            entry("minecraft:obsidian", 50, cond(), setCount(u(3.0F, 8.0F)))));
        }

        private static TableDef efleeFish() {
            return table("entities/flee_fish",
                    pool(u(1.0F, 2.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:ocean_fibre", 44, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:ocean_phloem", 44, cond(), setCount(u(2.0F, 2.0F))),
                            entry("caerula_arbor:ocean_crystal", 33, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(0.0F, 1.0F), null, cond(),
                            entry("caerula_arbor:bone_shard", 99, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef efloaterProkaryote() {
            return table("entities/floater_prokaryote",
                    pool(u(1.0F, 2.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:ocean_cell", 50, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:broken_ocean_cell", 50, cond(), setCount(u(2.0F, 4.0F)))));
        }

        private static TableDef eguideAbyssal() {
            return table("entities/guide_abyssal",
                    pool(u(1.0F, 2.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:ocean_phloem", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:sea_trail_mor", 66, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(0.0F, 1.0F), null, cond(),
                            entry("minecraft:tube_coral_fan", 1, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_fibre", 1, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef ehighmore() {
            return table("entities/highmore",
                    pool(u(2.0F, 3.0F), u(1.0F, 2.0F), cond(),
                            entry("caerula_arbor:ocean_peduncle", 55, cond(), setCount(u(4.0F, 6.0F))),
                            entry("caerula_arbor:complex_chitin", 25, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_chitin", 35, cond(), setCount(u(9.0F, 12.0F))),
                            entry("caerula_arbor:ocean_crystal", 35, cond(), setCount(u(8.0F, 14.0F))),
                            entry("caerula_arbor:elite_peduncle", 35, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(1.0F, 2.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:coin_of_trade", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:whirl_eye", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:ochre_froglight", 33, cond(), setCount(u(5.0F, 8.0F))),
                            entry("caerula_arbor:caerula_heart", 9, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:highmore_scythe", 100, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef eisharmla() {
            return table("entities/isharmla",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:leviathan_animus", 100, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(4.0F, 7.0F), null, cond(),
                            entry("caerula_arbor:heteropic_piece", 15, cond(), setCount(u(3.0F, 8.0F))),
                            entry("minecraft:gold_nugget", 25, cond(), setCount(u(9.0F, 16.0F))),
                            entry("caerula_arbor:cell_cluster", 25, cond(), setCount(u(4.0F, 7.0F))),
                            entry("caerula_arbor:trail_powder", 15, cond(), setCount(u(2.0F, 5.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:record_isharmla", 1, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:tear_isharmla", 100, cond(), setCount(u(3.0F, 3.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:isharmla_scute", 100, cond(), setCount(u(19.0F, 25.0F)))));
        }

        private static TableDef eizumikOffspring() {
            return table("entities/izumik_offspring",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:colourfull_jelly", 100, cond(), setCount(u(3.0F, 8.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:broken_ocean_cell", 66, cond(), setCount(u(4.0F, 9.0F))),
                            entry("caerula_arbor:elite_peduncle", 33, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef eizumik() {
            return table("entities/izumik",
                    pool(u(4.0F, 9.0F), u(3.0F, 5.0F), cond(),
                            entry("caerula_arbor:ocean_cell", 33, cond(), setCount(u(8.0F, 12.0F))),
                            entry("caerula_arbor:cell_cluster", 33, cond(), setCount(u(2.0F, 5.0F))),
                            entry("caerula_arbor:immunosuppressor", 22, cond(), setCount(u(3.0F, 9.0F))),
                            entry("minecraft:prismarine_shard", 22, cond(), setCount(u(24.0F, 39.0F))),
                            entry("caerula_arbor:trail_shard", 11, cond(), setCount(u(8.0F, 16.0F))),
                            entry("caerula_arbor:redstonium", 11, cond(), setCount(u(8.0F, 16.0F))),
                            entry("caerula_arbor:elite_peduncle", 22, cond(), setCount(u(48.0F, 64.0F))),
                            entry("caerula_arbor:ocean_peduncle", 33, cond(), setCount(u(48.0F, 64.0F))),
                            entry("caerula_arbor:trail_powder", 33, cond(), setCount(u(7.0F, 16.0F))),
                            entry("minecraft:prismarine_crystals", 33, cond(), setCount(u(8.0F, 11.0F)))),
                    pool(u(4.0F, 7.0F), null, cond(),
                            entry("caerula_arbor:colourfull_jelly", 50, cond(), setCount(u(16.0F, 32.0F))),
                            entry("minecraft:glow_ink_sac", 50, cond(), setCount(u(9.0F, 15.0F))),
                            entry("caerula_arbor:heteropic_piece", 33, cond(), setCount(u(9.0F, 17.0F))),
                            entry("caerula_arbor:complex_chitin", 33, cond(), setCount(u(1.0F, 5.0F))),
                            entry("caerula_arbor:whirl_eye", 33, cond(), setCount(u(2.0F, 4.0F)))),
                    pool(u(2.0F, 3.0F), null, cond(),
                            entry("caerula_arbor:enchanted_trail_golden_apple", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:enchanted_golden_apple", 33, cond(), setCount(u(1.0F, 3.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:record_endospore", 100, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:mizuki_determination", 100, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef elastKnightAndHorse() {
            return table("entities/last_knight_and_horse",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:knight_corpse", 99, cond(), setCount(u(4.0F, 6.0F)))),
                    pool(u(5.0F, 7.0F), null, cond(),
                            entry("minecraft:iron_ingot", 33, cond(), setCount(u(3.0F, 7.0F))),
                            entry("caerula_arbor:ocean_crystal", 22, cond(), setCount(u(3.0F, 4.0F))),
                            entry("minecraft:reinforced_deepslate", 11, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:tide_hunet_template", 99, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef elingeringPathshaper() {
            return table("entities/lingering_pathshaper",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:path_inaugurator", 99, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("caerula_arbor:ocean_phloem", 33, cond(), setCount(u(5.0F, 12.0F))),
                            entry("caerula_arbor:ocean_fibre", 33, cond(), setCount(u(6.0F, 11.0F))),
                            entry("caerula_arbor:ocean_crystal", 33, cond(), setCount(u(4.0F, 5.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("minecraft:conduit", 2, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:prismarine_crystals", 50, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:sea_pickle", 40, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef emartus() {
            return table("entities/martus",
                    pool(u(3.0F, 6.0F), null, cond(),
                            entry("caerula_arbor:trail_powder_core", 15, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:sea_trail_mor", 33, cond(), setCount(u(6.0F, 16.0F))),
                            entry("caerula_arbor:ocean_ovary", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_phloem", 33, cond(), setCount(u(6.0F, 16.0F))),
                            entry("minecraft:diamond", 44, cond(), setCount(u(9.0F, 24.0F)))),
                    pool(u(3.0F, 5.0F), null, cond(),
                            entry("caerula_arbor:heteropic_piece", 66, cond(), setCount(u(10.0F, 18.0F))),
                            entry("caerula_arbor:heteropic_block", 33, cond(), setCount(u(2.0F, 4.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:cell_cluster", 100, cond(), setCount(u(3.0F, 3.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:record_path_ahead", 100, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:martus_book", 100, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:archive_of_martus", 48, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_cell", 32, cond(), setCount(u(3.0F, 6.0F)))));
        }

        private static TableDef emegaChest() {
            return table("entities/mega_chest",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:redstone_ingot", 100, cond(), setCount(u(22.0F, 36.0F)))),
                    pool(u(3.0F, 4.0F), null, cond(),
                            entry("minecraft:diamond", 30, cond(), setCount(u(5.0F, 9.0F))),
                            entry("minecraft:netherite_scrap", 20, cond(), setCount(u(3.0F, 5.0F))),
                            entry("minecraft:gold_ingot", 40, cond(), setCount(u(12.0F, 19.0F))),
                            entry("caerula_arbor:radiant_berries", 20, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:fluore_berries", 35, cond(), setCount(u(2.0F, 3.0F))),
                            entry("minecraft:emerald", 20, cond(), setCount(u(4.0F, 7.0F))),
                            entry("caerula_arbor:coin_of_trade", 20, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_chitin", 40, cond(), setCount(u(8.0F, 16.0F))),
                            entry("caerula_arbor:complex_chitin", 15, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:trail_shard", 3, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:ender_pearl", 30, cond(), setCount(u(4.0F, 6.0F))),
                            entry("caerula_arbor:whirl_eye", 15, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:ocean_peduncle", 75, cond(), setCount(u(1.0F, 4.0F))),
                            entry("caerula_arbor:elite_peduncle", 25, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef enucleicMaleficent() {
            return table("entities/nucleic_maleficent",
                    pool(u(2.0F, 3.0F), null, cond(),
                            entry("caerula_arbor:ocean_phloem", 33, cond(), setCount(u(3.0F, 6.0F))),
                            entry("caerula_arbor:cell_cluster", 44, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_cell", 44, cond(), setCount(u(3.0F, 6.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:deep_seagrass", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:heteropic_piece", 22, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:ocean_eye", 22, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef eoceanizeRabbit() {
            return table("entities/oceanize_rabbit",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:coral_feet", 50, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:air", 50, cond(), setCount(u(0.0F, 0.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:rabbit", 60, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:rabbit_hide", 40, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef eoceanizedBrute() {
            return table("entities/oceanized_brute",
                    pool(number(2.0F), u(1.0F, 2.0F), cond(),
                            entry("minecraft:gold_ingot", 44, cond(), setCount(u(2.0F, 5.0F))),
                            entry("minecraft:gilded_blackstone", 22, cond(), setCount(u(6.0F, 11.0F))),
                            entry("caerula_arbor:trail_golden_apple", 11, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:enchanted_trail_golden_apple", 5, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(2.0F, 4.0F), u(1.0F, 2.0F), cond(),
                            entry("caerula_arbor:ocean_peduncle", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:sea_trail_mor", 33, cond(), setCount(u(3.0F, 5.0F))),
                            entry("caerula_arbor:ocean_chitin", 33, cond(), setCount(u(2.0F, 6.0F))),
                            entry("minecraft:brain_coral", 22, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:book", 56, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:piglin_diary", 44, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef eoceanizedCow() {
            return table("entities/oceanized_cow",
                    pool(u(1.0F, 2.0F), number(1.0F), cond(),
                            entry("caerula_arbor:ocean_phloem", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("minecraft:beef", 33, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 1, cond(), setCount(u(0.0F, 2.0F)))));
        }

        private static TableDef eoceanizedEnderina() {
            return table("entities/oceanized_enderina",
                    pool(u(2.0F, 3.0F), null, cond(),
                            entry("caerula_arbor:moist_crystal_item", 25, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:end_crystal", 50, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(u(4.0F, 6.0F), null, cond(),
                            entry("caerula_arbor:dragon_brand", 25, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:obisidian_ball", 15, cond(), setCount(u(3.0F, 6.0F))),
                            entry("caerula_arbor:ocean_crystal", 25, cond(), setCount(u(5.0F, 12.0F))),
                            entry("caerula_arbor:trail_powder", 25, cond(), setCount(u(2.0F, 4.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:coin_of_trade", 100, cond(), setCount(u(1.0F, 3.0F)))));
        }

        private static TableDef eoceanizedEndermanumber() {
            return table("entities/oceanized_enderman",
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:ender_pearl", 66, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:ocean_eye", 33, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:ocean_fibre", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:ocean_phloem", 33, cond(), setCount(u(2.0F, 4.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:water_logged_pearl", 100, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef eoceanizedEvoker() {
            return table("entities/oceanized_evoker",
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:totem_of_undying", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:emerald", 55, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("caerula_arbor:heteropic_piece", 55, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:broken_ocean_cell", 44, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:sea_trail_mor", 44, cond(), setCount(u(2.0F, 4.0F)))));
        }

        private static TableDef eoceanizedHorse() {
            return table("entities/oceanized_horse",
                    pool(u(2.0F, 3.0F), u(1.0F, 2.0F), cond(),
                            entry("caerula_arbor:ocean_crystal", 33, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:ocean_phloem", 44, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:leather", 44, cond(), setCount(u(1.0F, 3.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 64, cond(), setCount(u(0.0F, 4.0F))),
                            entry("caerula_arbor:trail_apple", 32, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef eoceanizedIllusioner() {
            return table("entities/oceanized_illusioner",
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:ocean_chitin", 50, cond(), setCount(u(2.0F, 6.0F))),
                            entry("caerula_arbor:ocean_phloem", 50, cond(), setCount(u(3.0F, 8.0F)))),
                    pool(u(2.0F, 4.0F), null, cond(),
                            entry("caerula_arbor:fluore_berries", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:glow_berries", 67, cond(), setCount(u(2.0F, 4.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("minecraft:emerald", 50, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:obisidian_ball", 25, cond(), setCount(u(3.0F, 7.0F))),
                            entry("caerula_arbor:whirl_eye", 25, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:archive_of_raider", 100, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef eoceanizedPig() {
            return table("entities/oceanized_pig",
                    pool(u(1.0F, 2.0F), number(1.0F), cond(),
                            entry("minecraft:porkchop", 65, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:sea_trail_mor", 45, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:ocean_peduncle", 1, cond(), setCount(u(0.0F, 1.0F)))));
        }

        private static TableDef eoceanizedPiglinumber() {
            return table("entities/oceanized_piglin",
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("minecraft:gold_ingot", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:gold_nugget", 66, cond(), setCount(u(3.0F, 6.0F)))),
                    pool(u(2.0F, 3.0F), number(1.0F), cond(),
                            entry("caerula_arbor:ocean_peduncle", 44, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:sea_trail_mor", 33, cond(), setCount(u(2.0F, 3.0F))),
                            entry("minecraft:bubble_coral_fan", 22, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef eoceanizedPillager() {
            return table("entities/oceanized_pillager",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:ocean_peduncle", 50, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:ocean_arrow", 50, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef eoceanizedPolarBear() {
            return table("entities/oceanized_polar_bear",
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:salmon", 4, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:tropical_fish", 1, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:cod", 4, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:collector_meat", 99, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef eoceanizedSheep() {
            return table("entities/oceanized_sheep",
                    pool(u(1.0F, 2.0F), number(1.0F), cond(),
                            entry("minecraft:mutton", 1, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:ocean_peduncle", 1, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 1, cond(), setCount(u(0.0F, 3.0F))),
                            entry("minecraft:string", 1, cond(), setCount(u(1.0F, 4.0F)))));
        }

        private static TableDef eoceanizedShulker() {
            return table("entities/oceanized_shulker",
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:shulker_shell", 25, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_chitin", 15, cond(), setCount(u(2.0F, 6.0F))),
                            entry("caerula_arbor:white_chitin", 10, cond(), setCount(u(2.0F, 6.0F)))));
        }

        private static TableDef eoceanizedSpider() {
            return table("entities/oceanized_spider",
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("minecraft:spider_eye", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:dead_horn_coral_fan", 44, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(2.0F, 3.0F), null, cond(),
                            entry("minecraft:string", 50, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:sea_trail_mor", 50, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef eoceanizedVindicator() {
            return table("entities/oceanized_vindicator",
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:emerald", 1, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:ocean_peduncle", 50, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:sea_trail_mor", 30, cond(), setCount(u(2.0F, 5.0F)))));
        }

        private static TableDef eoceanizedWardenumber() {
            return table("entities/oceanized_warden",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:moist_echo_shard", 100, cond(), setCount(u(1.0F, 3.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("minecraft:sculk_catalyst", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:sculk_shrieker", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:sculk", 33, cond(), setCount(u(1.0F, 3.0F)))),
                    pool(u(2.0F, 3.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 44, cond(), setCount(u(4.0F, 6.0F))),
                            entry("caerula_arbor:ocean_ovary", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:sea_trail_solid", 33, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef eoceanizedWardenis() {
            return table("entities/oceanized_wardenis",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:moist_echo_shard", 1, cond(), setCount(u(1.0F, 3.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("minecraft:sculk_catalyst", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:sculk_shrieker", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:sculk", 33, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:calibrated_sculk_sensor", 22, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(2.0F, 3.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 33, cond(), setCount(u(4.0F, 6.0F))),
                            entry("caerula_arbor:ocean_ovary", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:sea_trail_solid", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:sea_trail_burnt", 22, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef eoceanizedWither() {
            return table("entities/oceanized_wither",
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:nether_star", 100, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(6.0F, 12.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 33, cond(), setCount(u(2.0F, 5.0F))),
                            entry("caerula_arbor:ocean_crystal", 33, cond(), setCount(u(4.0F, 9.0F))),
                            entry("minecraft:soul_sand", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:wither_skeleton_skull", 22, cond(), setCount(u(1.0F, 3.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:heart_of_the_sea", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_eye", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_chitin", 33, cond(), setCount(u(2.0F, 4.0F)))));
        }

        private static TableDef eoceanziedWitch() {
            return table("entities/oceanzied_witch",
                    pool(u(3.0F, 6.0F), u(1.0F, 2.0F), cond(),
                            entry("minecraft:redstone", 33, cond(), setCount(u(3.0F, 7.0F))),
                            entry("minecraft:glowstone_dust", 33, cond(), setCount(u(4.0F, 9.0F))),
                            entry("caerula_arbor:trail_apple", 33, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:trail_mushroom", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:fermented_spider_eye", 22, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:glistering_melon_slice", 12, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(u(2.0F, 3.0F), number(1.0F), cond(),
                            entry("minecraft:glass_bottle", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:cutin_stick", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_peduncle", 33, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef epredatorAbyssal() {
            return table("entities/predator_abyssal",
                    pool(u(1.0F, 2.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:ocean_phloem", 40, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_chitin", 60, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(u(0.0F, 1.0F), null, cond(),
                            entry("minecraft:tube_coral_fan", 1, cond(), setCount(u(0.0F, 1.0F)))));
        }

        private static TableDef epregnantFish() {
            return table("entities/pregnant_fish",
                    pool(u(1.0F, 2.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:ocean_fibre", 66, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_phloem", 44, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:real_egg", 22, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef epunctureFish() {
            return table("entities/puncture_fish",
                    pool(u(1.0F, 2.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:ocean_fibre", 65, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:ocean_phloem", 45, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:bone_shard", 25, cond(), setCount(u(0.0F, 3.0F))),
                            entry("caerula_arbor:ocean_peduncle", 35, cond(), setCount(u(0.0F, 2.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:ocean_crystal", 1, cond(), setCount(u(0.0F, 2.0F)))));
        }

        private static TableDef ereaperFish() {
            return table("entities/reaper_fish",
                    pool(u(1.0F, 2.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:ocean_fibre", 65, cond(), setCount(u(0.0F, 2.0F))),
                            entry("caerula_arbor:ocean_phloem", 35, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:bone_shard", 25, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_peduncle", 35, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:ocean_eye", 100, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef erouteShaper() {
            return table("entities/route_shaper",
                    pool(u(1.0F, 3.0F), u(0.0F, 2.0F), cond(),
                            entry("caerula_arbor:ocean_phloem", 55, cond(), setCount(u(5.0F, 8.0F))),
                            entry("caerula_arbor:ocean_fibre", 55, cond(), setCount(u(4.0F, 9.0F))),
                            entry("caerula_arbor:ocean_crystal", 33, cond(), setCount(u(2.0F, 3.0F)))),
                    pool(u(0.0F, 1.0F), null, cond(),
                            entry("minecraft:heart_of_the_sea", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:prismarine_crystals", 50, cond(), setCount(u(1.0F, 3.0F))),
                            entry("minecraft:tube_coral", 40, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:path_inaugurator", 100, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef escreamChestFish() {
            return table("entities/scream_chest_fish",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:elite_peduncle", 100, cond(), setCount(u(4.0F, 8.0F)))),
                    pool(u(2.0F, 3.0F), null, cond(),
                            entry("caerula_arbor:ocean_peduncle", 50, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:ocean_fibre", 25, cond(), setCount(u(3.0F, 5.0F))),
                            entry("caerula_arbor:sea_trail_mor", 25, cond(), setCount(u(3.0F, 6.0F))),
                            entry("caerula_arbor:white_chitin", 15, cond(), setCount(u(2.0F, 6.0F)))));
        }

        private static TableDef esliderFish() {
            return table("entities/slider_fish",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:ocean_phloem", 70, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_peduncle", 30, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef espikeChest() {
            return table("entities/spike_chest",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:redstone_ingot", 100, cond(), setCount(u(12.0F, 18.0F)))),
                    pool(u(1.0F, 5.0F), null, cond(),
                            entry("caerula_arbor:ocean_chitin", 45, cond(), setCount(u(3.0F, 8.0F))),
                            entry("minecraft:fire_coral_fan", 35, cond(), setCount(u(1.0F, 1.0F))),
                            entry("minecraft:nether_wart", 35, cond(), setCount(u(3.0F, 6.0F))),
                            entry("minecraft:redstone", 45, cond(), setCount(u(5.0F, 9.0F))),
                            entry("minecraft:spider_eye", 30, cond(), setCount(u(2.0F, 4.0F))),
                            entry("caerula_arbor:transform_cell", 5, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef esplasherAbyssal() {
            return table("entities/splasher_abyssal",
                    pool(u(1.0F, 2.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:ocean_chitin", 55, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:ocean_fibre", 44, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:bone_shard", 22, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:ocean_peduncle", 32, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(u(0.0F, 1.0F), null, cond(),
                            entry("caerula_arbor:sea_trail_mor", 0, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:ocean_phloem", 1, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef ethirster() {
            return table("entities/thirster",
                    pool(number(1.0F), null, cond(),
                            entry("minecraft:sponge", 100, cond(), setCount(u(3.0F, 9.0F)))),
                    pool(u(3.0F, 6.0F), null, cond(),
                            entry("caerula_arbor:ocean_chitin", 50, cond(), setCount(u(8.0F, 12.0F))),
                            entry("minecraft:tube_coral_block", 25, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:tube_coral", 25, cond(), setCount(u(2.0F, 4.0F))),
                            entry("minecraft:tube_coral_fan", 25, cond(), setCount(u(2.0F, 6.0F))),
                            entry("caerula_arbor:ocean_fibre", 15, cond(), setCount(u(5.0F, 9.0F))),
                            entry("caerula_arbor:ocean_phloem", 15, cond(), setCount(u(5.0F, 9.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:nervous_regeneration", 100, cond(), setCount(u(3.0F, 5.0F)))));
        }

        private static TableDef etideBishop() {
            return table("entities/tide_bishop",
                    pool(number(2.0F), u(1.0F, 2.0F), cond(),
                            entry("minecraft:leather", 75, cond(), setCount(u(4.0F, 11.0F))),
                            entry("minecraft:gold_nugget", 25, cond(), setCount(u(12.0F, 22.0F))),
                            entry("caerula_arbor:ocean_chitin", 45, cond(), setCount(u(9.0F, 13.0F))),
                            entry("caerula_arbor:trailed_stone_sword", 15, cond(), setCount(u(1.0F, 1.0F)), enchantWithLevels(u(1.0F, 3.0F), true))),
                    pool(number(1.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:ocean_peduncle", 65, cond(), setCount(u(6.0F, 8.0F))),
                            entry("caerula_arbor:elite_peduncle", 35, cond(), setCount(u(3.0F, 4.0F))),
                            entry("caerula_arbor:base_egg", 25, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:trail_cream", 25, cond(), setCount(u(2.0F, 4.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:tide_wand", 100, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:archive_of_tidelink", 100, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef etideDeathrepeller() {
            return table("entities/tide_deathrepeller",
                    pool(number(2.0F), u(1.0F, 2.0F), cond(),
                            entry("caerula_arbor:ocean_phloem", 35, cond(), setCount(u(9.0F, 11.0F))),
                            entry("caerula_arbor:ocean_crystal", 35, cond(), setCount(u(12.0F, 17.0F))),
                            entry("caerula_arbor:ocean_fibre", 30, cond(), setCount(u(18.0F, 22.0F))),
                            entry("caerula_arbor:ocean_chitin", 45, cond(), setCount(u(8.0F, 12.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:broken_cell_cluster", 32, cond(), setCount(u(1.0F, 3.0F))),
                            entry("caerula_arbor:broken_ocean_cell", 48, cond(), setCount(u(7.0F, 11.0F))),
                            entry("minecraft:prismarine_shard", 24, cond(), setCount(u(8.0F, 14.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:repeller_shell", 1, cond(), setCount(u(6.0F, 9.0F)))));
        }

        private static TableDef etidutantRockSpider() {
            return table("entities/tidutant_rock_spider",
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:ocean_chitin", 33, cond(), setCount(u(3.0F, 6.0F))),
                            entry("caerula_arbor:sea_trail_mor", 33, cond(), setCount(u(2.0F, 5.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("minecraft:spider_eye", 33, cond(), setCount(u(1.0F, 2.0F))),
                            entry("minecraft:brain_coral_block", 55, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef eumbrellaAbyssal() {
            return table("entities/umbrella_abyssal",
                    pool(u(1.0F, 2.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:ocean_phloem", 66, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:sea_trail_mor", 44, cond(), setCount(u(1.0F, 2.0F))),
                            entry("caerula_arbor:ocean_fibre", 22, cond(), setCount(u(1.0F, 2.0F)))),
                    pool(u(0.0F, 1.0F), null, cond(),
                            entry("minecraft:tube_coral", 1, cond(), setCount(u(1.0F, 1.0F)))));
        }
    }

    /**
     * 生成 gameplay 战利品表
     */
    public static final class GameplayTables extends GeneratedLootTableProvider {
        /**
         * 创建 gameplay 战利品表子 provider
         */
        public GameplayTables() {
            super(List.of(
                    ghighmoreRelics(),
                    gmereGeenSample(),
                    grelicBishop(),
                    grelicIsharmla(),
                    grelicIzumik(),
                    grelicRoute(),
                    grelicTidebi(),
                    gtableOfHands(),
                    gterminalRelics(),
                    gtriggerCrisisTable()
            ));
        }

        private static TableDef ghighmoreRelics() {
            return table("gameplay/highmore_relics",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:redstone_ingot", 100, cond(), setCount(u(16.0F, 27.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:toponym_textology", 40, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:piglin_diary", 30, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:survivor_contract", 10, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:redstone_iris_flower", 30, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:relic_cursed_glowbody", 10, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_of_barren", 10, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:relic_crown", 10, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:kettle", 20, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef gmereGeenSample() {
            return table("gameplay/mere_geen_sample",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:gene_sample_superb", 100, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef grelicBishop() {
            return table("gameplay/relic_bishop",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:redstone_ingot", 1, cond(), setCount(u(8.0F, 16.0F)), oreBonus())),
                    pool(number(1.0F), u(0.0F, 1.0F), cond(),
                            entry("caerula_arbor:redstone_iris_flower", 10, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:kings_spear", 4, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:solo_music_box", 10, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:guardian_stare", 10, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:kings_crystal", 6, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:piglin_diary", 16, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:coffee_candy", 14, cond(), setCount(u(2.0F, 3.0F))),
                            entry("caerula_arbor:caerula_heart", 1, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef grelicIsharmla() {
            return table("gameplay/relic_isharmla",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:relic_crown", 2, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:kings_armour", 10, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:archfiends_artifact", 2, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:chitin_knife", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:archfiends_flag", 10, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:archfiends_bed", 15, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:guardian_stare", 25, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:voyage_of_gold", 25, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:hand_of_thorns", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_of_strangle", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_of_fertiliy", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_of_speed", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_of_spotless", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_of_barren", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_of_firework", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_of_engrave", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_sword", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:redstone_iris_flower", 50, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:coin_of_trade", 30, cond(), setCount(u(2.0F, 4.0F)))),
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:redstone_ingot", 64, cond(), setCount(u(32.0F, 56.0F))),
                            entry("caerula_arbor:redstonium", 32, cond(), setCount(u(1.0F, 2.0F)))));
        }

        private static TableDef grelicIzumik() {
            return table("gameplay/relic_izumik",
                    pool(u(1.0F, 2.0F), null, cond(),
                            entry("caerula_arbor:redstone_ingot", 64, cond(), setCount(u(35.0F, 68.0F))),
                            entry("caerula_arbor:redstonium", 36, cond(), setCount(u(3.0F, 5.0F)))),
                    pool(number(3.0F), null, cond(),
                            entry("caerula_arbor:chitin_knife", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:unripe_yearning", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:guardian_stare", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:archfiends_artifact", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:archfiends_flag", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:archfiends_bed", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:relic_crown", 22, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:kings_spear", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:kings_armour", 33, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:kettle", 44, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:caerula_heart", 8, cond(), setCount(u(1.0F, 1.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:coin_of_trade", 100, cond(), setCount(u(1.0F, 3.0F)))));
        }

        private static TableDef grelicRoute() {
            return table("gameplay/relic_route",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:redstone_ingot", 99, cond(), setCount(u(6.0F, 14.0F)), oreBonus())),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:voyage_of_gold", 20, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:kings_extension", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:aromatic_coffee", 15, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:score", 20, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:omni_key", 20, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:proof_of_longevity", 10, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_sword", 15, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:smelly_hemostatic", 10, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef grelicTidebi() {
            return table("gameplay/relic_tidebi",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:redstone_ingot", 100, cond(), setCount(u(5.0F, 9.0F)))),
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:relic_cursed_research", 5, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_of_firework", 30, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_of_engrave", 30, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:piglin_diary", 45, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:rainbow_candy", 55, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:piglin_diary", 45, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:allay_sculpture", 45, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:omni_key", 45, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef gtableOfHands() {
            return table("gameplay/table_of_hands",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:hand_of_thorns", 30, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_of_strangle", 20, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_of_fertiliy", 30, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_of_speed", 30, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_of_barren", 15, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_of_spotless", 20, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_of_engrave", 15, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:hand_sword", 20, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef gterminalRelics() {
            return table("gameplay/terminal_relics",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:relic_crown", 40, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:chitin_knife", 30, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:archfiends_artifact", 40, cond(), setCount(u(1.0F, 1.0F))),
                            entry("caerula_arbor:survivor_contract", 25, cond(), setCount(u(1.0F, 1.0F)))));
        }

        private static TableDef gtriggerCrisisTable() {
            return table("gameplay/trigger_crisis_table",
                    pool(number(1.0F), null, cond(),
                            entry("caerula_arbor:record_deepness", 100, cond(), setCount(u(1.0F, 1.0F)))));
        }
    }

    private record TableDef(String path, PoolDef[] pools) {
    }

    private record PoolDef(NumberDef rolls, NumberDef bonusRolls, CondDef[] conditions, EntryDef[] entries) {
    }

    private record EntryDef(String item, int weight, CondDef[] conditions, FuncDef[] functions) {
    }

    private record FuncDef(String type, NumberDef number, boolean treasure, CondDef[] conditions) {
    }

    private record CondDef(String type, String block, String property, String value, CondDef term) {
    }

    private record NumberDef(float min, float max, boolean uniform) {
    }
}
