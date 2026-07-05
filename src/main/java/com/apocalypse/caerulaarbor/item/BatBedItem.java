
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.init.CABlocks;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public class BatBedItem extends Item {
	public BatBedItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.bat_bed.description_0"));
		list.add(Component.translatable("item.caerula_arbor.bat_bed.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemStack itemstack = ar.getObject();
        if ((LevelAccessor) world instanceof Level level) {
            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, SoundSource.NEUTRAL, (float) 3.5, 1);
        }
        if ((LevelAccessor) world instanceof ServerLevel level)
            level.sendParticles(ParticleTypes.ASH, x, y, z, 72, 1, 1, 1, 0.1);
        {
            boolean setval = true;
            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.relic_util_BATBED = setval;
                capability.syncPlayerVariables(entity);
            });
        }
        {
            double setval = (((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_maxlive + 4;
            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_maxlive = setval;
                capability.syncPlayerVariables(entity);
            });
        }
        {
            double setval = (((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives + 4;
            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_lives = setval;
                capability.syncPlayerVariables(entity);
            });
        }
        if (((LevelAccessor) world).isClientSide())
            Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
        itemstack.shrink(1);
        if ((Entity) entity instanceof Player player) {
            ItemStack setstack = new ItemStack(CABlocks.BLOCK_BATBED.get()).copy();
            setstack.setCount(1);
            ItemHandlerHelper.giveItemToPlayer(player, setstack);
        }
        return ar;
	}
}
