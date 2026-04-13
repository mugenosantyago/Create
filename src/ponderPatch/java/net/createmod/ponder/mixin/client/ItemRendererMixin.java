package net.createmod.ponder.mixin.client;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

import net.createmod.ponder.client.ItemRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;

/**
 * MC 1.21.8: {@link ItemRenderer} no longer has a {@code textureManager} field. Ponder still casts
 * to {@link ItemRendererAccessor}; {@link Implements} adds that interface to the target without the
 * mixin class {@code implements} a type under {@code net.createmod.ponder.mixin.*} (which Mixin forbids).
 */
@Mixin(ItemRenderer.class)
@Implements(@Interface(iface = ItemRendererAccessor.class, prefix = ""))
public class ItemRendererMixin {

	public TextureManager catnip$getTextureManager() {
		return Minecraft.getInstance().getTextureManager();
	}
}
