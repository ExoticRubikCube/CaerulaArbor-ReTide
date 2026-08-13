package com.susen36.caerulaarbor.util;

import com.susen36.babel.collectible.Collectibles;
import com.susen36.babel.effect.LessArmorMobEffect;
import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAConfigs;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.ArrayList;

public class PlayerStateUtils {

	private PlayerStateUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	// 生命点数
	public static int getLifePoint(Entity entity) {
		return (int) ModCapabilities.getPlayerVariables(entity).player_lives;
	}

	public static void setLifePoint(Entity entity, int value) {
		if (value < 1)
			return;
		int maxPoint = getMaxLifePoint(entity);
		PlayerVariable c = ModCapabilities.getPlayerVariables(entity);
		c.player_lives = Math.min(value, maxPoint);
		c.syncPlayerVariables(entity);
	}

	public static int getMaxLifePoint(Entity entity) {
		return (int) ModCapabilities.getPlayerVariables(entity).player_maxlive;
	}

	public static void setMaxLifePoint(Entity entity, int value) {
		if (value < 1)
			return;
		int clampedValue = (int) Math.min(value, CAConfigs.LP_LIMIT.get());
		PlayerVariable c = ModCapabilities.getPlayerVariables(entity);
		c.player_maxlive = clampedValue;
		c.syncPlayerVariables(entity);
		if (clampedValue < getLifePoint(entity))
			setLifePoint(entity, clampedValue);
	}

	// 护盾点数
	public static int getShieldPoint(Entity entity) {
		return (int) ModCapabilities.getPlayerVariables(entity).player_shield;
	}

	public static void setShieldPoint(Entity entity, int value) {
		if (value < 0)
			return;
		int clampedValue = (int) Math.min(value, CAConfigs.SHIELD_LIMIT.get());
		PlayerVariable c = ModCapabilities.getPlayerVariables(entity);
		c.player_shield = clampedValue;
		c.syncPlayerVariables(entity);
	}

	// 光芒值
	public static double getPlayerLight(Entity entity) {
		return ModCapabilities.getPlayerVariables(entity).player_light;
	}

	public static void setLights(Entity entity, double value) {
		PlayerVariable c = ModCapabilities.getPlayerVariables(entity);
		c.player_light = Mth.clamp(value, 0, 100);
		c.syncPlayerVariables(entity);
	}

	public static void reviveLights(Entity entity, double value) {
		EntityUtils.restorePlayerLights(entity, value);
	}

	public static boolean isLightWaving(Entity entity) {
		double light = getPlayerLight(entity);
		return 50 <= light && light < 85;
	}

	public static boolean isLightBright(Entity entity) {
		return 85 <= getPlayerLight(entity);
	}

	public static boolean isLightCeased(Entity entity) {
		return getPlayerLight(entity) < 1;
	}

	public static boolean isLightDim(Entity entity) {
		double light = getPlayerLight(entity);
		return 1 <= light && light < 50;
	}

	// 护甲侵蚀
	public static void armorErrosion(Entity entity, int amount, int limit) {
		for (int i = 0; i < amount; i++) {
			if (entity instanceof LivingEntity living) {
				LessArmorMobEffect.apply(living);
			}
		}
	}

	public static void pokeSlightly(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null) {
			return;
		}
		ItemStack mainhand = (entity instanceof LivingEntity living ? living.getMainHandItem() : ItemStack.EMPTY);
		if (mainhand.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "nethersea_protective")))) {
			return;
		}
		if (entity instanceof LivingEntity livingEntity && ModCapabilities.getPlayerVariables(entity).player_oceanization < 3) {
			EPUtils.causeSanityInjury(livingEntity, Mth.nextInt(RandomSource.create(), 16, 32));
		}
		if (world instanceof ServerLevel level) {
			level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x + 0.5, y + 0.5, z + 0.5, 12, 0.75, 0.75, 0.75, 0.1);
		}
	}

	public static void pokePlayer(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null) {
			return;
		}
		ItemStack mainhand = (entity instanceof LivingEntity living ? living.getMainHandItem() : ItemStack.EMPTY);
		if (mainhand.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "nethersea_protective")))) {
			return;
		}
		if (entity instanceof LivingEntity livingEntity && ModCapabilities.getPlayerVariables(entity).player_oceanization < 2.85) {
			EPUtils.causeSanityInjury(livingEntity, Mth.nextInt(RandomSource.create(), 32, 96));
		}
		if (world instanceof ServerLevel level) {
			level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x + 0.5, y + 0.5, z + 0.5, 16, 0.75, 0.75, 0.75, 0.1);
		}
	}

	public static void replaceTrail(LevelAccessor world, BlockState toPlace, boolean water, double x, double y, double z) {
		if (!world.isClientSide()) {
			BlockPos pos = BlockPos.containing(x, y, z);
			if (world.getBlockState(pos).getDestroySpeed(world, BlockPos.ZERO) >= 0) {
				for (Entity player : new ArrayList<>(world.players())) {
					if (player instanceof ServerPlayer serverPlayer) {
						AdvancementHolder advancement = serverPlayer.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "start_of_calamity"));
						AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
						if (!progress.isDone()) {
							for (String criteria : progress.getRemainingCriteria()) {
								serverPlayer.getAdvancements().award(advancement, criteria);
							}
						}
					}
				}
				Block.dropResources(world.getBlockState(pos), world, BlockPos.containing(x + 0.5, y, z + 0.5), null);
				world.destroyBlock(pos, false);
				world.setBlock(pos, toPlace.getBlock().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty waterloggedProperty ? toPlace.setValue(waterloggedProperty, water) : toPlace, 3);
				world.levelEvent(2001, pos, Block.getId(CABlocks.SEA_TRAIL_INIT.get().defaultBlockState()));
				if (world instanceof Level level) {
					level.playSound(null, pos, SoundEvents.SCULK_VEIN_PLACE, SoundSource.NEUTRAL, 1, 1);
				}
			}
		}
	}

	/**
	 * 根据配置规则匹配注册名。
	 * <p>
	 * 支持 {@code *} 全匹配、完整注册名精确匹配，以及形如 {@code prefix*} 的前缀匹配。
	 * 不支持通配符出现在开头或中间的复杂匹配形式。
	 *
	 * @param item 用于匹配的配置规则
	 * @param name 待检测的完整注册名
	 * @return 当规则匹配该注册名时返回 {@code true}，否则返回 {@code false}
	 */
	public static boolean matchesRegistryName(String item, String name) {
		if (item == null || name == null) {
			return false;
		}
		if ("*".equals(item) || name.equals(item)) {
			return true;
		}
		int wildcardIndex = item.indexOf('*');
		return wildcardIndex > 0 && name.startsWith(item.substring(0, wildcardIndex));
	}

	public static boolean canPlayerEvo(Entity entity) {
		return ModCapabilities.getPlayerVariables(entity).can_player_evo;
	}

	public static double getSurvivor(Entity entity) {
		return entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).getLayer(CAItems.SURVIVOR_CONTRACT.get());
	}

	public static boolean hasSurvivorCont(Entity entity) {
		return getSurvivor(entity) >= 0;
	}

	public static boolean hasAromatic(Entity entity) {
		return ModCapabilities.getPlayerVariables(entity).player_util_AROMATIC;
	}
}