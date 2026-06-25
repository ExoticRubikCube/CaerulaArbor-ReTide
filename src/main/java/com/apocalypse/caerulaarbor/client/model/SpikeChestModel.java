package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import com.apocalypse.caerulaarbor.entity.SpikeChestEntity;

public class SpikeChestModel extends GeoModel<SpikeChestEntity> {
	@Override
	public ResourceLocation getAnimationResource(SpikeChestEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/spike_chest.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SpikeChestEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/spike_chest.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SpikeChestEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
