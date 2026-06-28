package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.block.item.TidewayCradleDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TidewayCradleDisplayModel extends GeoModel<TidewayCradleDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(TidewayCradleDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/tideway_cradle.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TidewayCradleDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/tideway_cradle.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TidewayCradleDisplayItem entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/tideway_cradle.png");
	}
}
