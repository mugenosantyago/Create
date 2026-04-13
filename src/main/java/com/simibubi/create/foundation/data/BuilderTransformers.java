package com.simibubi.create.foundation.data;

import static com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour.interactionBehaviour;
import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.foundation.data.BlockStateGen.axisBlock;
import static com.simibubi.create.foundation.data.CreateRegistrate.casingConnectivity;
import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.Create;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.contraptions.behaviour.DoorMovingInteraction;
import com.simibubi.create.content.contraptions.behaviour.TrapdoorMovingInteraction;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonGenerator;
import com.simibubi.create.content.decoration.MetalScaffoldingBlock;
import com.simibubi.create.content.decoration.MetalScaffoldingBlockItem;
import com.simibubi.create.content.decoration.MetalScaffoldingCTBehaviour;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorMovementBehaviour;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlock;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogCTBehaviour;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles.PackageStyle;
import com.simibubi.create.content.logistics.packager.PackagerGenerator;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlockItem;
import com.simibubi.create.content.logistics.tableCloth.TableClothModel;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlock;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlock.Shape;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelItem;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.StandardBogeyBlock;
import com.simibubi.create.foundation.block.ItemUseOverrides;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.HorizontalCTBehaviour;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.infrastructure.config.CStress;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.generators.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import com.simibubi.create.foundation.neoforge.compat.client.model.generators.ConfiguredModel;
import com.simibubi.create.foundation.neoforge.compat.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.Tags;

