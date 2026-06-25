package com.apocalypse.caerulaarbor.client.gui;

import com.apocalypse.caerulaarbor.utils.RelicUtils;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.network.RelicShowcaseButtonMessage;
import com.apocalypse.caerulaarbor.procedures.*;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import com.apocalypse.caerulaarbor.utils.PlayerStateUtils;
import com.apocalypse.caerulaarbor.world.inventory.RelicShowcaseMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public class RelicShowcaseScreen extends AbstractContainerScreen<RelicShowcaseMenu> {
	private final static HashMap<String, Object> guistate = RelicShowcaseMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_return;
	ImageButton imagebutton_relic_crown;
	ImageButton imagebutton_relic_spear;
	ImageButton imagebutton_kingsarmor;
	ImageButton imagebutton_extension;
	ImageButton imagebutton_kingcrystal;
	ImageButton imagebutton_archfiend_articraft;
	ImageButton imagebutton_archfi_flag;
	ImageButton imagebutton_archifi_bed;
	ImageButton imagebutton_royalfate;
	ImageButton imagebutton_hand_spike;
	ImageButton imagebutton_hand_reap;
	ImageButton imagebutton_hand_reap1;
	ImageButton imagebutton_hand_smash;
	ImageButton imagebutton_hand_swipe;
	ImageButton imagebutton_hand_curve;
	ImageButton imagebutton_hand_firework;
	ImageButton imagebutton_crimson_contarct_0;
	ImageButton imagebutton_survivor_contarct;
	ImageButton imagebutton_cursed_emelight_0;
	ImageButton imagebutton_cursed_glowbody_0;
	ImageButton imagebutton_cursed_research_0;
	ImageButton imagebutton_beef_can;
	ImageButton imagebutton_bowl_seagrass;
	ImageButton imagebutton_orangestorm;
	ImageButton imagebutton_coffee_candy;
	ImageButton imagebutton_cherrycan;
	ImageButton imagebutton_rainbow_candy;
	ImageButton imagebutton_boxcoffee;
	ImageButton imagebutton_musicboxsmall;
	ImageButton imagebutton_originium_iris;
	ImageButton imagebutton_flute;
	ImageButton imagebutton_voyageofsmall;
	ImageButton imagebutton_location_name;
	ImageButton imagebutton_kettle;
	ImageButton imagebutton_hand_sword;
	ImageButton imagebutton_chitinknife;
	ImageButton imagebutton_hand_speed;
	ImageButton imagebutton_smelly_hemostatic;
	ImageButton imagebutton_unripe_yearning;

	public RelicShowcaseScreen(RelicShowcaseMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 328;
		this.imageHeight = 216;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
		if (RelicUtils.hasCrown(entity))
			if (mouseX > leftPos + 4 && mouseX < leftPos + 20 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.relic_crown").getString() + ": " + Component.translatable("item.caerula_arbor.relic_crown.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasSpear(entity))
			if (mouseX > leftPos + 28 && mouseX < leftPos + 44 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.kings_spear").getString() + ": " + Component.translatable("item.caerula_arbor.kings_spear.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasArmor(entity))
			if (mouseX > leftPos + 100 && mouseX < leftPos + 116 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.kings_armour").getString() + ": " + Component.translatable("item.caerula_arbor.kings_armour.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasExtension(entity))
			if (mouseX > leftPos + 52 && mouseX < leftPos + 68 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.kings_extension").getString() + ": " + Component.translatable("item.caerula_arbor.kings_extension.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasCrystal(entity))
			if (mouseX > leftPos + 76 && mouseX < leftPos + 92 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.kings_crystal").getString() + ": " + Component.translatable("item.caerula_arbor.kings_crystal.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasArtifact(entity))
			if (mouseX > leftPos + 124 && mouseX < leftPos + 140 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.archfiends_artifact").getString() + ": " + Component.translatable("item.caerula_arbor.archfiends_artifact.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasFlag(entity))
			if (mouseX > leftPos + 148 && mouseX < leftPos + 164 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.archfiends_flag").getString() + ": " + Component.translatable("item.caerula_arbor.archfiends_flag.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasBed(entity))
			if (mouseX > leftPos + 172 && mouseX < leftPos + 188 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.archfiends_bed").getString() + ": " + Component.translatable("item.caerula_arbor.archfiends_bed.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRoyalfate(entity))
			if (mouseX > leftPos + 196 && mouseX < leftPos + 212 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.royal_fate").getString() + ": " + Component.translatable("item.caerula_arbor.royal_fate.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasThorns(entity))
			if (mouseX > leftPos + 4 && mouseX < leftPos + 20 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_of_thorns").getString() + ": " + Component.translatable("item.caerula_arbor.hand_of_thorns.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasStrangle(entity))
			if (mouseX > leftPos + 28 && mouseX < leftPos + 44 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_of_strangle").getString() + ": " + Component.translatable("item.caerula_arbor.hand_of_strangle.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasFertility(entity))
			if (mouseX > leftPos + 52 && mouseX < leftPos + 68 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_of_fertiliy").getString() + ": " + Component.translatable("item.caerula_arbor.hand_of_fertiliy.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasSpeed(entity))
			if (mouseX > leftPos + 76 && mouseX < leftPos + 92 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_of_speed").getString() + ": " + Component.translatable("item.caerula_arbor.hand_of_speed.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasBarren(entity))
			if (mouseX > leftPos + 100 && mouseX < leftPos + 116 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_of_barren").getString() + ": " + Component.translatable("item.caerula_arbor.hand_of_barren.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasSwipe(entity))
			if (mouseX > leftPos + 124 && mouseX < leftPos + 140 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_of_spotless").getString() + ": " + Component.translatable("item.caerula_arbor.hand_of_spotless.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.getEngrave(entity) > 0)
			if (mouseX > leftPos + 148 && mouseX < leftPos + 164 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_of_engrave").getString() + ": " + Component.translatable("item.caerula_arbor.hand_of_engrave.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasFirework(entity))
			if (mouseX > leftPos + 172 && mouseX < leftPos + 188 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_of_firework").getString() + ": " + Component.translatable("item.caerula_arbor.hand_of_firework.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasTreaty(entity))
			if (mouseX > leftPos + 4 && mouseX < leftPos + 20 && mouseY > topPos + 52 && mouseY < topPos + 68)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.crimson_treaty").getString() + ": " + Component.translatable("item.caerula_arbor.crimson_treaty.description_0").getString()), mouseX, mouseY);
		if (PlayerStateUtils.hasSurvivorCont(entity))
			if (mouseX > leftPos + 28 && mouseX < leftPos + 44 && mouseY > topPos + 52 && mouseY < topPos + 68)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.survivor_contract").getString() + ": " + Component.translatable("item.caerula_arbor.survivor_contract.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasEmelight(entity))
			if (mouseX > leftPos + 4 && mouseX < leftPos + 20 && mouseY > topPos + 196 && mouseY < topPos + 212)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.relic_curse_emelight").getString() + ": " + Component.translatable("item.caerula_arbor.relic_curse_emelight.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasGlowbody(entity))
			if (mouseX > leftPos + 28 && mouseX < leftPos + 44 && mouseY > topPos + 196 && mouseY < topPos + 212)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.relic_cursed_glowbody").getString() + ": " + Component.translatable("item.caerula_arbor.relic_cursed_glowbody.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasResearch(entity))
			if (mouseX > leftPos + 52 && mouseX < leftPos + 68 && mouseY > topPos + 196 && mouseY < topPos + 212)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.relic_cursed_research").getString() + ": " + Component.translatable("item.caerula_arbor.relic_cursed_research.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasMeatcan(entity))
			if (mouseX > leftPos + 4 && mouseX < leftPos + 20 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.meat_can").getString() + ": " + Component.translatable("item.caerula_arbor.meat_can.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasSeagrass(entity))
			if (mouseX > leftPos + 28 && mouseX < leftPos + 44 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.bowl_seagrass").getString() + ": " + Component.translatable("item.caerula_arbor.bowl_seagrass.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasOrange(entity))
			if (mouseX > leftPos + 52 && mouseX < leftPos + 68 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.golden_storm").getString() + ": " + Component.translatable("item.caerula_arbor.golden_storm.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasCoffee(entity))
			if (mouseX > leftPos + 76 && mouseX < leftPos + 92 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.coffee_candy").getString() + ": " + Component.translatable("item.caerula_arbor.coffee_candy.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasBerries(entity))
			if (mouseX > leftPos + 124 && mouseX < leftPos + 140 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.canned_cherry").getString() + ": " + Component.translatable("item.caerula_arbor.canned_cherry.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRainbow(entity))
			if (mouseX > leftPos + 100 && mouseX < leftPos + 116 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.rainbow_candy").getString() + ": " + Component.translatable("item.caerula_arbor.rainbow_candy.description_0").getString()), mouseX, mouseY);
		if (PlayerStateUtils.hasAromatic(entity))
			if (mouseX > leftPos + 148 && mouseX < leftPos + 164 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.aromatic_coffee").getString() + ": " + Component.translatable("item.caerula_arbor.aromatic_coffee.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasMusicbox(entity))
			if (mouseX > leftPos + 172 && mouseX < leftPos + 188 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.solo_music_box").getString() + ": " + Component.translatable("item.caerula_arbor.solo_music_box.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasIris(entity))
			if (mouseX > leftPos + 220 && mouseX < leftPos + 236 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.redstone_iris_flower").getString() + ": " + Component.translatable("item.caerula_arbor.redstone_iris_flower.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasFlute(entity))
			if (mouseX > leftPos + 196 && mouseX < leftPos + 212 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.odd_flute").getString() + ": " + Component.translatable("item.caerula_arbor.odd_flute.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasVoygold(entity))
			if (mouseX > leftPos + 244 && mouseX < leftPos + 260 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.voyage_of_gold").getString() + ": " + Component.translatable("item.caerula_arbor.voyage_of_gold.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasDurin(entity))
			if (mouseX > leftPos + 268 && mouseX < leftPos + 284 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.piglin_diary").getString() + ": " + Component.translatable("item.caerula_arbor.piglin_diary.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasToponym(entity))
			if (mouseX > leftPos + 292 && mouseX < leftPos + 308 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.toponym_textology").getString() + ": " + Component.translatable("item.caerula_arbor.toponym_textology.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasKettle(entity))
			if (mouseX > leftPos + 4 && mouseX < leftPos + 20 && mouseY > topPos + 100 && mouseY < topPos + 116)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.kettle").getString() + ": " + Component.translatable("item.caerula_arbor.kettle.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasChitin(entity))
			if (mouseX > leftPos + 52 && mouseX < leftPos + 68 && mouseY > topPos + 52 && mouseY < topPos + 68)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.chitin_knife").getString() + ": " + Component.translatable("item.caerula_arbor.chitin_knife.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasAllay(entity))
			if (mouseX > leftPos + 28 && mouseX < leftPos + 44 && mouseY > topPos + 100 && mouseY < topPos + 116)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.allay_sculpture").getString() + ": " + Component.translatable("item.caerula_arbor.allay_sculpture.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasBatbed(entity))
			if (mouseX > leftPos + 52 && mouseX < leftPos + 68 && mouseY > topPos + 100 && mouseY < topPos + 116)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.bat_bed").getString() + ": " + Component.translatable("item.caerula_arbor.bat_bed.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasOmnikey(entity))
			if (mouseX > leftPos + 124 && mouseX < leftPos + 140 && mouseY > topPos + 100 && mouseY < topPos + 116)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.omni_key").getString() + ": " + Component.translatable("item.caerula_arbor.omni_key.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasScore(entity))
			if (mouseX > leftPos + 76 && mouseX < leftPos + 92 && mouseY > topPos + 100 && mouseY < topPos + 116)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.score").getString() + ": " + Component.translatable("item.caerula_arbor.score.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRescission(entity))
			if (mouseX > leftPos + 100 && mouseX < leftPos + 116 && mouseY > topPos + 100 && mouseY < topPos + 116)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.rescission").getString() + ": " + Component.translatable("item.caerula_arbor.rescission.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasStare(entity))
			if (mouseX > leftPos + 148 && mouseX < leftPos + 164 && mouseY > topPos + 100 && mouseY < topPos + 116)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.guardian_stare").getString() + ": " + Component.translatable("item.caerula_arbor.guardian_stare.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasSword(entity))
			if (mouseX > leftPos + 196 && mouseX < leftPos + 212 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_sword").getString() + ": " + Component.translatable("item.caerula_arbor.hand_sword.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasHeart(entity))
			if (mouseX > leftPos + 76 && mouseX < leftPos + 92 && mouseY > topPos + 196 && mouseY < topPos + 212)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.caerula_heart").getString() + ": " + Component.translatable("item.caerula_arbor.caerula_heart.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasHemost(entity))
			if (mouseX > leftPos + 76 && mouseX < leftPos + 92 && mouseY > topPos + 52 && mouseY < topPos + 68)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.smelly_hemostatic").getString() + ": " + Component.translatable("item.caerula_arbor.smelly_hemostatic.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasLongevity(entity))
			if (mouseX > leftPos + 172 && mouseX < leftPos + 188 && mouseY > topPos + 100 && mouseY < topPos + 116)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.proof_of_longevity").getString() + ": " + Component.translatable("item.caerula_arbor.proof_of_longevity.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasYearning(entity))
			if (mouseX > leftPos + 100 && mouseX < leftPos + 116 && mouseY > topPos + 52 && mouseY < topPos + 68)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.unripe_yearning").getString() + ": " + Component.translatable("item.caerula_arbor.unripe_yearning.description_0").getString()), mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/relic_bg.png"), this.leftPos + 0, this.topPos + 0, 0, 0, 328, 216, 328, 216);

		if (RelicUtils.hasLongevity(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/longevity.png"), this.leftPos + 172, this.topPos + 100, 0, 0, 16, 16, 16, 16);
		}
		if (RelicUtils.hasDurin(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/durin_diary.png"), this.leftPos + 268, this.topPos + 76, 0, 0, 16, 16, 16, 16);
		}
		if (RelicUtils.hasAllay(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/stonealley.png"), this.leftPos + 28, this.topPos + 100, 0, 0, 16, 16, 16, 16);
		}
		if (RelicUtils.hasBatbed(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/itembatbed.png"), this.leftPos + 52, this.topPos + 100, 0, 0, 16, 16, 16, 16);
		}
		if (RelicUtils.hasOmnikey(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/omnikey.png"), this.leftPos + 124, this.topPos + 100, 0, 0, 16, 16, 16, 16);
		}
		if (RelicUtils.hasScore(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/score.png"), this.leftPos + 76, this.topPos + 100, 0, 0, 16, 16, 16, 16);
		}
		if (RelicUtils.hasRescission(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/rescission.png"), this.leftPos + 100, this.topPos + 100, 0, 0, 16, 16, 16, 16);
		}
		if (RelicUtils.hasStare(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/guardianstare.png"), this.leftPos + 148, this.topPos + 100, 0, 0, 16, 16, 16, 16);
		}
		if (RelicUtils.hasHeart(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/caerulaheart.png"), this.leftPos + 76, this.topPos + 196, 0, 0, 16, 16, 16, 16);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.relic_showcase.label_relic_showcase"), 4, -12, -1, false);
		if (RelicUtils.getEngrave(entity) > 0)
			guiGraphics.drawString(this.font,

					EntityUtils.getPlayerEnrave(entity), 157, 36, -16777165, false);
		if (RelicUtils.getEngrave(entity) > 0)
			guiGraphics.drawString(this.font,

					EntityUtils.getPlayerEnrave(entity), 156, 36, -1, false);
		if (PlayerStateUtils.hasSurvivorCont(entity))
			guiGraphics.drawString(this.font,

					EntityUtils.getPlayerSurvconta(entity), 37, 60, -12829636, false);
		if (PlayerStateUtils.hasSurvivorCont(entity))
			guiGraphics.drawString(this.font,

					EntityUtils.getPlayerSurvconta(entity), 36, 60, -1, false);
	}

	@Override
	public void init() {
		super.init();
		button_return = new PlainTextButton(this.leftPos + 292, this.topPos + 204, 24, 20, Component.translatable("gui.caerula_arbor.relic_showcase.button_return"), e -> {
			if (true) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(0, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}, this.font);
		guistate.put("button:button_return", button_return);
		this.addRenderableWidget(button_return);
		imagebutton_relic_crown = new ImageButton(this.leftPos + 4, this.topPos + 4, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_relic_crown.png"), 16, 32, e -> {
			if (RelicUtils.hasCrown(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(1, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasCrown(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_relic_crown", imagebutton_relic_crown);
		this.addRenderableWidget(imagebutton_relic_crown);
		imagebutton_relic_spear = new ImageButton(this.leftPos + 28, this.topPos + 4, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_relic_spear.png"), 16, 32, e -> {
			if (RelicUtils.hasSpear(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(2, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 2, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasSpear(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_relic_spear", imagebutton_relic_spear);
		this.addRenderableWidget(imagebutton_relic_spear);
		imagebutton_kingsarmor = new ImageButton(this.leftPos + 100, this.topPos + 4, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_kingsarmor.png"), 16, 32, e -> {
			if (RelicUtils.hasArmor(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(3, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 3, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasArmor(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_kingsarmor", imagebutton_kingsarmor);
		this.addRenderableWidget(imagebutton_kingsarmor);
		imagebutton_extension = new ImageButton(this.leftPos + 52, this.topPos + 4, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_extension.png"), 16, 32, e -> {
			if (RelicUtils.hasExtension(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(4, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 4, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasExtension(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_extension", imagebutton_extension);
		this.addRenderableWidget(imagebutton_extension);
		imagebutton_kingcrystal = new ImageButton(this.leftPos + 76, this.topPos + 4, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_kingcrystal.png"), 16, 32, e -> {
			if (RelicUtils.hasCrystal(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(5, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 5, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasCrystal(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_kingcrystal", imagebutton_kingcrystal);
		this.addRenderableWidget(imagebutton_kingcrystal);
		imagebutton_archfiend_articraft = new ImageButton(this.leftPos + 124, this.topPos + 4, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_archfiend_articraft.png"), 16, 32, e -> {
			if (RelicUtils.hasArtifact(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(6, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 6, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasArtifact(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_archfiend_articraft", imagebutton_archfiend_articraft);
		this.addRenderableWidget(imagebutton_archfiend_articraft);
		imagebutton_archfi_flag = new ImageButton(this.leftPos + 148, this.topPos + 4, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_archfi_flag.png"), 16, 32, e -> {
			if (RelicUtils.hasFlag(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(7, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 7, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasFlag(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_archfi_flag", imagebutton_archfi_flag);
		this.addRenderableWidget(imagebutton_archfi_flag);
		imagebutton_archifi_bed = new ImageButton(this.leftPos + 172, this.topPos + 4, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_archifi_bed.png"), 16, 32, e -> {
			if (RelicUtils.hasBed(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(8, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 8, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasBed(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_archifi_bed", imagebutton_archifi_bed);
		this.addRenderableWidget(imagebutton_archifi_bed);
		imagebutton_royalfate = new ImageButton(this.leftPos + 196, this.topPos + 4, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_royalfate.png"), 16, 32, e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRoyalfate(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_royalfate", imagebutton_royalfate);
		this.addRenderableWidget(imagebutton_royalfate);
		imagebutton_hand_spike = new ImageButton(this.leftPos + 4, this.topPos + 28, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_hand_spike.png"), 16, 32, e -> {
			if (RelicUtils.hasThorns(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(10, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 10, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasThorns(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_spike", imagebutton_hand_spike);
		this.addRenderableWidget(imagebutton_hand_spike);
		imagebutton_hand_reap = new ImageButton(this.leftPos + 28, this.topPos + 28, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_hand_reap.png"), 16, 32, e -> {
			if (RelicUtils.hasStrangle(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(11, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 11, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasStrangle(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_reap", imagebutton_hand_reap);
		this.addRenderableWidget(imagebutton_hand_reap);
		imagebutton_hand_reap1 = new ImageButton(this.leftPos + 52, this.topPos + 28, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_hand_reap1.png"), 16, 32, e -> {
			if (RelicUtils.hasFertility(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(12, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 12, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasFertility(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_reap1", imagebutton_hand_reap1);
		this.addRenderableWidget(imagebutton_hand_reap1);
		imagebutton_hand_smash = new ImageButton(this.leftPos + 100, this.topPos + 28, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_hand_smash.png"), 16, 32, e -> {
			if (RelicUtils.hasBarren(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(13, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 13, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasBarren(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_smash", imagebutton_hand_smash);
		this.addRenderableWidget(imagebutton_hand_smash);
		imagebutton_hand_swipe = new ImageButton(this.leftPos + 124, this.topPos + 28, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_hand_swipe.png"), 16, 32, e -> {
			if (RelicUtils.hasSwipe(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(14, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 14, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasSwipe(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_swipe", imagebutton_hand_swipe);
		this.addRenderableWidget(imagebutton_hand_swipe);
		imagebutton_hand_curve = new ImageButton(this.leftPos + 148, this.topPos + 28, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_hand_curve.png"), 16, 32, e -> {
			if (RelicUtils.getEngrave(entity) > 0) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(15, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 15, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.getEngrave(entity) > 0;
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_curve", imagebutton_hand_curve);
		this.addRenderableWidget(imagebutton_hand_curve);
		imagebutton_hand_firework = new ImageButton(this.leftPos + 172, this.topPos + 28, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_hand_firework.png"), 16, 32, e -> {
			if (RelicUtils.hasFirework(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(16, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 16, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasFirework(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_firework", imagebutton_hand_firework);
		this.addRenderableWidget(imagebutton_hand_firework);
		imagebutton_crimson_contarct_0 = new ImageButton(this.leftPos + 4, this.topPos + 52, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_crimson_contarct_0.png"), 16, 32, e -> {
			if (RelicUtils.hasTreaty(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(17, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 17, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasTreaty(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_crimson_contarct_0", imagebutton_crimson_contarct_0);
		this.addRenderableWidget(imagebutton_crimson_contarct_0);
		imagebutton_survivor_contarct = new ImageButton(this.leftPos + 28, this.topPos + 52, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_survivor_contarct.png"), 16, 32, e -> {
			if (PlayerStateUtils.hasSurvivorCont(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(18, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 18, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.hasSurvivorCont(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_survivor_contarct", imagebutton_survivor_contarct);
		this.addRenderableWidget(imagebutton_survivor_contarct);
		imagebutton_cursed_emelight_0 = new ImageButton(this.leftPos + 4, this.topPos + 196, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_cursed_emelight_0.png"), 16, 32, e -> {
			if (RelicUtils.hasEmelight(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(19, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 19, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasEmelight(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_cursed_emelight_0", imagebutton_cursed_emelight_0);
		this.addRenderableWidget(imagebutton_cursed_emelight_0);
		imagebutton_cursed_glowbody_0 = new ImageButton(this.leftPos + 28, this.topPos + 196, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_cursed_glowbody_0.png"), 16, 32, e -> {
			if (RelicUtils.hasGlowbody(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(20, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 20, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasGlowbody(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_cursed_glowbody_0", imagebutton_cursed_glowbody_0);
		this.addRenderableWidget(imagebutton_cursed_glowbody_0);
		imagebutton_cursed_research_0 = new ImageButton(this.leftPos + 52, this.topPos + 196, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_cursed_research_0.png"), 16, 32, e -> {
			if (RelicUtils.hasResearch(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(21, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 21, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasResearch(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_cursed_research_0", imagebutton_cursed_research_0);
		this.addRenderableWidget(imagebutton_cursed_research_0);
		imagebutton_beef_can = new ImageButton(this.leftPos + 4, this.topPos + 76, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_beef_can.png"), 16, 32, e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasMeatcan(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_beef_can", imagebutton_beef_can);
		this.addRenderableWidget(imagebutton_beef_can);
		imagebutton_bowl_seagrass = new ImageButton(this.leftPos + 28, this.topPos + 76, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_bowl_seagrass.png"), 16, 32, e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasSeagrass(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_bowl_seagrass", imagebutton_bowl_seagrass);
		this.addRenderableWidget(imagebutton_bowl_seagrass);
		imagebutton_orangestorm = new ImageButton(this.leftPos + 52, this.topPos + 76, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_orangestorm.png"), 16, 32, e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasOrange(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_orangestorm", imagebutton_orangestorm);
		this.addRenderableWidget(imagebutton_orangestorm);
		imagebutton_coffee_candy = new ImageButton(this.leftPos + 76, this.topPos + 76, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_coffee_candy.png"), 16, 32, e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasCoffee(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_coffee_candy", imagebutton_coffee_candy);
		this.addRenderableWidget(imagebutton_coffee_candy);
		imagebutton_cherrycan = new ImageButton(this.leftPos + 124, this.topPos + 76, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_cherrycan.png"), 16, 32, e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasBerries(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_cherrycan", imagebutton_cherrycan);
		this.addRenderableWidget(imagebutton_cherrycan);
		imagebutton_rainbow_candy = new ImageButton(this.leftPos + 100, this.topPos + 76, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_rainbow_candy.png"), 16, 32, e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRainbow(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_rainbow_candy", imagebutton_rainbow_candy);
		this.addRenderableWidget(imagebutton_rainbow_candy);
		imagebutton_boxcoffee = new ImageButton(this.leftPos + 148, this.topPos + 76, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_boxcoffee.png"), 16, 32, e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.hasAromatic(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_boxcoffee", imagebutton_boxcoffee);
		this.addRenderableWidget(imagebutton_boxcoffee);
		imagebutton_musicboxsmall = new ImageButton(this.leftPos + 172, this.topPos + 76, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_musicboxsmall.png"), 16, 32, e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasMusicbox(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_musicboxsmall", imagebutton_musicboxsmall);
		this.addRenderableWidget(imagebutton_musicboxsmall);
		imagebutton_originium_iris = new ImageButton(this.leftPos + 220, this.topPos + 76, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_originium_iris.png"), 16, 32, e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasIris(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_originium_iris", imagebutton_originium_iris);
		this.addRenderableWidget(imagebutton_originium_iris);
		imagebutton_flute = new ImageButton(this.leftPos + 196, this.topPos + 76, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_flute.png"), 16, 32, e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasFlute(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_flute", imagebutton_flute);
		this.addRenderableWidget(imagebutton_flute);
		imagebutton_voyageofsmall = new ImageButton(this.leftPos + 244, this.topPos + 76, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_voyageofsmall.png"), 16, 32, e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasVoygold(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_voyageofsmall", imagebutton_voyageofsmall);
		this.addRenderableWidget(imagebutton_voyageofsmall);
		imagebutton_location_name = new ImageButton(this.leftPos + 292, this.topPos + 76, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_location_name.png"), 16, 32, e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasToponym(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_location_name", imagebutton_location_name);
		this.addRenderableWidget(imagebutton_location_name);
		imagebutton_kettle = new ImageButton(this.leftPos + 4, this.topPos + 100, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_kettle.png"), 16, 32, e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasKettle(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_kettle", imagebutton_kettle);
		this.addRenderableWidget(imagebutton_kettle);
		imagebutton_hand_sword = new ImageButton(this.leftPos + 196, this.topPos + 28, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_hand_sword.png"), 16, 32, e -> {
			if (RelicUtils.hasSword(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(35, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 35, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasSword(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_sword", imagebutton_hand_sword);
		this.addRenderableWidget(imagebutton_hand_sword);
		imagebutton_chitinknife = new ImageButton(this.leftPos + 52, this.topPos + 52, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_chitinknife.png"), 16, 32, e -> {
			if (RelicUtils.hasChitin(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(36, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 36, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasChitin(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_chitinknife", imagebutton_chitinknife);
		this.addRenderableWidget(imagebutton_chitinknife);
		imagebutton_hand_speed = new ImageButton(this.leftPos + 76, this.topPos + 28, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_hand_speed.png"), 16, 32, e -> {
			if (RelicUtils.hasSpeed(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(37, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 37, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasSpeed(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_speed", imagebutton_hand_speed);
		this.addRenderableWidget(imagebutton_hand_speed);
		imagebutton_smelly_hemostatic = new ImageButton(this.leftPos + 76, this.topPos + 52, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_smelly_hemostatic.png"), 16, 32, e -> {
			if (RelicUtils.hasHemost(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(38, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 38, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasHemost(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_smelly_hemostatic", imagebutton_smelly_hemostatic);
		this.addRenderableWidget(imagebutton_smelly_hemostatic);
		imagebutton_unripe_yearning = new ImageButton(this.leftPos + 100, this.topPos + 52, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/atlas/imagebutton_unripe_yearning.png"), 16, 32, e -> {
			if (RelicUtils.hasYearning(entity)) {
				CaerulaArborMod.PACKET_HANDLER.sendToServer(new RelicShowcaseButtonMessage(39, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 39, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasYearning(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_unripe_yearning", imagebutton_unripe_yearning);
		this.addRenderableWidget(imagebutton_unripe_yearning);
	}
}
