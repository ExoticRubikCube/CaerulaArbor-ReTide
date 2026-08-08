
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler;
import com.susen36.caerulaarbor.init.CAParticles;
import com.susen36.caerulaarbor.manager.upgrade.SilenceUpgradeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.List;


public class LeviathanAnimusItem extends Item {
	public LeviathanAnimusItem() {
		super(new Item.Properties().stacksTo(8).fireResistant().rarity(Rarity.EPIC));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity user) {
		return 30;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.leviathan_animus.description_0"));
		list.add(Component.translatable("item.caerula_arbor.leviathan_animus.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
        entity.startUsingItem(hand);
		return super.use(world, entity, hand);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
        if (!MapVariables.get(world).silence_enabled) {
            MapVariablesHandler.setSilenceEnabled(world, true);
            if ((LevelAccessor) world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 4, (float) 0.85);
            }
            if ((LevelAccessor) world instanceof ServerLevel level)
                level.sendParticles(CAParticles.MOIST_BOOM.get(), x, (y + 2), z, 32, 2, 2, 2, 0.33);
            if (!world.isClientSide() && world.getServer() != null)
                world.getServer().getPlayerList().broadcastSystemMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_14").getString())), false);
            itemstack.shrink(1);
        } else {
            if (MapVariables.get(world).strategy_silence < 4 && SilenceUpgradeManager.canEnableSilence(world)) {
                SilenceUpgradeManager.applySilenceUpgrade(world, 99999999);
                itemstack.shrink(1);
            } else {
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.CANDLE_EXTINGUISH, SoundSource.PLAYERS, 2, 1);
                }
                if ((LevelAccessor) world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.ASH, x, (y + 2), z, 64, 2, 2, 2, 0.33);
            }
        }
        return retval;
	}
}