package com.susen36.caerulaarbor.init;

import com.susen36.caerulaarbor.CaerulaArborMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Objects;

@EventBusSubscriber
public class CAAttributes {
    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, CaerulaArborMod.MODID);
    public static final DeferredHolder<Attribute, Attribute> MAX_SANITY = REGISTRY.register("max_sanity", () -> new RangedAttribute("attribute.caerula_arbor.max_sanity", 1000, 1, 100000).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> SANITY_MODIFIER = REGISTRY.register("sanity_modifier", () -> new RangedAttribute("attribute.caerula_arbor.sanity_modifier", 1, 0, 999).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> SANITY_RATE = REGISTRY.register("sanity_rate", () -> new RangedAttribute("attribute.caerula_arbor.sanity_rate", 0, 0, 999).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> SANITY_INJURY_DAMAGE = REGISTRY.register("sanity_injury_damage", () -> new RangedAttribute("attribute.caerula_arbor.sanity_injury_damage", 0, 0, Integer.MAX_VALUE).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> EVOLVED = REGISTRY.register("evolved", () -> new RangedAttribute("attribute.caerula_arbor.evolved", 0, 0, 1).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> SUMMONABLE = REGISTRY.register("summonable", () -> new RangedAttribute("attribute.caerula_arbor.summonable", 1, 0, 1).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> MISSRATE = REGISTRY.register("missrate", () -> new RangedAttribute("attribute.caerula_arbor.missrate", 0, 0, 95).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> MAGIC_RESISTANCE = REGISTRY.register("magic_resistance", () -> new RangedAttribute("attribute.caerula_arbor.magic_resistance", 0, 0, 100).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> GENERAL_DEFENSE = REGISTRY.register("general_defense", () -> new RangedAttribute("attribute.caerula_arbor.general_defense", 0, 0, 131071).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> NUMB = REGISTRY.register("numb", () -> new RangedAttribute("attribute.caerula_arbor.numb", 0, 0, 9).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> SANITY_RESISTANCE = REGISTRY.register("sanity_resistance", () -> new RangedAttribute("attribute.caerula_arbor.sanity_resistance", 0, 0, 100).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> LIVING_BARRIER = REGISTRY.register("living_barrier", () -> new RangedAttribute("attribute.caerula_arbor.living_barrier", 0, 0, 214748364).setSyncable(true));

    @SubscribeEvent
    public static void addAttributes(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            event.add(entityType, MAX_SANITY);
            event.add(entityType, SANITY_MODIFIER);
            event.add(entityType, SANITY_RATE);
            event.add(entityType, SANITY_INJURY_DAMAGE);
        }
        addAttributeToEntities(event, EVOLVED, CAEntities.getLivingEntityTypes());
        addAttributeToEntities(event, SUMMONABLE, CAEntities.getSummonableEntityTypes());
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            event.add(entityType, MISSRATE);
            event.add(entityType, MAGIC_RESISTANCE);
            event.add(entityType, GENERAL_DEFENSE);
            event.add(entityType, NUMB);
            event.add(entityType, SANITY_RESISTANCE);
            event.add(entityType, LIVING_BARRIER);
        }
    }

    private static void addAttributeToEntities(EntityAttributeModificationEvent event, Holder<Attribute> attribute, List<DeferredHolder<EntityType<?>, ? extends EntityType<? extends LivingEntity>>> entityTypes) {
        for (DeferredHolder<EntityType<?>, ? extends EntityType<? extends LivingEntity>> registryObject : entityTypes) {
            event.add(registryObject.get(), attribute);
        }
    }

    @EventBusSubscriber
    public static class PlayerAttributesSync {
        @SubscribeEvent
        public static void playerClone(PlayerEvent.Clone event) {
            Player oldPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();
            Objects.requireNonNull(newPlayer.getAttribute(MAX_SANITY)).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(MAX_SANITY)).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(SANITY_MODIFIER)).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(SANITY_MODIFIER)).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(SANITY_RATE)).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(SANITY_RATE)).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(SANITY_INJURY_DAMAGE)).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(SANITY_INJURY_DAMAGE)).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(MISSRATE)).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(MISSRATE)).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(MAGIC_RESISTANCE)).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(MAGIC_RESISTANCE)).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(GENERAL_DEFENSE)).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(GENERAL_DEFENSE)).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(NUMB)).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(NUMB)).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(SANITY_RESISTANCE)).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(SANITY_RESISTANCE)).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(LIVING_BARRIER)).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(LIVING_BARRIER)).getBaseValue());
        }
    }
}