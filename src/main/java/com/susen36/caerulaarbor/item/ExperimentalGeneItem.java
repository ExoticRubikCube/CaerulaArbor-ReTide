
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;


public class ExperimentalGeneItem extends Item {
	public ExperimentalGeneItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.experimental_gene.description_0"));
		list.add(Component.translatable("item.caerula_arbor.experimental_gene.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
		if (capability.player_oceanization < 2) {
			capability.player_oceanization = 2;
			capability.syncPlayerVariables(entity);
			world.playSound(null, entity.blockPosition(), SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.PLAYERS, 1.0F, 1.0F);
			// 创造模式不消耗物品，仅普通模式扣除（原版判据：instabuild 许可）
			if (!entity.getAbilities().instabuild) {
				entity.getItemInHand(hand).shrink(1);
			}
			return InteractionResultHolder.success(entity.getItemInHand(hand));
		}
		// 海嗣化等级已达标：返回失败、不消耗物品，也不弹出失败提示
		return InteractionResultHolder.fail(entity.getItemInHand(hand));
	}
}