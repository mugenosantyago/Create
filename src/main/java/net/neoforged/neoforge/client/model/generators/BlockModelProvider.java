package net.neoforged.neoforge.client.model.generators;

import net.minecraft.resources.ResourceLocation;

/**
 * Stub for the removed NeoForge BlockModelProvider (MC 1.21.8 port).
 */
@SuppressWarnings("all")
public class BlockModelProvider {
    private final ResourceLocation modId;

    public BlockModelProvider(ResourceLocation modId) {
        this.modId = modId;
    }

    public ModelFile.ExistingModelFile getExistingFile(ResourceLocation loc) {
        return new ModelFile.ExistingModelFile(loc, null);
    }

    public ModelFile getBuilder(String path) {
        return new ModelFile(ResourceLocation.parse(path));
    }

    public ModelFile cubeAll(String name, ResourceLocation texture) {
        return new ModelFile(ResourceLocation.parse(name));
    }

    public ModelFile cube(String name, ResourceLocation down, ResourceLocation up, ResourceLocation north, ResourceLocation south, ResourceLocation east, ResourceLocation west) {
        return new ModelFile(ResourceLocation.parse(name));
    }

	public ModelFile slab(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return new ModelFile(ResourceLocation.parse(name));
	}

	public ModelFile slabTop(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return new ModelFile(ResourceLocation.parse(name));
	}

	public ModelFile cubeColumn(String name, ResourceLocation side, ResourceLocation end) {
		return new ModelFile(ResourceLocation.parse(name));
	}

	public ModelFile cubeColumnHorizontal(String name, ResourceLocation side, ResourceLocation end) {
		return new ModelFile(ResourceLocation.parse(name));
	}

	public ModelFile cubeBottomTop(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return new ModelFile(ResourceLocation.parse(name));
	}

    public BlockModelBuilder withExistingParent(String name, ResourceLocation parent) {
        return new BlockModelBuilder(ResourceLocation.parse(name));
    }

    public BlockModelBuilder withExistingParent(String name, String parent) {
        return new BlockModelBuilder(ResourceLocation.parse(name));
    }

    public ResourceLocation mcLoc(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

	public ResourceLocation modLoc(String path) {
		return ResourceLocation.fromNamespaceAndPath(modId.getNamespace(), path);
	}
}
