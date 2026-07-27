package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.blockentity.CrisisTableTileEntity;
import com.susen36.caerulaarbor.client.model.block.CrisisTableBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CrisisTableTileRenderer extends GeoBlockRenderer<CrisisTableTileEntity> {
	public CrisisTableTileRenderer() {
		super(new CrisisTableBlockModel());
	}

	@Override
	public RenderType getRenderType(CrisisTableTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}