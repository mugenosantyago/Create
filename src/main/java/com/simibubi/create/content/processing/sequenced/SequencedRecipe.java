package com.simibubi.create.content.processing.sequenced;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;

import io.netty.handler.codec.DecoderException;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import net.neoforged.neoforge.common.crafting.CompoundIngredient;

public class SequencedRecipe<T extends ProcessingRecipe<?, ?>> {
	/**
	 * {@link Recipe#CODEC} eagerly pulls in every registered recipe serializer; some modded codecs reference
	 * client-only classes. Lazy-init avoids {@link ClassNotFoundException} for e.g. {@code ClientLevel} during
	 * dedicated-server bootstrap when {@link com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeSerializer}
	 * is registered.
	 */
	public static final Codec<SequencedRecipe<?>> CODEC = Codec.lazyInitialized(() -> Recipe.CODEC
		.comapFlatMap(recipe -> recipe instanceof ProcessingRecipe<?,?> processing && recipe instanceof IAssemblyRecipe
				? DataResult.success(new SequencedRecipe<>(processing))
				: DataResult.error(() -> recipe.getClass().getSimpleName() + " is not supported in Sequenced Assembly"),
			SequencedRecipe::getRecipe
		));

	public static final StreamCodec<RegistryFriendlyByteBuf, SequencedRecipe<?>> STREAM_CODEC = new StreamCodec<>() {
		private StreamCodec<RegistryFriendlyByteBuf, SequencedRecipe<?>> delegate;

		private StreamCodec<RegistryFriendlyByteBuf, SequencedRecipe<?>> delegate() {
			if (delegate == null) {
				delegate = Recipe.STREAM_CODEC
					.map(recipe -> {
							if (recipe instanceof ProcessingRecipe<?,?> processing && recipe instanceof IAssemblyRecipe)
								return new SequencedRecipe<>(processing);
							throw new DecoderException("Unexpected " + recipe.getClass()
								.getSimpleName() + " not supported in Sequenced Assembly");
						},
						SequencedRecipe::getRecipe
					);
			}
			return delegate;
		}

		@Override
		public SequencedRecipe<?> decode(RegistryFriendlyByteBuf buf) {
			return delegate().decode(buf);
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, SequencedRecipe<?> value) {
			delegate().encode(buf, value);
		}
	};

	private final T wrapped;

	public SequencedRecipe(T wrapped) {
		this.wrapped = wrapped;
	}

	public IAssemblyRecipe getAsAssemblyRecipe() {
		return (IAssemblyRecipe) wrapped;
	}

	public T getRecipe() {
		return wrapped;
	}

	void initFromSequencedAssembly(SequencedAssemblyRecipe parent, boolean isFirst) {
		if (getAsAssemblyRecipe().supportsAssembly()) {
			Ingredient transit = Ingredient.of(parent.getTransitionalItem().getItem());
			wrapped.placementInfo().ingredients()
					.set(0, isFirst ? CompoundIngredient.of(transit, parent.getIngredient()) : transit);
		}
	}
}
