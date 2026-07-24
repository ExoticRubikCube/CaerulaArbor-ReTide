package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArborMod;

import com.susen36.caerulaarbor.block.item.TrailriteArmorstandDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TrailriteArmorstandDisplayModel extends GeoModel<TrailriteArmorstandDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(TrailriteArmorstandDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/trairite_armorstand.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TrailriteArmorstandDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/trairite_armorstand.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TrailriteArmorstandDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/trailrte_armorstand.png");
	}
}
