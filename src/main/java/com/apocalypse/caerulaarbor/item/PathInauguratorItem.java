
package com.apocalypse.caerulaarbor.item;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;

public class PathInauguratorItem extends AxeItem {
	public PathInauguratorItem() {
		super(new Tier() {
			public int getUses() {
				return 10025;
			}

			public float getSpeed() {
				return 11f;
			}

			public float getAttackDamageBonus() {
				return 15f;
			}

			public int getLevel() {
				return 3;
			}

			public int getEnchantmentValue() {
				return 14;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of();
			}
		}, 1, -2.5f, new Item.Properties().fireResistant());
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
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
                BlockPos _bp = BlockPos.containing(x, y, z);
                BlockState _bs = Blocks.FARMLAND.defaultBlockState();
                BlockState _bso = world.getBlockState(_bp);
                for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
                    Property<?> _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
                    if (_property != null && _bs.hasProperty(_property))
                        try {
                            _bs = setBlockStateValue(_bs, _property, entry.getValue());
                        } catch (Exception ignored) {
                        }
                }
                world.setBlock(_bp, _bs, 3);
            }
            if (world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.hoe.till")), SoundSource.PLAYERS, 1, 1);
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
                if (itemstack.hurt(1, RandomSource.create(), null)) {
                    itemstack.shrink(1);
                    itemstack.setDamageValue(0);
                }
            }
            return InteractionResult.SUCCESS;
        } else if (blockstate.getBlock() == Blocks.ROOTED_DIRT) {
            {
                BlockPos _bp = BlockPos.containing(x, y, z);
                BlockState _bs = Blocks.DIRT.defaultBlockState();
                BlockState _bso = world.getBlockState(_bp);
                for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
                    Property<?> _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
                    if (_property != null && _bs.hasProperty(_property))
                        try {
                            _bs = setBlockStateValue(_bs, _property, entry.getValue());
                        } catch (Exception ignored) {
                        }
                }
                world.setBlock(_bp, _bs, 3);
            }
            if (world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.hoe.till")), SoundSource.PLAYERS, 1, 1);
            }
            if (world instanceof ServerLevel _level) {
                ItemEntity entityToSpawn = new ItemEntity(_level, (x + 0.5 + direction.getStepX()), (y + 0.5 + direction.getStepY()), (z + 0.5 + direction.getStepZ()), new ItemStack(Blocks.HANGING_ROOTS));
                entityToSpawn.setPickUpDelay(10);
                _level.addFreshEntity(entityToSpawn);
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
