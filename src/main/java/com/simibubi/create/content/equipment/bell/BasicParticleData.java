package com.simibubi.create.content.equipment.bell;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Stateless particle option types (unit codecs). Client sprite factories are registered in
 * {@link com.simibubi.create.foundation.particle.CreateParticleProviders}.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BasicParticleData implements ParticleOptions, ICustomParticleDataWithSprite<BasicParticleData> {

	public BasicParticleData() {}

	@Override
	public abstract ParticleType<?> getType();

	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, BasicParticleData> getStreamCodec() {
		return StreamCodec.unit(this);
	}

	@Override
	public MapCodec<BasicParticleData> getCodec(ParticleType<BasicParticleData> type) {
		return MapCodec.unit(this);
	}
}
