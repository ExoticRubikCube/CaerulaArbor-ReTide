/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.client.model.entity.*;
import com.apocalypse.caerulaarbor.client.renderer.entity.*;
import com.apocalypse.caerulaarbor.entity.*;
import com.apocalypse.caerulaarbor.entity.bullets.*;
import com.apocalypse.caerulaarbor.entity.helper.Al1SHelperEntity;
import com.apocalypse.caerulaarbor.entity.helper.LittleHelperEntity;
import com.apocalypse.caerulaarbor.entity.routeshaper.LineringPathshaperEntity;
import com.apocalypse.caerulaarbor.entity.routeshaper.LingeringFractalEntity;
import com.apocalypse.caerulaarbor.entity.routeshaper.RouteFractalEntity;
import com.apocalypse.caerulaarbor.entity.routeshaper.RouteShaperEntity;
import com.apocalypse.caerulaarbor.entity.warden.OceanizedWardenEntity;
import com.apocalypse.caerulaarbor.entity.warden.OceanizedWardenisEntity;
import com.apocalypse.caerulaarbor.entity.wither.OceanizedWitherEntity;
import com.apocalypse.caerulaarbor.entity.wither.OceannizedWitheriaEntity;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CAEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CaerulaArborMod.MODID);
	public static final RegistryObject<EntityType<RunFishEntity>> RUN_FISH = register("run_fish",
			EntityType.Builder.<RunFishEntity>of(RunFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new RunFishEntity(level))

					.sized(0.4f, 0.5f));
	public static final RegistryObject<EntityType<SliderFishEntity>> SLIDER_FISH = register("slider_fish",
			EntityType.Builder.<SliderFishEntity>of(SliderFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SliderFishEntity(level))

					.sized(0.5f, 0.8f));
	public static final RegistryObject<EntityType<SuperSliderEntity>> SUPER_SLIDER = register("super_slider",
			EntityType.Builder.<SuperSliderEntity>of(SuperSliderEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SuperSliderEntity(level))

					.sized(0.3f, 0.8f));
	public static final RegistryObject<EntityType<ShooterFishEntity>> SHOOTER_FISH = register("shooter_fish",
			EntityType.Builder.<ShooterFishEntity>of(ShooterFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ShooterFishEntity(level))

					.sized(0.6f, 1.2f));
	public static final RegistryObject<EntityType<FishShootEntity>> FISH_SHOOT = register("fish_shoot",
			EntityType.Builder.<FishShootEntity>of(FishShootEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new FishShootEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
	public static final RegistryObject<EntityType<FlyFishEntity>> FLY_FISH = register("fly_fish",
			EntityType.Builder.<FlyFishEntity>of(FlyFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new FlyFishEntity(level))

					.sized(0.6f, 0.9f));
	public static final RegistryObject<EntityType<ReaperFishEntity>> REAPER_FISH = register("reaper_fish",
			EntityType.Builder.<ReaperFishEntity>of(ReaperFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(10).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ReaperFishEntity(level))

					.sized(1.2f, 2.8f));
	public static final RegistryObject<EntityType<CreeperFishEntity>> CREEPER_FISH = register("creeper_fish",
			EntityType.Builder.<CreeperFishEntity>of(CreeperFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new CreeperFishEntity(level))

					.sized(0.8f, 1.5f));
	public static final RegistryObject<EntityType<PunctureFishEntity>> PUNCTURE_FISH = register("puncture_fish",
			EntityType.Builder.<PunctureFishEntity>of(PunctureFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new PunctureFishEntity(level))

					.sized(0.9f, 2.7f));
	public static final RegistryObject<EntityType<BaselayerAbyssalEntity>> BASELAYER_ABYSSAL = register("baselayer_abyssal",
			EntityType.Builder.<BaselayerAbyssalEntity>of(BaselayerAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new BaselayerAbyssalEntity(level))

					.sized(0.7f, 1.5f));
	public static final RegistryObject<EntityType<PredatorAbyssalEntity>> PREDATOR_ABYSSAL = register("predator_abyssal",
			EntityType.Builder.<PredatorAbyssalEntity>of(PredatorAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new PredatorAbyssalEntity(level))

					.sized(0.6f, 1.4f));
	public static final RegistryObject<EntityType<GuideAbyssalEntity>> GUIDE_ABYSSAL = register("guide_abyssal",
			EntityType.Builder.<GuideAbyssalEntity>of(GuideAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new GuideAbyssalEntity(level))

					.sized(1f, 2.5f));
	public static final RegistryObject<EntityType<SplasherAbyssalEntity>> SPLASHER_ABYSSAL = register("splasher_abyssal",
			EntityType.Builder.<SplasherAbyssalEntity>of(SplasherAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SplasherAbyssalEntity(level))

					.sized(0.6f, 1.1f));
	public static final RegistryObject<EntityType<FishSplashEntity>> FISH_SPLASH = register("fish_splash",
			EntityType.Builder.<FishSplashEntity>of(FishSplashEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new FishSplashEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
	public static final RegistryObject<EntityType<UmbrellaAbyssalEntity>> UMBRELLA_ABYSSAL = register("umbrella_abyssal",
			EntityType.Builder.<UmbrellaAbyssalEntity>of(UmbrellaAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new UmbrellaAbyssalEntity(level))

					.sized(0.8f, 1.5f));
	public static final RegistryObject<EntityType<CrackerAbyssalEntity>> CRACKER_ABYSSAL = register("cracker_abyssal",
			EntityType.Builder.<CrackerAbyssalEntity>of(CrackerAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new CrackerAbyssalEntity(level))

					.sized(0.7f, 2f));
	public static final RegistryObject<EntityType<CollectorProkaryoteEntity>> COLLECTOR_PROKARYOTE = register("collector_prokaryote",
			EntityType.Builder.<CollectorProkaryoteEntity>of(CollectorProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3)
					.setCustomClientFactory((spawnEntity, level) -> new CollectorProkaryoteEntity(level))

					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<BoneFishEntity>> BONE_FISH = register("bone_fish",
			EntityType.Builder.<BoneFishEntity>of(BoneFishEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new BoneFishEntity(level))

					.sized(0.7f, 0.7f));
	public static final RegistryObject<EntityType<ChiselerFishEntity>> CHISELER_FISH = register("chiseler_fish",
			EntityType.Builder.<ChiselerFishEntity>of(ChiselerFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ChiselerFishEntity(level))

					.sized(0.6f, 0.6f));
	public static final RegistryObject<EntityType<FakerggShootEntity>> FAKERGG_SHOOT = register("fakergg_shoot",
			EntityType.Builder.<FakerggShootEntity>of(FakerggShootEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new FakerggShootEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.4f, 0.4f));
	public static final RegistryObject<EntityType<PregnantFishEntity>> PREGNANT_FISH = register("pregnant_fish",
			EntityType.Builder.<PregnantFishEntity>of(PregnantFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new PregnantFishEntity(level))

					.sized(0.7f, 1.1f));
	public static final RegistryObject<EntityType<FakeOffspringEntity>> FAKE_OFFSPRING = register("fake_offspring",
			EntityType.Builder.<FakeOffspringEntity>of(FakeOffspringEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new FakeOffspringEntity(level))

					.sized(0.6f, 0.6f));
	public static final RegistryObject<EntityType<FleefishBulletEntity>> FLEEFISH_BULLET = register("fleefish_bullet",
			EntityType.Builder.<FleefishBulletEntity>of(FleefishBulletEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new FleefishBulletEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.4f, 0.4f));
	public static final RegistryObject<EntityType<FleeFishEntity>> FLEE_FISH = register("flee_fish",
			EntityType.Builder.<FleeFishEntity>of(FleeFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new FleeFishEntity(level))

					.sized(0.8f, 1.1f));
	public static final RegistryObject<EntityType<RouteShaperEntity>> ROUTE_SHAPER = register("route_shaper",
			EntityType.Builder.<RouteShaperEntity>of(RouteShaperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new RouteShaperEntity(level))

					.sized(1.8f, 4f));
	public static final RegistryObject<EntityType<RouteFractalEntity>> ROUTE_FRACTAL = register("route_fractal",
			EntityType.Builder.<RouteFractalEntity>of(RouteFractalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new RouteFractalEntity(level))

					.sized(0.7f, 1.5f));
	public static final RegistryObject<EntityType<TellerShotEntity>> TELLER_SHOT = register("teller_shot",
			EntityType.Builder.<TellerShotEntity>of(TellerShotEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new TellerShotEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.4f, 0.4f));
	public static final RegistryObject<EntityType<FirstTellerEntity>> FIRST_TO_TALK = register("first_to_talk",
			EntityType.Builder.<FirstTellerEntity>of(FirstTellerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new FirstTellerEntity(level))

					.sized(0.9f, 2.7f));
	public static final RegistryObject<EntityType<ReaperPetEntity>> REAPER_PET = register("reaper_pet",
			EntityType.Builder.<ReaperPetEntity>of(ReaperPetEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ReaperPetEntity(level))

					.sized(0.6f, 1.3f));
	public static final RegistryObject<EntityType<BishopFishEntity>> BISHOP_FISH = register("bishop_fish",
			EntityType.Builder.<BishopFishEntity>of(BishopFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(18).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new BishopFishEntity(level))

					.sized(1.4f, 2.2f));
	public static final RegistryObject<EntityType<TideBishopEntity>> TIDE_BISHOP = register("tide_bishop",
			EntityType.Builder.<TideBishopEntity>of(TideBishopEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new TideBishopEntity(level))

					.sized(1.1f, 2.2f));
	public static final RegistryObject<EntityType<SonsEntity>> SONS = register("sons",
			EntityType.Builder.<SonsEntity>of(SonsEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SonsEntity(level))

					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<FloaterProkaryoteEntity>> FLOATER_PROKARYOTE = register("floater_prokaryote",
			EntityType.Builder.<FloaterProkaryoteEntity>of(FloaterProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new FloaterProkaryoteEntity(level))

					.sized(0.6f, 1.5f));
	public static final RegistryObject<EntityType<ChitinGolemEntity>> CHITIN_GOLEM = register("chitin_golem",
			EntityType.Builder.<ChitinGolemEntity>of(ChitinGolemEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ChitinGolemEntity(level))

					.sized(1.75f, 4f));
	public static final RegistryObject<EntityType<TideDeathrepellerEntity>> TIDE_DEATHREPELLER = register("tide_deathrepeller",
			EntityType.Builder.<TideDeathrepellerEntity>of(TideDeathrepellerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new TideDeathrepellerEntity(level))

					.sized(1.5f, 2f));
	public static final RegistryObject<EntityType<MegaChestEntity>> MEGA_CHEST = register("mega_chest",
			EntityType.Builder.<MegaChestEntity>of(MegaChestEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(14).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new MegaChestEntity(level))

					.sized(0.8f, 0.9f));
	public static final RegistryObject<EntityType<ApostleProkaryoteEntity>> APOSTLE_PROKARYOTE = register("apostle_prokaryote",
			EntityType.Builder.<ApostleProkaryoteEntity>of(ApostleProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ApostleProkaryoteEntity(level))

					.sized(0.6f, 1.7f));
	public static final RegistryObject<EntityType<HighmoreShootEntity>> HIGHMORE_SHOOT = register("highmore_shoot",
			EntityType.Builder.<HighmoreShootEntity>of(HighmoreShootEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new HighmoreShootEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.4f, 0.4f));
	public static final RegistryObject<EntityType<HighmoreEntity>> HIGHMORE = register("highmore",
			EntityType.Builder.<HighmoreEntity>of(HighmoreEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(18).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new HighmoreEntity(level))

					.sized(1.4f, 1.6f));
	public static final RegistryObject<EntityType<AccumulatorProkaryoteEntity>> ACCUMULATOR_PROKARYOTE = register("accumulator_prokaryote",
			EntityType.Builder.<AccumulatorProkaryoteEntity>of(AccumulatorProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
					.setCustomClientFactory((spawnEntity, level) -> new AccumulatorProkaryoteEntity(level))

					.sized(0.5f, 1f));
	public static final RegistryObject<EntityType<AccumulatorCloneEntity>> ACCUMULATOR_CLONE = register("accumulator_clone",
			EntityType.Builder.<AccumulatorCloneEntity>of(AccumulatorCloneEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new AccumulatorCloneEntity(level))

					.sized(0.5f, 1f));
	public static final RegistryObject<EntityType<FeederProkaryoteEntity>> FEEDER_PROKARYOTE = register("feeder_prokaryote",
			EntityType.Builder.<FeederProkaryoteEntity>of(FeederProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new FeederProkaryoteEntity(level))

					.sized(0.8f, 1.1f));
	public static final RegistryObject<EntityType<ChestFishEntity>> CHEST_FISH = register("chest_fish",
			EntityType.Builder.<ChestFishEntity>of(ChestFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ChestFishEntity(level))

					.sized(0.9f, 0.9f));
	public static final RegistryObject<EntityType<SpikeChestEntity>> SPIKE_CHEST = register("spike_chest",
			EntityType.Builder.<SpikeChestEntity>of(SpikeChestEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SpikeChestEntity(level))

					.sized(0.9f, 0.9f));
	public static final RegistryObject<EntityType<SkadiEntity>> SKADI = register("skadi",
			EntityType.Builder.<SkadiEntity>of(SkadiEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SkadiEntity(level))

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<DepositerProkaryoteEntity>> DEPOSITER_PROKARYOTE = register("depositer_prokaryote",
			EntityType.Builder.<DepositerProkaryoteEntity>of(DepositerProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
					.setCustomClientFactory((spawnEntity, level) -> new DepositerProkaryoteEntity(level))

					.sized(0.625f, 1f));
	public static final RegistryObject<EntityType<OceanizedVillagerEntity>> OCEANIZED_VILLAGER = register("oceanized_villager",
			EntityType.Builder.<OceanizedVillagerEntity>of(OceanizedVillagerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedVillagerEntity(level))

					.sized(0.6f, 2f));
	public static final RegistryObject<EntityType<OceanizedVindicatorEntity>> OCEANIZED_VINDICATOR = register("oceanized_vindicator",
			EntityType.Builder.<OceanizedVindicatorEntity>of(OceanizedVindicatorEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedVindicatorEntity(level))

					.sized(0.6f, 2f));
	public static final RegistryObject<EntityType<ShotOceanArrowEntity>> SHOT_OCEAN_ARROW = register("shot_ocean_arrow",
			EntityType.Builder.<ShotOceanArrowEntity>of(ShotOceanArrowEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new ShotOceanArrowEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<OceanizedPillagerEntity>> OCEANIZED_PILLAGER = register("oceanized_pillager",
			EntityType.Builder.<OceanizedPillagerEntity>of(OceanizedPillagerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedPillagerEntity(level))

					.sized(0.6f, 1.9f));
	public static final RegistryObject<EntityType<AnchorFlyEntity>> ANCHOR_FLY = register("anchor_fly",
			EntityType.Builder.<AnchorFlyEntity>of(AnchorFlyEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new AnchorFlyEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.2f, 0.2f));
	public static final RegistryObject<EntityType<OceanizedPigEntity>> OCEANIZED_PIG = register("oceanized_pig",
			EntityType.Builder.<OceanizedPigEntity>of(OceanizedPigEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedPigEntity(level))

					.sized(0.6f, 1f));
	public static final RegistryObject<EntityType<OceanizedCowEntity>> OCEANIZED_COW = register("oceanized_cow",
			EntityType.Builder.<OceanizedCowEntity>of(OceanizedCowEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedCowEntity(level))

					.sized(0.6f, 1.375f));
	public static final RegistryObject<EntityType<OceanizedSheepEntity>> OCEANIZED_SHEEP = register("oceanized_sheep",
			EntityType.Builder.<OceanizedSheepEntity>of(OceanizedSheepEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedSheepEntity(level))

					.sized(0.6f, 1f));
	public static final RegistryObject<EntityType<OceanizedHorseEntity>> OCEANIZED_HORSE = register("oceanized_horse",
			EntityType.Builder.<OceanizedHorseEntity>of(OceanizedHorseEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedHorseEntity(level))

					.sized(1.25f, 1.8f));
	public static final RegistryObject<EntityType<OceanizedPiglinEntity>> OCEANIZED_PIGLIN = register("oceanized_piglin", EntityType.Builder.<OceanizedPiglinEntity>of(OceanizedPiglinEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedPiglinEntity(level)).fireImmune().sized(0.6f, 2f));
	public static final RegistryObject<EntityType<OceanizedBruteEntity>> OCEANIZED_BRUTE = register("oceanized_brute", EntityType.Builder.<OceanizedBruteEntity>of(OceanizedBruteEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(10).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedBruteEntity(level)).fireImmune().sized(0.6f, 2f));
	public static final RegistryObject<EntityType<OceanizedSpiderEntity>> OCEANIZED_SPIDER = register("oceanized_spider",
			EntityType.Builder.<OceanizedSpiderEntity>of(OceanizedSpiderEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(7).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedSpiderEntity(level))

					.sized(0.9f, 0.7f));
	public static final RegistryObject<EntityType<OceanizedEndermanEntity>> OCEANIZED_ENDERMAN = register("oceanized_enderman",
			EntityType.Builder.<OceanizedEndermanEntity>of(OceanizedEndermanEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(10).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedEndermanEntity(level))

					.sized(0.6f, 3f));
	public static final RegistryObject<EntityType<OceanizedWolfEntity>> OCEANIZED_WOLF = register("oceanized_wolf",
			EntityType.Builder.<OceanizedWolfEntity>of(OceanizedWolfEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(7).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedWolfEntity(level))

					.sized(0.7f, 0.8f));
	public static final RegistryObject<EntityType<OceanizedDogEntity>> OCEANIZED_DOG = register("oceanized_dog",
			EntityType.Builder.<OceanizedDogEntity>of(OceanizedDogEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedDogEntity(level))

					.sized(0.7f, 0.8f));
	public static final RegistryObject<EntityType<OceanizedRavagerEntity>> OCEANIZED_RAVAGER = register("oceanized_ravager",
			EntityType.Builder.<OceanizedRavagerEntity>of(OceanizedRavagerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(10).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedRavagerEntity(level))

					.sized(1.9f, 2.5f));
	public static final RegistryObject<EntityType<OceanziedWitchEntity>> OCEANIZED_WITCH = register("oceanized_witch",
			EntityType.Builder.<OceanziedWitchEntity>of(OceanziedWitchEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanziedWitchEntity(level))

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<ThrowablePotionEntity>> THROWABLE_POTION = register("throwable_potion", EntityType.Builder.<ThrowablePotionEntity>of(ThrowablePotionEntity::new, MobCategory.MISC)
			.setCustomClientFactory((spawnEntity, level) -> new ThrowablePotionEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.2f, 0.2f));
	public static final RegistryObject<EntityType<IzumikOffspringEntity>> IZUMIK_OFFSPRING = register("izumik_offspring",
			EntityType.Builder.<IzumikOffspringEntity>of(IzumikOffspringEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new IzumikOffspringEntity(level))

					.sized(0.5f, 0.9f));
	public static final RegistryObject<EntityType<IzumikEntity>> IZUMIK = register("izumik",
			EntityType.Builder.<IzumikEntity>of(IzumikEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(21).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new IzumikEntity(level)).fireImmune().sized(3.3f, 9f));
	public static final RegistryObject<EntityType<DivicellularGoEntity>> DIVICELLULAR_GO = register("divicellular_go",
			EntityType.Builder.<DivicellularGoEntity>of(DivicellularGoEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new DivicellularGoEntity(level))

					.sized(0.5f, 1f));
	public static final RegistryObject<EntityType<OceanizedEvokerEntity>> OCEANIZED_EVOKER = register("oceanized_evoker",
			EntityType.Builder.<OceanizedEvokerEntity>of(OceanizedEvokerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedEvokerEntity(level))

					.sized(0.6f, 1.9f));
	public static final RegistryObject<EntityType<JuniorWarriorPriestEntity>> JUNIOR_WARRIOR_PRIEST = register("junior_warrior_priest",
			EntityType.Builder.<JuniorWarriorPriestEntity>of(JuniorWarriorPriestEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new JuniorWarriorPriestEntity(level))

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<WarriorPriestEntity>> WARRIOR_PRIEST = register("warrior_priest",
			EntityType.Builder.<WarriorPriestEntity>of(WarriorPriestEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new WarriorPriestEntity(level))

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<CorrectionalPhalanxyInfantryEntity>> CORRECTIONAL_PHALANXY_INFANTRY = register("correctional_phalanxy_infantry",
			EntityType.Builder.<CorrectionalPhalanxyInfantryEntity>of(CorrectionalPhalanxyInfantryEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
					.setCustomClientFactory((spawnEntity, level) -> new CorrectionalPhalanxyInfantryEntity(level))

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<CorrectinalPhalaxVanguardEntity>> CORRECTIONAL_PHALAX_VANGUARD = register("correctional_phalax_vanguard",
			EntityType.Builder.<CorrectinalPhalaxVanguardEntity>of(CorrectinalPhalaxVanguardEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)
					.setCustomClientFactory((spawnEntity, level) -> new CorrectinalPhalaxVanguardEntity(level))

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<TribunalHealerEntity>> TRIBUNAL_HEALER = register("tribunal_healer",
			EntityType.Builder.<TribunalHealerEntity>of(TribunalHealerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new TribunalHealerEntity(level))

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<HealBullletEntity>> HEAL_BULLLET = register("heal_bulllet",
			EntityType.Builder.<HealBullletEntity>of(HealBullletEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new HealBullletEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.1f, 0.1f));
	public static final RegistryObject<EntityType<MartusEntity>> MARTUS = register("martus",
			EntityType.Builder.<MartusEntity>of(MartusEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new MartusEntity(level)).fireImmune().sized(0.7f, 2.8f));
	public static final RegistryObject<EntityType<TheAbandonedEntity>> THE_ABANDONED = register("the_abandoned",
			EntityType.Builder.<TheAbandonedEntity>of(TheAbandonedEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new TheAbandonedEntity(level))

					.sized(0.7f, 2.25f));
	public static final RegistryObject<EntityType<AbandonedShootEntity>> ABANDONED_SHOOT = register("abandoned_shoot",
			EntityType.Builder.<AbandonedShootEntity>of(AbandonedShootEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new AbandonedShootEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
	public static final RegistryObject<EntityType<GunmuEntity>> GUNMU = register("gunmu",
			EntityType.Builder.<GunmuEntity>of(GunmuEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new GunmuEntity(level)).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<OceanizedWardenEntity>> OCEANIZED_WARDEN = register("oceanized_warden", EntityType.Builder.<OceanizedWardenEntity>of(OceanizedWardenEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedWardenEntity(level)).fireImmune().sized(1.2f, 3.1f));
	public static final RegistryObject<EntityType<OceanizedCatEntity>> OCEANIZED_CAT = register("oceanized_cat",
			EntityType.Builder.<OceanizedCatEntity>of(OceanizedCatEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedCatEntity(level))

					.sized(0.5f, 0.625f));
	public static final RegistryObject<EntityType<SuperBigCatEntity>> SUPER_BIG_CAT = register("super_big_cat",
			EntityType.Builder.<SuperBigCatEntity>of(SuperBigCatEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SuperBigCatEntity(level))

					.sized(3.5f, 4f));
	public static final RegistryObject<EntityType<ComplexChitinGolemEntity>> COMPLEX_CHITIN_GOLEM = register("complex_chitin_golem", EntityType.Builder.<ComplexChitinGolemEntity>of(ComplexChitinGolemEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ComplexChitinGolemEntity(level)).fireImmune().sized(1.75f, 4f));
	public static final RegistryObject<EntityType<OceanizedWardenisEntity>> OCEANIZED_WARDENIS = register("oceanized_wardenis", EntityType.Builder.<OceanizedWardenisEntity>of(OceanizedWardenisEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedWardenisEntity(level)).fireImmune().sized(0.6f, 1.85f));
	public static final RegistryObject<EntityType<NucleicMaleficentEntity>> NUCLEIC_MALEFICENT = register("nucleic_maleficent",
			EntityType.Builder.<NucleicMaleficentEntity>of(NucleicMaleficentEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new NucleicMaleficentEntity(level))

					.sized(0.7f, 1.5f));
	public static final RegistryObject<EntityType<OceanizedWitherEntity>> OCEANIZED_WITHER = register("oceanized_wither", EntityType.Builder.<OceanizedWitherEntity>of(OceanizedWitherEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedWitherEntity(level)).fireImmune().sized(1.1f, 3.5f));
	public static final RegistryObject<EntityType<WitherShootPreEntity>> WITHER_SHOOT_PRE = register("wither_shoot_pre",
			EntityType.Builder.<WitherShootPreEntity>of(WitherShootPreEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new WitherShootPreEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.1f, 0.1f));
	public static final RegistryObject<EntityType<OceannizedWitheriaEntity>> OCEANIZED_WITHERIA = register("oceanized_witheria", EntityType.Builder.<OceannizedWitheriaEntity>of(OceannizedWitheriaEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceannizedWitheriaEntity(level)).fireImmune().sized(0.7f, 2.5f));
	public static final RegistryObject<EntityType<TheLastKnightEntity>> THE_LAST_KNIGHT = register("the_last_knight", EntityType.Builder.<TheLastKnightEntity>of(TheLastKnightEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new TheLastKnightEntity(level)).fireImmune().sized(1f, 3.6f));
	public static final RegistryObject<EntityType<LastKnightAndHorseEntity>> LAST_KNIGHT_AND_HORSE = register("last_knight_and_horse", EntityType.Builder.<LastKnightAndHorseEntity>of(LastKnightAndHorseEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new LastKnightAndHorseEntity(level)).fireImmune().sized(1.2f, 4f));
	public static final RegistryObject<EntityType<RocinanteEntity>> ROCINANTE = register("rocinante",
			EntityType.Builder.<RocinanteEntity>of(RocinanteEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new RocinanteEntity(level))

					.sized(1.2f, 3f));
	public static final RegistryObject<EntityType<ApocataEntity>> APOCATA = register("apocata",
			EntityType.Builder.<ApocataEntity>of(ApocataEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ApocataEntity(level))

					.sized(0.6f, 1.85f));
	public static final RegistryObject<EntityType<OceanizedFoxEntity>> OCEANIZED_FOX = register("oceanized_fox",
			EntityType.Builder.<OceanizedFoxEntity>of(OceanizedFoxEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedFoxEntity(level))

					.sized(0.6f, 0.7f));
	public static final RegistryObject<EntityType<TidutantExcrescenceEntity>> TIDUTANT_EXCRESCENCE = register("tidutant_excrescence",
			EntityType.Builder.<TidutantExcrescenceEntity>of(TidutantExcrescenceEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new TidutantExcrescenceEntity(level))

					.sized(0.5f, 0.4f));
	public static final RegistryObject<EntityType<OceanizedPolarBearEntity>> OCEANIZED_POLAR_BEAR = register("oceanized_polar_bear",
			EntityType.Builder.<OceanizedPolarBearEntity>of(OceanizedPolarBearEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedPolarBearEntity(level))

					.sized(1f, 1.25f));
	public static final RegistryObject<EntityType<TideutantRockSpiderEntity>> TIDUTANT_ROCK_SPIDER = register("tidutant_rock_spider",
			EntityType.Builder.<TideutantRockSpiderEntity>of(TideutantRockSpiderEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new TideutantRockSpiderEntity(level))

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<EndspeakerEntity>> ENDSPEAKER = register("endspeaker",
			EntityType.Builder.<EndspeakerEntity>of(EndspeakerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(14).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new EndspeakerEntity(level))

					.sized(1f, 3.375f));
	public static final RegistryObject<EntityType<LineringPathshaperEntity>> LINGERING_PATHSHAPER = register("lingering_pathshaper",
			EntityType.Builder.<LineringPathshaperEntity>of(LineringPathshaperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new LineringPathshaperEntity(level))

					.sized(1.8f, 4f));
	public static final RegistryObject<EntityType<LingeringFractalEntity>> LINGERING_FRACTAL = register("lingering_fractal",
			EntityType.Builder.<LingeringFractalEntity>of(LingeringFractalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new LingeringFractalEntity(level))

					.sized(0.7f, 1.5f));
	public static final RegistryObject<EntityType<LittleHelperEntity>> LITTLE_HELPER = register("little_helper", EntityType.Builder.<LittleHelperEntity>of(LittleHelperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new LittleHelperEntity(level)).fireImmune().sized(0.875f, 0.2f));
	public static final RegistryObject<EntityType<Al1SHelperEntity>> AL_1_S_HELPER = register("al_1_s_helper", EntityType.Builder.<Al1SHelperEntity>of(Al1SHelperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new Al1SHelperEntity(level)).fireImmune().sized(0.875f, 0.2f));
	public static final RegistryObject<EntityType<UlpiansEntity>> ULPIANS = register("ulpians",
			EntityType.Builder.<UlpiansEntity>of(UlpiansEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new UlpiansEntity(level))

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<GladiiaEntity>> GLADIIA = register("gladiia",
			EntityType.Builder.<GladiiaEntity>of(GladiiaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new GladiiaEntity(level))

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<GladiiaWhirlEntity>> GLADIIA_WHIRL = register("gladiia_whirl",
			EntityType.Builder.<GladiiaWhirlEntity>of(GladiiaWhirlEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new GladiiaWhirlEntity(level))

					.sized(2f, 2f));
	public static final RegistryObject<EntityType<SpecterEntity>> SPECTER = register("specter",
			EntityType.Builder.<SpecterEntity>of(SpecterEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SpecterEntity(level))

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<SpecterDollEntity>> SPECTER_DOLL = register("specter_doll",
			EntityType.Builder.<SpecterDollEntity>of(SpecterDollEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SpecterDollEntity(level))

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<IreneEntity>> IRENE = register("irene",
			EntityType.Builder.<IreneEntity>of(IreneEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new IreneEntity(level))

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<TideChimeraEntity>> TIDE_CHIMERA = register("tide_chimera",
			EntityType.Builder.<TideChimeraEntity>of(TideChimeraEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new TideChimeraEntity(level))

					.sized(1.8f, 4.65f));
	public static final RegistryObject<EntityType<SkadiCorruptedEntity>> SKADI_CORRUPTED = register("skadi_corrupted", EntityType.Builder.<SkadiCorruptedEntity>of(SkadiCorruptedEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SkadiCorruptedEntity(level)).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<OceanizeRabbitEntity>> OCEANIZE_RABBIT = register("oceanize_rabbit",
			EntityType.Builder.<OceanizeRabbitEntity>of(OceanizeRabbitEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizeRabbitEntity(level))

					.sized(0.4f, 0.5f));
	public static final RegistryObject<EntityType<SaintCarmenEntity>> SAINT_CARMEN = register("saint_carmen",
			EntityType.Builder.<SaintCarmenEntity>of(SaintCarmenEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SaintCarmenEntity(level))

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<CarmenBulletEntity>> CARMEN_BULLET = register("carmen_bullet",
			EntityType.Builder.<CarmenBulletEntity>of(CarmenBulletEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new CarmenBulletEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
	public static final RegistryObject<EntityType<OceanizedIllusionerEntity>> OCEANIZED_ILLUSIONER = register("oceanized_illusioner",
			EntityType.Builder.<OceanizedIllusionerEntity>of(OceanizedIllusionerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(10).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedIllusionerEntity(level))

					.sized(0.6f, 1.9f));
	public static final RegistryObject<EntityType<OceanIllusionEntity>> OCEAN_ILLUSION = register("ocean_illusion",
			EntityType.Builder.<OceanIllusionEntity>of(OceanIllusionEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanIllusionEntity(level))

					.sized(0.6f, 1.9f));
	public static final RegistryObject<EntityType<FlamarineStatueEntity>> FLAMARINE_STATUE = register("flamarine_statue", EntityType.Builder.<FlamarineStatueEntity>of(FlamarineStatueEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new FlamarineStatueEntity(level)).fireImmune().sized(0.7f, 2f));
	public static final RegistryObject<EntityType<NautilusHeadhunterEntity>> NAUTILUS_HEADHUNTER = register("nautilus_headhunter",
			EntityType.Builder.<NautilusHeadhunterEntity>of(NautilusHeadhunterEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3)
					.setCustomClientFactory((spawnEntity, level) -> new NautilusHeadhunterEntity(level))

					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<XantisEntity>> XANTIS = register("xantis",
			EntityType.Builder.<XantisEntity>of(XantisEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new XantisEntity(level))

					.sized(0.5f, 0.68f));
	public static final RegistryObject<EntityType<FlamarineGolemEntity>> FLAMARINE_GOLEM = register("flamarine_golem", EntityType.Builder.<FlamarineGolemEntity>of(FlamarineGolemEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new FlamarineGolemEntity(level)).fireImmune().sized(1f, 2.75f));
	public static final RegistryObject<EntityType<OceanizedVexEntity>> OCEANIZED_VEX = register("oceanized_vex",
			EntityType.Builder.<OceanizedVexEntity>of(OceanizedVexEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedVexEntity(level))

					.sized(0.4f, 0.8f));
	public static final RegistryObject<EntityType<IsharmlaEntity>> ISHARMLA = register("isharmla",
			EntityType.Builder.<IsharmlaEntity>of(IsharmlaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new IsharmlaEntity(level))

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<QunyouWantedIsharmlaEntity>> QUNYOU_WANTED_ISHARMLA = register("qunyou_wanted_isharmla",
			EntityType.Builder.<QunyouWantedIsharmlaEntity>of(QunyouWantedIsharmlaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3)
					.setCustomClientFactory((spawnEntity, level) -> new QunyouWantedIsharmlaEntity(level))

					.sized(20f, 20f));
	public static final RegistryObject<EntityType<IsharmlaTearEntity>> ISHARMLA_TEAR = register("isharmla_tear",
			EntityType.Builder.<IsharmlaTearEntity>of(IsharmlaTearEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new IsharmlaTearEntity(level))

					.sized(1f, 0.5f));
	public static final RegistryObject<EntityType<PrayerSplashEntity>> PRAYER_SPLASH = register("prayer_splash",
			EntityType.Builder.<PrayerSplashEntity>of(PrayerSplashEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new PrayerSplashEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
	public static final RegistryObject<EntityType<CompassionPrayerEntity>> COMPASSION_PRAYER = register("compassion_prayer",
			EntityType.Builder.<CompassionPrayerEntity>of(CompassionPrayerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new CompassionPrayerEntity(level))

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<OceanizedEnderinaEntity>> OCEANIZED_ENDERINA = register("oceanized_enderina",
			EntityType.Builder.<OceanizedEnderinaEntity>of(OceanizedEnderinaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedEnderinaEntity(level))

					.sized(0.75f, 1.95f));
	public static final RegistryObject<EntityType<MoistDragonBreathEntity>> MOIST_DRAGON_BREATH = register("moist_dragon_breath",
			EntityType.Builder.<MoistDragonBreathEntity>of(MoistDragonBreathEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new MoistDragonBreathEntity(level))

					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<MoistEnderCrystalEntity>> MOIST_ENDER_CRYSTAL = register("moist_ender_crystal",
			EntityType.Builder.<MoistEnderCrystalEntity>of(MoistEnderCrystalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new MoistEnderCrystalEntity(level))

					.sized(2f, 2f));
	public static final RegistryObject<EntityType<ThirsterEntity>> THIRSTER = register("thirster",
			EntityType.Builder.<ThirsterEntity>of(ThirsterEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ThirsterEntity(level))

					.sized(1.5f, 1.75f));
	public static final RegistryObject<EntityType<AbsorberLimbEntity>> ABSORBER_LIMB = register("absorber_limb",
			EntityType.Builder.<AbsorberLimbEntity>of(AbsorberLimbEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new AbsorberLimbEntity(level))

					.sized(0.4f, 1f));
	public static final RegistryObject<EntityType<ScreamChestFishEntity>> SCREAM_CHEST_FISH = register("scream_chest_fish",
			EntityType.Builder.<ScreamChestFishEntity>of(ScreamChestFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ScreamChestFishEntity(level))

					.sized(0.9f, 0.9f));
	public static final RegistryObject<EntityType<OceanizedChickenEntity>> OCEANIZED_CHICKEN = register("oceanized_chicken",
			EntityType.Builder.<OceanizedChickenEntity>of(OceanizedChickenEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedChickenEntity(level))

					.sized(0.5f, 0.875f));
	public static final RegistryObject<EntityType<NetherseaSlimeEntity>> NETHERSEA_SLIME = register("nethersea_slime",
			EntityType.Builder.<NetherseaSlimeEntity>of(NetherseaSlimeEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new NetherseaSlimeEntity(level))

					.sized(1f, 1f));
	public static final RegistryObject<EntityType<OceanizedShulkerEntity>> OCEANIZED_SHULKER = register("oceanized_shulker",
			EntityType.Builder.<OceanizedShulkerEntity>of(OceanizedShulkerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedShulkerEntity(level))

					.sized(1f, 1f));

	// Start of user code block custom entities
	// End of user code block custom entities
	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			registerSpawnPlacements();
			registerDungeonMobs();
		});
	}

	private static void registerSpawnPlacements() {
		RunFishEntity.registerSpawnPlacements();
		SliderFishEntity.registerSpawnPlacements();
		ShooterFishEntity.registerSpawnPlacements();
		FlyFishEntity.registerSpawnPlacements();
		ReaperFishEntity.registerSpawnPlacements();
		CreeperFishEntity.registerSpawnPlacements();
		PunctureFishEntity.registerSpawnPlacements();
		BaselayerAbyssalEntity.registerSpawnPlacements();
		PredatorAbyssalEntity.registerSpawnPlacements();
		GuideAbyssalEntity.registerSpawnPlacements();
		SplasherAbyssalEntity.registerSpawnPlacements();
		UmbrellaAbyssalEntity.registerSpawnPlacements();
		CrackerAbyssalEntity.registerSpawnPlacements();
		CollectorProkaryoteEntity.registerSpawnPlacements();
		BoneFishEntity.registerSpawnPlacements();
		ChiselerFishEntity.registerSpawnPlacements();
		PregnantFishEntity.registerSpawnPlacements();
		FleeFishEntity.registerSpawnPlacements();
		FirstTellerEntity.registerSpawnPlacements();
		FloaterProkaryoteEntity.registerSpawnPlacements();
		ApostleProkaryoteEntity.registerSpawnPlacements();
		AccumulatorProkaryoteEntity.registerSpawnPlacements();
		FeederProkaryoteEntity.registerSpawnPlacements();
		ChestFishEntity.registerSpawnPlacements();
		SpikeChestEntity.registerSpawnPlacements();
		DepositerProkaryoteEntity.registerSpawnPlacements();
		IzumikOffspringEntity.registerSpawnPlacements();
		NucleicMaleficentEntity.registerSpawnPlacements();
		NautilusHeadhunterEntity.registerSpawnPlacements();
	}

	private static void registerDungeonMobs() {
		SliderFishEntity.registerDungeonMob();
		BaselayerAbyssalEntity.registerDungeonMob();
		SplasherAbyssalEntity.registerDungeonMob();
		UmbrellaAbyssalEntity.registerDungeonMob();
		FleeFishEntity.registerDungeonMob();
		NetherseaSlimeEntity.registerDungeonMob();
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(RUN_FISH.get(), RunFishEntity.createAttributes().build());
		event.put(SLIDER_FISH.get(), SliderFishEntity.createAttributes().build());
		event.put(SUPER_SLIDER.get(), SuperSliderEntity.createAttributes().build());
		event.put(SHOOTER_FISH.get(), ShooterFishEntity.createAttributes().build());
		event.put(FLY_FISH.get(), FlyFishEntity.createAttributes().build());
		event.put(REAPER_FISH.get(), ReaperFishEntity.createAttributes().build());
		event.put(CREEPER_FISH.get(), CreeperFishEntity.createAttributes().build());
		event.put(PUNCTURE_FISH.get(), PunctureFishEntity.createAttributes().build());
		event.put(BASELAYER_ABYSSAL.get(), BaselayerAbyssalEntity.createAttributes().build());
		event.put(PREDATOR_ABYSSAL.get(), PredatorAbyssalEntity.createAttributes().build());
		event.put(GUIDE_ABYSSAL.get(), GuideAbyssalEntity.createAttributes().build());
		event.put(SPLASHER_ABYSSAL.get(), SplasherAbyssalEntity.createAttributes().build());
		event.put(UMBRELLA_ABYSSAL.get(), UmbrellaAbyssalEntity.createAttributes().build());
		event.put(CRACKER_ABYSSAL.get(), CrackerAbyssalEntity.createAttributes().build());
		event.put(COLLECTOR_PROKARYOTE.get(), CollectorProkaryoteEntity.createAttributes().build());
		event.put(BONE_FISH.get(), BoneFishEntity.createAttributes().build());
		event.put(CHISELER_FISH.get(), ChiselerFishEntity.createAttributes().build());
		event.put(PREGNANT_FISH.get(), PregnantFishEntity.createAttributes().build());
		event.put(FAKE_OFFSPRING.get(), FakeOffspringEntity.createAttributes().build());
		event.put(FLEE_FISH.get(), FleeFishEntity.createAttributes().build());
		event.put(ROUTE_SHAPER.get(), RouteShaperEntity.createAttributes().build());
		event.put(ROUTE_FRACTAL.get(), RouteFractalEntity.createAttributes().build());
		event.put(FIRST_TO_TALK.get(), FirstTellerEntity.createAttributes().build());
		event.put(REAPER_PET.get(), ReaperPetEntity.createAttributes().build());
		event.put(BISHOP_FISH.get(), BishopFishEntity.createAttributes().build());
		event.put(TIDE_BISHOP.get(), TideBishopEntity.createAttributes().build());
		event.put(SONS.get(), SonsEntity.createAttributes().build());
		event.put(FLOATER_PROKARYOTE.get(), FloaterProkaryoteEntity.createAttributes().build());
		event.put(CHITIN_GOLEM.get(), ChitinGolemEntity.createAttributes().build());
		event.put(TIDE_DEATHREPELLER.get(), TideDeathrepellerEntity.createAttributes().build());
		event.put(MEGA_CHEST.get(), MegaChestEntity.createAttributes().build());
		event.put(APOSTLE_PROKARYOTE.get(), ApostleProkaryoteEntity.createAttributes().build());
		event.put(HIGHMORE.get(), HighmoreEntity.createAttributes().build());
		event.put(ACCUMULATOR_PROKARYOTE.get(), AccumulatorProkaryoteEntity.createAttributes().build());
		event.put(ACCUMULATOR_CLONE.get(), AccumulatorCloneEntity.createAttributes().build());
		event.put(FEEDER_PROKARYOTE.get(), FeederProkaryoteEntity.createAttributes().build());
		event.put(CHEST_FISH.get(), ChestFishEntity.createAttributes().build());
		event.put(SPIKE_CHEST.get(), SpikeChestEntity.createAttributes().build());
		event.put(SKADI.get(), SkadiEntity.createAttributes().build());
		event.put(DEPOSITER_PROKARYOTE.get(), DepositerProkaryoteEntity.createAttributes().build());
		event.put(OCEANIZED_VILLAGER.get(), OceanizedVillagerEntity.createAttributes().build());
		event.put(OCEANIZED_VINDICATOR.get(), OceanizedVindicatorEntity.createAttributes().build());
		event.put(OCEANIZED_PILLAGER.get(), OceanizedPillagerEntity.createAttributes().build());
		event.put(OCEANIZED_PIG.get(), OceanizedPigEntity.createAttributes().build());
		event.put(OCEANIZED_COW.get(), OceanizedCowEntity.createAttributes().build());
		event.put(OCEANIZED_SHEEP.get(), OceanizedSheepEntity.createAttributes().build());
		event.put(OCEANIZED_HORSE.get(), OceanizedHorseEntity.createAttributes().build());
		event.put(OCEANIZED_PIGLIN.get(), OceanizedPiglinEntity.createAttributes().build());
		event.put(OCEANIZED_BRUTE.get(), OceanizedBruteEntity.createAttributes().build());
		event.put(OCEANIZED_SPIDER.get(), OceanizedSpiderEntity.createAttributes().build());
		event.put(OCEANIZED_ENDERMAN.get(), OceanizedEndermanEntity.createAttributes().build());
		event.put(OCEANIZED_WOLF.get(), OceanizedWolfEntity.createAttributes().build());
		event.put(OCEANIZED_DOG.get(), OceanizedDogEntity.createAttributes().build());
		event.put(OCEANIZED_RAVAGER.get(), OceanizedRavagerEntity.createAttributes().build());
		event.put(OCEANIZED_WITCH.get(), OceanziedWitchEntity.createAttributes().build());
		event.put(IZUMIK_OFFSPRING.get(), IzumikOffspringEntity.createAttributes().build());
		event.put(IZUMIK.get(), IzumikEntity.createAttributes().build());
		event.put(DIVICELLULAR_GO.get(), DivicellularGoEntity.createAttributes().build());
		event.put(OCEANIZED_EVOKER.get(), OceanizedEvokerEntity.createAttributes().build());
		event.put(JUNIOR_WARRIOR_PRIEST.get(), JuniorWarriorPriestEntity.createAttributes().build());
		event.put(WARRIOR_PRIEST.get(), WarriorPriestEntity.createAttributes().build());
		event.put(CORRECTIONAL_PHALANXY_INFANTRY.get(), CorrectionalPhalanxyInfantryEntity.createAttributes().build());
		event.put(CORRECTIONAL_PHALAX_VANGUARD.get(), CorrectinalPhalaxVanguardEntity.createAttributes().build());
		event.put(TRIBUNAL_HEALER.get(), TribunalHealerEntity.createAttributes().build());
		event.put(MARTUS.get(), MartusEntity.createAttributes().build());
		event.put(THE_ABANDONED.get(), TheAbandonedEntity.createAttributes().build());
		event.put(GUNMU.get(), GunmuEntity.createAttributes().build());
		event.put(OCEANIZED_WARDEN.get(), OceanizedWardenEntity.createAttributes().build());
		event.put(OCEANIZED_CAT.get(), OceanizedCatEntity.createAttributes().build());
		event.put(SUPER_BIG_CAT.get(), SuperBigCatEntity.createAttributes().build());
		event.put(COMPLEX_CHITIN_GOLEM.get(), ComplexChitinGolemEntity.createAttributes().build());
		event.put(OCEANIZED_WARDENIS.get(), OceanizedWardenisEntity.createAttributes().build());
		event.put(NUCLEIC_MALEFICENT.get(), NucleicMaleficentEntity.createAttributes().build());
		event.put(OCEANIZED_WITHER.get(), OceanizedWitherEntity.createAttributes().build());
		event.put(OCEANIZED_WITHERIA.get(), OceannizedWitheriaEntity.createAttributes().build());
		event.put(THE_LAST_KNIGHT.get(), TheLastKnightEntity.createAttributes().build());
		event.put(LAST_KNIGHT_AND_HORSE.get(), LastKnightAndHorseEntity.createAttributes().build());
		event.put(ROCINANTE.get(), RocinanteEntity.createAttributes().build());
		event.put(APOCATA.get(), ApocataEntity.createAttributes().build());
		event.put(OCEANIZED_FOX.get(), OceanizedFoxEntity.createAttributes().build());
		event.put(TIDUTANT_EXCRESCENCE.get(), TidutantExcrescenceEntity.createAttributes().build());
		event.put(OCEANIZED_POLAR_BEAR.get(), OceanizedPolarBearEntity.createAttributes().build());
		event.put(TIDUTANT_ROCK_SPIDER.get(), TideutantRockSpiderEntity.createAttributes().build());
		event.put(ENDSPEAKER.get(), EndspeakerEntity.createAttributes().build());
		event.put(LINGERING_PATHSHAPER.get(), LineringPathshaperEntity.createAttributes().build());
		event.put(LINGERING_FRACTAL.get(), LingeringFractalEntity.createAttributes().build());
		event.put(LITTLE_HELPER.get(), LittleHelperEntity.createAttributes().build());
		event.put(AL_1_S_HELPER.get(), Al1SHelperEntity.createAttributes().build());
		event.put(ULPIANS.get(), UlpiansEntity.createAttributes().build());
		event.put(GLADIIA.get(), GladiiaEntity.createAttributes().build());
		event.put(GLADIIA_WHIRL.get(), GladiiaWhirlEntity.createAttributes().build());
		event.put(SPECTER.get(), SpecterEntity.createAttributes().build());
		event.put(SPECTER_DOLL.get(), SpecterDollEntity.createAttributes().build());
		event.put(IRENE.get(), IreneEntity.createAttributes().build());
		event.put(TIDE_CHIMERA.get(), TideChimeraEntity.createAttributes().build());
		event.put(SKADI_CORRUPTED.get(), SkadiCorruptedEntity.createAttributes().build());
		event.put(OCEANIZE_RABBIT.get(), OceanizeRabbitEntity.createAttributes().build());
		event.put(SAINT_CARMEN.get(), SaintCarmenEntity.createAttributes().build());
		event.put(OCEANIZED_ILLUSIONER.get(), OceanizedIllusionerEntity.createAttributes().build());
		event.put(OCEAN_ILLUSION.get(), OceanIllusionEntity.createAttributes().build());
		event.put(FLAMARINE_STATUE.get(), FlamarineStatueEntity.createAttributes().build());
		event.put(NAUTILUS_HEADHUNTER.get(), NautilusHeadhunterEntity.createAttributes().build());
		event.put(XANTIS.get(), XantisEntity.createAttributes().build());
		event.put(FLAMARINE_GOLEM.get(), FlamarineGolemEntity.createAttributes().build());
		event.put(OCEANIZED_VEX.get(), OceanizedVexEntity.createAttributes().build());
		event.put(ISHARMLA.get(), IsharmlaEntity.createAttributes().build());
		event.put(QUNYOU_WANTED_ISHARMLA.get(), QunyouWantedIsharmlaEntity.createAttributes().build());
		event.put(ISHARMLA_TEAR.get(), IsharmlaTearEntity.createAttributes().build());
		event.put(COMPASSION_PRAYER.get(), CompassionPrayerEntity.createAttributes().build());
		event.put(OCEANIZED_ENDERINA.get(), OceanizedEnderinaEntity.createAttributes().build());
		event.put(MOIST_DRAGON_BREATH.get(), MoistDragonBreathEntity.createAttributes().build());
		event.put(MOIST_ENDER_CRYSTAL.get(), MoistEnderCrystalEntity.createAttributes().build());
		event.put(THIRSTER.get(), ThirsterEntity.createAttributes().build());
		event.put(ABSORBER_LIMB.get(), AbsorberLimbEntity.createAttributes().build());
		event.put(SCREAM_CHEST_FISH.get(), ScreamChestFishEntity.createAttributes().build());
		event.put(OCEANIZED_CHICKEN.get(), OceanizedChickenEntity.createAttributes().build());
		event.put(NETHERSEA_SLIME.get(), NetherseaSlimeEntity.createAttributes().build());
		event.put(OCEANIZED_SHULKER.get(), OceanizedShulkerEntity.createAttributes().build());
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class CARenderers {
		@SubscribeEvent
		public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
			event.registerEntityRenderer(CAEntities.RUN_FISH.get(), RunFishRenderer::new);
			event.registerEntityRenderer(CAEntities.SLIDER_FISH.get(), SliderFishRenderer::new);
			event.registerEntityRenderer(CAEntities.SUPER_SLIDER.get(), SuperSliderRenderer::new);
			event.registerEntityRenderer(CAEntities.SHOOTER_FISH.get(), ShooterFishRenderer::new);
			event.registerEntityRenderer(CAEntities.FISH_SHOOT.get(), FishShootRenderer::new);
			event.registerEntityRenderer(CAEntities.FLY_FISH.get(), FlyFishRenderer::new);
			event.registerEntityRenderer(CAEntities.REAPER_FISH.get(), ReaperFishRenderer::new);
			event.registerEntityRenderer(CAEntities.CREEPER_FISH.get(), CreeperFishRenderer::new);
			event.registerEntityRenderer(CAEntities.PUNCTURE_FISH.get(), PunctureFishRenderer::new);
			event.registerEntityRenderer(CAEntities.BASELAYER_ABYSSAL.get(), BaselayerAbyssalRenderer::new);
			event.registerEntityRenderer(CAEntities.PREDATOR_ABYSSAL.get(), PredatorAbyssalRenderer::new);
			event.registerEntityRenderer(CAEntities.GUIDE_ABYSSAL.get(), GuideAbyssalRenderer::new);
			event.registerEntityRenderer(CAEntities.SPLASHER_ABYSSAL.get(), SplasherAbyssalRenderer::new);
			event.registerEntityRenderer(CAEntities.FISH_SPLASH.get(), FishSplashRenderer::new);
			event.registerEntityRenderer(CAEntities.UMBRELLA_ABYSSAL.get(), UmbrellaAbyssalRenderer::new);
			event.registerEntityRenderer(CAEntities.CRACKER_ABYSSAL.get(), CrackerAbyssalRenderer::new);
			event.registerEntityRenderer(CAEntities.COLLECTOR_PROKARYOTE.get(), CollectorProkaryoteRenderer::new);
			event.registerEntityRenderer(CAEntities.BONE_FISH.get(), BoneFishRenderer::new);
			event.registerEntityRenderer(CAEntities.CHISELER_FISH.get(), ChiselerFishRenderer::new);
			event.registerEntityRenderer(CAEntities.FAKERGG_SHOOT.get(), FakerggShootRenderer::new);
			event.registerEntityRenderer(CAEntities.PREGNANT_FISH.get(), PregnantFishRenderer::new);
			event.registerEntityRenderer(CAEntities.FAKE_OFFSPRING.get(), FakeOffspringRenderer::new);
			event.registerEntityRenderer(CAEntities.FLEEFISH_BULLET.get(), FleefishBulletRenderer::new);
			event.registerEntityRenderer(CAEntities.FLEE_FISH.get(), FleeFishRenderer::new);
			event.registerEntityRenderer(CAEntities.ROUTE_SHAPER.get(), RouteShaperRenderer::new);
			event.registerEntityRenderer(CAEntities.ROUTE_FRACTAL.get(), RouteFractalRenderer::new);
			event.registerEntityRenderer(CAEntities.TELLER_SHOT.get(), TellerShotRenderer::new);
			event.registerEntityRenderer(CAEntities.FIRST_TO_TALK.get(), FirstTellerRenderer::new);
			event.registerEntityRenderer(CAEntities.REAPER_PET.get(), ReaperPetRenderer::new);
			event.registerEntityRenderer(CAEntities.BISHOP_FISH.get(), BishopFishRenderer::new);
			event.registerEntityRenderer(CAEntities.TIDE_BISHOP.get(), TideBishopRenderer::new);
			event.registerEntityRenderer(CAEntities.SONS.get(), SonsRenderer::new);
			event.registerEntityRenderer(CAEntities.FLOATER_PROKARYOTE.get(), FloaterProkaryoteRenderer::new);
			event.registerEntityRenderer(CAEntities.CHITIN_GOLEM.get(), ChitinGolemRenderer::new);
			event.registerEntityRenderer(CAEntities.TIDE_DEATHREPELLER.get(), TideDeathrepellerRenderer::new);
			event.registerEntityRenderer(CAEntities.MEGA_CHEST.get(), MegaChestRenderer::new);
			event.registerEntityRenderer(CAEntities.APOSTLE_PROKARYOTE.get(), ApostleProkaryoteRenderer::new);
			event.registerEntityRenderer(CAEntities.HIGHMORE_SHOOT.get(), HighmoreShootRenderer::new);
			event.registerEntityRenderer(CAEntities.HIGHMORE.get(), HighmoreRenderer::new);
			event.registerEntityRenderer(CAEntities.ACCUMULATOR_PROKARYOTE.get(), AccumulatorProkaryoteRenderer::new);
			event.registerEntityRenderer(CAEntities.ACCUMULATOR_CLONE.get(), AccumulatorCloneRenderer::new);
			event.registerEntityRenderer(CAEntities.FEEDER_PROKARYOTE.get(), FeederProkaryoteRenderer::new);
			event.registerEntityRenderer(CAEntities.CHEST_FISH.get(), ChestFishRenderer::new);
			event.registerEntityRenderer(CAEntities.SPIKE_CHEST.get(), SpikeChestRenderer::new);
			event.registerEntityRenderer(CAEntities.SKADI.get(), SkadiRenderer::new);
			event.registerEntityRenderer(CAEntities.DEPOSITER_PROKARYOTE.get(), DepositerProkaryoteRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_VILLAGER.get(), OceanizedVillagerRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_VINDICATOR.get(), OceanizedVindicatorRenderer::new);
			event.registerEntityRenderer(CAEntities.SHOT_OCEAN_ARROW.get(), ShotOceanArrowRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_PILLAGER.get(), OceanizedPillagerRenderer::new);
			event.registerEntityRenderer(CAEntities.ANCHOR_FLY.get(), AnchorFlyRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_PIG.get(), OceanizedPigRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_COW.get(), OceanizedCowRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_SHEEP.get(), OceanizedSheepRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_HORSE.get(), OceanizedHorseRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_PIGLIN.get(), OceanizedPiglinRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_BRUTE.get(), OceanizedBruteRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_SPIDER.get(), OceanizedSpiderRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_ENDERMAN.get(), OceanizedEndermanRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_WOLF.get(), OceanizedWolfRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_DOG.get(), OceanizedDogRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_RAVAGER.get(), OceanizedRavagerRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_WITCH.get(), OceanziedWitchRenderer::new);
			event.registerEntityRenderer(CAEntities.THROWABLE_POTION.get(), ThrownItemRenderer::new);
			event.registerEntityRenderer(CAEntities.IZUMIK_OFFSPRING.get(), IzumikOffspringRenderer::new);
			event.registerEntityRenderer(CAEntities.IZUMIK.get(), IzumikRenderer::new);
			event.registerEntityRenderer(CAEntities.DIVICELLULAR_GO.get(), DivicellularGoRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_EVOKER.get(), OceanizedEvokerRenderer::new);
			event.registerEntityRenderer(CAEntities.JUNIOR_WARRIOR_PRIEST.get(), JuniorWarriorPriestRenderer::new);
			event.registerEntityRenderer(CAEntities.WARRIOR_PRIEST.get(), WarriorPriestRenderer::new);
			event.registerEntityRenderer(CAEntities.CORRECTIONAL_PHALANXY_INFANTRY.get(), CorrectionalPhalanxyInfantryRenderer::new);
			event.registerEntityRenderer(CAEntities.CORRECTIONAL_PHALAX_VANGUARD.get(), CorrectinalPhalaxVanguardRenderer::new);
			event.registerEntityRenderer(CAEntities.TRIBUNAL_HEALER.get(), TribunalHealerRenderer::new);
			event.registerEntityRenderer(CAEntities.HEAL_BULLLET.get(), ThrownItemRenderer::new);
			event.registerEntityRenderer(CAEntities.MARTUS.get(), MartusRenderer::new);
			event.registerEntityRenderer(CAEntities.THE_ABANDONED.get(), TheAbandonedRenderer::new);
			event.registerEntityRenderer(CAEntities.ABANDONED_SHOOT.get(), AbandonedShootRenderer::new);
			event.registerEntityRenderer(CAEntities.GUNMU.get(), GunmuRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_WARDEN.get(), OceanizedWardenRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_CAT.get(), OceanizedCatRenderer::new);
			event.registerEntityRenderer(CAEntities.SUPER_BIG_CAT.get(), SuperBigCatRenderer::new);
			event.registerEntityRenderer(CAEntities.COMPLEX_CHITIN_GOLEM.get(), ComplexChitinGolemRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_WARDENIS.get(), OceanizedWardenisRenderer::new);
			event.registerEntityRenderer(CAEntities.NUCLEIC_MALEFICENT.get(), NucleicMaleficentRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_WITHER.get(), OceanizedWitherRenderer::new);
			event.registerEntityRenderer(CAEntities.WITHER_SHOOT_PRE.get(), ThrownItemRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_WITHERIA.get(), OceannizedWitheriaRenderer::new);
			event.registerEntityRenderer(CAEntities.THE_LAST_KNIGHT.get(), TheLastKnightRenderer::new);
			event.registerEntityRenderer(CAEntities.LAST_KNIGHT_AND_HORSE.get(), LastKnightAndHorseRenderer::new);
			event.registerEntityRenderer(CAEntities.ROCINANTE.get(), RocinanteRenderer::new);
			event.registerEntityRenderer(CAEntities.APOCATA.get(), ApocataRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_FOX.get(), OceanizedFoxRenderer::new);
			event.registerEntityRenderer(CAEntities.TIDUTANT_EXCRESCENCE.get(), TidutantExcrescenceRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_POLAR_BEAR.get(), OceanizedPolarBearRenderer::new);
			event.registerEntityRenderer(CAEntities.TIDUTANT_ROCK_SPIDER.get(), TideutantRockSpiderRenderer::new);
			event.registerEntityRenderer(CAEntities.ENDSPEAKER.get(), context -> new EndspeakerRenderer<>(context));
			event.registerEntityRenderer(CAEntities.LINGERING_PATHSHAPER.get(), LineringPathshaperRenderer::new);
			event.registerEntityRenderer(CAEntities.LINGERING_FRACTAL.get(), LingeringFractalRenderer::new);
			event.registerEntityRenderer(CAEntities.LITTLE_HELPER.get(), LittleHelperRenderer::new);
			event.registerEntityRenderer(CAEntities.AL_1_S_HELPER.get(), Al1SHelperRenderer::new);
			event.registerEntityRenderer(CAEntities.ULPIANS.get(), UlpiansRenderer::new);
			event.registerEntityRenderer(CAEntities.GLADIIA.get(), GladiiaRenderer::new);
			event.registerEntityRenderer(CAEntities.GLADIIA_WHIRL.get(), GladiiaWhirlRenderer::new);
			event.registerEntityRenderer(CAEntities.SPECTER.get(), SpecterRenderer::new);
			event.registerEntityRenderer(CAEntities.SPECTER_DOLL.get(), SpecterDollRenderer::new);
			event.registerEntityRenderer(CAEntities.IRENE.get(), IreneRenderer::new);
			event.registerEntityRenderer(CAEntities.TIDE_CHIMERA.get(), TideChimeraRenderer::new);
			event.registerEntityRenderer(CAEntities.SKADI_CORRUPTED.get(), SkadiCorruptedRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZE_RABBIT.get(), OceanizeRabbitRenderer::new);
			event.registerEntityRenderer(CAEntities.SAINT_CARMEN.get(), SaintCarmenRenderer::new);
			event.registerEntityRenderer(CAEntities.CARMEN_BULLET.get(), CarmenBulletRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_ILLUSIONER.get(), OceanizedIllusionerRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEAN_ILLUSION.get(), OceanIllusionRenderer::new);
			event.registerEntityRenderer(CAEntities.FLAMARINE_STATUE.get(), FlamarineStatueRenderer::new);
			event.registerEntityRenderer(CAEntities.NAUTILUS_HEADHUNTER.get(), NautilusHeadhunterRenderer::new);
			event.registerEntityRenderer(CAEntities.XANTIS.get(), XantisRenderer::new);
			event.registerEntityRenderer(CAEntities.FLAMARINE_GOLEM.get(), FlamarineGolemRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_VEX.get(), OceanizedVexRenderer::new);
			event.registerEntityRenderer(CAEntities.ISHARMLA.get(), IsharmlaRenderer::new);
			event.registerEntityRenderer(CAEntities.QUNYOU_WANTED_ISHARMLA.get(), QunyouWantedIsharmlaRenderer::new);
			event.registerEntityRenderer(CAEntities.ISHARMLA_TEAR.get(), IsharmlaTearRenderer::new);
			event.registerEntityRenderer(CAEntities.PRAYER_SPLASH.get(), PrayerSplashRenderer::new);
			event.registerEntityRenderer(CAEntities.COMPASSION_PRAYER.get(), CompassionPrayerRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_ENDERINA.get(), OceanizedEnderinaRenderer::new);
			event.registerEntityRenderer(CAEntities.MOIST_DRAGON_BREATH.get(), MoistDragonBreathRenderer::new);
			event.registerEntityRenderer(CAEntities.MOIST_ENDER_CRYSTAL.get(), MoistEnderCrystalRenderer::new);
			event.registerEntityRenderer(CAEntities.THIRSTER.get(), ThirsterRenderer::new);
			event.registerEntityRenderer(CAEntities.ABSORBER_LIMB.get(), AbsorberLimbRenderer::new);
			event.registerEntityRenderer(CAEntities.SCREAM_CHEST_FISH.get(), ScreamChestFishRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_CHICKEN.get(), OceanizedChickenRenderer::new);
			event.registerEntityRenderer(CAEntities.NETHERSEA_SLIME.get(), NetherseaSlimeRenderer::new);
			event.registerEntityRenderer(CAEntities.OCEANIZED_SHULKER.get(), OceanizedShulkerRenderer::new);
		}
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
	public static class CAModels {
		@SubscribeEvent
		public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
			event.registerLayerDefinition(ModelBulletProjectile.LAYER_LOCATION, ModelBulletProjectile::createBodyLayer);
			event.registerLayerDefinition(ModelAnchorFly.LAYER_LOCATION, ModelAnchorFly::createBodyLayer);
			event.registerLayerDefinition(ModelSealeatherChitinArmor.LAYER_LOCATION, ModelSealeatherChitinArmor::createBodyLayer);
			event.registerLayerDefinition(ModelHighmoreShoot.LAYER_LOCATION, ModelHighmoreShoot::createBodyLayer);
			event.registerLayerDefinition(ModelFleefishBullet.LAYER_LOCATION, ModelFleefishBullet::createBodyLayer);
			event.registerLayerDefinition(ModelFakerggShoot.LAYER_LOCATION, ModelFakerggShoot::createBodyLayer);
			event.registerLayerDefinition(ModelOceanArrow.LAYER_LOCATION, ModelOceanArrow::createBodyLayer);
		}
	}
}
