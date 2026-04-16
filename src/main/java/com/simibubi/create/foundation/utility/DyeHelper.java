package com.simibubi.create.foundation.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.createmod.catnip.data.Couple;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class DyeHelper {

	public static ItemLike getWoolOfDye(DyeColor color) {
		return WOOL_TABLE.getOrDefault(color, () -> Blocks.WHITE_WOOL).get();
	}

	public static Couple<Integer> getDyeColors(DyeColor color){
		return DYE_TABLE.getOrDefault(color, DYE_TABLE.get(DyeColor.WHITE));
	}

	/**
	 * Adds a dye color s.t. Create's blocks can use it instead of defaulting to white.
	 * @param color Dye color to add
	 * @param brightColor Front (bright) RGB color
	 * @param darkColor Back (dark) RGB color
	 * @param wool Supplier of wool item/block corresponding to the color
	 */
	public static void addDye(DyeColor color, Integer brightColor, Integer darkColor, Supplier<ItemLike> wool){
		DYE_TABLE.put(color, Couple.create(brightColor, darkColor));
		WOOL_TABLE.put(color, wool);
	}

	private static void addDye(DyeColor color, Integer brightColor, Integer darkColor, ItemLike wool){
		addDye(color, brightColor, darkColor, () -> wool);
	}

	private static final Map<DyeColor, Supplier<ItemLike>> WOOL_TABLE = new HashMap<>();

	private static final Map<DyeColor, Couple<Integer>> DYE_TABLE = new HashMap<>();

	static {
		// DyeColor, ( Front RGB, Back RGB )
		addDye(DyeColor.BLACK, 0xFF45403B, 0xFF21201F, Blocks.BLACK_WOOL);
		addDye(DyeColor.RED, 0xFFB13937, 0xFF632737, Blocks.RED_WOOL);
		addDye(DyeColor.GREEN, 0xFF208A46, 0xFF1D6045, Blocks.GREEN_WOOL);
		addDye(DyeColor.BROWN, 0xFFAC855C, 0xFF68533E, Blocks.BROWN_WOOL);

		addDye(DyeColor.BLUE, 0xFF5391E1, 0xFF504B90, Blocks.BLUE_WOOL);
		addDye(DyeColor.GRAY, 0xFF5D666F, 0xFF313538, Blocks.GRAY_WOOL);
		addDye(DyeColor.LIGHT_GRAY, 0xFF95969B, 0xFF707070, Blocks.LIGHT_GRAY_WOOL);
		addDye(DyeColor.PURPLE, 0xFF9F54AE, 0xFF63366C, Blocks.PURPLE_WOOL);

		addDye(DyeColor.CYAN, 0xFF3EABB4, 0xFF3C7872, Blocks.CYAN_WOOL);
		addDye(DyeColor.PINK, 0xFFD5A8CB, 0xFFB86B95, Blocks.PINK_WOOL);
		addDye(DyeColor.LIME, 0xFFA3DF55, 0xFF4FB16F, Blocks.LIME_WOOL);
		addDye(DyeColor.YELLOW, 0xFFE6D756, 0xFFE9AC29, Blocks.YELLOW_WOOL);

		addDye(DyeColor.LIGHT_BLUE, 0xFF69CED2, 0xFF508AA5, Blocks.LIGHT_BLUE_WOOL);
		addDye(DyeColor.ORANGE, 0xFFEE9246, 0xFFD94927, Blocks.ORANGE_WOOL);
		addDye(DyeColor.MAGENTA, 0xFFF062B0, 0xFFC04488, Blocks.MAGENTA_WOOL);
		addDye(DyeColor.WHITE, 0xFFEDEAE5, 0xFFBBB6B0, Blocks.WHITE_WOOL);
	}
}
