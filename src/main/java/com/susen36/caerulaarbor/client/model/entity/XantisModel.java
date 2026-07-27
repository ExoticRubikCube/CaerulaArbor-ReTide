package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.XantisEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class XantisModel extends GeoModel<XantisEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/xantis.png");

	@Override
	public ResourceLocation getAnimationResource(XantisEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/xantis.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(XantisEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/xantis.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(XantisEntity entity) {
		return TEXTURE;
	}

}