package com.simibubi.create.foundation.utility;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeManager;
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
}
