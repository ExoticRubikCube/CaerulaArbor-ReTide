package com.apocalypse.caerulaarbor.block.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.block.entity.MizukiStatueTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MizukiStatueBlockModel extends GeoModel<MizukiStatueTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(MizukiStatueTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation(CaerulaArborMod.MODID, "animations/mizuki.animation.json");
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/mizuki.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MizukiStatueTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation(CaerulaArborMod.MODID, "geo/mizuki.geo.json");
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/mizuki.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MizukiStatueTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/mizuki_dim.png");
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/mizuki.png");
	}
}
