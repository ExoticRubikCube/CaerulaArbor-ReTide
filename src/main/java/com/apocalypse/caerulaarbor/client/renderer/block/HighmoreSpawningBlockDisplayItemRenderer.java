package com.apocalypse.caerulaarbor.client.renderer.block;

import com.apocalypse.caerulaarbor.block.item.HighmoreSpawningBlockDisplayItem;
import com.apocalypse.caerulaarbor.client.model.block.HighmoreSpawningBlockDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class HighmoreSpawningBlockDisplayItemRenderer extends GeoItemRenderer<HighmoreSpawningBlockDisplayItem> {
	public HighmoreSpawningBlockDisplayItemRenderer() {
		super(new HighmoreSpawningBlockDisplayModel());
	}

	@Override
	public RenderType getRenderType(HighmoreSpawningBlockDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
