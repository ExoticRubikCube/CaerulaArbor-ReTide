package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.display.HugeLilyDisplayItem;
import com.apocalypse.caerulaarbor.block.model.HugeLilyDisplayModel;
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
