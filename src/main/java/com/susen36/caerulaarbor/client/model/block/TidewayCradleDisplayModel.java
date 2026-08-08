package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArbor;

import com.susen36.caerulaarbor.block.item.TidewayCradleDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TidewayCradleDisplayModel extends GeoModel<TidewayCradleDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(TidewayCradleDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/tideway_cradle.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TidewayCradleDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/tideway_cradle.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TidewayCradleDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/tideway_cradle.png");
	}
}