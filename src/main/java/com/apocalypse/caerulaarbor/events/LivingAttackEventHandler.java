package com.apocalypse.caerulaarbor.events;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.configuration.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.entity.*;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.item.HighmoreScytheItem;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.procedures.*;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import com.apocalypse.caerulaarbor.utils.ValidationUtils;
import com.apocalypse.caerulaarbor.utils.WorldUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber
public class LivingAttackEventHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityAttack(LivingAttackEvent event) {
        if (event == null || event.getEntity() == null) return;

        handleInvulnerable(event);
        handleNumbness(event);
        handleMissRate(event);
        handleFoxHurt(event);
        handleLittleHelperNoHurt(event);
        handleImmueToArrow(event);
        handleMartusArrowImmue(event);
        handleOceanWitherImmunity(event);
        handleInquisitionFriendlyFire(event);
        handleDamagePrevention(event);
        handleEndermanTeleport(event);
        handleHighmoreCounter(event);
        handleHighmoreScytheEntityAttack(event);
        handleOceanWitherExtraAttack(event);
        handlePathshaperHit(event);
        handleTidutantArmorBreak(event);
        handleMobHit(event);
        handlePlayerHit(event);
    }

    private static void handleInvulnerable(LivingAttackEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (damagesource == null || entity == null) return;

        if (damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inv_killer")))) return;

        if (entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(CaerulaArborModMobEffects.INVULNERABLE.get())) {
            event.setCanceled(true);
        }
    }

    private static void handleNumbness(LivingAttackEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity sourceentity = event.getSource().getEntity();

        if (sourceentity == null) return;
        if (event.isCanceled()) return;

        double numb = sourceentity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(CaerulaArborModAttributes.NUMB.get())
                ? _livingEntity1.getAttribute(CaerulaArborModAttributes.NUMB.get()).getBaseValue()
                : 0;

        if (numb > 0) {
            if (sourceentity instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(CaerulaArborModAttributes.NUMB.get()))
                _livingEntity2.getAttribute(CaerulaArborModAttributes.NUMB.get()).setBaseValue((numb - 1));
            if (world instanceof ServerLevel _level)
                _level.sendParticles((SimpleParticleType) (CaerulaArborModParticleTypes.NUMBNESS.get()), (sourceentity.getX()), (sourceentity.getY() + 1), (sourceentity.getZ()), 12, 1, 1, 1, 0.1);
            if (world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sign.waxed_interact_fail")), SoundSource.HOSTILE, 2, 1);
            }
            event.setCanceled(true);
        }
    }

    private static void handleMissRate(LivingAttackEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (damagesource == null || entity == null) return;
        if (event.isCanceled()) return;

        double missRate = entity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(CaerulaArborModAttributes.MISSRATE.get())
                ? _livingEntity1.getAttribute(CaerulaArborModAttributes.MISSRATE.get()).getValue()
                : 0;

        if (missRate > 0) {
            if (!damagesource.is(TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bypass_miss")))) {
                if (!(entity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(CaerulaArborModMobEffects.MUTE.get()))) {
                    if (Math.random() * 100 < missRate) {
                        if (world instanceof ServerLevel _level)
                            _level.sendParticles((SimpleParticleType) (CaerulaArborModParticleTypes.MISS.get()), x, y, z, 6, 1, 1, 1, 0.1);
                        if (entity instanceof PredatorAbyssalEntity) {
                            ((PredatorAbyssalEntity) entity).setAnimation("animation.predator.miss");
                        }
                        if (entity instanceof ChitinGolemEntity) {
                            ((ChitinGolemEntity) entity).setAnimation("animation.chitgolem.block");
                        }
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    private static void handleFoxHurt(LivingAttackEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (damagesource == null || entity == null) return;

        if (entity instanceof OceanizedFoxEntity && damagesource.is(DamageTypes.SWEET_BERRY_BUSH)) {
            event.setCanceled(true);
        }
    }

    private static void handleLittleHelperNoHurt(LivingAttackEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();

        if (entity == null) return;

        if (entity instanceof LittleHelperEntity || entity instanceof Al1SHelperEntity) {
            event.setCanceled(true);

            boolean isAl1s = entity instanceof LittleHelperEntity;
            double dur;

            if (isAl1s) {
                dur = entity instanceof LittleHelperEntity _datEntI ? _datEntI.getEntityData().get(LittleHelperEntity.DATA_durability) : 0;
            } else {
                dur = entity instanceof Al1SHelperEntity _datEntI ? _datEntI.getEntityData().get(Al1SHelperEntity.DATA_durability) : 0;
            }

            if (dur > 0) {
                dur = dur - 1;
                if (entity instanceof LittleHelperEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(LittleHelperEntity.DATA_durability, (int) dur);
                if (entity instanceof Al1SHelperEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(Al1SHelperEntity.DATA_durability, (int) dur);
            } else {
                if (dur >= 0 && entity.isAlive()) {
                    if (!entity.level().isClientSide())
                        entity.discard();
                    if (isAl1s) {
                        if (world instanceof ServerLevel _level) {
                            ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(CaerulaArborModItems.ITEM_HELPER.get()));
                            entityToSpawn.setPickUpDelay(10);
                            entityToSpawn.setUnlimitedLifetime();
                            _level.addFreshEntity(entityToSpawn);
                        }
                    } else {
                        if (world instanceof ServerLevel _level) {
                            ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(CaerulaArborModItems.ITEM_HELPER_AL_1S.get()));
                            entityToSpawn.setPickUpDelay(10);
                            entityToSpawn.setUnlimitedLifetime();
                            _level.addFreshEntity(entityToSpawn);
                        }
                        if (!world.isClientSide()) {
                            if (world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "al1s_break")), SoundSource.BLOCKS, 3, 1);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void handleImmueToArrow(LivingAttackEvent event) {
        DamageSource damageSource = event.getSource();
        LivingEntity entity = event.getEntity();
        Entity immediatesourceentity = event.getSource().getDirectEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (entity == null || damageSource == null) return;

        if (damageSource.is(TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bypasses_enderman")))) return;
        if (entity.hasEffect(CaerulaArborModMobEffects.DIZZY.get()) || entity.hasEffect(CaerulaArborModMobEffects.MUTE.get())) return;

        if (entity instanceof OceanizedEndermanEntity) {
            if (immediatesourceentity != sourceentity || !(sourceentity instanceof LivingEntity)) {
                event.setCanceled(true);
                Mob _entity = (Mob) entity;
                if (sourceentity instanceof LivingEntity _ent)
                    _entity.setTarget(_ent);

                LevelAccessor world = entity.level();
                double x = entity.getX();
                double y = entity.getY();
                double z = entity.getZ();

                if (entity.isAlive()) {
                    for (int index0 = 0; index0 < 64; index0++) {
                        double angl = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                        double dist = Mth.nextDouble(RandomSource.create(), 3, 6);
                        double tX = x + dist * Math.sin(angl);
                        double tZ = z + dist * Math.cos(angl);
                        double validY = WorldUtils.findValidY(world, tX, y, tZ);
                        if (validY <= 114513) {
                            EntityUtils.teleportTo(world, entity, x, y, z, tX, validY, tZ);
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void handleMartusArrowImmue(LivingAttackEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (damagesource == null || entity == null || sourceentity == null) return;

        if (entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CaerulaArborModMobEffects.MARTUS_PROTECTION.get())) {
            if (damagesource.is(TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("minecraft:is_projectile"))) && (entity != null ? sourceentity.distanceTo(entity) : -1) > 2
                    && amount <= (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 2) {
                event.setCanceled(true);
            } else if ((sourceentity != null ? entity.distanceTo(sourceentity) : -1) > 3) {
                if (Math.random() < 0.5) {
                    event.setCanceled(true);
                }
            }
        }
    }

    private static void handleOceanWitherImmunity(LivingAttackEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (damagesource == null || entity == null) return;

        if ((entity instanceof OceanizedWitherEntity _datEntL0 && _datEntL0.getEntityData().get(OceanizedWitherEntity.DATA_shelled)
                || entity instanceof OceannizedWitheriaEntity _datEntL1 && _datEntL1.getEntityData().get(OceannizedWitheriaEntity.DATA_shelled))
                && damagesource.is(TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("minecraft:is_projectile")))) {
            event.setCanceled(true);
        }
    }

    private static void handleInquisitionFriendlyFire(LivingAttackEvent event) {
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (entity == null || sourceentity == null) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inquisition")))
                && sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inquisition")))) {
            event.setCanceled(true);
        }
    }

    private static void handleDamagePrevention(LivingAttackEvent event) {
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        LevelAccessor world = entity.level();

        preventSameTeamDamage(event, world, entity, sourceentity);
        preventInquisitionDamage(event, entity, sourceentity);
        preventHumanSideFriendlyFire(event, entity, sourceentity);
    }

    private static void preventSameTeamDamage(LivingAttackEvent event, LevelAccessor world, Entity entity, Entity sourceentity) {
        if (entity == null || sourceentity == null) return;
        if (sourceentity instanceof Player) return;
        if (entity instanceof Player) return;

        if (world.getLevelData().getGameRules().getBoolean(CaerulaArborModGameRules.AGGRESIVE_MODE) || !world.getLevelData().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING)) {
            if (EntityUtils.isSameTeam(entity, sourceentity)) {
                event.setCanceled(true);
            }
        }
    }

    private static void preventInquisitionDamage(LivingAttackEvent event, Entity entity, Entity sourceentity) {
        if (entity == null || sourceentity == null) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inquisition"))) && sourceentity instanceof Player) {
            if ((entity.getPersistentData().getString("recentCommander")).equals(sourceentity.getDisplayName().getString())) {
                event.setCanceled(true);
            }
            if ((sourceentity instanceof LivingEntity _entity) ? _entity.isHolding(CaerulaArborModItems.INTERPHONE.get()) : false) {
                entity.getPersistentData().putString("recentCommander", "");
            }
        }
    }

    private static void preventHumanSideFriendlyFire(LivingAttackEvent event, Entity entity, Entity sourceentity) {
        if (entity == null || sourceentity == null) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")))) {
            if (sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")))
                    && !(sourceentity == (entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null))) {
                event.setCanceled(true);
            }
        }
    }

    private static void handleEndermanTeleport(LivingAttackEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (entity == null || sourceentity == null) return;

        if (entity instanceof OceanizedEndermanEntity) {
            if (entity.distanceTo(sourceentity) >= 6) {
                double sx = sourceentity.getX();
                double sy = sourceentity.getY();
                double sz = sourceentity.getZ();
                if (WorldUtils.isValidPlace(world, sx, sy, sz)) {
                    if (!sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))
                            || (entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == sourceentity) {
                        EntityUtils.teleportTo(world, entity, x, y, z, sx, sy, sz);
                        EntityUtils.deductSanity(sourceentity, (entity instanceof LivingEntity _livingEntity8 && _livingEntity8.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                                ? _livingEntity8.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 15);
                        if (entity instanceof Mob _entity && sourceentity instanceof LivingEntity _ent)
                            _entity.setTarget(_ent);
                        sourceentity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_magic"))), entity),
                                (float) ((entity instanceof LivingEntity _livingEntity10 && _livingEntity10.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                                        ? _livingEntity10.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.75));
                    }
                }
            }
        }
    }

    private static void handleHighmoreCounter(LivingAttackEvent event) {
        LevelAccessor world = event.getEntity().level();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (damagesource == null || entity == null || sourceentity == null) return;

        if (entity instanceof HighmoreEntity livEnt1) {
            if (livEnt1.hasEffect(CaerulaArborModMobEffects.COOLDOWN_SINAL.get())) return;

            if (!(damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hand_spike"))) || damagesource.is(DamageTypes.THORNS) || sourceentity instanceof HighmoreEntity)) {
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.COOLDOWN_SINAL.get(), 100, 0, false, false));

                double range;
                if ((entity instanceof HighmoreEntity _datEntI ? _datEntI.getEntityData().get(HighmoreEntity.DATA_phase) : 0) == 0) {
                    range = 7;
                } else if ((entity instanceof HighmoreEntity _datEntI ? _datEntI.getEntityData().get(HighmoreEntity.DATA_phase) : 0) == 1) {
                    range = 11;
                } else {
                    range = 17;
                }

                if (entity.distanceTo(sourceentity) >= range) {
                    double atk = (entity instanceof LivingEntity _livingEntity10 && _livingEntity10.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                            ? _livingEntity10.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 2.5;

                    spawnHighmoreProjectile(world, entity, sourceentity, atk, sourceentity.getX(), sourceentity.getY() + sourceentity.getBbHeight() + 4, sourceentity.getZ(), 0, -1, 0);
                    spawnHighmoreProjectile(world, entity, sourceentity, atk, sourceentity.getX() + sourceentity.getBbWidth() * 2, sourceentity.getY() + sourceentity.getBbHeight(), sourceentity.getZ(), -1, 0, 0);
                    spawnHighmoreProjectile(world, entity, sourceentity, atk, sourceentity.getX() - sourceentity.getBbWidth() * 2, sourceentity.getY() + sourceentity.getBbHeight(), sourceentity.getZ(), 1, 0, 0);
                    spawnHighmoreProjectile(world, entity, sourceentity, atk, sourceentity.getX(), sourceentity.getY() + sourceentity.getBbHeight(), sourceentity.getZ() + sourceentity.getBbWidth() * 2, 0, 0, -1);
                    spawnHighmoreProjectile(world, entity, sourceentity, atk, sourceentity.getX(), sourceentity.getY() + sourceentity.getBbHeight(), sourceentity.getZ() - sourceentity.getBbWidth() * 2, 0, 0, 1);
                }
            }
        }
    }

    private static void spawnHighmoreProjectile(LevelAccessor world, Entity shooter, Entity target, double damage, double x, double y, double z, double dx, double dy, double dz) {
        if (Math.random() >= 0.5) return;
        if (!(world instanceof ServerLevel projectileLevel)) return;

        HighmoreShootEntity entityToSpawn = new HighmoreShootEntity(CaerulaArborModEntities.HIGHMORE_SHOOT.get(), projectileLevel);
        entityToSpawn.setOwner(shooter);
        entityToSpawn.setBaseDamage((float) damage);
        entityToSpawn.setKnockback(0);
        entityToSpawn.setSilent(true);
        entityToSpawn.setPierceLevel((byte) 1);
        entityToSpawn.setPos(x, y, z);
        entityToSpawn.shoot(dx, dy, dz, 2, 2);
        projectileLevel.addFreshEntity(entityToSpawn);
    }

    private static void handleHighmoreScytheEntityAttack(LivingAttackEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (damagesource == null || entity == null || sourceentity == null) return;

        if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.HIGHMORE_SCYTHE.get()
                && damagesource.is(DamageTypes.PLAYER_ATTACK)) {
            if (entity == null || sourceentity == null)
                return;
            if ((sourceentity instanceof Player _plr ? _plr.getAttackStrengthScale(0) : 0) >= 0.95) {
                EntityUtils.giveLessArmor(entity, 15);
                if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof HighmoreScytheItem)
                    (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().putString("geckoAnim", "animation.highmore_scythe.attack");
                CaerulaArborMod.queueServerWork(10, () -> {
                    if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.HIGHMORE_SCYTHE.get()) {
                        if (world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "scythe_highmore")), SoundSource.PLAYERS, (float) 1.5, 1);
                        }
                        {
                            final Vec3 _center = new Vec3(x, (y + 0.5), z);
                            List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                            for (Entity entityiterator : _entfound) {
                                if ((sourceentity != null ? entityiterator.distanceTo(sourceentity) : -1) <= 4) {
                                    if (entityiterator instanceof LivingEntity && !(entityiterator == sourceentity)) {
                                        if (!(entityiterator instanceof TamableAnimal _tamIsTamedBy && sourceentity instanceof LivingEntity _livEnt ? _tamIsTamedBy.isOwnedBy(_livEnt) : false)) {
                                            entityiterator.hurt(
                                                    new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "highmore_attack"))),
                                                            sourceentity),
                                                    (float) ((sourceentity instanceof LivingEntity _livingEntity10 && _livingEntity10.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                                                            ? _livingEntity10.getAttribute(Attributes.ATTACK_DAMAGE).getValue()
                                                            : 0) * (1.5 + 0.2 * (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getEnchantmentLevel(CaerulaArborModEnchantments.SYNESTHESIA.get()))));
                                            EntityUtils.giveLessArmor(entityiterator, 15);
                                        }
                                    }
                                }
                            }
                        }
                        if (!(new Object() {
                            public boolean checkGamemode(Entity _ent) {
                                if (_ent instanceof ServerPlayer _serverPlayer) {
                                    return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                                } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                                    return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                            && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                                }
                                return false;
                            }
                        }.checkGamemode(sourceentity))) {
                            {
                                ItemStack _ist = (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY);
                                if (_ist.hurt(1, RandomSource.create(), null)) {
                                    _ist.shrink(1);
                                    _ist.setDamageValue(0);
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    private static void handleOceanWitherExtraAttack(LivingAttackEvent event) {
        LevelAccessor world = event.getEntity().level();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity immediatesourceentity = event.getSource().getDirectEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (damagesource == null || entity == null || immediatesourceentity == null || sourceentity == null) return;
        if (event.isCanceled()) return;
        if (sourceentity == entity) return;

        if (sourceentity instanceof OceanizedWitherEntity || sourceentity instanceof OceannizedWitheriaEntity) {
            if (damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_wither"))) || damagesource.is(DamageTypes.WITHER_SKULL)) {
                entity.hurt(
                        new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_magic"))), immediatesourceentity, sourceentity),
                        (float) (sourceentity instanceof LivingEntity _livingEntity6 && _livingEntity6.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                                ? _livingEntity6.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
                entity.invulnerableTime = 0;
            }
        }
    }

    private static void handlePathshaperHit(LivingAttackEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity sourceentity = event.getSource().getEntity();

        if (sourceentity == null) return;
        if (event.isCanceled()) return;

        if (sourceentity instanceof RouteShaperEntity) {
            double sklp = sourceentity instanceof RouteShaperEntity _datEntI ? _datEntI.getEntityData().get(RouteShaperEntity.DATA_skillp) : 0;
            if (sourceentity instanceof RouteShaperEntity _datEntSetI)
                _datEntSetI.getEntityData().set(RouteShaperEntity.DATA_skillp, (int) (sklp + 1));
            if (sklp + 1 >= 8) {
                SummonFractalProcedure.execute(world, x, y, z, sourceentity);
                if (sourceentity instanceof RouteShaperEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(RouteShaperEntity.DATA_skillp, 0);
            }
        }
        if (sourceentity instanceof RouteFractalEntity) {
            double sklp = sourceentity instanceof RouteFractalEntity _datEntI ? _datEntI.getEntityData().get(RouteFractalEntity.DATA_skillp) : 0;
            if (sourceentity instanceof RouteFractalEntity _datEntSetI)
                _datEntSetI.getEntityData().set(RouteFractalEntity.DATA_skillp, (int) (sklp + 1));
            if (sklp + 1 >= 6) {
                SummonFractalProcedure.execute(world, x, y, z, sourceentity);
                if (sourceentity instanceof RouteFractalEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(RouteFractalEntity.DATA_skillp, 0);
            }
        }
        if (sourceentity instanceof LineringPathshaperEntity) {
            double sklp = sourceentity instanceof LineringPathshaperEntity _datEntI ? _datEntI.getEntityData().get(LineringPathshaperEntity.DATA_skillp) : 0;
            if (sourceentity instanceof LineringPathshaperEntity _datEntSetI)
                _datEntSetI.getEntityData().set(LineringPathshaperEntity.DATA_skillp, (int) (sklp + 1));
            if (sklp + 1 >= 8) {
                SummonFractalProcedure.execute(world, x, y, z, sourceentity);
                if (sourceentity instanceof LineringPathshaperEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(LineringPathshaperEntity.DATA_skillp, 0);
            }
        }
        if (sourceentity instanceof LingeringFractalEntity) {
            double sklp = sourceentity instanceof LingeringFractalEntity _datEntI ? _datEntI.getEntityData().get(LingeringFractalEntity.DATA_skillp) : 0;
            if (sourceentity instanceof LingeringFractalEntity _datEntSetI)
                _datEntSetI.getEntityData().set(LingeringFractalEntity.DATA_skillp, (int) (sklp + 1));
            if (sklp + 1 >= 6) {
                SummonFractalProcedure.execute(world, x, y, z, sourceentity);
                if (sourceentity instanceof LingeringFractalEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(LingeringFractalEntity.DATA_skillp, 0);
            }
        }
    }

    private static void handleTidutantArmorBreak(LivingAttackEvent event) {
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (entity == null || sourceentity == null) return;

        if (sourceentity instanceof TideutantRockSpiderEntity) {
            EntityUtils.giveLessArmor(entity, 8);
        } else if (sourceentity instanceof TidutantExcrescenceEntity) {
            EntityUtils.giveLessArmor(entity, 4);
        }
    }

    private static void handleMobHit(LivingAttackEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (damagesource == null || entity == null || sourceentity == null) return;
        if (event.isCanceled()) return;

        handleMobHitMigration(event, world, x, y, z, damagesource, entity, sourceentity, amount);
        handleMobHitEvolution(event, world, x, y, z, damagesource, entity, sourceentity, amount);
        handleMobHitSpecialEffects(event, world, x, y, z, damagesource, entity, sourceentity, amount);
    }

    private static void handleMobHitMigration(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, DamageSource damagesource, Entity entity, Entity sourceentity, double amount) {
        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))
                && !sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))
                && !entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanpet")))
                && !entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "skip_migration")))) {
            if (CaerulaArborModVariables.MapVariables.get(world).strategy_migration > 0) {
                if (!isCreativePlayer(sourceentity) && !damagesource.is(TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bypasses_migration")))) {
                    for (Entity entityiterator : world.getEntities(entity,
                            new AABB((x - (8 + CaerulaArborModVariables.MapVariables.get(world).strategy_migration * 16)), (y - 16), (z - (8 + CaerulaArborModVariables.MapVariables.get(world).strategy_migration * 16)),
                                    (x + 8 + CaerulaArborModVariables.MapVariables.get(world).strategy_migration * 24), (y + 16), (z + 8 + CaerulaArborModVariables.MapVariables.get(world).strategy_migration * 24)))) {
                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))
                                && !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanpet")))) {
                            if (entityiterator == sourceentity) continue;
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ignore_migration")))) continue;
                            if (entityiterator instanceof Mob _entity)
                                _entity.getNavigation().moveTo(x, y, z, 0.5);
                            if (entityiterator instanceof Mob _entity && sourceentity instanceof LivingEntity _ent)
                                _entity.setTarget(_ent);
                        }
                    }
                }
            }
        }

        if (entity instanceof Player && (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_oceanization >= 3) {
            if (CaerulaArborModVariables.MapVariables.get(world).strategy_migration > 0 && !sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                for (Entity entityiterator : world.getEntities(entity,
                        new AABB((x - (8 + CaerulaArborModVariables.MapVariables.get(world).strategy_migration * 24)), (y - 16), (z - (8 + CaerulaArborModVariables.MapVariables.get(world).strategy_migration * 24)),
                                (x + 8 + CaerulaArborModVariables.MapVariables.get(world).strategy_migration * 24), (y + 16), (z + 8 + CaerulaArborModVariables.MapVariables.get(world).strategy_migration * 24)))) {
                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))
                            && !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanpet")))) {
                        if (entityiterator == sourceentity) continue;
                        if (entityiterator instanceof Mob _entity)
                            _entity.getNavigation().moveTo(x, y, z, 0.8);
                        if (entityiterator instanceof Mob _entity && sourceentity instanceof LivingEntity _ent)
                            _entity.setTarget(_ent);
                    }
                }
            }
        }
    }

    private static void handleMobHitEvolution(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, DamageSource damagesource, Entity entity, Entity sourceentity, double amount) {
        if (sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
            if (world.getLevelData().getGameRules().getBoolean(CaerulaArborModGameRules.NATURAL_EVOLUTION)) {
                CaerulaArborModVariables.MapVariables.get(world).evo_point_grow = CaerulaArborModVariables.MapVariables.get(world).evo_point_grow + amount * 0.025;
                CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                UpgradeGrowProcedure.execute(world);
                UpgradeSilenceProcedure.execute(world, amount * 0.025);
            }
        }

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
            if (sourceentity instanceof ServerPlayer _player) {
                Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "encounter_from_the_ocean"));
                AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                if (!_ap.isDone()) {
                    for (String criteria : _ap.getRemainingCriteria())
                        _player.getAdvancements().award(_adv, criteria);
                }
            }

            if (world.getLevelData().getGameRules().getBoolean(CaerulaArborModGameRules.NATURAL_EVOLUTION)
                    && !damagesource.is(TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bypasses_evolution")))) {
                CaerulaArborModVariables.MapVariables.get(world).evo_point_subsisting = CaerulaArborModVariables.MapVariables.get(world).evo_point_subsisting
                        + Math.min(amount, entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.025;
                CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                UpgradeSubsisProcedure.execute(world);
                UpgradeSilenceProcedure.execute(world, Math.min(amount, entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.025);
            }

            if (sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                if (!(entity == (sourceentity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null))) {
                    event.setCanceled(true);
                }
            }

            if (entity instanceof LivingEntity _livingEntity71 && _livingEntity71.getAttributes().hasAttribute(CaerulaArborModAttributes.EVOLVED.get()))
                _livingEntity71.getAttribute(CaerulaArborModAttributes.EVOLVED.get()).setBaseValue(1);
        }
    }

    private static void handleMobHitSpecialEffects(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, DamageSource damagesource, Entity entity, Entity sourceentity, double amount) {
        if (sourceentity instanceof BoneFishEntity) {
            EntityUtils.giveLessArmor(entity, 1);
        }
        if (sourceentity instanceof FakeOffspringEntity) {
            EntityUtils.giveLessArmor(entity, 2);
        }
        if (sourceentity instanceof ChitinGolemEntity || sourceentity instanceof ComplexChitinGolemEntity) {
            if (entity.getBbWidth() * entity.getBbHeight() <= 6 && !damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "golem_attack")))) {
                entity.push(0, 0.5, 0);
            }
        }
        if (sourceentity instanceof HighmoreEntity) {
            if (!(sourceentity == entity)) {
                EntityUtils.giveLessArmor(entity, 21);
            }
        }

        if (entity instanceof SpikeChestEntity) {
            if (entity.isAlive()) {
                sourceentity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "chest_spike")))),
                        (float) (amount * 0.33));
            }
        }
    }

    private static void handlePlayerHit(LivingAttackEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        Entity immediatesourceentity = event.getSource().getDirectEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (entity == null || immediatesourceentity == null || sourceentity == null) return;

        ItemStack mainHandItem = (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).copy();

        if (EnchantmentHelper.getItemEnchantmentLevel(CaerulaArborModEnchantments.MUTE_ATTACK.get(), mainHandItem) != 0) {
            if (Math.random() < 0.2 * mainHandItem.getEnchantmentLevel(CaerulaArborModEnchantments.MUTE_ATTACK.get())) {
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.MUTE.get(), (int) (30 * mainHandItem.getEnchantmentLevel(CaerulaArborModEnchantments.MUTE_ATTACK.get())), 0, false, false));
            }
        }

        if (event.isCanceled()) return;

        if (sourceentity instanceof Player) {
            handlePlayerHitRelics(event, world, x, y, z, entity, immediatesourceentity, sourceentity, amount, mainHandItem);
        }
    }

    private static void handlePlayerHitRelics(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity immediatesourceentity, Entity sourceentity, double amount, ItemStack mainHandItem) {
        if (!(immediatesourceentity == sourceentity)) {
            String rname = ForgeRegistries.ITEMS.getKey(mainHandItem.getItem()).toString();
            boolean validItem;

            if ((sourceentity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_hand_STRANGLE) {
                validItem = false;
                if (mainHandItem.is(ItemTags.create(new ResourceLocation("forge:tools/crossbows")))) {
                    validItem = true;
                } else if (mainHandItem.getItem() instanceof CrossbowItem) {
                    validItem = true;
                } else {
                    for (String stringiterator : CaerulaConfigsConfiguration.HAND_STRANGLE.get()) {
                        if (ValidationUtils.isValidString(stringiterator, rname)) {
                            validItem = true;
                            break;
                        }
                    }
                }
                if (validItem && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.25) {
                    if (entity.isAlive()) {
                        entity.hurt(
                                new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hand_of_choker"))), sourceentity),
                                (float) ((entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 99));
                        if (world instanceof ServerLevel _level)
                            _level.sendParticles(ParticleTypes.GLOW_SQUID_INK, (entity.getX()), (entity.getY()), (entity.getZ()), 128, 1, 1, 1, 0.33);
                        if (world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wither.hurt")), SoundSource.NEUTRAL, 2, 1);
                        }
                    }
                }
            }

            if ((sourceentity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_hand_FIREWORK) {
                validItem = false;
                if (mainHandItem.getItem() == Items.BOW) {
                    validItem = true;
                } else if (mainHandItem.getItem() instanceof BowItem) {
                    validItem = true;
                } else if (mainHandItem.getItem() == CaerulaArborModItems.PHLOEM_BOW.get()) {
                    validItem = true;
                } else if (mainHandItem.is(ItemTags.create(new ResourceLocation("forge:tools/bows")))) {
                    validItem = true;
                } else {
                    for (String stringiterator : CaerulaConfigsConfiguration.HAND_FIREWORK.get()) {
                        if (ValidationUtils.isValidString(stringiterator, rname)) {
                            validItem = true;
                            break;
                        }
                    }
                }
                if (validItem && Math.random() < 0.33) {
                    if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(sourceentity.getX(), sourceentity.getY(), sourceentity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.firework_rocket.launch")), SoundSource.PLAYERS, (float) 3.6, 1);
                    }
                    CaerulaArborMod.queueServerWork(10, () -> {
                        if (world instanceof ServerLevel _level)
                            _level.sendParticles(ParticleTypes.FIREWORK, x, y, z, 85, 2, 2, 2, 0.22);
                        if (world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.firework_rocket.twinkle")), SoundSource.PLAYERS, (float) 3.6, 1);
                        }
                        final Vec3 _center = new Vec3(x, y, z);
                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                        for (Entity entityiterator : _entfound) {
                            if (((ForgeRegistries.ENTITY_TYPES.getKey(entityiterator.getType()).toString()).equals(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString()) || entityiterator instanceof Monster)
                                    && !(entityiterator == sourceentity)) {
                                entityiterator.hurt(
                                        new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hand_firework"))), sourceentity),
                                        (float) (amount * 3));
                            }
                        }
                    });
                }
            }
        }

        if ((sourceentity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_legend_CHITIN) {
            if (Math.random() < 0.05) {
                ItemStack _setval = (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY);
                sourceentity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                    capability.chitin_knife_selected = _setval.copy();
                    capability.syncPlayerVariables(sourceentity);
                });
                double perc = EntityUtils.getHealthPerc(sourceentity);
                if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.TIDE_OF_CHITIN.get(), 500, 0, false, false));
                if (perc > 0) {
                    if (sourceentity instanceof LivingEntity _entity)
                        _entity.setHealth((float) ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * perc));
                }
                if (world instanceof Level _level) {
                    if (_level.isClientSide()) {
                        _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.beacon.activate")), SoundSource.NEUTRAL, (float) 3.2, 1, false);
                    }
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
