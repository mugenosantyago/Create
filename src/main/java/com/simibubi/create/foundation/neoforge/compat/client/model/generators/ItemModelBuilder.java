package com.simibubi.create.foundation.neoforge.compat.client.model.generators;

import net.minecraft.resources.ResourceLocation;

/**
 * Compatibility stub for ItemModelBuilder.
 */
@SuppressWarnings("all")
public class ItemModelBuilder extends ModelFile {

	public ItemModelBuilder(ResourceLocation location) {
		super(location);
	}

	public ItemModelBuilder parent(ModelFile parent) {
		return this;
	}

	public ItemModelBuilder texture(String key, String texture) {
		return this;
	}

	public ItemModelBuilder texture(String key, ResourceLocation texture) {
		return this;
	}

	public ItemModelBuilder override() {
		return this;
	}

	public ItemModelBuilder end() {
		return this;
	}

}
