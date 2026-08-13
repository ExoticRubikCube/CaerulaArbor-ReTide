package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import com.susen36.caerulaarbor.init.CABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.items.ItemHandlerHelper;


public class CannedCherryItem extends CollectibleItem.CustomCollectibleItem {
	public CannedCherryItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON).food((new FoodProperties.Builder()).nutrition(6).saturationModifier(0.1f).alwaysEdible().build()), false, 25, CollectibleTiers.NORMAL, 0, 1, 0,
				CollectibleActivation.builder()
						.sound(SoundEvents.PLAYER_LEVELUP, 2F, 1F)
						.particle(ParticleTypes.HAPPY_VILLAGER, 72)
						.showOverlay(true)
						.build());
	}

	

	@Override
	public void onUse(ItemStack stack, Level level, Player player) {
		if (!level.isClientSide())
			player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 240, 1));
		ItemStack setstack = new ItemStack(Items.GLASS_BOTTLE).copy();
		setstack.setCount(1);
		ItemHandlerHelper.giveItemToPlayer(player, setstack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
        LevelAccessor world = context.getLevel();
        double x = context.getClickedPos().getX();
        double y = context.getClickedPos().getY();
        double z = context.getClickedPos().getZ();
        Direction direction = context.getClickedFace();
        Entity entity = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if (entity == null)
            return InteractionResult.PASS;
        if (CABlocks.BERRY_CAN.get().defaultBlockState().canSurvive(world, BlockPos.containing(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ()))) {
            world.setBlock(BlockPos.containing(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ()), CABlocks.BERRY_CAN.get().defaultBlockState(), 3);
            if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.GLASS_PLACE, SoundSource.BLOCKS, 1, 1);
            }
            {
                Direction dir = ((entity.getDirection()).getOpposite());
                BlockPos pos = BlockPos.containing(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ());
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
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
