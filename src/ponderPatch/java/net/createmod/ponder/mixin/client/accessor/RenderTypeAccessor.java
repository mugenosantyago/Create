package net.createmod.ponder.mixin.client.accessor;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;

/**
 * MC 1.21.8: {@link RenderType}'s old private {@code create} overload was replaced by
 * {@link RenderType#create(String, int, boolean, boolean, RenderPipeline, RenderType.CompositeState)}.
 * This source replaces the JiJ Ponder accessor (see {@code patchPonderJar} in build.gradle).
 */
@Mixin(RenderType.class)
public interface RenderTypeAccessor {

	static RenderType.CompositeRenderType catnip$create(
			String name,
			VertexFormat vertexFormat,
			VertexFormat.Mode mode,
			int bufferSize,
			boolean affectsCrumbling,
			boolean sort,
			RenderType.CompositeState compositeState
	) {
		RenderPipeline base = RenderPipelines.CUTOUT_MIPPED;
		RenderPipeline pipeline = base.toBuilder()
				.withLocation(name)
				.withVertexFormat(vertexFormat, mode)
				.build();
		return RenderType.create(name, bufferSize, affectsCrumbling, sort, pipeline, compositeState);
	}
}
