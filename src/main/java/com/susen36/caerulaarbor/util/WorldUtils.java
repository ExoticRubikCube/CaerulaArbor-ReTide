package com.susen36.caerulaarbor.util;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAConfigs;
import com.susen36.caerulaarbor.init.CAGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Difficulty;
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

public class WorldUtils {
	private WorldUtils() {
		throw new UnsupportedOperationException("Utility class");
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
				public BlockState with(BlockState bs, String property, int newValue) {
					Property<?> prop = bs.getBlock().getStateDefinition().getProperty(property);
					return prop instanceof IntegerProperty ip && prop.getPossibleValues().contains(newValue) ? bs.setValue(ip, newValue) : bs;
				}
			}.with(CABlocks.SEA_TRAIL_BURNT.get().defaultBlockState(), "longevity", toBeBurn.getBlock().getStateDefinition().getProperty("longevity") instanceof IntegerProperty getip4 ? toBeBurn.getValue(getip4) : -1));
			success = true;
		} else if (toBeBurn.getBlock() == CABlocks.SEA_TRAIL_SOLID.get() || toBeBurn.getBlock() == CABlocks.TRAIL_PULSE.get()) {
			output = CABlocks.SEA_TRAIL_BURNT_SOLID.get().defaultBlockState();
			success = true;
		}
		watered = toBeBurn.getBlock().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty getbp8 && toBeBurn.getValue(getbp8);
		if (success) {
			if (output.getBlock() == Blocks.AIR) {
				if (watered) {
					BlockPos bp = BlockPos.containing(px, py, pz);
					BlockState bso = world.getBlockState(bp);
					BlockState bs = Blocks.WATER.withPropertiesOf(bso);
					world.setBlock(bp, bs, 3);
				} else {
					world.setBlock(BlockPos.containing(px, py, pz), Blocks.AIR.defaultBlockState(), 3);
				}
			} else {
				BlockPos bp = BlockPos.containing(px, py, pz);
				BlockState bso = world.getBlockState(bp);
				BlockState bs = output.getBlock().withPropertiesOf(bso);
				if (output.hasProperty(BlockStateProperties.WATERLOGGED) && bs.hasProperty(BlockStateProperties.WATERLOGGED))
					bs = bs.setValue(BlockStateProperties.WATERLOGGED, output.getValue(BlockStateProperties.WATERLOGGED));
				if (output.getBlock().getStateDefinition().getProperty("longevity") instanceof IntegerProperty integerProp && bs.hasProperty(integerProp))
					bs = bs.setValue(integerProp, output.getValue(integerProp));
				world.setBlock(bp, bs, 3);
			}
			if (world instanceof Level level) {
					level.playSound(null, BlockPos.containing(px, py, pz), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, (float) 0.6, 1);
			}
		}
	}

	//可疑
	public static boolean canCommonSeabornSpawn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "common_spawn_biome")))) {
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

	//或许放到其他 util 比较好?可以专门制作一个海嗣 util
	public static boolean canDangerSeabornSpawn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "danger_spawn_biome")))) {
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
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "rare_spawn_biome")))) {
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


	/**
	 * 判断目标位置下方指定格数内是否不存在可作为地面的实心方块。
	 *
	 * <p>该方法会将空气和液体都视为"未接地"，因此可用于悬浮单位检测自己是否长期位于
	 * 深坑、水柱或其他无实心支撑的空间上方。
	 *
	 * @param world 世界
	 * @param x     目标 X 坐标
	 * @param y     目标 Y 坐标
	 * @param z     目标 Z 坐标
	 * @param depth 向下检测的最大格数（深度）
	 * @return 若下方 depth 格内都没有实心地面，则返回 {@code true}
	 */
	public static boolean hasNoSolidGroundBelow(Level world, double x, double y, double z, int depth) {
		// 如果实体已经处于世界最低点以下，直接返回 false
		if (y < world.getMinBuildHeight()) {
			return false;
		}

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		pos.set(x, y - 1, z);

		for (int i = 0; i < depth; i++) {
			if (pos.getY() < world.getMinBuildHeight()) {
				break;
			}
			BlockState state = world.getBlockState(pos);

			if (!state.isAir() && !(state.getBlock() instanceof LiquidBlock)) {
				return false;
			}
			pos.move(Direction.DOWN);// 坐标向下移动一格，进入下一次循环
		}

		return true;
	}

	//还行，暂时不动代码本身，但是真的需要放在这里吗。再评估有没有更合适的位置
	/**
	 * 以给定目标高度为中心，向上与向下搜索可用于生成实体的 Y 坐标。
	 *
	 * <p>这个方法适用于需要落在开阔空间内的普通生成逻辑。它会在搜索前于参数坐标
	 * {@code (x, y, z)} 播放一次方块音效，并在 {@code (xx, yy, zz)} 附近 ±12 格范围内
	 * 交替检查上下高度，返回首个未被地板高度判定阻挡的位置。
	 *
	 * <p>与 {@link #findFirstEmptyYAbove(LevelAccessor, double, double, double)} 不同，
	 * 这里关注的是"可落位的生成高度"，而不是单纯寻找空方块。
	 *
	 * @param world 世界
	 * @param x 触发音效的参数 X 坐标
	 * @param y 触发音效的参数 Y 坐标
	 * @param z 触发音效的参数 Z 坐标
	 * @param xx 目标生成点 X 坐标
	 * @param yy 目标生成点起始 Y 坐标
	 * @param zz 目标生成点 Z 坐标
	 * @return 找到的可生成 Y；若 12 格内未找到，则返回 {@link Double#NaN}
	 */
	public static double findValidSpawnY(LevelAccessor world, double x, double y, double z, double xx, double yy, double zz) {
		double validY;
		if (world instanceof Level level) {
			level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.AZALEA_HIT, SoundSource.NEUTRAL, 0, 1);
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
	 * <p>这个方法适用于泪滴、肢体等悬空生成物，它只要求目标方块本身为空，
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
		if (CAConfigs.EXTERNAL_ERROSION.get() && (block.is(BlockTags.create(ResourceLocation.parse("forge:phayrilesh"))) || block.is(BlockTags.create(ResourceLocation.parse("spore:fungal_blocks"))))) {
			return Math.random() < 0.33;
		}
		return block.is(BlockTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "organic")));
	}

	/**
	 * 判断指定位置是否具备人形单位可用的落点空间。
	 * <p>
	 * 该方法会检查目标坐标向上 3 格内是否存在会占用站立空间的地形，
	 * 若在客户端调用，还会在该位置播放一次末影人环境音，用于配合相关传送或生成表现。
	 *
	 * @param world 世界访问器
	 * @param xx 目标 X 坐标
	 * @param yy 目标 Y 坐标
	 * @param zz 目标 Z 坐标
	 * @return 若该位置可容纳人形单位站立则返回 {@code true}，否则返回 {@code false}
	 */
	public static boolean isValidHumanoidPlace(LevelAccessor world, double xx, double yy, double zz) {
		if (world instanceof Level level &&level.isClientSide()) {
			level.playLocalSound(xx, yy, zz, SoundEvents.ENDERMAN_AMBIENT, SoundSource.HOSTILE, 0, 1, false);
		}
		for (int dy = 0; dy <= 2; dy++) {
			if (world.getBlockFloorHeight(BlockPos.containing(xx, yy + dy, zz)) > 0) {
				return false;
			}
		}
		return true;
	}

	//游戏规则(生物课破坏)
	public static boolean canGrief(LevelAccessor world) {
		if (!world.isClientSide() && CAConfigs.BREAKABLE.get()) {
			return world.getLevelData().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
		}
		return false;
	}

	//需要注释解释，或许可以移动到别的 util
	public static boolean canSpawnUnderwaterSeaborn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "underwater_spawn_biome")))) {
			return false;
		}
		if (Math.random() * 100 < (world.getLevelData().getGameRules().getInt(CAGameRules.SEABORN_SPAWN_RATE))) {
			return world.getDifficulty() != Difficulty.PEACEFUL;
		}
		return false;
	}

	//同上
	public static boolean canSpawnMarineSeaborn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "marine_spawn_biome")))) {
			return false;
		}
		if (Math.random() * 100 < (world.getLevelData().getGameRules().getInt(CAGameRules.SEABORN_SPAWN_RATE))) {
			return world.getDifficulty() != Difficulty.PEACEFUL;
		}
		return false;
	}

}