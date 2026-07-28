package com.susen36.caerulaarbor.capability;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.capability.apoptosis.ApoptosisInjuryCapability;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.capability.sanity.SanityInjuryCapability;
import com.susen36.caerulaarbor.init.CABlockEntities;
import com.susen36.caerulaarbor.network.receive.SavedDataSyncMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = CaerulaArborMod.MODID)
public class CapabilityEventHandler {

    private CapabilityEventHandler() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void registerBlockCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.HUGE_LILY.get(),
                (blockEntity, side) -> side == null ? null : blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.CHESTMEGA_SPAWNER.get(),
                (blockEntity, side) -> side == null ? null : blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.CENTRIFUGER.get(),
                (blockEntity, side) -> side == null ? null : blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.ABANDONED_SULPTURE.get(),
                (blockEntity, side) -> side == null ? null : blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.CRISIS_TABLE.get(),
                (blockEntity, side) -> side == null ? null : blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.VIVIPAROUS_LILY.get(),
                (blockEntity, side) -> side == null ? null : blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.TRAILRITE_ARMORSTAND.get(),
                (blockEntity, side) -> side == null ? null : blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.TIDEWAY_CRADLE.get(),
                (blockEntity, side) -> side == null ? null : blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.SWARMCALLER_DOLL.get(),
                (blockEntity, side) -> side == null ? null : blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.STONECUTTER_DOLL.get(),
                (blockEntity, side) -> side == null ? null : blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.POCKET_SEA_DOLL.get(),
                (blockEntity, side) -> side == null ? null : blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.MIZUKI_STATUE.get(),
                (blockEntity, side) -> side == null ? null : blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.LIVING_ARMORSTAND.get(),
                (blockEntity, side) -> side == null ? null : blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.ILLUSIONER_BANNER.get(),
                (blockEntity, side) -> side == null ? null : blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.HIGHMORE_SPAWNING_BLOCK.get(),
                (blockEntity, side) -> side == null ? null : blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.HIGHMORE_SPAWNBLOCK.get(),
                (blockEntity, side) -> side == null ? null : blockEntity.getItemHandler(side));
    }

    @SubscribeEvent
    public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            ModCapabilities.getPlayerVariables(event.getEntity()).syncPlayerVariables(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            ModCapabilities.getPlayerVariables(event.getEntity()).syncPlayerVariables(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            ModCapabilities.getPlayerVariables(event.getEntity()).syncPlayerVariables(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        Player player = event.getEntity();
        Player oldPlayer = event.getOriginal();
        oldPlayer.revive();
        handleSanityCap(player, oldPlayer);
        handleApoptosisCap(player, oldPlayer);
        handlePlayerVariables(player, oldPlayer, event.isWasDeath());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            ServerPlayer serverPlayer = (ServerPlayer) event.getEntity();
            SavedData mapData = ModCapabilities.getMapVariables(event.getEntity().level());
            SavedData worldData = ModCapabilities.getWorldVariables(event.getEntity().level());
            if (mapData != null) {
                PacketDistributor.sendToPlayer(serverPlayer, new SavedDataSyncMessage(0, mapData, serverPlayer.level().registryAccess()));
            }
            if (worldData != null) {
                PacketDistributor.sendToPlayer(serverPlayer, new SavedDataSyncMessage(1, worldData, serverPlayer.level().registryAccess()));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            ServerPlayer serverPlayer = (ServerPlayer) event.getEntity();
            SavedData worldData = ModCapabilities.getWorldVariables(event.getEntity().level());
            if (worldData != null) {
                PacketDistributor.sendToPlayer(serverPlayer, new SavedDataSyncMessage(1, worldData, serverPlayer.level().registryAccess()));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide() && entity instanceof LivingEntity livingEntity) {
            ModCapabilities.getSanityInjury(livingEntity).tick();
            ModCapabilities.getApoptosisInjury(livingEntity).tick();
        }
    }

    private static void handleSanityCap(Player player, Player oldPlayer) {
        SanityInjuryCapability oldInjury = ModCapabilities.getSanityInjury(oldPlayer);
        SanityInjuryCapability newInjury = ModCapabilities.getSanityInjury(player);
        newInjury.deserializeNBT(oldPlayer.registryAccess(), oldInjury.serializeNBT(oldPlayer.registryAccess()));
    }

    private static void handleApoptosisCap(Player player, Player oldPlayer) {
        ApoptosisInjuryCapability oldApoptosis = ModCapabilities.getApoptosisInjury(oldPlayer);
        ApoptosisInjuryCapability newApoptosis = ModCapabilities.getApoptosisInjury(player);
        newApoptosis.deserializeNBT(oldPlayer.registryAccess(), oldApoptosis.serializeNBT(oldPlayer.registryAccess()));
    }

    private static void handlePlayerVariables(Player player, Player oldPlayer, boolean wasDeath) {
        PlayerVariable original = ModCapabilities.getPlayerVariables(oldPlayer);
        PlayerVariable clone = ModCapabilities.getPlayerVariables(player);
        clone.player_light = original.player_light;
        clone.player_lives = original.player_lives;
        clone.player_maxlive = original.player_maxlive;
        clone.player_shield = original.player_shield;
        clone.disoclusion = original.disoclusion;
        clone.show_stats = original.show_stats;
        clone.kingShowPtc = original.kingShowPtc;
        clone.player_util_RAINBOW = original.player_util_RAINBOW;
        clone.player_util_AROMATIC = original.player_util_AROMATIC;
        clone.player_king_suit = original.player_king_suit;
        clone.player_demon_suit = original.player_demon_suit;
        clone.player_oceanization = original.player_oceanization;
        clone.plauyer_balance = original.plauyer_balance;
        clone.can_player_evo = original.can_player_evo;
        clone.reserve_quantity = original.reserve_quantity;
        clone.reserve_quality = original.reserve_quality;
        clone.PEVO_NEXUS_no_rejection = original.PEVO_NEXUS_no_rejection;
        clone.PEVO_NEXUS_reg_sanity = original.PEVO_NEXUS_reg_sanity;
        clone.PEVO_NODE_add_def = original.PEVO_NODE_add_def;
        clone.PEVO_NODE_add_resis = original.PEVO_NODE_add_resis;
        clone.PEVO_NODE_add_speed = original.PEVO_NODE_add_speed;
        clone.PEVO_NODE_add_sanity = original.PEVO_NODE_add_sanity;
        clone.PEVO_NEXUS_reg_lights = original.PEVO_NEXUS_reg_lights;
        clone.PEVO_NODE_add_damage = original.PEVO_NODE_add_damage;
        clone.PEVO_NODE_less_damage = original.PEVO_NODE_less_damage;
        clone.PEVO_NODE_living_barrier = original.PEVO_NODE_living_barrier;
        clone.PEVO_NODE_add_miss = original.PEVO_NODE_add_miss;
        clone.PEVO_NEXUS_perc_damage = original.PEVO_NEXUS_perc_damage;
        clone.PEVO_NODE_real_damage = original.PEVO_NODE_real_damage;
        clone.PEVO_NODE_heal_damage = original.PEVO_NODE_heal_damage;
        clone.PEVO_NODE_worse_break = original.PEVO_NODE_worse_break;
        clone.PEVO_NEXUS_expo_shield = original.PEVO_NEXUS_expo_shield;
        clone.PEVO_NODE_eunectes = original.PEVO_NODE_eunectes;
        clone.PEVO_NODE_less_armor = original.PEVO_NODE_less_armor;
        for (Relic relic : Relic.values()) {
            if (relic.gained(original)) {
                relic.set(clone, relic.get(original));
            } else {
                relic.reset(clone);
            }
        }
        if (!wasDeath) {
            clone.chitin_knife_selected = original.chitin_knife_selected;
        }
    }
}
