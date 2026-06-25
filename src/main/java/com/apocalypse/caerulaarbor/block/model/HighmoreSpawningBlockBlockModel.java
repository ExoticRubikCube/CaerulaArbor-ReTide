package com.apocalypse.caerulaarbor.block.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.block.entity.HighmoreSpawningBlockTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HighmoreSpawningBlockBlockModel extends GeoModel<HighmoreSpawningBlockTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(HighmoreSpawningBlockTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation(CaerulaArborMod.MODID, "animations/highmore_spawnblock.animation.json");
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/highmore_spawnblock.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(HighmoreSpawningBlockTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation(CaerulaArborMod.MODID, "geo/highmore_spawnblock.geo.json");
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/highmore_spawnblock.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(HighmoreSpawningBlockTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/highmore_spawn_withoutlight.png");
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/highmore_spawnblock.png");
	}
}
