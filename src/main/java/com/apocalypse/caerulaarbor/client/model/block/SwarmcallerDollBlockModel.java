package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.block.entity.SwarmcallerDollTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SwarmcallerDollBlockModel extends GeoModel<SwarmcallerDollTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(SwarmcallerDollTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/umbrella_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SwarmcallerDollTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/umbrella_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SwarmcallerDollTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/umbrella.png");
	}
}
