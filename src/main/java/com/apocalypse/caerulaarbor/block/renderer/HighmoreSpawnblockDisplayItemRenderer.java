package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.display.HighmoreSpawnblockDisplayItem;
import com.apocalypse.caerulaarbor.block.model.HighmoreSpawnblockDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class HighmoreSpawnblockDisplayItemRenderer extends GeoItemRenderer<HighmoreSpawnblockDisplayItem> {
	public HighmoreSpawnblockDisplayItemRenderer() {
		super(new HighmoreSpawnblockDisplayModel());
	}

	@Override
	public RenderType getRenderType(HighmoreSpawnblockDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
