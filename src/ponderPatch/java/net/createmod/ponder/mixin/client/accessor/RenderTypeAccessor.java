package net.createmod.ponder.mixin.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderType;

/**
 * Correct {@link Mixin} invoker for {@link RenderType}'s private factory. The JiJ Ponder build
 * for MC 1.21.8 accidentally shipped this class without {@code @Mixin}, which makes Mixin fail
 * during prepare. This source is compiled only to replace that class inside the Ponder jar
 * (see {@code patchPonderJar} in build.gradle); it must not live in Create's main sources or
 * JPMS sees two modules exporting the same package.
 */
@Mixin(RenderType.class)
public interface RenderTypeAccessor {
	@Invoker("create")
	static RenderType.CompositeRenderType catnip$create(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, RenderType.CompositeState compositeState) {
		throw new AssertionError("Mixin application failed!");
	}
}
