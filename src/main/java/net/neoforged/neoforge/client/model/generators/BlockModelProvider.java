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

    public ModelFile getExistingFile(ResourceLocation loc) {
        return new ModelFile.ExistingModelFile(loc, null);
    }

    public ModelFile getBuilder(String path) {
        return new ModelFile(ResourceLocation.parse(path));
    }

    public ResourceLocation mcLoc(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

	public ResourceLocation modLoc(String path) {
		return ResourceLocation.fromNamespaceAndPath(modId.getNamespace(), path);
	}
}
