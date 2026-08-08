package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.tidelinked.TidelinkedImmortalEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TidelinkedImmortalModel extends GeoModel<TidelinkedImmortalEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/tidelinked_immortal.png");

	@Override
	public ResourceLocation getAnimationResource(TidelinkedImmortalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/tidelinked_immortal.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TidelinkedImmortalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/tidelinked_immortal.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TidelinkedImmortalEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(TidelinkedImmortalEntity animatable, long instanceId, AnimationState<TidelinkedImmortalEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}