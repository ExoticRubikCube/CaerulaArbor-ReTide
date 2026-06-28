package com.apocalypse.caerulaarbor.client.renderer.block;

import com.apocalypse.caerulaarbor.block.item.SwarmcallerDollDisplayItem;
import com.apocalypse.caerulaarbor.client.model.block.SwarmcallerDollDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SwarmcallerDollDisplayItemRenderer extends GeoItemRenderer<SwarmcallerDollDisplayItem> {
	public SwarmcallerDollDisplayItemRenderer() {
		super(new SwarmcallerDollDisplayModel());
	}

	@Override
	public RenderType getRenderType(SwarmcallerDollDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
