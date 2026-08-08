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

	public static boolean hasRelic(Relic relic, Entity entity) {
		return relic.gained(entity);
	}

	public static double getRelic(Relic relic, Entity entity) {
		return relic.get(entity);
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
		if (entity != null && !hasRelic(Relic.KING_SPEAR, entity)) {
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
