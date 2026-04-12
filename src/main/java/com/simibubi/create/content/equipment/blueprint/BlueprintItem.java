package com.simibubi.create.content.equipment.blueprint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.filter.AttributeFilterWhitelistMode;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute.ItemAttributeEntry;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.InTagAttribute;
import com.simibubi.create.foundation.item.ItemHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BlueprintItem extends Item {

	public BlueprintItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Direction face = ctx.getClickedFace();
		Player player = ctx.getPlayer();
		ItemStack stack = ctx.getItemInHand();
		BlockPos pos = ctx.getClickedPos()
			.relative(face);

		if (player != null && !player.mayUseItemAt(pos, face, stack))
			return InteractionResult.FAIL;

		Level world = ctx.getLevel();
		HangingEntity hangingentity = new BlueprintEntity(world, pos, face, face.getAxis()
			.isHorizontal() ? Direction.DOWN : ctx.getHorizontalDirection());
		CustomData customData = stack.get(DataComponents.CUSTOM_DATA);

		if (customData != null)
			EntityType.updateCustomEntityTag(world, player, hangingentity, customData);
		if (!hangingentity.survives())
			return InteractionResult.CONSUME;
		if (!world.isClientSide) {
			hangingentity.playPlacementSound();
			world.addFreshEntity(hangingentity);
		}

		stack.shrink(1);
		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	public static void assignCompleteRecipe(Level level, ItemStackHandler inv, Recipe<?> recipe) {
		java.util.List<Ingredient> ingredients = recipe.placementInfo().ingredients();

		for (int i = 0; i < 9; i++)
			inv.setStackInSlot(i, ItemStack.EMPTY);
		inv.setStackInSlot(9, recipe.assemble(null, null));

		if (recipe instanceof ShapedRecipe shapedRecipe) {
			for (int row = 0; row < shapedRecipe.getHeight(); row++)
				for (int col = 0; col < shapedRecipe.getWidth(); col++)
					inv.setStackInSlot(row * 3 + col,
						convertIngredientToFilter(ingredients.get(row * shapedRecipe.getWidth() + col)));
		} else {
			for (int i = 0; i < ingredients.size(); i++)
				inv.setStackInSlot(i, convertIngredientToFilter(ingredients.get(i)));
		}
	}

	private static ItemStack convertIngredientToFilter(Ingredient ingredient) {
		// In MC 1.21.8, Ingredient.values was removed; use items() stream instead
		java.util.List<ItemStack> itemStacks = ingredient.items()
			.map(holder -> new ItemStack(holder.value()))
			.toList();
		if (itemStacks.isEmpty() || itemStacks.size() > 18)
			return ItemStack.EMPTY;
		if (itemStacks.size() == 1)
			return itemStacks.get(0);

		ItemStack result = AllItems.FILTER.asStack();
		ItemStackHandler filterItems = AllItems.FILTER.get().getFilterItemHandler(result);
		for (int i = 0; i < itemStacks.size(); i++)
			filterItems.setStackInSlot(i, itemStacks.get(i));
		result.set(AllDataComponents.FILTER_ITEMS, ItemHelper.containerContentsFromHandler(filterItems));
		return result;
	}

	@SuppressWarnings("unused")
	private static ItemStack convertIItemListToFilter(Object itemList, boolean isCompoundIngredient) {
		// Stub: Ingredient.Value, ItemValue, TagValue removed in MC 1.21.8
		return ItemStack.EMPTY;
	}

}
