package com.tterrag.registrate.providers;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.world.level.ItemLike;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;

/**
 * Stub for old RegistrateRecipeProvider location (MC 1.21.8 port).
 * Class moved to com.tterrag.registrate.providers.generators.RegistrateRecipeProvider
 */
@SuppressWarnings("all")
public class RegistrateRecipeProvider {
    public static Criterion<?> has(ItemLike item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(item);
    }
}
