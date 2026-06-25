package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.entity.HighmoreSpawnblockTileEntity;
import com.apocalypse.caerulaarbor.block.model.HighmoreSpawnblockBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class HighmoreSpawnblockTileRenderer extends GeoBlockRenderer<HighmoreSpawnblockTileEntity> {
	public HighmoreSpawnblockTileRenderer() {
		super(new HighmoreSpawnblockBlockModel());
	}

	@Override
	public RenderType getRenderType(HighmoreSpawnblockTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
