package com.apocalypse.caerulaarbor.client.gui;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.menu.PlayerEvoMenu;
import com.apocalypse.caerulaarbor.network.CaerulaArborModNetwork;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.network.message.send.PlayerEvoButtonMessage;
import com.apocalypse.caerulaarbor.util.NodeUtils;
import com.apocalypse.caerulaarbor.util.PlayerStateUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public class PlayerEvoScreen extends AbstractContainerScreen<PlayerEvoMenu> {
	private final static HashMap<String, Object> guistate = PlayerEvoMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	ImageButton imagebutton_player_evo_nexus_no_rejection;
	ImageButton imagebutton_player_evo_checkbox;
	ImageButton imagebutton_player_evo_nexus_regnr_sanity;
	ImageButton imagebutton_player_evo_node_add_def;
	ImageButton imagebutton_player_evo_node_add_def1;
	ImageButton imagebutton_player_evo_node_add_def2;
	ImageButton imagebutton_player_evo_node_add_def3;
	ImageButton imagebutton_player_evo_node_add_resis;
	ImageButton imagebutton_player_evo_node_add_resis1;
	ImageButton imagebutton_player_evo_node_add_resis2;
	ImageButton imagebutton_player_evo_node_add_resis3;
	ImageButton imagebutton_player_evo_node_add_speed;
	ImageButton imagebutton_player_evo_node_add_speed1;
	ImageButton imagebutton_player_evo_node_add_speed2;
	ImageButton imagebutton_player_evo_node_add_speed3;
	ImageButton imagebutton_player_evo_node_add_nervous;
	ImageButton imagebutton_player_evo_node_add_nervous1;
	ImageButton imagebutton_player_evo_node_add_nervous2;
	ImageButton imagebutton_player_evo_node_add_nervous3;
	ImageButton imagebutton_player_evo_nexus_regnr_lights;
	ImageButton imagebutton_player_evo_node_add_damage;
	ImageButton imagebutton_player_evo_node_add_damage1;
	ImageButton imagebutton_player_evo_node_add_damage2;
	ImageButton imagebutton_player_evo_node_add_damage3;
	ImageButton imagebutton_player_evo_node_less_damage;
	ImageButton imagebutton_player_evo_node_less_damage1;
	ImageButton imagebutton_player_evo_node_less_damage2;
	ImageButton imagebutton_player_evo_node_less_damage3;
	ImageButton imagebutton_player_evo_node_living_barrier;
	ImageButton imagebutton_player_evo_node_living_barrier1;
	ImageButton imagebutton_player_evo_node_living_barrier2;
	ImageButton imagebutton_player_evo_node_living_barrier3;
	ImageButton imagebutton_player_evo_node_add_miss;
	ImageButton imagebutton_player_evo_node_add_miss1;
	ImageButton imagebutton_player_evo_node_add_miss2;
	ImageButton imagebutton_player_evo_node_add_miss3;
	ImageButton imagebutton_player_evo_nexus_perc_attack;
	ImageButton imagebutton_player_evo_node_real_damage;
	ImageButton imagebutton_player_evo_node_real_damage1;
	ImageButton imagebutton_player_evo_node_real_damage2;
	ImageButton imagebutton_player_evo_node_real_damage3;
	ImageButton imagebutton_player_evo_node_heal_damage;
	ImageButton imagebutton_player_evo_node_heal_damage1;
	ImageButton imagebutton_player_evo_node_heal_damage2;
	ImageButton imagebutton_player_evo_node_heal_damage3;
	ImageButton imagebutton_player_evo_node_worse_break;
	ImageButton imagebutton_player_evo_node_worse_break1;
	ImageButton imagebutton_player_evo_node_worse_break2;
	ImageButton imagebutton_player_evo_node_worse_break3;
	ImageButton imagebutton_player_evo_nexus_expo_shield;
	ImageButton imagebutton_player_evo_talent_eunectes;
	ImageButton imagebutton_player_evo_talent_eunectes1;
	ImageButton imagebutton_player_evo_talent_eunectes2;
	ImageButton imagebutton_player_evo_talent_eunectes3;
	ImageButton imagebutton_player_evo_node_reduce_armor;
	ImageButton imagebutton_player_evo_node_reduce_armor1;
	ImageButton imagebutton_player_evo_node_reduce_armor2;
	ImageButton imagebutton_player_evo_node_reduce_armor3;

	public PlayerEvoScreen(PlayerEvoMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 304;
		this.imageHeight = 216;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
        boolean result = false;
        if (entity != null) {
            String title1 = "";
            title1 = entity.getPersistentData().getString("showcasingEvoNode");
            result = !(title1).isEmpty();
        }
        if (result)
			if (mouseX > leftPos + 270 && mouseX < leftPos + 296 && mouseY > topPos + 177 && mouseY < topPos + 188)
				guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.player_evo.tooltip_click_to_learn_the_node"), mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_bg.png"), this.leftPos, this.topPos, 0, 0, 304, 216, 304, 216);

		if (PlayerStateUtils.isNexusNoRejectionSelected(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 26, this.topPos + 43, 0, 0, 6, 6, 6, 6);
		}
		if (PlayerStateUtils.isNexusNoRejectionSelected(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_short_bar.png"), this.leftPos + 30, this.topPos + 51, 0, 0, 4, 1, 4, 1);
		}
		if (PlayerStateUtils.isNexusRegSanitySelected(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_quart_bar.png"), this.leftPos + 58, this.topPos + 14, 0, 0, 9, 74, 9, 74);
		}
		if (PlayerStateUtils.isNexusRegSanitySelected(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 54, this.topPos + 43, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddDefAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evolution_ids.png"), this.leftPos + 83, this.topPos + 19, 0, 0, 72, 7, 72, 7);
		}
		if (NodeUtils.isNodeAddDefAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 83, this.topPos + 9, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddDefAtLeast(entity, 2)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 105, this.topPos + 9, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddDefAtLeast(entity, 3)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 127, this.topPos + 9, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddDefAtLeast(entity, 4)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 149, this.topPos + 9, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddDefAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evolution_ids.png"), this.leftPos + 83, this.topPos + 43, 0, 0, 72, 7, 72, 7);
		}
		if (NodeUtils.isNodeAddResisAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 83, this.topPos + 33, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddResisAtLeast(entity, 2)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 105, this.topPos + 33, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddResisAtLeast(entity, 3)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 127, this.topPos + 33, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddResisAtLeast(entity, 4)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 149, this.topPos + 33, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddSpeedAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evolution_ids.png"), this.leftPos + 83, this.topPos + 67, 0, 0, 72, 7, 72, 7);
		}
		if (NodeUtils.isNodeAddSpeedAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 83, this.topPos + 57, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddSpeedAtLeast(entity, 2)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 105, this.topPos + 57, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddSpeedAtLeast(entity, 3)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 127, this.topPos + 57, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddSpeedAtLeast(entity, 4)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 149, this.topPos + 57, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddSanityAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evolution_ids.png"), this.leftPos + 83, this.topPos + 91, 0, 0, 72, 7, 72, 7);
		}
		if (NodeUtils.isNodeAddSanityAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 83, this.topPos + 81, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddSanityAtLeast(entity, 2)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 105, this.topPos + 81, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddSanityAtLeast(entity, 3)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 127, this.topPos + 81, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddSanityAtLeast(entity, 4)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 149, this.topPos + 81, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeSet1Done(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_quart_bar_reverse.png"), this.leftPos + 153, this.topPos + 15, 0, 0, 9, 74, 9, 74);
		}
		if (PlayerStateUtils.isNexusRegLightsSelected(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_quart_bar.png"), this.leftPos + 186, this.topPos + 15, 0, 0, 9, 74, 9, 74);
		}
		if (PlayerStateUtils.isNexusRegLightsSelected(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 182, this.topPos + 44, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddDamageAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evolution_ids.png"), this.leftPos + 211, this.topPos + 19, 0, 0, 72, 7, 72, 7);
		}
		if (NodeUtils.isNodeAddDamageAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 211, this.topPos + 9, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddDamageAtLeast(entity, 2)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 233, this.topPos + 9, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddDamageAtLeast(entity, 3)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 255, this.topPos + 9, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddDamageAtLeast(entity, 4)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 277, this.topPos + 9, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeLessDamageAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evolution_ids.png"), this.leftPos + 211, this.topPos + 43, 0, 0, 72, 7, 72, 7);
		}
		if (NodeUtils.isNodeLessDamageAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 211, this.topPos + 33, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeLessDamageAtLeast(entity, 2)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 232, this.topPos + 33, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeLessDamageAtLeast(entity, 3)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 255, this.topPos + 33, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeLessDamageAtLeast(entity, 4)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 277, this.topPos + 33, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeLivingBarrierAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evolution_ids.png"), this.leftPos + 211, this.topPos + 67, 0, 0, 72, 7, 72, 7);
		}
		if (NodeUtils.isNodeLivingBarrierAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 211, this.topPos + 57, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeLivingBarrierAtLeast(entity, 2)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 233, this.topPos + 57, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeLivingBarrierAtLeast(entity, 3)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 255, this.topPos + 57, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeLivingBarrierAtLeast(entity, 4)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 277, this.topPos + 57, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddMissAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evolution_ids.png"), this.leftPos + 211, this.topPos + 91, 0, 0, 72, 7, 72, 7);
		}
		if (NodeUtils.isNodeAddMissAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 211, this.topPos + 81, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddMissAtLeast(entity, 2)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 233, this.topPos + 81, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddMissAtLeast(entity, 3)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 255, this.topPos + 81, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeAddMissAtLeast(entity, 4)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 277, this.topPos + 81, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeSet2Done(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_quart_bar_reverse.png"), this.leftPos + 281, this.topPos + 16, 0, 0, 9, 74, 9, 74);
		}
		if (NodeUtils.isNodeSet2Done(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_short_bar.png"), this.leftPos + 14, this.topPos + 135, 0, 0, 4, 1, 4, 1);
		}
		if (PlayerStateUtils.isNexusPercDamageSelected(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 38, this.topPos + 127, 0, 0, 6, 6, 6, 6);
		}
		if (PlayerStateUtils.isNexusPercDamageSelected(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_triple_bar.png"), this.leftPos + 42, this.topPos + 110, 0, 0, 9, 50, 9, 50);
		}
		if (NodeUtils.isNodeRealDamageAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evolution_ids.png"), this.leftPos + 67, this.topPos + 115, 0, 0, 72, 7, 72, 7);
		}
		if (NodeUtils.isNodeRealDamageAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 67, this.topPos + 105, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeRealDamageAtLeast(entity, 2)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 89, this.topPos + 105, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeRealDamageAtLeast(entity, 3)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 111, this.topPos + 105, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeRealDamageAtLeast(entity, 4)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 133, this.topPos + 105, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeHealDamageAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evolution_ids.png"), this.leftPos + 67, this.topPos + 139, 0, 0, 72, 7, 72, 7);
		}
		if (NodeUtils.isNodeHealDamageAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 67, this.topPos + 129, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeHealDamageAtLeast(entity, 2)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 89, this.topPos + 129, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeHealDamageAtLeast(entity, 3)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 111, this.topPos + 129, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeHealDamageAtLeast(entity, 4)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 133, this.topPos + 129, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeWorseBreakAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evolution_ids.png"), this.leftPos + 67, this.topPos + 163, 0, 0, 72, 7, 72, 7);
		}
		if (NodeUtils.isNodeWorseBreakAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 67, this.topPos + 153, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeWorseBreakAtLeast(entity, 2)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 89, this.topPos + 153, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeWorseBreakAtLeast(entity, 3)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 111, this.topPos + 153, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeWorseBreakAtLeast(entity, 4)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 133, this.topPos + 153, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeSet3Done(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_triple_bar_reverse.png"), this.leftPos + 137, this.topPos + 111, 0, 0, 9, 50, 9, 50);
		}
		if (PlayerStateUtils.isNexusExpoShieldSelected(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_double_bar.png"), this.leftPos + 170, this.topPos + 123, 0, 0, 9, 26, 9, 26);
		}
		if (PlayerStateUtils.isNexusExpoShieldSelected(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 166, this.topPos + 129, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeEunectesAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evolution_ids.png"), this.leftPos + 195, this.topPos + 128, 0, 0, 72, 7, 72, 7);
		}
		if (NodeUtils.isNodeEunectesAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 195, this.topPos + 118, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeEunectesAtLeast(entity, 2)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 217, this.topPos + 118, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeEunectesAtLeast(entity, 3)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 239, this.topPos + 118, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeEunectesAtLeast(entity, 4)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 261, this.topPos + 118, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeLessArmorAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evolution_ids.png"), this.leftPos + 195, this.topPos + 152, 0, 0, 72, 7, 72, 7);
		}
		if (NodeUtils.isNodeLessArmorAtLeast(entity, 1)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 195, this.topPos + 142, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeLessArmorAtLeast(entity, 2)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 217, this.topPos + 142, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeLessArmorAtLeast(entity, 3)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 239, this.topPos + 142, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeLessArmorAtLeast(entity, 4)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_checked.png"), this.leftPos + 261, this.topPos + 142, 0, 0, 6, 6, 6, 6);
		}
		if (NodeUtils.isNodeSet4Done(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/player_evo_double_bar_reverse.png"), this.leftPos + 265, this.topPos + 124, 0, 0, 9, 26, 9, 26);
		}
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
        String result3 = "";
        if (entity != null) {
            String title2 = "";
            title2 = entity.getPersistentData().getString("showcasingEvoNode");
            if ((title2).isEmpty()) {
                title2 = "empty";
            }
            result3 = Component.translatable(("p_evo.caerula_arbor." + title2)).getString();
        }
        guiGraphics.drawString(this.font,

                result3, 8, 178, -52, false);
        String result1 = "";
        if (entity != null) {
            String title1 = "";
            title1 = entity.getPersistentData().getString("showcasingEvoNode");
            if ((title1).isEmpty()) {
                title1 = "empty";
            }
            result1 = Component.translatable(("p_evo.caerula_arbor." + title1 + ".desc")).getString();
        }
        guiGraphics.drawString(this.font,

                result1, 9, 192, -1, false);
        String result2 = "";
        if (entity != null) {
            result2 = Component.translatable("p_evo.caerula_arbor.quantity_reserve").getString() + (int) ((((Entity) entity).getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).reserve_quantity);
        }
        guiGraphics.drawString(this.font,

                result2, 3, 3, -13158601, false);
        String result = "";
        if (entity != null) {
            result = Component.translatable("p_evo.caerula_arbor.quality_reserve").getString() + (int) ((((Entity) entity).getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).reserve_quality);
        }
        guiGraphics.drawString(this.font,

                result, 3, 14, -16777114, false);
		guiGraphics.drawString(this.font,

				getNodeDesc1(), 9, 202, -1, false);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_player_evo_nexus_no_rejection = new ImageButton(this.leftPos + 10, this.topPos + 44, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_nexus_no_rejection.png"), 16, 32, e -> {
			if (true) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(0, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		});
		guistate.put("button:imagebutton_player_evo_nexus_no_rejection", imagebutton_player_evo_nexus_no_rejection);
		this.addRenderableWidget(imagebutton_player_evo_nexus_no_rejection);
		imagebutton_player_evo_checkbox = new ImageButton(this.leftPos + 270, this.topPos + 176, 26, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_checkbox.png"), 26, 24, e -> {
			if (true) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(1, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		});
		guistate.put("button:imagebutton_player_evo_checkbox", imagebutton_player_evo_checkbox);
		this.addRenderableWidget(imagebutton_player_evo_checkbox);
		imagebutton_player_evo_nexus_regnr_sanity = new ImageButton(this.leftPos + 38, this.topPos + 44, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_nexus_regnr_sanity.png"), 16, 32, e -> {
			if (PlayerStateUtils.isNexusNoRejectionSelected(entity)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(2, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 2, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.isNexusNoRejectionSelected(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_nexus_regnr_sanity", imagebutton_player_evo_nexus_regnr_sanity);
		this.addRenderableWidget(imagebutton_player_evo_nexus_regnr_sanity);
		imagebutton_player_evo_node_add_def = new ImageButton(this.leftPos + 71, this.topPos + 10, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_def.png"), 12, 24, e -> {
			if (PlayerStateUtils.isNexusRegSanitySelected(entity)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(3, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 3, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.isNexusRegSanitySelected(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_def", imagebutton_player_evo_node_add_def);
		this.addRenderableWidget(imagebutton_player_evo_node_add_def);
		imagebutton_player_evo_node_add_def1 = new ImageButton(this.leftPos + 93, this.topPos + 10, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_def1.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddDefAtLeast(entity, 1)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(4, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 4, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddDefAtLeast(entity, 1);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_def1", imagebutton_player_evo_node_add_def1);
		this.addRenderableWidget(imagebutton_player_evo_node_add_def1);
		imagebutton_player_evo_node_add_def2 = new ImageButton(this.leftPos + 115, this.topPos + 10, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_def2.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddDefAtLeast(entity, 2)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(5, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 5, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddDefAtLeast(entity, 2);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_def2", imagebutton_player_evo_node_add_def2);
		this.addRenderableWidget(imagebutton_player_evo_node_add_def2);
		imagebutton_player_evo_node_add_def3 = new ImageButton(this.leftPos + 137, this.topPos + 10, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_def3.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddDefAtLeast(entity, 3)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(6, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 6, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddDefAtLeast(entity, 3);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_def3", imagebutton_player_evo_node_add_def3);
		this.addRenderableWidget(imagebutton_player_evo_node_add_def3);
		imagebutton_player_evo_node_add_resis = new ImageButton(this.leftPos + 71, this.topPos + 34, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_resis.png"), 12, 24, e -> {
			if (PlayerStateUtils.isNexusRegSanitySelected(entity)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(7, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 7, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.isNexusRegSanitySelected(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_resis", imagebutton_player_evo_node_add_resis);
		this.addRenderableWidget(imagebutton_player_evo_node_add_resis);
		imagebutton_player_evo_node_add_resis1 = new ImageButton(this.leftPos + 93, this.topPos + 34, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_resis1.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddResisAtLeast(entity, 1)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(8, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 8, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddResisAtLeast(entity, 1);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_resis1", imagebutton_player_evo_node_add_resis1);
		this.addRenderableWidget(imagebutton_player_evo_node_add_resis1);
		imagebutton_player_evo_node_add_resis2 = new ImageButton(this.leftPos + 115, this.topPos + 34, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_resis2.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddResisAtLeast(entity, 2)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(9, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 9, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddResisAtLeast(entity, 2);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_resis2", imagebutton_player_evo_node_add_resis2);
		this.addRenderableWidget(imagebutton_player_evo_node_add_resis2);
		imagebutton_player_evo_node_add_resis3 = new ImageButton(this.leftPos + 137, this.topPos + 34, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_resis3.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddResisAtLeast(entity, 3)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(10, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 10, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddResisAtLeast(entity, 3);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_resis3", imagebutton_player_evo_node_add_resis3);
		this.addRenderableWidget(imagebutton_player_evo_node_add_resis3);
		imagebutton_player_evo_node_add_speed = new ImageButton(this.leftPos + 71, this.topPos + 58, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_speed.png"), 12, 24, e -> {
			if (PlayerStateUtils.isNexusRegSanitySelected(entity)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(11, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 11, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.isNexusRegSanitySelected(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_speed", imagebutton_player_evo_node_add_speed);
		this.addRenderableWidget(imagebutton_player_evo_node_add_speed);
		imagebutton_player_evo_node_add_speed1 = new ImageButton(this.leftPos + 93, this.topPos + 58, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_speed1.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddSpeedAtLeast(entity, 1)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(12, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 12, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddSpeedAtLeast(entity, 1);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_speed1", imagebutton_player_evo_node_add_speed1);
		this.addRenderableWidget(imagebutton_player_evo_node_add_speed1);
		imagebutton_player_evo_node_add_speed2 = new ImageButton(this.leftPos + 115, this.topPos + 58, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_speed2.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddSpeedAtLeast(entity, 2)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(13, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 13, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddSpeedAtLeast(entity, 2);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_speed2", imagebutton_player_evo_node_add_speed2);
		this.addRenderableWidget(imagebutton_player_evo_node_add_speed2);
		imagebutton_player_evo_node_add_speed3 = new ImageButton(this.leftPos + 137, this.topPos + 58, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_speed3.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddSpeedAtLeast(entity, 3)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(14, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 14, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddSpeedAtLeast(entity, 3);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_speed3", imagebutton_player_evo_node_add_speed3);
		this.addRenderableWidget(imagebutton_player_evo_node_add_speed3);
		imagebutton_player_evo_node_add_nervous = new ImageButton(this.leftPos + 71, this.topPos + 82, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_nervous.png"), 12, 24, e -> {
			if (PlayerStateUtils.isNexusRegSanitySelected(entity)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(15, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 15, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.isNexusRegSanitySelected(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_nervous", imagebutton_player_evo_node_add_nervous);
		this.addRenderableWidget(imagebutton_player_evo_node_add_nervous);
		imagebutton_player_evo_node_add_nervous1 = new ImageButton(this.leftPos + 93, this.topPos + 82, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_nervous1.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddSanityAtLeast(entity, 1)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(16, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 16, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddSanityAtLeast(entity, 1);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_nervous1", imagebutton_player_evo_node_add_nervous1);
		this.addRenderableWidget(imagebutton_player_evo_node_add_nervous1);
		imagebutton_player_evo_node_add_nervous2 = new ImageButton(this.leftPos + 115, this.topPos + 82, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_nervous2.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddSanityAtLeast(entity, 2)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(17, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 17, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddSanityAtLeast(entity, 2);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_nervous2", imagebutton_player_evo_node_add_nervous2);
		this.addRenderableWidget(imagebutton_player_evo_node_add_nervous2);
		imagebutton_player_evo_node_add_nervous3 = new ImageButton(this.leftPos + 137, this.topPos + 82, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_nervous3.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddSanityAtLeast(entity, 3)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(18, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 18, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddSanityAtLeast(entity, 3);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_nervous3", imagebutton_player_evo_node_add_nervous3);
		this.addRenderableWidget(imagebutton_player_evo_node_add_nervous3);
		imagebutton_player_evo_nexus_regnr_lights = new ImageButton(this.leftPos + 166, this.topPos + 45, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_nexus_regnr_lights.png"), 16, 32, e -> {
			if (NodeUtils.isNodeSet1Done(entity)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(19, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 19, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeSet1Done(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_nexus_regnr_lights", imagebutton_player_evo_nexus_regnr_lights);
		this.addRenderableWidget(imagebutton_player_evo_nexus_regnr_lights);
		imagebutton_player_evo_node_add_damage = new ImageButton(this.leftPos + 199, this.topPos + 10, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_damage.png"), 12, 24, e -> {
			if (PlayerStateUtils.isNexusRegLightsSelected(entity)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(20, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 20, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.isNexusRegLightsSelected(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_damage", imagebutton_player_evo_node_add_damage);
		this.addRenderableWidget(imagebutton_player_evo_node_add_damage);
		imagebutton_player_evo_node_add_damage1 = new ImageButton(this.leftPos + 221, this.topPos + 10, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_damage1.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddDamageAtLeast(entity, 1)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(21, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 21, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddDamageAtLeast(entity, 1);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_damage1", imagebutton_player_evo_node_add_damage1);
		this.addRenderableWidget(imagebutton_player_evo_node_add_damage1);
		imagebutton_player_evo_node_add_damage2 = new ImageButton(this.leftPos + 243, this.topPos + 10, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_damage2.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddDamageAtLeast(entity, 2)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(22, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 22, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddDamageAtLeast(entity, 2);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_damage2", imagebutton_player_evo_node_add_damage2);
		this.addRenderableWidget(imagebutton_player_evo_node_add_damage2);
		imagebutton_player_evo_node_add_damage3 = new ImageButton(this.leftPos + 265, this.topPos + 10, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_damage3.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddDamageAtLeast(entity, 3)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(23, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 23, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddDamageAtLeast(entity, 3);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_damage3", imagebutton_player_evo_node_add_damage3);
		this.addRenderableWidget(imagebutton_player_evo_node_add_damage3);
		imagebutton_player_evo_node_less_damage = new ImageButton(this.leftPos + 199, this.topPos + 34, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_less_damage.png"), 12, 24, e -> {
			if (PlayerStateUtils.isNexusRegLightsSelected(entity)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(24, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 24, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.isNexusRegLightsSelected(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_less_damage", imagebutton_player_evo_node_less_damage);
		this.addRenderableWidget(imagebutton_player_evo_node_less_damage);
		imagebutton_player_evo_node_less_damage1 = new ImageButton(this.leftPos + 221, this.topPos + 34, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_less_damage1.png"), 12, 24, e -> {
			if (NodeUtils.isNodeLessDamageAtLeast(entity, 1)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(25, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 25, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeLessDamageAtLeast(entity, 1);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_less_damage1", imagebutton_player_evo_node_less_damage1);
		this.addRenderableWidget(imagebutton_player_evo_node_less_damage1);
		imagebutton_player_evo_node_less_damage2 = new ImageButton(this.leftPos + 243, this.topPos + 34, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_less_damage2.png"), 12, 24, e -> {
			if (NodeUtils.isNodeLessDamageAtLeast(entity, 2)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(26, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 26, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeLessDamageAtLeast(entity, 2);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_less_damage2", imagebutton_player_evo_node_less_damage2);
		this.addRenderableWidget(imagebutton_player_evo_node_less_damage2);
		imagebutton_player_evo_node_less_damage3 = new ImageButton(this.leftPos + 265, this.topPos + 34, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_less_damage3.png"), 12, 24, e -> {
			if (NodeUtils.isNodeLessDamageAtLeast(entity, 3)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(27, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 27, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeLessDamageAtLeast(entity, 3);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_less_damage3", imagebutton_player_evo_node_less_damage3);
		this.addRenderableWidget(imagebutton_player_evo_node_less_damage3);
		imagebutton_player_evo_node_living_barrier = new ImageButton(this.leftPos + 199, this.topPos + 58, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_living_barrier.png"), 12, 24, e -> {
			if (PlayerStateUtils.isNexusRegLightsSelected(entity)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(28, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 28, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.isNexusRegLightsSelected(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_living_barrier", imagebutton_player_evo_node_living_barrier);
		this.addRenderableWidget(imagebutton_player_evo_node_living_barrier);
		imagebutton_player_evo_node_living_barrier1 = new ImageButton(this.leftPos + 221, this.topPos + 58, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_living_barrier1.png"), 12, 24, e -> {
			if (NodeUtils.isNodeLivingBarrierAtLeast(entity, 1)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(29, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 29, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeLivingBarrierAtLeast(entity, 1);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_living_barrier1", imagebutton_player_evo_node_living_barrier1);
		this.addRenderableWidget(imagebutton_player_evo_node_living_barrier1);
		imagebutton_player_evo_node_living_barrier2 = new ImageButton(this.leftPos + 243, this.topPos + 58, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_living_barrier2.png"), 12, 24, e -> {
			if (NodeUtils.isNodeLivingBarrierAtLeast(entity, 2)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(30, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 30, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeLivingBarrierAtLeast(entity, 2);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_living_barrier2", imagebutton_player_evo_node_living_barrier2);
		this.addRenderableWidget(imagebutton_player_evo_node_living_barrier2);
		imagebutton_player_evo_node_living_barrier3 = new ImageButton(this.leftPos + 265, this.topPos + 58, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_living_barrier3.png"), 12, 24, e -> {
			if (NodeUtils.isNodeLivingBarrierAtLeast(entity, 3)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(31, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 31, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeLivingBarrierAtLeast(entity, 3);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_living_barrier3", imagebutton_player_evo_node_living_barrier3);
		this.addRenderableWidget(imagebutton_player_evo_node_living_barrier3);
		imagebutton_player_evo_node_add_miss = new ImageButton(this.leftPos + 199, this.topPos + 82, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_miss.png"), 12, 24, e -> {
			if (PlayerStateUtils.isNexusRegLightsSelected(entity)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(32, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 32, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.isNexusRegLightsSelected(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_miss", imagebutton_player_evo_node_add_miss);
		this.addRenderableWidget(imagebutton_player_evo_node_add_miss);
		imagebutton_player_evo_node_add_miss1 = new ImageButton(this.leftPos + 221, this.topPos + 82, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_miss1.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddMissAtLeast(entity, 1)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(33, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 33, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddMissAtLeast(entity, 1);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_miss1", imagebutton_player_evo_node_add_miss1);
		this.addRenderableWidget(imagebutton_player_evo_node_add_miss1);
		imagebutton_player_evo_node_add_miss2 = new ImageButton(this.leftPos + 243, this.topPos + 82, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_miss2.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddMissAtLeast(entity, 2)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(34, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 34, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddMissAtLeast(entity, 2);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_miss2", imagebutton_player_evo_node_add_miss2);
		this.addRenderableWidget(imagebutton_player_evo_node_add_miss2);
		imagebutton_player_evo_node_add_miss3 = new ImageButton(this.leftPos + 265, this.topPos + 82, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_add_miss3.png"), 12, 24, e -> {
			if (NodeUtils.isNodeAddMissAtLeast(entity, 3)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(35, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 35, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeAddMissAtLeast(entity, 3);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_add_miss3", imagebutton_player_evo_node_add_miss3);
		this.addRenderableWidget(imagebutton_player_evo_node_add_miss3);
		imagebutton_player_evo_nexus_perc_attack = new ImageButton(this.leftPos + 22, this.topPos + 128, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_nexus_perc_attack.png"), 16, 32, e -> {
			if (NodeUtils.isNodeSet2Done(entity)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(36, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 36, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeSet2Done(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_nexus_perc_attack", imagebutton_player_evo_nexus_perc_attack);
		this.addRenderableWidget(imagebutton_player_evo_nexus_perc_attack);
		imagebutton_player_evo_node_real_damage = new ImageButton(this.leftPos + 55, this.topPos + 106, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_real_damage.png"), 12, 24, e -> {
			if (PlayerStateUtils.isNexusPercDamageSelected(entity)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(37, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 37, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.isNexusPercDamageSelected(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_real_damage", imagebutton_player_evo_node_real_damage);
		this.addRenderableWidget(imagebutton_player_evo_node_real_damage);
		imagebutton_player_evo_node_real_damage1 = new ImageButton(this.leftPos + 77, this.topPos + 106, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_real_damage1.png"), 12, 24, e -> {
			if (NodeUtils.isNodeRealDamageAtLeast(entity, 1)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(38, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 38, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeRealDamageAtLeast(entity, 1);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_real_damage1", imagebutton_player_evo_node_real_damage1);
		this.addRenderableWidget(imagebutton_player_evo_node_real_damage1);
		imagebutton_player_evo_node_real_damage2 = new ImageButton(this.leftPos + 99, this.topPos + 106, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_real_damage2.png"), 12, 24, e -> {
			if (NodeUtils.isNodeRealDamageAtLeast(entity, 2)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(39, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 39, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeRealDamageAtLeast(entity, 2);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_real_damage2", imagebutton_player_evo_node_real_damage2);
		this.addRenderableWidget(imagebutton_player_evo_node_real_damage2);
		imagebutton_player_evo_node_real_damage3 = new ImageButton(this.leftPos + 121, this.topPos + 106, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_real_damage3.png"), 12, 24, e -> {
			if (NodeUtils.isNodeRealDamageAtLeast(entity, 3)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(40, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 40, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeRealDamageAtLeast(entity, 3);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_real_damage3", imagebutton_player_evo_node_real_damage3);
		this.addRenderableWidget(imagebutton_player_evo_node_real_damage3);
		imagebutton_player_evo_node_heal_damage = new ImageButton(this.leftPos + 55, this.topPos + 130, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_heal_damage.png"), 12, 24, e -> {
			if (PlayerStateUtils.isNexusPercDamageSelected(entity)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(41, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 41, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.isNexusPercDamageSelected(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_heal_damage", imagebutton_player_evo_node_heal_damage);
		this.addRenderableWidget(imagebutton_player_evo_node_heal_damage);
		imagebutton_player_evo_node_heal_damage1 = new ImageButton(this.leftPos + 77, this.topPos + 130, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_heal_damage1.png"), 12, 24, e -> {
			if (NodeUtils.isNodeHealDamageAtLeast(entity, 1)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(42, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 42, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeHealDamageAtLeast(entity, 1);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_heal_damage1", imagebutton_player_evo_node_heal_damage1);
		this.addRenderableWidget(imagebutton_player_evo_node_heal_damage1);
		imagebutton_player_evo_node_heal_damage2 = new ImageButton(this.leftPos + 99, this.topPos + 130, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_heal_damage2.png"), 12, 24, e -> {
			if (NodeUtils.isNodeHealDamageAtLeast(entity, 2)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(43, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 43, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeHealDamageAtLeast(entity, 2);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_heal_damage2", imagebutton_player_evo_node_heal_damage2);
		this.addRenderableWidget(imagebutton_player_evo_node_heal_damage2);
		imagebutton_player_evo_node_heal_damage3 = new ImageButton(this.leftPos + 121, this.topPos + 130, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_heal_damage3.png"), 12, 24, e -> {
			if (NodeUtils.isNodeHealDamageAtLeast(entity, 3)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(44, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 44, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeHealDamageAtLeast(entity, 3);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_heal_damage3", imagebutton_player_evo_node_heal_damage3);
		this.addRenderableWidget(imagebutton_player_evo_node_heal_damage3);
		imagebutton_player_evo_node_worse_break = new ImageButton(this.leftPos + 55, this.topPos + 154, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_worse_break.png"), 12, 24, e -> {
			if (PlayerStateUtils.isNexusPercDamageSelected(entity)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(45, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 45, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.isNexusPercDamageSelected(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_worse_break", imagebutton_player_evo_node_worse_break);
		this.addRenderableWidget(imagebutton_player_evo_node_worse_break);
		imagebutton_player_evo_node_worse_break1 = new ImageButton(this.leftPos + 77, this.topPos + 154, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_worse_break1.png"), 12, 24, e -> {
			if (NodeUtils.isNodeWorseBreakAtLeast(entity, 1)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(46, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 46, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeWorseBreakAtLeast(entity, 1);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_worse_break1", imagebutton_player_evo_node_worse_break1);
		this.addRenderableWidget(imagebutton_player_evo_node_worse_break1);
		imagebutton_player_evo_node_worse_break2 = new ImageButton(this.leftPos + 99, this.topPos + 154, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_worse_break2.png"), 12, 24, e -> {
			if (NodeUtils.isNodeWorseBreakAtLeast(entity, 2)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(47, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 47, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeWorseBreakAtLeast(entity, 2);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_worse_break2", imagebutton_player_evo_node_worse_break2);
		this.addRenderableWidget(imagebutton_player_evo_node_worse_break2);
		imagebutton_player_evo_node_worse_break3 = new ImageButton(this.leftPos + 121, this.topPos + 154, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_worse_break3.png"), 12, 24, e -> {
			if (NodeUtils.isNodeWorseBreakAtLeast(entity, 3)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(48, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 48, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeWorseBreakAtLeast(entity, 3);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_worse_break3", imagebutton_player_evo_node_worse_break3);
		this.addRenderableWidget(imagebutton_player_evo_node_worse_break3);
		imagebutton_player_evo_nexus_expo_shield = new ImageButton(this.leftPos + 150, this.topPos + 130, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_nexus_expo_shield.png"), 16, 32, e -> {
			if (NodeUtils.isNodeSet3Done(entity)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(49, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 49, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeSet3Done(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_nexus_expo_shield", imagebutton_player_evo_nexus_expo_shield);
		this.addRenderableWidget(imagebutton_player_evo_nexus_expo_shield);
		imagebutton_player_evo_talent_eunectes = new ImageButton(this.leftPos + 183, this.topPos + 119, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_talent_eunectes.png"), 12, 24, e -> {
			if (PlayerStateUtils.isNexusExpoShieldSelected(entity)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(50, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 50, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.isNexusExpoShieldSelected(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_talent_eunectes", imagebutton_player_evo_talent_eunectes);
		this.addRenderableWidget(imagebutton_player_evo_talent_eunectes);
		imagebutton_player_evo_talent_eunectes1 = new ImageButton(this.leftPos + 205, this.topPos + 119, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_talent_eunectes1.png"), 12, 24, e -> {
			if (NodeUtils.isNodeEunectesAtLeast(entity, 1)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(51, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 51, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeEunectesAtLeast(entity, 1);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_talent_eunectes1", imagebutton_player_evo_talent_eunectes1);
		this.addRenderableWidget(imagebutton_player_evo_talent_eunectes1);
		imagebutton_player_evo_talent_eunectes2 = new ImageButton(this.leftPos + 227, this.topPos + 119, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_talent_eunectes2.png"), 12, 24, e -> {
			if (NodeUtils.isNodeEunectesAtLeast(entity, 2)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(52, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 52, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeEunectesAtLeast(entity, 2);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_talent_eunectes2", imagebutton_player_evo_talent_eunectes2);
		this.addRenderableWidget(imagebutton_player_evo_talent_eunectes2);
		imagebutton_player_evo_talent_eunectes3 = new ImageButton(this.leftPos + 249, this.topPos + 119, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_talent_eunectes3.png"), 12, 24, e -> {
			if (NodeUtils.isNodeEunectesAtLeast(entity, 3)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(53, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 53, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeEunectesAtLeast(entity, 3);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_talent_eunectes3", imagebutton_player_evo_talent_eunectes3);
		this.addRenderableWidget(imagebutton_player_evo_talent_eunectes3);
		imagebutton_player_evo_node_reduce_armor = new ImageButton(this.leftPos + 183, this.topPos + 143, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_reduce_armor.png"), 12, 24, e -> {
			if (PlayerStateUtils.isNexusExpoShieldSelected(entity)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(54, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 54, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.isNexusExpoShieldSelected(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_reduce_armor", imagebutton_player_evo_node_reduce_armor);
		this.addRenderableWidget(imagebutton_player_evo_node_reduce_armor);
		imagebutton_player_evo_node_reduce_armor1 = new ImageButton(this.leftPos + 205, this.topPos + 143, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_reduce_armor1.png"), 12, 24, e -> {
			if (NodeUtils.isNodeLessArmorAtLeast(entity, 1)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(55, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 55, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeLessArmorAtLeast(entity, 1);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_reduce_armor1", imagebutton_player_evo_node_reduce_armor1);
		this.addRenderableWidget(imagebutton_player_evo_node_reduce_armor1);
		imagebutton_player_evo_node_reduce_armor2 = new ImageButton(this.leftPos + 227, this.topPos + 143, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_reduce_armor2.png"), 12, 24, e -> {
			if (NodeUtils.isNodeLessArmorAtLeast(entity, 2)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(56, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 56, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeLessArmorAtLeast(entity, 2);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_reduce_armor2", imagebutton_player_evo_node_reduce_armor2);
		this.addRenderableWidget(imagebutton_player_evo_node_reduce_armor2);
		imagebutton_player_evo_node_reduce_armor3 = new ImageButton(this.leftPos + 249, this.topPos + 143, 12, 12, 0, 0, 12, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_player_evo_node_reduce_armor3.png"), 12, 24, e -> {
			if (NodeUtils.isNodeLessArmorAtLeast(entity, 3)) {
				CaerulaArborModNetwork.PACKET_HANDLER.sendToServer(new PlayerEvoButtonMessage(57, x, y, z));
				PlayerEvoButtonMessage.handleButtonAction(entity, 57, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = NodeUtils.isNodeLessArmorAtLeast(entity, 3);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_player_evo_node_reduce_armor3", imagebutton_player_evo_node_reduce_armor3);
		this.addRenderableWidget(imagebutton_player_evo_node_reduce_armor3);
	}

	private String getNodeDesc1() {
		String title = entity.getPersistentData().getString("showcasingEvoNode");
		if (title.isEmpty()) {
			title = "empty";
		}
		String key = "p_evo.caerula_arbor." + title + ".desc1";
		title = Component.translatable(key).getString();
		if (title.equals(key)) {
			return "";
		}
		return title;
	}
}















