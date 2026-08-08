package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.ReaperPetEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ReaperPetModel extends GeoModel<ReaperPetEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/fishpet.png");

	@Override
	public ResourceLocation getAnimationResource(ReaperPetEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/reaperpet.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ReaperPetEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/reaperpet.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ReaperPetEntity entity) {
		return TEXTURE;
	}

}