package com.susen36.caerulaarbor.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.susen36.babel.collectible.Collectibles;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.init.CACollectible;
import com.susen36.caerulaarbor.menu.RelicShowcaseMenu;
import com.susen36.caerulaarbor.network.send.RelicShowcaseButtonMessage;
import com.susen36.caerulaarbor.util.RecordColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
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
import java.util.Map;
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

	// ===== 显示条目（纯遍历 Collectibles 自动生成，不保留手工特殊条目） =====
	private record RelicDisplayEntry(
		String guiKey,
		Item relic,
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
		for (Map.Entry<String, Holder<Item>> collectible : Collectibles.Collectibles.entrySet()) {
			String id = collectible.getKey();
			Item relic = collectible.getValue().value();
			ALL_ENTRIES.add(new RelicDisplayEntry("imagebutton_" + id, relic, RelicDisplayEntry.SpecialType.NONE, -1,
				"imagebutton_" + id, id,
				entity -> entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(relic),
				null, null));
		}
	}

	// ===== 根据条目解析对应的 MC 物品：直接使用 entry.relic()，如果为 null 则返回 EMPTY =====
	private ItemStack resolveItem(RelicDisplayEntry entry) {
		if (entry.relic() == null) {
			return ItemStack.EMPTY;
		}
		return new ItemStack(entry.relic());
	}

	public RelicShowcaseScreen(RelicShowcaseMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 312;
		this.imageHeight = 192;
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

		int relicBgColor = RecordColor.fromName(ModCapabilities.getPlayerVariables(entity).current_theme).getSubBgColor();
		RenderSystem.setShaderColor(((relicBgColor >> 16) & 0xFF) / 255.0F, ((relicBgColor >> 8) & 0xFF) / 255.0F, (relicBgColor & 0xFF) / 255.0F, 1.0F);
		guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/screen/relic_bg__gray.png"), this.leftPos, this.topPos, 0, 0, 312, 192, 312, 192);
		RenderSystem.setShaderColor(1, 1, 1, 1);
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
		// 数值标签：HAND_ENGRAVE / SURVIVOR（遍历模式下无 labelPos 条目，不渲染）
		for (int i = 0; i < ALL_ENTRIES.size(); i++) {
			if (!isEntryOnPage(i)) continue;
			RelicDisplayEntry entry = ALL_ENTRIES.get(i);
			if (entry.labelPos() == null) continue;
			if (!entry.visibleTest().test(entity)) continue;
			int[] lp = entry.labelPos();
			String text = switch (entry.special()) {
				case SURVIVOR ->
						"" + Math.round(entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).getLayer(CACollectible.SURVIVOR_CONTRACT));
				case NONE -> entry.relic() == CACollectible.HAND_OF_ENGRAVE ? "" + entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).getLayer(CACollectible.HAND_OF_ENGRAVE) : "";
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
			ItemStack stack = resolveItem(entry);
			WidgetSprites dummySprites = new WidgetSprites(
				ResourceLocation.fromNamespaceAndPath("minecraft", "missingno"),
				ResourceLocation.fromNamespaceAndPath("minecraft", "missingno"));
			ImageButton btn = new ImageButton(sx, sy, SLOT_SIZE, SLOT_SIZE, dummySprites, e -> {
				if (entry.visibleTest().test(entity)) {
					PacketDistributor.sendToServer(new RelicShowcaseButtonMessage(entry.relic(), x, y, z));
					RelicShowcaseButtonMessage.handleButtonAction(entity, entry.relic(), x, y, z);
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

		// 元素相对偏移保持不变：Y=198，X=4/24/292
		final int buttonY = 198;

		// 返回按钮
		button_return = new PlainTextButton(this.leftPos + 292, this.topPos + buttonY, 24, 16,
			Component.translatable("gui.caerula_arbor.relic_showcase.button_return"), e -> {
			PacketDistributor.sendToServer(new RelicShowcaseButtonMessage((Item) null, x, y, z));
			RelicShowcaseButtonMessage.handleButtonAction(entity, (Item) null, x, y, z);
		}, this.font);
		guistate.put("button:button_return", button_return);
		this.addRenderableWidget(button_return);

		// 上一页和下一页
		button_prev_page = new PlainTextButton(this.leftPos + 4, this.topPos + buttonY, 16, 16,
			Component.literal("◀"), e -> setPage(currentPage - 1), this.font);
		this.addRenderableWidget(button_prev_page);
		button_next_page = new PlainTextButton(this.leftPos + 24, this.topPos + buttonY, 16, 16,
			Component.literal("▶"), e -> setPage(currentPage + 1), this.font);
		this.addRenderableWidget(button_next_page);

		rebuildButtons();
	}
}