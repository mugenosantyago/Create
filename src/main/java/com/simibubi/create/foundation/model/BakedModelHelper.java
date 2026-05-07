package com.simibubi.create.foundation.model;

import static net.createmod.catnip.render.SpriteShiftEntry.getUnInterpolatedU;
import static net.createmod.catnip.render.SpriteShiftEntry.getUnInterpolatedV;

import java.util.Arrays;
import java.util.function.UnaryOperator;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BakedModelHelper {

	public static int[] cropAndMove(int[] vertexData, TextureAtlasSprite sprite, AABB crop, Vec3 move) {
		vertexData = Arrays.copyOf(vertexData, vertexData.length);

		Vec3 xyz0 = BakedQuadHelper.getXYZ(vertexData, 0);
		Vec3 xyz1 = BakedQuadHelper.getXYZ(vertexData, 1);
		Vec3 xyz2 = BakedQuadHelper.getXYZ(vertexData, 2);
		Vec3 xyz3 = BakedQuadHelper.getXYZ(vertexData, 3);

		Vec3 uAxis = xyz3.add(xyz2).scale(.5);
		Vec3 vAxis = xyz1.add(xyz2).scale(.5);
		Vec3 center = xyz3.add(xyz2).add(xyz0).add(xyz1).scale(.25);

		float u0 = BakedQuadHelper.getU(vertexData, 0);
		float u3 = BakedQuadHelper.getU(vertexData, 3);
		float v0 = BakedQuadHelper.getV(vertexData, 0);
		float v1 = BakedQuadHelper.getV(vertexData, 1);

		float uScale = (float) Math
			.round((getUnInterpolatedU(sprite, u3) - getUnInterpolatedU(sprite, u0)) / xyz3.distanceTo(xyz0));
		float vScale = (float) Math
			.round((getUnInterpolatedV(sprite, v1) - getUnInterpolatedV(sprite, v0)) / xyz1.distanceTo(xyz0));

		if (uScale == 0) {
			float v3 = BakedQuadHelper.getV(vertexData, 3);
			float u1 = BakedQuadHelper.getU(vertexData, 1);
			uAxis = xyz1.add(xyz2).scale(.5);
			vAxis = xyz3.add(xyz2).scale(.5);
			uScale = (float) Math
				.round((getUnInterpolatedU(sprite, u1) - getUnInterpolatedU(sprite, u0)) / xyz1.distanceTo(xyz0));
			vScale = (float) Math
				.round((getUnInterpolatedV(sprite, v3) - getUnInterpolatedV(sprite, v0)) / xyz3.distanceTo(xyz0));

		}

		uAxis = uAxis.subtract(center).normalize();
		vAxis = vAxis.subtract(center).normalize();

		Vec3 min = new Vec3(crop.minX, crop.minY, crop.minZ);
		Vec3 max = new Vec3(crop.maxX, crop.maxY, crop.maxZ);

		for (int vertex = 0; vertex < 4; vertex++) {
			Vec3 xyz = BakedQuadHelper.getXYZ(vertexData, vertex);
			Vec3 newXyz = VecHelper.componentMin(max, VecHelper.componentMax(xyz, min));
			Vec3 diff = newXyz.subtract(xyz);

			if (diff.lengthSqr() > 0) {
				float u = BakedQuadHelper.getU(vertexData, vertex);
				float v = BakedQuadHelper.getV(vertexData, vertex);
				float uDiff = (float) uAxis.dot(diff) * uScale;
				float vDiff = (float) vAxis.dot(diff) * vScale;
				BakedQuadHelper.setU(vertexData, vertex, sprite.getU(getUnInterpolatedU(sprite, u) + uDiff));
				BakedQuadHelper.setV(vertexData, vertex, sprite.getV(getUnInterpolatedV(sprite, v) + vDiff));
			}

			BakedQuadHelper.setXYZ(vertexData, vertex, newXyz.add(move));
		}

		return vertexData;
	}

	/**
	 * MC 1.21.8: Sprite swapping is now handled via Flywheel's BakedModelBuffererMixin.
	 * This method sets the sprite swapper for the current thread before returning the template.
	 * The sprite swapper will be applied when the model is buffered by Flywheel.
	 */
	public static BlockStateModel generateModel(BlockStateModel template, UnaryOperator<TextureAtlasSprite> spriteSwapper) {
		try {
			// Use reflection to call the mixin since it's in the flywheelPatch source set
			Class<?> mixinClass = Class.forName("dev.engine_room.flywheel.lib.model.baked.BakedModelBuffererMixin");
			java.lang.reflect.Method setSpriteSwapper = mixinClass.getMethod("setSpriteSwapper", UnaryOperator.class);
			setSpriteSwapper.invoke(null, spriteSwapper);
		} catch (Exception e) {
			// Flywheel mixin not available (e.g., during data gen or when Flywheel is not loaded)
		}
		return template;
	}
}
