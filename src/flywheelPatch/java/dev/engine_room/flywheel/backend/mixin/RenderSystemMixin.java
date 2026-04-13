package dev.engine_room.flywheel.backend.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.engine_room.flywheel.backend.gl.GlCompat;

/**
 * MC 1.21.8: {@code RenderSystem.initRenderer} signature changed from {@code (IZ)V} to
 * {@code (JIZLjava/util/function/BiFunction;Z)V}. Upstream Flywheel 1.0.8 still targets the old method;
 * this class is compiled only to replace the nested JiJ class via {@code patchFlywheelJar}.
 */
@Mixin(value = RenderSystem.class, remap = false)
abstract class RenderSystemMixin {

	@Inject(method = "initRenderer(JIZLjava/util/function/BiFunction;Z)V", at = @At("RETURN"))
	private static void flywheel$onInitRenderer(CallbackInfo ci) {
		GlCompat.init();
	}
}
