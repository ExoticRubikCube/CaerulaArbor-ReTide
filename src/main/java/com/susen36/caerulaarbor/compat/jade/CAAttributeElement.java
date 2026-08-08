package com.susen36.caerulaarbor.compat.jade;

import com.mojang.blaze3d.systems.RenderSystem;
import com.susen36.caerulaarbor.CaerulaArbor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.Element;
import snownee.jade.overlay.DisplayHelper;
import snownee.jade.overlay.OverlayRenderer;

public class CAAttributeElement extends Element {
    private final String defense;
    private final String resistance;
    private final boolean showDefense;
    private final boolean showResistance;

    private static final ResourceLocation DEFENSE_ICON = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/defense.png");
    private static final ResourceLocation MAGIC_ICON = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/magic_resistance.png");

    public CAAttributeElement(double defense, double resistance) {
        defense = Math.ceil(defense);
        resistance = Math.ceil(resistance);
        if (defense > 2147483647) this.defense = "Too Large";
        else this.defense = DisplayHelper.dfCommas.format(defense);
        if (resistance > 2147483647) this.resistance = "Too Large";
        else this.resistance = DisplayHelper.dfCommas.format(resistance) + "%";
        this.showDefense = defense > 0;
        this.showResistance = resistance > 0;
    }

    public Vec2 getSize() {
        Font font = Minecraft.getInstance().font;
        return new Vec2((float) getFirstPartLen() + (float) font.width(this.resistance) + 17, 11.0F);
    }

    public int getFirstPartLen() {
        if (!showDefense) return 0;
        return Minecraft.getInstance().font.width(this.defense) + 17;
    }

    public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, OverlayRenderer.alpha);
        RenderSystem.setShaderTexture(0, DEFENSE_ICON);
        RenderSystem.setShaderTexture(0, MAGIC_ICON);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if (showDefense) guiGraphics.blit(DEFENSE_ICON, (int) x, (int) y, 0, 0, 9, 9, 9, 9);
        if (showResistance) guiGraphics.blit(MAGIC_ICON, (int) x + getFirstPartLen(), (int) y, 0, 0, 9, 9, 9, 9);
        if (showDefense) DisplayHelper.INSTANCE.drawText(guiGraphics, this.defense, x + 13F, y, IThemeHelper.get().getNormalColor());
        if (showResistance) DisplayHelper.INSTANCE.drawText(guiGraphics, this.resistance, x + getFirstPartLen() + 13F, y, IThemeHelper.get().getNormalColor());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}