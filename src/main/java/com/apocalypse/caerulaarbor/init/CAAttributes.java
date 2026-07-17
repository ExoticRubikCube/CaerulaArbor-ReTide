package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CAAttributes {
    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, CaerulaArborMod.MODID);
    public static final RegistryObject<Attribute> MAX_SANITY = REGISTRY.register("max_sanity", () -> new RangedAttribute("attribute.caerula_arbor.max_sanity", 1000, 1, 100000).setSyncable(true));
    public static final RegistryObject<Attribute> SANITY_MODIFIER = REGISTRY.register("sanity_modifier", () -> new RangedAttribute("attribute.caerula_arbor.sanity_modifier", 1, 0, 999).setSyncable(true));
    public static final RegistryObject<Attribute> SANITY_RATE = REGISTRY.register("sanity_rate", () -> new RangedAttribute("attribute.caerula_arbor.sanity_rate", 0, 0, 999).setSyncable(true));
    public static final RegistryObject<Attribute> SANITY_INJURY_DAMAGE = REGISTRY.register("sanity_injury_damage", () -> new RangedAttribute("attribute.caerula_arbor.sanity_injury_damage", 0, 0, Integer.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> EVOLVED = REGISTRY.register("evolved", () -> new RangedAttribute("attribute.caerula_arbor.evolved", 0, 0, 1).setSyncable(true));
    public static final RegistryObject<Attribute> SUMMONABLE = REGISTRY.register("summonable", () -> new RangedAttribute("attribute.caerula_arbor.summonable", 1, 0, 1).setSyncable(true));
    public static final RegistryObject<Attribute> MISSRATE = REGISTRY.register("missrate", () -> new RangedAttribute("attribute.caerula_arbor.missrate", 0, 0, 100).setSyncable(true));
    public static final RegistryObject<Attribute> MAGIC_RESISTANCE = REGISTRY.register("magic_resistance", () -> new RangedAttribute("attribute.caerula_arbor.magic_resistance", 0, 0, 100).setSyncable(true));
    public static final RegistryObject<Attribute> GENERAL_DEFENSE = REGISTRY.register("general_defense", () -> new RangedAttribute("attribute.caerula_arbor.general_defense", 0, 0, 131071).setSyncable(true));
    public static final RegistryObject<Attribute> NUMB = REGISTRY.register("numb", () -> new RangedAttribute("attribute.caerula_arbor.numb", 0, 0, 9).setSyncable(true));
    public static final RegistryObject<Attribute> SANITY_RESISTANCE = REGISTRY.register("sanity_resistance", () -> new RangedAttribute("attribute.caerula_arbor.sanity_resistance", 0, 0, 100).setSyncable(true));
    public static final RegistryObject<Attribute> LIVING_BARRIER = REGISTRY.register("living_barrier", () -> new RangedAttribute("attribute.caerula_arbor.living_barrier", 0, 0, 214748364).setSyncable(true));

    @SubscribeEvent
    public static void addAttributes(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            event.add(entityType, MAX_SANITY.get());
            event.add(entityType, SANITY_MODIFIER.get());
            event.add(entityType, SANITY_RATE.get());
            event.add(entityType, SANITY_INJURY_DAMAGE.get());
        }
        addAttributeToEntities(event, EVOLVED.get(), CAEntities.getLivingEntityTypes());
        addAttributeToEntities(event, SUMMONABLE.get(), CAEntities.getSummonableEntityTypes());
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            event.add(entityType, MISSRATE.get());
            event.add(entityType, MAGIC_RESISTANCE.get());
            event.add(entityType, GENERAL_DEFENSE.get());
            event.add(entityType, NUMB.get());
            event.add(entityType, SANITY_RESISTANCE.get());
            event.add(entityType, LIVING_BARRIER.get());
        }
    }

    private static void addAttributeToEntities(
            EntityAttributeModificationEvent event, Attribute attribute,
            List<RegistryObject<? extends EntityType<? extends LivingEntity>>> entityTypes
    ) {
        for (RegistryObject<? extends EntityType<? extends LivingEntity>> registryObject : entityTypes) {
            event.add(registryObject.get(), attribute);
        }
    }

    @Mod.EventBusSubscriber
    public static class PlayerAttributesSync {
        @SubscribeEvent
        public static void playerClone(PlayerEvent.Clone event) {
            Player oldPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();
            Objects.requireNonNull(newPlayer.getAttribute(MAX_SANITY.get())).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(MAX_SANITY.get())).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(SANITY_MODIFIER.get())).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(SANITY_MODIFIER.get())).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(SANITY_RATE.get())).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(SANITY_RATE.get())).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(SANITY_INJURY_DAMAGE.get())).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(SANITY_INJURY_DAMAGE.get())).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(MISSRATE.get())).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(MISSRATE.get())).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(MAGIC_RESISTANCE.get())).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(MAGIC_RESISTANCE.get())).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(GENERAL_DEFENSE.get())).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(GENERAL_DEFENSE.get())).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(NUMB.get())).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(NUMB.get())).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(SANITY_RESISTANCE.get())).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(SANITY_RESISTANCE.get())).getBaseValue());
            Objects.requireNonNull(newPlayer.getAttribute(LIVING_BARRIER.get())).setBaseValue(Objects.requireNonNull(oldPlayer.getAttribute(LIVING_BARRIER.get())).getBaseValue());
        }
    }
}
