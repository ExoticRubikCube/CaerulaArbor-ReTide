package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.item.ViviparousLilyDisplayItem;
import com.susen36.caerulaarbor.client.model.block.ViviparousLilyDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ViviparousLilyDisplayItemRenderer extends GeoItemRenderer<ViviparousLilyDisplayItem> {
	public ViviparousLilyDisplayItemRenderer() {
		super(new ViviparousLilyDisplayModel());
	}

	@Override
	public RenderType getRenderType(ViviparousLilyDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}