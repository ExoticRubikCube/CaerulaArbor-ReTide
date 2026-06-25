package com.apocalypse.caerulaarbor.block.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.block.display.CentrifugerDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CentrifugerDisplayModel extends GeoModel<CentrifugerDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(CentrifugerDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/centrifuger.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CentrifugerDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/centrifuger.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CentrifugerDisplayItem entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/centrifuger.png");
	}
}
