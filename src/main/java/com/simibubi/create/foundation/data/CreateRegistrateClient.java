package com.simibubi.create.foundation.data;

import java.lang.reflect.Constructor;
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
			Function<BlockStateModel, BlockStateModel> factory;
			
			if (methodName.equals("new")) {
				// Handle constructor reference
				Constructor<?> constructor = factoryClass.getConstructor(BlockStateModel.class);
				factory = model -> {
					try {
						return (BlockStateModel) constructor.newInstance(model);
					} catch (Exception e) {
						throw new RuntimeException("Failed to instantiate " + className, e);
					}
				};
			} else {
				// Handle static method reference
				Method method = factoryClass.getMethod(methodName);
				Object factoryObj = method.invoke(null);
				
				if (factoryObj == null) {
					throw new RuntimeException("Factory method returned null in " + className + "." + methodName);
				}
				
				factory = (Function<BlockStateModel, BlockStateModel>) factoryObj;
			}
			
			CreateClient.MODEL_SWAPPER.getCustomBlockModels()
				.register(RegisteredObjectsHelper.getKeyOrThrow(entry), factory);
		} catch (Exception e) {
			throw new RuntimeException("Failed to register block model for " + factorySpec, e);
		}
	}
}
