package com.simibubi.create.content.processing.sequenced;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;

import io.netty.handler.codec.DecoderException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import net.neoforged.neoforge.common.crafting.CompoundIngredient;

public class SequencedRecipe<T extends ProcessingRecipe<?, ?>> {
	/**
	 * Do not use {@link Recipe#CODEC}: its dispatch is built from every registered {@link RecipeSerializer}, and some
	 * modded serializers reference client-only classes (e.g. {@code ClientLevel}) in their codec graph. Sequenced
	 * assembly only ever embeds Create's own assembly-capable recipes, so we dispatch strictly over those ids.
	 */
	public static final Codec<SequencedRecipe<?>> CODEC = Codec.lazyInitialized(() -> assemblyStepRecipeCodec()
		.comapFlatMap(recipe -> recipe instanceof ProcessingRecipe<?,?> processing && recipe instanceof IAssemblyRecipe
				? DataResult.success(new SequencedRecipe<>(processing))
				: DataResult.error(() -> recipe.getClass().getSimpleName() + " is not supported in Sequenced Assembly"),
			SequencedRecipe::getRecipe
		));

	/**
	 * Same rationale as {@link #CODEC}: avoid {@link Recipe#STREAM_CODEC}, which is tied to the full serializer
	 * registry and initializes together with {@link Recipe#CODEC}.
	 */
	public static final StreamCodec<RegistryFriendlyByteBuf, SequencedRecipe<?>> STREAM_CODEC = new StreamCodec<>() {
		private StreamCodec<RegistryFriendlyByteBuf, SequencedRecipe<?>> delegate;

		private StreamCodec<RegistryFriendlyByteBuf, SequencedRecipe<?>> delegate() {
			if (delegate == null) {
				delegate = assemblyStepRecipeStreamCodec()
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

	private static Codec<Recipe<?>> assemblyStepRecipeCodec() {
		return ResourceLocation.CODEC.partialDispatch(
			"type",
			recipe -> {
				ResourceLocation key = BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipe.getSerializer());
				return key != null
					? DataResult.success(key)
					: DataResult.error(() -> "Recipe serializer not registered: " + recipe);
			},
			SequencedRecipe::codecForAssemblyStepType
		);
	}

	private static DataResult<? extends Codec<? extends Recipe<?>>> codecForAssemblyStepType(ResourceLocation id) {
		if (id.equals(AllRecipeTypes.CUTTING.id))
			return DataResult.success(castRecipeCodec(AllRecipeTypes.CUTTING.getSerializer()));
		if (id.equals(AllRecipeTypes.PRESSING.id))
			return DataResult.success(castRecipeCodec(AllRecipeTypes.PRESSING.getSerializer()));
		if (id.equals(AllRecipeTypes.FILLING.id))
			return DataResult.success(castRecipeCodec(AllRecipeTypes.FILLING.getSerializer()));
		if (id.equals(AllRecipeTypes.DEPLOYING.id))
			return DataResult.success(castRecipeCodec(AllRecipeTypes.DEPLOYING.getSerializer()));
		return DataResult.error(() -> "Unsupported sequenced assembly step type: " + id);
	}

	@SuppressWarnings("unchecked")
	private static <R extends Recipe<?>> Codec<Recipe<?>> castRecipeCodec(RecipeSerializer<R> serializer) {
		return (Codec<Recipe<?>>) (Codec<?>) serializer.codec().codec();
	}

	private static StreamCodec<RegistryFriendlyByteBuf, Recipe<?>> assemblyStepRecipeStreamCodec() {
		return ByteBufCodecs.registry(Registries.RECIPE_SERIALIZER).dispatch(
			SequencedRecipe::assemblySerializerOf,
			SequencedRecipe::streamCodecForAssemblySerializer
		);
	}

	private static RecipeSerializer<?> assemblySerializerOf(Recipe<?> recipe) {
		return recipe.getSerializer();
	}

	@SuppressWarnings("unchecked")
	private static <T extends Recipe<?>> StreamCodec<RegistryFriendlyByteBuf, T> streamCodecForAssemblySerializer(RecipeSerializer<?> serializer) {
		if (serializer == AllRecipeTypes.CUTTING.getSerializer())
			return (StreamCodec<RegistryFriendlyByteBuf, T>) AllRecipeTypes.CUTTING.getSerializer().streamCodec();
		if (serializer == AllRecipeTypes.PRESSING.getSerializer())
			return (StreamCodec<RegistryFriendlyByteBuf, T>) AllRecipeTypes.PRESSING.getSerializer().streamCodec();
		if (serializer == AllRecipeTypes.FILLING.getSerializer())
			return (StreamCodec<RegistryFriendlyByteBuf, T>) AllRecipeTypes.FILLING.getSerializer().streamCodec();
		if (serializer == AllRecipeTypes.DEPLOYING.getSerializer())
			return (StreamCodec<RegistryFriendlyByteBuf, T>) AllRecipeTypes.DEPLOYING.getSerializer().streamCodec();
		throw new DecoderException("Unsupported sequenced assembly step serializer: " + BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer));
	}

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
