package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.IzumikEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IzumikModel extends GeoModel<IzumikEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/izumik_base.png");

	@Override
	public ResourceLocation getAnimationResource(IzumikEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/izumik.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(IzumikEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/izumik.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(IzumikEntity entity) {
		return TEXTURE;
	}

}