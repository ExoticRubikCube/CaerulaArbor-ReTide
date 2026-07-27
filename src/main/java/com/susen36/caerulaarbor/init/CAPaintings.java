package com.susen36.caerulaarbor.init;

import com.susen36.caerulaarbor.CaerulaArborMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CAPaintings {
    public static final DeferredRegister<PaintingVariant> REGISTRY = DeferredRegister.create(Registries.PAINTING_VARIANT, CaerulaArborMod.MODID);

    public static final DeferredHolder<PaintingVariant, PaintingVariant> PRESIOUS_DAYS = REGISTRY.register("presious_days", () -> new PaintingVariant(48, 32, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "presious_days")));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> AGE_OF_SILENCE = REGISTRY.register("age_of_silence", () -> new PaintingVariant(48, 32, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "age_of_silence")));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> PRICE_OF_PIECE = REGISTRY.register("price_of_piece", () -> new PaintingVariant(48, 32, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "price_of_piece")));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> CAERULA_STELLA = REGISTRY.register("caerula_stella", () -> new PaintingVariant(48, 32, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "caerula_stella")));
}