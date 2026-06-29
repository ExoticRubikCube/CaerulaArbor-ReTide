/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CAPaintings {
	public static final DeferredRegister<PaintingVariant> REGISTRY = DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, CaerulaArborMod.MODID);
	public static final RegistryObject<PaintingVariant> PRESIOUS_DAYS = REGISTRY.register("presious_days", () -> new PaintingVariant(48, 32));
	public static final RegistryObject<PaintingVariant> AGE_OF_SILENCE = REGISTRY.register("age_of_silence", () -> new PaintingVariant(48, 32));
	public static final RegistryObject<PaintingVariant> PRICE_OF_PIECE = REGISTRY.register("price_of_piece", () -> new PaintingVariant(48, 32));
	public static final RegistryObject<PaintingVariant> CAERULA_STELLA = REGISTRY.register("caerula_stella", () -> new PaintingVariant(48, 32));
}
