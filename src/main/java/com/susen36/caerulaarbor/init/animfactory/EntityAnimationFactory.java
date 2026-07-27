package com.susen36.caerulaarbor.init.animfactory;

import com.susen36.caerulaarbor.entity.base.SyncedAnimationEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;


@EventBusSubscriber
public class EntityAnimationFactory {

	@SubscribeEvent
	public static void onEntityTick(LivingTickEvent event) {
		// 建议仅在服务端处理同步逻辑，避免客户端重复发包或计算
		if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof SyncedAnimationEntity syncable) {
			syncable.syncClientAnimation();
		}
	}
}