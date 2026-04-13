package com.simibubi.create.content.equipment.bell;

import com.simibubi.create.AllParticleTypes;

import net.minecraft.core.particles.ParticleType;

public class SoulBaseParticleData extends BasicParticleData {

	@Override
	public ParticleType<?> getType() {
		return AllParticleTypes.SOUL_BASE.get();
	}
}
