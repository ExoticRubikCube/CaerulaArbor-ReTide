package com.apocalypse.caerulaarbor.init.animfactory;

import com.apocalypse.caerulaarbor.item.SyncedAnimationItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ItemAnimationFactory {
	@SubscribeEvent
	public static void animatedItems(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
            syncQueuedAnimation(event.player.getMainHandItem(), event.player.level().isClientSide());
			syncQueuedAnimation(event.player.getOffhandItem(), event.player.level().isClientSide());
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
