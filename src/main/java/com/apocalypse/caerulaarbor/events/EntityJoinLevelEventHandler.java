package com.apocalypse.caerulaarbor.events;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.configuration.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import com.apocalypse.caerulaarbor.init.CaerulaArborModGameRules;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

@Mod.EventBusSubscriber
public class EntityJoinLevelEventHandler {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() == null) return;

        handleMobInit(event);
        handleBornFunc(event);
    }

    private static void handleMobInit(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();

        if (entity == null) return;

        if ((entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get())
                ? _livingEntity0.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()).getBaseValue()
                : 0) == 1) {
            if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bossoffspring")))) {
                if (entity instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()))
                    _livingEntity2.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()).setBaseValue(0.16);
            }
            if (entity instanceof LivingEntity _livEnt3 && _livEnt3.getMobType() == MobType.UNDEAD) {
                if (entity instanceof LivingEntity _livingEntity4 && _livingEntity4.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()))
                    _livingEntity4.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()).setBaseValue(0.5);
            }
            if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "with_low_sanity_modifier")))) {
                if (entity instanceof LivingEntity _livingEntity6 && _livingEntity6.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()))
                    _livingEntity6.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()).setBaseValue(0.5);
            }
            if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "with_lower_sanity_modifier")))) {
                if (entity instanceof LivingEntity _livingEntity8 && _livingEntity8.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()))
                    _livingEntity8.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()).setBaseValue(0.33);
            }
            if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "with_lowest_sanity_modifier")))) {
                if (entity instanceof LivingEntity _livingEntity10 && _livingEntity10.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()))
                    _livingEntity10.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()).setBaseValue(0.25);
            }
            if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "with_lowest_smaller_sanity_modifier")))) {
                if (entity instanceof LivingEntity _livingEntity12 && _livingEntity12.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()))
                    _livingEntity12.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()).setBaseValue(0.2);
            }
            if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "with_lowest_smallest_sanity_modifier")))) {
                if (entity instanceof LivingEntity _livingEntity14 && _livingEntity14.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()))
                    _livingEntity14.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()).setBaseValue(0.1);
            }
            if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "with_zero_sanity_modifier")))) {
                if (entity instanceof LivingEntity _livingEntity16 && _livingEntity16.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()))
                    _livingEntity16.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()).setBaseValue(0);
            }
        }
    }

    private static void handleBornFunc(EntityJoinLevelEvent event) {
        LevelAccessor world = event.getLevel();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();

        if (entity == null) return;
        if (!(entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(CaerulaArborModAttributes.EVOLVED.get()))) return;

        double health_index;
        double attack_index;
        double armor_index;
        double n;
        double coef;
        double coef_cur = 1;
        double percentage;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
            if (!entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "marinemobs")))) {
                if (entity instanceof LivingEntity _livingEntity4 && _livingEntity4.getAttributes().hasAttribute(ForgeMod.SWIM_SPEED.get()))
                    _livingEntity4.getAttribute(ForgeMod.SWIM_SPEED.get())
                            .setBaseValue(((entity instanceof LivingEntity _livingEntity3 && _livingEntity3.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED) ? _livingEntity3.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() : 0) * 10));
            }
            if ((entity instanceof LivingEntity _livingEntity5 && _livingEntity5.getAttributes().hasAttribute(CaerulaArborModAttributes.EVOLVED.get()) ? _livingEntity5.getAttribute(CaerulaArborModAttributes.EVOLVED.get()).getBaseValue() : 0) == 0) {
                health_index = 1 + 0.3 * CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting;
                attack_index = 1 + 0.25 * CaerulaArborModVariables.MapVariables.get(world).strategy_grow;
                armor_index = 1;
                n = Math.min((world.getLevelData().getGameRules().getInt(CaerulaArborModGameRules.SURGING_WAVES)), 18);
                if (n > 0) {
                    n = 1 + 0.01 * n * 2;
                    coef = 1;
                    for (Entity entityiterator : new ArrayList<>(world.players())) {
                        if (entityiterator instanceof ServerPlayer _plr7 && _plr7.level() instanceof ServerLevel
                                && _plr7.getAdvancements().getOrStartProgress(_plr7.server.getAdvancements().getAdvancement(new ResourceLocation("minecraft:story/iron_tools"))).isDone()) {
                            coef_cur = 2;
                            if (entityiterator instanceof ServerPlayer _plr8 && _plr8.level() instanceof ServerLevel
                                    && _plr8.getAdvancements().getOrStartProgress(_plr8.server.getAdvancements().getAdvancement(new ResourceLocation("minecraft:story/enter_the_nether"))).isDone()) {
                                coef_cur = 3;
                                if (entityiterator instanceof ServerPlayer _plr9 && _plr9.level() instanceof ServerLevel
                                        && _plr9.getAdvancements().getOrStartProgress(_plr9.server.getAdvancements().getAdvancement(new ResourceLocation("minecraft:story/enter_the_end"))).isDone()) {
                                    coef_cur = 4;
                                    if (entityiterator instanceof ServerPlayer _plr10 && _plr10.level() instanceof ServerLevel
                                            && _plr10.getAdvancements().getOrStartProgress(_plr10.server.getAdvancements().getAdvancement(new ResourceLocation("minecraft:end/find_end_city"))).isDone()) {
                                        coef_cur = 5;
                                    }
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
                health_index = Math.max((double) CaerulaConfigsConfiguration.HEALTH_MULT.get(), 0.1) * health_index;
                attack_index = Math.max((double) CaerulaConfigsConfiguration.ATTACK_MULT.get(), 0.1) * attack_index;
                armor_index = Math.max((double) CaerulaConfigsConfiguration.ARMOR_MULT.get(), 0.1) * armor_index;
                percentage = (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) / (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1);
                if (entity instanceof LivingEntity _livingEntity18 && _livingEntity18.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                    _livingEntity18.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                            ((entity instanceof LivingEntity _livingEntity17 && _livingEntity17.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? _livingEntity17.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * health_index));
                if (entity instanceof LivingEntity _entity)
                    _entity.setHealth(
                            (float) ((entity instanceof LivingEntity _livingEntity19 && _livingEntity19.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? _livingEntity19.getAttribute(Attributes.MAX_HEALTH).getValue() : 0) * percentage));
                if (entity instanceof LivingEntity _livingEntity22 && _livingEntity22.getAttributes().hasAttribute(Attributes.ARMOR))
                    _livingEntity22.getAttribute(Attributes.ARMOR)
                            .setBaseValue((((entity instanceof LivingEntity _livingEntity21 && _livingEntity21.getAttributes().hasAttribute(Attributes.ARMOR) ? _livingEntity21.getAttribute(Attributes.ARMOR).getBaseValue() : 0)
                                    + 2 * CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting) * armor_index));
                if (entity instanceof LivingEntity _livingEntity24 && _livingEntity24.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()))
                    _livingEntity24.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get())
                            .setBaseValue((((entity instanceof LivingEntity _livingEntity23 && _livingEntity23.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get())
                                    ? _livingEntity23.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()).getBaseValue()
                                    : 0) + 1 * CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting) * armor_index));
                if (entity instanceof LivingEntity _livingEntity26 && _livingEntity26.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS))
                    _livingEntity26.getAttribute(Attributes.ARMOR_TOUGHNESS)
                            .setBaseValue((((entity instanceof LivingEntity _livingEntity25 && _livingEntity25.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS) ? _livingEntity25.getAttribute(Attributes.ARMOR_TOUGHNESS).getBaseValue() : 0)
                                    + 2 * CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting) * armor_index));
                if (entity instanceof LivingEntity _livingEntity28 && _livingEntity28.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                    _livingEntity28.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                            ((entity instanceof LivingEntity _livingEntity27 && _livingEntity27.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity27.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * attack_index));
                final double finalX = x;
                final double finalY = y;
                final double finalZ = z;
                final Entity finalEntity = entity;
                final LevelAccessor finalWorld = world;
                CaerulaArborMod.queueServerWork(10, () -> {
                    if (!(finalEntity instanceof LivingEntity _livEnt29 && _livEnt29.hasEffect(CaerulaArborModMobEffects.POWER_OF_ANCHOR.get()))) {
                        if (CaerulaArborModVariables.MapVariables.get(finalWorld).strategy_breed > 0) {
                            if (!finalEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bossoffspring")))
                                    && !finalEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanspawn")))
                                    && !finalEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanpet")))) {
                                if (EntityUtils.getFellowAround(finalWorld, finalX, finalY, finalZ, finalEntity) < 5) {
                                    if (Math.random() < 0.05 + 0.05 * CaerulaArborModVariables.MapVariables.get(finalWorld).strategy_breed) {
                                        Entity _ent = finalEntity;
                                        if (!_ent.level().isClientSide() && _ent.getServer() != null) {
                                            _ent.getServer().getCommands().performPrefixedCommand(
                                                    new CommandSourceStack(CommandSource.NULL, _ent.position(), _ent.getRotationVector(), _ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null, 4, _ent.getName().getString(),
                                                            _ent.getDisplayName(), _ent.level().getServer(), _ent),
                                                    ("summon " + ForgeRegistries.ENTITY_TYPES.getKey(finalEntity.getType()).toString() + " ~" + Mth.nextDouble(RandomSource.create(), -1, 1) + " ~ ~" + Mth.nextDouble(RandomSource.create(), -1, 1)));
                                        }
                                    }
                                    if (EntityUtils.getFellowAround(finalWorld, finalX, finalY, finalZ, finalEntity) < 5) {
                                        if (CaerulaArborModVariables.MapVariables.get(finalWorld).strategy_breed >= 3) {
                                            if (Math.random() < 0.05 * (CaerulaArborModVariables.MapVariables.get(finalWorld).strategy_breed - 2)) {
                                                Entity _ent = finalEntity;
                                                if (!_ent.level().isClientSide() && _ent.getServer() != null) {
                                                    _ent.getServer().getCommands().performPrefixedCommand(
                                                            new CommandSourceStack(CommandSource.NULL, _ent.position(), _ent.getRotationVector(), _ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null, 4,
                                                                    _ent.getName().getString(), _ent.getDisplayName(), _ent.level().getServer(), _ent),
                                                            ("summon " + ForgeRegistries.ENTITY_TYPES.getKey(finalEntity.getType()).toString() + " ~" + Mth.nextDouble(RandomSource.create(), -1, 1) + " ~ ~"
                                                                    + Mth.nextDouble(RandomSource.create(), -1, 1)));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (finalEntity instanceof LivingEntity _livingEntity41 && _livingEntity41.getAttributes().hasAttribute(CaerulaArborModAttributes.EVOLVED.get()))
                            _livingEntity41.getAttribute(CaerulaArborModAttributes.EVOLVED.get()).setBaseValue(1);
                    }
                });
            }
        }
        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "golems")))) {
            if ((entity instanceof LivingEntity _livingEntity44 && _livingEntity44.getAttributes().hasAttribute(CaerulaArborModAttributes.EVOLVED.get())
                    ? _livingEntity44.getAttribute(CaerulaArborModAttributes.EVOLVED.get()).getBaseValue()
                    : 0) == 0) {
                percentage = (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) / (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1);
                if (entity instanceof LivingEntity _livingEntity48 && _livingEntity48.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                    _livingEntity48.getAttribute(Attributes.MAX_HEALTH)
                            .setBaseValue(((entity instanceof LivingEntity _livingEntity47 && _livingEntity47.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? _livingEntity47.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0)
                                    * (1 + 0.3 * CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting)));
                if (entity instanceof LivingEntity _entity)
                    _entity.setHealth(
                            (float) ((entity instanceof LivingEntity _livingEntity49 && _livingEntity49.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? _livingEntity49.getAttribute(Attributes.MAX_HEALTH).getValue() : 0) * percentage));
                if (entity instanceof LivingEntity _livingEntity52 && _livingEntity52.getAttributes().hasAttribute(Attributes.ARMOR))
                    _livingEntity52.getAttribute(Attributes.ARMOR)
                            .setBaseValue(((entity instanceof LivingEntity _livingEntity51 && _livingEntity51.getAttributes().hasAttribute(Attributes.ARMOR) ? _livingEntity51.getAttribute(Attributes.ARMOR).getBaseValue() : 0)
                                    + 2 * CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting));
                if (entity instanceof LivingEntity _livingEntity54 && _livingEntity54.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()))
                    _livingEntity54.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get())
                            .setBaseValue(((entity instanceof LivingEntity _livingEntity53 && _livingEntity53.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get())
                                    ? _livingEntity53.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()).getBaseValue()
                                    : 0) + 2 * CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting));
                if (entity instanceof LivingEntity _livingEntity56 && _livingEntity56.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS))
                    _livingEntity56.getAttribute(Attributes.ARMOR_TOUGHNESS)
                            .setBaseValue(((entity instanceof LivingEntity _livingEntity55 && _livingEntity55.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS) ? _livingEntity55.getAttribute(Attributes.ARMOR_TOUGHNESS).getBaseValue() : 0)
                                    + 2 * CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting));
                if (entity instanceof LivingEntity _livingEntity58 && _livingEntity58.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                    _livingEntity58.getAttribute(Attributes.ATTACK_DAMAGE)
                            .setBaseValue(((entity instanceof LivingEntity _livingEntity57 && _livingEntity57.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity57.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0)
                                    * (1 + 0.25 * CaerulaArborModVariables.MapVariables.get(world).strategy_grow)));
                if (entity instanceof LivingEntity _livingEntity59 && _livingEntity59.getAttributes().hasAttribute(CaerulaArborModAttributes.EVOLVED.get()))
                    _livingEntity59.getAttribute(CaerulaArborModAttributes.EVOLVED.get()).setBaseValue(1);
            }
        }
    }
}
