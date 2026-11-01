package com.ruinedmango.kart.component;

import net.minecraft.resources.Identifier;

public record ComponentDefinition(
		Identifier id,
		ComponentType type,
		String translationKey,
		ComponentStats stats) {
}
