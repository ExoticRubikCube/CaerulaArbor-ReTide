
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import java.util.List;


public class PathShaperSpawneggItem extends DeferredSpawnEggItem {
	public PathShaperSpawneggItem() {
		super(CAEntities.ROUTE_SHAPER, -1, -1, new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.path_shaper_spawnegg.description_0"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		ItemStack itemstack = entity.getItemInHand(hand);
		InteractionResultHolder<ItemStack> result;
		if (entity.isShiftKeyDown()) {
			CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putBoolean("lingering", !tag.getBoolean("lingering")));
			result = InteractionResultHolder.sidedSuccess(itemstack, world.isClientSide());
		} else {
			result = super.use(world, entity, hand);
		}
		return result;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		InteractionResult result = InteractionResult.PASS;
		if (player == null || !player.isShiftKeyDown()) {
			LevelAccessor world = context.getLevel();
			double x = context.getClickedPos().getX();
			double y = context.getClickedPos().getY();
			double z = context.getClickedPos().getZ();
			Direction direction = context.getClickedFace();
			ItemStack itemstack = context.getItemInHand();
			boolean lingering = itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("lingering");
			if (world instanceof ServerLevel level) {
				Entity entityToSpawn;
				if (lingering) {
					entityToSpawn = CAEntities.LINGERING_PATHSHAPER.get().spawn(level, BlockPos.containing(x + direction.getStepX() + 0.5, y + direction.getStepY() + 0.5, z + direction.getStepZ() + 0.5), MobSpawnType.MOB_SUMMONED);
				} else {
					entityToSpawn = CAEntities.ROUTE_SHAPER.get().spawn(level, BlockPos.containing(x + direction.getStepX() + 0.5, y + direction.getStepY() + 0.5, z + direction.getStepZ() + 0.5), MobSpawnType.MOB_SUMMONED);
				}
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
			itemstack.shrink(1);
			result = InteractionResult.SUCCESS;
		}
		return result;
    }
}