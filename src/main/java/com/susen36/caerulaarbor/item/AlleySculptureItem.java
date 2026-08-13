package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CARelics;
import com.susen36.caerulaarbor.item.relic.RelicItemBase;
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
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;


public class AlleySculptureItem extends RelicItemBase {
	public AlleySculptureItem() {
		super(CARelics.UTIL_ALLEY, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemStack itemstack = ar.getObject();
        if (world instanceof Level level) {
            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ALLAY_AMBIENT_WITH_ITEM, SoundSource.NEUTRAL, (float) 3.5, 1);
        }
        if (world instanceof ServerLevel level)
            level.sendParticles(ParticleTypes.RAIN, x, y, z, 72, 1, 1, 1, 0.1);
        {
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            CARelics.UTIL_ALLEY.get().set(capability, 1);
            capability.syncPlayerVariables(entity);
        }
        {
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            capability.player_maxlive = capability.player_maxlive + 3;
            capability.syncPlayerVariables(entity);
        }
        {
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            capability.player_lives = capability.player_lives + 3;
            capability.syncPlayerVariables(entity);
        }
        if (world.isClientSide())
            Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
        if ((Entity) entity instanceof Player player) {
            ItemStack setstack = new ItemStack(CABlocks.ALLAY_BLOCK.get()).copy();
            setstack.setCount(1);
            ItemHandlerHelper.giveItemToPlayer(player, setstack);
        }
        itemstack.shrink(1);
        return ar;
	}
}