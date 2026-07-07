package com.apocalypse.caerulaarbor.client.renderer.block;

import com.apocalypse.caerulaarbor.block.blockentity.LivingArmorstandTileEntity;
import com.apocalypse.caerulaarbor.client.model.block.LivingArmorstandBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class LivingArmorstandTileRenderer extends GeoBlockRenderer<LivingArmorstandTileEntity> {
	public LivingArmorstandTileRenderer() {
		super(new LivingArmorstandBlockModel());
	}

	@Override
	public RenderType getRenderType(LivingArmorstandTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
