package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.QunyouWantedIsharmlaEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class QunyouWantedIsharmlaModel extends GeoModel<QunyouWantedIsharmlaEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/isharmla_corrupted_heart.png");

	@Override
	public ResourceLocation getAnimationResource(QunyouWantedIsharmlaEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/isharmla.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(QunyouWantedIsharmlaEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/isharmla.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(QunyouWantedIsharmlaEntity entity) {
		return TEXTURE;
	}

}
