package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.warden.OceanizedWardenEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedWardenModel extends GeoModel<OceanizedWardenEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanized_warden.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedWardenEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/oceanized_warden.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedWardenEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/oceanized_warden.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedWardenEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(OceanizedWardenEntity animatable, long instanceId, AnimationState<OceanizedWardenEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}