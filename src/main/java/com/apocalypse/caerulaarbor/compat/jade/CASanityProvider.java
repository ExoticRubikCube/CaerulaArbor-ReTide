package com.apocalypse.caerulaarbor.compat.jade;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum CASanityProvider implements IEntityComponentProvider {
    INSTANCE;

    public static final ResourceLocation UID = new ResourceLocation(CaerulaArborMod.MODID, "sanity_provider");

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        Entity entity = entityAccessor.getEntity();
        double sanity;
        if (entity instanceof LivingEntity living) {
            sanity = getSanity(living);
            if (sanity < -10) return;
            iTooltip.add(new CASanityElement(sanity, getMax(living)));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    private double getSanity(LivingEntity entity) {
        AttributeInstance instanceMod = entity.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get());
        AttributeInstance instance = entity.getAttribute(CaerulaArborModAttributes.SANITY.get());
        if (instance != null && instanceMod != null) {
            double mod = instanceMod.getValue();
            if (mod <= 0) return getMax(entity);
            return instance.getBaseValue() / mod;
        }
        return -100;
    }

    private double getMax(LivingEntity entity) {
        AttributeInstance instanceMod = entity.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get());
        if (instanceMod != null) {
            double mod = instanceMod.getValue();
            if (mod <= 0) return -2;
            return 1000 / mod;
        }
        return -1;
    }
}
