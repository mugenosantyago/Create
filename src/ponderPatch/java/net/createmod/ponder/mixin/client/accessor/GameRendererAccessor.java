package net.createmod.ponder.mixin.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

/**
 * MC 1.21.8: {@code GameRenderer#getFov} still exists but is private and returns {@code float} (was
 * {@code double} in older Yarn mappings). Ponder bytecode calls {@code catnip$callGetFov} as
 * returning double; {@code patchPonderJar} fixes {@link net.createmod.catnip.math.VecHelper}.
 */
@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

	@Invoker("getFov")
	float catnip$callGetFov(Camera camera, float partialTick, boolean useFovSetting);
}
