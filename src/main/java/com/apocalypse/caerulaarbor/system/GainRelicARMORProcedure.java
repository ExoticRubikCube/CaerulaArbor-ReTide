package com.apocalypse.caerulaarbor.system;

import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

// TODO: 是公共方法,但可能需要评估怎么安置
public class GainRelicARMORProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		PlayerVariable playerVariables = entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null)
				.orElse(new PlayerVariable());
		if (playerVariables.relic_king_ARMOR)
			return;

		BlockPos pos = BlockPos.containing(x, y, z);
		double storedLives = playerVariables.player_lives;

		if (world instanceof Level level) {
			level.playSound(null, pos, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.totem.use")), SoundSource.NEUTRAL, 2, 1);
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
}
