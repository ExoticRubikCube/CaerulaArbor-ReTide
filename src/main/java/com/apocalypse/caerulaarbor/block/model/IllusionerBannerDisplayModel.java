package com.apocalypse.caerulaarbor.block.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import com.apocalypse.caerulaarbor.block.display.IllusionerBannerDisplayItem;

public class IllusionerBannerDisplayModel extends GeoModel<IllusionerBannerDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(IllusionerBannerDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/oceanized_banner.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(IllusionerBannerDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/oceanized_banner.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(IllusionerBannerDisplayItem entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/illusioner_banner.png");
	}
}
