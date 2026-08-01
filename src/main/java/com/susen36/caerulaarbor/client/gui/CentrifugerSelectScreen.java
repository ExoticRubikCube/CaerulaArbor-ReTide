package com.susen36.caerulaarbor.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.GladiiaEntity;
import com.susen36.caerulaarbor.entity.SkadiEntity;
import com.susen36.caerulaarbor.entity.SpecterEntity;
import com.susen36.caerulaarbor.entity.UlpiansEntity;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.menu.CentrifugerSelectMenu;
import com.susen36.caerulaarbor.network.send.CentrifugerSelectButtonMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;

// TODO: 新增了 textures/gui/sprites/overlay/atlas/ 目录下的 sprite 文件。原来的 textures/overlay/atlas/ 目录中的纹理文件可以保留（其他地方可能还在使用），也可以后续清理
public class CentrifugerSelectScreen extends AbstractContainerScreen<CentrifugerSelectMenu> {
	private final static HashMap<String, Object> guistate = CentrifugerSelectMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	ImageButton imagebutton_cent_button_skadi_0;
	ImageButton imagebutton_cent_button_ulpians_0;
	ImageButton imagebutton_cent_button_gladiia_0;
	ImageButton imagebutton_cent_button_done_0;
	ImageButton imagebutton_cen_button_specter_0;

	public CentrifugerSelectScreen(CentrifugerSelectMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 194;
		this.imageHeight = 130;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
        Entity result;
        if (entity == null) {
            result = null;
        } else {
            String selection;
            selection = entity.getPersistentData().getString("centrifugerSelection");
            if ((selection).equals("skadi")) {
                result = (LevelAccessor) world instanceof Level level ? new SkadiEntity(CAEntities.SKADI.get(), level) : null;
            } else if ((selection).equals("ulpians")) {
                result = (LevelAccessor) world instanceof Level level ? new UlpiansEntity(CAEntities.ULPIANS.get(), level) : null;
            } else if ((selection).equals("gladiia")) {
                result = (LevelAccessor) world instanceof Level level ? new GladiiaEntity(CAEntities.GLADIIA.get(), level) : null;
            } else if ((selection).equals("specter")) {
                result = (LevelAccessor) world instanceof Level level ? new SpecterEntity(CAEntities.SPECTER.get(), level) : null;
            } else {
                result = entity;
            }
        }
        if (result instanceof LivingEntity livingEntity) {
			InventoryScreen.renderEntityInInventoryFollowsAngle(guiGraphics, this.leftPos + 99, this.topPos + 64, this.leftPos + 159, this.topPos + 124, 30, 0f, 0f + (float) Math.atan((this.leftPos + 129 - mouseX) / 40.0), (float) Math.atan((this.topPos + 45 - mouseY) / 40.0), livingEntity);
		}
		this.renderTooltip(guiGraphics, mouseX, mouseY);
		if (mouseX > leftPos + 117 && mouseX < leftPos + 141 && mouseY > topPos + 105 && mouseY < topPos + 121)
			guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.centrifuger_select.tooltip_clicktocentrifuge"), mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/centrifuger_select_bg.png"), this.leftPos, this.topPos, 0, 0, 194, 130, 194, 130);

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
		guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.centrifuger_select.label_centrifugerselection"), 2, 3, -13816531, false);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_cent_button_skadi_0 = new ImageButton(this.leftPos + 36, this.topPos + 40, 26, 18, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "overlay/atlas/imagebutton_cent_button_skadi_0"), ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "overlay/atlas/imagebutton_cent_button_skadi_0_highlighted")), e -> {
			if (true) {
				PacketDistributor.sendToServer(new CentrifugerSelectButtonMessage(0, x, y, z));
				CentrifugerSelectButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		});
		guistate.put("button:imagebutton_cent_button_skadi_0", imagebutton_cent_button_skadi_0);
		this.addRenderableWidget(imagebutton_cent_button_skadi_0);
		imagebutton_cent_button_ulpians_0 = new ImageButton(this.leftPos + 68, this.topPos + 40, 26, 18, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "overlay/atlas/imagebutton_cent_button_ulpians_0"), ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "overlay/atlas/imagebutton_cent_button_ulpians_0_highlighted")), e -> {
			if (true) {
				PacketDistributor.sendToServer(new CentrifugerSelectButtonMessage(1, x, y, z));
				CentrifugerSelectButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		});
		guistate.put("button:imagebutton_cent_button_ulpians_0", imagebutton_cent_button_ulpians_0);
		this.addRenderableWidget(imagebutton_cent_button_ulpians_0);
		imagebutton_cent_button_gladiia_0 = new ImageButton(this.leftPos + 36, this.topPos + 72, 26, 18, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "overlay/atlas/imagebutton_cent_button_gladiia_0"), ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "overlay/atlas/imagebutton_cent_button_gladiia_0_highlighted")), e -> {
			if (true) {
				PacketDistributor.sendToServer(new CentrifugerSelectButtonMessage(2, x, y, z));
				CentrifugerSelectButtonMessage.handleButtonAction(entity, 2, x, y, z);
			}
		});
		guistate.put("button:imagebutton_cent_button_gladiia_0", imagebutton_cent_button_gladiia_0);
		this.addRenderableWidget(imagebutton_cent_button_gladiia_0);
		imagebutton_cent_button_done_0 = new ImageButton(this.leftPos + 116, this.topPos + 104, 26, 18, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "overlay/atlas/imagebutton_cent_button_done_0"), ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "overlay/atlas/imagebutton_cent_button_done_0_highlighted")), e -> {
			if (true) {
				PacketDistributor.sendToServer(new CentrifugerSelectButtonMessage(3, x, y, z));
				CentrifugerSelectButtonMessage.handleButtonAction(entity, 3, x, y, z);
			}
		});
		guistate.put("button:imagebutton_cent_button_done_0", imagebutton_cent_button_done_0);
		this.addRenderableWidget(imagebutton_cent_button_done_0);
		imagebutton_cen_button_specter_0 = new ImageButton(this.leftPos + 68, this.topPos + 72, 26, 18, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "overlay/atlas/imagebutton_cen_button_specter_0"), ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "overlay/atlas/imagebutton_cen_button_specter_0_highlighted")), e -> {
			if (true) {
				PacketDistributor.sendToServer(new CentrifugerSelectButtonMessage(4, x, y, z));
				CentrifugerSelectButtonMessage.handleButtonAction(entity, 4, x, y, z);
			}
		});
		guistate.put("button:imagebutton_cen_button_specter_0", imagebutton_cen_button_specter_0);
		this.addRenderableWidget(imagebutton_cen_button_specter_0);
	}
}