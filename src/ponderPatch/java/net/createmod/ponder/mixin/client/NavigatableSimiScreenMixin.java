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
 * This mixin prevents duplicate blur calls within the same frame.
 */
@Mixin(AbstractSimiScreen.class)
public class NavigatableSimiScreenMixin {

	// Static field to track blur state across all instances and frames
	private static long catnip$lastBlurFrame = -1;

	@Inject(method = "renderWindowBackground", at = @At("HEAD"), cancellable = true)
	private void catnip$preventDuplicateBlur(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		// Get the current frame count from Minecraft's timer
		long currentFrame = net.minecraft.client.Minecraft.getInstance().getFrameTimeNs();

		// If blur was already applied this frame, skip the window background render to prevent second blur
		if (catnip$lastBlurFrame == currentFrame) {
			ci.cancel();
			return;
		}
		catnip$lastBlurFrame = currentFrame;
	}
}
