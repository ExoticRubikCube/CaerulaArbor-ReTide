
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

public class SmellyHemostaticItem extends Item {
	public SmellyHemostaticItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.smelly_hemostatic.description_0"));
		list.add(Component.translatable("item.caerula_arbor.smelly_hemostatic.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemStack itemstack = ar.getObject();
        if (!(((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_HEMOST) {
            if ((LevelAccessor) world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, (float) 3.5, 1);
            }
            if ((LevelAccessor) world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, x, y, z, 8, 1, 1, 1, 0.1);
            {
                boolean setval = true;
                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.relic_HEMOST = setval;
                    capability.syncPlayerVariables(entity);
                });
            }
            if (world.isClientSide())
                Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
        }
        return ar;
	}
}
