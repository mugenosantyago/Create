package com.simibubi.create.infrastructure;

import java.util.HashMap;
import java.util.Map;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.recipe.CommonMetal;

import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber
public class RemapHelper {
	private static final Map<String, ResourceLocation> reMap = new HashMap<>();

	static {
		try {
			Class<?> allBlocksClass = Class.forName("com.simibubi.create.AllBlocks");
			Class<?> allItemsClass = Class.forName("com.simibubi.create.AllItems");
			Class<?> allPaletteBlocksClass = Class.forName("com.simibubi.create.content.decoration.palettes.AllPaletteBlocks");
			
			reMap.put("toggle_latch", getId(allBlocksClass, "POWERED_TOGGLE_LATCH"));
			reMap.put("encased_shaft", getId(allBlocksClass, "ANDESITE_ENCASED_SHAFT"));
			reMap.put("encased_belt", getId(allBlocksClass, "ENCASED_CHAIN_DRIVE"));
			reMap.put("adjustable_pulley", getId(allBlocksClass, "ADJUSTABLE_CHAIN_GEARSHIFT"));
			reMap.put("stockswitch", getId(allBlocksClass, "THRESHOLD_SWITCH"));
			reMap.put("redstone_latch", getId(allBlocksClass, "POWERED_LATCH"));
			reMap.put("contact", getId(allBlocksClass, "REDSTONE_CONTACT"));
			reMap.put("belt_funnel", getId(allBlocksClass, "BRASS_BELT_FUNNEL"));
			reMap.put("entity_detector", getId(allBlocksClass, "SMART_OBSERVER"));
			reMap.put("saw", getId(allBlocksClass, "MECHANICAL_SAW"));
			reMap.put("flexpulsepeater", getId(allBlocksClass, "PULSE_REPEATER"));
			reMap.put("stress_gauge", getId(allBlocksClass, "STRESSOMETER"));
			reMap.put("harvester", getId(allBlocksClass, "MECHANICAL_HARVESTER"));
			reMap.put("plough", getId(allBlocksClass, "MECHANICAL_PLOUGH"));
			reMap.put("drill", getId(allBlocksClass, "MECHANICAL_DRILL"));
			reMap.put("flexpeater", getId(allBlocksClass, "PULSE_EXTENDER"));
			reMap.put("rotation_chassis", getId(allBlocksClass, "RADIAL_CHASSIS"));
			reMap.put("belt_tunnel", getId(allBlocksClass, "BRASS_TUNNEL"));
			reMap.put("redstone_bridge", getId(allBlocksClass, "REDSTONE_LINK"));
			reMap.put("speed_gauge", getId(allBlocksClass, "SPEEDOMETER"));
			reMap.put("translation_chassis", getId(allBlocksClass, "LINEAR_CHASSIS"));
			reMap.put("translation_chassis_secondary", getId(allBlocksClass, "SECONDARY_LINEAR_CHASSIS"));
			reMap.put("piston_pole", getId(allBlocksClass, "PISTON_EXTENSION_POLE"));
			reMap.put("adjustable_pulse_repeater", getId(allBlocksClass, "PULSE_REPEATER"));
			reMap.put("adjustable_repeater", getId(allBlocksClass, "PULSE_REPEATER"));

			reMap.put("copper_block", RegisteredObjectsHelper.getKeyOrThrow(Blocks.COPPER_BLOCK));
			reMap.put("copper_ore", RegisteredObjectsHelper.getKeyOrThrow(Blocks.COPPER_ORE));

			reMap.put("acacia_glass", getId(allPaletteBlocksClass, "ACACIA_WINDOW"));
			reMap.put("acacia_glass_pane", getId(allPaletteBlocksClass, "ACACIA_WINDOW_PANE"));
			reMap.put("birch_glass", getId(allPaletteBlocksClass, "BIRCH_WINDOW"));
			reMap.put("birch_glass_pane", getId(allPaletteBlocksClass, "BIRCH_WINDOW_PANE"));
			reMap.put("dark_oak_glass", getId(allPaletteBlocksClass, "DARK_OAK_WINDOW"));
			reMap.put("dark_oak_glass_pane", getId(allPaletteBlocksClass, "DARK_OAK_WINDOW_PANE"));
			reMap.put("jungle_glass", getId(allPaletteBlocksClass, "JUNGLE_WINDOW"));
			reMap.put("jungle_glass_pane", getId(allPaletteBlocksClass, "JUNGLE_WINDOW_PANE"));
			reMap.put("oak_glass", getId(allPaletteBlocksClass, "OAK_WINDOW"));
			reMap.put("oak_glass_pane", getId(allPaletteBlocksClass, "OAK_WINDOW_PANE"));
			reMap.put("iron_glass", getId(allPaletteBlocksClass, "ORNATE_IRON_WINDOW"));
			reMap.put("iron_glass_pane", getId(allPaletteBlocksClass, "ORNATE_IRON_WINDOW_PANE"));
			reMap.put("spruce_glass", getId(allPaletteBlocksClass, "SPRUCE_WINDOW"));
			reMap.put("spruce_glass_pane", getId(allPaletteBlocksClass, "SPRUCE_WINDOW_PANE"));

			// 1.14 palettes
			remapPaletteBlock("andesite", "andesite", true);
			remapPaletteBlock("diorite", "diorite", true);
			remapPaletteBlock("granite", "granite", true);
			remapPaletteBlock("limestone", "limestone", false);
			remapPaletteBlock("gabbro", "dripstone", false);
			remapPaletteBlock("scoria", "scoria", false);
			remapPaletteBlock("dark_scoria", "scorchia", false);
			remapPaletteBlock("dolomite", "calcite", false);
			remapPaletteBlock("weathered_limestone", "tuff", false);

			reMap.put("natural_scoria", Create.asResource("scoria"));

			reMap.put("empty_blueprint", getId(allItemsClass, "SCHEMATIC"));
			reMap.put("gold_sheet", getId(allItemsClass, "GOLDEN_SHEET"));
			reMap.put("flour", getId(allItemsClass, "WHEAT_FLOUR"));
			reMap.put("blueprint_and_quill", getId(allItemsClass, "SCHEMATIC_AND_QUILL"));
			reMap.put("slot_cover", getId(allItemsClass, "CRAFTER_SLOT_COVER"));
			reMap.put("blueprint", getId(allItemsClass, "SCHEMATIC"));
			reMap.put("symmetry_wand", getId(allItemsClass, "WAND_OF_SYMMETRY"));
			reMap.put("terrain_zapper", getId(allItemsClass, "WORLDSHAPER"));
			reMap.put("property_filter", getId(allItemsClass, "ATTRIBUTE_FILTER"));
			reMap.put("obsidian_dust", getId(allItemsClass, "POWDERED_OBSIDIAN"));
			reMap.put("diving_helmet", getId(allItemsClass, "COPPER_DIVING_HELMET"));
			reMap.put("diving_boots", getId(allItemsClass, "COPPER_DIVING_BOOTS"));

			// 1.18 crushed ores
			for (String metal : new String[] { "iron", "gold", "copper", "zinc" })
				reMap.put("crushed_" + metal + "_ore", Create.asResource("crushed_raw_" + metal));
			for (CommonMetal compatMetal : CommonMetal.values())
				reMap.put("crushed_" + compatMetal.name + "_ore",
					Create.asResource("crushed_raw_" + compatMetal.name));
		} catch (Exception e) {
			// If classes are not available (e.g., on server), skip the remapping
		}
	}

