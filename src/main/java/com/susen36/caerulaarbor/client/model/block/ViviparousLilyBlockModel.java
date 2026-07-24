package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.block.blockentity.ViviparousLilyTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ViviparousLilyBlockModel extends GeoModel<ViviparousLilyTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(ViviparousLilyTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/viviparous_lily.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ViviparousLilyTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/viviparous_lily.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ViviparousLilyTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/vivi_lily.png");
	}
}
