package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CARelics;
import com.susen36.caerulaarbor.item.relic.ActivateParams;
import com.susen36.caerulaarbor.item.relic.RelicItemBase;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class GoldenChaliseItem extends RelicItemBase {

    private static final ActivateParams PARAMS = ActivateParams.builder()
            .sound(SoundEvents.PLAYER_LEVELUP, 2.0f, 1.0f)
            .particle(ParticleTypes.HAPPY_VILLAGER, 72)
            .showOverlay(true)
            .build();

    public GoldenChaliseItem() {
        super(CARelics.GOLDEN_CHALISE, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean activated = performActivate(level, player, stack, PARAMS);
        if (activated) {
            PlayerVariable capability = ModCapabilities.getPlayerVariables(player);
            CARelics.GOLDEN_CHALISE.get().set(capability, 1);
            capability.syncPlayerVariables(player);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (!player.isShiftKeyDown()) {
            ItemStack stack = context.getItemInHand();
            boolean activated = performActivate(context.getLevel(), player, stack, PARAMS);
            if (activated) {
                PlayerVariable capability = ModCapabilities.getPlayerVariables(player);
                CARelics.GOLDEN_CHALISE.get().set(capability, 1);
                capability.syncPlayerVariables(player);
            }
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
        }
        BlockPlaceContext blockContext = new BlockPlaceContext(context);
        if (!blockContext.canPlace()) {
            return InteractionResult.PASS;
        }
        BlockState blockstate = CABlocks.GOLDEN_CHALISE.get().getStateForPlacement(blockContext);
        if (blockstate == null) {
            return InteractionResult.PASS;
        }
        context.getLevel().setBlock(blockContext.getClickedPos(), blockstate, 3);
        if (!player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
    }
}