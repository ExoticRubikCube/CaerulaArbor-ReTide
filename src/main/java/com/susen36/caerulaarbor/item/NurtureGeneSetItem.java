package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CARelics;
import com.susen36.caerulaarbor.item.relic.ActivateParams;
import com.susen36.caerulaarbor.item.relic.RelicItemBase;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class NurtureGeneSetItem extends RelicItemBase {

    private static final ActivateParams PARAMS = ActivateParams.builder()
            .sound(SoundEvents.BEACON_ACTIVATE, 2.0f, 1.0f)
            .particle(ParticleTypes.HAPPY_VILLAGER, 0)
            .showOverlay(true)
            .shrink(true)
            .build();

    public NurtureGeneSetItem() {
        super(CARelics.NURTURE_GENE_SET, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable("item.caerula_arbor.nurture_gene_set.description_2"));
        list.add(Component.translatable("item.caerula_arbor.nurture_gene_set.description_3"));
        super.appendHoverText(itemstack, context, list, flag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
        ItemStack stack = entity.getItemInHand(hand);
        boolean activated = performActivate(world, entity, stack, PARAMS);
        if (activated) {
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            capability.can_player_evo = true;
            capability.syncPlayerVariables(entity);
        }
        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }
}