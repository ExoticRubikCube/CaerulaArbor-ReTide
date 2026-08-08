package com.susen36.caerulaarbor.event;

import com.susen36.babel.api.BabelAPI;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAConfigs;
import com.susen36.caerulaarbor.init.CAGameRules;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.ArrayList;

@EventBusSubscriber
public class EntityJoinLevelEventHandler {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        handleMobInit(event);
        handleBornFunc(event);
    }

    private static void handleMobInit(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof LivingEntity livingEntity0) {
            double sanityModifier = 1;
            if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "bossoffspring")))) sanityModifier = 0.16;
            if (livingEntity0.getType().is(EntityTypeTags.UNDEAD)) sanityModifier = 0.5;
            if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "with_low_elemental_modifier")))) sanityModifier = 0.5;
            if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "with_lower_elemental_modifier")))) sanityModifier = 0.33;
            if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "with_lowest_elemental_modifier")))) sanityModifier = 0.25;
            if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "with_lowest_smaller_elemental_modifier")))) sanityModifier = 0.2;
            if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "with_lowest_smallest_elemental_modifier")))) sanityModifier = 0.1;
            if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "with_zero_elemental_modifier")))) sanityModifier = 0;
            BabelAPI.setElementalDefenseBaseModifier(livingEntity0, sanityModifier);
        }
    }

    private static void handleBornFunc(EntityJoinLevelEvent event) {
        LevelAccessor world = event.getLevel();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();

        if (!(entity instanceof LivingEntity livingEntity0 && livingEntity0.getAttributes().hasAttribute(CAAttributes.EVOLVED))) return;

        double health_index;
        double attack_index;
        double armor_index;
        double n;
        double coef;
        double coef_cur = 1;
        double percentage;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born")))) {
            if (!entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "marinemobs")))) {
                if (entity instanceof LivingEntity livingEntity4 && livingEntity4.getAttributes().hasAttribute(NeoForgeMod.SWIM_SPEED))
                    livingEntity4.getAttribute(NeoForgeMod.SWIM_SPEED)
                            .setBaseValue(((entity instanceof LivingEntity livingEntity3 && livingEntity3.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED) ? livingEntity3.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() : 0) * 10));
            }
            if ((entity instanceof LivingEntity livingEntity5 && livingEntity5.getAttributes().hasAttribute(CAAttributes.EVOLVED) ? livingEntity5.getAttribute(CAAttributes.EVOLVED).getBaseValue() : 0) == 0) {
                health_index = 1 + 0.3 * MapVariables.get(world).strategy_subsisting;
                attack_index = 1 + 0.25 * MapVariables.get(world).strategy_grow;
                armor_index = 1;
                n = Math.min((world.getLevelData().getGameRules().getInt(CAGameRules.SURGING_WAVES)), 18);
                if (n > 0) {
                    n = 1 + 0.01 * n * 2;
                    coef = 1;
                    for (Entity playerEntity : new ArrayList<>(world.players())) {
                        if (entity instanceof ServerPlayer plr) {
                            coef_cur = 1;
                            java.util.List<? extends String> entries = CAConfigs.N18_ENTRY.get();
                            for (int i = 0; i < Math.min(entries.size(), 4); i++) {
                                ResourceLocation entryAdvancement = ResourceLocation.parse(entries.get(i));
                                if (plr.getAdvancements().getOrStartProgress(plr.server.getAdvancements().get(entryAdvancement)).isDone()) {
                                    coef_cur = i + 2;
                                }
                            }
                        }
                        if (coef_cur > coef) {
                            coef = coef_cur;
                        }
                    }
                    n = Math.pow(n, coef);
                    health_index = n * health_index;
                    attack_index = n * attack_index;
                    armor_index = n * armor_index;
                }
                health_index = Math.max(CAConfigs.HEALTH_MULT.get(), 0.1) * health_index;
                attack_index = Math.max(CAConfigs.ATTACK_MULT.get(), 0.1) * attack_index;
                armor_index = Math.max(CAConfigs.ARMOR_MULT.get(), 0.1) * armor_index;
                percentage = (entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) / (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1);
                if (entity instanceof LivingEntity livingEntity18 && livingEntity18.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                    livingEntity18.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                            ((entity instanceof LivingEntity livingEntity17 && livingEntity17.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? livingEntity17.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * health_index));
                if (entity instanceof LivingEntity livingEntity)
                    livingEntity.setHealth(
                            (float) ((entity instanceof LivingEntity livingEntity19 && livingEntity19.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? livingEntity19.getAttribute(Attributes.MAX_HEALTH).getValue() : 0) * percentage));
                if (entity instanceof LivingEntity livingEntity22 && livingEntity22.getAttributes().hasAttribute(Attributes.ARMOR))
                    livingEntity22.getAttribute(Attributes.ARMOR)
                            .setBaseValue((((entity instanceof LivingEntity livingEntity21 && livingEntity21.getAttributes().hasAttribute(Attributes.ARMOR) ? livingEntity21.getAttribute(Attributes.ARMOR).getBaseValue() : 0)
                                    + 2 * MapVariables.get(world).strategy_subsisting) * armor_index));
                if (entity instanceof LivingEntity livingEntity24 && livingEntity24.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE))
                    livingEntity24.getAttribute(CAAttributes.GENERAL_DEFENSE)
                            .setBaseValue((((entity instanceof LivingEntity livingEntity23 && livingEntity23.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)
                                    ? livingEntity23.getAttribute(CAAttributes.GENERAL_DEFENSE).getBaseValue()
                                    : 0) + 1 * MapVariables.get(world).strategy_subsisting) * armor_index));
                if (entity instanceof LivingEntity livingEntity26 && livingEntity26.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS))
                    livingEntity26.getAttribute(Attributes.ARMOR_TOUGHNESS)
                            .setBaseValue((((entity instanceof LivingEntity livingEntity25 && livingEntity25.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS) ? livingEntity25.getAttribute(Attributes.ARMOR_TOUGHNESS).getBaseValue() : 0)
                                    + 2 * MapVariables.get(world).strategy_subsisting) * armor_index));
                if (entity instanceof LivingEntity livingEntity28 && livingEntity28.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                    livingEntity28.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                            ((entity instanceof LivingEntity livingEntity27 && livingEntity27.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity27.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * attack_index));

                double subl = MapVariables.get(world).strategy_sublimation;
                if (subl >= 1.0 && !entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "bossoffspring")))) {
                    if (entity instanceof LivingEntity livingEntity30 && livingEntity30.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                        livingEntity30.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                                ((entity instanceof LivingEntity livingEntity29 && livingEntity29.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? livingEntity29.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * (1.0 + 0.1 * subl)));
                }

                final double finalX = x;
                final double finalY = y;
                final double finalZ = z;
                final Entity finalEntity = entity;
                final LevelAccessor finalWorld = world;
                CaerulaArbor.queueServerWork(10, () -> {
                    if (!(finalEntity instanceof LivingEntity livEnt29 && livEnt29.hasEffect(CAMobEffects.POWER_OF_ANCHOR))) {
                        if (MapVariables.get(finalWorld).strategy_breed > 0) {
                            if (!finalEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "bossoffspring")))
                                    && !finalEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "oceanspawn")))
                                    && !finalEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "oceanpet")))
                                    && finalEntity.getPersistentData().getBoolean("caerulaNaturalSpawn")) {
                                if (EntityUtils.getFellowAround(finalWorld, finalX, finalY, finalZ, finalEntity) < 5) {
                                    if (Math.random() < 0.05 + 0.05 * MapVariables.get(finalWorld).strategy_breed) {
                                        if (!finalEntity.level().isClientSide() && finalEntity.getServer() != null) {
                                            finalEntity.getServer().getCommands().performPrefixedCommand(
                                                    new CommandSourceStack(CommandSource.NULL, finalEntity.position(), finalEntity.getRotationVector(), finalEntity.level() instanceof ServerLevel ? (ServerLevel) finalEntity.level() : null, 4, finalEntity.getName().getString(),
                                                            finalEntity.getDisplayName(), finalEntity.level().getServer(), finalEntity),
                                                    ("summon " + BuiltInRegistries.ENTITY_TYPE.getKey(finalEntity.getType()) + " ~" + Mth.nextDouble(RandomSource.create(), -1, 1) + " ~ ~" + Mth.nextDouble(RandomSource.create(), -1, 1)));
                                        }
                                    }
                                    if (EntityUtils.getFellowAround(finalWorld, finalX, finalY, finalZ, finalEntity) < 5) {
                                        if (MapVariables.get(finalWorld).strategy_breed >= 3) {
                                            if (Math.random() < 0.05 * (MapVariables.get(finalWorld).strategy_breed - 2)) {
                                                if (!finalEntity.level().isClientSide() && finalEntity.getServer() != null) {
                                                    finalEntity.getServer().getCommands().performPrefixedCommand(
                                                            new CommandSourceStack(CommandSource.NULL, finalEntity.position(), finalEntity.getRotationVector(), finalEntity.level() instanceof ServerLevel ? (ServerLevel) finalEntity.level() : null, 4,
                                                                    finalEntity.getName().getString(), finalEntity.getDisplayName(), finalEntity.level().getServer(), finalEntity),
                                                            ("summon " + BuiltInRegistries.ENTITY_TYPE.getKey(finalEntity.getType()) + " ~" + Mth.nextDouble(RandomSource.create(), -1, 1) + " ~ ~"
                                                                    + Mth.nextDouble(RandomSource.create(), -1, 1)));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (finalEntity instanceof LivingEntity livingEntity41 && livingEntity41.getAttributes().hasAttribute(CAAttributes.EVOLVED))
                            livingEntity41.getAttribute(CAAttributes.EVOLVED).setBaseValue(1);
                    }
                });
            }
        }
        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "golems")))) {
            if ((entity instanceof LivingEntity livingEntity44 && livingEntity44.getAttributes().hasAttribute(CAAttributes.EVOLVED)
                    ? livingEntity44.getAttribute(CAAttributes.EVOLVED).getBaseValue()
                    : 0) == 0) {
                percentage = (entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) / (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1);
                if (entity instanceof LivingEntity livingEntity48 && livingEntity48.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                    livingEntity48.getAttribute(Attributes.MAX_HEALTH)
                            .setBaseValue(((entity instanceof LivingEntity livingEntity47 && livingEntity47.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? livingEntity47.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0)
                                    * (1 + 0.3 * MapVariables.get(world).strategy_subsisting)));
                if (entity instanceof LivingEntity livingEntity)
                    livingEntity.setHealth(
                            (float) ((entity instanceof LivingEntity livingEntity49 && livingEntity49.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? livingEntity49.getAttribute(Attributes.MAX_HEALTH).getValue() : 0) * percentage));
                if (entity instanceof LivingEntity livingEntity52 && livingEntity52.getAttributes().hasAttribute(Attributes.ARMOR))
                    livingEntity52.getAttribute(Attributes.ARMOR)
                            .setBaseValue(((entity instanceof LivingEntity livingEntity51 && livingEntity51.getAttributes().hasAttribute(Attributes.ARMOR) ? livingEntity51.getAttribute(Attributes.ARMOR).getBaseValue() : 0)
                                    + 2 * MapVariables.get(world).strategy_subsisting));
                if (entity instanceof LivingEntity livingEntity54 && livingEntity54.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE))
                    livingEntity54.getAttribute(CAAttributes.GENERAL_DEFENSE)
                            .setBaseValue(((entity instanceof LivingEntity livingEntity53 && livingEntity53.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)
                                    ? livingEntity53.getAttribute(CAAttributes.GENERAL_DEFENSE).getBaseValue()
                                    : 0) + 2 * MapVariables.get(world).strategy_subsisting));
                if (entity instanceof LivingEntity livingEntity56 && livingEntity56.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS))
                    livingEntity56.getAttribute(Attributes.ARMOR_TOUGHNESS)
                            .setBaseValue(((entity instanceof LivingEntity livingEntity55 && livingEntity55.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS) ? livingEntity55.getAttribute(Attributes.ARMOR_TOUGHNESS).getBaseValue() : 0)
                                    + 2 * MapVariables.get(world).strategy_subsisting));
                if (entity instanceof LivingEntity livingEntity58 && livingEntity58.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                    livingEntity58.getAttribute(Attributes.ATTACK_DAMAGE)
                            .setBaseValue(((entity instanceof LivingEntity livingEntity57 && livingEntity57.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity57.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0)
                                    * (1 + 0.25 * MapVariables.get(world).strategy_grow)));
                n = Math.min((world.getLevelData().getGameRules().getInt(CAGameRules.SURGING_WAVES)), 18);
                if (n > 0) {
                    n = 1 + 0.01 * n * 2;
                    coef = 1;
                    for (Entity playerEntity : new ArrayList<>(world.players())) {
                        if (entity instanceof ServerPlayer plr) {
                            coef_cur = 1;
                            java.util.List<? extends String> entries = CAConfigs.N18_ENTRY.get();
                            for (int i = 0; i < Math.min(entries.size(), 4); i++) {
                                ResourceLocation entryAdvancement = ResourceLocation.parse(entries.get(i));
                                if (plr.getAdvancements().getOrStartProgress(plr.server.getAdvancements().get(entryAdvancement)).isDone()) {
                                    coef_cur = i + 2;
                                }
                            }
                        }
                        if (coef_cur > coef) {
                            coef = coef_cur;
                        }
                    }
                    n = Math.pow(n, coef);
                    if (n > 1.0) {
                        percentage = (entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) / (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1);
                        if (entity instanceof LivingEntity livingEntity59a && livingEntity59a.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                            livingEntity59a.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                                    ((entity instanceof LivingEntity livingEntity59b && livingEntity59b.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? livingEntity59b.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * n));
                        if (entity instanceof LivingEntity livingEntity)
                            livingEntity.setHealth(
                                    (float) ((entity instanceof LivingEntity livingEntity59c && livingEntity59c.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? livingEntity59c.getAttribute(Attributes.MAX_HEALTH).getValue() : 0) * percentage));
                        if (entity instanceof LivingEntity livingEntity59d && livingEntity59d.getAttributes().hasAttribute(Attributes.ARMOR))
                            livingEntity59d.getAttribute(Attributes.ARMOR)
                                    .setBaseValue(((entity instanceof LivingEntity livingEntity59e && livingEntity59e.getAttributes().hasAttribute(Attributes.ARMOR) ? livingEntity59e.getAttribute(Attributes.ARMOR).getBaseValue() : 0) * n));
                        if (entity instanceof LivingEntity livingEntity59f && livingEntity59f.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE))
                            livingEntity59f.getAttribute(CAAttributes.GENERAL_DEFENSE)
                                    .setBaseValue(((entity instanceof LivingEntity livingEntity59g && livingEntity59g.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)
                                            ? livingEntity59g.getAttribute(CAAttributes.GENERAL_DEFENSE).getBaseValue()
                                            : 0) * n));
                        if (entity instanceof LivingEntity livingEntity59h && livingEntity59h.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS))
                            livingEntity59h.getAttribute(Attributes.ARMOR_TOUGHNESS)
                                    .setBaseValue(((entity instanceof LivingEntity livingEntity59i && livingEntity59i.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS) ? livingEntity59i.getAttribute(Attributes.ARMOR_TOUGHNESS).getBaseValue() : 0) * n));
                        if (entity instanceof LivingEntity livingEntity59j && livingEntity59j.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                            livingEntity59j.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                                    ((entity instanceof LivingEntity livingEntity59k && livingEntity59k.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity59k.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * n));
                    }
                }
                if (entity instanceof LivingEntity livingEntity59 && livingEntity59.getAttributes().hasAttribute(CAAttributes.EVOLVED))
                    livingEntity59.getAttribute(CAAttributes.EVOLVED).setBaseValue(1);
            }
        }

        if ((entity instanceof LivingEntity livingEntity60 && livingEntity60.getAttributes().hasAttribute(CAAttributes.EVOLVED)
                ? livingEntity60.getAttribute(CAAttributes.EVOLVED).getBaseValue()
                : 0) == 0) {
            boolean isSeaborn = entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born")));
            String extendN18 = CAConfigs.EXTEND_N18.get();
            if (!entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "bypasses_surging_waves")))
                    && !isSeaborn && !extendN18.equals("off")
                    && (extendN18.equals("on")
                    || extendN18.equals("animal_only") && entity instanceof Animal
                    || extendN18.equals("exclude_monster") && !(entity instanceof Monster)
                    || extendN18.equals("monster_only") && entity instanceof Monster
                    || extendN18.equals("exclude_animal") && !(entity instanceof Animal))) {
                n = Math.min((world.getLevelData().getGameRules().getInt(CAGameRules.SURGING_WAVES)), 18);
                if (n > 0) {
                    n = 1 + 0.01 * n * 2;
                    coef = 1;
                    for (Entity playerEntity : new ArrayList<>(world.players())) {
                        if (entity instanceof ServerPlayer plr) {
                            coef_cur = 1;
                            java.util.List<? extends String> entries = CAConfigs.N18_ENTRY.get();
                            for (int i = 0; i < Math.min(entries.size(), 4); i++) {
                                ResourceLocation entryAdvancement = ResourceLocation.parse(entries.get(i));
                                if (plr.getAdvancements().getOrStartProgress(plr.server.getAdvancements().get(entryAdvancement)).isDone()) {
                                    coef_cur = i + 2;
                                }
                            }
                        }
                        if (coef_cur > coef) {
                            coef = coef_cur;
                        }
                    }
                    n = Math.pow(n, coef);
                    if (n > 1.0) {
                        percentage = (entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) / (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1);
                        if (entity instanceof LivingEntity livingEntity61 && livingEntity61.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                            livingEntity61.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                                    ((entity instanceof LivingEntity livingEntity62 && livingEntity62.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? livingEntity62.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * n));
                        if (entity instanceof LivingEntity livingEntity)
                            livingEntity.setHealth(
                                    (float) ((entity instanceof LivingEntity livingEntity63 && livingEntity63.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? livingEntity63.getAttribute(Attributes.MAX_HEALTH).getValue() : 0) * percentage));
                        if (entity instanceof LivingEntity livingEntity64 && livingEntity64.getAttributes().hasAttribute(Attributes.ARMOR))
                            livingEntity64.getAttribute(Attributes.ARMOR)
                                    .setBaseValue(((entity instanceof LivingEntity livingEntity65 && livingEntity65.getAttributes().hasAttribute(Attributes.ARMOR) ? livingEntity65.getAttribute(Attributes.ARMOR).getBaseValue() : 0) * n));
                        if (entity instanceof LivingEntity livingEntity66 && livingEntity66.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE))
                            livingEntity66.getAttribute(CAAttributes.GENERAL_DEFENSE)
                                    .setBaseValue(((entity instanceof LivingEntity livingEntity67 && livingEntity67.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)
                                            ? livingEntity67.getAttribute(CAAttributes.GENERAL_DEFENSE).getBaseValue()
                                            : 0) * n));
                        if (entity instanceof LivingEntity livingEntity68 && livingEntity68.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS))
                            livingEntity68.getAttribute(Attributes.ARMOR_TOUGHNESS)
                                    .setBaseValue(((entity instanceof LivingEntity livingEntity69 && livingEntity69.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS) ? livingEntity69.getAttribute(Attributes.ARMOR_TOUGHNESS).getBaseValue() : 0) * n));
                        if (entity instanceof LivingEntity livingEntity70 && livingEntity70.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                            livingEntity70.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                                    ((entity instanceof LivingEntity livingEntity71 && livingEntity71.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity71.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * n));
                    }
                }
            }
            if (entity instanceof LivingEntity livingEntity72 && livingEntity72.getAttributes().hasAttribute(CAAttributes.EVOLVED))
                livingEntity72.getAttribute(CAAttributes.EVOLVED).setBaseValue(1);
        }
    }
}