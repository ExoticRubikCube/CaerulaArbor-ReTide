package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.HighmoreEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class HighmoreModel extends GeoModel<HighmoreEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/highmore.png");

	@Override
	public ResourceLocation getAnimationResource(HighmoreEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/highmore.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(HighmoreEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/highmore.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(HighmoreEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(HighmoreEntity animatable, long instanceId, AnimationState<HighmoreEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("core");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}