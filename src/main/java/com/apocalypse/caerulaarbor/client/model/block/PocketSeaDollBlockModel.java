package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.block.entity.PocketSeaDollTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PocketSeaDollBlockModel extends GeoModel<PocketSeaDollTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(PocketSeaDollTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/creeper_fish_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(PocketSeaDollTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/creeper_fish_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PocketSeaDollTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/creeperfish.png");
	}
}
