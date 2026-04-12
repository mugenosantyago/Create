package com.simibubi.create.content.fluids.spout;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class FillingBySpout {

	public static boolean canItemBeFilled(Level world, ItemStack stack) {
		SingleRecipeInput input = new SingleRecipeInput(stack);

		Optional<RecipeHolder<FillingRecipe>> assemblyRecipe =
			SequencedAssemblyRecipe.getRecipe(world, input, AllRecipeTypes.FILLING.getType(), FillingRecipe.class);
		if (assemblyRecipe.isPresent())
			return true;

		if (AllRecipeTypes.FILLING.find(input, world)
			.isPresent())
			return true;
		return GenericItemFilling.canItemBeFilled(world, stack);
	}

	public static int getRequiredAmountForItem(Level world, ItemStack stack, FluidStack availableFluid) {
		SingleRecipeInput input = new SingleRecipeInput(stack);

		Optional<RecipeHolder<FillingRecipe>> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(world, input,
			AllRecipeTypes.FILLING.getType(), FillingRecipe.class, matchItemAndFluid(world, availableFluid, input));
		if (assemblyRecipe.isPresent()) {
			SizedFluidIngredient requiredFluid = assemblyRecipe.get().value()
				.getRequiredFluid();
			if (requiredFluid.ingredient().test(availableFluid))
				return requiredFluid.amount();
		}

		for (RecipeHolder<?> recipe : com.simibubi.create.foundation.utility.RecipeCompat.getRecipeManager(world)
			.getRecipes()) {
			if (recipe.value().getType() != AllRecipeTypes.FILLING.getType())
				continue;
			FillingRecipe fillingRecipe = (FillingRecipe) recipe.value();
			if (!fillingRecipe.matches(input, world))
				continue;
			SizedFluidIngredient requiredFluid = fillingRecipe.getRequiredFluid();
			if (requiredFluid.ingredient().test(availableFluid))
				return requiredFluid.amount();
		}
		return GenericItemFilling.getRequiredAmountForItem(world, stack, availableFluid);
	}

	public static ItemStack fillItem(Level level, int requiredAmount, ItemStack stack, FluidStack availableFluid) {
		FluidStack toFill = availableFluid.copy();
		toFill.setAmount(requiredAmount);

		SingleRecipeInput input = new SingleRecipeInput(stack);

		RecipeHolder<FillingRecipe> fillingRecipe = SequencedAssemblyRecipe
			.getRecipe(level, input, AllRecipeTypes.FILLING.getType(), FillingRecipe.class,
				matchItemAndFluid(level, availableFluid, input))
			.filter(fr -> fr.value().getRequiredFluid()
					.test(toFill))
				.orElseGet(() -> {
					for (RecipeHolder<?> recipe : com.simibubi.create.foundation.utility.RecipeCompat.getRecipeManager(level)
						.getRecipes()) {
						if (recipe.value().getType() != AllRecipeTypes.FILLING.getType())
							continue;
						FillingRecipe fr = (FillingRecipe) recipe.value();
						if (!fr.matches(input, level))
							continue;
						SizedFluidIngredient requiredFluid = fr.getRequiredFluid();
						if (requiredFluid.test(toFill))
							return new RecipeHolder<>(recipe.id(), fr);
					}
					return null;
				});

		if (fillingRecipe != null) {
			List<ItemStack> results = fillingRecipe.value().rollResults(level.random);
			availableFluid.shrink(requiredAmount);
			stack.shrink(1);
			return results.isEmpty() ? ItemStack.EMPTY : results.get(0);
		}

		return GenericItemFilling.fillItem(level, requiredAmount, stack, availableFluid);
	}

	private static Predicate<RecipeHolder<FillingRecipe>> matchItemAndFluid(Level world, FluidStack availableFluid, SingleRecipeInput input) {
		return r -> r.value().matches(input, world) && r.value().getRequiredFluid()
			.test(availableFluid);
	}

}
