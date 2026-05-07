package com.simibubi.create.foundation.model;

import java.util.Map;

import com.simibubi.create.foundation.block.render.CustomBlockModels;
import com.simibubi.create.foundation.item.render.CustomItemModels;

import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;

public class ModelSwapper {

	protected CustomBlockModels customBlockModels = new CustomBlockModels();
	protected CustomItemModels customItemModels = new CustomItemModels();

	public CustomBlockModels getCustomBlockModels() {
		return customBlockModels;
	}

	public CustomItemModels getCustomItemModels() {
		return customItemModels;
	}

	public void registerListeners(IEventBus modEventBus) {
		modEventBus.addListener(this::onModifyBakingResult);
	}

	@OnlyIn(Dist.CLIENT)
	private void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
		Map<BlockState, BlockStateModel> blockStateModels = event.getBakingResult().blockStateModels();
		customBlockModels.forEach((block, modelFunc) -> {
			for (BlockState state : block.getStateDefinition().getPossibleStates()) {
				BlockStateModel original = blockStateModels.get(state);
				if (original != null) {
					blockStateModels.put(state, modelFunc.apply(original));
				}
			}
		});
	}

}
