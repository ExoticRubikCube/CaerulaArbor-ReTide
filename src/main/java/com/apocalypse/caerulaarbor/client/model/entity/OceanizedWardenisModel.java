package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.OceanizedWardenisEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedWardenisModel extends GeoModel<OceanizedWardenisEntity> {
	@Override
	public ResourceLocation getAnimationResource(OceanizedWardenisEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/oceanized_wardenis.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedWardenisEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/oceanized_wardenis.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedWardenisEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(OceanizedWardenisEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
