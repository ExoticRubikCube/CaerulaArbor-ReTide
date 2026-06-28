package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.configuration.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

public class UpgradeGrowProcedure {
	public static void execute(LevelAccessor world) {
		double stra = 0;
		String num = "";
		String prefix = "";
		stra = CaerulaArborModVariables.MapVariables.get(world).strategy_grow;
		if (stra < 4) {
			if (CaerulaArborModVariables.MapVariables.get(world).evo_point_grow >= Math.pow(stra + 1, 3) * (double) CaerulaConfigsConfiguration.COEFFICIENT.get()) {
				for (Entity entityiterator : new ArrayList<>(world.players())) {
					if (entityiterator instanceof ServerPlayer _player) {
						Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "to_experience_evolution"));
						AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
						if (!_ap.isDone()) {
							for (String criteria : _ap.getRemainingCriteria())
								_player.getAdvancements().award(_adv, criteria);
						}
					}
				}
				CaerulaArborModVariables.MapVariables.get(world).strategy_grow = stra + 1;
				CaerulaArborModVariables.MapVariables.get(world).syncData(world);
				stra = CaerulaArborModVariables.MapVariables.get(world).strategy_grow;
				CaerulaArborModVariables.MapVariables.get(world).evo_point_grow = 0;
				CaerulaArborModVariables.MapVariables.get(world).syncData(world);
				if (stra == 1) {
					num = "I";
					prefix = "\u00A7p";
				} else if (stra == 2) {
					num = "II";
					prefix = "\u00A7b";
				} else if (stra == 3) {
					num = "III";
					prefix = "\u00A79";
				} else if (stra == 4) {
					num = "IV";
					prefix = "\u00A71";
				}
				if (CaerulaConfigsConfiguration.EVOSOUND.get()) {
					for (Entity entityiterator : new ArrayList<>(world.players())) {
						if (stra >= 3) {
							if (world instanceof Level _level) {
									_level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "grow2")),
											SoundSource.NEUTRAL, 4, 1);
							}
						} else if (stra > 0) {
							if (world instanceof Level _level) {
									_level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "grow1")),
											SoundSource.NEUTRAL, 4, 1);
							}
						}
					}
				}
				if (!world.isClientSide() && world.getServer() != null)
					world.getServer().getPlayerList().broadcastSystemMessage(Component.literal((prefix + "" + Component.translatable("item.caerula_arbor.sample_grow.description_5").getString() + num)), false);
			}
		} else {
			for (Entity entityiterator : new ArrayList<>(world.players())) {
				if (entityiterator instanceof ServerPlayer _player) {
					Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "to_terminate_evolution"));
					AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
					if (!_ap.isDone()) {
						for (String criteria : _ap.getRemainingCriteria())
							_player.getAdvancements().award(_adv, criteria);
					}
				}
			}
			CaerulaArborModVariables.MapVariables.get(world).evo_point_grow = 1;
			CaerulaArborModVariables.MapVariables.get(world).syncData(world);
		}
	}
}

// TODO: 调用次数 = 6，但 procedure 非常长（98行），副作用密集（修改全局变量、同步数据、播放声音、给予玩家成就、显示消息），保持原样不重构
