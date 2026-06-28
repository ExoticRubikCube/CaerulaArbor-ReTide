package com.apocalypse.caerulaarbor.client.renderer.block;

import com.apocalypse.caerulaarbor.block.item.ViviparousLilyDisplayItem;
import com.apocalypse.caerulaarbor.client.model.block.ViviparousLilyDisplayModel;
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
