package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.helper.LittleHelperEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(value = {Dist.CLIENT})
public class PlayerLeftClickEventHandler {

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        handleHelperLeftClick(event);
    }

    private static void handleHelperLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        PacketDistributor.sendToServer(new HelperLeftClickMessage());
        executeHelperLeftClick(event.getEntity());
    }

    public static void executeHelperLeftClick(Entity entity) {
        if (entity instanceof Player player && player.isPassenger() && player.getVehicle() instanceof LittleHelperEntity helper) {
            helper.playPassengerLeftClickSound(player);
            helper.clearNetherseaAround();
        }
    }

    public static class HelperLeftClickMessage implements CustomPacketPayload {
        public static final Type<HelperLeftClickMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "helper_left_click"));
        public static final StreamCodec<FriendlyByteBuf, HelperLeftClickMessage> STREAM_CODEC = StreamCodec.of(
                (buf, msg) -> {
                },
                buf -> new HelperLeftClickMessage()
        );

        public HelperLeftClickMessage() {
        }

        public static void handle(HelperLeftClickMessage message, IPayloadContext context) {
            context.enqueueWork(() -> {
                Player sender = context.player();
                if (!sender.level().hasChunkAt(sender.blockPosition())) {
                    return;
                }
                executeHelperLeftClick(sender);
            });
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}