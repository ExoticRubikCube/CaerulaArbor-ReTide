package com.apocalypse.caerulaarbor.block.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import com.apocalypse.caerulaarbor.block.display.ChestmegaSpawnerDisplayItem;

public class ChestmegaSpawnerDisplayModel extends GeoModel<ChestmegaSpawnerDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(ChestmegaSpawnerDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/chestmega_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ChestmegaSpawnerDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/chestmega_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ChestmegaSpawnerDisplayItem entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/megachest.png");
	}
}
