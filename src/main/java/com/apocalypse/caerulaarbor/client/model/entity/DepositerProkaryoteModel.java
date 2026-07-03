package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.DepositerProkaryoteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DepositerProkaryoteModel extends GeoModel<DepositerProkaryoteEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/depositer.png");

	@Override
	public ResourceLocation getAnimationResource(DepositerProkaryoteEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/depsoiter.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(DepositerProkaryoteEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/depsoiter.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(DepositerProkaryoteEntity entity) {
		return TEXTURE;
	}

}
