package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.ReaperPetEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ReaperPetModel extends GeoModel<ReaperPetEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/fishpet.png");

	@Override
	public ResourceLocation getAnimationResource(ReaperPetEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/reaperpet.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ReaperPetEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/reaperpet.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ReaperPetEntity entity) {
		return TEXTURE;
	}

}
