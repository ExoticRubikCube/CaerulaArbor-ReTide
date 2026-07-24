package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import com.susen36.caerulaarbor.block.item.ViviparousLilyDisplayItem;

public class ViviparousLilyDisplayModel extends GeoModel<ViviparousLilyDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(ViviparousLilyDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/viviparous_lily.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ViviparousLilyDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/viviparous_lily.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ViviparousLilyDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/vivi_lily.png");
	}
}
