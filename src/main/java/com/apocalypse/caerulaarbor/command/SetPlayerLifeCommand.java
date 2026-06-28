
package com.apocalypse.caerulaarbor.command;

import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SetPlayerLifeCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("caerula_arbor:player_data").requires(s -> s.hasPermission(2))
				.then(Commands.argument("name", EntityArgument.players()).then(Commands.literal("life_point").then(Commands.argument("life", DoubleArgumentType.doubleArg(0, 255)).executes(arguments -> {
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
                    double lfs = 0;
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            lfs = Math.min(DoubleArgumentType.getDouble(arguments, "life"), (entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_maxlive);
                            {
                                double _setval = lfs;
                                entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.player_lives = _setval;
                                    capability.syncPlayerVariables(entityiterator);
                                });
                            }
                            info = Component.translatable("command.set_life").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            info = info.replace("<num>", "" + Math.round(lfs));
                            {
                                final String _success = info;
                                final boolean _informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				}))).then(Commands.literal("max_life_point").then(Commands.argument("life", DoubleArgumentType.doubleArg(1, 255)).executes(arguments -> {
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
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double _setval = DoubleArgumentType.getDouble(arguments, "life");
                                entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.player_maxlive = _setval;
                                    capability.syncPlayerVariables(entityiterator);
                                });
                            }
                            info = Component.translatable("command.set_maxlife").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            info = info.replace("<num>", "" + Math.round(DoubleArgumentType.getDouble(arguments, "life")));
                            {
                                final String _success = info;
                                final boolean _informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				}))).then(Commands.literal("shield").then(Commands.argument("shield", DoubleArgumentType.doubleArg(0, 999)).executes(arguments -> {
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
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double _setval = DoubleArgumentType.getDouble(arguments, "shield");
                                entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.player_shield = _setval;
                                    capability.syncPlayerVariables(entityiterator);
                                });
                            }
                            info = Component.translatable("command.set_shield").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            info = info.replace("<num>", "" + Math.round(DoubleArgumentType.getDouble(arguments, "shield")));
                            {
                                final String _success = info;
                                final boolean _informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				}))).then(Commands.literal("lights").then(Commands.literal("ablaze").executes(arguments -> {
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
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double _setval = 100;
                                entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.player_light = _setval;
                                    capability.syncPlayerVariables(entityiterator);
                                });
                            }
                            info = Component.translatable("command.set_light").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            info = info.replace("<num>", "100");
                            {
                                final String _success = info;
                                final boolean _informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})).then(Commands.literal("flicker").executes(arguments -> {
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
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double _setval = 80;
                                entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.player_light = _setval;
                                    capability.syncPlayerVariables(entityiterator);
                                });
                            }
                            info = Component.translatable("command.set_light").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            info = info.replace("<num>", "80");
                            {
                                final String _success = info;
                                final boolean _informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})).then(Commands.literal("dim").executes(arguments -> {
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
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double _setval = 40;
                                entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.player_light = _setval;
                                    capability.syncPlayerVariables(entityiterator);
                                });
                            }
                            info = Component.translatable("command.set_light").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            info = info.replace("<num>", "40");
                            {
                                final String _success = info;
                                final boolean _informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})).then(Commands.literal("tranquil").executes(arguments -> {
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
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double _setval = 0;
                                entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.player_light = _setval;
                                    capability.syncPlayerVariables(entityiterator);
                                });
                            }
                            info = Component.translatable("command.set_light").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            info = info.replace("<num>", "0");
                            {
                                final String _success = info;
                                final boolean _informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})).then(Commands.argument("light", DoubleArgumentType.doubleArg(0, 100)).executes(arguments -> {
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
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double _setval = DoubleArgumentType.getDouble(arguments, "light");
                                entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.player_light = _setval;
                                    capability.syncPlayerVariables(entityiterator);
                                });
                            }
                            info = Component.translatable("command.set_light").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            info = info.replace("<num>", "" + Math.round(DoubleArgumentType.getDouble(arguments, "light")));
                            {
                                final String _success = info;
                                final boolean _informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				}))).then(Commands.literal("oceanization").then(Commands.argument("state", DoubleArgumentType.doubleArg(0, 3)).executes(arguments -> {
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
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double _setval = DoubleArgumentType.getDouble(arguments, "state");
                                entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.player_oceanization = _setval;
                                    capability.syncPlayerVariables(entityiterator);
                                });
                            }
                            info = Component.translatable("command.oceanize").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            info = info.replace("<num>", "" + Math.round(DoubleArgumentType.getDouble(arguments, "state")));
                            {
                                final String _success = info;
                                final boolean _informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				}))).then(Commands.literal("rejection").then(Commands.literal("clear").executes(arguments -> {
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
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double _setval = 0;
                                entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.disoclusion = _setval;
                                    capability.syncPlayerVariables(entityiterator);
                                });
                            }
                            info = Component.translatable("command.rejection.clear").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            {
                                final String _success = info;
                                final boolean _informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})).then(Commands.literal("disconcentration").executes(arguments -> {
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
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double _setval = 1;
                                entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.disoclusion = _setval;
                                    capability.syncPlayerVariables(entityiterator);
                                });
                            }
                            info = Component.translatable("command.rejection.set_disconcentration").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            {
                                final String _success = info;
                                final boolean _informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})).then(Commands.literal("haemophilia").executes(arguments -> {
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
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double _setval = 2;
                                entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.disoclusion = _setval;
                                    capability.syncPlayerVariables(entityiterator);
                                });
                            }
                            info = Component.translatable("command.rejection.set_haemophilia").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            {
                                final String _success = info;
                                final boolean _informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})).then(Commands.literal("neurodegression").executes(arguments -> {
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
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double _setval = 3;
                                entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.disoclusion = _setval;
                                    capability.syncPlayerVariables(entityiterator);
                                });
                            }
                            info = Component.translatable("command.rejection.set_neurodegression").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            {
                                final String _success = info;
                                final boolean _informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})).then(Commands.literal("deformity").executes(arguments -> {
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
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double _setval = 1;
                                entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.disoclusion = _setval;
                                    capability.syncPlayerVariables(entityiterator);
                                });
                            }
                            info = Component.translatable("command.rejection.set_deformity").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            {
                                final String _success = info;
                                final boolean _informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})))));
	}
}
