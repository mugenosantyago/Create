package com.simibubi.create.foundation.item.render;

import java.util.IdentityHashMap;
import java.util.Map;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

// BakedModel was removed in MC 1.21.8 - this class is stubbed to compile
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomItemModels {

	private final Multimap<ResourceLocation, NonNullFunction<Object, ? extends Object>> modelFuncs = MultimapBuilder.hashKeys().arrayListValues().build();
	private final Map<Item, NonNullFunction<Object, ? extends Object>> finalModelFuncs = new IdentityHashMap<>();
	private boolean funcsLoaded = false;

	public void register(ResourceLocation item, NonNullFunction<Object, ? extends Object> func) {
		modelFuncs.put(item, func);
	}

	public void forEach(NonNullBiConsumer<Item, NonNullFunction<Object, ? extends Object>> consumer) {
		loadEntriesIfMissing();
		finalModelFuncs.forEach(consumer);
	}

	private void loadEntriesIfMissing() {
		if (!funcsLoaded) {
			loadEntries();
			funcsLoaded = true;
		}
	}

	private void loadEntries() {
		finalModelFuncs.clear();
		modelFuncs.asMap().forEach((location, funcList) -> {
			Item item = BuiltInRegistries.ITEM.get(location);
			if (item == Items.AIR) {
				return;
			}

			NonNullFunction<Object, Object> finalFunc = null;
			for (NonNullFunction<Object, ? extends Object> func : funcList) {
				if (finalFunc == null) {
					finalFunc = (NonNullFunction<Object, Object>) func;
				} else {
					NonNullFunction<Object, Object> prev = finalFunc;
					NonNullFunction<Object, Object> next = (NonNullFunction<Object, Object>) func;
					finalFunc = o -> next.apply(prev.apply(o));
				}
			}

			finalModelFuncs.put(item, finalFunc);
		});
	}

}
