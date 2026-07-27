package com.susen36.caerulaarbor.client.overlay;

import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.util.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class EntityTransporterDisplayerOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		Player entity = Minecraft.getInstance().player;
        boolean result = false;
        if (entity != null) {
            ItemStack item;
            item = ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
            if (item.getItem() == CAItems.PERSONNEL_TRANSPORTER.get()) {
                result = ItemUtils.isFilledwithPersonnel(item);
            }
        }
        if (result) {
            Entity result1;
            String emptyNameHolder = "apocata";
            ItemStack transp;
            String name;
            transp = ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
            if (ItemUtils.isFilledwithPersonnel(transp)) {
                name = transp.getOrCreateTag().getString("name");
                if (name.isEmpty() || name.equals(emptyNameHolder)) {
                    result1 = entity;
                } else {
                    ResourceLocation location = ResourceLocation.parse(name);
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
            if (result1 instanceof LivingEntity livingEntity) {
				InventoryScreen.renderEntityInInventoryFollowsAngle(event.getGuiGraphics(), w - 134, h - 22, 30, 1.1f, 0, livingEntity);
			}
		}
	}
}