
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;

public class LongSwordOfKnightCorpusItem extends SwordItem {
	private static final Tier TIER = new SimpleTier(
			BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
			4000,
			9f,
			10f,
			16,
			() -> Ingredient.of(new ItemStack(CAItems.KNIGHT_CORPSE.get()))
	);

	public LongSwordOfKnightCorpusItem() {
		super(TIER, new Item.Properties().fireResistant().attributes(SwordItem.createAttributes(TIER, 3, -2.9f)));
	}

	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemstack) {
		ItemStack retval = new ItemStack(this);
		retval.setDamageValue(itemstack.getDamageValue() + 1);
		if (retval.getDamageValue() >= retval.getMaxDamage()) {
			return ItemStack.EMPTY;
		}
		return retval;
	}

	@Override
	public boolean isRepairable(ItemStack itemstack) {
		return false;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.long_sword_of_knight_corpus.description_0"));
		list.add(Component.translatable("item.caerula_arbor.long_sword_of_knight_corpus.description_1"));
		list.add(Component.translatable("item.caerula_arbor.long_sword_of_knight_corpus.description_2"));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected) {
            if (!(entity instanceof LivingEntity livEnt0 && livEnt0.hasEffect(CAMobEffects.ADD_REACH))) {
                if (entity instanceof LivingEntity living && !living.level().isClientSide())
                    living.addEffect(new MobEffectInstance(CAMobEffects.ADD_REACH, 20, 2, false, false));
            }
        }
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
		if (!sourceentity.level().isClientSide()) {
			float baseDamage = sourceentity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? (float) sourceentity.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
			float bonusDamage = this.applyDamageBonus(entity, baseDamage) - baseDamage;
			if (bonusDamage > 0) {
				entity.hurt(CADamageTypes.playerAttack(sourceentity.level(), sourceentity), bonusDamage);
			}
		}
		return retval;
	}

	public float applyDamageBonus(Entity target, float baseDamage) {
		float damage = baseDamage;
		if (target.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))) {
			damage *= 1.5F;
		}
		if (target instanceof LivingEntity livingTarget && livingTarget.getHealth() < livingTarget.getMaxHealth() * 0.33F) {
			damage *= 1.5F;
		}
		return damage;
	}
}