package com.apocalypse.caerulaarbor.block.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import com.apocalypse.caerulaarbor.block.display.ViviparousLilyDisplayItem;

public class ViviparousLilyDisplayModel extends GeoModel<ViviparousLilyDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(ViviparousLilyDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/viviparous_lily.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ViviparousLilyDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/viviparous_lily.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ViviparousLilyDisplayItem entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/vivi_lily.png");
	}
}
