package com.apocalypse.caerulaarbor.util;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CABlocks;
import com.apocalypse.caerulaarbor.init.CAConfigs;
import com.apocalypse.caerulaarbor.init.CAGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
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

public class WorldUtils {
	private WorldUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * 鎸夊綋鍓嶆捣鍡ｇ棔杩规柟鍧楃殑鐢熼暱瑙勫垯鎻愰珮鍏?{@code grow_age}銆?
	 *
	 * <p>璇ユ柟娉曚細鐩存帴璇诲彇鐩爣浣嶇疆涓婄殑鏂瑰潡鐘舵€侊紱鑻ヨ鏂瑰潡涓嶅瓨鍦?{@code grow_age}
	 * 鏁村瀷灞炴€э紝鍒欎笉鎵ц浠讳綍鎿嶄綔銆傚綋鍓嶅疄鐜颁粎鍦ㄥ勾榫勫皬浜?{@code 30} 鏃剁敓鏁堬紝
	 * 骞跺皾璇曞皢鍏朵竴娆℃€у鍔?{@code 8}銆傚彧鏈夊綋澧炲姞鍚庣殑鍊间粛灞炰簬璇ュ睘鎬у厑璁哥殑鍙栧€艰寖鍥存椂锛?
	 * 鎵嶄細鐪熸鍐欏洖涓栫晫銆?
	 *
	 * @param world 涓栫晫
	 * @param pos 鐩爣鏂瑰潡浣嶇疆
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

	//鍙枒
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

	//涓嬫斁鎴栦娇鐢ㄥ熀绫绘垨鎺ュ彛
	public static boolean canLilyExist(LevelAccessor world, double x, double y, double z) {
		return world.getBlockState(BlockPos.containing(x, y - 1, z)).isFaceSturdy(world, BlockPos.containing(x, y - 1, z), Direction.UP);
	}