@SuppressWarnings("removal") // addLayer is staying... for now
public class BuilderTransformers {
	public static <B extends EncasedShaftBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> encasedShaft(String casing,
																										 Supplier<CTSpriteShiftEntry> casingShift) {
		return builder -> encasedBase(builder, () -> AllBlocks.SHAFT.get())
			.onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(casingShift.get())))
			.onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, casingShift.get(),
				(s, f) -> f.getAxis() != s.getValue(EncasedShaftBlock.AXIS))))
			.defaultBlockstate()
			.item()
			.model(() -> AssetLookup.customBlockItemModel("encased_shaft", "item_" + casing))
			.build();
	}

	public static <B extends StandardBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> bogey() {
		return b -> b.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
			.properties(p -> p.noOcclusion())
			.transform(pickaxeOnly())
			.defaultBlockstate()
			.loot((p, l) -> p.dropOther(l, AllBlocks.RAILWAY_CASING.get()))
			.onRegister(
				block -> AbstractBogeyBlock.registerStandardBogey(RegisteredObjectsHelper.getKeyOrThrow(block)));
	}

	public static <B extends CopycatBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> copycat() {
		return b -> b.initialProperties(SharedProperties::softMetal)
			.defaultBlockstate()
			.properties(p -> p.noOcclusion()
				.mapColor(MapColor.NONE)
				.isValidSpawn((state, level, pos, type) -> false))
			// Registrate 1.3.x / MC 1.21.8: only one render layer per block.
			.addLayer(() -> () -> net.minecraft.client.renderer.chunk.ChunkSectionLayer.CUTOUT)
			// Copycat block tint: RegisterColorHandlersEvent in CreateClient (avoids BlockColor on dedicated server).
			.transform(TagGen.axeOrPickaxe());
	}

	public static <B extends TrapDoorBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> trapdoor(boolean orientable) {
		return b -> b.defaultBlockstate()
			.transform(pickaxeOnly())
			.tag(BlockTags.TRAPDOORS)
			.onRegister(interactionBehaviour(new TrapdoorMovingInteraction()))
			.item()
			.tag(ItemTags.TRAPDOORS)
			.build();
	}

	public static <B extends SlidingDoorBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> slidingDoor(String type) {
		return b -> b.initialProperties(() -> Blocks.IRON_DOOR)
			.properties(p -> p.requiresCorrectToolForDrops()
				.strength(3.0F, 6.0F))
			.defaultBlockstate()
			.addLayer(() -> () -> net.minecraft.client.renderer.chunk.ChunkSectionLayer.CUTOUT_MIPPED)
			.transform(pickaxeOnly())
			.onRegister(interactionBehaviour(new DoorMovingInteraction()))
			.onRegister(movementBehaviour(new SlidingDoorMovementBehaviour()))
			.tag(BlockTags.DOORS)
			.tag(BlockTags.WOODEN_DOORS) // for villager AI
			.tag(AllBlockTags.NON_DOUBLE_DOOR.tag)
			.loot((lr, block) -> lr.add(block, lr.createDoorTable(block)))
			.item()
			.tag(ItemTags.DOORS)
			.tag(AllItemTags.CONTRAPTION_CONTROLLED.tag)
			.model(() -> (c, p) -> p.generateFlatItem(c.getEntry(), p.modLoc("item/" + type + "_door")))
			.build();
	}

	public static <B extends EncasedCogwheelBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> encasedCogwheel(
		String casing, Supplier<CTSpriteShiftEntry> casingShift) {
		return b -> encasedCogwheelBase(b, casing, casingShift, () -> AllBlocks.COGWHEEL.get(), false);
	}

	public static <B extends EncasedCogwheelBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> encasedLargeCogwheel(
		String casing, Supplier<CTSpriteShiftEntry> casingShift) {
		return b -> encasedCogwheelBase(b, casing, casingShift, () -> AllBlocks.LARGE_COGWHEEL.get(), true)
			.onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCogCTBehaviour(casingShift.get())));
	}

	private static <B extends EncasedCogwheelBlock, P> BlockBuilder<B, P> encasedCogwheelBase(BlockBuilder<B, P> b,
																							  String casing, Supplier<CTSpriteShiftEntry> casingShift, Supplier<ItemLike> drop, boolean large) {
		String blockFolder = large ? "encased_large_cogwheel" : "encased_cogwheel";
		return encasedBase(b, drop).addLayer(() -> () -> net.minecraft.client.renderer.chunk.ChunkSectionLayer.CUTOUT_MIPPED)
			.onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, casingShift.get(),
				(s, f) -> f.getAxis() == s.getValue(EncasedCogwheelBlock.AXIS)
					&& !s.getValue(f.getAxisDirection() == AxisDirection.POSITIVE ? EncasedCogwheelBlock.TOP_SHAFT
					: EncasedCogwheelBlock.BOTTOM_SHAFT))))
			.defaultBlockstate()
			.item()
			.model(() -> AssetLookup.encasedCogwheelItemModel(blockFolder, casing, large))
			.build();
	}

	private static <B extends RotatedPillarKineticBlock, P> BlockBuilder<B, P> encasedBase(BlockBuilder<B, P> b,
																						   Supplier<ItemLike> drop) {
		return b.initialProperties(SharedProperties::stone)
			.properties(BlockBehaviour.Properties::noOcclusion)
			.transform(CStress.setNoImpact())
			.loot((p, lb) -> p.dropOther(lb, drop.get()));
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> cuckooClock() {
		return b -> b.initialProperties(SharedProperties::wooden)
			.defaultBlockstate()
			.addLayer(() -> () -> net.minecraft.client.renderer.chunk.ChunkSectionLayer.CUTOUT_MIPPED)
			.transform(CStress.setImpact(1))
			.item()
			.transform(ModelGen.customItemModel("cuckoo_clock", "item"));
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> ladder(String name,
																					   Supplier<DataIngredient> ingredient, MapColor color) {
		return b -> b.initialProperties(() -> Blocks.LADDER)
			.properties(p -> p.mapColor(color))
			.addLayer(() -> () -> net.minecraft.client.renderer.chunk.ChunkSectionLayer.CUTOUT)
			.defaultBlockstate()
			.properties(p -> p.sound(SoundType.COPPER))
			.transform(pickaxeOnly())
			.tag(BlockTags.CLIMBABLE)
			.item()
			.recipe((c, p) -> p.stonecutting(ingredient.get(), RecipeCategory.DECORATIONS, c::get, 2))
			.model(() -> (c, p) -> p.generateFlatItem(c.getEntry(), p.modLoc("block/ladder_" + name)))
			.build();
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> scaffold(String name,
																						 Supplier<DataIngredient> ingredient, MapColor color, CTSpriteShiftEntry scaffoldShift,
																						 CTSpriteShiftEntry scaffoldInsideShift, CTSpriteShiftEntry casingShift) {
		return b -> b.initialProperties(() -> Blocks.SCAFFOLDING)
			.properties(p -> p.sound(SoundType.COPPER)
				.mapColor(color))
			.addLayer(() -> () -> net.minecraft.client.renderer.chunk.ChunkSectionLayer.CUTOUT)
			.defaultBlockstate()
			.onRegister(connectedTextures(
				() -> new MetalScaffoldingCTBehaviour(scaffoldShift, scaffoldInsideShift, casingShift)))
			.transform(pickaxeOnly())
			.tag(BlockTags.CLIMBABLE)
			.item(MetalScaffoldingBlockItem::new)
			.recipe((c, p) -> p.stonecutting(ingredient.get(), RecipeCategory.DECORATIONS, c::get, 2))
			.model(() -> (c, p) -> p.createWithExistingModel(c.getEntry(), p.modLoc("block/" + c.getName())))
			.build();
	}

	public static <B extends ValveHandleBlock> NonNullUnaryOperator<BlockBuilder<B, CreateRegistrate>> valveHandle(
		@Nullable DyeColor color) {
		return b -> b.initialProperties(SharedProperties::copperMetal)
			.defaultBlockstate()
			.tag(AllBlockTags.BRITTLE.tag, AllBlockTags.VALVE_HANDLES.tag)
			.onRegister(BlockStressValues.setGeneratorSpeed(32))
			.onRegister(ItemUseOverrides::addBlock)
			.item()
			.tag(AllItemTags.VALVE_HANDLES.tag)
			.build();
	}

	public static <B extends CasingBlock> NonNullUnaryOperator<BlockBuilder<B, CreateRegistrate>> casing(
		Supplier<CTSpriteShiftEntry> ct) {
		return b -> b.initialProperties(SharedProperties::stone)
			.properties(p -> p.sound(SoundType.WOOD))
			.transform(axeOrPickaxe())
			.defaultBlockstate()
			.onRegister(connectedTextures(() -> new EncasedCTBehaviour(ct.get())))
			.onRegister(casingConnectivity((block, cc) -> cc.makeCasing(block, ct.get())))
			.tag(AllBlockTags.CASING.tag)
			.item()
			.tag(AllItemTags.CASING.tag)
			.build();
	}

	public static <B extends CasingBlock> NonNullUnaryOperator<BlockBuilder<B, CreateRegistrate>> layeredCasing(
		Supplier<CTSpriteShiftEntry> ct, Supplier<CTSpriteShiftEntry> ct2) {
		return b -> b.initialProperties(SharedProperties::stone)
			.transform(axeOrPickaxe())
			.defaultBlockstate()
			.onRegister(connectedTextures(() -> new HorizontalCTBehaviour(ct.get(), ct2.get())))
			.onRegister(casingConnectivity((block, cc) -> cc.makeCasing(block, ct.get())))
			.tag(AllBlockTags.CASING.tag)
			.item()
			.tag(AllItemTags.CASING.tag)
			.build();
	}

	public static <B extends BeltTunnelBlock> NonNullUnaryOperator<BlockBuilder<B, CreateRegistrate>> beltTunnel(
		String type, ResourceLocation particleTexture) {
		return b -> b.initialProperties(SharedProperties::stone)
			.addLayer(() -> () -> net.minecraft.client.renderer.chunk.ChunkSectionLayer.CUTOUT_MIPPED)
			.properties(BlockBehaviour.Properties::noOcclusion)
			.transform(pickaxeOnly())
			.defaultBlockstate()
			.item(BeltTunnelItem::new)
			.model(() -> AssetLookup.beltTunnelItemModel(type, particleTexture))
			.build();
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> mechanicalPiston(PistonType type) {
		return b -> b.initialProperties(SharedProperties::stone)
			.properties(p -> p.noOcclusion())
			.defaultBlockstate()
			.addLayer(() -> () -> net.minecraft.client.renderer.chunk.ChunkSectionLayer.CUTOUT_MIPPED)
			.transform(CStress.setImpact(4.0))
			.item()
			.transform(ModelGen.customItemModel("mechanical_piston", type.getSerializedName(), "item"));
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> bearing(String prefix,
																						String backTexture) {
		ResourceLocation baseBlockModelLocation = Create.asResource("block/bearing/block");
		ResourceLocation baseItemModelLocation = Create.asResource("block/bearing/item");
		ResourceLocation topTextureLocation = Create.asResource("block/bearing_top");
		ResourceLocation sideTextureLocation = Create.asResource("block/" + prefix + "_bearing_side");
		ResourceLocation backTextureLocation = Create.asResource("block/" + backTexture);
		return b -> b.initialProperties(SharedProperties::stone)
			.properties(p -> p.noOcclusion())
			.defaultBlockstate()
			.item()
			.model(() -> AssetLookup.bearingItemModel(baseItemModelLocation, topTextureLocation, sideTextureLocation,
				backTextureLocation))
			.build();
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> crate(String type) {
		return b -> b.initialProperties(SharedProperties::stone)
			.transform(axeOrPickaxe())
			.defaultBlockstate()
			.item()
			.properties(p -> type.equals("creative") ? p.rarity(Rarity.EPIC) : p)
			.transform(ModelGen.customItemModel("crate", type, "single"));
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> backtank(Supplier<ItemLike> drop) {
		return backtankLootItem(() -> drop.get()
			.asItem());
	}

	/**
	 * Resolves the dropped armor item from the registry when loot is built, so common block registration
	 * does not need a static reference to another registrate class (which may pull client-only types on dedicated servers).
	 */
	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> backtankLootItem(ResourceLocation dropItemId) {
		return backtankLootItem(() -> {
			Item item = BuiltInRegistries.ITEM.getValue(dropItemId);
			if (item == null)
				throw new IllegalStateException("Missing backtank loot item: " + dropItemId);
			return item;
		});
	}

	private static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> backtankLootItem(Supplier<Item> dropItem) {
		return b -> b.defaultBlockstate()
			.transform(pickaxeOnly())
			.addLayer(() -> () -> net.minecraft.client.renderer.chunk.ChunkSectionLayer.CUTOUT_MIPPED)
			.transform(CStress.setImpact(4.0))
			.loot((lt, block) -> {
				Builder builder = LootTable.lootTable();
				LootItemCondition.Builder survivesExplosion = ExplosionCondition.survivesExplosion();
				lt.add(block, builder.withPool(LootPool.lootPool()
					.when(survivesExplosion)
					.setRolls(ConstantValue.exactly(1))
					.add(LootItem.lootTableItem(dropItem.get())
						.apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
							.include(AllDataComponents.BACKTANK_AIR)))));
			});
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> bell() {
		return b -> b.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.noOcclusion()
				.sound(SoundType.ANVIL))
			.transform(pickaxeOnly())
			.addLayer(() -> () -> net.minecraft.client.renderer.chunk.ChunkSectionLayer.CUTOUT_MIPPED)
			.tag(AllBlockTags.BRITTLE.tag)
			.defaultBlockstate()
			.item()
			.model(() -> (c, p) -> p.createWithExistingModel(c.getEntry(), p.modLoc("block/" + c.getName())))
			.tag(AllItemTags.CONTRAPTION_CONTROLLED.tag)
			.build();
	}

	public static ItemBuilder<PackageItem, CreateRegistrate> packageItem(PackageStyle style) {
		String size = "_" + style.width() + "x" + style.height();
		return Create.registrate().item(style.getItemId()
				.getPath(), p -> new PackageItem(p, style))
			.properties(p -> p.stacksTo(1))
			.tag(AllItemTags.PACKAGES.tag)
			.model(() -> (c, p) -> {
				if (style.rare()) {
					TextureSlot layer = TextureSlot.create("2");
					ResourceLocation styleTex = Create.asResource("item/package/" + style.type());
					ModelTemplate template = new ModelTemplate(
						Optional.of(Create.asResource("item/package/custom" + size)),
						Optional.empty(),
						layer,
						TextureSlot.PARTICLE
					);
					TextureMapping mapping = new TextureMapping()
						.put(layer, styleTex)
						.put(TextureSlot.PARTICLE, styleTex);
					p.generateWithTemplate(c.getEntry(), template, mapping);
				} else
					p.createWithExistingModel(c.getEntry(), p.modLoc("item/package/" + style.type() + size));
			})
			.lang((style.rare() ? "Rare"
				: style.type()
				.substring(0, 1)
				.toUpperCase(Locale.ROOT)
				+ style.type()
				.substring(1))
				+ " Package");
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> tableCloth(String name,
																						   NonNullSupplier<? extends Block> initialProps, boolean dyed) {
		return b -> {
			TagKey<Block> soundTag = dyed ? BlockTags.COMBINATION_STEP_SOUND_BLOCKS : BlockTags.INSIDE_STEP_SOUND_BLOCKS;

			ItemBuilder<TableClothBlockItem, BlockBuilder<B, P>> item = b.initialProperties(initialProps)
				.addLayer(() -> () -> net.minecraft.client.renderer.chunk.ChunkSectionLayer.CUTOUT_MIPPED)
				.defaultBlockstate()
				.onRegister(CreateRegistrate.blockModel())
				.tag(AllBlockTags.TABLE_CLOTHS.tag, soundTag)
				.onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.create.table_cloth"))
				.item(TableClothBlockItem::new);

			if (dyed)
				item.tag(AllItemTags.DYED_TABLE_CLOTHS.tag);

			return item.model(() -> AssetLookup.tableClothItemModel(name))
				.tag(AllItemTags.TABLE_CLOTHS.tag)
				.recipe((c, p) -> ShapelessRecipeBuilder.shapeless(p.itemLookup(), RecipeCategory.MISC, c.get())
					.requires(c.get())
					.unlockedBy("has_" + c.getName(), p.has(c.get()))
					.save(p, p.safeKey(Create.asResource("crafting/logistics/" + c.getName() + "_clear"))))
				.build();
		};
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> packager() {
		return b -> b.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.noOcclusion())
			.properties(p -> p.isRedstoneConductor(($1, $2, $3) -> false))
			.properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE)
				.sound(SoundType.NETHERITE_BLOCK))
			.transform(pickaxeOnly())
			.addLayer(() -> () -> net.minecraft.client.renderer.chunk.ChunkSectionLayer.CUTOUT_MIPPED)
			.defaultBlockstate()
			.item()
			.model(() -> AssetLookup::customItemModel)
			.build();
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> palettesIronBlock() {
		return b -> b.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.mapColor(MapColor.COLOR_GRAY)
				.sound(SoundType.NETHERITE_BLOCK)
				.requiresCorrectToolForDrops())
			.transform(pickaxeOnly())
			.defaultBlockstate()
			.tag(AllBlockTags.WRENCH_PICKUP.tag)
			.recipe((c, p) -> p.stonecutting(com.simibubi.create.foundation.data.recipe.DataIngredientCompat.tag(Tags.Items.INGOTS_IRON), RecipeCategory.BUILDING_BLOCKS,
				c::get, 2))
			.simpleItem();
	}
}
