
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;


public class CarmenTreatyItem extends Item {
	public CarmenTreatyItem() {
		super(new Item.Properties().stacksTo(8).fireResistant().rarity(Rarity.UNCOMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.carmen_treaty.description_0"));
		list.add(Component.translatable("item.caerula_arbor.carmen_treaty.description_1"));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
        LevelAccessor world = context.getLevel();
        double x = context.getClickedPos().getX();
        double y = context.getClickedPos().getY();
        double z = context.getClickedPos().getZ();
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        Direction direction = context.getClickedFace();
        Entity entity = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if (entity == null)
            return InteractionResult.PASS;
        double tx;
        double ty;
        double tz;
        if (blockstate.getBlock() == CABlocks.FAX.get()) {
            tx = x + 0.5 + direction.getStepX();
            ty = y + direction.getStepY();
            tz = z + 0.5 + direction.getStepZ();
            if (world instanceof ServerLevel level)
                level.sendParticles(CAParticles.PURPLE_FLAME.get(), tx, (ty + 0.64), tz, 48, 0.64, 0.64, 0.64, 0.1);
            if (world instanceof ServerLevel level) {
                Entity entityToSpawn = CAEntities.SAINT_CARMEN.get().spawn(level, BlockPos.containing(tx, ty, tz), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
            }
            if (entity instanceof Player player && !player.level().isClientSide())
                player.displayClientMessage(Component.literal((Component.translatable("spawn.saint_carmen").getString())), true);
            itemstack.shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}