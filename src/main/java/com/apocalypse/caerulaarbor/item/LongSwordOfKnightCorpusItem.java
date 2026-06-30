
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.List;

public class LongSwordOfKnightCorpusItem extends SwordItem {
	public LongSwordOfKnightCorpusItem() {
		super(new Tier() {
			public int getUses() {
				return 4000;
			}

			public float getSpeed() {
				return 9f;
			}

			public float getAttackDamageBonus() {
				return 10f;
			}

			public int getLevel() {
				return 4;
			}

			public int getEnchantmentValue() {
				return 16;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(CAItems.KNIGHT_CORPSE.get()));
			}
		}, 3, -2.9f, new Item.Properties().fireResistant());
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
		list.add(Component.translatable("item.caerula_arbor.long_sword_of_knight_corpus.description_0"));
		list.add(Component.translatable("item.caerula_arbor.long_sword_of_knight_corpus.description_1"));
		list.add(Component.translatable("item.caerula_arbor.long_sword_of_knight_corpus.description_2"));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected) {
            if (entity == null)
                return;
            if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CAMobEffects.ADD_REACH.get()))) {
                if (entity instanceof LivingEntity living && !living.level().isClientSide())
                    living.addEffect(new MobEffectInstance(CAMobEffects.ADD_REACH.get(), 20, 2, false, false));
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
				entity.hurt(new DamageSource(sourceentity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK), sourceentity), bonusDamage);
			}
		}
		return retval;
	}

	public float applyDamageBonus(Entity target, float baseDamage) {
		float damage = baseDamage;
		if (target.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
			damage *= 1.5F;
		}
		if (target instanceof LivingEntity livingTarget && livingTarget.getHealth() < livingTarget.getMaxHealth() * 0.33F) {
			damage *= 1.5F;
		}
		return damage;
	}
}
