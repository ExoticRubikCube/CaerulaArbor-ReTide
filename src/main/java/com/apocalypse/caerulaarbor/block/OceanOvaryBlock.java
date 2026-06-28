package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.configuration.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.init.CaerulaArborModGameRules;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.procedures.SummonRandomSeabornProcedure;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

public class OceanOvaryBlock extends AbstractOvaryBlock {

	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");

	public OceanOvaryBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.SCULK_SENSOR).strength(6f, 18f).lightLevel(s -> (new Object() {
			public int getLightLevel() {
				if (s.getValue(BLOCKSTATE) == 1)
					return 0;
				return 4;
			}
		}.getLightLevel())).requiresCorrectToolForDrops().speedFactor(0.9f).jumpFactor(0.9f).noOcclusion().pushReaction(PushReaction.BLOCK).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true)
				.isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OUTPUT, 0).setValue(POWERED, false).setValue(WATERLOGGED, false));
	}

	@Override
	protected int getTickDelay() {
		return 40;
	}

	@Override
	protected String getDescriptionKey() {
		return "block.caerula_arbor.ocean_ovary.description_0";
	}

	@Override
	protected double getDestroySpawnRate() {
		return 0.05;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (state.getValue(BLOCKSTATE) == 1) {
			return BASE_SHAPE;
		}
		return FULL_SHAPE;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, OUTPUT, POWERED, WATERLOGGED, BLOCKSTATE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(POWERED, false);
	}

	@Override
	public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
		super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
		if (world.getBestNeighborSignal(pos) > 0) {
			double x = pos.getX();
			double y = pos.getY();
			double z = pos.getZ();
			{
				BlockPos _pos = BlockPos.containing(x, y, z);
				BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
				if (_bs.getBlock().getStateDefinition().getProperty("powered") instanceof BooleanProperty _booleanProp)
					((LevelAccessor) world).setBlock(_pos, _bs.setValue(_booleanProp, true), 3);
			}
			{
				int _value = 0;
				BlockPos _pos = BlockPos.containing(x, y, z);
				BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
				if (_bs.getBlock().getStateDefinition().getProperty("output") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
					((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
			}
			{
				int _value = 0;
				BlockPos _pos = BlockPos.containing(x, y, z);
				BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
				if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
					((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
			}
		} else {
			double x = pos.getX();
			double y = pos.getY();
			double z = pos.getZ();
			{
				BlockPos _pos = BlockPos.containing(x, y, z);
				BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
				if (_bs.getBlock().getStateDefinition().getProperty("powered") instanceof BooleanProperty _booleanProp)
					((LevelAccessor) world).setBlock(_pos, _bs.setValue(_booleanProp, false), 3);
			}
		}
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		boolean finished = false;
		double chance = 0;
		double rate = 0;
		if (!(world.getDifficulty() == Difficulty.PEACEFUL)) {
			if (!(world.getBlockFloorHeight(BlockPos.containing(x, (double) y + 1, z)) > 0) && !(world.getBlockFloorHeight(BlockPos.containing(x, (double) y + 2, z)) > 0)) {
				if (blockstate.getValue(BLOCKSTATE) == 0) {
					rate = 0.05;
					if (CaerulaArborModVariables.MapVariables.get(world).strategy_breed >= 2) {
						rate = 0.08;
					}
					if (CaerulaArborModVariables.MapVariables.get(world).strategy_breed >= 4) {
						rate = 0.1;
					}
					chance = blockstate.getValue(OUTPUT);
					if (Math.random() < chance * 0.005) {
						if (EntityUtils.getSeabornNum(world, x, y, z) >= Math.min((double) CaerulaConfigsConfiguration.CLONE_NUM.get(), (((LevelAccessor) world).getLevelData().getGameRules().getInt(CaerulaArborModGameRules.CLONE_NUMBER_LIMIT)))) {
							finished = true;
						} else {
							com.apocalypse.caerulaarbor.utils.WorldUtils.summonRandomSeaborn(world, rate, (double) x + 0.5, (double) y + 1.5, (double) z + 0.5);
							if (!blockstate.getValue(POWERED)) {
								{
									int _value = 1;
									BlockPos _pos = BlockPos.containing(x, y, z);
									BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
									if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
										((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
								}
							}
							{
								int _value = 0;
								BlockPos _pos = BlockPos.containing(x, y, z);
								BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
								if (_bs.getBlock().getStateDefinition().getProperty("output") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
									((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
							}
						}
					} else {
						{
							int _value = (int) (chance + 1);
							BlockPos _pos = BlockPos.containing(x, y, z);
							BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
							if (_bs.getBlock().getStateDefinition().getProperty("output") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
								((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
						}
					}
				}
				if (!finished) {
					if (blockstate.getValue(POWERED)) {
						{
							int _value = 0;
							BlockPos _pos = BlockPos.containing(x, y, z);
							BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
							if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
								((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
						}
					}
				}
			}
		}
		world.scheduleTick(pos, this, 40);
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
			ItemStack fed = ItemStack.EMPTY;
			if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "fish_food")))) {
				fed = ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
			} else if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "fish_food")))) {
				fed = ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).copy();
			}
			if (fed.is(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "fish_food")))) {
				if (blockstate.getValue(BLOCKSTATE) == 1) {
					{
						int _value = 0;
						BlockPos _pos = BlockPos.containing(x, y, z);
						BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
						if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
							((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
					}
					{
						int _value = 0;
						BlockPos _pos = BlockPos.containing(x, y, z);
						BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
						if (_bs.getBlock().getStateDefinition().getProperty("output") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
							((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
					}
					if ((LevelAccessor) world instanceof Level _level) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.panda.eat")), SoundSource.BLOCKS, (float) 0.95, 1);
					}
					fed.shrink(1);
					result = InteractionResult.SUCCESS;
				}
			}
		}
		return result;
	}
}