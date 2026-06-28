
package com.apocalypse.caerulaarbor.command;

import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import com.apocalypse.caerulaarbor.utils.WorldUtils;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
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
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CaerulaArborEndspeakerCommand {
	
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("caerula_arbor:endspeaker").requires(s -> s.hasPermission(2)).then(Commands.literal("inquiry").executes(arguments -> {
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
                String res_line_1 = "";
                String res_line_2 = "";
                String code = "";
                String res_line_3 = "";
                code = "" + Math.round(CaerulaArborModVariables.MapVariables.get(world).endspeaker_abolities);
                res_line_1 = Component.translatable("command.endspeaker.inquiry.0").getString();
                res_line_1 = res_line_1.replace("{code}", code);
                res_line_2 = Component.translatable("command.endspeaker.inquiry.1").getString();
                for (int index0 = 0; index0 < 3; index0++) {
                    res_line_2 = res_line_2.replace("{a" + (index0 + 1) + "}", "" + EntityUtils.inquirybility(world, index0));
                }
                res_line_3 = Component.translatable("command.endspeaker.inquiry.2").getString();
                for (int index1 = 0; index1 < 3; index1++) {
                    res_line_3 = res_line_3.replace("{a" + (index1 + 4) + "}", "" + EntityUtils.inquirybility(world, index1 + 3));
                }
                if (entity instanceof Player _player && !_player.level().isClientSide())
                    _player.displayClientMessage(Component.literal(res_line_1), false);
                if (entity instanceof Player _player && !_player.level().isClientSide())
                    _player.displayClientMessage(Component.literal(res_line_2), false);
                if (entity instanceof Player _player && !_player.level().isClientSide())
                    _player.displayClientMessage(Component.literal(res_line_3), false);
            }
            return 0;
		})).then(Commands.literal("bestow").then(Commands.literal("all").executes(arguments -> {
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

            double ind = 0;
            String info = "";
            for (int index0 = 0; index0 < 6; index0++) {
                WorldUtils.bestowAbility(world, index0);
            }
            info = Component.translatable("command.endspeaker.bestow.all").getString();
            {
                final String _success = info;
                final boolean _informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
            }
            return 0;
		})).then(Commands.argument("index", DoubleArgumentType.doubleArg(1, 6)).executes(arguments -> {
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
            double ind = 0;
            ind = Math.round(DoubleArgumentType.getDouble(arguments, "index"));
            WorldUtils.bestowAbility(world, ind - 1);
            info = Component.translatable("command.endspeaker.bestow.one").getString();
            info = info.replace("{index}", "" + Math.round(ind));
            {
                final String _success = info;
                final boolean _informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
            }
            return 0;
		}))).then(Commands.literal("revoke").then(Commands.literal("all").executes(arguments -> {
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

            double ind = 0;
            String info = "";
            for (int index0 = 0; index0 < 6; index0++) {
                revokeAbility(world, index0);
            }
            info = Component.translatable("command.endspeaker.revoke.all").getString();
            {
                final String _success = info;
                final boolean _informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
            }
            return 0;
		})).then(Commands.argument("index", DoubleArgumentType.doubleArg(1, 6)).executes(arguments -> {
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
            double ind = 0;
            ind = Math.round(DoubleArgumentType.getDouble(arguments, "index"));
            revokeAbility(world, ind - 1);
            info = Component.translatable("command.endspeaker.revoke.one").getString();
            info = info.replace("{index}", "" + Math.round(ind));
            {
                final String _success = info;
                final boolean _informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
            }
            return 0;
		}))).then(Commands.literal("can_summon").then(Commands.argument("can", BoolArgumentType.bool()).executes(arguments -> {
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
            boolean cancanneed = false;
            cancanneed = BoolArgumentType.getBool(arguments, "can");
            CaerulaArborModVariables.MapVariables.get(world).endspeakerSummon = cancanneed;
            CaerulaArborModVariables.MapVariables.get(world).syncData(world);
            if (cancanneed) {
                info = Component.translatable("command.endspeaker.summon.true").getString();
            } else {
                info = Component.translatable("command.endspeaker.summon.false").getString();
            }
            {
                final String _success = info;
                final boolean _informAdmins = true;
                arguments.getSource().sendSuccess(() -> Component.literal(_success), _informAdmins);
            }
            return 0;
		}))));
	}

    private static void revokeAbility(Level world, double index) {
		if (EntityUtils.inquirybility(world, index)) {
			CaerulaArborModVariables.MapVariables.get(world).endspeaker_abolities = (int) CaerulaArborModVariables.MapVariables.get(world).endspeaker_abolities - (int) Math.pow(2, index);
			CaerulaArborModVariables.MapVariables.get(world).syncData(world);
		}
	}
}
