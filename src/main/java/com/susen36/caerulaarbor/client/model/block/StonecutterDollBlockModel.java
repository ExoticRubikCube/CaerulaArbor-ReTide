package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.block.blockentity.StonecutterDollTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StonecutterDollBlockModel extends GeoModel<StonecutterDollTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(StonecutterDollTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/chiseler_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(StonecutterDollTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/chiseler_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(StonecutterDollTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/chieslerfish.png");
	}
}
