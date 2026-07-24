
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.util.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;

public class RelicCurseEMELIGHTItem extends Item {
	public RelicCurseEMELIGHTItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
        String hoverText = ItemUtils.getCursedDescription(itemstack);
        for (String line : hoverText.split("\n")) {
            list.add(Component.literal(line));
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
        double tX;
        double tY;
        double tZ;
        boolean wattered;
        BlockState toPlace;
        if (!itemstack.getOrCreateTag().getBoolean("used")) {
            return InteractionResult.PASS;
        }
        tX = x + direction.getStepX();
        tY = y + direction.getStepY();
        tZ = z + direction.getStepZ();
        wattered = (world.getFluidState(BlockPos.containing(tX, tY, tZ)).createLegacyBlock()).getBlock() == Blocks.WATER;
        toPlace = (CABlocks.EMERGENCY_LIGHT.get().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty withbp8
                ? CABlocks.EMERGENCY_LIGHT.get().defaultBlockState().setValue(withbp8, wattered)
                : CABlocks.EMERGENCY_LIGHT.get().defaultBlockState());
        if (CABlocks.EMERGENCY_LIGHT.get().defaultBlockState().canSurvive(world, BlockPos.containing(tX, tY, tZ)) && (world.getBlockState(BlockPos.containing(tX, tY, tZ))).canBeReplaced()) {
            if (direction == Direction.DOWN) {
                world.setBlock(BlockPos.containing(tX, tY, tZ), (new Object() {
                    public BlockState with(BlockState bs, String property, int newValue) {
                        Property<?> prop = bs.getBlock().getStateDefinition().getProperty(property);
                        return prop instanceof IntegerProperty ip && prop.getPossibleValues().contains(newValue) ? bs.setValue(ip, newValue) : bs;
                    }
                }.with(toPlace, "blockstate", 2)), 3);
            } else {
                world.setBlock(BlockPos.containing(tX, tY, tZ), (new Object() {
                    public BlockState with(BlockState bs, String property, int newValue) {
                        Property<?> prop = bs.getBlock().getStateDefinition().getProperty(property);
                        return prop instanceof IntegerProperty ip && prop.getPossibleValues().contains(newValue) ? bs.setValue(ip, newValue) : bs;
                    }
                }.with(toPlace, "blockstate", 1)), 3);
            }
            if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.LANTERN_PLACE, SoundSource.NEUTRAL, 1, 1);
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
                itemstack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (!itemstack.getOrCreateTag().getBoolean("used")) {
            if (!(entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_cursed_EMELIGHT) {
                {
                    boolean setval = true;
                    entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.relic_cursed_EMELIGHT = setval;
                        capability.syncPlayerVariables(entity);
                    });
                }
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD.value(), SoundSource.NEUTRAL, 2, 1);
                }
                if ((LevelAccessor) world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.CRIMSON_SPORE, x, y, z, 99, 1, 1, 1, 1);
                if (world.isClientSide())
                    Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
                itemstack.getOrCreateTag().putBoolean("used", true);
            }
        }
    }
}
