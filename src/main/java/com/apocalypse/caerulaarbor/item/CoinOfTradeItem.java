package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class CoinOfTradeItem extends Item {
	public CoinOfTradeItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 30;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.coin_of_trade.description_0"));
		list.add(Component.translatable("item.caerula_arbor.coin_of_trade.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		entity.startUsingItem(hand);
		return ar;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		ItemStack togive = ItemStack.EMPTY;
		if (entity.getOffhandItem().is(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))) && entity.getMainHandItem().getItem() == itemstack.getItem()) {
			for (int index0 = 0; index0 < 64; index0++) {
				togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
				if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
					break;
				}
			}
			if (entity instanceof Player player) {
				ItemStack _setstack = togive.copy();
				_setstack.setCount(1);
				ItemHandlerHelper.giveItemToPlayer(player, _setstack);
			}
			entity.getOffhandItem().shrink(1);
			entity.getMainHandItem().shrink(1);
		}
		return retval;
	}
}
