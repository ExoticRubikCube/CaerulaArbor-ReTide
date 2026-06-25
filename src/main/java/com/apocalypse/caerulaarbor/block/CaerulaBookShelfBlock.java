
package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.entity.MartusEntity;
import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CaerulaBookShelfBlock extends Block {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public CaerulaBookShelfBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(7f, 24f));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, BlockGetter level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("block.caerula_arbor.caerula_book_shelf.description_0"));
		list.add(Component.translatable("block.caerula_arbor.caerula_book_shelf.description_1"));
		list.add(Component.translatable("block.caerula_arbor.caerula_book_shelf.description_2"));
		list.add(Component.translatable("block.caerula_arbor.caerula_book_shelf.description_3"));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos) {
		return 4f;
	}

	@Override
	public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		super.use(blockstate, world, pos, entity, hand, hit);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		double hitX = hit.getLocation().x;
		double hitY = hit.getLocation().y;
		double hitZ = hit.getLocation().z;
		Direction direction = hit.getDirection();
        InteractionResult result = InteractionResult.PASS;
        if (entity != null) {
            double num = 0;
            BlockState tgt = Blocks.AIR.defaultBlockState();
            if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.WHIRL_EYE.get()) {
                if (world.getEntitiesOfClass(MartusEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).isEmpty()) {
                    for (int index0 = 0; index0 < 3; index0++) {
                        for (int index1 = 0; index1 < 3; index1++) {
                            for (int index2 = 0; index2 < 3; index2++) {
                                tgt = (((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + index0 - 1, (double) y + index1 - 1, (double) z + index2 - 1)));
                                if (tgt.getBlock() == CaerulaArborModBlocks.CAERULA_BOOK_SHELF.get()) {
                                    num++;
                                }
                                if (num >= 9) {
                                    break;
                                }
                            }
                            if (num >= 9) {
                                break;
                            }
                        }
                        if (num >= 9) {
                            break;
                        }
                    }
                    if (num >= 9) {
                        for (int index3 = 0; index3 < 3; index3++) {
                            for (int index4 = 0; index4 < 3; index4++) {
                                for (int index5 = 0; index5 < 3; index5++) {
                                    tgt = (((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + index3 - 1, (double) y + index4 - 1, (double) z + index5 - 1)));
                                    if (tgt.getBlock() == CaerulaArborModBlocks.CAERULA_BOOK_SHELF.get()) {
                                        world.destroyBlock(BlockPos.containing((double) x + index3 - 1, (double) y + index4 - 1, (double) z + index5 - 1), false);
                                        num--;
                                    }
                                    if (num <= 0) {
                                        break;
                                    }
                                }
                                if (num <= 0) {
                                    break;
                                }
                            }
                            if (num <= 0) {
                                break;
                            }
                        }
                        if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                            _player.displayClientMessage(Component.literal((Component.translatable("spawn.martus").getString())), false);
                        ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                        if ((LevelAccessor) world instanceof ServerLevel _level) {
                            Entity entityToSpawn = CaerulaArborModEntities.MARTUS.get().spawn(_level, BlockPos.containing((double) x + 0.5, y, (double) z + 0.5), MobSpawnType.MOB_SUMMONED);
                            if (entityToSpawn != null) {
                                entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                            }
                        }
                        result = InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return result;
	}
}
