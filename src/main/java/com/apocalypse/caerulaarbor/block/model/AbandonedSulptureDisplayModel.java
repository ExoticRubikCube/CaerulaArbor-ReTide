package com.apocalypse.caerulaarbor.block.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import com.apocalypse.caerulaarbor.block.display.AbandonedSulptureDisplayItem;

public class AbandonedSulptureDisplayModel extends GeoModel<AbandonedSulptureDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(AbandonedSulptureDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/abandoned_sulpture.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AbandonedSulptureDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/abandoned_sulpture.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AbandonedSulptureDisplayItem entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/theabandoned_sculpture.png");
	}
}
