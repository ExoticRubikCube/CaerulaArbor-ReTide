package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.blockentity.StonecutterDollTileEntity;
import com.susen36.caerulaarbor.client.model.block.StonecutterDollBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class StonecutterDollTileRenderer extends GeoBlockRenderer<StonecutterDollTileEntity> {
	public StonecutterDollTileRenderer() {
		super(new StonecutterDollBlockModel());
	}

	@Override
	public RenderType getRenderType(StonecutterDollTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
