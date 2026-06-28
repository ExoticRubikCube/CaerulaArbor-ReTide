package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.OceanizedVindicatorEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedVindicatorModel extends GeoModel<OceanizedVindicatorEntity> {
	@Override
	public ResourceLocation getAnimationResource(OceanizedVindicatorEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/ocean_vindic.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedVindicatorEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/ocean_vindic.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedVindicatorEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(OceanizedVindicatorEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
