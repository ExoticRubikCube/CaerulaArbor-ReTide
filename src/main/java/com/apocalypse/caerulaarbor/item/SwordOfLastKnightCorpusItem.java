
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CADamageTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.List;

public class SwordOfLastKnightCorpusItem extends SwordItem {
	public SwordOfLastKnightCorpusItem() {
		super(new Tier() {
			public int getUses() {
				return 800;
			}

			public float getSpeed() {
				return 7f;
			}

			public float getAttackDamageBonus() {
				return 5f;
			}

			public int getLevel() {
				return 3;
			}

			public int getEnchantmentValue() {
				return 17;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(Items.IRON_INGOT));
			}
		}, 3, -2.6f, new Item.Properties().fireResistant());
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
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.iron_sword_of_knight_corpus.description_0"));
		list.add(Component.translatable("item.caerula_arbor.iron_sword_of_knight_corpus.description_1"));
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
		if (target.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
			damage *= 1.5F;
		}
		return damage;
	}
}
