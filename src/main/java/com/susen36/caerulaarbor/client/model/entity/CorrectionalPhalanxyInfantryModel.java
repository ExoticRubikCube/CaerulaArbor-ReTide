package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.CorrectionalPhalanxyInfantryEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CorrectionalPhalanxyInfantryModel extends GeoModel<CorrectionalPhalanxyInfantryEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/correctionalphalanx_infantry_shield.png");

	@Override
	public ResourceLocation getAnimationResource(CorrectionalPhalanxyInfantryEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/correctional_phalanx__infantry.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CorrectionalPhalanxyInfantryEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/correctional_phalanx__infantry.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CorrectionalPhalanxyInfantryEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(CorrectionalPhalanxyInfantryEntity animatable, long instanceId, AnimationState<CorrectionalPhalanxyInfantryEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}