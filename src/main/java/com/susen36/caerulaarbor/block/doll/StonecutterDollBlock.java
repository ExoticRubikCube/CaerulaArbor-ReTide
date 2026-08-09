
package com.susen36.caerulaarbor.block.doll;

import com.susen36.caerulaarbor.entity.OceanStonecutteEntity;
import com.susen36.caerulaarbor.init.CABlockEntities;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class StonecutterDollBlock extends SeaBornDollBlock<OceanStonecutteEntity> {
	public static final IntegerProperty DATA_ANIMATION = IntegerProperty.create("animation", 0, 2);

	public StonecutterDollBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.WOOL).strength(0.5f, 2f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return CABlockEntities.STONECUTTER_DOLL.get().create(blockPos, blockState);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return box(5, 0, 5, 11, 6, 11);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		builder.add(DATA_ANIMATION);
		super.createBlockStateDefinition(builder);
	}

	@Override
	protected float getSpawnScale() {
		return 0.8F;
	}

	@Override
	protected OceanStonecutteEntity createEntity(ServerLevel level) {
		return new OceanStonecutteEntity(CAEntities.OCEAN_STONECUTTE.get(), level);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState blockstate, Level world, BlockPos pos, Player entity, BlockHitResult hit) {
		super.useWithoutItem(blockstate, world, pos, entity, hit);
		InteractionResult result;
		world.playSound(null, BlockPos.containing(pos.getX(), pos.getY(), pos.getZ()), SoundEvents.TURTLE_DEATH, SoundSource.BLOCKS, 0.8F, 0.95F);
		int value = 1;
		BlockState bs = world.getBlockState(pos);
		if (bs.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value)) {
			world.setBlock(pos, bs.setValue(integerProp, value), 3);
		}
		result = InteractionResult.SUCCESS;
		return result;
	}
}