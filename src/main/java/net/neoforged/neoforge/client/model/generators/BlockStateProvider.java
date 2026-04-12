package net.neoforged.neoforge.client.model.generators;

import net.minecraft.resources.ResourceLocation;

/**
 * Compatibility stub for BlockStateProvider which was removed in NeoForge for MC 1.21.8.
 * Use RegistrateBlockModelGenerator instead.
 */
@SuppressWarnings("all")
public abstract class BlockStateProvider {

	public BlockModelProvider models() {
		return new BlockModelProvider(ResourceLocation.parse("minecraft:stub"));
	}

	public void simpleBlock(net.minecraft.world.level.block.Block block) {
	}

	public void simpleBlock(net.minecraft.world.level.block.Block block, ModelFile model) {
	}

	public net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder getVariantBuilder(net.minecraft.world.level.block.Block block) {
		return new net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder(block);
	}

	public net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder getMultipartBuilder(net.minecraft.world.level.block.Block block) {
		return null;
	}

	public ResourceLocation mcLoc(String name) {
		return ResourceLocation.withDefaultNamespace(name);
	}

	public ResourceLocation modLoc(String name) {
		return ResourceLocation.parse(name);
	}

}
