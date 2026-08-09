package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.item.doll.StonecutterDollDisplayItem;
import com.susen36.caerulaarbor.client.model.block.doll.StonecutterDollDisplayModel;
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