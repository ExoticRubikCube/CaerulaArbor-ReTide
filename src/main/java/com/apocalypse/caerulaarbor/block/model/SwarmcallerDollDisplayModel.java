package com.apocalypse.caerulaarbor.block.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import com.apocalypse.caerulaarbor.block.display.SwarmcallerDollDisplayItem;

public class SwarmcallerDollDisplayModel extends GeoModel<SwarmcallerDollDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(SwarmcallerDollDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/umbrella_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SwarmcallerDollDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/umbrella_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SwarmcallerDollDisplayItem entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/umbrella.png");
	}
}
