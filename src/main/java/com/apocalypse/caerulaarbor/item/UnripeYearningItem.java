
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
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

public class UnripeYearningItem extends Item {
	public UnripeYearningItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.unripe_yearning.description_0"));
		list.add(Component.translatable("item.caerula_arbor.unripe_yearning.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemStack itemstack = ar.getObject();
        if (!(((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_YEARNING) {
            if ((LevelAccessor) world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.NEUTRAL, 2, 1);
            }
            if ((LevelAccessor) world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.DOLPHIN, x, y, z, 72, 1, 1, 1, 1);
            {
                boolean setval = true;
                ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.relic_YEARNING = setval;
                    capability.syncPlayerVariables(entity);
                });
            }
            if (((LevelAccessor) world).isClientSide())
                Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
        }
        return ar;
	}
}
