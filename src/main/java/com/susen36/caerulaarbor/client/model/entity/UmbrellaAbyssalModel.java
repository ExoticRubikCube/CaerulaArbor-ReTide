package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.UmbrellaAbyssalEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class UmbrellaAbyssalModel extends GeoModel<UmbrellaAbyssalEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/umbrella.png");

	@Override
	public ResourceLocation getAnimationResource(UmbrellaAbyssalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/umbrella.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(UmbrellaAbyssalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/umbrella.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(UmbrellaAbyssalEntity entity) {
		return TEXTURE;
	}

}
