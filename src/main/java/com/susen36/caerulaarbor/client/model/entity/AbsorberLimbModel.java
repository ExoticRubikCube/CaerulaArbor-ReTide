package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.AbsorberLimbEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class AbsorberLimbModel extends GeoModel<AbsorberLimbEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/absorber_limb.png");

	@Override
	public ResourceLocation getAnimationResource(AbsorberLimbEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/absorber_limb.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AbsorberLimbEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/absorber_limb.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AbsorberLimbEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(AbsorberLimbEntity animatable, long instanceId, AnimationState<AbsorberLimbEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}