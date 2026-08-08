package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.GladiiaWhirlEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GladiiaWhirlModel extends GeoModel<GladiiaWhirlEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/gladiia_whirl.png");

	@Override
	public ResourceLocation getAnimationResource(GladiiaWhirlEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/gladiia_whirl.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GladiiaWhirlEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/gladiia_whirl.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GladiiaWhirlEntity entity) {
		return TEXTURE;
	}

}