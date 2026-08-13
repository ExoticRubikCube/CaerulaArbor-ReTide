package com.susen36.caerulaarbor;

import com.mojang.logging.LogUtils;
import com.susen36.caerulaarbor.capability.CapabilityEventHandler;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.init.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod(CaerulaArbor.MODID)
public class CaerulaArbor {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "caerula_arbor";
    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

    public CaerulaArbor(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, CAConfigs.SPEC, "caerular_configs.toml");
        NeoForge.EVENT_BUS.register(this);
        CASounds.REGISTRY.register(modEventBus);
        CABlocks.REGISTRY.register(modEventBus);
        CABlockEntities.REGISTRY.register(modEventBus);
        CAItems.REGISTRY.register(modEventBus);
        CAItems.COLLECTIBLE.register(modEventBus);
        CAEntities.REGISTRY.register(modEventBus);
        CATabs.REGISTRY.register(modEventBus);

        CAMobEffects.REGISTRY.register(modEventBus);
        CAPotions.REGISTRY.register(modEventBus);
        CAPaintings.REGISTRY.register(modEventBus);
        CAParticles.REGISTRY.register(modEventBus);
        CAVillagerProfessions.POIS.register(modEventBus);
        CAVillagerProfessions.PROFESSIONS.register(modEventBus);
        CAMenus.REGISTRY.register(modEventBus);
        CAAttributes.REGISTRY.register(modEventBus);
        ModCapabilities.register(modEventBus);
        CARelics.register(modEventBus);
        CARelics.REGISTRY.register(modEventBus);
        CALootModifier.init(modEventBus);
        CAGameRules.init();
        modEventBus.addListener(CapabilityEventHandler::registerBlockCapabilities);
    }

    public static void queueServerWork(int tick, Runnable action) {
        workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
    }

    public static ResourceLocation ModLoc(String path) {
        var patchedPath = path.toLowerCase();
        return ResourceLocation.fromNamespaceAndPath(MODID, patchedPath);
    }

    // TODO: MCreator 模板延迟队列
    //   远期迁移方向（非 MCreator 生态主流方案）：
    //   方案 A: vanilla MinecraftServer.tell(new TickTask(server.getTickCount()+delay, runnable))
    //   适用于能拿到 MinecraftServer 实例的调用点，零基础设施
    //   方案 B: 绑定到 BlockEntity/Entity 的 serverTick() 内维护 tickCount 字段
    //   适用于延迟逻辑与具体方块/实体绑定的场景（vanilla 自身做法）
    @SubscribeEvent
    public void tick(ServerTickEvent.Post event) {
        if (!workQueue.isEmpty()) {
            List<Runnable> pending = new ArrayList<>();
            workQueue.removeIf(work -> {
                work.setValue(work.getValue() - 1);
                if (work.getValue() <= 0) {
                    pending.add(work.getKey());
                    return true;
                }
                return false;
            });
            for (Runnable action : pending) {
                action.run();
            }
        }
    }
}