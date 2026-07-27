package com.susen36.caerulaarbor.event;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.susen36.caerulaarbor.init.CAGameRules;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class CommandEventHandler {
	@SubscribeEvent
	public static void onCommand(CommandEvent event) {
		CommandContext<CommandSourceStack> commandContext = event.getParseResults().getContext().build(event.getParseResults().getReader().getString());
		Entity commandExecutor = commandContext.getSource().getEntity();
		if (!(commandExecutor instanceof Player player)) {
			return;
		}

		if (player.level().isClientSide() || commandContext.getSource().getServer().getDefaultGameType() != GameType.SURVIVAL) {
			return;
		}

		boolean hasGameRuleNode = false;
		boolean hasSurgingWavesNode = false;
		boolean hasValueNode = false;
		for (ParsedCommandNode<CommandSourceStack> parsedNode : commandContext.getNodes()) {
			String nodeName = parsedNode.getNode().getName();
			if ("gamerule".equals(nodeName)) {
				hasGameRuleNode = true;
			} else if (CAGameRules.SURGING_WAVES.getId().equals(nodeName)) {
				hasSurgingWavesNode = true;
			} else if ("value".equals(nodeName)) {
				hasValueNode = true;
			}
		}

		if (!hasGameRuleNode || !hasSurgingWavesNode || !hasValueNode) {
			return;
		}

		int targetSurgingWavesLevel = IntegerArgumentType.getInteger(commandContext, "value");
		int currentSurgingWavesLevel = player.level().getLevelData().getGameRules().getInt(CAGameRules.SURGING_WAVES);
		if (targetSurgingWavesLevel <= currentSurgingWavesLevel) {
			return;
		}

		Component warningMessage = null;
		if (currentSurgingWavesLevel < 12 && targetSurgingWavesLevel >= 12) {
			warningMessage = Component.translatable("gameplay.caerula_arbor.n_warn_12");
		} else if (currentSurgingWavesLevel < 6 && targetSurgingWavesLevel >= 6) {
			warningMessage = Component.translatable("gameplay.caerula_arbor.n_warn_6");
		}

		if (warningMessage != null) {
			player.displayClientMessage(warningMessage, false);
		}
	}
}