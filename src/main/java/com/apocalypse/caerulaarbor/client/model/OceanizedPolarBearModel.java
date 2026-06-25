package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.OceanizedPolarBearEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedPolarBearModel extends GeoModel<OceanizedPolarBearEntity> {
	@Override
	public ResourceLocation getAnimationResource(OceanizedPolarBearEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/oceanized_polarbear.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedPolarBearEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/oceanized_polarbear.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedPolarBearEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(OceanizedPolarBearEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
