package com.susen36.caerulaarbor.manager.upgrade;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler.StrategyType;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CAConfigs;
import com.susen36.caerulaarbor.init.CASounds;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SilenceUpgradeManager {
	public static void applySilenceUpgrade(LevelAccessor world, double point) {
		double stra;
		String num = "";
		String prefix = "";
		stra = MapVariables.get(world).strategy_silence;
		if (canEnableSilence(world)) {
			MapVariablesHandler.addEvoPoint(world, StrategyType.SILENCE, point);
			if (stra > 0) {
				for (Player entityiterator : new ArrayList<>(world.players())) {
					if (entityiterator instanceof ServerPlayer player) {
						AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "she_coming"));
						if (adv == null) continue;
						AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
						if (!ap.isDone()) {
							for (String criteria : ap.getRemainingCriteria())
								player.getAdvancements().award(adv, criteria);
						}
					}
				}
			}
			if (stra < 4) {
				if (MapVariables.get(world).evo_point_silence >= Math.pow(stra + 1, 3) * CAConfigs.COEFFICIENT.get() * 8) {
					MapVariablesHandler.setStrategyLevel(world, StrategyType.SILENCE, stra + 1);
					stra = MapVariables.get(world).strategy_silence;
					MapVariablesHandler.setEvoPoint(world, StrategyType.SILENCE, 0);
					if (stra == 1) {
						num = "I";
						prefix = "§p";
					} else if (stra == 2) {
						num = "II";
						prefix = "§p";
					} else if (stra == 3) {
						num = "III";
						prefix = "§c";
					} else if (stra == 4) {
						num = "IV";
						prefix = "§4";
					}
					if (CAConfigs.EVOSOUND.get()) {
						for (Player entityiterator : new ArrayList<>(world.players())) {
							if (stra == 1) {
								if (world instanceof Level level) {
										level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.SILENCE1.get(),
												SoundSource.NEUTRAL, 6, 1);
								}
								if (entityiterator instanceof Player player && !player.level().isClientSide())
									player.displayClientMessage(Component.literal(getSilenceUnlockPlayerMsg(1)), true);
							} else if (stra == 2) {
								if (world instanceof Level level) {
										level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.SILENCE2.get(),
												SoundSource.NEUTRAL, 6, 1);
								}
								if (entityiterator instanceof Player player && !player.level().isClientSide())
									player.displayClientMessage(Component.literal(getSilenceUnlockPlayerMsg(2)), true);
							} else if (stra == 3) {
								if (world instanceof Level level) {
										level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.SILENCE3.get(),
												SoundSource.NEUTRAL, 6, 1);
								}
								if (entityiterator instanceof Player player && !player.level().isClientSide())
									player.displayClientMessage(Component.literal(getSilenceUnlockPlayerMsg(3)), true);
							} else if (stra == 4) {
								if (world instanceof Level level) {
										level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.SILENCE4.get(),
												SoundSource.NEUTRAL, 6, 1);
								}
								if (entityiterator instanceof Player player && !player.level().isClientSide())
									player.displayClientMessage(Component.literal(getSilenceUnlockPlayerMsg(4)), true);
							}
						}
					}
					if (!world.isClientSide() && world.getServer() != null)
						world.getServer().getPlayerList().broadcastSystemMessage(Component.literal((prefix + Component.translatable("item.caerula_arbor.language_key.description_4").getString().replace("{num}", num))), false);
				}
			} else {
				MapVariablesHandler.setEvoPoint(world, StrategyType.SILENCE, 0);
				for (Player entityiterator : new ArrayList<>(world.players())) {
					if (entityiterator instanceof ServerPlayer player) {
						AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "hymn_of_land"));
						if (adv == null) continue;
						AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
						if (!ap.isDone()) {
							for (String criteria : ap.getRemainingCriteria())
								player.getAdvancements().award(adv, criteria);
						}
					}
				}
			}
		} else {
			MapVariablesHandler.setStrategyLevel(world, StrategyType.SILENCE, 0);
		}
	}

	public static boolean isSilence(LevelAccessor world) {
		return MapVariables.get(world).strategy_silence > 0;
	}

	public static boolean canEnableSilence(LevelAccessor world) {
		return MapVariables.get(world).strategy_grow >= 4 && MapVariables.get(world).strategy_subsisting >= 4 && MapVariables.get(world).strategy_breed >= 4
				&& MapVariables.get(world).strategy_migration >= 4 && MapVariables.get(world).silence_enabled;
	}

	public static double getStraSilence(LevelAccessor world) {
		return MapVariables.get(world).strategy_silence;
	}

	public static String getSilenceMigration(LevelAccessor world) {
		return MigrationUpgradeManager.getDescrSilenceMigra(world);
	}

	public static String getSilenceSubsis(LevelAccessor world) {
		return SubsistingUpgradeManager.getDescrSilenceSubsis(world);
	}

	public static String getSilenceBreed(LevelAccessor world) {
		return BreedUpgradeManager.getDescrSilenceBreed(world);
	}

	public static String getSilenceGrow(LevelAccessor world) {
		return GrowUpgradeManager.getDescrSilenceGrow(world);
	}

	public static String getCmdFeedback(long lvl) {
		return Component.translatable("command.evolution.silence").getString().replace("<num>", "" + lvl);
	}

	public static String getSilenceUnlockPlayerMsg(long lvl) {
		if (lvl == 1) {
			return Component.translatable("item.caerula_arbor.language_key.description_6").getString();
		} else if (lvl == 2) {
			return Component.translatable("item.caerula_arbor.language_key.description_7").getString();
		} else if (lvl == 3) {
			return Component.translatable("item.caerula_arbor.language_key.description_8").getString();
		} else {
			return Component.translatable("item.caerula_arbor.language_key.description_9").getString();
		}
	}

	public static String getSilenceLockedMsg() {
		return Component.translatable("item.caerula_arbor.language_key.description_5").getString();
	}

	// 静谧音量系数双缓存：target=每5tick扫描重算的目标值，cached=每tick向target指数平滑的展示值；声音事件仅O(1)读cached
	private static float cachedVolumeScale = 1.0F;
	private static float targetVolumeScale = 1.0F;
	// 平滑系数：等价原版Mth.lerp，每tick收敛剩余差距的20%，约0.5秒逼近目标约九成，滤掉海嗣进出边界的音量台阶
	private static final float VOLUME_SMOOTHING = 0.2F;

	// 削减率基准(等级1=5%)配合静谧等级每级+1%，海嗣充足时可压到 0 完全静音
	private static final double REDUCTION_BASE = 0.04;
	private static final float MIN_VOLUME = 0.0F;
	// 基础范围内每多一只海嗣，动态半径再扩大 1 格，封顶为基础+32
	private static final double RANGE_GROWTH_PER_SEABORN = 1.0;
	private static final double MAX_RANGE_GROWTH = 32.0;

	// 伊莎玛拉及其怪物形态的发声路径前缀，不参与削减
	private static final String ISHARMLA_PREFIX = "isharmla";
	// 静谧声音键(silence1~4)本身就是伊莎玛拉的发声，精确匹配、同样不参与削减
	private static final Set<String> SILENCE_KEYS = Set.of("silence1", "silence2", "silence3", "silence4");

	/**
	 * 每 5 tick 重算静谧音量系数**目标值**(静谧等级与附近海嗣多寡共同决定)；
	 * 展示值由 {@link #tickSmoothVolumeScale()} 每 tick 向该目标平滑，切勿在此直改展示值。
	 *
	 * @param player 当前客户端玩家，用于读取静谧等级与统计附近海嗣
	 */
	public static void tickVolumeScale(Player player) {
		double silenceLevel = getStraSilence(player.level());
		if (silenceLevel <= 0) {
			// 静谧未解锁时无削减需求，目标系数恒为 1
			targetVolumeScale = 1.0F;
		} else {
			double reductionPerSeaborn = REDUCTION_BASE + silenceLevel / 100.0;
			targetVolumeScale = (float) Math.max(1.0 - reductionPerSeaborn * countNearbySeaMonster(player, silenceLevel), MIN_VOLUME);
		}
	}

	/**
	 * 每 tick 将展示系数向目标值指数平滑逼近，切掉海嗣进出边界造成的目标瞬时跳变。
	 */
	public static void tickSmoothVolumeScale() {
		cachedVolumeScale = Mth.lerp(VOLUME_SMOOTHING, cachedVolumeScale, targetVolumeScale);
	}

	/**
	 * 供 {@code SoundEngineMixin} 在音量算出后统一缩放；伊莎玛拉发声保持原样。
	 *
	 * @param inst   当前正在计算音量的声音实例
	 * @param volume clamp 之后、分类音量叠乘之后的最终音量
	 * @return 缩放后的音量
	 */
	public static float scaleVolume(SoundInstance inst, float volume) {
		if (inst != null && cachedVolumeScale < 1.0F) {
			// 伊莎玛拉与静谧(即伊莎玛拉的silence1~4)发声保持原样，不参与削减
			String path = inst.getLocation().getPath();
			if (!path.startsWith(ISHARMLA_PREFIX) && !SILENCE_KEYS.contains(path)) {
				return volume * cachedVolumeScale;
			}
		}
		return volume;
	}

	private static double countNearbySeaMonster(Player player, double silenceLevel) {
		// 单次以最大动态范围扫描，同时完成基数统计与距离权重，避免两次 getEntitiesOfClass
		double baseRange = baseRange(silenceLevel);
		double maxRange = baseRange + MAX_RANGE_GROWTH;
		List<SeaMonster> seamonsters = player.level().getEntitiesOfClass(SeaMonster.class, player.getBoundingBox().inflate(maxRange));
		// baseCount：与基础范围包围盒相交的海嗣数，用于决定动态半径(与 baseRange 内 getEntitiesOfClass 等价，因 maxRange 覆盖 baseRange)
		AABB baseAab = player.getBoundingBox().inflate(baseRange);
		double baseCount = 0;
		for (SeaMonster seamonster : seamonsters) {
			if (seamonster.getBoundingBox().intersects(baseAab)) {
				baseCount++;
			}
		}
		double finalRange = Math.min(baseRange + baseCount * RANGE_GROWTH_PER_SEABORN, maxRange);
		// 距离衰减：海嗣距玩家越远权重越低(按基础范围平方递减)，基础范围内满权重、远处趋零；全程用平方距离避免开方
		double finalRangeSqr = finalRange * finalRange;
		double baseRangeSqr = baseRange * baseRange;
		double totalWeight = 0.0;
		for (SeaMonster seamonster : seamonsters) {
			double distanceSqr = player.distanceToSqr(seamonster);
			if (distanceSqr > finalRangeSqr) {
				continue;
			}
			double weight = 1.0 - distanceSqr / baseRangeSqr;
			totalWeight += Math.max(weight, 0.0);
		}
		return totalWeight;
	}

	private static double baseRange(double silenceLevel) {
		// 静谧等级 1/2/3/4 对应基础范围 16/24/32/40，共4层递增
		if (silenceLevel <= 1) {
			return 16.0;
		}
		if (silenceLevel <= 2) {
			return 24.0;
		}
		if (silenceLevel <= 3) {
			return 32.0;
		}
		return 40.0;
	}
}