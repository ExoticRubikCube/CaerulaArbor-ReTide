
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CAEntities;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.List;

public class OceanizedWardenSpawneggItem extends ForgeSpawnEggItem {
	public OceanizedWardenSpawneggItem() {
		super(CAEntities.OCEANIZED_WARDEN, -1, -1, new Item.Properties().stacksTo(64).rarity(Rarity.EPIC));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.oceanized_warden_spawnegg.description_0"));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if(player != null && !player.isShiftKeyDown()) return super.useOn(context);
        LevelAccessor world = context.getLevel();
        double x = context.getClickedPos().getX();
        double y = context.getClickedPos().getY();
        double z = context.getClickedPos().getZ();
        Direction direction = context.getClickedFace();
        Entity entity = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if (entity == null)
            return InteractionResult.PASS;
        if (entity.isShiftKeyDown()) {
            if (world instanceof ServerLevel level) {
                Entity entityToSpawn = CAEntities.OCEANIZED_WARDENIS.get().spawn(level, BlockPos.containing(x + direction.getStepX() + 0.5, y + direction.getStepY() + 0.5, z + direction.getStepZ() + 0.5), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
            }
        } else {
            if (world instanceof ServerLevel level) {
                Entity entityToSpawn = CAEntities.OCEANIZED_WARDEN.get().spawn(level, BlockPos.containing(x + direction.getStepX() + 0.5, y + direction.getStepY() + 0.5, z + direction.getStepZ() + 0.5), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
            }
        }
        itemstack.shrink(1);
        return InteractionResult.SUCCESS;
    }
}
