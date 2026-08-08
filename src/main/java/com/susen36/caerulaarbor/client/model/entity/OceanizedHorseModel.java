package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.OceanizedHorseEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedHorseModel extends GeoModel<OceanizedHorseEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanized_horse.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedHorseEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/oceanzied_horse.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedHorseEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/oceanzied_horse.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedHorseEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(OceanizedHorseEntity animatable, long instanceId, AnimationState<OceanizedHorseEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("neck");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}