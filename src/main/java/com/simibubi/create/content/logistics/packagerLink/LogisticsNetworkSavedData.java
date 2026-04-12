package com.simibubi.create.content.logistics.packagerLink;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.simibubi.create.Create;

import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class LogisticsNetworkSavedData extends SavedData {

	public static final SavedDataType<LogisticsNetworkSavedData> TYPE = new SavedDataType<>(
		"create_logistics",
		ctx -> new LogisticsNetworkSavedData(),
		ctx -> CompoundTag.CODEC.xmap(
			tag -> LogisticsNetworkSavedData.load(tag, ctx.levelOrThrow().registryAccess()),
			data -> data.writeToNbt(new CompoundTag(), ctx.levelOrThrow().registryAccess())
		)
	);

	private Map<UUID, LogisticsNetwork> logisticsNetworks = new HashMap<>();

	private CompoundTag writeToNbt(CompoundTag nbt, HolderLookup.Provider registries) {
		GlobalLogisticsManager logistics = Create.LOGISTICS;
		nbt.put("LogisticsNetworks",
			NBTHelper.writeCompoundList(logistics.logisticsNetworks.values(), network -> network.write(registries)));
		return nbt;
	}

	private static LogisticsNetworkSavedData load(CompoundTag nbt, HolderLookup.Provider registries) {
		LogisticsNetworkSavedData sd = new LogisticsNetworkSavedData();
		sd.logisticsNetworks = new HashMap<>();
		NBTHelper.iterateCompoundList(nbt.getListOrEmpty("LogisticsNetworks"), c -> {
			LogisticsNetwork network = LogisticsNetwork.read(c, registries);
			sd.logisticsNetworks.put(network.id, network);
		});
		return sd;
	}

	public Map<UUID, LogisticsNetwork> getLogisticsNetworks() {
		return logisticsNetworks;
	}

	private LogisticsNetworkSavedData() {}

	public static LogisticsNetworkSavedData load(MinecraftServer server) {
		return server.overworld()
			.getDataStorage()
			.computeIfAbsent(TYPE);
	}

}
