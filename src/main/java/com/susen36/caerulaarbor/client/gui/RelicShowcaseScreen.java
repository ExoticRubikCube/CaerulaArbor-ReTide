package com.susen36.caerulaarbor.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.Relic;
import com.susen36.caerulaarbor.item.relic.RelicItemBase;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class RelicShowcaseScreen extends AbstractContainerScreen<RelicShowcaseMenu> {
	private final static HashMap<String, Object> guistate = RelicShowcaseMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;

	// ===== 网格 & 分页 =====
	private static final int SLOT_ORIGIN_X = 4;
	private static final int SLOT_ORIGIN_Y = 4;
	private static final int SLOT_SIZE = 16;
	private static final int SLOT_STEP = 24;
	private static final int COLS_PER_PAGE = 13;
	private static final int ROWS_PER_PAGE = 8;
	private static final int PAGE_SIZE = COLS_PER_PAGE * ROWS_PER_PAGE;
	private int currentPage = 0;
	private int totalPages;

	// ===== 滑动 & 拖拽 =====
	private static final int DRAG_THRESHOLD = 40;
	private boolean isDragging;
	private int dragStartX;
	private int dragAccum;

	// ===== 控件 =====
	Button button_return;
	Button button_prev_page;
	Button button_next_page;
	final List<ImageButton> relicButtons = new ArrayList<>();

	// ===== 显示条目（按原 init 创建顺序，保持布局语义） =====
	private record RelicDisplayEntry(
		String guiKey,
		Relic relic,
		SpecialType special,
		int buttonId,
		String atlasBase,
		String itemKey,
		Predicate<Entity> visibleTest,
		String overlayTex,
		int[] labelPos  // [offsetX, offsetY, shadowColor, textColor] or null
	) {
		enum SpecialType { NONE, SURVIVOR, AROMATIC }
	}

	private static final List<RelicDisplayEntry> ALL_ENTRIES;
	static {
		ALL_ENTRIES = new ArrayList<>();
		// ===== 第 1 行 =====
		ALL_ENTRIES.add(e("imagebutton_relic_crown",          Relic.KING_CROWN,                1,  "imagebutton_relic_crown",          "relic_crown"));
		ALL_ENTRIES.add(e("imagebutton_relic_spear",          Relic.KING_SPEAR,                2,  "imagebutton_relic_spear",          "kings_spear"));
		ALL_ENTRIES.add(e("imagebutton_extension",            Relic.KING_EXTENSION,            4,  "imagebutton_extension",            "kings_extension"));
		ALL_ENTRIES.add(e("imagebutton_kingcrystal",          Relic.KING_CRYSTAL,              5,  "imagebutton_kingcrystal",          "kings_crystal"));
		ALL_ENTRIES.add(e("imagebutton_kingsarmor",           Relic.KING_ARMOR,                3,  "imagebutton_kingsarmor",           "kings_armour"));
		ALL_ENTRIES.add(e("imagebutton_archfiend_articraft",  Relic.SARKAZ_KING_ARTIFACT,      6,  "imagebutton_archfiend_articraft",  "archfiends_artifact"));
		ALL_ENTRIES.add(e("imagebutton_archfi_flag",          Relic.SARKAZ_KING_FLAG,          7,  "imagebutton_archfi_flag",          "archfiends_flag"));
		ALL_ENTRIES.add(e("imagebutton_archifi_bed",          Relic.SARKAZ_KING_BED,           8,  "imagebutton_archifi_bed",          "archfiends_bed"));
		ALL_ENTRIES.add(e("imagebutton_royalfate",            Relic.ROYALFATE,                -1,  "imagebutton_royalfate",            "royal_fate"));
		ALL_ENTRIES.add(e("imagebutton_hand_sword",           Relic.HAND_SWORD,               35,  "imagebutton_hand_sword",           "hand_sword"));
		// ===== 第 2 行 =====
		ALL_ENTRIES.add(e("imagebutton_hand_spike",           Relic.HAND_THORNS,              10,  "imagebutton_hand_spike",           "hand_of_thorns"));
		ALL_ENTRIES.add(e("imagebutton_hand_reap",            Relic.HAND_STRANGLE,            11,  "imagebutton_hand_reap",            "hand_of_strangle"));
		ALL_ENTRIES.add(e("imagebutton_hand_reap1",           Relic.HAND_FERTILITY,           12,  "imagebutton_hand_reap1",           "hand_of_fertiliy"));
		ALL_ENTRIES.add(e("imagebutton_hand_speed",           Relic.HAND_SPEED,               37,  "imagebutton_hand_speed",           "hand_of_speed"));
		ALL_ENTRIES.add(e("imagebutton_hand_smash",           Relic.HAND_OF_PULVERIZATION,    13,  "imagebutton_hand_smash",           "hand_of_barren"));
		ALL_ENTRIES.add(e("imagebutton_hand_swipe",           Relic.HAND_SWIPE,               14,  "imagebutton_hand_swipe",           "hand_of_spotless"));
		ALL_ENTRIES.add(new RelicDisplayEntry("imagebutton_hand_curve", Relic.HAND_ENGRAVE, RelicDisplayEntry.SpecialType.NONE, 15,
			"imagebutton_hand_curve", "hand_of_engrave",
			entity -> RelicUtils.getRelic(Relic.HAND_ENGRAVE, entity) > 0,
			null, new int[]{157, 36, -16777165, -1})); // label at (157/156, 36)
		ALL_ENTRIES.add(e("imagebutton_hand_firework",        Relic.HAND_FIREWORK,            16,  "imagebutton_hand_firework",        "hand_of_firework"));
		ALL_ENTRIES.add(blank());
		ALL_ENTRIES.add(blank());
		// ===== 第 3 行 =====
		ALL_ENTRIES.add(e("imagebutton_crimson_contarct_0",   Relic.TREATY,                   17,  "imagebutton_crimson_contarct_0",   "crimson_treaty"));
		ALL_ENTRIES.add(new RelicDisplayEntry("imagebutton_survivor_contarct", null, RelicDisplayEntry.SpecialType.SURVIVOR, 18,
			"imagebutton_survivor_contarct", "survivor_contract",
			PlayerStateUtils::hasSurvivorCont,
			null, new int[]{37, 60, -12829636, -1}));
		ALL_ENTRIES.add(e("imagebutton_chitinknife",          Relic.LEGEND_CHITIN,            36,  "imagebutton_chitinknife",          "chitin_knife"));
		ALL_ENTRIES.add(e("imagebutton_smelly_hemostatic",    Relic.HEMOST,                   38,  "imagebutton_smelly_hemostatic",    "smelly_hemostatic"));
		ALL_ENTRIES.add(e("imagebutton_unripe_yearning",      Relic.YEARNING,                 39,  "imagebutton_unripe_yearning",      "unripe_yearning"));
		ALL_ENTRIES.add(blank());
		ALL_ENTRIES.add(blank());
		ALL_ENTRIES.add(blank());
		ALL_ENTRIES.add(blank());
		ALL_ENTRIES.add(blank());
		// ===== 第 4 行 =====
		ALL_ENTRIES.add(e("imagebutton_beef_can",             Relic.FEATURED_CANNED_MEAT,     -1, "imagebutton_beef_can",             "meat_can"));
		ALL_ENTRIES.add(e("imagebutton_bowl_seagrass",        Relic.SEAWEED_SALAD,            -1, "imagebutton_bowl_seagrass",        "bowl_seagrass"));
		ALL_ENTRIES.add(e("imagebutton_orangestorm",          Relic.ORANGE_STORM,             -1, "imagebutton_orangestorm",          "golden_storm"));
		ALL_ENTRIES.add(e("imagebutton_coffee_candy",         Relic.COFFEE_PLAINS_COFFEE_CANDY, -1, "imagebutton_coffee_candy",      "coffee_candy"));
		ALL_ENTRIES.add(e("imagebutton_rainbow_candy",        Relic.UTIL_RAINBOW,             -1, "imagebutton_rainbow_candy",        "rainbow_candy"));
		ALL_ENTRIES.add(e("imagebutton_cherrycan",            Relic.PITTS_ASSORTED_FRUITS,    -1, "imagebutton_cherrycan",            "canned_cherry"));
		ALL_ENTRIES.add(new RelicDisplayEntry("imagebutton_boxcoffee", null, RelicDisplayEntry.SpecialType.AROMATIC, -1,
			"imagebutton_boxcoffee", "aromatic_coffee",
			PlayerStateUtils::hasAromatic, null, null));
		ALL_ENTRIES.add(e("imagebutton_musicboxsmall",        Relic.UTIL_MUSICBOX,            -1, "imagebutton_musicboxsmall",        "solo_music_box"));
		ALL_ENTRIES.add(e("imagebutton_flute",                Relic.WEIRD_FLUTE,              -1, "imagebutton_flute",                "odd_flute"));
		ALL_ENTRIES.add(e("imagebutton_originium_iris",       Relic.UTIL_IRIS,                -1, "imagebutton_originium_iris",       "redstone_iris_flower"));
		// ===== 第 5 行 =====
		ALL_ENTRIES.add(e("imagebutton_kettle",               Relic.HOT_WATER_KETTLE,         -1, "imagebutton_kettle",               "kettle"));
		ALL_ENTRIES.add(new RelicDisplayEntry("imagebutton_alley", Relic.UTIL_ALLEY, RelicDisplayEntry.SpecialType.NONE, -1,
			"imagebutton_alley", "allay_sculpture",
			entity -> RelicUtils.hasRelic(Relic.UTIL_ALLEY, entity),
			null, null));
		ALL_ENTRIES.add(new RelicDisplayEntry("imagebutton_batbed", Relic.VAMPIRES_BED, RelicDisplayEntry.SpecialType.NONE, -1,
			"imagebutton_batbed", "bat_bed",
			entity -> RelicUtils.hasRelic(Relic.VAMPIRES_BED, entity),
			null, null));
		ALL_ENTRIES.add(new RelicDisplayEntry("imagebutton_score", Relic.UTIL_SCORE, RelicDisplayEntry.SpecialType.NONE, -1,
			"imagebutton_score", "score",
			entity -> RelicUtils.hasRelic(Relic.UTIL_SCORE, entity),
			null, null));
		ALL_ENTRIES.add(new RelicDisplayEntry("imagebutton_rescission", Relic.UTIL_RESCISSION, RelicDisplayEntry.SpecialType.NONE, -1,
			"imagebutton_rescission", "rescission",
			entity -> RelicUtils.hasRelic(Relic.UTIL_RESCISSION, entity),
			null, null));
		ALL_ENTRIES.add(new RelicDisplayEntry("imagebutton_omnikey", Relic.UTIL_OMNIKEY, RelicDisplayEntry.SpecialType.NONE, -1,
			"imagebutton_omnikey", "omni_key",
			entity -> RelicUtils.hasRelic(Relic.UTIL_OMNIKEY, entity),
			null, null));
		ALL_ENTRIES.add(new RelicDisplayEntry("imagebutton_stare", Relic.UTIL_STARE, RelicDisplayEntry.SpecialType.NONE, -1,
			"imagebutton_stare", "guardian_stare",
			entity -> RelicUtils.hasRelic(Relic.UTIL_STARE, entity),
			null, null));
		ALL_ENTRIES.add(new RelicDisplayEntry("imagebutton_longevity", Relic.PROOF_OF_LONGEVITY, RelicDisplayEntry.SpecialType.NONE, -1,
			"imagebutton_longevity", "proof_of_longevity",
			entity -> RelicUtils.hasRelic(Relic.PROOF_OF_LONGEVITY, entity),
			null, null));
		ALL_ENTRIES.add(blank());
		ALL_ENTRIES.add(blank());
		// ===== 第 6 行（翻页第 2 页开始） =====
		ALL_ENTRIES.add(e("imagebutton_voyageofsmall",        Relic.PURE_GOLD_EXPEDITION,     -1, "imagebutton_voyageofsmall",        "voyage_of_gold"));
		ALL_ENTRIES.add(new RelicDisplayEntry("imagebutton_piglin_diary", Relic.DURIN_OVERGROUND_ODYSSEY, RelicDisplayEntry.SpecialType.NONE, -1,
			"imagebutton_piglin_diary", "piglin_diary",
			entity -> RelicUtils.hasRelic(Relic.DURIN_OVERGROUND_ODYSSEY, entity),
			"durin_diary.png", null));
		ALL_ENTRIES.add(e("imagebutton_location_name",        Relic.UTIL_TOPONYM,             -1, "imagebutton_location_name",        "toponym_textology"));
		ALL_ENTRIES.add(e("imagebutton_cursed_emelight_0",    Relic.CURSED_EMELIGHT,          19, "imagebutton_cursed_emelight_0",    "relic_curse_emelight"));
		ALL_ENTRIES.add(e("imagebutton_cursed_glowbody_0",    Relic.CURSED_GLOWBODY,          20, "imagebutton_cursed_glowbody_0",    "relic_cursed_glowbody"));
		ALL_ENTRIES.add(e("imagebutton_cursed_research_0",    Relic.CURSED_RESEARCH,          21, "imagebutton_cursed_research_0",    "relic_cursed_research"));
		ALL_ENTRIES.add(new RelicDisplayEntry("imagebutton_cursed_heart", Relic.CURSED_HEART, RelicDisplayEntry.SpecialType.NONE, -1,
			"imagebutton_cursed_heart", "caerula_heart",
			entity -> RelicUtils.hasRelic(Relic.CURSED_HEART, entity),
			null, null));
	}

	// ===== 工厂辅助：普通 boolean 遗物 =====
	private static RelicDisplayEntry e(String guiKey, Relic relic, int buttonId, String atlasBase, String itemKey) {
		return new RelicDisplayEntry(guiKey, relic, RelicDisplayEntry.SpecialType.NONE, buttonId,
			atlasBase, itemKey,
			entity -> RelicUtils.hasRelic(relic, entity),
			null, null);
	}

	// ===== 工厂辅助：占位空格子 =====
	private static RelicDisplayEntry blank() {
		return new RelicDisplayEntry("", null, RelicDisplayEntry.SpecialType.NONE, -1,
			"", "", entity -> false, null, null);
	}

	// ===== 根据条目解析对应的 MC 物品（优先用 Relic 绑定表，无则按 itemKey 注册表反查） =====
	private Item resolveItem(RelicDisplayEntry entry) {
		Item byRelic = entry.relic() != null ? RelicItemBase.byRelic(entry.relic()) : null;
		if (byRelic != null) {
			return byRelic;
		}
		return BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, entry.itemKey()));
	}

	public RelicShowcaseScreen(RelicShowcaseMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 328;
		this.imageHeight = 216;
		this.totalPages = (ALL_ENTRIES.size() + PAGE_SIZE - 1) / PAGE_SIZE;
		if (this.totalPages < 1) this.totalPages = 1;
	}

	// ===== 根据条目索引计算网格坐标 =====
	private int slotX(int entryIndex) {
		int within = entryIndex - currentPage * PAGE_SIZE;
		int col = within % COLS_PER_PAGE;
		return SLOT_ORIGIN_X + col * SLOT_STEP;
	}
	private int slotY(int entryIndex) {
		int within = entryIndex - currentPage * PAGE_SIZE;
		int row = within / COLS_PER_PAGE;
		return SLOT_ORIGIN_Y + row * SLOT_STEP;
	}
	private boolean isEntryOnPage(int entryIndex) {
		return entryIndex >= currentPage * PAGE_SIZE && entryIndex < (currentPage + 1) * PAGE_SIZE;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);

		// Tooltip 渲染：遍历当前页条目
		for (int i = 0; i < ALL_ENTRIES.size(); i++) {
			if (!isEntryOnPage(i)) continue;
			RelicDisplayEntry entry = ALL_ENTRIES.get(i);
			if (entry.guiKey().isEmpty()) continue;
			if (!entry.visibleTest().test(entity)) continue;
			int sx = this.leftPos + slotX(i);
			int sy = this.topPos + slotY(i);
			if (mouseX >= sx && mouseX < sx + SLOT_SIZE && mouseY >= sy && mouseY < sy + SLOT_SIZE) {
				String title = Component.translatable("item.caerula_arbor." + entry.itemKey()).getString();
				String desc = Component.translatable("item.caerula_arbor." + entry.itemKey() + ".description_0").getString();
				guiGraphics.renderTooltip(font, Component.literal(title + ": " + desc), mouseX, mouseY);
			}
		}
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/relic_bg.png"), this.leftPos, this.topPos, 0, 0, 328, 216, 328, 216);
		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		// 左右方向键翻页
		if (key == 263 && currentPage > 0) {
			setPage(currentPage - 1);
			return true;
		}
		if (key == 262 && currentPage < totalPages - 1) {
			setPage(currentPage + 1);
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	public boolean mouseScrolled(double mx, double my, double hDelta, double vDelta) {
		// 水平/垂直滚轮皆可翻页：右/下 = 下一页
		double delta = Math.abs(hDelta) > Math.abs(vDelta) ? hDelta : vDelta;
		if (delta < 0 && currentPage < totalPages - 1) {
			setPage(currentPage + 1);
			return true;
		}
		if (delta > 0 && currentPage > 0) {
			setPage(currentPage - 1);
			return true;
		}
		return super.mouseScrolled(mx, my, hDelta, vDelta);
	}

	@Override
	public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
		if (button == 0) {
			int ix = (int) mx;
			if (!isDragging) {
				isDragging = true;
				dragStartX = ix;
				dragAccum = 0;
			} else {
				dragAccum += ix - lastMouseX;
			}
			lastMouseX = ix;
			if (dragAccum <= -DRAG_THRESHOLD && currentPage < totalPages - 1) {
				setPage(currentPage + 1);
				dragAccum = 0;
				isDragging = false;
				return true;
			}
			if (dragAccum >= DRAG_THRESHOLD && currentPage > 0) {
				setPage(currentPage - 1);
				dragAccum = 0;
				isDragging = false;
				return true;
			}
		}
		return super.mouseDragged(mx, my, button, dx, dy);
	}
	private int lastMouseX;

	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		if (button == 0) {
			isDragging = false;
			dragAccum = 0;
			lastMouseX = (int) mx;
		}
		return super.mouseClicked(mx, my, button);
	}

	@Override
	public boolean mouseReleased(double mx, double my, int button) {
		isDragging = false;
		dragAccum = 0;
		return super.mouseReleased(mx, my, button);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.relic_showcase.label_relic_showcase"), 4, -12, -1, false);
		// 页码
		String pageLabel = (currentPage + 1) + "/" + totalPages;
		guiGraphics.drawString(this.font, pageLabel, imageWidth - 30, -12, -1, false);
		// 数值标签：HAND_ENGRAVE / SURVIVOR
		for (int i = 0; i < ALL_ENTRIES.size(); i++) {
			if (!isEntryOnPage(i)) continue;
			RelicDisplayEntry entry = ALL_ENTRIES.get(i);
			if (entry.labelPos() == null) continue;
			if (!entry.visibleTest().test(entity)) continue;
			int[] lp = entry.labelPos();
			String text = switch (entry.special()) {
				case SURVIVOR -> EntityUtils.getPlayerSurvconta(entity);
				case NONE -> entry.relic() == Relic.HAND_ENGRAVE ? EntityUtils.getPlayerEnrave(entity) : "";
				default -> "";
			};
			if (!text.isEmpty()) {
				int pageCol = (i - currentPage * PAGE_SIZE) % COLS_PER_PAGE;
				int pageRow = (i - currentPage * PAGE_SIZE) / COLS_PER_PAGE;
				int lx = SLOT_ORIGIN_X + pageCol * SLOT_STEP + 1;
				int ly = SLOT_ORIGIN_Y + pageRow * SLOT_STEP + 20;
				guiGraphics.drawString(this.font, text, lx - 1, ly - 1, lp[2], false);
				guiGraphics.drawString(this.font, text, lx, ly - 1, lp[3], false);
			}
		}
	}

	private void setPage(int page) {
		this.currentPage = Math.max(0, Math.min(totalPages - 1, page));
		rebuildButtons();
	}

	private void rebuildButtons() {
		// 移除旧按钮
		for (ImageButton btn : relicButtons) {
			this.removeWidget(btn);
		}
		relicButtons.clear();

		// 按当前页遍历条目创建按钮：用 renderItem 渲染真实绑定物品，不再依赖自定义 atlas 纹理
		for (int i = 0; i < ALL_ENTRIES.size(); i++) {
			if (!isEntryOnPage(i)) continue;
			RelicDisplayEntry entry = ALL_ENTRIES.get(i);
			if (entry.guiKey().isEmpty()) continue;
			final int entryIndex = i;
			int sx = this.leftPos + slotX(entryIndex);
			int sy = this.topPos + slotY(entryIndex);
			ItemStack stack = new ItemStack(resolveItem(entry));
			WidgetSprites dummySprites = new WidgetSprites(
				ResourceLocation.fromNamespaceAndPath("minecraft", "missingno"),
				ResourceLocation.fromNamespaceAndPath("minecraft", "missingno"));
			ImageButton btn = new ImageButton(sx, sy, SLOT_SIZE, SLOT_SIZE, dummySprites, e -> {
				if (entry.buttonId() >= 0 && entry.visibleTest().test(entity)) {
					PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(entry.buttonId(), x, y, z));
					RelicShowcaseButtonMessage.handleButtonAction(entity, entry.buttonId(), x, y, z);
				}
			}) {
				@Override
				public void renderWidget(GuiGraphics gg, int ggx, int ggy, float ticks) {
					boolean visible = entry.visibleTest().test(RelicShowcaseScreen.this.entity);
					this.visible = visible;
					if (!visible) return;
					int bx = this.getX();
					int by = this.getY();
					if (this.isHovered()) {
						gg.fill(bx, by, bx + SLOT_SIZE, by + SLOT_SIZE, 0x60FFFFFF);
					}
					gg.renderItem(stack, bx, by);
					gg.renderItemDecorations(RelicShowcaseScreen.this.font, stack, bx, by);
				}
			};
			guistate.put("button:" + entry.guiKey(), btn);
			this.addRenderableWidget(btn);
			relicButtons.add(btn);
		}

		// 翻页按钮状态
		if (button_prev_page != null) button_prev_page.active = currentPage > 0;
		if (button_next_page != null) button_next_page.active = currentPage < totalPages - 1;
	}

	@Override
	public void init() {
		super.init();

		// 返回按钮
		button_return = new PlainTextButton(this.leftPos + 292, this.topPos + 204, 24, 20,
			Component.translatable("gui.caerula_arbor.relic_showcase.button_return"), e -> {
			PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(0, x, y, z));
			RelicShowcaseButtonMessage.handleButtonAction(entity, 0, x, y, z);
		}, this.font);
		guistate.put("button:button_return", button_return);
		this.addRenderableWidget(button_return);

		// 上一页
		button_prev_page = new PlainTextButton(this.leftPos + 4, this.topPos + 204, 16, 16,
			Component.literal("◀"), e -> setPage(currentPage - 1), this.font);
		this.addRenderableWidget(button_prev_page);

		// 下一页
		button_next_page = new PlainTextButton(this.leftPos + 24, this.topPos + 204, 16, 16,
			Component.literal("▶"), e -> setPage(currentPage + 1), this.font);
		this.addRenderableWidget(button_next_page);

		rebuildButtons();
	}
}