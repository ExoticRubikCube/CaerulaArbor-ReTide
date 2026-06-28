package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;

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
            BlockState cradle = Blocks.AIR.defaultBlockState();
            boolean found = false;
            double px = 0;
            double py = 0;
            double pz = 0;
            double bs = 0;
            if ((LevelAccessor) world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "ocarino")), SoundSource.PLAYERS, 1, 1);
            }
            if ((Entity) entity instanceof Player _player)
                _player.getCooldowns().addCooldown(itemstack.getItem(), 200);
            if (world.getDifficulty() != Difficulty.PEACEFUL) {
                if ((Entity) entity instanceof Player _playerHasItem ? _playerHasItem.getInventory().contains(new ItemStack(CaerulaArborModItems.WHIRL_EYE.get())) : false) {
                    for (int index0 = 0; index0 < 64; index0++) {
                        for (int index1 = 0; index1 < 24; index1++) {
                            for (int index2 = 0; index2 < 64; index2++) {
                                px = x + -32 + index0;
                                py = y + -12 + index1;
                                pz = z + -32 + index2;
                                cradle = (((LevelAccessor) world).getBlockState(BlockPos.containing(px, py, pz)));
                                bs = cradle.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _getip6 ? cradle.getValue(_getip6) : -1;
                                if (cradle.getBlock() == CaerulaArborModBlocks.TIDEWAY_CRADLE.get()) {
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
                                                Entity entityToSpawn = CaerulaArborModEntities.LINGERING_PATHSHAPER.get().spawn(_level, BlockPos.containing(px, py + 1, pz), MobSpawnType.MOB_SUMMONED);
                                                if (entityToSpawn != null) {
                                                    entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                                                }
                                            }
                                        } else {
                                            if ((LevelAccessor) world instanceof ServerLevel _level) {
                                                Entity entityToSpawn = CaerulaArborModEntities.ROUTE_SHAPER.get().spawn(_level, BlockPos.containing(px, py + 1, pz), MobSpawnType.MOB_SUMMONED);
                                                if (entityToSpawn != null) {
                                                    entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                                                }
                                            }
                                        }
                                        {
                                            ItemStack _ist = itemstack;
                                            if (_ist.hurt(1, RandomSource.create(), null)) {
                                                _ist.shrink(1);
                                                _ist.setDamageValue(0);
                                            }
                                        }
                                        if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                                            _player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_15").getString())), false);
                                        if ((Entity) entity instanceof Player _player) {
                                            ItemStack _stktoremove = new ItemStack(CaerulaArborModItems.WHIRL_EYE.get());
                                            _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                                        }
                                        found = true;
                                    }
                                } else if (cradle.getBlock() == CaerulaArborModBlocks.ENDSPEAKER_NEST.get()) {
                                    {
                                        ItemStack _ist = itemstack;
                                        if (_ist.hurt(1, RandomSource.create(), null)) {
                                            _ist.shrink(1);
                                            _ist.setDamageValue(0);
                                        }
                                    }
                                    world.destroyBlock(BlockPos.containing(px, py, pz), false);
                                    if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                                        _player.displayClientMessage(Component.literal((Component.translatable("spawn.endspeaker").getString())), false);
                                    if ((Entity) entity instanceof Player _player) {
                                        ItemStack _stktoremove = new ItemStack(CaerulaArborModItems.WHIRL_EYE.get());
                                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                                    }
                                    if ((LevelAccessor) world instanceof ServerLevel _level) {
                                        Entity entityToSpawn = CaerulaArborModEntities.ENDSPEAKER_0.get().spawn(_level, BlockPos.containing(px, py, pz), MobSpawnType.MOB_SUMMONED);
                                        if (entityToSpawn != null) {
                                            entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                                        }
                                    }
                                } else if (cradle.getBlock() == CaerulaArborModBlocks.MIZUKI_STATUE.get()) {
                                    if (bs == 0) {
                                        {
                                            int _value = 1;
                                            BlockPos _pos = BlockPos.containing(px, py, pz);
                                            BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                                            if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                                                ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
                                        }
                                        {
                                            ItemStack _ist = itemstack;
                                            if (_ist.hurt(1, RandomSource.create(), null)) {
                                                _ist.shrink(1);
                                                _ist.setDamageValue(0);
                                            }
                                        }
                                        if ((LevelAccessor) world instanceof ServerLevel _level) {
                                            Entity entityToSpawn = CaerulaArborModEntities.IZUMIK.get().spawn(_level, BlockPos.containing(px, py, pz), MobSpawnType.MOB_SUMMONED);
                                            if (entityToSpawn != null) {
                                                entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                                            }
                                        }
                                        if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                                            _player.displayClientMessage(Component.literal((Component.translatable("spawn.izumik.init").getString())), false);
                                        if ((Entity) entity instanceof Player _player) {
                                            ItemStack _stktoremove = new ItemStack(CaerulaArborModItems.WHIRL_EYE.get());
                                            _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                                        }
                                        found = true;
                                    }
                                } else if (cradle.getBlock() == CaerulaArborModBlocks.HIGHMORE_SPAWNBLOCK.get()) {
                                    ((LevelAccessor) world).setBlock(BlockPos.containing(px, py, pz), CaerulaArborModBlocks.HIGHMORE_SPAWNING_BLOCK.get().defaultBlockState(), 3);
                                    {
                                        ItemStack _ist = itemstack;
                                        if (_ist.hurt(1, RandomSource.create(), null)) {
                                            _ist.shrink(1);
                                            _ist.setDamageValue(0);
                                        }
                                    }
                                    if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                                        _player.displayClientMessage(Component.literal((Component.translatable("spawn.highmore").getString())), false);
                                    if ((Entity) entity instanceof Player _player) {
                                        ItemStack _stktoremove = new ItemStack(CaerulaArborModItems.WHIRL_EYE.get());
                                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                                    }
                                    found = true;
                                } else if (cradle.getBlock() == CaerulaArborModBlocks.TIDE_BISHOP_CORE.get()) {
                                    double hdns = 0;
                                    for (int dx = (int) (-1); dx <= (int) 1; dx++) {
                                        for (int dy = (int) (-1); dy <= (int) 5; dy++) {
                                            for (int dz = (int) (-1); dz <= (int) 1; dz++) {
                                                hdns = (((LevelAccessor) world).getBlockState(BlockPos.containing(px + dx, py + dy, pz + dz))).getDestroySpeed(world, BlockPos.containing(0, 0, 0));
                                                if (hdns <= 6 && hdns > 0) {
                                                    world.destroyBlock(BlockPos.containing(px + dx, py + dy, pz + dz), false);
                                                }
                                            }
                                        }
                                    }
                                    if ((LevelAccessor) world instanceof ServerLevel _level) {
                                        Entity entityToSpawn = CaerulaArborModEntities.TIDE_BISHOP.get().spawn(_level, BlockPos.containing(px + 0.5, py, pz + 0.5), MobSpawnType.MOB_SUMMONED);
                                        if (entityToSpawn != null) {
                                            entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                                        }
                                    }
                                    {
                                        ItemStack _ist = itemstack;
                                        if (_ist.hurt(1, RandomSource.create(), null)) {
                                            _ist.shrink(1);
                                            _ist.setDamageValue(0);
                                        }
                                    }
                                    if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                                        _player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_16").getString())), false);
                                    if ((Entity) entity instanceof Player _player) {
                                        ItemStack _stktoremove = new ItemStack(CaerulaArborModItems.WHIRL_EYE.get());
                                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                                    }
                                    world.destroyBlock(BlockPos.containing(px, py, pz), false);
                                    found = true;
                                } else if (cradle.getBlock() == CaerulaArborModBlocks.UNDERTIDE_TABLE.get()) {
                                    if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                                        _player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.a_second_key.description_3").getString())), false);
                                    if ((Entity) entity instanceof Player _player) {
                                        ItemStack _stktoremove = new ItemStack(CaerulaArborModItems.WHIRL_EYE.get());
                                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                                    }
                                    {
                                        BlockPos _bp = BlockPos.containing(px, py, pz);
                                        BlockState _bs = CaerulaArborModBlocks.UNDERTIDE_SPAWN.get().defaultBlockState();
                                        BlockState _bso = ((LevelAccessor) world).getBlockState(_bp);
                                        for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
                                            Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
                                            if (_property != null && _bs.getValue(_property) != null)
                                                try {
                                                    _bs = _bs.setValue(_property, (Comparable) entry.getValue());
                                                } catch (Exception e) {
                                                }
                                        }
                                        ((LevelAccessor) world).setBlock(_bp, _bs, 3);
                                    }
                                    found = true;
                                    if ((LevelAccessor) world instanceof ServerLevel _level) {
                                        Entity entityToSpawn = CaerulaArborModEntities.BISHOP_FISH.get().spawn(_level, BlockPos.containing(px + 0.5, py, pz + 0.5), MobSpawnType.MOB_SUMMONED);
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
