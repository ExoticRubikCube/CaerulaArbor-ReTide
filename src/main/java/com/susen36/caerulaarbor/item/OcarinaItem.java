package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.entity.EndspeakerEntity;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
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
	public int getUseDuration(ItemStack itemstack, LivingEntity user) {
		return 30;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.ocarina.description_0"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		entity.startUsingItem(hand);
		return super.use(world, entity, hand);
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
            if ((LevelAccessor) world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), CASounds.OCARINO.get(), SoundSource.PLAYERS, 1, 1);
            }
            if ((Entity) entity instanceof Player player)
                player.getCooldowns().addCooldown(itemstack.getItem(), 200);
            if (world.getDifficulty() != Difficulty.PEACEFUL) {
                if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.WHIRL_EYE.get()))) {
                    for (int index0 = 0; index0 < 64; index0++) {
                        for (int index1 = 0; index1 < 24; index1++) {
                            for (int index2 = 0; index2 < 64; index2++) {
                                px = x + -32 + index0;
                                py = y + -12 + index1;
                                pz = z + -32 + index2;
                                cradle = (world.getBlockState(BlockPos.containing(px, py, pz)));
                                bs = cradle.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty getip6 ? cradle.getValue(getip6) : -1;
                                if (cradle.getBlock() == CABlocks.TIDEWAY_CRADLE.get()) {
                                    if (bs == 0) {
                                        {
                                            int value = 1;
                                            BlockPos pos = BlockPos.containing(px, py, pz);
                                            BlockState blockState = world.getBlockState(pos);
                                            if (blockState.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                                                world.setBlock(pos, blockState.setValue(integerProp, value), 3);
                                        }
                                        if (Math.random() < 0.05) {
                                            if ((LevelAccessor) world instanceof ServerLevel level) {
                                                Entity entityToSpawn = CAEntities.LINGERING_PATHSHAPER.get().spawn(level, BlockPos.containing(px, py + 1, pz), MobSpawnType.MOB_SUMMONED);
                                                if (entityToSpawn != null) {
                                                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                                }
                                            }
                                        } else {
                                            if ((LevelAccessor) world instanceof ServerLevel level) {
                                                Entity entityToSpawn = CAEntities.ROUTE_SHAPER.get().spawn(level, BlockPos.containing(px, py + 1, pz), MobSpawnType.MOB_SUMMONED);
                                                if (entityToSpawn != null) {
                                                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                                }
                                            }
                                        }
                                        {
                                            if (world instanceof ServerLevel _level) {
                                                itemstack.hurtAndBreak(1, _level, null, _item -> itemstack.setDamageValue(0));
                                            }
                                        }
                                        if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                                            player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_15").getString())), false);
                                        if ((Entity) entity instanceof Player player) {
                                            ItemStack stktoremove = new ItemStack(CAItems.WHIRL_EYE.get());
                                            player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                                        }
                                        found = true;
                                    }
                                } else if (cradle.getBlock() == CABlocks.ENDSPEAKER_NEST.get()) {
                                    {
                                        if (world instanceof ServerLevel _level) {
                                            itemstack.hurtAndBreak(1, _level, null, _item -> itemstack.setDamageValue(0));
                                        }
                                    }
                                    world.destroyBlock(BlockPos.containing(px, py, pz), false);
                                    if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                                        player.displayClientMessage(Component.literal((Component.translatable("spawn.endspeaker").getString())), false);
                                    if ((Entity) entity instanceof Player player) {
                                        ItemStack stktoremove = new ItemStack(CAItems.WHIRL_EYE.get());
                                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                                    }
                                    if ((LevelAccessor) world instanceof ServerLevel level) {
                                        EndspeakerEntity.spawnForPhase(level, BlockPos.containing(px, py, pz), MobSpawnType.MOB_SUMMONED, 0);
                                    }
                                } else if (cradle.getBlock() == CABlocks.MIZUKI_STATUE.get()) {
                                    if (bs == 0) {
                                        {
                                            int value = 1;
                                            BlockPos pos = BlockPos.containing(px, py, pz);
                                            BlockState blockState = world.getBlockState(pos);
                                            if (blockState.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                                                world.setBlock(pos, blockState.setValue(integerProp, value), 3);
                                        }
                                        if (world instanceof ServerLevel _level) {
                                            itemstack.hurtAndBreak(1, _level, null, _item -> itemstack.setDamageValue(0));
                                        }
                                        if ((LevelAccessor) world instanceof ServerLevel level) {
                                            Entity entityToSpawn = CAEntities.IZUMIK.get().spawn(level, BlockPos.containing(px, py, pz), MobSpawnType.MOB_SUMMONED);
                                            if (entityToSpawn != null) {
                                                entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                            }
                                        }
                                        if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                                            player.displayClientMessage(Component.literal((Component.translatable("spawn.izumik.init").getString())), false);
                                        if ((Entity) entity instanceof Player player) {
                                            ItemStack stktoremove = new ItemStack(CAItems.WHIRL_EYE.get());
                                            player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                                        }
                                        found = true;
                                    }
                                } else if (cradle.getBlock() == CABlocks.HIGHMORE_SPAWNBLOCK.get()) {
                                    world.setBlock(BlockPos.containing(px, py, pz), CABlocks.HIGHMORE_SPAWNING_BLOCK.get().defaultBlockState(), 3);
                                    {
                                        if (world instanceof ServerLevel _level) {
                                            itemstack.hurtAndBreak(1, _level, null, _item -> itemstack.setDamageValue(0));
                                        }
                                    }
                                    if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                                        player.displayClientMessage(Component.literal((Component.translatable("spawn.highmore").getString())), false);
                                    if ((Entity) entity instanceof Player player) {
                                        ItemStack stktoremove = new ItemStack(CAItems.WHIRL_EYE.get());
                                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                                    }
                                    found = true;
                                } else if (cradle.getBlock() == CABlocks.TIDE_BISHOP_CORE.get()) {
                                    double hdns;
                                    for (int dx = -1; dx <= 1; dx++) {
                                        for (int dy = -1; dy <= 5; dy++) {
                                            for (int dz = -1; dz <= 1; dz++) {
                                                hdns = (world.getBlockState(BlockPos.containing(px + dx, py + dy, pz + dz))).getDestroySpeed(world, BlockPos.containing(0, 0, 0));
                                                if (hdns <= 6 && hdns > 0) {
                                                    world.destroyBlock(BlockPos.containing(px + dx, py + dy, pz + dz), false);
                                                }
                                            }
                                        }
                                    }
                                    if ((LevelAccessor) world instanceof ServerLevel level) {
                                        Entity entityToSpawn = CAEntities.TIDE_BISHOP.get().spawn(level, BlockPos.containing(px + 0.5, py, pz + 0.5), MobSpawnType.MOB_SUMMONED);
                                        if (entityToSpawn != null) {
                                            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                        }
                                    }
                                    {
                                        if (world instanceof ServerLevel _level) {
                                            itemstack.hurtAndBreak(1, _level, null, _item -> itemstack.setDamageValue(0));
                                        }
                                    }
                                    if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                                        player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_16").getString())), false);
                                    if ((Entity) entity instanceof Player player) {
                                        ItemStack stktoremove = new ItemStack(CAItems.WHIRL_EYE.get());
                                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                                    }
                                    world.destroyBlock(BlockPos.containing(px, py, pz), false);
                                    world.setBlock(BlockPos.containing(px, py - 2, pz), CABlocks.TIDE_BISHOP_CORE_EMPTY.get().defaultBlockState(), 3);
                                    found = true;
                                } else if (cradle.getBlock() == CABlocks.UNDERTIDE_TABLE.get()) {
                                    if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                                        player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.a_second_key.description_3").getString())), false);
                                    if ((Entity) entity instanceof Player player) {
                                        ItemStack stktoremove = new ItemStack(CAItems.WHIRL_EYE.get());
                                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                                    }
                                    {
                                        BlockPos bp = BlockPos.containing(px, py, pz);
                                        BlockState blockState = CABlocks.UNDERTIDE_SPAWN.get().withPropertiesOf(world.getBlockState(bp));
                                        world.setBlock(bp, blockState, 3);
                                    }
                                    found = true;
                                    if ((LevelAccessor) world instanceof ServerLevel level) {
                                        Entity entityToSpawn = CAEntities.BISHOP_FISH.get().spawn(level, BlockPos.containing(px + 0.5, py, pz + 0.5), MobSpawnType.MOB_SUMMONED);
                                        if (entityToSpawn != null) {
                                            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
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