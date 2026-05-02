package net.createmod.catnip.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiGraphics;

/**
 * MC 1.21.8: renderBlurredBackground can only be called once per frame.
 * NavigatableSimiScreen calls renderBackground -> renderWindowBackground -> renderBackground,
 * which causes a second blur call. This mixin prevents the second blur.
 */
@Mixin(NavigatableSimiScreen.class)
public class NavigatableSimiScreenMixin {

	private boolean catnip$blurAppliedThisFrame = false;

	@Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
	private void catnip$preventDuplicateBlur(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		// If blur was already applied this frame, skip the background render to prevent second blur
		if (catnip$blurAppliedThisFrame) {
			ci.cancel();
			return;
		}
		catnip$blurAppliedThisFrame = true;
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void catnip$resetBlurFlag(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		// Reset the flag at the start of each frame
		catnip$blurAppliedThisFrame = false;
	}
}
