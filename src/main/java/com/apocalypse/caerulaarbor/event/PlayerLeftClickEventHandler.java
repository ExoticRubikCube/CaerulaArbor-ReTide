package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.helper.LittleHelperEntity;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CANetwork;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(value = {Dist.CLIENT})
public class PlayerLeftClickEventHandler {

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        handleHelperLeftClick(event);
        handleRangedLightning(event);
    }

    private static void handleHelperLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        CANetwork.PACKET_HANDLER.sendToServer(new HelperLeftClickMessage());
        executeHelperLeftClick(event.getEntity());
    }

    private static void handleRangedLightning(PlayerInteractEvent.LeftClickEmpty event) {
        CANetwork.PACKET_HANDLER.sendToServer(new RangedLightningMessage());
        executeRangedLightning(event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getEntity());
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class HelperLeftClickMessage {
        public HelperLeftClickMessage() {}

        public HelperLeftClickMessage(FriendlyByteBuf buffer) {}

        public static void buffer(HelperLeftClickMessage message, FriendlyByteBuf buffer) {}

        public static void handler(HelperLeftClickMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                if (!context.getSender().level().hasChunkAt(context.getSender().blockPosition())) return;
                executeHelperLeftClick(context.getSender());
            });
            context.setPacketHandled(true);
        }

        @SubscribeEvent
        public static void registerMessage(FMLCommonSetupEvent event) {
            CANetwork.addNetworkMessage(HelperLeftClickMessage.class, HelperLeftClickMessage::buffer, HelperLeftClickMessage::new, HelperLeftClickMessage::handler);
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RangedLightningMessage {
        public RangedLightningMessage() {}

        public RangedLightningMessage(FriendlyByteBuf buffer) {}

        public static void buffer(RangedLightningMessage message, FriendlyByteBuf buffer) {}

        public static void handler(RangedLightningMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                if (!context.getSender().level().hasChunkAt(context.getSender().blockPosition())) return;
                executeRangedLightning(context.getSender().level(), context.getSender().getX(), context.getSender().getY(), context.getSender().getZ(), context.getSender());
            });
            context.setPacketHandled(true);
        }

        @SubscribeEvent
        public static void registerMessage(FMLCommonSetupEvent event) {
            CANetwork.addNetworkMessage(RangedLightningMessage.class, RangedLightningMessage::buffer, RangedLightningMessage::new, RangedLightningMessage::handler);
        }
    }

    public static void executeHelperLeftClick(Entity entity) {
        if (entity instanceof Player player && player.isPassenger() && player.getVehicle() instanceof LittleHelperEntity helper) {
            helper.handlePassengerLeftClick(player);
        }
    }

    public static void executeRangedLightning(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) return;
        if (entity instanceof LivingEntity _entity && _entity.isHolding(CAItems.APOCATA_SWORD.get())) {
            final Vec3 _center = new Vec3(x, y, z);
            List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(64 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
            for (Entity entityiterator : _entfound) {
                if (entityiterator instanceof LightningBolt) {
                    continue;
                }
                if ((entityiterator.getDisplayName().getString()).equals("item")) {
                    continue;
                }
                if (!(entity == entityiterator)) {
                    entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inv_killer")))), 114514);
                }
            }
        }
    }

}
