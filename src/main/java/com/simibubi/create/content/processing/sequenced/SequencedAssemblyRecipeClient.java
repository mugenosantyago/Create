package com.simibubi.create.content.processing.sequenced;

import java.util.List;
import java.util.Optional;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * Client-only tooltip wiring kept out of {@link SequencedAssemblyRecipe} so that class does not
 * reference {@link net.minecraft.client.multiplayer.ClientLevel} in its constant pool (dedicated
 * servers load recipe classes during codec registration).
 */
@OnlyIn(Dist.CLIENT)
public final class SequencedAssemblyRecipeClient {

	private SequencedAssemblyRecipeClient() {
	}

	public static void addToTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		if (!stack.has(AllDataComponents.SEQUENCED_ASSEMBLY))
			return;
		SequencedAssemblyRecipe.SequencedAssembly sequencedAssembly = stack.get(AllDataComponents.SEQUENCED_ASSEMBLY);
		@SuppressWarnings({"RedundantCast", "DataFlowIssue"}) // The java compiler thinks `byKey` returns an Optional<RecipeHolder<?>>
		Optional<RecipeHolder<? extends Recipe<?>>> optionalRecipe =
			(Optional<RecipeHolder<?>>) com.simibubi.create.foundation.utility.RecipeCompat.getRecipeManager(Minecraft.getInstance().level)
				.byKey(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, sequencedAssembly.id()));
		if (optionalRecipe.isEmpty())
			return;
		Recipe<?> recipe = optionalRecipe.get().value();
		if (!(recipe instanceof SequencedAssemblyRecipe sequencedAssemblyRecipe))
			return;

		List<?> seq = sequencedAssemblyRecipe.getSequence();
		int length = seq.size();
		int step = sequencedAssemblyRecipe.getStep(stack);
		int total = length * sequencedAssemblyRecipe.loops;
		List<Component> tooltip = event.getToolTip();
		tooltip.add(CommonComponents.EMPTY);
		tooltip.add(CreateLang.translateDirect("recipe.sequenced_assembly")
			.withStyle(ChatFormatting.GRAY));
		tooltip.add(CreateLang.translateDirect("recipe.assembly.progress", step, total)
			.withStyle(ChatFormatting.DARK_GRAY));

		int remaining = total - step;
		for (int i = 0; i < length; i++) {
			if (i >= remaining)
				break;
			SequencedRecipe<?> sequencedRecipe = (SequencedRecipe<?>) seq.get(
				(i + step) % length);
			Component textComponent = sequencedRecipe.getAsAssemblyRecipe()
				.getDescriptionForAssembly();
			if (i == 0)
				tooltip.add(CreateLang.translateDirect("recipe.assembly.next", textComponent)
					.withStyle(ChatFormatting.AQUA));
			else {
				tooltip.add(Component.literal("-> ").append(textComponent)
					.withStyle(ChatFormatting.DARK_AQUA));
			}
		}
	}
}
