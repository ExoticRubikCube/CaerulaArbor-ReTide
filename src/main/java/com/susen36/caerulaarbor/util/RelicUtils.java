package com.susen36.caerulaarbor.util;

import com.susen36.babel.collectible.Collectibles;
import com.susen36.babel.network.BabelNetwork;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class RelicUtils {
	//TODO:准备迁移到巴别塔lib
	private RelicUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static boolean hasRelic(Item relic, Entity entity) {
		return entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(relic);
	}

	public static double getRelic(Item relic, Entity entity) {
		return entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).getLayer(relic);
	}

	public static void gainArmor(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		PlayerVariable playerVariables = ModCapabilities.getPlayerVariables(entity);
		if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.KING_ARMOR.get()))
			return;

		BlockPos pos = BlockPos.containing(x, y, z);
		double storedLives = playerVariables.player_lives;

		if (world instanceof Level level) {
			level.playSound(null, pos, SoundEvents.TOTEM_USE, SoundSource.NEUTRAL, 2, 1);
		}
		if (world instanceof ServerLevel level)
			level.sendParticles(ParticleTypes.ENCHANTED_HIT, x, y, z, 72, 1, 1, 1, 1);

		entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).markUsed(CAItems.KING_ARMOR.get());
		if (entity instanceof Player player)
			BabelNetwork.syncCollectibles(player);

		if (world.isClientSide())
			Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);

		if (storedLives > 1) {
			playerVariables.player_lives = 1;
			playerVariables.syncPlayerVariables(entity);
		}

		double shieldAfterLifeTransfer = playerVariables.player_shield + storedLives;
		playerVariables.player_shield = shieldAfterLifeTransfer;
		playerVariables.syncPlayerVariables(entity);

		playerVariables.player_shield = shieldAfterLifeTransfer + 3;
		playerVariables.syncPlayerVariables(entity);
	}

	public static void gainSpear(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity != null && !hasRelic(CAItems.KING_SPEAR.get(), entity)) {
			if (world instanceof Level level) {
				level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.TOTEM_USE, SoundSource.NEUTRAL, 2, 1);
				if (world instanceof ServerLevel serverLevel)
					serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, x, y, z, 72, 1, 1, 1, 1);
			}
			entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).markUsed(CAItems.KING_SPEAR.get());
			if (entity instanceof Player player)
				BabelNetwork.syncCollectibles(player);
			if (world.isClientSide())
				Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
		}
	}
}
