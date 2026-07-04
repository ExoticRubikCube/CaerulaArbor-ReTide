package com.apocalypse.caerulaarbor.util;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.config.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.init.CABlocks;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAGameRules;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
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

	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] WATER_ELITE_POOL = {
		CAEntities.APOSTLE_PROKARYOTE,
		CAEntities.NUCLEIC_MALEFICENT
	};

	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] LAND_ELITE_POOL = {
		CAEntities.BASELAYER_ABYSSAL,
		CAEntities.CRACKER_ABYSSAL,
		CAEntities.CREEPER_FISH,
		CAEntities.GUIDE_ABYSSAL,
		CAEntities.PUNCTURE_FISH,
		CAEntities.REAPER_FISH,
		CAEntities.UMBRELLA_ABYSSAL,
		CAEntities.PREGNANT_FISH,
		CAEntities.FLEE_FISH,
		CAEntities.CHEST_FISH
	};

	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] OCEANIZED_ELITE_POOL = {
		CAEntities.OCEANIZED_VINDICATOR,
		CAEntities.OCEANIZED_EVOKER,
		CAEntities.OCEANIZED_RAVAGER,
		CAEntities.OCEANIZED_ENDERMAN,
		CAEntities.COMPASSION_PRAYER
	};

	private WorldUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * 按当前海嗣痕迹方块的生长规则提高其 {@code grow_age}。
	 *
	 * <p>该方法会直接读取目标位置上的方块状态；若该方块不存在 {@code grow_age}
	 * 整型属性，则不执行任何操作。当前实现仅在年龄小于 {@code 30} 时生效，
	 * 并尝试将其一次性增加 {@code 8}。只有当增加后的值仍属于该属性允许的取值范围时，
	 * 才会真正写回世界。
	 *
	 * @param world 世界
	 * @param pos 目标方块位置
	 */
	public static void addGrowAge(LevelAccessor world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (state.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty growAgeProperty) {
			int growAge = state.getValue(growAgeProperty);
			if (growAge >= 30) {
				return;
			}
			int nextGrowAge = growAge + 8;
			if (!growAgeProperty.getPossibleValues().contains(nextGrowAge)) {
				return;
			}
			world.setBlock(pos, state.setValue(growAgeProperty, nextGrowAge), 3);
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
					BlockPos _bp = BlockPos.containing(px, py, pz);
					BlockState _bso = world.getBlockState(_bp);
					BlockState _bs = Blocks.WATER.withPropertiesOf(_bso);
					world.setBlock(_bp, _bs, 3);
				} else {
					world.setBlock(BlockPos.containing(px, py, pz), Blocks.AIR.defaultBlockState(), 3);
				}
			} else {
				BlockPos _bp = BlockPos.containing(px, py, pz);
				BlockState _bso = world.getBlockState(_bp);
				BlockState _bs = output.getBlock().withPropertiesOf(_bso);
				if (output.hasProperty(BlockStateProperties.WATERLOGGED) && _bs.hasProperty(BlockStateProperties.WATERLOGGED))
					_bs = _bs.setValue(BlockStateProperties.WATERLOGGED, output.getValue(BlockStateProperties.WATERLOGGED));
				if (output.getBlock().getStateDefinition().getProperty("longevity") instanceof IntegerProperty _integerProp && _bs.hasProperty(_integerProp))
					_bs = _bs.setValue(_integerProp, output.getValue(_integerProp));
				world.setBlock(_bp, _bs, 3);
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

	/**
	 * 判断目标位置是否允许放置海嗣痕迹方块。
	 *
	 * <p>该方法检查目标位置正下方的方块：它的上表面必须能够承托方块，
	 * 或者被显式标记进 {@code trail_existable} 标签；同时该支撑方块不能是
	 * {@code SEA_TRAIL_SOLID}，以避免在实心海嗣痕迹上继续叠放普通痕迹。
	 *
	 * @param world 世界
	 * @param x 目标 X 坐标
	 * @param y 目标 Y 坐标
	 * @param z 目标 Z 坐标
	 * @return 若当前位置允许放置海嗣痕迹，则返回 {@code true}
	 */
	public static boolean canPutTrail(LevelAccessor world, double x, double y, double z) {
		BlockPos belowPos = BlockPos.containing(x, y - 1, z);
		BlockState belowState = world.getBlockState(belowPos);
		return (belowState.isFaceSturdy(world, belowPos, Direction.UP)
				|| belowState.is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "trail_existable"))))
				&& belowState.getBlock() != CABlocks.SEA_TRAIL_SOLID.get();
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
				ty = findValidSpawnY(world, x, y, z, tx, y, tz);
				if (!Double.isNaN(ty)) {
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

	/**
	 * 判断目标位置下方 20 格内是否不存在可作为地面的实心方块。
	 *
	 * <p>该方法会将空气和液体都视为“未接地”，因此可用于悬浮单位检测自己是否长期位于
	 * 深坑、水柱或其他无实心支撑的空间上方。
	 *
	 * @param world 世界
	 * @param x 目标 X 坐标
	 * @param y 目标 Y 坐标
	 * @param z 目标 Z 坐标
	 * @return 若下方 20 格内都没有实心地面，则返回 {@code true}
	 */
	public static boolean hasNoSolidGroundWithin20Below(LevelAccessor world, double x, double y, double z) {
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
			summonEliteSeaborn(world, x, y, z);
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

	public static void summonEliteSeaborn(LevelAccessor world, double x, double y, double z) {
		if ((world.getFluidState(BlockPos.containing(x, y, z)).createLegacyBlock()).getBlock() == Blocks.WATER) {
			int rand = Mth.nextInt(RandomSource.create(), 0, WATER_ELITE_POOL.length - 1);
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = ((EntityType<?>) WATER_ELITE_POOL[rand].get()).spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
		} else {
			int rand = Mth.nextInt(RandomSource.create(), 0, 8);
			if (Math.random() < 0.04) {
				rand = 9;
			}
			if (rand <= 9) {
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = ((EntityType<?>) LAND_ELITE_POOL[rand].get()).spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			} else {
				int rand1 = Mth.nextInt(RandomSource.create(), 0, OCEANIZED_ELITE_POOL.length - 1);
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = ((EntityType<?>) OCEANIZED_ELITE_POOL[rand1].get()).spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			}
		}
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.CLOUD, x, y, z, 32, 1, 1, 1, 0.1);
		if (world instanceof Level _level) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.phantom.swoop")), SoundSource.NEUTRAL, 1, 1);
		}
	}

	/**
	 * 以给定目标高度为中心，向上与向下搜索可用于生成实体的 Y 坐标。
	 *
	 * <p>这个方法适用于需要落在开阔空间内的普通生成逻辑。它会在搜索前于参考坐标
	 * {@code (x, y, z)} 播放一次方块音效，并在 {@code (xx, yy, zz)} 附近的 12 格范围内
	 * 交替检查上下高度，返回首个未被地板高度判定阻挡的位置。
	 *
	 * <p>与 {@link #findFirstEmptyYAbove(LevelAccessor, double, double, double)} 不同，
	 * 这里关注的是“可落位的生成高度”，而不是单纯寻找空方块。
	 *
	 * @param world 世界
	 * @param x 触发音效的参考 X 坐标
	 * @param y 触发音效的参考 Y 坐标
	 * @param z 触发音效的参考 Z 坐标
	 * @param xx 目标生成点 X 坐标
	 * @param yy 目标生成点起始 Y 坐标
	 * @param zz 目标生成点 Z 坐标
	 * @return 找到的可生成 Y；若 12 格内未找到，则返回 {@link Double#NaN}
	 */
	public static double findValidSpawnY(LevelAccessor world, double x, double y, double z, double xx, double yy, double zz) {
		double validY;
		if (world instanceof Level level) {
			level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.azalea.hit")), SoundSource.NEUTRAL, 0, 1);
		}
		for (int index0 = 0; index0 < 12; index0++) {
			validY = yy + index0;
			if (!(world.getBlockFloorHeight(BlockPos.containing(xx, validY, zz)) > 0)) {
				return validY;
			}
			validY = yy - index0 - 1;
			if (!(world.getBlockFloorHeight(BlockPos.containing(xx, validY, zz)) > 0)) {
				return validY;
			}
		}
		return Double.NaN;
	}

	/**
	 * 自给定高度起向上搜索首个可用的空方块 Y 坐标。
	 *
	 * <p>这个方法适用于泪滴、肢体等悬空生成物。它只要求目标方块本身为空，
	 * 不像 {@link #findValidSpawnY(LevelAccessor, double, double, double, double, double, double)}
	 * 那样还会校验脚下是否存在可站立表面。
	 *
	 * @param world 世界
	 * @param x 目标 X 坐标
	 * @param startY 搜索起始 Y 坐标
	 * @param z 目标 Z 坐标
	 * @return 找到的首个空方块 Y；若 12 格内未找到，则返回 {@link Double#NaN}
	 */
	public static double findFirstEmptyYAbove(LevelAccessor world, double x, double startY, double z) {
		double validY;
		for (int index0 = 0; index0 < 12; index0++) {
			validY = startY + index0;
			if (world.isEmptyBlock(BlockPos.containing(x, validY, z))) {
				return validY;
			}
		}
		return Double.NaN;
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

	/**
	 * 判断指定位置是否具备人形单位可用的落点空间。
	 * <p>
	 * 该方法会检查目标坐标向上 3 格内是否存在会占用站立空间的地形。
	 * 若在客户端调用，还会在该位置播放一次末影人环境音，用于配合相关传送或生成表现。
	 *
	 * @param world 世界访问器
	 * @param xx 目标 X 坐标
	 * @param yy 目标 Y 坐标
	 * @param zz 目标 Z 坐标
	 * @return 若该位置可容纳人形单位站立则返回 {@code true}，否则返回 {@code false}
	 */
	public static boolean isValidHumanoidPlace(LevelAccessor world, double xx, double yy, double zz) {
		if (world instanceof Level _level &&_level.isClientSide()) {
			_level.playLocalSound(xx, yy, zz, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.enderman.ambient")), SoundSource.HOSTILE, 0, 1, false);
		}
		for (int dy = 0; dy <= 2; dy++) {
			if (world.getBlockFloorHeight(BlockPos.containing(xx, yy + dy, zz)) > 0) {
				return false;
			}
		}
		return true;
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
