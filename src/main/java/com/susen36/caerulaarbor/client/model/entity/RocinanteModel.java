package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.RocinanteEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class RocinanteModel extends GeoModel<RocinanteEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/mere_horse.png");

	@Override
	public ResourceLocation getAnimationResource(RocinanteEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/rocinante.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(RocinanteEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/rocinante.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(RocinanteEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(RocinanteEntity animatable, long instanceId, AnimationState<RocinanteEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("Neck");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
