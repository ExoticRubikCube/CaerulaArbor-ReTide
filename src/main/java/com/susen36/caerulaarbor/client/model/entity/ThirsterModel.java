package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.ThirsterEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ThirsterModel extends GeoModel<ThirsterEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/thirster.png");

	@Override
	public ResourceLocation getAnimationResource(ThirsterEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/thirster.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ThirsterEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/thirster.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ThirsterEntity entity) {
		return TEXTURE;
	}

}