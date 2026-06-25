package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.display.TrailriteArmorstandDisplayItem;
import com.apocalypse.caerulaarbor.block.model.TrailriteArmorstandDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class TrailriteArmorstandDisplayItemRenderer extends GeoItemRenderer<TrailriteArmorstandDisplayItem> {
	public TrailriteArmorstandDisplayItemRenderer() {
		super(new TrailriteArmorstandDisplayModel());
	}

	@Override
	public RenderType getRenderType(TrailriteArmorstandDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
