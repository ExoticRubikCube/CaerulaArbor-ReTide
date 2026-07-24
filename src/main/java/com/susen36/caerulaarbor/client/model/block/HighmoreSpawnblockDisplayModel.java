package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.block.item.HighmoreSpawnblockDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HighmoreSpawnblockDisplayModel extends GeoModel<HighmoreSpawnblockDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(HighmoreSpawnblockDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/highmore_spawnblock.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(HighmoreSpawnblockDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/highmore_spawnblock.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(HighmoreSpawnblockDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/highmore_spawnblock.png");
	}
}
