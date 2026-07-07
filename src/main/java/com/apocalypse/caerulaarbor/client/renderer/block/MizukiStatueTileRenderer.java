package com.apocalypse.caerulaarbor.client.renderer.block;

import com.apocalypse.caerulaarbor.block.blockentity.MizukiStatueTileEntity;
import com.apocalypse.caerulaarbor.client.model.block.MizukiStatueBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MizukiStatueTileRenderer extends GeoBlockRenderer<MizukiStatueTileEntity> {
	public MizukiStatueTileRenderer() {
		super(new MizukiStatueBlockModel());
	}

	@Override
	public RenderType getRenderType(MizukiStatueTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
