package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.OceanStonecutteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class OceanStonecutteModel extends GeoModel<OceanStonecutteEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/ocean_stonecutte.png");

	@Override
	public ResourceLocation getAnimationResource(OceanStonecutteEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/ocean_stonecutte.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanStonecutteEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/ocean_stonecutte.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanStonecutteEntity entity) {
		return TEXTURE;
	}
}
