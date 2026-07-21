package com.apocalypse.caerulaarbor.client.overlay.skillbar;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.IsharmlaEntity;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
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
public class IsharmlasSkillBarOverlay {

	public static final ResourceLocation BAR = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/isharmla_bar.png");

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
        boolean result = false;
        Entity corrupted;
        corrupted = world.getEntitiesOfClass(IsharmlaEntity.class, AABB.ofSize(new Vec3(x, y, z), 48, 48, 48), e1 -> true).stream().min(new Object() {
            Comparator<Entity> compareDistOf(double x, double y, double z) {
                return Comparator.comparingDouble(entity -> entity.distanceToSqr(x, y, z));
            }
        }.compareDistOf(x, y, z)).orElse(null);
        if (corrupted != null) {
            result = !(corrupted instanceof IsharmlaEntity datEntL2 && datEntL2.getEntityData().get(IsharmlaEntity.DATA_IS_MONSTER));
        }
        if (result) {

            Entity ent;
            double ind = 0;
            ent = world.getEntitiesOfClass(IsharmlaEntity.class, AABB.ofSize(new Vec3(x, y, z), 48, 48, 48), e -> true)
                    .stream().min(EntityUtils.compareDistOf(x, y, z)).orElse(null);
            if (!(ent == null)) {
                ind = 2400 - (ent instanceof IsharmlaEntity datEntI ? datEntI.getEntityData().get(IsharmlaEntity.DATA_SKILLP_1) : 0);
            }
            double process = ind / 2400;
			int len = (int) (process * 100);
			event.getGuiGraphics().blit(BAR, 9, h / 2 -54, 0, 0, 8, 109, 10, 109);

			event.getGuiGraphics().blit(BAR, 12, h / 2 -50 + (100-len), 8, 100 - len, 2, len, 10, 109);
		}
	}
}
