package com.simibubi.create.foundation.data;

import java.util.function.Function;

import com.simibubi.create.CreateClient;

import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.world.level.block.Block;

/**
 * Client-only helper for CreateRegistrate to avoid loading client classes on the server.
 */
public class CreateRegistrateClient {

	public static void registerBlockModel(Block entry, Object factory) {
		CreateClient.MODEL_SWAPPER.getCustomBlockModels()
			.register(RegisteredObjectsHelper.getKeyOrThrow(entry), (Function<BlockStateModel, BlockStateModel>) factory);
	}
}
