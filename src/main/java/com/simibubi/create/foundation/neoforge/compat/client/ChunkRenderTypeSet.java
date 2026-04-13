package com.simibubi.create.foundation.neoforge.compat.client;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.minecraft.client.renderer.RenderType;

/**
 * Compatibility stub for ChunkRenderTypeSet which was removed in NeoForge for MC 1.21.8.
 * In 1.21.8, chunk rendering uses ChunkSectionLayer instead.
 * <p>
 * Lives under Create's package namespace so the mod JAR does not ship classes in
 * {@code net.neoforged.*} (avoids JPMS split-package conflicts with the NeoForge module).
 */
public class ChunkRenderTypeSet implements Iterable<RenderType> {

	public static final ChunkRenderTypeSet ALL = new ChunkRenderTypeSet();
	public static final ChunkRenderTypeSet NONE = new ChunkRenderTypeSet();

	public static ChunkRenderTypeSet of(RenderType... types) {
		return ALL;
	}

	public static ChunkRenderTypeSet union(Collection<ChunkRenderTypeSet> sets) {
		return ALL;
	}

	public boolean contains(RenderType type) {
		return true;
	}

	public boolean isEmpty() {
		return false;
	}

	@Override
	public Iterator<RenderType> iterator() {
		return Collections.emptyIterator();
	}

}
