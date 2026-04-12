package com.simibubi.create.foundation.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * Compatibility utilities for MC 1.21.8 API changes.
 */
@SuppressWarnings("all")
public class RecipeCompat {

    /**
     * Get the RecipeManager from a Level.
     * In MC 1.21.8, Level.getRecipeManager() was removed.
     */
    public static RecipeManager getRecipeManager(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getServer().getRecipeManager();
        }
        return null;
    }

    /**
     * Get the RecipeManager from a ClientPacketListener (client-side).
     * In MC 1.21.8, this no longer exists - returns null.
     */
    public static RecipeManager getRecipeManagerFromConnection(ClientPacketListener connection) {
        // In MC 1.21.8, the client no longer has access to a RecipeManager via connection
        // Recipe lookup on client side needs to be done differently
        return null;
    }

	/**
	 * Lists recipes of a type via {@link net.minecraft.world.item.crafting.RecipeMap#byType} (replacement for removed RecipeManager bulk lookup).
	 */
	public static <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> getAllRecipesFor(RecipeManager manager,
		RecipeType<T> type) {
		if (manager == null)
			return Collections.emptyList();
		return new ArrayList<>(manager.recipeMap()
			.byType(type));
	}
}
