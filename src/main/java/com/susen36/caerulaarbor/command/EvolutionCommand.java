package com.susen36.caerulaarbor.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler.StrategyType;
import com.susen36.caerulaarbor.init.CASounds;
import com.susen36.caerulaarbor.manager.BreedUpgradeManager;
import com.susen36.caerulaarbor.manager.GrowUpgradeManager;
import com.susen36.caerulaarbor.manager.MigrationUpgradeManager;
import com.susen36.caerulaarbor.manager.SubsistingUpgradeManager;
import com.susen36.caerulaarbor.util.StrategyUtils;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class EvolutionCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("caerula_arbor:evolution").requires(s -> s.hasPermission(2)).then(Commands.literal("grow").then(Commands.literal("update").executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
            Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel servLevel)
				entity = FakePlayerFactory.getMinecraft(servLevel);
            if (entity != null)
                entity.getDirection();

			GrowUpgradeManager.applyGrowthUpgrade(world);
			return 0;
		})).then(Commands.argument("lvl", DoubleArgumentType.doubleArg(0, 4)).executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel servLevel)
				entity = FakePlayerFactory.getMinecraft(servLevel);
            if (entity != null)
                entity.getDirection();

            String info;
            MapVariablesHandler.setStrategyLevel(world, StrategyType.GROW, Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
            if (DoubleArgumentType.getDouble(arguments, "lvl") >= 3) {
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.GROW2.get(), SoundSource.NEUTRAL, 4, 1);
                }
            } else if (DoubleArgumentType.getDouble(arguments, "lvl") > 0) {
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.GROW1.get(), SoundSource.NEUTRAL, 4, 1);
                }
            }
            if (DoubleArgumentType.getDouble(arguments, "lvl") < 4) {
                MapVariablesHandler.setStrategyLevel(world, StrategyType.SILENCE, 0);
            }
            info = Component.translatable("command.evolution.grow").getString();
            info = info.replace("<num>", "" + Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
            {
                final String success = info;
                final boolean informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
            }
            return 0;
		}))).then(Commands.literal("breed").then(Commands.literal("update").executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
            Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel servLevel)
				entity = FakePlayerFactory.getMinecraft(servLevel);
            if (entity != null)
                entity.getDirection();

			BreedUpgradeManager.applyBreedUpgrade(world);
			return 0;
		})).then(Commands.argument("lvl", DoubleArgumentType.doubleArg(0, 4)).executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel servLevel)
				entity = FakePlayerFactory.getMinecraft(servLevel);
            if (entity != null)
                entity.getDirection();

            String info;
            MapVariablesHandler.setStrategyLevel(world, StrategyType.BREED, Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
            if (DoubleArgumentType.getDouble(arguments, "lvl") >= 3) {
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.BREED2.get(), SoundSource.NEUTRAL, 4, 1);
                }
            } else if (DoubleArgumentType.getDouble(arguments, "lvl") > 0) {
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.BREED1.get(), SoundSource.NEUTRAL, 4, 1);
                }
            }
            if (DoubleArgumentType.getDouble(arguments, "lvl") < 4) {
                MapVariablesHandler.setStrategyLevel(world, StrategyType.SILENCE, 0);
            }
            info = Component.translatable("command.evolution.breed").getString();
            info = info.replace("<num>", "" + Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
            {
                final String success = info;
                final boolean informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
            }
            return 0;
		}))).then(Commands.literal("migration").then(Commands.literal("update").executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
            Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel servLevel)
				entity = FakePlayerFactory.getMinecraft(servLevel);
            if (entity != null)
                entity.getDirection();

			MigrationUpgradeManager.applyMigrationUpgrade(world);
			return 0;
		})).then(Commands.argument("lvl", DoubleArgumentType.doubleArg(0, 4)).executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel servLevel)
				entity = FakePlayerFactory.getMinecraft(servLevel);
            if (entity != null)
                entity.getDirection();

            String info;
            MapVariablesHandler.setStrategyLevel(world, StrategyType.MIGRATION, Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
            if (DoubleArgumentType.getDouble(arguments, "lvl") >= 3) {
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.MIGRATION2.get(), SoundSource.NEUTRAL, 4, 1);
                }
            } else if (DoubleArgumentType.getDouble(arguments, "lvl") > 0) {
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.MIGRATION1.get(), SoundSource.NEUTRAL, 4, 1);
                }
            }
            if (DoubleArgumentType.getDouble(arguments, "lvl") < 4) {
                MapVariablesHandler.setStrategyLevel(world, StrategyType.SILENCE, 0);
            }
            info = Component.translatable("command.evolution.migration").getString();
            info = info.replace("<num>", "" + Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
            {
                final String success = info;
                final boolean informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
            }
            return 0;
		}))).then(Commands.literal("subsisting").then(Commands.literal("update").executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
            Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel servLevel)
				entity = FakePlayerFactory.getMinecraft(servLevel);
            if (entity != null)
                entity.getDirection();

			SubsistingUpgradeManager.applySubsistingUpgrade(world);
			return 0;
		})).then(Commands.argument("lvl", DoubleArgumentType.doubleArg(0, 4)).executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel servLevel)
				entity = FakePlayerFactory.getMinecraft(servLevel);
            if (entity != null)
                entity.getDirection();

            String info;
            MapVariablesHandler.setStrategyLevel(world, StrategyType.SUBSISTING, Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
            if (DoubleArgumentType.getDouble(arguments, "lvl") >= 3) {
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.SUBSISTING2.get(), SoundSource.NEUTRAL, 4, 1);
                }
            } else if (DoubleArgumentType.getDouble(arguments, "lvl") > 0) {
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.SUBSISTING1.get(), SoundSource.NEUTRAL, 4, 1);
                }
            }
            if (DoubleArgumentType.getDouble(arguments, "lvl") < 4) {
                MapVariablesHandler.setStrategyLevel(world, StrategyType.SILENCE, 0);
            }
            info = Component.translatable("command.evolution.subsisting").getString();
            info = info.replace("<num>", "" + Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
            {
                final String success = info;
                final boolean informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
            }
            return 0;
		}))).then(Commands.literal("silence").then(Commands.argument("lvl", DoubleArgumentType.doubleArg(0, 4)).executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel servLevel)
				entity = FakePlayerFactory.getMinecraft(servLevel);
            if (entity != null)
                entity.getDirection();

            if (entity != null) {
                String info;
                if (StrategyUtils.canEnableSilence(world)) {
                    MapVariablesHandler.setStrategyLevel(world, StrategyType.SILENCE, Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
                    if (DoubleArgumentType.getDouble(arguments, "lvl") == 1) {
                        if ((LevelAccessor) world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), CASounds.SILENCE1.get(), SoundSource.NEUTRAL, 6, 1);
                        }
                        if (entity instanceof Player player && !player.level().isClientSide())
                            player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_6").getString())), true);
                    } else if (DoubleArgumentType.getDouble(arguments, "lvl") == 2) {
                        if ((LevelAccessor) world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), CASounds.SILENCE2.get(), SoundSource.NEUTRAL, 6, 1);
                        }
                        if (entity instanceof Player player && !player.level().isClientSide())
                            player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_7").getString())), true);
                    } else if (DoubleArgumentType.getDouble(arguments, "lvl") == 3) {
                        if ((LevelAccessor) world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), CASounds.SILENCE3.get(), SoundSource.NEUTRAL, 6, 1);
                        }
                        if (entity instanceof Player player && !player.level().isClientSide())
                            player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_8").getString())), true);
                    } else if (DoubleArgumentType.getDouble(arguments, "lvl") == 4) {
                        if ((LevelAccessor) world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), CASounds.SILENCE4.get(), SoundSource.NEUTRAL, 6, 1);
                        }
                        if (entity instanceof Player player && !player.level().isClientSide())
                            player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_9").getString())), true);
                    }
                } else {
                    if (entity instanceof Player player && !player.level().isClientSide())
                        player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_5").getString())), true);
                }
                info = Component.translatable("command.evolution.silence").getString();
                info = info.replace("<num>", "" + Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
                {
                    final String success = info;
                    final boolean informAdmins = true;
                    arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
                }
            }
            return 0;
		}))).then(Commands.literal("sublimation")
				.then(Commands.literal("rise").executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel servLevel)
				entity = FakePlayerFactory.getMinecraft(servLevel);
            if (entity != null)
                entity.getDirection();

            if (MapVariables.get(world).if_sublimation) {
                double doneLvl = Math.min(MapVariables.get(world).strategy_sublimation + 1.0, 4.0);
                MapVariablesHandler.setStrategyLevel(world, StrategyType.SUBLIMATION, doneLvl);
                if (doneLvl >= 3) {
                    if ((LevelAccessor) world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), CASounds.SUBLIMATION_2.get(), SoundSource.NEUTRAL, 4, 1);
                    }
                } else if (doneLvl > 0) {
                    if ((LevelAccessor) world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), CASounds.SUBLIMATION_1.get(), SoundSource.NEUTRAL, 4, 1);
                    }
                }
                String info = Component.translatable("command.evolution.sublimation").getString();
                info = info.replace("<num>", "" + Math.round(doneLvl));
                {
                    final String success = info;
                    final boolean informAdmins = true;
                    arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
                }
            } else {
                String info = Component.translatable("command.evolution.sublimation.fail").getString();
                {
                    final String success = info;
                    final boolean informAdmins = true;
                    arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
                }
            }
            return 0;
		})).then(Commands.literal("disable").executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel servLevel)
				entity = FakePlayerFactory.getMinecraft(servLevel);
            if (entity != null)
                entity.getDirection();

            MapVariables.get(world).if_sublimation = false;
            MapVariablesHandler.setStrategyLevel(world, StrategyType.SUBLIMATION, 0);
            String info = Component.translatable("command.evolution.sublimation.ban").getString();
            {
                final String success = info;
                final boolean informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
            }
            return 0;
		})).then(Commands.argument("lvl", DoubleArgumentType.doubleArg(0, 4)).executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel servLevel)
				entity = FakePlayerFactory.getMinecraft(servLevel);
            if (entity != null)
                entity.getDirection();

            if (MapVariables.get(world).if_sublimation) {
                MapVariablesHandler.setStrategyLevel(world, StrategyType.SUBLIMATION, Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
                if (DoubleArgumentType.getDouble(arguments, "lvl") >= 3) {
                    if ((LevelAccessor) world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), CASounds.SUBLIMATION_2.get(), SoundSource.NEUTRAL, 4, 1);
                    }
                } else if (DoubleArgumentType.getDouble(arguments, "lvl") > 0) {
                    if ((LevelAccessor) world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), CASounds.SUBLIMATION_1.get(), SoundSource.NEUTRAL, 4, 1);
                    }
                }
                String info = Component.translatable("command.evolution.sublimation").getString();
                info = info.replace("<num>", "" + Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
                {
                    final String success = info;
                    final boolean informAdmins = true;
                    arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
                }
            } else {
                String info = Component.translatable("command.evolution.sublimation.fail").getString();
                {
                    final String success = info;
                    final boolean informAdmins = true;
                    arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
                }
            }
            return 0;
		}))));
	}
}