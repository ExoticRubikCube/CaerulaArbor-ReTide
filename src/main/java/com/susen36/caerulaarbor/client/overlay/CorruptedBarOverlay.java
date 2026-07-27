package com.susen36.caerulaarbor.client.overlay;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.SkadiCorruptedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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
public class CorruptedBarOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
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
        boolean result1 = false;
        Entity corrupted1;
        corrupted1 = world.getEntitiesOfClass(SkadiCorruptedEntity.class, AABB.ofSize(new Vec3(x, y, z), 48, 48, 48), e1 -> true).stream().sorted(new Object() {
            Comparator<Entity> compareDistOf(double x, double y, double z) {
                return Comparator.comparingDouble(entity -> entity.distanceToSqr(x, y, z));
            }
        }.compareDistOf(x, y, z)).findFirst().orElse(null);
        if (corrupted1 != null) {
            result1 = (corrupted1 instanceof SkadiCorruptedEntity datEntI1 ? datEntI1.getEntityData().get(SkadiCorruptedEntity.DATA_PHASE) : 0) < 1.5;
        }
        if (result1) {

            double result2 = 0;
            Entity corrupted2;
            double convertP;
            double phase;
            double progress = 0;
            corrupted2 = world.getEntitiesOfClass(SkadiCorruptedEntity.class, AABB.ofSize(new Vec3(x, y, z), 48, 48, 48), e1 -> true).stream().sorted(new Object() {
                Comparator<Entity> compareDistOf(double x, double y, double z) {
                    return Comparator.comparingDouble(entity -> entity.distanceToSqr(x, y, z));
                }
            }.compareDistOf(x, y, z)).findFirst().orElse(null);
            if (corrupted2 != null) {
                convertP = corrupted2 instanceof SkadiCorruptedEntity datEntI1 ? datEntI1.getEntityData().get(SkadiCorruptedEntity.DATA_CONVERT_P) : 0;
                phase = corrupted2 instanceof SkadiCorruptedEntity datEntI1 ? datEntI1.getEntityData().get(SkadiCorruptedEntity.DATA_PHASE) : 0;
                if (phase < 0.5) {
                    progress = (900 - convertP) / (double) 900;
                } else if (phase < 1.5) {
                    progress = (1120 - convertP) / (double) 1120;
                }
                result2 = Math.max(0, Math.min(Math.round(50 * progress), 50));
            }
            event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/corrupted_bar_inner.png"), 28, h / 2 + -42, Mth.clamp((int) result2 * 2, 0, 100), 0, 2, 88, 102, 88);

            double result = 0;
            Entity corrupted;
            double P;
            corrupted = world.getEntitiesOfClass(SkadiCorruptedEntity.class, AABB.ofSize(new Vec3(x, y, z), 48, 48, 48), e -> true).stream().min(new Object() {
                Comparator<Entity> compareDistOf(double x, double y, double z) {
                    return Comparator.comparingDouble(entity -> entity.distanceToSqr(x, y, z));
                }
            }.compareDistOf(x, y, z)).orElse(null);
            if (corrupted != null) {
                P = corrupted instanceof SkadiCorruptedEntity datEntI ? datEntI.getEntityData().get(SkadiCorruptedEntity.DATA_PHASE) : 0;
                if (!(P < 0.5)) {
                    if (P < 1.5) {
                        result = 1;
                    }
                }
            }
            event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/corrupted_bar.png"), 25, h / 2 + -48, 0, Mth.clamp((int) result * 99, 0, 99), 8, 99, 8, 198);

		}
	}
}