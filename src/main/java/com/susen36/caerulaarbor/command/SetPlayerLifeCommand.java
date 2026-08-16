package com.susen36.caerulaarbor.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class SetPlayerLifeCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("caerula_arbor:player_data").requires(s -> s.hasPermission(2))
				.then(Commands.argument("name", EntityArgument.players()).then(Commands.literal("lights").then(Commands.literal("ablaze").executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
                    Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel servLevel)
						entity = FakePlayerFactory.getMinecraft(servLevel);
                    if (entity != null)
                        entity.getDirection();

                    String info;
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double setval = 100;
                                PlayerVariable capability = ModCapabilities.getPlayerVariables(entityiterator);
                                capability.player_light = setval;
                                capability.syncPlayerVariables(entityiterator);
                            }
                            info = Component.translatable("command.set_light").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            info = info.replace("<num>", "100");
                            {
                                final String success = info;
                                final boolean informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})).then(Commands.literal("flicker").executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
					Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel servLevel)
						entity = FakePlayerFactory.getMinecraft(servLevel);
                    if (entity != null)
                        entity.getDirection();

                    String info;
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double setval = 80;
                                PlayerVariable capability = ModCapabilities.getPlayerVariables(entityiterator);
                                capability.player_light = setval;
                                capability.syncPlayerVariables(entityiterator);
                            }
                            info = Component.translatable("command.set_light").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            info = info.replace("<num>", "80");
                            {
                                final String success = info;
                                final boolean informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})).then(Commands.literal("dim").executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
					Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel servLevel)
						entity = FakePlayerFactory.getMinecraft(servLevel);
					if (entity != null)
                        entity.getDirection();

                    String info;
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double setval = 40;
                                PlayerVariable capability = ModCapabilities.getPlayerVariables(entityiterator);
                                capability.player_light = setval;
                                capability.syncPlayerVariables(entityiterator);
                            }
                            info = Component.translatable("command.set_light").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            info = info.replace("<num>", "40");
                            {
                                final String success = info;
                                final boolean informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})).then(Commands.literal("tranquil").executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
                    Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel servLevel)
						entity = FakePlayerFactory.getMinecraft(servLevel);
                    if (entity != null)
                        entity.getDirection();

                    String info;
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double setval = 0;
                                PlayerVariable capability = ModCapabilities.getPlayerVariables(entityiterator);
                                capability.player_light = setval;
                                capability.syncPlayerVariables(entityiterator);
                            }
                            info = Component.translatable("command.set_light").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            info = info.replace("<num>", "0");
                            {
                                final String success = info;
                                final boolean informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})).then(Commands.argument("light", DoubleArgumentType.doubleArg(0, 100)).executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
                    Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel servLevel)
						entity = FakePlayerFactory.getMinecraft(servLevel);
                    if (entity != null)
                        entity.getDirection();

                    String info;
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double setval = DoubleArgumentType.getDouble(arguments, "light");
                                PlayerVariable capability = ModCapabilities.getPlayerVariables(entityiterator);
                                capability.player_light = setval;
                                capability.syncPlayerVariables(entityiterator);
                            }
                            info = Component.translatable("command.set_light").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            info = info.replace("<num>", "" + Math.round(DoubleArgumentType.getDouble(arguments, "light")));
                            {
                                final String success = info;
                                final boolean informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				}))).then(Commands.literal("oceanization").then(Commands.argument("state", DoubleArgumentType.doubleArg(0, 3)).executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
                    Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel servLevel)
						entity = FakePlayerFactory.getMinecraft(servLevel);
                    if (entity != null)
                        entity.getDirection();

                    String info;
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double setval = DoubleArgumentType.getDouble(arguments, "state");
                                PlayerVariable capability = ModCapabilities.getPlayerVariables(entityiterator);
                                capability.player_oceanization = setval;
                                capability.syncPlayerVariables(entityiterator);
                            }
                            info = Component.translatable("command.oceanize").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            info = info.replace("<num>", "" + Math.round(DoubleArgumentType.getDouble(arguments, "state")));
                            {
                                final String success = info;
                                final boolean informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				}))).then(Commands.literal("rejection").then(Commands.literal("clear").executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
                    Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel servLevel)
						entity = FakePlayerFactory.getMinecraft(servLevel);
                    if (entity != null)
                        entity.getDirection();

                    String info;
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double setval = 0;
                                PlayerVariable capability = ModCapabilities.getPlayerVariables(entityiterator);
                                capability.disoclusion = setval;
                                capability.syncPlayerVariables(entityiterator);
                            }
                            info = Component.translatable("command.rejection.clear").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            {
                                final String success = info;
                                final boolean informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})).then(Commands.literal("disconcentration").executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
                    Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel servLevel)
						entity = FakePlayerFactory.getMinecraft(servLevel);
                    if (entity != null)
                        entity.getDirection();

                    String info;
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double setval = 1;
                                PlayerVariable capability = ModCapabilities.getPlayerVariables(entityiterator);
                                capability.disoclusion = setval;
                                capability.syncPlayerVariables(entityiterator);
                            }
                            info = Component.translatable("command.rejection.set_disconcentration").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            {
                                final String success = info;
                                final boolean informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})).then(Commands.literal("haemophilia").executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
                    Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel servLevel)
						entity = FakePlayerFactory.getMinecraft(servLevel);
                    if (entity != null)
                        entity.getDirection();

                    String info;
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double setval = 2;
                                PlayerVariable capability = ModCapabilities.getPlayerVariables(entityiterator);
                                capability.disoclusion = setval;
                                capability.syncPlayerVariables(entityiterator);
                            }
                            info = Component.translatable("command.rejection.set_haemophilia").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            {
                                final String success = info;
                                final boolean informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})).then(Commands.literal("neurodegression").executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
                    Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel servLevel)
						entity = FakePlayerFactory.getMinecraft(servLevel);
                    if (entity != null)
                        entity.getDirection();

                    String info;
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double setval = 3;
                                PlayerVariable capability = ModCapabilities.getPlayerVariables(entityiterator);
                                capability.disoclusion = setval;
                                capability.syncPlayerVariables(entityiterator);
                            }
                            info = Component.translatable("command.rejection.set_neurodegression").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            {
                                final String success = info;
                                final boolean informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})).then(Commands.literal("deformity").executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
                    Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel servLevel)
						entity = FakePlayerFactory.getMinecraft(servLevel);
                    if (entity != null)
                        entity.getDirection();

                    String info;
                    try {
                        for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                            {
                                double setval = 1;
                                PlayerVariable capability = ModCapabilities.getPlayerVariables(entityiterator);
                                capability.disoclusion = setval;
                                capability.syncPlayerVariables(entityiterator);
                            }
                            info = Component.translatable("command.rejection.set_deformity").getString();
                            info = info.replace("<player>", entityiterator.getDisplayName().getString());
                            {
                                final String success = info;
                                final boolean informAdmins = true;
                                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
                            }
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return 0;
				})))));
	}
}