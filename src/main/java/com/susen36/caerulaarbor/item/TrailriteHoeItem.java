
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;

public class TrailriteHoeItem extends HoeItem {
	public TrailriteHoeItem() {
		super(new Tier() {
			public int getUses() {
				return 7999;
			}

			public float getSpeed() {
				return 19f;
			}

			public float getAttackDamageBonus() {
				return 0.5f;
			}

			public int getLevel() {
				return 4;
			}

			public int getEnchantmentValue() {
				return 22;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(CAItems.TRAILRITE.get()));
			}
		}, 0, 0.5f, new Item.Properties().fireResistant());
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
		SIHelper.causeSanityInjury(entity, sourceentity, 128);
		return retval;
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
		list.add(Component.translatable("item.caerula_arbor.trailrite_hoe.description_0"));
		list.add(Component.translatable("item.caerula_arbor.trailrite_hoe.description_1"));
		list.add(Component.translatable("item.caerula_arbor.trailrite_hoe.description_2"));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		if (context.getPlayer() == null) {
			return InteractionResult.PASS;
		}
		// TODO：评估是否为 ComplexChitinHoeItem 与 TrailriteHoeItem 制作共同基类，并将这段共享交互逻辑收口到那里。
		BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
		if (context.getPlayer().isShiftKeyDown() && clickedState.getBlock() == Blocks.FARMLAND) {
			BlockState oceanFarmlandState = CABlocks.OCEAN_FARMLAND.get().withPropertiesOf(clickedState);
			context.getLevel().setBlock(context.getClickedPos(), oceanFarmlandState, 3);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean canBeHurtBy(DamageSource pDamageSource) {
		return pDamageSource.is(DamageTypeTags.BYPASSES_EFFECTS);
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		return Math.min(amount, 1);
	}
}
