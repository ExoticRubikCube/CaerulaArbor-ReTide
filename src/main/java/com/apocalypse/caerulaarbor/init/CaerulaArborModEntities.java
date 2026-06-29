/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.*;
import com.apocalypse.caerulaarbor.entity.routeshaper.LineringPathshaperEntity;
import com.apocalypse.caerulaarbor.entity.routeshaper.LingeringFractalEntity;
import com.apocalypse.caerulaarbor.entity.routeshaper.RouteFractalEntity;
import com.apocalypse.caerulaarbor.entity.routeshaper.RouteShaperEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CaerulaArborModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CaerulaArborMod.MODID);
	public static final RegistryObject<EntityType<RunFishEntity>> RUN_FISH = register("run_fish",
			EntityType.Builder.<RunFishEntity>of(RunFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory(RunFishEntity::new)

					.sized(0.4f, 0.5f));
	public static final RegistryObject<EntityType<SliderFishEntity>> SLIDER_FISH = register("slider_fish",
			EntityType.Builder.<SliderFishEntity>of(SliderFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory(SliderFishEntity::new)

					.sized(0.5f, 0.8f));
	public static final RegistryObject<EntityType<SuperSliderEntity>> SUPER_SLIDER = register("super_slider",
			EntityType.Builder.<SuperSliderEntity>of(SuperSliderEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(SuperSliderEntity::new)

					.sized(0.3f, 0.8f));
	public static final RegistryObject<EntityType<ShooterFishEntity>> SHOOTER_FISH = register("shooter_fish",
			EntityType.Builder.<ShooterFishEntity>of(ShooterFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(ShooterFishEntity::new)

					.sized(0.6f, 1.2f));
	public static final RegistryObject<EntityType<FishShootEntity>> FISH_SHOOT = register("fish_shoot",
			EntityType.Builder.<FishShootEntity>of(FishShootEntity::new, MobCategory.MISC).setCustomClientFactory(FishShootEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
	public static final RegistryObject<EntityType<FlyFishEntity>> FLY_FISH = register("fly_fish",
			EntityType.Builder.<FlyFishEntity>of(FlyFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(FlyFishEntity::new)

					.sized(0.6f, 0.9f));
	public static final RegistryObject<EntityType<ReaperFishEntity>> REAPER_FISH = register("reaper_fish",
			EntityType.Builder.<ReaperFishEntity>of(ReaperFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(10).setUpdateInterval(3).setCustomClientFactory(ReaperFishEntity::new)

					.sized(1.2f, 2.8f));
	public static final RegistryObject<EntityType<CreeperFishEntity>> CREEPER_FISH = register("creeper_fish",
			EntityType.Builder.<CreeperFishEntity>of(CreeperFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(CreeperFishEntity::new)

					.sized(0.8f, 1.5f));
	public static final RegistryObject<EntityType<PunctureFishEntity>> PUNCTURE_FISH = register("puncture_fish",
			EntityType.Builder.<PunctureFishEntity>of(PunctureFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(PunctureFishEntity::new)

					.sized(0.9f, 2.7f));
	public static final RegistryObject<EntityType<BaselayerAbyssalEntity>> BASELAYER_ABYSSAL = register("baselayer_abyssal",
			EntityType.Builder.<BaselayerAbyssalEntity>of(BaselayerAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(BaselayerAbyssalEntity::new)

					.sized(0.7f, 1.5f));
	public static final RegistryObject<EntityType<PredatorAbyssalEntity>> PREDATOR_ABYSSAL = register("predator_abyssal",
			EntityType.Builder.<PredatorAbyssalEntity>of(PredatorAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(PredatorAbyssalEntity::new)

					.sized(0.6f, 1.4f));
	public static final RegistryObject<EntityType<GuideAbyssalEntity>> GUIDE_ABYSSAL = register("guide_abyssal",
			EntityType.Builder.<GuideAbyssalEntity>of(GuideAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(GuideAbyssalEntity::new)

					.sized(1f, 2.5f));
	public static final RegistryObject<EntityType<SplasherAbyssalEntity>> SPLASHER_ABYSSAL = register("splasher_abyssal",
			EntityType.Builder.<SplasherAbyssalEntity>of(SplasherAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(SplasherAbyssalEntity::new)

					.sized(0.6f, 1.1f));
	public static final RegistryObject<EntityType<FishSplashEntity>> FISH_SPLASH = register("fish_splash",
			EntityType.Builder.<FishSplashEntity>of(FishSplashEntity::new, MobCategory.MISC).setCustomClientFactory(FishSplashEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
	public static final RegistryObject<EntityType<UmbrellaAbyssalEntity>> UMBRELLA_ABYSSAL = register("umbrella_abyssal",
			EntityType.Builder.<UmbrellaAbyssalEntity>of(UmbrellaAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(UmbrellaAbyssalEntity::new)

					.sized(0.8f, 1.5f));
	public static final RegistryObject<EntityType<CrackerAbyssalEntity>> CRACKER_ABYSSAL = register("cracker_abyssal",
			EntityType.Builder.<CrackerAbyssalEntity>of(CrackerAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(CrackerAbyssalEntity::new)

					.sized(0.7f, 2f));
	public static final RegistryObject<EntityType<CollectorProkaryoteEntity>> COLLECTOR_PROKARYOTE = register("collector_prokaryote",
			EntityType.Builder.<CollectorProkaryoteEntity>of(CollectorProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3)
					.setCustomClientFactory(CollectorProkaryoteEntity::new)

					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<BoneFishEntity>> BONE_FISH = register("bone_fish",
			EntityType.Builder.<BoneFishEntity>of(BoneFishEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory(BoneFishEntity::new)

					.sized(0.7f, 0.7f));
	public static final RegistryObject<EntityType<ChiselerFishEntity>> CHISELER_FISH = register("chiseler_fish",
			EntityType.Builder.<ChiselerFishEntity>of(ChiselerFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(ChiselerFishEntity::new)

					.sized(0.6f, 0.6f));
	public static final RegistryObject<EntityType<FakerggShootEntity>> FAKERGG_SHOOT = register("fakergg_shoot",
			EntityType.Builder.<FakerggShootEntity>of(FakerggShootEntity::new, MobCategory.MISC).setCustomClientFactory(FakerggShootEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.4f, 0.4f));
	public static final RegistryObject<EntityType<PregnantFishEntity>> PREGNANT_FISH = register("pregnant_fish",
			EntityType.Builder.<PregnantFishEntity>of(PregnantFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(PregnantFishEntity::new)

					.sized(0.7f, 1.1f));
	public static final RegistryObject<EntityType<FakeOffspringEntity>> FAKE_OFFSPRING = register("fake_offspring",
			EntityType.Builder.<FakeOffspringEntity>of(FakeOffspringEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory(FakeOffspringEntity::new)

					.sized(0.6f, 0.6f));
	public static final RegistryObject<EntityType<FleefishBulletEntity>> FLEEFISH_BULLET = register("fleefish_bullet",
			EntityType.Builder.<FleefishBulletEntity>of(FleefishBulletEntity::new, MobCategory.MISC).setCustomClientFactory(FleefishBulletEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.4f, 0.4f));
	public static final RegistryObject<EntityType<FleeFishEntity>> FLEE_FISH = register("flee_fish",
			EntityType.Builder.<FleeFishEntity>of(FleeFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(FleeFishEntity::new)

					.sized(0.8f, 1.1f));
	public static final RegistryObject<EntityType<RouteShaperEntity>> ROUTE_SHAPER = register("route_shaper",
			EntityType.Builder.<RouteShaperEntity>of(RouteShaperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory(RouteShaperEntity::new)

					.sized(1.8f, 4f));
	public static final RegistryObject<EntityType<RouteFractalEntity>> ROUTE_FRACTAL = register("route_fractal",
			EntityType.Builder.<RouteFractalEntity>of(RouteFractalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(RouteFractalEntity::new)

					.sized(0.7f, 1.5f));
	public static final RegistryObject<EntityType<TellerShotEntity>> TELLER_SHOT = register("teller_shot",
			EntityType.Builder.<TellerShotEntity>of(TellerShotEntity::new, MobCategory.MISC).setCustomClientFactory(TellerShotEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.4f, 0.4f));
	public static final RegistryObject<EntityType<FirstTellerEntity>> FIRST_TO_TALK = register("first_to_talk",
			EntityType.Builder.<FirstTellerEntity>of(FirstTellerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(FirstTellerEntity::new)

					.sized(0.9f, 2.7f));
	public static final RegistryObject<EntityType<ReaperPetEntity>> REAPER_PET = register("reaper_pet",
			EntityType.Builder.<ReaperPetEntity>of(ReaperPetEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(ReaperPetEntity::new)

					.sized(0.6f, 1.3f));
	public static final RegistryObject<EntityType<BishopFishEntity>> BISHOP_FISH = register("bishop_fish",
			EntityType.Builder.<BishopFishEntity>of(BishopFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(18).setUpdateInterval(3).setCustomClientFactory(BishopFishEntity::new)

					.sized(1.4f, 2.2f));
	public static final RegistryObject<EntityType<TideBishopEntity>> TIDE_BISHOP = register("tide_bishop",
			EntityType.Builder.<TideBishopEntity>of(TideBishopEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory(TideBishopEntity::new)

					.sized(1.1f, 2.2f));
	public static final RegistryObject<EntityType<SonsEntity>> SONS = register("sons",
			EntityType.Builder.<SonsEntity>of(SonsEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory(SonsEntity::new)

					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<FloaterProkaryoteEntity>> FLOATER_PROKARYOTE = register("floater_prokaryote",
			EntityType.Builder.<FloaterProkaryoteEntity>of(FloaterProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(FloaterProkaryoteEntity::new)

					.sized(0.6f, 1.5f));
	public static final RegistryObject<EntityType<ChitinGolemEntity>> CHITIN_GOLEM = register("chitin_golem",
			EntityType.Builder.<ChitinGolemEntity>of(ChitinGolemEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(ChitinGolemEntity::new)

					.sized(1.75f, 4f));
	public static final RegistryObject<EntityType<TideDeathrepellerEntity>> TIDE_DEATHREPELLER = register("tide_deathrepeller",
			EntityType.Builder.<TideDeathrepellerEntity>of(TideDeathrepellerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(TideDeathrepellerEntity::new)

					.sized(1.5f, 2f));
	public static final RegistryObject<EntityType<MegaChestEntity>> MEGA_CHEST = register("mega_chest",
			EntityType.Builder.<MegaChestEntity>of(MegaChestEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(14).setUpdateInterval(3).setCustomClientFactory(MegaChestEntity::new)

					.sized(0.8f, 0.9f));
	public static final RegistryObject<EntityType<ApostleProkaryoteEntity>> APOSTLE_PROKARYOTE = register("apostle_prokaryote",
			EntityType.Builder.<ApostleProkaryoteEntity>of(ApostleProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(ApostleProkaryoteEntity::new)

					.sized(0.6f, 1.7f));
	public static final RegistryObject<EntityType<HighmoreShootEntity>> HIGHMORE_SHOOT = register("highmore_shoot",
			EntityType.Builder.<HighmoreShootEntity>of(HighmoreShootEntity::new, MobCategory.MISC).setCustomClientFactory(HighmoreShootEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.4f, 0.4f));
	public static final RegistryObject<EntityType<HighmoreEntity>> HIGHMORE = register("highmore",
			EntityType.Builder.<HighmoreEntity>of(HighmoreEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(18).setUpdateInterval(3).setCustomClientFactory(HighmoreEntity::new)

					.sized(1.4f, 1.6f));
	public static final RegistryObject<EntityType<AccumulatorProkaryoteEntity>> ACCUMULATOR_PROKARYOTE = register("accumulator_prokaryote",
			EntityType.Builder.<AccumulatorProkaryoteEntity>of(AccumulatorProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
					.setCustomClientFactory(AccumulatorProkaryoteEntity::new)

					.sized(0.5f, 1f));
	public static final RegistryObject<EntityType<AccumulatorCloneEntity>> ACCUMULATOR_CLONE = register("accumulator_clone",
			EntityType.Builder.<AccumulatorCloneEntity>of(AccumulatorCloneEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(AccumulatorCloneEntity::new)

					.sized(0.5f, 1f));
	public static final RegistryObject<EntityType<FeederProkaryoteEntity>> FEEDER_PROKARYOTE = register("feeder_prokaryote",
			EntityType.Builder.<FeederProkaryoteEntity>of(FeederProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(FeederProkaryoteEntity::new)

					.sized(0.8f, 1.1f));
	public static final RegistryObject<EntityType<ChestFishEntity>> CHEST_FISH = register("chest_fish",
			EntityType.Builder.<ChestFishEntity>of(ChestFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(ChestFishEntity::new)

					.sized(0.9f, 0.9f));
	public static final RegistryObject<EntityType<SpikeChestEntity>> SPIKE_CHEST = register("spike_chest",
			EntityType.Builder.<SpikeChestEntity>of(SpikeChestEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(SpikeChestEntity::new)

					.sized(0.9f, 0.9f));
	public static final RegistryObject<EntityType<SkadiEntity>> SKADI = register("skadi",
			EntityType.Builder.<SkadiEntity>of(SkadiEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(SkadiEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<DepositerProkaryoteEntity>> DEPOSITER_PROKARYOTE = register("depositer_prokaryote",
			EntityType.Builder.<DepositerProkaryoteEntity>of(DepositerProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
					.setCustomClientFactory(DepositerProkaryoteEntity::new)

					.sized(0.625f, 1f));
	public static final RegistryObject<EntityType<OceanizedVillagerEntity>> OCEANIZED_VILLAGER = register("oceanized_villager",
			EntityType.Builder.<OceanizedVillagerEntity>of(OceanizedVillagerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(OceanizedVillagerEntity::new)

					.sized(0.6f, 2f));
	public static final RegistryObject<EntityType<OceanizedVindicatorEntity>> OCEANIZED_VINDICATOR = register("oceanized_vindicator",
			EntityType.Builder.<OceanizedVindicatorEntity>of(OceanizedVindicatorEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(OceanizedVindicatorEntity::new)

					.sized(0.6f, 2f));
	public static final RegistryObject<EntityType<ShotOceanArrowEntity>> SHOT_OCEAN_ARROW = register("shot_ocean_arrow",
			EntityType.Builder.<ShotOceanArrowEntity>of(ShotOceanArrowEntity::new, MobCategory.MISC).setCustomClientFactory(ShotOceanArrowEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<OceanizedPillagerEntity>> OCEANIZED_PILLAGER = register("oceanized_pillager",
			EntityType.Builder.<OceanizedPillagerEntity>of(OceanizedPillagerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(OceanizedPillagerEntity::new)

					.sized(0.6f, 1.9f));
	public static final RegistryObject<EntityType<AnchorFlyEntity>> ANCHOR_FLY = register("anchor_fly",
			EntityType.Builder.<AnchorFlyEntity>of(AnchorFlyEntity::new, MobCategory.MISC).setCustomClientFactory(AnchorFlyEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.2f, 0.2f));
	public static final RegistryObject<EntityType<OceanizedPigEntity>> OCEANIZED_PIG = register("oceanized_pig",
			EntityType.Builder.<OceanizedPigEntity>of(OceanizedPigEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory(OceanizedPigEntity::new)

					.sized(0.6f, 1f));
	public static final RegistryObject<EntityType<OceanizedCowEntity>> OCEANIZED_COW = register("oceanized_cow",
			EntityType.Builder.<OceanizedCowEntity>of(OceanizedCowEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory(OceanizedCowEntity::new)

					.sized(0.6f, 1.375f));
	public static final RegistryObject<EntityType<OceanizedSheepEntity>> OCEANIZED_SHEEP = register("oceanized_sheep",
			EntityType.Builder.<OceanizedSheepEntity>of(OceanizedSheepEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory(OceanizedSheepEntity::new)

					.sized(0.6f, 1f));
	public static final RegistryObject<EntityType<OceanizedHorseEntity>> OCEANIZED_HORSE = register("oceanized_horse",
			EntityType.Builder.<OceanizedHorseEntity>of(OceanizedHorseEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(OceanizedHorseEntity::new)

					.sized(1.25f, 1.8f));
	public static final RegistryObject<EntityType<OceanizedPiglinEntity>> OCEANIZED_PIGLIN = register("oceanized_piglin", EntityType.Builder.<OceanizedPiglinEntity>of(OceanizedPiglinEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(OceanizedPiglinEntity::new).fireImmune().sized(0.6f, 2f));
	public static final RegistryObject<EntityType<OceanizedBruteEntity>> OCEANIZED_BRUTE = register("oceanized_brute", EntityType.Builder.<OceanizedBruteEntity>of(OceanizedBruteEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(10).setUpdateInterval(3).setCustomClientFactory(OceanizedBruteEntity::new).fireImmune().sized(0.6f, 2f));
	public static final RegistryObject<EntityType<OceanizedSpiderEntity>> OCEANIZED_SPIDER = register("oceanized_spider",
			EntityType.Builder.<OceanizedSpiderEntity>of(OceanizedSpiderEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(7).setUpdateInterval(3).setCustomClientFactory(OceanizedSpiderEntity::new)

					.sized(0.9f, 0.7f));
	public static final RegistryObject<EntityType<OceanizedEndermanEntity>> OCEANIZED_ENDERMAN = register("oceanized_enderman",
			EntityType.Builder.<OceanizedEndermanEntity>of(OceanizedEndermanEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(10).setUpdateInterval(3).setCustomClientFactory(OceanizedEndermanEntity::new)

					.sized(0.6f, 3f));
	public static final RegistryObject<EntityType<OceanizedWolfEntity>> OCEANIZED_WOLF = register("oceanized_wolf",
			EntityType.Builder.<OceanizedWolfEntity>of(OceanizedWolfEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(7).setUpdateInterval(3).setCustomClientFactory(OceanizedWolfEntity::new)

					.sized(0.7f, 0.8f));
	public static final RegistryObject<EntityType<OceanizedDogEntity>> OCEANIZED_DOG = register("oceanized_dog",
			EntityType.Builder.<OceanizedDogEntity>of(OceanizedDogEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(OceanizedDogEntity::new)

					.sized(0.7f, 0.8f));
	public static final RegistryObject<EntityType<OceanizedRavagerEntity>> OCEANIZED_RAVAGER = register("oceanized_ravager",
			EntityType.Builder.<OceanizedRavagerEntity>of(OceanizedRavagerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(10).setUpdateInterval(3).setCustomClientFactory(OceanizedRavagerEntity::new)

					.sized(1.9f, 2.5f));
	public static final RegistryObject<EntityType<OceanziedWitchEntity>> OCEANIZED_WITCH = register("oceanized_witch",
			EntityType.Builder.<OceanziedWitchEntity>of(OceanziedWitchEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(OceanziedWitchEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<ThrowablePotionEntity>> THROWABLE_POTION = register("throwable_potion", EntityType.Builder.<ThrowablePotionEntity>of(ThrowablePotionEntity::new, MobCategory.MISC)
			.setCustomClientFactory(ThrowablePotionEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.2f, 0.2f));
	public static final RegistryObject<EntityType<IzumikOffspringEntity>> IZUMIK_OFFSPRING = register("izumik_offspring",
			EntityType.Builder.<IzumikOffspringEntity>of(IzumikOffspringEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(IzumikOffspringEntity::new)

					.sized(0.5f, 0.9f));
	public static final RegistryObject<EntityType<IzumikEntity>> IZUMIK = register("izumik",
			EntityType.Builder.<IzumikEntity>of(IzumikEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(21).setUpdateInterval(3).setCustomClientFactory(IzumikEntity::new).fireImmune().sized(3.3f, 9f));
	public static final RegistryObject<EntityType<DivicellularGoEntity>> DIVICELLULAR_GO = register("divicellular_go",
			EntityType.Builder.<DivicellularGoEntity>of(DivicellularGoEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(DivicellularGoEntity::new)

					.sized(0.5f, 1f));
	public static final RegistryObject<EntityType<OceanizedEvokerEntity>> OCEANIZED_EVOKER = register("oceanized_evoker",
			EntityType.Builder.<OceanizedEvokerEntity>of(OceanizedEvokerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(OceanizedEvokerEntity::new)

					.sized(0.6f, 1.9f));
	public static final RegistryObject<EntityType<JuniorWarriorPriestEntity>> JUNIOR_WARRIOR_PRIEST = register("junior_warrior_priest",
			EntityType.Builder.<JuniorWarriorPriestEntity>of(JuniorWarriorPriestEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(JuniorWarriorPriestEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<WarriorPriestEntity>> WARRIOR_PRIEST = register("warrior_priest",
			EntityType.Builder.<WarriorPriestEntity>of(WarriorPriestEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(WarriorPriestEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<CorrectionalPhalanxyInfantryEntity>> CORRECTIONAL_PHALANXY_INFANTRY = register("correctional_phalanxy_infantry",
			EntityType.Builder.<CorrectionalPhalanxyInfantryEntity>of(CorrectionalPhalanxyInfantryEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
					.setCustomClientFactory(CorrectionalPhalanxyInfantryEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<CorrectinalPhalaxVanguardEntity>> CORRECTIONAL_PHALAX_VANGUARD = register("correctional_phalax_vanguard",
			EntityType.Builder.<CorrectinalPhalaxVanguardEntity>of(CorrectinalPhalaxVanguardEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)
					.setCustomClientFactory(CorrectinalPhalaxVanguardEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<TribunalHealerEntity>> TRIBUNAL_HEALER = register("tribunal_healer",
			EntityType.Builder.<TribunalHealerEntity>of(TribunalHealerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(TribunalHealerEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<HealBullletEntity>> HEAL_BULLLET = register("heal_bulllet",
			EntityType.Builder.<HealBullletEntity>of(HealBullletEntity::new, MobCategory.MISC).setCustomClientFactory(HealBullletEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.1f, 0.1f));
	public static final RegistryObject<EntityType<MartusEntity>> MARTUS = register("martus",
			EntityType.Builder.<MartusEntity>of(MartusEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(MartusEntity::new).fireImmune().sized(0.7f, 2.8f));
	public static final RegistryObject<EntityType<TheAbandonedEntity>> THE_ABANDONED = register("the_abandoned",
			EntityType.Builder.<TheAbandonedEntity>of(TheAbandonedEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(TheAbandonedEntity::new)

					.sized(0.7f, 2.25f));
	public static final RegistryObject<EntityType<AbandonedShootEntity>> ABANDONED_SHOOT = register("abandoned_shoot",
			EntityType.Builder.<AbandonedShootEntity>of(AbandonedShootEntity::new, MobCategory.MISC).setCustomClientFactory(AbandonedShootEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
	public static final RegistryObject<EntityType<GunmuEntity>> GUNMU = register("gunmu",
			EntityType.Builder.<GunmuEntity>of(GunmuEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(GunmuEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<OceanizedWardenEntity>> OCEANIZED_WARDEN = register("oceanized_warden", EntityType.Builder.<OceanizedWardenEntity>of(OceanizedWardenEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(OceanizedWardenEntity::new).fireImmune().sized(1.2f, 3.1f));
	public static final RegistryObject<EntityType<OceanizedCatEntity>> OCEANIZED_CAT = register("oceanized_cat",
			EntityType.Builder.<OceanizedCatEntity>of(OceanizedCatEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(OceanizedCatEntity::new)

					.sized(0.5f, 0.625f));
	public static final RegistryObject<EntityType<SuperBigCatEntity>> SUPER_BIG_CAT = register("super_big_cat",
			EntityType.Builder.<SuperBigCatEntity>of(SuperBigCatEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(SuperBigCatEntity::new)

					.sized(3.5f, 4f));
	public static final RegistryObject<EntityType<ComplexChitinGolemEntity>> COMPLEX_CHITIN_GOLEM = register("complex_chitin_golem", EntityType.Builder.<ComplexChitinGolemEntity>of(ComplexChitinGolemEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ComplexChitinGolemEntity::new).fireImmune().sized(1.75f, 4f));
	public static final RegistryObject<EntityType<OceanizedWardenisEntity>> OCEANIZED_WARDENIS = register("oceanized_wardenis", EntityType.Builder.<OceanizedWardenisEntity>of(OceanizedWardenisEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(OceanizedWardenisEntity::new).fireImmune().sized(0.6f, 1.85f));
	public static final RegistryObject<EntityType<NucleicMaleficentEntity>> NUCLEIC_MALEFICENT = register("nucleic_maleficent",
			EntityType.Builder.<NucleicMaleficentEntity>of(NucleicMaleficentEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(NucleicMaleficentEntity::new)

					.sized(0.7f, 1.5f));
	public static final RegistryObject<EntityType<OceanizedWitherEntity>> OCEANIZED_WITHER = register("oceanized_wither", EntityType.Builder.<OceanizedWitherEntity>of(OceanizedWitherEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(OceanizedWitherEntity::new).fireImmune().sized(1.1f, 3.5f));
	public static final RegistryObject<EntityType<WitherShootPreEntity>> WITHER_SHOOT_PRE = register("wither_shoot_pre",
			EntityType.Builder.<WitherShootPreEntity>of(WitherShootPreEntity::new, MobCategory.MISC).setCustomClientFactory(WitherShootPreEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.1f, 0.1f));
	public static final RegistryObject<EntityType<OceannizedWitheriaEntity>> OCEANIZED_WITHERIA = register("oceanized_witheria", EntityType.Builder.<OceannizedWitheriaEntity>of(OceannizedWitheriaEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(OceannizedWitheriaEntity::new).fireImmune().sized(0.7f, 2.5f));
	public static final RegistryObject<EntityType<TheLastKnightEntity>> THE_LAST_KNIGHT = register("the_last_knight", EntityType.Builder.<TheLastKnightEntity>of(TheLastKnightEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory(TheLastKnightEntity::new).fireImmune().sized(1f, 3.6f));
	public static final RegistryObject<EntityType<LastKnightAndHorseEntity>> LAST_KNIGHT_AND_HORSE = register("last_knight_and_horse", EntityType.Builder.<LastKnightAndHorseEntity>of(LastKnightAndHorseEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(LastKnightAndHorseEntity::new).fireImmune().sized(1.2f, 4f));
	public static final RegistryObject<EntityType<RocinanteEntity>> ROCINANTE = register("rocinante",
			EntityType.Builder.<RocinanteEntity>of(RocinanteEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(RocinanteEntity::new)

					.sized(1.2f, 3f));
	public static final RegistryObject<EntityType<ApocataEntity>> APOCATA = register("apocata",
			EntityType.Builder.<ApocataEntity>of(ApocataEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ApocataEntity::new)

					.sized(0.6f, 1.85f));
	public static final RegistryObject<EntityType<OceanizedFoxEntity>> OCEANIZED_FOX = register("oceanized_fox",
			EntityType.Builder.<OceanizedFoxEntity>of(OceanizedFoxEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(OceanizedFoxEntity::new)

					.sized(0.6f, 0.7f));
	public static final RegistryObject<EntityType<TidutantExcrescenceEntity>> TIDUTANT_EXCRESCENCE = register("tidutant_excrescence",
			EntityType.Builder.<TidutantExcrescenceEntity>of(TidutantExcrescenceEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(TidutantExcrescenceEntity::new)

					.sized(0.5f, 0.4f));
	public static final RegistryObject<EntityType<OceanizedPolarBearEntity>> OCEANIZED_POLAR_BEAR = register("oceanized_polar_bear",
			EntityType.Builder.<OceanizedPolarBearEntity>of(OceanizedPolarBearEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(OceanizedPolarBearEntity::new)

					.sized(1f, 1.25f));
	public static final RegistryObject<EntityType<TideutantRockSpiderEntity>> TIDUTANT_ROCK_SPIDER = register("tidutant_rock_spider",
			EntityType.Builder.<TideutantRockSpiderEntity>of(TideutantRockSpiderEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(TideutantRockSpiderEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<Endspeaker0Entity>> ENDSPEAKER_0 = register("endspeaker_0",
			EntityType.Builder.<Endspeaker0Entity>of(Endspeaker0Entity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(Endspeaker0Entity::new)

					.sized(0.65f, 0.7f));
	public static final RegistryObject<EntityType<Endspeaker1Entity>> ENDSPEAKER_1 = register("endspeaker_1",
			EntityType.Builder.<Endspeaker1Entity>of(Endspeaker1Entity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(Endspeaker1Entity::new)

					.sized(0.75f, 1.5f));
	public static final RegistryObject<EntityType<Endspeaker2Entity>> ENDSPEAKER_2 = register("endspeaker_2",
			EntityType.Builder.<Endspeaker2Entity>of(Endspeaker2Entity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(Endspeaker2Entity::new)

					.sized(1.2f, 2.8f));
	public static final RegistryObject<EntityType<Endspeaker3Entity>> ENDSPEAKER_3 = register("endspeaker_3",
			EntityType.Builder.<Endspeaker3Entity>of(Endspeaker3Entity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(14).setUpdateInterval(3).setCustomClientFactory(Endspeaker3Entity::new)

					.sized(1f, 3.375f));
	public static final RegistryObject<EntityType<LineringPathshaperEntity>> LINGERING_PATHSHAPER = register("lingering_pathshaper",
			EntityType.Builder.<LineringPathshaperEntity>of(LineringPathshaperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory(LineringPathshaperEntity::new)

					.sized(1.8f, 4f));
	public static final RegistryObject<EntityType<LingeringFractalEntity>> LINGERING_FRACTAL = register("lingering_fractal",
			EntityType.Builder.<LingeringFractalEntity>of(LingeringFractalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(LingeringFractalEntity::new)

					.sized(0.7f, 1.5f));
	public static final RegistryObject<EntityType<LittleHelperEntity>> LITTLE_HELPER = register("little_helper", EntityType.Builder.<LittleHelperEntity>of(LittleHelperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(LittleHelperEntity::new).fireImmune().sized(0.875f, 0.2f));
	public static final RegistryObject<EntityType<Al1SHelperEntity>> AL_1_S_HELPER = register("al_1_s_helper", EntityType.Builder.<Al1SHelperEntity>of(Al1SHelperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(Al1SHelperEntity::new).fireImmune().sized(0.875f, 0.2f));
	public static final RegistryObject<EntityType<DamageTesterEntity>> DAMAGE_TESTER = register("damage_tester",
			EntityType.Builder.<DamageTesterEntity>of(DamageTesterEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(DamageTesterEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<UlpiansEntity>> ULPIANS = register("ulpians",
			EntityType.Builder.<UlpiansEntity>of(UlpiansEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(UlpiansEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<GladiiaEntity>> GLADIIA = register("gladiia",
			EntityType.Builder.<GladiiaEntity>of(GladiiaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(GladiiaEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<GladiiaWhirlEntity>> GLADIIA_WHIRL = register("gladiia_whirl",
			EntityType.Builder.<GladiiaWhirlEntity>of(GladiiaWhirlEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory(GladiiaWhirlEntity::new)

					.sized(2f, 2f));
	public static final RegistryObject<EntityType<SpecterEntity>> SPECTER = register("specter",
			EntityType.Builder.<SpecterEntity>of(SpecterEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(SpecterEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<SpecterDollEntity>> SPECTER_DOLL = register("specter_doll",
			EntityType.Builder.<SpecterDollEntity>of(SpecterDollEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(SpecterDollEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<IreneEntity>> IRENE = register("irene",
			EntityType.Builder.<IreneEntity>of(IreneEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(IreneEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<TideChimeraEntity>> TIDE_CHIMERA = register("tide_chimera",
			EntityType.Builder.<TideChimeraEntity>of(TideChimeraEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory(TideChimeraEntity::new)

					.sized(1.8f, 4.65f));
	public static final RegistryObject<EntityType<SkadiCorruptedEntity>> SKADI_CORRUPTED = register("skadi_corrupted", EntityType.Builder.<SkadiCorruptedEntity>of(SkadiCorruptedEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory(SkadiCorruptedEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<OceanizeRabbitEntity>> OCEANIZE_RABBIT = register("oceanize_rabbit",
			EntityType.Builder.<OceanizeRabbitEntity>of(OceanizeRabbitEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(OceanizeRabbitEntity::new)

					.sized(0.4f, 0.5f));
	public static final RegistryObject<EntityType<SaintCarmenEntity>> SAINT_CARMEN = register("saint_carmen",
			EntityType.Builder.<SaintCarmenEntity>of(SaintCarmenEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory(SaintCarmenEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<CarmenBulletEntity>> CARMEN_BULLET = register("carmen_bullet",
			EntityType.Builder.<CarmenBulletEntity>of(CarmenBulletEntity::new, MobCategory.MISC).setCustomClientFactory(CarmenBulletEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
	public static final RegistryObject<EntityType<OceanizedIllusionerEntity>> OCEANIZED_ILLUSIONER = register("oceanized_illusioner",
			EntityType.Builder.<OceanizedIllusionerEntity>of(OceanizedIllusionerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(10).setUpdateInterval(3).setCustomClientFactory(OceanizedIllusionerEntity::new)

					.sized(0.6f, 1.9f));
	public static final RegistryObject<EntityType<OceanIllusionEntity>> OCEAN_ILLUSION = register("ocean_illusion",
			EntityType.Builder.<OceanIllusionEntity>of(OceanIllusionEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory(OceanIllusionEntity::new)

					.sized(0.6f, 1.9f));
	public static final RegistryObject<EntityType<FlamarineStatueEntity>> FLAMARINE_STATUE = register("flamarine_statue", EntityType.Builder.<FlamarineStatueEntity>of(FlamarineStatueEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory(FlamarineStatueEntity::new).fireImmune().sized(0.7f, 2f));
	public static final RegistryObject<EntityType<NautilusHeadhunterEntity>> NAUTILUS_HEADHUNTER = register("nautilus_headhunter",
			EntityType.Builder.<NautilusHeadhunterEntity>of(NautilusHeadhunterEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3)
					.setCustomClientFactory(NautilusHeadhunterEntity::new)

					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<XantisEntity>> XANTIS = register("xantis",
			EntityType.Builder.<XantisEntity>of(XantisEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory(XantisEntity::new)

					.sized(0.5f, 0.68f));
	public static final RegistryObject<EntityType<FlamarineGolemEntity>> FLAMARINE_GOLEM = register("flamarine_golem", EntityType.Builder.<FlamarineGolemEntity>of(FlamarineGolemEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(FlamarineGolemEntity::new).fireImmune().sized(1f, 2.75f));
	public static final RegistryObject<EntityType<OceanizedVexEntity>> OCEANIZED_VEX = register("oceanized_vex",
			EntityType.Builder.<OceanizedVexEntity>of(OceanizedVexEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory(OceanizedVexEntity::new)

					.sized(0.4f, 0.8f));
	public static final RegistryObject<EntityType<IsharmlaEntity>> ISHARMLA = register("isharmla",
			EntityType.Builder.<IsharmlaEntity>of(IsharmlaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory(IsharmlaEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<QunyouWantedIsharmlaEntity>> QUNYOU_WANTED_ISHARMLA = register("qunyou_wanted_isharmla",
			EntityType.Builder.<QunyouWantedIsharmlaEntity>of(QunyouWantedIsharmlaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3)
					.setCustomClientFactory(QunyouWantedIsharmlaEntity::new)

					.sized(20f, 20f));
	public static final RegistryObject<EntityType<IsharmlaTearEntity>> ISHARMLA_TEAR = register("isharmla_tear",
			EntityType.Builder.<IsharmlaTearEntity>of(IsharmlaTearEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(IsharmlaTearEntity::new)

					.sized(1f, 0.5f));
	public static final RegistryObject<EntityType<PrayerSplashEntity>> PRAYER_SPLASH = register("prayer_splash",
			EntityType.Builder.<PrayerSplashEntity>of(PrayerSplashEntity::new, MobCategory.MISC).setCustomClientFactory(PrayerSplashEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
	public static final RegistryObject<EntityType<CompassionPrayerEntity>> COMPASSION_PRAYER = register("compassion_prayer",
			EntityType.Builder.<CompassionPrayerEntity>of(CompassionPrayerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory(CompassionPrayerEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<OceanizedEnderinaEntity>> OCEANIZED_ENDERINA = register("oceanized_enderina",
			EntityType.Builder.<OceanizedEnderinaEntity>of(OceanizedEnderinaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory(OceanizedEnderinaEntity::new)

					.sized(0.75f, 1.95f));
	public static final RegistryObject<EntityType<MoistDragonBreathEntity>> MOIST_DRAGON_BREATH = register("moist_dragon_breath",
			EntityType.Builder.<MoistDragonBreathEntity>of(MoistDragonBreathEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory(MoistDragonBreathEntity::new)

					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<MoistEnderCrystalEntity>> MOIST_ENDER_CRYSTAL = register("moist_ender_crystal",
			EntityType.Builder.<MoistEnderCrystalEntity>of(MoistEnderCrystalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(MoistEnderCrystalEntity::new)

					.sized(2f, 2f));
	public static final RegistryObject<EntityType<ThirsterEntity>> THIRSTER = register("thirster",
			EntityType.Builder.<ThirsterEntity>of(ThirsterEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(ThirsterEntity::new)

					.sized(1.5f, 1.75f));
	public static final RegistryObject<EntityType<AbsorberLimbEntity>> ABSORBER_LIMB = register("absorber_limb",
			EntityType.Builder.<AbsorberLimbEntity>of(AbsorberLimbEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory(AbsorberLimbEntity::new)

					.sized(0.4f, 1f));
	public static final RegistryObject<EntityType<ScreamChestFishEntity>> SCREAM_CHEST_FISH = register("scream_chest_fish",
			EntityType.Builder.<ScreamChestFishEntity>of(ScreamChestFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ScreamChestFishEntity::new)

					.sized(0.9f, 0.9f));
	public static final RegistryObject<EntityType<OceanizedChickenEntity>> OCEANIZED_CHICKEN = register("oceanized_chicken",
			EntityType.Builder.<OceanizedChickenEntity>of(OceanizedChickenEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory(OceanizedChickenEntity::new)

					.sized(0.5f, 0.875f));
	public static final RegistryObject<EntityType<NetherseaSlimeEntity>> NETHERSEA_SLIME = register("nethersea_slime",
			EntityType.Builder.<NetherseaSlimeEntity>of(NetherseaSlimeEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory(NetherseaSlimeEntity::new)

					.sized(1f, 1f));
	public static final RegistryObject<EntityType<OceanizedShulkerEntity>> OCEANIZED_SHULKER = register("oceanized_shulker",
			EntityType.Builder.<OceanizedShulkerEntity>of(OceanizedShulkerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory(OceanizedShulkerEntity::new)

					.sized(1f, 1f));

	// Start of user code block custom entities
	// End of user code block custom entities
	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> entityTypeBuilder.build(registryname));
	}

	//TODO:需要清理和下放，高优先级
	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			RunFishEntity.init();
			SliderFishEntity.init();
			SuperSliderEntity.init();
			ShooterFishEntity.init();
			FlyFishEntity.init();
			ReaperFishEntity.init();
			CreeperFishEntity.init();
			PunctureFishEntity.init();
			BaselayerAbyssalEntity.init();
			PredatorAbyssalEntity.init();
			GuideAbyssalEntity.init();
			SplasherAbyssalEntity.init();
			UmbrellaAbyssalEntity.init();
			CrackerAbyssalEntity.init();
			CollectorProkaryoteEntity.init();
			BoneFishEntity.init();
			ChiselerFishEntity.init();
			PregnantFishEntity.init();
			FakeOffspringEntity.init();
			FleeFishEntity.init();
			RouteShaperEntity.init();
			RouteFractalEntity.init();
			FirstTellerEntity.init();
			ReaperPetEntity.init();
			BishopFishEntity.init();
			TideBishopEntity.init();
			SonsEntity.init();
			FloaterProkaryoteEntity.init();
			ChitinGolemEntity.init();
			TideDeathrepellerEntity.init();
			MegaChestEntity.init();
			ApostleProkaryoteEntity.init();
			HighmoreEntity.init();
			AccumulatorProkaryoteEntity.init();
			AccumulatorCloneEntity.init();
			FeederProkaryoteEntity.init();
			ChestFishEntity.init();
			SpikeChestEntity.init();
			SkadiEntity.init();
			DepositerProkaryoteEntity.init();
			OceanizedVillagerEntity.init();
			OceanizedVindicatorEntity.init();
			OceanizedPillagerEntity.init();
			OceanizedPigEntity.init();
			OceanizedCowEntity.init();
			OceanizedSheepEntity.init();
			OceanizedHorseEntity.init();
			OceanizedPiglinEntity.init();
			OceanizedBruteEntity.init();
			OceanizedSpiderEntity.init();
			OceanizedEndermanEntity.init();
			OceanizedWolfEntity.init();
			OceanizedDogEntity.init();
			OceanizedRavagerEntity.init();
			OceanziedWitchEntity.init();
			IzumikOffspringEntity.init();
			IzumikEntity.init();
			DivicellularGoEntity.init();
			OceanizedEvokerEntity.init();
			JuniorWarriorPriestEntity.init();
			WarriorPriestEntity.init();
			CorrectionalPhalanxyInfantryEntity.init();
			CorrectinalPhalaxVanguardEntity.init();
			TribunalHealerEntity.init();
			MartusEntity.init();
			TheAbandonedEntity.init();
			GunmuEntity.init();
			OceanizedWardenEntity.init();
			OceanizedCatEntity.init();
			SuperBigCatEntity.init();
			ComplexChitinGolemEntity.init();
			OceanizedWardenisEntity.init();
			NucleicMaleficentEntity.init();
			OceanizedWitherEntity.init();
			OceannizedWitheriaEntity.init();
			TheLastKnightEntity.init();
			LastKnightAndHorseEntity.init();
			RocinanteEntity.init();
			ApocataEntity.init();
			OceanizedFoxEntity.init();
			TidutantExcrescenceEntity.init();
			OceanizedPolarBearEntity.init();
			TideutantRockSpiderEntity.init();
			LineringPathshaperEntity.init();
			LingeringFractalEntity.init();
			LittleHelperEntity.init();
			Al1SHelperEntity.init();
			DamageTesterEntity.init();
			UlpiansEntity.init();
			GladiiaEntity.init();
			GladiiaWhirlEntity.init();
			SpecterEntity.init();
			SpecterDollEntity.init();
			IreneEntity.init();
			TideChimeraEntity.init();
			SkadiCorruptedEntity.init();
			OceanizeRabbitEntity.init();
			SaintCarmenEntity.init();
			OceanizedIllusionerEntity.init();
			OceanIllusionEntity.init();
			FlamarineStatueEntity.init();
			NautilusHeadhunterEntity.init();
			XantisEntity.init();
			FlamarineGolemEntity.init();
			OceanizedVexEntity.init();
			IsharmlaEntity.init();
			QunyouWantedIsharmlaEntity.init();
			IsharmlaTearEntity.init();
			CompassionPrayerEntity.init();
			OceanizedEnderinaEntity.init();
			MoistDragonBreathEntity.init();
			MoistEnderCrystalEntity.init();
			ThirsterEntity.init();
			AbsorberLimbEntity.init();
			ScreamChestFishEntity.init();
			OceanizedChickenEntity.init();
			NetherseaSlimeEntity.init();
			OceanizedShulkerEntity.init();
		});
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
		event.put(ENDSPEAKER_0.get(), Endspeaker0Entity.createAttributes().build());
		event.put(ENDSPEAKER_1.get(), Endspeaker1Entity.createAttributes().build());
		event.put(ENDSPEAKER_2.get(), Endspeaker2Entity.createAttributes().build());
		event.put(ENDSPEAKER_3.get(), Endspeaker3Entity.createAttributes().build());
		event.put(LINGERING_PATHSHAPER.get(), LineringPathshaperEntity.createAttributes().build());
		event.put(LINGERING_FRACTAL.get(), LingeringFractalEntity.createAttributes().build());
		event.put(LITTLE_HELPER.get(), LittleHelperEntity.createAttributes().build());
		event.put(AL_1_S_HELPER.get(), Al1SHelperEntity.createAttributes().build());
		event.put(DAMAGE_TESTER.get(), DamageTesterEntity.createAttributes().build());
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
}
