package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.Relic;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
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
        if (!Relic.HEMOST.gained(entity)) {
            if ((LevelAccessor) world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, (float) 3.5, 1);
            }
            if ((LevelAccessor) world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, x, y, z, 8, 1, 1, 1, 0.1);
            {
                boolean setval = true;
                PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                Relic.HEMOST.set(capability, setval ? 1 : 0);
                capability.syncPlayerVariables(entity);
            }
            if (world.isClientSide())
                Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
        }
        return ar;
	}
}