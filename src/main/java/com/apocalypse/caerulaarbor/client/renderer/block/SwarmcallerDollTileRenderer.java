package com.apocalypse.caerulaarbor.client.renderer.block;

import com.apocalypse.caerulaarbor.block.entity.SwarmcallerDollTileEntity;
import com.apocalypse.caerulaarbor.client.model.block.SwarmcallerDollBlockModel;
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
