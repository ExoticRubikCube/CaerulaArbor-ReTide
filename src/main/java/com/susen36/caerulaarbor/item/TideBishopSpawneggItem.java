package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.entity.tidelinked.TideBishopEntity;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import java.util.List;

public class TideBishopSpawneggItem extends DeferredSpawnEggItem {
    public TideBishopSpawneggItem() {
        super(CAEntities.TIDE_BISHOP, -1, -1, new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.translatable("item.caerula_arbor.tide_bishop_spawnegg.description_0"));
        list.add(Component.translatable("item.caerula_arbor.tide_bishop_spawnegg.description_1"));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && !player.isShiftKeyDown()) {
            return super.useOn(context);
        }

        LevelAccessor level = context.getLevel();
        Direction direction = context.getClickedFace();
        BlockPos spawnPos = context.getClickedPos().relative(direction);
        ItemStack itemStack = context.getItemInHand();
        if (level instanceof ServerLevel serverLevel) {
            TideBishopEntity bishop = new TideBishopEntity(CAEntities.TIDE_BISHOP.get(), serverLevel, true);
            bishop.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, level.getRandom().nextFloat() * 360F, 0.0F);
            bishop.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(spawnPos), MobSpawnType.MOB_SUMMONED, null);
            serverLevel.addFreshEntity(bishop);
        }
        itemStack.shrink(1);
        return InteractionResult.SUCCESS;
    }
}
