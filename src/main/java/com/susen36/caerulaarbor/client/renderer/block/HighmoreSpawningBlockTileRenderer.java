package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.blockentity.HighmoreSpawningBlockTileEntity;
import com.susen36.caerulaarbor.client.model.block.HighmoreSpawningBlockBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class HighmoreSpawningBlockTileRenderer extends GeoBlockRenderer<HighmoreSpawningBlockTileEntity> {
	public HighmoreSpawningBlockTileRenderer() {
		super(new HighmoreSpawningBlockBlockModel());
	}

	@Override
	public RenderType getRenderType(HighmoreSpawningBlockTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
