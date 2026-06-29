package com.apocalypse.caerulaarbor.client.screens;

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
            ent = world.getEntitiesOfClass(BishopFishEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream().sorted(new Object() {
                Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                    return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                }
            }.compareDistOf(x, y, z)).findFirst().orElse(null);
            if (!(ent == null)) {
                ind = Math.round((ent instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(BishopFishEntity.DATA_endp) : 0) / 24);
            }
            if (ind > 100) {
                ind = 100;
            } else if (ind < 0) {
                ind = 0;
            }
            event.getGuiGraphics().blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/bishop_skill_bar.png"), 2, h / 2 + -48, Mth.clamp((int) ind * 4, 0, 400), 0, 4, 102, 404, 102);

		}
	}
}
