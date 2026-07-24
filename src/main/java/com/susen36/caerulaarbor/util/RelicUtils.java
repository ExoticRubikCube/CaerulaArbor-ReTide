package com.susen36.caerulaarbor.util;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class RelicUtils {

	private RelicUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	private static PlayerVariable getPlayerVariables(Entity entity) {
		if (entity == null)
			return new PlayerVariable();
		return entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable());
	}

	public static boolean hasSpear(Entity entity) {
		return getPlayerVariables(entity).relic_king_SPEAR;
	}

	public static boolean hasArmor(Entity entity) {
		return getPlayerVariables(entity).relic_king_ARMOR;
	}

	public static boolean hasExtension(Entity entity) {
		return getPlayerVariables(entity).relic_king_EXTENSION;
	}

	public static boolean hasCrystal(Entity entity) {
		return getPlayerVariables(entity).relic_king_CRYSTAL;
	}

	public static boolean hasThorns(Entity entity) {
		return getPlayerVariables(entity).relic_hand_THORNS;
	}

	public static boolean hasStrangle(Entity entity) {
		return getPlayerVariables(entity).relic_hand_STRANGLE;
	}

	public static boolean hasFertility(Entity entity) {
		return getPlayerVariables(entity).relic_hand_FERTILITY;
	}

	public static boolean hasSpeed(Entity entity) {
		return getPlayerVariables(entity).relic_hand_SPEED;
	}

	public static boolean hasBarren(Entity entity) {
		return getPlayerVariables(entity).relic_hand_BARREN;
	}

	public static boolean hasSwipe(Entity entity) {
		return getPlayerVariables(entity).relic_hand_SWIPE;
	}

	public static boolean hasArtifact(Entity entity) {
		return getPlayerVariables(entity).relic_archfi_ARTIFACT;
	}

	public static boolean hasFirework(Entity entity) {
		return getPlayerVariables(entity).relic_hand_FIREWORK;
	}

	public static boolean hasFlag(Entity entity) {
		return getPlayerVariables(entity).relic_archfi_FLAG;
	}

	public static double getEngrave(Entity entity) {
		return getPlayerVariables(entity).relic_hand_ENGRAVE;
	}

	public static boolean hasBed(Entity entity) {
		return getPlayerVariables(entity).relic_archfi_BED;
	}

	public static double getSurvivor(Entity entity) {
		return getPlayerVariables(entity).relic_SURVIVOR;
	}

	public static boolean hasTreaty(Entity entity) {
		return getPlayerVariables(entity).relic_TREATY;
	}

	public static boolean hasRylfate(Entity entity) {
		return getPlayerVariables(entity).relic_archifi_RYLFATE;
	}

	public static boolean hasMeatcan(Entity entity) {
		return getPlayerVariables(entity).relic_util_MEATCAN;
	}

	public static boolean hasSeagrass(Entity entity) {
		return getPlayerVariables(entity).relic_util_SEAGRASS;
	}

	public static boolean hasOrange(Entity entity) {
		return getPlayerVariables(entity).relic_util_ORANGE;
	}

	public static boolean hasCoffee(Entity entity) {
		return getPlayerVariables(entity).relic_util_COFFEE;
	}

	public static boolean hasBerries(Entity entity) {
		return getPlayerVariables(entity).relic_util_BERRIES;
	}

	public static boolean hasMusicbox(Entity entity) {
		return getPlayerVariables(entity).relic_util_MUSICBOX;
	}

	public static boolean hasIris(Entity entity) {
		return getPlayerVariables(entity).relic_util_IRIS;
	}

	public static boolean hasFlute(Entity entity) {
		return getPlayerVariables(entity).relic_util_FLUTE;
	}

	public static boolean hasVoygold(Entity entity) {
		return getPlayerVariables(entity).relic_util_VOYGOLD;
	}

	public static boolean hasDurin(Entity entity) {
		return getPlayerVariables(entity).relic_util_DURIN;
	}

	public static boolean hasToponym(Entity entity) {
		return getPlayerVariables(entity).relic_util_TOPONYM;
	}

	public static boolean hasKettle(Entity entity) {
		return getPlayerVariables(entity).relic_util_KETTLE;
	}

	public static boolean hasChitin(Entity entity) {
		return getPlayerVariables(entity).relic_legend_CHITIN;
	}

	public static boolean hasAlley(Entity entity) {
		return getPlayerVariables(entity).relic_util_ALLEY;
	}

	public static boolean hasBatbed(Entity entity) {
		return getPlayerVariables(entity).relic_util_BATBED;
	}

	public static boolean hasLongevity(Entity entity) {
		return getPlayerVariables(entity).relic_util_LONGEVITY;
	}

	public static boolean hasOmnikey(Entity entity) {
		return getPlayerVariables(entity).relic_util_OMNIKEY;
	}

	public static boolean hasScore(Entity entity) {
		return getPlayerVariables(entity).relic_util_score;
	}

	public static boolean hasRescission(Entity entity) {
		return getPlayerVariables(entity).relic_util_RESCISSION;
	}

	public static boolean hasStare(Entity entity) {
		return getPlayerVariables(entity).relic_util_STARE;
	}

	public static boolean hasSword(Entity entity) {
		return getPlayerVariables(entity).relic_hand_SWORD;
	}

	public static boolean hasEmelight(Entity entity) {
		return getPlayerVariables(entity).relic_cursed_EMELIGHT;
	}

	public static boolean hasGlowbody(Entity entity) {
		return getPlayerVariables(entity).relic_cursed_GLOWBODY;
	}

	public static boolean hasResearch(Entity entity) {
		return getPlayerVariables(entity).relic_cursed_RESEARCH;
	}

	public static boolean hasCrown(Entity entity) {
		return getPlayerVariables(entity).relic_king_CROWN;
	}

	public static boolean hasHeart(Entity entity) {
		return getPlayerVariables(entity).relic_cursed_HEART;
	}

	public static boolean hasHemost(Entity entity) {
		return getPlayerVariables(entity).relic_HEMOST;
	}

	public static boolean hasYearning(Entity entity) {
		return getPlayerVariables(entity).relic_YEARNING;
	}

	public static boolean hasAllay(Entity entity) {
		return getPlayerVariables(entity).relic_util_ALLAY;
	}

	public static boolean hasRainbow(Entity entity) {
		return getPlayerVariables(entity).relic_util_RAINBOW;
	}

	public static boolean hasDiso(Entity entity) {
		return getPlayerVariables(entity).relic_diso;
	}

	public static boolean hasDisoFlesh(Entity entity) {
		return getPlayerVariables(entity).relic_diso_FLESH;
	}

	public static boolean hasDisoBlood(Entity entity) {
		return getPlayerVariables(entity).relic_diso_BLOOD;
	}

	public static boolean hasDisoNeuro(Entity entity) {
		return getPlayerVariables(entity).relic_diso_NEURO;
	}

	public static boolean hasAhndSwipe(Entity entity) {
		return getPlayerVariables(entity).relic_ahnd_SWIPE;
	}

	public static boolean hasDisoAttention(Entity entity) {
		return getPlayerVariables(entity).relic_diso_ATTENTION;
	}

	public static boolean hasHanshandSpike(Entity entity) {
		return getPlayerVariables(entity).relic_hanshand_SPIKE;
	}

	public static boolean hasRoyalfate(Entity entity) {
		return getPlayerVariables(entity).relic_royalfate;
	}

	public static void gainArmor(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		PlayerVariable playerVariables = entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable());
		if (playerVariables.relic_king_ARMOR)
			return;

		BlockPos pos = BlockPos.containing(x, y, z);
		double storedLives = playerVariables.player_lives;

		if (world instanceof Level level) {
			level.playSound(null, pos, SoundEvents.TOTEM_USE, SoundSource.NEUTRAL, 2, 1);
		}
		if (world instanceof ServerLevel level)
			level.sendParticles(ParticleTypes.ENCHANTED_HIT, x, y, z, 72, 1, 1, 1, 1);

		entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
			capability.relic_king_ARMOR = true;
			capability.syncPlayerVariables(entity);
		});

		if (world.isClientSide())
			Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);

		if (storedLives > 1) {
			entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
				capability.player_lives = 1;
				capability.syncPlayerVariables(entity);
			});
		}

		double shieldAfterLifeTransfer = playerVariables.player_shield + storedLives;
		entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
			capability.player_shield = shieldAfterLifeTransfer;
			capability.syncPlayerVariables(entity);
		});

		entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
			capability.player_shield = shieldAfterLifeTransfer + 3;
			capability.syncPlayerVariables(entity);
		});
	}

	public static void gainSpear(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity != null && !hasSpear(entity)) {
			if (world instanceof Level level) {
				level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.TOTEM_USE, SoundSource.NEUTRAL, 2, 1);
				if (world instanceof ServerLevel serverLevel)
					serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, x, y, z, 72, 1, 1, 1, 1);
			}
            entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
				capability.relic_king_SPEAR = true;
				capability.syncPlayerVariables(entity);
			});
			if (world.isClientSide())
				Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
		}
	}
}