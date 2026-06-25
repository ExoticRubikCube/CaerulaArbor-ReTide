package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.TideutantRockSpiderEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TideutantRockSpiderModel extends GeoModel<TideutantRockSpiderEntity> {
	@Override
	public ResourceLocation getAnimationResource(TideutantRockSpiderEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/tidutant_rock_spider.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TideutantRockSpiderEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/tidutant_rock_spider.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TideutantRockSpiderEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
