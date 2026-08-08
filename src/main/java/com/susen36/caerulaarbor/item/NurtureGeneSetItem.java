
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.LevelAccessor;

import java.util.List;


public class NurtureGeneSetItem extends Item {
	public NurtureGeneSetItem() {
		super(new Item.Properties().stacksTo(8).rarity(Rarity.EPIC));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.nurture_gene_set.description_0"));
		list.add(Component.translatable("item.caerula_arbor.nurture_gene_set.description_1"));
		list.add(Component.translatable("item.caerula_arbor.nurture_gene_set.description_2"));
		list.add(Component.translatable("item.caerula_arbor.nurture_gene_set.description_3"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemStack itemstack = ar.getObject();
        if (!ModCapabilities.getPlayerVariables(entity).can_player_evo) {
            entity.swing(InteractionHand.MAIN_HAND, true);
            itemstack.shrink(1);
            boolean setval = true;
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            capability.can_player_evo = setval;
            capability.syncPlayerVariables(entity);
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 2, 1);
            }
        }
        return ar;
	}
}