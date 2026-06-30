package com.apocalypse.caerulaarbor.client.overlay.skillbar;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.IzumikEntity;
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
public class IzumikSkillbarOverlay {
	@SubscribeEvent(priority = EventPriority.HIGHEST)
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
        if (!world.getEntitiesOfClass(IzumikEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e1 -> true).isEmpty()) {

            Entity ent;
            double ind = 0;
            double phase;
            ent = world.getEntitiesOfClass(IzumikEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream().min(new Object() {
                Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                    return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                }
            }.compareDistOf(x, y, z)).orElse(null);
            if (!(ent == null)) {
                phase = ent instanceof IzumikEntity _datEntI ? _datEntI.getEntityData().get(IzumikEntity.DATA_phase) : 0;
                ind = ent instanceof IzumikEntity _datEntI ? _datEntI.getEntityData().get(IzumikEntity.DATA_skillp) : 0;
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
            event.getGuiGraphics().blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/izumik_skillbar.png"), 13, h / 2 + -48, Mth.clamp((int) ind * 4, 0, 400), 0, 4, 100, 404, 100);

		}
	}
}
