
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.init.CAParticles;
import com.susen36.caerulaarbor.util.MathUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.ArrayList;
import java.util.List;

public class DizzyMobEffect extends MobEffect {
	public DizzyMobEffect() {
		super(MobEffectCategory.HARMFUL, -3355444);
		this.addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "dizzy_attack_speed"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		this.addAttributeModifier(Attributes.JUMP_STRENGTH, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "dizzy_jump_strength"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "dizzy_movement_speed"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		this.addAttributeModifier(NeoForgeMod.SWIM_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "dizzy_swim_speed"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		// TODO: NeoForge 1.21.1 removed NeoForgeMod.BLOCK_REACH, reimplement when replacement is known
		// this.addAttributeModifier(NeoForgeMod.BLOCK_REACH, "51754579-64ad-31b8-856b-d95093079463", -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		// TODO: NeoForge 1.21.1 removed NeoForgeMod.ENTITY_REACH, reimplement when replacement is known
		// this.addAttributeModifier(NeoForgeMod.ENTITY_REACH, "37b0609d-2667-3863-9226-91695957e9ce", -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		// TODO: NeoForge 1.21.1 removed NeoForgeMod.STEP_HEIGHT_ADDITION, reimplement when replacement is known
		// this.addAttributeModifier(NeoForgeMod.STEP_HEIGHT_ADDITION, "db41f8c8-0f93-3c63-9c58-1e8e293749f3", -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
	}

	// TODO: 1.21.1 removed MobEffect.getCurativeItems(), curative logic needs migration to ConsumeEffect
	public List<ItemStack> getCurativeItems() {
		ArrayList<ItemStack> cures = new ArrayList<>();
		cures.add(new ItemStack(Items.TOTEM_OF_UNDYING));
		cures.add(new ItemStack(Items.HONEY_BOTTLE));
		return cures;
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (world instanceof ServerLevel level)
            level.sendParticles(CAParticles.DIZZINESS.get(), x, y, z, 2, 1, 1, 1, 0.1);
        world.addParticle(CAParticles.DIZZINESS.get(), x, y, z, (0.5 - Math.random()), 0.1, (0.5 - Math.random()));
	    return true;
    }

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return MathUtils.isMultipleOf(duration, 10);
	}
}