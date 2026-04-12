package com.simibubi.create.foundation.data.recipe;

import com.tterrag.registrate.util.DataIngredient;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Compatibility shim for DataIngredient API changes in MC 1.21.8.
 * In 1.21.8, DataIngredient.tag() now requires HolderSet.Named instead of TagKey.
 */
@SuppressWarnings("all")
public class DataIngredientCompat {

    /**
     * Create a DataIngredient from a TagKey<Item>.
     * Uses the built-in item registry to resolve the tag.
     */
    public static DataIngredient tag(TagKey<Item> tagKey) {
        HolderSet.Named<Item> namedSet = BuiltInRegistries.ITEM.get(tagKey)
            .orElseGet(() -> HolderSet.emptyNamed(BuiltInRegistries.ITEM, tagKey));
        return DataIngredient.tag(namedSet);
    }
}
