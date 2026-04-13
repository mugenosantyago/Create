package net.createmod.ponder.mixin.client.accessor;

import net.minecraft.client.renderer.texture.TextureManager;

/**
 * Cast target for Ponder code that does {@code ((ItemRendererAccessor) itemRenderer).catnip$getTextureManager()}.
 * The implementation is mixed in via {@link net.createmod.ponder.mixin.client.ItemRendererMixin}.
 */
public interface ItemRendererAccessor {

	TextureManager catnip$getTextureManager();
}
