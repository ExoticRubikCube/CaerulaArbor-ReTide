
package com.apocalypse.caerulaarbor.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;

public class RulerItem extends Item {
	public RulerItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
        LevelAccessor world = context.getLevel();
        double x = context.getClickedPos().getX();
        double y = context.getClickedPos().getY();
        double z = context.getClickedPos().getZ();
        Direction direction = context.getClickedFace();
        Entity entity = context.getPlayer();
        if (entity == null)
            return InteractionResult.PASS;
        if (entity instanceof Player player && !player.level().isClientSide())
            player.displayClientMessage(Component.literal(("light, block:" + world.getBrightness(LightLayer.BLOCK, BlockPos.containing(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ())) + "sky: "
                    + world.getBrightness(LightLayer.SKY, BlockPos.containing(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ())))), false);
        return InteractionResult.SUCCESS;
    }

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        LevelAccessor world = entity.level();
        if (!world.isClientSide() && world.getServer() != null)
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal(("scale of " + entity.getDisplayName().getString() + "(W x H):" + entity.getBbWidth() + "x" + entity.getBbHeight())), false);
        if (!world.isClientSide() && world.getServer() != null)
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal(("look of" + entity.getDisplayName().getString() + "x " + Math.round(Math.pow(10, 3) * (entity.getLookAngle().x)) / Math.pow(10, 3) + " y "
                    + Math.round(Math.pow(10, 3) * (entity.getLookAngle().y)) / Math.pow(10, 3) + " z " + Math.round(Math.pow(10, 3) * (entity.getLookAngle().z)) / Math.pow(10, 3))), false);
        return retval;
	}
}
