package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.SonsEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SonsModel extends GeoModel<SonsEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/bishopson.png");

	@Override
	public ResourceLocation getAnimationResource(SonsEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/bishopson.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SonsEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/bishopson.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SonsEntity entity) {
		return TEXTURE;
	}

}
