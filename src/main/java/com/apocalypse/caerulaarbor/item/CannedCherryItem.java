
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.registries.ForgeRegistries;

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
			boolean _setval = true;
			entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
				capability.relic_util_BERRIES = _setval;
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
        if (CaerulaArborModBlocks.BERRY_CAN.get().defaultBlockState().canSurvive(world, BlockPos.containing(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ()))) {
            world.setBlock(BlockPos.containing(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ()), CaerulaArborModBlocks.BERRY_CAN.get().defaultBlockState(), 3);
            if (world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.glass.place")), SoundSource.BLOCKS, 1, 1);
            }
            {
                Direction _dir = ((entity.getDirection()).getOpposite());
                BlockPos _pos = BlockPos.containing(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ());
                BlockState _bs = world.getBlockState(_pos);
                Property<?> _property = _bs.getBlock().getStateDefinition().getProperty("facing");
                if (_property instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(_dir)) {
                    world.setBlock(_pos, _bs.setValue(_dp, _dir), 3);
                } else {
                    _property = _bs.getBlock().getStateDefinition().getProperty("axis");
                    if (_property instanceof EnumProperty _ap && _ap.getPossibleValues().contains(_dir.getAxis()))
                        world.setBlock(_pos, _bs.setValue(_ap, _dir.getAxis()), 3);
                }
            }
            itemstack.shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
