package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.ScreamChestFishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ScreamChestFishModel extends GeoModel<ScreamChestFishEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/scream_chest_fish.png");

	@Override
	public ResourceLocation getAnimationResource(ScreamChestFishEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/scream_chest_fish.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ScreamChestFishEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/scream_chest_fish.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ScreamChestFishEntity entity) {
		return TEXTURE;
	}

}