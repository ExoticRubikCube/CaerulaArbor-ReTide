package com.apocalypse.caerulaarbor.command;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.map.MapVariablesHandler;
import com.apocalypse.caerulaarbor.capability.map.MapVariablesHandler.StrategyType;
import com.apocalypse.caerulaarbor.manager.BreedUpgradeManager;
import com.apocalypse.caerulaarbor.manager.GrowUpgradeManager;
import com.apocalypse.caerulaarbor.manager.MigrationUpgradeManager;
import com.apocalypse.caerulaarbor.manager.SubsistingUpgradeManager;
import com.apocalypse.caerulaarbor.util.StrategyUtils;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber
public class EvolutionCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("caerula_arbor:evolution").requires(s -> s.hasPermission(2)).then(Commands.literal("grow").then(Commands.literal("update").executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel _servLevel)
				entity = FakePlayerFactory.getMinecraft(_servLevel);
			Direction direction = Direction.DOWN;
			if (entity != null)
                entity.getDirection();

			GrowUpgradeManager.execute(world);
			return 0;
		})).then(Commands.argument("lvl", DoubleArgumentType.doubleArg(0, 4)).executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel _servLevel)
				entity = FakePlayerFactory.getMinecraft(_servLevel);
			Direction direction = Direction.DOWN;
			if (entity != null)
                entity.getDirection();

            String info;
            MapVariablesHandler.setStrategyLevel(world, StrategyType.GROW, Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
            if (DoubleArgumentType.getDouble(arguments, "lvl") >= 3) {
                if ((LevelAccessor) world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "grow2")), SoundSource.NEUTRAL, 4, 1);
                }
            } else if (DoubleArgumentType.getDouble(arguments, "lvl") > 0) {
                if ((LevelAccessor) world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "grow1")), SoundSource.NEUTRAL, 4, 1);
                }
            }
            if (DoubleArgumentType.getDouble(arguments, "lvl") < 4) {
                MapVariablesHandler.setStrategyLevel(world, StrategyType.SILENCE, 0);
            }
            info = Component.translatable("command.evolution.grow").getString();
            info = info.replace("<num>", "" + Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
            {
                final String _success = info;
                final boolean _informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
            }
            return 0;
		}))).then(Commands.literal("breed").then(Commands.literal("update").executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel _servLevel)
				entity = FakePlayerFactory.getMinecraft(_servLevel);
			Direction direction = Direction.DOWN;
			if (entity != null)
                entity.getDirection();

			BreedUpgradeManager.execute(world);
			return 0;
		})).then(Commands.argument("lvl", DoubleArgumentType.doubleArg(0, 4)).executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel _servLevel)
				entity = FakePlayerFactory.getMinecraft(_servLevel);
			Direction direction = Direction.DOWN;
			if (entity != null)
                entity.getDirection();

            String info;
            MapVariablesHandler.setStrategyLevel(world, StrategyType.BREED, Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
            if (DoubleArgumentType.getDouble(arguments, "lvl") >= 3) {
                if ((LevelAccessor) world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "breed2")), SoundSource.NEUTRAL, 4, 1);
                }
            } else if (DoubleArgumentType.getDouble(arguments, "lvl") > 0) {
                if ((LevelAccessor) world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "breed1")), SoundSource.NEUTRAL, 4, 1);
                }
            }
            if (DoubleArgumentType.getDouble(arguments, "lvl") < 4) {
                MapVariablesHandler.setStrategyLevel(world, StrategyType.SILENCE, 0);
            }
            info = Component.translatable("command.evolution.breed").getString();
            info = info.replace("<num>", "" + Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
            {
                final String _success = info;
                final boolean _informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
            }
            return 0;
		}))).then(Commands.literal("migration").then(Commands.literal("update").executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel _servLevel)
				entity = FakePlayerFactory.getMinecraft(_servLevel);
			Direction direction = Direction.DOWN;
			if (entity != null)
                entity.getDirection();

			MigrationUpgradeManager.execute(world);
			return 0;
		})).then(Commands.argument("lvl", DoubleArgumentType.doubleArg(0, 4)).executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel _servLevel)
				entity = FakePlayerFactory.getMinecraft(_servLevel);
			Direction direction = Direction.DOWN;
			if (entity != null)
                entity.getDirection();

            String info;
            MapVariablesHandler.setStrategyLevel(world, StrategyType.MIGRATION, Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
            if (DoubleArgumentType.getDouble(arguments, "lvl") >= 3) {
                if ((LevelAccessor) world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "migration2")), SoundSource.NEUTRAL, 4, 1);
                }
            } else if (DoubleArgumentType.getDouble(arguments, "lvl") > 0) {
                if ((LevelAccessor) world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "migration1")), SoundSource.NEUTRAL, 4, 1);
                }
            }
            if (DoubleArgumentType.getDouble(arguments, "lvl") < 4) {
                MapVariablesHandler.setStrategyLevel(world, StrategyType.SILENCE, 0);
            }
            info = Component.translatable("command.evolution.migration").getString();
            info = info.replace("<num>", "" + Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
            {
                final String _success = info;
                final boolean _informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
            }
            return 0;
		}))).then(Commands.literal("subsisting").then(Commands.literal("update").executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel _servLevel)
				entity = FakePlayerFactory.getMinecraft(_servLevel);
			Direction direction = Direction.DOWN;
			if (entity != null)
                entity.getDirection();

			SubsistingUpgradeManager.execute(world);
			return 0;
		})).then(Commands.argument("lvl", DoubleArgumentType.doubleArg(0, 4)).executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel _servLevel)
				entity = FakePlayerFactory.getMinecraft(_servLevel);
			Direction direction = Direction.DOWN;
			if (entity != null)
                entity.getDirection();

            String info;
            MapVariablesHandler.setStrategyLevel(world, StrategyType.SUBSISTING, Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
            if (DoubleArgumentType.getDouble(arguments, "lvl") >= 3) {
                if ((LevelAccessor) world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "subsisting2")), SoundSource.NEUTRAL, 4, 1);
                }
            } else if (DoubleArgumentType.getDouble(arguments, "lvl") > 0) {
                if ((LevelAccessor) world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "subsisting1")), SoundSource.NEUTRAL, 4, 1);
                }
            }
            if (DoubleArgumentType.getDouble(arguments, "lvl") < 4) {
                MapVariablesHandler.setStrategyLevel(world, StrategyType.SILENCE, 0);
            }
            info = Component.translatable("command.evolution.subsisting").getString();
            info = info.replace("<num>", "" + Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
            {
                final String _success = info;
                final boolean _informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
            }
            return 0;
		}))).then(Commands.literal("silence").then(Commands.argument("lvl", DoubleArgumentType.doubleArg(0, 4)).executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel _servLevel)
				entity = FakePlayerFactory.getMinecraft(_servLevel);
			Direction direction = Direction.DOWN;
			if (entity != null)
                entity.getDirection();

            if (entity != null) {
                String info;
                if (StrategyUtils.canEnableSilence(world)) {
                    MapVariablesHandler.setStrategyLevel(world, StrategyType.SILENCE, Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
                    if (DoubleArgumentType.getDouble(arguments, "lvl") == 1) {
                        if ((LevelAccessor) world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "silence1")), SoundSource.NEUTRAL, 6, 1);
                        }
                        if (entity instanceof Player _player && !_player.level().isClientSide())
                            _player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_6").getString())), true);
                    } else if (DoubleArgumentType.getDouble(arguments, "lvl") == 2) {
                        if ((LevelAccessor) world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "silence2")), SoundSource.NEUTRAL, 6, 1);
                        }
                        if (entity instanceof Player _player && !_player.level().isClientSide())
                            _player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_7").getString())), true);
                    } else if (DoubleArgumentType.getDouble(arguments, "lvl") == 3) {
                        if ((LevelAccessor) world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "silence3")), SoundSource.NEUTRAL, 6, 1);
                        }
                        if (entity instanceof Player _player && !_player.level().isClientSide())
                            _player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_8").getString())), true);
                    } else if (DoubleArgumentType.getDouble(arguments, "lvl") == 4) {
                        if ((LevelAccessor) world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "silence4")), SoundSource.NEUTRAL, 6, 1);
                        }
                        if (entity instanceof Player _player && !_player.level().isClientSide())
                            _player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_9").getString())), true);
                    }
                } else {
                    if (entity instanceof Player _player && !_player.level().isClientSide())
                        _player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_5").getString())), true);
                }
                info = Component.translatable("command.evolution.silence").getString();
                info = info.replace("<num>", "" + Math.round(DoubleArgumentType.getDouble(arguments, "lvl")));
                {
                    final String _success = info;
                    final boolean _informAdmins = true;
                    arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
                }
            }
            return 0;
		}))));
	}
}
