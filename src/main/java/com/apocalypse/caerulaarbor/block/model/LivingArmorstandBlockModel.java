package com.apocalypse.caerulaarbor.block.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.block.entity.LivingArmorstandTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LivingArmorstandBlockModel extends GeoModel<LivingArmorstandTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(LivingArmorstandTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/living_armorstand.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(LivingArmorstandTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/living_armorstand.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LivingArmorstandTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/flamarine_armorstand.png");
	}
}
