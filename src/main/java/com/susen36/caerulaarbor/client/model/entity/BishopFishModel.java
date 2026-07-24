package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.BishopFishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BishopFishModel extends GeoModel<BishopFishEntity> {
	@Override
	public ResourceLocation getAnimationResource(BishopFishEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/bishop.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BishopFishEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/bishop.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BishopFishEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/bishop.png");
	}

}
