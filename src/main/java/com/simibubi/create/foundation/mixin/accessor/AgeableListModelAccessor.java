package com.simibubi.create.foundation.mixin.accessor;

import net.minecraft.client.model.geom.ModelPart;

// AgeableListModel was removed in MC 1.21.8 - this accessor is a stub
public interface AgeableListModelAccessor {
	default Iterable<ModelPart> create$callHeadParts() {
		return java.util.Collections.emptyList();
	}

	default Iterable<ModelPart> create$callBodyParts() {
		return java.util.Collections.emptyList();
	}
}
