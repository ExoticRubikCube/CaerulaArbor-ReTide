package com.apocalypse.caerulaarbor.events;

import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DisconcentrationFuncProcedure {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return;
        if (!player.isAlive()) return;
        if(player.tickCount%20!=0) return;
		if (player.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null)
				.orElse(new CaerulaArborModVariables.PlayerVariables()).disoclusion == 1) handleRejection(player);
    }

    private static void handleRejection(LocalPlayer player) {
        if (player.level().random.nextDouble() <= 0.1) {
            if (player.level().random.nextDouble() > 0.5) {
                KeyMapping.click(Minecraft.getInstance().options.keyUse.getKey());
            } else {
                KeyMapping.click(Minecraft.getInstance().options.keyAttack.getKey());
            }
        }
    }
}
