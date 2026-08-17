
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


public class EvolutionaryGenomeItem extends Item {
	public EvolutionaryGenomeItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.EPIC));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.evolutionary_genome.description_0"));
		list.add(Component.translatable("item.caerula_arbor.evolutionary_genome.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
		if (!capability.permanent_evo) {
			// 永久进化时同时开启临时进化
			capability.permanent_evo = true;
			capability.can_player_evo = true;
			capability.syncPlayerVariables(entity);
			world.playSound(null, entity.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
		} else {
			// 已拥有永久进化时，对等折算为经验等级补充
			if (!world.isClientSide()) {
				entity.giveExperienceLevels(10);
				world.playSound(null, entity.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
			}
		}
		// 创造模式不消耗物品，仅普通模式扣除（原版判据：instabuild 许可）
		if (!entity.getAbilities().instabuild) {
			entity.getItemInHand(hand).shrink(1);
		}
		return InteractionResultHolder.success(entity.getItemInHand(hand));
	}
}