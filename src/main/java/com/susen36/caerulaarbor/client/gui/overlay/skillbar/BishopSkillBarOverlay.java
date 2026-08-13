package com.susen36.caerulaarbor.client.gui.overlay.skillbar;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.BishopFishEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.Comparator;

@EventBusSubscriber({Dist.CLIENT})
public class BishopSkillBarOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		int h = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		Level world = null;
		double x = 0;
		double y = 0;
		double z = 0;
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
			world = entity.level();
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
		}
        double fx = x;
        double fy = y;
        double fz = z;
        if (!world.getEntitiesOfClass(BishopFishEntity.class, AABB.ofSize(new Vec3(fx, fy, fz), 64, 64, 64), e1 -> true).isEmpty()) {
			BishopFishEntity ent;
            double ind = 0;
            ent = world.getEntitiesOfClass(BishopFishEntity.class, AABB.ofSize(new Vec3(fx, fy, fz), 64, 64, 64), e -> true)
                    .stream().min(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(fx, fy, fz))).orElse(null);
            if (!(ent == null)) {
                ind = Math.round((float) ent.getEntityData().get(BishopFishEntity.DATA_ENDP) / 24);
            }
            if (ind > 100) {
                ind = 100;
            } else if (ind < 0) {
                ind = 0;
            }
            event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/screen/bishop_skill_bar.png"), 2, h / 2 + -48, Mth.clamp((int) ind * 4, 0, 400), 0, 4, 102, 404, 102);
		}
	}
}