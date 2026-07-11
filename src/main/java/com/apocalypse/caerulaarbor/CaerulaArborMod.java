package com.apocalypse.caerulaarbor;

import com.apocalypse.caerulaarbor.init.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod(CaerulaArborMod.MODID)
public class CaerulaArborMod {
    public static final Logger LOGGER = LogManager.getLogger(CaerulaArborMod.class);
    public static final String MODID = "caerula_arbor";
    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

    public CaerulaArborMod(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, CAConfigs.SPEC, "caerular_configs.toml");
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = context.getModEventBus();
        CASounds.REGISTRY.register(bus);
        CABlocks.REGISTRY.register(bus);
        CABlockEntities.REGISTRY.register(bus);
        CAItems.REGISTRY.register(bus);
        CAEntities.REGISTRY.register(bus);
        CAEnchantments.REGISTRY.register(bus);
        CATabs.REGISTRY.register(bus);

        CAMobEffects.REGISTRY.register(bus);
        CAPotions.REGISTRY.register(bus);
        CAPaintings.REGISTRY.register(bus);
        CAParticles.REGISTRY.register(bus);
        CAVillagerProfessions.PROFESSIONS.register(bus);
        CAMenus.REGISTRY.register(bus);
        CAAttributes.REGISTRY.register(bus);
        bus.addListener(this::onCommonSetup);
    }

    public static void queueServerWork(int tick, Runnable action) {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
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
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
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
}
