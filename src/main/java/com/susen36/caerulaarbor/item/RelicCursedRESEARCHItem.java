
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.util.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.List;

public class RelicCursedRESEARCHItem extends Item {
	public RelicCursedRESEARCHItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		String hoverText = ItemUtils.getCursedDescription(itemstack);
        for (String line : hoverText.split("\n")) {
            list.add(Component.literal(line));
        }
    }

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (!itemstack.getOrCreateTag().getBoolean("used")) {
            if (!(entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_cursed_RESEARCH) {
                boolean setval = true;
                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.relic_cursed_RESEARCH = setval;
                    capability.syncPlayerVariables(entity);
                });
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD.value(), SoundSource.NEUTRAL, 2, 1);
                }
                if ((LevelAccessor) world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.CRIMSON_SPORE, x, y, z, 99, 1, 1, 1, 1);
                if (((LevelAccessor) world).isClientSide())
                    Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
                itemstack.getOrCreateTag().putBoolean("used", true);
            }
        }
    }
}
