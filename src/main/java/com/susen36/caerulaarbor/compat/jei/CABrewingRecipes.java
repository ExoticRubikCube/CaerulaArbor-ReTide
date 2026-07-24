package com.susen36.caerulaarbor.compat.jei;

import com.susen36.caerulaarbor.CaerulaArborMod;

import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAPotions;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class CABrewingRecipes implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "brewing_recipes");
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
		PotionUtils.setPotion(potion, Potions.AWKWARD);
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), new ItemStack(CAItems.CANNED_CHERRY.get())));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CABlocks.TRAIL_MUSHROOM.get()));
		PotionUtils.setPotion(potion, Potions.AWKWARD);
		PotionUtils.setPotion(potion2, CAPotions.INST_SANITY.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CAItems.TRAIL_APPLE.get()));
		PotionUtils.setPotion(potion, CAPotions.INST_SANITY.get());
		PotionUtils.setPotion(potion2, CAPotions.SANITY_CURE.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(Items.GLOWSTONE_DUST));
		PotionUtils.setPotion(potion, CAPotions.INST_SANITY.get());
		PotionUtils.setPotion(potion2, CAPotions.INST_SANITY_II.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(Items.GLOWSTONE_DUST));
		PotionUtils.setPotion(potion, CAPotions.SANITY_CURE.get());
		PotionUtils.setPotion(potion2, CAPotions.SANITY_CURE_II.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CAItems.FERMENTED_OCEAN_EYE.get()));
		PotionUtils.setPotion(potion, CAPotions.SANITY_CURE.get());
		PotionUtils.setPotion(potion2, CAPotions.SANITY_IMMUE_POTION.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CAItems.CORAL_FEET.get()));
		PotionUtils.setPotion(potion, Potions.AWKWARD);
		PotionUtils.setPotion(potion2, CAPotions.FAST_SWIM_POTION.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(Items.REDSTONE));
		PotionUtils.setPotion(potion, CAPotions.FAST_SWIM_POTION.get());
		PotionUtils.setPotion(potion2, CAPotions.FAST_SWIM_POTION_LONG.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(Items.GLOWSTONE_DUST));
		PotionUtils.setPotion(potion, CAPotions.FAST_SWIM_POTION.get());
		PotionUtils.setPotion(potion2, CAPotions.FAST_SWIM_POTION_II.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CAItems.CELL_CLUSTER.get()));
		PotionUtils.setPotion(potion, CAPotions.FAST_SWIM_POTION_II.get());
		PotionUtils.setPotion(potion2, CAPotions.FAST_SWIM_POTION_III.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CAItems.TEAR_ISHARMLA.get()));
		PotionUtils.setPotion(potion, Potions.AWKWARD);
		PotionUtils.setPotion(potion2, CAPotions.PERCENTAGE_REGENERATION.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(Items.GLOWSTONE_DUST));
		PotionUtils.setPotion(potion, CAPotions.PERCENTAGE_REGENERATION.get());
		PotionUtils.setPotion(potion2, CAPotions.PERCENTAGE_REGENERATION_II.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CAItems.NERVOUS_REGENERATION.get()));
		PotionUtils.setPotion(potion, CAPotions.SANITY_IMMUE_POTION.get());
		PotionUtils.setPotion(potion2, CAPotions.LONG_SNT_IMMUE.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		registration.addRecipes(RecipeTypes.BREWING, brewingRecipes);
	}
}
