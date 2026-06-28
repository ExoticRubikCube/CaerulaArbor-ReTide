package com.apocalypse.caerulaarbor.command;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.helper.UpgradeBreedProcedure;
import com.apocalypse.caerulaarbor.helper.UpgradeGrowProcedure;
import com.apocalypse.caerulaarbor.helper.UpgradeMigraProcedure;
import com.apocalypse.caerulaarbor.helper.UpgradeSubsisProcedure;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.utils.StrategyUtils;
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
				direction = entity.getDirection();

			UpgradeGrowProcedure.execute(world);
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
				direction = entity.getDirection();

            String info = "";
            CaerulaArborModVariables.MapVariables.get(world).strategy_grow = Math.round(DoubleArgumentType.getDouble(arguments, "lvl"));
            CaerulaArborModVariables.MapVariables.get(world).syncData(world);
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
                CaerulaArborModVariables.MapVariables.get(world).strategy_silence = 0;
                CaerulaArborModVariables.MapVariables.get(world).syncData(world);
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
				direction = entity.getDirection();

			UpgradeBreedProcedure.execute(world);
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
				direction = entity.getDirection();

            String info = "";
            CaerulaArborModVariables.MapVariables.get(world).strategy_breed = Math.round(DoubleArgumentType.getDouble(arguments, "lvl"));
            CaerulaArborModVariables.MapVariables.get(world).syncData(world);
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
                CaerulaArborModVariables.MapVariables.get(world).strategy_silence = 0;
                CaerulaArborModVariables.MapVariables.get(world).syncData(world);
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
				direction = entity.getDirection();

			UpgradeMigraProcedure.execute(world);
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
				direction = entity.getDirection();

            String info = "";
            CaerulaArborModVariables.MapVariables.get(world).strategy_migration = Math.round(DoubleArgumentType.getDouble(arguments, "lvl"));
            CaerulaArborModVariables.MapVariables.get(world).syncData(world);
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
                CaerulaArborModVariables.MapVariables.get(world).strategy_silence = 0;
                CaerulaArborModVariables.MapVariables.get(world).syncData(world);
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
				direction = entity.getDirection();

			UpgradeSubsisProcedure.execute(world);
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
				direction = entity.getDirection();

            String info = "";
            CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting = Math.round(DoubleArgumentType.getDouble(arguments, "lvl"));
            CaerulaArborModVariables.MapVariables.get(world).syncData(world);
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
                CaerulaArborModVariables.MapVariables.get(world).strategy_silence = 0;
                CaerulaArborModVariables.MapVariables.get(world).syncData(world);
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
				direction = entity.getDirection();

            if (entity != null) {
                String info = "";
                if (StrategyUtils.canEnableSilence(world)) {
                    CaerulaArborModVariables.MapVariables.get(world).strategy_silence = Math.round(DoubleArgumentType.getDouble(arguments, "lvl"));
                    CaerulaArborModVariables.MapVariables.get(world).syncData(world);
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
