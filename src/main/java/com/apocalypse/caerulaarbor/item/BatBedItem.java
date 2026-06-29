
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.init.CABlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraftforge.registries.ForgeRegistries;

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
        if ((LevelAccessor) world instanceof Level _level) {
            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.allay.ambient_without_item")), SoundSource.NEUTRAL, (float) 3.5, 1);
        }
        if ((LevelAccessor) world instanceof ServerLevel _level)
            _level.sendParticles(ParticleTypes.ASH, x, y, z, 72, 1, 1, 1, 0.1);
        {
            boolean _setval = true;
            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.relic_util_BATBED = _setval;
                capability.syncPlayerVariables(entity);
            });
        }
        {
            double _setval = (((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_maxlive + 4;
            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_maxlive = _setval;
                capability.syncPlayerVariables(entity);
            });
        }
        {
            double _setval = (((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives + 4;
            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_lives = _setval;
                capability.syncPlayerVariables(entity);
            });
        }
        if (((LevelAccessor) world).isClientSide())
            Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
        itemstack.shrink(1);
        if ((Entity) entity instanceof Player _player) {
            ItemStack _setstack = new ItemStack(CABlocks.BLOCK_BATBED.get()).copy();
            _setstack.setCount(1);
            ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
        }
        return ar;
	}
}
