package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.block.item.MizukiStatueDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MizukiStatueDisplayModel extends GeoModel<MizukiStatueDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(MizukiStatueDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/mizuki.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MizukiStatueDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/mizuki.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MizukiStatueDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/mizuki.png");
	}
}
