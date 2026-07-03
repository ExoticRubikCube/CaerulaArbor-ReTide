package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.XantisEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class XantisModel extends GeoModel<XantisEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/xantis.png");

	@Override
	public ResourceLocation getAnimationResource(XantisEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/xantis.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(XantisEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/xantis.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(XantisEntity entity) {
		return TEXTURE;
	}

}
