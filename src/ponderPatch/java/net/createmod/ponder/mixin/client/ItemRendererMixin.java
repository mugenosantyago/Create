package net.createmod.ponder.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import net.createmod.ponder.client.ItemRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;

/**
 * MC 1.21.8: {@link ItemRenderer} no longer has a {@code textureManager} field. Ponder casts to
 * {@link ItemRendererAccessor}, which lives in {@code net.createmod.ponder.client} (outside
 * {@code net.createmod.ponder.mixin.*}) so the mixin class may implement it directly. Empty
 * {@code @Implements} prefix is invalid for Mixin.
 */
@Mixin(ItemRenderer.class)
public class ItemRendererMixin implements ItemRendererAccessor {

	@Override
	public TextureManager catnip$getTextureManager() {
		return Minecraft.getInstance().getTextureManager();
	}
}
