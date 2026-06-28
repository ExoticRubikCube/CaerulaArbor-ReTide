package com.apocalypse.caerulaarbor.client.renderer.block;

import com.apocalypse.caerulaarbor.block.entity.CrisisTableTileEntity;
import com.apocalypse.caerulaarbor.client.model.block.CrisisTableBlockModel;
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
