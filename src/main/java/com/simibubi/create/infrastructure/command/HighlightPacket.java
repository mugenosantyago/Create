package com.simibubi.create.infrastructure.command;

import net.minecraft.world.entity.player.Player;

import com.simibubi.create.AllPackets;
import com.simibubi.create.AllSpecialTextures;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.shapes.Shapes;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record HighlightPacket(BlockPos pos) implements ClientboundPacketPayload {
	public static final StreamCodec<ByteBuf, HighlightPacket> STREAM_CODEC = BlockPos.STREAM_CODEC.map(HighlightPacket::new, p -> p.pos);

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handle(Player player) {
		if (!player.level().isLoaded(pos)) {
			return;
		}

		Outliner.getInstance().showAABB("highlightCommand", Shapes.block().bounds().move(pos), 200)
			.lineWidth(1 / 32f)
			.colored(0xFFEeEeEe)
			// .colored(0xFF243B50)
			.withFaceTexture(AllSpecialTextures.SELECTION);
	}

	@Override
	public PacketTypeProvider getTypeProvider() {
		return AllPackets.BLOCK_HIGHLIGHT;
	}
}
