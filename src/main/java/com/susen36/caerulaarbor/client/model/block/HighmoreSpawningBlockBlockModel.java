package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.block.blockentity.HighmoreSpawningBlockTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HighmoreSpawningBlockBlockModel extends GeoModel<HighmoreSpawningBlockTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(HighmoreSpawningBlockTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/highmore_spawnblock.animation.json");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/highmore_spawnblock.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(HighmoreSpawningBlockTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/highmore_spawnblock.geo.json");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/highmore_spawnblock.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(HighmoreSpawningBlockTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/highmore_spawn_withoutlight.png");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/highmore_spawnblock.png");
	}
}