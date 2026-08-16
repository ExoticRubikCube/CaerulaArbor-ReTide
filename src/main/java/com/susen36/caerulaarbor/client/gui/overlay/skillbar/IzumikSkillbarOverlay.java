package com.susen36.caerulaarbor.client.gui.overlay.skillbar;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.IzumikEntity;
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
public class IzumikSkillbarOverlay {
	@SubscribeEvent(priority = EventPriority.HIGHEST)
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
        if (!world.getEntitiesOfClass(IzumikEntity.class, AABB.ofSize(new Vec3(fx, fy, fz), 64, 64, 64), e1 -> true).isEmpty()) {
            IzumikEntity ent;
            double ind = 0;
            double phase;
            ent = world.getEntitiesOfClass(IzumikEntity.class, AABB.ofSize(new Vec3(fx, fy, fz), 64, 64, 64), e -> true)
                    .stream().min(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(fx, fy, fz))).orElse(null);
            if (!(ent == null)) {
                phase = ent instanceof IzumikEntity datEntI ? datEntI.getEntityData().get(IzumikEntity.DATA_PHASE) : 0;
                ind = ent instanceof IzumikEntity datEntI ? datEntI.getEntityData().get(IzumikEntity.DATA_SKILLP) : 0;
                if (phase == 0) {
                    ind = Math.round(ind * 20);
                } else if (phase == 1) {
                    ind = Math.round(ind / 6);
                } else {
                    ind = Math.round(ind / 4);
                }
            }
            if (ind > 100) {
                ind = 100;
            } else if (ind < 0) {
                ind = 0;
            }
            event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/bossbar/skillbar/izumik_skillbar.png"), 13, h / 2 + -48, Mth.clamp((int) ind * 4, 0, 400), 0, 4, 100, 404, 100);
		}
	}
}