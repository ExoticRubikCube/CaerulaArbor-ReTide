package com.apocalypse.caerulaarbor.block.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.block.display.MizukiStatueDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MizukiStatueDisplayModel extends GeoModel<MizukiStatueDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(MizukiStatueDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/mizuki.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MizukiStatueDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/mizuki.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MizukiStatueDisplayItem entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/mizuki.png");
	}
}
