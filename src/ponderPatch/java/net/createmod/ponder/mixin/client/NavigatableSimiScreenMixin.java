package net.createmod.ponder.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.createmod.catnip.gui.NavigatableSimiScreen;
import net.minecraft.client.gui.GuiGraphics;

/**
 * MC 1.21.8: renderBlurredBackground can only be called once per frame.
 * NavigatableSimiScreen calls renderBackground -> renderWindowBackground -> renderBackground,
 * which causes a second blur call. This mixin prevents the second blur.
 */
@Mixin(NavigatableSimiScreen.class)
public class NavigatableSimiScreenMixin {

	// Static field to track blur state across all instances and frames
	private static long catnip$lastBlurFrame = -1;

	@Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
	private void catnip$preventDuplicateBlur(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		// Get the current frame count from Minecraft's timer
		long currentFrame = net.minecraft.client.Minecraft.getInstance().getFrameTimeNs();

		// If blur was already applied this frame, skip the background render to prevent second blur
		if (catnip$lastBlurFrame == currentFrame) {
			ci.cancel();
			return;
		}
		catnip$lastBlurFrame = currentFrame;
	}
}
