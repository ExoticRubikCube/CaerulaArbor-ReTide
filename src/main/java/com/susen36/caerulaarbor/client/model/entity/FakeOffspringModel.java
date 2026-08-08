package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.FakeOffspringEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class FakeOffspringModel extends GeoModel<FakeOffspringEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/fakepffspr.png");

	@Override
	public ResourceLocation getAnimationResource(FakeOffspringEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/fakeegg.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FakeOffspringEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/fakeegg.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FakeOffspringEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(FakeOffspringEntity animatable, long instanceId, AnimationState<FakeOffspringEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("ball");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}