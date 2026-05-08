package com.simibubi.create.foundation.data;

import java.lang.reflect.Method;
import java.util.function.Function;

import com.simibubi.create.CreateClient;

import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.world.level.block.Block;

/**
 * Client-only helper for CreateRegistrate to avoid loading client classes on the server.
 */
public class CreateRegistrateClient {

	public static void registerBlockModel(Block entry, String factorySpec) {
		try {
			String[] parts = factorySpec.split("#");
			String className = parts[0];
			String methodName = parts.length > 1 ? parts[1] : "new";
			
			Class<?> factoryClass = Class.forName(className);
			Method method = factoryClass.getMethod(methodName);
			Object factory = method.invoke(null);
			
			if (factory == null) {
				throw new RuntimeException("Factory method returned null in " + className + "." + methodName);
			}
			
			CreateClient.MODEL_SWAPPER.getCustomBlockModels()
				.register(RegisteredObjectsHelper.getKeyOrThrow(entry), (Function<BlockStateModel, BlockStateModel>) factory);
		} catch (Exception e) {
			throw new RuntimeException("Failed to register block model for " + factorySpec, e);
		}
	}
}
