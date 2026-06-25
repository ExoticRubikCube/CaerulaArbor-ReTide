package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

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
public class CaerulaArborModBrewingRecipes implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(CaerulaArborMod.MODID, "brewing_recipes");
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
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), new ItemStack(CaerulaArborModItems.CANNED_CHERRY.get())));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CaerulaArborModBlocks.TRAIL_MUSHROOM.get()));
		PotionUtils.setPotion(potion, Potions.AWKWARD);
		PotionUtils.setPotion(potion2, CaerulaArborModPotions.INST_SANITY.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CaerulaArborModItems.TRAIL_APPLE.get()));
		PotionUtils.setPotion(potion, CaerulaArborModPotions.INST_SANITY.get());
		PotionUtils.setPotion(potion2, CaerulaArborModPotions.SANITY_CURE.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(Items.GLOWSTONE_DUST));
		PotionUtils.setPotion(potion, CaerulaArborModPotions.INST_SANITY.get());
		PotionUtils.setPotion(potion2, CaerulaArborModPotions.INST_SANITY_II.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(Items.GLOWSTONE_DUST));
		PotionUtils.setPotion(potion, CaerulaArborModPotions.SANITY_CURE.get());
		PotionUtils.setPotion(potion2, CaerulaArborModPotions.SANITY_CURE_II.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CaerulaArborModItems.FERMENTED_OCEAN_EYE.get()));
		PotionUtils.setPotion(potion, CaerulaArborModPotions.SANITY_CURE.get());
		PotionUtils.setPotion(potion2, CaerulaArborModPotions.SANITY_IMMUE_POTION.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CaerulaArborModItems.CORAL_FEET.get()));
		PotionUtils.setPotion(potion, Potions.AWKWARD);
		PotionUtils.setPotion(potion2, CaerulaArborModPotions.FAST_SWIM_POTION.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(Items.REDSTONE));
		PotionUtils.setPotion(potion, CaerulaArborModPotions.FAST_SWIM_POTION.get());
		PotionUtils.setPotion(potion2, CaerulaArborModPotions.FAST_SWIM_POTION_LONG.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(Items.GLOWSTONE_DUST));
		PotionUtils.setPotion(potion, CaerulaArborModPotions.FAST_SWIM_POTION.get());
		PotionUtils.setPotion(potion2, CaerulaArborModPotions.FAST_SWIM_POTION_II.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CaerulaArborModItems.CELL_CLUSTER.get()));
		PotionUtils.setPotion(potion, CaerulaArborModPotions.FAST_SWIM_POTION_II.get());
		PotionUtils.setPotion(potion2, CaerulaArborModPotions.FAST_SWIM_POTION_III.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CaerulaArborModItems.TEAR_ISHARMLA.get()));
		PotionUtils.setPotion(potion, Potions.AWKWARD);
		PotionUtils.setPotion(potion2, CaerulaArborModPotions.PERCENTAGE_REGENERATION.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(Items.GLOWSTONE_DUST));
		PotionUtils.setPotion(potion, CaerulaArborModPotions.PERCENTAGE_REGENERATION.get());
		PotionUtils.setPotion(potion2, CaerulaArborModPotions.PERCENTAGE_REGENERATION_II.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		ingredientStack.add(new ItemStack(CaerulaArborModItems.NERVOUS_REGENERATION.get()));
		PotionUtils.setPotion(potion, CaerulaArborModPotions.SANITY_IMMUE_POTION.get());
		PotionUtils.setPotion(potion2, CaerulaArborModPotions.LONG_SNT_IMMUE.get());
		brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
		ingredientStack.clear();
		registration.addRecipes(RecipeTypes.BREWING, brewingRecipes);
	}
}
