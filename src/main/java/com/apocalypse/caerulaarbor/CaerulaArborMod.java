package com.apocalypse.caerulaarbor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.apocalypse.caerulaarbor.network.CaerulaArborModMessages;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;

import com.apocalypse.caerulaarbor.init.CaerulaArborModVillagerProfessions;
import com.apocalypse.caerulaarbor.init.CaerulaArborModTabs;
import com.apocalypse.caerulaarbor.init.CaerulaArborModSounds;
import com.apocalypse.caerulaarbor.init.CaerulaArborModPotions;
import com.apocalypse.caerulaarbor.init.CaerulaArborModParticleTypes;
import com.apocalypse.caerulaarbor.init.CaerulaArborModPaintings;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMenus;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEnchantments;
import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.init.CaerulaArborModBlockEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;

import java.util.function.Supplier;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.AbstractMap;

@Mod(CaerulaArborMod.MODID)
public class CaerulaArborMod {
	public static final Logger LOGGER = LogManager.getLogger(CaerulaArborMod.class);
	public static final String MODID = "caerula_arbor";

	public CaerulaArborMod() {
		// Start of user code block mod constructor
		// End of user code block mod constructor
		MinecraftForge.EVENT_BUS.register(this);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		CaerulaArborModSounds.REGISTRY.register(bus);
		CaerulaArborModBlocks.REGISTRY.register(bus);
		CaerulaArborModBlockEntities.REGISTRY.register(bus);
		CaerulaArborModItems.REGISTRY.register(bus);
		CaerulaArborModEntities.REGISTRY.register(bus);
		CaerulaArborModEnchantments.REGISTRY.register(bus);
		CaerulaArborModTabs.REGISTRY.register(bus);

		CaerulaArborModMobEffects.REGISTRY.register(bus);
		CaerulaArborModPotions.REGISTRY.register(bus);
		CaerulaArborModPaintings.REGISTRY.register(bus);
		CaerulaArborModParticleTypes.REGISTRY.register(bus);
		CaerulaArborModVillagerProfessions.PROFESSIONS.register(bus);
		CaerulaArborModMenus.REGISTRY.register(bus);
		CaerulaArborModAttributes.REGISTRY.register(bus);
		bus.addListener(this::onCommonSetup);
		// Start of user code block mod init
		// End of user code block mod init
	}

	// Start of user code block mod methods
	// End of user code block mod methods
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private static int messageID = 0;

	public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
		PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
		messageID++;
	}

	private void onCommonSetup(final FMLCommonSetupEvent event) {
		CaerulaArborModMessages.register();
	}

	private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
			workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
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
