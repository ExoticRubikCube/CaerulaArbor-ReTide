package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.blockentity.SwarmcallerDollTileEntity;
import com.susen36.caerulaarbor.client.model.block.SwarmcallerDollBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SwarmcallerDollTileRenderer extends GeoBlockRenderer<SwarmcallerDollTileEntity> {
	public SwarmcallerDollTileRenderer() {
		super(new SwarmcallerDollBlockModel());
	}

	@Override
	public RenderType getRenderType(SwarmcallerDollTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
