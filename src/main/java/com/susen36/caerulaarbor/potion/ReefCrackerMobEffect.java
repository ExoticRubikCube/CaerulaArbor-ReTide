
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.init.CAParticles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.LevelAccessor;

public class ReefCrackerMobEffect extends MobEffect {
    public ReefCrackerMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -10066177);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "reef_cracker_attack_damage"), 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (world instanceof ServerLevel level)
            level.sendParticles(CAParticles.CRACKER_BUFF_0.get(), x, (y + entity.getBbHeight() * 0.5), z, (int) ((double) amplifier + 1), 0.8, 1.5, 0.8, 0.3);
        if ((double) amplifier > 6) {
            if (world instanceof ServerLevel level)
                level.sendParticles(CAParticles.CRACKER_BUFF_1.get(), x, y, z, 3, 1, 0.5, 1, 0.3);
        }
        return true;
    }

    @Override
    public void onMobRemoved(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        super.onMobRemoved(entity, amplifier, reason);
        if ((double) amplifier >= 2) {
            if ((Entity) entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                livingEntity.addEffect(new MobEffectInstance(CAMobEffects.REEF_CRACKER, 60, (int) ((double) amplifier - 2), false, false));
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}