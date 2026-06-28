package com.apocalypse.caerulaarbor.client.renderer.block;

import com.apocalypse.caerulaarbor.block.item.TidewayCradleDisplayItem;
import com.apocalypse.caerulaarbor.client.model.block.TidewayCradleDisplayModel;
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
