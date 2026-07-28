package com.susen36.caerulaarbor.client.overlay;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber({Dist.CLIENT})
public class RelicFuncShowOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		int h = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		Level world = null;
		double x = 0;
		double y = 0;
		double z = 0;
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
            entity.level();
        }
        boolean result2 = false;
        if (entity != null) {
            result2 = !(ModCapabilities.getPlayerVariables(entity)).kingShowPtc;
        }
        if (result2) {

            double result = 0;
            if (entity != null) {
                result = (ModCapabilities.getPlayerVariables(entity)).player_king_suit;
            }
            event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/icon_king.png"), 6, 8, Mth.clamp((int) result * 16, 0, 32), 0, 16, 16, 48, 16);

            double result3 = 0;
            if (entity != null) {
                result3 = (ModCapabilities.getPlayerVariables(entity)).player_demon_suit;
            }
            event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/icon_artifi.png"), 22, 8, Mth.clamp((int) result3 * 16, 0, 32), 0, 16, 16, 48, 16);

            boolean result1 = false;
            if (entity != null) {
                if ((Entity) entity instanceof LivingEntity livEnt0 && livEnt0.hasEffect(CAMobEffects.TIDE_OF_CHITIN)) {
                    result1 = true;
                }
            }
            if (result1) {
				event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/icon_chitin.png"), 38, 8, 0, 0, 16, 16, 16, 16);
			}
		}
	}
}