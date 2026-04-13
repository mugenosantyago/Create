package com.simibubi.create.foundation.neoforge.compat.client.model.generators;

import net.minecraft.resources.ResourceLocation;

/**
 * Stub for the removed NeoForge model generators API (MC 1.21.8 port).
 * This class is a no-op stub to allow compilation only.
 */
@SuppressWarnings("all")
public class ModelFile {
	private final ResourceLocation location;

	public ModelFile(ResourceLocation location) {
		this.location = location;
	}

	public ResourceLocation getLocation() {
		return location;
	}

	public static class UncheckedModelFile extends ModelFile {
		public UncheckedModelFile(String path) {
			super(ResourceLocation.parse(path));
		}

		public UncheckedModelFile(ResourceLocation location) {
			super(location);
		}
	}

	public static class ExistingModelFile extends ModelFile {
		public ExistingModelFile(ResourceLocation location, Object helper) {
			super(location);
		}
	}
}
