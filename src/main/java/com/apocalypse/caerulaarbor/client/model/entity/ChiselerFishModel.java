package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.ChiselerFishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ChiselerFishModel extends GeoModel<ChiselerFishEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/chieslerfish.png");

	@Override
	public ResourceLocation getAnimationResource(ChiselerFishEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/chiseler.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ChiselerFishEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/chiseler.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ChiselerFishEntity entity) {
		return TEXTURE;
	}

}
