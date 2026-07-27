package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.LastKnightAndHorseEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class LastKnightAndHorseModel extends GeoModel<LastKnightAndHorseEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/knight_amd_horse.png");

	@Override
	public ResourceLocation getAnimationResource(LastKnightAndHorseEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/last_knight_horse.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(LastKnightAndHorseEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/last_knight_horse.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LastKnightAndHorseEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(LastKnightAndHorseEntity animatable, long instanceId, AnimationState<LastKnightAndHorseEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("Head2");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}