	/**
	 * 鍒ゆ柇鐩爣浣嶇疆鏄惁鍏佽鏀剧疆娴峰棧鐥曡抗鏂瑰潡銆?
	 *
	 * <p>璇ユ柟娉曟鏌ョ洰鏍囦綅缃涓嬫柟鐨勬柟鍧楋細瀹冪殑涓婅〃闈㈠繀椤昏兘澶熸壙鎵樻柟鍧楋紝
	 * 鎴栬€呰鏄惧紡鏍囪杩?{@code trail_existable} 鏍囩锛涘悓鏃惰鏀拺鏂瑰潡涓嶈兘鏄?
	 * {@code SEA_TRAIL_SOLID}锛屼互閬垮厤鍦ㄥ疄蹇冩捣鍡ｇ棔杩逛笂缁х画鍙犳斁鏅€氱棔杩广€?
	 *
	 * @param world 涓栫晫
	 * @param x 鐩爣 X 鍧愭爣
	 * @param y 鐩爣 Y 鍧愭爣
	 * @param z 鐩爣 Z 鍧愭爣
	 * @return 鑻ュ綋鍓嶄綅缃厑璁告斁缃捣鍡ｇ棔杩癸紝鍒欒繑鍥?{@code true}
	 */
	public static boolean canPutTrail(LevelAccessor world, double x, double y, double z) {
		BlockPos belowPos = BlockPos.containing(x, y - 1, z);
		BlockState belowState = world.getBlockState(belowPos);
		return (belowState.isFaceSturdy(world, belowPos, Direction.UP)
				|| belowState.is(BlockTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "trail_existable"))))
				&& belowState.getBlock() != CABlocks.SEA_TRAIL_SOLID.get();
	}

	//鍙枒
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
					if (target.is(BlockTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "trail")))) {
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
								BlockPos pos = BlockPos.containing(px, py, pz);
								net.minecraft.world.level.block.Block.dropResources(world.getBlockState(pos), world, BlockPos.containing(px + 0.5, py + 0.5, pz + 0.5), null);
								world.destroyBlock(pos, false);
							}
						} else {
							world.destroyBlock(BlockPos.containing(px, py, pz), false);
						}
					}
				}
			}
		}
	}

	//鍙互瀹夋帓鍒伴偅涓狟aseSeaborn
	public static boolean canCommonSeabornSpawn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "common_spawn_biome")))) {
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

	//鎴栬鏀惧埌鍏朵粬 util 姣旇緝濂斤紵鍙互涓撻棬鍒朵綔涓€涓捣鍡?util
	public static boolean canDangerSeabornSpawn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "danger_spawn_biome")))) {
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

	//鍙枒
	public static boolean canRareSeabornSpawn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "rare_spawn_biome")))) {
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

	//TODO 鍙枒锛屼负浠€涔堜笉鏀惧湪鍏朵粬util
	public static void dropRelicRoute(LevelAccessor world, double x, double y, double z) {
		if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
			if (!world.isClientSide() && world.getServer() != null) {
				for (ItemStack itemstackiterator : world.getServer().getLootData().getLootTable(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "gameplay/relic_route"))
						.getRandomItems(new LootParams.Builder((ServerLevel) world).create(LootContextParamSets.EMPTY))) {
					if (world instanceof ServerLevel level) {
						ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, itemstackiterator);
						entityToSpawn.setPickUpDelay(10);
						entityToSpawn.setUnlimitedLifetime();
						level.addFreshEntity(entityToSpawn);
					}
				}
			}
		}
	}

	/**
	 * 鍒ゆ柇鐩爣浣嶇疆涓嬫柟 20 鏍煎唴鏄惁涓嶅瓨鍦ㄥ彲浣滀负鍦伴潰鐨勫疄蹇冩柟鍧椼€?
	 *
	 * <p>璇ユ柟娉曚細灏嗙┖姘斿拰娑蹭綋閮借涓衡€滄湭鎺ュ湴鈥濓紝鍥犳鍙敤浜庢偓娴崟浣嶆娴嬭嚜宸辨槸鍚﹂暱鏈熶綅浜?
	 * 娣卞潙銆佹按鏌辨垨鍏朵粬鏃犲疄蹇冩敮鎾戠殑绌洪棿涓婃柟銆?
	 *
	 * @param world 涓栫晫
	 * @param x 鐩爣 X 鍧愭爣
	 * @param y 鐩爣 Y 鍧愭爣
	 * @param z 鐩爣 Z 鍧愭爣
	 * @return 鑻ヤ笅鏂?20 鏍煎唴閮芥病鏈夊疄蹇冨湴闈紝鍒欒繑鍥?{@code true}
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

	//TODO:鎴栬鍙互涓嬫斁
	public static void dropRelicTidebi(LevelAccessor world, double x, double y, double z) {
		if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
			if (!world.isClientSide() && world.getServer() != null) {
				for (ItemStack itemstackiterator : world.getServer().getLootData().getLootTable(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "gameplay/relic_tidebi"))
						.getRandomItems(new LootParams.Builder((ServerLevel) world).create(LootContextParamSets.EMPTY))) {
					if (world instanceof ServerLevel level) {
						ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, itemstackiterator);
						entityToSpawn.setPickUpDelay(10);
						entityToSpawn.setUnlimitedLifetime();
						level.addFreshEntity(entityToSpawn);
					}
				}
			}
		}
	}

	//杩樿锛屾殏鏃朵笉鍔ㄤ唬鐮佹湰韬紝浣嗘槸鐪熺殑闇€瑕佹斁鍦ㄨ繖閲屽悧銆傘€傝瘎浼版湁娌℃湁鏇村悎閫傜殑
	/**
	 * 浠ョ粰瀹氱洰鏍囬珮搴︿负涓績锛屽悜涓婁笌鍚戜笅鎼滅储鍙敤浜庣敓鎴愬疄浣撶殑 Y 鍧愭爣銆?
	 *
	 * <p>杩欎釜鏂规硶閫傜敤浜庨渶瑕佽惤鍦ㄥ紑闃旂┖闂村唴鐨勬櫘閫氱敓鎴愰€昏緫銆傚畠浼氬湪鎼滅储鍓嶄簬鍙傝€冨潗鏍?
	 * {@code (x, y, z)} 鎾斁涓€娆℃柟鍧楅煶鏁堬紝骞跺湪 {@code (xx, yy, zz)} 闄勮繎鐨?12 鏍艰寖鍥村唴
	 * 浜ゆ浛妫€鏌ヤ笂涓嬮珮搴︼紝杩斿洖棣栦釜鏈鍦版澘楂樺害鍒ゅ畾闃绘尅鐨勪綅缃€?
	 *
	 * <p>涓?{@link #findFirstEmptyYAbove(LevelAccessor, double, double, double)} 涓嶅悓锛?
	 * 杩欓噷鍏虫敞鐨勬槸鈥滃彲钀戒綅鐨勭敓鎴愰珮搴︹€濓紝鑰屼笉鏄崟绾鎵剧┖鏂瑰潡銆?
	 *
	 * @param world 涓栫晫
	 * @param x 瑙﹀彂闊虫晥鐨勫弬鑰?X 鍧愭爣
	 * @param y 瑙﹀彂闊虫晥鐨勫弬鑰?Y 鍧愭爣
	 * @param z 瑙﹀彂闊虫晥鐨勫弬鑰?Z 鍧愭爣
	 * @param xx 鐩爣鐢熸垚鐐?X 鍧愭爣
	 * @param yy 鐩爣鐢熸垚鐐硅捣濮?Y 鍧愭爣
	 * @param zz 鐩爣鐢熸垚鐐?Z 鍧愭爣
	 * @return 鎵惧埌鐨勫彲鐢熸垚 Y锛涜嫢 12 鏍煎唴鏈壘鍒帮紝鍒欒繑鍥?{@link Double#NaN}
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
	 * 鑷粰瀹氶珮搴﹁捣鍚戜笂鎼滅储棣栦釜鍙敤鐨勭┖鏂瑰潡 Y 鍧愭爣銆?
	 *
	 * <p>杩欎釜鏂规硶閫傜敤浜庢唱婊淬€佽偄浣撶瓑鎮┖鐢熸垚鐗┿€傚畠鍙姹傜洰鏍囨柟鍧楁湰韬负绌猴紝
	 * 涓嶅儚 {@link #findValidSpawnY(LevelAccessor, double, double, double, double, double, double)}
	 * 閭ｆ牱杩樹細鏍￠獙鑴氫笅鏄惁瀛樺湪鍙珯绔嬭〃闈€?
	 *
	 * @param world 涓栫晫
	 * @param x 鐩爣 X 鍧愭爣
	 * @param startY 鎼滅储璧峰 Y 鍧愭爣
	 * @param z 鐩爣 Z 鍧愭爣
	 * @return 鎵惧埌鐨勯涓┖鏂瑰潡 Y锛涜嫢 12 鏍煎唴鏈壘鍒帮紝鍒欒繑鍥?{@link Double#NaN}
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

	//闇€瑕佽瘎浼扮劧鍚庢坊鍔犳枃妗ｆ敞閲婅В閲婁綔鐢?
	public static boolean isOrganic(BlockState block) {
		if (block.getBlock() == CABlocks.TRAIL_PULSE.get() || block.getBlock() == CABlocks.TRAIL_LOG.get() || block.getBlock() == CABlocks.TRAIL_LEAVE.get()
				|| block.getBlock() == CABlocks.STRIPPED_TRAIL_LOG.get()) {
			return false;
		}
		if (CAConfigs.EXTERNAL_ERROSION.get() && (block.is(BlockTags.create(ResourceLocation.parse("forge:phayrilesh"))) || block.is(BlockTags.create(ResourceLocation.parse("spore:fungal_blocks"))))) {
			return Math.random() < 0.33;
		}
		return block.is(BlockTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "organic")));
	}

	/**
	 * 鍒ゆ柇鎸囧畾浣嶇疆鏄惁鍏峰浜哄舰鍗曚綅鍙敤鐨勮惤鐐圭┖闂淬€?
	 * <p>
	 * 璇ユ柟娉曚細妫€鏌ョ洰鏍囧潗鏍囧悜涓?3 鏍煎唴鏄惁瀛樺湪浼氬崰鐢ㄧ珯绔嬬┖闂寸殑鍦板舰銆?
	 * 鑻ュ湪瀹㈡埛绔皟鐢紝杩樹細鍦ㄨ浣嶇疆鎾斁涓€娆℃湯褰变汉鐜闊筹紝鐢ㄤ簬閰嶅悎鐩稿叧浼犻€佹垨鐢熸垚琛ㄧ幇銆?
	 *
	 * @param world 涓栫晫璁块棶鍣?
	 * @param xx 鐩爣 X 鍧愭爣
	 * @param yy 鐩爣 Y 鍧愭爣
	 * @param zz 鐩爣 Z 鍧愭爣
	 * @return 鑻ヨ浣嶇疆鍙绾充汉褰㈠崟浣嶇珯绔嬪垯杩斿洖 {@code true}锛屽惁鍒欒繑鍥?{@code false}
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

	//闇€瑕佹敞閲婅В閲?
	public static boolean canGrief(LevelAccessor world) {
		if (world.isClientSide()) {
			return false;
		}
		if (CAConfigs.BREAKABLE.get()) {
			return world.getLevelData().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
		}
		return false;
	}

	//闇€瑕佹敞閲婅В閲婏紝鎴栬鍙互绉诲姩鍒板埆鐨剈til
	public static boolean canSpawnUnderwaterSeaborn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "underwater_spawn_biome")))) {
			return false;
		}
		if (Math.random() * 100 < (world.getLevelData().getGameRules().getInt(CAGameRules.SEABORN_SPAWN_RATE))) {
			return world.getDifficulty() != Difficulty.PEACEFUL;
		}
		return false;
	}

	//鍚屼笂
	public static boolean canSpawnMarineSeaborn(LevelAccessor world, double x, double y, double z) {
		if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "marine_spawn_biome")))) {
			return false;
		}
		if (Math.random() * 100 < (world.getLevelData().getGameRules().getInt(CAGameRules.SEABORN_SPAWN_RATE))) {
			return world.getDifficulty() != Difficulty.PEACEFUL;
		}
		return false;
	}

}
