package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import com.susen36.caerulaarbor.init.CASounds;

import java.util.List;

public class LittleHelperItem extends Item {
	public LittleHelperItem() {
		this(Rarity.UNCOMMON);
	}

	protected LittleHelperItem(Rarity rarity) {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(rarity));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.item_helper.description_0"));
		list.add(Component.translatable("item.caerula_arbor.item_helper.description_1"));
		list.add(Component.translatable("item.caerula_arbor.item_helper.description_2"));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		LevelAccessor world = context.getLevel();
		double x = context.getClickedPos().getX();
		double y = context.getClickedPos().getY();
		double z = context.getClickedPos().getZ();
		Direction direction = context.getClickedFace();
		ItemStack itemstack = context.getItemInHand();
		double tgtX = x + direction.getStepX() + 0.5;
		double tgtY = y + direction.getStepY() + 0.5;
		double tgtZ = z + direction.getStepZ() + 0.5;
		if (world instanceof ServerLevel level) {
			Entity entityToSpawn = this.getHelperEntityType().spawn(level, BlockPos.containing(tgtX, tgtY, tgtZ), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
			}
		}
		if (world instanceof Level level) {
			BlockPos soundPos = BlockPos.containing(x, y, z);
			level.playSound(null, soundPos, CASounds.CLEAN_BOT_START.get(), SoundSource.BLOCKS, 3, 1);
			if (!world.isClientSide()) {
				this.playPlaceSound(level, soundPos);
			}
		}
		itemstack.shrink(1);
		return InteractionResult.SUCCESS;
	}

	protected EntityType<?> getHelperEntityType() {
		return CAEntities.LITTLE_HELPER.get();
	}

	protected void playPlaceSound(Level level, BlockPos soundPos) {
	}
}
