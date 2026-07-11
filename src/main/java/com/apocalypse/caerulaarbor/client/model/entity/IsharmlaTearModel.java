package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.IsharmlaTearEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IsharmlaTearModel extends GeoModel<IsharmlaTearEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/isharmla_tear.png");

	@Override
	public ResourceLocation getAnimationResource(IsharmlaTearEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/isharmla_tear.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(IsharmlaTearEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/isharmla_tear.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(IsharmlaTearEntity entity) {
		return TEXTURE;
	}

}
