package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.OceanizeRabbitEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizeRabbitModel extends GeoModel<OceanizeRabbitEntity> {

	@Override
	public ResourceLocation getAnimationResource(OceanizeRabbitEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/oceanized_rabbit.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizeRabbitEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/oceanized_rabbit.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizeRabbitEntity entity) {
		int variant = entity.getEntityData().get(OceanizeRabbitEntity.DATA_VARIANT);
		if (variant == 5) {
			//TODO 需要移除雪狼变种
			//return BLOODY_TEXTURE;
		}
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanized_rabbit_" + variant + ".png");
	}

	@Override
	public void setCustomAnimations(OceanizeRabbitEntity animatable, long instanceId, AnimationState<OceanizeRabbitEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}