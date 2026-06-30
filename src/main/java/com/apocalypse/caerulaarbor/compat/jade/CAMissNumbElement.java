package com.apocalypse.caerulaarbor.compat.jade;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.Element;
import snownee.jade.overlay.DisplayHelper;
import snownee.jade.overlay.OverlayRenderer;

public class CAMissNumbElement extends Element {
    private final String miss;
    private final int style;
    private final boolean showMiss;

    private static final ResourceLocation MISS_ICON = new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/miss.png");
    private static final ResourceLocation NUMB_ICON = new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/numb.png");

    public CAMissNumbElement(int miss, int numb) {
        this.miss = "" + miss;
        this.style = numb;
        this.showMiss = miss > 0;
    }

    public Vec2 getSize() {
        Font font = Minecraft.getInstance().font;
        return new Vec2((float) getFirstPartLen() + (float) getSecondPartLen() + 17, 11.0F);
    }

    public int getFirstPartLen() {
        if (!showMiss) return 0;
        return Minecraft.getInstance().font.width(this.miss) + 20;
    }

    public int getSecondPartLen() {
        if (style <= 0) return 0;
        if (style <= 3) return style * 5 + 5;
        else return 26;
    }

    public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, OverlayRenderer.alpha);
        RenderSystem.setShaderTexture(0, MISS_ICON);
        RenderSystem.setShaderTexture(0, NUMB_ICON);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if (showMiss) guiGraphics.blit(MISS_ICON, (int) x, (int) y + 1, 0, 0, 16, 6, 16, 6);
        if (style > 0) guiGraphics.blit(NUMB_ICON, (int) x + getFirstPartLen(), (int) y, 0, 9 * (style - 1), 25, 9, 25, 36);
        if (showMiss) DisplayHelper.INSTANCE.drawText(guiGraphics, this.miss, x + 18F, y, IThemeHelper.get().getNormalColor());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
