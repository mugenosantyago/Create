package com.simibubi.create.content.decoration.girder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.block.connected.CTModel;
import com.simibubi.create.foundation.model.BlockStateModelUtil;

import net.createmod.catnip.data.Iterate;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * MC 1.21.8: Rewritten to use BlockStateModel.collectParts() instead of BakedModel.getQuads().
 */
public class ConnectedGirderModel extends CTModel {

	public ConnectedGirderModel(BlockStateModel originalModel) {
		super(originalModel, new GirderCTBehaviour());
	}

	@Override
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random,
			List<BlockModelPart> out) {
		// Get connection data
		ConnectionData connectionData = new ConnectionData();
		for (Direction d : Iterate.horizontalDirections)
			connectionData.setConnected(d, GirderBlock.isConnected(level, pos, state, d));

		// Get base parts from parent CTModel
		List<BlockModelPart> baseParts = new ArrayList<>();
		super.collectParts(level, pos, state, random, baseParts);

		// Add connection bracket parts
		for (Direction d : Iterate.horizontalDirections) {
			if (connectionData.isConnected(d)) {
				BlockStateModel bracketModel = AllPartialModels.METAL_GIRDER_BRACKETS.get(d).get();
				bracketModel.collectParts(level, pos, state, random, baseParts);
			}
		}

		out.addAll(baseParts);
	}

	private static class ConnectionData {
		boolean[] connectedFaces;

		public ConnectionData() {
			connectedFaces = new boolean[4];
			Arrays.fill(connectedFaces, false);
		}

		void setConnected(Direction face, boolean connected) {
			connectedFaces[face.get2DDataValue()] = connected;
		}

		boolean isConnected(Direction face) {
			return connectedFaces[face.get2DDataValue()];
		}
	}

}
