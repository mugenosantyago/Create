package com.simibubi.create.content.logistics.packagerLink;

import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.equipment.bell.BasicParticleData;

import net.minecraft.core.particles.ParticleType;

public class WiFiParticleData extends BasicParticleData {

	@Override
	public ParticleType<?> getType() {
		return AllParticleTypes.WIFI.get();
	}
}
