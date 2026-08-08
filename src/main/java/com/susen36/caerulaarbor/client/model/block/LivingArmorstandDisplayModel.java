package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.block.item.LivingArmorstandDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LivingArmorstandDisplayModel extends GeoModel<LivingArmorstandDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(LivingArmorstandDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/living_armorstand.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(LivingArmorstandDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/living_armorstand.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LivingArmorstandDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/flamarine_armorstand.png");
	}
}