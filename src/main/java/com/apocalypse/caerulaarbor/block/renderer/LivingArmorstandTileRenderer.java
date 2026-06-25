package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.entity.LivingArmorstandTileEntity;
import com.apocalypse.caerulaarbor.block.model.LivingArmorstandBlockModel;
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
