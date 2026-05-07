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
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;


/**
 * MC 1.21.8: Updated for BlockStateModel API.
 */
public class CopycatBarsModel extends CopycatModel {

	public CopycatBarsModel(BlockStateModel originalModel) {
		super(originalModel);
	}

	@Override
	protected List<BakedQuad> processQuadsForFace(List<BakedQuad> templateQuads, Direction side,
			BlockState copycatState, BlockState material) {
		BlockStateModel model = getModelOf(material);
		TextureAtlasSprite targetSprite = model.particleIcon();

		boolean vertical = copycatState.getValue(CopycatPanelBlock.FACING)
			.getAxis() == Axis.Y;

		if (side != null && (vertical || side.getAxis() == Axis.Y)) {
			List<BakedQuad> materialQuads = BlockStateModelUtil.collectQuads(model, material, null, null);
			for (BakedQuad quad : materialQuads) {
				if (quad.direction() != Direction.UP)
					continue;
				targetSprite = quad.sprite();
				break;
			}
		}

		if (targetSprite == null)
			return templateQuads;

		List<BakedQuad> quads = new ArrayList<>();

		for (BakedQuad quad : templateQuads) {
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
