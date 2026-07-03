package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.PunctureFishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PunctureFishModel extends GeoModel<PunctureFishEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/puncturefish.png");

	@Override
	public ResourceLocation getAnimationResource(PunctureFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/puncturefish.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(PunctureFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/puncturefish.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PunctureFishEntity entity) {
		return TEXTURE;
	}

}
