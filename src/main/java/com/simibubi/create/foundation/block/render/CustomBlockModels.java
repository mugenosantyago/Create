package com.simibubi.create.foundation.block.render;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.BiConsumer;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class CustomBlockModels {

	private final Multimap<ResourceLocation, Function<BlockStateModel, BlockStateModel>> modelFuncs =
			MultimapBuilder.hashKeys().arrayListValues().build();
	private final Map<Block, Function<BlockStateModel, BlockStateModel>> finalModelFuncs = new IdentityHashMap<>();
	private boolean funcsLoaded = false;

	public void register(ResourceLocation block, Function<BlockStateModel, BlockStateModel> func) {
		modelFuncs.put(block, func);
	}

	public void forEach(BiConsumer<Block, Function<BlockStateModel, BlockStateModel>> consumer) {
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
			Block block = BuiltInRegistries.BLOCK.getValue(location);
			if (block == Blocks.AIR) {
				return;
			}

			Function<BlockStateModel, BlockStateModel> finalFunc = null;
			for (Function<BlockStateModel, BlockStateModel> func : funcList) {
				if (finalFunc == null) {
					finalFunc = func;
				} else {
					Function<BlockStateModel, BlockStateModel> prev = finalFunc;
					finalFunc = prev.andThen(func);
				}
			}

			finalModelFuncs.put(block, finalFunc);
		});
	}

}
