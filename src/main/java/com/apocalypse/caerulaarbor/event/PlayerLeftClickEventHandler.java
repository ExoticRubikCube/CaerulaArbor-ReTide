package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.Al1SHelperEntity;
import com.apocalypse.caerulaarbor.entity.LittleHelperEntity;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEnchantments;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.item.HighmoreScytheItem;
import com.apocalypse.caerulaarbor.network.CaerulaArborModNetwork;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(value = {Dist.CLIENT})
public class PlayerLeftClickEventHandler {

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        handleHelperLeftClick(event);
        handleHighmoreScytheAirAttack(event);
        handleRangedLightning(event);
    }

    private static void handleHelperLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new HelperLeftClickMessage());
        executeHelperLeftClick(event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getEntity());
    }

    private static void handleHighmoreScytheAirAttack(PlayerInteractEvent.LeftClickEmpty event) {
        CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new HighmoreScytheAirAttackMessage());
        executeHighmoreScytheAirAttack(event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getEntity());
    }

    private static void handleRangedLightning(PlayerInteractEvent.LeftClickEmpty event) {
        CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new RangedLightningMessage());
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
                executeHelperLeftClick(context.getSender().level(), context.getSender().getX(), context.getSender().getY(), context.getSender().getZ(), context.getSender());
            });
            context.setPacketHandled(true);
        }

        @SubscribeEvent
        public static void registerMessage(FMLCommonSetupEvent event) {
            CaerulaArborModNetwork.addNetworkMessage(HelperLeftClickMessage.class, HelperLeftClickMessage::buffer, HelperLeftClickMessage::new, HelperLeftClickMessage::handler);
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class HighmoreScytheAirAttackMessage {
        public HighmoreScytheAirAttackMessage() {}

        public HighmoreScytheAirAttackMessage(FriendlyByteBuf buffer) {}

        public static void buffer(HighmoreScytheAirAttackMessage message, FriendlyByteBuf buffer) {}

        public static void handler(HighmoreScytheAirAttackMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                if (!context.getSender().level().hasChunkAt(context.getSender().blockPosition())) return;
                executeHighmoreScytheAirAttack(context.getSender().level(), context.getSender().getX(), context.getSender().getY(), context.getSender().getZ(), context.getSender());
            });
            context.setPacketHandled(true);
        }

        @SubscribeEvent
        public static void registerMessage(FMLCommonSetupEvent event) {
            CaerulaArborModNetwork.addNetworkMessage(HighmoreScytheAirAttackMessage.class, HighmoreScytheAirAttackMessage::buffer, HighmoreScytheAirAttackMessage::new, HighmoreScytheAirAttackMessage::handler);
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
            CaerulaArborModNetwork.addNetworkMessage(RangedLightningMessage.class, RangedLightningMessage::buffer, RangedLightningMessage::new, RangedLightningMessage::handler);
        }
    }

    public static void executeHelperLeftClick(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) return;
        if (entity.isPassenger()) {
            Entity helper = entity.getVehicle();
            if (helper instanceof LittleHelperEntity || helper instanceof Al1SHelperEntity) {
                if (!world.isClientSide() && helper instanceof Al1SHelperEntity) {
                    if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "al1s_work")), SoundSource.BLOCKS, 3, 1);
                    }
                }
                WorldUtils.clearNetherseaAround(world, helper.getX(), helper.getY() - 1, helper.getZ(), helper);
            }
        }
    }

    public static void executeHighmoreScytheAirAttack(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) return;
        if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.HIGHMORE_SCYTHE.get()) {
            if ((entity instanceof Player _plr ? _plr.getAttackStrengthScale(0) : 0) >= 0.95) {
                if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof HighmoreScytheItem)
                    (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().putString("geckoAnim", "animation.highmore_scythe.attack");
                CaerulaArborMod.queueServerWork(10, () -> {
                    if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.HIGHMORE_SCYTHE.get()) {
                        if (world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "scythe_highmore")), SoundSource.PLAYERS, (float) 1.5, 1);
                        }
                        final Vec3 _center = new Vec3(x, (y + 0.5), z);
                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                        for (Entity entityiterator : _entfound) {
                            if (entityiterator.distanceTo(entity) <= 4) {
                                if (entityiterator instanceof LivingEntity && !(entityiterator == entity)) {
                                    if (!(entityiterator instanceof TamableAnimal _tamIsTamedBy && entity instanceof LivingEntity _livEnt && _tamIsTamedBy.isOwnedBy(_livEnt))) {
                                        entityiterator.hurt(
                                                new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "highmore_attack"))), entity),
                                                (float) ((entity instanceof LivingEntity _livingEntity10 && _livingEntity10.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                                                        ? _livingEntity10.getAttribute(Attributes.ATTACK_DAMAGE).getValue()
                                                        : 0) * (1.5 + 0.2 * (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getEnchantmentLevel(CaerulaArborModEnchantments.SYNESTHESIA.get()))));
                                        EntityUtils.giveLessArmor(entityiterator, 15);
                                    }
                                }
                            }
                        }
                        if (!isCreativePlayer(entity)) {
                            ItemStack _ist = (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY);
                            if (_ist.hurt(1, RandomSource.create(), null)) {
                                _ist.shrink(1);
                                _ist.setDamageValue(0);
                            }
                        }
                    }
                });
            }
        }
    }

    public static void executeRangedLightning(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) return;
        if (entity instanceof LivingEntity _entity && _entity.isHolding(CaerulaArborModItems.APOCATA_SWORD.get())) {
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

    private static boolean isCreativePlayer(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
        }
        if (entity.level().isClientSide() && entity instanceof Player player) {
            var connection = Minecraft.getInstance().getConnection();
            var playerInfo = connection == null ? null : connection.getPlayerInfo(player.getGameProfile().getId());
            return playerInfo != null && playerInfo.getGameMode() == GameType.CREATIVE;
        }
        return false;
    }
}
