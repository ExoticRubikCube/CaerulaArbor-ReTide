package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.DepositerProkaryoteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DepositerProkaryoteModel extends GeoModel<DepositerProkaryoteEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/depositer.png");

	@Override
	public ResourceLocation getAnimationResource(DepositerProkaryoteEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/depsoiter.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(DepositerProkaryoteEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/depsoiter.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(DepositerProkaryoteEntity entity) {
		return TEXTURE;
	}

}
