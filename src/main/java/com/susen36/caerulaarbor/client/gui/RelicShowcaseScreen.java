package com.susen36.caerulaarbor.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.Relic;
import com.susen36.caerulaarbor.menu.RelicShowcaseMenu;
import com.susen36.caerulaarbor.network.send.RelicShowcaseButtonMessage;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.PlayerStateUtils;
import com.susen36.caerulaarbor.util.RelicUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

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
		this.renderBackground(guiGraphics,mouseX,mouseY,partialTicks);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.KING_CROWN, entity))
			if (mouseX > leftPos + 4 && mouseX < leftPos + 20 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.relic_crown").getString() + ": " + Component.translatable("item.caerula_arbor.relic_crown.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.KING_SPEAR, entity))
			if (mouseX > leftPos + 28 && mouseX < leftPos + 44 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.kings_spear").getString() + ": " + Component.translatable("item.caerula_arbor.kings_spear.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.KING_ARMOR, entity))
			if (mouseX > leftPos + 100 && mouseX < leftPos + 116 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.kings_armour").getString() + ": " + Component.translatable("item.caerula_arbor.kings_armour.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.KING_EXTENSION, entity))
			if (mouseX > leftPos + 52 && mouseX < leftPos + 68 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.kings_extension").getString() + ": " + Component.translatable("item.caerula_arbor.kings_extension.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.KING_CRYSTAL, entity))
			if (mouseX > leftPos + 76 && mouseX < leftPos + 92 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.kings_crystal").getString() + ": " + Component.translatable("item.caerula_arbor.kings_crystal.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.SARKAZ_KING_ARTIFACT, entity))
			if (mouseX > leftPos + 124 && mouseX < leftPos + 140 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.archfiends_artifact").getString() + ": " + Component.translatable("item.caerula_arbor.archfiends_artifact.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.SARKAZ_KING_FLAG, entity))
			if (mouseX > leftPos + 148 && mouseX < leftPos + 164 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.archfiends_flag").getString() + ": " + Component.translatable("item.caerula_arbor.archfiends_flag.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.SARKAZ_KING_BED, entity))
			if (mouseX > leftPos + 172 && mouseX < leftPos + 188 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.archfiends_bed").getString() + ": " + Component.translatable("item.caerula_arbor.archfiends_bed.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.ROYALFATE, entity))
			if (mouseX > leftPos + 196 && mouseX < leftPos + 212 && mouseY > topPos + 4 && mouseY < topPos + 20)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.royal_fate").getString() + ": " + Component.translatable("item.caerula_arbor.royal_fate.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.HAND_THORNS, entity))
			if (mouseX > leftPos + 4 && mouseX < leftPos + 20 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_of_thorns").getString() + ": " + Component.translatable("item.caerula_arbor.hand_of_thorns.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.HAND_STRANGLE, entity))
			if (mouseX > leftPos + 28 && mouseX < leftPos + 44 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_of_strangle").getString() + ": " + Component.translatable("item.caerula_arbor.hand_of_strangle.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.HAND_FERTILITY, entity))
			if (mouseX > leftPos + 52 && mouseX < leftPos + 68 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_of_fertiliy").getString() + ": " + Component.translatable("item.caerula_arbor.hand_of_fertiliy.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.HAND_SPEED, entity))
			if (mouseX > leftPos + 76 && mouseX < leftPos + 92 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_of_speed").getString() + ": " + Component.translatable("item.caerula_arbor.hand_of_speed.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.HAND_OF_PULVERIZATION, entity))
			if (mouseX > leftPos + 100 && mouseX < leftPos + 116 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_of_barren").getString() + ": " + Component.translatable("item.caerula_arbor.hand_of_barren.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.HAND_SWIPE, entity))
			if (mouseX > leftPos + 124 && mouseX < leftPos + 140 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_of_spotless").getString() + ": " + Component.translatable("item.caerula_arbor.hand_of_spotless.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.getRelic(Relic.HAND_ENGRAVE, entity) > 0)
			if (mouseX > leftPos + 148 && mouseX < leftPos + 164 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_of_engrave").getString() + ": " + Component.translatable("item.caerula_arbor.hand_of_engrave.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.HAND_FIREWORK, entity))
			if (mouseX > leftPos + 172 && mouseX < leftPos + 188 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_of_firework").getString() + ": " + Component.translatable("item.caerula_arbor.hand_of_firework.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.TREATY, entity))
			if (mouseX > leftPos + 4 && mouseX < leftPos + 20 && mouseY > topPos + 52 && mouseY < topPos + 68)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.crimson_treaty").getString() + ": " + Component.translatable("item.caerula_arbor.crimson_treaty.description_0").getString()), mouseX, mouseY);
		if (PlayerStateUtils.hasSurvivorCont(entity))
			if (mouseX > leftPos + 28 && mouseX < leftPos + 44 && mouseY > topPos + 52 && mouseY < topPos + 68)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.survivor_contract").getString() + ": " + Component.translatable("item.caerula_arbor.survivor_contract.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.CURSED_EMELIGHT, entity))
			if (mouseX > leftPos + 4 && mouseX < leftPos + 20 && mouseY > topPos + 196 && mouseY < topPos + 212)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.relic_curse_emelight").getString() + ": " + Component.translatable("item.caerula_arbor.relic_curse_emelight.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.CURSED_GLOWBODY, entity))
			if (mouseX > leftPos + 28 && mouseX < leftPos + 44 && mouseY > topPos + 196 && mouseY < topPos + 212)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.relic_cursed_glowbody").getString() + ": " + Component.translatable("item.caerula_arbor.relic_cursed_glowbody.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.CURSED_RESEARCH, entity))
			if (mouseX > leftPos + 52 && mouseX < leftPos + 68 && mouseY > topPos + 196 && mouseY < topPos + 212)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.relic_cursed_research").getString() + ": " + Component.translatable("item.caerula_arbor.relic_cursed_research.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.FEATURED_CANNED_MEAT, entity))
			if (mouseX > leftPos + 4 && mouseX < leftPos + 20 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.meat_can").getString() + ": " + Component.translatable("item.caerula_arbor.meat_can.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.SEAWEED_SALAD, entity))
			if (mouseX > leftPos + 28 && mouseX < leftPos + 44 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.bowl_seagrass").getString() + ": " + Component.translatable("item.caerula_arbor.bowl_seagrass.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.ORANGE_STORM, entity))
			if (mouseX > leftPos + 52 && mouseX < leftPos + 68 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.golden_storm").getString() + ": " + Component.translatable("item.caerula_arbor.golden_storm.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.COFFEE_PLAINS_COFFEE_CANDY, entity))
			if (mouseX > leftPos + 76 && mouseX < leftPos + 92 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.coffee_candy").getString() + ": " + Component.translatable("item.caerula_arbor.coffee_candy.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.PITTS_ASSORTED_FRUITS, entity))
			if (mouseX > leftPos + 124 && mouseX < leftPos + 140 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.canned_cherry").getString() + ": " + Component.translatable("item.caerula_arbor.canned_cherry.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.UTIL_RAINBOW, entity))
			if (mouseX > leftPos + 100 && mouseX < leftPos + 116 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.rainbow_candy").getString() + ": " + Component.translatable("item.caerula_arbor.rainbow_candy.description_0").getString()), mouseX, mouseY);
		if (PlayerStateUtils.hasAromatic(entity))
			if (mouseX > leftPos + 148 && mouseX < leftPos + 164 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.aromatic_coffee").getString() + ": " + Component.translatable("item.caerula_arbor.aromatic_coffee.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.UTIL_MUSICBOX, entity))
			if (mouseX > leftPos + 172 && mouseX < leftPos + 188 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.solo_music_box").getString() + ": " + Component.translatable("item.caerula_arbor.solo_music_box.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.UTIL_IRIS, entity))
			if (mouseX > leftPos + 220 && mouseX < leftPos + 236 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.redstone_iris_flower").getString() + ": " + Component.translatable("item.caerula_arbor.redstone_iris_flower.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.WEIRD_FLUTE, entity))
			if (mouseX > leftPos + 196 && mouseX < leftPos + 212 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.odd_flute").getString() + ": " + Component.translatable("item.caerula_arbor.odd_flute.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.PURE_GOLD_EXPEDITION, entity))
			if (mouseX > leftPos + 244 && mouseX < leftPos + 260 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.voyage_of_gold").getString() + ": " + Component.translatable("item.caerula_arbor.voyage_of_gold.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.DURIN_OVERGROUND_ODYSSEY, entity))
			if (mouseX > leftPos + 268 && mouseX < leftPos + 284 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.piglin_diary").getString() + ": " + Component.translatable("item.caerula_arbor.piglin_diary.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.UTIL_TOPONYM, entity))
			if (mouseX > leftPos + 292 && mouseX < leftPos + 308 && mouseY > topPos + 76 && mouseY < topPos + 92)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.toponym_textology").getString() + ": " + Component.translatable("item.caerula_arbor.toponym_textology.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.HOT_WATER_KETTLE, entity))
			if (mouseX > leftPos + 4 && mouseX < leftPos + 20 && mouseY > topPos + 100 && mouseY < topPos + 116)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.kettle").getString() + ": " + Component.translatable("item.caerula_arbor.kettle.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.LEGEND_CHITIN, entity))
			if (mouseX > leftPos + 52 && mouseX < leftPos + 68 && mouseY > topPos + 52 && mouseY < topPos + 68)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.chitin_knife").getString() + ": " + Component.translatable("item.caerula_arbor.chitin_knife.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.UTIL_ALLAY, entity))
			if (mouseX > leftPos + 28 && mouseX < leftPos + 44 && mouseY > topPos + 100 && mouseY < topPos + 116)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.allay_sculpture").getString() + ": " + Component.translatable("item.caerula_arbor.allay_sculpture.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.VAMPIRES_BED, entity))
			if (mouseX > leftPos + 52 && mouseX < leftPos + 68 && mouseY > topPos + 100 && mouseY < topPos + 116)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.bat_bed").getString() + ": " + Component.translatable("item.caerula_arbor.bat_bed.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.UTIL_OMNIKEY, entity))
			if (mouseX > leftPos + 124 && mouseX < leftPos + 140 && mouseY > topPos + 100 && mouseY < topPos + 116)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.omni_key").getString() + ": " + Component.translatable("item.caerula_arbor.omni_key.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.UTIL_SCORE, entity))
			if (mouseX > leftPos + 76 && mouseX < leftPos + 92 && mouseY > topPos + 100 && mouseY < topPos + 116)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.score").getString() + ": " + Component.translatable("item.caerula_arbor.score.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.UTIL_RESCISSION, entity))
			if (mouseX > leftPos + 100 && mouseX < leftPos + 116 && mouseY > topPos + 100 && mouseY < topPos + 116)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.rescission").getString() + ": " + Component.translatable("item.caerula_arbor.rescission.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.UTIL_STARE, entity))
			if (mouseX > leftPos + 148 && mouseX < leftPos + 164 && mouseY > topPos + 100 && mouseY < topPos + 116)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.guardian_stare").getString() + ": " + Component.translatable("item.caerula_arbor.guardian_stare.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.HAND_SWORD, entity))
			if (mouseX > leftPos + 196 && mouseX < leftPos + 212 && mouseY > topPos + 28 && mouseY < topPos + 44)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.hand_sword").getString() + ": " + Component.translatable("item.caerula_arbor.hand_sword.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.CURSED_HEART, entity))
			if (mouseX > leftPos + 76 && mouseX < leftPos + 92 && mouseY > topPos + 196 && mouseY < topPos + 212)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.caerula_heart").getString() + ": " + Component.translatable("item.caerula_arbor.caerula_heart.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.HEMOST, entity))
			if (mouseX > leftPos + 76 && mouseX < leftPos + 92 && mouseY > topPos + 52 && mouseY < topPos + 68)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.smelly_hemostatic").getString() + ": " + Component.translatable("item.caerula_arbor.smelly_hemostatic.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.PROOF_OF_LONGEVITY, entity))
			if (mouseX > leftPos + 172 && mouseX < leftPos + 188 && mouseY > topPos + 100 && mouseY < topPos + 116)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.proof_of_longevity").getString() + ": " + Component.translatable("item.caerula_arbor.proof_of_longevity.description_0").getString()), mouseX, mouseY);
		if (RelicUtils.hasRelic(Relic.YEARNING, entity))
			if (mouseX > leftPos + 100 && mouseX < leftPos + 116 && mouseY > topPos + 52 && mouseY < topPos + 68)
                guiGraphics.renderTooltip(font, Component.literal(Component.translatable("item.caerula_arbor.unripe_yearning").getString() + ": " + Component.translatable("item.caerula_arbor.unripe_yearning.description_0").getString()), mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/relic_bg.png"), this.leftPos, this.topPos, 0, 0, 328, 216, 328, 216);

		if (RelicUtils.hasRelic(Relic.PROOF_OF_LONGEVITY, entity)) {
			guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/longevity.png"), this.leftPos + 172, this.topPos + 100, 0, 0, 16, 16, 16, 16);
		}
		if (RelicUtils.hasRelic(Relic.DURIN_OVERGROUND_ODYSSEY, entity)) {
			guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/durin_diary.png"), this.leftPos + 268, this.topPos + 76, 0, 0, 16, 16, 16, 16);
		}
		if (RelicUtils.hasRelic(Relic.UTIL_ALLAY, entity)) {
			guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/stonealley.png"), this.leftPos + 28, this.topPos + 100, 0, 0, 16, 16, 16, 16);
		}
		if (RelicUtils.hasRelic(Relic.VAMPIRES_BED, entity)) {
			guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/itembatbed.png"), this.leftPos + 52, this.topPos + 100, 0, 0, 16, 16, 16, 16);
		}
		if (RelicUtils.hasRelic(Relic.UTIL_OMNIKEY, entity)) {
			guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/omnikey.png"), this.leftPos + 124, this.topPos + 100, 0, 0, 16, 16, 16, 16);
		}
		if (RelicUtils.hasRelic(Relic.UTIL_SCORE, entity)) {
			guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/score.png"), this.leftPos + 76, this.topPos + 100, 0, 0, 16, 16, 16, 16);
		}
		if (RelicUtils.hasRelic(Relic.UTIL_RESCISSION, entity)) {
			guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/rescission.png"), this.leftPos + 100, this.topPos + 100, 0, 0, 16, 16, 16, 16);
		}
		if (RelicUtils.hasRelic(Relic.UTIL_STARE, entity)) {
			guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/guardianstare.png"), this.leftPos + 148, this.topPos + 100, 0, 0, 16, 16, 16, 16);
		}
		if (RelicUtils.hasRelic(Relic.CURSED_HEART, entity)) {
			guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/caerulaheart.png"), this.leftPos + 76, this.topPos + 196, 0, 0, 16, 16, 16, 16);
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
		if (RelicUtils.getRelic(Relic.HAND_ENGRAVE, entity) > 0)
			guiGraphics.drawString(this.font,

					EntityUtils.getPlayerEnrave(entity), 157, 36, -16777165, false);
		if (RelicUtils.getRelic(Relic.HAND_ENGRAVE, entity) > 0)
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
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(0, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}, this.font);
		guistate.put("button:button_return", button_return);
		this.addRenderableWidget(button_return);
		imagebutton_relic_crown = new ImageButton(this.leftPos + 4, this.topPos + 4, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_relic_crown"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_relic_crown_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.KING_CROWN, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(1, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.KING_CROWN, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_relic_crown", imagebutton_relic_crown);
		this.addRenderableWidget(imagebutton_relic_crown);
		imagebutton_relic_spear = new ImageButton(this.leftPos + 28, this.topPos + 4, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_relic_spear"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_relic_spear_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.KING_SPEAR, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(2, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 2, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.KING_SPEAR, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_relic_spear", imagebutton_relic_spear);
		this.addRenderableWidget(imagebutton_relic_spear);
		imagebutton_kingsarmor = new ImageButton(this.leftPos + 100, this.topPos + 4, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_kingsarmor"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_kingsarmor_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.KING_ARMOR, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(3, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 3, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.KING_ARMOR, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_kingsarmor", imagebutton_kingsarmor);
		this.addRenderableWidget(imagebutton_kingsarmor);
		imagebutton_extension = new ImageButton(this.leftPos + 52, this.topPos + 4, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_extension"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_extension_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.KING_EXTENSION, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(4, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 4, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.KING_EXTENSION, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_extension", imagebutton_extension);
		this.addRenderableWidget(imagebutton_extension);
		imagebutton_kingcrystal = new ImageButton(this.leftPos + 76, this.topPos + 4, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_kingcrystal"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_kingcrystal_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.KING_CRYSTAL, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(5, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 5, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.KING_CRYSTAL, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_kingcrystal", imagebutton_kingcrystal);
		this.addRenderableWidget(imagebutton_kingcrystal);
		imagebutton_archfiend_articraft = new ImageButton(this.leftPos + 124, this.topPos + 4, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_archfiend_articraft"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_archfiend_articraft_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.SARKAZ_KING_ARTIFACT, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(6, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 6, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.SARKAZ_KING_ARTIFACT, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_archfiend_articraft", imagebutton_archfiend_articraft);
		this.addRenderableWidget(imagebutton_archfiend_articraft);
		imagebutton_archfi_flag = new ImageButton(this.leftPos + 148, this.topPos + 4, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_archfi_flag"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_archfi_flag_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.SARKAZ_KING_FLAG, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(7, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 7, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.SARKAZ_KING_FLAG, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_archfi_flag", imagebutton_archfi_flag);
		this.addRenderableWidget(imagebutton_archfi_flag);
		imagebutton_archifi_bed = new ImageButton(this.leftPos + 172, this.topPos + 4, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_archifi_bed"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_archifi_bed_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.SARKAZ_KING_BED, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(8, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 8, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.SARKAZ_KING_BED, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_archifi_bed", imagebutton_archifi_bed);
		this.addRenderableWidget(imagebutton_archifi_bed);
		imagebutton_royalfate = new ImageButton(this.leftPos + 196, this.topPos + 4, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_royalfate"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_royalfate_highlighted")), e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.ROYALFATE, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_royalfate", imagebutton_royalfate);
		this.addRenderableWidget(imagebutton_royalfate);
		imagebutton_hand_spike = new ImageButton(this.leftPos + 4, this.topPos + 28, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_spike"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_spike_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.HAND_THORNS, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(10, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 10, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.HAND_THORNS, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_spike", imagebutton_hand_spike);
		this.addRenderableWidget(imagebutton_hand_spike);
		imagebutton_hand_reap = new ImageButton(this.leftPos + 28, this.topPos + 28, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_reap"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_reap_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.HAND_STRANGLE, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(11, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 11, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.HAND_STRANGLE, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_reap", imagebutton_hand_reap);
		this.addRenderableWidget(imagebutton_hand_reap);
		imagebutton_hand_reap1 = new ImageButton(this.leftPos + 52, this.topPos + 28, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_reap1"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_reap1_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.HAND_FERTILITY, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(12, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 12, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.HAND_FERTILITY, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_reap1", imagebutton_hand_reap1);
		this.addRenderableWidget(imagebutton_hand_reap1);
		imagebutton_hand_smash = new ImageButton(this.leftPos + 100, this.topPos + 28, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_smash"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_smash_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.HAND_OF_PULVERIZATION, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(13, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 13, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.HAND_OF_PULVERIZATION, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_smash", imagebutton_hand_smash);
		this.addRenderableWidget(imagebutton_hand_smash);
		imagebutton_hand_swipe = new ImageButton(this.leftPos + 124, this.topPos + 28, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_swipe"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_swipe_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.HAND_SWIPE, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(14, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 14, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.HAND_SWIPE, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_swipe", imagebutton_hand_swipe);
		this.addRenderableWidget(imagebutton_hand_swipe);
		imagebutton_hand_curve = new ImageButton(this.leftPos + 148, this.topPos + 28, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_curve"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_curve_highlighted")), e -> {
			if (RelicUtils.getRelic(Relic.HAND_ENGRAVE, entity) > 0) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(15, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 15, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.getRelic(Relic.HAND_ENGRAVE, entity) > 0;
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_curve", imagebutton_hand_curve);
		this.addRenderableWidget(imagebutton_hand_curve);
		imagebutton_hand_firework = new ImageButton(this.leftPos + 172, this.topPos + 28, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_firework"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_firework_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.HAND_FIREWORK, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(16, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 16, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.HAND_FIREWORK, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_firework", imagebutton_hand_firework);
		this.addRenderableWidget(imagebutton_hand_firework);
		imagebutton_crimson_contarct_0 = new ImageButton(this.leftPos + 4, this.topPos + 52, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_crimson_contarct_0"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_crimson_contarct_0_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.TREATY, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(17, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 17, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.TREATY, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_crimson_contarct_0", imagebutton_crimson_contarct_0);
		this.addRenderableWidget(imagebutton_crimson_contarct_0);
		imagebutton_survivor_contarct = new ImageButton(this.leftPos + 28, this.topPos + 52, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_survivor_contarct"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_survivor_contarct_highlighted")), e -> {
			if (PlayerStateUtils.hasSurvivorCont(entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(18, x, y, z));
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
		imagebutton_cursed_emelight_0 = new ImageButton(this.leftPos + 4, this.topPos + 196, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_cursed_emelight_0"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_cursed_emelight_0_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.CURSED_EMELIGHT, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(19, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 19, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.CURSED_EMELIGHT, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_cursed_emelight_0", imagebutton_cursed_emelight_0);
		this.addRenderableWidget(imagebutton_cursed_emelight_0);
		imagebutton_cursed_glowbody_0 = new ImageButton(this.leftPos + 28, this.topPos + 196, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_cursed_glowbody_0"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_cursed_glowbody_0_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.CURSED_GLOWBODY, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(20, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 20, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.CURSED_GLOWBODY, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_cursed_glowbody_0", imagebutton_cursed_glowbody_0);
		this.addRenderableWidget(imagebutton_cursed_glowbody_0);
		imagebutton_cursed_research_0 = new ImageButton(this.leftPos + 52, this.topPos + 196, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_cursed_research_0"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_cursed_research_0_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.CURSED_RESEARCH, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(21, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 21, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.CURSED_RESEARCH, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_cursed_research_0", imagebutton_cursed_research_0);
		this.addRenderableWidget(imagebutton_cursed_research_0);
		imagebutton_beef_can = new ImageButton(this.leftPos + 4, this.topPos + 76, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_beef_can"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_beef_can_highlighted")), e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.FEATURED_CANNED_MEAT, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_beef_can", imagebutton_beef_can);
		this.addRenderableWidget(imagebutton_beef_can);
		imagebutton_bowl_seagrass = new ImageButton(this.leftPos + 28, this.topPos + 76, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_bowl_seagrass"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_bowl_seagrass_highlighted")), e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.SEAWEED_SALAD, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_bowl_seagrass", imagebutton_bowl_seagrass);
		this.addRenderableWidget(imagebutton_bowl_seagrass);
		imagebutton_orangestorm = new ImageButton(this.leftPos + 52, this.topPos + 76, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_orangestorm"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_orangestorm_highlighted")), e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.ORANGE_STORM, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_orangestorm", imagebutton_orangestorm);
		this.addRenderableWidget(imagebutton_orangestorm);
		imagebutton_coffee_candy = new ImageButton(this.leftPos + 76, this.topPos + 76, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_coffee_candy"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_coffee_candy_highlighted")), e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.COFFEE_PLAINS_COFFEE_CANDY, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_coffee_candy", imagebutton_coffee_candy);
		this.addRenderableWidget(imagebutton_coffee_candy);
		imagebutton_cherrycan = new ImageButton(this.leftPos + 124, this.topPos + 76, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_cherrycan"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_cherrycan_highlighted")), e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.PITTS_ASSORTED_FRUITS, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_cherrycan", imagebutton_cherrycan);
		this.addRenderableWidget(imagebutton_cherrycan);
		imagebutton_rainbow_candy = new ImageButton(this.leftPos + 100, this.topPos + 76, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_rainbow_candy"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_rainbow_candy_highlighted")), e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.UTIL_RAINBOW, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_rainbow_candy", imagebutton_rainbow_candy);
		this.addRenderableWidget(imagebutton_rainbow_candy);
		imagebutton_boxcoffee = new ImageButton(this.leftPos + 148, this.topPos + 76, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_boxcoffee"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_boxcoffee_highlighted")), e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.hasAromatic(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_boxcoffee", imagebutton_boxcoffee);
		this.addRenderableWidget(imagebutton_boxcoffee);
		imagebutton_musicboxsmall = new ImageButton(this.leftPos + 172, this.topPos + 76, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_musicboxsmall"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_musicboxsmall_highlighted")), e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.UTIL_MUSICBOX, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_musicboxsmall", imagebutton_musicboxsmall);
		this.addRenderableWidget(imagebutton_musicboxsmall);
		imagebutton_originium_iris = new ImageButton(this.leftPos + 220, this.topPos + 76, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_originium_iris"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_originium_iris_highlighted")), e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.UTIL_IRIS, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_originium_iris", imagebutton_originium_iris);
		this.addRenderableWidget(imagebutton_originium_iris);
		imagebutton_flute = new ImageButton(this.leftPos + 196, this.topPos + 76, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_flute"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_flute_highlighted")), e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.WEIRD_FLUTE, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_flute", imagebutton_flute);
		this.addRenderableWidget(imagebutton_flute);
		imagebutton_voyageofsmall = new ImageButton(this.leftPos + 244, this.topPos + 76, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_voyageofsmall"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_voyageofsmall_highlighted")), e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.PURE_GOLD_EXPEDITION, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_voyageofsmall", imagebutton_voyageofsmall);
		this.addRenderableWidget(imagebutton_voyageofsmall);
		imagebutton_location_name = new ImageButton(this.leftPos + 292, this.topPos + 76, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_location_name"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_location_name_highlighted")), e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.UTIL_TOPONYM, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_location_name", imagebutton_location_name);
		this.addRenderableWidget(imagebutton_location_name);
		imagebutton_kettle = new ImageButton(this.leftPos + 4, this.topPos + 100, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_kettle"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_kettle_highlighted")), e -> {
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.HOT_WATER_KETTLE, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_kettle", imagebutton_kettle);
		this.addRenderableWidget(imagebutton_kettle);
		imagebutton_hand_sword = new ImageButton(this.leftPos + 196, this.topPos + 28, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_sword"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_sword_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.HAND_SWORD, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(35, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 35, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.HAND_SWORD, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_sword", imagebutton_hand_sword);
		this.addRenderableWidget(imagebutton_hand_sword);
		imagebutton_chitinknife = new ImageButton(this.leftPos + 52, this.topPos + 52, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_chitinknife"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_chitinknife_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.LEGEND_CHITIN, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(36, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 36, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.LEGEND_CHITIN, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_chitinknife", imagebutton_chitinknife);
		this.addRenderableWidget(imagebutton_chitinknife);
		imagebutton_hand_speed = new ImageButton(this.leftPos + 76, this.topPos + 28, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_speed"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_hand_speed_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.HAND_SPEED, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(37, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 37, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.HAND_SPEED, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_hand_speed", imagebutton_hand_speed);
		this.addRenderableWidget(imagebutton_hand_speed);
		imagebutton_smelly_hemostatic = new ImageButton(this.leftPos + 76, this.topPos + 52, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_smelly_hemostatic"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_smelly_hemostatic_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.HEMOST, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(38, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 38, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.HEMOST, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_smelly_hemostatic", imagebutton_smelly_hemostatic);
		this.addRenderableWidget(imagebutton_smelly_hemostatic);
		imagebutton_unripe_yearning = new ImageButton(this.leftPos + 100, this.topPos + 52, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_unripe_yearning"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "overlay/atlas/imagebutton_unripe_yearning_highlighted")), e -> {
			if (RelicUtils.hasRelic(Relic.YEARNING, entity)) {
				PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(39, x, y, z));
				RelicShowcaseButtonMessage.handleButtonAction(entity, 39, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = RelicUtils.hasRelic(Relic.YEARNING, entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_unripe_yearning", imagebutton_unripe_yearning);
		this.addRenderableWidget(imagebutton_unripe_yearning);
		imagebutton_relic_crown.visible = RelicUtils.hasRelic(Relic.KING_CROWN, entity);
		imagebutton_relic_spear.visible = RelicUtils.hasRelic(Relic.KING_SPEAR, entity);
		imagebutton_kingsarmor.visible = RelicUtils.hasRelic(Relic.KING_ARMOR, entity);
		imagebutton_extension.visible = RelicUtils.hasRelic(Relic.KING_EXTENSION, entity);
		imagebutton_kingcrystal.visible = RelicUtils.hasRelic(Relic.KING_CRYSTAL, entity);
		imagebutton_archfiend_articraft.visible = RelicUtils.hasRelic(Relic.SARKAZ_KING_ARTIFACT, entity);
		imagebutton_archfi_flag.visible = RelicUtils.hasRelic(Relic.SARKAZ_KING_FLAG, entity);
		imagebutton_archifi_bed.visible = RelicUtils.hasRelic(Relic.SARKAZ_KING_BED, entity);
		imagebutton_royalfate.visible = RelicUtils.hasRelic(Relic.ROYALFATE, entity);
		imagebutton_hand_spike.visible = RelicUtils.hasRelic(Relic.HAND_THORNS, entity);
		imagebutton_hand_reap.visible = RelicUtils.hasRelic(Relic.HAND_STRANGLE, entity);
		imagebutton_hand_reap1.visible = RelicUtils.hasRelic(Relic.HAND_FERTILITY, entity);
		imagebutton_hand_smash.visible = RelicUtils.hasRelic(Relic.HAND_OF_PULVERIZATION, entity);
		imagebutton_hand_swipe.visible = RelicUtils.hasRelic(Relic.HAND_SWIPE, entity);
		imagebutton_hand_curve.visible = RelicUtils.getRelic(Relic.HAND_ENGRAVE, entity) > 0;
		imagebutton_hand_firework.visible = RelicUtils.hasRelic(Relic.HAND_FIREWORK, entity);
		imagebutton_crimson_contarct_0.visible = RelicUtils.hasRelic(Relic.TREATY, entity);
		imagebutton_survivor_contarct.visible = PlayerStateUtils.hasSurvivorCont(entity);
		imagebutton_cursed_emelight_0.visible = RelicUtils.hasRelic(Relic.CURSED_EMELIGHT, entity);
		imagebutton_cursed_glowbody_0.visible = RelicUtils.hasRelic(Relic.CURSED_GLOWBODY, entity);
		imagebutton_cursed_research_0.visible = RelicUtils.hasRelic(Relic.CURSED_RESEARCH, entity);
		imagebutton_beef_can.visible = RelicUtils.hasRelic(Relic.FEATURED_CANNED_MEAT, entity);
		imagebutton_bowl_seagrass.visible = RelicUtils.hasRelic(Relic.SEAWEED_SALAD, entity);
		imagebutton_orangestorm.visible = RelicUtils.hasRelic(Relic.ORANGE_STORM, entity);
		imagebutton_coffee_candy.visible = RelicUtils.hasRelic(Relic.COFFEE_PLAINS_COFFEE_CANDY, entity);
		imagebutton_cherrycan.visible = RelicUtils.hasRelic(Relic.PITTS_ASSORTED_FRUITS, entity);
		imagebutton_rainbow_candy.visible = RelicUtils.hasRelic(Relic.UTIL_RAINBOW, entity);
		imagebutton_boxcoffee.visible = PlayerStateUtils.hasAromatic(entity);
		imagebutton_musicboxsmall.visible = RelicUtils.hasRelic(Relic.UTIL_MUSICBOX, entity);
		imagebutton_originium_iris.visible = RelicUtils.hasRelic(Relic.UTIL_IRIS, entity);
		imagebutton_flute.visible = RelicUtils.hasRelic(Relic.WEIRD_FLUTE, entity);
		imagebutton_voyageofsmall.visible = RelicUtils.hasRelic(Relic.PURE_GOLD_EXPEDITION, entity);
		imagebutton_location_name.visible = RelicUtils.hasRelic(Relic.UTIL_TOPONYM, entity);
		imagebutton_kettle.visible = RelicUtils.hasRelic(Relic.HOT_WATER_KETTLE, entity);
		imagebutton_hand_sword.visible = RelicUtils.hasRelic(Relic.HAND_SWORD, entity);
		imagebutton_chitinknife.visible = RelicUtils.hasRelic(Relic.LEGEND_CHITIN, entity);
		imagebutton_hand_speed.visible = RelicUtils.hasRelic(Relic.HAND_SPEED, entity);
		imagebutton_smelly_hemostatic.visible = RelicUtils.hasRelic(Relic.HEMOST, entity);
		imagebutton_unripe_yearning.visible = RelicUtils.hasRelic(Relic.YEARNING, entity);
	}
}