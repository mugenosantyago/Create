package com.simibubi.create.content.logistics.factoryBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelState;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelType;
import com.simibubi.create.foundation.model.BlockStateModelUtil;
import com.simibubi.create.foundation.model.BakedQuadHelper;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.VecHelper;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class FactoryPanelModel implements BlockStateModel {

	private final BlockStateModel wrapped;

	public FactoryPanelModel(BlockStateModel wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public void collectParts(RandomSource random, List<BlockModelPart> out) {
		wrapped.collectParts(random, out);
	}

	@Override
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random,
			List<BlockModelPart> out) {
		List<BlockModelPart> baseParts = new ArrayList<>();
		wrapped.collectParts(level, pos, state, random, baseParts);
		out.addAll(baseParts);

		boolean ponder = level instanceof PonderLevel;
		float xRot = Mth.RAD_TO_DEG * FactoryPanelBlock.getXRot(state);
		float yRot = Mth.RAD_TO_DEG * FactoryPanelBlock.getYRot(state);

		for (PanelSlot slot : PanelSlot.values()) {
			FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(level, new FactoryPanelPosition(pos, slot));
			if (behaviour == null)
				continue;

			PanelState panelState = behaviour.count == 0 ? PanelState.PASSIVE : PanelState.ACTIVE;
			PanelType type = behaviour.panelBE().restocker ? PanelType.PACKAGER : PanelType.NETWORK;

			PartialModel factoryPanel = panelState == PanelState.PASSIVE
				? type == PanelType.NETWORK ? AllPartialModels.FACTORY_PANEL : AllPartialModels.FACTORY_PANEL_RESTOCKER
				: type == PanelType.NETWORK ? AllPartialModels.FACTORY_PANEL_WITH_BULB
					: AllPartialModels.FACTORY_PANEL_RESTOCKER_WITH_BULB;

			List<BakedQuad> panelQuads = buildPanelQuads(factoryPanel.get(), state, slot, xRot, yRot, ponder, random);
			if (!panelQuads.isEmpty())
				out.add(new NullFaceBlockModelPart(panelQuads, wrapped.particleIcon()));
		}
	}

	@Override
	public TextureAtlasSprite particleIcon() {
		return wrapped.particleIcon();
	}

	private static List<BakedQuad> buildPanelQuads(BlockStateModel panelModel, BlockState state, PanelSlot slot,
			float xRot, float yRot, boolean ponder, RandomSource rand) {
		List<BakedQuad> source = BlockStateModelUtil.collectQuads(panelModel, state, null, rand);
		List<BakedQuad> result = new ArrayList<>(source.size());

		for (BakedQuad bakedQuad : source) {
			int[] vertices = bakedQuad.vertices();
			int[] tv = Arrays.copyOf(vertices, vertices.length);

			Vec3 quadNormal = Vec3.atLowerCornerOf(bakedQuad.direction().getUnitVec3i());
			quadNormal = VecHelper.rotate(quadNormal, 180, Axis.Y);
			quadNormal = VecHelper.rotate(quadNormal, xRot + 90, Axis.X);
			quadNormal = VecHelper.rotate(quadNormal, yRot, Axis.Y);

			for (int i = 0; i < vertices.length / BakedQuadHelper.VERTEX_STRIDE; i++) {
				Vec3 vertex = BakedQuadHelper.getXYZ(vertices, i);

				vertex = vertex.add(slot.xOffset * .5, 0, slot.yOffset * .5);
				vertex = VecHelper.rotateCentered(vertex, 180, Axis.Y);
				vertex = VecHelper.rotateCentered(vertex, xRot + 90, Axis.X);
				vertex = VecHelper.rotateCentered(vertex, yRot, Axis.Y);

				Vec3 normal = BakedQuadHelper.getNormalXYZ(vertices, i);
				normal = VecHelper.rotate(normal, 180, Axis.Y);
				normal = VecHelper.rotate(normal, xRot + 90, Axis.X);
				normal = VecHelper.rotate(normal, yRot, Axis.Y);

				BakedQuadHelper.setXYZ(tv, i, vertex);
				BakedQuadHelper.setNormalXYZ(tv, i, new Vec3(0, 1, 0));
			}

			Direction newNormal = Direction.getApproximateNearest(quadNormal);
			result.add(new BakedQuad(tv, bakedQuad.tintIndex(), newNormal, bakedQuad.sprite(),
				!ponder && bakedQuad.shade(), 0, bakedQuad.hasAmbientOcclusion()));
		}
		return result;
	}

	private static class NullFaceBlockModelPart implements BlockModelPart {
		private final List<BakedQuad> quads;
		private final TextureAtlasSprite particle;

		NullFaceBlockModelPart(List<BakedQuad> quads, TextureAtlasSprite particle) {
			this.quads = quads;
			this.particle = particle;
		}

		@Override
		public List<BakedQuad> getQuads(Direction side) {
			return side == null ? quads : List.of();
		}

		@Override
		public TextureAtlasSprite particleIcon() {
			return particle;
		}

		@Override
		public ChunkSectionLayer getRenderType(BlockState state) {
			return ChunkSectionLayer.SOLID;
		}

		@Override
		public boolean useAmbientOcclusion() {
			return false;
		}
	}

}
