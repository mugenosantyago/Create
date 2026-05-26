package dev.engine_room.flywheel.impl.mixin;

import java.util.SortedSet;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.impl.FlwImplXplat;
import dev.engine_room.flywheel.impl.event.RenderContextImpl;
import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.entity.Entity;

/**
 * Patched for MC 1.21.8 {@link LevelRenderer#renderLevel}: frame-graph signature replaces
 * GameRenderer/LightTexture parameters. Shipped via {@code patchFlywheelJar} over upstream Flywheel JiJ.
 */
@Mixin(value = LevelRenderer.class, priority = 1001)
abstract class LevelRendererMixin {
	@Shadow
	@Nullable
	private ClientLevel level;

	@Shadow
	@Final
	private RenderBuffers renderBuffers;

	@Shadow
	@Final
	private Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress;

	@Unique
	@Nullable
	private RenderContextImpl flywheel$renderContext;

	@Unique
	private static int flywheel$debugCounter = 0;

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/lighting/LevelLightEngine;runLightUpdates()I", shift = At.Shift.AFTER), require = 0)
	private void flywheel$beginRender(GraphicsResourceAllocator graphicsResourceAllocator, DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, Matrix4f modelMatrix, Matrix4f projectionMatrix, GpuBufferSlice gpuBufferSlice, Vector4f fogRedGreenBlue, boolean isSkyDark, CallbackInfo ci) {
		flywheel$renderContext = RenderContextImpl.create((LevelRenderer) (Object) this, level, renderBuffers, modelMatrix, projectionMatrix, camera, deltaTracker.getGameTimeDeltaPartialTick(false));
		if (flywheel$debugCounter < 3) {
			System.out.println("[Flywheel-DEBUG] beginRender fired, context=" + flywheel$renderContext);
		}

		VisualizationManager manager = VisualizationManager.get(level);
		if (manager != null) {
			manager.renderDispatcher().onStartLevelRender(flywheel$renderContext);
		}
	}

	@Inject(method = "renderLevel", at = @At("RETURN"), require = 0)
	private void flywheel$endRender(GraphicsResourceAllocator graphicsResourceAllocator, DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, Matrix4f modelMatrix, Matrix4f projectionMatrix, GpuBufferSlice gpuBufferSlice, Vector4f fogRedGreenBlue, boolean isSkyDark, CallbackInfo ci) {
		flywheel$renderContext = null;
	}

	@Inject(method = "allChanged", at = @At("RETURN"))
	private void flywheel$reload(CallbackInfo ci) {
		if (level != null) {
			FlwImplXplat.INSTANCE.dispatchReloadLevelRendererEvent(level);
		}
	}

	@Inject(method = "renderBlockEntities", at = @At("HEAD"), require = 0)
	private void flywheel$beforeBlockEntities(CallbackInfo ci) {
		if (flywheel$debugCounter < 3) {
			System.out.println("[Flywheel-DEBUG] beforeBlockEntities fired, context=" + flywheel$renderContext);
			flywheel$debugCounter++;
		}
		if (flywheel$renderContext != null) {
			VisualizationManager manager = VisualizationManager.get(level);
			if (manager != null) {
				manager.renderDispatcher().afterEntities(flywheel$renderContext);
			}
		}
	}

	@Inject(method = "renderBlockDestroyAnimation", at = @At("HEAD"), require = 0)
	private void flywheel$beforeRenderCrumbling(CallbackInfo ci) {
		if (flywheel$renderContext != null) {
			VisualizationManager manager = VisualizationManager.get(level);
			if (manager != null) {
				manager.renderDispatcher().beforeCrumbling(flywheel$renderContext, destructionProgress);
			}
		}
	}

	@Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
	private void flywheel$decideNotToRenderEntity(Entity entity, double camX, double camY, double camZ, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci) {
		if (VisualizationManager.supportsVisualization(entity.level()) && VisualizationHelper.skipVanillaRender(entity)) {
			ci.cancel();
		}
	}
}
