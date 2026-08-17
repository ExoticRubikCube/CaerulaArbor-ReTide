package com.susen36.caerulaarbor.event;

import com.susen36.babel.api.event.HealthConsumeEvent;
import com.susen36.babel.collectible.Collectibles;
import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.babel.init.BabelGameRules;
import com.susen36.babel.manager.EPManager;
import com.susen36.babel.network.BabelNetwork;
import com.susen36.babel.util.LifePointUtils;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler.StrategyType;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.entity.SkadiEntity;
import com.susen36.caerulaarbor.init.*;
import com.susen36.caerulaarbor.manager.spwan.SeabornTransformManager;
import com.susen36.caerulaarbor.manager.upgrade.BreedUpgradeManager;
import com.susen36.caerulaarbor.manager.upgrade.SilenceUpgradeManager;
import com.susen36.caerulaarbor.util.PlayerStateUtils;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class LivingDeathEventHandler {

    public static final TagKey<DamageType> BYPASS = CADamageTags.BYPASS_PROTECTION;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityDeath(LivingDeathEvent event) {
        handleGameRuleOffLightCost(event);
        handleBarrierReset(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onHealthConsumePre(HealthConsumeEvent.Pre event) {
        if (event.getSource().is(BYPASS)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onHealthConsumePost(HealthConsumeEvent.Post event) {
        LivingEntity livingEntity = event.getEntity();
        Level world = livingEntity.level();
        DamageSource source = event.getSource();
        double x = livingEntity.getX();
        double y = livingEntity.getY();
        double z = livingEntity.getZ();

        if (event.isBlocked()) {
            if (livingEntity instanceof ServerPlayer serverPlayer) {
                AdvancementHolder adv = serverPlayer.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "another_breath"));
                AdvancementProgress ap = serverPlayer.getAdvancements().getOrStartProgress(adv);
                if (!ap.isDone()) {
                    for (String criteria : ap.getRemainingCriteria())
                        serverPlayer.getAdvancements().award(adv, criteria);
                }
            }
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, (float) 0.33, 1);
                level.playSound(null, BlockPos.containing(x, y, z), CASounds.TARGET_DAMAGED.get(), SoundSource.PLAYERS, (float) 0.33, 1);
            }
            EPManager.healToFull(livingEntity, AbstractEPCapability.EPType.NERVOUS);
            if (event.isShieldConsumed()) {
                if (world instanceof ServerLevel level)
                    level.sendParticles(CAParticles.SHIELDLOSS.get(), x, (y + 0.95), z, 72, 0.75, 0.55, 0.75, 0.2);
                if (!world.isClientSide()) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0));
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 4));
                    livingEntity.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 100, 0));
                }
                livingEntity.setHealth(livingEntity.getMaxHealth());
            } else if (event.isLivesConsumed()) {
                if (world instanceof ServerLevel level)
                    level.sendParticles(CAParticles.LIFELOSS.get(), x, (y + 0.95), z, 72, 0.75, 0.55, 0.75, 0.2);
                if (!world.isClientSide())
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 2));
                livingEntity.setHealth(livingEntity.getMaxHealth() * 0.5f);
                double lightCost;
                if (source.is(CADamageTags.RARE)) {
                    lightCost = 15;
                } else if (source.is(CADamageTags.HORROR)) {
                    lightCost = 10;
                } else {
                    lightCost = 5;
                }
                PlayerVariable capability = ModCapabilities.getPlayerVariables(livingEntity);
                capability.player_light = Math.max(0, capability.player_light - lightCost);
                capability.syncPlayerVariables(livingEntity);
            }
        } else {
            PlayerVariable capability = ModCapabilities.getPlayerVariables(livingEntity);
            capability.player_light = Math.max(0, capability.player_light - 50);
            capability.syncPlayerVariables(livingEntity);
        }
    }

    @SubscribeEvent
    public static void onEntityDeathNormal(LivingDeathEvent event) {
        if (event == null) return;

        handleExtractorAdv(event);
        handleGeneSampleDrop(event);
        handleKillFunc(event);
        handleMobDiedOnTrail(event);
        handlePlayerDiedFunc(event);
        handlePlayerDiedInOceanization(event);
        handleSeabornTransform(event);
        handleTrailriteArmorSelfMend(event);
    }

    private static void handleGameRuleOffLightCost(LivingDeathEvent event) {
        if (!event.isCanceled() && event.getEntity() instanceof Player player && !player.level().isClientSide()
                && !player.level().getLevelData().getGameRules().getBoolean(BabelGameRules.TARGET_LIFE_FUNCTION)
                && !event.getSource().is(BYPASS)) {
            PlayerVariable capability = ModCapabilities.getPlayerVariables(player);
            capability.player_light = Math.max(0, capability.player_light - 25);
            capability.syncPlayerVariables(player);
        }
    }

    private static void handleBarrierReset(LivingDeathEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof LivingEntity livingEntity1 && livingEntity1.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER))
            livingEntity1.getAttribute(CAAttributes.LIVING_BARRIER).setBaseValue(0);
        entity.getPersistentData().putDouble("playerEvoHitTime", 0);
    }

    private static void handleExtractorAdv(LivingDeathEvent event) {
        DamageSource damagesource = event.getSource();
        Entity sourceentity = event.getSource().getEntity();

        if (sourceentity instanceof ServerPlayer player && damagesource.is(CADamageTypes.EXTRACTOR_DAMAGE)) {
            AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "little_by_little"));
            if (adv != null) {
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

        if (sourceentity == null) return;
        if (!world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) return;

        if (sourceentity instanceof Player) {
            boolean result = false;
            result = (ModCapabilities.getPlayerVariables(sourceentity)).can_player_evo
                    && (sourceentity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO) || (ModCapabilities.getPlayerVariables(sourceentity)).player_oceanization >= 3);
            if (result) {
                double r0 = 0, r1 = 0, r2 = 0;
                if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn_boss")))) {
                    r0 = 0.5;
                    r1 = 0.25;
                    r2 = 0.125;
                } else if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "oceanelite")))) {
                    r0 = 0.3;
                    r1 = 0.075;
                    r2 = 0.0075;
                } else if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))) {
                    r0 = 0.15;
                }
                if (Math.random() < r0) {
                    if (world instanceof ServerLevel level) {
                        ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(CAItems.EVOLUTIONARY_GENE.get()));
                        entityToSpawn.setPickUpDelay(10);
                        entityToSpawn.setUnlimitedLifetime();
                        level.addFreshEntity(entityToSpawn);
                    }
                }
                if (Math.random() < r1) {
                    if (world instanceof ServerLevel level) {
                        ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(CAItems.EVOLUTIONARY_GENOME.get()));
                        entityToSpawn.setPickUpDelay(10);
                        entityToSpawn.setUnlimitedLifetime();
                        level.addFreshEntity(entityToSpawn);
                    }
                }
                if (Math.random() < r2) {
                    if (world instanceof ServerLevel level) {
                        ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(CAItems.EXPERIMENTAL_GENE.get()));
                        entityToSpawn.setPickUpDelay(10);
                        entityToSpawn.setUnlimitedLifetime();
                        level.addFreshEntity(entityToSpawn);
                    }
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

        if (sourceentity == null) return;

        if (sourceentity instanceof Player) {
            handlePlayerKillRelics(event, world, x, y, z, entity, sourceentity);
        }

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))) {
            if (world.getLevelData().getGameRules().getBoolean(CAGameRules.NATURAL_EVOLUTION)) {
                if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), e -> true).isEmpty()) {
                    MapVariablesHandler.addEvoPoint(world, StrategyType.BREED, (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.1);
                    BreedUpgradeManager.applyBreedUpgrade(world);
                    SilenceUpgradeManager.applySilenceUpgrade(world, (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.1);
                }
            }
        }

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "oceanelite")))) {
            if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                if (Math.random() < 0.1) {
                    if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.AMBIENT, 1, 1);
                    }
                    if (world instanceof ServerLevel level) {
                        ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "common_relics"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))));
                        entityToSpawn.setPickUpDelay(10);
                        entityToSpawn.setUnlimitedLifetime();
                        level.addFreshEntity(entityToSpawn);
                    }
                }
            }
        }

        if ((sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "self_mendable")))) {
            ItemStack weapon = (sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
            int unbreakingLevel = 0;
            if (entity.level() instanceof ServerLevel serverLevel) {
                unbreakingLevel = serverLevel.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(Enchantments.UNBREAKING)
                        .map(h -> weapon.getEnchantmentLevel(h)).orElse(0);
            }
            double dama = weapon.getDamageValue() - Mth.nextInt(RandomSource.create(), 1, 5 + unbreakingLevel);
            if (dama <= 0) {
                (sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).setDamageValue(0);
            } else {
                (sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).setDamageValue((int) dama);
            }
        }
    }

    private static void handlePlayerKillRelics(LivingDeathEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
        PlayerVariable capability = ModCapabilities.getPlayerVariables(sourceentity);
        if (sourceentity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.CURSED_EMELIGHT)) {
            capability.player_light = capability.player_light - Mth.nextDouble(RandomSource.create(), 0.1, 0.2);
            capability.syncPlayerVariables(sourceentity);
        }
        if (sourceentity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.CURSED_GLOWBODY)) {
            capability.player_light = capability.player_light - Mth.nextDouble(RandomSource.create(), 0.2, 0.3);
            capability.syncPlayerVariables(sourceentity);
        }
        if (sourceentity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.CURSED_RESEARCH)) {
            capability.player_light = capability.player_light - Mth.nextDouble(RandomSource.create(), 0.3, 0.5);
            capability.syncPlayerVariables(sourceentity);
        }
        if (capability.player_light < 0) {
            capability.player_light = 0;
            capability.syncPlayerVariables(sourceentity);
        }
        if (sourceentity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.KING_ARMOR)) {
            if (Math.random() < 0.08) {
                if (LifePointUtils.getLifePoint(sourceentity) > 1) {
                    LifePointUtils.setLifePoint(sourceentity, LifePointUtils.getLifePoint(sourceentity) - 1);
                }
                LifePointUtils.setShieldPoint(sourceentity, LifePointUtils.getShieldPoint(sourceentity) + 1);
            }
        }
        if (sourceentity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.KING_CRYSTAL)) {
            if (Math.random() < 0.1) {
                if (LifePointUtils.getLifePoint(sourceentity) > 1) {
                    LifePointUtils.setLifePoint(sourceentity, Math.max(LifePointUtils.getLifePoint(sourceentity) - 2, 1));
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
        if (sourceentity instanceof Player player) {
            int engrave = player.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).getLayer(CACollectible.HAND_OF_ENGRAVE);
            if (engrave >= 0 && engrave < 99) {
                if (entity instanceof Monster || (entity instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) == sourceentity) {
                    boolean validweapon = false;
                    if ((sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.TRIDENT) {
                        validweapon = true;
                    } else if ((sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(ResourceLocation.parse("forge:tools/tridents")))) {
                        validweapon = true;
                    } else if (event.getSource().is(DamageTypes.TRIDENT)) {
                        validweapon = true;
                    } else {
                        String rname = BuiltInRegistries.ITEM.getKey((sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()).toString();
                        for (String stringiterator : CAConfigs.HAND_ENGRAVE.get()) {
                            if (PlayerStateUtils.matchesRegistryName(stringiterator, rname)) {
                                validweapon = true;
                                break;
                            }
                        }
                    }
                    if (validweapon) {
                        player.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CACollectible.HAND_OF_ENGRAVE, engrave + 1);
                    }
                }
            }
        }
        int survivor = sourceentity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).getLayer(CACollectible.SURVIVOR_CONTRACT);
        if (survivor >= 0
                && survivor < 32) {
            if (entity instanceof Monster || (entity instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) == sourceentity) {
                if (Math.random() < 0.035 || entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("forge:bosses")))) {
                    sourceentity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CACollectible.SURVIVOR_CONTRACT, survivor + 1);
                    if (sourceentity instanceof Player player)
                        BabelNetwork.syncCollectibles(player);
                    if (world instanceof ServerLevel level)
                        level.sendParticles(ParticleTypes.WAX_ON, x, y, z, 48, 0.7, 1.5, 0.7, 0.2);
                }
            }
        }
    }

    private static void handleMobDiedOnTrail(LivingDeathEvent event) {
        Level world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (entity instanceof SkadiEntity) return;

        if (damagesource.is(CADamageTags.CAN_TRIGGER_OCEANIZATION)) {
            if (SeabornTransformManager.transformToSeaborn(world, x, y, z, entity)) {
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

        if (event.isCanceled()) return;

        if (entity instanceof Player && damagesource.is(CADamageTypes.OCEANIZE_DAMAGE)) {
            if (world instanceof ServerLevel level) {
                Entity entityToSpawn = CAEntities.SLIDER_FISH.get().spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setDeltaMovement(0, 0.15, 0);
                }
            }
            if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SCULK_VEIN_PLACE, SoundSource.PLAYERS, (float) 0.75, 1);
            }
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            capability.player_oceanization = 0;
            capability.syncPlayerVariables(entity);
        }
    }

    private static void handleSeabornTransform(LivingDeathEvent event) {
        Level world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        DamageSource source = event.getSource();
        Entity sourceentity = source.getEntity();

        if (event.isCanceled()) return;

        if (entity.level().isClientSide() && !(entity.getType().is(CAEntityTypeTags.SEABORN)||entity.getType().is(CAEntityTypeTags.SEABORN_BOSS)||entity.getType().is(CAEntityTypeTags.SEABORN_MINION))) {
            if((sourceentity != null && sourceentity.getType().is(CAEntityTypeTags.SEABORN)||source.is(CADamageTypes.TRAIL_DAMAGE))) {
                if (SeabornTransformManager.transformToSeaborn(world, x, y, z, entity,source)) {
                    event.setCanceled(true);
                }
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