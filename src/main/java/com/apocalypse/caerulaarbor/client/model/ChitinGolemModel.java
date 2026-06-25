package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.constant.DataTickets;

import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;

import com.apocalypse.caerulaarbor.entity.ChitinGolemEntity;

public class ChitinGolemModel extends GeoModel<ChitinGolemEntity> {
	@Override
	public ResourceLocation getAnimationResource(ChitinGolemEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/chitgolem.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ChitinGolemEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/chitgolem.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ChitinGolemEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(ChitinGolemEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
