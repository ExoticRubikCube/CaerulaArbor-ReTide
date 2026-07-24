package com.susen36.caerulaarbor;

import com.mojang.logging.LogUtils;
import com.susen36.caerulaarbor.init.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod(CaerulaArborMod.MODID)
public class CaerulaArborMod {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "caerula_arbor";
    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

    public CaerulaArborMod(IEventBus modEventBus) {
        CALootModifier.init(FMLJavaModLoadingContext.get());

        FMLJavaModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CAConfigs.SPEC, "caerular_configs.toml");
        NeoForge.EVENT_BUS.register(this);
        CASounds.REGISTRY.register(modEventBus);
        CABlocks.REGISTRY.register(modEventBus);
        CABlockEntities.REGISTRY.register(modEventBus);
        CAItems.REGISTRY.register(modEventBus);
        CAEntities.REGISTRY.register(modEventBus);
        CAEnchantments.REGISTRY.register(modEventBus);
        CATabs.REGISTRY.register(modEventBus);

        CAMobEffects.REGISTRY.register(modEventBus);
        CAPotions.REGISTRY.register(modEventBus);
        CAPaintings.REGISTRY.register(modEventBus);
        CAParticles.REGISTRY.register(modEventBus);
        CAVillagerProfessions.PROFESSIONS.register(modEventBus);
        CAMenus.REGISTRY.register(modEventBus);
        CAAttributes.REGISTRY.register(modEventBus);
        modEventBus.addListener(this::onCommonSetup);
    }

    public static void queueServerWork(int tick, Runnable action) {
        workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
    }

    public static ResourceLocation ModLoc(String path) {
        var patchedPath = path.toLowerCase();
        return ResourceLocation.fromNamespaceAndPath(MODID, patchedPath);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        CANetwork.register();
        CACompostableItems.addComposterItems(event);
    }

    @SubscribeEvent
    public void tick(ServerTickEvent.Post event) {
        List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
        workQueue.forEach(work -> {
            work.setValue(work.getValue() - 1);
            if (work.getValue() == 0)
                actions.add(work);
        });
        actions.forEach(e -> e.getKey().run());
        workQueue.removeAll(actions);
    }
}
