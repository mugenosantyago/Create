package com.simibubi.create.content.decoration.copycat;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.model.BakedModelHelper;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import com.simibubi.create.foundation.model.BlockStateModelUtil;

import net.createmod.catnip.data.Iterate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;


/**
 * MC 1.21.8: Updated for BlockStateModel API.
 */
public class CopycatPanelModel extends CopycatModel {

	protected static final AABB CUBE_AABB = new AABB(BlockPos.ZERO);

	public CopycatPanelModel(BlockStateModel originalModel) {
		super(originalModel);
	}

	@Override
	protected List<BakedQuad> processQuadsForFace(List<BakedQuad> templateQuads, Direction side,
			BlockState copycatState, BlockState material) {
		Direction facing = copycatState.getOptionalValue(CopycatPanelBlock.FACING)
			.orElse(Direction.UP);

		BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

		// Handle special cases
		BlockState specialCopycatModelState = null;
		if (CopycatSpecialCases.isBarsMaterial(material))
			specialCopycatModelState = AllBlocks.COPYCAT_BARS.getDefaultState();
		if (CopycatSpecialCases.isTrapdoorMaterial(material)) {
			BlockStateModel model = blockRenderer.getBlockModel(material);
			return BlockStateModelUtil.collectQuads(model, material, side, null);
		}

		if (specialCopycatModelState != null) {
			BlockStateModel barsModel = blockRenderer.getBlockModel(
				specialCopycatModelState.setValue(DirectionalBlock.FACING, facing));
			return BlockStateModelUtil.collectQuads(barsModel, material, side, null);
		}

		int size = templateQuads.size();
		List<BakedQuad> quads = new ArrayList<>();

		Vec3 normal = Vec3.atLowerCornerOf(facing.getUnitVec3i());
		Vec3 normalScaled14 = normal.scale(14 / 16f);

		// 2 Pieces
		for (boolean front : Iterate.trueAndFalse) {
			Vec3 normalScaledN13 = normal.scale(front ? 0 : -13 / 16f);
			float contract = 16 - (front ? 1 : 2);
			AABB bb = CUBE_AABB.contract(normal.x * contract / 16, normal.y * contract / 16, normal.z * contract / 16);
			if (!front)
				bb = bb.move(normalScaled14);

			for (int i = 0; i < size; i++) {
				BakedQuad quad = templateQuads.get(i);
				Direction direction = quad.direction();

				if (front && direction == facing)
					continue;
				if (!front && direction == facing.getOpposite())
					continue;

				quads.add(BakedQuadHelper.cloneWithCustomGeometry(quad,
					BakedModelHelper.cropAndMove(quad.vertices(), quad.sprite(), bb, normalScaledN13)));
			}
		}

		return quads;
	}

}
