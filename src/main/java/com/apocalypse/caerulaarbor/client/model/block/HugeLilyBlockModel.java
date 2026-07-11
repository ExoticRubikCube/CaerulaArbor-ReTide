package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.block.blockentity.HugeLilyTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HugeLilyBlockModel extends GeoModel<HugeLilyTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(HugeLilyTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/viviparous_lily_huge.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(HugeLilyTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/viviparous_lily_huge.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(HugeLilyTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/vivi_lily.png");
	}
}
