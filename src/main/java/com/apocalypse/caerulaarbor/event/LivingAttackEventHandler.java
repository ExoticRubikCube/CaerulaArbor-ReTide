package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import com.apocalypse.caerulaarbor.capability.map.MapVariablesHandler;
import com.apocalypse.caerulaarbor.capability.map.MapVariablesHandler.StrategyType;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.init.CAConfigs;
import com.apocalypse.caerulaarbor.entity.*;
import com.apocalypse.caerulaarbor.entity.bullets.HighmoreShootEntity;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.manager.GrowUpgradeManager;
import com.apocalypse.caerulaarbor.manager.SilenceUpgradeManager;
import com.apocalypse.caerulaarbor.manager.SubsistingUpgradeManager;
import com.apocalypse.caerulaarbor.util.CaerulaUtil;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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
        handleMartusArrowImmue(event);
        handleInquisitionFriendlyFire(event);
        handleDamagePrevention(event);
        handleHighmoreCounter(event);
        handleTidutantArmorBreak(event);
        handleMobHit(event);
        handlePlayerHit(event);
    }

    private static void handleInvulnerable(LivingAttackEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (damagesource == null || entity == null || damagesource.is(CADamageTypes.INV_KILLER)) return;

        if (entity instanceof LivingEntity livEnt1 && livEnt1.hasEffect(CAMobEffects.INVULNERABLE.get())) {
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

        double numb = sourceentity instanceof LivingEntity livingEntity1 && livingEntity1.getAttributes().hasAttribute(CAAttributes.NUMB.get())
                ? livingEntity1.getAttribute(CAAttributes.NUMB.get()).getBaseValue()
                : 0;

        if (numb > 0) {
            if (sourceentity instanceof LivingEntity livingEntity2 && livingEntity2.getAttributes().hasAttribute(CAAttributes.NUMB.get()))
                livingEntity2.getAttribute(CAAttributes.NUMB.get()).setBaseValue((numb - 1));
            if (world instanceof ServerLevel level)
                level.sendParticles(CAParticles.NUMBNESS.get(), (sourceentity.getX()), (sourceentity.getY() + 1), (sourceentity.getZ()), 12, 1, 1, 1, 0.1);
            if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WAXED_SIGN_INTERACT_FAIL, SoundSource.HOSTILE, 2, 1);
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

        double missRate = entity instanceof LivingEntity livingEntity1 && livingEntity1.getAttributes().hasAttribute(CAAttributes.MISSRATE.get())
                ? livingEntity1.getAttribute(CAAttributes.MISSRATE.get()).getValue()
                : 0;

        if (missRate > 0) {
            if (!damagesource.is(CADamageTags.BYPASS_MISS)) {
                if (!(entity instanceof LivingEntity livEnt3 && livEnt3.hasEffect(CAMobEffects.MUTE.get()))) {
                    if (Math.random() * 100 < missRate) {
                        if (world instanceof ServerLevel level)
                            level.sendParticles(CAParticles.MISS.get(), x, y, z, 6, 1, 1, 1, 0.1);
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

    private static void handleMartusArrowImmue(LivingAttackEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (damagesource == null || entity == null || sourceentity == null) return;

        if (entity instanceof LivingEntity livEnt0 && livEnt0.hasEffect(CAMobEffects.MARTUS_PROTECTION.get())) {
            if (damagesource.is(DamageTypeTags.IS_PROJECTILE) && sourceentity.distanceTo(entity) > 2 && amount <= (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 2) {
                event.setCanceled(true);
            } else if (entity.distanceTo(sourceentity) > 3) {
                if (Math.random() < 0.5) {
                    event.setCanceled(true);
                }
            }
        }
    }

    private static void handleInquisitionFriendlyFire(LivingAttackEvent event) {
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (entity == null || sourceentity == null) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "inquisition")))
                && sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "inquisition")))) {
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

        if (world.getLevelData().getGameRules().getBoolean(CAGameRules.AGGRESIVE_MODE) || !world.getLevelData().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING)) {
            if (EntityUtils.isSameTeam(entity, sourceentity)) {
                event.setCanceled(true);
            }
        }
    }

    private static void preventInquisitionDamage(LivingAttackEvent event, Entity entity, Entity sourceentity) {
        if (entity == null || sourceentity == null) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "inquisition"))) && sourceentity instanceof Player entity1) {
            if ((entity.getPersistentData().getString("recentCommander")).equals(sourceentity.getDisplayName().getString())) {
                event.setCanceled(true);
            }
            if (entity1.isHolding(CAItems.INTERPHONE.get())) {
                entity.getPersistentData().putString("recentCommander", "");
            }
        }
    }

    private static void preventHumanSideFriendlyFire(LivingAttackEvent event, Entity entity, Entity sourceentity) {
        if (entity == null || sourceentity == null) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "is_humanside")))) {
            if (sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "is_humanside")))
                    && !(sourceentity == (entity instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null))) {
                event.setCanceled(true);
            }
        }
    }

    // TODO锛欻ighmore 鍙嶅嚮閫昏緫浠嶉渶澶嶆牳锛屽彲鑳介渶瑕佷笅鏀?
    private static void handleHighmoreCounter(LivingAttackEvent event) {
        LevelAccessor world = event.getEntity().level();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (damagesource == null || entity == null || sourceentity == null) return;

        if (entity instanceof HighmoreEntity livEnt1) {
            if (livEnt1.hasEffect(CAMobEffects.COOLDOWN_SINAL.get())) return;

            if (!(damagesource.is(CADamageTypes.HAND_SPIKE) || damagesource.is(DamageTypes.THORNS) || sourceentity instanceof HighmoreEntity)) {
                if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                    livingEntity.addEffect(new MobEffectInstance(CAMobEffects.COOLDOWN_SINAL.get(), 100, 0, false, false));

                double range;
                if ((entity instanceof HighmoreEntity datEntI ? datEntI.getEntityData().get(HighmoreEntity.DATA_PHASE) : 0) == 0) {
                    range = 7;
                } else if ((entity instanceof HighmoreEntity datEntI ? datEntI.getEntityData().get(HighmoreEntity.DATA_PHASE) : 0) == 1) {
                    range = 11;
                } else {
                    range = 17;
                }

                if (entity.distanceTo(sourceentity) >= range) {
                    double atk = (entity instanceof LivingEntity livingEntity10 && livingEntity10.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                            ? livingEntity10.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 2.5;

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

        HighmoreShootEntity entityToSpawn = new HighmoreShootEntity(CAEntities.HIGHMORE_SHOOT.get(), projectileLevel);
        entityToSpawn.setOwner(shooter);
        entityToSpawn.setBaseDamage((float) damage);
        entityToSpawn.setKnockback(0);
        entityToSpawn.setSilent(true);
        entityToSpawn.setPierceLevel((byte) 1);
        entityToSpawn.setPos(x, y, z);
        entityToSpawn.shoot(dx, dy, dz, 2, 2);
        projectileLevel.addFreshEntity(entityToSpawn);
    }

    // TODO锛歍idutant 鐮寸敳閫昏緫浠嶉渶澶嶆牳
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
        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))
                && !sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))
                && !entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanpet")))
                && !entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "skip_migration")))) {
            if (MapVariables.get(world).strategy_migration > 0) {
                if (!(sourceentity instanceof Player player && player.getAbilities().instabuild)
                        && !damagesource.is(CADamageTags.BYPASSES_MIGRATION)) {
                    for (Entity entityiterator : world.getEntities(entity,
                            new AABB((x - (8 + MapVariables.get(world).strategy_migration * 16)), (y - 16), (z - (8 + MapVariables.get(world).strategy_migration * 16)),
                                    (x + 8 + MapVariables.get(world).strategy_migration * 24), (y + 16), (z + 8 + MapVariables.get(world).strategy_migration * 24)))) {
                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))
                                && !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanpet")))) {
                            if (entityiterator == sourceentity) continue;
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "ignore_migration")))) continue;
                            if (entityiterator instanceof Mob mob)
                                mob.getNavigation().moveTo(x, y, z, 0.5);
                            if (entityiterator instanceof Mob mob && sourceentity instanceof LivingEntity livingEntity)
                                mob.setTarget(livingEntity);
                        }
                    }
                }
            }
        }

        if (entity instanceof Player && (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_oceanization >= 3) {
            if (MapVariables.get(world).strategy_migration > 0 && !sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
                for (Entity entityiterator : world.getEntities(entity,
                        new AABB((x - (8 + MapVariables.get(world).strategy_migration * 24)), (y - 16), (z - (8 + MapVariables.get(world).strategy_migration * 24)),
                                (x + 8 + MapVariables.get(world).strategy_migration * 24), (y + 16), (z + 8 + MapVariables.get(world).strategy_migration * 24)))) {
                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))
                            && !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanpet")))) {
                        if (entityiterator == sourceentity) continue;
                        if (entityiterator instanceof Mob mob)
                            mob.getNavigation().moveTo(x, y, z, 0.8);
                        if (entityiterator instanceof Mob mob && sourceentity instanceof LivingEntity livingEntity)
                            mob.setTarget(livingEntity);
                    }
                }
            }
        }
    }

    private static void handleMobHitEvolution(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, DamageSource damagesource, Entity entity, Entity sourceentity, double amount) {
        if (sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
            if (world.getLevelData().getGameRules().getBoolean(CAGameRules.NATURAL_EVOLUTION)) {
                MapVariablesHandler.addEvoPoint(world, StrategyType.GROW, amount * 0.025);
                GrowUpgradeManager.applyGrowthUpgrade(world);
                SilenceUpgradeManager.applySilenceUpgrade(world, amount * 0.025);
            }
        }

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
            if (sourceentity instanceof ServerPlayer player) {
                Advancement adv = player.server.getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "encounter_from_the_ocean"));
                AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
                if (!ap.isDone()) {
                    for (String criteria : ap.getRemainingCriteria())
                        player.getAdvancements().award(adv, criteria);
                }
            }

            if (world.getLevelData().getGameRules().getBoolean(CAGameRules.NATURAL_EVOLUTION)
                    && !damagesource.is(CADamageTags.BYPASSES_EVOLUTION)) {
                MapVariablesHandler.addEvoPoint(world, StrategyType.SUBSISTING,
                        Math.min(amount, entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.025);
                SubsistingUpgradeManager.applySubsistingUpgrade(world);
                SilenceUpgradeManager.applySilenceUpgrade(world, Math.min(amount, entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.025);
            }

            if (sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
                if (!(entity == (sourceentity instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null))) {
                    event.setCanceled(true);
                }
            }

            if (entity instanceof LivingEntity livingEntity71 && livingEntity71.getAttributes().hasAttribute(CAAttributes.EVOLVED.get()))
                livingEntity71.getAttribute(CAAttributes.EVOLVED.get()).setBaseValue(1);
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
            if (entity.getBbWidth() * entity.getBbHeight() <= 6 && !damagesource.is(CADamageTypes.GOLEM_ATTACK)) {
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
                sourceentity.hurt(CADamageTypes.source(world, CADamageTypes.CHEST_SPIKE),
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

        ItemStack mainHandItem = (sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).copy();

        if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.MUTE_ATTACK.get(), mainHandItem) != 0) {
            if (Math.random() < 0.2 * mainHandItem.getEnchantmentLevel(CAEnchantments.MUTE_ATTACK.get())) {
                if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                    livingEntity.addEffect(new MobEffectInstance(CAMobEffects.MUTE.get(), 30 * mainHandItem.getEnchantmentLevel(CAEnchantments.MUTE_ATTACK.get()), 0, false, false));
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

            if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_STRANGLE) {
                validItem = false;
                if (mainHandItem.is(ItemTags.create(ResourceLocation.parse("forge:tools/crossbows")))) {
                    validItem = true;
                } else if (mainHandItem.getItem() instanceof CrossbowItem) {
                    validItem = true;
                } else {
                    for (String stringiterator : CAConfigs.HAND_STRANGLE.get()) {
                        if (CaerulaUtil.matchesRegistryName(stringiterator, rname)) {
                            validItem = true;
                            break;
                        }
                    }
                }
                if (validItem && (entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) < (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.25) {
                    if (entity.isAlive()) {
                        entity.hurt(CADamageTypes.source(world, CADamageTypes.HAND_OF_CHOKER, sourceentity),
                                (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 99);
                        if (world instanceof ServerLevel level)
                            level.sendParticles(ParticleTypes.GLOW_SQUID_INK, (entity.getX()), (entity.getY()), (entity.getZ()), 128, 1, 1, 1, 0.33);
                        if (world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), SoundEvents.WITHER_HURT, SoundSource.NEUTRAL, 2, 1);
                        }
                    }
                }
            }

            if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_FIREWORK) {
                validItem = false;
                if (mainHandItem.getItem() == Items.BOW) {
                    validItem = true;
                } else if (mainHandItem.getItem() instanceof BowItem) {
                    validItem = true;
                } else if (mainHandItem.getItem() == CAItems.PHLOEM_BOW.get()) {
                    validItem = true;
                } else if (mainHandItem.is(ItemTags.create(ResourceLocation.parse("forge:tools/bows")))) {
                    validItem = true;
                } else {
                    for (String stringiterator : CAConfigs.HAND_FIREWORK.get()) {
                        if (CaerulaUtil.matchesRegistryName(stringiterator, rname)) {
                            validItem = true;
                            break;
                        }
                    }
                }
                if (validItem && Math.random() < 0.33) {
                    if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(sourceentity.getX(), sourceentity.getY(), sourceentity.getZ()), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, (float) 3.6, 1);
                    }
                    CaerulaArborMod.queueServerWork(10, () -> {
                        if (world instanceof ServerLevel level)
                            level.sendParticles(ParticleTypes.FIREWORK, x, y, z, 85, 2, 2, 2, 0.22);
                        if (world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.PLAYERS, (float) 3.6, 1);
                        }
                        final Vec3 center = new Vec3(x, y, z);
                        List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                        for (Entity entityiterator : entfound) {
                            if (((ForgeRegistries.ENTITY_TYPES.getKey(entityiterator.getType()).toString()).equals(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString()) || entityiterator instanceof Monster)
                                    && !(entityiterator == sourceentity)) {
                                entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.HAND_FIREWORK, sourceentity),
                                        (float) (amount * 3));
                            }
                        }
                    });
                }
            }
        }

        if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_legend_CHITIN) {
            if (Math.random() < 0.05) {
                ItemStack setval = (sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY);
                sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.chitin_knife_selected = setval.copy();
                    capability.syncPlayerVariables(sourceentity);
                });
                double perc = EntityUtils.getHealthPerc(sourceentity);
                if (sourceentity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                    livingEntity.addEffect(new MobEffectInstance(CAMobEffects.TIDE_OF_CHITIN.get(), 500, 0, false, false));
                if (perc > 0) {
                    if (sourceentity instanceof LivingEntity livingEntity)
                        livingEntity.setHealth((float) ((sourceentity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * perc));
                }
                if (world instanceof Level level) {
                    if (level.isClientSide()) {
                        level.playLocalSound(x, y, z, SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, (float) 3.2, 1, false);
                    }
                }
            }
        }
    }
}
