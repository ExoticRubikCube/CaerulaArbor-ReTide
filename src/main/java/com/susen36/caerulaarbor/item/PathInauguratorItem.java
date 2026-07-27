
package com.susen36.caerulaarbor.item;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;
import java.util.Map;


public class PathInauguratorItem extends AxeItem {
	private static final Tier TIER = new SimpleTier(
			BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
			10025,
			11f,
			15f,
			14,
			() -> Ingredient.of()
	);

	public PathInauguratorItem() {
		super(TIER, new Item.Properties().fireResistant().attributes(AxeItem.createAttributes(TIER, 1, -2.5f)));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.path_inaugurator.description_0"));
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
        if (blockstate.getBlock() == Blocks.GRASS_BLOCK || blockstate.getBlock() == Blocks.DIRT_PATH || blockstate.getBlock() == Blocks.DIRT) {
            {
                BlockPos bp = BlockPos.containing(x, y, z);
                BlockState bs = Blocks.FARMLAND.defaultBlockState();
                BlockState bso = world.getBlockState(bp);
                for (Map.Entry<Property<?>, Comparable<?>> entry : bso.getValues().entrySet()) {
                    Property<?> property = bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
                    if (property != null && bs.hasProperty(property))
                        try {
                            bs = setBlockStateValue(bs, property, entry.getValue());
                        } catch (Exception ignored) {
                        }
                }
                world.setBlock(bp, bs, 3);
            }
            if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.HOE_TILL, SoundSource.PLAYERS, 1, 1);
            }
            if (!(new Object() {
                public boolean checkGamemode(Entity ent) {
                    if (ent instanceof ServerPlayer serverPlayer) {
                        return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                    } else if (ent.level().isClientSide() && ent instanceof Player player) {
                        return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                    }
                    return false;
                }
            }.checkGamemode(entity))) {
                if (itemstack.hurt(1, RandomSource.create(), null)) {
                    itemstack.shrink(1);
                    itemstack.setDamageValue(0);
                }
            }
            return InteractionResult.SUCCESS;
        } else if (blockstate.getBlock() == Blocks.ROOTED_DIRT) {
            {
                BlockPos bp = BlockPos.containing(x, y, z);
                BlockState bs = Blocks.DIRT.defaultBlockState();
                BlockState bso = world.getBlockState(bp);
                for (Map.Entry<Property<?>, Comparable<?>> entry : bso.getValues().entrySet()) {
                    Property<?> property = bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
                    if (property != null && bs.hasProperty(property))
                        try {
                            bs = setBlockStateValue(bs, property, entry.getValue());
                        } catch (Exception ignored) {
                        }
                }
                world.setBlock(bp, bs, 3);
            }
            if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.HOE_TILL, SoundSource.PLAYERS, 1, 1);
            }
            if (world instanceof ServerLevel level) {
                ItemEntity entityToSpawn = new ItemEntity(level, (x + 0.5 + direction.getStepX()), (y + 0.5 + direction.getStepY()), (z + 0.5 + direction.getStepZ()), new ItemStack(Blocks.HANGING_ROOTS));
                entityToSpawn.setPickUpDelay(10);
                level.addFreshEntity(entityToSpawn);
            }
            if (!(new Object() {
                public boolean checkGamemode(Entity ent) {
                    if (ent instanceof ServerPlayer serverPlayer) {
                        return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                    } else if (ent.level().isClientSide() && ent instanceof Player player) {
                        return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                    }
                    return false;
                }
            }.checkGamemode(entity))) {
                if (itemstack.hurt(1, RandomSource.create(), null)) {
                    itemstack.shrink(1);
                    itemstack.setDamageValue(0);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static BlockState setBlockStateValue(BlockState blockState, Property property, Comparable value) {
        return blockState.setValue(property, value);
    }
}