package com.ruinedmango.kart.client;

import com.ruinedmango.kart.KART;
import com.ruinedmango.kart.component.KartComponentIds;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class KartModel extends EntityModel<KartRenderState> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			Identifier.fromNamespaceAndPath(KART.MODID, "kart"), "main");
	private static final float WHEEL_BASE_Y = 2.0F;
	private static final float BODY_BASE_Y = -2.0F;

	private final ModelPart body;
	private final ModelPart fallbackChassis;
	private final ModelPart frontLeftWheel;
	private final ModelPart frontRightWheel;
	private final ModelPart rearLeftWheel;
	private final ModelPart rearRightWheel;
	private final ModelPart suspensionFrontLeft;
	private final ModelPart suspensionFrontRight;
	private final ModelPart suspensionRearLeft;
	private final ModelPart suspensionRearRight;

	private final Map<Identifier, ModelPart> chassisModels = new HashMap<>();
	private final Map<Identifier, ModelPart> engineModels = new HashMap<>();
	private final Map<Identifier, ModelPart> boosterModels = new HashMap<>();
	private final Map<Identifier, ModelPart> accessoryModels = new HashMap<>();

	public KartModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.fallbackChassis = this.body.getChild("chassis_fallback");
		this.frontLeftWheel = root.getChild("front_left_wheel");
		this.frontRightWheel = root.getChild("front_right_wheel");
		this.rearLeftWheel = root.getChild("rear_left_wheel");
		this.rearRightWheel = root.getChild("rear_right_wheel");
		this.suspensionFrontLeft = root.getChild("suspension_front_left");
		this.suspensionFrontRight = root.getChild("suspension_front_right");
		this.suspensionRearLeft = root.getChild("suspension_rear_left");
		this.suspensionRearRight = root.getChild("suspension_rear_right");

		register(this.chassisModels, KartComponentIds.SHOPPING_CART_FRAME, this.body, "chassis_shopping_cart");
		register(this.chassisModels, KartComponentIds.BUDGET_GO_KART_FRAME, this.body, "chassis_budget_go_kart");
		register(this.chassisModels, KartComponentIds.LAWNMOWER_FRAME, this.body, "chassis_lawnmower");

		register(this.engineModels, KartComponentIds.ELECTRIC_MOTOR, this.body, "engine_electric_motor");
		register(this.engineModels, KartComponentIds.LAWNMOWER_ENGINE, this.body, "engine_lawnmower_engine");
		register(this.engineModels, KartComponentIds.QUESTIONABLE_V8, this.body, "engine_questionable_v8");
		register(this.engineModels, KartComponentIds.DIESEL_ENGINE, this.body, "engine_diesel");

		register(this.boosterModels, KartComponentIds.NOS_TANK, this.body, "booster_nos_tank");
		register(this.boosterModels, KartComponentIds.WICKET_NITRO_TANK, this.body, "booster_wicket_tank");

		register(this.accessoryModels, KartComponentIds.SPOILER, this.body, "accessory_spoiler");
		register(this.accessoryModels, KartComponentIds.HORN, this.body, "accessory_horn");
		register(this.accessoryModels, KartComponentIds.BUMPER, this.body, "accessory_bumper");
		register(this.accessoryModels, KartComponentIds.TIGHT_TURN_KIT, this.body, "accessory_tight_turn_kit");
		register(this.accessoryModels, KartComponentIds.WIDE_TURN_KIT, this.body, "accessory_wide_turn_kit");
	}

	public static LayerDefinition createBodyLayer() {
		return KartModelLayer.createBodyLayer();
	}

	private static CubeListBuilder wheel() {
		return CubeListBuilder.create()
				.texOffs(32, 64).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F)
				.texOffs(32, 64).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F);
	}

	private static CubeListBuilder suspensionArm(float x) {
		return CubeListBuilder.create()
				.texOffs(112, 64).addBox(x, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F);
	}

	@Override
	public void setupAnim(KartRenderState state) {
		super.setupAnim(state);

		this.body.y = BODY_BASE_Y + state.suspension * 6.0F;
		this.frontLeftWheel.y = WHEEL_BASE_Y + state.suspensionFL * 16.0F;
		this.frontRightWheel.y = WHEEL_BASE_Y + state.suspensionFR * 16.0F;
		this.rearLeftWheel.y = WHEEL_BASE_Y + state.suspensionRL * 16.0F;
		this.rearRightWheel.y = WHEEL_BASE_Y + state.suspensionRR * 16.0F;
		updateSuspensionArm(this.suspensionFrontLeft, state.suspensionFL, true);
		updateSuspensionArm(this.suspensionFrontRight, state.suspensionFR, false);
		updateSuspensionArm(this.suspensionRearLeft, state.suspensionRL, true);
		updateSuspensionArm(this.suspensionRearRight, state.suspensionRR, false);

		applyChassis(state.chassis);
		applySingle(this.engineModels, state.engine);
		applySingle(this.boosterModels, state.booster);
		applyAccessories(state.accessory1, state.accessory2);

		float wheelScale = Mth.clamp(state.wheelRadius / 0.35F, 0.55F, 1.45F);
		boolean hasWheels = state.wheels != null;
		this.frontLeftWheel.visible = hasWheels;
		this.frontRightWheel.visible = hasWheels;
		this.rearLeftWheel.visible = hasWheels;
		this.rearRightWheel.visible = hasWheels;
		this.suspensionFrontLeft.visible = hasWheels;
		this.suspensionFrontRight.visible = hasWheels;
		this.suspensionRearLeft.visible = hasWheels;
		this.suspensionRearRight.visible = hasWheels;

		setWheelScale(this.frontLeftWheel, wheelScale);
		setWheelScale(this.frontRightWheel, wheelScale);
		setWheelScale(this.rearLeftWheel, wheelScale);
		setWheelScale(this.rearRightWheel, wheelScale);

		this.frontLeftWheel.xRot = state.wheelRotation;
		this.frontRightWheel.xRot = state.wheelRotation;
		this.rearLeftWheel.xRot = state.wheelRotation;
		this.rearRightWheel.xRot = state.wheelRotation;

		this.frontLeftWheel.yRot = state.steering * 0.45F;
		this.frontRightWheel.yRot = state.steering * 0.45F;
		this.rearLeftWheel.yRot = 0.0F;
		this.rearRightWheel.yRot = 0.0F;
	}

	private void applyChassis(@Nullable Identifier id) {
		for (ModelPart part : this.chassisModels.values()) {
			part.visible = false;
		}
		this.fallbackChassis.visible = false;

		ModelPart selected = id == null ? null : this.chassisModels.get(id);
		if (selected == null) {
			this.fallbackChassis.visible = true;
		} else {
			selected.visible = true;
		}
	}

	private static void applySingle(Map<Identifier, ModelPart> models, @Nullable Identifier id) {
		for (ModelPart part : models.values()) {
			part.visible = false;
		}
		if (id != null) {
			ModelPart selected = models.get(id);
			if (selected != null) {
				selected.visible = true;
			}
		}
	}

	private void applyAccessories(@Nullable Identifier first, @Nullable Identifier second) {
		for (ModelPart part : this.accessoryModels.values()) {
			part.visible = false;
		}
		showAccessory(first);
		if (second != null && !second.equals(first)) {
			showAccessory(second);
		}
	}

	private void showAccessory(@Nullable Identifier id) {
		if (id == null) {
			return;
		}
		ModelPart part = this.accessoryModels.get(id);
		if (part != null) {
			part.visible = true;
		}
	}

	private static void setWheelScale(ModelPart wheel, float scale) {
		wheel.xScale = scale;
		wheel.yScale = scale;
		wheel.zScale = scale;
	}

	private static void updateSuspensionArm(ModelPart arm, float travel, boolean leftSide) {
		float travelUnits = travel * 16.0F;
		float baseLength = 4.0F;
		float length = (float) Math.sqrt(baseLength * baseLength + travelUnits * travelUnits);
		arm.xScale = length / baseLength;
		arm.zRot = (leftSide ? 1.0F : -1.0F) * (float) Math.atan2(travelUnits, baseLength);
	}

	private static void register(
			Map<Identifier, ModelPart> models,
			Identifier id,
			ModelPart parent,
			String childName) {
		models.put(id, parent.getChild(childName));
	}
}
