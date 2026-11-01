package com.ruinedmango.kart.component;

import org.jspecify.annotations.Nullable;

public enum ComponentSlot {
	CHASSIS("chassis", ComponentType.CHASSIS),
	ENGINE("engine", ComponentType.ENGINE),
	WHEELS("wheels", ComponentType.WHEELS),
	BOOSTER("booster", ComponentType.BOOSTER),
	ACCESSORY_1("accessory_1", ComponentType.ACCESSORY),
	ACCESSORY_2("accessory_2", ComponentType.ACCESSORY);

	private final String serializedName;
	private final ComponentType type;

	ComponentSlot(String serializedName, ComponentType type) {
		this.serializedName = serializedName;
		this.type = type;
	}

	public String serializedName() {
		return this.serializedName;
	}

	public ComponentType type() {
		return this.type;
	}

	public boolean accepts(ComponentType candidate) {
		return this.type == candidate;
	}

	public String saveKey() {
		return "component_" + this.serializedName;
	}

	public static @Nullable ComponentSlot byName(String name) {
		for (ComponentSlot slot : values()) {
			if (slot.serializedName.equalsIgnoreCase(name)) {
				return slot;
			}
		}
		return null;
	}
}
