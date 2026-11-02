package com.ruinedmango.kart.client;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;

import org.jspecify.annotations.Nullable;

public final class KartRenderState extends EntityRenderState {
	public float yRot;
	public float steering;
	public float wheelRotation;
	public float wheelRadius = 0.35F;
	public float suspension;
	public float pitch;
	public float roll;
	public float suspensionFL;
	public float suspensionFR;
	public float suspensionRL;
	public float suspensionRR;
	public @Nullable Identifier chassis;
	public @Nullable Identifier engine;
	public @Nullable Identifier wheels;
	public @Nullable Identifier booster;
	public @Nullable Identifier accessory1;
	public @Nullable Identifier accessory2;
}
