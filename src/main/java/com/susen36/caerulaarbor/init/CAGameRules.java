package com.susen36.caerulaarbor.init;

import net.minecraft.world.level.GameRules;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class CAGameRules {
    public static final GameRules.Key<GameRules.BooleanValue> TARGET_LIFE_FUNCTION = GameRules.register("targetLifeFunction", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.BooleanValue> NATURAL_EVOLUTION = GameRules.register("naturalEvolution", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.IntegerValue> CLONE_NUMBER_LIMIT = GameRules.register("cloneNumberLimit", GameRules.Category.MOBS, GameRules.IntegerValue.create(24));
    public static final GameRules.Key<GameRules.IntegerValue> SPREAD_RATE = GameRules.register("spreadRate", GameRules.Category.PLAYER, GameRules.IntegerValue.create(100));
    public static final GameRules.Key<GameRules.BooleanValue> AGGRESIVE_MODE = GameRules.register("aggresiveMode", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
    public static final GameRules.Key<GameRules.BooleanValue> OCEANIZATION_MODE = GameRules.register("oceanizationMode", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.IntegerValue> SURGING_WAVES = GameRules.register("surgingWaves", GameRules.Category.MISC, GameRules.IntegerValue.create(0));
    public static final GameRules.Key<GameRules.IntegerValue> SEABORN_SPAWN_RATE = GameRules.register("seabornSpawnRate", GameRules.Category.PLAYER, GameRules.IntegerValue.create(45));
    public static final GameRules.Key<GameRules.BooleanValue> DEFENSIVE_MODE = GameRules.register("defensiveMode", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
}
