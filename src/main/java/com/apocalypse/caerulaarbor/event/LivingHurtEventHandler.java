package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.config.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.entity.*;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
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
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber
public class LivingHurtEventHandler {

    public static final TagKey<DamageType> B_PROTECTION = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bypass_protection"));
    public static final TagKey<DamageType> IS_MAGIC = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_magic"));
    public static final TagKey<DamageType> B_DEFENSE = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bypass_defense"));
    public static final ResourceKey<DamageType> NETHERSEA_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "trail_damage"));

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onEntityHurt(LivingHurtEvent event) {
        if (event == null || event.getEntity() == null) return;

        handleBarrierFunc(event);
        handleMagicResis(event);
        handleCompchitinLimitDamage(event);
        handleFlamarineHurt(event);
        handleTrailriteAttackBonus(event);
        handleArmorKnight(event);
        handleBossHit(event);
        handleChimeraReefbreaker(event);
        handleChimeraKilledByApocata(event);
        handleCorruptedBurdenDamage(event);
        handleDamageBurdenVeicle(event);
        handleCrimsonTreaty(event);
        handleEndspeakerHurt(event);
        handleEndspeakerttack(event);
        handleExtraMagicdamage(event);
        handleGladiiaAttackBonus(event);
        handleHandHoeSword(event);
        handleHandThorns(event);
        handleHuntersHit(event);
        handleOnArrowHit(event);
        handleSanityReaper(event);
        handleSeabornKiller(event);
        handleSeabornsGetOffShip(event);
        handleMoreFallDamageEffect(event);
        handleSlimeFunc(event);
        handleWarriorTactic(event);
        handlePlayerEvolutionDamageReduction(event);
        handlePlayerEvolutionDamageAmplification(event);
        handleKillMuteSelf(event);
    }

    private static void handleKillMuteSelf(LivingHurtEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (damagesource == null || entity == null) return;

        if (damagesource.is(DamageTypes.GENERIC_KILL)) {
            if (entity instanceof BaselayerAbyssalEntity _datEntSetI)
                _datEntSetI.getEntityData().set(BaselayerAbyssalEntity.DATA_mute_time, 100);
        }
    }

    private static void handleBarrierFunc(LivingHurtEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getAmount();

        if (damagesource == null || entity == null) return;

        if (damagesource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || damagesource.is(DamageTypeTags.BYPASSES_EFFECTS)) return;

        double brr = entity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER.get())
                ? _livingEntity1.getAttribute(CAAttributes.LIVING_BARRIER.get()).getBaseValue()
                : 0;

        if (brr > 0) {
            double disp;
            if (brr >= amount) {
                if (entity instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER.get()))
                    _livingEntity2.getAttribute(CAAttributes.LIVING_BARRIER.get()).setBaseValue((brr - amount));
                disp = amount;
                event.setAmount(0);
            } else {
                if (entity instanceof LivingEntity _livingEntity4 && _livingEntity4.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER.get()))
                    _livingEntity4.getAttribute(CAAttributes.LIVING_BARRIER.get()).setBaseValue(0);
                disp = brr;
                event.setAmount((float) (amount - brr));
            }
            if (world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "living_barrier")), SoundSource.HOSTILE, 2, (float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
            }
            if (world instanceof ServerLevel _level)
                _level.sendParticles(CAParticles.LIVING_BARRIER_SHOW.get(), x, (y + 0.75), z, (int) Math.min(disp * 0.5, 24), 0.75, 0.75, 0.75, 0.1);
        }
    }

    private static void handleMagicResis(LivingHurtEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getAmount();

        if (damagesource == null || entity == null) return;

        if (damagesource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
        if (damagesource.is(DamageTypeTags.BYPASSES_EFFECTS)) return;
        if (damagesource.is(B_PROTECTION)) return;

        if (damagesource.is(IS_MAGIC) && !damagesource.is(DamageTypeTags.BYPASSES_ENCHANTMENTS)) {
            double mgc_resis = entity instanceof LivingEntity _livingEntity5 && _livingEntity5.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get())
                    ? _livingEntity5.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).getValue()
                    : 0;
            if (mgc_resis > 0) {
                event.setAmount((float) Math.max(amount * 0.01 * (100 - mgc_resis), amount * 0.05));
            }
        } else {
            double def = entity instanceof LivingEntity _livingEntity7 && _livingEntity7.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get())
                    ? _livingEntity7.getAttribute(CAAttributes.GENERAL_DEFENSE.get()).getValue()
                    : 0;
            if (def > 0 && !damagesource.is(B_DEFENSE)) {
                event.setAmount((float) Math.max(amount - def, amount * 0.05));
            }
        }
    }

    private static void handleCompchitinLimitDamage(LivingHurtEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getAmount();

        if (damagesource == null || entity == null) return;

        if (damagesource.is(TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("minecraft:bypasses_invulnerability")))) return;
        if (damagesource.is(B_PROTECTION)) return;

        if (entity instanceof ComplexChitinGolemEntity && amount > (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.1) {
            event.setAmount((float) ((entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.1));
        }
    }

    private static void handleFlamarineHurt(LivingHurtEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getAmount();

        if (damagesource == null || entity == null) return;

        double gap = entity.tickCount - (entity instanceof LivingEntity _livEnt ? _livEnt.getLastHurtByMobTimestamp() : 0);
        double ratie = 1;

        if (damagesource.is(TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("minecraft:bypasses_invulnerability")))) return;

        if (entity instanceof FlamarineStatueEntity || entity instanceof FlamarineGolemEntity) {
            if (gap < 10) {
                ratie = Math.max(gap * 0.1, 0.5);
            }
            event.setAmount((float) Math.min(amount * ratie, (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.34));

            if (entity instanceof FlamarineStatueEntity) {
                if (entity instanceof LivingEntity _livingEntity8 && _livingEntity8.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get()))
                    _livingEntity8.getAttribute(CAAttributes.GENERAL_DEFENSE.get())
                            .setBaseValue(Math.max((entity instanceof LivingEntity _livingEntity7 && _livingEntity7.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get())
                                    ? _livingEntity7.getAttribute(CAAttributes.GENERAL_DEFENSE.get()).getBaseValue()
                                    : 0) - 0.5, 1));
                if (entity instanceof LivingEntity _livingEntity10 && _livingEntity10.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
                    _livingEntity10.getAttribute(CAAttributes.MAGIC_RESISTANCE.get())
                            .setBaseValue(Math.max((entity instanceof LivingEntity _livingEntity9 && _livingEntity9.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get())
                                    ? _livingEntity9.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).getBaseValue()
                                    : 0) - 2, 15));
            } else {
                double add = entity instanceof FlamarineGolemEntity _datEntI ? _datEntI.getEntityData().get(FlamarineGolemEntity.DATA_addition) : 0;
                if (add > 0) {
                    if (entity instanceof LivingEntity _livingEntity13 && _livingEntity13.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get()))
                        _livingEntity13.getAttribute(CAAttributes.GENERAL_DEFENSE.get())
                                .setBaseValue(((entity instanceof LivingEntity _livingEntity12 && _livingEntity12.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get())
                                        ? _livingEntity12.getAttribute(CAAttributes.GENERAL_DEFENSE.get()).getBaseValue()
                                        : 0) + 1));
                    if (entity instanceof LivingEntity _livingEntity15 && _livingEntity15.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
                        _livingEntity15.getAttribute(CAAttributes.MAGIC_RESISTANCE.get())
                                .setBaseValue(((entity instanceof LivingEntity _livingEntity14 && _livingEntity14.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get())
                                        ? _livingEntity14.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).getBaseValue()
                                        : 0) + 1.5));
                    if (entity instanceof FlamarineGolemEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(FlamarineGolemEntity.DATA_addition, (int) (add - 1));
                }
            }
        }
    }

    private static void handleTrailriteAttackBonus(LivingHurtEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (damagesource == null || entity == null || sourceentity == null) return;

        ItemStack helm = (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).copy();
        ItemStack chest = (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).copy();
        ItemStack legg = (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).copy();
        ItemStack boot = (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).copy();

        if (helm.getItem() == CAItems.TRAILRITE_ARMOR_HELMET.get() && chest.getItem() == CAItems.TRAILRITE_ARMOR_CHESTPLATE.get()
                && legg.getItem() == CAItems.TRAILRITE_ARMOR_LEGGINGS.get() && boot.getItem() == CAItems.TRAILRITE_ARMOR_BOOTS.get()) {
            EntityUtils.giveLessArmor(entity, 24);
        }

        helm = (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).copy();
        chest = (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).copy();
        legg = (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).copy();
        boot = (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).copy();

        if (helm.getItem() == CAItems.TRAILRITE_ARMOR_HELMET.get() && chest.getItem() == CAItems.TRAILRITE_ARMOR_CHESTPLATE.get()
                && legg.getItem() == CAItems.TRAILRITE_ARMOR_LEGGINGS.get() && boot.getItem() == CAItems.TRAILRITE_ARMOR_BOOTS.get()) {
            if (!damagesource.is(TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("minecraft:bypasses_invulnerability")))) {
                double gap = entity.tickCount - (entity instanceof LivingEntity _livEnt ? _livEnt.getLastHurtByMobTimestamp() : 0);
                double maxH = entity instanceof LivingEntity _livingEntity18 && _livingEntity18.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? _livingEntity18.getAttribute(Attributes.MAX_HEALTH).getValue() : 0;
                if (gap < 10) {
                    event.setAmount((float) Math.min(amount * Math.min(1, Math.max(gap * 0.1, 0.05)), Math.max(maxH * 0.33, 16)));
                }
            }
        }
    }

    private static void handleArmorKnight(LivingHurtEvent event) {
        LevelAccessor world = event.getEntity().level();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (entity == null || sourceentity == null) return;

        double rate = 0;
        double freeze;
        ItemStack helm = (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).copy();
        ItemStack chest = (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).copy();
        ItemStack legg = (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).copy();
        ItemStack boot = (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).copy();

        if (helm.getItem() == CAItems.KNIGHT_IRON_HELMET.get()) rate = rate + 0.25;
        if (chest.getItem() == CAItems.KNIGHT_IRON_CHESTPLATE.get()) rate = rate + 0.25;
        if (legg.getItem() == CAItems.KNIGHT_IRON_LEGGINGS.get()) rate = rate + 0.25;
        if (boot.getItem() == CAItems.KNIGHT_IRON_BOOTS.get()) rate = rate + 0.25;

        if (rate > 0) {
            freeze = sourceentity.getTicksFrozen();
            if (freeze < 140) {
                if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5) {
                    sourceentity.setTicksFrozen((int) Math.min(freeze + 120 * rate, 200));
                } else {
                    sourceentity.setTicksFrozen((int) Math.min(freeze + 60 * rate, 200));
                }
            } else {
                if (!(sourceentity instanceof LivingEntity _livEnt13 && _livEnt13.hasEffect(CAMobEffects.FROZEN.get()))) {
                    if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(sourceentity.getX(), sourceentity.getY(), sourceentity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "last_jnight_freeze")), SoundSource.HOSTILE, 4, (float) Mth.nextDouble(RandomSource.create(), 1, 1.15));
                    }
                }
                if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5) {
                    if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(CAMobEffects.FROZEN.get(), (int) (120 * rate), 0, false, false));
                } else {
                    if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(CAMobEffects.FROZEN.get(), (int) (60 * rate), 0, false, false));
                }
            }
        }
        if (rate > 0.8) {
            if (sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 1));
            }
        }
    }

    private static void handleBossHit(LivingHurtEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (damagesource == null || entity == null) return;
        if (event.isCanceled()) return;
        if (damagesource.is(TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "never_trigger_boss_protection")))) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bossoffspring")))) {
            if (entity.tickCount - (entity instanceof LivingEntity _livEnt ? _livEnt.getLastHurtByMobTimestamp() : 0) < 5) {
                if (Math.random() < 0.33) {
                    if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10, 4, false, true));
                    if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 10, 0, false, true));
                }
            }
        }
    }

    private static void handleChimeraReefbreaker(LivingHurtEvent event) {
        Entity sourceentity = event.getSource().getEntity();
        if (sourceentity == null) return;

        if (sourceentity instanceof TideChimeraEntity livEnt) {
            double amplifi = livEnt.hasEffect(CAMobEffects.REEF_CRACKER.get()) ? livEnt.getEffect(CAMobEffects.REEF_CRACKER.get()).getAmplifier() : 0;
            if (livEnt.hasEffect(CAMobEffects.REEF_CRACKER.get())) {
                if (amplifi < 31) {
                    LivingEntity _entity = (LivingEntity) sourceentity;
                    if (!_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(CAMobEffects.REEF_CRACKER.get(), 100, (int) (amplifi + 1), false, false));
                } else {
                    LivingEntity _entity = (LivingEntity) sourceentity;
                    if (!_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(CAMobEffects.REEF_CRACKER.get(), 100, 31, false, false));
                }
            } else {
                LivingEntity _entity = (LivingEntity) sourceentity;
                if (!_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CAMobEffects.REEF_CRACKER.get(), 100, 0, false, false));
            }
        }
    }

    private static void handleChimeraKilledByApocata(LivingHurtEvent event) {
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (entity == null || sourceentity == null) return;

        if (entity instanceof TideChimeraEntity) {
            String name = sourceentity.getDisplayName().getString();
            if (name.contains("apocata") || name.contains("Apocata")) {
                event.setAmount((float) Math.max(1000000, amount));
            }
        }
    }

    private static void handleCorruptedBurdenDamage(LivingHurtEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (damagesource == null || entity == null || sourceentity == null) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
            if (entity instanceof SkadiCorruptedEntity) return;
            if (amount > (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1)) return;

            Entity skadiCorrupted = world.getEntitiesOfClass(SkadiCorruptedEntity.class, AABB.ofSize(new Vec3(x, y, z), 32, 32, 32), e -> true).stream()
                    .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(x, y, z))).findFirst().orElse(null);

            if (skadiCorrupted == null) return;
            if ((skadiCorrupted != null ? entity.distanceTo(skadiCorrupted) : -1) > 16) return;
            if (skadiCorrupted == entity) return;
            if (skadiCorrupted == sourceentity) return;
            if (entity == (skadiCorrupted instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null)) return;

            if ((skadiCorrupted instanceof SkadiCorruptedEntity _datEntI ? _datEntI.getEntityData().get(SkadiCorruptedEntity.DATA_phase) : 0) < 0.5) {
                event.setAmount((float) (amount * 0.5));
                skadiCorrupted.hurt(damagesource, (float) (amount * 0.5));
            }
        }
    }

    private static void handleDamageBurdenVeicle(LivingHurtEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getAmount();

        if (damagesource == null || entity == null) return;

        if (entity.isAlive() && entity.isPassenger()) {
            Entity vehicle = entity.getVehicle();
            if (vehicle != null && vehicle.isAlive()) {
                if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                    if (vehicle instanceof OceanizedRavagerEntity) {
                        event.setAmount((float) (amount * 0.5));
                        vehicle.hurt(damagesource, (float) (amount * 0.5));
                    } else if (vehicle instanceof OceanizedPolarBearEntity) {
                        event.setAmount((float) (amount * 0.65));
                        vehicle.hurt(damagesource, (float) (amount * 0.35));
                    }
                }
            }
        }
    }

    private static void handleCrimsonTreaty(LivingHurtEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (entity == null || sourceentity == null) return;

        boolean valid = false;
        String regName;

        if (entity instanceof Player && (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_TREATY) {
            if (sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge:nether_mobs")))) {
                valid = true;
            } else {
                regName = ForgeRegistries.ENTITY_TYPES.getKey(sourceentity.getType()).toString();
                for (String stringiterator : CaerulaConfigsConfiguration.CRIMSON_TREATY.get()) {
                    if (ValidationUtils.isValidString(stringiterator, regName)) {
                        valid = true;
                        break;
                    }
                }
            }
            if (valid) {
                if (sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge:bosses")))) {
                    event.setAmount((float) (amount * 0.5));
                } else {
                    event.setAmount((float) (amount * 0.01));
                }
                if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.fire.extinguish")), SoundSource.PLAYERS, 1, (float) Mth.nextDouble(RandomSource.create(), 0.8, 1.2));
                }
                if (world instanceof ServerLevel _level)
                    _level.sendParticles(ParticleTypes.SMOKE, x, (y + 1), z, 16, 1, 1, 1, 0.1);
            }
        }
    }

    private static void handleEndspeakerHurt(LivingHurtEvent event) {
        LevelAccessor world = event.getEntity().level();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (entity == null || sourceentity == null) return;

        if (EndspeakerEntity.hasAbility(world, 3)) {
            if (sourceentity instanceof EndspeakerEntity && entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CAMobEffects.TRAIL_BUFF.get())) {
                event.setAmount((float) (amount * 1.5));
            }
            if (entity instanceof EndspeakerEntity
                    && (entity instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(CAMobEffects.TRAIL_BUFF.get()) || sourceentity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(CAMobEffects.TRAIL_BUFF.get()))) {
                event.setAmount((float) (amount * 0.65));
            }
        }
    }

    private static void handleEndspeakerttack(LivingHurtEvent event) {
        LevelAccessor world = event.getEntity().level();
        Entity sourceentity = event.getSource().getEntity();
        if (sourceentity == null) return;

        if (sourceentity instanceof EndspeakerEntity) {
            if (EndspeakerEntity.hasAbility(world, 5)) {
                double amplifi = sourceentity instanceof LivingEntity _livEnt && _livEnt.hasEffect(CAMobEffects.REEF_CRACKER.get()) ? _livEnt.getEffect(CAMobEffects.REEF_CRACKER.get()).getAmplifier() : 0;
                if (sourceentity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(CAMobEffects.REEF_CRACKER.get())) {
                    if (amplifi < 11) {
                        if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                            _entity.addEffect(new MobEffectInstance(CAMobEffects.REEF_CRACKER.get(), 120, (int) (amplifi + 1), false, false));
                    } else {
                        if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                            _entity.addEffect(new MobEffectInstance(CAMobEffects.REEF_CRACKER.get(), 80, 11, false, false));
                    }
                } else {
                    if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(CAMobEffects.REEF_CRACKER.get(), 80, 0, false, false));
                }
            }
        }
    }

    private static void handleExtraMagicdamage(LivingHurtEvent event) {
        LevelAccessor world = event.getEntity().level();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (damagesource == null || entity == null || sourceentity == null) return;

        if (MapVariables.get(world).strategy_grow >= 3) {
            if (sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))
                    && !damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_magic")))) {
                if (entity.isAlive() && sourceentity.isAlive()) {
                    entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_magic")))),
                            (float) (amount * 0.2 * (MapVariables.get(world).strategy_grow - 2)));
                }
            }
        }
    }

    private static void handleGladiiaAttackBonus(LivingHurtEvent event) {
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (entity == null || sourceentity == null) return;

        double factor;
        if (sourceentity instanceof GladiiaEntity) {
            factor = 1;
            if (EntityUtils.getSize(entity) < EntityUtils.getSize(sourceentity) * 2) {
                factor = factor * 1.3;
            }
            if (EntityUtils.getHealthPerc(entity) < EntityUtils.getHealthPerc(sourceentity)) {
                factor = factor * 1.5;
            }
            event.setAmount((float) (amount * factor));
        } else if (entity instanceof GladiiaEntity) {
            factor = 1;
            if (EntityUtils.getSize(sourceentity) >= EntityUtils.getSize(entity) * 2) {
                factor = 0.75;
            }
            event.setAmount((float) (amount * factor));
        }
    }

    private static void handleHandHoeSword(LivingHurtEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (damagesource == null || entity == null || sourceentity == null) return;

        if (sourceentity instanceof Player && damagesource.is(DamageTypes.PLAYER_ATTACK)) {
            ItemStack item_temp = (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
            if (item_temp.getItem() instanceof HoeItem || item_temp.is(ItemTags.create(new ResourceLocation("minecraft:hoes")))) {
                if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_FERTILITY) {
                    entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hand_of_choker"))), sourceentity),
                            (float) ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) * 0.075));
                    if (world instanceof ServerLevel _level)
                        _level.sendParticles(ParticleTypes.SQUID_INK, x, y, z, 8, 0.75, 0.9, 0.75, 0.1);
                }
            }
            if (item_temp.getItem() instanceof SwordItem || item_temp.is(ItemTags.create(new ResourceLocation("minecraft:swords")))) {
                if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_SWORD) {
                    if (!(entity instanceof LivingEntity _livEnt11 && _livEnt11.hasEffect(CAMobEffects.ROCK_BREAK.get()))) {
                        if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                            _entity.addEffect(new MobEffectInstance(CAMobEffects.ROCK_BREAK.get(), 120, 1));
                    }
                    if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_REACH.get(), 120, 3, false, false));
                        EntityUtils.heal(_entity, _entity.getMaxHealth() * 0.1);
                    }
                }
            }
        }
    }

    private static void handleHandThorns(LivingHurtEvent event) {
        LevelAccessor world = event.getEntity().level();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (damagesource == null || entity == null || sourceentity == null) return;

        if (entity instanceof Player && entity.tickCount - (entity instanceof LivingEntity _livEnt ? _livEnt.getLastHurtByMobTimestamp() : 0) >= 5) {
            if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_THORNS) {
                if (!entity.isShiftKeyDown() && sourceentity.isAlive() && entity.isAlive()) {
                    if (!(sourceentity instanceof Player)) {
                        if (!damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hand_spike"))) && !damagesource.is(DamageTypes.THORNS)
                                && !damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "gunmu_damage"))) && !(entity == sourceentity)) {
                            CaerulaArborMod.queueServerWork(2, () -> {
                                if (!(sourceentity instanceof TamableAnimal _tamIsTamedBy && entity instanceof LivingEntity _livEnt && _tamIsTamedBy.isOwnedBy(_livEnt))) {
                                    if (world instanceof ServerLevel _level)
                                        _level.sendParticles(ParticleTypes.SMOKE, (sourceentity.getX()), (sourceentity.getY()), (sourceentity.getZ()), 72, 0.85, 1, 0.85, 0.2);
                                    sourceentity.hurt(
                                            new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hand_spike"))), entity),
                                            entity instanceof LivingEntity _livEnt ? _livEnt.getArmorValue() : 0);
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    private static void handleHuntersHit(LivingHurtEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getAmount();

        if (damagesource == null || entity == null) return;

        if (damagesource.is(TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("minecraft:bypasses_invulnerability")))) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunters")))) {
            double factor = 1;
            if (GladiiaEntity.getGladiiaAround(world, x, y, z) != null) {
                factor = 0.7;
            }
            if (entity instanceof GladiiaEntity) {
                double xxx = EntityUtils.getHealthPerc(entity);
                factor = factor * Math.max(1 - 2.68 * Math.pow(xxx - 1, 2), 0.33);
            } else if (entity instanceof SpecterDollEntity) {
                Entity lastEnemt = (entity instanceof LivingEntity _entity) ? _entity.getLastHurtByMob() : null;
                if (!(lastEnemt == null) && (lastEnemt != null ? entity.distanceTo(lastEnemt) : -1) <= 9) {
                    factor = factor * 0.65;
                }
            }
            event.setAmount((float) (amount * factor));
        }
    }

    private static void handleOnArrowHit(LivingHurtEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getAmount();

        if (damagesource == null || entity == null) return;
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
                        Projectile _entityToSpawn = new Object() {
                            public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
                                AbstractArrow entityToSpawn = new Arrow(EntityType.ARROW, level);
                                entityToSpawn.setOwner(shooter);
                                entityToSpawn.setBaseDamage(damage);
                                entityToSpawn.setKnockback(knockback);
                                entityToSpawn.setPierceLevel(piercing);
                                entityToSpawn.setCritArrow(true);
                                entityToSpawn.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                                return entityToSpawn;
                            }
                        }.getArrow(projectileLevel, (damagesource.getEntity()), (float) (amount * 0.64), 0, (byte) 1);
                        _entityToSpawn.setPos(x, (arrow.getY()), z);
                        _entityToSpawn.shoot((1.5 * (vx * cosine + vz * sine)), (1.5 + arrow.getDeltaMovement().y()), (1.5 + vz * cosine - vx * sine), (float) 1.5, (float) 0.05);
                        projectileLevel.addFreshEntity(_entityToSpawn);
                    }
                }
            }
            double lll = arrow.getPersistentData().getDouble("TrailriteLink");
            if (lll > 0) {
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CAMobEffects.COOLDOWN_SINAL.get(), 20, 0, false, false));
                double y1 = arrow.getY();
                Entity entity1 = damagesource.getEntity();
                if (entity1 != null) {
                    if (!(lll <= 0)) {
                        final Vec3 _center = new Vec3(x, y1, z);
                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(24 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                        for (Entity entityiterator : _entfound) {
                            if (!(entityiterator instanceof LivingEntity)) continue;
                            if (entityiterator == entity1) continue;
                            if (entityiterator instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(CAMobEffects.COOLDOWN_SINAL.get())) continue;
                            if (!(entityiterator instanceof Monster)) {
                                Entity recentVictim = (entity1 instanceof LivingEntity _entity) ? _entity.getLastHurtMob() : null;
                                Entity recentAttacker = (entity1 instanceof LivingEntity _entity) ? _entity.getLastHurtByMob() : null;
                                if (!((entityiterator instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == entity1 || entityiterator == recentVictim || entityiterator == recentAttacker))
                                    continue;
                            }
                            if (world instanceof ServerLevel projectileLevel) {
                                Projectile _entityToSpawn = new Object() {
                                    public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
                                        AbstractArrow entityToSpawn = new Arrow(EntityType.ARROW, level);
                                        entityToSpawn.setOwner(shooter);
                                        entityToSpawn.setBaseDamage(damage);
                                        entityToSpawn.setKnockback(knockback);
                                        entityToSpawn.setCritArrow(true);
                                        entityToSpawn.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                                        return entityToSpawn;
                                    }
                                }.getArrow(projectileLevel, entity1, (float) amount, 0);
                                _entityToSpawn.setPos(x, y1, z);
                                _entityToSpawn.getPersistentData().putDouble("TrailriteLink", lll - 1);
                                _entityToSpawn.shoot((entityiterator.getX() - x), ((entityiterator.getY() + entityiterator.getBbHeight() * 0.9) - y1), (entityiterator.getZ() - z), (float) 1.75, 0);
                                projectileLevel.addFreshEntity(_entityToSpawn);
                                break;
                            }
                        }
                    }
                }
                if (lll > 4) {
                    if (world instanceof ServerLevel _level)
                        _level.sendParticles(CAParticles.MOIST_BOOM.get(), x, (y + 0.5), z, 2, 0.1, 0.1, 0.1, 0.1);
                    if (entity instanceof LivingEntity target) {
                        if (entity1 instanceof LivingEntity attacker) {
                            SIHelper.causeSanityInjury(target, attacker, amount * 5, SanityEvent.Hurt.Type.ENTITY);
                        } else {
                            SIHelper.causeSanityInjury(target, amount * 5, SanityEvent.Hurt.Type.ENTITY);
                        }
                    }
                    EntityUtils.giveLessArmor(entity, 16);
                }
            }
        }
    }

    private static void handleSanityReaper(LivingHurtEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (entity == null || sourceentity == null) return;

        ItemStack mainHandItem = (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
        if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.SANITY_REAPER.get(), mainHandItem) != 0) {
            double lvl = mainHandItem.getEnchantmentLevel(CAEnchantments.SANITY_REAPER.get());
            if (entity instanceof LivingEntity target && sourceentity instanceof LivingEntity attacker) {
                SIHelper.causeSanityInjury(target, attacker, amount * 2 * lvl, SanityEvent.Hurt.Type.ENTITY);
            }
            if (world instanceof ServerLevel _level)
                _level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 1 + entity.getBbHeight() * 0.5), z, (int) Math.min(8 * lvl, 40), 1, 1, 1.2, 0.1);
        }
    }

    private static void handleSeabornKiller(LivingHurtEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (entity == null || sourceentity == null) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))
                && EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.OCEANOSPR_KILLER.get(), (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY)) != 0) {
            double lvl = (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getEnchantmentLevel(CAEnchantments.OCEANOSPR_KILLER.get());
            double addition = Math.max(amount * lvl * 0.15, lvl * 5);
            if (world instanceof ServerLevel _level)
                _level.sendParticles(ParticleTypes.ENCHANTED_HIT, x, (y + 0.6), z, 24, 0.6, 0.6, 0.6, 0.1);
            event.setAmount((float) (amount + addition));
        }
    }

    private static void handleSeabornsGetOffShip(LivingHurtEvent event) {
        LevelAccessor world = event.getEntity().level();
        Entity entity = event.getEntity();

        if (entity == null) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanelite"))) && WorldUtils.canGrief(world)) {
            Entity eee = entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
            if (eee != null && eee.isAlive()) {
                Entity boat = entity.getVehicle();
                if (boat instanceof Boat) {
                    boat.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "general_seaborn_attack")))), 20);
                }
            }
        }
    }

    private static void handleMoreFallDamageEffect(LivingHurtEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getAmount();

        if (damagesource == null || entity == null) return;

        Entity bullet = damagesource.getDirectEntity();
        if (bullet instanceof ShulkerBullet && bullet.getPersistentData().getBoolean("oceanized")) {
            EntityUtils.giveLessArmor(entity, 8);
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CAMobEffects.MORE_FALL_DAMAGE.get(), 300, 0));
        }
        if (damagesource.is(DamageTypes.FALL) && entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(CAMobEffects.MORE_FALL_DAMAGE.get())) {
            double level = livingEntity.getEffect(CAMobEffects.MORE_FALL_DAMAGE.get()).getAmplifier() + 1;
            event.setAmount((float) (amount * (1 + 0.25 * level)));
        }
    }

    private static void handleSlimeFunc(LivingHurtEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();

        if (damagesource == null || entity == null) return;

        if (damagesource.is(NETHERSEA_DAMAGE)) {
            if (entity instanceof Slime slime) {
                if (slime.getHealth() <= slime.getMaxHealth() * 0.5 || slime.getHealth() <= 1) {
                    int size = slime.getSize();
                    if (world instanceof ServerLevel _level) {
                        NetherseaSlimeEntity entityToSpawn = CAEntities.NETHERSEA_SLIME.get().create(_level);
                        if (entityToSpawn != null) {
                            entityToSpawn.setPos(x, y, z);
                            entityToSpawn.getEntityData().set(NetherseaSlimeEntity.DATA_SIZE, size);
                            entityToSpawn.setYRot(entity.getYRot());
                            entity.discard();
                            _level.addFreshEntity(entityToSpawn);
                        }
                    }
                }
            }
        }
        if ((damagesource.getEntity()) instanceof NetherseaSlimeEntity) {
            entity.invulnerableTime = 0;
        }
    }

    private static void handleWarriorTactic(LivingHurtEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (entity == null || sourceentity == null) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring"))) && sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "warriors")))) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CAMobEffects.ROCK_BREAK.get(), 100, 0, false, false));
        }

        if (entity instanceof JuniorWarriorPriestEntity) {
            if (MathUtils.getCosine(sourceentity.getX() - entity.getX(), entity.getLookAngle().x, sourceentity.getZ() - entity.getZ(), entity.getLookAngle().z) >= 0.5) {
                if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.shield.block")), SoundSource.HOSTILE, (float) 0.75, 1);
                }
                event.setAmount((float) (amount * 0.6));
            }
        } else if (entity instanceof WarriorPriestEntity) {
            if (MathUtils.getCosine(sourceentity.getX() - entity.getX(), entity.getLookAngle().x, sourceentity.getZ() - entity.getZ(), entity.getLookAngle().z) >= 0.5) {
                if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.shield.block")), SoundSource.HOSTILE, (float) 0.75, 1);
                }
                event.setAmount((float) (amount * 0.5));
            }
        } else if (entity instanceof CorrectionalPhalanxyInfantryEntity) {
            double rate = 1;
            double less = 1;
            if (MathUtils.getCosine(sourceentity.getX() - entity.getX(), entity.getLookAngle().x, sourceentity.getZ() - entity.getZ(), entity.getLookAngle().z) >= 0.5) {
                if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.shield.block")), SoundSource.HOSTILE, (float) 0.75, 1);
                }
                rate = 0.5;
            }
            final Vec3 _center = new Vec3(x, y, z);
            List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(16 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
            for (Entity entityiterator : _entfound) {
                if (entityiterator == entity) continue;
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "phalax")))) {
                    less = less - 0.06;
                }
                if (less <= 0.4) break;
            }
            event.setAmount((float) (amount * rate * less));
        } else if (entity instanceof IreneEntity) {
            double less = 1;
            final Vec3 _center = new Vec3(x, y, z);
            List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(16 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
            for (Entity entityiterator : _entfound) {
                if (entityiterator == entity) continue;
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "phalax")))) {
                    less = less - 0.06;
                }
                if (less <= 0.4) break;
            }
            event.setAmount((float) (amount * less));
        } else if (entity instanceof CorrectinalPhalaxVanguardEntity) {
            if (MathUtils.getCosine(sourceentity.getX() - entity.getX(), entity.getLookAngle().x, sourceentity.getZ() - entity.getZ(), entity.getLookAngle().z) <= -0.5) {
                if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.shield.block")), SoundSource.HOSTILE, (float) 0.75, 1);
                }
                event.setAmount((float) (amount * 0.5));
            }
        }
    }

    private static void handlePlayerEvolutionDamageReduction(LivingHurtEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        double amount = event.getAmount();

        if (damagesource == null || entity == null) return;

        if (!(entity instanceof Player player) || !EntityUtils.canPlayerEvo(player)) return;

        boolean isIndirect = damagesource.isIndirect();
        double rate = 1;
        double e = (player.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).PEVO_NODE_less_damage;
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
            e = EntityUtils.getNodeEutectes(player);
            if (e >= 4) rate = 0.5;
            else if (e >= 3) rate = 0.3;
            else if (e >= 2) rate = 0.15;
            else if (e >= 1) rate = 0.05;

            if (rate < 1) {
                finalAmount = finalAmount * rate;
            }
        }

        if (finalAmount < amount) {
            event.setAmount((float) finalAmount);
        }
    }

    private static void handlePlayerEvolutionDamageAmplification(LivingHurtEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (damagesource == null || entity == null || sourceentity == null) return;

        if (!(sourceentity instanceof Player attacker) || !EntityUtils.canPlayerEvo(attacker)) return;

        boolean isIndirect = damagesource.isIndirect();
        double rate = 1;
        double e = EntityUtils.getNodeAddDamage(attacker);
        double finalValue = 0;

        if (e >= 1 && !isIndirect) rate = 1.2;
        if (e >= 2 && isIndirect) rate = 1.2;
        if (e >= 3 && !isIndirect) rate = 1.6;
        if (e >= 4 && isIndirect) rate = 1.6;

        if (rate > 1) {
            finalValue = amount * rate;
        }

        if (entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(CAMobEffects.UNDER_BREAK.get())) {
            e = EntityUtils.getNodeWorseBreak(attacker);
            if (e >= 4) rate = 2.4;
            else if (e >= 3) rate = 1.9;
            else if (e >= 2) rate = 1.5;
            else if (e >= 1) rate = 1.2;

            if (rate > 1) {
                finalValue = finalValue * rate;
            }
        }

        e = (attacker.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).PEVO_NODE_less_armor;

        if (e > 0) {
            double lll = 0;
            if (e >= 4) { rate = 1.4; lll = 23; }
            else if (e >= 3) { rate = 1.15; lll = 23; }
            else if (e >= 2) { rate = 1; lll = 17; }
            else if (e >= 1) { rate = 1; lll = 17; }

            for (int index0 = 0; index0 < (int) e; index0++) {
                EntityUtils.giveLessArmor(entity, lll);
            }

            if ((entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(CAMobEffects.LESS_ARMOR.get()) ? _livEnt.getEffect(CAMobEffects.LESS_ARMOR.get()).getAmplifier() : 0) >= lll) {
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
                result = perc * (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1);
            } else {
                attacker.getPersistentData().putDouble("playerEvoHitTime", 0);
            }

            finalValue = Math.min(Math.max(result, finalValue), finalValue * 32);
        }

        if (finalValue > amount) {
            event.setAmount((float) finalValue);
        }
    }
}
