package com.simibubi.create.foundation.utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import com.simibubi.create.Create;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class DynamicComponent {

	private JsonElement rawCustomText;
	private Component parsedCustomText;

	public DynamicComponent() {}

	public void displayCustomText(Level level, BlockPos pos, String tagElement) {
		if (tagElement == null)
			return;

		rawCustomText = getJsonFromString(tagElement);
		parsedCustomText = parseCustomText(level, pos, rawCustomText);
	}

	public boolean sameAs(String tagElement) {
		return isValid() && rawCustomText.equals(getJsonFromString(tagElement));
	}

	public boolean isValid() {
		return parsedCustomText != null && rawCustomText != null;
	}

	public String resolve() {
		return parsedCustomText.getString();
	}

	public MutableComponent get() {
		return parsedCustomText == null ? Component.empty() : parsedCustomText.copy();
	}

	public void read(BlockPos pos, CompoundTag nbt, HolderLookup.Provider registries) {
		rawCustomText = getJsonFromString(nbt.getStringOr("RawCustomText", ""));
		try {
			parsedCustomText = ComponentSerialization.CODEC.parse(net.minecraft.resources.RegistryOps.create(net.minecraft.nbt.NbtOps.INSTANCE, registries), net.minecraft.nbt.StringTag.valueOf(nbt.getStringOr("CustomText", ""))).result().orElse(null);
		} catch (JsonParseException e) {
			parsedCustomText = null;
		}
	}

	public void write(CompoundTag nbt, HolderLookup.Provider registries) {
		if (!isValid())
			return;

		nbt.putString("RawCustomText", rawCustomText.toString());
		nbt.putString("CustomText", ComponentSerialization.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, parsedCustomText).result().map(Object::toString).orElse(""));
	}

	public static JsonElement getJsonFromString(String string) {
		try {
			return JsonParser.parseString(string);
		} catch (JsonParseException e) {
			return null;
		}
	}

	public static Component parseCustomText(Level level, BlockPos pos, JsonElement customText) {
		if (!(level instanceof ServerLevel serverLevel))
			return null;
		try {
			return ComponentUtils.updateForEntity(getCommandSource(serverLevel, pos),
				ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, customText).result().orElse(null), null, 0);
		} catch (JsonParseException | CommandSyntaxException e) {
			return null;
		}
	}

	/** Resolves selectors and components against a block position (display links, etc.). */
	public static Component parseCustomText(Level level, BlockPos pos, Component customText) {
		if (!(level instanceof ServerLevel serverLevel))
			return null;
		try {
			return ComponentUtils.updateForEntity(getCommandSource(serverLevel, pos), customText, null, 0);
		} catch (JsonParseException | CommandSyntaxException e) {
			return null;
		}
	}

	public static CommandSourceStack getCommandSource(ServerLevel level, BlockPos pos) {
		return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(pos), Vec2.ZERO, level, 2, Create.ID,
			Component.literal(Create.ID), level.getServer(), null);
	}

}
