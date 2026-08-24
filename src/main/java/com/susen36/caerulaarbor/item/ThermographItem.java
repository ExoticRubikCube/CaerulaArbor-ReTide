
package com.susen36.caerulaarbor.item;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.init.CAAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class ThermographItem extends Item {
	public ThermographItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        if (!world.isClientSide() && world.getServer() != null)
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal(("Temperature:" + world.getBiome(BlockPos.containing(entity.getX(), entity.getY(), entity.getZ())).value().getBaseTemperature() * 100f)), false);
        return ar;
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        double def;
        double mgc;
        double snt;
        def = Math.round(Math.pow(10, 2) * ((Entity) entity instanceof LivingEntity livingEntity0 && livingEntity0.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)
                ? livingEntity0.getAttribute(CAAttributes.GENERAL_DEFENSE).getValue()
                : 0)) / Math.pow(10, 2);
        mgc = Math.round(Math.pow(10, 2) * ((Entity) entity instanceof LivingEntity livingEntity2 && livingEntity2.getAttributes().hasAttribute(BabelAttributes.MAGIC_RESISTANCE)
                ? livingEntity2.getAttribute(BabelAttributes.MAGIC_RESISTANCE).getValue()
                : 0)) / Math.pow(10, 2);
        snt = Math.round(Math.pow(10, 2) * ((Entity) entity instanceof LivingEntity livingEntity4 && livingEntity4.getAttributes().hasAttribute(CAAttributes.SANITY_RESISTANCE)
                ? livingEntity4.getAttribute(CAAttributes.SANITY_RESISTANCE).getValue()
                : 0)) / Math.pow(10, 2);
        if (sourceentity instanceof Player player && !player.level().isClientSide())
            player.displayClientMessage(Component.literal(("Defense: " + def)), false);
        if (sourceentity instanceof Player player && !player.level().isClientSide())
            player.displayClientMessage(Component.literal(("Magic Resiatance: " + mgc)), false);
        if (sourceentity instanceof Player player && !player.level().isClientSide())
            player.displayClientMessage(Component.literal(("Sanity Resistance: " + snt)), false);
        return retval;
	}
}