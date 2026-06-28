
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
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
import net.minecraft.world.level.LevelAccessor;

public class ThermographItem extends Item {
	public ThermographItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        if (!((LevelAccessor) world).isClientSide() && ((LevelAccessor) world).getServer() != null)
            ((LevelAccessor) world).getServer().getPlayerList().broadcastSystemMessage(Component.literal(("Temperature:" + world.getBiome(BlockPos.containing(entity.getX(), entity.getY(), entity.getZ())).value().getBaseTemperature() * 100f)), false);
        return ar;
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        double def = 0;
        double mgc = 0;
        double snt = 0;
        def = Math.round(Math.pow(10, 2) * ((Entity) entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get())
                ? _livingEntity0.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()).getValue()
                : 0)) / Math.pow(10, 2);
        mgc = Math.round(Math.pow(10, 2) * ((Entity) entity instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get())
                ? _livingEntity2.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).getValue()
                : 0)) / Math.pow(10, 2);
        snt = Math.round(Math.pow(10, 2) * ((Entity) entity instanceof LivingEntity _livingEntity4 && _livingEntity4.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RESISTANCE.get())
                ? _livingEntity4.getAttribute(CaerulaArborModAttributes.SANITY_RESISTANCE.get()).getValue()
                : 0)) / Math.pow(10, 2);
        if ((Entity) sourceentity instanceof Player _player && !_player.level().isClientSide())
            _player.displayClientMessage(Component.literal(("Defense: " + def)), false);
        if ((Entity) sourceentity instanceof Player _player && !_player.level().isClientSide())
            _player.displayClientMessage(Component.literal(("Magic Resiatance: " + mgc)), false);
        if ((Entity) sourceentity instanceof Player _player && !_player.level().isClientSide())
            _player.displayClientMessage(Component.literal(("Sanity Resistance: " + snt)), false);
        return retval;
	}
}
