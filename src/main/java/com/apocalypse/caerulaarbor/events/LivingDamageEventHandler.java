package com.apocalypse.caerulaarbor.events;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.*;
import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.init.CaerulaArborModParticleTypes;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.procedures.RangedSanityAttackProcedure;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber
public class LivingDamageEventHandler {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event == null || event.getEntity() == null) return;

        handleBruteHurtPlayer(event);
        handleCorruptedHit(event);
        handleCreeperChimeraHit(event);
        handleCreeperHit(event);
        handleEndspeakerDamage(event);
        handleIzumikHit(event);
        handlePlayerRejection(event);
        handleReduceLightsWithDamage(event);
        handleSanityRateFunctions(event);
        handleThirsterAddInteg(event);
        handlePlayerEvolutionDamage(event);
    }

    private static void handleBruteHurtPlayer(LivingDamageEvent event) {
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (entity == null || sourceentity == null) return;

        if (sourceentity instanceof OceanizedBruteEntity && entity instanceof Player) {
            String str = sourceentity.getPersistentData().getString("hurtPlayer");
            String name = entity.getDisplayName().getString();
            if (!str.contains(name)) {
                sourceentity.getPersistentData().putString("hurtPlayer", (str + "," + name));
            }
        }
    }

    private static void handleCorruptedHit(LivingDamageEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getAmount();

        if (damagesource == null || entity == null) return;

        if (entity instanceof SkadiCorruptedEntity) {
            boolean result = EntityUtils.isCorruptedSource(damagesource);

            if (result) {
                if ((entity instanceof SkadiCorruptedEntity _datEntI ? _datEntI.getEntityData().get(SkadiCorruptedEntity.DATA_phase) : 0) > 1.5) {
                    return;
                }
                double deal = (entity instanceof SkadiCorruptedEntity _datEntI ? _datEntI.getEntityData().get(SkadiCorruptedEntity.DATA_deal) : 0) + amount;
                if (entity instanceof SkadiCorruptedEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(SkadiCorruptedEntity.DATA_deal, (int) deal);
                if (deal >= (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.7) {
                    if (entity instanceof SkadiCorruptedEntity _datEntSetL)
                        _datEntSetL.getEntityData().set(SkadiCorruptedEntity.DATA_mayCorrupt, false);
                    if ((entity instanceof SkadiCorruptedEntity _datEntI ? _datEntI.getEntityData().get(SkadiCorruptedEntity.DATA_phase) : 0) > 0.5) {
                        return;
                    }
                    if (entity instanceof SkadiCorruptedEntity) {
                        ((SkadiCorruptedEntity) entity).setAnimation("animation.skadi_corrupted.convert_in_1");
                    }
                    if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 9999, 9, false, false));
                    if (entity instanceof SkadiCorruptedEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(SkadiCorruptedEntity.DATA_duration, 10000);
                    if (entity instanceof SkadiCorruptedEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(SkadiCorruptedEntity.DATA_convertTick, 30);
                    if (entity instanceof SkadiCorruptedEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(SkadiCorruptedEntity.DATA_convertP, 10000);
                } else {
                    if (entity instanceof SkadiCorruptedEntity _datEntSetL)
                        _datEntSetL.getEntityData().set(SkadiCorruptedEntity.DATA_mayCorrupt, true);
                }
            }
        }
    }

    private static void handleCreeperChimeraHit(LivingDamageEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        double amount = event.getAmount();

        if (entity == null) return;

        if (entity instanceof TideChimeraEntity livEnt) {
            if (amount > livEnt.getHealth()) return;
            if (!entity.isAlive()) return;

            double deal = (entity instanceof TideChimeraEntity _datEntI ? _datEntI.getEntityData().get(TideChimeraEntity.DATA_deal) : 0) + amount;
            if (entity instanceof TideChimeraEntity _datEntSetI)
                _datEntSetI.getEntityData().set(TideChimeraEntity.DATA_deal, (int) deal);
            if (deal >= livEnt.getMaxHealth() * 0.25) {
                RangedSanityAttackProcedure.execute(world, x, y, z, entity);
                if (entity instanceof TideChimeraEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(TideChimeraEntity.DATA_deal, 0);
            }
        }
    }

    private static void handleCreeperHit(LivingDamageEvent event) {
        LevelAccessor world = event.getEntity().level();
        Entity entity = event.getEntity();
        double amount = event.getAmount();

        if (entity == null) return;

        if (entity instanceof CreeperFishEntity livEnt) {
            if (amount > livEnt.getHealth()) return;
            if (!entity.isAlive()) return;

            double deal = (entity instanceof CreeperFishEntity _datEntI ? _datEntI.getEntityData().get(CreeperFishEntity.DATA_deal) : 0) + amount;
            if (entity instanceof CreeperFishEntity _datEntSetI)
                _datEntSetI.getEntityData().set(CreeperFishEntity.DATA_deal, (int) deal);
            if (deal >= livEnt.getMaxHealth() * 0.15) {
                RangedSanityAttackProcedure.execute(world, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), entity);
                if (entity instanceof CreeperFishEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(CreeperFishEntity.DATA_deal, 0);
            }
        }
    }

    private static void handleEndspeakerDamage(LivingDamageEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (sourceentity == null) return;

        if (sourceentity instanceof Endspeaker3Entity livEnt && amount > 0) {
            if (world instanceof ServerLevel _level)
                _level.sendParticles((SimpleParticleType) (CaerulaArborModParticleTypes.ENDSPEAKER_PARTICLE.get()), x, (y + 0.75), z, 8, 0.75, 0.75, 0.75, 0.1);
            if (livEnt.getHealth() >= (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.4) {
                if (sourceentity instanceof LivingEntity _entity)
                    _entity.setHealth((float) ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + amount * 1.4));
            } else {
                if (sourceentity instanceof LivingEntity _entity)
                    _entity.setHealth((float) ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + amount * 1.8));
            }
        }
    }

    private static void handleIzumikHit(LivingDamageEvent event) {
        Entity entity = event.getEntity();
        double amount = event.getAmount();

        if (entity == null) return;

        if (entity instanceof IzumikEntity livEnt) {
            double deal = entity instanceof IzumikEntity _datEntI ? _datEntI.getEntityData().get(IzumikEntity.DATA_deal) : 0;
            if (deal >= livEnt.getMaxHealth() * 0.3) {
                if (entity instanceof IzumikEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(IzumikEntity.DATA_skillp, 0);
                if (entity instanceof IzumikEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(IzumikEntity.DATA_deal, 0);
            } else {
                if (entity instanceof IzumikEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(IzumikEntity.DATA_deal, (int) (deal + amount));
            }
        }
    }

    private static void handlePlayerRejection(LivingDamageEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (entity == null || sourceentity == null) return;
        if (event.isCanceled()) return;

        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_oceanization > 2) return;

        if (entity instanceof Player && sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
            double light = (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_light;
            if (light < 85 && (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).disoclusion == 0) {
                double per = Math.min(amount / (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1), 1);
                double rate = 0.0025 + 0.0125 * per;
                if (light < 1) {
                    rate = rate * 2;
                } else if (light >= 50) {
                    rate = rate * 0.5;
                }
                if (CheckRejectionEnchantmentProcedure.execute(world, entity)) {
                    rate = rate * 2;
                }
                if (Math.random() < rate) {
                    double rejection = Mth.nextInt(RandomSource.create(), 1, 4);
                    entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                        capability.disoclusion = rejection;
                        capability.syncPlayerVariables(entity);
                    });
                    if (world instanceof Level _level) {
                        if (!_level.isClientSide()) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.zombie_villager.cure")), SoundSource.PLAYERS, 1, 1);
                        } else {
                            _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.zombie_villager.cure")), SoundSource.PLAYERS, 1, 1, false);
                        }
                    }
                    if (entity instanceof Player _player && !_player.level().isClientSide())
                        _player.displayClientMessage(
                                Component.literal(
                                        (Component.translatable("item.caerula_arbor.rejection_key.description_0").getString() + "" + Component.translatable(("item.caerula_arbor.rejection_key.description_" + Math.round(rejection))).getString())),
                                false);
                    if (entity instanceof ServerPlayer _player) {
                        Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "to_we_many"));
                        AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                        if (!_ap.isDone()) {
                            for (String criteria : _ap.getRemainingCriteria())
                                _player.getAdvancements().award(_adv, criteria);
                        }
                    }
                }
            }
        }
    }

    private static void handleReduceLightsWithDamage(LivingDamageEvent event) {
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (entity == null || sourceentity == null) return;
        if (event.isCanceled()) return;
        if (entity == sourceentity) return;

        if (entity instanceof Player) {
            double light_cost = Math.min(amount * 0.0025, 0.25);
            double _setval = Math.max((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_light - light_cost, 0);
            entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                capability.player_light = _setval;
                capability.syncPlayerVariables(entity);
            });
        }
    }

    private static void handleSanityRateFunctions(LivingDamageEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (entity == null || sourceentity == null) return;
        if (event.isCanceled()) return;

        if ((sourceentity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RATE.get())
                ? _livingEntity1.getAttribute(CaerulaArborModAttributes.SANITY_RATE.get()).getValue()
                : 0) > 0) {
            EntityUtils.deductSanity(entity, amount * (sourceentity instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RATE.get())
                    ? _livingEntity2.getAttribute(CaerulaArborModAttributes.SANITY_RATE.get()).getValue()
                    : 0));
            new Object() {
                void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                    if (world instanceof ServerLevel _level)
                        _level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + entity.getBbHeight() * 0.5), z,
                                (int) Math.min(1 * (sourceentity instanceof LivingEntity _livingEntity4 && _livingEntity4.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RATE.get())
                                        ? _livingEntity4.getAttribute(CaerulaArborModAttributes.SANITY_RATE.get()).getValue()
                                        : 0), 16),
                                1.2, 1.5, 1.2, 0.1);
                    final int tick2 = ticks;
                    CaerulaArborMod.queueServerWork(tick2, () -> {
                        if (timedlooptotal > timedloopiterator + 1) {
                            timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                        }
                    });
                }
            }.timedLoop(0, 5, 1);
        }
    }

    private static void handleThirsterAddInteg(LivingDamageEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        double amount = event.getAmount();

        if (entity == null) return;

        if (entity instanceof ThirsterEntity livEnt) {
            if (!entity.isAlive()) return;

            double dura = entity instanceof ThirsterEntity _datEntI ? _datEntI.getEntityData().get(ThirsterEntity.DATA_DURATION) : 0;
            double interg = entity instanceof ThirsterEntity _datEntI ? _datEntI.getEntityData().get(ThirsterEntity.DATA_INTEGRATION) : 0;
            double maxH = livEnt.getMaxHealth();
            if (entity instanceof ThirsterEntity _datEntSetI)
                _datEntSetI.getEntityData().set(ThirsterEntity.DATA_INTEGRATION, (int) (interg + Math.max(1, amount)));
            interg = interg + Math.max(1, amount);
            if (interg >= maxH * 0.15 && dura <= 0) {
                double num = 0;
                Entity enemy = null;
                new Object() {
                    void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                        double d = timedloopiterator * 4;
                        for (int index0 = 0; index0 < 120; index0++) {
                            double angle = index0 * 3;
                            if (world instanceof ServerLevel _level)
                                _level.sendParticles(ParticleTypes.CLOUD, (x + d * Math.sin(angle)), (y + 0.5), (z + d * Math.cos(angle)), 2, 0.1, 0.1, 0.1, 0.1);
                        }
                        final int tick2 = ticks;
                        CaerulaArborMod.queueServerWork(tick2, () -> {
                            if (timedlooptotal > timedloopiterator + 1) {
                                timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                            }
                        });
                    }
                }.timedLoop(0, 5, 1);
                if (world instanceof Level _level) {
                    if (!_level.isClientSide()) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "bishopfish_attack")), SoundSource.HOSTILE, (float) 2.5, 1);
                    } else {
                        _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "bishopfish_attack")), SoundSource.HOSTILE, (float) 2.5, 1, false);
                    }
                }
                num = entity instanceof ThirsterEntity _datEntI ? _datEntI.getEntityData().get(ThirsterEntity.DATA_DIZZY_NUM) : 0;
                enemy = entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null;
                final Vec3 _center = new Vec3(x, y, z);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(40 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                        if (!(entityiterator == enemy)) {
                            continue;
                        }
                    }
                    if (!(entityiterator instanceof LivingEntity)) {
                        continue;
                    }
                    if (entityiterator instanceof Player) {
                        if (isCreativePlayer(entityiterator) || isSpectatorMode(entityiterator)) {
                            continue;
                        }
                    }
                    if (entity.distanceTo(entityiterator) < 20) {
                        if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
                            _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.DIZZY.get(), 160, 0, false, false));
                        num = num - 1;
                        if (num <= 1) {
                            break;
                        }
                    }
                }
                if (entity instanceof ThirsterEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(ThirsterEntity.DATA_INTEGRATION, 0);
                if (entity instanceof ThirsterEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(ThirsterEntity.DATA_DURATION, 400);
                if (entity instanceof LivingEntity _livingEntity9 && _livingEntity9.getAttributes().hasAttribute(CaerulaArborModAttributes.LIVING_BARRIER.get()))
                    _livingEntity9.getAttribute(CaerulaArborModAttributes.LIVING_BARRIER.get()).setBaseValue((maxH - (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1)));
            }
        }
    }

    private static void handlePlayerEvolutionDamage(LivingDamageEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (damagesource == null || entity == null || sourceentity == null) return;

        if (!(sourceentity instanceof Player attacker) || !EntityUtils.canPlayerEvo(attacker)) return;

        double barrier = attacker.getAttributes().hasAttribute(CaerulaArborModAttributes.LIVING_BARRIER.get())
                ? attacker.getAttribute(CaerulaArborModAttributes.LIVING_BARRIER.get()).getBaseValue()
                : 0;

        double rate;
        double max;
        double lvl = EntityUtils.getNodeLivingBarrier(attacker);

        if (lvl >= 4) { rate = 0.3; max = 10; }
        else if (lvl >= 3) { rate = 0.18; max = 10; }
        else if (lvl >= 2) { rate = 0.09; max = 5; }
        else if (lvl >= 1) { rate = 0.03; max = 5; }
        else { rate = 0; max = 0; }

        max = max * attacker.getMaxHealth();

        if (barrier < max && rate > 0) {
            if (attacker.getAttributes().hasAttribute(CaerulaArborModAttributes.LIVING_BARRIER.get()))
                attacker.getAttribute(CaerulaArborModAttributes.LIVING_BARRIER.get()).setBaseValue(Math.min(barrier + event.getAmount() * rate, max));
        }

        lvl = EntityUtils.getNodeRealDamage(attacker);

        if (lvl >= 4) rate = 0.3;
        else if (lvl >= 3) rate = 0.18;
        else if (lvl >= 2) rate = 0.09;
        else if (lvl >= 1) rate = 0.03;
        else rate = 0;

        if (rate > 0) {
            if (attacker.isShiftKeyDown()) return;

            LevelAccessor world = entity.level();
            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();
            double amount = event.getAmount();

            double h = (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) - amount;
            double d = Math.min((attacker.getAttributes().hasAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) ? attacker.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue() : 0) * rate, h - 1);

            if (d > 0) {
                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hand_of_choker"))), attacker), (float) d);
                if (world instanceof ServerLevel _level)
                    _level.sendParticles(ParticleTypes.GLOW_SQUID_INK, x, (y + 0.75), z, 3, 0.75, 0.75, 0.75, 0.1);
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

    private static boolean isSpectatorMode(Entity _ent) {
        if (_ent instanceof ServerPlayer _serverPlayer) {
            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR;
        }
        return false;
    }
}
