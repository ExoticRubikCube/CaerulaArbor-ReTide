package com.apocalypse.caerulaarbor.compat.jade;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.Identifiers;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.Element;
import snownee.jade.impl.config.PluginConfig;
import snownee.jade.overlay.DisplayHelper;
import snownee.jade.overlay.OverlayRenderer;

public class CASanityElement extends Element {
    private final String text;
    private final float process;
    private static final ResourceLocation SANITY_ICON = new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/sanity_icon.png");
    private static final ResourceLocation SANITY_BAR = new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/sanity_bar.png");

    public CASanityElement(double value, double max) {
        if (!PluginConfig.INSTANCE.get(Identifiers.MC_ENTITY_HEALTH_SHOW_FRACTIONS)) {
            value = Math.ceil(value);
            max = Math.ceil(max);
        }
        if (max <= 0) this.process = 1;
        else this.process = (float) (value / max);
        if (max < -1) this.text = "Infinity";
        else if (max > 2147483647) this.text = "Too Large";
        else this.text = String.format("%s / %s",
                DisplayHelper.dfCommas.format(value),
                DisplayHelper.dfCommas.format(max));
    }

    public Vec2 getSize() {
        Font font = Minecraft.getInstance().font;
        return new Vec2(17F + (float) font.width(this.text), 12F);
    }

    public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, OverlayRenderer.alpha);
        RenderSystem.setShaderTexture(0, SANITY_ICON);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(SANITY_ICON, (int) x, (int) y, 0, 0, 10, 10, 10, 10);
        guiGraphics.blit(SANITY_BAR, (int) x + 11, (int) y + 8, 0, 2, 51, 2, 51, 4);
        guiGraphics.blit(SANITY_BAR, (int) x + 12, (int) y + 8, 0, 0, (int) (50 * this.process), 2, 51, 4);
        DisplayHelper.INSTANCE.drawText(guiGraphics, this.text, x + 13F, y, IThemeHelper.get().getNormalColor());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
