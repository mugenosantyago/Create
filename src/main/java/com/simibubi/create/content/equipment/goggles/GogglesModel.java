package com.simibubi.create.content.equipment.goggles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.client.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import com.simibubi.create.foundation.neoforge.compat.client.model.BakedModelWrapper;

public class GogglesModel extends BakedModelWrapper<BakedModel> {

	public GogglesModel(BakedModel template) {
		super(template);
	}

	@Override
	public BakedModel applyTransform(ItemDisplayContext cameraItemDisplayContext, PoseStack mat, boolean leftHanded) {
		// Partial models are BlockStateModel in 1.21.8+; item transforms are applied via the wrapped model.
		return super.applyTransform(cameraItemDisplayContext, mat, leftHanded);
	}

}
