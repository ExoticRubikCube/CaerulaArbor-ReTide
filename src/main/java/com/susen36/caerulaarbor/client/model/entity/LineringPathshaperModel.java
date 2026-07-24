package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.routeshaper.LineringPathshaperEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LineringPathshaperModel extends GeoModel<LineringPathshaperEntity> {
	@Override
	public ResourceLocation getAnimationResource(LineringPathshaperEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/routeshaper.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(LineringPathshaperEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/routeshaper.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LineringPathshaperEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/lingering_pathshaper.png");
	}

}

