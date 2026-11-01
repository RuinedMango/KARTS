package com.ruinedmango.kart.component;

import com.ruinedmango.kart.component.definitions.KartComponentDefinitions;

import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class KartComponents {
	private static final Map<Identifier, ComponentDefinition> COMPONENTS = new LinkedHashMap<>();
	private static boolean loaded;

	private KartComponents() {
	}

	public static Optional<ComponentDefinition> byId(Identifier id) {
		ensureLoaded();
		return Optional.ofNullable(COMPONENTS.get(id));
	}

	public static List<ComponentDefinition> all() {
		ensureLoaded();
		return List.copyOf(COMPONENTS.values());
	}

	public static List<ComponentDefinition> byType(ComponentType type) {
		ensureLoaded();
		return COMPONENTS.values().stream().filter(definition -> definition.type() == type).toList();
	}

	public static void register(ComponentDefinition definition) {
		ComponentDefinition previous = COMPONENTS.putIfAbsent(definition.id(), definition);
		if (previous != null) {
			throw new IllegalStateException("Duplicate kart component id: " + definition.id());
		}
	}

	private static void ensureLoaded() {
		if (!loaded) {
			loaded = true;
			KartComponentDefinitions.register();
		}
	}
}
