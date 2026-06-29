package com.apocalypse.caerulaarbor.client.screens;

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

	public static final ResourceLocation BAR = new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/isharmla_bar.png");

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
        corrupted = world.getEntitiesOfClass(IsharmlaEntity.class, AABB.ofSize(new Vec3(x, y, z), 48, 48, 48), e1 -> true).stream().sorted(new Object() {
            Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
            }
        }.compareDistOf(x, y, z)).findFirst().orElse(null);
        if (corrupted != null) {
            result = !(corrupted instanceof IsharmlaEntity _datEntL2 && _datEntL2.getEntityData().get(IsharmlaEntity.DATA_IS_MONSTER));
        }
        if (result) {

            Entity ent;
            double ind = 0;
            ent = world.getEntitiesOfClass(IsharmlaEntity.class, AABB.ofSize(new Vec3(x, y, z), 48, 48, 48), e -> true)
            .stream()
            .sorted(EntityUtils.compareDistOf(x, y, z))
            .findFirst().orElse(null);
            if (!(ent == null)) {
                ind = 2400 - (ent instanceof IsharmlaEntity _datEntI ? _datEntI.getEntityData().get(IsharmlaEntity.DATA_SKILLP_1) : 0);
            }
            double process = ind / 2400;
			int len = (int) (process * 100);
			event.getGuiGraphics().blit(BAR, 9, h / 2 -54, 0, 0, 8, 109, 10, 109);

			event.getGuiGraphics().blit(BAR, 12, h / 2 -50 + (100-len), 8, 100 - len, 2, len, 10, 109);
		}
	}
}
