package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.display.TidewayCradleDisplayItem;
import com.apocalypse.caerulaarbor.block.model.TidewayCradleDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class TidewayCradleDisplayItemRenderer extends GeoItemRenderer<TidewayCradleDisplayItem> {
	public TidewayCradleDisplayItemRenderer() {
		super(new TidewayCradleDisplayModel());
	}

	@Override
	public RenderType getRenderType(TidewayCradleDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
