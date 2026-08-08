package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Comparator;
import java.util.List;


public class SeaPrairieBombBlock extends Block {
	public SeaPrairieBombBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.GLASS).strength(4.0f, 64.0f).lightLevel(s -> 8).requiresCorrectToolForDrops().noOcclusion());
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("block.caerula_arbor.sea_prairie_bomb.description_0"));
	}

	private boolean burnTrail(LevelAccessor world, BlockState toBeBurn, double px, double py, double pz) {
		BlockState output = Blocks.AIR.defaultBlockState();
		boolean success = false;
		boolean watered = false;
		if (toBeBurn.getBlock() == CABlocks.SEA_TRAIL_INIT.get() || toBeBurn.getBlock() == CABlocks.SEA_TRAIL_GROWING.get()) {
			output = Blocks.AIR.defaultBlockState();
			success = true;
		} else if (toBeBurn.getBlock() == CABlocks.SEA_TRAIL_GROWN.get() || toBeBurn.getBlock() == CABlocks.SEA_TRAIL_STOP.get()) {
			IntegerProperty longevityProp = (IntegerProperty) toBeBurn.getBlock().getStateDefinition().getProperty("longevity");
			int longevity = longevityProp != null ? toBeBurn.getValue(longevityProp) : -1;
			output = CABlocks.SEA_TRAIL_BURNT.get().defaultBlockState();
			if (longevity >= 0) {
				IntegerProperty outputLongevity = (IntegerProperty) output.getBlock().getStateDefinition().getProperty("longevity");
				if (outputLongevity != null && outputLongevity.getPossibleValues().contains(longevity)) {
					output = output.setValue(outputLongevity, longevity);
				}
			}
			success = true;
		} else if (toBeBurn.getBlock() == CABlocks.SEA_TRAIL_SOLID.get() || toBeBurn.getBlock() == CABlocks.TRAIL_PULSE.get()) {
			output = CABlocks.SEA_TRAIL_BURNT_SOLID.get().defaultBlockState();
			success = true;
		}
		Property<?> waterloggedProp = toBeBurn.getBlock().getStateDefinition().getProperty("waterlogged");
		if (waterloggedProp instanceof BooleanProperty) {
			watered = toBeBurn.getValue((BooleanProperty) waterloggedProp);
		}
		if (success) {
			BlockPos pos = BlockPos.containing(px, py, pz);
			if (output.getBlock() == Blocks.AIR) {
				if (watered) {
					world.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
				} else {
					world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				}
			} else {
				world.setBlock(pos, output, 3);
			}
			if (world instanceof Level level) {
				level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.6f, 1.0f);
			}
		}
		return success;
	}

	private boolean triggerChainReaction(LevelAccessor world, double x, double y, double z, boolean burn, double iter) {
		BlockState target = world.getBlockState(BlockPos.containing(x, y, z));
		boolean worked = false;
		if (target.getBlock() == CABlocks.OCEAN_OVARY.get() || target.getBlock() == CABlocks.RED_OVARY.get()) {
			BlockPos pos = BlockPos.containing(x, y, z);
			Block.dropResources(world.getBlockState(pos), world, BlockPos.containing(x + 0.5, y, z + 0.5), null);
			world.destroyBlock(pos, false);
			worked = true;
		} else if (target.getBlock() == CABlocks.NETHERSEA_WOOD.get() || target.getBlock() == CABlocks.STRIPPED_NETHERSEA_WOOD.get() || target.getBlock() == CABlocks.TRAIL_LOG.get() || target.getBlock() == CABlocks.STRIPPED_TRAIL_LOG.get()) {
			world.destroyBlock(BlockPos.containing(x, y, z), false);
			worked = true;
		} else if (burn && this.burnTrail(world, target, x, y, z)) {
			worked = true;
		} else if (target.is(BlockTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "blow_up")))) {
			world.destroyBlock(BlockPos.containing(x, y, z), false);
			worked = true;
		}
		if (worked && iter > 0.0) {
			CaerulaArbor.queueServerWork(1, () -> {
				for (int index0 = 0; index0 < 3; ++index0) {
					for (int index1 = 0; index1 < 3; ++index1) {
						for (int index2 = 0; index2 < 3; ++index2) {
							if (index0 == 1 && index1 == 1 && index2 == 1) continue;
							triggerChainReaction(world, x + index0 - 1.0, y + index1 - 1.0, z + index2 - 1.0, burn, iter - 1.0);
						}
					}
				}
			});
		}
		return worked;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 0;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
		super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
		if (world.getBestNeighborSignal(pos) > 0) {
			double x = pos.getX();
			double y = pos.getY();
			double z = pos.getZ();
			boolean consume = false;
			for (Direction direction : Direction.values()) {
				if (this.triggerChainReaction(world, x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ(), false, 256.0)) {
					consume = true;
				}
			}
			if (consume) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
				if ((LevelAccessor) world instanceof Level level) {
					level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.BLOCKS, 3.0f, 1.0f);
				}
			} else {
				Entity player = world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 32.0, 32.0, 32.0), e -> true)
						.stream().min(Comparator.comparingDouble(e -> e.distanceToSqr(x, y, z)))
						.orElse(null);
				if (player instanceof Player p && !p.level().isClientSide()) {
					p.displayClientMessage(Component.translatable("block.caerula_arbor.sea_prairie_bomb.no_func"), true);
				}
			}
		}
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack itemstack, BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		super.useItemOn(itemstack, blockstate, world, pos, entity, hand, hit);
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();
        boolean shouldConsume = false;
		ItemStack mainHand = (Entity) entity instanceof LivingEntity living ? living.getMainHandItem() : ItemStack.EMPTY;
		if (mainHand.getItem() == CAItems.LANTERN_JUDGEMENT.get()) {
			for (Direction direction : Direction.values()) {
				if (this.triggerChainReaction(world, x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ(), true, 256.0)) {
					shouldConsume = true;
				}
			}
			if (shouldConsume) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
				if ((LevelAccessor) world instanceof Level level) {
					level.playSound(null, BlockPos.containing(x, y, z), CASounds.NOTICE.get(), SoundSource.BLOCKS, 3.0f, 1.0f);
				}
				ItemStack ist = entity.getMainHandItem();
				if (ist.getDamageValue() + 1 >= ist.getMaxDamage()) {
					ist.shrink(1);
					ist.setDamageValue(0);
				} else {
					ist.setDamageValue(ist.getDamageValue() + 1);
				}
				return ItemInteractionResult.SUCCESS;
			}
			if (!entity.level().isClientSide()) {
				entity.displayClientMessage(Component.translatable("block.caerula_arbor.sea_prairie_bomb.no_func"), true);
			}
			return ItemInteractionResult.FAIL;
		}
		if (mainHand.getItem() == Blocks.AIR.asItem()) {
			ItemStack offHand = (Entity) entity instanceof LivingEntity living ? living.getOffhandItem() : ItemStack.EMPTY;
			if (offHand.getItem() == Blocks.AIR.asItem() && (Entity) entity instanceof Player player && !player.level().isClientSide()) {
				player.displayClientMessage(Component.translatable("block.caerula_arbor.sea_prairie_bomb.note"), true);
			}
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}
}