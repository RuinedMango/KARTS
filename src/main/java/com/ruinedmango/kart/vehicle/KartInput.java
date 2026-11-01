package com.ruinedmango.kart.vehicle;

public record KartInput(float throttle, float steering, boolean handbrake, boolean boost) {
	public static final KartInput NEUTRAL = new KartInput(0.0F, 0.0F, false, false);
}
