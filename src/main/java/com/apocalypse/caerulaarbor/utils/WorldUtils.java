package com.apocalypse.caerulaarbor.utils;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.configuration.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.entity.Al1SHelperEntity;
import com.apocalypse.caerulaarbor.entity.LittleHelperEntity;
import com.apocalypse.caerulaarbor.entity.OceanizedWitherEntity;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;

import com.apocalypse.caerulaarbor.procedures.SummonEliteFishProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
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
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class WorldUtils {
	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] WATER_NORMAL_POOL = {
		CaerulaArborModEntities.COLLECTOR_PROKARYOTE,
		CaerulaArborModEntities.FLOATER_PROKARYOTE,
		CaerulaArborModEntities.DEPOSITER_PROKARYOTE,
		CaerulaArborModEntities.ACCUMULATOR_PROKARYOTE,
		CaerulaArborModEntities.FEEDER_PROKARYOTE,
		CaerulaArborModEntities.BONE_FISH
	};

	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] LAND_NORMAL_POOL = {
		CaerulaArborModEntities.CHISELER_FISH,
		CaerulaArborModEntities.FLY_FISH,
		CaerulaArborModEntities.PREDATOR_ABYSSAL,
		CaerulaArborModEntities.FAKE_OFFSPRING,
		CaerulaArborModEntities.SLIDER_FISH,
		CaerulaArborModEntities.RUN_FISH,
		CaerulaArborModEntities.SHOOTER_FISH,
		CaerulaArborModEntities.SPLASHER_ABYSSAL
	};

	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] OCEANIZED_ANIMAL_POOL = {
		CaerulaArborModEntities.OCEANIZED_PIG,
		CaerulaArborModEntities.OCEANIZED_COW,
		CaerulaArborModEntities.OCEANIZED_SHEEP,
		CaerulaArborModEntities.OCEANIZED_HORSE,
		CaerulaArborModEntities.OCEANIZED_WOLF,
		CaerulaArborModEntities.OCEANIZED_SPIDER,
		CaerulaArborModEntities.OCEANIZED_VILLAGER,
		CaerulaArborModEntities.OCEANIZED_WITCH,
		CaerulaArborModEntities.OCEANIZED_FOX,
		CaerulaArborModEntities.OCEANIZED_POLAR_BEAR,
		CaerulaArborModEntities.OCEANIZE_RABBIT,
		CaerulaArborModEntities.OCEANIZED_CAT,
		CaerulaArborModEntities.OCEANIZED_CHICKEN
	};

	private WorldUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static InteractionResult convertToOceanFarmland(LevelAccessor world, BlockPos pos, BlockState blockstate, Entity entity) {
		if (entity == null)
			return InteractionResult.PASS;
		if (entity.isShiftKeyDown() && blockstate.getBlock() == Blocks.FARMLAND) {
			BlockState _bs = CaerulaArborModBlocks.OCEAN_FARMLAND.get().defaultBlockState();
			BlockState _bso = world.getBlockState(pos);
			for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
				Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
				if (_property != null && _bs.getValue(_property) != null)
					try {
						_bs = _bs.setValue(_property, (Comparable) entry.getValue());
					} catch (Exception e) {
					}
			}
			world.setBlock(pos, _bs, 3);
		}
		return InteractionResult.SUCCESS;
	}

	public static void addGrowAge(LevelAccessor world, BlockPos pos, BlockState blockstate) {
		if ((blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip1 ? blockstate.getValue(_getip1) : -1) < 30) {
			int value = (int) ((blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip3 ? blockstate.getValue(_getip3) : -1) + 8);
			BlockState _bs = world.getBlockState(pos);
			if (_bs.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
				world.setBlock(pos, _bs.setValue(integerProp, value), 3);
		}
	}

	public static void bestowAbility(LevelAccessor world, double index) {
		CaerulaArborModVariables.MapVariables.get(world).endspeaker_abolities =(double) ((int)CaerulaArborModVariables.MapVariables.get(world).endspeaker_abolities | (int)Math.pow(2, index));
		CaerulaArborModVariables.MapVariables.get(world).syncData(world);
	}

	public static void burndownTrail(LevelAccessor world, BlockState toBeBurn, double px, double py, double pz) {
		BlockState output = Blocks.AIR.defaultBlockState();
		boolean success = false;
		boolean watered = false;
		if (toBeBurn.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_INIT.get() || toBeBurn.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_GROWING.get()) {
			output = Blocks.AIR.defaultBlockState();
			success = true;
		} else if (toBeBurn.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_GROWN.get() || toBeBurn.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_STOP.get()) {
			output = (new Object() {
				public BlockState with(BlockState _bs, String _property, int _newValue) {
					Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty(_property);
					return _prop instanceof IntegerProperty _ip && _prop.getPossibleValues().contains(_newValue) ? _bs.setValue(_ip, _newValue) : _bs;
				}
			}.with(CaerulaArborModBlocks.SEA_TRAIL_BURNT.get().defaultBlockState(), "longevity", toBeBurn.getBlock().getStateDefinition().getProperty("longevity") instanceof IntegerProperty _getip4 ? toBeBurn.getValue(_getip4) : -1));
			success = true;
		} else if (toBeBurn.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_SOLID.get() || toBeBurn.getBlock() == CaerulaArborModBlocks.TRAIL_PULSE.get()) {
			output = CaerulaArborModBlocks.SEA_TRAIL_BURNT_SOLID.get().defaultBlockState();
			success = true;
		}
		watered = toBeBurn.getBlock().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _getbp8 && toBeBurn.getValue(_getbp8);
		if (success) {
			if (output.getBlock() == Blocks.AIR) {
				if (watered) {
					{
						BlockPos _bp = BlockPos.containing(px, py, pz);
						BlockState _bs = Blocks.WATER.defaultBlockState();
						BlockState _bso = world.getBlockState(_bp);
						for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
							Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
							if (_property != null && _bs.getValue(_property) != null)
								try {
									_bs = _bs.setValue(_property, (Comparable) entry.getValue());
								} catch (Exception e) {
								}
						}
						world.setBlock(_bp, _bs, 3);
					}
				} else {
					world.setBlock(BlockPos.containing(px, py, pz), Blocks.AIR.defaultBlockState(), 3);
				}
			} else {
				{
					BlockPos _bp = BlockPos.containing(px, py, pz);
					BlockState _bs = output;
					BlockState _bso = world.getBlockState(_bp);
					for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
						Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
						if (_property != null && _bs.getValue(_property) != null)
							try {
								_bs = _bs.setValue(_property, (Comparable) entry.getValue());
							} catch (Exception e) {
							}
					}
					world.setBlock(_bp, _bs, 3);
				}
			}
			if (world instanceof Level _level) {
					_level.playSound(null, BlockPos.containing(px, py, pz), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.fire.extinguish")), SoundSource.BLOCKS, (float) 0.6, 1);
			}
		}
	}

	public static void bulletParticle(LevelAccessor world, double x, double y, double z) {
		world.addParticle((SimpleParticleType) (CaerulaArborModParticleTypes.SEA_SPLASH.get()), x, y, z, 0, 0, 0);
	}

	public static boolean canLilyExist(LevelAccessor world, double x, double y, double z) {
		return world.getBlockState(BlockPos.containing(x, y - 1, z)).isFaceSturdy(world, BlockPos.containing(x, y - 1, z), Direction.UP);
	}

	public static boolean canPutTrail(LevelAccessor world, double x, double y, double z) {
		return (world.getBlockState(BlockPos.containing(x, y - 1, z)).isFaceSturdy(world, BlockPos.containing(x, y - 1, z), Direction.UP)
				|| (world.getBlockState(BlockPos.containing(x, y - 1, z))).is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "trail_existable"))))
				&& !((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == CaerulaArborModBlocks.SEA_TRAIL_SOLID.get());
	}

	/**
	 * Checks for the Oceanized Wither T-shaped summon structure and consumes it when matched.
	 */
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

	public static void clearNetherseaAround(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		BlockState target = Blocks.AIR.defaultBlockState();
		boolean canBreak = false;
		boolean mayDrop = false;
		double px = 0;
		double pz = 0;
		double py = 0;
		double dur = 0;
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
					} else if (target.getBlock() == CaerulaArborModBlocks.OCEAN_OVARY.get()) {
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

	public static boolean canCommonSeabornSpawn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation(CaerulaArborMod.MODID, "common_spawn_biome")))) {
			return false;
		}
		if (Math.random() * 100 < (world.getLevelData().getGameRules().getInt(CaerulaArborModGameRules.SEABORN_SPAWN_RATE))) {
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

	public static boolean canDangerSeabornSpawn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation(CaerulaArborMod.MODID, "danger_spawn_biome")))) {
			return false;
		}
		if (Math.random() * 100 < (world.getLevelData().getGameRules().getInt(CaerulaArborModGameRules.SEABORN_SPAWN_RATE))) {
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

	public static boolean canRareSeabornSpawn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation(CaerulaArborMod.MODID, "rare_spawn_biome")))) {
			return false;
		}
		if (Math.random() * 100 < (world.getLevelData().getGameRules().getInt(CaerulaArborModGameRules.SEABORN_SPAWN_RATE))) {
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

	public static void corruptedSpawnMobs(LevelAccessor world, double x, double y, double z, double n) {
		double tx = 0;
		double ty = 0;
		double tz = 0;
		double R = 0;
		double T = 0;
		if (EntityUtils.getSeabornNum(world, x, y, z) >= (world.getLevelData().getGameRules().getInt(CaerulaArborModGameRules.CLONE_NUMBER_LIMIT))) {
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

	public static boolean isDistFromGround(LevelAccessor world, double x, double y, double z) {
		double block = 0;
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

	public static void dropMoistStar(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
			if (world instanceof ServerLevel _level) {
				ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(CaerulaArborModItems.MOIST_STAR.get()));
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
				Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_WITHERIA.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
		}
	}

	public static double findValidYForCat(LevelAccessor world, double x, double y, double z, double xx, double yy, double zz) {
		double y_found = 0;
		if (world instanceof Level _level) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.azalea.hit")), SoundSource.NEUTRAL, 0, 1);
		}
		y_found = yy;
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

	public static void playFractalSummonSound(LevelAccessor world, double x, double y, double z) {
		if (world instanceof Level _level) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.axolotl.splash")), SoundSource.HOSTILE, (float) 0.75, 1);
		}
	}

	public static double findYzforTear(LevelAccessor world, double xx, double yy, double zz) {
		double y_found = 0;
		y_found = yy;
		for (int index0 = 0; index0 < 12; index0++) {
			y_found = yy + index0;
			if (world.isEmptyBlock(BlockPos.containing(xx, y_found, zz))) {
				return y_found;
			}
		}
		return 114514;
	}

	public static double findGround(LevelAccessor world, double xx, double yy, double zz) {
		BlockState target = Blocks.AIR.defaultBlockState();
		double findY = 0;
		findY = 114514;
		for (int dy = (int) 0; dy <= (int) 3; dy++) {
			target = (world.getBlockState(BlockPos.containing(xx, yy + dy, zz)));
			if (target.canBeReplaced() && world.getBlockFloorHeight(BlockPos.containing(xx, yy + dy - 1, zz)) > 0) {
				findY = yy + dy;
				break;
			}
			target = (world.getBlockState(BlockPos.containing(xx, yy - dy, zz)));
			if (target.canBeReplaced() && world.getBlockFloorHeight(BlockPos.containing(xx, yy - dy - 1, zz)) > 0) {
				findY = yy - dy;
				break;
			}
		}
		return findY;
	}

	public static void shootWitherTo(LevelAccessor world, Entity from, Entity target) {
		if (from == null || target == null)
			return;
		double vx = target.getX() - from.getX();
		double vy = (target.getY() + target.getBbHeight() * 0.5) - (from.getY() + 2.7);
		double vz = target.getZ() - from.getZ();
		shootWitherSkull(world, from, 0.1, vx, vy, vz, 1, Mth.nextDouble(RandomSource.create(), 0.42, 0.56), from.getX(), from.getY() + 2.7, from.getZ());
	}

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

	public static void ireneBurnBrandAround(LevelAccessor world, double x, double y, double z) {
		BlockState toBeBurn = Blocks.AIR.defaultBlockState();
		double px = 0;
		double py = 0;
		double pz = 0;
		for (int index0 = 0; index0 < 3; index0++) {
			for (int index1 = 0; index1 < 3; index1++) {
				for (int index2 = 0; index2 < 3; index2++) {
					px = x + index0 - 1;
					py = y + index1 - 1;
					pz = z + index2 - 1;
					toBeBurn = (world.getBlockState(BlockPos.containing(px, py, pz)));
					if (toBeBurn.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_INIT.get() || toBeBurn.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_GROWING.get() || toBeBurn.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_GROWN.get()
							|| toBeBurn.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_STOP.get() || toBeBurn.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_SOLID.get() || toBeBurn.getBlock() == CaerulaArborModBlocks.TRAIL_PULSE.get()) {
						burndownTrail(world, toBeBurn, px, py, pz);
						if (world instanceof ServerLevel _level)
							_level.sendParticles((SimpleParticleType) (CaerulaArborModParticleTypes.PURPLE_FLAME.get()), (x + 0.5), (y + 1), (z + 0.5), 16, 0.75, 0.75, 0.75, 0.15);
					}
				}
			}
		}
	}

	public static boolean isOrganic(BlockState block) {
		if (block.getBlock() == CaerulaArborModBlocks.TRAIL_PULSE.get() || block.getBlock() == CaerulaArborModBlocks.TRAIL_LOG.get() || block.getBlock() == CaerulaArborModBlocks.TRAIL_LEAVE.get()
				|| block.getBlock() == CaerulaArborModBlocks.STRIPPED_TRAIL_LOG.get()) {
			return false;
		}
		if (block.is(BlockTags.create(new ResourceLocation("forge:phayrilesh"))) || block.is(BlockTags.create(new ResourceLocation("spore:fungal_blocks")))) {
			return Math.random() < 0.33;
		}
		return block.is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "organic")));
	}

	public static boolean isValidPlace(LevelAccessor world, double xx, double yy, double zz) {
		if (world instanceof Level _level) {
			if (_level.isClientSide()) {
				_level.playLocalSound(xx, yy, zz, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.enderman.ambient")), SoundSource.HOSTILE, 0, 1, false);
			}
		}
		for (int dy = (int) 0; dy <= (int) 3; dy++) {
			if (world.getBlockFloorHeight(BlockPos.containing(xx, yy + dy, zz)) > 0) {
				return false;
			}
		}
		return true;
	}

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

	public static void isharmlaLinkPtcToEntity(LevelAccessor world, double x, double y, double z, Entity tgt) {
		if (tgt == null)
			return;
		double vx = tgt.getX() - (x + 0.5);
		double vy = tgt.getY() - (y + 0.5);
		double vz = tgt.getZ() - (z + 0.5);
		double size = Math.max(Math.min(Math.round(Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2) + Math.pow(vz, 2))), 32), 1);
		for (int index0 = 0; index0 < (int) size; index0++) {
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (CaerulaArborModParticleTypes.ISHARMLA_CURSE_PARTICLE.get()), (x + 0.5 + (vx / size) * index0), (y + 0.5 + (vy / size) * index0 + 0.5), (z + 0.5 + (vz / size) * index0), 5, 0.32, 0.5, 0.32, 0.05);
		}
	}

	public static InteractionResult summonMegachest(LevelAccessor world, double x, double y, double z, BlockState blockstate) {
		if ((blockstate.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _getip1 ? blockstate.getValue(_getip1) : -1) == 0) {
			{
				int _value = 1;
				BlockPos _pos = BlockPos.containing(x, y, z);
				BlockState _bs = world.getBlockState(_pos);
				if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
					world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
			}
			{
				int _value = 1;
				BlockPos _pos = BlockPos.containing(x, y, z);
				BlockState _bs = world.getBlockState(_pos);
				if (_bs.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
					world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
			}
			if ((new Object() {
				public Direction getDirection(BlockState _bs) {
					Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
					if (_prop instanceof DirectionProperty _dp)
						return _bs.getValue(_dp);
					_prop = _bs.getBlock().getStateDefinition().getProperty("axis");
					return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) _bs.getValue(_ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
				}
			}.getDirection(blockstate)) == Direction.NORTH) {
				CaerulaArborMod.queueServerWork(15, () -> {
					world.destroyBlock(BlockPos.containing(x, y, z), false);
					if (world instanceof Level _level) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.ender_chest.close")), SoundSource.BLOCKS, 1, 1);
					}
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.MEGA_CHEST.get().spawn(_level, BlockPos.containing(x + 0.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(-180);
							entityToSpawn.setYBodyRot(-180);
							entityToSpawn.setYHeadRot(-180);
						}
					}
				});
			} else if ((new Object() {
				public Direction getDirection(BlockState _bs) {
					Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
					if (_prop instanceof DirectionProperty _dp)
						return _bs.getValue(_dp);
					_prop = _bs.getBlock().getStateDefinition().getProperty("axis");
					return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) _bs.getValue(_ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
				}
			}.getDirection(blockstate)) == Direction.SOUTH) {
				CaerulaArborMod.queueServerWork(15, () -> {
					world.destroyBlock(BlockPos.containing(x, y, z), false);
					if (world instanceof Level _level) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.ender_chest.close")), SoundSource.BLOCKS, 1, 1);
					}
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.MEGA_CHEST.get().spawn(_level, BlockPos.containing(x + 0.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
						}
					}
				});
			} else if ((new Object() {
				public Direction getDirection(BlockState _bs) {
					Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
					if (_prop instanceof DirectionProperty _dp)
						return _bs.getValue(_dp);
					_prop = _bs.getBlock().getStateDefinition().getProperty("axis");
					return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) _bs.getValue(_ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
				}
			}.getDirection(blockstate)) == Direction.WEST) {
				CaerulaArborMod.queueServerWork(15, () -> {
					world.destroyBlock(BlockPos.containing(x, y, z), false);
					if (world instanceof Level _level) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.ender_chest.close")), SoundSource.BLOCKS, 1, 1);
					}
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.MEGA_CHEST.get().spawn(_level, BlockPos.containing(x + 0.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(90);
							entityToSpawn.setYBodyRot(90);
							entityToSpawn.setYHeadRot(90);
						}
					}
				});
			} else if (true) {
				CaerulaArborMod.queueServerWork(15, () -> {
					world.destroyBlock(BlockPos.containing(x, y, z), false);
					if (world instanceof Level _level) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.ender_chest.close")), SoundSource.BLOCKS, 1, 1);
					}
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.MEGA_CHEST.get().spawn(_level, BlockPos.containing(x + 0.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(-90);
							entityToSpawn.setYBodyRot(-90);
							entityToSpawn.setYHeadRot(-90);
						}
					}
				});
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	public static void witheriaDestroyBlocks(LevelAccessor world, double x, double y, double z) {
		boolean once = false;
		double dx = 0;
		double dy = 0;
		double dz = 0;
		double hardness = 0;
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

	public static boolean canGrief(LevelAccessor world) {
		if (world.isClientSide()) {
			return false;
		}
		if (CaerulaConfigsConfiguration.BREAKABLE.get()) {
			return world.getLevelData().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
		}
		return false;
	}

	public static boolean canSpawnUnderwaterSeaborn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation(CaerulaArborMod.MODID, "underwater_spawn_biome")))) {
			return false;
		}
		if (Math.random() * 100 < (world.getLevelData().getGameRules().getInt(CaerulaArborModGameRules.SEABORN_SPAWN_RATE))) {
			return world.getDifficulty() != Difficulty.PEACEFUL;
		}
		return false;
	}

	public static boolean canSpawnMarineSeaborn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation(CaerulaArborMod.MODID, "marine_spawn_biome")))) {
			return false;
		}
		if (Math.random() * 100 < (world.getLevelData().getGameRules().getInt(CaerulaArborModGameRules.SEABORN_SPAWN_RATE))) {
			return world.getDifficulty() != Difficulty.PEACEFUL;
		}
		return false;
	}

	public static void saveWaterloggedState(LevelAccessor world, double x, double y, double z, BlockState oldState) {
		if (oldState.getBlock() == Blocks.WATER) {
			BlockPos _pos = BlockPos.containing(x, y, z);
			BlockState _bs = world.getBlockState(_pos);
			if (_bs.getBlock().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _booleanProp)
				world.setBlock(_pos, _bs.setValue(_booleanProp, true), 3);
		}
	}
}
