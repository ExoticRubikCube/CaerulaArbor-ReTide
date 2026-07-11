package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.IzumikOffspringEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IzumikOffspringModel extends GeoModel<IzumikOffspringEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/izumik_offspring.png");

	@Override
	public ResourceLocation getAnimationResource(IzumikOffspringEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/izumik_offspring.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(IzumikOffspringEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/izumik_offspring.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(IzumikOffspringEntity entity) {
		return TEXTURE;
	}

}
