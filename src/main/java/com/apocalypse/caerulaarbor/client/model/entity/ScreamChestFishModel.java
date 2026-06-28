package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.ScreamChestFishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ScreamChestFishModel extends GeoModel<ScreamChestFishEntity> {
	@Override
	public ResourceLocation getAnimationResource(ScreamChestFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/scream_chest_fish.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ScreamChestFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/scream_chest_fish.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ScreamChestFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
