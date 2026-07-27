package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.item.HugeLilyDisplayItem;
import com.susen36.caerulaarbor.client.model.block.HugeLilyDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class HugeLilyDisplayItemRenderer extends GeoItemRenderer<HugeLilyDisplayItem> {
	public HugeLilyDisplayItemRenderer() {
		super(new HugeLilyDisplayModel());
	}

	@Override
	public RenderType getRenderType(HugeLilyDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}