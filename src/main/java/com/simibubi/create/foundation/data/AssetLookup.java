package com.simibubi.create.foundation.data;

import java.util.Optional;
import java.util.function.Function;

import com.simibubi.create.Create;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.generators.RegistrateItemModelGenerator;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class AssetLookup {

	/**
	 * Custom block models packaged with other partials. Example:
	 * models/block/schematicannon/block.json <br>
	 * <br>
	 * Adding "powered", "vertical" will look for /block_powered_vertical.json
	 */
	public static ModelFile partialBaseModel(DataGenContext<?, ?> ctx, RegistrateBlockstateProvider prov,
		String... suffix) {
		String string = "/block";
		for (String suf : suffix)
			if (!suf.isEmpty())
				string += "_" + suf;
		final String location = "block/" + ctx.getName() + string;
		return prov.models()
			.getExistingFile(prov.modLoc(location));
	}

	/**
	 * Custom block model from models/block/x.json
	 */
	public static ModelFile standardModel(DataGenContext<?, ?> ctx, RegistrateBlockstateProvider prov) {
		return prov.models()
			.getExistingFile(prov.modLoc("block/" + ctx.getName()));
	}

	/**
	 * Generate item model inheriting from models/block/&lt;name&gt;/item.json
	 */
	public static <I extends BlockItem> void customItemModel(DataGenContext<Item, I> ctx,
		RegistrateItemModelGenerator prov) {
		prov.generateBlockItem(ctx.getEntry(), "/item");
	}

	/**
	 * Generate item model inheriting from a separate model in
	 * models/block/folders[0]/folders[1]/... "_" will be replaced by the item name
	 */
	public static <I extends BlockItem> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelGenerator> customBlockItemModel(
		String... folders) {
		return (c, p) -> {
			String path = "block";
			for (String string : folders)
				path += "/" + ("_".equals(string) ? c.getName() : string);
			p.createWithExistingModel(c.get(), p.modLoc(path));
		};
	}

	public static <I extends Item> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelGenerator> customGenericItemModel(
		String... folders) {
		return (c, p) -> {
			String path = "block";
			for (String string : folders)
				path += "/" + ("_".equals(string) ? c.getName() : string);
			p.createWithExistingModel(c.get(), p.modLoc(path));
		};
	}

	public static Function<BlockState, ModelFile> forPowered(DataGenContext<?, ?> ctx,
		RegistrateBlockstateProvider prov) {
		return state -> state.getValue(BlockStateProperties.POWERED) ? partialBaseModel(ctx, prov, "powered")
			: partialBaseModel(ctx, prov);
	}

	public static Function<BlockState, ModelFile> forPowered(DataGenContext<?, ?> ctx,
		RegistrateBlockstateProvider prov, String path) {
		return state -> prov.models()
			.getExistingFile(
				prov.modLoc("block/" + path + (state.getValue(BlockStateProperties.POWERED) ? "_powered" : "")));
	}

	public static Function<BlockState, ModelFile> withIndicator(DataGenContext<?, ?> ctx,
		RegistrateBlockstateProvider prov, Function<BlockState, ModelFile> baseModelFunc, IntegerProperty property) {
		return state -> {
			ResourceLocation baseModel = baseModelFunc.apply(state)
				.getLocation();
			Integer integer = state.getValue(property);
			return prov.models()
				.withExistingParent(ctx.getName() + "_" + integer, baseModel)
				.texture("indicator", "block/indicator/" + integer);
		};
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelGenerator> existingItemModel() {
		return (c, p) -> p.createWithExistingModel(c.get(), p.modLoc("item/" + c.getName()));
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelGenerator> itemModel(String name) {
		return (c, p) -> p.createWithExistingModel(c.get(), p.modLoc("item/" + name));
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelGenerator> itemModelWithPartials() {
		return (c, p) -> p.createWithExistingModel(c.get(), p.modLoc("item/" + c.getName() + "/item"));
	}

	public static <I extends BlockItem> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelGenerator> encasedCogwheelItemModel(
		String blockFolder, String casing, boolean large) {
		return (c, p) -> {
			String encasedSuffix = "_encased_cogwheel_side" + (large ? "_connected" : "");
			String wood = casing.equals("brass") ? "dark_oak" : "spruce";
			TextureSlot casingSlot = TextureSlot.create("casing");
			TextureSlot oneSlot = TextureSlot.create("1");
			ResourceLocation woodTop = ResourceLocation.withDefaultNamespace("block/stripped_" + wood + "_log_top");
			ResourceLocation casingTex = Create.asResource("block/" + casing + "_casing");
			ResourceLocation sideTex = Create.asResource("block/" + casing + encasedSuffix);

			if (large) {
				TextureSlot fourSlot = TextureSlot.create("4");
				ModelTemplate template = new ModelTemplate(
					Optional.of(Create.asResource("block/" + blockFolder + "/item")),
					Optional.empty(),
					casingSlot, oneSlot, TextureSlot.SIDE, fourSlot, TextureSlot.PARTICLE
				);
				TextureMapping mapping = new TextureMapping()
					.put(casingSlot, casingTex)
					.put(oneSlot, woodTop)
					.put(TextureSlot.SIDE, sideTex)
					.put(fourSlot, Create.asResource("block/large_cogwheel"))
					.put(TextureSlot.PARTICLE, casingTex);
				p.generateWithTemplate(c.getEntry(), template, mapping);
			} else {
				TextureSlot oneTwoSlot = TextureSlot.create("1_2");
				ModelTemplate template = new ModelTemplate(
					Optional.of(Create.asResource("block/" + blockFolder + "/item")),
					Optional.empty(),
					casingSlot, TextureSlot.PARTICLE, oneSlot, TextureSlot.SIDE, oneTwoSlot
				);
				TextureMapping mapping = new TextureMapping()
					.put(casingSlot, casingTex)
					.put(TextureSlot.PARTICLE, casingTex)
					.put(oneSlot, woodTop)
					.put(TextureSlot.SIDE, sideTex)
					.put(oneTwoSlot, Create.asResource("block/cogwheel"));
				p.generateWithTemplate(c.getEntry(), template, mapping);
			}
		};
	}

	public static <I extends BlockItem> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelGenerator> beltTunnelItemModel(
		String type, ResourceLocation particleTexture) {
		return (c, p) -> {
			String prefix = "block/tunnel/" + type + "_tunnel";
			String funnelPrefix = "block/funnel/" + type + "_funnel";
			ModelTemplate template = new ModelTemplate(
				Optional.of(Create.asResource("block/belt_tunnel/item")),
				Optional.empty(),
				TextureSlot.create("top"),
				TextureSlot.create("tunnel"),
				TextureSlot.create("direction"),
				TextureSlot.create("frame"),
				TextureSlot.PARTICLE
			);
			TextureMapping mapping = new TextureMapping()
				.put(TextureSlot.create("top"), Create.asResource(prefix + "_top"))
				.put(TextureSlot.create("tunnel"), Create.asResource(prefix))
				.put(TextureSlot.create("direction"), Create.asResource(funnelPrefix + "_neutral"))
				.put(TextureSlot.create("frame"), Create.asResource(funnelPrefix + "_frame"))
				.put(TextureSlot.PARTICLE, particleTexture);
			p.generateWithTemplate(c.getEntry(), template, mapping);
		};
	}

	public static <I extends BlockItem> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelGenerator> bearingItemModel(
		ResourceLocation baseItemModel, ResourceLocation top, ResourceLocation side, ResourceLocation back) {
		return (c, p) -> {
			ModelTemplate template = new ModelTemplate(
				Optional.of(baseItemModel),
				Optional.empty(),
				TextureSlot.create("top"),
				TextureSlot.create("side"),
				TextureSlot.create("back")
			);
			TextureMapping mapping = new TextureMapping()
				.put(TextureSlot.create("top"), top)
				.put(TextureSlot.create("side"), side)
				.put(TextureSlot.create("back"), back);
			p.generateWithTemplate(c.getEntry(), template, mapping);
		};
	}

	public static <I extends BlockItem> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelGenerator> bracketItemModel(String material) {
		return (c, p) -> {
			ResourceLocation plate = Create.asResource("block/bracket_plate_" + material);
			ModelTemplate template = new ModelTemplate(
				Optional.of(Create.asResource("block/bracket/item")),
				Optional.empty(),
				TextureSlot.create("bracket"),
				TextureSlot.create("plate"),
				TextureSlot.PARTICLE
			);
			TextureMapping mapping = new TextureMapping()
				.put(TextureSlot.create("bracket"), Create.asResource("block/bracket_" + material))
				.put(TextureSlot.create("plate"), plate)
				.put(TextureSlot.PARTICLE, plate);
			p.generateWithTemplate(c.getEntry(), template, mapping);
		};
	}

	public static <I extends BlockItem> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelGenerator> tableClothItemModel(String name) {
		return (c, p) -> {
			ResourceLocation cloth = Create.asResource("block/table_cloth/" + name);
			TextureSlot layer0 = TextureSlot.create("0");
			ModelTemplate template = new ModelTemplate(
				Optional.of(Create.asResource("block/table_cloth/item")),
				Optional.empty(),
				layer0,
				TextureSlot.PARTICLE
			);
			TextureMapping mapping = new TextureMapping()
				.put(layer0, cloth)
				.put(TextureSlot.PARTICLE, cloth);
			p.generateWithTemplate(c.getEntry(), template, mapping);
		};
	}

}
