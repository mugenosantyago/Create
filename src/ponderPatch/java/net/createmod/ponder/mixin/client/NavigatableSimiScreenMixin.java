package net.createmod.ponder.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.GuiGraphics;

/**
 * MC 1.21.8: renderBlurredBackground can only be called once per frame.
 * AbstractSimiScreen.renderWindowBackground calls Screen.renderBackground which applies blur.
 * This mixin prevents duplicate blur calls by setting a flag when inside renderWindowBackground.
 */
@Mixin(AbstractSimiScreen.class)
public class NavigatableSimiScreenMixin {

	// ThreadLocal flag to track when we're inside renderWindowBackground
	private static final ThreadLocal<Boolean> INSIDE_RENDER_WINDOW_BG = ThreadLocal.withInitial(() -> false);

	@Inject(method = "renderWindowBackground", at = @At("HEAD"))
	private void catnip$enterRenderWindowBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		INSIDE_RENDER_WINDOW_BG.set(true);
	}

	@Inject(method = "renderWindowBackground", at = @At("RETURN"))
	private void catnip$exitRenderWindowBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		INSIDE_RENDER_WINDOW_BG.set(false);
	}

	public static boolean catnip$isInsideRenderWindowBackground() {
		return INSIDE_RENDER_WINDOW_BG.get();
	}
}
