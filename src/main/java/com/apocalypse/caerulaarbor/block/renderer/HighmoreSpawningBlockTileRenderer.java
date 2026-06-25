package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.entity.HighmoreSpawningBlockTileEntity;
import com.apocalypse.caerulaarbor.block.model.HighmoreSpawningBlockBlockModel;
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
