package com.susen36.caerulaarbor.event;

import com.susen36.babel.init.BabelMobEffects;
import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler.StrategyType;
import com.susen36.caerulaarbor.entity.*;
import com.susen36.caerulaarbor.entity.ai.SeabornAggressiveTargetGoal;
import com.susen36.caerulaarbor.entity.ai.SeabornCounterTargetGoal;
import com.susen36.caerulaarbor.entity.enderdragon.MoistEnderCrystalEntity;
import com.susen36.caerulaarbor.entity.enderdragon.OceanizedEnderinaEntity;
import com.susen36.caerulaarbor.entity.tidelinked.TidelinkedBishopEntity;
import com.susen36.caerulaarbor.entity.tidelinked.TidelinkedImmortalEntity;
import com.susen36.caerulaarbor.init.*;
import com.susen36.caerulaarbor.manager.upgrade.MigrationUpgradeManager;
import com.susen36.caerulaarbor.manager.upgrade.SilenceUpgradeManager;
import com.susen36.caerulaarbor.manager.upgrade.SubsistingUpgradeManager;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Comparator;

import static com.susen36.caerulaarbor.util.EntityUtils.SEABORN_BOSS;

@EventBusSubscriber
public class LivingTickEventHandler {
    static final ResourceLocation NETHERSEA_WALKER_SPEED_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "nethersea_walker_movement_speed");
    static final ResourceLocation NETHERSEA_WALKER_EFFICIENCY_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "nethersea_walker_movement_efficiency");

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        handleModeGoals(event);
        handleChangeAttackGoal(event);
        handleMobTick(event);
        handleNetherseaWalker(event);
        handleArmorEnchantFunc(event);
    }

    private static void handleModeGoals(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }
        if (!(entity instanceof Mob mob)) {
            return;
        }
        if (entity.tickCount % 20 != 0) {
            return;
        }
        boolean isSeaborn = entity.getType().is(EntityUtils.SEABORN);
        boolean isSeabornPet = entity.getType().is(EntityUtils.SEABORN_PET);
        boolean isSeaFriend = entity.getType().is(EntityUtils.SEA_FRIEND);
        if (isSeaborn && !isSeabornPet && entity.level().getGameRules().getBoolean(CAGameRules.AGGRESIVE_MODE)) {
            boolean alreadyAdded = false;
            for (WrappedGoal goal : mob.targetSelector.getAvailableGoals()) {
                if (goal.getGoal() instanceof SeabornAggressiveTargetGoal) {
                    alreadyAdded = true;
                    break;
                }
            }
            if (!alreadyAdded) {
                mob.targetSelector.addGoal(8, new SeabornAggressiveTargetGoal(mob));
            }
        }
        if (!isSeaborn && !isSeaFriend && entity.level().getGameRules().getBoolean(CAGameRules.DEFENSIVE_MODE)) {
            boolean alreadyAdded = false;
            for (WrappedGoal goal : mob.targetSelector.getAvailableGoals()) {
                if (goal.getGoal() instanceof SeabornCounterTargetGoal) {
                    alreadyAdded = true;
                    break;
                }
            }
            if (!alreadyAdded) {
                mob.targetSelector.addGoal(9, new SeabornCounterTargetGoal(mob));
            }
        }
    }

    //TODO 可能需要下放
    private static void handleChangeAttackGoal(EntityTickEvent.Post event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();

        if (entity.tickCount % 30 != 1) return;

        Entity enemy = entity instanceof Mob mobEnt ? mobEnt.getTarget() : null;
        if (enemy == null) return;

        Entity other = null;

        switch (enemy) {
            case TidelinkedImmortalEntity livEnt5 when livEnt5.hasEffect(CAMobEffects.FAKE_DEATH) ->
                    other = world.getEntitiesOfClass(TidelinkedBishopEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream().min(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z))).orElse(null);
            case TidelinkedBishopEntity livEnt8 when livEnt8.hasEffect(CAMobEffects.FAKE_DEATH) ->
                    other = world.getEntitiesOfClass(TidelinkedImmortalEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream().min(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z))).orElse(null);
            case MartusEntity livEnt11 when livEnt11.hasEffect(CAMobEffects.INVULNERABLE) -> {
                Entity tgt_ent = null;
                Entity tgt_blessed = null;
                double max_h = -1.0D;
                double blesses_h = -1.0D;
                double d1;
                for (Mob entityiterator : world.getEntitiesOfClass(Mob.class, new AABB((x + 32), (y + 32), (z + 32), (x - 32), (y - 32), (z - 32)))) {
                    if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn"))))
                        continue;
                    if (entityiterator instanceof MartusEntity) continue;
                    d1 = entity.distanceToSqr(entityiterator);
                    if (entityiterator.getPersistentData().getBoolean("blessed") && (blesses_h == -1.0D || d1 < blesses_h)) {
                        blesses_h = d1;
                        tgt_blessed = entityiterator;
                    } else if (max_h == -1.0D || d1 < max_h) {
                        max_h = d1;
                        tgt_ent = entityiterator;
                    }
                }
                other = tgt_blessed != null ? tgt_blessed : tgt_ent;
            }
            default -> {
                switch (enemy) {
                    case EndspeakerEntity endspeaker when endspeaker.getPhase() < 3 && endspeaker.hasEffect(CAMobEffects.INVULNERABLE) ->
                            other = world.getEntitiesOfClass(LivingEntity.class, new AABB((x + 32), (y + 32), (z + 32), (x - 32), (y - 32), (z - 32)),
                                    e -> e.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))
                            ).stream().min(Comparator.comparingDouble(e -> e.distanceToSqr(enemy))).orElse(null);
                    case OceanizedIllusionerEntity oceanizedIllusionerEntity ->
                            other = world.getEntitiesOfClass(OceanIllusionEntity.class, AABB.ofSize(new Vec3(x, y, z), 48, 48, 48), e -> true).stream().min(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z))).orElse(null);
                    case OceanizedEnderinaEntity enderina when !enderina.isEnderinaDurative() ->
                            other = world.getEntitiesOfClass(MoistEnderCrystalEntity.class, AABB.ofSize(new Vec3(x, y, z), 48, 48, 48), e -> true).stream().min(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z))).orElse(null);
                    default -> {
                    }
                }
            }
        }

        if (other instanceof LivingEntity _ent && entity instanceof Mob _entity) {
            _entity.setTarget(_ent);
        }
    }

    //TODO有性能问题
    private static void handleMobTick(EntityTickEvent.Post event) {
        Level world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn"))))
        {
            handleMobBuffs(world, entity);
            handleNaturalEvolution(world, x, y, z, entity);
        }
    }

    private static void handleMobBuffs(Level world, Entity entity) {
        double subsistingLevel = MapVariables.get(world).strategy_subsisting;
        int resistLvl = SubsistingUpgradeManager.getSubsistResistLevel(subsistingLevel);
        if (resistLvl >= 0) {
            if (!(entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(MobEffects.DAMAGE_RESISTANCE))) {
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, resistLvl));
            }
        }

        if (entity instanceof LivingEntity livEnt3 && livEnt3.hasEffect(CAMobEffects.POWER_OF_ANCHOR)) return;

        handleSublimationBuffs(world, entity);
    }

    //TODO 可以下放到海嗣基类实现
    private static void handleSublimationBuffs(Level world, Entity entity) {
        if (!world.isClientSide()&&entity.getType().is(SEABORN_BOSS)) {
            double subl = MapVariables.get(world).strategy_sublimation;
            double subs = MapVariables.get(world).strategy_subsisting;
            double migra = MapVariables.get(world).strategy_migration;
            double finalSubs = Math.min(subs, subl);
            double finalMigra = Math.min(migra, subl);

            if (entity instanceof LivingEntity living) {
                double maxHealth = living.getMaxHealth();
                double notHurtTick = entity.tickCount - entity.getPersistentData().getDouble("caerula.lastHurtByTime");

                if (notHurtTick >= 220.0 - 20.0 * finalSubs && living.tickCount % 20 == 0) {
                    if (finalSubs > 0.0 && !(entity instanceof AbsorberLimbEntity)) {
                        living.heal((float) (maxHealth * finalSubs * 0.01 * 0.05));
                    }
                }

                if (!entity.getPersistentData().getBoolean("sublimationBlessed") && finalMigra >= 3.0) {
                    float currentHealth = living.getHealth();
                    if (currentHealth < maxHealth * 0.3) {
                        boolean isElite = entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "oceanelite")));
                        boolean isTiny = entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "tiny_seaborn")));
                        entity.getPersistentData().putBoolean("sublimationBlessed", true);

                        if (!isTiny) {
                            if (finalMigra == 3.0) {
                                if (isElite) {
                                    living.addEffect(new MobEffectInstance(CAMobEffects.IMMORTAL, 100, 0, false, false));
                                } else {
                                    living.addEffect(new MobEffectInstance(CAMobEffects.IMMORTAL, 60, 0, false, false));
                                }
                            } else if (finalMigra == 4.0) {
                                if (isElite) {
                                    living.addEffect(new MobEffectInstance(CAMobEffects.IMMORTAL, 200, 0, false, false));
                                } else {
                                    living.addEffect(new MobEffectInstance(CAMobEffects.IMMORTAL, 100, 0, false, false));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void handleNaturalEvolution(Level world, double x, double y, double z, Entity entity) {
        if (!world.getLevelData().getGameRules().getBoolean(CAGameRules.NATURAL_EVOLUTION)) return;
        if (entity.tickCount % 10 != 0) return;
        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn_pet")))) return;

        if (Math.random() < 0.16 && !world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 96, 96, 96), e -> true).isEmpty()) {
            double pnt = Mth.nextDouble(RandomSource.create(), 0, 0.005);
            MapVariablesHandler.addEvoPoint(world, StrategyType.MIGRATION,
                    pnt * Math.max(MapVariables.get(world).strategy_subsisting + MapVariables.get(world).strategy_grow + MapVariables.get(world).strategy_breed, 1));
            MigrationUpgradeManager.applyMigrationUpgrade(world);
            SilenceUpgradeManager.applySilenceUpgrade(world, pnt);
        }
    }

    private static void handleNetherseaWalker(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living && entity.tickCount % 5 == 0) {
            Level world = living.level();
            double x = living.getX();
            double y = living.getY();
            double z = living.getZ();
            ItemStack boots = living.getItemBySlot(EquipmentSlot.FEET);
            int lvl = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.NETHERSEA_WALKER), boots);
            boolean onValidBlock = world.getBlockState(BlockPos.containing(x, y - 0.5, z)).is(BlockTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "nethersea_walker_functions")));
            double dx = x - entity.xo;
            double dz = z - entity.zo;
            double distSqr = Mth.square(dx) + Mth.square(dz);
            boolean moving = distSqr > 1.0E-6D;
            boolean active = lvl > 0 && onValidBlock && entity.onGround() && !living.isFallFlying() && moving;
            if (!world.isClientSide()) {
                AttributeInstance speedAttr = living.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speedAttr != null) {
                    if (active) {
                        double speedValue = 0.3D + (double) lvl * 0.115D;
                        AttributeModifier existing = speedAttr.getModifier(NETHERSEA_WALKER_SPEED_ID);
                        if (existing == null) {
                            speedAttr.addTransientModifier(new AttributeModifier(NETHERSEA_WALKER_SPEED_ID, speedValue, AttributeModifier.Operation.ADD_VALUE));
                        } else if (Math.abs(existing.amount() - speedValue) > 1.0E-7D) {
                            speedAttr.removeModifier(NETHERSEA_WALKER_SPEED_ID);
                            speedAttr.addTransientModifier(new AttributeModifier(NETHERSEA_WALKER_SPEED_ID, speedValue, AttributeModifier.Operation.ADD_VALUE));
                        }
                    } else if (speedAttr.getModifier(NETHERSEA_WALKER_SPEED_ID) != null) {
                        speedAttr.removeModifier(NETHERSEA_WALKER_SPEED_ID);
                    }
                }
                AttributeInstance effAttr = living.getAttribute(Attributes.MOVEMENT_EFFICIENCY);
                if (effAttr != null) {
                    if (active) {
                        if (effAttr.getModifier(NETHERSEA_WALKER_EFFICIENCY_ID) == null) {
                            effAttr.addTransientModifier(new AttributeModifier(NETHERSEA_WALKER_EFFICIENCY_ID, 1.0D, AttributeModifier.Operation.ADD_VALUE));
                        }
                    } else if (effAttr.getModifier(NETHERSEA_WALKER_EFFICIENCY_ID) != null) {
                        effAttr.removeModifier(NETHERSEA_WALKER_EFFICIENCY_ID);
                    }
                }
            } else if (active) {
                RandomSource rand = entity.getRandom();
                world.addParticle(
                        CAParticles.SEA_SPLASH.get(),
                        x + (rand.nextDouble() - 0.5D) * (double) entity.getBbWidth(),
                        y + 0.1D,
                        z + (rand.nextDouble() - 0.5D) * (double) entity.getBbWidth(),
                        (rand.nextDouble() - 0.5D) * 0.05D,
                        0.02D,
                        (rand.nextDouble() - 0.5D) * 0.05D
                );
                if (rand.nextFloat() < 0.35F) {
                    world.playSound(
                            living instanceof Player ? (Player) living : null,
                            BlockPos.containing(x, y, z),
                            CASounds.SHALLOW_SEA.get(),
                            SoundSource.PLAYERS,
                            0.6F,
                            0.6F + rand.nextFloat() * 0.4F
                    );
                }
            }
        }
    }

    //TODO 之后需要移除buff依赖
    private static void handleArmorEnchantFunc(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living) {
            ItemStack helm = living.getItemBySlot(EquipmentSlot.HEAD).copy();
            ItemStack chest = living.getItemBySlot(EquipmentSlot.CHEST).copy();
            ItemStack legg = living.getItemBySlot(EquipmentSlot.LEGS).copy();
            ItemStack boot = living.getItemBySlot(EquipmentSlot.FEET).copy();
            if (living.tickCount % 5 == 0) {
                double lvl = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.FLEXIBILITY), helm) + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.FLEXIBILITY), chest)
                        + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.FLEXIBILITY), legg) + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.FLEXIBILITY), boot);
                if (lvl > 0) {
                    if (!living.level().isClientSide()) {
                        living.addEffect(new MobEffectInstance(CAMobEffects.FLEXIBILITY_BUFF, 10, (int) Math.min(lvl - 1, 16), false, false));
                    }
                }
                lvl = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.MAGIC_TOLERANCE), helm) + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.MAGIC_TOLERANCE), chest)
                        + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.MAGIC_TOLERANCE), legg) + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.MAGIC_TOLERANCE), boot);
                if (lvl > 0) {
                    if (!living.level().isClientSide()) {
                        living.addEffect(new MobEffectInstance(CAMobEffects.MAGIC_RESIS_BUFF, 10, (int) Math.min(lvl - 1, 16), false, false));
                    }
                }
                lvl = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.SANITY_INJURY_CURSE), helm) + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.SANITY_INJURY_CURSE), chest)
                        + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.SANITY_INJURY_CURSE), legg) + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.SANITY_INJURY_CURSE), boot);
                if (lvl > 0) {
                    EPUtils.causeSanityInjury(living, lvl);
                }
            }
            double lvl0 = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.HAZARD_PROTECTION), helm);
            double lvl1 = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.HAZARD_PROTECTION), chest);
            double lvl2 = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.HAZARD_PROTECTION), legg);
            double lvl3 = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.HAZARD_PROTECTION), boot);
            double lvl = lvl0 + lvl1 + lvl2 + lvl3;
            if (lvl > 0) {
                double gap = Math.max(600 - 25 * lvl, 300);
                double maxAmplif = Math.min(Math.max(Math.max(lvl0, lvl1), Math.max(lvl2, lvl3)), 2);
                if (living.tickCount % (int) gap == 64) {
                    if (!living.level().isClientSide()) {
                        living.addEffect(new MobEffectInstance(BabelMobEffects.ESSENCE_RESISTANCE, 260, (int) (maxAmplif - 1), false, false));
                    }
                }
            }
        }
    }
}