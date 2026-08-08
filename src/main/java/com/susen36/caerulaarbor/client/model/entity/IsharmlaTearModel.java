package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.isharmla.IsharmlaTearEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IsharmlaTearModel extends GeoModel<IsharmlaTearEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/isharmla_tear.png");

	@Override
	public ResourceLocation getAnimationResource(IsharmlaTearEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/isharmla_tear.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(IsharmlaTearEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/isharmla_tear.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(IsharmlaTearEntity entity) {
		return TEXTURE;
	}

}