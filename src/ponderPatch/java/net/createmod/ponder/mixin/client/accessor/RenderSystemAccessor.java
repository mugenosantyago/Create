package net.createmod.ponder.mixin.client.accessor;

import java.lang.reflect.Field;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;

import com.mojang.blaze3d.systems.RenderSystem;

/**
 * MC 1.21.8: RenderSystem no longer stores shader lights as {@code Vector3f[]}.
 * Ponder still expects this helper; provide a stable fallback to keep diffuse shading.
 */
@Mixin(value = RenderSystem.class, remap = false)
public interface RenderSystemAccessor {
	Vector3f FALLBACK_LIGHT_0 = new Vector3f(0.2f, 1.0f, -0.7f).normalize();
	Vector3f FALLBACK_LIGHT_1 = new Vector3f(-0.2f, 1.0f, 0.7f).normalize();

	static Vector3f[] catnip$getShaderLightDirections() {
		Vector3f[] fromLighting = tryReadLightingDefaults();
		if (fromLighting != null) {
			return fromLighting;
		}
		return new Vector3f[] { new Vector3f(FALLBACK_LIGHT_0), new Vector3f(FALLBACK_LIGHT_1) };
	}

	private static Vector3f[] tryReadLightingDefaults() {
		try {
			Class<?> lightingClass = Class.forName("com.mojang.blaze3d.platform.Lighting");
			Field light0Field = lightingClass.getDeclaredField("DIFFUSE_LIGHT_0");
			Field light1Field = lightingClass.getDeclaredField("DIFFUSE_LIGHT_1");
			light0Field.setAccessible(true);
			light1Field.setAccessible(true);
			Object light0 = light0Field.get(null);
			Object light1 = light1Field.get(null);
			if (light0 instanceof Vector3f v0 && light1 instanceof Vector3f v1) {
				return new Vector3f[] { new Vector3f(v0), new Vector3f(v1) };
			}
		} catch (ReflectiveOperationException ignored) {
		}
		return null;
	}
}
