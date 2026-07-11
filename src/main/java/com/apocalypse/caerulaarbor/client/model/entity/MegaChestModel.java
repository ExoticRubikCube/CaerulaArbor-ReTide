package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.MegaChestEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MegaChestModel extends GeoModel<MegaChestEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/megachest.png");

	@Override
	public ResourceLocation getAnimationResource(MegaChestEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/chestmega.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MegaChestEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/chestmega.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MegaChestEntity entity) {
		return TEXTURE;
	}

}
