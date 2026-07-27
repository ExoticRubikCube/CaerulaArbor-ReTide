package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.item.IllusionerBannerDisplayItem;
import com.susen36.caerulaarbor.client.model.block.IllusionerBannerDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class IllusionerBannerDisplayItemRenderer extends GeoItemRenderer<IllusionerBannerDisplayItem> {
	public IllusionerBannerDisplayItemRenderer() {
		super(new IllusionerBannerDisplayModel());
	}

	@Override
	public RenderType getRenderType(IllusionerBannerDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}