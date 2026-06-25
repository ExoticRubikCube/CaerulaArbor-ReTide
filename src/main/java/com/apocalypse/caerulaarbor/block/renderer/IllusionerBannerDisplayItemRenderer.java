package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.display.IllusionerBannerDisplayItem;
import com.apocalypse.caerulaarbor.block.model.IllusionerBannerDisplayModel;
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
