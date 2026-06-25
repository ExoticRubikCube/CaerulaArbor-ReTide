package com.apocalypse.caerulaarbor.compat.jei;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@JeiPlugin
public class CaerulaArborModJeiInformation implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(CaerulaArborMod.MODID, "information");
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addIngredientInfo(List.of(new ItemStack(CaerulaArborModItems.OCARINA.get())), VanillaTypes.ITEM_STACK, Component.translatable("jei.caerula_arbor.descr_ocarino"));
		registration.addIngredientInfo(List.of(new ItemStack(CaerulaArborModItems.MOIST_STAR.get())), VanillaTypes.ITEM_STACK, Component.translatable("jei.caerula_arbor.desc_moist_star"));
		registration.addIngredientInfo(List.of(new ItemStack(CaerulaArborModItems.GENE_SAMPLE_NORMAL.get()), new ItemStack(CaerulaArborModItems.GENE_SAMPLE_UPGRADED.get()), new ItemStack(CaerulaArborModItems.GENE_SAMPLE_SUPERB.get())),
				VanillaTypes.ITEM_STACK, Component.translatable("jei.caerula_arbor.desc_gene_sample"));
	}
}
