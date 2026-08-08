package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.FlyFishEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class FlyFishModel extends GeoModel<FlyFishEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/flyfish.png");

	@Override
	public ResourceLocation getAnimationResource(FlyFishEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/flyfish.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FlyFishEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/flyfish.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FlyFishEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(FlyFishEntity animatable, long instanceId, AnimationState<FlyFishEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("body");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}