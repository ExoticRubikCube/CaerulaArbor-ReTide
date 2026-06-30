package com.apocalypse.caerulaarbor.compat.jade;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.ui.Element;
import snownee.jade.overlay.OverlayRenderer;

public class CAHotKettleElement extends Element {
    public final boolean noodled, watered, boiling;
    public final ItemStack item;

    public static final Vec2 SIZE = new Vec2(16, 16);
    public static final ResourceLocation KETTLE = new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/kettle_icon.png");
    public static final ResourceLocation RESULTS = new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/kettle_res.png");

    public CAHotKettleElement(boolean watered, boolean noodled, boolean boiling, ItemStack item) {
        this.noodled = noodled;
        this.watered = watered;
        this.boiling = boiling;
        this.item = item;
    }

    public Vec2 getSize() {
        return SIZE;
    }

    public int getIndex() {
        if (noodled) return 3;
        if (boiling && watered) return 2;
        if (watered) return 1;
        return 0;
    }

    public int getRes() {
        int index = getIndex();
        if (index == 0) {
            if (item.is(CAItems.CANNED_WATER.get()) || item.is(CAItems.A_CUP_OF_WATER.get())) return 0;
        } else if (index == 1) {
            if (item.is(CAItems.EMPTY_CAN.get())) return 2;
            if (item.is(CAItems.OCEANGLASS_CUP.get())) return 7;
            if (item.is(CAItems.CANNED_LAVA.get())) return 5;
        } else if (index == 2) {
            if (item.is(CAItems.CANNED_LAVA.get())) return 5;
            if (item.is(CAItems.INSTANT_NOODLE.get())) return 1;
            if (item.is(CAItems.REAL_EGG.get())) return 6;
            if (item.is(CAItems.EMPTY_CAN.get())) return 3;
        } else if (index == 3) {
            if (item.is(CAItems.EMPTY_CAN.get())) return 4;
        }
        return -1;
    }

    public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, OverlayRenderer.alpha);
        RenderSystem.setShaderTexture(0, KETTLE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(KETTLE, (int) x + 2, (int) y, getIndex() * 12, 0, 12, 14, 48, 14);
        int res = getRes();
        if (res != -1) {
            guiGraphics.blit(RESULTS, (int) x + 16, (int) y, 128, 0, 16, 16, 144, 16);
            guiGraphics.blit(RESULTS, (int) x + 32, (int) y, res * 16, 0, 16, 16, 144, 16);
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
