package com.apocalypse.caerulaarbor.compat.jade;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum CASanityProvider implements IEntityComponentProvider {
    INSTANCE;

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "sanity_provider");

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
        return ModCapabilities.getSanityInjury(entity).getValue();
    }

    private double getMax(LivingEntity entity) {
        return 1000;
    }
}
