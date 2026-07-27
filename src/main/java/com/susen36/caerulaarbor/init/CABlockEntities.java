package com.susen36.caerulaarbor.init;

import com.mojang.datafixers.types.Type;
import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.block.blockentity.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


/**
 * fuck IDEA
 * <p>
 * there are no @NotNull in {@link BlockEntityType.Builder#build(Type)} why cannot use {@code BlockEntityType.Builder.build(null);}
 */

@SuppressWarnings("DataFlowIssue")
public class CABlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CaerulaArborMod.MODID);

    private static DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> register(String registryname, DeferredHolder<Block, ? extends Block> block, BlockEntityType.BlockEntitySupplier<?> supplier) {
        return REGISTRY.register(
                registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));

    }

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TidewayCradleTileEntity>> TIDEWAY_CRADLE = REGISTRY.register(
            "tideway_cradle",
            () -> BlockEntityType.Builder.of(TidewayCradleTileEntity::new, CABlocks.TIDEWAY_CRADLE.get()).build(null));


    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChestmegaSpawnerTileEntity>> CHESTMEGA_SPAWNER = REGISTRY.register(
            "chestmega_spawner",
            () -> BlockEntityType.Builder.of(ChestmegaSpawnerTileEntity::new, CABlocks.CHESTMEGA_SPAWNER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ViviparousLilyTileEntity>> VIVIPAROUS_LILY = REGISTRY.register(
            "viviparous_lily",
            () -> BlockEntityType.Builder.of(ViviparousLilyTileEntity::new, CABlocks.VIVIPAROUS_LILY.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HugeLilyTileEntity>> HUGE_LILY = REGISTRY.register(
            "huge_lily", () -> BlockEntityType.Builder.of(HugeLilyTileEntity::new, CABlocks.HUGE_LILY.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HighmoreSpawnblockTileEntity>> HIGHMORE_SPAWNBLOCK = REGISTRY.register(
            "highmore_spawnblock",
            () -> BlockEntityType.Builder.of(HighmoreSpawnblockTileEntity::new, CABlocks.HIGHMORE_SPAWNBLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrisisTableTileEntity>> CRISIS_TABLE = REGISTRY.register(
            "crisis_table", () -> BlockEntityType.Builder.of(CrisisTableTileEntity::new, CABlocks.CRISIS_TABLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HighmoreSpawningBlockTileEntity>> HIGHMORE_SPAWNING_BLOCK = REGISTRY.register(
            "highmore_spawning_block",
            () -> BlockEntityType.Builder.of(HighmoreSpawningBlockTileEntity::new, CABlocks.HIGHMORE_SPAWNING_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MizukiStatueTileEntity>> MIZUKI_STATUE = REGISTRY.register(
            "mizuki_statue", () -> BlockEntityType.Builder.of(MizukiStatueTileEntity::new, CABlocks.MIZUKI_STATUE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PocketSeaDollTileEntity>> POCKET_SEA_DOLL = REGISTRY.register(
            "pocket_sea_doll",
            () -> BlockEntityType.Builder.of(PocketSeaDollTileEntity::new, CABlocks.POCKET_SEA_DOLL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SwarmcallerDollTileEntity>> SWARMCALLER_DOLL = REGISTRY.register(
            "swarmcaller_doll",
            () -> BlockEntityType.Builder.of(SwarmcallerDollTileEntity::new, CABlocks.SWARMCALLER_DOLL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StonecutterDollTileEntity>> STONECUTTER_DOLL = REGISTRY.register(
            "stonecutter_doll",
            () -> BlockEntityType.Builder.of(StonecutterDollTileEntity::new, CABlocks.STONECUTTER_DOLL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AbandonedSulptureTileEntity>> ABANDONED_SULPTURE = REGISTRY.register(
            "abandoned_sulpture",
            () -> BlockEntityType.Builder.of(AbandonedSulptureTileEntity::new, CABlocks.ABANDONED_SULPTURE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CentrifugerTileEntity>> CENTRIFUGER = REGISTRY.register(
            "centrifuger", () -> BlockEntityType.Builder.of(CentrifugerTileEntity::new, CABlocks.CENTRIFUGER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IllusionerBannerTileEntity>> ILLUSIONER_BANNER = REGISTRY.register(
            "illusioner_banner",
            () -> BlockEntityType.Builder.of(IllusionerBannerTileEntity::new, CABlocks.ILLUSIONER_BANNER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LivingArmorstandTileEntity>> LIVING_ARMORSTAND = REGISTRY.register(
            "living_armorstand",
            () -> BlockEntityType.Builder.of(LivingArmorstandTileEntity::new, CABlocks.LIVING_ARMORSTAND.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrailriteArmorstandTileEntity>> TRAILRITE_ARMORSTAND = REGISTRY.register(
            "trailrite_armorstand",
            () -> BlockEntityType.Builder.of(TrailriteArmorstandTileEntity::new, CABlocks.TRAILRITE_ARMORSTAND.get()).build(null));
}