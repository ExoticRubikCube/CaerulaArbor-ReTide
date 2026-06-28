package com.apocalypse.caerulaarbor.recipe;

import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.init.CaerulaArborModPotions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BrewingRecipeHandler {
	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			// Fast Swim
			addPotionRecipe(Potions.AWKWARD, CaerulaArborModItems.CORAL_FEET.get(), CaerulaArborModPotions.FAST_SWIM_POTION.get());
			addPotionRecipe(CaerulaArborModPotions.FAST_SWIM_POTION.get(), Items.REDSTONE, CaerulaArborModPotions.FAST_SWIM_POTION_LONG.get());
			addPotionRecipe(CaerulaArborModPotions.FAST_SWIM_POTION.get(), Items.GLOWSTONE_DUST, CaerulaArborModPotions.FAST_SWIM_POTION_II.get());
			addPotionRecipe(CaerulaArborModPotions.FAST_SWIM_POTION_II.get(), CaerulaArborModItems.CELL_CLUSTER.get(), CaerulaArborModPotions.FAST_SWIM_POTION_III.get());

			// Instant Sanity
			addPotionRecipe(Potions.AWKWARD, CaerulaArborModBlocks.TRAIL_MUSHROOM.get(), CaerulaArborModPotions.INST_SANITY.get());
			addPotionRecipe(CaerulaArborModPotions.INST_SANITY.get(), Items.GLOWSTONE_DUST, CaerulaArborModPotions.INST_SANITY_II.get());

			// Sanity Cure
			addPotionRecipe(CaerulaArborModPotions.INST_SANITY.get(), CaerulaArborModItems.TRAIL_APPLE.get(), CaerulaArborModPotions.SANITY_CURE.get());
			addPotionRecipe(CaerulaArborModPotions.SANITY_CURE.get(), Items.GLOWSTONE_DUST, CaerulaArborModPotions.SANITY_CURE_II.get());

			// Sanity Immunity
			addPotionRecipe(CaerulaArborModPotions.SANITY_CURE.get(), CaerulaArborModItems.FERMENTED_OCEAN_EYE.get(), CaerulaArborModPotions.SANITY_IMMUE_POTION.get());
			addPotionRecipe(CaerulaArborModPotions.SANITY_IMMUE_POTION.get(), CaerulaArborModItems.NERVOUS_REGENERATION.get(), CaerulaArborModPotions.LONG_SNT_IMMUE.get());

			// Percentage Regeneration
			addPotionRecipe(Potions.AWKWARD, CaerulaArborModItems.TEAR_ISHARMLA.get(), CaerulaArborModPotions.PERCENTAGE_REGENERATION.get());
			addPotionRecipe(CaerulaArborModPotions.PERCENTAGE_REGENERATION.get(), Items.GLOWSTONE_DUST, CaerulaArborModPotions.PERCENTAGE_REGENERATION_II.get());

			// Make Berry (Item output)
			addItemRecipe(Potions.AWKWARD, Items.SWEET_BERRIES, CaerulaArborModItems.CANNED_CHERRY.get());
		});
	}

	private static void addPotionRecipe(Potion input, ItemLike ingredient, Potion output) {
		BrewingRecipeRegistry.addRecipe(new CustomPotionBrewingRecipe(input, ingredient, output));
	}

	private static void addItemRecipe(Potion input, ItemLike ingredient, ItemLike output) {
		BrewingRecipeRegistry.addRecipe(new CustomItemBrewingRecipe(input, ingredient, output));
	}

	private static class CustomPotionBrewingRecipe implements IBrewingRecipe {
		private final Potion inputPotion;
		private final ItemLike ingredient;
		private final Potion outputPotion;

		public CustomPotionBrewingRecipe(Potion inputPotion, ItemLike ingredient, Potion outputPotion) {
			this.inputPotion = inputPotion;
			this.ingredient = ingredient;
			this.outputPotion = outputPotion;
		}

		@Override
		public boolean isInput(ItemStack input) {
			Item inputItem = input.getItem();
			return (inputItem == Items.POTION || inputItem == Items.SPLASH_POTION || inputItem == Items.LINGERING_POTION) && PotionUtils.getPotion(input) == inputPotion;
		}

		@Override
		public boolean isIngredient(ItemStack ingredientStack) {
			return Ingredient.of(new ItemStack(ingredient)).test(ingredientStack);
		}

		@Override
		public ItemStack getOutput(ItemStack input, ItemStack ingredientStack) {
			if (isInput(input) && isIngredient(ingredientStack)) {
				return PotionUtils.setPotion(new ItemStack(input.getItem()), outputPotion);
			}
			return ItemStack.EMPTY;
		}
	}

	private static class CustomItemBrewingRecipe implements IBrewingRecipe {
		private final Potion inputPotion;
		private final ItemLike ingredient;
		private final ItemLike outputItem;

		public CustomItemBrewingRecipe(Potion inputPotion, ItemLike ingredient, ItemLike outputItem) {
			this.inputPotion = inputPotion;
			this.ingredient = ingredient;
			this.outputItem = outputItem;
		}

		@Override
		public boolean isInput(ItemStack input) {
			Item inputItem = input.getItem();
			return (inputItem == Items.POTION || inputItem == Items.SPLASH_POTION || inputItem == Items.LINGERING_POTION) && PotionUtils.getPotion(input) == inputPotion;
		}

		@Override
		public boolean isIngredient(ItemStack ingredientStack) {
			return Ingredient.of(new ItemStack(ingredient)).test(ingredientStack);
		}

		@Override
		public ItemStack getOutput(ItemStack input, ItemStack ingredientStack) {
			if (isInput(input) && isIngredient(ingredientStack)) {
				return new ItemStack(outputItem);
			}
			return ItemStack.EMPTY;
		}
	}
}
