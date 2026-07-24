package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.BoneFishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BoneFishModel extends GeoModel<BoneFishEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/bonefish.png");

	@Override
	public ResourceLocation getAnimationResource(BoneFishEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/bonefish.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BoneFishEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/bonefish.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BoneFishEntity entity) {
		return TEXTURE;
	}

}
