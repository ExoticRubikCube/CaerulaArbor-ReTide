package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.block.entity.AbandonedSulptureTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AbandonedSulptureBlockModel extends GeoModel<AbandonedSulptureTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(AbandonedSulptureTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/abandoned_sulpture.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AbandonedSulptureTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/abandoned_sulpture.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AbandonedSulptureTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/theabandoned_sculpture.png");
	}
}
