package dev.engine_room.flywheel.lib.model.baked;

import java.util.function.UnaryOperator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
	 * Helper method to swap sprite in a quad.
	 * This is called from the NeoforgeMeshEmitter mixin.
	 */
	public static BakedQuad swapSpriteInQuad(BakedQuad quad, UnaryOperator<TextureAtlasSprite> swapper) {
		TextureAtlasSprite sprite = getSpriteFromQuad(quad);
		if (sprite == null) {
			return quad;
		}
		TextureAtlasSprite newSprite = swapper.apply(sprite);
		if (newSprite == null || newSprite == sprite) {
			return quad;
		}
		return createQuadWithNewSprite(quad, newSprite);
	}
	
	private static TextureAtlasSprite getSpriteFromQuad(BakedQuad quad) {
		// In 1.21.8, BakedQuad API changed - need to access sprite differently
		// This is a placeholder - the actual implementation depends on the 1.21.8 BakedQuad API
		return null;
	}
	
	private static BakedQuad createQuadWithNewSprite(BakedQuad quad, TextureAtlasSprite newSprite) {
		// In 1.21.8, BakedQuad API changed - need to create quad differently
		// This is a placeholder - the actual implementation depends on the 1.21.8 BakedQuad API
		return quad;
	}
}
