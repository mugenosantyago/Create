package com.simibubi.create.foundation.data;

import java.util.concurrent.CompletableFuture;


import com.simibubi.create.AllDamageTypes;
import com.simibubi.create.Create;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;

public class DamageTypeTagGen extends TagsProvider<DamageType> {
	public DamageTypeTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, Registries.DAMAGE_TYPE, lookupProvider, Create.ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		TagAppender.forBuilder(getOrCreateRawBuilder(DamageTypeTags.BYPASSES_ARMOR))
				.add(AllDamageTypes.CRUSH, AllDamageTypes.FAN_FIRE, AllDamageTypes.FAN_LAVA, AllDamageTypes.DRILL, AllDamageTypes.SAW);
		TagAppender.forBuilder(getOrCreateRawBuilder(DamageTypeTags.IS_FIRE))
				.add(AllDamageTypes.FAN_FIRE, AllDamageTypes.FAN_LAVA);
		TagAppender.forBuilder(getOrCreateRawBuilder(DamageTypeTags.IS_EXPLOSION))
				.add(AllDamageTypes.CUCKOO_SURPRISE);
	}

	@Override
	public String getName() {
		return "Create's Damage Type Tags";
	}
}
