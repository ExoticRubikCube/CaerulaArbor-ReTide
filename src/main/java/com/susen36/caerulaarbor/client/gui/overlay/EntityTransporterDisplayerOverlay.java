package com.susen36.caerulaarbor.client.gui.overlay;

import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.item.PersonnelTransporterItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber({Dist.CLIENT})
public class EntityTransporterDisplayerOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		int h = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		Player player = Minecraft.getInstance().player;
        boolean result = false;
        if (player != null) {
            ItemStack item;
            item = player.getMainHandItem().copy();
            if (item.getItem() == CAItems.PERSONNEL_TRANSPORTER.get()) {
                result = PersonnelTransporterItem.isFilledwithPersonnel(item);
            }
        }
        if (result) {
            Entity result1;
            String emptyNameHolder = "apocata";
            ItemStack transp;
            String name;
            transp = player.getMainHandItem().copy();
            if (PersonnelTransporterItem.isFilledwithPersonnel(transp)) {
                name = transp.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString("name");
                if (name.isEmpty() || name.equals(emptyNameHolder)) {
                    result1 = player;
                } else {
                    ResourceLocation location = ResourceLocation.parse(name);
                    EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(location);
                    result1 = type.create(player.level());
                }
            } else {
                result1 = player;
            }
            if (result1 instanceof LivingEntity livingEntity) {
				InventoryScreen.renderEntityInInventoryFollowsAngle(event.getGuiGraphics(), w - 164, h - 52, w - 104, h + 8, 30, 0f, 1.1f, 0f, livingEntity);
			}
		}
	}
}