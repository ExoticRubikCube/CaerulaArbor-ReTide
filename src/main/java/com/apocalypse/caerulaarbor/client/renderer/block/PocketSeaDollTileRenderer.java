package com.apocalypse.caerulaarbor.client.renderer.block;

import com.apocalypse.caerulaarbor.block.entity.PocketSeaDollTileEntity;
import com.apocalypse.caerulaarbor.client.model.block.PocketSeaDollBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PocketSeaDollTileRenderer extends GeoBlockRenderer<PocketSeaDollTileEntity> {
	public PocketSeaDollTileRenderer() {
		super(new PocketSeaDollBlockModel());
	}

	@Override
	public RenderType getRenderType(PocketSeaDollTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
