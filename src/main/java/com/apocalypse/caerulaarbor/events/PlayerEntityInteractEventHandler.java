package com.apocalypse.caerulaarbor.events;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.*;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.procedures.TransformIndexProcedure;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber
public class PlayerEntityInteractEventHandler {

    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() == null || event.getTarget() == null) return;

        handleCatalystFunc(event);
        handleFeedCandy(event);
        handleRightClickFunc(event);
    }

    private static void handleCatalystFunc(PlayerInteractEvent.EntityInteract event) {
        LevelAccessor world = event.getLevel();
        double x = event.getPos().getX();
        double y = event.getPos().getY();
        double z = event.getPos().getZ();
        Entity entity = event.getTarget();
        Entity sourceentity = event.getEntity();
        ItemStack itemstack = event.getItemStack();
        boolean clientside = event.getSide() == LogicalSide.CLIENT;

        if (entity == null || sourceentity == null) return;
        if (clientside) return;
        if (!entity.isAlive()) return;
        if (!(entity instanceof Mob livEnt)) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge:bosses")))
                && livEnt.getHealth() > (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) * 4.5) {
            return;
        }

        if (itemstack.getItem() == CaerulaArborModItems.OCEANIZE_CATALYST.get()) {
            double perc = 1 - (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) / (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1);

            if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "cannot_transform")))
                    || entity instanceof LivingEntity _livEnt10 && _livEnt10.getMobType() == MobType.UNDEAD
                    || entity instanceof LivingEntity _livEnt11 && _livEnt11.isBaby()) {
                return;
            }

            if (Math.random() < perc + 0.05 && TransformIndexProcedure.execute(world, x, y, z, entity)) {
                if (!entity.level().isClientSide())
                    entity.discard();
                itemstack.shrink(1);
            } else {
                double dama = Math.min((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) * 0.33, (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) * 1.5);
                if (world instanceof Level _level) {
                    if (!_level.isClientSide()) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.lava.extinguish")), SoundSource.HOSTILE, 1, 1);
                    } else {
                        _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.lava.extinguish")), SoundSource.HOSTILE, 1, 1, false);
                    }
                }
                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "extractor_damage")))), (float) 0.5);
                EntityUtils.deductSanity(entity, 256);
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
                if (entity instanceof LivingEntity _entity) {
                    _entity.setHealth((float) Math.max(_entity.getHealth() - dama, 0.5));
                }
                itemstack.shrink(1);
            }
        }
    }

    private static void handleFeedCandy(PlayerInteractEvent.EntityInteract event) {
        Entity entity = event.getTarget();
        Entity sourceentity = event.getEntity();

        if (entity == null || sourceentity == null) return;

        if (entity instanceof Sheep) {
            if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.RAINBOW_CANDY.get()) {
                (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                entity.setCustomName(Component.literal("jeb_"));
            } else if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.RAINBOW_CANDY.get()) {
                (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).shrink(1);
                entity.setCustomName(Component.literal("jeb_"));
            }
        }
    }

    private static void handleRightClickFunc(PlayerInteractEvent.EntityInteract event) {
        LevelAccessor world = event.getLevel();
        double x = event.getPos().getX();
        double y = event.getPos().getY();
        double z = event.getPos().getZ();
        Entity entity = event.getTarget();
        Entity sourceentity = event.getEntity();
        ItemStack itemstack = event.getItemStack();
        boolean clientside = event.getSide() == LogicalSide.CLIENT;

        if (entity == null || sourceentity == null) return;
        if (clientside) return;

        if (itemstack.getItem() == CaerulaArborModItems.OCEAN_EXTRACTOR.get()) {
            boolean extracted = false;

            if (entity instanceof ReaperFishEntity) {
                extracted = true;
                if (sourceentity instanceof Player _player) {
                    ItemStack _setstack = new ItemStack(CaerulaArborModItems.DNA_REAPER.get()).copy();
                    _setstack.setCount(1);
                    ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                }
            } else if (entity instanceof OceanizedHorseEntity) {
                extracted = true;
                if (sourceentity instanceof Player _player) {
                    ItemStack _setstack = new ItemStack(CaerulaArborModItems.DNA_HORSE.get()).copy();
                    _setstack.setCount(1);
                    ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                }
            }

            if (extracted) {
                itemstack.shrink(1);
                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "extractor_damage"))), sourceentity),
                        (float) 0.5);
            }
        } else if (itemstack.getItem() == CaerulaArborModItems.ROCINANTE_INJECTOR.get() && entity instanceof OceanizedHorseEntity) {
            itemstack.shrink(1);
            if (world instanceof Level _level) {
                if (!_level.isClientSide()) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.husk.converted_to_zombie")), SoundSource.NEUTRAL, 2, 1);
                } else {
                    _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.husk.converted_to_zombie")), SoundSource.NEUTRAL, 2, 1, false);
                }
            }
            if (world instanceof ServerLevel _level)
                _level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, (y + 1), z, 32, 1, 1, 1, 1);
            if (sourceentity instanceof Player _player) {
                ItemStack _setstack = new ItemStack(CaerulaArborModItems.OCEAN_EXTRACTOR.get()).copy();
                _setstack.setCount(1);
                ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
            }
            if (!entity.level().isClientSide())
                entity.discard();
            if (world instanceof ServerLevel _level) {
                Entity entityToSpawn = CaerulaArborModEntities.ROCINANTE.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
            }
        }
    }
}
