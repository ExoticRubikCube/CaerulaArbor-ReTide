
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.init.CABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;

public class CannedCherryItem extends Item {
	public CannedCherryItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON).food((new FoodProperties.Builder()).nutrition(6).saturationMod(0.1f).alwaysEat().build()));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.canned_cherry.description_0"));
		list.add(Component.translatable("item.caerula_arbor.canned_cherry.description_1"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = new ItemStack(Items.GLASS_BOTTLE);
		super.finishUsingItem(itemstack, world, entity);
		if (!entity.level().isClientSide())
			entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 240, 1));
		{
			boolean setval = true;
			entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
				capability.relic_util_BERRIES = setval;
				capability.syncPlayerVariables(entity);
			});
		}
		if (itemstack.isEmpty()) {
			return retval;
		} else {
			if (entity instanceof Player player && !player.getAbilities().instabuild) {
				if (!player.getInventory().add(retval))
					player.drop(retval, false);
			}
			return itemstack;
		}
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
