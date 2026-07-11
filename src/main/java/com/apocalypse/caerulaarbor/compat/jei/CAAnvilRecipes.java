package com.apocalypse.caerulaarbor.compat.jei;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.init.CAItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class CAAnvilRecipes implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "anvil_recipes");
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();
		List<IJeiAnvilRecipe> anvilRecipes = new ArrayList<>();
		ItemStack rightItem;
		rightItem = new ItemStack(CAItems.RELIC_CROWN.get());
		rightItem.setCount(1);
		anvilRecipes.add(factory.createAnvilRecipe(new ItemStack(Items.IRON_HELMET), List.of(rightItem.copy()), List.of(new ItemStack(CAItems.WEARABLE_CROWN_HELMET.get()))));
		rightItem = new ItemStack(CAItems.KINGS_ARMOUR.get());
		rightItem.setCount(1);
		anvilRecipes.add(factory.createAnvilRecipe(new ItemStack(Items.IRON_CHESTPLATE), List.of(rightItem.copy()), List.of(new ItemStack(CAItems.WEARABLE_CHEST_CHESTPLATE.get()))));
		rightItem = new ItemStack(Items.COPPER_INGOT);
		rightItem.setCount(1);
		anvilRecipes.add(factory.createAnvilRecipe(new ItemStack(CAItems.SOLO_MUSIC_BOX.get()), List.of(rightItem.copy()), List.of(new ItemStack(CAItems.MUSIC_BOX_FIXED.get()))));
		rightItem = new ItemStack(CAItems.KNIGHT_CORPSE.get());
		rightItem.setCount(1);
		anvilRecipes.add(factory.createAnvilRecipe(new ItemStack(Items.IRON_SWORD), List.of(rightItem.copy()), List.of(new ItemStack(CAItems.IRON_SWORD_OF_KNIGHT_CORPUS.get()))));
		registration.addRecipes(RecipeTypes.ANVIL, anvilRecipes);
	}
}
