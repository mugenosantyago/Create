package net.createmod.ponder.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import net.createmod.ponder.mixin.client.accessor.ItemRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;

/**
 * MC 1.21.8: {@link ItemRenderer} no longer has a {@code textureManager} field. Ponder's accessor
 * must be a class mixin (not an interface mixin) because {@link ItemRenderer} is a class.
 */
@Mixin(ItemRenderer.class)
public class ItemRendererMixin implements ItemRendererAccessor {

	@Override
	public TextureManager catnip$getTextureManager() {
		return Minecraft.getInstance().getTextureManager();
	}
}
