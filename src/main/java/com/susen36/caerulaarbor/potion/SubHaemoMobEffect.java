
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.init.CAParticles;
import com.susen36.caerulaarbor.util.MathUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;

import java.util.ArrayList;
import java.util.List;

public class SubHaemoMobEffect extends MobEffect {
	public SubHaemoMobEffect() {
		super(MobEffectCategory.HARMFUL, -7051604);
		this.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "sub_haemo_block_reach"), 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
		this.addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "sub_haemo_entity_reach"), 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
		this.addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "sub_haemo_attack_speed"), -0.35, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
	}

	// TODO: 1.21.1 removed MobEffect.getCurativeItems(), 需要迁移为 public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
	public List<ItemStack> getCurativeItems() {
		ArrayList<ItemStack> cures = new ArrayList<>();
		cures.add(new ItemStack(Items.MILK_BUCKET));
		cures.add(new ItemStack(Items.TOTEM_OF_UNDYING));
		cures.add(new ItemStack(Items.HONEY_BOTTLE));
		return cures;
	}


	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        double health_cur;
        if (Math.round((Entity) entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) < Math.round((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1)) {
            health_cur = ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) * 0.98;
            if (health_cur < 1) {
                health_cur = 1;
            }
            if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) > 1 && entity.isAlive()) {
                if ((Entity) entity instanceof LivingEntity livingEntity)
                    livingEntity.setHealth((float) health_cur);
                for (int index0 = 0; index0 < 24; index0++) {
                    world.addParticle(CAParticles.BLOODOOZE.get(), entity.getX(), (entity.getY() + 1.33), entity.getZ(), (Mth.nextDouble(RandomSource.create(), -1.25, 1.25)), (Mth.nextDouble(RandomSource.create(), -0.05, 0.05)),
                            (Mth.nextDouble(RandomSource.create(), -1.25, 1.25)));
                }
            }
        }
	    return true;
    }

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return MathUtils.isMultipleOf(duration, 40);
	}
}