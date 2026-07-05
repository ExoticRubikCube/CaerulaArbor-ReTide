
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.init.CAParticles;
import com.apocalypse.caerulaarbor.util.MathUtils;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.List;

public class SubHaemoMobEffect extends MobEffect {
	public SubHaemoMobEffect() {
		super(MobEffectCategory.HARMFUL, -7051604);
		this.addAttributeModifier(ForgeMod.BLOCK_REACH.get(), "adc95ba5-c2bc-34d0-848e-e949d7cf0951", 0.25, AttributeModifier.Operation.MULTIPLY_BASE);
		this.addAttributeModifier(ForgeMod.ENTITY_REACH.get(), "534b08c2-f1c4-3db0-ba17-bbaaa359db84", 0.25, AttributeModifier.Operation.MULTIPLY_BASE);
		this.addAttributeModifier(Attributes.ATTACK_SPEED, "0c05b9af-7673-3331-b1d8-d567880c5fe8", -0.35, AttributeModifier.Operation.MULTIPLY_BASE);
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		ArrayList<ItemStack> cures = new ArrayList<ItemStack>();
		cures.add(new ItemStack(Items.MILK_BUCKET));
		cures.add(new ItemStack(Items.TOTEM_OF_UNDYING));
		cures.add(new ItemStack(Items.HONEY_BOTTLE));
		return cures;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        double health_cur;
        if (Math.round((Entity) entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) < Math.round((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1)) {
            health_cur = ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) * 0.98;
            if (health_cur < 1) {
                health_cur = 1;
            }
            if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) > 1 && ((Entity) entity).isAlive()) {
                if ((Entity) entity instanceof LivingEntity livingEntity)
                    livingEntity.setHealth((float) health_cur);
                for (int index0 = 0; index0 < 24; index0++) {
                    world.addParticle(CAParticles.BLOODOOZE.get(), entity.getX(), (entity.getY() + 1.33), entity.getZ(), (Mth.nextDouble(RandomSource.create(), -1.25, 1.25)), (Mth.nextDouble(RandomSource.create(), -0.05, 0.05)),
                            (Mth.nextDouble(RandomSource.create(), -1.25, 1.25)));
                }
            }
        }
    }

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return MathUtils.isMultipleOf(duration, 40);
	}
}
