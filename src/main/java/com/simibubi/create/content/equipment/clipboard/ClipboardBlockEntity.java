package com.simibubi.create.content.equipment.clipboard;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.AddressEditBoxHelper;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ClipboardBlockEntity extends SmartBlockEntity {
	private UUID lastEdit;

	public ClipboardBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void initialize() {
		super.initialize();
		updateWrittenState();
	}

	public void onEditedBy(Player player) {
		lastEdit = player.getUUID();
		notifyUpdate();
		updateWrittenState();
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (level.isClientSide())
			CatnipServices.PLATFORM.executeOnClientOnly(() -> this::advertiseToAddressHelper);
	}

	public void updateWrittenState() {
		BlockState blockState = getBlockState();
		if (!AllBlocks.CLIPBOARD.has(blockState))
			return;
		if (level.isClientSide())
			return;
		boolean isWritten = blockState.getValue(ClipboardBlock.WRITTEN);
		boolean shouldBeWritten = components().has(AllDataComponents.CLIPBOARD_CONTENT);
		if (isWritten == shouldBeWritten)
			return;
		level.setBlockAndUpdate(worldPosition, blockState.setValue(ClipboardBlock.WRITTEN, shouldBeWritten));
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);

		if (clientPacket) {
			DataComponentMap.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), components())
				.result()
				.ifPresent(encoded -> tag.put("components", encoded));

			if (lastEdit != null)
				tag.putIntArray("LastEdit", net.minecraft.core.UUIDUtil.uuidToIntArray(lastEdit));
		}
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);

		if (clientPacket) {
			if (tag.contains("components"))
				DataComponentMap.CODEC.decode(registries.createSerializationContext(NbtOps.INSTANCE), tag.getCompoundOrEmpty("components"))
					.result()
					.map(Pair::getFirst)
					.ifPresent(this::setComponents);

			CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> readClientSide(tag));
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void readClientSide(CompoundTag tag) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen == null
			|| !"com.simibubi.create.content.equipment.clipboard.ClipboardScreen".equals(mc.screen.getClass().getName()))
			return;
		Object screen = mc.screen;
		if (tag.contains("LastEdit") && tag.getIntArray("LastEdit").map(net.minecraft.core.UUIDUtil::uuidFromIntArray).orElse(null)
			.equals(mc.player.getUUID()))
			return;
		try {
			BlockPos targeted = (BlockPos) screen.getClass().getField("targetedBlock").get(screen);
			if (!worldPosition.equals(targeted))
				return;
			Method reopenWith = screen.getClass().getMethod("reopenWith", ClipboardContent.class);
			reopenWith.invoke(screen, components().getOrDefault(AllDataComponents.CLIPBOARD_CONTENT, ClipboardContent.EMPTY));
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void advertiseToAddressHelper() {
		AddressEditBoxHelper.advertiseClipboard(this);
	}

	@Override
	public void setComponents(DataComponentMap components) {
		super.setComponents(components);
	}
}
