package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler.StrategyType;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.entity.ChitinGolemEntity;
import com.susen36.caerulaarbor.entity.PredatorAbyssalEntity;
import com.susen36.caerulaarbor.init.*;
import com.susen36.caerulaarbor.manager.upgrade.GrowUpgradeManager;
import com.susen36.caerulaarbor.manager.upgrade.SilenceUpgradeManager;
import com.susen36.caerulaarbor.manager.upgrade.SubsistingUpgradeManager;
import com.susen36.caerulaarbor.util.CaerulaUtil;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

@SuppressWarnings("unused")
@EventBusSubscriber
public class LivingAttackEventHandler {

    private static final TagKey<EntityType<?>> INQUISITION = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "inquisition"));
    private static final TagKey<EntityType<?>> HUMAN_SIDE = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "is_humanside"));
    private static final TagKey<EntityType<?>> OCEAN_OFFSPRING = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born"));
    private static final TagKey<EntityType<?>> OCEAN_PET = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "oceanpet"));
    private static final TagKey<EntityType<?>> SKIP_MIGRATION = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "skip_migration"));
    private static final TagKey<EntityType<?>> IGNORE_MIGRATION = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "ignore_migration"));

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityAttack(LivingIncomingDamageEvent event) {
        handleInvulnerable(event);
        handleMissRate(event);
        handleMartusArrowImmunity(event);
        handleInquisitionFriendlyFire(event);
        handleDamagePrevention(event);
        handleMobHit(event);
        handlePlayerHit(event);
    }

    private static void handleInvulnerable(LivingIncomingDamageEvent event) {
        var damageSource = event.getSource();
        var target = event.getEntity();
        if (damageSource.is(CADamageTypes.INV_KILLER)) return;

        if (target.hasEffect(CAMobEffects.INVULNERABLE)) {
            event.setCanceled(true);
        }
    }

    private static void handleMissRate(LivingIncomingDamageEvent event) {
        var world = event.getEntity().level();
        var target = event.getEntity();
        var damageSource = event.getSource();
        if (event.isCanceled()) return;

        var missRate = target.getAttribute(CAAttributes.MISSRATE);
        if (missRate == null || missRate.getValue() <= 0 || damageSource.is(CADamageTags.BYPASS_MISS)
                || target.hasEffect(CAMobEffects.MUTE))
            return;

        var sourceEntity = damageSource.getEntity();
        if (!damageSource.is(CADamageTags.SKIP_SOURCE_CHECK) && sourceEntity == null)
            return;

        if (target.getRandom().nextDouble() * 100 >= missRate.getValue())
            return;

        if (world instanceof ServerLevel level) {
            level.sendParticles(CAParticles.MISS.get(), target.getX(), target.getY(), target.getZ(), 6, 1, 1, 1, 0.1);
        }
        if (target instanceof PredatorAbyssalEntity predator) {
            predator.setAnimation("animation.predator.miss");
        }
        if (target instanceof ChitinGolemEntity chitinGolem) {
            chitinGolem.setAnimation("animation.chitgolem.block");
        }
        event.setCanceled(true);
    }

    private static void handleMartusArrowImmunity(LivingIncomingDamageEvent event) {
        var damageSource = event.getSource();
        var target = event.getEntity();
        var sourceEntity = damageSource.getEntity();
        if (sourceEntity == null || !target.hasEffect(CAMobEffects.MARTUS_PROTECTION)) return;

        if (damageSource.is(DamageTypeTags.IS_PROJECTILE) && sourceEntity.distanceTo(target) > 2
                && event.getAmount() <= target.getMaxHealth() * 2) {
            event.setCanceled(true);
        } else if (target.distanceTo(sourceEntity) > 3 && target.getRandom().nextBoolean()) {
            event.setCanceled(true);
        }
    }

    private static void handleInquisitionFriendlyFire(LivingIncomingDamageEvent event) {
        var target = event.getEntity();
        var sourceEntity = event.getSource().getEntity();
        if (sourceEntity != null && target.getType().is(INQUISITION) && sourceEntity.getType().is(INQUISITION)) {
            event.setCanceled(true);
        }
    }

    private static void handleDamagePrevention(LivingIncomingDamageEvent event) {
        var target = event.getEntity();
        var sourceEntity = event.getSource().getEntity();
        if (sourceEntity == null) return;

        preventSameTeamDamage(event, target.level(), target, sourceEntity);
        preventInquisitionDamage(event, target, sourceEntity);
        preventHumanSideFriendlyFire(event, target, sourceEntity);
    }

    private static void preventSameTeamDamage(LivingIncomingDamageEvent event, LevelAccessor world, LivingEntity target, Entity sourceEntity) {
        if (target instanceof Player || sourceEntity instanceof Player) return;

        if ((world.getLevelData().getGameRules().getBoolean(CAGameRules.AGGRESIVE_MODE)
                || !world.getLevelData().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING))
                && EntityUtils.isSameTeam(target, sourceEntity)) {
            event.setCanceled(true);
        }
    }

    private static void preventInquisitionDamage(LivingIncomingDamageEvent event, LivingEntity target, Entity sourceEntity) {
        if (!target.getType().is(INQUISITION) || !(sourceEntity instanceof Player player)) return;

        if (target.getPersistentData().getString("recentCommander").equals(sourceEntity.getDisplayName().getString())) {
            event.setCanceled(true);
        }
        if (player.isHolding(CAItems.INTERPHONE.get())) {
            target.getPersistentData().putString("recentCommander", "");
        }
    }

    private static void preventHumanSideFriendlyFire(LivingIncomingDamageEvent event, LivingEntity target, Entity sourceEntity) {
        if (!target.getType().is(HUMAN_SIDE) || !sourceEntity.getType().is(HUMAN_SIDE)
                || target instanceof Mob mob && sourceEntity == mob.getTarget()) return;

        event.setCanceled(true);
    }

    private static void handleMobHit(LivingIncomingDamageEvent event) {
        var world = event.getEntity().level();
        var target = event.getEntity();
        var damageSource = event.getSource();
        var sourceEntity = damageSource.getEntity();
        if (sourceEntity == null) return;
        if (event.isCanceled()) return;

        handleMobHitMigration(world, target, sourceEntity, damageSource);
        handleOceanOffspringFriendlyFire(event, target, sourceEntity);
        if (event.isCanceled()) return;
        handleMobHitEvolution(event, world, target, sourceEntity, damageSource, event.getAmount());
        event.getAmount();
        handleSublimationAttack(world, target, sourceEntity, damageSource);
    }

    private static void handleOceanOffspringFriendlyFire(LivingIncomingDamageEvent event, LivingEntity target, Entity sourceEntity) {
        if (sourceEntity.getType().is(OCEAN_OFFSPRING) && target.getType().is(OCEAN_OFFSPRING)) {
            LivingEntity srcTarget = sourceEntity instanceof Mob mobEnt ? mobEnt.getTarget() : null;
            if (target != srcTarget) {
                event.setCanceled(true);
            }
        }
    }

    private static void handleMobHitMigration(LevelAccessor world, LivingEntity target, Entity sourceEntity, DamageSource damageSource) {
        var migrationLevel = MapVariables.get(world).strategy_migration;
        if (migrationLevel <= 0) return;

        if (target.getType().is(OCEAN_OFFSPRING) && !sourceEntity.getType().is(OCEAN_OFFSPRING)
                && !target.getType().is(OCEAN_PET) && !target.getType().is(SKIP_MIGRATION)
                && !(sourceEntity instanceof Player player && player.getAbilities().instabuild)
                && !damageSource.is(CADamageTags.BYPASSES_MIGRATION)) {
            var migrationArea = new AABB(target.getX() - (8 + migrationLevel * 16), target.getY() - 16,
                    target.getZ() - (8 + migrationLevel * 16), target.getX() + 8 + migrationLevel * 24,
                    target.getY() + 16, target.getZ() + 8 + migrationLevel * 24);
            directMigratingMobs(world, target, sourceEntity, migrationArea, 0.5, true);
        }

        if (target instanceof Player player
                && ModCapabilities.getPlayerVariables(player).player_oceanization >= 3
                && !sourceEntity.getType().is(OCEAN_OFFSPRING)) {
            var migrationArea = new AABB(target.getX() - (8 + migrationLevel * 24), target.getY() - 16,
                    target.getZ() - (8 + migrationLevel * 24), target.getX() + 8 + migrationLevel * 24,
                    target.getY() + 16, target.getZ() + 8 + migrationLevel * 24);
            directMigratingMobs(world, target, sourceEntity, migrationArea, 0.8, false);
        }
    }

    private static void directMigratingMobs(LevelAccessor world, LivingEntity target, Entity sourceEntity, AABB area, double speed, boolean ignoreMarkedEntities) {
        for (var candidate : world.getEntities(target, area)) {
            if (!candidate.getType().is(OCEAN_OFFSPRING) || candidate.getType().is(OCEAN_PET)
                    || candidate == sourceEntity || ignoreMarkedEntities && candidate.getType().is(IGNORE_MIGRATION)
                    || !(candidate instanceof Mob mob)) continue;

            mob.getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), speed);
            if (sourceEntity instanceof LivingEntity livingSource) {
                mob.setTarget(livingSource);
            }
        }
    }

    private static void handleMobHitEvolution(LivingIncomingDamageEvent event, LevelAccessor world, LivingEntity target, Entity sourceEntity, DamageSource damageSource, double amount) {
        if (sourceEntity.getType().is(OCEAN_OFFSPRING)
                && world.getLevelData().getGameRules().getBoolean(CAGameRules.NATURAL_EVOLUTION)) {
            var growthPoints = amount * 0.025;
            MapVariablesHandler.addEvoPoint(world, StrategyType.GROW, growthPoints);
            GrowUpgradeManager.applyGrowthUpgrade(world);
            SilenceUpgradeManager.applySilenceUpgrade(world, growthPoints);
        }

        if (!target.getType().is(OCEAN_OFFSPRING)) return;

        if (sourceEntity instanceof ServerPlayer player) {
            var advancement = player.server.getAdvancements().get(
                    ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "encounter_from_the_ocean"));
            if (advancement != null) {
                var progress = player.getAdvancements().getOrStartProgress(advancement);
                for (var criterion : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, criterion);
                }
            }
        }

        if (world.getLevelData().getGameRules().getBoolean(CAGameRules.NATURAL_EVOLUTION)
                && !damageSource.is(CADamageTags.BYPASSES_EVOLUTION)) {
            var subsistingPoints = Math.min(amount, target.getMaxHealth()) * 0.025;
            MapVariablesHandler.addEvoPoint(world, StrategyType.SUBSISTING, subsistingPoints);
            SubsistingUpgradeManager.applySubsistingUpgrade(world);
            SilenceUpgradeManager.applySilenceUpgrade(world, subsistingPoints);
        }

        var evolved = target.getAttribute(CAAttributes.EVOLVED);
        if (evolved != null) {
            evolved.setBaseValue(1);
        }
    }

    private static void handleSublimationAttack(LevelAccessor world, LivingEntity target, Entity sourceEntity, DamageSource damageSource) {
        double finalGrow = Math.min(MapVariables.get(world).strategy_sublimation, MapVariables.get(world).strategy_grow);
        if (finalGrow > 0.0 && sourceEntity instanceof LivingEntity livingSource
                && !damageSource.is(CADamageTypes.OCEAN_REAL)) {
            double damage = livingSource.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                    ? livingSource.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0.0;
            target.hurt(CADamageTypes.source(world, CADamageTypes.OCEAN_REAL, sourceEntity), (float) (damage * finalGrow * 0.03));
        }
    }

    private static void handlePlayerHit(LivingIncomingDamageEvent event) {
        var world = event.getEntity().level();
        var target = event.getEntity();
        var immediateSource = event.getSource().getDirectEntity();
        var sourceEntity = event.getSource().getEntity();
        if (immediateSource == null || sourceEntity == null) return;

        var mainHandItem = sourceEntity instanceof LivingEntity livingSource ? livingSource.getMainHandItem() : ItemStack.EMPTY;
        var muteAttackLevel = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(sourceEntity.level().registryAccess(), CAEnchantments.MUTE_ATTACK), mainHandItem);
        if (muteAttackLevel > 0 && world.random.nextFloat() < 0.2F * muteAttackLevel && !world.isClientSide()) {
            target.addEffect(new MobEffectInstance(CAMobEffects.MUTE, 30 * muteAttackLevel, 0, false, false));
        }

        if (event.isCanceled()) return;

        if (sourceEntity instanceof Player player) {
            PlayerVariable playerVariables = ModCapabilities.getPlayerVariables(player);
            handlePlayerHitRelics(world, target, target.position(), immediateSource, player, event.getAmount(), mainHandItem, playerVariables);
        }
    }

    private static void handlePlayerHitRelics(Level world, LivingEntity target, Vec3 targetPosition, Entity immediateSource,
                                              Player player, double amount, ItemStack mainHandItem, PlayerVariable playerVariables) {
        if (immediateSource != player) {
            var itemKey = BuiltInRegistries.ITEM.getKey(mainHandItem.getItem());
            var registryName = itemKey.toString();

            if (playerVariables.relic_hand_STRANGLE && isStrangleWeapon(mainHandItem, registryName)
                    && target.isAlive() && target.getHealth() < target.getMaxHealth() * 0.25F) {
                target.hurt(CADamageTypes.source(world, CADamageTypes.HAND_OF_CHOKER, player), target.getMaxHealth() * 99);
                if (world instanceof ServerLevel level) {
                    level.sendParticles(ParticleTypes.GLOW_SQUID_INK, target.getX(), target.getY(), target.getZ(), 128, 1, 1, 1, 0.33);
                }
                world.playSound(null, target.blockPosition(), SoundEvents.WITHER_HURT, SoundSource.NEUTRAL, 2, 1);
            }

            if (playerVariables.relic_hand_FIREWORK && isFireworkWeapon(mainHandItem, registryName)
                    && player.getRandom().nextFloat() < 0.33F) {
                world.playSound(null, player.blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 3.6F, 1);
                CaerulaArbor.queueServerWork(10, () -> detonateFireworkRelic(world, target, targetPosition, player, amount));
            }
        }

        if (!playerVariables.relic_legend_CHITIN || player.getRandom().nextFloat() >= 0.05F) return;

        playerVariables.chitin_knife_selected = player.getMainHandItem().copy();
        playerVariables.syncPlayerVariables(player);
        var healthPercent = EntityUtils.getHealthPerc(player);
        if (!world.isClientSide()) {
            player.addEffect(new MobEffectInstance(CAMobEffects.TIDE_OF_CHITIN, 500, 0, false, false));
        }
        if (healthPercent > 0) {
            player.setHealth((float) (player.getMaxHealth() * healthPercent));
        }
        if (world.isClientSide()) {
            world.playLocalSound(targetPosition.x, targetPosition.y, targetPosition.z,
                    SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 3.2F, 1, false);
        }
    }

    private static boolean isStrangleWeapon(ItemStack itemStack, String registryName) {
        return itemStack.is(Tags.Items.TOOLS_CROSSBOW) || itemStack.getItem() instanceof CrossbowItem
                || matchesConfiguredItem(CAConfigs.HAND_STRANGLE.get(), registryName);
    }

    private static boolean isFireworkWeapon(ItemStack itemStack, String registryName) {
        return itemStack.is(Tags.Items.TOOLS_BOW) || itemStack.getItem() instanceof BowItem
                || itemStack.is(CAItems.PHLOEM_BOW.get())
                || matchesConfiguredItem(CAConfigs.HAND_FIREWORK.get(), registryName);
    }

    private static boolean matchesConfiguredItem(Iterable<? extends String> configuredItems, String registryName) {
        for (var configuredItem : configuredItems) {
            if (CaerulaUtil.matchesRegistryName(configuredItem, registryName)) {
                return true;
            }
        }
        return false;
    }

    private static void detonateFireworkRelic(Level world, LivingEntity target, Vec3 targetPosition, Player player, double amount) {
        if (world instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.FIREWORK, targetPosition.x, targetPosition.y, targetPosition.z, 85, 2, 2, 2, 0.22);
        }
        world.playSound(null, BlockPos.containing(targetPosition), SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.PLAYERS, 3.6F, 1);

        List<LivingEntity> nearbyEntities = world.getEntitiesOfClass(LivingEntity.class, new AABB(targetPosition, targetPosition).inflate(2.5),
                candidate -> candidate != player && (candidate.getType() == target.getType() || candidate instanceof Monster));
        for (LivingEntity candidate : nearbyEntities) {
            candidate.hurt(CADamageTypes.source(world, CADamageTypes.HAND_FIREWORK, player), (float) (amount * 3));
        }
    }
}