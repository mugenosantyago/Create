package dev.engine_room.flywheel.impl.visualization.storage;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.SectionPos;

/**
 * MC 1.21.8: Light section coordinate calculation changed, causing ArrayIndexOutOfBoundsException
 * when adding invalid coordinates (-1) to the light update set. This patched version adds bounds checking.
 */
public class LightUpdatedVisualStorage {
	private final LongOpenHashSet sections = new LongOpenHashSet();

	public void onLightUpdate(SectionPos pos) {
		if (pos == null) {
			return;
		}
		long key = pos.asLong();
		// Add bounds check to prevent ArrayIndexOutOfBoundsException with invalid coordinates
		if (key >= 0) {
			sections.add(key);
		}
	}

	public void reset() {
		sections.clear();
	}

	public boolean contains(SectionPos pos) {
		if (pos == null) {
			return false;
		}
		long key = pos.asLong();
		return key >= 0 && sections.contains(key);
	}
}
