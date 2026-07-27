
package com.susen36.caerulaarbor.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class CaerulaSanityCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("caerula_arbor:sanity").requires(s -> s.hasPermission(2)).then(Commands.literal("check").then(Commands.argument("name", EntityArgument.entity()).executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel servLevel)
				entity = FakePlayerFactory.getMinecraft(servLevel);
			Direction direction = Direction.DOWN;
			if (entity != null)
                entity.getDirection();

            Entity ent;
            String info;
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
                    "" + Math.round(Math.pow(10, 1) * (ent instanceof LivingEntity livingEntity ? ModCapabilities.getSanityInjury(livingEntity).getValue() : 0)) / Math.pow(10, 1));
            {
                final String success = info;
                final boolean informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
            }
            return 0;
		}))).then(Commands.literal("set").then(Commands.argument("name", EntityArgument.entities()).then(Commands.argument("amount", DoubleArgumentType.doubleArg(-1, 1000)).executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel servLevel)
				entity = FakePlayerFactory.getMinecraft(servLevel);
			Direction direction = Direction.DOWN;
			if (entity != null)
                entity.getDirection();

            Entity ent;
            String info = "";
            double num = 0;
            try {
                for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                    ent = entityiterator;
                    if (ent instanceof LivingEntity) {
                        num = num + 1;
                        CompoundTag sanityData = ModCapabilities.getSanityInjury((LivingEntity) ent).serializeNBT();
                        sanityData.putDouble("SanityInjury", DoubleArgumentType.getDouble(arguments, "amount"));
                        sanityData.putBoolean("SanityRecovering", false);
                        sanityData.putBoolean("SanityLocked", false);
                        ModCapabilities.getSanityInjury((LivingEntity) ent).deserializeNBT(sanityData);
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
                final String success = info;
                final boolean informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
            }
            return 0;
		})))).then(Commands.literal("hurt").then(Commands.argument("name", EntityArgument.entities()).then(Commands.argument("amount", DoubleArgumentType.doubleArg(0, 2147483647)).executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel servLevel)
				entity = FakePlayerFactory.getMinecraft(servLevel);
			Direction direction = Direction.DOWN;
			if (entity != null)
                entity.getDirection();

            Entity ent;
            String info = "";
            double num = 0;
            try {
                for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                    ent = entityiterator;
                    if (!(ent == null) && ent instanceof LivingEntity) {
                        num = num + 1;
                        ModCapabilities.getSanityInjury((LivingEntity) ent).hurt(DoubleArgumentType.getDouble(arguments, "amount"));
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
                final String success = info;
                final boolean informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
            }
            return 0;
		})))).then(Commands.literal("heal").then(Commands.argument("name", EntityArgument.entities()).then(Commands.argument("amount", DoubleArgumentType.doubleArg(0, 2147483647)).executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null && world instanceof ServerLevel servLevel)
				entity = FakePlayerFactory.getMinecraft(servLevel);
			Direction direction = Direction.DOWN;
			if (entity != null)
                entity.getDirection();

            Entity ent;
            String info = "";
            double num = 0;
            try {
                for (Entity entityiterator : EntityArgument.getEntities(arguments, "name")) {
                    ent = entityiterator;
                    if (!(ent == null) && ent instanceof LivingEntity) {
                        num = num + 1;
                        ModCapabilities.getSanityInjury((LivingEntity) ent).heal(DoubleArgumentType.getDouble(arguments, "amount"));
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
                final String success = info;
                final boolean informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
            }
            return 0;
		})))));
	}
}