package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LivingExpDropEventHandler {
	@SubscribeEvent
	public static void onLivingDropXp(LivingExperienceDropEvent event) {
		if (event == null || event.getEntity() == null) return;

		LevelAccessor world = event.getEntity().level();
		double x = event.getEntity().getX();
		double y = event.getEntity().getY();
		double z = event.getEntity().getZ();
		Entity sourceentity = event.getAttackingPlayer();
		double originalexperience = event.getOriginalExperience();
		if (sourceentity == null)
			return;
		double exp_left;
		if (sourceentity instanceof Player && (sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_EXTENSION) {
			if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives <= 1) {
				exp_left = originalexperience;
				while (exp_left >= 11) {
					if (world instanceof ServerLevel level)
						level.addFreshEntity(new ExperienceOrb(level, (x + Mth.nextDouble(RandomSource.create(), -1, 1)), (y + Mth.nextDouble(RandomSource.create(), 0.1, 0.85)), (z + Mth.nextDouble(RandomSource.create(), -1, 1)), 11));
					exp_left = exp_left - 11;
				}
				while (exp_left >= 5) {
					if (world instanceof ServerLevel level)
						level.addFreshEntity(new ExperienceOrb(level, (x + Mth.nextDouble(RandomSource.create(), -1, 1)), (y + Mth.nextDouble(RandomSource.create(), 0.1, 0.85)), (z + Mth.nextDouble(RandomSource.create(), -1, 1)), 7));
					exp_left = exp_left - 7;
				}
				//TODO 可疑，可能需要更新
				while (exp_left >= 5) {
					if (world instanceof ServerLevel _level)
						_level.addFreshEntity(new ExperienceOrb(_level, (x + Mth.nextDouble(RandomSource.create(), -1, 1)), (y + Mth.nextDouble(RandomSource.create(), 0.1, 0.85)), (z + Mth.nextDouble(RandomSource.create(), -1, 1)), 3));
					exp_left = exp_left - 3;
				}
				while (exp_left >= 1) {
					if (world instanceof ServerLevel level)
						level.addFreshEntity(new ExperienceOrb(level, (x + Mth.nextDouble(RandomSource.create(), -1, 1)), (y + Mth.nextDouble(RandomSource.create(), 0.1, 0.85)), (z + Mth.nextDouble(RandomSource.create(), -1, 1)), 1));
					exp_left = exp_left - 1;
				}
			}
		}
	}
}