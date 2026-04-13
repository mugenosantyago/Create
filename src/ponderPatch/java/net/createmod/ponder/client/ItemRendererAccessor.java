package net.createmod.ponder.client;

import net.minecraft.client.renderer.texture.TextureManager;

/**
 * Must live outside {@code net.createmod.ponder.mixin.*} so Mixin does not forbid loading it when
 * bridging onto {@link net.minecraft.client.renderer.entity.ItemRenderer}. Ponder bytecode is
 * remapped from the old accessor FQN to this type in {@code patchPonderJar}.
 */
public interface ItemRendererAccessor {

	TextureManager catnip$getTextureManager();
}
