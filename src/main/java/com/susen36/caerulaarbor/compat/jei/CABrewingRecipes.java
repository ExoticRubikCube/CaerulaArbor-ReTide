package com.susen36.caerulaarbor.compat.jei;

import com.susen36.caerulaarbor.CaerulaArbor;

import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CACollectible;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAPotions;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class CABrewingRecipes implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "brewing_recipes");
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();
		List<IJeiBrewingRecipe> brewingRecipes = new ArrayList<>();
		ItemStack potion = new ItemStack(Items.POTION);
		ItemStack potion2 = new ItemStack(Items.POTION);
		List<ItemStack> ingredientStack = new ArrayList<>();
		List<ItemStack> inputStack = new ArrayList<>();
		ingredientStack.add(new ItemStack(Items.SWEET_BERRIES));
		potion.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.AWKWARD));
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), new ItemStack(CACollectible.CANNED_CHERRY)));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CABlocks.TRAIL_MUSHROOM.get()));
		potion.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.AWKWARD));
		potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.INST_SANITY));
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CAItems.TRAIL_APPLE.get()));
		potion.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.INST_SANITY));
		potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.SANITY_CURE));
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(Items.GLOWSTONE_DUST));
		potion.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.INST_SANITY));
		potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.INST_SANITY_II));
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(Items.GLOWSTONE_DUST));
		potion.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.SANITY_CURE));
		potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.SANITY_CURE_II));
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CAItems.FERMENTED_OCEAN_EYE.get()));
		potion.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.SANITY_CURE));
		potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.SANITY_IMMUE_POTION));
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CAItems.CORAL_FEET.get()));
		potion.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.AWKWARD));
		potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.FAST_SWIM_POTION));
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(Items.REDSTONE));
		potion.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.FAST_SWIM_POTION));
		potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.FAST_SWIM_POTION_LONG));
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(Items.GLOWSTONE_DUST));
		potion.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.FAST_SWIM_POTION));
		potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.FAST_SWIM_POTION_II));
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CAItems.CELL_CLUSTER.get()));
		potion.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.FAST_SWIM_POTION_II));
		potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.FAST_SWIM_POTION_III));
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CAItems.TEAR_ISHARMLA.get()));
		potion.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.AWKWARD));
		potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.PERCENTAGE_REGENERATION));
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(Items.GLOWSTONE_DUST));
		potion.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.PERCENTAGE_REGENERATION));
		potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.PERCENTAGE_REGENERATION_II));
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CAItems.NERVOUS_REGENERATION.get()));
		potion.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.SANITY_IMMUE_POTION));
		potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(CAPotions.LONG_SNT_IMMUE));
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		registration.addRecipes(RecipeTypes.BREWING, brewingRecipes);
	}
}