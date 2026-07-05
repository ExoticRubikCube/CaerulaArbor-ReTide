package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.entity.EndspeakerEntity;
import com.apocalypse.caerulaarbor.init.CABlocks;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.List;
public class OcarinaItem extends Item {
	public OcarinaItem() {
		super(new Item.Properties().durability(8).fireResistant().rarity(Rarity.UNCOMMON));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 30;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.ocarina.description_0"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		entity.startUsingItem(hand);
		return ar;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
        if (entity != null) {
            BlockState cradle;
            boolean found = false;
            double px;
            double py;
            double pz;
            double bs;
            if ((LevelAccessor) world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), CASounds.OCARINO.get(), SoundSource.PLAYERS, 1, 1);
            }
            if ((Entity) entity instanceof Player _player)
                _player.getCooldowns().addCooldown(itemstack.getItem(), 200);
            if (world.getDifficulty() != Difficulty.PEACEFUL) {
                if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.WHIRL_EYE.get()))) {
                    for (int index0 = 0; index0 < 64; index0++) {
                        for (int index1 = 0; index1 < 24; index1++) {
                            for (int index2 = 0; index2 < 64; index2++) {
                                px = x + -32 + index0;
                                py = y + -12 + index1;
                                pz = z + -32 + index2;
                                cradle = (((LevelAccessor) world).getBlockState(BlockPos.containing(px, py, pz)));
                                bs = cradle.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _getip6 ? cradle.getValue(_getip6) : -1;
                                if (cradle.getBlock() == CABlocks.TIDEWAY_CRADLE.get()) {
                                    if (bs == 0) {
                                        {
                                            int _value = 1;
                                            BlockPos _pos = BlockPos.containing(px, py, pz);
                                            BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                                            if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                                                ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
                                        }
                                        if (Math.random() < 0.05) {
                                            if ((LevelAccessor) world instanceof ServerLevel _level) {
                                                Entity entityToSpawn = CAEntities.LINGERING_PATHSHAPER.get().spawn(_level, BlockPos.containing(px, py + 1, pz), MobSpawnType.MOB_SUMMONED);
                                                if (entityToSpawn != null) {
                                                    entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                                                }
                                            }
                                        } else {
                                            if ((LevelAccessor) world instanceof ServerLevel _level) {
                                                Entity entityToSpawn = CAEntities.ROUTE_SHAPER.get().spawn(_level, BlockPos.containing(px, py + 1, pz), MobSpawnType.MOB_SUMMONED);
                                                if (entityToSpawn != null) {
                                                    entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                                                }
                                            }
                                        }
                                        {
                                            if (itemstack.hurt(1, RandomSource.create(), null)) {
                                                itemstack.shrink(1);
                                                itemstack.setDamageValue(0);
                                            }
                                        }
                                        if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                                            _player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_15").getString())), false);
                                        if ((Entity) entity instanceof Player _player) {
                                            ItemStack _stktoremove = new ItemStack(CAItems.WHIRL_EYE.get());
                                            _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                                        }
                                        found = true;
                                    }
                                } else if (cradle.getBlock() == CABlocks.ENDSPEAKER_NEST.get()) {
                                    {
                                        if (itemstack.hurt(1, RandomSource.create(), null)) {
                                            itemstack.shrink(1);
                                            itemstack.setDamageValue(0);
                                        }
                                    }
                                    world.destroyBlock(BlockPos.containing(px, py, pz), false);
                                    if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                                        _player.displayClientMessage(Component.literal((Component.translatable("spawn.endspeaker").getString())), false);
                                    if ((Entity) entity instanceof Player _player) {
                                        ItemStack _stktoremove = new ItemStack(CAItems.WHIRL_EYE.get());
                                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                                    }
                                    if ((LevelAccessor) world instanceof ServerLevel level) {
                                        EndspeakerEntity.spawnForPhase(level, BlockPos.containing(px, py, pz), MobSpawnType.MOB_SUMMONED, 0);
                                    }
                                } else if (cradle.getBlock() == CABlocks.MIZUKI_STATUE.get()) {
                                    if (bs == 0) {
                                        {
                                            int _value = 1;
                                            BlockPos _pos = BlockPos.containing(px, py, pz);
                                            BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                                            if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                                                ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
                                        }
                                        {
                                            if (itemstack.hurt(1, RandomSource.create(), null)) {
                                                itemstack.shrink(1);
                                                itemstack.setDamageValue(0);
                                            }
                                        }
                                        if ((LevelAccessor) world instanceof ServerLevel _level) {
                                            Entity entityToSpawn = CAEntities.IZUMIK.get().spawn(_level, BlockPos.containing(px, py, pz), MobSpawnType.MOB_SUMMONED);
                                            if (entityToSpawn != null) {
                                                entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                                            }
                                        }
                                        if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                                            _player.displayClientMessage(Component.literal((Component.translatable("spawn.izumik.init").getString())), false);
                                        if ((Entity) entity instanceof Player _player) {
                                            ItemStack _stktoremove = new ItemStack(CAItems.WHIRL_EYE.get());
                                            _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                                        }
                                        found = true;
                                    }
                                } else if (cradle.getBlock() == CABlocks.HIGHMORE_SPAWNBLOCK.get()) {
                                    ((LevelAccessor) world).setBlock(BlockPos.containing(px, py, pz), CABlocks.HIGHMORE_SPAWNING_BLOCK.get().defaultBlockState(), 3);
                                    {
                                        if (itemstack.hurt(1, RandomSource.create(), null)) {
                                            itemstack.shrink(1);
                                            itemstack.setDamageValue(0);
                                        }
                                    }
                                    if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                                        _player.displayClientMessage(Component.literal((Component.translatable("spawn.highmore").getString())), false);
                                    if ((Entity) entity instanceof Player _player) {
                                        ItemStack _stktoremove = new ItemStack(CAItems.WHIRL_EYE.get());
                                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                                    }
                                    found = true;
                                } else if (cradle.getBlock() == CABlocks.TIDE_BISHOP_CORE.get()) {
                                    double hdns;
                                    for (int dx = -1; dx <= 1; dx++) {
                                        for (int dy = -1; dy <= 5; dy++) {
                                            for (int dz = -1; dz <= 1; dz++) {
                                                hdns = (((LevelAccessor) world).getBlockState(BlockPos.containing(px + dx, py + dy, pz + dz))).getDestroySpeed(world, BlockPos.containing(0, 0, 0));
                                                if (hdns <= 6 && hdns > 0) {
                                                    world.destroyBlock(BlockPos.containing(px + dx, py + dy, pz + dz), false);
                                                }
                                            }
                                        }
                                    }
                                    if ((LevelAccessor) world instanceof ServerLevel _level) {
                                        Entity entityToSpawn = CAEntities.TIDE_BISHOP.get().spawn(_level, BlockPos.containing(px + 0.5, py, pz + 0.5), MobSpawnType.MOB_SUMMONED);
                                        if (entityToSpawn != null) {
                                            entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                                        }
                                    }
                                    {
                                        if (itemstack.hurt(1, RandomSource.create(), null)) {
                                            itemstack.shrink(1);
                                            itemstack.setDamageValue(0);
                                        }
                                    }
                                    if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                                        _player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_16").getString())), false);
                                    if ((Entity) entity instanceof Player _player) {
                                        ItemStack _stktoremove = new ItemStack(CAItems.WHIRL_EYE.get());
                                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                                    }
                                    world.destroyBlock(BlockPos.containing(px, py, pz), false);
                                    found = true;
                                } else if (cradle.getBlock() == CABlocks.UNDERTIDE_TABLE.get()) {
                                    if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                                        _player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.a_second_key.description_3").getString())), false);
                                    if ((Entity) entity instanceof Player _player) {
                                        ItemStack _stktoremove = new ItemStack(CAItems.WHIRL_EYE.get());
                                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                                    }
                                    {
                                        BlockPos _bp = BlockPos.containing(px, py, pz);
                                        BlockState _bs = CABlocks.UNDERTIDE_SPAWN.get().withPropertiesOf(((LevelAccessor) world).getBlockState(_bp));
                                        ((LevelAccessor) world).setBlock(_bp, _bs, 3);
                                    }
                                    found = true;
                                    if ((LevelAccessor) world instanceof ServerLevel _level) {
                                        Entity entityToSpawn = CAEntities.BISHOP_FISH.get().spawn(_level, BlockPos.containing(px + 0.5, py, pz + 0.5), MobSpawnType.MOB_SUMMONED);
                                        if (entityToSpawn != null) {
                                            entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                                        }
                                    }
                                }
                            }
                            if (found) {
                                break;
                            }
                        }
                        if (found) {
                            break;
                        }
                    }
                }
            }
        }
        return retval;
	}
}
