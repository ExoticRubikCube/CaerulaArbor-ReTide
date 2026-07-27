package com.susen36.caerulaarbor.init;

import com.google.common.collect.ImmutableSet;
import com.susen36.caerulaarbor.CaerulaArborMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class CAVillagerProfessions {
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(Registries.VILLAGER_PROFESSION, CaerulaArborMod.MODID);
    public static final DeferredRegister<PoiType> POIS = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, CaerulaArborMod.MODID);
    public static final ResourceKey<PoiType> CANNOT_GOODENOUGH_POI = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "cannot_goodenough"));
    public static final DeferredHolder<PoiType, PoiType> CANNOT_GOODENOUGH_POI_HOLDER = registerPoi("cannot_goodenough", CABlocks.BLOCK_RECORDER);
    public static final DeferredHolder<VillagerProfession, VillagerProfession> CANNOT_GOODENOUGH = registerProfession("cannot_goodenough", CANNOT_GOODENOUGH_POI_HOLDER,
            () -> SoundEvents.VILLAGER_WORK_CLERIC);

    private static DeferredHolder<PoiType, PoiType> registerPoi(String name, Supplier<Block> block) {
        return POIS.register(name, () -> new PoiType(ImmutableSet.copyOf(block.get().getStateDefinition().getPossibleStates()), 1, 1));
    }

    private static DeferredHolder<VillagerProfession, VillagerProfession> registerProfession(String name, DeferredHolder<PoiType, PoiType> poiHolder, Supplier<SoundEvent> soundEvent) {
        return PROFESSIONS.register(name, () -> {
            Predicate<Holder<PoiType>> poiPredicate = poiTypeHolder -> poiTypeHolder.value() == poiHolder.value();
            return new VillagerProfession(CaerulaArborMod.MODID + ":" + name, poiPredicate, poiPredicate, ImmutableSet.of(), ImmutableSet.of(), soundEvent.get());
        });
    }
}