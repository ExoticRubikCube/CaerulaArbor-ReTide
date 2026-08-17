package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.ApocataEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ApocataModel extends GeoModel<ApocataEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/apocata.png");

	@Override
	public ResourceLocation getAnimationResource(ApocataEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/apocata.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ApocataEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/apocata.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ApocataEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(ApocataEntity animatable, long instanceId, AnimationState<ApocataEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("realHead");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}