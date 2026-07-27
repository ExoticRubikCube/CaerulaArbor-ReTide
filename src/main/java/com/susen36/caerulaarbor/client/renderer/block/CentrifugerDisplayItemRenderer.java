package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.item.CentrifugerDisplayItem;
import com.susen36.caerulaarbor.client.model.block.CentrifugerDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class CentrifugerDisplayItemRenderer extends GeoItemRenderer<CentrifugerDisplayItem> {
	public CentrifugerDisplayItemRenderer() {
		super(new CentrifugerDisplayModel());
	}

	@Override
	public RenderType getRenderType(CentrifugerDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}