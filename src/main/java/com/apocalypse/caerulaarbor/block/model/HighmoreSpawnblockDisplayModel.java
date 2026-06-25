package com.apocalypse.caerulaarbor.block.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import com.apocalypse.caerulaarbor.block.display.HighmoreSpawnblockDisplayItem;

public class HighmoreSpawnblockDisplayModel extends GeoModel<HighmoreSpawnblockDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(HighmoreSpawnblockDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/highmore_spawnblock.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(HighmoreSpawnblockDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/highmore_spawnblock.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(HighmoreSpawnblockDisplayItem entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/highmore_spawnblock.png");
	}
}
