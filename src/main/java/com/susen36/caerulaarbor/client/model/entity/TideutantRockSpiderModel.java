package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.TideutantRockSpiderEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TideutantRockSpiderModel extends GeoModel<TideutantRockSpiderEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/tideutant_rock_spider.png");

	@Override
	public ResourceLocation getAnimationResource(TideutantRockSpiderEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/tidutant_rock_spider.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TideutantRockSpiderEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/tidutant_rock_spider.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TideutantRockSpiderEntity entity) {
		return TEXTURE;
	}

}
