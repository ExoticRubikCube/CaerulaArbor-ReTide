package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.display.LivingArmorstandDisplayItem;
import com.apocalypse.caerulaarbor.block.model.LivingArmorstandDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class LivingArmorstandDisplayItemRenderer extends GeoItemRenderer<LivingArmorstandDisplayItem> {
	public LivingArmorstandDisplayItemRenderer() {
		super(new LivingArmorstandDisplayModel());
	}

	@Override
	public RenderType getRenderType(LivingArmorstandDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
