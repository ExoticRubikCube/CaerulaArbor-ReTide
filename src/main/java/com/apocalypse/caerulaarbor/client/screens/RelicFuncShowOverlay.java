package com.apocalypse.caerulaarbor.client.screens;

import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class RelicFuncShowOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		Level world = null;
		double x = 0;
		double y = 0;
		double z = 0;
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
			world = entity.level();
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
		}
        boolean result2 = false;
        if (entity != null) {
            result2 = !(((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).kingShowPtc;
        }
        if (result2) {

            double result = 0;
            if (entity != null) {
                result = (((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_king_suit;
            }
            event.getGuiGraphics().blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/icon_king.png"), 6, 8, Mth.clamp((int) result * 16, 0, 32), 0, 16, 16, 48, 16);

            double result3 = 0;
            if (entity != null) {
                result3 = (((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_demon_suit;
            }
            event.getGuiGraphics().blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/icon_artifi.png"), 22, 8, Mth.clamp((int) result3 * 16, 0, 32), 0, 16, 16, 48, 16);

            boolean result1 = false;
            if (entity != null) {
                if ((Entity) entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CaerulaArborModMobEffects.TIDE_OF_CHITIN.get())) {
                    result1 = true;
                }
            }
            if (result1) {
				event.getGuiGraphics().blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/icon_chitin.png"), 38, 8, 0, 0, 16, 16, 16, 16);
			}
		}
	}
}
