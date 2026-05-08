package com.simibubi.create.foundation.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.simibubi.create.foundation.model.SpriteShiftBuffer;

import dev.engine_room.flywheel.lib.model.baked.NeoforgeMeshEmitter;
import net.minecraft.client.renderer.block.model.BakedQuad;

@Mixin(NeoforgeMeshEmitter.class)
public class NeoforgeMeshEmitterMixin {

	@ModifyVariable(method = "putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;FFFFII)V", at = @At("HEAD"), argsOnly = true)
	private BakedQuad create$swapSpriteInQuad1(BakedQuad quad) {
		return SpriteShiftBuffer.swapSpriteInQuad(quad);
	}

	@ModifyVariable(method = "putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;[FFFFF[IIZ)V", at = @At("HEAD"), argsOnly = true)
	private BakedQuad create$swapSpriteInQuad2(BakedQuad quad) {
		return SpriteShiftBuffer.swapSpriteInQuad(quad);
	}

}
