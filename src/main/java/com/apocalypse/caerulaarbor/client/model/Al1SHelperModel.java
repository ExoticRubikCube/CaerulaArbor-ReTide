package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import com.apocalypse.caerulaarbor.entity.Al1SHelperEntity;

public class Al1SHelperModel extends GeoModel<Al1SHelperEntity> {
	@Override
	public ResourceLocation getAnimationResource(Al1SHelperEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/little_helper.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(Al1SHelperEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/little_helper.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(Al1SHelperEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
