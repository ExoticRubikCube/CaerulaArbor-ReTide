package com.susen36.caerulaarbor.compat.jade;

import com.susen36.caerulaarbor.CaerulaArbor;

import com.susen36.caerulaarbor.init.CAAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum CAAttributeProvider implements IEntityComponentProvider {
    INSTANCE;

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "attribute_provider");

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        Entity entity = entityAccessor.getEntity();
        if (entity instanceof LivingEntity living) {
            double def = getDefense(living);
            double resistance = getResistance(living);
            if (def > 0 || resistance > 0) {
                iTooltip.add(new CAAttributeElement(def, resistance));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    private double getDefense(LivingEntity living) {
        AttributeInstance instance = living.getAttribute(CAAttributes.GENERAL_DEFENSE);
        if (instance != null) return instance.getValue();
        return 0;
    }

    private double getResistance(LivingEntity living) {
        AttributeInstance instance = living.getAttribute(CAAttributes.MAGIC_RESISTANCE);
        if (instance != null) return instance.getValue();
        return 0;
    }
}