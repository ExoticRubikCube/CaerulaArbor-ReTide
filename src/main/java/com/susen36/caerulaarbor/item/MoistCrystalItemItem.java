
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.entity.enderdragon.MoistEnderCrystalEntity;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelAccessor;


public class MoistCrystalItemItem extends Item {
	public MoistCrystalItemItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
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
        if (world instanceof ServerLevel level) {
            Entity entityToSpawn = CAEntities.MOIST_ENDER_CRYSTAL.get().spawn(level, BlockPos.containing(x + direction.getStepX() + 0.5, y + direction.getStepY() + 0.5, z + direction.getStepZ() + 0.5), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
                entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                if (entityToSpawn instanceof MoistEnderCrystalEntity crystal){
                    crystal.IS_STATIC = true;
                }
            }
        }
        itemstack.shrink(1);
        return InteractionResult.SUCCESS;
    }
}