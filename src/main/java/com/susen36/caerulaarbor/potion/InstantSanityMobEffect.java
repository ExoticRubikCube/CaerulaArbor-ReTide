package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class InstantSanityMobEffect extends MobEffect {
    public InstantSanityMobEffect() {
        super(MobEffectCategory.HARMFUL, -3342388);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    

    @Override
    public void applyInstantenousEffect(Entity source, Entity indirectSource, LivingEntity entity, int amplifier, double health) {
        if (!entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "immue_to_inst_sanity")))) {
            SIHelper.causeSanityInjury(entity, 125 * ((double) amplifier + 1), SanityEvent.Hurt.Type.POTION);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}