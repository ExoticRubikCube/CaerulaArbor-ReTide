package com.apocalypse.caerulaarbor.util;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.init.CABlocks;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

public class CaerulaUtil {
	// Shared utility methods migrated from procedures.
	// Life points
	public static int getLifePoint(Player player){
		return (int) player.getCapability(ModCapabilities.PLAYER_VARIABLE, null)
		.orElse(new PlayerVariable()).player_lives;
	}

	public static void setLifePoint(Player player, int value){
		if(value < 1) return;
		int maxPoint = getMaxLifePoint(player);
		player.getCapability(ModCapabilities.PLAYER_VARIABLE, null)
		.ifPresent(c -> {
			c.player_lives = Math.min(value,maxPoint);
			c.syncPlayerVariables(player);
		});
	}

	public static int getMaxLifePoint(Player player){
		return (int) player.getCapability(ModCapabilities.PLAYER_VARIABLE, null)
		.orElse(new PlayerVariable()).player_maxlive;
	}

	public static void setMaxLifePoint(Player player, int value){
		if(value < 1) return;
		player.getCapability(ModCapabilities.PLAYER_VARIABLE, null)
		.ifPresent(c -> {
			c.player_maxlive = value;
			c.syncPlayerVariables(player);
		});
		if (value < getLifePoint(player)) setLifePoint(player, value);
	}

	// Shield points
	public static int getShieldPoint(Player player){
		return (int) player.getCapability(ModCapabilities.PLAYER_VARIABLE, null)
		.orElse(new PlayerVariable()).player_shield;
	}
	public static void setShieldPoint(Player player, int value){
		if(value < 0) return;
		player.getCapability(ModCapabilities.PLAYER_VARIABLE, null)
		.ifPresent(c -> {
			c.player_shield = value;
			c.syncPlayerVariables(player);
		});
	}

	// Lights
	public static double getLights(Player player){
		return player.getCapability(ModCapabilities.PLAYER_VARIABLE, null)
		.orElse(new PlayerVariable()).player_light;
	}
	public static void setLights(Player player, double value){
		player.getCapability(ModCapabilities.PLAYER_VARIABLE, null)
		.ifPresent(c -> {
			c.player_light = Mth.clamp(value, 0, 100);
			c.syncPlayerVariables(player);
		});
	}
	public static void reviveLights(Player player, double value){
		EntityUtils.restorePlayerLights(player, value);
	}

	// Sanity injury
	public static void dealSanityInjury(LivingEntity living, double amount){
		SIHelper.causeSanityInjury(living, amount);
	}
	public static void healSanityInjury(LivingEntity living, double amount){
		ModCapabilities.getSanityInjury(living).heal(amount);
	}

	// Armor erosion
	public static void armorErrosion(Entity entity, int amount, int limit){
		for (int i=0;i<amount;i++){
			EntityUtils.giveLessArmor(entity, limit);
		}
	}

	public static void replaceTrail(LevelAccessor world, BlockState toPlace, boolean water, double x, double y, double z) {
		if (!world.isClientSide()) {
			BlockPos pos = BlockPos.containing(x, y, z);
			if (world.getBlockState(pos).getDestroySpeed(world, BlockPos.ZERO) >= 0) {
				for (Entity player : new ArrayList<>(world.players())) {
					if (player instanceof ServerPlayer serverPlayer) {
						Advancement advancement = serverPlayer.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "start_of_calamity"));
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
					level.playSound(null, pos, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.place")), SoundSource.NEUTRAL, 1, 1);
				}
			}
		}
	}

	public static class Tags{
		public static final TagKey<EntityType<?>> SEABORNS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring"));
	}
}
