package com.susen36.caerulaarbor.init;

import com.google.common.collect.ImmutableSet;
import com.susen36.caerulaarbor.CaerulaArborMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

@EventBusSubscriber
public class CAVillagerProfessions {
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, CaerulaArborMod.MODID);
    public static final ResourceKey<PoiType> CANNOT_GOODENOUGH_POI = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "cannot_goodenough"));
    private static final Map<String, ProfessionPoiType> POI_TYPES = new HashMap<>();
    public static final DeferredHolder<VillagerProfession, ? extends VillagerProfession> CANNOT_GOODENOUGH = registerProfession("cannot_goodenough", CABlocks.BLOCK_RECORDER,
            () -> SoundEvents.VILLAGER_WORK_CLERIC);

    private static DeferredHolder<VillagerProfession, ? extends VillagerProfession> registerProfession(String name, Supplier<Block> block, Supplier<SoundEvent> soundEvent) {
        POI_TYPES.put(name, new ProfessionPoiType(block, null));
        return PROFESSIONS.register(name, () -> {
            Predicate<Holder<PoiType>> poiPredicate = poiTypeHolder -> (POI_TYPES.get(name).poiType != null) && (poiTypeHolder.get() == POI_TYPES.get(name).poiType.get());
            return new VillagerProfession(CaerulaArborMod.MODID + ":" + name, poiPredicate, poiPredicate, ImmutableSet.of(), ImmutableSet.of(), soundEvent.get());
        });
    }

    @SubscribeEvent
    public static void registerProfessionPointsOfInterest(RegisterEvent event) {
        event.register(Registries.POINT_OF_INTEREST_TYPE, registerHelper -> {
            for (Map.Entry<String, ProfessionPoiType> entry : POI_TYPES.entrySet()) {
                Block block = entry.getValue().block.get();
                String name = entry.getKey();
                Optional<Holder<PoiType>> existingCheck = PoiTypes.forState(block.defaultBlockState());
                if (existingCheck.isPresent()) {
                    CaerulaArborMod.LOGGER.error("Skipping villager profession " + name + " that uses POI block " + block + " that is already in use by " + existingCheck);
                    continue;
                }
                PoiType poiType = new PoiType(ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates()), 1, 1);
                registerHelper.register(name, poiType);
                ResourceKey<PoiType> poiKey = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, name));
                entry.getValue().poiType = BuiltInRegistries.POINT_OF_INTEREST_TYPE.getHolder(poiKey).get();
            }
        });
    }

    private static class ProfessionPoiType {
        final Supplier<Block> block;
        Holder<PoiType> poiType;

        ProfessionPoiType(Supplier<Block> block, Holder<PoiType> poiType) {
            this.block = block;
            this.poiType = poiType;
        }
    }
}
