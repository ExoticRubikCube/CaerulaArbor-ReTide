package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import com.apocalypse.caerulaarbor.capability.map.MapVariablesHandler;
import com.apocalypse.caerulaarbor.capability.map.MapVariablesHandler.StrategyType;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.config.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.entity.*;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.system.TransformIndexProcedure;
import com.apocalypse.caerulaarbor.system.UpgradeBreedProcedure;
import com.apocalypse.caerulaarbor.system.UpgradeSilenceProcedure;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.ValidationUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber
public class LivingDeathEventHandler {

    public static final TagKey<DamageType> BYPASS = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bypass_protection"));

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event == null || event.getEntity() == null) return;

        handleLifePoint(event);
        handleBarrierReset(event);
        handleInvulnerableDeath(event);
    }

    @SubscribeEvent
    public static void onEntityDeathNormal(LivingDeathEvent event) {
        if (event == null || event.getEntity() == null) return;

        handleBossKilled(event);
        handleCorruptSkadi(event);
        handleDieInWall(event);
        handleExtractorAdv(event);
        handleGeneSampleDrop(event);
        handleKillFunc(event);
        handleMobDiedOnTrail(event);
        handlePlayerDiedFunc(event);
        handlePlayerDiedInOceanization(event);
        handleSeabornKillMartus(event);
        handleSeabornTransform(event);
        handleTideBiKill(event);
        handleTrailriteArmorSelfMend(event);
        handleWitherKill(event);
    }

    private static void handleLifePoint(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null) return;

        LevelAccessor world = entity.level();
        DamageSource damagesource = event.getSource();
        if (damagesource == null) return;

        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        boolean death_blocked = false;
        boolean is_shield = false;
        boolean should_func = world.getLevelData().getGameRules().getBoolean(CaerulaArborModGameRules.TARGET_LIFE_FUNCTION);
        double light_cost = 0;

        if (entity instanceof Player && !event.isCanceled()) {
            if (damagesource.is(BYPASS)) {
                return;
            }
            if (!should_func) {
                light_cost = 25;
            } else {
                if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_shield > 0) {
                    death_blocked = true;
                    is_shield = true;
                    double _setval = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_shield - 1;
                    entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.player_shield = _setval;
                        capability.syncPlayerVariables(entity);
                    });
                } else if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives > 1) {
                    death_blocked = true;
                    double _setval = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives - 1;
                    entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.player_lives = _setval;
                        capability.syncPlayerVariables(entity);
                    });
                } else {
                    light_cost = 50;
                }
            }
            if (death_blocked) {
                if (event.isCancelable()) {
                    event.setCanceled(true);
                }
                if (entity instanceof ServerPlayer _player) {
                    Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "another_breath"));
                    AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                    if (!_ap.isDone()) {
                        for (String criteria : _ap.getRemainingCriteria())
                            _player.getAdvancements().award(_adv, criteria);
                    }
                }
                if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.totem.use")), SoundSource.PLAYERS, (float) 0.33, 1);
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "target_damaged")), SoundSource.PLAYERS, (float) 0.33, 1);
                }
                if (entity.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get()))
                    entity.getAttribute(CaerulaArborModAttributes.SANITY.get()).setBaseValue(1000);
                if (is_shield) {
                    if (world instanceof ServerLevel _level)
                        _level.sendParticles(CaerulaArborModParticleTypes.SHIELDLOSS.get(), x, (y + 0.95), z, 72, 0.75, 0.55, 0.75, 0.2);
                    if (!world.isClientSide()) {
                        entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0));
                        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 4));
                        entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 100, 0));
                    }
                    entity.setHealth(entity.getMaxHealth());
                } else {
                    if (world instanceof ServerLevel _level)
                        _level.sendParticles(CaerulaArborModParticleTypes.LIFELOSS.get(), x, (y + 0.95), z, 72, 0.75, 0.55, 0.75, 0.2);
                    if (!world.isClientSide())
                        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 2));
                    entity.setHealth(entity.getMaxHealth() * 0.5f);
                    if (damagesource.is(TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "rare")))) {
                        light_cost = 15;
                    } else if (damagesource.is(TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "horror")))) {
                        light_cost = 10;
                    } else {
                        light_cost = 5;
                    }
                }
            }
            if (light_cost > 0) {
                double cur_light = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_light;
                double _setval = Math.max(0, cur_light - light_cost);
                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.player_light = _setval;
                    capability.syncPlayerVariables(entity);
                });
            }
        }
    }

    private static void handleBarrierReset(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity == null) return;

        if (!event.isCanceled()) {
            if (entity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(CaerulaArborModAttributes.LIVING_BARRIER.get()))
                _livingEntity1.getAttribute(CaerulaArborModAttributes.LIVING_BARRIER.get()).setBaseValue(0);
            entity.getPersistentData().putDouble("playerEvoHitTime", 0);
        }
    }

    private static void handleInvulnerableDeath(LivingDeathEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (damagesource == null || entity == null || sourceentity == null) return;

        if (entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CaerulaArborModMobEffects.INVULNERABLE.get()) && !damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inv_killer")))) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
        }
    }

    private static void handleBossKilled(LivingDeathEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (damagesource == null || entity == null) return;
        if (damagesource.is(DamageTypes.GENERIC_KILL)) return;
        if (event.isCanceled()) return;

        if (entity instanceof OceanizedEnderinaEntity) {
            handleOceanizedEnderinaDeath(event, world, x, y, z, damagesource, entity);
        }
        if (entity instanceof CompassionPrayerEntity) {
            handleCompassionPrayerDeath(event, world, x, y, z, entity);
        }
        if (entity instanceof MartusEntity) {
            handleMartusDeath(event, entity);
        }
        if (entity instanceof IzumikEntity) {
            handleIzumikDeath(event, world, entity);
        }
        if (entity instanceof RouteShaperEntity) {
            handleRouteShaperDeath(event, world, entity);
        }
        if (entity instanceof HighmoreEntity) {
            handleHighmoreDeath(event, world, entity);
        }
    }

    private static void handleOceanizedEnderinaDeath(LivingDeathEvent event, LevelAccessor world, double x, double y, double z, DamageSource damagesource, Entity entity) {
        boolean result = false;
        if (damagesource != null) {
            Entity sEntity = damagesource.getEntity();
            if (sEntity != null) {
                if ((sEntity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.ENDERINA_SPAWNER.get()) {
                    result = true;
                } else if (!sEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                    if (!(sEntity instanceof TamableAnimal _tamEnt) || !_tamEnt.isTame()) {
                        result = !(sEntity instanceof Player || sEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside"))));
                    }
                }
            }
        }
        if (result) {
            if ((entity instanceof OceanizedEnderinaEntity _datEntI ? _datEntI.getEntityData().get(OceanizedEnderinaEntity.DATA_PHASE) : 0) == 0) {
                if (event.isCancelable()) {
                    event.setCanceled(true);
                }
                if (entity instanceof OceanizedEnderinaEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(OceanizedEnderinaEntity.DATA_REVIVE_TICK, 200);
                if (entity instanceof OceanizedEnderinaEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(OceanizedEnderinaEntity.DATA_PHASE, 1);
                if (entity instanceof LivingEntity _livingEntity7 && _livingEntity7.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                    _livingEntity7.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(((entity instanceof LivingEntity _livingEntity6 && _livingEntity6.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity6.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * 2));
                if (entity instanceof LivingEntity _livingEntity9 && _livingEntity9.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                    _livingEntity9.getAttribute(Attributes.MAX_HEALTH).setBaseValue(((entity instanceof LivingEntity _livingEntity8 && _livingEntity8.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? _livingEntity8.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * 3));
                if (entity instanceof LivingEntity _livingEntity11 && _livingEntity11.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()))
                    _livingEntity11.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()).setBaseValue(((entity instanceof LivingEntity _livingEntity10 && _livingEntity10.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()) ? _livingEntity10.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()).getBaseValue() : 0) * 2));
                if (entity instanceof LivingEntity _livingEntity13 && _livingEntity13.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
                    _livingEntity13.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(((entity instanceof LivingEntity _livingEntity12 && _livingEntity12.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()) ? _livingEntity12.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).getBaseValue() : 0) + 20));
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 200, 1, false, false));
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FAKE_DEATH.get(), 200, 1, false, false));
            }
        }
    }

    private static void handleCompassionPrayerDeath(LivingDeathEvent event, LevelAccessor world, double x, double y, double z, Entity entity) {
        if ((entity instanceof CompassionPrayerEntity _datEntI ? _datEntI.getEntityData().get(CompassionPrayerEntity.DATA_PHASE) : 0) == 0
                && (entity instanceof CompassionPrayerEntity _datEntI ? _datEntI.getEntityData().get(CompassionPrayerEntity.DATA_REVIVE_TICK) : 0) <= 0) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
            final Vec3 _center = new Vec3(x, y, z);
            List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
            for (Entity entityiterator : _entfound) {
                if (!(entity == entityiterator)) {
                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                        if (entityiterator.isAlive()) {
                            if (entityiterator instanceof LivingEntity _entity1 && !_entity1.level().isClientSide())
                                _entity1.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.IMMORTAL.get(), 200, 0, false, false));
                        }
                    }
                }
            }
            if (entity instanceof CompassionPrayerEntity _datEntSetI)
                _datEntSetI.getEntityData().set(CompassionPrayerEntity.DATA_REVIVE_TICK, 200);
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 200, 1, false, false));
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FAKE_DEATH.get(), 200, 1, false, false));
        }
    }

    private static void handleMartusDeath(LivingDeathEvent event, Entity entity) {
        if ((entity instanceof MartusEntity _datEntI ? _datEntI.getEntityData().get(MartusEntity.DATA_phase) : 0) == 0) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
            if (entity instanceof LivingEntity _entity)
                _entity.removeEffect(CaerulaArborModMobEffects.INVULNERABLE.get());
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 200, 1, false, false));
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FAKE_DEATH.get(), 200, 1, false, false));
            if (entity instanceof MartusEntity _datEntSetI)
                _datEntSetI.getEntityData().set(MartusEntity.DATA_phase, 1);
            if (entity instanceof MartusEntity _datEntSetI)
                _datEntSetI.getEntityData().set(MartusEntity.DATA_skillp1, 600);
            if (entity instanceof MartusEntity _datEntSetI)
                _datEntSetI.getEntityData().set(MartusEntity.DATA_skillp2, 200);
        }
    }

    private static void handleIzumikDeath(LivingDeathEvent event, LevelAccessor world, Entity entity) {
        if ((entity instanceof IzumikEntity _datEntI ? _datEntI.getEntityData().get(IzumikEntity.DATA_phase) : 0) == 0) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
            if (entity instanceof LivingEntity _entity)
                _entity.setHealth((float) ((entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.6));
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 300, 1, false, false));
            if (entity instanceof IzumikEntity _datEntSetI)
                _datEntSetI.getEntityData().set(IzumikEntity.DATA_phase, 1);
            if (entity instanceof LivingEntity _livingEntity37 && _livingEntity37.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()))
                _livingEntity37.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()).setBaseValue(((entity instanceof LivingEntity _livingEntity36 && _livingEntity36.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()) ? _livingEntity36.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()).getBaseValue() : 0) + 2));
        } else if ((entity instanceof IzumikEntity _datEntI ? _datEntI.getEntityData().get(IzumikEntity.DATA_phase) : 0) == 1 && MapVariables.get(world).strategy_silence >= 3) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 200, 1, false, false));
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FAKE_DEATH.get(), 200, 1, false, false));
            if (entity instanceof IzumikEntity _datEntSetI)
                _datEntSetI.getEntityData().set(IzumikEntity.DATA_phase, 2);
            if (entity instanceof LivingEntity _livingEntity43 && _livingEntity43.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()))
                _livingEntity43.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()).setBaseValue(((entity instanceof LivingEntity _livingEntity42 && _livingEntity42.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()) ? _livingEntity42.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()).getBaseValue() : 0) + 2));
        } else {
            for (Entity entityiterator : new ArrayList<>(world.players())) {
                if ((entity.level().dimension()) == (entityiterator.level().dimension())) {
                    if (entityiterator instanceof ServerPlayer _player) {
                        Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "stella_caerula"));
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

    private static void handleRouteShaperDeath(LivingDeathEvent event, LevelAccessor world, Entity entity) {
        if (MapVariables.get(world).strategy_subsisting >= 4) {
            if ((entity instanceof RouteShaperEntity _datEntI ? _datEntI.getEntityData().get(RouteShaperEntity.DATA_phase) : 0) == 0) {
                if (event.isCancelable()) {
                    event.setCanceled(true);
                }
                if (entity instanceof RouteShaperEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(RouteShaperEntity.DATA_phase, 1);
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 200, 1, false, false));
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FAKE_DEATH.get(), 200, 1, false, false));
            }
        }
    }

    private static void handleHighmoreDeath(LivingDeathEvent event, LevelAccessor world, Entity entity) {
        if ((entity instanceof HighmoreEntity _datEntI ? _datEntI.getEntityData().get(HighmoreEntity.DATA_phase) : 0) == 0) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 400, 1, false, false));
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FAKE_DEATH.get(), 400, 0, false, false));
            CaerulaArborMod.queueServerWork(300, () -> {
                if (entity != null) {
                    if (entity instanceof HighmoreEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(HighmoreEntity.DATA_phase, 1);
                }
            });
        } else if ((entity instanceof HighmoreEntity _datEntI ? _datEntI.getEntityData().get(HighmoreEntity.DATA_phase) : 0) == 1 && MapVariables.get(world).strategy_silence >= 3) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 200, 1, false, false));
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FAKE_DEATH.get(), 200, 1, false, false));
            CaerulaArborMod.queueServerWork(150, () -> {
                if (entity != null) {
                    if (entity instanceof HighmoreEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(HighmoreEntity.DATA_phase, 2);
                }
            });
        } else {
            for (Entity entityiterator : new ArrayList<>(world.players())) {
                if ((entity.level().dimension()) == (entityiterator.level().dimension())) {
                    if (entityiterator instanceof ServerPlayer _player) {
                        Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "precious_days"));
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

    private static void handleCorruptSkadi(LivingDeathEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (damagesource == null || entity == null) return;
        if (damagesource.is(DamageTypes.GENERIC_KILL)) return;
        if (event.isCanceled()) return;

        if (entity instanceof SkadiEntity) {
            if (damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "isharmla_cursed")))) {
                for (Entity entityiterator : new ArrayList<>(world.players())) {
                    if ((entityiterator != null ? entity.distanceTo(entityiterator) : -1) < 32) {
                        if (entityiterator instanceof Player _player && !_player.level().isClientSide())
                            _player.displayClientMessage(Component.literal((Component.translatable("entity.caerula_arbor.skadi_corrupted.start").getString())), false);
                    }
                }
                if (!entity.level().isClientSide())
                    entity.discard();
                if (world instanceof ServerLevel _level)
                    _level.sendParticles(ParticleTypes.EXPLOSION, x, (y + 1), z, 5, 0, 0, 0, 0.1);
                if (world instanceof ServerLevel _level) {
                    Entity entityToSpawn = CaerulaArborModEntities.SKADI_CORRUPTED.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                }
            } else {
                double p = entity instanceof SkadiEntity _datEntI ? _datEntI.getEntityData().get(SkadiEntity.DATA_phase) : 0;
                if (p == 0) {
                    if (event.isCancelable()) {
                        event.setCanceled(true);
                    }
                    if (entity instanceof SkadiEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(SkadiEntity.DATA_phase, 1);
                    if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "skadi_talk")), SoundSource.HOSTILE, 2, 1);
                    }
                    if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 100, 1, false, false));
                    if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FAKE_DEATH.get(), 100, 3, false, false));
                    if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 131071, 1, false, true));
                    if (entity instanceof LivingEntity _livingEntity18 && _livingEntity18.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                        _livingEntity18.getAttribute(Attributes.MAX_HEALTH).setBaseValue(((entity instanceof LivingEntity _livingEntity17 && _livingEntity17.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? _livingEntity17.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * 0.75));
                    if (entity instanceof LivingEntity _livingEntity20 && _livingEntity20.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                        _livingEntity20.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(((entity instanceof LivingEntity _livingEntity19 && _livingEntity19.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity19.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * 1.5));
                    if (entity instanceof LivingEntity _livingEntity22 && _livingEntity22.getAttributes().hasAttribute(Attributes.ARMOR))
                        _livingEntity22.getAttribute(Attributes.ARMOR).setBaseValue(((entity instanceof LivingEntity _livingEntity21 && _livingEntity21.getAttributes().hasAttribute(Attributes.ARMOR) ? _livingEntity21.getAttribute(Attributes.ARMOR).getBaseValue() : 0) * 2));
                } else if (p == 1) {
                    if (event.isCancelable()) {
                        event.setCanceled(true);
                    }
                    if (entity instanceof SkadiEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(SkadiEntity.DATA_phase, 2);
                    if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "skadi_talk")), SoundSource.HOSTILE, 2, 1);
                    }
                    if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 100, 1, false, false));
                    if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FAKE_DEATH.get(), 100, 3, false, false));
                    if (entity instanceof LivingEntity _livingEntity28 && _livingEntity28.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                        _livingEntity28.getAttribute(Attributes.MAX_HEALTH).setBaseValue(((entity instanceof LivingEntity _livingEntity27 && _livingEntity27.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? _livingEntity27.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * 0.8));
                    if (entity instanceof LivingEntity _livingEntity30 && _livingEntity30.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                        _livingEntity30.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(((entity instanceof LivingEntity _livingEntity29 && _livingEntity29.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity29.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * 1.25));
                    if (entity instanceof LivingEntity _livingEntity32 && _livingEntity32.getAttributes().hasAttribute(Attributes.ARMOR))
                        _livingEntity32.getAttribute(Attributes.ARMOR).setBaseValue(((entity instanceof LivingEntity _livingEntity31 && _livingEntity31.getAttributes().hasAttribute(Attributes.ARMOR) ? _livingEntity31.getAttribute(Attributes.ARMOR).getBaseValue() : 0) * 2));
                }
            }
        }
    }

    private static void handleDieInWall(LivingDeathEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (damagesource == null || entity == null) return;

        if (damagesource.is(DamageTypes.IN_WALL)) {
            if (entity instanceof BaselayerAbyssalEntity _datEntSetI)
                _datEntSetI.getEntityData().set(BaselayerAbyssalEntity.DATA_mute_time, 999);
        }
    }

    private static void handleExtractorAdv(LivingDeathEvent event) {
        DamageSource damagesource = event.getSource();
        Entity sourceentity = event.getSource().getEntity();

        if (damagesource == null || sourceentity == null) return;

        if (damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "extractor_damage")))) {
            if (sourceentity instanceof ServerPlayer _player) {
                Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "little_by_little"));
                AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                if (!_ap.isDone()) {
                    for (String criteria : _ap.getRemainingCriteria())
                        _player.getAdvancements().award(_adv, criteria);
                }
            }
        }
    }

    private static void handleGeneSampleDrop(LivingDeathEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (entity == null || sourceentity == null) return;
        if (event.isCanceled()) return;
        if (!world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) return;

        if (sourceentity instanceof Player && EntityUtils.canPlayerEvo(sourceentity)) {
            double r0 = 0, r1 = 0, r2 = 0;
            if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bossoffspring")))) {
                r0 = 0.5; r1 = 0.25; r2 = 0.125;
            } else if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanelite")))) {
                r0 = 0.3; r1 = 0.075; r2 = 0.0075;
            } else if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                r0 = 0.15;
            }
            if (Math.random() < r0) {
                if (world instanceof ServerLevel _level) {
                    ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(CaerulaArborModItems.GENE_SAMPLE_NORMAL.get()));
                    entityToSpawn.setPickUpDelay(10);
                    entityToSpawn.setUnlimitedLifetime();
                    _level.addFreshEntity(entityToSpawn);
                }
            }
            if (Math.random() < r1) {
                if (world instanceof ServerLevel _level) {
                    ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(CaerulaArborModItems.GENE_SAMPLE_UPGRADED.get()));
                    entityToSpawn.setPickUpDelay(10);
                    entityToSpawn.setUnlimitedLifetime();
                    _level.addFreshEntity(entityToSpawn);
                }
            }
            if (Math.random() < r2) {
                if (world instanceof ServerLevel _level) {
                    ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(CaerulaArborModItems.GENE_SAMPLE_SUPERB.get()));
                    entityToSpawn.setPickUpDelay(10);
                    entityToSpawn.setUnlimitedLifetime();
                    _level.addFreshEntity(entityToSpawn);
                }
            }
        }
    }

    private static void handleKillFunc(LivingDeathEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (damagesource == null || entity == null || sourceentity == null) return;
        if (event.isCanceled()) return;

        if (sourceentity instanceof Player) {
            handlePlayerKillRelics(event, world, x, y, z, entity, sourceentity);
        }

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
            if (world.getLevelData().getGameRules().getBoolean(CaerulaArborModGameRules.NATURAL_EVOLUTION)) {
                if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), e -> true).isEmpty()) {
                    MapVariablesHandler.addEvoPoint(world, StrategyType.BREED, (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.1);
                    UpgradeBreedProcedure.execute(world);
                    UpgradeSilenceProcedure.execute(world, (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.1);
                }
            }
        }

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanelite")))) {
            if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                if (Math.random() < 0.1) {
                    if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_cluster.break")), SoundSource.AMBIENT, 1, 1);
                    }
                    if (world instanceof ServerLevel _level) {
                        ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "common_relics"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))));
                        entityToSpawn.setPickUpDelay(10);
                        entityToSpawn.setUnlimitedLifetime();
                        _level.addFreshEntity(entityToSpawn);
                    }
                }
            }
        }

        if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "self_mendable")))) {
            ItemStack weapon = (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
            double dama = weapon.getDamageValue() - Mth.nextInt(RandomSource.create(), 1, 5 + weapon.getEnchantmentLevel(Enchantments.UNBREAKING));
            if (dama <= 0) {
                (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).setDamageValue(0);
            } else {
                (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).setDamageValue((int) dama);
            }
        }
    }

    private static void handlePlayerKillRelics(LivingDeathEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
        if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_cursed_EMELIGHT) {
            double _setval = (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_light - Mth.nextDouble(RandomSource.create(), 0.1, 0.2);
            sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_light = _setval;
                capability.syncPlayerVariables(sourceentity);
            });
        }
        if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_cursed_GLOWBODY) {
            double _setval = (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_light - Mth.nextDouble(RandomSource.create(), 0.2, 0.3);
            sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_light = _setval;
                capability.syncPlayerVariables(sourceentity);
            });
        }
        if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_cursed_RESEARCH) {
            double _setval = (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_light - Mth.nextDouble(RandomSource.create(), 0.3, 0.5);
            sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_light = _setval;
                capability.syncPlayerVariables(sourceentity);
            });
        }
        if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_light < 0) {
            sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_light = 0;
                capability.syncPlayerVariables(sourceentity);
            });
        }
        if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_ARMOR) {
            if (Math.random() < 0.08) {
                if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives > 1) {
                    double _setval = (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives - 1;
                    sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.player_lives = _setval;
                        capability.syncPlayerVariables(sourceentity);
                    });
                }
                double _setval = (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_shield + 1;
                sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.player_shield = _setval;
                    capability.syncPlayerVariables(sourceentity);
                });
            }
        }
        if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_CRYSTAL) {
            if (Math.random() < 0.1) {
                if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives > 1) {
                    double _setval = Math.max((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives - 2, 1);
                    sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.player_lives = _setval;
                        capability.syncPlayerVariables(sourceentity);
                    });
                }
                if (sourceentity instanceof Player _player)
                    _player.giveExperienceLevels(1);
                if (world instanceof ServerLevel _level) {
                    ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(CaerulaArborModItems.REDSTONE_INGOT.get()));
                    entityToSpawn.setPickUpDelay(10);
                    _level.addFreshEntity(entityToSpawn);
                }
            }
        }
        if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_ENGRAVE >= 0
                && (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_ENGRAVE < 99) {
            if (entity instanceof Monster || (entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == sourceentity) {
                boolean validweapon = false;
                if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.TRIDENT) {
                    validweapon = true;
                } else if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("forge:tools/tridents")))) {
                    validweapon = true;
                } else if (event.getSource().is(DamageTypes.TRIDENT)) {
                    validweapon = true;
                } else {
                    String rname = ForgeRegistries.ITEMS.getKey((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()).toString();
                    for (String stringiterator : CaerulaConfigsConfiguration.HAND_ENGRAVE.get()) {
                        if (ValidationUtils.isValidString(stringiterator, rname)) {
                            validweapon = true;
                            break;
                        }
                    }
                }
                if (validweapon) {
                    double _setval = (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_ENGRAVE + 1;
                    sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.relic_hand_ENGRAVE = _setval;
                        capability.syncPlayerVariables(sourceentity);
                    });
                }
            }
        }
        if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_SURVIVOR >= 0
                && (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_SURVIVOR < 32) {
            if (entity instanceof Monster || (entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == sourceentity) {
                if (Math.random() < 0.035 || entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge:bosses")))) {
                    double _setval = (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_SURVIVOR + 1;
                    sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.relic_SURVIVOR = _setval;
                        capability.syncPlayerVariables(sourceentity);
                    });
                    if (world instanceof ServerLevel _level)
                        _level.sendParticles(ParticleTypes.WAX_ON, x, y, z, 48, 0.7, 1.5, 0.7, 0.2);
                }
            }
        }
    }

    private static void handleMobDiedOnTrail(LivingDeathEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (damagesource == null || entity == null) return;
        if (event.isCanceled()) return;
        if (entity instanceof SkadiEntity) return;

        if (damagesource.is(TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "can_trigger_oceanization")))) {
            if (TransformIndexProcedure.transformToSeaborn(world, x, y, z, entity)) {
                if (event.isCancelable()) {
                    event.setCanceled(true);
                }
                if (!entity.level().isClientSide())
                    entity.discard();
            }
        }
    }

    private static void handlePlayerDiedFunc(LivingDeathEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (damagesource == null || entity == null) return;
        if (event.isCanceled()) return;

        if (MapVariables.get(world).strategy_breed >= 3) {
            if (!damagesource.is(DamageTypes.GENERIC_KILL)) {
                handleTrailGrowth(world, x, y, z, entity);
            }
        }
    }

    private static void handleTrailGrowth(LevelAccessor world, double x, double y, double z, Entity entity) {
        double dx, dy, dz, num, light_cost = 1;
        dx = -1;
        for (int index0 = 0; index0 < 3; index0++) {
            dz = -1;
            for (int index1 = 0; index1 < 3; index1++) {
                dy = -1;
                for (int index2 = 0; index2 < 3; index2++) {
                    if ((world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz))).getBlock() == CaerulaArborModBlocks.SEA_TRAIL_INIT.get()
                            || (world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz))).getBlock() == CaerulaArborModBlocks.SEA_TRAIL_GROWING.get()) {
                        int _value = ((world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz))).getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip6
                                ? (world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz))).getValue(_getip6)
                                : -1) + 4;
                        BlockPos _pos = BlockPos.containing(x + dx, y + dy, z + dz);
                        BlockState _bs = world.getBlockState(_pos);
                        if (_bs.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                            world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
                    }
                    dy = dy + 1;
                }
                dz = dz + 1;
            }
            dx = dx + 1;
        }
        num = Math.round(Math.sqrt(entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1));
        light_cost = 1;
        if (num > 8) {
            light_cost = 2;
        }
        if (num > 24) {
            num = 24;
            light_cost = 4;
        }
        if (num > 0) {
            dx = (-1) * num;
            for (int index3 = 0; index3 < (int) (2 * num); index3++) {
                dz = (-1) * num;
                for (int index4 = 0; index4 < (int) (2 * num); index4++) {
                    dy = (-1) * num;
                    for (int index5 = 0; index5 < (int) (2 * num); index5++) {
                        if (light_cost <= 0) {
                            return;
                        }
                        if ((world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz))).getBlock() == CaerulaArborModBlocks.OCEAN_OVARY.get()) {
                            if (1 == ((world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz))).getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _getip12
                                    ? (world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz))).getValue(_getip12)
                                    : -1)) {
                                BlockPos _pos = BlockPos.containing(x + dx, y + dy, z + dz);
                                BlockState _bs = world.getBlockState(_pos);
                                if (_bs.getBlock().getStateDefinition().getProperty("output") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(0))
                                    world.setBlock(_pos, _bs.setValue(_integerProp, 0), 3);
                                _bs = world.getBlockState(_pos);
                                if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(0))
                                    world.setBlock(_pos, _bs.setValue(_integerProp, 0), 3);
                                light_cost = light_cost - 1;
                            }
                        }
                        dy = dy + 1;
                    }
                    dz = dz + 1;
                }
                dx = dx + 1;
            }
        }
    }

    private static void handlePlayerDiedInOceanization(LivingDeathEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (damagesource == null || entity == null) return;
        if (event.isCanceled()) return;

        if (entity instanceof Player && damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanize_damage")))) {
            if (world instanceof ServerLevel _level) {
                Entity entityToSpawn = CaerulaArborModEntities.SLIDER_FISH.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setDeltaMovement(0, 0.15, 0);
                }
            }
            if (world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.place")), SoundSource.PLAYERS, (float) 0.75, 1);
            }
            if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_oceanization < 2.9) {
                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.player_oceanization = 0;
                    capability.syncPlayerVariables(entity);
                });
            }
        }
    }

    private static void handleSeabornKillMartus(LivingDeathEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (entity == null) return;
        if (event.isCanceled()) return;
        if (entity instanceof MartusEntity) return;

        Entity martus = world.getEntitiesOfClass(MartusEntity.class, AABB.ofSize(new Vec3(x, y, z), 96, 96, 96), e -> true).stream()
                .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(x, y, z))).findFirst().orElse(null);

        if (martus == null) return;

        if (entity.getPersistentData().getBoolean("blessed")) {
            EntityUtils.hurtMartus(world, martus, sourceentity, Math.max(Math.min((entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.25, (martus instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.4),
                    (martus instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.05), 0);
        } else if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring"))) && (martus instanceof MartusEntity _datEntI ? _datEntI.getEntityData().get(MartusEntity.DATA_phase) : 0) >= 1) {
            EntityUtils.hurtMartus(world, martus, sourceentity, Math.max(Math.min((entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.03, (martus instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.025),
                    (martus instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.018), 0);
        }
    }

    private static void handleSeabornTransform(LivingDeathEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (entity == null || sourceentity == null) return;
        if (event.isCanceled()) return;

        if (!entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring"))) && sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
            if (TransformIndexProcedure.transformToSeaborn(world, x, y, z, entity)) {
                if (event.isCancelable()) {
                    event.setCanceled(true);
                }
                if (!entity.level().isClientSide())
                    entity.discard();
            }
        }
    }

    private static void handleTideBiKill(LivingDeathEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();

        if (entity == null) return;

        if (entity instanceof TideDeathrepellerEntity) {
            handleTideDeathrepellerDeath(event, world, x, y, z, entity);
        }
        if (entity instanceof TideBishopEntity) {
            handleTideBishopDeath(event, world, x, y, z, entity);
        }
    }

    private static void handleTideDeathrepellerDeath(LivingDeathEvent event, LevelAccessor world, double x, double y, double z, Entity entity) {
        boolean keepup = true;
        if (world.getEntitiesOfClass(TideBishopEntity.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), e -> true).isEmpty()) {
            keepup = false;
        } else {
            Entity bishop = world.getEntitiesOfClass(TideBishopEntity.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), e -> true).stream()
                    .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(x, y, z))).findFirst().orElse(null);
            if (bishop instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(CaerulaArborModMobEffects.FAKE_DEATH.get())) {
                keepup = false;
            }
        }
        if (keepup) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
            entity.setShiftKeyDown(true);
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 200, 0, false, false));
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FAKE_DEATH.get(), 200, 1, false, false));
        }
    }

    private static void handleTideBishopDeath(LivingDeathEvent event, LevelAccessor world, double x, double y, double z, Entity entity) {
        boolean keepup = true;
        if (world.getEntitiesOfClass(TideDeathrepellerEntity.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), e -> true).isEmpty()) {
            keepup = false;
        } else {
            Entity repeller = world.getEntitiesOfClass(TideDeathrepellerEntity.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), e -> true).stream()
                    .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(x, y, z))).findFirst().orElse(null);
            if (repeller instanceof LivingEntity _livEnt10 && _livEnt10.hasEffect(CaerulaArborModMobEffects.FAKE_DEATH.get())) {
                keepup = false;
            }
        }
        if (keepup) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
            entity.setShiftKeyDown(true);
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 400, 0, false, false));
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FAKE_DEATH.get(), 400, 0, false, false));
        }
    }

    private static void handleTrailriteArmorSelfMend(LivingDeathEvent event) {
        Entity sourceentity = event.getSource().getEntity();

        if (sourceentity == null) return;
        if (event.isCanceled()) return;

        ItemStack helm = (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).copy();
        ItemStack chest = (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).copy();
        ItemStack legg = (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).copy();
        ItemStack boot = (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).copy();

        if (helm.getItem() == CaerulaArborModItems.TRAILRITE_ARMOR_HELMET.get() && chest.getItem() == CaerulaArborModItems.TRAILRITE_ARMOR_CHESTPLATE.get()
                && legg.getItem() == CaerulaArborModItems.TRAILRITE_ARMOR_LEGGINGS.get() && boot.getItem() == CaerulaArborModItems.TRAILRITE_ARMOR_BOOTS.get()) {
            (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).setDamageValue(helm.getDamageValue() - 3);
            (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).setDamageValue(chest.getDamageValue() - 3);
            (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).setDamageValue(legg.getDamageValue() - 3);
            (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).setDamageValue(boot.getDamageValue() - 3);
        }
    }

    private static void handleWitherKill(LivingDeathEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (damagesource == null || entity == null) return;
        if (event.isCanceled()) return;

        if (entity instanceof OceanizedWitherEntity || entity instanceof OceannizedWitheriaEntity) {
            // TODO: WITHER_FRAGMENT entity is not implemented yet.
            // if (world instanceof ServerLevel _level) {
            //     Entity entityToSpawn = CaerulaArborModEntities.WITHER_FRAGMENT.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
            //     if (entityToSpawn != null) {
            //         entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
            //     }
            // }
        }
    }
}
