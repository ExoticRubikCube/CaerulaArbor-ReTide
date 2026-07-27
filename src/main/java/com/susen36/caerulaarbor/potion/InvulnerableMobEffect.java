
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.entity.EndspeakerEntity;
import com.susen36.caerulaarbor.entity.IzumikEntity;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAParticles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.LevelAccessor;

public class InvulnerableMobEffect extends MobEffect {
	public InvulnerableMobEffect() {
		super(MobEffectCategory.NEUTRAL, -10092442);
		this.addAttributeModifier(CAAttributes.SANITY_RESISTANCE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "invulnerable_sanity_resistance"), 100, AttributeModifier.Operation.ADD_VALUE);
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        double ang;
        double phase;
        entity.invulnerableTime = 10;
        ang = Mth.nextDouble(RandomSource.create(), 0, 6.283);
        if ((double) amplifier > 4) {
             return true;
        }
        if ((double) amplifier == 0) {
            if (world instanceof ServerLevel level)
                level.sendParticles(CAParticles.INV_PTC_BLUE.get(), (x + 2 * Math.sin(ang)), (y + 1.25), (z + 2 * Math.cos(ang)), 1, 0.1, 2, 0.1, 0.2);
        } else if ((double) amplifier == 1) {
            if (entity instanceof EndspeakerEntity) {
                if (world instanceof ServerLevel level)
                    level.sendParticles(CAParticles.ENDSPEAKER_INV.get(), (x + 2 * Math.sin(ang)), (y + 1.25), (z + 2 * Math.cos(ang)), 4, 0.1, 2, 0.1, 0.2);
            } else {
                if (world instanceof ServerLevel level)
                    level.sendParticles(CAParticles.INV_PTC.get(), (x + 2 * Math.sin(ang)), (y + 1.25), (z + 2 * Math.cos(ang)), 4, 0.1, 2, 0.1, 0.2);
            }
        } else {
            if (world instanceof ServerLevel level)
                level.sendParticles(CAParticles.INV_PTC_VOILET.get(), (x + 2 * Math.sin(ang)), (y + 1.25), (z + 2 * Math.cos(ang)), 4, 0.1, 2, 0.1, 0.2);
            if (entity instanceof IzumikEntity && ((Entity) entity instanceof IzumikEntity datEntI ? datEntI.getEntityData().get(IzumikEntity.DATA_PHASE) : 0) == 0) {
                phase = (double) ((Entity) entity instanceof IzumikEntity datEntI ? datEntI.getEntityData().get(IzumikEntity.DATA_GROWTH_P) : 0) / 5;
                if ((Entity) entity instanceof LivingEntity livingEntity)
                    livingEntity.setHealth((float) (livingEntity.getMaxHealth() * (0.4 + phase * 0.15)));
            }
        }
	    return true;
    }

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}
}