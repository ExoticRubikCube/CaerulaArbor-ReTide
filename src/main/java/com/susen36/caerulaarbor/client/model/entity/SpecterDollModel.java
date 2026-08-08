package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.SpecterDollEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpecterDollModel extends GeoModel<SpecterDollEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/specter_doll.png");

	@Override
	public ResourceLocation getAnimationResource(SpecterDollEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/specter_doll.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SpecterDollEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/specter_doll.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SpecterDollEntity entity) {
		return TEXTURE;
	}

}