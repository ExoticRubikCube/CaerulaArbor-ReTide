
package com.susen36.caerulaarbor.block.doll;

import com.susen36.caerulaarbor.entity.UmbrellaAbyssalEntity;
import com.susen36.caerulaarbor.init.CABlockEntities;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class SwarmcallerDollBlock extends SeaBornDollBlock<UmbrellaAbyssalEntity> {
	public static final IntegerProperty DATA_ANIMATION = IntegerProperty.create("animation", 0, 1);

	public SwarmcallerDollBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.WOOL).strength(0.5f, 2f).lightLevel(s -> 13).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return CABlockEntities.SWARMCALLER_DOLL.get().create(blockPos, blockState);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return box(4, 0, 4, 12, 9, 12);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		builder.add(DATA_ANIMATION);
		super.createBlockStateDefinition(builder);
	}

	@Override
	protected float getSpawnScale() {
		return 0.65F;
	}

	@Override
	protected UmbrellaAbyssalEntity createEntity(ServerLevel level) {
		return new UmbrellaAbyssalEntity(CAEntities.UMBRELLA_ABYSSAL.get(), level);
	}
}