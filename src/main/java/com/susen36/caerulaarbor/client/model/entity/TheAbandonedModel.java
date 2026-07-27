package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.TheAbandonedEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TheAbandonedModel extends GeoModel<TheAbandonedEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/theabandoned_texture.png");

	@Override
	public ResourceLocation getAnimationResource(TheAbandonedEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/the_abandoned.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TheAbandonedEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/the_abandoned.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TheAbandonedEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(TheAbandonedEntity animatable, long instanceId, AnimationState<TheAbandonedEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("realhead");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}