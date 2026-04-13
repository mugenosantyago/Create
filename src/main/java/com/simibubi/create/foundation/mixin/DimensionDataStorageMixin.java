package com.simibubi.create.foundation.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.simibubi.create.content.trains.station.StationMapData;
import com.simibubi.create.content.trains.station.StationMarker;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

@Mixin(DimensionDataStorage.class)
public abstract class DimensionDataStorageMixin {

	@Unique
	private static final ThreadLocal<CompoundTag> create$PENDING_MAP_ROOT = new ThreadLocal<>();

	@Unique
	private static final String create$STATION_MARKERS_KEY = "create:stations";

	@Shadow
	@Final
	private HolderLookup.Provider registries;

	@Inject(method = "readSavedData(Lnet/minecraft/world/level/saveddata/SavedDataType;)Lnet/minecraft/world/level/saveddata/SavedData;", at = @At("HEAD"))
	private void create$clearPendingMapRoot(CallbackInfoReturnable<SavedData> cir) {
		create$PENDING_MAP_ROOT.remove();
	}

	@Inject(
			method = "readSavedData(Lnet/minecraft/world/level/saveddata/SavedDataType;)Lnet/minecraft/world/level/saveddata/SavedData;",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/serialization/Codec;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;",
					shift = At.Shift.BEFORE
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private <T extends SavedData> void create$captureMapRootBeforeCodecParse(
			SavedDataType<T> type,
			CallbackInfoReturnable<T> cir,
			java.nio.file.Path path,
			CompoundTag rootTag,
			RegistryOps<Tag> registryOps
	) {
		if (type.dataFixType() == DataFixTypes.SAVED_DATA_MAP_DATA) {
			create$PENDING_MAP_ROOT.set(rootTag);
		}
	}

	@Inject(method = "readSavedData(Lnet/minecraft/world/level/saveddata/SavedDataType;)Lnet/minecraft/world/level/saveddata/SavedData;", at = @At("RETURN"))
	private <T extends SavedData> void create$loadMapStationMarkers(SavedDataType<T> type, CallbackInfoReturnable<T> cir) {
		CompoundTag root = create$PENDING_MAP_ROOT.get();
		create$PENDING_MAP_ROOT.remove();
		SavedData data = cir.getReturnValue();
		if (root == null || !(data instanceof MapItemSavedData mapData)) {
			return;
		}
		StationMapData stationMapData = (StationMapData) mapData;
		ListTag listTag = root.getListOrEmpty(create$STATION_MARKERS_KEY);
		for (int i = 0; i < listTag.size(); ++i) {
			StationMarker stationMarker = StationMarker.load(listTag.getCompoundOrEmpty(i), registries);
			stationMapData.addStationMarker(stationMarker);
		}
	}

	@Inject(
			method = "encodeUnchecked",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/nbt/NbtUtils;addCurrentDataVersion(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/nbt/CompoundTag;",
					shift = At.Shift.AFTER
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private <T extends SavedData> void create$saveMapStationMarkers(
			SavedDataType<T> type,
			T data,
			RegistryOps<Tag> ops,
			CallbackInfoReturnable<CompoundTag> cir,
			com.mojang.serialization.Codec<?> codec,
			CompoundTag root
	) {
		if (!(data instanceof MapItemSavedData)) {
			return;
		}
		StationMapData stationMapData = (StationMapData) data;
		ListTag listTag = new ListTag();
		for (StationMarker stationMarker : stationMapData.getStationMarkersForPersistence()) {
			listTag.add(stationMarker.save(registries));
		}
		root.put(create$STATION_MARKERS_KEY, listTag);
	}
}
