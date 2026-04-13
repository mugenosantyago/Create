package com.simibubi.create.foundation.mixin.accessor;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.data.models.ItemModelGenerators;

@Mixin(ItemModelGenerators.class)
public interface ItemModelGeneratorsAccessor {
	/** Vanilla renamed {@code GENERATED_TRIM_MODELS} to {@code TRIM_MATERIAL_MODELS} in recent versions. */
	@Accessor("TRIM_MATERIAL_MODELS")
	static List<ItemModelGenerators.TrimMaterialData> create$getGENERATED_TRIM_MODELS() {
		throw new AssertionError();
	}
}
