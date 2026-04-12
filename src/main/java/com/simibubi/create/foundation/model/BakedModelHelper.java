package com.simibubi.create.foundation.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * Stub for BakedModelHelper - in MC 1.21.8, BakedModel was removed.
 */
public class BakedModelHelper {

	/**
	 * Stub: In 1.21.8, BakedModel and sprite swapping via BakedModel was removed.
	 * Returns null as models are now data-driven.
	 */
	public static Object generateModel(Object template, java.util.function.UnaryOperator<TextureAtlasSprite> spriteSwapper) {
		return template;
	}

}
