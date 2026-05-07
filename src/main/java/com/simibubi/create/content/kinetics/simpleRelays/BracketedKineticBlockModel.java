package com.simibubi.create.content.kinetics.simpleRelays;

import java.util.List;

import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BracketedKineticBlockModel implements BlockStateModel {

	private final BlockStateModel wrapped;

	public BracketedKineticBlockModel(BlockStateModel wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public void collectParts(RandomSource random, List<BlockModelPart> out) {
		wrapped.collectParts(random, out);
	}

	@Override
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random,
			List<BlockModelPart> out) {
		if (level instanceof PonderLevel) {
			wrapped.collectParts(level, pos, state, random, out);
			return;
		}

		BracketedBlockEntityBehaviour behaviour =
			BlockEntityBehaviour.get(level, pos, BracketedBlockEntityBehaviour.TYPE);
		if (behaviour != null) {
			BlockState bracketState = behaviour.getBracket();
			if (bracketState != null) {
				BlockStateModel bracketModel =
					Minecraft.getInstance().getBlockRenderer().getBlockModel(bracketState);
				bracketModel.collectParts(level, pos, bracketState, random, out);
				return;
			}
		}
		// No bracket: Flywheel handles the visual; render nothing here
	}

	@Override
	public TextureAtlasSprite particleIcon() {
		return wrapped.particleIcon();
	}

}
