package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.display.StonecutterDollDisplayItem;
import com.apocalypse.caerulaarbor.block.model.StonecutterDollDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class StonecutterDollDisplayItemRenderer extends GeoItemRenderer<StonecutterDollDisplayItem> {
	public StonecutterDollDisplayItemRenderer() {
		super(new StonecutterDollDisplayModel());
	}

	@Override
	public RenderType getRenderType(StonecutterDollDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
