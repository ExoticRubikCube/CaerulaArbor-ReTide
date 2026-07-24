package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.CrackerAbyssalEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CrackerAbyssalModel extends GeoModel<CrackerAbyssalEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/reefbreaker.png");

	@Override
	public ResourceLocation getAnimationResource(CrackerAbyssalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/nethersea_reefbreaker.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CrackerAbyssalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/nethersea_reefbreaker.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CrackerAbyssalEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(CrackerAbyssalEntity animatable, long instanceId, AnimationState<CrackerAbyssalEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
