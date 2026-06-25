package com.apocalypse.caerulaarbor.block.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.block.display.HugeLilyDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HugeLilyDisplayModel extends GeoModel<HugeLilyDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(HugeLilyDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/viviparous_lily_huge.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(HugeLilyDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/viviparous_lily_huge.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(HugeLilyDisplayItem entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/vivi_lily.png");
	}
}
