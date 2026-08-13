package com.susen36.caerulaarbor.capability;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CABlockEntities;
import com.susen36.caerulaarbor.network.receive.SavedDataSyncMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = CaerulaArbor.MODID)
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
        handlePlayerVariables(player, oldPlayer);
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

    private static void handlePlayerVariables(Player player, Player oldPlayer) {
        PlayerVariable original = ModCapabilities.getPlayerVariables(oldPlayer);
        PlayerVariable clone = ModCapabilities.getPlayerVariables(player);
        clone.player_light = original.player_light;
        clone.player_lives = original.player_lives;
        clone.player_maxlive = original.player_maxlive;
        clone.player_shield = original.player_shield;
        clone.disoclusion = original.disoclusion;
        clone.show_stats = original.show_stats;
        clone.player_util_RAINBOW = original.player_util_RAINBOW;
        clone.player_util_AROMATIC = original.player_util_AROMATIC;
        clone.player_oceanization = original.player_oceanization;
        clone.plauyer_balance = original.plauyer_balance;
        clone.can_player_evo = original.can_player_evo;
        clone.reserve_quantity = original.reserve_quantity;
        clone.reserve_quality = original.reserve_quality;
    }
}
