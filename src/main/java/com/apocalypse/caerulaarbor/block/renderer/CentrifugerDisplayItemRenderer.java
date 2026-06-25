package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.display.CentrifugerDisplayItem;
import com.apocalypse.caerulaarbor.block.model.CentrifugerDisplayModel;
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
