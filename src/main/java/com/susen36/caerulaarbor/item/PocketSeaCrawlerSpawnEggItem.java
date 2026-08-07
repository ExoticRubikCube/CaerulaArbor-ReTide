package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.entity.crawler.PocketSeaCrawlerEntity;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

public class PocketSeaCrawlerSpawnEggItem extends DeferredSpawnEggItem {
	public PocketSeaCrawlerSpawnEggItem() {
		super(CAEntities.POCKET_SEA_CRAWLER, -3342337, -10092442, new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player != null && !player.isShiftKeyDown()) return super.useOn(context);
		LevelAccessor world = context.getLevel();
		double x = context.getClickedPos().getX();
		double y = context.getClickedPos().getY();
		double z = context.getClickedPos().getZ();
		Direction direction = context.getClickedFace();
		if (player == null) return InteractionResult.PASS;
		if (player.isShiftKeyDown()) {
			if (world instanceof ServerLevel level) {
				PocketSeaCrawlerEntity entity = CAEntities.POCKET_SEA_CRAWLER.get().spawn(level, BlockPos.containing(x + direction.getStepX() + 0.5, y + direction.getStepY() + 0.5, z + direction.getStepZ() + 0.5), MobSpawnType.MOB_SUMMONED);
				if (entity != null) {
					entity.setCharged();
					entity.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
		} else {
			if (world instanceof ServerLevel level) {
				PocketSeaCrawlerEntity entity = CAEntities.POCKET_SEA_CRAWLER.get().spawn(level, BlockPos.containing(x + direction.getStepX() + 0.5, y + direction.getStepY() + 0.5, z + direction.getStepZ() + 0.5), MobSpawnType.MOB_SUMMONED);
				if (entity != null) {
					entity.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
		}
		context.getItemInHand().shrink(1);
		return InteractionResult.SUCCESS;
	}
}