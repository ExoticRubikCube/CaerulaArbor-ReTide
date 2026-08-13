package com.susen36.caerulaarbor.compat.jade;

import com.mojang.blaze3d.systems.RenderSystem;
import com.susen36.caerulaarbor.CaerulaArbor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.Element;
import snownee.jade.overlay.DisplayHelper;
import snownee.jade.overlay.OverlayRenderer;

public class CABarrierElement extends Element {
    private final String barrier;

    private static final ResourceLocation BARRIER_ICON = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/screen/living_barrier.png");

    public CABarrierElement(double barrier) {
        barrier = Math.ceil(barrier);
        if (barrier > 2147483647) this.barrier = "Too Large";
        else this.barrier = DisplayHelper.dfCommas.format(barrier);
    }

    public Vec2 getSize() {
        int w = Minecraft.getInstance().font.width(this.barrier);
        return new Vec2(w + 13, 11.0F);
    }

    public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, OverlayRenderer.alpha);
        RenderSystem.setShaderTexture(0, BARRIER_ICON);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(BARRIER_ICON, (int) x, (int) y, 0, 0, 9, 9, 9, 9);
        DisplayHelper.INSTANCE.drawText(guiGraphics, this.barrier, x + 13F, y + 1, IThemeHelper.get().getNormalColor());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}