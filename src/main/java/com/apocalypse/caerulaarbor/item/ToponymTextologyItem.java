
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.util.ItemUtils;
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

import java.util.List;

public class ToponymTextologyItem extends Item {
	public ToponymTextologyItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
        String hoverText = ItemUtils.getOneUseItemDescription(itemstack);
        for (String line : hoverText.split("\n")) {
            list.add(Component.literal(line));
        }
    }

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemStack itemstack = ar.getObject();
        if (!itemstack.getOrCreateTag().getBoolean("used")) {
            {
                boolean setval = true;
                ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.relic_util_TOPONYM = setval;
                    capability.syncPlayerVariables(entity);
                });
            }
            if ((LevelAccessor) world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.NEUTRAL, 2, 1);
            }
            if ((LevelAccessor) world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.NAUTILUS, x, y, z, 72, 0.75, 1, 0.75, 1);
            itemstack.getOrCreateTag().putBoolean("used", true);
            if (((LevelAccessor) world).isClientSide())
                Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
            {
                double setval = (((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_shield + 6;
                ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.player_shield = setval;
                    capability.syncPlayerVariables(entity);
                });
            }
        }
        return ar;
	}
}
