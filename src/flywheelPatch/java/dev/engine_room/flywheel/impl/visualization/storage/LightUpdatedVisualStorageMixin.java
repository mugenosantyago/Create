package dev.engine_room.flywheel.impl.visualization.storage;

import net.minecraft.core.SectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MC 1.21.8: Light section coordinate calculation changed, causing ArrayIndexOutOfBoundsException
 * when adding invalid coordinates (-1) to the light update set. This mixin adds bounds checking.
 */
@Mixin(LightUpdatedVisualStorage.class)
public class LightUpdatedVisualStorageMixin {

	@Inject(method = "onLightUpdate", at = @At("HEAD"), cancellable = true, remap = false)
	private void flywheel$onLightUpdate(SectionPos pos, CallbackInfo ci) {
		if (pos == null) {
			ci.cancel();
			return;
		}
		long key = pos.asLong();
		// Add bounds check to prevent ArrayIndexOutOfBoundsException with invalid coordinates
		if (key < 0) {
			ci.cancel();
		}
	}
}
