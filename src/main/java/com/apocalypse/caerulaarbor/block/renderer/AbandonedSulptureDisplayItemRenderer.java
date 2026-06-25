package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.display.AbandonedSulptureDisplayItem;
import com.apocalypse.caerulaarbor.block.model.AbandonedSulptureDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class AbandonedSulptureDisplayItemRenderer extends GeoItemRenderer<AbandonedSulptureDisplayItem> {
	public AbandonedSulptureDisplayItemRenderer() {
		super(new AbandonedSulptureDisplayModel());
	}

	@Override
	public RenderType getRenderType(AbandonedSulptureDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
