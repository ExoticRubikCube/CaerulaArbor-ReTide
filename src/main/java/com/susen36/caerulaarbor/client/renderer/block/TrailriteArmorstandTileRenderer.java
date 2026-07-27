package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.blockentity.TrailriteArmorstandTileEntity;
import com.susen36.caerulaarbor.client.model.block.TrailriteArmorstandBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class TrailriteArmorstandTileRenderer extends GeoBlockRenderer<TrailriteArmorstandTileEntity> {
	public TrailriteArmorstandTileRenderer() {
		super(new TrailriteArmorstandBlockModel());
	}

	@Override
	public RenderType getRenderType(TrailriteArmorstandTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}