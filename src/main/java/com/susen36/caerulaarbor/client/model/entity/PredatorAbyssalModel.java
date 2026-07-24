package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.PredatorAbyssalEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PredatorAbyssalModel extends GeoModel<PredatorAbyssalEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/predator.png");

	@Override
	public ResourceLocation getAnimationResource(PredatorAbyssalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/predator.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(PredatorAbyssalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/predator.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PredatorAbyssalEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(PredatorAbyssalEntity animatable, long instanceId, AnimationState<PredatorAbyssalEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
