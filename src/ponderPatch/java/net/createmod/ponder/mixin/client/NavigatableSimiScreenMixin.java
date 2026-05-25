package net.createmod.ponder.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.createmod.catnip.gui.NavigatableSimiScreen;
import net.createmod.ponder.client.BlurFlag;
import net.minecraft.client.gui.GuiGraphics;

/**
 * MC 1.21.8: renderBlurredBackground can only be called once per frame.
 * NavigatableSimiScreen.renderWindowBackground calls renderBackground which applies blur.
 * This mixin prevents duplicate blur calls by setting a flag when inside renderWindowBackground.
 * NOTE: Must target NavigatableSimiScreen (not AbstractSimiScreen) because NavigatableSimiScreen
 * overrides renderWindowBackground without calling super, so AbstractSimiScreen injections never fire.
 */
@Mixin(NavigatableSimiScreen.class)
public class NavigatableSimiScreenMixin {

	@Inject(method = "renderWindowBackground", at = @At("HEAD"))
	private void catnip$enterRenderWindowBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		BlurFlag.setInsideRenderWindowBackground(true);
	}

	@Inject(method = "renderWindowBackground", at = @At("RETURN"))
	private void catnip$exitRenderWindowBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		BlurFlag.setInsideRenderWindowBackground(false);
	}
}
