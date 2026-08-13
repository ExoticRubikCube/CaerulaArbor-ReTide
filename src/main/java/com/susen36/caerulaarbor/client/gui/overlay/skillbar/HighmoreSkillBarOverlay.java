package com.susen36.caerulaarbor.client.gui.overlay.skillbar;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.HighmoreEntity;
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
public class HighmoreSkillBarOverlay {
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

        if (!world.getEntitiesOfClass(HighmoreEntity.class, AABB.ofSize(new Vec3(fx, fy, fz), 64, 64, 64), e1 -> true).isEmpty()) {
            HighmoreEntity ent;
            double ind = 0;
            ent = world.getEntitiesOfClass(HighmoreEntity.class, AABB.ofSize(new Vec3(fx, fy, fz), 64, 64, 64), e -> true)
                    .stream().min(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(fx, fy, fz))).orElse(null);
            if (!(ent == null)) {
                ind = Math.round((float) (ent instanceof HighmoreEntity datEntI ? datEntI.getEntityData().get(HighmoreEntity.DATA_SKILLP_2) : 0) / 8);
            }
            if (ind > 85) {
                ind = 85;
            } else if (ind < 0) {
                ind = 0;
            }
            event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/screen/highmore_skill_bar.png"), 8, h / 2 + -41, Mth.clamp((int) ind * 4, 0, 340), 0, 4, 87, 344, 87);

		}
	}
}