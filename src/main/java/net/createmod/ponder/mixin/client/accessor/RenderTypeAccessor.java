package net.createmod.ponder.mixin.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderType;

/**
 * Correct {@link Mixin} invoker for {@link RenderType}'s private factory. The jar-in-jar
 * Ponder build published for MC 1.21.8 accidentally shipped this class without the
 * {@code @Mixin} annotation, which makes Mixin fail during prepare. Shipping the proper
 * interface here ensures it is loaded from Create's main jar before the nested copy.
 */
@Mixin(RenderType.class)
public interface RenderTypeAccessor {
	@Invoker("create")
	static RenderType.CompositeRenderType catnip$create(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, RenderType.CompositeState compositeState) {
		throw new AssertionError("Mixin application failed!");
	}
}
