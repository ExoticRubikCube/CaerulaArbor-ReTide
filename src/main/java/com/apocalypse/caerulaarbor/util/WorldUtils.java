package com.apocalypse.caerulaarbor.util;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.config.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.entity.Al1SHelperEntity;
import com.apocalypse.caerulaarbor.entity.LittleHelperEntity;
import com.apocalypse.caerulaarbor.entity.OceanizedWitherEntity;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.procedures.SummonEliteFishProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WorldUtils {
	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] WATER_NORMAL_POOL = {
		CAEntities.COLLECTOR_PROKARYOTE,
		CAEntities.FLOATER_PROKARYOTE,
		CAEntities.DEPOSITER_PROKARYOTE,
		CAEntities.ACCUMULATOR_PROKARYOTE,
		CAEntities.FEEDER_PROKARYOTE,
		CAEntities.BONE_FISH
	};

	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] LAND_NORMAL_POOL = {
		CAEntities.CHISELER_FISH,
		CAEntities.FLY_FISH,
		CAEntities.PREDATOR_ABYSSAL,
		CAEntities.FAKE_OFFSPRING,
		CAEntities.SLIDER_FISH,
		CAEntities.RUN_FISH,
		CAEntities.SHOOTER_FISH,
		CAEntities.SPLASHER_ABYSSAL
	};

	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] OCEANIZED_ANIMAL_POOL = {
		CAEntities.OCEANIZED_PIG,
		CAEntities.OCEANIZED_COW,
		CAEntities.OCEANIZED_SHEEP,
		CAEntities.OCEANIZED_HORSE,
		CAEntities.OCEANIZED_WOLF,
		CAEntities.OCEANIZED_SPIDER,
		CAEntities.OCEANIZED_VILLAGER,
		CAEntities.OCEANIZED_WITCH,
		CAEntities.OCEANIZED_FOX,
		CAEntities.OCEANIZED_POLAR_BEAR,
		CAEntities.OCEANIZE_RABBIT,
		CAEntities.OCEANIZED_CAT,
		CAEntities.OCEANIZED_CHICKEN
	};

	private WorldUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	//TODO可疑,或许可以下放到基类
	public static InteractionResult convertToOceanFarmland(LevelAccessor world, BlockPos pos, BlockState blockstate, Entity entity) {
		if (entity == null)
			return InteractionResult.PASS;
		if (entity.isShiftKeyDown() && blockstate.getBlock() == Blocks.FARMLAND) {
			BlockState _bs = CABlocks.OCEAN_FARMLAND.get().withPropertiesOf(blockstate);
			world.setBlock(pos, _bs, 3);
		}
		return InteractionResult.SUCCESS;
	}

	//或许可以使用基类或接口
	public static void addGrowAge(LevelAccessor world, BlockPos pos, BlockState blockstate) {
		if ((blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip1 ? blockstate.getValue(_getip1) : -1) < 30) {
			int value = (blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip3 ? blockstate.getValue(_getip3) : -1) + 8;
			BlockState _bs = world.getBlockState(pos);
			if (_bs.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
				world.setBlock(pos, _bs.setValue(integerProp, value), 3);
		}
	}

	//可疑
	public static void burndownTrail(LevelAccessor world, BlockState toBeBurn, double px, double py, double pz) {
		BlockState output = Blocks.AIR.defaultBlockState();
		boolean success = false;
		boolean watered;
		if (toBeBurn.getBlock() == CABlocks.SEA_TRAIL_INIT.get() || toBeBurn.getBlock() == CABlocks.SEA_TRAIL_GROWING.get()) {
			output = Blocks.AIR.defaultBlockState();
			success = true;
		} else if (toBeBurn.getBlock() == CABlocks.SEA_TRAIL_GROWN.get() || toBeBurn.getBlock() == CABlocks.SEA_TRAIL_STOP.get()) {
			output = (new Object() {
				public BlockState with(BlockState _bs, String _property, int _newValue) {
					Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty(_property);
					return _prop instanceof IntegerProperty _ip && _prop.getPossibleValues().contains(_newValue) ? _bs.setValue(_ip, _newValue) : _bs;
				}
			}.with(CABlocks.SEA_TRAIL_BURNT.get().defaultBlockState(), "longevity", toBeBurn.getBlock().getStateDefinition().getProperty("longevity") instanceof IntegerProperty _getip4 ? toBeBurn.getValue(_getip4) : -1));
			success = true;
		} else if (toBeBurn.getBlock() == CABlocks.SEA_TRAIL_SOLID.get() || toBeBurn.getBlock() == CABlocks.TRAIL_PULSE.get()) {
			output = CABlocks.SEA_TRAIL_BURNT_SOLID.get().defaultBlockState();
			success = true;
		}
		watered = toBeBurn.getBlock().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _getbp8 && toBeBurn.getValue(_getbp8);
		if (success) {
			if (output.getBlock() == Blocks.AIR) {
				if (watered) {
					{
						BlockPos _bp = BlockPos.containing(px, py, pz);
						BlockState _bso = world.getBlockState(_bp);
						BlockState _bs = Blocks.WATER.withPropertiesOf(_bso);
						world.setBlock(_bp, _bs, 3);
					}
				} else {
					world.setBlock(BlockPos.containing(px, py, pz), Blocks.AIR.defaultBlockState(), 3);
				}
			} else {
				{
					BlockPos _bp = BlockPos.containing(px, py, pz);
					BlockState _bso = world.getBlockState(_bp);
					BlockState _bs = output.getBlock().withPropertiesOf(_bso);
					if (output.hasProperty(BlockStateProperties.WATERLOGGED) && _bs.hasProperty(BlockStateProperties.WATERLOGGED))
						_bs = _bs.setValue(BlockStateProperties.WATERLOGGED, output.getValue(BlockStateProperties.WATERLOGGED));
					if (output.getBlock().getStateDefinition().getProperty("longevity") instanceof IntegerProperty _integerProp && _bs.hasProperty(_integerProp))
						_bs = _bs.setValue(_integerProp, output.getValue(_integerProp));
					world.setBlock(_bp, _bs, 3);
				}
			}
			if (world instanceof Level _level) {
					_level.playSound(null, BlockPos.containing(px, py, pz), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.fire.extinguish")), SoundSource.BLOCKS, (float) 0.6, 1);
			}
		}
	}

	//下放或使用基类或接口
	public static boolean canLilyExist(LevelAccessor world, double x, double y, double z) {
		return world.getBlockState(BlockPos.containing(x, y - 1, z)).isFaceSturdy(world, BlockPos.containing(x, y - 1, z), Direction.UP);
	}

	//或许可以使用基类或接口
	public static boolean canPutTrail(LevelAccessor world, double x, double y, double z) {
		return (world.getBlockState(BlockPos.containing(x, y - 1, z)).isFaceSturdy(world, BlockPos.containing(x, y - 1, z), Direction.UP)
				|| (world.getBlockState(BlockPos.containing(x, y - 1, z))).is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "trail_existable"))))
				&& !((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == CABlocks.SEA_TRAIL_SOLID.get());
	}

	//TODO:下放,应该为两个凋零制作一个共同的基类，然后置入那里,其他两个凋零都调用的utils方法同理
	public static boolean checkTShape(LevelAccessor world, double x, double y, double z, BlockState target) {
		double direction = 0;
		boolean verticalMatched = false;
		boolean horizontalMatched = false;
		if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == target.getBlock() && (world.getBlockState(BlockPos.containing(x, y - 2, z))).getBlock() == target.getBlock()) {
			verticalMatched = true;
		}
		if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z))).getBlock() == target.getBlock() && (world.getBlockState(BlockPos.containing(x + 1, y - 1, z))).getBlock() == target.getBlock()
				&& (world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == Blocks.WITHER_SKELETON_SKULL && (world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == Blocks.WITHER_SKELETON_SKULL) {
			horizontalMatched = true;
			direction = 0;
		} else if ((world.getBlockState(BlockPos.containing(x, y - 1, z - 1))).getBlock() == target.getBlock() && (world.getBlockState(BlockPos.containing(x, y - 1, z + 1))).getBlock() == target.getBlock()
				&& (world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == Blocks.WITHER_SKELETON_SKULL && (world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == Blocks.WITHER_SKELETON_SKULL) {
			horizontalMatched = true;
			direction = 1;
		}
		if (horizontalMatched && verticalMatched) {
			world.destroyBlock(BlockPos.containing(x, y, z), false);
			world.destroyBlock(BlockPos.containing(x, y - 1, z), false);
			world.destroyBlock(BlockPos.containing(x, y - 2, z), false);
			if (direction == 0) {
				world.destroyBlock(BlockPos.containing(x - 1, y, z), false);
				world.destroyBlock(BlockPos.containing(x + 1, y, z), false);
				world.destroyBlock(BlockPos.containing(x - 1, y - 1, z), false);
				world.destroyBlock(BlockPos.containing(x + 1, y - 1, z), false);
			} else {
				world.destroyBlock(BlockPos.containing(x, y, z - 1), false);
				world.destroyBlock(BlockPos.containing(x, y, z + 1), false);
				world.destroyBlock(BlockPos.containing(x, y - 1, z - 1), false);
				world.destroyBlock(BlockPos.containing(x, y - 1, z + 1), false);
			}
			return true;
		}
		return false;
	}

	//可疑
	public static void clearNetherseaAround(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		BlockState target;
		boolean canBreak;
		boolean mayDrop;
		double px;
		double pz;
		double py;
		double dur;
		if (entity.tickCount % 30 == 5) {
			if (entity instanceof LittleHelperEntity) {
				dur = entity instanceof LittleHelperEntity _datEntI ? _datEntI.getEntityData().get(LittleHelperEntity.DATA_durability) : 0;
			} else {
				dur = entity instanceof Al1SHelperEntity _datEntI ? _datEntI.getEntityData().get(Al1SHelperEntity.DATA_durability) : 0;
			}
			if (dur < 4) {
				dur = dur + 1;
				if (entity instanceof LittleHelperEntity _datEntSetI)
					_datEntSetI.getEntityData().set(LittleHelperEntity.DATA_durability, (int) dur);
				if (entity instanceof Al1SHelperEntity _datEntSetI)
					_datEntSetI.getEntityData().set(Al1SHelperEntity.DATA_durability, (int) dur);
			}
		}
		if (!entity.isVehicle()) {
			return;
		}
		for (int index0 = 0; index0 < 3; index0++) {
			for (int index1 = 0; index1 < 2; index1++) {
				for (int index2 = 0; index2 < 3; index2++) {
					px = index0 - 1 + x;
					py = index1 + y;
					pz = index2 - 1 + z;
					target = world.getBlockState(BlockPos.containing(px, py, pz));
					canBreak = false;
					mayDrop = false;
					if (target.is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "trail")))) {
						canBreak = true;
					} else if (target.getBlock() == CABlocks.OCEAN_OVARY.get()) {
						canBreak = true;
						mayDrop = true;
					} else if (target.canBeReplaced()) {
						canBreak = true;
						mayDrop = true;
					}
					if (canBreak) {
						if (mayDrop) {
							{
								BlockPos _pos = BlockPos.containing(px, py, pz);
								net.minecraft.world.level.block.Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(px + 0.5, py + 0.5, pz + 0.5), null);
								world.destroyBlock(_pos, false);
							}
						} else {
							world.destroyBlock(BlockPos.containing(px, py, pz), false);
						}
					}
				}
			}
		}
	}

	//可以安排到那个BaseSeaborn
	public static boolean canCommonSeabornSpawn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation(CaerulaArborMod.MODID, "common_spawn_biome")))) {
			return false;
		}
		if (Math.random() * 100 < (world.getLevelData().getGameRules().getInt(CAGameRules.SEABORN_SPAWN_RATE))) {
			if (world.getDifficulty() == Difficulty.PEACEFUL) {
				return false;
			}
			if (Math.random() < 0.8) {
				return world.getBrightness(LightLayer.SKY, BlockPos.containing(x, y, z)) >= 14 && world.getBrightness(LightLayer.BLOCK, BlockPos.containing(x, y, z)) < 3;
			}
			return world.getBrightness(LightLayer.SKY, BlockPos.containing(x, y, z)) >= 10 && world.getBrightness(LightLayer.BLOCK, BlockPos.containing(x, y, z)) < 3;
		}
		return false;
	}

	//或许放到其他util比较好?可以专门制作一个海嗣util
	public static boolean canDangerSeabornSpawn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation(CaerulaArborMod.MODID, "danger_spawn_biome")))) {
			return false;
		}
		if (Math.random() * 100 < (world.getLevelData().getGameRules().getInt(CAGameRules.SEABORN_SPAWN_RATE))) {
			if (world.getDifficulty() == Difficulty.PEACEFUL) {
				return false;
			}
			if (Math.random() < 0.8) {
				return world.getBrightness(LightLayer.SKY, BlockPos.containing(x, y, z)) >= 14 && world.getBrightness(LightLayer.BLOCK, BlockPos.containing(x, y, z)) < 3;
			}
			return world.getBrightness(LightLayer.SKY, BlockPos.containing(x, y, z)) >= 10 && world.getBrightness(LightLayer.BLOCK, BlockPos.containing(x, y, z)) < 3;
		}
		return false;
	}

	//可疑
	public static boolean canRareSeabornSpawn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation(CaerulaArborMod.MODID, "rare_spawn_biome")))) {
			return false;
		}
		if (Math.random() * 100 < (world.getLevelData().getGameRules().getInt(CAGameRules.SEABORN_SPAWN_RATE))) {
			if (world.getDifficulty() == Difficulty.PEACEFUL) {
				return false;
			}
			if (Math.random() < 0.8) {
				return world.getBrightness(LightLayer.SKY, BlockPos.containing(x, y, z)) >= 14 && world.getBrightness(LightLayer.BLOCK, BlockPos.containing(x, y, z)) < 3;
			}
			return world.getBrightness(LightLayer.SKY, BlockPos.containing(x, y, z)) >= 10 && world.getBrightness(LightLayer.BLOCK, BlockPos.containing(x, y, z)) < 3;
		}
		return false;
	}

	//TODO:下放
	public static void corruptedSpawnMobs(LevelAccessor world, double x, double y, double z, double n) {
		double tx;
		double ty;
		double tz;
		double R;
		double T;
		if (EntityUtils.getSeabornNum(world, x, y, z) >= (world.getLevelData().getGameRules().getInt(CAGameRules.CLONE_NUMBER_LIMIT))) {
			return;
		}
		for (int index0 = 0; index0 < (int) n; index0++) {
			for (int index1 = 0; index1 < 8; index1++) {
				R = Mth.nextInt(RandomSource.create(), 4, 16);
				T = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				tx = x + R * Math.sin(T);
				tz = z + R * Math.cos(T);
				ty = findValidYForCat(world, x, y, z, tx, y, tz);
				if (ty < 999) {
					assert Boolean.TRUE;
					summonRandomSeaborn(world, 0.33, tx, ty, tz);
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.CLOUD, tx, (ty + 0.75), tz, 64, 0.75, 0.75, 0.75, 0.1);
					break;
				}
			}
		}
	}

	//可疑，为什么不放在其他util
	public static void dropRelicRoute(LevelAccessor world, double x, double y, double z) {
		if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
			if (!world.isClientSide() && world.getServer() != null) {
				for (ItemStack itemstackiterator : world.getServer().getLootData().getLootTable(new ResourceLocation(CaerulaArborMod.MODID, "gameplay/relic_route"))
						.getRandomItems(new LootParams.Builder((ServerLevel) world).create(LootContextParamSets.EMPTY))) {
					if (world instanceof ServerLevel _level) {
						ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, itemstackiterator);
						entityToSpawn.setPickUpDelay(10);
						entityToSpawn.setUnlimitedLifetime();
						_level.addFreshEntity(entityToSpawn);
					}
				}
			}
		}
	}

	//可疑
	public static boolean isDistFromGround(LevelAccessor world, double x, double y, double z) {
        if (y < -32) {
			return false;
		}
		for (int index0 = 0; index0 < 20; index0++) {
			if (!(world.isEmptyBlock(BlockPos.containing(x, y - index0 - 1, z)) || (world.getBlockState(BlockPos.containing(x, y - index0 - 1, z))).getBlock() instanceof LiquidBlock)) {
				return false;
			}
		}
		return true;
	}

	//TODO:或许可以下放
	public static void dropRelicTidebi(LevelAccessor world, double x, double y, double z) {
		if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
			if (!world.isClientSide() && world.getServer() != null) {
				for (ItemStack itemstackiterator : world.getServer().getLootData().getLootTable(new ResourceLocation(CaerulaArborMod.MODID, "gameplay/relic_tidebi"))
						.getRandomItems(new LootParams.Builder((ServerLevel) world).create(LootContextParamSets.EMPTY))) {
					if (world instanceof ServerLevel _level) {
						ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, itemstackiterator);
						entityToSpawn.setPickUpDelay(10);
						entityToSpawn.setUnlimitedLifetime();
						_level.addFreshEntity(entityToSpawn);
					}
				}
			}
		}
	}

	//还行，暂时不动代码本身，但是真的需要放在这里吗。。评估有没有更合适的
	public static void summonRandomSeaborn(LevelAccessor world, double eliteChance, double x, double y, double z) {
		if (Math.random() < eliteChance) {
			SummonEliteFishProcedure.execute(world, x, y, z);
		} else {
			if ((world.getFluidState(BlockPos.containing(x, y, z)).createLegacyBlock()).getBlock() == Blocks.WATER) {
				int rand = Mth.nextInt(RandomSource.create(), 0, WATER_NORMAL_POOL.length - 1);
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = ((EntityType<?>) WATER_NORMAL_POOL[rand].get()).spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			} else {
				int rand = Mth.nextInt(RandomSource.create(), 0, 8);
				if (rand < LAND_NORMAL_POOL.length) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = ((EntityType<?>) LAND_NORMAL_POOL[rand].get()).spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
						}
					}
				} else {
					int rand1 = Mth.nextInt(RandomSource.create(), 0, OCEANIZED_ANIMAL_POOL.length - 1);
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = ((EntityType<?>) OCEANIZED_ANIMAL_POOL[rand1].get()).spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
						}
					}
				}
			}
		}
	}

	//TODO:下放,应该为两个凋零制作一个共同的基类，然后置入那里
	public static void dropMoistStar(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
			if (world instanceof ServerLevel _level) {
				ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(CAItems.MOIST_STAR.get()));
				entityToSpawn.setPickUpDelay(10);
				entityToSpawn.setUnlimitedLifetime();
				_level.addFreshEntity(entityToSpawn);
			}
			for (int index0 = 0; index0 < 64; index0++) {
				if (world instanceof ServerLevel _level)
					_level.addFreshEntity(new ExperienceOrb(_level, (x + Mth.nextDouble(RandomSource.create(), -1, 1)), y, (z + Mth.nextDouble(RandomSource.create(), -1, 1)), Mth.nextInt(RandomSource.create(), 32, 48)));
			}
		}
		if (entity instanceof OceanizedWitherEntity) {
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = CAEntities.OCEANIZED_WITHERIA.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
		}
	}

	//TODO:下放回SuperBigCatEntity作为辅助方法并更新调用
	public static double findValidYForCat(LevelAccessor world, double x, double y, double z, double xx, double yy, double zz) {
		double y_found;
		if (world instanceof Level _level) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.azalea.hit")), SoundSource.NEUTRAL, 0, 1);
		}
        for (int index0 = 0; index0 < 12; index0++) {
			y_found = yy + index0;
			if (!(world.getBlockFloorHeight(BlockPos.containing(xx, y_found, zz)) > 0)) {
				return y_found;
			}
		}
		return 114514;
	}

	/**
	 * Finds the nearest valid standing Y around the given Y within a small vertical range.
	 */
	//可以，需要解释
	public static double findValidY(LevelAccessor world, double xx, double yy, double zz) {
		double yFound;
		for (int index0 = 0; index0 < 12; index0++) {
			yFound = yy + index0;
			if (isValidPlace(world, xx, yFound, zz)) {
				return yFound;
			}
			yFound = yy - index0 - 1;
			if (isValidPlace(world, xx, yFound, zz)) {
				return yFound;
			}
		}
		return 114514;
	}

	//或许可以下放或制作接口
	public static void playFractalSummonSound(LevelAccessor world, double x, double y, double z) {
		if (world instanceof Level _level) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.axolotl.splash")), SoundSource.HOSTILE, (float) 0.75, 1);
		}
	}

	//同，需要解释
	public static double findYzforTear(LevelAccessor world, double xx, double yy, double zz) {
		double y_found;
        for (int index0 = 0; index0 < 12; index0++) {
			y_found = yy + index0;
			if (world.isEmptyBlock(BlockPos.containing(xx, y_found, zz))) {
				return y_found;
			}
		}
		return 114514;
	}

	//TODO:之后也进行下放
	public static void shootWitherSkull(LevelAccessor world, Entity from, double a, double dx, double dy, double dz, double inaccu, double speed, double xx, double yy, double zz) {
		if (from == null)
			return;
		double ddx = 0;
		double ddy = 0;
		double ddz = 0;
		double module;
        module = Math.sqrt(dx * dx + dy * dy + dz * dz);
		if (module > 0) {
			ddx = (dx / module) * a;
			ddy = (dy / module) * a;
			ddz = (dz / module) * a;
		}
		CaerulaArborMod.queueServerWork(Mth.nextInt(RandomSource.create(), 0, 4), () -> {
			if (world instanceof Level _level) {
					_level.playSound(null, BlockPos.containing(xx, yy, zz), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wither.shoot")), SoundSource.HOSTILE, (float) 0.85, 1);
			}
		});
		if (world instanceof ServerLevel projectileLevel) {
			Projectile _entityToSpawn = new Object() {
				public Projectile getFireball(Level level, Entity shooter, double ax, double ay, double az) {
					AbstractHurtingProjectile entityToSpawn = new WitherSkull(EntityType.WITHER_SKULL, level);
					entityToSpawn.setOwner(shooter);
					entityToSpawn.xPower = ax;
					entityToSpawn.yPower = ay;
					entityToSpawn.zPower = az;
					return entityToSpawn;
				}
			}.getFireball(projectileLevel, from, ddx, ddy, ddz);
			_entityToSpawn.setPos(xx, yy, zz);
			_entityToSpawn.shoot(dx, dy, dz, (float) speed, (float) inaccu);
			projectileLevel.addFreshEntity(_entityToSpawn);
		}
	}

	//TODO:下放
	public static void ireneBurnBrandAround(LevelAccessor world, double x, double y, double z) {
		BlockState toBeBurn;
		double px;
		double py;
		double pz;
		for (int index0 = 0; index0 < 3; index0++) {
			for (int index1 = 0; index1 < 3; index1++) {
				for (int index2 = 0; index2 < 3; index2++) {
					px = x + index0 - 1;
					py = y + index1 - 1;
					pz = z + index2 - 1;
					toBeBurn = (world.getBlockState(BlockPos.containing(px, py, pz)));
					if (toBeBurn.getBlock() == CABlocks.SEA_TRAIL_INIT.get() || toBeBurn.getBlock() == CABlocks.SEA_TRAIL_GROWING.get() || toBeBurn.getBlock() == CABlocks.SEA_TRAIL_GROWN.get()
							|| toBeBurn.getBlock() == CABlocks.SEA_TRAIL_STOP.get() || toBeBurn.getBlock() == CABlocks.SEA_TRAIL_SOLID.get() || toBeBurn.getBlock() == CABlocks.TRAIL_PULSE.get()) {
						burndownTrail(world, toBeBurn, px, py, pz);
						if (world instanceof ServerLevel _level)
							_level.sendParticles(CAParticleTypes.PURPLE_FLAME.get(), (x + 0.5), (y + 1), (z + 0.5), 16, 0.75, 0.75, 0.75, 0.15);
					}
				}
			}
		}
	}

	//需要评估然后添加文档注释解释作用
	public static boolean isOrganic(BlockState block) {
		if (block.getBlock() == CABlocks.TRAIL_PULSE.get() || block.getBlock() == CABlocks.TRAIL_LOG.get() || block.getBlock() == CABlocks.TRAIL_LEAVE.get()
				|| block.getBlock() == CABlocks.STRIPPED_TRAIL_LOG.get()) {
			return false;
		}
		if (block.is(BlockTags.create(new ResourceLocation("forge:phayrilesh"))) || block.is(BlockTags.create(new ResourceLocation("spore:fungal_blocks")))) {
			return Math.random() < 0.33;
		}
		return block.is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "organic")));
	}

	//同，需要注释
	public static boolean isValidPlace(LevelAccessor world, double xx, double yy, double zz) {
		if (world instanceof Level _level) {
			if (_level.isClientSide()) {
				_level.playLocalSound(xx, yy, zz, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.enderman.ambient")), SoundSource.HOSTILE, 0, 1, false);
			}
		}
		for (int dy = 0; dy <= 3; dy++) {
			if (world.getBlockFloorHeight(BlockPos.containing(xx, yy + dy, zz)) > 0) {
				return false;
			}
		}
		return true;
	}

	//TODO:可疑
	public static boolean isValidForMan(LevelAccessor world, double xx, double yy, double zz) {
		if (world instanceof Level _level) {
			if (_level.isClientSide()) {
				_level.playLocalSound(xx, yy, zz, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.enderman.ambient")), SoundSource.HOSTILE, 0, 1, false);
			}
		}
		for (int dy = 0; dy <= 2; dy++) {
			if (world.getBlockFloorHeight(BlockPos.containing(xx, yy + dy, zz)) > 0) {
				return false;
			}
		}
		return true;
	}

	//TODO:下放，但是需要先制作基类
	public static void witheriaDestroyBlocks(LevelAccessor world, double x, double y, double z) {
		boolean once = false;
		double dx;
		double dy;
		double dz;
		double hardness;
		BlockState block;
		if (canGrief(world)) {
			dx = -1;
			for (int index0 = 0; index0 < 3; index0++) {
				dz = -1;
				for (int index1 = 0; index1 < 3; index1++) {
					dy = 0;
					for (int index2 = 0; index2 < 2; index2++) {
						block = (world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz)));
						if (!block.is(BlockTags.create(new ResourceLocation("minecraft:wither_immnue")))) {
							hardness = block.getDestroySpeed(world, BlockPos.containing(0, 0, 0));
							if (hardness <= 7.5 && hardness >= 0 && world.getBlockFloorHeight(BlockPos.containing(x + dx, y + dy, z + dz)) > 0) {
								{
									BlockPos _pos = BlockPos.containing(x + dx, y + dy, z + dz);
									Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
									world.destroyBlock(_pos, false);
								}
								if (world instanceof Level _level)
									_level.updateNeighborsAt(BlockPos.containing(x + dx, y + dy, z + dz), _level.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz)).getBlock());
								once = true;
							}
						}
						dy = dy + 1;
					}
					dz = dz + 1;
				}
				dx = dx + 1;
			}
			if (once) {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wither.break_block")), SoundSource.NEUTRAL, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wither.break_block")), SoundSource.NEUTRAL, 1, 1, false);
					}
				}
			}
		}
	}

	//需要注释解释
	public static boolean canGrief(LevelAccessor world) {
		if (world.isClientSide()) {
			return false;
		}
		if (CaerulaConfigsConfiguration.BREAKABLE.get()) {
			return world.getLevelData().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
		}
		return false;
	}

	//需要注释解释，或许可以移动到别的util
	public static boolean canSpawnUnderwaterSeaborn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation(CaerulaArborMod.MODID, "underwater_spawn_biome")))) {
			return false;
		}
		if (Math.random() * 100 < (world.getLevelData().getGameRules().getInt(CAGameRules.SEABORN_SPAWN_RATE))) {
			return world.getDifficulty() != Difficulty.PEACEFUL;
		}
		return false;
	}

	//同上
	public static boolean canSpawnMarineSeaborn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation(CaerulaArborMod.MODID, "marine_spawn_biome")))) {
			return false;
		}
		if (Math.random() * 100 < (world.getLevelData().getGameRules().getInt(CAGameRules.SEABORN_SPAWN_RATE))) {
			return world.getDifficulty() != Difficulty.PEACEFUL;
		}
		return false;
	}

}
