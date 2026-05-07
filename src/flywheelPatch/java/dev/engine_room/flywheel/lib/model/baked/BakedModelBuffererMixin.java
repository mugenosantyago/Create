package dev.engine_room.flywheel.lib.model.baked;

import java.util.Arrays;
import java.util.function.UnaryOperator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import dev.engine_room.flywheel.lib.model.SimpleModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * MC 1.21.8: Mixin to support sprite swapping in Flywheel's BakedModelBufferer.
 * This allows Create to swap textures in models (e.g., water wheel wood types).
 */
@Mixin(BakedModelBufferer.class)
public class BakedModelBuffererMixin {
	
	// ThreadLocal storage for sprite swapping function
	private static final ThreadLocal<UnaryOperator<TextureAtlasSprite>> SPRITE_SWAPPER = ThreadLocal.withInitial(() -> sprite -> sprite);
	
	/**
	 * Sets the sprite swapper for the current thread.
	 * Call this before buffering a model to swap its sprites.
	 */
	public static void setSpriteSwapper(UnaryOperator<TextureAtlasSprite> swapper) {
		if (swapper != null) {
			SPRITE_SWAPPER.set(swapper);
		} else {
			SPRITE_SWAPPER.remove();
		}
	}
	
	/**
	 * Gets the current sprite swapper for the current thread.
	 */
	public static UnaryOperator<TextureAtlasSprite> getSpriteSwapper() {
		return SPRITE_SWAPPER.get();
	}
	
	/**
	 * Clears the sprite swapper for the current thread.
	 */
	public static void clearSpriteSwapper() {
		SPRITE_SWAPPER.remove();
	}
	
	/**
	 * Inject into bufferModel to clear the sprite swapper after buffering.
	 */
	@Inject(method = "bufferModel", at = @At("RETURN"))
	private static void onClearSpriteSwapperScope(CallbackInfoReturnable<SimpleModel> cir) {
		SPRITE_SWAPPER.remove();
	}
	
	/**
	 * Helper method to swap sprite in a quad by modifying UV coordinates.
	 * This is called from the NeoforgeMeshEmitter mixin.
	 */
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
		
		// Clone the quad with the new sprite
		int[] vertexData = Arrays.copyOf(quad.vertices(), quad.vertices().length);
		
		// Vertex stride for BLOCK format
		int vertexStride = DefaultVertexFormat.BLOCK.getVertexSize() / 4;
		int uOffset = 4;
		int vOffset = 5;
		
		// Modify UV coordinates to match the new sprite
		for (int vertex = 0; vertex < 4; vertex++) {
			float u = Float.intBitsToFloat(vertexData[vertex * vertexStride + uOffset]);
			float v = Float.intBitsToFloat(vertexData[vertex * vertexStride + vOffset]);
			
			// Convert UV from old sprite to normalized (0-1)
			float normalizedU = (u - sprite.getU0()) / (sprite.getU1() - sprite.getU0());
			float normalizedV = (v - sprite.getV0()) / (sprite.getV1() - sprite.getV0());
			
			// Convert normalized UV to new sprite coordinates
			float newU = normalizedU * (newSprite.getU1() - newSprite.getU0()) + newSprite.getU0();
			float newV = normalizedV * (newSprite.getV1() - newSprite.getV0()) + newSprite.getV0();
			
			vertexData[vertex * vertexStride + uOffset] = Float.floatToRawIntBits(newU);
			vertexData[vertex * vertexStride + vOffset] = Float.floatToRawIntBits(newV);
		}
		
		// Create new quad with modified vertex data and new sprite
		return new BakedQuad(vertexData, quad.tintIndex(), quad.direction(), newSprite, quad.shade(), 0, quad.hasAmbientOcclusion());
	}
}
