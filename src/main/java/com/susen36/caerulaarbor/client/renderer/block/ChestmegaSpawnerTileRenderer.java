package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.blockentity.ChestmegaSpawnerTileEntity;
import com.susen36.caerulaarbor.client.model.block.ChestmegaSpawnerBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ChestmegaSpawnerTileRenderer extends GeoBlockRenderer<ChestmegaSpawnerTileEntity> {
	public ChestmegaSpawnerTileRenderer() {
		super(new ChestmegaSpawnerBlockModel());
	}

	@Override
	public RenderType getRenderType(ChestmegaSpawnerTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
