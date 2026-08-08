package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.MegaChestEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MegaChestModel extends GeoModel<MegaChestEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/megachest.png");

	@Override
	public ResourceLocation getAnimationResource(MegaChestEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/chestmega.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MegaChestEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/chestmega.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MegaChestEntity entity) {
		return TEXTURE;
	}

}