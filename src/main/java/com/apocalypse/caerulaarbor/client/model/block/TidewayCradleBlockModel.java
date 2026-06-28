package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.block.entity.TidewayCradleTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TidewayCradleBlockModel extends GeoModel<TidewayCradleTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(TidewayCradleTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation(CaerulaArborMod.MODID, "animations/tideway_cradle.animation.json");
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/tideway_cradle.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TidewayCradleTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation(CaerulaArborMod.MODID, "geo/tideway_cradle.geo.json");
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/tideway_cradle.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TidewayCradleTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/cradle_dim.png");
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/tideway_cradle.png");
	}
}
