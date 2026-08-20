
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


public class EvolutionaryGeneItem extends Item {
	public EvolutionaryGeneItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.evolutionary_gene.description_0"));
		list.add(Component.translatable("item.caerula_arbor.evolutionary_gene.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
		if (capability.can_player_evo) {
			return super.use(world, entity, hand);
		}
		// 仅排异反应或海嗣化状态下可获取进化，否则不可用不消耗
		if (capability.disoclusion <= 0 && capability.player_oceanization < 1) {
			return InteractionResultHolder.fail(entity.getItemInHand(hand));
		}
		capability.can_player_evo = true;
		capability.syncPlayerVariables(entity);
		world.playSound(null, entity.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
		// 一次性进化提示，金色以区别于排异消息的紫色
		if (!world.isClientSide()) {
			entity.displayClientMessage(Component.translatable("item.caerula_arbor.evo_message_temporary"), false);
		}
		
		if (!entity.getAbilities().instabuild) {
			entity.getItemInHand(hand).shrink(1);
		}
		return InteractionResultHolder.success(entity.getItemInHand(hand));
	}
}