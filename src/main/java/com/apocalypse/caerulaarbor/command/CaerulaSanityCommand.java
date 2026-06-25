
package com.apocalypse.caerulaarbor.command;

import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.common.util.FakePlayerFactory;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.Commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;

@Mod.EventBusSubscriber
public class CaerulaSanityCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("caerula_arbor:sanity").requires(s -> s.hasPermission(2)).then(Commands.literal("check").then(Commands.argument("name", EntityArgument.entity()).executes(arguments -> {
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

            Entity ent = null;
            String info = "";
            ent = new Object() {
                public Entity getEntity() {
                    try {
                        return EntityArgument.getEntity(arguments, "name");
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }.getEntity();
            if (ent == null) {
                arguments.getSource().sendFailure(Component.literal((Component.translatable("command.sanity.check.fail").getString())));
            }
            info = Component.translatable("command.sanity.check.success").getString();
            info = info.replace("{name}", ent.getDisplayName().getString());
            info = info.replace("{num}",
                    "" + Math.round(Math.pow(10, 1)
                            * (ent instanceof LivingEntity _livingEntity6 && _livingEntity6.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get()) ? _livingEntity6.getAttribute(CaerulaArborModAttributes.SANITY.get()).getBaseValue() : 0))
                            / Math.pow(10, 1));
            {
                final String _success = info;
                final boolean _informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
            }
            return 0;
		}))).then(Commands.literal("set").then(Commands.argument("name", EntityArgument.entities()).then(Commands.argument("amount", DoubleArgumentType.doubleArg(-1, 1000)).executes(arguments -> {
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

            Entity ent = null;
            String info = "";
            double num = 0;
            try {
                for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                    ent = entityiterator;
                    if (!(ent == null) && ent instanceof LivingEntity) {
                        num = num + 1;
                        if (ent instanceof LivingEntity _livingEntity3 && _livingEntity3.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get()))
                            _livingEntity3.getAttribute(CaerulaArborModAttributes.SANITY.get()).setBaseValue((DoubleArgumentType.getDouble(arguments, "amount")));
                        if (num == 1) {
                            info = Component.translatable("command.sanity.set.single").getString();
                            info = info.replace("{name}", ent.getDisplayName().getString());
                        } else {
                            info = Component.translatable("command.sanity.set.mult").getString();
                        }
                    }
                }
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
            info = info.replace("{num}", "" + Math.round(num));
            info = info.replace("{amount}", "" + Math.round(Math.pow(10, 1) * (DoubleArgumentType.getDouble(arguments, "amount"))) / Math.pow(10, 1));
            {
                final String _success = info;
                final boolean _informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
            }
            return 0;
		})))).then(Commands.literal("hurt").then(Commands.argument("name", EntityArgument.entities()).then(Commands.argument("amount", DoubleArgumentType.doubleArg(0, 2147483647)).executes(arguments -> {
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

            Entity ent = null;
            String info = "";
            double num = 0;
            try {
                for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                    ent = entityiterator;
                    if (!(ent == null) && ent instanceof LivingEntity) {
                        num = num + 1;
                        EntityUtils.deductSanity(ent, DoubleArgumentType.getDouble(arguments, "amount"));
                        if (num == 1) {
                            info = Component.translatable("command.sanity.hurt.single").getString();
                            info = info.replace("{name}", ent.getDisplayName().getString());
                        } else {
                            info = Component.translatable("command.sanity.hurt.mult").getString();
                        }
                    }
                }
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
            info = info.replace("{num}", "" + Math.round(num));
            info = info.replace("{amount}", "" + Math.round(Math.pow(10, 1) * (DoubleArgumentType.getDouble(arguments, "amount"))) / Math.pow(10, 1));
            {
                final String _success = info;
                final boolean _informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
            }
            return 0;
		})))).then(Commands.literal("heal").then(Commands.argument("name", EntityArgument.entities()).then(Commands.argument("amount", DoubleArgumentType.doubleArg(0, 2147483647)).executes(arguments -> {
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

            Entity ent = null;
            String info = "";
            double num = 0;
            try {
                for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                    ent = entityiterator;
                    if (!(ent == null) && ent instanceof LivingEntity) {
                        num = num + 1;
                        EntityUtils.restoreSanity(ent, DoubleArgumentType.getDouble(arguments, "amount"));
                        if (num == 1) {
                            info = Component.translatable("command.sanity.heal.single").getString();
                            info = info.replace("{name}", ent.getDisplayName().getString());
                        } else {
                            info = Component.translatable("command.sanity.heal.mult").getString();
                        }
                    }
                }
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
            info = info.replace("{num}", "" + Math.round(num));
            info = info.replace("{amount}", "" + Math.round(Math.pow(10, 1) * (DoubleArgumentType.getDouble(arguments, "amount"))) / Math.pow(10, 1));
            {
                final String _success = info;
                final boolean _informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
            }
            return 0;
		})))));
	}
}
