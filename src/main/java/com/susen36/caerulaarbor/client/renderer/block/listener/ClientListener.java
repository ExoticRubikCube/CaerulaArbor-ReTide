package com.susen36.caerulaarbor.client.renderer.block.listener;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.client.renderer.block.*;
import com.susen36.caerulaarbor.init.CABlockEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CaerulaArborMod.MODID)
public class ClientListener {
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(CABlockEntities.TIDEWAY_CRADLE.get(), context -> new TidewayCradleTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.CHESTMEGA_SPAWNER.get(), context -> new ChestmegaSpawnerTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.VIVIPAROUS_LILY.get(), context -> new ViviparousLilyTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.HUGE_LILY.get(), context -> new HugeLilyTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.HIGHMORE_SPAWNBLOCK.get(), context -> new HighmoreSpawnblockTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.CRISIS_TABLE.get(), context -> new CrisisTableTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.HIGHMORE_SPAWNING_BLOCK.get(), context -> new HighmoreSpawningBlockTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.MIZUKI_STATUE.get(), context -> new MizukiStatueTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.POCKET_SEA_DOLL.get(), context -> new PocketSeaDollTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.SWARMCALLER_DOLL.get(), context -> new SwarmcallerDollTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.STONECUTTER_DOLL.get(), context -> new StonecutterDollTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.ABANDONED_SULPTURE.get(), context -> new AbandonedSulptureTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.CENTRIFUGER.get(), context -> new CentrifugerTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.ILLUSIONER_BANNER.get(), context -> new IllusionerBannerTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.LIVING_ARMORSTAND.get(), context -> new LivingArmorstandTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.TRAILRITE_ARMORSTAND.get(), context -> new TrailriteArmorstandTileRenderer());
	}
}
