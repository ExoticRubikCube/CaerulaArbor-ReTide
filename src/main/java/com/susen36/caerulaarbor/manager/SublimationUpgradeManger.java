package com.susen36.caerulaarbor.manager;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler.StrategyType;
import com.susen36.caerulaarbor.init.CAConfigs;
import com.susen36.caerulaarbor.init.CASounds;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;

public class SublimationUpgradeManger {

    public static void applySublimationUpgrade(LevelAccessor world, double point) {
        AdvancementProgress _ap;
        AdvancementHolder _adv;
        ServerPlayer _player;
        double stra;
        String prefix = "";
        String num = "";
        stra = MapVariables.get(world).strategy_sublimation;

        if (stra > 0.0) {
            for (Entity entityiterator : new ArrayList<>(world.players())) {
                if (!(entityiterator instanceof ServerPlayer serverPlayer)) continue;
                _player = serverPlayer;
                _adv = _player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "fifth_touch"));
                if (_adv == null) {
                    CaerulaArborMod.LOGGER.error("Missing advancement: {}:fifth_touch", CaerulaArborMod.MODID);
                    continue;
                }
                _ap = _player.getAdvancements().getOrStartProgress(_adv);
                if (_ap.isDone()) continue;
                for (String criteria : _ap.getRemainingCriteria()) {
                    _player.getAdvancements().award(_adv, criteria);
                }
            }
        }

        if (MapVariables.get(world).if_sublimation) {
            MapVariablesHandler.addEvoPoint(world, StrategyType.SUBLIMATION, point);

            if (stra < 4.0) {
                if (MapVariables.get(world).evo_point_sublimation >= Math.pow(stra + 1.0, 3.0) * CAConfigs.SUBLIMATION_COEFFICIENT.get() * 12.0) {
                    MapVariablesHandler.setStrategyLevel(world, StrategyType.SUBLIMATION, stra + 1.0);
                    stra = MapVariables.get(world).strategy_sublimation;
                    MapVariablesHandler.setEvoPoint(world, StrategyType.SUBLIMATION, 0.0);

                    if (stra == 1.0) {
                        num = "I";
                        prefix = "§p";
                    } else if (stra == 2.0) {
                        num = "II";
                        prefix = "§p";
                    } else if (stra == 3.0) {
                        num = "III";
                        prefix = "§e";
                    } else if (stra == 4.0) {
                        num = "IV";
                        prefix = "§6";
                    }

                    if (CAConfigs.EVOSOUND.get()) {
                        for (Entity entityiterator : new ArrayList<>(world.players())) {
                            if (stra <= 2.0) {
                                if (world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.SUBLIMATION_1.get(), SoundSource.NEUTRAL, 5.0f, 1.0f);
                                }
                            } else {
                                if (world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.SUBLIMATION_2.get(), SoundSource.NEUTRAL, 5.0f, 1.0f);
                                }
                            }
                        }
                    }

                    if (!world.isClientSide() && world.getServer() != null) {
                        world.getServer().getPlayerList().broadcastSystemMessage(Component.literal(prefix + Component.translatable("evolution.caerula_arbor.sublimation").getString() + num), false);
                    }
                }
            } else {
                MapVariablesHandler.setEvoPoint(world, StrategyType.SUBLIMATION, 0.0);
                for (Entity entityiterator : new ArrayList<>(world.players())) {
                    if (!(entityiterator instanceof ServerPlayer)) continue;
                    _player = (ServerPlayer) entityiterator;
                    _adv = _player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "absurd_of_evolution"));
                    if (_adv == null) {
                        CaerulaArborMod.LOGGER.error("Missing advancement: {}:absurd_of_evolution", CaerulaArborMod.MODID);
                        continue;
                    }
                    _ap = _player.getAdvancements().getOrStartProgress(_adv);
                    if (_ap.isDone()) continue;
                    for (String criteria : _ap.getRemainingCriteria()) {
                        _player.getAdvancements().award(_adv, criteria);
                    }
                }
            }
        } else {
            MapVariablesHandler.setStrategyLevel(world, StrategyType.SUBLIMATION, 0.0);
        }
    }
}