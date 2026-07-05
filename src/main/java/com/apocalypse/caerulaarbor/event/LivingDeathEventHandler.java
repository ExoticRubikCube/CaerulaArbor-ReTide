package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import com.apocalypse.caerulaarbor.capability.map.MapVariablesHandler;
import com.apocalypse.caerulaarbor.capability.map.MapVariablesHandler.StrategyType;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.config.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.entity.MartusEntity;
import com.apocalypse.caerulaarbor.entity.SkadiEntity;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.manager.BreedUpgradeManager;
import com.apocalypse.caerulaarbor.manager.SilenceUpgradeManager;
import com.apocalypse.caerulaarbor.manager.TransformManager;
import com.apocalypse.caerulaarbor.util.CaerulaUtil;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
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

import java.util.Comparator;

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

        handleExtractorAdv(event);
        handleGeneSampleDrop(event);
        handleKillFunc(event);
        handleMobDiedOnTrail(event);
        handlePlayerDiedFunc(event);
        handlePlayerDiedInOceanization(event);
        handleSeabornKillMartus(event);
        handleSeabornTransform(event);
        handleTrailriteArmorSelfMend(event);
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
        boolean should_func = world.getLevelData().getGameRules().getBoolean(CAGameRules.TARGET_LIFE_FUNCTION);
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
                    double setval = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_shield - 1;
                    entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.player_shield = setval;
                        capability.syncPlayerVariables(entity);
                    });
                } else if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives > 1) {
                    death_blocked = true;
                    double setval = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives - 1;
                    entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.player_lives = setval;
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
                if (entity instanceof ServerPlayer player) {
                    Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "another_breath"));
                    AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
                    if (!ap.isDone()) {
                        for (String criteria : ap.getRemainingCriteria())
                            player.getAdvancements().award(adv, criteria);
                    }
                }
                if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, (float) 0.33, 1);
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.TARGET_DAMAGED.get(), SoundSource.PLAYERS, (float) 0.33, 1);
                }
                ModCapabilities.getSanityInjury(entity).heal(1000);
                if (is_shield) {
                    if (world instanceof ServerLevel level)
                        level.sendParticles(CAParticles.SHIELDLOSS.get(), x, (y + 0.95), z, 72, 0.75, 0.55, 0.75, 0.2);
                    if (!world.isClientSide()) {
                        entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0));
                        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 4));
                        entity.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 100, 0));
                    }
                    entity.setHealth(entity.getMaxHealth());
                } else {
                    if (world instanceof ServerLevel level)
                        level.sendParticles(CAParticles.LIFELOSS.get(), x, (y + 0.95), z, 72, 0.75, 0.55, 0.75, 0.2);
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
                double setval = Math.max(0, cur_light - light_cost);
                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.player_light = setval;
                    capability.syncPlayerVariables(entity);
                });
            }
        }
    }

    private static void handleBarrierReset(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity == null) return;

        if (!event.isCanceled()) {
            if (entity instanceof LivingEntity livingEntity1 && livingEntity1.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER.get()))
                livingEntity1.getAttribute(CAAttributes.LIVING_BARRIER.get()).setBaseValue(0);
            entity.getPersistentData().putDouble("playerEvoHitTime", 0);
        }
    }

    private static void handleInvulnerableDeath(LivingDeathEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (damagesource == null || entity == null || sourceentity == null) return;

        if (entity instanceof LivingEntity livEnt0 && livEnt0.hasEffect(CAMobEffects.INVULNERABLE.get()) && !damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inv_killer")))) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
        }
    }

    private static void handleExtractorAdv(LivingDeathEvent event) {
        DamageSource damagesource = event.getSource();
        Entity sourceentity = event.getSource().getEntity();

        if (damagesource == null || sourceentity == null) return;

        if (damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "extractor_damage")))) {
            if (sourceentity instanceof ServerPlayer player) {
                Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "little_by_little"));
                AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
                if (!ap.isDone()) {
                    for (String criteria : ap.getRemainingCriteria())
                        player.getAdvancements().award(adv, criteria);
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
                if (world instanceof ServerLevel level) {
                    ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(CAItems.GENE_SAMPLE_NORMAL.get()));
                    entityToSpawn.setPickUpDelay(10);
                    entityToSpawn.setUnlimitedLifetime();
                    level.addFreshEntity(entityToSpawn);
                }
            }
            if (Math.random() < r1) {
                if (world instanceof ServerLevel level) {
                    ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(CAItems.GENE_SAMPLE_UPGRADED.get()));
                    entityToSpawn.setPickUpDelay(10);
                    entityToSpawn.setUnlimitedLifetime();
                    level.addFreshEntity(entityToSpawn);
                }
            }
            if (Math.random() < r2) {
                if (world instanceof ServerLevel level) {
                    ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(CAItems.GENE_SAMPLE_SUPERB.get()));
                    entityToSpawn.setPickUpDelay(10);
                    entityToSpawn.setUnlimitedLifetime();
                    level.addFreshEntity(entityToSpawn);
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
            if (world.getLevelData().getGameRules().getBoolean(CAGameRules.NATURAL_EVOLUTION)) {
                if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), e -> true).isEmpty()) {
                    MapVariablesHandler.addEvoPoint(world, StrategyType.BREED, (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.1);
                    BreedUpgradeManager.applyBreedUpgrade(world);
                    SilenceUpgradeManager.applySilenceUpgrade(world, (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.1);
                }
            }
        }

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanelite")))) {
            if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                if (Math.random() < 0.1) {
                    if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.AMBIENT, 1, 1);
                    }
                    if (world instanceof ServerLevel level) {
                        ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "common_relics"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))));
                        entityToSpawn.setPickUpDelay(10);
                        entityToSpawn.setUnlimitedLifetime();
                        level.addFreshEntity(entityToSpawn);
                    }
                }
            }
        }

        if ((sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "self_mendable")))) {
            ItemStack weapon = (sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
            double dama = weapon.getDamageValue() - Mth.nextInt(RandomSource.create(), 1, 5 + weapon.getEnchantmentLevel(Enchantments.UNBREAKING));
            if (dama <= 0) {
                (sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).setDamageValue(0);
            } else {
                (sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).setDamageValue((int) dama);
            }
        }
    }

    private static void handlePlayerKillRelics(LivingDeathEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
        if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_cursed_EMELIGHT) {
            double setval = (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_light - Mth.nextDouble(RandomSource.create(), 0.1, 0.2);
            sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_light = setval;
                capability.syncPlayerVariables(sourceentity);
            });
        }
        if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_cursed_GLOWBODY) {
            double setval = (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_light - Mth.nextDouble(RandomSource.create(), 0.2, 0.3);
            sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_light = setval;
                capability.syncPlayerVariables(sourceentity);
            });
        }
        if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_cursed_RESEARCH) {
            double setval = (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_light - Mth.nextDouble(RandomSource.create(), 0.3, 0.5);
            sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_light = setval;
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
                    double setval = (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives - 1;
                    sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.player_lives = setval;
                        capability.syncPlayerVariables(sourceentity);
                    });
                }
                double setval = (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_shield + 1;
                sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.player_shield = setval;
                    capability.syncPlayerVariables(sourceentity);
                });
            }
        }
        if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_CRYSTAL) {
            if (Math.random() < 0.1) {
                if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives > 1) {
                    double setval = Math.max((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives - 2, 1);
                    sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.player_lives = setval;
                        capability.syncPlayerVariables(sourceentity);
                    });
                }
                if (sourceentity instanceof Player player)
                    player.giveExperienceLevels(1);
                if (world instanceof ServerLevel level) {
                    ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(CAItems.REDSTONE_INGOT.get()));
                    entityToSpawn.setPickUpDelay(10);
                    level.addFreshEntity(entityToSpawn);
                }
            }
        }
        if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_ENGRAVE >= 0
                && (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_ENGRAVE < 99) {
            if (entity instanceof Monster || (entity instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) == sourceentity) {
                boolean validweapon = false;
                if ((sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.TRIDENT) {
                    validweapon = true;
                } else if ((sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("forge:tools/tridents")))) {
                    validweapon = true;
                } else if (event.getSource().is(DamageTypes.TRIDENT)) {
                    validweapon = true;
                } else {
                    String rname = ForgeRegistries.ITEMS.getKey((sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()).toString();
                    for (String stringiterator : CaerulaConfigsConfiguration.HAND_ENGRAVE.get()) {
                        if (CaerulaUtil.matchesRegistryName(stringiterator, rname)) {
                            validweapon = true;
                            break;
                        }
                    }
                }
                if (validweapon) {
                    double setval = (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_ENGRAVE + 1;
                    sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.relic_hand_ENGRAVE = setval;
                        capability.syncPlayerVariables(sourceentity);
                    });
                }
            }
        }
        if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_SURVIVOR >= 0
                && (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_SURVIVOR < 32) {
            if (entity instanceof Monster || (entity instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) == sourceentity) {
                if (Math.random() < 0.035 || entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge:bosses")))) {
                    double setval = (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_SURVIVOR + 1;
                    sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.relic_SURVIVOR = setval;
                        capability.syncPlayerVariables(sourceentity);
                    });
                    if (world instanceof ServerLevel level)
                        level.sendParticles(ParticleTypes.WAX_ON, x, y, z, 48, 0.7, 1.5, 0.7, 0.2);
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
            if (TransformManager.transformToSeaborn(world, x, y, z, entity)) {
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
        double dx, dy, dz, num, light_cost;
        dx = -1;
        for (int index0 = 0; index0 < 3; index0++) {
            dz = -1;
            for (int index1 = 0; index1 < 3; index1++) {
                dy = -1;
                for (int index2 = 0; index2 < 3; index2++) {
                    if ((world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz))).getBlock() == CABlocks.SEA_TRAIL_INIT.get()
                            || (world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz))).getBlock() == CABlocks.SEA_TRAIL_GROWING.get()) {
                        int value = ((world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz))).getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty getip6
                                ? (world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz))).getValue(getip6)
                                : -1) + 4;
                        BlockPos pos = BlockPos.containing(x + dx, y + dy, z + dz);
                        BlockState bs = world.getBlockState(pos);
                        if (bs.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                            world.setBlock(pos, bs.setValue(integerProp, value), 3);
                    }
                    dy = dy + 1;
                }
                dz = dz + 1;
            }
            dx = dx + 1;
        }
        num = Math.round(Math.sqrt(entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1));
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
                        if ((world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz))).getBlock() == CABlocks.OCEAN_OVARY.get()) {
                            if (1 == ((world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz))).getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty getip12
                                    ? (world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz))).getValue(getip12)
                                    : -1)) {
                                BlockPos pos = BlockPos.containing(x + dx, y + dy, z + dz);
                                BlockState bs = world.getBlockState(pos);
                                if (bs.getBlock().getStateDefinition().getProperty("output") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(0))
                                    world.setBlock(pos, bs.setValue(integerProp, 0), 3);
                                bs = world.getBlockState(pos);
                                if (bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(0))
                                    world.setBlock(pos, bs.setValue(integerProp, 0), 3);
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
            if (world instanceof ServerLevel level) {
                Entity entityToSpawn = CAEntities.SLIDER_FISH.get().spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setDeltaMovement(0, 0.15, 0);
                }
            }
            if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SCULK_VEIN_PLACE, SoundSource.PLAYERS, (float) 0.75, 1);
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

        if (event.isCanceled()||entity == null) return;

        MartusEntity martus = world.getEntitiesOfClass(MartusEntity.class, AABB.ofSize(new Vec3(x, y, z), 96, 96, 96), e -> true).stream()
                .sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z))).findFirst().orElse(null);

        if (martus == null) return;

        if (entity.getPersistentData().getBoolean("blessed")) {
            EntityUtils.hurtMartus(world, martus, sourceentity, Math.max(Math.min((entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.25, martus.getMaxHealth() * 0.4),
                    martus.getMaxHealth()) * 0.05, 0);
        } else if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring"))) && martus.getEntityData().get(MartusEntity.DATA_PHASE) >= 1) {
            EntityUtils.hurtMartus(world, martus, sourceentity, Math.max(Math.min((entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.03, martus.getMaxHealth() * 0.025),
                    martus.getMaxHealth() * 0.018), 0);
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
            if (TransformManager.transformToSeaborn(world, x, y, z, entity)) {
                if (event.isCancelable()) {
                    event.setCanceled(true);
                }
                if (!entity.level().isClientSide())
                    entity.discard();
            }
        }
    }

    private static void handleTrailriteArmorSelfMend(LivingDeathEvent event) {
        Entity sourceentity = event.getSource().getEntity();

        if (sourceentity == null) return;
        if (event.isCanceled()) return;

        ItemStack helm = (sourceentity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).copy();
        ItemStack chest = (sourceentity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).copy();
        ItemStack legg = (sourceentity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).copy();
        ItemStack boot = (sourceentity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).copy();

        if (helm.getItem() == CAItems.TRAILRITE_ARMOR_HELMET.get() && chest.getItem() == CAItems.TRAILRITE_ARMOR_CHESTPLATE.get()
                && legg.getItem() == CAItems.TRAILRITE_ARMOR_LEGGINGS.get() && boot.getItem() == CAItems.TRAILRITE_ARMOR_BOOTS.get()) {
            (sourceentity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).setDamageValue(helm.getDamageValue() - 3);
            (sourceentity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).setDamageValue(chest.getDamageValue() - 3);
            (sourceentity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).setDamageValue(legg.getDamageValue() - 3);
            (sourceentity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).setDamageValue(boot.getDamageValue() - 3);
        }
    }

}
