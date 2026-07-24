package com.susen36.caerulaarbor.init.animfactory;

import com.susen36.caerulaarbor.item.SyncedAnimationItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class ArmorAnimationFactory {
	private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{
		EquipmentSlot.HEAD,
		EquipmentSlot.CHEST,
		EquipmentSlot.LEGS,
		EquipmentSlot.FEET
	};

	@SubscribeEvent
	public static void animatedArmors(PlayerTickEvent.Post event) {
		boolean clientSide = event.getEntity().level().isClientSide();
		for (EquipmentSlot armorSlot : ARMOR_SLOTS) {
			syncQueuedAnimation(event.getEntity().getItemBySlot(armorSlot), clientSide);
		}
	}

	private static void syncQueuedAnimation(ItemStack stack, boolean clientSide) {
		if (clientSide && stack.getItem() instanceof SyncedAnimationItem syncable) {
			String animation = syncable.consumeQueuedAnimation(stack);
			if (!animation.isEmpty()) {
				syncable.setAnimationProcedure(animation);
			}
		}
	}
}
