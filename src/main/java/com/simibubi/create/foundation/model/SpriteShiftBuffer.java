package com.simibubi.create.foundation.model;

import java.util.Arrays;
import java.util.function.UnaryOperator;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class SpriteShiftBuffer {

	private static final ThreadLocal<UnaryOperator<TextureAtlasSprite>> SPRITE_SWAPPER = ThreadLocal.withInitial(() -> sprite -> sprite);

	public static void setSpriteSwapper(UnaryOperator<TextureAtlasSprite> swapper) {
		if (swapper != null) {
			SPRITE_SWAPPER.set(swapper);
		} else {
			SPRITE_SWAPPER.remove();
		}
	}

	public static UnaryOperator<TextureAtlasSprite> getSpriteSwapper() {
		return SPRITE_SWAPPER.get();
	}

	public static void clearSpriteSwapper() {
		SPRITE_SWAPPER.remove();
	}

	public static BakedQuad swapSpriteInQuad(BakedQuad quad) {
		UnaryOperator<TextureAtlasSprite> swapper = SPRITE_SWAPPER.get();
		if (swapper == null) {
			return quad;
		}

		TextureAtlasSprite sprite = quad.sprite();
		if (sprite == null) {
			return quad;
		}

		TextureAtlasSprite newSprite = swapper.apply(sprite);
		if (newSprite == null || newSprite == sprite) {
			return quad;
		}

		int[] vertexData = Arrays.copyOf(quad.vertices(), quad.vertices().length);

		for (int vertex = 0; vertex < 4; vertex++) {
			float u = Float.intBitsToFloat(vertexData[vertex * BakedQuadHelper.VERTEX_STRIDE + BakedQuadHelper.U_OFFSET]);
			float v = Float.intBitsToFloat(vertexData[vertex * BakedQuadHelper.VERTEX_STRIDE + BakedQuadHelper.V_OFFSET]);

			float normalizedU = (u - sprite.getU0()) / (sprite.getU1() - sprite.getU0());
			float normalizedV = (v - sprite.getV0()) / (sprite.getV1() - sprite.getV0());

			float newU = normalizedU * (newSprite.getU1() - newSprite.getU0()) + newSprite.getU0();
			float newV = normalizedV * (newSprite.getV1() - newSprite.getV0()) + newSprite.getV0();

			vertexData[vertex * BakedQuadHelper.VERTEX_STRIDE + BakedQuadHelper.U_OFFSET] = Float.floatToRawIntBits(newU);
			vertexData[vertex * BakedQuadHelper.VERTEX_STRIDE + BakedQuadHelper.V_OFFSET] = Float.floatToRawIntBits(newV);
		}

		return new BakedQuad(vertexData, quad.tintIndex(), quad.direction(), newSprite, quad.shade(), 0, quad.hasAmbientOcclusion());
	}

}
