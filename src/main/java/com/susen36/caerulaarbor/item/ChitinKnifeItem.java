
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAItems;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;


public class ChitinKnifeItem extends RelicItemBase {
	public ChitinKnifeItem() {
		super(CARelics.LEGEND_CHITIN, new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC));
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
        if (!CARelics.LEGEND_CHITIN.get().gained(entity)) {
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.BELL_BLOCK, SoundSource.NEUTRAL, (float) 3.5, 1);
            }
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.DOLPHIN, x, y, z, 72, 1, 1, 1, 0.1);
            {
                boolean setval = true;
                PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                CARelics.LEGEND_CHITIN.get().set(capability, setval ? 1 : 0);
                capability.syncPlayerVariables(entity);
            }
            if (world.isClientSide())
                Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
            if (world instanceof ServerLevel level) {
                ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(CAItems.OCEAN_TRIM_TEMPLATE.get()));
                entityToSpawn.setPickUpDelay(5);
                entityToSpawn.setUnlimitedLifetime();
                level.addFreshEntity(entityToSpawn);
            }
        }
        return ar;
	}
}