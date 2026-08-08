package com.susen36.caerulaarbor.compat.jade;

import com.susen36.babel.init.BabelMobEffects;
import com.susen36.caerulaarbor.CaerulaArbor;

import com.susen36.caerulaarbor.init.CAAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum CAMissNumbProvider implements IEntityComponentProvider {
    INSTANCE;

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "miss_numb_provider");

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        Entity entity = entityAccessor.getEntity();
        if (entity instanceof LivingEntity living) {
            int miss = (int) getMiss(living);
            int numb = (int) getNumb(living);
            if (miss > 0 || numb > 0) {
                iTooltip.add(new CAMissNumbElement(miss, numb));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    private double getMiss(LivingEntity living) {
        AttributeInstance instance = living.getAttribute(CAAttributes.MISSRATE);
        if (instance != null) return instance.getValue();
        return 0;
    }

    private double getNumb(LivingEntity living) {
        MobEffectInstance instance = living.getEffect(BabelMobEffects.NUMB);
        if (instance != null) return instance.getAmplifier() + 1;
        return 0;
    }
}