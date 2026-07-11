package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.block.item.ChestmegaSpawnerDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ChestmegaSpawnerDisplayModel extends GeoModel<ChestmegaSpawnerDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(ChestmegaSpawnerDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/chestmega_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ChestmegaSpawnerDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/chestmega_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ChestmegaSpawnerDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/megachest.png");
	}
}
