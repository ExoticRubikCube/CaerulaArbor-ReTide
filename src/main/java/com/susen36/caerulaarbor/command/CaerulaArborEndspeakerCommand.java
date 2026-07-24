
package com.susen36.caerulaarbor.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler;
import com.susen36.caerulaarbor.entity.EndspeakerEntity;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class CaerulaArborEndspeakerCommand {
	
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("caerula_arbor:endspeaker").requires(s -> s.hasPermission(2)).then(Commands.literal("inquiry").executes(arguments -> {
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

            if (entity != null) {
                String res_line_1;
                String res_line_2;
                String code;
                String res_line_3;
                code = "" + Math.round(MapVariables.get(world).endspeaker_abolities);
                res_line_1 = Component.translatable("command.endspeaker.inquiry.0").getString();
                res_line_1 = res_line_1.replace("{code}", code);
                res_line_2 = Component.translatable("command.endspeaker.inquiry.1").getString();
                for (int index0 = 0; index0 < 3; index0++) {
                    res_line_2 = res_line_2.replace("{a" + (index0 + 1) + "}", "" + EndspeakerEntity.hasAbility(world, index0));
                }
                res_line_3 = Component.translatable("command.endspeaker.inquiry.2").getString();
                for (int index1 = 0; index1 < 3; index1++) {
                    res_line_3 = res_line_3.replace("{a" + (index1 + 4) + "}", "" + EndspeakerEntity.hasAbility(world, index1 + 3));
                }
                if (entity instanceof Player player && !player.level().isClientSide())
                    player.displayClientMessage(Component.literal(res_line_1), false);
                if (entity instanceof Player player && !player.level().isClientSide())
                    player.displayClientMessage(Component.literal(res_line_2), false);
                if (entity instanceof Player player && !player.level().isClientSide())
                    player.displayClientMessage(Component.literal(res_line_3), false);
            }
            return 0;
		})).then(Commands.literal("bestow").then(Commands.literal("all").executes(arguments -> {
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

            double ind = 0;
            String info;
            for (int index0 = 0; index0 < 6; index0++) {
                MapVariablesHandler.bestowAbility(world, index0);
            }
            info = Component.translatable("command.endspeaker.bestow.all").getString();
            {
                final String success = info;
                final boolean informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
            }
            return 0;
		})).then(Commands.argument("index", DoubleArgumentType.doubleArg(1, 6)).executes(arguments -> {
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

            String info;
            double ind;
            ind = Math.round(DoubleArgumentType.getDouble(arguments, "index"));
            MapVariablesHandler.bestowAbility(world, ind - 1);
            info = Component.translatable("command.endspeaker.bestow.one").getString();
            info = info.replace("{index}", "" + Math.round(ind));
            {
                final String success = info;
                final boolean informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
            }
            return 0;
		}))).then(Commands.literal("revoke").then(Commands.literal("all").executes(arguments -> {
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

            double ind = 0;
            String info;
            for (int index0 = 0; index0 < 6; index0++) {
                revokeAbility(world, index0);
            }
            info = Component.translatable("command.endspeaker.revoke.all").getString();
            {
                final String success = info;
                final boolean informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
            }
            return 0;
		})).then(Commands.argument("index", DoubleArgumentType.doubleArg(1, 6)).executes(arguments -> {
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

            String info;
            double ind;
            ind = Math.round(DoubleArgumentType.getDouble(arguments, "index"));
            revokeAbility(world, ind - 1);
            info = Component.translatable("command.endspeaker.revoke.one").getString();
            info = info.replace("{index}", "" + Math.round(ind));
            {
                final String success = info;
                final boolean informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
            }
            return 0;
		}))).then(Commands.literal("can_summon").then(Commands.argument("can", BoolArgumentType.bool()).executes(arguments -> {
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

            String info;
            boolean cancanneed;
            cancanneed = BoolArgumentType.getBool(arguments, "can");
            MapVariablesHandler.setEndspeakerSummon(world, cancanneed);
            if (cancanneed) {
                info = Component.translatable("command.endspeaker.summon.true").getString();
            } else {
                info = Component.translatable("command.endspeaker.summon.false").getString();
            }
            {
                final String success = info;
                final boolean informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(success), informAdmins);
            }
            return 0;
		}))));
	}

    private static void revokeAbility(Level world, double index) {
		if (EndspeakerEntity.hasAbility(world, index)) {
			MapVariablesHandler.revokeAbility(world, index);
		}
	}
}
