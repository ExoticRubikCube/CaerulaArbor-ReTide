
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;
import java.util.function.Consumer;


public class TrailriteHoeItem extends HoeItem {
	private static final Tier TIER = new SimpleTier(
			BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
			7999,
			19f,
			0.5f,
			22,
			() -> Ingredient.of(new ItemStack(CAItems.TRAILRITE.get()))
	);

	public TrailriteHoeItem() {
		super(TIER, new Item.Properties().fireResistant().attributes(HoeItem.createAttributes(TIER, 0, 0.5f)));
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
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
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
		// TODO锛氳瘎浼版槸鍚︿负 ComplexChitinHoeItem 涓?TrailriteHoeItem 鍒朵綔鍏卞悓鍩虹被锛屽苟灏嗚繖娈靛叡浜氦浜掗€昏緫鏀跺彛鍒伴偅閲屻€?
		BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
		if (context.getPlayer().isShiftKeyDown() && clickedState.getBlock() == Blocks.FARMLAND) {
			BlockState oceanFarmlandState = CABlocks.OCEAN_FARMLAND.get().withPropertiesOf(clickedState);
			context.getLevel().setBlock(context.getClickedPos(), oceanFarmlandState, 3);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean canBeHurtBy(ItemStack stack, DamageSource pDamageSource) {
		return pDamageSource.is(DamageTypeTags.BYPASSES_EFFECTS);
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
		return Math.min(amount, 1);
	}
}