package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.enderdragon.MoistDragonBreathEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MoistDragonBreathModel extends GeoModel<MoistDragonBreathEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/moist_dragon_ball.png");

	@Override
	public ResourceLocation getAnimationResource(MoistDragonBreathEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/moist_dragon_breath.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MoistDragonBreathEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/moist_dragon_breath.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MoistDragonBreathEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(MoistDragonBreathEntity animatable, long instanceId, AnimationState<MoistDragonBreathEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("Ball");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}