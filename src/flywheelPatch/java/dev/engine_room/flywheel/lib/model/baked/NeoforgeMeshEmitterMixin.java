package dev.engine_room.flywheel.lib.model.baked;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.renderer.block.model.BakedQuad;

/**
 * MC 1.21.8: Mixin to apply sprite swapping in NeoforgeMeshEmitter.
 * This swaps sprites in quads before they are emitted to the buffer.
 */
@Mixin(NeoforgeMeshEmitter.class)
public class NeoforgeMeshEmitterMixin {

	@ModifyVariable(method = "putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;FFFFII)V", at = @At("HEAD"), argsOnly = true)
	private BakedQuad flywheel$swapSpriteInQuad1(BakedQuad quad) {
		return BakedModelBuffererMixin.swapSpriteInQuad(quad);
	}

	@ModifyVariable(method = "putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;[FFFFF[IIZ)V", at = @At("HEAD"), argsOnly = true)
	private BakedQuad flywheel$swapSpriteInQuad2(BakedQuad quad) {
		return BakedModelBuffererMixin.swapSpriteInQuad(quad);
	}
}
