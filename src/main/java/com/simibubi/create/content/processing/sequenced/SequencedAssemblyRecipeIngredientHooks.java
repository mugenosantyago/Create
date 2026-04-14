package com.simibubi.create.content.processing.sequenced;

import com.simibubi.create.AllDataComponents;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;

/**
 * Ingredient / placement wiring lives here so {@link SequencedAssemblyRecipe} contains no
 * {@link Ingredient} type references in its bytecode. Otherwise loading the recipe class during
 * {@link SequencedAssemblyRecipeSerializer#buildCodec()} (RecordCodecBuilder setup) can resolve
 * {@code Ingredient} during verification and run NeoForge's {@code IngredientCodecs} far too early
 * on dedicated servers.
 */
final class SequencedAssemblyRecipeIngredientHooks {

	private SequencedAssemblyRecipeIngredientHooks() {
	}

	static boolean appliesTo(SequencedAssemblyRecipe recipe, ResourceLocation id, ItemStack input) {
		if (input.has(AllDataComponents.SEQUENCED_ASSEMBLY)) {
			return recipe.getTransitionalItem().getItem() == input.getItem() && input
				.get(AllDataComponents.SEQUENCED_ASSEMBLY)
				.id().equals(id);
		}
		return ((Ingredient) recipe.ingredient).test(input);
	}

	static PlacementInfo placementInfo(Object ingredient) {
		return PlacementInfo.create((Ingredient) ingredient);
	}
}
