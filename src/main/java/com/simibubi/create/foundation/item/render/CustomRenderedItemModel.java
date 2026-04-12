package com.simibubi.create.foundation.item.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.item.ItemDisplayContext;

/**
 * Stub class for custom rendered item models.
 * In MC 1.21.8, BakedModel and BakedModelWrapper were removed.
 * Custom item rendering now uses IClientItemExtensions.
 */
public class CustomRenderedItemModel {

	protected final Object originalModel;

	public CustomRenderedItemModel(Object originalModel) {
		this.originalModel = originalModel;
	}

	public boolean isCustomRenderer() {
		return true;
	}

	public Object applyTransform(ItemDisplayContext cameraItemDisplayContext, PoseStack mat, boolean leftHand) {
		return this;
	}

	public Object getOriginalModel() {
		return originalModel;
	}

}
