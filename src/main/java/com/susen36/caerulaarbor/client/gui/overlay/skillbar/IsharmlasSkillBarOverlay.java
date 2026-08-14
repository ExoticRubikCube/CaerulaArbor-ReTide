package com.susen36.caerulaarbor.client.gui.overlay.skillbar;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.isharmla.IsharmlaEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
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
public class IsharmlasSkillBarOverlay {

	public static final ResourceLocation BAR = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/isharmla_bar.png");

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
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

		IsharmlaEntity corrupted;
        corrupted = world.getEntitiesOfClass(IsharmlaEntity.class, AABB.ofSize(new Vec3(fx, fy, fz), 48, 48, 48), e1 -> true)
                .stream().min(Comparator.<Entity>comparingDouble(entcnd -> entcnd.distanceToSqr(fx, fy, fz))).orElse(null);
        if (corrupted != null && corrupted.isMonster()) {
            double ind = 0;
			IsharmlaEntity isharmla = world.getEntitiesOfClass(IsharmlaEntity.class, AABB.ofSize(new Vec3(fx, fy, fz), 48, 48, 48), e -> true)
                    .stream().min(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(fx, fy, fz))).orElse(null);
            if (!(isharmla == null)) {
                ind = 2400 - isharmla.getEntityData().get(IsharmlaEntity.DATA_SKILLP_1);
            }
            double process = ind / 2400;
			int len = (int) (process * 100);
			event.getGuiGraphics().blit(BAR, 9, h / 2 -54, 0, 0, 8, 109, 10, 109);
			event.getGuiGraphics().blit(BAR, 12, h / 2 -50 + (100-len), 8, 100 - len, 2, len, 10, 109);
		}
	}
}