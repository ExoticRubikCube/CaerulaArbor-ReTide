package com.apocalypse.caerulaarbor.client.overlay.skillbar;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.BishopFishEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class BishopSkillBarOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
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
        if (!world.getEntitiesOfClass(BishopFishEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e1 -> true).isEmpty()) {

            Entity ent;
            double ind = 0;
            ent = world.getEntitiesOfClass(BishopFishEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream().min(new Object() {
                Comparator<Entity> compareDistOf(double x, double y, double z) {
                    return Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z));
                }
            }.compareDistOf(x, y, z)).orElse(null);
            if (!(ent == null)) {
                ind = Math.round((float) (ent instanceof BishopFishEntity datEntI ? datEntI.getEntityData().get(BishopFishEntity.DATA_ENDP) : 0) / 24);
            }
            if (ind > 100) {
                ind = 100;
            } else if (ind < 0) {
                ind = 0;
            }
            event.getGuiGraphics().blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/bishop_skill_bar.png"), 2, h / 2 + -48, Mth.clamp((int) ind * 4, 0, 400), 0, 4, 102, 404, 102);

		}
	}
}
