package com.simibubi.create.foundation.utility;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * MC 1.21.8: {@code Ingredient.EMPTY} is gone, and {@code Ingredient.of(HolderSet.empty())} throws
 * {@code UnsupportedOperationException: Ingredients can't be empty}. Use a sentinel ingredient
 * that never appears in Create recipes (same role as vanilla's former empty placeholder).
 */
public final class IngredientCompat {
	public static final Ingredient EMPTY = Ingredient.of(Items.BARRIER);

	private IngredientCompat() {
	}
}
