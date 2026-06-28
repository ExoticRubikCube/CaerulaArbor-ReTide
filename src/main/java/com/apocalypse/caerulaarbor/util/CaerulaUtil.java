package com.apocalypse.caerulaarbor.util;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class CaerulaUtil {
	// Shared utility methods migrated from procedures.
	// Life points
	public static int getLifePoint(Player player){
		return (int) player.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null)
		.orElse(new CaerulaArborModVariables.PlayerVariables()).player_lives;
	}

	public static void setLifePoint(Player player, int value){
		if(value < 1) return;
		int maxPoint = getMaxLifePoint(player);
		player.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null)
		.ifPresent(c -> {
			c.player_lives = Math.min(value,maxPoint);
			c.syncPlayerVariables(player);
		});
	}

	public static int getMaxLifePoint(Player player){
		return (int) player.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null)
		.orElse(new CaerulaArborModVariables.PlayerVariables()).player_maxlive;
	}

	public static void setMaxLifePoint(Player player, int value){
		if(value < 1) return;
		player.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null)
		.ifPresent(c -> {
			c.player_maxlive = value;
			c.syncPlayerVariables(player);
		});
		if (value < getLifePoint(player)) setLifePoint(player, value);
	}

	// Shield points
	public static int getShieldPoint(Player player){
		return (int) player.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null)
		.orElse(new CaerulaArborModVariables.PlayerVariables()).player_shield;
	}
	public static void setShieldPoint(Player player, int value){
		if(value < 0) return;
		player.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null)
		.ifPresent(c -> {
			c.player_shield = value;
			c.syncPlayerVariables(player);
		});
	}

	// Lights
	public static double getLights(Player player){
		return player.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null)
		.orElse(new CaerulaArborModVariables.PlayerVariables()).player_light;
	}
	public static void setLights(Player player, double value){
		player.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null)
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
		EntityUtils.deductSanity(living, amount);
	}
	public static void healSanityInjury(LivingEntity living, double amount){
		EntityUtils.restoreSanity(living, amount);
	}

	// Armor erosion
	public static void armorErrosion(Entity entity, int amount, int limit){
		for (int i=0;i<amount;i++){
			EntityUtils.giveLessArmor(entity, limit);
		}
	}

	public static class Tags{
		public static final TagKey<EntityType<?>> SEABORNS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring"));
	}
}
