package com.apocalypse.caerulaarbor.capability.map;

import net.minecraft.world.level.LevelAccessor;

public class MapVariablesHandler {

    private MapVariablesHandler() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void addEvoPoint(LevelAccessor world, StrategyType strategy, double point) {
        MapVariables mapVariables = MapVariables.get(world);
        switch (strategy) {
            case GROW -> mapVariables.evo_point_grow += point;
            case SUBSISTING -> mapVariables.evo_point_subsisting += point;
            case BREED -> mapVariables.evo_point_breed += point;
            case MIGRATION -> mapVariables.evo_point_migration += point;
            case SILENCE -> mapVariables.evo_point_silence += point;
            case SUBLIMATION -> mapVariables.evo_point_sublimation += point;
        }
        mapVariables.syncData(world);
    }

    public static void setEvoPoint(LevelAccessor world, StrategyType strategy, double point) {
        MapVariables mapVariables = MapVariables.get(world);
        switch (strategy) {
            case GROW -> mapVariables.evo_point_grow = point;
            case SUBSISTING -> mapVariables.evo_point_subsisting = point;
            case BREED -> mapVariables.evo_point_breed = point;
            case MIGRATION -> mapVariables.evo_point_migration = point;
            case SILENCE -> mapVariables.evo_point_silence = point;
            case SUBLIMATION -> mapVariables.evo_point_sublimation = point;
        }
        mapVariables.syncData(world);
    }

    public static void setStrategyLevel(LevelAccessor world, StrategyType strategy, double level) {
        MapVariables mapVariables = MapVariables.get(world);
        switch (strategy) {
            case GROW -> mapVariables.strategy_grow = level;
            case SUBSISTING -> mapVariables.strategy_subsisting = level;
            case BREED -> mapVariables.strategy_breed = level;
            case MIGRATION -> mapVariables.strategy_migration = level;
            case SILENCE -> mapVariables.strategy_silence = level;
            case SUBLIMATION -> mapVariables.strategy_sublimation = level;
        }
        mapVariables.syncData(world);
    }

    public static void setSilenceEnabled(LevelAccessor world, boolean enabled) {
        MapVariables mapVariables = MapVariables.get(world);
        mapVariables.silence_enabled = enabled;
        mapVariables.syncData(world);
    }

    public static void setEndspeakerSummon(LevelAccessor world, boolean canSummon) {
        MapVariables mapVariables = MapVariables.get(world);
        mapVariables.endspeakerSummon = canSummon;
        mapVariables.syncData(world);
    }

    public static void setIncandescentUseTick(LevelAccessor world, double gameTick) {
        MapVariables mapVariables = MapVariables.get(world);
        mapVariables.incandescentAnimaUseTick = gameTick;
        mapVariables.syncData(world);
    }

    public static void bestowAbility(LevelAccessor world, double index) {
        MapVariables mapVariables = MapVariables.get(world);
        mapVariables.endspeaker_abolities = (int) mapVariables.endspeaker_abolities | (int) Math.pow(2, index);
        mapVariables.syncData(world);
    }

    public static void revokeAbility(LevelAccessor world, double index) {
        MapVariables mapVariables = MapVariables.get(world);
        mapVariables.endspeaker_abolities = (int) mapVariables.endspeaker_abolities - (int) Math.pow(2, index);
        mapVariables.syncData(world);
    }

    public static void resetAllEndspeakerAbilities(LevelAccessor world) {
        MapVariables mapVariables = MapVariables.get(world);
        mapVariables.endspeaker_abolities = 0;
        mapVariables.syncData(world);
    }

    public enum StrategyType {
        GROW,
        SUBSISTING,
        BREED,
        MIGRATION,
        SILENCE,
        SUBLIMATION
    }
}
