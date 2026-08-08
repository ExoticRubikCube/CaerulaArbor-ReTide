package com.susen36.caerulaarbor.util;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.Relic;
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
		return ModCapabilities.getPlayerVariables(entity);
	}

	public static boolean hasSpear(Entity entity) {
		return Relic.KING_SPEAR.gained(entity);
	}

	public static boolean hasArmor(Entity entity) {
		return Relic.KING_ARMOR.gained(entity);
	}

	public static boolean hasExtension(Entity entity) {
		return Relic.KING_EXTENSION.gained(entity);
	}

	public static boolean hasCrystal(Entity entity) {
		return Relic.KING_CRYSTAL.gained(entity);
	}

	public static boolean hasThorns(Entity entity) {
		return Relic.HAND_THORNS.gained(entity);
	}

	public static boolean hasStrangle(Entity entity) {
		return Relic.HAND_STRANGLE.gained(entity);
	}

	public static boolean hasFertility(Entity entity) {
		return Relic.HAND_FERTILITY.gained(entity);
	}

	public static boolean hasSpeed(Entity entity) {
		return Relic.HAND_SPEED.gained(entity);
	}

	public static boolean hasBarren(Entity entity) {
		return Relic.HAND_OF_PULVERIZATION.gained(entity);
	}

	public static boolean hasSwipe(Entity entity) {
		return Relic.HAND_SWIPE.gained(entity);
	}

	public static boolean hasArtifact(Entity entity) {
		return Relic.SARKAZ_KING_ARTIFACT.gained(entity);
	}

	public static boolean hasFirework(Entity entity) {
		return Relic.HAND_FIREWORK.gained(entity);
	}

	public static boolean hasFlag(Entity entity) {
		return Relic.SARKAZ_KING_FLAG.gained(entity);
	}

	public static double getEngrave(Entity entity) {
		return Relic.HAND_ENGRAVE.get(entity);
	}

	public static boolean hasBed(Entity entity) {
		return Relic.SARKAZ_KING_BED.gained(entity);
	}

	public static double getSurvivor(Entity entity) {
		return Relic.SURVIVOR_CONTRACT.get(entity);
	}

	public static boolean hasTreaty(Entity entity) {
		return Relic.TREATY.gained(entity);
	}

	public static boolean hasRylfate(Entity entity) {
		return Relic.SARKAZ_KING_RYLFATE.gained(entity);
	}

	public static boolean hasMeatcan(Entity entity) {
		return Relic.FEATURED_CANNED_MEAT.gained(entity);
	}

	public static boolean hasSeagrass(Entity entity) {
		return Relic.SEAWEED_SALAD.gained(entity);
	}

	public static boolean hasOrange(Entity entity) {
		return Relic.ORANGE_STORM.gained(entity);
	}

	public static boolean hasCoffee(Entity entity) {
		return Relic.COFFEE_PLAINS_COFFEE_CANDY.gained(entity);
	}

	public static boolean hasBerries(Entity entity) {
		return Relic.PITTS_ASSORTED_FRUITS.gained(entity);
	}

	public static boolean hasMusicbox(Entity entity) {
		return Relic.UTIL_MUSICBOX.gained(entity);
	}

	public static boolean hasIris(Entity entity) {
		return Relic.UTIL_IRIS.gained(entity);
	}

	public static boolean hasFlute(Entity entity) {
		return Relic.WEIRD_FLUTE.gained(entity);
	}

	public static boolean hasVoygold(Entity entity) {
		return Relic.PURE_GOLD_EXPEDITION.gained(entity);
	}

	public static boolean hasDurin(Entity entity) {
		return Relic.DURIN_OVERGROUND_ODYSSEY.gained(entity);
	}

	public static boolean hasToponym(Entity entity) {
		return Relic.UTIL_TOPONYM.gained(entity);
	}

	public static boolean hasKettle(Entity entity) {
		return Relic.HOT_WATER_KETTLE.gained(entity);
	}

	public static boolean hasChitin(Entity entity) {
		return Relic.LEGEND_CHITIN.gained(entity);
	}

	public static boolean hasAlley(Entity entity) {
		return Relic.UTIL_ALLEY.gained(entity);
	}

	public static boolean hasBatbed(Entity entity) {
		return Relic.VAMPIRES_BED.gained(entity);
	}

	public static boolean hasLongevity(Entity entity) {
		return Relic.PROOF_OF_LONGEVITY.gained(entity);
	}

	public static boolean hasOmnikey(Entity entity) {
		return Relic.UTIL_OMNIKEY.gained(entity);
	}

	public static boolean hasScore(Entity entity) {
		return Relic.UTIL_SCORE.gained(entity);
	}

	public static boolean hasRescission(Entity entity) {
		return Relic.UTIL_RESCISSION.gained(entity);
	}

	public static boolean hasStare(Entity entity) {
		return Relic.UTIL_STARE.gained(entity);
	}

	public static boolean hasSword(Entity entity) {
		return Relic.HAND_SWORD.gained(entity);
	}

	public static boolean hasEmelight(Entity entity) {
		return Relic.CURSED_EMELIGHT.gained(entity);
	}

	public static boolean hasGlowbody(Entity entity) {
		return Relic.CURSED_GLOWBODY.gained(entity);
	}

	public static boolean hasResearch(Entity entity) {
		return Relic.CURSED_RESEARCH.gained(entity);
	}

	public static boolean hasCrown(Entity entity) {
		return Relic.KING_CROWN.gained(entity);
	}

	public static boolean hasHeart(Entity entity) {
		return Relic.CURSED_HEART.gained(entity);
	}

	public static boolean hasHemost(Entity entity) {
		return Relic.HEMOST.gained(entity);
	}

	public static boolean hasYearning(Entity entity) {
		return Relic.YEARNING.gained(entity);
	}

	public static boolean hasAllay(Entity entity) {
		return Relic.UTIL_ALLAY.gained(entity);
	}

	public static boolean hasRainbow(Entity entity) {
		return Relic.UTIL_RAINBOW.gained(entity);
	}

	public static boolean hasDiso(Entity entity) {
		return Relic.DISO.gained(entity);
	}

	public static boolean hasDisoFlesh(Entity entity) {
		return Relic.DISO_FLESH.gained(entity);
	}

	public static boolean hasDisoBlood(Entity entity) {
		return Relic.DISO_BLOOD.gained(entity);
	}

	public static boolean hasDisoNeuro(Entity entity) {
		return Relic.DISO_NEURO.gained(entity);
	}

	public static boolean hasAhndSwipe(Entity entity) {
		return Relic.AHND_SWIPE.gained(entity);
	}

	public static boolean hasDisoAttention(Entity entity) {
		return Relic.DISO_ATTENTION.gained(entity);
	}

	public static boolean hasHanshandSpike(Entity entity) {
		return Relic.HANSHAND_SPIKE.gained(entity);
	}

	public static boolean hasRoyalfate(Entity entity) {
		return Relic.ROYALFATE.gained(entity);
	}

	public static void gainArmor(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		PlayerVariable playerVariables = ModCapabilities.getPlayerVariables(entity);
		if (Relic.KING_ARMOR.gained(playerVariables))
			return;

		BlockPos pos = BlockPos.containing(x, y, z);
		double storedLives = playerVariables.player_lives;

		if (world instanceof Level level) {
			level.playSound(null, pos, SoundEvents.TOTEM_USE, SoundSource.NEUTRAL, 2, 1);
		}
		if (world instanceof ServerLevel level)
			level.sendParticles(ParticleTypes.ENCHANTED_HIT, x, y, z, 72, 1, 1, 1, 1);

		PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
		Relic.KING_ARMOR.gainAndSync(capability, entity);

		if (world.isClientSide())
			Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);

		if (storedLives > 1) {
			playerVariables.player_lives = 1;
			playerVariables.syncPlayerVariables(entity);
		}

		double shieldAfterLifeTransfer = playerVariables.player_shield + storedLives;
		capability.player_shield = shieldAfterLifeTransfer;
		capability.syncPlayerVariables(entity);

		capability.player_shield = shieldAfterLifeTransfer + 3;
		capability.syncPlayerVariables(entity);
	}

	public static void gainSpear(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity != null && !hasSpear(entity)) {
			if (world instanceof Level level) {
				level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.TOTEM_USE, SoundSource.NEUTRAL, 2, 1);
				if (world instanceof ServerLevel serverLevel)
					serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, x, y, z, 72, 1, 1, 1, 1);
			}
			PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
			Relic.KING_SPEAR.gainAndSync(capability, entity);
			if (world.isClientSide())
				Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
		}
	}
}
