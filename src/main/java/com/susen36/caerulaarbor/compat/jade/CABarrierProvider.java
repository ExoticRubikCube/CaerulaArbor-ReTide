package com.susen36.caerulaarbor.compat.jade;

import com.susen36.caerulaarbor.CaerulaArborMod;

import com.susen36.caerulaarbor.init.CAAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum CABarrierProvider implements IEntityComponentProvider {
    INSTANCE;

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "barrier_provider");

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        Entity entity = entityAccessor.getEntity();
        if (entity instanceof LivingEntity living) {
            double barrier = getBarrier(living);
            if (barrier > 0) {
                iTooltip.add(new CABarrierElement(barrier));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    private double getBarrier(LivingEntity living) {
        AttributeInstance instance = living.getAttribute(CAAttributes.LIVING_BARRIER.get());
        if (instance != null) return instance.getValue();
        return 0;
    }
}
