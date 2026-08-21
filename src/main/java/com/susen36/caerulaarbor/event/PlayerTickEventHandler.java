package com.susen36.caerulaarbor.event;

import com.susen36.babel.collectible.Collectibles;
import com.susen36.babel.init.BabelMobEffects;
import com.susen36.babel.manager.EPManager;
import com.susen36.babel.util.LifePointUtils;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.init.CACollectible;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAEnchantments;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.PlayerStateUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

@EventBusSubscriber
public class PlayerTickEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        handleEssenceResistanceWithIce(player);
        handleHandSwipeFunc(player);
        handlePlayerTickFunc(player);
    }

    private static void handleEssenceResistanceWithIce(Player entity) {
        if (entity == null) return;
        if (entity.tickCount % 2 == 0 && entity.hasEffect(BabelMobEffects.ESSENCE_RESISTANCE)) {
            if (entity.getTicksFrozen() < 140) entity.setTicksFrozen(Math.max(entity.getTicksFrozen() - 1, 0));
            entity.setRemainingFireTicks(Math.max(entity.getRemainingFireTicks() - 1, 0));
        }
    }

    private static void handleHandSwipeFunc(Player entity) {
        if (entity == null) return;
        if (entity.tickCount % 20 != 0) return;

        if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.HAND_SWIPE)) {
            if (!((entity.getOffhandItem()).getItem() == net.minecraft.world.item.Items.BRUSH)) return;

            if (entity.hasEffect(MobEffects.REGENERATION)) {
                LevelAccessor world = entity.level();
                double x = entity.getX();
                double y = entity.getY();
                double z = entity.getZ();

                final Vec3 center = new Vec3(x, y, z);
                List<net.minecraft.world.entity.monster.Monster> entfound = world.getEntitiesOfClass(net.minecraft.world.entity.monster.Monster.class, new AABB(center, center).inflate(16 / 2d), e -> true);
                for (net.minecraft.world.entity.monster.Monster entityiterator : entfound) {
                    if (entity.distanceToSqr(entityiterator) < 64) {
                        entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.WIPE_MAGIC, entity), (float) (5 * (1 + (entity.hasEffect(MobEffects.REGENERATION) ? entity.getEffect(MobEffects.REGENERATION).getAmplifier() : 0))));
                        if (world instanceof ServerLevel level)
                            level.sendParticles(ParticleTypes.WAX_OFF, (entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), 12, 0.8, 1, 0.8, 0.1);
                    }
                }
            }
        }
    }

    private static void handlePlayerTickFunc(Player entity) {
        if (entity == null) return;
        if (entity.tickCount % 10 != 0) return;

        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        handleSanityModifier(entity);
        handleKingSuit(entity);
        handleHandSpeed(entity, world, x, y, z);
        handleArchfiSuit(entity);
        handleEngraveAndSurvivor(entity);
        handleSanityDefendEnchant(entity);
        handleOceanizationEffects(entity);
        handleRelicHemost(entity);
        handleRelicYearning(entity);
        handleLegendChitin(entity);
        handleGoldenChalise(entity);
    }

    private static void handleSanityModifier(Player entity) {
        double modifi = 1;
        if (PlayerStateUtils.isLightDim(entity)) {
            modifi = 1.2;
        } else if (PlayerStateUtils.isLightCeased(entity)) {
            modifi = 1.5;
        }
        if (ModCapabilities.getPlayerVariables(entity).player_oceanization >= 3) {
            modifi = modifi * 0.33;
        }
        EPManager.setElementalDefenseBaseModifier(entity, modifi);
    }

    private static void handleKingSuit(Player entity) {
        if (LifePointUtils.getLifePoint(entity) > 1) return;

        double suitKing = 0;
        if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.KING_SPEAR)) {
            suitKing = suitKing + 1;
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.KINGS_BOOST, 20, 1, false, false));
        }
        if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.KING_ARMOR)) {
            suitKing = suitKing + 1;
        }
        if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.KING_EXTENSION)) {
            suitKing = suitKing + 1;
            double amplifi = Math.ceil(entity.getMaxHealth() / 20);
            if (amplifi > 24) amplifi = 24;
            if ((entity.hasEffect(MobEffects.REGENERATION) ? entity.getEffect(MobEffects.REGENERATION).getAmplifier() : 0) < amplifi) {
                if (!entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, (int) amplifi, false, false));
            }
        }
        if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.KING_CROWN)) {
            suitKing = suitKing + 1;
            if (!entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(CAMobEffects.KINGS_BREATH, 20, suitKing < 3 ? 0 : 2, false, false));
            }
        }
    }

    private static void handleHandSpeed(Player entity, LevelAccessor world, double x, double y, double z) {
        if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.HAND_SPEED)
                && entity.getMainHandItem().is(ItemTags.PICKAXES) && !entity.level().isClientSide()) {
            boolean valid = true;
            AABB area = AABB.ofSize(new Vec3(x, y, z), 8, 8, 8);
            for (LivingEntity nearby : world.getEntitiesOfClass(LivingEntity.class, area)) {
                if (nearby != entity && (nearby instanceof Player || nearby instanceof Animal)) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                MobEffectInstance haste = entity.getEffect(MobEffects.DIG_SPEED);
                if (haste == null || haste.getAmplifier() < 2) {
                    entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 2));
                }
                entity.addEffect(new MobEffectInstance(CAMobEffects.HANDS_SPEED, 20, 2));
            }
        }
    }

    private static void handleArchfiSuit(Player entity) {
        if (LifePointUtils.getLifePoint(entity) < LifePointUtils.getMaxLifePoint(entity))
            return;

        double suitArchfi = 0;
        if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.SARKAZ_KING_FLAG)) {
            suitArchfi = suitArchfi + 1;
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.FLAG_SWINGS, 20, 2, false, false));
        }
        if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.SARKAZ_KING_BED)) {
            suitArchfi = suitArchfi + 1;
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.KEEP_BEDDING, 20, 0, false, false));
        }
        if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.SARKAZ_KING_ARTIFACT)) {
            suitArchfi = suitArchfi + 1;
            if (!entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(CAMobEffects.SACREFICE, 20, suitArchfi < 3 ? 0 : 2, false, false));
            }
        }
    }

    private static void handleEngraveAndSurvivor(Player entity) {
        int engrave = entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).getLayer(CACollectible.HAND_OF_ENGRAVE);
        if (engrave > 0) {
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.ENGRAVED_TRIUMPH, 20,
                        engrave - 1, false, false));
        }
        int survivor = entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).getLayer(CACollectible.SURVIVOR_CONTRACT);
        if (survivor > 0) {
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.SURVIVORS_GUIDE, 20,
                        survivor - 1, false, false));
        }
    }

    private static void handleSanityDefendEnchant(Player entity) {
        double enchant = 0;
        if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_DEFEND), entity.getItemBySlot(EquipmentSlot.FEET)) != 0) {
            enchant = enchant + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_DEFEND), entity.getItemBySlot(EquipmentSlot.FEET));
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_DEFEND), entity.getItemBySlot(EquipmentSlot.LEGS)) != 0) {
            enchant = enchant + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_DEFEND), entity.getItemBySlot(EquipmentSlot.LEGS));
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_DEFEND), entity.getItemBySlot(EquipmentSlot.CHEST)) != 0) {
            enchant = enchant + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_DEFEND), entity.getItemBySlot(EquipmentSlot.CHEST));
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_DEFEND), entity.getItemBySlot(EquipmentSlot.HEAD)) != 0) {
            enchant = enchant + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_DEFEND), entity.getItemBySlot(EquipmentSlot.HEAD));
        }
        if (enchant > 16) enchant = 16;
        if (enchant > 0 && !entity.hasEffect(CAMobEffects.SANIDY_DEFENDER) && !entity.level().isClientSide()) {
            entity.addEffect(new MobEffectInstance(CAMobEffects.SANIDY_DEFENDER, 20, (int) (enchant - 1), false, false));
        }
    }

    private static void handleOceanizationEffects(Player entity) {
        if (ModCapabilities.getPlayerVariables(entity).player_oceanization >= 3) {
            if (entity.isUnderWater() && !entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 20, 0));
                entity.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20, 0));
            }
        }
    }

    private static void handleRelicHemost(Player entity) {
        if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.HEMOST)) {
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.HEMOSTATIC, 20, 0, false, false));
        }
    }

    private static void handleRelicYearning(Player entity) {
        if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.YEARNING)) {
            if (!(entity.getItemBySlot(EquipmentSlot.CHEST).getItem() == ItemStack.EMPTY.getItem())) {
                double amplifi = Math.min(Math.floor(entity.experienceLevel * 0.25), 64);
                if (amplifi >= 1 && !entity.level().isClientSide()) {
                    entity.addEffect(new MobEffectInstance(CAMobEffects.UNRIPE_THOUGHTS, 20, (int) amplifi, false, false));
                }
            }
        }
    }

    private static final ResourceLocation CHALISE_ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "golden_chalise_attack_speed");
    private static final ResourceLocation CHITIN_ATTACK_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "legend_chitin_attack");
    private static final ResourceLocation CHITIN_MAX_HEALTH_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "legend_chitin_max_health");

    private static void handleLegendChitin(Player entity) {
        if (!entity.level().isClientSide()) {
            boolean used = entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.LEGEND_CHITIN);
            AttributeInstance attackAttr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
            AttributeInstance healthAttr = entity.getAttribute(Attributes.MAX_HEALTH);
            if (used) {
                if (attackAttr != null && attackAttr.getModifier(CHITIN_ATTACK_ID) == null) {
                    attackAttr.addTransientModifier(new AttributeModifier(CHITIN_ATTACK_ID, 1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                }
                if (healthAttr != null && healthAttr.getModifier(CHITIN_MAX_HEALTH_ID) == null) {
                    healthAttr.addTransientModifier(new AttributeModifier(CHITIN_MAX_HEALTH_ID, 1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                }
            } else {
                if (attackAttr != null && attackAttr.getModifier(CHITIN_ATTACK_ID) != null) {
                    attackAttr.removeModifier(CHITIN_ATTACK_ID);
                }
                if (healthAttr != null && healthAttr.getModifier(CHITIN_MAX_HEALTH_ID) != null) {
                    healthAttr.removeModifier(CHITIN_MAX_HEALTH_ID);
                }
            }
        }
    }

    private static void handleGoldenChalise(Player entity) {
        if (!entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.GOLDEN_CHALISE)) {
            AttributeInstance attr = entity.getAttribute(Attributes.ATTACK_SPEED);
            if (attr != null && attr.getModifier(CHALISE_ATTACK_SPEED_ID) != null) {
                attr.removeModifier(CHALISE_ATTACK_SPEED_ID);
            }
        } else if (!entity.level().isClientSide()) {
            int goldCount = 0;
            for (ItemStack stack : entity.getInventory().items) {
                if (stack.getItem() == Items.GOLD_INGOT) {
                    goldCount += stack.getCount();
                }
            }
            double boost = Math.min(goldCount, 200) / 200.0;

            AttributeInstance attr = entity.getAttribute(Attributes.ATTACK_SPEED);
            if (attr != null) {
                AttributeModifier existing = attr.getModifier(CHALISE_ATTACK_SPEED_ID);
                if (existing != null) {
                    if (Math.abs(existing.amount() - boost) > 0.0001) {
                        attr.removeModifier(CHALISE_ATTACK_SPEED_ID);
                        if (boost > 0) {
                            attr.addTransientModifier(new AttributeModifier(CHALISE_ATTACK_SPEED_ID, boost, AttributeModifier.Operation.ADD_VALUE));
                        }
                    }
                } else if (boost > 0) {
                    attr.addTransientModifier(new AttributeModifier(CHALISE_ATTACK_SPEED_ID, boost, AttributeModifier.Operation.ADD_VALUE));
                }
            }
        }
    }
}