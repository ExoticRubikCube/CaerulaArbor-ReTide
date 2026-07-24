package com.susen36.caerulaarbor.client.gui;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.entity.GuideAbyssalEntity;
import com.susen36.caerulaarbor.init.CAConfigs;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CANetwork;
import com.susen36.caerulaarbor.menu.InfoStrategySubsisMenu;
import com.susen36.caerulaarbor.network.send.InfoStrategyReturnButtonMessage;
import com.susen36.caerulaarbor.util.StrategyUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.HashMap;

public class InfoStrategySubsisScreen extends AbstractContainerScreen<InfoStrategySubsisMenu> {
	private final static HashMap<String, Object> guistate = InfoStrategySubsisMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_return;

	public InfoStrategySubsisScreen(InfoStrategySubsisMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 256;
		this.imageHeight = 168;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
        if (((Entity) ((LevelAccessor) world instanceof Level level ? new GuideAbyssalEntity(CAEntities.GUIDE_ABYSSAL.get(), level) : null)) instanceof LivingEntity livingEntity) {
			InventoryScreen.renderEntityInInventoryFollowsAngle(guiGraphics, this.leftPos + 29, this.topPos + 96, 20, 0f + (float) Math.atan((this.leftPos + 29 - mouseX) / 40.0), (float) Math.atan((this.topPos + 47 - mouseY) / 40.0), livingEntity);
		}
		this.renderTooltip(guiGraphics, mouseX, mouseY);
		if (mouseX > leftPos + 244 && mouseX < leftPos + 253 && mouseY > topPos + 20 && mouseY < topPos + 92) {
            String result = "\u00A7bFinished";
            double rate = 0;
            if (!(MapVariables.get(world).strategy_subsisting >= 4)) {
                result = Math.round(MapVariables.get(world).evo_point_subsisting) + "\u00A7b/"
                        + Math.round(Math.pow(MapVariables.get(world).strategy_subsisting + 1, 3) * CAConfigs.COEFFICIENT.get());
            }
            guiGraphics.renderTooltip(font, Component.literal(result), mouseX, mouseY);
        }
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/sidebar.png"), this.leftPos + -3, this.topPos + -3, 0, 0, 262, 174, 262, 174);

		guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/bg_subsis.png"), this.leftPos, this.topPos, Mth.clamp((int) StrategyUtils.getStraSubsis(world) * 256, 0, 1024), 0, 256, 168, 1280, 168);

        double result = 18;
        double rate;
        if (!(MapVariables.get(world).strategy_subsisting >= 4)) {
            rate = MapVariables.get(world).evo_point_subsisting / (Math.pow(MapVariables.get(world).strategy_subsisting + 1, 3) * CAConfigs.COEFFICIENT.get());
            if (rate > 1) {
                rate = 1;
            }
            result = Math.round(18 * rate);
        }
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/barevo.png"), this.leftPos + 244, this.topPos + 20, Mth.clamp((int) result * 8, 0, 144), 0, 8, 72, 152, 72);

		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.info_strategy_subsis.label_strategy_subsisting"), 1, -12, -16717080, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.info_strategy_subsis.label_to_subsist_is_fundamental"), 1, 4, -1, false);
		guiGraphics.drawString(this.font,

				StrategyUtils.getDescrSubsis(world), 1, 100, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.info_strategy_subsis.label_proceed"), 1, 172, -1, false);
		if (StrategyUtils.isSilence(world))
			guiGraphics.drawString(this.font,

					StrategyUtils.getSilenceSubsis(world), 1, 116, -3407872, false);
		if (MapVariables.get(world).if_sublimation)
			guiGraphics.drawString(this.font,

					StrategyUtils.getSublimationSubsis(world), 1, 132, -26113, false);
		if (MapVariables.get(world).if_sublimation)
			guiGraphics.drawString(this.font,

					StrategyUtils.getSublimationSubsis2(world), 1, 148, -26113, false);
	}

	@Override
	public void init() {
		super.init();
		button_return = new PlainTextButton(this.leftPos + 226, this.topPos + 156, 32, 20, Component.translatable("gui.caerula_arbor.info_strategy_subsis.button_return"), e -> {
			if (true) {
				CANetwork.PACKET_HANDLER.sendToServer(new InfoStrategyReturnButtonMessage(0, x, y, z));
				InfoStrategyReturnButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}, this.font);
		guistate.put("button:button_return", button_return);
		this.addRenderableWidget(button_return);
	}
}

