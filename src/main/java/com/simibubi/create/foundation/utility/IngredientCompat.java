package com.simibubi.create.foundation.utility;

import net.minecraft.core.HolderSet;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * MC 1.21.8 removed {@code Ingredient.EMPTY}. Use an empty {@link HolderSet} ingredient instead.
 */
public final class IngredientCompat {
	public static final Ingredient EMPTY = Ingredient.of(HolderSet.empty());

	private IngredientCompat() {
	}
}
