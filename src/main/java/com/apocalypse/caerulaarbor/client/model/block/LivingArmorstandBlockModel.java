package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.block.blockentity.LivingArmorstandTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LivingArmorstandBlockModel extends GeoModel<LivingArmorstandTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(LivingArmorstandTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/living_armorstand.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(LivingArmorstandTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/living_armorstand.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LivingArmorstandTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/flamarine_armorstand.png");
	}
}
