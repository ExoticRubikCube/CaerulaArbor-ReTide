
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CaerulaArborModParticleTypes;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.procedures.UpgradeSilenceProcedure;
import com.apocalypse.caerulaarbor.utils.StrategyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;

import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

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
	public int getUseDuration(ItemStack itemstack) {
		return 30;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.leviathan_animus.description_0"));
		list.add(Component.translatable("item.caerula_arbor.leviathan_animus.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		entity.startUsingItem(hand);
		return ar;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
        if (!CaerulaArborModVariables.MapVariables.get(world).silence_enabled) {
            CaerulaArborModVariables.MapVariables.get(world).silence_enabled = true;
            CaerulaArborModVariables.MapVariables.get(world).syncData(world);
            if ((LevelAccessor) world instanceof Level _level) {
                if (!_level.isClientSide()) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.end_portal.spawn")), SoundSource.PLAYERS, 4, (float) 0.85);
                } else {
                    _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.end_portal.spawn")), SoundSource.PLAYERS, 4, (float) 0.85, false);
                }
            }
            if ((LevelAccessor) world instanceof ServerLevel _level)
                _level.sendParticles((SimpleParticleType) (CaerulaArborModParticleTypes.MOIST_BOOM.get()), x, (y + 2), z, 32, 2, 2, 2, 0.33);
            if (!((LevelAccessor) world).isClientSide() && ((LevelAccessor) world).getServer() != null)
                ((LevelAccessor) world).getServer().getPlayerList().broadcastSystemMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_14").getString())), false);
            itemstack.shrink(1);
        } else {
            if (CaerulaArborModVariables.MapVariables.get(world).strategy_silence < 4 && StrategyUtils.canEnableSilence(world)) {
                UpgradeSilenceProcedure.execute(world, 99999999);
                itemstack.shrink(1);
            } else {
                if ((LevelAccessor) world instanceof Level _level) {
                    if (!_level.isClientSide()) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.candle.extinguish")), SoundSource.PLAYERS, 2, 1);
                    } else {
                        _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.candle.extinguish")), SoundSource.PLAYERS, 2, 1, false);
                    }
                }
                if ((LevelAccessor) world instanceof ServerLevel _level)
                    _level.sendParticles(ParticleTypes.ASH, x, (y + 2), z, 64, 2, 2, 2, 0.33);
            }
        }
        return retval;
	}
}
