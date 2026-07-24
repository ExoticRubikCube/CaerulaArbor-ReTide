package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.entity.helper.LittleHelperEntity;
import com.susen36.caerulaarbor.init.CANetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Objects;
import java.util.function.Supplier;

@EventBusSubscriber(value = {Dist.CLIENT})
public class PlayerLeftClickEventHandler {

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        handleHelperLeftClick(event);
    }

    private static void handleHelperLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        CANetwork.PACKET_HANDLER.sendToServer(new HelperLeftClickMessage());
        executeHelperLeftClick(event.getEntity());
    }

    public static void executeHelperLeftClick(Entity entity) {
        if (entity instanceof Player player && player.isPassenger() && player.getVehicle() instanceof LittleHelperEntity helper) {
            helper.handlePassengerLeftClick(player);
        }
    }

    @EventBusSubscriber
    public static class HelperLeftClickMessage {
        public HelperLeftClickMessage() {
        }

        public HelperLeftClickMessage(FriendlyByteBuf buffer) {
        }

        public static void buffer(HelperLeftClickMessage message, FriendlyByteBuf buffer) {
        }

        public static void handler(HelperLeftClickMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                if (!Objects.requireNonNull(context.getSender()).level().hasChunkAt(context.getSender().blockPosition()))
                    return;
                executeHelperLeftClick(context.getSender());
            });
            context.setPacketHandled(true);
        }

        @SubscribeEvent
        public static void registerMessage(FMLCommonSetupEvent event) {
            CANetwork.addNetworkMessage(HelperLeftClickMessage.class, HelperLeftClickMessage::buffer, HelperLeftClickMessage::new, HelperLeftClickMessage::handler);
        }
    }

}
