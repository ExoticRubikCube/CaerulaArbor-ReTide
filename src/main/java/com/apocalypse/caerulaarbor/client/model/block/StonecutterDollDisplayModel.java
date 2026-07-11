package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import com.apocalypse.caerulaarbor.block.item.StonecutterDollDisplayItem;

public class StonecutterDollDisplayModel extends GeoModel<StonecutterDollDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(StonecutterDollDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/chiseler_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(StonecutterDollDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/chiseler_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(StonecutterDollDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/chieslerfish.png");
	}
}
