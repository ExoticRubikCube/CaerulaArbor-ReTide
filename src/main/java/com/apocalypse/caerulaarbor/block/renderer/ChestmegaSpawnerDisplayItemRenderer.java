package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.display.ChestmegaSpawnerDisplayItem;
import com.apocalypse.caerulaarbor.block.model.ChestmegaSpawnerDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ChestmegaSpawnerDisplayItemRenderer extends GeoItemRenderer<ChestmegaSpawnerDisplayItem> {
	public ChestmegaSpawnerDisplayItemRenderer() {
		super(new ChestmegaSpawnerDisplayModel());
	}

	@Override
	public RenderType getRenderType(ChestmegaSpawnerDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
