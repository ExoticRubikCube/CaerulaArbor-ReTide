package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.MoistDragonBreathEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MoistDragonBreathModel extends GeoModel<MoistDragonBreathEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/moist_dragon_ball.png");

	@Override
	public ResourceLocation getAnimationResource(MoistDragonBreathEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/moist_dragon_breath.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MoistDragonBreathEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/moist_dragon_breath.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MoistDragonBreathEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(MoistDragonBreathEntity animatable, long instanceId, AnimationState<MoistDragonBreathEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("Ball");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
