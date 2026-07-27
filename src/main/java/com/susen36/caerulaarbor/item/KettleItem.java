
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CABlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;


public class KettleItem extends Item {
	public KettleItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.kettle.description_0"));
		list.add(Component.translatable("item.caerula_arbor.kettle.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemStack itemstack = ar.getObject();
        {
            boolean setval = true;
            entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.relic_util_KETTLE = setval;
                capability.syncPlayerVariables(entity);
            });
        }
        if ((LevelAccessor) world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 2, 1);
        }
        if ((LevelAccessor) world instanceof ServerLevel level)
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 72, 0.75, 1, 0.75, 1);
        if (world.isClientSide())
            Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
        {
            double setval = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_maxlive + 1;
            entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_maxlive = setval;
                capability.syncPlayerVariables(entity);
            });
        }
        {
            double setval = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives + 1;
            entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_lives = setval;
                capability.syncPlayerVariables(entity);
            });
        }
        if ((Entity) entity instanceof Player player) {
            ItemStack setstack = new ItemStack(CABlocks.BLOCK_KETTLE.get()).copy();
            setstack.setCount(1);
            ItemHandlerHelper.giveItemToPlayer(player, setstack);
        }
        for (int index0 = 0; index0 < 2; index0++) {
            if ((LevelAccessor) world instanceof ServerLevel level)
                level.addFreshEntity(new ExperienceOrb(level, x, y, z, 4));
        }
        itemstack.shrink(1);
        return ar;
	}
}