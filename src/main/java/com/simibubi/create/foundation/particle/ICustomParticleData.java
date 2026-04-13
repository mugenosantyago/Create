package com.simibubi.create.foundation.particle;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import org.jetbrains.annotations.NotNull;

/**
 * Particle network/codec registration only. Client factories live in {@link CreateParticleProviders} so dedicated
 * servers never resolve {@code net.minecraft.client.particle.*} while loading Create.
 */
public interface ICustomParticleData<T extends ParticleOptions> {

	MapCodec<T> getCodec(ParticleType<T> type);

	StreamCodec<? super RegistryFriendlyByteBuf, T> getStreamCodec();

	default ParticleType<T> createType() {
		return new ParticleType<>(false) {

			@Override
			public @NotNull MapCodec<T> codec() {
				return ICustomParticleData.this.getCodec(this);
			}

			@Override
			public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
				return ICustomParticleData.this.getStreamCodec();
			}
		};
	}
}
