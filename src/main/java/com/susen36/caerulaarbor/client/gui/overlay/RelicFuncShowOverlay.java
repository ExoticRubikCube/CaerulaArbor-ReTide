package com.susen36.caerulaarbor.client.gui.overlay;

import com.susen36.babel.collectible.Collectibles;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CACollectible;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber({Dist.CLIENT})
public class RelicFuncShowOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
			double result = computeKingSuit(entity);
			event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/icon_king.png"), 6, 8, Mth.clamp((int) result * 16, 0, 32), 0, 16, 16, 48, 16);

			double result3 = computeDemonSuit(entity);
			event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/icon_artifi.png"), 22, 8, Mth.clamp((int) result3 * 16, 0, 32), 0, 16, 16, 48, 16);

			boolean result1 = entity.hasEffect(CAMobEffects.TIDE_OF_CHITIN);
			if (result1) {
				event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/icon_chitin.png"), 38, 8, 0, 0, 16, 16, 16, 16);
			}
		}
	}

	private static double computeKingSuit(Player entity) {
		Collectibles collectibles = entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get());
		if (!collectibles.isUsed(CACollectible.KING_CROWN.get())) {
			return 0;
		}
		int suitKing = 0;
		if (collectibles.isUsed(CACollectible.KING_SPEAR.get())) {
			suitKing++;
		}
		if (collectibles.isUsed(CACollectible.KING_ARMOR.get())) {
			suitKing++;
		}
		if (collectibles.isUsed(CACollectible.KING_EXTENSION.get())) {
			suitKing++;
		}
		if (collectibles.isUsed(CACollectible.KING_CROWN.get())) {
			suitKing++;
		}
		return suitKing < 3 ? 1 : 2;
	}

	private static double computeDemonSuit(Player entity) {
		Collectibles collectibles = entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get());
		if (!collectibles.isUsed(CACollectible.SARKAZ_KING_ARTIFACT.get())) {
			return 0;
		}
		int suitArchfi = 0;
		if (collectibles.isUsed(CACollectible.SARKAZ_KING_FLAG.get())) {
			suitArchfi++;
		}
		if (collectibles.isUsed(CACollectible.SARKAZ_KING_BED.get())) {
			suitArchfi++;
		}
		if (collectibles.isUsed(CACollectible.SARKAZ_KING_ARTIFACT.get())) {
			suitArchfi++;
		}
		return suitArchfi < 3 ? 1 : 2;
	}
}
