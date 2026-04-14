package com.simibubi.create.content.processing.sequenced;

import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;

import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SequencedAssemblyRecipeSerializer implements RecipeSerializer<SequencedAssemblyRecipe> {
	/**
	 * Built lazily so {@link RecordCodecBuilder#mapCodec} does not run during {@link DeferredRegister}
	 * construction on dedicated servers (avoids loading client-only types via codec graph too early).
	 */
	private volatile MapCodec<SequencedAssemblyRecipe> codec;

	private volatile StreamCodec<RegistryFriendlyByteBuf, SequencedAssemblyRecipe> streamCodec;

	@SuppressWarnings("removal")
	private MapCodec<SequencedAssemblyRecipe> buildCodec() {
		// Do not reference Ingredient.CODEC (or ProcessingOutput.CODEC) directly here: touching
		// Ingredient.CODEC runs Ingredient's static initializer, which ends in NeoForge's
		// IngredientCodecs.codec(...) and can eagerly pull modded/custom ingredient codecs that
		// reference client-only classes. Lazy-wrap so this serializer can be built on dedicated
		// servers before those codecs are needed for actual JSON parsing.
		// SequencedAssemblyRecipe also stores its input as Object (see field javadoc) so loading
		// that class does not force Ingredient <clinit> during codec graph construction.
		// Do not declare Codec<Ingredient>: that type parameter names Ingredient in this class file.
		@SuppressWarnings("unchecked")
		Codec<Object> ingredientCodec = (Codec<Object>) (Codec<?>) Codec.lazyInitialized(
			() -> net.minecraft.world.item.crafting.Ingredient.CODEC);
		Codec<ProcessingOutput> processingOutputCodec = Codec.lazyInitialized(() -> ProcessingOutput.CODEC);
		// RecordCodecBuilder compiles `processingOutputCodec.listOf().fieldOf(...)` into a call to
		// listOf() while wiring the group — that ran at serializer registration time and defeated lazy
		// codecs. Wrap the list codec so listOf() only runs when the results field is decoded.
		Codec<List<ProcessingOutput>> resultsCodec = Codec.lazyInitialized(() -> processingOutputCodec.listOf());
		// SequencedRecipe.CODEC is already lazy; wrapping listOf() defers ListCodec construction
		// until the sequence field is decoded (same pattern as ingredient/output codecs).
		// Do not declare Codec<List<SequencedRecipe<?>>>: that names SequencedRecipe in this class.
		@SuppressWarnings("unchecked")
		Codec<List<Object>> sequenceCodec = (Codec<List<Object>>) (Codec<?>) Codec.lazyInitialized(
			() -> SequencedRecipe.CODEC.listOf());
		return RecordCodecBuilder.mapCodec(
			i -> i.group(
				ingredientCodec.fieldOf("ingredient").forGetter(r -> r.ingredient),
				processingOutputCodec.fieldOf("transitional_item").forGetter(r -> r.transitionalItem),
				sequenceCodec.fieldOf("sequence").forGetter(r -> r.sequence),
				resultsCodec.fieldOf("results").forGetter(r -> r.resultPool),
				ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("loops", 1).forGetter(SequencedAssemblyRecipe::getLoops)
			).apply(i, (ingredient, transitionalItem, sequence, results, loops) -> {
				SequencedAssemblyRecipe recipe = new SequencedAssemblyRecipe(this);
				recipe.ingredient = ingredient;
				recipe.transitionalItem = transitionalItem;
				recipe.sequence.addAll((List<?>) sequence);
				recipe.resultPool.addAll(results);
				recipe.loops = loops;
				return recipe;
			})
		);
	}

	/**
	 * Defers {@code getstatic} on stream codecs whose classes run heavy {@code <clinit>} chains on
	 * first touch (notably {@link Ingredient#CONTENTS_STREAM_CODEC} → {@link Ingredient#CODEC} via
	 * NeoForge's {@code IngredientCodecs}, and {@link ProcessingOutput}'s static codecs). Without
	 * this, {@link #streamCodec()} can initialize those classes during serializer registration before
	 * recipe JSON is parsed — the same classloading order that produced {@code ClientLevel} errors
	 * on dedicated servers when a mod registers a client-only ingredient codec.
	 */
	private static <B extends RegistryFriendlyByteBuf, V> StreamCodec<B, V> lazyStreamCodec(Supplier<StreamCodec<B, V>> delegate) {
		return new StreamCodec<>() {
			private volatile StreamCodec<B, V> resolved;

			private StreamCodec<B, V> resolve() {
				StreamCodec<B, V> d = resolved;
				if (d == null) {
					synchronized (this) {
						if (resolved == null)
							resolved = d = delegate.get();
					}
				}
				return d;
			}

			@Override
			public V decode(B buf) {
				return resolve().decode(buf);
			}

			@Override
			public void encode(B buf, V value) {
				resolve().encode(buf, value);
			}
		};
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private StreamCodec<RegistryFriendlyByteBuf, SequencedAssemblyRecipe> buildStreamCodec() {
		// Raw Function / Function5: any typed getter (Ingredient, List<SequencedRecipe<?>>, …) in this class
		// file links those classes when the stream codec is built, defeating lazyIngredient codecs on servers.
		java.util.function.Function id = o -> ((SequencedAssemblyRecipe) o).ingredient;
		java.util.function.Function seq = o -> ((SequencedAssemblyRecipe) o).sequence;
		java.util.function.Function pool = o -> ((SequencedAssemblyRecipe) o).resultPool;
		java.util.function.Function trans = o -> ((SequencedAssemblyRecipe) o).transitionalItem;
		java.util.function.Function loopsGet = o -> ((SequencedAssemblyRecipe) o).loops;
		return (StreamCodec<RegistryFriendlyByteBuf, SequencedAssemblyRecipe>) (StreamCodec) StreamCodec.composite(
			lazyStreamCodec(() -> net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC),
			id,
			lazyStreamCodec(() -> CatnipStreamCodecBuilders.list(SequencedRecipe.STREAM_CODEC)),
			seq,
			lazyStreamCodec(() -> CatnipStreamCodecBuilders.list(ProcessingOutput.STREAM_CODEC)),
			pool,
			lazyStreamCodec(() -> ProcessingOutput.STREAM_CODEC),
			trans,
			ByteBufCodecs.VAR_INT,
			loopsGet,
			(com.mojang.datafixers.util.Function5) (ingredient, sequence, resultPool, transitionalItem, loops) -> {
				SequencedAssemblyRecipe recipe = new SequencedAssemblyRecipe(this);
				recipe.ingredient = ingredient;
				recipe.sequence.addAll((List) sequence);
				recipe.resultPool.addAll((List) resultPool);
				recipe.transitionalItem = (ProcessingOutput) transitionalItem;
				recipe.loops = (Integer) loops;
				return recipe;
			}
		);
	}

	@Override
	public @NotNull MapCodec<SequencedAssemblyRecipe> codec() {
		MapCodec<SequencedAssemblyRecipe> c = codec;
		if (c == null) {
			synchronized (this) {
				if (codec == null)
					codec = c = buildCodec();
			}
		}
		return c;
	}

	@Override
	public @NotNull StreamCodec<RegistryFriendlyByteBuf, SequencedAssemblyRecipe> streamCodec() {
		StreamCodec<RegistryFriendlyByteBuf, SequencedAssemblyRecipe> s = streamCodec;
		if (s == null) {
			synchronized (this) {
				if (streamCodec == null)
					streamCodec = s = buildStreamCodec();
			}
		}
		return s;
	}
}
