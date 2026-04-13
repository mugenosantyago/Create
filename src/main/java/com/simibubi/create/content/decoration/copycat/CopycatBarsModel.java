package com.simibubi.create.content.decoration.copycat;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.foundation.model.BakedQuadHelper;
import com.simibubi.create.foundation.model.BlockStateModelUtil;

import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.simibubi.create.foundation.client.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.model.data.ModelData;

public class CopycatBarsModel extends CopycatModel {

	public CopycatBarsModel(BakedModel originalModel) {
		super(originalModel);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return false;
	}

	@Override
	protected List<BakedQuad> getCroppedQuads(BlockState state, Direction side, RandomSource rand, BlockState material,
											  ModelData wrappedData, RenderType renderType) {
		BlockStateModel model = getModelOf(material);
		List<BakedQuad> superQuads = wrapped.getQuads(state, side, rand, wrappedData, renderType);
		TextureAtlasSprite targetSprite = model.particleIcon();

		boolean vertical = state.getValue(CopycatPanelBlock.FACING)
			.getAxis() == Axis.Y;

		if (side != null && (vertical || side.getAxis() == Axis.Y)) {
			List<BakedQuad> templateQuads = BlockStateModelUtil.collectQuads(model, material, null, rand);
			for (BakedQuad quad : templateQuads) {
				if (quad.direction() != Direction.UP)
					continue;
				targetSprite = quad.sprite();
				break;
			}
		}

		if (targetSprite == null)
			return superQuads;

		List<BakedQuad> quads = new ArrayList<>();

		for (BakedQuad quad : superQuads) {
			TextureAtlasSprite original = quad.sprite();
			BakedQuad newQuad = BakedQuadHelper.clone(quad);
			int[] vertexData = newQuad.vertices();
			for (int vertex = 0; vertex < 4; vertex++) {
				BakedQuadHelper.setU(vertexData, vertex, targetSprite
					.getU(SpriteShiftEntry.getUnInterpolatedU(original, BakedQuadHelper.getU(vertexData, vertex))));
				BakedQuadHelper.setV(vertexData, vertex, targetSprite
					.getV(SpriteShiftEntry.getUnInterpolatedV(original, BakedQuadHelper.getV(vertexData, vertex))));
			}
			quads.add(newQuad);
		}

		return quads;
	}

}
