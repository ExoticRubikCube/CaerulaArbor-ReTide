package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.warden.OceanizedWardenisEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedWardenisModel extends GeoModel<OceanizedWardenisEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanized_wardenis.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedWardenisEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/oceanized_wardenis.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedWardenisEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/oceanized_wardenis.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedWardenisEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(OceanizedWardenisEntity animatable, long instanceId, AnimationState<OceanizedWardenisEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}