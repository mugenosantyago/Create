package com.simibubi.create.foundation.item.render;

import java.lang.reflect.Method;

import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import com.tterrag.registrate.util.nullness.NonNullSupplier;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Supplies {@link IClientItemExtensions} for Registrate without loading client-only classes
 * (e.g. {@link CustomRenderedItemModelRenderer} / {@code SpecialModelRenderer}) during common
 * class initialization on dedicated servers.
 */
public final class ClientItemExtensionFactories {

	private static final String SIMPLE_CUSTOM_RENDERER =
		"com.simibubi.create.foundation.item.render.SimpleCustomRenderer";
	private static final String CUSTOM_RENDERED_MODEL_RENDERER =
		"com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer";

	private ClientItemExtensionFactories() {}

	public static <T extends Item> Function<T, NonNullSupplier<Supplier<IClientItemExtensions>>> simpleCustomRenderer(
		String itemRendererClassName) {
		return item -> () -> () -> extensionsForSimpleRenderer(item, itemRendererClassName);
	}

	private static IClientItemExtensions extensionsForSimpleRenderer(Item item, String itemRendererClassName) {
		if (FMLLoader.getDist() != Dist.CLIENT) {
			return IClientItemExtensions.DEFAULT;
		}
		try {
			Class<?> rendererClass = Class.forName(itemRendererClassName);
			Object renderer = rendererClass.getDeclaredConstructor().newInstance();
			Class<?> smrClass = Class.forName(SIMPLE_CUSTOM_RENDERER);
			Class<?> crimrClass = Class.forName(CUSTOM_RENDERED_MODEL_RENDERER);
			Method create = smrClass.getMethod("create", Item.class, crimrClass);
			return (IClientItemExtensions) create.invoke(null, item, renderer);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to attach SimpleCustomRenderer for " + itemRendererClassName, e);
		}
	}

	public static NonNullSupplier<Supplier<IClientItemExtensions>> cardboardStealthOverlay() {
		return () -> () -> extensionsForCardboardOverlay();
	}

	private static IClientItemExtensions extensionsForCardboardOverlay() {
		if (FMLLoader.getDist() != Dist.CLIENT) {
			return IClientItemExtensions.DEFAULT;
		}
		try {
			Class<?> c = Class.forName("com.simibubi.create.content.equipment.armor.CardboardArmorStealthOverlay");
			return (IClientItemExtensions) c.getDeclaredConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}
