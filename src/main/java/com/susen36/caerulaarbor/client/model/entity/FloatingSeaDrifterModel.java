package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.FloatingSeaDrifterEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class FloatingSeaDrifterModel extends GeoModel<FloatingSeaDrifterEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/floating_sea_drifter.png");

	@Override
	public ResourceLocation getAnimationResource(FloatingSeaDrifterEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/floating_sea_drifter.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FloatingSeaDrifterEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/floating_sea_drifter.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FloatingSeaDrifterEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(FloatingSeaDrifterEntity animatable, long instanceId, AnimationState<FloatingSeaDrifterEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("body");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}