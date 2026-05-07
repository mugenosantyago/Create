package com.simibubi.create.content.fluids;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.fluids.FluidTransportBehaviour.AttachmentTypes;
import com.simibubi.create.content.fluids.FluidTransportBehaviour.AttachmentTypes.ComponentPartials;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.data.Iterate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class PipeAttachmentModel implements BlockStateModel {

	private final BlockStateModel wrapped;
	private final boolean ao;

	public static PipeAttachmentModel withAO(BlockStateModel template) {
		return new PipeAttachmentModel(template, true);
	}

	public static PipeAttachmentModel withoutAO(BlockStateModel template) {
		return new PipeAttachmentModel(template, false);
	}

	public PipeAttachmentModel(BlockStateModel wrapped, boolean ao) {
		this.wrapped = wrapped;
		this.ao = ao;
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

		if (!ao) {
			for (BlockModelPart part : baseParts)
				out.add(new AOOverrideBlockModelPart(part, false));
		} else {
			out.addAll(baseParts);
		}

		FluidTransportBehaviour transport = BlockEntityBehaviour.get(level, pos, FluidTransportBehaviour.TYPE);
		BracketedBlockEntityBehaviour bracket = BlockEntityBehaviour.get(level, pos, BracketedBlockEntityBehaviour.TYPE);

		if (bracket != null) {
			BlockState bracketState = bracket.getBracket();
			if (bracketState != null) {
				BlockStateModel bracketModel =
					Minecraft.getInstance().getBlockRenderer().getBlockModel(bracketState);
				bracketModel.collectParts(level, pos, bracketState, random, out);
			}
		}

		if (transport != null) {
			for (Direction d : Iterate.directions) {
				AttachmentTypes type = transport.getRenderedRimAttachment(level, pos, state, d);
				for (ComponentPartials partial : type.partials) {
					BlockStateModel attachmentModel = AllPartialModels.PIPE_ATTACHMENTS.get(partial).get(d).get();
					attachmentModel.collectParts(random, out);
				}
			}
		}

		if (FluidPipeBlock.shouldDrawCasing(level, pos, state))
			AllPartialModels.FLUID_PIPE_CASING.get().collectParts(random, out);
	}

	@Override
	public TextureAtlasSprite particleIcon() {
		return wrapped.particleIcon();
	}

	private static class AOOverrideBlockModelPart implements BlockModelPart {
		private final BlockModelPart wrapped;
		private final boolean ao;

		AOOverrideBlockModelPart(BlockModelPart wrapped, boolean ao) {
			this.wrapped = wrapped;
			this.ao = ao;
		}

		@Override
		public List<BakedQuad> getQuads(Direction side) {
			return wrapped.getQuads(side);
		}

		@Override
		public TextureAtlasSprite particleIcon() {
			return wrapped.particleIcon();
		}

		@Override
		public ChunkSectionLayer getRenderType(BlockState state) {
			return wrapped.getRenderType(state);
		}

		@Override
		public boolean useAmbientOcclusion() {
			return ao;
		}
	}

}
