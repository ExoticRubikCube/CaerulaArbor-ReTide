/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.client.model.entity.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class CAModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ModelBulletProjectile.LAYER_LOCATION, ModelBulletProjectile::createBodyLayer);
		event.registerLayerDefinition(ModelAnchorFly.LAYER_LOCATION, ModelAnchorFly::createBodyLayer);
		event.registerLayerDefinition(ModelSealeatherChitinArmor.LAYER_LOCATION, ModelSealeatherChitinArmor::createBodyLayer);
		event.registerLayerDefinition(ModelHighmoreShoot.LAYER_LOCATION, ModelHighmoreShoot::createBodyLayer);
		event.registerLayerDefinition(ModelFleefishBullet.LAYER_LOCATION, ModelFleefishBullet::createBodyLayer);
		event.registerLayerDefinition(ModelFakerggShoot.LAYER_LOCATION, ModelFakerggShoot::createBodyLayer);
		event.registerLayerDefinition(ModelOceanArrow.LAYER_LOCATION, ModelOceanArrow::createBodyLayer);
	}
}
