package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import com.susen36.caerulaarbor.CaerulaArbor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;


public class OmniKeyItem extends CollectibleItem.CustomCollectibleItem {
	public OmniKeyItem() {
		super(new Item.Properties().durability(64).rarity(Rarity.UNCOMMON), false, 25, CollectibleTiers.NORMAL, 0, 1, 0,
				CollectibleActivation.builder()
						.sound(SoundEvents.PLAYER_LEVELUP, 2F, 1F)
						.particle(ParticleTypes.HAPPY_VILLAGER, 72)
						.showOverlay(true)
						.build());
	}

	@Override
	public void onUse(ItemStack stack, Level level, Player player) {
		player.giveExperienceLevels(3);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
        LevelAccessor world = context.getLevel();
        double x = context.getClickedPos().getX();
        double y = context.getClickedPos().getY();
        double z = context.getClickedPos().getZ();
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        ItemStack itemstack = context.getItemInHand();
        if (blockstate.getBlock() == Blocks.IRON_DOOR || blockstate.getBlock() == Blocks.IRON_TRAPDOOR) {
            if (!(blockstate.getBlock().getStateDefinition().getProperty("open") instanceof BooleanProperty getbp5 && blockstate.getValue(getbp5))) {
                {
                    BlockPos pos = BlockPos.containing(x, y, z);
                    BlockState bs = world.getBlockState(pos);
                    if (bs.getBlock().getStateDefinition().getProperty("open") instanceof BooleanProperty booleanProp)
                        world.setBlock(pos, bs.setValue(booleanProp, true), 3);
                }
                world.scheduleTick(BlockPos.containing(x, y, z), world.getBlockState(BlockPos.containing(x, y, z)).getBlock(), 22);
                if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.CHAIN_STEP, SoundSource.NEUTRAL, 1, 1);
                }
                CaerulaArbor.queueServerWork(20, () -> {
                    {
                        BlockPos pos = BlockPos.containing(x, y, z);
                        BlockState bs = world.getBlockState(pos);
                        if (bs.getBlock().getStateDefinition().getProperty("open") instanceof BooleanProperty booleanProp)
                            world.setBlock(pos, bs.setValue(booleanProp, false), 3);
                    }
                });
                if (world instanceof ServerLevel _level) {
                    itemstack.hurtAndBreak(1, _level, null, _item -> itemstack.setDamageValue(0));
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
