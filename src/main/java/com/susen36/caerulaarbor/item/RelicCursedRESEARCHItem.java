
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.Relic;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.util.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.List;


public class RelicCursedRESEARCHItem extends Item {
	public RelicCursedRESEARCHItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
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
        if (!itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("used")) {
            if (!Relic.CURSED_RESEARCH.gained(entity)) {
                boolean setval = true;
                PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                Relic.CURSED_RESEARCH.set(capability, setval ? 1 : 0);
                capability.syncPlayerVariables(entity);
                if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD.value(), SoundSource.NEUTRAL, 2, 1);
                }
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.CRIMSON_SPORE, x, y, z, 99, 1, 1, 1, 1);
                if (world.isClientSide())
                    Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
                CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putBoolean("used", true));
            }
        }
    }
}