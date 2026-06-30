
package com.apocalypse.caerulaarbor.client.overlay;

import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.util.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class EntityTransporterDisplayerOverlay {
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
            entity.level();
        }
        boolean result = false;
        if (entity != null) {
            ItemStack item;
            item = ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
            if (item.getItem() == CAItems.PERSONNEL_TRANSPORTER.get()) {
                result = ItemUtils.isFilledwithPersonnel(item);
            }
        }
        if (result) {
            Entity result1;
            if (entity == null) {
                result1 = null;
            } else {
                String emptyNameHolder = "apocata";
                ItemStack transp;
                String name;
                transp = ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
                if (ItemUtils.isFilledwithPersonnel(transp)) {
                    name = transp.getOrCreateTag().getString("name");
                    if (name.isEmpty() || name.equals(emptyNameHolder)) {
                        result1 = entity;
                    } else {
                        ResourceLocation location = new ResourceLocation(name);
                        EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(location);
                        if (type == null) {
                            result1 = entity;
                        } else {
                            result1 = type.create(entity.level());
                        }
                    }
                } else {
                    result1 = entity;
                }
            }
            if (result1 instanceof LivingEntity livingEntity) {
				InventoryScreen.renderEntityInInventoryFollowsAngle(event.getGuiGraphics(), w - 134, h - 22, 30, 1.1f, 0, livingEntity);
			}
		}
	}
}
