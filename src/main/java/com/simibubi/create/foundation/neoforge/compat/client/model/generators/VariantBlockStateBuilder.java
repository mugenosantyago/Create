package com.simibubi.create.foundation.neoforge.compat.client.model.generators;

/**
 * Compatibility stub for VariantBlockStateBuilder.
 */
@SuppressWarnings("all")
public class VariantBlockStateBuilder {

	private final net.minecraft.world.level.block.Block block;

	public VariantBlockStateBuilder(net.minecraft.world.level.block.Block block) {
		this.block = block;
	}

	public PartialBlockstate partialState() {
		return new PartialBlockstate();
	}

	public static class PartialBlockstate {
		public <T extends Comparable<T>> PartialBlockstate with(net.minecraft.world.level.block.state.properties.Property<T> prop, T value) {
			return this;
		}
		public PartialBlockstate setModels(ConfiguredModel... models) {
			return this;
		}
	}

}
