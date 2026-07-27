package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.blockentity.TidewayCradleTileEntity;
import com.susen36.caerulaarbor.client.model.block.TidewayCradleBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class TidewayCradleTileRenderer extends GeoBlockRenderer<TidewayCradleTileEntity> {
	public TidewayCradleTileRenderer() {
		super(new TidewayCradleBlockModel());
	}

	@Override
	public RenderType getRenderType(TidewayCradleTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}