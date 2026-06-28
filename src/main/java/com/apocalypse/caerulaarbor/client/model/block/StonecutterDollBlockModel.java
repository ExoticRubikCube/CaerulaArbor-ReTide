package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.block.entity.StonecutterDollTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StonecutterDollBlockModel extends GeoModel<StonecutterDollTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(StonecutterDollTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/chiseler_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(StonecutterDollTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/chiseler_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(StonecutterDollTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/chieslerfish.png");
	}
}
