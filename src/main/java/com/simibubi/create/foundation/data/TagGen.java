package com.simibubi.create.foundation.data;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.simibubi.create.foundation.data.recipe.Mods;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.core.Holder;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TagGen {
	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> axeOrPickaxe() {
		return b -> b.tag(BlockTags.MINEABLE_WITH_AXE)
			.tag(BlockTags.MINEABLE_WITH_PICKAXE);
	}

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> axeOnly() {
		return b -> b.tag(BlockTags.MINEABLE_WITH_AXE);
	}

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> pickaxeOnly() {
		return b -> b.tag(BlockTags.MINEABLE_WITH_PICKAXE);
	}

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, ItemBuilder<BlockItem, BlockBuilder<T, P>>> tagBlockAndItem(
		CommonMetal.ItemLikeTag tag) {
		return tagBlockAndItem(Map.of(tag.blocks(), tag.items()));
	}

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, ItemBuilder<BlockItem, BlockBuilder<T, P>>> tagBlockAndItem(
		TagKey<Block> blockTag, TagKey<Item> itemTag) {
		return tagBlockAndItem(Map.of(blockTag, itemTag));
	}

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, ItemBuilder<BlockItem, BlockBuilder<T, P>>> tagBlockAndItem(
		Map<TagKey<Block>, TagKey<Item>> tags) {
		return b -> {
			for (TagKey<Block> blockTag : tags.keySet()) {
				b.tag(blockTag);
			}
			ItemBuilder<BlockItem, BlockBuilder<T, P>> item = b.item();
			for (TagKey<Item> itemTag : tags.values()) {
				item.tag(itemTag);
			}
			return item;
		};
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static <T extends TagAppender<?, ?>> T addOptional(T appender, Mods mod, String id) {
		((TagAppender) appender).addOptional(mod.asResource(id));
		return appender;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static <T> CreateTagAppender<T> addOptional(CreateTagAppender<T> appender, Mods mod, String id) {
		((TagAppender) appender.delegate).addOptional(mod.asResource(id));
		return appender;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static <T> CreateTagAppender<T> addOptional(CreateTagAppender<T> appender, Mods mod, List<String> ids) {
		for (String id : ids)
			((TagAppender) appender.delegate).addOptional(mod.asResource(id));
		return appender;
	}

	public static class CreateTagsProvider<T> {
		private final RegistrateTagsProvider<T> provider;
		private final Function<T, ResourceKey<T>> keyExtractor;

		public CreateTagsProvider(RegistrateTagsProvider<T> provider, Function<T, Holder.Reference<T>> refExtractor) {
			this.provider = provider;
			this.keyExtractor = refExtractor.andThen(Holder.Reference::key);
		}

		public CreateTagAppender<T> tag(TagKey<T> tag) {
			TagBuilder tagbuilder = getOrCreateRawBuilder(tag);
			return new CreateTagAppender<>(tagbuilder, keyExtractor);
		}

		public TagBuilder getOrCreateRawBuilder(TagKey<T> tag) {
			return provider.rawBuilder(tag);
		}
	}

	public static class CreateTagAppender<T> {

		private final TagAppender<ResourceKey<T>, T> delegate;
		private final Function<T, ResourceKey<T>> keyExtractor;

		public CreateTagAppender(TagBuilder pBuilder, Function<T, ResourceKey<T>> pKeyExtractor) {
			this.delegate = TagAppender.forBuilder(pBuilder);
			this.keyExtractor = pKeyExtractor;
		}

		public CreateTagAppender<T> add(T entry) {
			delegate.add(keyExtractor.apply(entry));
			return this;
		}

		@SafeVarargs
		public final CreateTagAppender<T> add(T... entries) {
			Stream.<T>of(entries)
				.map(keyExtractor)
				.forEach(delegate::add);
			return this;
		}

		public CreateTagAppender<T> addTag(TagKey<T> tag) {
			delegate.addTag(tag);
			return this;
		}

		public CreateTagAppender<T> addOptionalTag(TagKey<T> tag) {
			delegate.addOptionalTag(tag);
			return this;
		}

	}
}
