package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import com.apocalypse.caerulaarbor.entity.BishopFishEntity;

public class BishopFishModel extends GeoModel<BishopFishEntity> {
	@Override
	public ResourceLocation getAnimationResource(BishopFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/bishop.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BishopFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/bishop.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BishopFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
