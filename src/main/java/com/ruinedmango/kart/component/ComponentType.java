package com.ruinedmango.kart.component;

public enum ComponentType {
	CHASSIS("chassis"),
	ENGINE("engine"),
	WHEELS("wheels"),
	BOOSTER("booster"),
	ACCESSORY("accessory");

	private final String serializedName;

	ComponentType(String serializedName) {
		this.serializedName = serializedName;
	}

	public String serializedName() {
		return this.serializedName;
	}
}
