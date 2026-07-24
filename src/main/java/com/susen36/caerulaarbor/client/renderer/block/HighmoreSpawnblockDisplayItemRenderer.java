package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.item.HighmoreSpawnblockDisplayItem;
import com.susen36.caerulaarbor.client.model.block.HighmoreSpawnblockDisplayModel;
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
