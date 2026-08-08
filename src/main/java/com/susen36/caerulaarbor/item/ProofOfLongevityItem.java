
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


public class ProofOfLongevityItem extends Item {
	public ProofOfLongevityItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.proof_of_longevity.description_0"));
		list.add(Component.translatable("item.caerula_arbor.proof_of_longevity.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemStack itemstack = ar.getObject();
        if (world instanceof Level level) {
            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.AMBIENT_WARPED_FOREST_MOOD.value(), SoundSource.NEUTRAL, (float) 3.5, 1);
        }
        if (world instanceof ServerLevel level)
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 72, 1, 1, 1, 0.1);
        {
            boolean setval = true;
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            Relic.PROOF_OF_LONGEVITY.set(capability, setval ? 1 : 0);
            capability.syncPlayerVariables(entity);
        }
        {
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            double setval = capability.player_maxlive + 6;
            capability.player_maxlive = setval;
            capability.syncPlayerVariables(entity);
        }
        {
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            double setval = capability.player_lives + 6;
            capability.player_lives = setval;
            capability.syncPlayerVariables(entity);
        }
        if (world.isClientSide())
            Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
        itemstack.shrink(1);
        return ar;
	}
}