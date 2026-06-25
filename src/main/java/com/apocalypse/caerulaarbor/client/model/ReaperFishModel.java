package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import com.apocalypse.caerulaarbor.entity.ReaperFishEntity;

public class ReaperFishModel extends GeoModel<ReaperFishEntity> {
	@Override
	public ResourceLocation getAnimationResource(ReaperFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/reaperfish.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ReaperFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/reaperfish.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ReaperFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
