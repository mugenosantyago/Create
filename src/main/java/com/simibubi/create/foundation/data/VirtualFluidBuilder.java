package com.simibubi.create.foundation.data;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Properties;

/**
 * For registering fluids with no buckets/blocks
 */
public class VirtualFluidBuilder<T extends BaseFlowingFluid, P> extends FluidBuilder<T, P> {

	public VirtualFluidBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback,
		ResourceLocation stillTexture, ResourceLocation flowingTexture, FluidBuilder.FluidTypeFactory typeFactory,
		NonNullFunction<Properties, T> sourceFactory,
	    NonNullFunction<Properties, T> flowingFactory
   ) {
		// In Registrate 1.21.8, FluidBuilder takes FluidTypeFactory and FluidFactory<T>
		// NonNullFunction is functionally compatible with FluidFactory via method reference
		super(owner, parent, name, callback, typeFactory, props -> flowingFactory.apply(props));
		source(sourceFactory);
	}

	@Override
	public NonNullSupplier<T> asSupplier() {
		return this::getEntry;
	}

}