	private static ResourceLocation getId(Class<?> clazz, String fieldName) throws Exception {
		Object field = clazz.getField(fieldName).get(null);
		Class<?> registryEntryClass = Class.forName("com.tterrag.registrate.util.entry.RegistryEntry");
		return (ResourceLocation) registryEntryClass.getMethod("getId").invoke(field);
	}

	private static void remapPaletteBlock(String type, String newType, boolean vanilla) {
		reMap.put("%s_cobblestone_stairs".formatted(type), Create.asResource("cut_%s_stairs".formatted(newType)));
		reMap.put("%s_cobblestone_slab".formatted(type), Create.asResource("cut_%s_slab".formatted(newType)));
		reMap.put("%s_cobblestone_wall".formatted(type), Create.asResource("cut_%s_wall".formatted(newType)));

		if (!vanilla) {
			if (type != "gabbro")
				reMap.put("%s_cobblestone".formatted(type), Create.asResource("%s".formatted(newType)));
			reMap.put("polished_%s".formatted(type), Create.asResource("polished_cut_%s".formatted(newType)));
			reMap.put("polished_%s_stairs".formatted(type),
				Create.asResource("polished_cut_%s_stairs".formatted(newType)));
			reMap.put("polished_%s_slab".formatted(type), Create.asResource("polished_cut_%s_slab".formatted(newType)));
			reMap.put("polished_%s_wall".formatted(type), Create.asResource("polished_cut_%s_wall".formatted(newType)));
		}

		reMap.put("%s_bricks".formatted(type), Create.asResource("cut_%s_bricks".formatted(newType)));
		reMap.put("%s_bricks_stairs".formatted(type), Create.asResource("cut_%s_brick_stairs".formatted(newType)));
		reMap.put("%s_bricks_slab".formatted(type), Create.asResource("cut_%s_brick_slab".formatted(newType)));
		reMap.put("%s_bricks_wall".formatted(type), Create.asResource("cut_%s_brick_wall".formatted(newType)));
		reMap.put("fancy_%s_bricks".formatted(type), Create.asResource("small_%s_bricks".formatted(newType)));
		reMap.put("fancy_%s_bricks_stairs".formatted(type),
			Create.asResource("small_%s_brick_stairs".formatted(newType)));
		reMap.put("fancy_%s_bricks_slab".formatted(type), Create.asResource("small_%s_brick_slab".formatted(newType)));
		reMap.put("fancy_%s_bricks_wall".formatted(type), Create.asResource("small_%s_brick_wall".formatted(newType)));
		reMap.put("paved_%s".formatted(type), Create.asResource("small_%s_bricks".formatted(newType)));
		reMap.put("paved_%s_stairs".formatted(type), Create.asResource("small_%s_brick_stairs".formatted(newType)));
		reMap.put("paved_%s_slab".formatted(type), Create.asResource("small_%s_brick_slab".formatted(newType)));
		reMap.put("paved_%s_wall".formatted(type), Create.asResource("small_%s_brick_wall".formatted(newType)));

		if (!vanilla)
			reMap.put("chiseled_%s".formatted(type), Create.asResource("polished_cut_%s".formatted(newType)));

		reMap.put("mossy_%s".formatted(type), Create.asResource("cut_%s_bricks".formatted(newType)));
		reMap.put("overgrown_%s".formatted(type), Create.asResource("cut_%s_bricks".formatted(newType)));

		if (!type.equals(newType)) {
			reMap.put("layered_%s".formatted(type), Create.asResource("layered_%s".formatted(newType)));
			reMap.put("%s_pillar".formatted(type), Create.asResource("%s_pillar".formatted(newType)));
		}
	}

	@SubscribeEvent
	public static void remap(RegisterEvent event) {
		Registry<?> registry = event.getRegistry();

		if (registry == Registries.BLOCK || registry == Registries.ITEM) {
			reMap.forEach((string, resourceLocation) -> registry.addAlias(Create.asResource(string), resourceLocation));
		}

		if (registry == Registries.FLUID) {
			registry.addAlias(Create.asResource("milk"), NeoForgeMod.MILK.getId());
			registry.addAlias(Create.asResource("flowing_milk"), NeoForgeMod.FLOWING_MILK.getId());
		}

		if (registry == Registries.BLOCK_ENTITY_TYPE) {
			registry.addAlias(Create.asResource("copper_backtank"), AllBlockEntityTypes.BACKTANK.getId());
			registry.addAlias(Create.asResource("adjustable_pulley"), AllBlockEntityTypes.ADJUSTABLE_CHAIN_GEARSHIFT.getId());
		}
	}
}
