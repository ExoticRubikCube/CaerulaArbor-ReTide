package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.GuideAbyssalEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GuideAbyssalModel extends GeoModel<GuideAbyssalEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/nethersea__bandguider.png");

	@Override
	public ResourceLocation getAnimationResource(GuideAbyssalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/nethersea_brandguider.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GuideAbyssalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/nethersea_brandguider.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GuideAbyssalEntity entity) {
		return TEXTURE;
	}

}
