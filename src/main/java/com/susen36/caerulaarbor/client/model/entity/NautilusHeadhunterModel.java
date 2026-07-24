package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.NautilusHeadhunterEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NautilusHeadhunterModel extends GeoModel<NautilusHeadhunterEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/nautilus_headhunter.png");

	@Override
	public ResourceLocation getAnimationResource(NautilusHeadhunterEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/nultilus_headhunter.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(NautilusHeadhunterEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/nultilus_headhunter.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(NautilusHeadhunterEntity entity) {
		return TEXTURE;
	}

}
