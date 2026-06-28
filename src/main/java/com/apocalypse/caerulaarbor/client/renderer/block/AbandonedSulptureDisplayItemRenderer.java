package com.apocalypse.caerulaarbor.client.renderer.block;

import com.apocalypse.caerulaarbor.block.item.AbandonedSulptureDisplayItem;
import com.apocalypse.caerulaarbor.client.model.block.AbandonedSulptureDisplayModel;
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
