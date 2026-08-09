
package com.susen36.caerulaarbor.block.doll;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.crawler.PocketSeaCreeperEntity;
import com.susen36.caerulaarbor.init.CABlockEntities;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class PocketSeaDollBlock extends SeaBornDollBlock<PocketSeaCreeperEntity> {
	public static final IntegerProperty DATA_ANIMATION = IntegerProperty.create("animation", 0, 2);

	public PocketSeaDollBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.WOOL).strength(0.5f, 2f).lightLevel(s -> 3).noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((bs, br, bp) -> false));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return CABlockEntities.POCKET_SEA_DOLL.get().create(blockPos, blockState);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return box(3, 0, 3, 13, 10, 13);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		builder.add(DATA_ANIMATION);
		super.createBlockStateDefinition(builder);
	}

	@Override
	protected float getSpawnScale() {
		return 0.6F;
	}

	@Override
	protected PocketSeaCreeperEntity createEntity(ServerLevel level) {
		return new PocketSeaCreeperEntity(CAEntities.POCKET_SEA_CREEPER.get(), level);
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack itemstack, BlockState blockstate, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		super.useItemOn(itemstack, blockstate, world, pos, player, hand, hit);
		ItemInteractionResult result = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		if (player.isHolding(Items.FLINT_AND_STEEL)) {
			world.playSound(null, BlockPos.containing(pos.getX(), pos.getY(), pos.getZ()), SoundEvents.CREEPER_PRIMED, SoundSource.BLOCKS, 1, 1);
			new Object() {
				void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
					world.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.8F, Level.ExplosionInteraction.NONE);
					final int tick2 = ticks;
					CaerulaArbor.queueServerWork(tick2, () -> {
						if (timedlooptotal > timedloopiterator + 1) {
							timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
						}
					});
				}
			}.timedLoop(0, 1, 10);
			world.destroyBlock(pos, false);
			result = ItemInteractionResult.SUCCESS;
		}
		return result;
	}
}