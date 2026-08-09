package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.item.doll.PocketSeaDollDisplayItem;
import com.susen36.caerulaarbor.client.model.block.doll.PocketSeaDollDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PocketSeaDollDisplayItemRenderer extends GeoItemRenderer<PocketSeaDollDisplayItem> {
	public PocketSeaDollDisplayItemRenderer() {
		super(new PocketSeaDollDisplayModel());
	}

	@Override
	public RenderType getRenderType(PocketSeaDollDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}