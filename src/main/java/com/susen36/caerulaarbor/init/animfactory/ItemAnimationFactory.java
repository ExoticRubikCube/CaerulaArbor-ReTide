package com.susen36.caerulaarbor.init.animfactory;

import com.susen36.caerulaarbor.item.SyncedAnimationItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class ItemAnimationFactory {
	@SubscribeEvent
	public static void animatedItems(PlayerTickEvent.Pre event) {
		syncQueuedAnimation(event.getEntity().getMainHandItem(), event.getEntity().level().isClientSide());
		syncQueuedAnimation(event.getEntity().getOffhandItem(), event.getEntity().level().isClientSide());
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
