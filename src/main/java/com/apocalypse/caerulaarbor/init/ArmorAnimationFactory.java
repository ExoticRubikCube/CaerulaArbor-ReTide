package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.item.SyncedAnimationItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ArmorAnimationFactory {
	private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{
		EquipmentSlot.HEAD,
		EquipmentSlot.CHEST,
		EquipmentSlot.LEGS,
		EquipmentSlot.FEET
	};

	@SubscribeEvent
	public static void animatedArmors(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			boolean clientSide = event.player.level().isClientSide();
			for (EquipmentSlot armorSlot : ARMOR_SLOTS) {
				syncQueuedAnimation(event.player.getItemBySlot(armorSlot), clientSide);
			}
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
