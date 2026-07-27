package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.blockentity.HighmoreSpawnblockTileEntity;
import com.susen36.caerulaarbor.client.model.block.HighmoreSpawnblockBlockModel;
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