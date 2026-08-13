package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


public class RoyalFateItem extends CollectibleItem.CustomCollectibleItem {
	public RoyalFateItem() {
		super(new Item.Properties().stacksTo(2).fireResistant().rarity(Rarity.EPIC), false, 25, false, CollectibleTiers.RARE, new CollectibleItem.Levels(0, 1, 0),
				CollectibleActivation.builder()
						.sound(SoundEvents.WARDEN_DEATH, 2F, 1F)
						.particle(ParticleTypes.END_ROD, 72)
						.showOverlay(true)
						.build());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack itemstack) {
		return true;
	}

	

	@Override
	public void onUse(ItemStack stack, Level level, Player player, CollectibleItem.CustomCollectibleItem self) {
		if (ModCapabilities.getPlayerVariables(player).player_maxlive > 1) {
			double lives_left = ModCapabilities.getPlayerVariables(player).player_maxlive;
			PlayerVariable capability = ModCapabilities.getPlayerVariables(player);
			capability.player_maxlive = 1;
			capability.syncPlayerVariables(player);
			PlayerVariable capability2 = ModCapabilities.getPlayerVariables(player);
			capability2.player_lives = 1;
			capability2.syncPlayerVariables(player);
			PlayerVariable capability3 = ModCapabilities.getPlayerVariables(player);
            capability3.player_shield = capability3.player_shield + lives_left;
			capability3.syncPlayerVariables(player);
			PlayerVariable capability4 = ModCapabilities.getPlayerVariables(player);
            capability4.player_shield = capability4.player_shield + 3;
			capability4.syncPlayerVariables(player);
			stack.shrink(1);
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
        LevelAccessor world = context.getLevel();
        double x = context.getClickedPos().getX();
        double y = context.getClickedPos().getY();
        double z = context.getClickedPos().getZ();
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        Entity entity = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if (entity == null)
            return InteractionResult.PASS;
        if (blockstate.getBlock() == Blocks.DEEPSLATE_BRICK_SLAB) {
            world.setBlock(BlockPos.containing(x, y, z), CABlocks.BLOCK_FATE.get().defaultBlockState(), 3);
            {
                Direction dir = ((entity.getDirection()).getOpposite());
                BlockPos pos = BlockPos.containing(x, y, z);
                BlockState bs = world.getBlockState(pos);
                Property<?> property = bs.getBlock().getStateDefinition().getProperty("facing");
                if (property instanceof DirectionProperty dp && dp.getPossibleValues().contains(dir)) {
                    world.setBlock(pos, bs.setValue(dp, dir), 3);
                } else {
                    property = bs.getBlock().getStateDefinition().getProperty("axis");
                    if (property instanceof EnumProperty ap && ap.getPossibleValues().contains(dir.getAxis()))
                        world.setBlock(pos, bs.setValue(ap, dir.getAxis()), 3);
                }
            }
            itemstack.shrink(1);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
