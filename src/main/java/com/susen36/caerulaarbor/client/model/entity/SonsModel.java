package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.SonsEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SonsModel extends GeoModel<SonsEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/bishopson.png");

	@Override
	public ResourceLocation getAnimationResource(SonsEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/bishopson.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SonsEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/bishopson.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SonsEntity entity) {
		return TEXTURE;
	}

}