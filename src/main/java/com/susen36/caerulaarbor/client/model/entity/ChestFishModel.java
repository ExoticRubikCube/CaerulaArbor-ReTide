package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.ChestFishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ChestFishModel extends GeoModel<ChestFishEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/chest_fish.png");

	@Override
	public ResourceLocation getAnimationResource(ChestFishEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/chest_fish.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ChestFishEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/chest_fish.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ChestFishEntity entity) {
		return TEXTURE;
	}

}