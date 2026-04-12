package net.neoforged.neoforge.client.model.generators;

import java.util.function.BiConsumer;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

/**
 * Stub for the removed NeoForge BlockModelBuilder (MC 1.21.8 port).
 * This class is a no-op stub to allow compilation only.
 */
@SuppressWarnings("all")
public class BlockModelBuilder extends ModelFile {

    public BlockModelBuilder(ResourceLocation location) {
        super(location);
    }

    public BlockModelBuilder texture(String key, ResourceLocation loc) { return this; }
    public BlockModelBuilder texture(String key, String loc) { return this; }
    public BlockModelBuilder renderType(String renderType) { return this; }
    public BlockModelBuilder renderType(ResourceLocation renderType) { return this; }
    public BlockModelBuilder transforms() { return this; }
    public BlockModelBuilder ao(boolean ao) { return this; }
    public BlockModelBuilder guiLight(String light) { return this; }

    public ElementBuilder element() {
        return new ElementBuilder(this);
    }

    public <L extends net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder> L customLoader(java.util.function.BiFunction<BlockModelBuilder, net.neoforged.neoforge.client.model.generators.loaders.CompositeModelBuilder, L> loader) {
        return null;
    }

	public static final class ElementBuilder {
		private final BlockModelBuilder parent;

		ElementBuilder(BlockModelBuilder parent) {
			this.parent = parent;
		}

		public ElementBuilder from(float x, float y, float z) {
			return this;
		}

		public ElementBuilder to(float x, float y, float z) {
			return this;
		}

		public FaceBuilder face(Direction direction) {
			return new FaceBuilder(this);
		}

		public ElementBuilder faces(BiConsumer<Direction, FaceBuilder> consumer) {
			return this;
		}

		public BlockModelBuilder end() {
			return parent;
		}
	}

	public static final class FaceBuilder {
		private final ElementBuilder element;

		FaceBuilder(ElementBuilder element) {
			this.element = element;
		}

		public FaceBuilder uvs(float u1, float v1, float u2, float v2) {
			return this;
		}

		public FaceBuilder texture(String texture) {
			return this;
		}

		public ElementBuilder end() {
			return element;
		}
	}
}
