package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.GladiiaEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class GladiiaModel extends GeoModel<GladiiaEntity> {
	@Override
	public ResourceLocation getAnimationResource(GladiiaEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/gladiia.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GladiiaEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/gladiia.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GladiiaEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/hunter_gladiia.png");
	}

	@Override
	public void setCustomAnimations(GladiiaEntity animatable, long instanceId, AnimationState<GladiiaEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}