package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.entity.EndspeakerEntity;
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
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import java.util.List;


public class EndspeakerSpawneggItem extends DeferredSpawnEggItem {
	public EndspeakerSpawneggItem() {
		super(CAEntities.ENDSPEAKER, -1, -1, new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.endspeaker_spawnegg.description_0"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		ItemStack item = entity.getItemInHand(hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        double phase;
        double tgtX;
        double tgtY;
        double tgtZ;
        phase = item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble("phase");
        if (entity.isShiftKeyDown()) {
            CustomData.update(DataComponents.CUSTOM_DATA, item, tag -> tag.putDouble("phase", ((phase + 1) % 4)));
            if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                player.displayClientMessage(Component.literal(((Component.translatable("item.caerula_arbor.endspeaker_spawnegg.use").getString()).replace("{p}", "" + Math.round(item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble("phase") + 1)))), true);
        } else {
            if (!((world.getFluidState(BlockPos.containing(x, y, z)).createLegacyBlock()).getBlock() == Blocks.AIR)) {
                tgtX = x + 0.5;
                tgtY = y + 0.5;
                tgtZ = z + 0.5;
                if (world instanceof ServerLevel level) {
                    EndspeakerEntity.spawnForPhase(level, BlockPos.containing(tgtX, tgtY, tgtZ), MobSpawnType.MOB_SUMMONED, (int) phase);
                }
                item.shrink(1);
            }
        }
        return InteractionResultHolder.pass(item);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if(player != null && player.isShiftKeyDown()) return super.useOn(context);
        LevelAccessor world = context.getLevel();
        double x = context.getClickedPos().getX();
        double y = context.getClickedPos().getY();
        double z = context.getClickedPos().getZ();
        Direction direction = context.getClickedFace();
        ItemStack itemstack = context.getItemInHand();
        double tgtX;
        double tgtY;
        double tgtZ;
        double phase;
        tgtX = x + direction.getStepX() + 0.5;
        tgtY = y + direction.getStepY() + 0.5;
        tgtZ = z + direction.getStepZ() + 0.5;
        phase = itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble("phase");
        if (world instanceof ServerLevel level) {
            EndspeakerEntity.spawnForPhase(level, BlockPos.containing(tgtX, tgtY, tgtZ), MobSpawnType.MOB_SUMMONED, (int) phase);
        }
        itemstack.shrink(1);
        return InteractionResult.SUCCESS;
    }
}