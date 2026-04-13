package com.simibubi.create.foundation.particle;

import net.minecraft.core.particles.ParticleOptions;

/**
 * Marker for particles that use sprite sets on the client. Registration is handled in
 * {@link CreateParticleProviders}.
 */
public interface ICustomParticleDataWithSprite<T extends ParticleOptions> extends ICustomParticleData<T> {}
