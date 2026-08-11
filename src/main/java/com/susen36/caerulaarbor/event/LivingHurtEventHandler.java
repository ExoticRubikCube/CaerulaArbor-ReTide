package com.susen36.caerulaarbor.event;

import com.susen36.babel.api.BabelAPI;
import com.susen36.babel.effect.LessArmorMobEffect;
import com.susen36.babel.init.BabelMobEffects;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.Relic;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import com.susen36.caerulaarbor.entity.*;
import com.susen36.caerulaarbor.init.*;
import com.susen36.caerulaarbor.manager.upgrade.GrowUpgradeManager;
import com.susen36.caerulaarbor.manager.upgrade.SublimationUpgradeManger;
import com.susen36.caerulaarbor.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.Comparator;
import java.util.List;

import static com.susen36.caerulaarbor.util.EntityUtils.SEA_BORN;

@EventBusSubscriber
public class LivingHurtEventHandler {

    public static final TagKey<DamageType> B_PROTECTION = CADamageTags.BYPASS_PROTECTION;
    public static final TagKey<DamageType> IS_MAGIC = CADamageTags.IS_MAGIC;
    public static final TagKey<DamageType> B_DEFENSE = CADamageTags.BYPASS_DEFENSE;

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onEntityHurt(LivingDamageEvent.Pre event) {
        handleBarrierFunc(event);
        handleMagicResis(event);
        handleFlamarineHurt(event);
        handleTrailriteAttackBonus(event);
        handleArmorKnight(event);
        handleBossHit(event);
        handleCorruptedBurdenDamage(event);
        handleDamageBurdenVeicle(event);
        handleCrimsonTreaty(event);
        handleExtraMagicdamage(event);
        handleHandHoeSword(event);
        handleHandThorns(event);
        handleHuntersHit(event);
        handleOnArrowHit(event);
        handleSanityReaper(event);
        handleSeabornKiller(event);
        handleSeabornsGetOffShip(event);
        handleMoreFallDamageEffect(event);
        handleWarriorTactic(event);
        handlePlayerEvolutionDamageReduction(event);
        handlePlayerEvolutionDamageAmplification(event);
        handleKillMuteSelf(event);
        handleSublimationDamage(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void livingIncomingHurt(LivingIncomingDamageEvent event) {
        DamageSource damageSource = event.getSource();
        LivingEntity entity = event.getEntity();

        if (damageSource.is(CADamageTypes.TRAIL_DAMAGE) && entity.getType().is(SEA_BORN)) {
            event.setCanceled(true);
        }
    }

    private static void handleKillMuteSelf(LivingDamageEvent.Pre event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (damagesource.is(DamageTypes.GENERIC_KILL)) {
            if (entity instanceof BaselayerAbyssalEntity datEntSetI)
                datEntSetI.getEntityData().set(BaselayerAbyssalEntity.DATA_MUTE_TIME, 100);
        }
    }

    private static void handleSublimationDamage(LivingDamageEvent.Pre event) {
        LevelAccessor world = event.getEntity().level();
        Entity entity = event.getEntity();
        double amount = event.getNewDamage();

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born")))) {
            entity.getPersistentData().putDouble("caerula.lastHurtByTime", entity.tickCount);
            entity.getPersistentData().putDouble("caerula.sublimationDamage", entity.getPersistentData().getDouble("caerula.sublimationDamage") + amount);
            SublimationUpgradeManger.applySublimationUpgrade(world, amount);
        }
    }

    private static void handleBarrierFunc(LivingDamageEvent.Pre event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getNewDamage();

        if (damagesource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || damagesource.is(DamageTypeTags.BYPASSES_EFFECTS))
            return;

        double brr = entity instanceof LivingEntity livingEntity1 && livingEntity1.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER)
                ? livingEntity1.getAttribute(CAAttributes.LIVING_BARRIER).getBaseValue()
                : 0;

        if (brr > 0) {
            double disp;
            if (brr >= amount) {
                if (entity instanceof LivingEntity livingEntity2 && livingEntity2.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER))
                    livingEntity2.getAttribute(CAAttributes.LIVING_BARRIER).setBaseValue((brr - amount));
                disp = amount;
                event.setNewDamage(0);
            } else {
                if (entity instanceof LivingEntity livingEntity4 && livingEntity4.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER))
                    livingEntity4.getAttribute(CAAttributes.LIVING_BARRIER).setBaseValue(0);
                disp = brr;
                event.setNewDamage((float) (amount - brr));
            }
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), CASounds.LIVING_BARRIER.get(), SoundSource.HOSTILE, 2, (float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
            }
            if (world instanceof ServerLevel level)
                level.sendParticles(CAParticles.LIVING_BARRIER_SHOW.get(), x, (y + 0.75), z, (int) Math.min(disp * 0.5, 24), 0.75, 0.75, 0.75, 0.1);
        }
    }

    private static void handleMagicResis(LivingDamageEvent.Pre event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getNewDamage();

        if (damagesource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
        if (damagesource.is(DamageTypeTags.BYPASSES_EFFECTS)) return;
        if (damagesource.is(B_PROTECTION)) return;

        if (damagesource.is(IS_MAGIC) && !damagesource.is(DamageTypeTags.BYPASSES_ENCHANTMENTS)) {
            double mgc_resis = entity instanceof LivingEntity livingEntity5 && livingEntity5.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE)
                    ? livingEntity5.getAttribute(CAAttributes.MAGIC_RESISTANCE).getValue()
                    : 0;
            if (mgc_resis > 0) {
                event.setNewDamage((float) Math.max(amount * 0.01 * (100 - mgc_resis), amount * 0.05));
            }
        } else {
            double def = entity instanceof LivingEntity livingEntity7 && livingEntity7.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)
                    ? livingEntity7.getAttribute(CAAttributes.GENERAL_DEFENSE).getValue()
                    : 0;
            if (def > 0 && !damagesource.is(B_DEFENSE)) {
                event.setNewDamage((float) Math.max(amount - def, amount * 0.05));
            }
        }
    }

    //TODO 需要制作共同的基类然后下放
    private static void handleFlamarineHurt(LivingDamageEvent.Pre event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getNewDamage();

        double gap = entity.tickCount - (entity instanceof LivingEntity livEnt ? livEnt.getLastHurtByMobTimestamp() : 0);
        double ratie = 1;

        if (damagesource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;

        if (entity instanceof FlamarineStatueEntity || entity instanceof FlamarineGolemEntity) {
            if (gap < 10) {
                ratie = Math.max(gap * 0.1, 0.5);
            }
            event.setNewDamage((float) Math.min(amount * ratie, (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.34));

            if (entity instanceof FlamarineStatueEntity) {
                if (entity instanceof LivingEntity livingEntity8 && livingEntity8.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE))
                    livingEntity8.getAttribute(CAAttributes.GENERAL_DEFENSE)
                            .setBaseValue(Math.max((entity instanceof LivingEntity livingEntity7 && livingEntity7.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)
                                    ? livingEntity7.getAttribute(CAAttributes.GENERAL_DEFENSE).getBaseValue()
                                    : 0) - 0.5, 1));
                if (entity instanceof LivingEntity livingEntity10 && livingEntity10.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE))
                    livingEntity10.getAttribute(CAAttributes.MAGIC_RESISTANCE)
                            .setBaseValue(Math.max((entity instanceof LivingEntity livingEntity9 && livingEntity9.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE)
                                    ? livingEntity9.getAttribute(CAAttributes.MAGIC_RESISTANCE).getBaseValue()
                                    : 0) - 2, 15));
            } else {
                double add = entity instanceof FlamarineGolemEntity datEntI ? datEntI.getEntityData().get(FlamarineGolemEntity.DATA_ADDITION) : 0;
                if (add > 0) {
                    if (entity instanceof LivingEntity livingEntity13 && livingEntity13.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE))
                        livingEntity13.getAttribute(CAAttributes.GENERAL_DEFENSE)
                                .setBaseValue(((entity instanceof LivingEntity livingEntity12 && livingEntity12.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)
                                        ? livingEntity12.getAttribute(CAAttributes.GENERAL_DEFENSE).getBaseValue()
                                        : 0) + 1));
                    if (entity instanceof LivingEntity livingEntity15 && livingEntity15.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE))
                        livingEntity15.getAttribute(CAAttributes.MAGIC_RESISTANCE)
                                .setBaseValue(((entity instanceof LivingEntity livingEntity14 && livingEntity14.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE)
                                        ? livingEntity14.getAttribute(CAAttributes.MAGIC_RESISTANCE).getBaseValue()
                                        : 0) + 1.5));
                    if (entity instanceof FlamarineGolemEntity datEntSetI)
                        datEntSetI.getEntityData().set(FlamarineGolemEntity.DATA_ADDITION, (int) (add - 1));
                }
            }
        }
    }

    private static void handleTrailriteAttackBonus(LivingDamageEvent.Pre event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getNewDamage();

        if (sourceentity == null) return;

        ItemStack helm = (sourceentity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).copy();
        ItemStack chest = (sourceentity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).copy();
        ItemStack legg = (sourceentity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).copy();
        ItemStack boot = (sourceentity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).copy();

        if (helm.getItem() == CAItems.TRAILRITE_ARMOR_HELMET.get() && chest.getItem() == CAItems.TRAILRITE_ARMOR_CHESTPLATE.get()
                && legg.getItem() == CAItems.TRAILRITE_ARMOR_LEGGINGS.get() && boot.getItem() == CAItems.TRAILRITE_ARMOR_BOOTS.get()) {
            if (entity instanceof LivingEntity living)
                LessArmorMobEffect.apply(living);
        }

        helm = (entity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).copy();
        chest = (entity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).copy();
        legg = (entity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).copy();
        boot = (entity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).copy();

        if (helm.getItem() == CAItems.TRAILRITE_ARMOR_HELMET.get() && chest.getItem() == CAItems.TRAILRITE_ARMOR_CHESTPLATE.get()
                && legg.getItem() == CAItems.TRAILRITE_ARMOR_LEGGINGS.get() && boot.getItem() == CAItems.TRAILRITE_ARMOR_BOOTS.get()) {
            if (!damagesource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                double gap = entity.tickCount - (entity instanceof LivingEntity livEnt ? livEnt.getLastHurtByMobTimestamp() : 0);
                double maxH = entity instanceof LivingEntity livingEntity18 && livingEntity18.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? livingEntity18.getAttribute(Attributes.MAX_HEALTH).getValue() : 0;
                if (gap < 10) {
                    event.setNewDamage((float) Math.clamp(maxH * 0.33, 16, amount * Math.clamp(gap * 0.1, 0.05, 1)));
                }
            }
        }
    }

    private static void handleArmorKnight(LivingDamageEvent.Pre event) {
        LevelAccessor world = event.getEntity().level();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (sourceentity == null) return;

        double rate = 0;
        double freeze;
        ItemStack helm = (entity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).copy();
        ItemStack chest = (entity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).copy();
        ItemStack legg = (entity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).copy();
        ItemStack boot = (entity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).copy();

        if (helm.getItem() == CAItems.KNIGHT_IRON_HELMET.get()) rate = rate + 0.25;
        if (chest.getItem() == CAItems.KNIGHT_IRON_CHESTPLATE.get()) rate = rate + 0.25;
        if (legg.getItem() == CAItems.KNIGHT_IRON_LEGGINGS.get()) rate = rate + 0.25;
        if (boot.getItem() == CAItems.KNIGHT_IRON_BOOTS.get()) rate = rate + 0.25;

        if (rate > 0) {
            freeze = sourceentity.getTicksFrozen();
            if (freeze < 140) {
                if ((entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) < (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.5) {
                    sourceentity.setTicksFrozen((int) Math.min(freeze + 120 * rate, 200));
                } else {
                    sourceentity.setTicksFrozen((int) Math.min(freeze + 60 * rate, 200));
                }
            } else {
                if (!(sourceentity instanceof LivingEntity livEnt13 && livEnt13.hasEffect(CAMobEffects.FROZEN))) {
                    if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(sourceentity.getX(), sourceentity.getY(), sourceentity.getZ()), CASounds.LAST_JNIGHT_FREEZE.get(), SoundSource.HOSTILE, 4, (float) Mth.nextDouble(RandomSource.create(), 1, 1.15));
                    }
                }
                if ((entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) < (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.5) {
                    if (sourceentity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                        livingEntity.addEffect(new MobEffectInstance(CAMobEffects.FROZEN, (int) (120 * rate), 0, false, false));
                } else {
                    if (sourceentity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                        livingEntity.addEffect(new MobEffectInstance(CAMobEffects.FROZEN, (int) (60 * rate), 0, false, false));
                }
            }
        }
        if (rate > 0.8) {
            if (sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born")))) {
                if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 1));
            }
        }
    }

    //TODO 需要下放到海嗣BOSS基类
    private static void handleBossHit(LivingDamageEvent.Pre event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (damagesource.is(CADamageTags.NEVER_TRIGGER_BOSS_PROTECTION)) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born_boss")))) {
            if (entity.tickCount - (entity instanceof LivingEntity livEnt ? livEnt.getLastHurtByMobTimestamp() : 0) < 5) {
                if (Math.random() < 0.33) {
                    if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10, 4, false, true));
                    if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                        livingEntity.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 10, 0, false, true));
                }
            }
        }
    }

    private static void handleCorruptedBurdenDamage(LivingDamageEvent.Pre event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getNewDamage();

        if (sourceentity == null) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born")))) {
            if (entity instanceof SkadiCorruptedEntity) return;
            if (amount > (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1)) return;

            Entity skadiCorrupted = world.getEntitiesOfClass(SkadiCorruptedEntity.class, AABB.ofSize(new Vec3(x, y, z), 32, 32, 32), e -> true).stream()
                    .min(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z))).orElse(null);

            if (skadiCorrupted == null) return;
            if (entity.distanceTo(skadiCorrupted) > 16) return;
            if (skadiCorrupted == entity) return;
            if (skadiCorrupted == sourceentity) return;
            if (entity == (skadiCorrupted instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null)) return;

            if ((skadiCorrupted instanceof SkadiCorruptedEntity datEntI ? datEntI.getEntityData().get(SkadiCorruptedEntity.DATA_PHASE) : 0) < 0.5) {
                event.setNewDamage((float) (amount * 0.5));
                skadiCorrupted.hurt(damagesource, (float) (amount * 0.5));
            }
        }
    }

    private static void handleDamageBurdenVeicle(LivingDamageEvent.Pre event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getNewDamage();

        if (entity.isAlive() && entity.isPassenger()) {
            Entity vehicle = entity.getVehicle();
            if (vehicle != null && vehicle.isAlive()) {
                if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born")))) {
                    if (vehicle instanceof OceanizedRavagerEntity) {
                        event.setNewDamage((float) (amount * 0.5));
                        vehicle.hurt(damagesource, (float) (amount * 0.5));
                    } else if (vehicle instanceof OceanizedPolarBearEntity) {
                        event.setNewDamage((float) (amount * 0.65));
                        vehicle.hurt(damagesource, (float) (amount * 0.35));
                    }
                }
            }
        }
    }

    private static void handleCrimsonTreaty(LivingDamageEvent.Pre event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getNewDamage();

        if (sourceentity == null) return;

        boolean valid = false;
        String regName;

        if (entity instanceof Player player && Relic.TREATY.gained(player)) {
            if (sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("forge:nether_mobs")))) {
                valid = true;
            } else {
                regName = BuiltInRegistries.ENTITY_TYPE.getKey(sourceentity.getType()).toString();
                for (String stringiterator : CAConfigs.CRIMSON_TREATY.get()) {
                    if (PlayerStateUtils.matchesRegistryName(stringiterator, regName)) {
                        valid = true;
                        break;
                    }
                }
            }
            if (valid) {
                if (sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("forge:bosses")))) {
                    event.setNewDamage((float) (amount * 0.5));
                } else {
                    event.setNewDamage((float) (amount * 0.01));
                }
                if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1, (float) Mth.nextDouble(RandomSource.create(), 0.8, 1.2));
                }
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.SMOKE, x, (y + 1), z, 16, 1, 1, 1, 0.1);
            }
        }
    }

    private static void handleExtraMagicdamage(LivingDamageEvent.Pre event) {
        LevelAccessor world = event.getEntity().level();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getNewDamage();
        double growLevel = MapVariables.get(world).strategy_grow;

        if (sourceentity == null) return;

        if (growLevel >= 3) {
            double magicMult = GrowUpgradeManager.getGrowMagicMultiplier(growLevel);
            if (sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born")))
                    && !damagesource.is(CADamageTypes.OCEAN_MAGIC)) {
                if (entity.isAlive() && sourceentity.isAlive()) {
                    entity.hurt(CADamageTypes.source(world, CADamageTypes.OCEAN_MAGIC),
                            (float) (amount * magicMult));
                }
            }
        }
    }

    private static void handleHandHoeSword(LivingDamageEvent.Pre event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (sourceentity == null) return;

        if (sourceentity instanceof Player player && damagesource.is(DamageTypes.PLAYER_ATTACK)) {
            ItemStack item_temp = (sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
            if (item_temp.getItem() instanceof HoeItem || item_temp.is(ItemTags.create(ResourceLocation.parse("minecraft:hoes")))) {
                if (RelicUtils.hasRelic(Relic.HAND_FERTILITY, player)) {
                    entity.hurt(CADamageTypes.source(world, CADamageTypes.HAND_OF_CHOKER, sourceentity), (float) ((entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) * 0.075));
                    if (world instanceof ServerLevel level)
                        level.sendParticles(ParticleTypes.SQUID_INK, x, y, z, 8, 0.75, 0.9, 0.75, 0.1);
                }
            }
            if (item_temp.getItem() instanceof SwordItem || item_temp.is(ItemTags.create(ResourceLocation.parse("minecraft:swords")))) {
                if (RelicUtils.hasRelic(Relic.HAND_SWORD, player)) {
                    if (!(entity instanceof LivingEntity livEnt11 && livEnt11.hasEffect(BabelMobEffects.LESS_ARMOR))) {
                        if (entity instanceof LivingEntity living && !entity.level().isClientSide())
                            living.addEffect(new MobEffectInstance(BabelMobEffects.LESS_ARMOR, 120, 1));
                    }
                    if (sourceentity instanceof LivingEntity living && !entity.level().isClientSide()) {
                        living.addEffect(new MobEffectInstance(CAMobEffects.ADD_REACH, 120, 3, false, false));
                        living.heal((float) (living.getMaxHealth() * 0.1));
                    }
                }
            }
        }
    }

    private static void handleHandThorns(LivingDamageEvent.Pre event) {
        LevelAccessor world = event.getEntity().level();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (sourceentity == null) return;

        if (entity instanceof Player && entity.tickCount - (entity instanceof LivingEntity livEnt ? livEnt.getLastHurtByMobTimestamp() : 0) >= 5) {
            if (entity instanceof Player player && RelicUtils.hasRelic(Relic.HAND_THORNS, player)) {
                if (!entity.isShiftKeyDown() && sourceentity.isAlive() && entity.isAlive()) {
                    if (!(sourceentity instanceof Player)) {
                        if (!damagesource.is(CADamageTypes.HAND_SPIKE) && !damagesource.is(DamageTypes.THORNS) && !damagesource.is(CADamageTypes.GUNMU_DAMAGE)) {
                            CaerulaArbor.queueServerWork(2, () -> {
                                if (!(sourceentity instanceof TamableAnimal tamIsTamedBy && entity instanceof LivingEntity livEnt && tamIsTamedBy.isOwnedBy(livEnt))) {
                                    if (world instanceof ServerLevel level)
                                        level.sendParticles(ParticleTypes.SMOKE, (sourceentity.getX()), (sourceentity.getY()), (sourceentity.getZ()), 72, 0.85, 1, 0.85, 0.2);
                                    sourceentity.hurt(CADamageTypes.source(world, CADamageTypes.HAND_SPIKE, entity), entity instanceof LivingEntity livEnt ? livEnt.getArmorValue() : 0);
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    private static void handleHuntersHit(LivingDamageEvent.Pre event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getNewDamage();

        if (damagesource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "hunters")))) {
            double factor = 1;
            if (GladiiaEntity.getGladiiaAround(world, x, y, z) != null) {
                factor = 0.7;
            }
            if (entity instanceof GladiiaEntity) {
                double xxx = EntityUtils.getHealthPerc(entity);
                factor = factor * Math.max(1 - 2.68 * Math.pow(xxx - 1, 2), 0.33);
            } else if (entity instanceof SpecterDollEntity) {
                Entity lastEnemt = (entity instanceof LivingEntity livingEntity) ? livingEntity.getLastHurtByMob() : null;
                if (!(lastEnemt == null) && (lastEnemt != null ? entity.distanceTo(lastEnemt) : -1) <= 9) {
                    factor = factor * 0.65;
                }
            }
            event.setNewDamage((float) (amount * factor));
        }
    }

    //TODO 可能需要下放
    private static void handleOnArrowHit(LivingDamageEvent.Pre event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getNewDamage();

        if (world.isClientSide()) return;

        Entity arrow = damagesource.getDirectEntity();
        if (arrow instanceof Arrow) {
            if (arrow.getPersistentData().getBoolean("ComplexChitin")) {
                if (entity instanceof LivingEntity target) {
                    SIHelper.causeSanityInjury(target, amount * 4, SanityEvent.Hurt.Type.ENTITY);
                }
                for (int index0 = 0; index0 < 3; index0++) {
                    double yaw = Mth.nextInt(RandomSource.create(), -30, 30);
                    double sine = Math.sin(Math.toRadians(yaw));
                    double cosine = Math.cos(Math.toRadians(yaw));
                    double vx = arrow.getDeltaMovement().x();
                    double vz = arrow.getDeltaMovement().z();
                    if (world instanceof ServerLevel projectileLevel) {
                        AbstractArrow entityToSpawn = new Arrow(EntityType.ARROW, projectileLevel);
                        entityToSpawn.setOwner(damagesource.getEntity());
                        entityToSpawn.setBaseDamage((float) (amount * 0.64));
                        entityToSpawn.setCritArrow(true);
                        entityToSpawn.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        entityToSpawn.setPos(x, (arrow.getY()), z);
                        entityToSpawn.shoot((1.5 * (vx * cosine + vz * sine)), (1.5 + arrow.getDeltaMovement().y()), (1.5 + vz * cosine - vx * sine), (float) 1.5, (float) 0.05);
                        projectileLevel.addFreshEntity(entityToSpawn);
                    }
                }
            }
            double lll = arrow.getPersistentData().getDouble("TrailriteLink");
            if (lll > 0) {
                if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                    livingEntity.addEffect(new MobEffectInstance(CAMobEffects.COOLDOWN_SINAL, 20, 0, false, false));
                double y1 = arrow.getY();
                Entity entity1 = damagesource.getEntity();
                if (entity1 != null) {
                    final Vec3 center = new Vec3(x, y1, z);
                    List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(24 / 2d),
                            e -> e != entity1 && !e.hasEffect(CAMobEffects.COOLDOWN_SINAL));
                    LivingEntity nextTarget = null;
                    double minDist = -1.0D;
                    Entity recentVictim = (entity1 instanceof LivingEntity livingEntity) ? livingEntity.getLastHurtMob() : null;
                    Entity recentAttacker = (entity1 instanceof LivingEntity livingEntity) ? livingEntity.getLastHurtByMob() : null;
                    for (LivingEntity entityiterator : entfound) {
                        boolean isValid;
                        if (entityiterator instanceof Monster) {
                            isValid = true;
                        } else {
                            isValid = (entityiterator instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) == entity1
                                    || entityiterator == recentVictim
                                    || entityiterator == recentAttacker;
                        }
                        if (isValid) {
                            double d = entityiterator.distanceToSqr(x, y1, z);
                            if (minDist == -1.0D || d < minDist) {
                                minDist = d;
                                nextTarget = entityiterator;
                            }
                        }
                    }
                    if (nextTarget != null && world instanceof ServerLevel projectileLevel) {
                        AbstractArrow entityToSpawn = new Arrow(EntityType.ARROW, projectileLevel);
                        entityToSpawn.setOwner(entity1);
                        entityToSpawn.setBaseDamage((float) amount);
                        entityToSpawn.setCritArrow(true);
                        entityToSpawn.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        entityToSpawn.setPos(x, y1, z);
                        entityToSpawn.getPersistentData().putDouble("TrailriteLink", lll - 1);
                        entityToSpawn.shoot((nextTarget.getX() - x), ((nextTarget.getY() + nextTarget.getBbHeight() * 0.9) - y1), (nextTarget.getZ() - z), (float) 1.75, 0);
                        projectileLevel.addFreshEntity(entityToSpawn);
                    }
                }
                if (lll > 4) {
                    if (world instanceof ServerLevel level)
                        level.sendParticles(CAParticles.MOIST_BOOM.get(), x, (y + 0.5), z, 2, 0.1, 0.1, 0.1, 0.1);
                    if (entity instanceof LivingEntity target) {
                        if (entity1 instanceof LivingEntity attacker) {
                            SIHelper.causeSanityInjury(target, attacker, amount * 5, SanityEvent.Hurt.Type.ENTITY);
                        } else {
                            SIHelper.causeSanityInjury(target, amount * 5, SanityEvent.Hurt.Type.ENTITY);
                        }
                    }
                    if (entity instanceof LivingEntity living)
                        LessArmorMobEffect.apply(living);
                }
            }
        }
    }

    private static void handleSanityReaper(LivingDamageEvent.Pre event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getNewDamage();

        if (sourceentity == null) return;

        ItemStack mainHandItem = (sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
        if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_REAPER), mainHandItem) != 0) {
            double lvl = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_REAPER), mainHandItem);
            if (entity instanceof LivingEntity target && sourceentity instanceof LivingEntity attacker) {
                SIHelper.causeSanityInjury(target, attacker, amount * 2 * lvl, SanityEvent.Hurt.Type.ENTITY);
            }
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 1 + entity.getBbHeight() * 0.5), z, (int) Math.min(8 * lvl, 40), 1, 1, 1.2, 0.1);
        }
    }

    private static void handleSeabornKiller(LivingDamageEvent.Pre event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getNewDamage();

        if (sourceentity == null) return;

        if (entity.getType().is(SEA_BORN) && EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.OCEANOSPR_KILLER), (sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY)) != 0) {
            double lvl = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.OCEANOSPR_KILLER), (sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY));
            double addition = Math.max(amount * lvl * 0.15, lvl * 5);
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.ENCHANTED_HIT, x, (y + 0.6), z, 24, 0.6, 0.6, 0.6, 0.1);
            event.setNewDamage((float) (amount + addition));
        }
    }

    private static void handleSeabornsGetOffShip(LivingDamageEvent.Pre event) {
        LevelAccessor world = event.getEntity().level();
        Entity entity = event.getEntity();

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "oceanelite"))) && WorldUtils.canGrief(world)) {
            LivingEntity living = entity instanceof Mob mobEnt ? mobEnt.getTarget() : null;
            if (living != null && living.isAlive()) {
                Entity boat = entity.getVehicle();
                if (boat instanceof Boat) {
                    boat.hurt(CADamageTypes.source(world, CADamageTypes.GENERIC_SEABORN_ATTACK), 20);
                }
            }
        }
    }

    //TODO 需要破罐是否需要下放
    private static void handleMoreFallDamageEffect(LivingDamageEvent.Pre event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getNewDamage();

        Entity bullet = damagesource.getDirectEntity();
        if (bullet instanceof ShulkerBullet && bullet.getPersistentData().getBoolean("oceanized")) {
            if (entity instanceof LivingEntity living)
                LessArmorMobEffect.apply(living);
            if (entity instanceof LivingEntity living && !living.level().isClientSide())
                living.addEffect(new MobEffectInstance(CAMobEffects.MORE_FALL_DAMAGE, 300, 0));
        }
        if (damagesource.is(DamageTypes.FALL) && entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(CAMobEffects.MORE_FALL_DAMAGE)) {
            double level = livingEntity.getEffect(CAMobEffects.MORE_FALL_DAMAGE).getAmplifier() + 1;
            event.setNewDamage((float) (amount * (1 + 0.25 * level)));
        }
    }

    private static void handleWarriorTactic(LivingDamageEvent.Pre event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getNewDamage();

        if (sourceentity == null) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born"))) && sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "warriors")))) {
            if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                livingEntity.addEffect(new MobEffectInstance(BabelMobEffects.LESS_ARMOR, 100, 0, false, false));
        }

        switch (entity) {
            case JuniorWarriorPriestEntity juniorWarriorPriestEntity -> {
                if (MathUtils.getCosine(sourceentity.getX() - entity.getX(), entity.getLookAngle().x, sourceentity.getZ() - entity.getZ(), entity.getLookAngle().z) >= 0.5) {
                    if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, (float) 0.75, 1);
                    }
                    event.setNewDamage((float) (amount * 0.6));
                }
            }
            case WarriorPriestEntity warriorPriestEntity -> {
                if (MathUtils.getCosine(sourceentity.getX() - entity.getX(), entity.getLookAngle().x, sourceentity.getZ() - entity.getZ(), entity.getLookAngle().z) >= 0.5) {
                    if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, (float) 0.75, 1);
                    }
                    event.setNewDamage((float) (amount * 0.5));
                }
            }
            case CorrectionalPhalanxyInfantryEntity correctionalPhalanxyInfantryEntity -> {
                double rate = 1;
                double less = 1;
                if (MathUtils.getCosine(sourceentity.getX() - entity.getX(), entity.getLookAngle().x, sourceentity.getZ() - entity.getZ(), entity.getLookAngle().z) >= 0.5) {
                    if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, (float) 0.75, 1);
                    }
                    rate = 0.5;
                }
                final Vec3 center = new Vec3(x, y, z);
                List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(16 / 2d),
                        e -> e != entity && e.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "phalax"))));
                for (LivingEntity ignored : entfound) {
                    less = less - 0.06;
                    if (less <= 0.4) break;
                }
                event.setNewDamage((float) (amount * rate * less));
            }
            case IreneEntity ireneEntity -> {
                double less = 1;
                final Vec3 center = new Vec3(x, y, z);
                List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(16 / 2d),
                        e -> e != entity && e.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "phalax"))));
                for (LivingEntity ignored : entfound) {
                    less = less - 0.06;
                    if (less <= 0.4) break;
                }
                event.setNewDamage((float) (amount * less));
            }
            case CorrectinalPhalaxVanguardEntity correctinalPhalaxVanguardEntity -> {
                if (MathUtils.getCosine(sourceentity.getX() - entity.getX(), entity.getLookAngle().x, sourceentity.getZ() - entity.getZ(), entity.getLookAngle().z) <= -0.5) {
                    if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, (float) 0.75, 1);
                    }
                    event.setNewDamage((float) (amount * 0.5));
                }
            }
            default -> {
            }
        }
    }

    private static void handlePlayerEvolutionDamageReduction(LivingDamageEvent.Pre event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getNewDamage();

        if (!(entity instanceof Player player) || !EntityUtils.canPlayerEvo(player)) return;

        boolean isIndirect = damagesource.getDirectEntity() != damagesource.getEntity();
        double rate = 1;
        double e = ModCapabilities.getPlayerVariables(player).PEVO_NODE_less_damage;
        double finalAmount = amount;

        if (PlayerStateUtils.isNexusExpoShieldSelected(player)) {
            finalAmount = Math.min(32 * Math.pow(finalAmount / 32, 0.75), finalAmount);
        }

        if (e >= 1 && !isIndirect) rate = 0.85;
        if (e >= 2 && isIndirect) rate = 0.85;
        if (e >= 3 && !isIndirect) rate = 0.5;
        if (e >= 4 && isIndirect) rate = 0.5;

        if (rate < 1) {
            finalAmount = finalAmount * rate;
        }

        if (EntityUtils.getHealthPerc(player) <= 0.5) {
            e = NodeUtils.getNodeEunectes(player);
            if (e >= 4) rate = 0.5;
            else if (e >= 3) rate = 0.3;
            else if (e >= 2) rate = 0.15;
            else if (e >= 1) rate = 0.05;

            if (rate < 1) {
                finalAmount = finalAmount * rate;
            }
        }

        if (finalAmount < amount) {
            event.setNewDamage((float) finalAmount);
        }
    }

    private static void handlePlayerEvolutionDamageAmplification(LivingDamageEvent.Pre event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getNewDamage();

        if (sourceentity == null) return;

        if (!(sourceentity instanceof Player attacker) || !EntityUtils.canPlayerEvo(attacker)) return;

        boolean isIndirect = damagesource.getDirectEntity() != damagesource.getEntity();
        double rate = 1;
        double e = NodeUtils.getNodeAddDamage(attacker);
        double finalValue = 0;

        if (e >= 1 && !isIndirect) rate = 1.2;
        if (e >= 2 && isIndirect) rate = 1.2;
        if (e >= 3 && !isIndirect) rate = 1.6;
        if (e >= 4 && isIndirect) rate = 1.6;

        if (rate > 1) {
            finalValue = amount * rate;
        }

        if (entity instanceof LivingEntity livEnt && BabelAPI.isUnderBreak(livEnt)) {
            e = NodeUtils.getNodeWorseBreak(attacker);
            if (e >= 4) rate = 2.4;
            else if (e >= 3) rate = 1.9;
            else if (e >= 2) rate = 1.5;
            else if (e >= 1) rate = 1.2;

            if (rate > 1) {
                finalValue = finalValue * rate;
            }
        }

        e = ModCapabilities.getPlayerVariables(attacker).PEVO_NODE_less_armor;

        if (e > 0) {
            double lll = 0;
            if (e >= 4) {
                rate = 1.4;
                lll = 23;
            } else if (e >= 3) {
                rate = 1.15;
                lll = 23;
            } else if (e >= 2) {
                rate = 1;
                lll = 17;
            } else if (e >= 1) {
                rate = 1;
                lll = 17;
            }

            for (int index0 = 0; index0 < (int) e; index0++) {
                LessArmorMobEffect.apply((LivingEntity) entity);
            }

            if ((entity instanceof LivingEntity livEnt && livEnt.hasEffect(BabelMobEffects.LESS_ARMOR) ? livEnt.getEffect(BabelMobEffects.LESS_ARMOR).getAmplifier() : 0) >= lll) {
                if (rate > 1) {
                    finalValue = finalValue * rate;
                }
            }
        }

        if (PlayerStateUtils.isNexusPercDamageSelected(attacker)) {
            double hitTime = attacker.getPersistentData().getDouble("playerEvoHitTime");
            double perc;
            double result = 0;

            if (entity == attacker.getLastHurtMob()) {
                perc = Math.min((int) ((hitTime - 1) / 2) * 0.005, 0.25);
                attacker.getPersistentData().putDouble("playerEvoHitTime", (hitTime + 1));
                result = perc * (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1);
            } else {
                attacker.getPersistentData().putDouble("playerEvoHitTime", 0);
            }

            finalValue = Math.clamp(finalValue, result, finalValue * 32);
        }

        if (finalValue > amount) {
            event.setNewDamage((float) finalValue);
        }
    }
}