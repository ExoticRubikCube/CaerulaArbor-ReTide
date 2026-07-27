package com.susen36.caerulaarbor.compat.jei;

import com.susen36.caerulaarbor.CaerulaArborMod;

import com.susen36.caerulaarbor.init.CAItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@JeiPlugin
public class CAJeiInformation implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "information");
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addIngredientInfo(List.of(new ItemStack(CAItems.OCARINA.get())), VanillaTypes.ITEM_STACK, Component.translatable("jei.caerula_arbor.descr_ocarino"));
		registration.addIngredientInfo(List.of(new ItemStack(CAItems.MOIST_STAR.get())), VanillaTypes.ITEM_STACK, Component.translatable("jei.caerula_arbor.desc_moist_star"));
		registration.addIngredientInfo(List.of(new ItemStack(CAItems.GENE_SAMPLE_NORMAL.get()), new ItemStack(CAItems.GENE_SAMPLE_UPGRADED.get()), new ItemStack(CAItems.GENE_SAMPLE_SUPERB.get())),
				VanillaTypes.ITEM_STACK, Component.translatable("jei.caerula_arbor.desc_gene_sample"));
	}
}