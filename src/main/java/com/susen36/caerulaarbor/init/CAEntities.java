package com.susen36.caerulaarbor.init;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.client.model.entity.*;
import com.susen36.caerulaarbor.client.renderer.entity.*;
import com.susen36.caerulaarbor.entity.*;
import com.susen36.caerulaarbor.entity.bullets.*;
import com.susen36.caerulaarbor.entity.enderdragon.OceanizedEnderDragonEntity;
import com.susen36.caerulaarbor.entity.enderdragon.OceanizedEnderinaEntity;
import com.susen36.caerulaarbor.entity.helper.Al1SHelperEntity;
import com.susen36.caerulaarbor.entity.helper.LittleHelperEntity;
import com.susen36.caerulaarbor.entity.routeshaper.LineringPathshaperEntity;
import com.susen36.caerulaarbor.entity.routeshaper.LingeringFractalEntity;
import com.susen36.caerulaarbor.entity.routeshaper.RouteFractalEntity;
import com.susen36.caerulaarbor.entity.routeshaper.RouteShaperEntity;
import com.susen36.caerulaarbor.entity.warden.OceanizedWardenEntity;
import com.susen36.caerulaarbor.entity.warden.OceanizedWardenisEntity;
import com.susen36.caerulaarbor.entity.wither.OceanizedWitherEntity;
import com.susen36.caerulaarbor.entity.wither.OceanizedWitheriaEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@EventBusSubscriber
public class CAEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, CaerulaArborMod.MODID);
    private static final LinkedHashMap<DeferredHolder<? extends EntityType<?>, ? extends EntityType<?>>, EntityRegistrationData> ENTITY_REGISTRATIONS = new LinkedHashMap<>();
    public static final DeferredHolder<EntityType<RunFishEntity>, ? extends EntityType<RunFishEntity>> RUN_FISH = register("run_fish",
            EntityType.Builder.<RunFishEntity>of(RunFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new RunFishEntity(level))

                    .sized(0.4f, 0.5f));
    public static final DeferredHolder<EntityType<SliderFishEntity>, ? extends EntityType<SliderFishEntity>> SLIDER_FISH = register("slider_fish",
            EntityType.Builder.<SliderFishEntity>of(SliderFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SliderFishEntity(level))

                    .sized(0.5f, 0.8f));
    public static final DeferredHolder<EntityType<SuperSliderEntity>, ? extends EntityType<SuperSliderEntity>> SUPER_SLIDER = register("super_slider",
            EntityType.Builder.<SuperSliderEntity>of(SuperSliderEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SuperSliderEntity(level))

                    .sized(0.3f, 0.8f));
    public static final DeferredHolder<EntityType<ShooterFishEntity>, ? extends EntityType<ShooterFishEntity>> SHOOTER_FISH = register("shooter_fish",
            EntityType.Builder.<ShooterFishEntity>of(ShooterFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ShooterFishEntity(level))

                    .sized(0.6f, 1.2f));
    public static final DeferredHolder<EntityType<FishShootEntity>, ? extends EntityType<FishShootEntity>> FISH_SHOOT = register("fish_shoot",
            EntityType.Builder.<FishShootEntity>of(FishShootEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new FishShootEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
    public static final DeferredHolder<EntityType<FlyFishEntity>, ? extends EntityType<FlyFishEntity>> FLY_FISH = register("fly_fish",
            EntityType.Builder.<FlyFishEntity>of(FlyFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new FlyFishEntity(level))

                    .sized(0.6f, 0.9f));
    public static final DeferredHolder<EntityType<ReaperFishEntity>, ? extends EntityType<ReaperFishEntity>> REAPER_FISH = register("reaper_fish",
            EntityType.Builder.<ReaperFishEntity>of(ReaperFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(10).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ReaperFishEntity(level))

                    .sized(1.2f, 2.8f));
    public static final DeferredHolder<EntityType<PocketSeaCreeperEntity>, ? extends EntityType<PocketSeaCreeperEntity>> POCKET_SEA_CREEPER = register("pocket_sea_creeper",
            EntityType.Builder.<PocketSeaCreeperEntity>of(PocketSeaCreeperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new PocketSeaCreeperEntity(level))

                    .sized(0.8f, 1.5f));
    public static final DeferredHolder<EntityType<PunctureFishEntity>, ? extends EntityType<PunctureFishEntity>> PUNCTURE_FISH = register("puncture_fish",
            EntityType.Builder.<PunctureFishEntity>of(PunctureFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new PunctureFishEntity(level))

                    .sized(0.9f, 2.7f));
    public static final DeferredHolder<EntityType<BaselayerAbyssalEntity>, ? extends EntityType<BaselayerAbyssalEntity>> BASELAYER_ABYSSAL = register("baselayer_abyssal",
            EntityType.Builder.<BaselayerAbyssalEntity>of(BaselayerAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new BaselayerAbyssalEntity(level))

                    .sized(0.7f, 1.5f));
    public static final DeferredHolder<EntityType<PredatorAbyssalEntity>, ? extends EntityType<PredatorAbyssalEntity>> PREDATOR_ABYSSAL = register("predator_abyssal",
            EntityType.Builder.<PredatorAbyssalEntity>of(PredatorAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new PredatorAbyssalEntity(level))

                    .sized(0.6f, 1.4f));
    public static final DeferredHolder<EntityType<GuideAbyssalEntity>, ? extends EntityType<GuideAbyssalEntity>> GUIDE_ABYSSAL = register("guide_abyssal",
            EntityType.Builder.<GuideAbyssalEntity>of(GuideAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new GuideAbyssalEntity(level))

                    .sized(1f, 2.5f));
    public static final DeferredHolder<EntityType<SplasherAbyssalEntity>, ? extends EntityType<SplasherAbyssalEntity>> SPLASHER_ABYSSAL = register("splasher_abyssal",
            EntityType.Builder.<SplasherAbyssalEntity>of(SplasherAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SplasherAbyssalEntity(level))

                    .sized(0.6f, 1.1f));
    public static final DeferredHolder<EntityType<FishSplashEntity>, ? extends EntityType<FishSplashEntity>> FISH_SPLASH = register("fish_splash",
            EntityType.Builder.<FishSplashEntity>of(FishSplashEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new FishSplashEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
    public static final DeferredHolder<EntityType<UmbrellaAbyssalEntity>, ? extends EntityType<UmbrellaAbyssalEntity>> UMBRELLA_ABYSSAL = register("umbrella_abyssal",
            EntityType.Builder.<UmbrellaAbyssalEntity>of(UmbrellaAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new UmbrellaAbyssalEntity(level))

                    .sized(0.8f, 1.5f));
    public static final DeferredHolder<EntityType<CrackerAbyssalEntity>, ? extends EntityType<CrackerAbyssalEntity>> CRACKER_ABYSSAL = register("cracker_abyssal",
            EntityType.Builder.<CrackerAbyssalEntity>of(CrackerAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new CrackerAbyssalEntity(level))

                    .sized(0.7f, 2f));
    public static final DeferredHolder<EntityType<CollectorProkaryoteEntity>, ? extends EntityType<CollectorProkaryoteEntity>> COLLECTOR_PROKARYOTE = register("collector_prokaryote",
            EntityType.Builder.<CollectorProkaryoteEntity>of(CollectorProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3)
                    .setCustomClientFactory((spawnEntity, level) -> new CollectorProkaryoteEntity(level))

                    .sized(0.5f, 0.5f));
    public static final DeferredHolder<EntityType<BoneFishEntity>, ? extends EntityType<BoneFishEntity>> BONE_FISH = register("bone_fish",
            EntityType.Builder.<BoneFishEntity>of(BoneFishEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new BoneFishEntity(level))

                    .sized(0.7f, 0.7f));
    public static final DeferredHolder<EntityType<ChiselerFishEntity>, ? extends EntityType<ChiselerFishEntity>> CHISELER_FISH = register("chiseler_fish",
            EntityType.Builder.<ChiselerFishEntity>of(ChiselerFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ChiselerFishEntity(level))

                    .sized(0.6f, 0.6f));
    public static final DeferredHolder<EntityType<FakerggShootEntity>, ? extends EntityType<FakerggShootEntity>> FAKERGG_SHOOT = register("fakergg_shoot",
            EntityType.Builder.<FakerggShootEntity>of(FakerggShootEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new FakerggShootEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.4f, 0.4f));
    public static final DeferredHolder<EntityType<PregnantFishEntity>, ? extends EntityType<PregnantFishEntity>> PREGNANT_FISH = register("pregnant_fish",
            EntityType.Builder.<PregnantFishEntity>of(PregnantFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new PregnantFishEntity(level))

                    .sized(0.7f, 1.1f));
    public static final DeferredHolder<EntityType<FakeOffspringEntity>, ? extends EntityType<FakeOffspringEntity>> FAKE_OFFSPRING = register("fake_offspring",
            EntityType.Builder.<FakeOffspringEntity>of(FakeOffspringEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new FakeOffspringEntity(level))

                    .sized(0.6f, 0.6f));
    public static final DeferredHolder<EntityType<FleefishBulletEntity>, ? extends EntityType<FleefishBulletEntity>> FLEEFISH_BULLET = register("fleefish_bullet",
            EntityType.Builder.<FleefishBulletEntity>of(FleefishBulletEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new FleefishBulletEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.4f, 0.4f));
    public static final DeferredHolder<EntityType<FleeFishEntity>, ? extends EntityType<FleeFishEntity>> FLEE_FISH = register("flee_fish",
            EntityType.Builder.<FleeFishEntity>of(FleeFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new FleeFishEntity(level))

                    .sized(0.8f, 1.1f));
    public static final DeferredHolder<EntityType<RouteShaperEntity>, ? extends EntityType<RouteShaperEntity>> ROUTE_SHAPER = register("route_shaper",
            EntityType.Builder.<RouteShaperEntity>of(RouteShaperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new RouteShaperEntity(level))

                    .sized(1.8f, 4f));
    public static final DeferredHolder<EntityType<RouteFractalEntity>, ? extends EntityType<RouteFractalEntity>> ROUTE_FRACTAL = register("route_fractal",
            EntityType.Builder.<RouteFractalEntity>of(RouteFractalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new RouteFractalEntity(level))

                    .sized(0.7f, 1.5f));
    public static final DeferredHolder<EntityType<TellerShotEntity>, ? extends EntityType<TellerShotEntity>> TELLER_SHOT = register("teller_shot",
            EntityType.Builder.<TellerShotEntity>of(TellerShotEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new TellerShotEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.4f, 0.4f));
    public static final DeferredHolder<EntityType<FirstTellerEntity>, ? extends EntityType<FirstTellerEntity>> FIRST_TO_TALK = register("first_to_talk",
            EntityType.Builder.<FirstTellerEntity>of(FirstTellerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new FirstTellerEntity(level))

                    .sized(0.9f, 2.7f));
    public static final DeferredHolder<EntityType<ReaperPetEntity>, ? extends EntityType<ReaperPetEntity>> REAPER_PET = register("reaper_pet",
            EntityType.Builder.<ReaperPetEntity>of(ReaperPetEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ReaperPetEntity(level))

                    .sized(0.6f, 1.3f));
    public static final DeferredHolder<EntityType<BishopFishEntity>, ? extends EntityType<BishopFishEntity>> BISHOP_FISH = register("bishop_fish",
            EntityType.Builder.<BishopFishEntity>of(BishopFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(18).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new BishopFishEntity(level))

                    .sized(1.4f, 2.2f));
    public static final DeferredHolder<EntityType<TideBishopEntity>, ? extends EntityType<TideBishopEntity>> TIDE_BISHOP = register("tide_bishop",
            EntityType.Builder.<TideBishopEntity>of(TideBishopEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new TideBishopEntity(level))

                    .sized(1.1f, 2.2f));
    public static final DeferredHolder<EntityType<SonsEntity>, ? extends EntityType<SonsEntity>> SONS = register("sons",
            EntityType.Builder.<SonsEntity>of(SonsEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SonsEntity(level))

                    .sized(0.5f, 0.5f));
    public static final DeferredHolder<EntityType<FloaterProkaryoteEntity>, ? extends EntityType<FloaterProkaryoteEntity>> FLOATER_PROKARYOTE = register("floater_prokaryote",
            EntityType.Builder.<FloaterProkaryoteEntity>of(FloaterProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new FloaterProkaryoteEntity(level))

                    .sized(0.6f, 1.5f));
    public static final DeferredHolder<EntityType<ChitinGolemEntity>, ? extends EntityType<ChitinGolemEntity>> CHITIN_GOLEM = register("chitin_golem",
            EntityType.Builder.<ChitinGolemEntity>of(ChitinGolemEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ChitinGolemEntity(level))

                    .sized(1.75f, 4f));
    public static final DeferredHolder<EntityType<TideDeathrepellerEntity>, ? extends EntityType<TideDeathrepellerEntity>> TIDE_DEATHREPELLER = register("tide_deathrepeller",
            EntityType.Builder.<TideDeathrepellerEntity>of(TideDeathrepellerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new TideDeathrepellerEntity(level))

                    .sized(1.5f, 2f));
    public static final DeferredHolder<EntityType<MegaChestEntity>, ? extends EntityType<MegaChestEntity>> MEGA_CHEST = register("mega_chest",
            EntityType.Builder.<MegaChestEntity>of(MegaChestEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(14).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new MegaChestEntity(level))

                    .sized(0.8f, 0.9f));
    public static final DeferredHolder<EntityType<ApostleProkaryoteEntity>, ? extends EntityType<ApostleProkaryoteEntity>> APOSTLE_PROKARYOTE = register("apostle_prokaryote",
            EntityType.Builder.<ApostleProkaryoteEntity>of(ApostleProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ApostleProkaryoteEntity(level))

                    .sized(0.6f, 1.7f));
    public static final DeferredHolder<EntityType<HighmoreShootEntity>, ? extends EntityType<HighmoreShootEntity>> HIGHMORE_SHOOT = register("highmore_shoot",
            EntityType.Builder.<HighmoreShootEntity>of(HighmoreShootEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new HighmoreShootEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.4f, 0.4f));
    public static final DeferredHolder<EntityType<HighmoreEntity>, ? extends EntityType<HighmoreEntity>> HIGHMORE = register("highmore",
            EntityType.Builder.<HighmoreEntity>of(HighmoreEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(18).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new HighmoreEntity(level))

                    .sized(1.4f, 1.6f));
    public static final DeferredHolder<EntityType<AccumulatorProkaryoteEntity>, ? extends EntityType<AccumulatorProkaryoteEntity>> ACCUMULATOR_PROKARYOTE = register("accumulator_prokaryote",
            EntityType.Builder.<AccumulatorProkaryoteEntity>of(AccumulatorProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .setCustomClientFactory((spawnEntity, level) -> new AccumulatorProkaryoteEntity(level))

                    .sized(0.5f, 1f));
    public static final DeferredHolder<EntityType<AccumulatorCloneEntity>, ? extends EntityType<AccumulatorCloneEntity>> ACCUMULATOR_CLONE = register("accumulator_clone",
            EntityType.Builder.<AccumulatorCloneEntity>of(AccumulatorCloneEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new AccumulatorCloneEntity(level))

                    .sized(0.5f, 1f));
    public static final DeferredHolder<EntityType<FeederProkaryoteEntity>, ? extends EntityType<FeederProkaryoteEntity>> FEEDER_PROKARYOTE = register("feeder_prokaryote",
            EntityType.Builder.<FeederProkaryoteEntity>of(FeederProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new FeederProkaryoteEntity(level))

                    .sized(0.8f, 1.1f));
    public static final DeferredHolder<EntityType<ChestFishEntity>, ? extends EntityType<ChestFishEntity>> CHEST_FISH = register("chest_fish",
            EntityType.Builder.<ChestFishEntity>of(ChestFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ChestFishEntity(level))

                    .sized(0.9f, 0.9f));
    public static final DeferredHolder<EntityType<SpikeChestEntity>, ? extends EntityType<SpikeChestEntity>> SPIKE_CHEST = register("spike_chest",
            EntityType.Builder.<SpikeChestEntity>of(SpikeChestEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SpikeChestEntity(level))

                    .sized(0.9f, 0.9f));
    public static final DeferredHolder<EntityType<SkadiEntity>, ? extends EntityType<SkadiEntity>> SKADI = register("skadi",
            EntityType.Builder.<SkadiEntity>of(SkadiEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SkadiEntity(level))

                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<DepositerProkaryoteEntity>, ? extends EntityType<DepositerProkaryoteEntity>> DEPOSITER_PROKARYOTE = register("depositer_prokaryote",
            EntityType.Builder.<DepositerProkaryoteEntity>of(DepositerProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .setCustomClientFactory((spawnEntity, level) -> new DepositerProkaryoteEntity(level))

                    .sized(0.625f, 1f));
    public static final DeferredHolder<EntityType<OceanizedVillagerEntity>, ? extends EntityType<OceanizedVillagerEntity>> OCEANIZED_VILLAGER = register("oceanized_villager",
            EntityType.Builder.<OceanizedVillagerEntity>of(OceanizedVillagerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedVillagerEntity(level))

                    .sized(0.6f, 2f));
    public static final DeferredHolder<EntityType<OceanizedVindicatorEntity>, ? extends EntityType<OceanizedVindicatorEntity>> OCEANIZED_VINDICATOR = register("oceanized_vindicator",
            EntityType.Builder.<OceanizedVindicatorEntity>of(OceanizedVindicatorEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedVindicatorEntity(level))

                    .sized(0.6f, 2f));
    public static final DeferredHolder<EntityType<ShotOceanArrowEntity>, ? extends EntityType<ShotOceanArrowEntity>> SHOT_OCEAN_ARROW = register("shot_ocean_arrow",
            EntityType.Builder.<ShotOceanArrowEntity>of(ShotOceanArrowEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new ShotOceanArrowEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
    public static final DeferredHolder<EntityType<OceanizedPillagerEntity>, ? extends EntityType<OceanizedPillagerEntity>> OCEANIZED_PILLAGER = register("oceanized_pillager",
            EntityType.Builder.<OceanizedPillagerEntity>of(OceanizedPillagerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedPillagerEntity(level))

                    .sized(0.6f, 1.9f));
    public static final DeferredHolder<EntityType<AnchorFlyEntity>, ? extends EntityType<AnchorFlyEntity>> ANCHOR_FLY = register("anchor_fly",
            EntityType.Builder.<AnchorFlyEntity>of(AnchorFlyEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new AnchorFlyEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.2f, 0.2f));
    public static final DeferredHolder<EntityType<OceanizedPigEntity>, ? extends EntityType<OceanizedPigEntity>> OCEANIZED_PIG = register("oceanized_pig",
            EntityType.Builder.<OceanizedPigEntity>of(OceanizedPigEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedPigEntity(level))

                    .sized(0.6f, 1f));
    public static final DeferredHolder<EntityType<OceanizedCowEntity>, ? extends EntityType<OceanizedCowEntity>> OCEANIZED_COW = register("oceanized_cow",
            EntityType.Builder.<OceanizedCowEntity>of(OceanizedCowEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedCowEntity(level))

                    .sized(0.6f, 1.375f));
    public static final DeferredHolder<EntityType<OceanizedSheepEntity>, ? extends EntityType<OceanizedSheepEntity>> OCEANIZED_SHEEP = register("oceanized_sheep",
            EntityType.Builder.<OceanizedSheepEntity>of(OceanizedSheepEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedSheepEntity(level))

                    .sized(0.6f, 1f));
    public static final DeferredHolder<EntityType<OceanizedHorseEntity>, ? extends EntityType<OceanizedHorseEntity>> OCEANIZED_HORSE = register("oceanized_horse",
            EntityType.Builder.<OceanizedHorseEntity>of(OceanizedHorseEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedHorseEntity(level))

                    .sized(1.25f, 1.8f));
    public static final DeferredHolder<EntityType<OceanizedPiglinEntity>, ? extends EntityType<OceanizedPiglinEntity>> OCEANIZED_PIGLIN = register("oceanized_piglin", EntityType.Builder.<OceanizedPiglinEntity>of(OceanizedPiglinEntity::new, MobCategory.MONSTER)
            .setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedPiglinEntity(level)).fireImmune().sized(0.6f, 2f));
    public static final DeferredHolder<EntityType<OceanizedBruteEntity>, ? extends EntityType<OceanizedBruteEntity>> OCEANIZED_BRUTE = register("oceanized_brute", EntityType.Builder.<OceanizedBruteEntity>of(OceanizedBruteEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(10).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedBruteEntity(level)).fireImmune().sized(0.6f, 2f));
    public static final DeferredHolder<EntityType<OceanizedSpiderEntity>, ? extends EntityType<OceanizedSpiderEntity>> OCEANIZED_SPIDER = register("oceanized_spider",
            EntityType.Builder.<OceanizedSpiderEntity>of(OceanizedSpiderEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(7).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedSpiderEntity(level))

                    .sized(0.9f, 0.7f));
    public static final DeferredHolder<EntityType<OceanizedEndermanEntity>, ? extends EntityType<OceanizedEndermanEntity>> OCEANIZED_ENDERMAN = register("oceanized_enderman",
            EntityType.Builder.<OceanizedEndermanEntity>of(OceanizedEndermanEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(10).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedEndermanEntity(level))

                    .sized(0.6f, 3f));
    public static final DeferredHolder<EntityType<OceanizedWolfEntity>, ? extends EntityType<OceanizedWolfEntity>> OCEANIZED_WOLF = register("oceanized_wolf",
            EntityType.Builder.<OceanizedWolfEntity>of(OceanizedWolfEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(7).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedWolfEntity(level))

                    .sized(0.7f, 0.8f));
    public static final DeferredHolder<EntityType<OceanizedDogEntity>, ? extends EntityType<OceanizedDogEntity>> OCEANIZED_DOG = register("oceanized_dog",
            EntityType.Builder.<OceanizedDogEntity>of(OceanizedDogEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedDogEntity(level))

                    .sized(0.7f, 0.8f));
    public static final DeferredHolder<EntityType<OceanizedRavagerEntity>, ? extends EntityType<OceanizedRavagerEntity>> OCEANIZED_RAVAGER = register("oceanized_ravager",
            EntityType.Builder.<OceanizedRavagerEntity>of(OceanizedRavagerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(10).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedRavagerEntity(level))

                    .sized(1.9f, 2.5f));
    public static final DeferredHolder<EntityType<OceanziedWitchEntity>, ? extends EntityType<OceanziedWitchEntity>> OCEANIZED_WITCH = register("oceanized_witch",
            EntityType.Builder.<OceanziedWitchEntity>of(OceanziedWitchEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanziedWitchEntity(level))

                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<ThrowablePotionEntity>, ? extends EntityType<ThrowablePotionEntity>> THROWABLE_POTION = register("throwable_potion", EntityType.Builder.<ThrowablePotionEntity>of(ThrowablePotionEntity::new, MobCategory.MISC)
            .setCustomClientFactory((spawnEntity, level) -> new ThrowablePotionEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.2f, 0.2f));
    public static final DeferredHolder<EntityType<IzumikOffspringEntity>, ? extends EntityType<IzumikOffspringEntity>> IZUMIK_OFFSPRING = register("izumik_offspring",
            EntityType.Builder.<IzumikOffspringEntity>of(IzumikOffspringEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new IzumikOffspringEntity(level))

                    .sized(0.5f, 0.9f));
    public static final DeferredHolder<EntityType<CaerulaOffspringEntity>, ? extends EntityType<CaerulaOffspringEntity>> CAERULA_OFFSPRING = register("caerula_offspring",
            EntityType.Builder.<CaerulaOffspringEntity>of(CaerulaOffspringEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new CaerulaOffspringEntity(level))

                    .sized(0.5f, 0.9f));
    public static final DeferredHolder<EntityType<IzumikEntity>, ? extends EntityType<IzumikEntity>> IZUMIK = register("izumik",
            EntityType.Builder.<IzumikEntity>of(IzumikEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(21).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new IzumikEntity(level)).fireImmune().sized(3.3f, 9f));
    public static final DeferredHolder<EntityType<DivicellularGoEntity>, ? extends EntityType<DivicellularGoEntity>> DIVICELLULAR_GO = register("divicellular_go",
            EntityType.Builder.<DivicellularGoEntity>of(DivicellularGoEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new DivicellularGoEntity(level))

                    .sized(0.5f, 1f));
    public static final DeferredHolder<EntityType<OceanizedEvokerEntity>, ? extends EntityType<OceanizedEvokerEntity>> OCEANIZED_EVOKER = register("oceanized_evoker",
            EntityType.Builder.<OceanizedEvokerEntity>of(OceanizedEvokerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedEvokerEntity(level))

                    .sized(0.6f, 1.9f));
    public static final DeferredHolder<EntityType<JuniorWarriorPriestEntity>, ? extends EntityType<JuniorWarriorPriestEntity>> JUNIOR_WARRIOR_PRIEST = register("junior_warrior_priest",
            EntityType.Builder.<JuniorWarriorPriestEntity>of(JuniorWarriorPriestEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new JuniorWarriorPriestEntity(level))

                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<WarriorPriestEntity>, ? extends EntityType<WarriorPriestEntity>> WARRIOR_PRIEST = register("warrior_priest",
            EntityType.Builder.<WarriorPriestEntity>of(WarriorPriestEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new WarriorPriestEntity(level))

                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<CorrectionalPhalanxyInfantryEntity>, ? extends EntityType<CorrectionalPhalanxyInfantryEntity>> CORRECTIONAL_PHALANXY_INFANTRY = register("correctional_phalanxy_infantry",
            EntityType.Builder.<CorrectionalPhalanxyInfantryEntity>of(CorrectionalPhalanxyInfantryEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .setCustomClientFactory((spawnEntity, level) -> new CorrectionalPhalanxyInfantryEntity(level))

                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<CorrectinalPhalaxVanguardEntity>, ? extends EntityType<CorrectinalPhalaxVanguardEntity>> CORRECTIONAL_PHALAX_VANGUARD = register("correctional_phalax_vanguard",
            EntityType.Builder.<CorrectinalPhalaxVanguardEntity>of(CorrectinalPhalaxVanguardEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)
                    .setCustomClientFactory((spawnEntity, level) -> new CorrectinalPhalaxVanguardEntity(level))

                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<TribunalHealerEntity>, ? extends EntityType<TribunalHealerEntity>> TRIBUNAL_HEALER = register("tribunal_healer",
            EntityType.Builder.<TribunalHealerEntity>of(TribunalHealerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new TribunalHealerEntity(level))

                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<HealBullletEntity>, ? extends EntityType<HealBullletEntity>> HEAL_BULLLET = register("heal_bulllet",
            EntityType.Builder.<HealBullletEntity>of(HealBullletEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new HealBullletEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.1f, 0.1f));
    public static final DeferredHolder<EntityType<MartusEntity>, ? extends EntityType<MartusEntity>> MARTUS = register("martus",
            EntityType.Builder.<MartusEntity>of(MartusEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new MartusEntity(level)).fireImmune().sized(0.7f, 2.8f));
    public static final DeferredHolder<EntityType<TheAbandonedEntity>, ? extends EntityType<TheAbandonedEntity>> THE_ABANDONED = register("the_abandoned",
            EntityType.Builder.<TheAbandonedEntity>of(TheAbandonedEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new TheAbandonedEntity(level))

                    .sized(0.7f, 2.25f));
    public static final DeferredHolder<EntityType<AbandonedShootEntity>, ? extends EntityType<AbandonedShootEntity>> ABANDONED_SHOOT = register("abandoned_shoot",
            EntityType.Builder.<AbandonedShootEntity>of(AbandonedShootEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new AbandonedShootEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
    public static final DeferredHolder<EntityType<GunmuEntity>, ? extends EntityType<GunmuEntity>> GUNMU = register("gunmu",
            EntityType.Builder.<GunmuEntity>of(GunmuEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new GunmuEntity(level)).fireImmune().sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<OceanizedWardenEntity>, ? extends EntityType<OceanizedWardenEntity>> OCEANIZED_WARDEN = register("oceanized_warden", EntityType.Builder.<OceanizedWardenEntity>of(OceanizedWardenEntity::new, MobCategory.MONSTER)
            .setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedWardenEntity(level)).fireImmune().sized(1.2f, 3.1f));
    public static final DeferredHolder<EntityType<OceanizedCatEntity>, ? extends EntityType<OceanizedCatEntity>> OCEANIZED_CAT = register("oceanized_cat",
            EntityType.Builder.<OceanizedCatEntity>of(OceanizedCatEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedCatEntity(level))

                    .sized(0.5f, 0.625f));
    public static final DeferredHolder<EntityType<SuperBigCatEntity>, ? extends EntityType<SuperBigCatEntity>> SUPER_BIG_CAT = register("super_big_cat",
            EntityType.Builder.<SuperBigCatEntity>of(SuperBigCatEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SuperBigCatEntity(level))

                    .sized(3.5f, 4f));
    public static final DeferredHolder<EntityType<ComplexChitinGolemEntity>, ? extends EntityType<ComplexChitinGolemEntity>> COMPLEX_CHITIN_GOLEM = register("complex_chitin_golem", EntityType.Builder.<ComplexChitinGolemEntity>of(ComplexChitinGolemEntity::new, MobCategory.MONSTER)
            .setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ComplexChitinGolemEntity(level)).fireImmune().sized(1.75f, 4f));
    public static final DeferredHolder<EntityType<OceanizedWardenisEntity>, ? extends EntityType<OceanizedWardenisEntity>> OCEANIZED_WARDENIS = register("oceanized_wardenis", EntityType.Builder.<OceanizedWardenisEntity>of(OceanizedWardenisEntity::new, MobCategory.MONSTER)
            .setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedWardenisEntity(level)).fireImmune().sized(0.6f, 1.85f));
    public static final DeferredHolder<EntityType<NucleicMaleficentEntity>, ? extends EntityType<NucleicMaleficentEntity>> NUCLEIC_MALEFICENT = register("nucleic_maleficent",
            EntityType.Builder.<NucleicMaleficentEntity>of(NucleicMaleficentEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new NucleicMaleficentEntity(level))

                    .sized(0.7f, 1.5f));
    public static final DeferredHolder<EntityType<OceanizedWitherEntity>, ? extends EntityType<OceanizedWitherEntity>> OCEANIZED_WITHER = register("oceanized_wither", EntityType.Builder.<OceanizedWitherEntity>of(OceanizedWitherEntity::new, MobCategory.MONSTER)
            .setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedWitherEntity(level)).fireImmune().sized(1.1f, 3.5f));
    public static final DeferredHolder<EntityType<WitherShootPreEntity>, ? extends EntityType<WitherShootPreEntity>> WITHER_SHOOT_PRE = register("wither_shoot_pre",
            EntityType.Builder.<WitherShootPreEntity>of(WitherShootPreEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new WitherShootPreEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.1f, 0.1f));
    public static final DeferredHolder<EntityType<OceanizedWitheriaEntity>, ? extends EntityType<OceanizedWitheriaEntity>> OCEANIZED_WITHERIA = register("oceanized_witheria", EntityType.Builder.<OceanizedWitheriaEntity>of(OceanizedWitheriaEntity::new, MobCategory.MONSTER)
            .setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedWitheriaEntity(level)).fireImmune().sized(0.7f, 2.5f));
    public static final DeferredHolder<EntityType<TheLastKnightEntity>, ? extends EntityType<TheLastKnightEntity>> THE_LAST_KNIGHT = register("the_last_knight", EntityType.Builder.<TheLastKnightEntity>of(TheLastKnightEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new TheLastKnightEntity(level)).fireImmune().sized(1f, 3.6f));
    public static final DeferredHolder<EntityType<LastKnightAndHorseEntity>, ? extends EntityType<LastKnightAndHorseEntity>> LAST_KNIGHT_AND_HORSE = register("last_knight_and_horse", EntityType.Builder.<LastKnightAndHorseEntity>of(LastKnightAndHorseEntity::new, MobCategory.MONSTER)
            .setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new LastKnightAndHorseEntity(level)).fireImmune().sized(1.2f, 4f));
    public static final DeferredHolder<EntityType<RocinanteEntity>, ? extends EntityType<RocinanteEntity>> ROCINANTE = register("rocinante",
            EntityType.Builder.<RocinanteEntity>of(RocinanteEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new RocinanteEntity(level))

                    .sized(1.2f, 3f));
    public static final DeferredHolder<EntityType<ApocataEntity>, ? extends EntityType<ApocataEntity>> APOCATA = register("apocata",
            EntityType.Builder.<ApocataEntity>of(ApocataEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ApocataEntity(level))

                    .sized(0.6f, 1.85f));
    public static final DeferredHolder<EntityType<OceanizedFoxEntity>, ? extends EntityType<OceanizedFoxEntity>> OCEANIZED_FOX = register("oceanized_fox",
            EntityType.Builder.<OceanizedFoxEntity>of(OceanizedFoxEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedFoxEntity(level))

                    .sized(0.6f, 0.7f));
    public static final DeferredHolder<EntityType<TidutantExcrescenceEntity>, ? extends EntityType<TidutantExcrescenceEntity>> TIDUTANT_EXCRESCENCE = register("tidutant_excrescence",
            EntityType.Builder.<TidutantExcrescenceEntity>of(TidutantExcrescenceEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new TidutantExcrescenceEntity(level))

                    .sized(0.5f, 0.4f));
    public static final DeferredHolder<EntityType<OceanizedPolarBearEntity>, ? extends EntityType<OceanizedPolarBearEntity>> OCEANIZED_POLAR_BEAR = register("oceanized_polar_bear",
            EntityType.Builder.<OceanizedPolarBearEntity>of(OceanizedPolarBearEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedPolarBearEntity(level))

                    .sized(1f, 1.25f));
    public static final DeferredHolder<EntityType<TideutantRockSpiderEntity>, ? extends EntityType<TideutantRockSpiderEntity>> TIDUTANT_ROCK_SPIDER = register("tidutant_rock_spider",
            EntityType.Builder.<TideutantRockSpiderEntity>of(TideutantRockSpiderEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new TideutantRockSpiderEntity(level))

                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<EndspeakerEntity>, ? extends EntityType<EndspeakerEntity>> ENDSPEAKER = register("endspeaker",
            EntityType.Builder.<EndspeakerEntity>of(EndspeakerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(14).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new EndspeakerEntity(level))

                    .sized(1f, 3.375f));
    public static final DeferredHolder<EntityType<LineringPathshaperEntity>, ? extends EntityType<LineringPathshaperEntity>> LINGERING_PATHSHAPER = register("lingering_pathshaper",
            EntityType.Builder.<LineringPathshaperEntity>of(LineringPathshaperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new LineringPathshaperEntity(level))

                    .sized(1.8f, 4f));
    public static final DeferredHolder<EntityType<LingeringFractalEntity>, ? extends EntityType<LingeringFractalEntity>> LINGERING_FRACTAL = register("lingering_fractal",
            EntityType.Builder.<LingeringFractalEntity>of(LingeringFractalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new LingeringFractalEntity(level))

                    .sized(0.7f, 1.5f));
    public static final DeferredHolder<EntityType<LittleHelperEntity>, ? extends EntityType<LittleHelperEntity>> LITTLE_HELPER = register("little_helper", EntityType.Builder.<LittleHelperEntity>of(LittleHelperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new LittleHelperEntity(level)).fireImmune().sized(0.875f, 0.2f));
    public static final DeferredHolder<EntityType<Al1SHelperEntity>, ? extends EntityType<Al1SHelperEntity>> AL_1_S_HELPER = register("al_1_s_helper", EntityType.Builder.<Al1SHelperEntity>of(Al1SHelperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new Al1SHelperEntity(level)).fireImmune().sized(0.875f, 0.2f));
    public static final DeferredHolder<EntityType<UlpiansEntity>, ? extends EntityType<UlpiansEntity>> ULPIANS = register("ulpians",
            EntityType.Builder.<UlpiansEntity>of(UlpiansEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new UlpiansEntity(level))

                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<GladiiaEntity>, ? extends EntityType<GladiiaEntity>> GLADIIA = register("gladiia",
            EntityType.Builder.<GladiiaEntity>of(GladiiaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new GladiiaEntity(level))

                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<GladiiaWhirlEntity>, ? extends EntityType<GladiiaWhirlEntity>> GLADIIA_WHIRL = register("gladiia_whirl",
            EntityType.Builder.<GladiiaWhirlEntity>of(GladiiaWhirlEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new GladiiaWhirlEntity(level))

                    .sized(2f, 2f));
    public static final DeferredHolder<EntityType<SpecterEntity>, ? extends EntityType<SpecterEntity>> SPECTER = register("specter",
            EntityType.Builder.<SpecterEntity>of(SpecterEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SpecterEntity(level))

                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<SpecterDollEntity>, ? extends EntityType<SpecterDollEntity>> SPECTER_DOLL = register("specter_doll",
            EntityType.Builder.<SpecterDollEntity>of(SpecterDollEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SpecterDollEntity(level))

                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<IreneEntity>, ? extends EntityType<IreneEntity>> IRENE = register("irene",
            EntityType.Builder.<IreneEntity>of(IreneEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new IreneEntity(level))

                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<TideChimeraEntity>, ? extends EntityType<TideChimeraEntity>> TIDE_CHIMERA = register("tide_chimera",
            EntityType.Builder.<TideChimeraEntity>of(TideChimeraEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new TideChimeraEntity(level))

                    .sized(1.8f, 4.65f));
    public static final DeferredHolder<EntityType<SkadiCorruptedEntity>, ? extends EntityType<SkadiCorruptedEntity>> SKADI_CORRUPTED = register("skadi_corrupted", EntityType.Builder.<SkadiCorruptedEntity>of(SkadiCorruptedEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SkadiCorruptedEntity(level)).fireImmune().sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<OceanizeRabbitEntity>, ? extends EntityType<OceanizeRabbitEntity>> OCEANIZE_RABBIT = register("oceanize_rabbit",
            EntityType.Builder.<OceanizeRabbitEntity>of(OceanizeRabbitEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizeRabbitEntity(level))

                    .sized(0.4f, 0.5f));
    public static final DeferredHolder<EntityType<SaintCarmenEntity>, ? extends EntityType<SaintCarmenEntity>> SAINT_CARMEN = register("saint_carmen",
            EntityType.Builder.<SaintCarmenEntity>of(SaintCarmenEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new SaintCarmenEntity(level))

                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<CarmenBulletEntity>, ? extends EntityType<CarmenBulletEntity>> CARMEN_BULLET = register("carmen_bullet",
            EntityType.Builder.<CarmenBulletEntity>of(CarmenBulletEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new CarmenBulletEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
    public static final DeferredHolder<EntityType<OceanizedIllusionerEntity>, ? extends EntityType<OceanizedIllusionerEntity>> OCEANIZED_ILLUSIONER = register("oceanized_illusioner",
            EntityType.Builder.<OceanizedIllusionerEntity>of(OceanizedIllusionerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(10).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedIllusionerEntity(level))

                    .sized(0.6f, 1.9f));
    public static final DeferredHolder<EntityType<OceanIllusionEntity>, ? extends EntityType<OceanIllusionEntity>> OCEAN_ILLUSION = register("ocean_illusion",
            EntityType.Builder.<OceanIllusionEntity>of(OceanIllusionEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanIllusionEntity(level))

                    .sized(0.6f, 1.9f));
    public static final DeferredHolder<EntityType<FlamarineStatueEntity>, ? extends EntityType<FlamarineStatueEntity>> FLAMARINE_STATUE = register("flamarine_statue", EntityType.Builder.<FlamarineStatueEntity>of(FlamarineStatueEntity::new, MobCategory.MONSTER)
            .setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new FlamarineStatueEntity(level)).fireImmune().sized(0.7f, 2f));
    public static final DeferredHolder<EntityType<NautilusHeadhunterEntity>, ? extends EntityType<NautilusHeadhunterEntity>> NAUTILUS_HEADHUNTER = register("nautilus_headhunter",
            EntityType.Builder.<NautilusHeadhunterEntity>of(NautilusHeadhunterEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3)
                    .setCustomClientFactory((spawnEntity, level) -> new NautilusHeadhunterEntity(level))

                    .sized(0.5f, 0.5f));
    public static final DeferredHolder<EntityType<XantisEntity>, ? extends EntityType<XantisEntity>> XANTIS = register("xantis",
            EntityType.Builder.<XantisEntity>of(XantisEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new XantisEntity(level))

                    .sized(0.5f, 0.68f));
    public static final DeferredHolder<EntityType<FlamarineGolemEntity>, ? extends EntityType<FlamarineGolemEntity>> FLAMARINE_GOLEM = register("flamarine_golem", EntityType.Builder.<FlamarineGolemEntity>of(FlamarineGolemEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new FlamarineGolemEntity(level)).fireImmune().sized(1f, 2.75f));
    public static final DeferredHolder<EntityType<OceanizedVexEntity>, ? extends EntityType<OceanizedVexEntity>> OCEANIZED_VEX = register("oceanized_vex",
            EntityType.Builder.<OceanizedVexEntity>of(OceanizedVexEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedVexEntity(level))

                    .sized(0.4f, 0.8f));
    public static final DeferredHolder<EntityType<IsharmlaEntity>, ? extends EntityType<IsharmlaEntity>> ISHARMLA = register("isharmla",
            EntityType.Builder.<IsharmlaEntity>of(IsharmlaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new IsharmlaEntity(level))

                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<QunyouWantedIsharmlaEntity>, ? extends EntityType<QunyouWantedIsharmlaEntity>> QUNYOU_WANTED_ISHARMLA = register("qunyou_wanted_isharmla",
            EntityType.Builder.<QunyouWantedIsharmlaEntity>of(QunyouWantedIsharmlaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3)
                    .setCustomClientFactory((spawnEntity, level) -> new QunyouWantedIsharmlaEntity(level))

                    .sized(20f, 20f));
    public static final DeferredHolder<EntityType<IsharmlaTearEntity>, ? extends EntityType<IsharmlaTearEntity>> ISHARMLA_TEAR = register("isharmla_tear",
            EntityType.Builder.<IsharmlaTearEntity>of(IsharmlaTearEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new IsharmlaTearEntity(level))

                    .sized(1f, 0.5f));
    public static final DeferredHolder<EntityType<PrayerSplashEntity>, ? extends EntityType<PrayerSplashEntity>> PRAYER_SPLASH = register("prayer_splash",
            EntityType.Builder.<PrayerSplashEntity>of(PrayerSplashEntity::new, MobCategory.MISC).setCustomClientFactory((spawnEntity, level) -> new PrayerSplashEntity(level)).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
    public static final DeferredHolder<EntityType<CompassionPrayerEntity>, ? extends EntityType<CompassionPrayerEntity>> COMPASSION_PRAYER = register("compassion_prayer",
            EntityType.Builder.<CompassionPrayerEntity>of(CompassionPrayerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new CompassionPrayerEntity(level))

                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<OceanizedEnderinaEntity>, ? extends EntityType<OceanizedEnderinaEntity>> OCEANIZED_ENDERINA = register("oceanized_enderina",
            EntityType.Builder.<OceanizedEnderinaEntity>of(OceanizedEnderinaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedEnderinaEntity(level)));

    public static final DeferredHolder<EntityType<OceanizedEnderDragonEntity>, ? extends EntityType<OceanizedEnderDragonEntity>> OCEANIZED_ENDER_DRAGON = register("oceanized_ender_dragon",
            EntityType.Builder.<OceanizedEnderDragonEntity>of(OceanizedEnderDragonEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedEnderDragonEntity(level)).fireImmune()

                    .sized(16.0f, 8.0f));
    public static final DeferredHolder<EntityType<MoistDragonBreathEntity>, ? extends EntityType<MoistDragonBreathEntity>> MOIST_DRAGON_BREATH = register("moist_dragon_breath",
            EntityType.Builder.<MoistDragonBreathEntity>of(MoistDragonBreathEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new MoistDragonBreathEntity(level))

                    .sized(0.5f, 0.5f));
    public static final DeferredHolder<EntityType<MoistEnderCrystalEntity>, ? extends EntityType<MoistEnderCrystalEntity>> MOIST_ENDER_CRYSTAL = register("moist_ender_crystal",
            EntityType.Builder.<MoistEnderCrystalEntity>of(MoistEnderCrystalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new MoistEnderCrystalEntity(level))

                    .sized(2f, 2f));
    public static final DeferredHolder<EntityType<ThirsterEntity>, ? extends EntityType<ThirsterEntity>> THIRSTER = register("thirster",
            EntityType.Builder.<ThirsterEntity>of(ThirsterEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ThirsterEntity(level))

                    .sized(1.5f, 1.75f));
    public static final DeferredHolder<EntityType<AbsorberLimbEntity>, ? extends EntityType<AbsorberLimbEntity>> ABSORBER_LIMB = register("absorber_limb",
            EntityType.Builder.<AbsorberLimbEntity>of(AbsorberLimbEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new AbsorberLimbEntity(level))

                    .sized(0.4f, 1f));
    public static final DeferredHolder<EntityType<ScreamChestFishEntity>, ? extends EntityType<ScreamChestFishEntity>> SCREAM_CHEST_FISH = register("scream_chest_fish",
            EntityType.Builder.<ScreamChestFishEntity>of(ScreamChestFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new ScreamChestFishEntity(level))

                    .sized(0.9f, 0.9f));
    public static final DeferredHolder<EntityType<OceanizedChickenEntity>, ? extends EntityType<OceanizedChickenEntity>> OCEANIZED_CHICKEN = register("oceanized_chicken",
            EntityType.Builder.<OceanizedChickenEntity>of(OceanizedChickenEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedChickenEntity(level))

                    .sized(0.5f, 0.875f));
    public static final DeferredHolder<EntityType<NetherseaSlimeEntity>, ? extends EntityType<NetherseaSlimeEntity>> NETHERSEA_SLIME = register("nethersea_slime",
            EntityType.Builder.<NetherseaSlimeEntity>of(NetherseaSlimeEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new NetherseaSlimeEntity(level))

                    .sized(1f, 1f));
    public static final DeferredHolder<EntityType<OceanizedShulkerEntity>, ? extends EntityType<OceanizedShulkerEntity>> OCEANIZED_SHULKER = register("oceanized_shulker",
            EntityType.Builder.<OceanizedShulkerEntity>of(OceanizedShulkerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).setCustomClientFactory((spawnEntity, level) -> new OceanizedShulkerEntity(level))

                    .sized(1f, 1f));
    private static final List<DeferredHolder<? extends EntityType<? extends LivingEntity>, ? extends EntityType<? extends LivingEntity>>> LIVING_ENTITY_TYPES;
    private static final List<DeferredHolder<? extends EntityType<? extends LivingEntity>, ? extends EntityType<? extends LivingEntity>>> SUMMONABLE_ENTITY_TYPES;

    static {
        addAttributeRegistration(RUN_FISH, RunFishEntity::createAttributes);
        addAttributeRegistration(SLIDER_FISH, SliderFishEntity::createAttributes);
        addAttributeRegistration(SUPER_SLIDER, SuperSliderEntity::createAttributes);
        addAttributeRegistration(SHOOTER_FISH, ShooterFishEntity::createAttributes);
        addAttributeRegistration(FLY_FISH, FlyFishEntity::createAttributes);
        addAttributeRegistration(REAPER_FISH, ReaperFishEntity::createAttributes);
        addAttributeRegistration(POCKET_SEA_CREEPER, PocketSeaCreeperEntity::createAttributes);
        addAttributeRegistration(PUNCTURE_FISH, PunctureFishEntity::createAttributes);
        addAttributeRegistration(BASELAYER_ABYSSAL, BaselayerAbyssalEntity::createAttributes);
        addAttributeRegistration(PREDATOR_ABYSSAL, PredatorAbyssalEntity::createAttributes);
        addAttributeRegistration(GUIDE_ABYSSAL, GuideAbyssalEntity::createAttributes);
        addAttributeRegistration(SPLASHER_ABYSSAL, SplasherAbyssalEntity::createAttributes);
        addAttributeRegistration(UMBRELLA_ABYSSAL, UmbrellaAbyssalEntity::createAttributes);
        addAttributeRegistration(CRACKER_ABYSSAL, CrackerAbyssalEntity::createAttributes);
        addAttributeRegistration(COLLECTOR_PROKARYOTE, CollectorProkaryoteEntity::createAttributes);
        addAttributeRegistration(BONE_FISH, BoneFishEntity::createAttributes);
        addAttributeRegistration(CHISELER_FISH, ChiselerFishEntity::createAttributes);
        addAttributeRegistration(PREGNANT_FISH, PregnantFishEntity::createAttributes);
        addAttributeRegistration(FAKE_OFFSPRING, FakeOffspringEntity::createAttributes);
        addAttributeRegistration(FLEE_FISH, FleeFishEntity::createAttributes);
        addAttributeRegistration(ROUTE_SHAPER, RouteShaperEntity::createAttributes);
        addAttributeRegistration(ROUTE_FRACTAL, RouteFractalEntity::createAttributes);
        addAttributeRegistration(FIRST_TO_TALK, FirstTellerEntity::createAttributes);
        addAttributeRegistration(REAPER_PET, ReaperPetEntity::createAttributes);
        addAttributeRegistration(BISHOP_FISH, BishopFishEntity::createAttributes);
        addAttributeRegistration(TIDE_BISHOP, TideBishopEntity::createAttributes);
        addAttributeRegistration(SONS, SonsEntity::createAttributes);
        addAttributeRegistration(FLOATER_PROKARYOTE, FloaterProkaryoteEntity::createAttributes);
        addAttributeRegistration(CHITIN_GOLEM, ChitinGolemEntity::createAttributes);
        addAttributeRegistration(TIDE_DEATHREPELLER, TideDeathrepellerEntity::createAttributes);
        addAttributeRegistration(MEGA_CHEST, MegaChestEntity::createAttributes);
        addAttributeRegistration(APOSTLE_PROKARYOTE, ApostleProkaryoteEntity::createAttributes);
        addAttributeRegistration(HIGHMORE, HighmoreEntity::createAttributes);
        addAttributeRegistration(ACCUMULATOR_PROKARYOTE, AccumulatorProkaryoteEntity::createAttributes);
        addAttributeRegistration(ACCUMULATOR_CLONE, AccumulatorCloneEntity::createAttributes);
        addAttributeRegistration(FEEDER_PROKARYOTE, FeederProkaryoteEntity::createAttributes);
        addAttributeRegistration(CHEST_FISH, ChestFishEntity::createAttributes);
        addAttributeRegistration(SPIKE_CHEST, SpikeChestEntity::createAttributes);
        addAttributeRegistration(SKADI, SkadiEntity::createAttributes);
        addAttributeRegistration(DEPOSITER_PROKARYOTE, DepositerProkaryoteEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_VILLAGER, OceanizedVillagerEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_VINDICATOR, OceanizedVindicatorEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_PILLAGER, OceanizedPillagerEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_PIG, OceanizedPigEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_COW, OceanizedCowEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_SHEEP, OceanizedSheepEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_HORSE, OceanizedHorseEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_PIGLIN, OceanizedPiglinEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_BRUTE, OceanizedBruteEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_SPIDER, OceanizedSpiderEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_ENDERMAN, OceanizedEndermanEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_WOLF, OceanizedWolfEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_DOG, OceanizedDogEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_RAVAGER, OceanizedRavagerEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_WITCH, OceanziedWitchEntity::createAttributes);
        addAttributeRegistration(IZUMIK_OFFSPRING, IzumikOffspringEntity::createAttributes);
        addAttributeRegistration(CAERULA_OFFSPRING, CaerulaOffspringEntity::createAttributes);
        addAttributeRegistration(IZUMIK, IzumikEntity::createAttributes);
        addAttributeRegistration(DIVICELLULAR_GO, DivicellularGoEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_EVOKER, OceanizedEvokerEntity::createAttributes);
        addAttributeRegistration(JUNIOR_WARRIOR_PRIEST, JuniorWarriorPriestEntity::createAttributes);
        addAttributeRegistration(WARRIOR_PRIEST, WarriorPriestEntity::createAttributes);
        addAttributeRegistration(CORRECTIONAL_PHALANXY_INFANTRY, CorrectionalPhalanxyInfantryEntity::createAttributes);
        addAttributeRegistration(CORRECTIONAL_PHALAX_VANGUARD, CorrectinalPhalaxVanguardEntity::createAttributes);
        addAttributeRegistration(TRIBUNAL_HEALER, TribunalHealerEntity::createAttributes);
        addAttributeRegistration(MARTUS, MartusEntity::createAttributes);
        addAttributeRegistration(THE_ABANDONED, TheAbandonedEntity::createAttributes);
        addAttributeRegistration(GUNMU, GunmuEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_WARDEN, OceanizedWardenEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_CAT, OceanizedCatEntity::createAttributes);
        addAttributeRegistration(SUPER_BIG_CAT, SuperBigCatEntity::createAttributes);
        addAttributeRegistration(COMPLEX_CHITIN_GOLEM, ComplexChitinGolemEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_WARDENIS, OceanizedWardenisEntity::createAttributes);
        addAttributeRegistration(NUCLEIC_MALEFICENT, NucleicMaleficentEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_WITHER, OceanizedWitherEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_WITHERIA, OceanizedWitheriaEntity::createAttributes);
        addAttributeRegistration(THE_LAST_KNIGHT, TheLastKnightEntity::createAttributes);
        addAttributeRegistration(LAST_KNIGHT_AND_HORSE, LastKnightAndHorseEntity::createAttributes);
        addAttributeRegistration(ROCINANTE, RocinanteEntity::createAttributes);
        addAttributeRegistration(APOCATA, ApocataEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_FOX, OceanizedFoxEntity::createAttributes);
        addAttributeRegistration(TIDUTANT_EXCRESCENCE, TidutantExcrescenceEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_POLAR_BEAR, OceanizedPolarBearEntity::createAttributes);
        addAttributeRegistration(TIDUTANT_ROCK_SPIDER, TideutantRockSpiderEntity::createAttributes);
        addAttributeRegistration(ENDSPEAKER, EndspeakerEntity::createAttributes);
        addAttributeRegistration(LINGERING_PATHSHAPER, LineringPathshaperEntity::createAttributes);
        addAttributeRegistration(LINGERING_FRACTAL, LingeringFractalEntity::createAttributes);
        addAttributeRegistration(LITTLE_HELPER, LittleHelperEntity::createAttributes);
        addAttributeRegistration(AL_1_S_HELPER, Al1SHelperEntity::createAttributes);
        addAttributeRegistration(ULPIANS, UlpiansEntity::createAttributes);
        addAttributeRegistration(GLADIIA, GladiiaEntity::createAttributes);
        addAttributeRegistration(GLADIIA_WHIRL, GladiiaWhirlEntity::createAttributes);
        addAttributeRegistration(SPECTER, SpecterEntity::createAttributes);
        addAttributeRegistration(SPECTER_DOLL, SpecterDollEntity::createAttributes);
        addAttributeRegistration(IRENE, IreneEntity::createAttributes);
        addAttributeRegistration(TIDE_CHIMERA, TideChimeraEntity::createAttributes);
        addAttributeRegistration(SKADI_CORRUPTED, SkadiCorruptedEntity::createAttributes);
        addAttributeRegistration(OCEANIZE_RABBIT, OceanizeRabbitEntity::createAttributes);
        addAttributeRegistration(SAINT_CARMEN, SaintCarmenEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_ILLUSIONER, OceanizedIllusionerEntity::createAttributes);
        addAttributeRegistration(OCEAN_ILLUSION, OceanIllusionEntity::createAttributes);
        addAttributeRegistration(FLAMARINE_STATUE, FlamarineStatueEntity::createAttributes);
        addAttributeRegistration(NAUTILUS_HEADHUNTER, NautilusHeadhunterEntity::createAttributes);
        addAttributeRegistration(XANTIS, XantisEntity::createAttributes);
        addAttributeRegistration(FLAMARINE_GOLEM, FlamarineGolemEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_VEX, OceanizedVexEntity::createAttributes);
        addAttributeRegistration(ISHARMLA, IsharmlaEntity::createAttributes);
        addAttributeRegistration(QUNYOU_WANTED_ISHARMLA, QunyouWantedIsharmlaEntity::createAttributes);
        addAttributeRegistration(ISHARMLA_TEAR, IsharmlaTearEntity::createAttributes);
        addAttributeRegistration(COMPASSION_PRAYER, CompassionPrayerEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_ENDERINA, OceanizedEnderinaEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_ENDER_DRAGON, OceanizedEnderDragonEntity::createAttributes);
        addAttributeRegistration(MOIST_DRAGON_BREATH, MoistDragonBreathEntity::createAttributes);
        addAttributeRegistration(MOIST_ENDER_CRYSTAL, MoistEnderCrystalEntity::createAttributes);
        addAttributeRegistration(THIRSTER, ThirsterEntity::createAttributes);
        addAttributeRegistration(ABSORBER_LIMB, AbsorberLimbEntity::createAttributes);
        addAttributeRegistration(SCREAM_CHEST_FISH, ScreamChestFishEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_CHICKEN, OceanizedChickenEntity::createAttributes);
        addAttributeRegistration(NETHERSEA_SLIME, NetherseaSlimeEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_SHULKER, OceanizedShulkerEntity::createAttributes);

        markSummonable(RUN_FISH);
        markSummonable(SLIDER_FISH);
        markSummonable(SUPER_SLIDER);
        markSummonable(SHOOTER_FISH);
        markSummonable(FLY_FISH);
        markSummonable(REAPER_FISH);
        markSummonable(POCKET_SEA_CREEPER);
        markSummonable(PUNCTURE_FISH);
        markSummonable(BASELAYER_ABYSSAL);
        markSummonable(PREDATOR_ABYSSAL);
        markSummonable(GUIDE_ABYSSAL);
        markSummonable(SPLASHER_ABYSSAL);
        markSummonable(UMBRELLA_ABYSSAL);
        markSummonable(CRACKER_ABYSSAL);
        markSummonable(COLLECTOR_PROKARYOTE);
        markSummonable(BONE_FISH);
        markSummonable(CHISELER_FISH);
        markSummonable(PREGNANT_FISH);
        markSummonable(FAKE_OFFSPRING);
        markSummonable(FLEE_FISH);
        markSummonable(ROUTE_SHAPER);
        markSummonable(ROUTE_FRACTAL);
        markSummonable(FIRST_TO_TALK);
        markSummonable(REAPER_PET);
        markSummonable(BISHOP_FISH);
        markSummonable(TIDE_BISHOP);
        markSummonable(SONS);
        markSummonable(FLOATER_PROKARYOTE);
        markSummonable(CHITIN_GOLEM);
        markSummonable(TIDE_DEATHREPELLER);
        markSummonable(MEGA_CHEST);
        markSummonable(APOSTLE_PROKARYOTE);
        markSummonable(HIGHMORE);
        markSummonable(ACCUMULATOR_PROKARYOTE);
        markSummonable(ACCUMULATOR_CLONE);
        markSummonable(FEEDER_PROKARYOTE);
        markSummonable(CHEST_FISH);
        markSummonable(SPIKE_CHEST);
        markSummonable(SKADI);
        markSummonable(DEPOSITER_PROKARYOTE);
        markSummonable(OCEANIZED_VILLAGER);
        markSummonable(OCEANIZED_VINDICATOR);
        markSummonable(OCEANIZED_PILLAGER);
        markSummonable(OCEANIZED_PIG);
        markSummonable(OCEANIZED_COW);
        markSummonable(OCEANIZED_SHEEP);
        markSummonable(OCEANIZED_HORSE);
        markSummonable(OCEANIZED_PIGLIN);
        markSummonable(OCEANIZED_BRUTE);
        markSummonable(OCEANIZED_SPIDER);
        markSummonable(OCEANIZED_ENDERMAN);
        markSummonable(OCEANIZED_WOLF);
        markSummonable(OCEANIZED_DOG);
        markSummonable(OCEANIZED_RAVAGER);
        markSummonable(OCEANIZED_WITCH);
        markSummonable(IZUMIK_OFFSPRING);
        markSummonable(IZUMIK);
        markSummonable(DIVICELLULAR_GO);
        markSummonable(OCEANIZED_EVOKER);
        markSummonable(JUNIOR_WARRIOR_PRIEST);
        markSummonable(WARRIOR_PRIEST);
        markSummonable(CORRECTIONAL_PHALANXY_INFANTRY);
        markSummonable(CORRECTIONAL_PHALAX_VANGUARD);
        markSummonable(TRIBUNAL_HEALER);
        markSummonable(MARTUS);
        markSummonable(THE_ABANDONED);
        markSummonable(GUNMU);
        markSummonable(OCEANIZED_WARDEN);
        markSummonable(OCEANIZED_CAT);
        markSummonable(SUPER_BIG_CAT);
        markSummonable(COMPLEX_CHITIN_GOLEM);
        markSummonable(OCEANIZED_WARDENIS);
        markSummonable(NUCLEIC_MALEFICENT);

        addSpawnPlacementRegistration(RUN_FISH, RunFishEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(SLIDER_FISH, SliderFishEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(SHOOTER_FISH, ShooterFishEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(FLY_FISH, FlyFishEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(REAPER_FISH, ReaperFishEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(POCKET_SEA_CREEPER, PocketSeaCreeperEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(PUNCTURE_FISH, PunctureFishEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(BASELAYER_ABYSSAL, BaselayerAbyssalEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(PREDATOR_ABYSSAL, PredatorAbyssalEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(GUIDE_ABYSSAL, GuideAbyssalEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(SPLASHER_ABYSSAL, SplasherAbyssalEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(UMBRELLA_ABYSSAL, UmbrellaAbyssalEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(CRACKER_ABYSSAL, CrackerAbyssalEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(COLLECTOR_PROKARYOTE, CollectorProkaryoteEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(BONE_FISH, BoneFishEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(CHISELER_FISH, ChiselerFishEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(PREGNANT_FISH, PregnantFishEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(FLEE_FISH, FleeFishEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(FIRST_TO_TALK, FirstTellerEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(FLOATER_PROKARYOTE, FloaterProkaryoteEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(APOSTLE_PROKARYOTE, ApostleProkaryoteEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(ACCUMULATOR_PROKARYOTE, AccumulatorProkaryoteEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(FEEDER_PROKARYOTE, FeederProkaryoteEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(CHEST_FISH, ChestFishEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(SPIKE_CHEST, SpikeChestEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(DEPOSITER_PROKARYOTE, DepositerProkaryoteEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(IZUMIK_OFFSPRING, IzumikOffspringEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(NUCLEIC_MALEFICENT, NucleicMaleficentEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(NAUTILUS_HEADHUNTER, NautilusHeadhunterEntity::registerSpawnPlacements);

        addDungeonMobRegistration(SLIDER_FISH, SliderFishEntity::registerDungeonMob);
        addDungeonMobRegistration(BASELAYER_ABYSSAL, BaselayerAbyssalEntity::registerDungeonMob);
        addDungeonMobRegistration(SPLASHER_ABYSSAL, SplasherAbyssalEntity::registerDungeonMob);
        addDungeonMobRegistration(UMBRELLA_ABYSSAL, UmbrellaAbyssalEntity::registerDungeonMob);
        addDungeonMobRegistration(FLEE_FISH, FleeFishEntity::registerDungeonMob);
        addDungeonMobRegistration(NETHERSEA_SLIME, NetherseaSlimeEntity::registerDungeonMob);

        LIVING_ENTITY_TYPES = collectLivingEntityTypes(false);
        SUMMONABLE_ENTITY_TYPES = collectLivingEntityTypes(true);
    }

    private static <T extends Entity> DeferredHolder<EntityType<T>, ? extends EntityType<T>> register(String registryName, EntityType.Builder<T> builder) {
        DeferredHolder<EntityType<T>, ? extends EntityType<T>> entityType = REGISTRY.register(registryName, () -> builder.build(registryName));
        ENTITY_REGISTRATIONS.put(entityType, new EntityRegistrationData());
        return entityType;
    }

    private static <T extends LivingEntity> void addAttributeRegistration(
            DeferredHolder<EntityType<T>, ? extends EntityType<T>> entityType,
            Supplier<AttributeSupplier.Builder> attributes
    ) {
        EntityRegistrationData data = registrationData(entityType);
        data.livingEntityType = entityType;
        data.attributeRegistration = event -> event.put(entityType.get(), attributes.get().build());
    }

    private static <T extends LivingEntity> void markSummonable(DeferredHolder<EntityType<T>, ? extends EntityType<T>> entityType) {
        registrationData(entityType).summonable = true;
    }

    private static void addSpawnPlacementRegistration(DeferredHolder<? extends EntityType<?>, ? extends EntityType<?>> entityType, Runnable registration) {
        registrationData(entityType).spawnPlacementRegistration = registration;
    }

    private static void addDungeonMobRegistration(DeferredHolder<? extends EntityType<?>, ? extends EntityType<?>> entityType, Runnable registration) {
        registrationData(entityType).dungeonMobRegistration = registration;
    }

    private static EntityRegistrationData registrationData(DeferredHolder<? extends EntityType<?>, ? extends EntityType<?>> entityType) {
        EntityRegistrationData data = ENTITY_REGISTRATIONS.get(entityType);
        if (data == null) {
            throw new IllegalArgumentException("Entity type is not registered by CAEntities: " + entityType.getId());
        }
        return data;
    }

    /**
     * 获取具备默认属性的生物实体类型
     *
     * @return 按实体注册顺序排列的不可修改列表
     */
    public static List<DeferredHolder<? extends EntityType<? extends LivingEntity>, ? extends EntityType<? extends LivingEntity>>> getLivingEntityTypes() {
        return LIVING_ENTITY_TYPES;
    }

    /**
     * 获取具有可召唤属性的生物实体类型
     *
     * @return 按实体注册顺序排列的不可修改列表
     */
    public static List<DeferredHolder<? extends EntityType<? extends LivingEntity>, ? extends EntityType<? extends LivingEntity>>> getSummonableEntityTypes() {
        return SUMMONABLE_ENTITY_TYPES;
    }

    private static List<DeferredHolder<? extends EntityType<? extends LivingEntity>, ? extends EntityType<? extends LivingEntity>>> collectLivingEntityTypes(boolean summonableOnly) {
        List<DeferredHolder<? extends EntityType<? extends LivingEntity>, ? extends EntityType<? extends LivingEntity>>> entityTypes = new ArrayList<>();
        for (EntityRegistrationData data : ENTITY_REGISTRATIONS.values()) {
            if (data.livingEntityType != null && (!summonableOnly || data.summonable)) {
                entityTypes.add(data.livingEntityType);
            }
        }
        return List.copyOf(entityTypes);
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            for (EntityRegistrationData data : ENTITY_REGISTRATIONS.values()) {
                if (data.spawnPlacementRegistration != null) {
                    data.spawnPlacementRegistration.run();
                }
            }
            for (EntityRegistrationData data : ENTITY_REGISTRATIONS.values()) {
                if (data.dungeonMobRegistration != null) {
                    data.dungeonMobRegistration.run();
                }
            }
        });
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        for (EntityRegistrationData data : ENTITY_REGISTRATIONS.values()) {
            if (data.attributeRegistration != null) {
                data.attributeRegistration.accept(event);
            }
        }
    }

    private static final class EntityRegistrationData {
        private DeferredHolder<? extends EntityType<? extends LivingEntity>, ? extends EntityType<? extends LivingEntity>> livingEntityType;
        private Consumer<EntityAttributeCreationEvent> attributeRegistration;
        private Runnable spawnPlacementRegistration;
        private Runnable dungeonMobRegistration;
        private boolean summonable;
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class CARenderers {
        private static final LinkedHashMap<DeferredHolder<? extends EntityType<?>, ? extends EntityType<?>>, Consumer<EntityRenderersEvent.RegisterRenderers>> RENDERER_REGISTRATIONS = new LinkedHashMap<>();

        static {
            addRenderer(CAEntities.RUN_FISH, RunFishRenderer::new);
            addRenderer(CAEntities.SLIDER_FISH, SliderFishRenderer::new);
            addRenderer(CAEntities.SUPER_SLIDER, SuperSliderRenderer::new);
            addRenderer(CAEntities.SHOOTER_FISH, ShooterFishRenderer::new);
            addRenderer(CAEntities.FISH_SHOOT, FishShootRenderer::new);
            addRenderer(CAEntities.FLY_FISH, FlyFishRenderer::new);
            addRenderer(CAEntities.REAPER_FISH, ReaperFishRenderer::new);
            addRenderer(CAEntities.POCKET_SEA_CREEPER, PocketSeaCreeperRenderer::new);
            addRenderer(CAEntities.PUNCTURE_FISH, PunctureFishRenderer::new);
            addRenderer(CAEntities.BASELAYER_ABYSSAL, BaselayerAbyssalRenderer::new);
            addRenderer(CAEntities.PREDATOR_ABYSSAL, PredatorAbyssalRenderer::new);
            addRenderer(CAEntities.GUIDE_ABYSSAL, GuideAbyssalRenderer::new);
            addRenderer(CAEntities.SPLASHER_ABYSSAL, SplasherAbyssalRenderer::new);
            addRenderer(CAEntities.FISH_SPLASH, FishSplashRenderer::new);
            addRenderer(CAEntities.UMBRELLA_ABYSSAL, UmbrellaAbyssalRenderer::new);
            addRenderer(CAEntities.CRACKER_ABYSSAL, CrackerAbyssalRenderer::new);
            addRenderer(CAEntities.COLLECTOR_PROKARYOTE, CollectorProkaryoteRenderer::new);
            addRenderer(CAEntities.BONE_FISH, BoneFishRenderer::new);
            addRenderer(CAEntities.CHISELER_FISH, ChiselerFishRenderer::new);
            addRenderer(CAEntities.FAKERGG_SHOOT, FakerggShootRenderer::new);
            addRenderer(CAEntities.PREGNANT_FISH, PregnantFishRenderer::new);
            addRenderer(CAEntities.FAKE_OFFSPRING, FakeOffspringRenderer::new);
            addRenderer(CAEntities.FLEEFISH_BULLET, FleefishBulletRenderer::new);
            addRenderer(CAEntities.FLEE_FISH, FleeFishRenderer::new);
            addRenderer(CAEntities.ROUTE_SHAPER, RouteShaperRenderer::new);
            addRenderer(CAEntities.ROUTE_FRACTAL, RouteFractalRenderer::new);
            addRenderer(CAEntities.TELLER_SHOT, TellerShotRenderer::new);
            addRenderer(CAEntities.FIRST_TO_TALK, FirstTellerRenderer::new);
            addRenderer(CAEntities.REAPER_PET, ReaperPetRenderer::new);
            addRenderer(CAEntities.BISHOP_FISH, BishopFishRenderer::new);
            addRenderer(CAEntities.TIDE_BISHOP, TideBishopRenderer::new);
            addRenderer(CAEntities.SONS, SonsRenderer::new);
            addRenderer(CAEntities.FLOATER_PROKARYOTE, FloaterProkaryoteRenderer::new);
            addRenderer(CAEntities.CHITIN_GOLEM, ChitinGolemRenderer::new);
            addRenderer(CAEntities.TIDE_DEATHREPELLER, TideDeathrepellerRenderer::new);
            addRenderer(CAEntities.MEGA_CHEST, MegaChestRenderer::new);
            addRenderer(CAEntities.APOSTLE_PROKARYOTE, ApostleProkaryoteRenderer::new);
            addRenderer(CAEntities.HIGHMORE_SHOOT, HighmoreShootRenderer::new);
            addRenderer(CAEntities.HIGHMORE, HighmoreRenderer::new);
            addRenderer(CAEntities.ACCUMULATOR_PROKARYOTE, AccumulatorProkaryoteRenderer::new);
            addRenderer(CAEntities.ACCUMULATOR_CLONE, AccumulatorCloneRenderer::new);
            addRenderer(CAEntities.FEEDER_PROKARYOTE, FeederProkaryoteRenderer::new);
            addRenderer(CAEntities.CHEST_FISH, ChestFishRenderer::new);
            addRenderer(CAEntities.SPIKE_CHEST, SpikeChestRenderer::new);
            addRenderer(CAEntities.SKADI, SkadiRenderer::new);
            addRenderer(CAEntities.DEPOSITER_PROKARYOTE, DepositerProkaryoteRenderer::new);
            addRenderer(CAEntities.OCEANIZED_VILLAGER, OceanizedVillagerRenderer::new);
            addRenderer(CAEntities.OCEANIZED_VINDICATOR, OceanizedVindicatorRenderer::new);
            addRenderer(CAEntities.SHOT_OCEAN_ARROW, ShotOceanArrowRenderer::new);
            addRenderer(CAEntities.OCEANIZED_PILLAGER, OceanizedPillagerRenderer::new);
            addRenderer(CAEntities.ANCHOR_FLY, AnchorFlyRenderer::new);
            addRenderer(CAEntities.OCEANIZED_PIG, OceanizedPigRenderer::new);
            addRenderer(CAEntities.OCEANIZED_COW, OceanizedCowRenderer::new);
            addRenderer(CAEntities.OCEANIZED_SHEEP, OceanizedSheepRenderer::new);
            addRenderer(CAEntities.OCEANIZED_HORSE, OceanizedHorseRenderer::new);
            addRenderer(CAEntities.OCEANIZED_PIGLIN, OceanizedPiglinRenderer::new);
            addRenderer(CAEntities.OCEANIZED_BRUTE, OceanizedBruteRenderer::new);
            addRenderer(CAEntities.OCEANIZED_SPIDER, OceanizedSpiderRenderer::new);
            addRenderer(CAEntities.OCEANIZED_ENDERMAN, OceanizedEndermanRenderer::new);
            addRenderer(CAEntities.OCEANIZED_WOLF, OceanizedWolfRenderer::new);
            addRenderer(CAEntities.OCEANIZED_DOG, OceanizedDogRenderer::new);
            addRenderer(CAEntities.OCEANIZED_RAVAGER, OceanizedRavagerRenderer::new);
            addRenderer(CAEntities.OCEANIZED_WITCH, OceanziedWitchRenderer::new);
            addRenderer(CAEntities.THROWABLE_POTION, ThrownItemRenderer::new);
            addRenderer(CAEntities.IZUMIK_OFFSPRING, IzumikOffspringRenderer::new);
            addRenderer(CAEntities.CAERULA_OFFSPRING, CaerulaOffspringRenderer::new);
            addRenderer(CAEntities.IZUMIK, IzumikRenderer::new);
            addRenderer(CAEntities.DIVICELLULAR_GO, DivicellularGoRenderer::new);
            addRenderer(CAEntities.OCEANIZED_EVOKER, OceanizedEvokerRenderer::new);
            addRenderer(CAEntities.JUNIOR_WARRIOR_PRIEST, JuniorWarriorPriestRenderer::new);
            addRenderer(CAEntities.WARRIOR_PRIEST, WarriorPriestRenderer::new);
            addRenderer(CAEntities.CORRECTIONAL_PHALANXY_INFANTRY, CorrectionalPhalanxyInfantryRenderer::new);
            addRenderer(CAEntities.CORRECTIONAL_PHALAX_VANGUARD, CorrectinalPhalaxVanguardRenderer::new);
            addRenderer(CAEntities.TRIBUNAL_HEALER, TribunalHealerRenderer::new);
            addRenderer(CAEntities.HEAL_BULLLET, ThrownItemRenderer::new);
            addRenderer(CAEntities.MARTUS, MartusRenderer::new);
            addRenderer(CAEntities.THE_ABANDONED, TheAbandonedRenderer::new);
            addRenderer(CAEntities.ABANDONED_SHOOT, AbandonedShootRenderer::new);
            addRenderer(CAEntities.GUNMU, GunmuRenderer::new);
            addRenderer(CAEntities.OCEANIZED_WARDEN, OceanizedWardenRenderer::new);
            addRenderer(CAEntities.OCEANIZED_CAT, OceanizedCatRenderer::new);
            addRenderer(CAEntities.SUPER_BIG_CAT, SuperBigCatRenderer::new);
            addRenderer(CAEntities.COMPLEX_CHITIN_GOLEM, ComplexChitinGolemRenderer::new);
            addRenderer(CAEntities.OCEANIZED_WARDENIS, OceanizedWardenisRenderer::new);
            addRenderer(CAEntities.NUCLEIC_MALEFICENT, NucleicMaleficentRenderer::new);
            addRenderer(CAEntities.OCEANIZED_WITHER, OceanizedWitherRenderer::new);
            addRenderer(CAEntities.WITHER_SHOOT_PRE, ThrownItemRenderer::new);
            addRenderer(CAEntities.OCEANIZED_WITHERIA, OceannizedWitheriaRenderer::new);
            addRenderer(CAEntities.THE_LAST_KNIGHT, TheLastKnightRenderer::new);
            addRenderer(CAEntities.LAST_KNIGHT_AND_HORSE, LastKnightAndHorseRenderer::new);
            addRenderer(CAEntities.ROCINANTE, RocinanteRenderer::new);
            addRenderer(CAEntities.APOCATA, ApocataRenderer::new);
            addRenderer(CAEntities.OCEANIZED_FOX, OceanizedFoxRenderer::new);
            addRenderer(CAEntities.TIDUTANT_EXCRESCENCE, TidutantExcrescenceRenderer::new);
            addRenderer(CAEntities.OCEANIZED_POLAR_BEAR, OceanizedPolarBearRenderer::new);
            addRenderer(CAEntities.TIDUTANT_ROCK_SPIDER, TideutantRockSpiderRenderer::new);
            addRenderer(CAEntities.ENDSPEAKER, EndspeakerRenderer::new);
            addRenderer(CAEntities.LINGERING_PATHSHAPER, LineringPathshaperRenderer::new);
            addRenderer(CAEntities.LINGERING_FRACTAL, LingeringFractalRenderer::new);
            addRenderer(CAEntities.LITTLE_HELPER, LittleHelperRenderer::new);
            addRenderer(CAEntities.AL_1_S_HELPER, Al1SHelperRenderer::new);
            addRenderer(CAEntities.ULPIANS, UlpiansRenderer::new);
            addRenderer(CAEntities.GLADIIA, GladiiaRenderer::new);
            addRenderer(CAEntities.GLADIIA_WHIRL, GladiiaWhirlRenderer::new);
            addRenderer(CAEntities.SPECTER, SpecterRenderer::new);
            addRenderer(CAEntities.SPECTER_DOLL, SpecterDollRenderer::new);
            addRenderer(CAEntities.IRENE, IreneRenderer::new);
            addRenderer(CAEntities.TIDE_CHIMERA, TideChimeraRenderer::new);
            addRenderer(CAEntities.SKADI_CORRUPTED, SkadiCorruptedRenderer::new);
            addRenderer(CAEntities.OCEANIZE_RABBIT, OceanizeRabbitRenderer::new);
            addRenderer(CAEntities.SAINT_CARMEN, SaintCarmenRenderer::new);
            addRenderer(CAEntities.CARMEN_BULLET, CarmenBulletRenderer::new);
            addRenderer(CAEntities.OCEANIZED_ILLUSIONER, OceanizedIllusionerRenderer::new);
            addRenderer(CAEntities.OCEAN_ILLUSION, OceanIllusionRenderer::new);
            addRenderer(CAEntities.FLAMARINE_STATUE, FlamarineStatueRenderer::new);
            addRenderer(CAEntities.NAUTILUS_HEADHUNTER, NautilusHeadhunterRenderer::new);
            addRenderer(CAEntities.XANTIS, XantisRenderer::new);
            addRenderer(CAEntities.FLAMARINE_GOLEM, FlamarineGolemRenderer::new);
            addRenderer(CAEntities.OCEANIZED_VEX, OceanizedVexRenderer::new);
            addRenderer(CAEntities.ISHARMLA, IsharmlaRenderer::new);
            addRenderer(CAEntities.QUNYOU_WANTED_ISHARMLA, QunyouWantedIsharmlaRenderer::new);
            addRenderer(CAEntities.ISHARMLA_TEAR, IsharmlaTearRenderer::new);
            addRenderer(CAEntities.PRAYER_SPLASH, PrayerSplashRenderer::new);
            addRenderer(CAEntities.COMPASSION_PRAYER, CompassionPrayerRenderer::new);
            addRenderer(CAEntities.OCEANIZED_ENDERINA, OceanizedEnderinaRenderer::new);
            addRenderer(CAEntities.OCEANIZED_ENDER_DRAGON, OceanizedEnderDragonRenderer::new);
            addRenderer(CAEntities.MOIST_DRAGON_BREATH, MoistDragonBreathRenderer::new);
            addRenderer(CAEntities.MOIST_ENDER_CRYSTAL, MoistEnderCrystalRenderer::new);
            addRenderer(CAEntities.THIRSTER, ThirsterRenderer::new);
            addRenderer(CAEntities.ABSORBER_LIMB, AbsorberLimbRenderer::new);
            addRenderer(CAEntities.SCREAM_CHEST_FISH, ScreamChestFishRenderer::new);
            addRenderer(CAEntities.OCEANIZED_CHICKEN, OceanizedChickenRenderer::new);
            addRenderer(CAEntities.NETHERSEA_SLIME, NetherseaSlimeRenderer::new);
            addRenderer(CAEntities.OCEANIZED_SHULKER, OceanizedShulkerRenderer::new);
        }

        private static <T extends Entity> void addRenderer(DeferredHolder<EntityType<T>, ? extends EntityType<T>> entityType, EntityRendererProvider<T> renderer) {
            RENDERER_REGISTRATIONS.put(entityType, event -> event.registerEntityRenderer(entityType.get(), renderer));
        }

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            for (Consumer<EntityRenderersEvent.RegisterRenderers> registration : RENDERER_REGISTRATIONS.values()) {
                registration.accept(event);
            }
        }
    }

    @EventBusSubscriber(value = {Dist.CLIENT})
    public static class CAModels {
        private static final LinkedHashMap<ModelLayerLocation, Supplier<LayerDefinition>> LAYER_DEFINITIONS = new LinkedHashMap<>();

        static {
            LAYER_DEFINITIONS.put(ModelBulletProjectile.LAYER_LOCATION, ModelBulletProjectile::createBodyLayer);
            LAYER_DEFINITIONS.put(ModelAnchorFly.LAYER_LOCATION, ModelAnchorFly::createBodyLayer);
            LAYER_DEFINITIONS.put(ModelSealeatherChitinArmor.LAYER_LOCATION, ModelSealeatherChitinArmor::createBodyLayer);
            LAYER_DEFINITIONS.put(ModelHighmoreShoot.LAYER_LOCATION, ModelHighmoreShoot::createBodyLayer);
            LAYER_DEFINITIONS.put(ModelFleefishBullet.LAYER_LOCATION, ModelFleefishBullet::createBodyLayer);
            LAYER_DEFINITIONS.put(ModelFakerggShoot.LAYER_LOCATION, ModelFakerggShoot::createBodyLayer);
            LAYER_DEFINITIONS.put(ModelOceanArrow.LAYER_LOCATION, ModelOceanArrow::createBodyLayer);
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            for (var entry : LAYER_DEFINITIONS.entrySet()) {
                event.registerLayerDefinition(entry.getKey(), entry.getValue());
            }
        }
    }
}
