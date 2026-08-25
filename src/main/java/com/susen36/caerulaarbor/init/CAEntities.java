package com.susen36.caerulaarbor.init;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.client.model.entity.*;
import com.susen36.caerulaarbor.client.renderer.entity.*;
import com.susen36.caerulaarbor.entity.*;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.entity.base.SeaMonsterBoss;
import com.susen36.caerulaarbor.entity.bullets.*;
import com.susen36.caerulaarbor.entity.crawler.PocketSeaCrawlerEntity;
import com.susen36.caerulaarbor.entity.crawler.PocketSeaCreeperEntity;
import com.susen36.caerulaarbor.entity.enderdragon.MoistDragonBreathEntity;
import com.susen36.caerulaarbor.entity.enderdragon.MoistEnderCrystalEntity;
import com.susen36.caerulaarbor.entity.enderdragon.OceanizedEnderDragonEntity;
import com.susen36.caerulaarbor.entity.enderdragon.OceanizedEnderinaEntity;
import com.susen36.caerulaarbor.entity.helper.Al1SHelperEntity;
import com.susen36.caerulaarbor.entity.helper.LittleHelperEntity;
import com.susen36.caerulaarbor.entity.isharmla.IsharmlaEntity;
import com.susen36.caerulaarbor.entity.isharmla.IsharmlaTearEntity;
import com.susen36.caerulaarbor.entity.shaper.LineringPathshaperEntity;
import com.susen36.caerulaarbor.entity.shaper.LingeringFractalEntity;
import com.susen36.caerulaarbor.entity.shaper.RouteFractalEntity;
import com.susen36.caerulaarbor.entity.shaper.RouteShaperEntity;
import com.susen36.caerulaarbor.entity.slime.FissionProkaryoteSlimeEntity;
import com.susen36.caerulaarbor.entity.slime.NetherseaSlimeEntity;
import com.susen36.caerulaarbor.entity.tidelinked.TidelinkedArchonEntity;
import com.susen36.caerulaarbor.entity.tidelinked.TidelinkedBishopEntity;
import com.susen36.caerulaarbor.entity.tidelinked.TidelinkedImmortalEntity;
import com.susen36.caerulaarbor.entity.warden.OceanizedWardenEntity;
import com.susen36.caerulaarbor.entity.warden.OceanizedWardenisEntity;
import com.susen36.caerulaarbor.entity.wither.OceanizedWitherEntity;
import com.susen36.caerulaarbor.entity.wither.OceanizedWitheriaEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@EventBusSubscriber
public class CAEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, CaerulaArbor.MODID);
    public static final List<DeferredHolder<EntityType<?>, ? extends EntityType<?>>> SEA_MONSTERS = new ArrayList<>();
    public static final List<DeferredHolder<EntityType<?>, ? extends EntityType<?>>> SEA_MONSTER_BOSSES = new ArrayList<>();
    private static final LinkedHashMap<DeferredHolder<? extends EntityType<?>, ? extends EntityType<?>>, EntityRegistrationData> ENTITY_REGISTRATIONS = new LinkedHashMap<>();

    public static final DeferredHolder<EntityType<?>, EntityType<ShellSeaRunnerEntity>> SHELL_SEA_RUNNER = register("shell_sea_runner", ShellSeaRunnerEntity.class,
            EntityType.Builder.<ShellSeaRunnerEntity>of(ShellSeaRunnerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3)
                    .sized(0.4f, 0.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<SliderFishEntity>> SLIDER_FISH = register("slider_fish", SliderFishEntity.class,
            EntityType.Builder.<SliderFishEntity>of(SliderFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3)
                    .sized(0.5f, 0.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<SuperSliderEntity>> SUPER_SLIDER = register("super_slider", SuperSliderEntity.class,
            EntityType.Builder.<SuperSliderEntity>of(SuperSliderEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(0.3f, 0.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<ShooterFishEntity>> SHOOTER_FISH = register("shooter_fish", ShooterFishEntity.class,
            EntityType.Builder.<ShooterFishEntity>of(ShooterFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 1.2f));
    public static final DeferredHolder<EntityType<?>, EntityType<FishShootEntity>> FISH_SHOOT = register("fish_shoot", FishShootEntity.class,
            EntityType.Builder.<FishShootEntity>of(FishShootEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
    public static final DeferredHolder<EntityType<?>, EntityType<FloatingSeaDrifterEntity>> FLOATING_SEA_DRIFTER = register("floating_sea_drifter", FloatingSeaDrifterEntity.class,
            EntityType.Builder.<FloatingSeaDrifterEntity>of(FloatingSeaDrifterEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 0.9f));
    public static final DeferredHolder<EntityType<?>, EntityType<ReaperFishEntity>> REAPER_FISH = register("reaper_fish", ReaperFishEntity.class,
            EntityType.Builder.<ReaperFishEntity>of(ReaperFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(10).setUpdateInterval(3)
                    .sized(1.2f, 2.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<PocketSeaCreeperEntity>> POCKET_SEA_CREEPER = register("pocket_sea_creeper", PocketSeaCreeperEntity.class,
            EntityType.Builder.<PocketSeaCreeperEntity>of(PocketSeaCreeperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.8f, 1.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<PocketSeaCrawlerEntity>> POCKET_SEA_CRAWLER = register("pocket_sea_crawler", PocketSeaCrawlerEntity.class,
            EntityType.Builder.<PocketSeaCrawlerEntity>of(PocketSeaCrawlerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.8f, 1.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<PunctureFishEntity>> PUNCTURE_FISH = register("puncture_fish", PunctureFishEntity.class,
            EntityType.Builder.<PunctureFishEntity>of(PunctureFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.9f, 2.7f));
    public static final DeferredHolder<EntityType<?>, EntityType<BaselayerAbyssalEntity>> BASELAYER_ABYSSAL = register("baselayer_abyssal", BaselayerAbyssalEntity.class,
            EntityType.Builder.<BaselayerAbyssalEntity>of(BaselayerAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.7f, 1.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<PredatorAbyssalEntity>> PREDATOR_ABYSSAL = register("predator_abyssal", PredatorAbyssalEntity.class,
            EntityType.Builder.<PredatorAbyssalEntity>of(PredatorAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 1.4f));
    public static final DeferredHolder<EntityType<?>, EntityType<GuideAbyssalEntity>> GUIDE_ABYSSAL = register("guide_abyssal", GuideAbyssalEntity.class,
            EntityType.Builder.<GuideAbyssalEntity>of(GuideAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(1f, 2.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<SplasherAbyssalEntity>> SPLASHER_ABYSSAL = register("splasher_abyssal", SplasherAbyssalEntity.class,
            EntityType.Builder.<SplasherAbyssalEntity>of(SplasherAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 1.1f));
    public static final DeferredHolder<EntityType<?>, EntityType<FishSplashEntity>> FISH_SPLASH = register("fish_splash", FishSplashEntity.class,
            EntityType.Builder.<FishSplashEntity>of(FishSplashEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
    public static final DeferredHolder<EntityType<?>, EntityType<UmbrellaAbyssalEntity>> UMBRELLA_ABYSSAL = register("umbrella_abyssal", UmbrellaAbyssalEntity.class,
            EntityType.Builder.<UmbrellaAbyssalEntity>of(UmbrellaAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.8f, 1.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<CrackerAbyssalEntity>> CRACKER_ABYSSAL = register("cracker_abyssal", CrackerAbyssalEntity.class,
            EntityType.Builder.<CrackerAbyssalEntity>of(CrackerAbyssalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.7f, 2f));
    public static final DeferredHolder<EntityType<?>, EntityType<CollectorProkaryoteEntity>> COLLECTOR_PROKARYOTE = register("collector_prokaryote", CollectorProkaryoteEntity.class,
            EntityType.Builder.<CollectorProkaryoteEntity>of(CollectorProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3)
                    .sized(0.5f, 0.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<BoneFishEntity>> BONE_FISH = register("bone_fish", BoneFishEntity.class,
            EntityType.Builder.<BoneFishEntity>of(BoneFishEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3)
                    .sized(0.7f, 0.7f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanStonecutteEntity>> OCEAN_STONECUTTE = register("ocean_stonecutte", OceanStonecutteEntity.class,
            EntityType.Builder.<OceanStonecutteEntity>of(OceanStonecutteEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 0.6f));
    public static final DeferredHolder<EntityType<?>, EntityType<FakerggShootEntity>> FAKERGG_SHOOT = register("fakergg_shoot", FakerggShootEntity.class,
            EntityType.Builder.<FakerggShootEntity>of(FakerggShootEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.4f, 0.4f));
    public static final DeferredHolder<EntityType<?>, EntityType<PregnantFishEntity>> PREGNANT_FISH = register("pregnant_fish", PregnantFishEntity.class,
            EntityType.Builder.<PregnantFishEntity>of(PregnantFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.7f, 1.1f));
    public static final DeferredHolder<EntityType<?>, EntityType<FakeOffspringEntity>> FAKE_OFFSPRING = register("fake_offspring", FakeOffspringEntity.class,
            EntityType.Builder.<FakeOffspringEntity>of(FakeOffspringEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3)
                    .sized(0.6f, 0.6f));
    public static final DeferredHolder<EntityType<?>, EntityType<SkimmingSeaBulletEntity>> SKIMMING_SEA_BULLET = register("skimming_sea_bullet", SkimmingSeaBulletEntity.class,
            EntityType.Builder.<SkimmingSeaBulletEntity>of(SkimmingSeaBulletEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.4f, 0.4f));
    public static final DeferredHolder<EntityType<?>, EntityType<SkimmingSeaDrifterEntity>> SKIMMING_SEA_DRIFTER = register("skimming_sea_drifter", SkimmingSeaDrifterEntity.class,
            EntityType.Builder.<SkimmingSeaDrifterEntity>of(SkimmingSeaDrifterEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.8f, 1.1f));
    public static final DeferredHolder<EntityType<?>, EntityType<RouteShaperEntity>> ROUTE_SHAPER = register("route_shaper", RouteShaperEntity.class,
            EntityType.Builder.<RouteShaperEntity>of(RouteShaperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3)
                    .sized(1.8f, 4f));
    public static final DeferredHolder<EntityType<?>, EntityType<RouteFractalEntity>> ROUTE_FRACTAL = register("route_fractal", RouteFractalEntity.class,
            EntityType.Builder.<RouteFractalEntity>of(RouteFractalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(0.7f, 1.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<TellerShotEntity>> TELLER_SHOT = register("teller_shot", TellerShotEntity.class,
            EntityType.Builder.<TellerShotEntity>of(TellerShotEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.4f, 0.4f));
    public static final DeferredHolder<EntityType<?>, EntityType<FirstTellerEntity>> FIRST_TO_TALK = register("first_to_talk", FirstTellerEntity.class,
            EntityType.Builder.<FirstTellerEntity>of(FirstTellerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(0.9f, 2.7f));
    public static final DeferredHolder<EntityType<?>, EntityType<ReaperPetEntity>> REAPER_PET = register("reaper_pet", ReaperPetEntity.class,
            EntityType.Builder.<ReaperPetEntity>of(ReaperPetEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 1.3f));
    public static final DeferredHolder<EntityType<?>, EntityType<BishopFishEntity>> BISHOP_FISH = register("bishop_fish", BishopFishEntity.class,
            EntityType.Builder.<BishopFishEntity>of(BishopFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(18).setUpdateInterval(3)
                    .sized(1.4f, 2.2f));
    public static final DeferredHolder<EntityType<?>, EntityType<TidelinkedBishopEntity>> TIDELINKED_BISHOP = register("tidelinked_bishop", TidelinkedBishopEntity.class,
            EntityType.Builder.<TidelinkedBishopEntity>of(TidelinkedBishopEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3)
                    .sized(1.1f, 2.2f));
    public static final DeferredHolder<EntityType<?>, EntityType<SonsEntity>> SONS = register("sons", SonsEntity.class,
            EntityType.Builder.<SonsEntity>of(SonsEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3)
                    .sized(0.5f, 0.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<FloaterProkaryoteEntity>> FLOATER_PROKARYOTE = register("floater_prokaryote", FloaterProkaryoteEntity.class,
            EntityType.Builder.<FloaterProkaryoteEntity>of(FloaterProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 1.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<ChitinGolemEntity>> CHITIN_GOLEM = register("chitin_golem", ChitinGolemEntity.class,
            EntityType.Builder.<ChitinGolemEntity>of(ChitinGolemEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(1.75f, 4f));
    public static final DeferredHolder<EntityType<?>, EntityType<TidelinkedImmortalEntity>> TIDELINKED_IMMORTAL = register("tidelinked_immortal", TidelinkedImmortalEntity.class,
            EntityType.Builder.<TidelinkedImmortalEntity>of(TidelinkedImmortalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(1.5f, 2f));
    public static final DeferredHolder<EntityType<?>, EntityType<TidelinkedArchonEntity>> TIDELINKED_ARCHON = register("tidelinked_archon", TidelinkedArchonEntity.class,
            EntityType.Builder.<TidelinkedArchonEntity>of(TidelinkedArchonEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(1.5f, 2f));
    public static final DeferredHolder<EntityType<?>, EntityType<MegaChestEntity>> MEGA_CHEST = register("mega_chest", MegaChestEntity.class,
            EntityType.Builder.<MegaChestEntity>of(MegaChestEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(14).setUpdateInterval(3)
                    .sized(0.8f, 0.9f));
    public static final DeferredHolder<EntityType<?>, EntityType<ApostleProkaryoteEntity>> APOSTLE_PROKARYOTE = register("apostle_prokaryote", ApostleProkaryoteEntity.class,
            EntityType.Builder.<ApostleProkaryoteEntity>of(ApostleProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 1.7f));
    public static final DeferredHolder<EntityType<?>, EntityType<HighmoreShootEntity>> HIGHMORE_SHOOT = register("highmore_shoot", HighmoreShootEntity.class,
            EntityType.Builder.<HighmoreShootEntity>of(HighmoreShootEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.4f, 0.4f));
    public static final DeferredHolder<EntityType<?>, EntityType<HighmoreEntity>> HIGHMORE = register("highmore", HighmoreEntity.class,
            EntityType.Builder.<HighmoreEntity>of(HighmoreEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(18).setUpdateInterval(3)
                    .sized(1.4f, 1.6f));
    public static final DeferredHolder<EntityType<?>, EntityType<AccumulatorProkaryoteEntity>> ACCUMULATOR_PROKARYOTE = register("accumulator_prokaryote", AccumulatorProkaryoteEntity.class,
            EntityType.Builder.<AccumulatorProkaryoteEntity>of(AccumulatorProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.5f, 1f));
    public static final DeferredHolder<EntityType<?>, EntityType<FeederProkaryoteEntity>> FEEDER_PROKARYOTE = register("feeder_prokaryote", FeederProkaryoteEntity.class,
            EntityType.Builder.<FeederProkaryoteEntity>of(FeederProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.8f, 1.1f));
    public static final DeferredHolder<EntityType<?>, EntityType<ChestFishEntity>> CHEST_FISH = register("chest_fish", ChestFishEntity.class,
            EntityType.Builder.<ChestFishEntity>of(ChestFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.9f, 0.9f));
    public static final DeferredHolder<EntityType<?>, EntityType<SpikeChestEntity>> SPIKE_CHEST = register("spike_chest", SpikeChestEntity.class,
            EntityType.Builder.<SpikeChestEntity>of(SpikeChestEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.9f, 0.9f));
    public static final DeferredHolder<EntityType<?>, EntityType<SkadiEntity>> SKADI = register("skadi", SkadiEntity.class,
            EntityType.Builder.<SkadiEntity>of(SkadiEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<DepositerProkaryoteEntity>> DEPOSITER_PROKARYOTE = register("depositer_prokaryote", DepositerProkaryoteEntity.class,
            EntityType.Builder.<DepositerProkaryoteEntity>of(DepositerProkaryoteEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.625f, 1f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedVillagerEntity>> OCEANIZED_VILLAGER = register("oceanized_villager", OceanizedVillagerEntity.class,
            EntityType.Builder.<OceanizedVillagerEntity>of(OceanizedVillagerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 2f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedVindicatorEntity>> OCEANIZED_VINDICATOR = register("oceanized_vindicator", OceanizedVindicatorEntity.class,
            EntityType.Builder.<OceanizedVindicatorEntity>of(OceanizedVindicatorEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 2f));
    public static final DeferredHolder<EntityType<?>, EntityType<ShotOceanArrowEntity>> SHOT_OCEAN_ARROW = register("shot_ocean_arrow", ShotOceanArrowEntity.class,
            EntityType.Builder.<ShotOceanArrowEntity>of(ShotOceanArrowEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedPillagerEntity>> OCEANIZED_PILLAGER = register("oceanized_pillager", OceanizedPillagerEntity.class,
            EntityType.Builder.<OceanizedPillagerEntity>of(OceanizedPillagerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 1.9f));
    public static final DeferredHolder<EntityType<?>, EntityType<AnchorFlyEntity>> ANCHOR_FLY = register("anchor_fly", AnchorFlyEntity.class,
            EntityType.Builder.<AnchorFlyEntity>of(AnchorFlyEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.2f, 0.2f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedPigEntity>> OCEANIZED_PIG = register("oceanized_pig", OceanizedPigEntity.class,
            EntityType.Builder.<OceanizedPigEntity>of(OceanizedPigEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3)
                    .sized(0.6f, 1f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedCowEntity>> OCEANIZED_COW = register("oceanized_cow", OceanizedCowEntity.class,
            EntityType.Builder.<OceanizedCowEntity>of(OceanizedCowEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3)
                    .sized(0.6f, 1.375f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedSheepEntity>> OCEANIZED_SHEEP = register("oceanized_sheep", OceanizedSheepEntity.class,
            EntityType.Builder.<OceanizedSheepEntity>of(OceanizedSheepEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(6).setUpdateInterval(3)
                    .sized(0.6f, 1f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedHorseEntity>> OCEANIZED_HORSE = register("oceanized_horse", OceanizedHorseEntity.class,
            EntityType.Builder.<OceanizedHorseEntity>of(OceanizedHorseEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(1.15f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedPiglinEntity>> OCEANIZED_PIGLIN = register("oceanized_piglin", OceanizedPiglinEntity.class, EntityType.Builder.<OceanizedPiglinEntity>of(OceanizedPiglinEntity::new, MobCategory.MONSTER)
            .setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3).fireImmune().sized(0.6f, 2f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedBruteEntity>> OCEANIZED_BRUTE = register("oceanized_brute", OceanizedBruteEntity.class, EntityType.Builder.<OceanizedBruteEntity>of(OceanizedBruteEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(10).setUpdateInterval(3).fireImmune().sized(0.6f, 2f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedSpiderEntity>> OCEANIZED_SPIDER = register("oceanized_spider", OceanizedSpiderEntity.class,
            EntityType.Builder.<OceanizedSpiderEntity>of(OceanizedSpiderEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(7).setUpdateInterval(3)
                    .sized(0.9f, 0.7f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedEndermanEntity>> OCEANIZED_ENDERMAN = register("oceanized_enderman", OceanizedEndermanEntity.class,
            EntityType.Builder.<OceanizedEndermanEntity>of(OceanizedEndermanEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(10).setUpdateInterval(3)
                    .sized(0.6f, 3f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedWolfEntity>> OCEANIZED_WOLF = register("oceanized_wolf", OceanizedWolfEntity.class,
            EntityType.Builder.<OceanizedWolfEntity>of(OceanizedWolfEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(7).setUpdateInterval(3)
                    .sized(0.7f, 0.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedDogEntity>> OCEANIZED_DOG = register("oceanized_dog", OceanizedDogEntity.class,
            EntityType.Builder.<OceanizedDogEntity>of(OceanizedDogEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.7f, 0.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedRavagerEntity>> OCEANIZED_RAVAGER = register("oceanized_ravager", OceanizedRavagerEntity.class,
            EntityType.Builder.<OceanizedRavagerEntity>of(OceanizedRavagerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(10).setUpdateInterval(3)
                    .sized(1.9f, 2.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanziedWitchEntity>> OCEANIZED_WITCH = register("oceanized_witch", OceanziedWitchEntity.class,
            EntityType.Builder.<OceanziedWitchEntity>of(OceanziedWitchEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowablePotionEntity>> THROWABLE_POTION = register("throwable_potion", ThrowablePotionEntity.class, EntityType.Builder.<ThrowablePotionEntity>of(ThrowablePotionEntity::new, MobCategory.MISC)
            .setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.2f, 0.2f));
    public static final DeferredHolder<EntityType<?>, EntityType<IzumikOffspringEntity>> IZUMIK_OFFSPRING = register("izumik_offspring", IzumikOffspringEntity.class,
            EntityType.Builder.<IzumikOffspringEntity>of(IzumikOffspringEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(0.5f, 0.9f));
    public static final DeferredHolder<EntityType<?>, EntityType<CaerulaOffspringEntity>> CAERULA_OFFSPRING = register("caerula_offspring", CaerulaOffspringEntity.class,
            EntityType.Builder.<CaerulaOffspringEntity>of(CaerulaOffspringEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(0.5f, 0.9f));
    public static final DeferredHolder<EntityType<?>, EntityType<IzumikEntity>> IZUMIK = register("izumik", IzumikEntity.class,
            EntityType.Builder.<IzumikEntity>of(IzumikEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(21).setUpdateInterval(3).fireImmune().sized(3.3f, 9f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedEvokerEntity>> OCEANIZED_EVOKER = register("oceanized_evoker", OceanizedEvokerEntity.class,
            EntityType.Builder.<OceanizedEvokerEntity>of(OceanizedEvokerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 1.9f));
    public static final DeferredHolder<EntityType<?>, EntityType<JuniorWarriorPriestEntity>> JUNIOR_WARRIOR_PRIEST = register("junior_warrior_priest", JuniorWarriorPriestEntity.class,
            EntityType.Builder.<JuniorWarriorPriestEntity>of(JuniorWarriorPriestEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<WarriorPriestEntity>> WARRIOR_PRIEST = register("warrior_priest", WarriorPriestEntity.class,
            EntityType.Builder.<WarriorPriestEntity>of(WarriorPriestEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<CorrectionalPhalanxyInfantryEntity>> CORRECTIONAL_PHALANXY_INFANTRY = register("correctional_phalanxy_infantry", CorrectionalPhalanxyInfantryEntity.class,
            EntityType.Builder.<CorrectionalPhalanxyInfantryEntity>of(CorrectionalPhalanxyInfantryEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<CorrectinalPhalaxVanguardEntity>> CORRECTIONAL_PHALAX_VANGUARD = register("correctional_phalax_vanguard", CorrectinalPhalaxVanguardEntity.class,
            EntityType.Builder.<CorrectinalPhalaxVanguardEntity>of(CorrectinalPhalaxVanguardEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)
                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<TribunalHealerEntity>> TRIBUNAL_HEALER = register("tribunal_healer", TribunalHealerEntity.class,
            EntityType.Builder.<TribunalHealerEntity>of(TribunalHealerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<HealBullletEntity>> HEAL_BULLLET = register("heal_bulllet", HealBullletEntity.class,
            EntityType.Builder.<HealBullletEntity>of(HealBullletEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.1f, 0.1f));
    public static final DeferredHolder<EntityType<?>, EntityType<MartusEntity>> MARTUS = register("martus", MartusEntity.class,
            EntityType.Builder.<MartusEntity>of(MartusEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).fireImmune().sized(0.7f, 2.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<TheAbandonedEntity>> THE_ABANDONED = register("the_abandoned", TheAbandonedEntity.class,
            EntityType.Builder.<TheAbandonedEntity>of(TheAbandonedEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.7f, 2.25f));
    public static final DeferredHolder<EntityType<?>, EntityType<AbandonedShootEntity>> ABANDONED_SHOOT = register("abandoned_shoot", AbandonedShootEntity.class,
            EntityType.Builder.<AbandonedShootEntity>of(AbandonedShootEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
    public static final DeferredHolder<EntityType<?>, EntityType<GunmuEntity>> GUNMU = register("gunmu", GunmuEntity.class,
            EntityType.Builder.<GunmuEntity>of(GunmuEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).fireImmune().sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedWardenEntity>> OCEANIZED_WARDEN = register("oceanized_warden", OceanizedWardenEntity.class, EntityType.Builder.<OceanizedWardenEntity>of(OceanizedWardenEntity::new, MobCategory.MONSTER)
            .setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).fireImmune().sized(1.2f, 3.1f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedCatEntity>> OCEANIZED_CAT = register("oceanized_cat", OceanizedCatEntity.class,
            EntityType.Builder.<OceanizedCatEntity>of(OceanizedCatEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.5f, 0.625f));
    public static final DeferredHolder<EntityType<?>, EntityType<SuperBigCatEntity>> SUPER_BIG_CAT = register("super_big_cat", SuperBigCatEntity.class,
            EntityType.Builder.<SuperBigCatEntity>of(SuperBigCatEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(3.5f, 4f));
    public static final DeferredHolder<EntityType<?>, EntityType<ComplexChitinGolemEntity>> COMPLEX_CHITIN_GOLEM = register("complex_chitin_golem", ComplexChitinGolemEntity.class, EntityType.Builder.<ComplexChitinGolemEntity>of(ComplexChitinGolemEntity::new, MobCategory.MONSTER)
            .setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).fireImmune().sized(1.75f, 4f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedWardenisEntity>> OCEANIZED_WARDENIS = register("oceanized_wardenis", OceanizedWardenisEntity.class, EntityType.Builder.<OceanizedWardenisEntity>of(OceanizedWardenisEntity::new, MobCategory.MONSTER)
            .setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).fireImmune().sized(0.6f, 1.85f));
    public static final DeferredHolder<EntityType<?>, EntityType<NucleicMaleficentEntity>> NUCLEIC_MALEFICENT = register("nucleic_maleficent", NucleicMaleficentEntity.class,
            EntityType.Builder.<NucleicMaleficentEntity>of(NucleicMaleficentEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)
                    .sized(0.7f, 1.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedWitherEntity>> OCEANIZED_WITHER = register("oceanized_wither", OceanizedWitherEntity.class, EntityType.Builder.<OceanizedWitherEntity>of(OceanizedWitherEntity::new, MobCategory.MONSTER)
            .setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).fireImmune().sized(1.1f, 3.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<WitherShootPreEntity>> WITHER_SHOOT_PRE = register("wither_shoot_pre", WitherShootPreEntity.class,
            EntityType.Builder.<WitherShootPreEntity>of(WitherShootPreEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.1f, 0.1f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedWitheriaEntity>> OCEANIZED_WITHERIA = register("oceanized_witheria", OceanizedWitheriaEntity.class, EntityType.Builder.<OceanizedWitheriaEntity>of(OceanizedWitheriaEntity::new, MobCategory.MONSTER)
            .setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3).fireImmune().sized(0.7f, 2.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<TheLastKnightEntity>> THE_LAST_KNIGHT = register("the_last_knight", TheLastKnightEntity.class, EntityType.Builder.<TheLastKnightEntity>of(TheLastKnightEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(16).setUpdateInterval(3).fireImmune().sized(1.0f, 3.4f));
    public static final DeferredHolder<EntityType<?>, EntityType<RocinanteEntity>> ROCINANTE = register("rocinante", RocinanteEntity.class,
            EntityType.Builder.<RocinanteEntity>of(RocinanteEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(1.2f, 3f).attach(EntityAttachment.PASSENGER, 0f, 2.5f, 0f));
    public static final DeferredHolder<EntityType<?>, EntityType<ApocataEntity>> APOCATA = register("apocata", ApocataEntity.class,
            EntityType.Builder.<ApocataEntity>of(ApocataEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)
                    .sized(0.6f, 1.85f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedFoxEntity>> OCEANIZED_FOX = register("oceanized_fox", OceanizedFoxEntity.class,
            EntityType.Builder.<OceanizedFoxEntity>of(OceanizedFoxEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 0.7f));
    public static final DeferredHolder<EntityType<?>, EntityType<TidutantExcrescenceEntity>> TIDUTANT_EXCRESCENCE = register("tidutant_excrescence", TidutantExcrescenceEntity.class,
            EntityType.Builder.<TidutantExcrescenceEntity>of(TidutantExcrescenceEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.5f, 0.4f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedPolarBearEntity>> OCEANIZED_POLAR_BEAR = register("oceanized_polar_bear", OceanizedPolarBearEntity.class,
            EntityType.Builder.<OceanizedPolarBearEntity>of(OceanizedPolarBearEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(1f, 1.25f));
    public static final DeferredHolder<EntityType<?>, EntityType<TideutantRockSpiderEntity>> TIDUTANT_ROCK_SPIDER = register("tidutant_rock_spider", TideutantRockSpiderEntity.class,
            EntityType.Builder.<TideutantRockSpiderEntity>of(TideutantRockSpiderEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<EndspeakerEntity>> ENDSPEAKER = register("endspeaker", EndspeakerEntity.class,
            EntityType.Builder.<EndspeakerEntity>of(EndspeakerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(14).setUpdateInterval(3)
                    .sized(1f, 3.375f));
    public static final DeferredHolder<EntityType<?>, EntityType<LineringPathshaperEntity>> LINGERING_PATHSHAPER = register("lingering_pathshaper", LineringPathshaperEntity.class,
            EntityType.Builder.<LineringPathshaperEntity>of(LineringPathshaperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3)
                    .sized(1.8f, 4f));
    public static final DeferredHolder<EntityType<?>, EntityType<LingeringFractalEntity>> LINGERING_FRACTAL = register("lingering_fractal", LingeringFractalEntity.class,
            EntityType.Builder.<LingeringFractalEntity>of(LingeringFractalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(0.7f, 1.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<LittleHelperEntity>> LITTLE_HELPER = register("little_helper", LittleHelperEntity.class, EntityType.Builder.<LittleHelperEntity>of(LittleHelperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(12).setUpdateInterval(3).fireImmune().sized(0.875f, 0.2f).attach(EntityAttachment.PASSENGER, 0f, -0.13f, 0f));
    public static final DeferredHolder<EntityType<?>, EntityType<Al1SHelperEntity>> AL_1_S_HELPER = register("al_1_s_helper", Al1SHelperEntity.class, EntityType.Builder.<Al1SHelperEntity>of(Al1SHelperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(12).setUpdateInterval(3).fireImmune().sized(0.875f, 0.2f));
    public static final DeferredHolder<EntityType<?>, EntityType<UlpiansEntity>> ULPIANS = register("ulpians", UlpiansEntity.class,
            EntityType.Builder.<UlpiansEntity>of(UlpiansEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<GladiiaEntity>> GLADIIA = register("gladiia", GladiiaEntity.class,
            EntityType.Builder.<GladiiaEntity>of(GladiiaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<GladiiaWhirlEntity>> GLADIIA_WHIRL = register("gladiia_whirl", GladiiaWhirlEntity.class,
            EntityType.Builder.<GladiiaWhirlEntity>of(GladiiaWhirlEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3)
                    .sized(2f, 2f));
    public static final DeferredHolder<EntityType<?>, EntityType<SpecterEntity>> SPECTER = register("specter", SpecterEntity.class,
            EntityType.Builder.<SpecterEntity>of(SpecterEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<SpecterDollEntity>> SPECTER_DOLL = register("specter_doll", SpecterDollEntity.class,
            EntityType.Builder.<SpecterDollEntity>of(SpecterDollEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<IreneEntity>> IRENE = register("irene", IreneEntity.class,
            EntityType.Builder.<IreneEntity>of(IreneEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<TideChimeraEntity>> TIDE_CHIMERA = register("tide_chimera", TideChimeraEntity.class,
            EntityType.Builder.<TideChimeraEntity>of(TideChimeraEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3)
                    .sized(1.8f, 4.65f));
    public static final DeferredHolder<EntityType<?>, EntityType<SkadiCorruptedEntity>> SKADI_CORRUPTED = register("skadi_corrupted", SkadiCorruptedEntity.class, EntityType.Builder.<SkadiCorruptedEntity>of(SkadiCorruptedEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(16).setUpdateInterval(3).fireImmune().sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizeRabbitEntity>> OCEANIZE_RABBIT = register("oceanize_rabbit", OceanizeRabbitEntity.class,
            EntityType.Builder.<OceanizeRabbitEntity>of(OceanizeRabbitEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.4f, 0.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<SaintCarmenEntity>> SAINT_CARMEN = register("saint_carmen", SaintCarmenEntity.class,
            EntityType.Builder.<SaintCarmenEntity>of(SaintCarmenEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3)
                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<CarmenBulletEntity>> CARMEN_BULLET = register("carmen_bullet", CarmenBulletEntity.class,
            EntityType.Builder.<CarmenBulletEntity>of(CarmenBulletEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedIllusionerEntity>> OCEANIZED_ILLUSIONER = register("oceanized_illusioner", OceanizedIllusionerEntity.class,
            EntityType.Builder.<OceanizedIllusionerEntity>of(OceanizedIllusionerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(10).setUpdateInterval(3)
                    .sized(0.6f, 1.9f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanIllusionEntity>> OCEAN_ILLUSION = register("ocean_illusion", OceanIllusionEntity.class,
            EntityType.Builder.<OceanIllusionEntity>of(OceanIllusionEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3)
                    .sized(0.6f, 1.9f));
    public static final DeferredHolder<EntityType<?>, EntityType<FlamarineStatueEntity>> FLAMARINE_STATUE = register("flamarine_statue", FlamarineStatueEntity.class, EntityType.Builder.<FlamarineStatueEntity>of(FlamarineStatueEntity::new, MobCategory.MONSTER)
            .setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3).fireImmune().sized(0.7f, 2f));
    public static final DeferredHolder<EntityType<?>, EntityType<NautilusHeadhunterEntity>> NAUTILUS_HEADHUNTER = register("nautilus_headhunter", NautilusHeadhunterEntity.class,
            EntityType.Builder.<NautilusHeadhunterEntity>of(NautilusHeadhunterEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3)
                    .sized(0.5f, 0.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<XantisEntity>> XANTIS = register("xantis", XantisEntity.class,
            EntityType.Builder.<XantisEntity>of(XantisEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3)
                    .sized(0.5f, 0.68f));
    public static final DeferredHolder<EntityType<?>, EntityType<FlamarineGolemEntity>> FLAMARINE_GOLEM = register("flamarine_golem", FlamarineGolemEntity.class, EntityType.Builder.<FlamarineGolemEntity>of(FlamarineGolemEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(12).setUpdateInterval(3).fireImmune().sized(1f, 2.75f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedVexEntity>> OCEANIZED_VEX = register("oceanized_vex", OceanizedVexEntity.class,
            EntityType.Builder.<OceanizedVexEntity>of(OceanizedVexEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.4f, 0.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<IsharmlaEntity>> ISHARMLA = register("isharmla", IsharmlaEntity.class,
            EntityType.Builder.<IsharmlaEntity>of(IsharmlaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3)
                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<QunyouWantedIsharmlaEntity>> QUNYOU_WANTED_ISHARMLA = register("qunyou_wanted_isharmla", QunyouWantedIsharmlaEntity.class,
            EntityType.Builder.<QunyouWantedIsharmlaEntity>of(QunyouWantedIsharmlaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3)
                    .sized(20f, 20f));
    public static final DeferredHolder<EntityType<?>, EntityType<IsharmlaTearEntity>> ISHARMLA_TEAR = register("isharmla_tear", IsharmlaTearEntity.class,
            EntityType.Builder.<IsharmlaTearEntity>of(IsharmlaTearEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)
                    .sized(1f, 0.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<PrayerSplashEntity>> PRAYER_SPLASH = register("prayer_splash", PrayerSplashEntity.class,
            EntityType.Builder.<PrayerSplashEntity>of(PrayerSplashEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.3f, 0.3f));
    public static final DeferredHolder<EntityType<?>, EntityType<CompassionPrayerEntity>> COMPASSION_PRAYER = register("compassion_prayer", CompassionPrayerEntity.class,
            EntityType.Builder.<CompassionPrayerEntity>of(CompassionPrayerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3)
                    .sized(0.6f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedEnderinaEntity>> OCEANIZED_ENDERINA = register("oceanized_enderina", OceanizedEnderinaEntity.class,
            EntityType.Builder.<OceanizedEnderinaEntity>of(OceanizedEnderinaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).fireImmune());
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedEnderDragonEntity>> OCEANIZED_ENDER_DRAGON = register("oceanized_ender_dragon", OceanizedEnderDragonEntity.class,
            EntityType.Builder.<OceanizedEnderDragonEntity>of(OceanizedEnderDragonEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).fireImmune()
                    .sized(6.0f, 3.6f));
    public static final DeferredHolder<EntityType<?>, EntityType<MoistDragonBreathEntity>> MOIST_DRAGON_BREATH = register("moist_dragon_breath", MoistDragonBreathEntity.class,
            EntityType.Builder.<MoistDragonBreathEntity>of(MoistDragonBreathEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3)
                    .sized(0.5f, 0.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<MoistEnderCrystalEntity>> MOIST_ENDER_CRYSTAL = register("moist_ender_crystal", MoistEnderCrystalEntity.class,
            EntityType.Builder.<MoistEnderCrystalEntity>of(MoistEnderCrystalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)
                    .sized(2f, 2f));
    public static final DeferredHolder<EntityType<?>, EntityType<ThirsterEntity>> THIRSTER = register("thirster", ThirsterEntity.class,
            EntityType.Builder.<ThirsterEntity>of(ThirsterEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(1.5f, 1.75f));
    public static final DeferredHolder<EntityType<?>, EntityType<AbsorberLimbEntity>> ABSORBER_LIMB = register("absorber_limb", AbsorberLimbEntity.class,
            EntityType.Builder.<AbsorberLimbEntity>of(AbsorberLimbEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(12).setUpdateInterval(3)
                    .sized(0.4f, 1f));
    public static final DeferredHolder<EntityType<?>, EntityType<ScreamChestFishEntity>> SCREAM_CHEST_FISH = register("scream_chest_fish", ScreamChestFishEntity.class,
            EntityType.Builder.<ScreamChestFishEntity>of(ScreamChestFishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)
                    .sized(0.9f, 0.9f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedChickenEntity>> OCEANIZED_CHICKEN = register("oceanized_chicken", OceanizedChickenEntity.class,
            EntityType.Builder.<OceanizedChickenEntity>of(OceanizedChickenEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3)
                    .sized(0.5f, 0.875f).attach(EntityAttachment.PASSENGER, 0f, 0.675f, 0f));
    public static final DeferredHolder<EntityType<?>, EntityType<NetherseaSlimeEntity>> NETHERSEA_SLIME = register("nethersea_slime", NetherseaSlimeEntity.class,
            EntityType.Builder.<NetherseaSlimeEntity>of(NetherseaSlimeEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3)
                    .sized(1f, 1f));
    public static final DeferredHolder<EntityType<?>, EntityType<FissionProkaryoteSlimeEntity>> FISSION_PROKARYOTE_SLIME = register("fission_prokaryote_slime", FissionProkaryoteSlimeEntity.class,
            EntityType.Builder.<FissionProkaryoteSlimeEntity>of(FissionProkaryoteSlimeEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3)
                    .sized(1f, 1f));
    public static final DeferredHolder<EntityType<?>, EntityType<OceanizedShulkerEntity>> OCEANIZED_SHULKER = register("oceanized_shulker", OceanizedShulkerEntity.class,
            EntityType.Builder.<OceanizedShulkerEntity>of(OceanizedShulkerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(9).setUpdateInterval(3)
                    .sized(1f, 1f));
    private static final List<DeferredHolder<EntityType<?>, ? extends EntityType<? extends LivingEntity>>> LIVING_ENTITY_TYPES;
    private static final List<DeferredHolder<EntityType<?>, ? extends EntityType<? extends LivingEntity>>> SUMMONABLE_ENTITY_TYPES;

    static {
        addAttributeRegistration(SHELL_SEA_RUNNER, ShellSeaRunnerEntity::createAttributes);
        addAttributeRegistration(SLIDER_FISH, SliderFishEntity::createAttributes);
        addAttributeRegistration(SUPER_SLIDER, SuperSliderEntity::createAttributes);
        addAttributeRegistration(SHOOTER_FISH, ShooterFishEntity::createAttributes);
        addAttributeRegistration(FLOATING_SEA_DRIFTER, FloatingSeaDrifterEntity::createAttributes);
        addAttributeRegistration(REAPER_FISH, ReaperFishEntity::createAttributes);
        addAttributeRegistration(POCKET_SEA_CREEPER, PocketSeaCreeperEntity::createAttributes);
        addAttributeRegistration(POCKET_SEA_CRAWLER, PocketSeaCrawlerEntity::createAttributes);
        addAttributeRegistration(PUNCTURE_FISH, PunctureFishEntity::createAttributes);
        addAttributeRegistration(BASELAYER_ABYSSAL, BaselayerAbyssalEntity::createAttributes);
        addAttributeRegistration(PREDATOR_ABYSSAL, PredatorAbyssalEntity::createAttributes);
        addAttributeRegistration(GUIDE_ABYSSAL, GuideAbyssalEntity::createAttributes);
        addAttributeRegistration(SPLASHER_ABYSSAL, SplasherAbyssalEntity::createAttributes);
        addAttributeRegistration(UMBRELLA_ABYSSAL, UmbrellaAbyssalEntity::createAttributes);
        addAttributeRegistration(CRACKER_ABYSSAL, CrackerAbyssalEntity::createAttributes);
        addAttributeRegistration(COLLECTOR_PROKARYOTE, CollectorProkaryoteEntity::createAttributes);
        addAttributeRegistration(BONE_FISH, BoneFishEntity::createAttributes);
        addAttributeRegistration(OCEAN_STONECUTTE, OceanStonecutteEntity::createAttributes);
        addAttributeRegistration(PREGNANT_FISH, PregnantFishEntity::createAttributes);
        addAttributeRegistration(FAKE_OFFSPRING, FakeOffspringEntity::createAttributes);
        addAttributeRegistration(SKIMMING_SEA_DRIFTER, SkimmingSeaDrifterEntity::createAttributes);
        addAttributeRegistration(ROUTE_SHAPER, RouteShaperEntity::createAttributes);
        addAttributeRegistration(ROUTE_FRACTAL, RouteFractalEntity::createAttributes);
        addAttributeRegistration(FIRST_TO_TALK, FirstTellerEntity::createAttributes);
        addAttributeRegistration(REAPER_PET, ReaperPetEntity::createAttributes);
        addAttributeRegistration(BISHOP_FISH, BishopFishEntity::createAttributes);
        addAttributeRegistration(TIDELINKED_BISHOP, TidelinkedBishopEntity::createAttributes);
        addAttributeRegistration(SONS, SonsEntity::createAttributes);
        addAttributeRegistration(FLOATER_PROKARYOTE, FloaterProkaryoteEntity::createAttributes);
        addAttributeRegistration(CHITIN_GOLEM, ChitinGolemEntity::createAttributes);
        addAttributeRegistration(TIDELINKED_IMMORTAL, TidelinkedImmortalEntity::createAttributes);
        addAttributeRegistration(TIDELINKED_ARCHON, TidelinkedArchonEntity::createAttributes);
        addAttributeRegistration(MEGA_CHEST, MegaChestEntity::createAttributes);
        addAttributeRegistration(APOSTLE_PROKARYOTE, ApostleProkaryoteEntity::createAttributes);
        addAttributeRegistration(HIGHMORE, HighmoreEntity::createAttributes);
        addAttributeRegistration(ACCUMULATOR_PROKARYOTE, AccumulatorProkaryoteEntity::createAttributes);
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
        addAttributeRegistration(FISSION_PROKARYOTE_SLIME, FissionProkaryoteSlimeEntity::createAttributes);
        addAttributeRegistration(OCEANIZED_SHULKER, OceanizedShulkerEntity::createAttributes);

        markSummonable(SHELL_SEA_RUNNER);
        markSummonable(SLIDER_FISH);
        markSummonable(SUPER_SLIDER);
        markSummonable(SHOOTER_FISH);
        markSummonable(FLOATING_SEA_DRIFTER);
        markSummonable(REAPER_FISH);
        markSummonable(POCKET_SEA_CREEPER);
        markSummonable(POCKET_SEA_CRAWLER);
        markSummonable(PUNCTURE_FISH);
        markSummonable(BASELAYER_ABYSSAL);
        markSummonable(PREDATOR_ABYSSAL);
        markSummonable(GUIDE_ABYSSAL);
        markSummonable(SPLASHER_ABYSSAL);
        markSummonable(UMBRELLA_ABYSSAL);
        markSummonable(CRACKER_ABYSSAL);
        markSummonable(COLLECTOR_PROKARYOTE);
        markSummonable(BONE_FISH);
        markSummonable(OCEAN_STONECUTTE);
        markSummonable(PREGNANT_FISH);
        markSummonable(FAKE_OFFSPRING);
        markSummonable(SKIMMING_SEA_DRIFTER);
        markSummonable(ROUTE_SHAPER);
        markSummonable(ROUTE_FRACTAL);
        markSummonable(FIRST_TO_TALK);
        markSummonable(REAPER_PET);
        markSummonable(BISHOP_FISH);
        markSummonable(TIDELINKED_BISHOP);
        markSummonable(SONS);
        markSummonable(FLOATER_PROKARYOTE);
        markSummonable(CHITIN_GOLEM);
        markSummonable(TIDELINKED_IMMORTAL);
        markSummonable(TIDELINKED_ARCHON);
        markSummonable(MEGA_CHEST);
        markSummonable(APOSTLE_PROKARYOTE);
        markSummonable(HIGHMORE);
        markSummonable(ACCUMULATOR_PROKARYOTE);
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

        addSpawnPlacementRegistration(SHELL_SEA_RUNNER, ShellSeaRunnerEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(SLIDER_FISH, SliderFishEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(SHOOTER_FISH, ShooterFishEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(FLOATING_SEA_DRIFTER, FloatingSeaDrifterEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(REAPER_FISH, ReaperFishEntity::registerSpawnPlacements);
         addSpawnPlacementRegistration(POCKET_SEA_CRAWLER, PocketSeaCrawlerEntity::registerSpawnPlacements);
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
        addSpawnPlacementRegistration(OCEAN_STONECUTTE, OceanStonecutteEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(PREGNANT_FISH, PregnantFishEntity::registerSpawnPlacements);
        addSpawnPlacementRegistration(SKIMMING_SEA_DRIFTER, SkimmingSeaDrifterEntity::registerSpawnPlacements);
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

        LIVING_ENTITY_TYPES = collectLivingEntityTypes(false);
        SUMMONABLE_ENTITY_TYPES = collectLivingEntityTypes(true);
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryName, Class<T> entityClass, EntityType.Builder<T> builder) {
        DeferredHolder<EntityType<?>, EntityType<T>> entityType = REGISTRY.register(registryName, () -> builder.build(registryName));
        ENTITY_REGISTRATIONS.put(entityType, new EntityRegistrationData());
        if (SeaMonster.class.isAssignableFrom(entityClass)) {
            SEA_MONSTERS.add(entityType);
        }
        if (SeaMonsterBoss.class.isAssignableFrom(entityClass)) {
            SEA_MONSTER_BOSSES.add(entityType);
        }
        return entityType;
    }

    private static <T extends LivingEntity> void addAttributeRegistration(
            DeferredHolder<EntityType<?>, EntityType<T>> entityType,
            Supplier<AttributeSupplier.Builder> attributes
    ) {
        EntityRegistrationData data = registrationData(entityType);
        data.livingEntityType = entityType;
        data.attributeRegistration = event -> event.put(entityType.get(), attributes.get().build());
    }

    private static <T extends LivingEntity> void markSummonable(DeferredHolder<EntityType<?>, EntityType<T>> entityType) {
        registrationData(entityType).summonable = true;
    }

    private static void addSpawnPlacementRegistration(DeferredHolder<? extends EntityType<?>, ? extends EntityType<?>> entityType, Consumer<RegisterSpawnPlacementsEvent> registration) {
        registrationData(entityType).spawnPlacementRegistration = registration;
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
    public static List<DeferredHolder<EntityType<?>, ? extends EntityType<? extends LivingEntity>>> getLivingEntityTypes() {
        return LIVING_ENTITY_TYPES;
    }

    /**
     * 获取具有可召唤属性的生物实体类型
     *
     * @return 按实体注册顺序排列的不可修改列表
     */
    public static List<DeferredHolder<EntityType<?>, ? extends EntityType<? extends LivingEntity>>> getSummonableEntityTypes() {
        return SUMMONABLE_ENTITY_TYPES;
    }

    private static List<DeferredHolder<EntityType<?>, ? extends EntityType<? extends LivingEntity>>> collectLivingEntityTypes(boolean summonableOnly) {
        List<DeferredHolder<EntityType<?>, ? extends EntityType<? extends LivingEntity>>> entityTypes = new ArrayList<>();
        for (EntityRegistrationData data : ENTITY_REGISTRATIONS.values()) {
            if (data.livingEntityType != null && (!summonableOnly || data.summonable)) {
                entityTypes.add(data.livingEntityType);
            }
        }
        return List.copyOf(entityTypes);
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        for (EntityRegistrationData data : ENTITY_REGISTRATIONS.values()) {
            if (data.spawnPlacementRegistration != null) {
                data.spawnPlacementRegistration.accept(event);
            }
        }
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
        private DeferredHolder<EntityType<?>, ? extends EntityType<? extends LivingEntity>> livingEntityType;
        private Consumer<EntityAttributeCreationEvent> attributeRegistration;
        private Consumer<RegisterSpawnPlacementsEvent> spawnPlacementRegistration;
        private boolean summonable;
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class CARenderers {
        private static final LinkedHashMap<DeferredHolder<? extends EntityType<?>, ? extends EntityType<?>>, Consumer<EntityRenderersEvent.RegisterRenderers>> RENDERER_REGISTRATIONS = new LinkedHashMap<>();

        static {
            addRenderer(CAEntities.SHELL_SEA_RUNNER, ShellSeaRunnerRenderer::new);
            addRenderer(CAEntities.SLIDER_FISH, SliderFishRenderer::new);
            addRenderer(CAEntities.SUPER_SLIDER, SuperSliderRenderer::new);
            addRenderer(CAEntities.SHOOTER_FISH, ShooterFishRenderer::new);
            addRenderer(CAEntities.FISH_SHOOT, FishShootRenderer::new);
            addRenderer(CAEntities.FLOATING_SEA_DRIFTER, FloatingSeaDrifterRenderer::new);
            addRenderer(CAEntities.REAPER_FISH, ReaperFishRenderer::new);
            addRenderer(CAEntities.POCKET_SEA_CREEPER, PocketSeaCreeperRenderer::new);
            addRenderer(CAEntities.POCKET_SEA_CRAWLER, PocketSeaCrawlerRenderer::new);
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
            addRenderer(CAEntities.OCEAN_STONECUTTE, OceanStonecutteRenderer::new);
            addRenderer(CAEntities.FAKERGG_SHOOT, FakerggShootRenderer::new);
            addRenderer(CAEntities.PREGNANT_FISH, PregnantFishRenderer::new);
            addRenderer(CAEntities.FAKE_OFFSPRING, FakeOffspringRenderer::new);
            addRenderer(CAEntities.SKIMMING_SEA_BULLET, SkimmingSeaBulletRenderer::new);
            addRenderer(CAEntities.SKIMMING_SEA_DRIFTER, SkimmingSeaDrifterRenderer::new);
            addRenderer(CAEntities.ROUTE_SHAPER, RouteShaperRenderer::new);
            addRenderer(CAEntities.ROUTE_FRACTAL, RouteFractalRenderer::new);
            addRenderer(CAEntities.TELLER_SHOT, TellerShotRenderer::new);
            addRenderer(CAEntities.FIRST_TO_TALK, FirstTellerRenderer::new);
            addRenderer(CAEntities.REAPER_PET, ReaperPetRenderer::new);
            addRenderer(CAEntities.BISHOP_FISH, BishopFishRenderer::new);
            addRenderer(CAEntities.TIDELINKED_BISHOP, TidelinkedBishopRenderer::new);
            addRenderer(CAEntities.SONS, SonsRenderer::new);
            addRenderer(CAEntities.FLOATER_PROKARYOTE, FloaterProkaryoteRenderer::new);
            addRenderer(CAEntities.CHITIN_GOLEM, ChitinGolemRenderer::new);
            addRenderer(CAEntities.TIDELINKED_IMMORTAL, TidelinkedImmortalRenderer::new);
            addRenderer(CAEntities.TIDELINKED_ARCHON, TidelinkedArchonRenderer::new);
            addRenderer(CAEntities.MEGA_CHEST, MegaChestRenderer::new);
            addRenderer(CAEntities.APOSTLE_PROKARYOTE, ApostleProkaryoteRenderer::new);
            addRenderer(CAEntities.HIGHMORE_SHOOT, HighmoreShootRenderer::new);
            addRenderer(CAEntities.HIGHMORE, HighmoreRenderer::new);
            addRenderer(CAEntities.ACCUMULATOR_PROKARYOTE, AccumulatorProkaryoteRenderer::new);
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
            addRenderer(CAEntities.WITHER_SHOOT_PRE, WitherShootPreRenderer::new);
            addRenderer(CAEntities.OCEANIZED_WITHERIA, OceannizedWitheriaRenderer::new);
            addRenderer(CAEntities.THE_LAST_KNIGHT, TheLastKnightRenderer::new);
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
            addRenderer(CAEntities.FISSION_PROKARYOTE_SLIME, FissionProkaryoteSlimeRenderer::new);
            addRenderer(CAEntities.OCEANIZED_SHULKER, OceanizedShulkerRenderer::new);
        }

        private static <T extends Entity> void addRenderer(DeferredHolder<EntityType<?>, EntityType<T>> entityType, EntityRendererProvider<T> renderer) {
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
            LAYER_DEFINITIONS.put(ModelSkimmingSeaBullet.LAYER_LOCATION, ModelSkimmingSeaBullet::createBodyLayer);
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
