
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.init.CABlocks;
import com.apocalypse.caerulaarbor.util.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class RelicCurseEMELIGHTItem extends Item {
	public RelicCurseEMELIGHTItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		Entity entity = itemstack.getEntityRepresentation();
		String hoverText = ItemUtils.getCursedDescription(itemstack);
		if (hoverText != null) {
			for (String line : hoverText.split("\n")) {
				list.add(Component.literal(line));
			}
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
        if (direction == null || entity == null)
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
        toPlace = (CABlocks.EMERGENCY_LIGHT.get().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _withbp8
                ? CABlocks.EMERGENCY_LIGHT.get().defaultBlockState().setValue(_withbp8, wattered)
                : CABlocks.EMERGENCY_LIGHT.get().defaultBlockState());
        if (CABlocks.EMERGENCY_LIGHT.get().defaultBlockState().canSurvive(world, BlockPos.containing(tX, tY, tZ)) && (world.getBlockState(BlockPos.containing(tX, tY, tZ))).canBeReplaced()) {
            if (direction == Direction.DOWN) {
                world.setBlock(BlockPos.containing(tX, tY, tZ), (new Object() {
                    public BlockState with(BlockState _bs, String _property, int _newValue) {
                        Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty(_property);
                        return _prop instanceof IntegerProperty _ip && _prop.getPossibleValues().contains(_newValue) ? _bs.setValue(_ip, _newValue) : _bs;
                    }
                }.with(toPlace, "blockstate", 2)), 3);
            } else {
                world.setBlock(BlockPos.containing(tX, tY, tZ), (new Object() {
                    public BlockState with(BlockState _bs, String _property, int _newValue) {
                        Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty(_property);
                        return _prop instanceof IntegerProperty _ip && _prop.getPossibleValues().contains(_newValue) ? _bs.setValue(_ip, _newValue) : _bs;
                    }
                }.with(toPlace, "blockstate", 1)), 3);
            }
            if (world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.lantern.place")), SoundSource.NEUTRAL, 1, 1);
            }
            if (!(new Object() {
                public boolean checkGamemode(Entity _ent) {
                    if (_ent instanceof ServerPlayer _serverPlayer) {
                        return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                    } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                        return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
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
                    boolean _setval = true;
                    entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.relic_cursed_EMELIGHT = _setval;
                        capability.syncPlayerVariables(entity);
                    });
                }
                if ((LevelAccessor) world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambient.soul_sand_valley.mood")), SoundSource.NEUTRAL, 2, 1);
                }
                if ((LevelAccessor) world instanceof ServerLevel _level)
                    _level.sendParticles(ParticleTypes.CRIMSON_SPORE, x, y, z, 99, 1, 1, 1, 1);
                if (((LevelAccessor) world).isClientSide())
                    Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
                itemstack.getOrCreateTag().putBoolean("used", true);
            }
        }
    }
}
