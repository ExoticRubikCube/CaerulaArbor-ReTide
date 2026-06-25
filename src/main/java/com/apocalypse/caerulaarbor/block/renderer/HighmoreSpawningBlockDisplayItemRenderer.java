package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.display.HighmoreSpawningBlockDisplayItem;
import com.apocalypse.caerulaarbor.block.model.HighmoreSpawningBlockDisplayModel;
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
