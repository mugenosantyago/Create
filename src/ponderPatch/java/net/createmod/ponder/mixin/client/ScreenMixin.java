package net.createmod.ponder.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

/**
 * MC 1.21.8: renderBlurredBackground can only be called once per frame.
 * This mixin skips the blur in Screen.renderBackground when called from AbstractSimiScreen.renderWindowBackground.
 */
@Mixin(Screen.class)
public class ScreenMixin {

	@Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
	private void ponder$skipBlurInSimiScreen(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		// Check if we're inside AbstractSimiScreen.renderWindowBackground
		if (net.createmod.ponder.mixin.client.NavigatableSimiScreenMixin.catnip$isInsideRenderWindowBackground()) {
			// Skip the blur by cancelling the entire renderBackground call
			ci.cancel();
		}
	}
}
