package com.ruinedmango.kart.client;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public final class KartModelLayer {
	private KartModelLayer() {
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.00F, 0.00F, 0.00F));
			PartDefinition seat = body.addOrReplaceChild("seat", CubeListBuilder.create()
				.texOffs(10, 70).addBox(-3.00F, -7.00F, 1.00F, 6.00F, 2.00F, 6.00F), PartPose.offset(0.00F, 0.00F, 0.00F));
			PartDefinition chassis_shopping_cart = body.addOrReplaceChild("chassis_shopping_cart", CubeListBuilder.create()
				.texOffs(20, 20).addBox(-7.00F, -4.00F, -10.00F, 14.00F, 2.00F, 20.00F)
				.texOffs(20, 20).addBox(-7.00F, -8.00F, -9.00F, 1.00F, 4.00F, 18.00F)
				.texOffs(20, 20).addBox(6.00F, -8.00F, -9.00F, 1.00F, 4.00F, 18.00F)
				.texOffs(20, 20).addBox(-7.00F, -8.00F, 8.00F, 14.00F, 4.00F, 1.00F)
				.texOffs(20, 20).addBox(-4.00F, -10.00F, -11.00F, 8.00F, 2.00F, 2.00F), PartPose.offset(0.00F, 0.00F, 0.00F));
			PartDefinition chassis_budget_go_kart = body.addOrReplaceChild("chassis_budget_go_kart", CubeListBuilder.create()
				.texOffs(30, 20).addBox(-6.00F, -4.00F, -9.00F, 12.00F, 3.00F, 18.00F)
				.texOffs(30, 20).addBox(-5.00F, -3.00F, -12.00F, 10.00F, 2.00F, 4.00F)
				.texOffs(30, 20).addBox(-7.00F, -3.00F, 2.00F, 2.00F, 1.00F, 8.00F)
				.texOffs(30, 20).addBox(5.00F, -3.00F, 2.00F, 2.00F, 1.00F, 8.00F), PartPose.offset(0.00F, 0.00F, 0.00F));
			PartDefinition chassis_lawnmower = body.addOrReplaceChild("chassis_lawnmower", CubeListBuilder.create()
				.texOffs(40, 20).addBox(-8.00F, -4.00F, -10.00F, 16.00F, 4.00F, 20.00F)
				.texOffs(40, 20).addBox(-6.00F, -8.00F, -8.00F, 12.00F, 4.00F, 14.00F)
				.texOffs(40, 20).addBox(-7.00F, -5.00F, -12.00F, 14.00F, 2.00F, 3.00F), PartPose.offset(0.00F, 0.00F, 0.00F));
			PartDefinition chassis_fallback = body.addOrReplaceChild("chassis_fallback", CubeListBuilder.create()
				.texOffs(20, 20).addBox(-7.00F, -4.00F, -10.00F, 14.00F, 4.00F, 20.00F), PartPose.offset(0.00F, 0.00F, 0.00F));
			PartDefinition engine_electric_motor = body.addOrReplaceChild("engine_electric_motor", CubeListBuilder.create()
				.texOffs(90, 10).addBox(-3.00F, -5.00F, 5.00F, 6.00F, 5.00F, 6.00F), PartPose.offset(0.00F, 0.00F, 0.00F));
			PartDefinition engine_lawnmower_engine = body.addOrReplaceChild("engine_lawnmower_engine", CubeListBuilder.create()
				.texOffs(90, 10).addBox(-4.00F, -6.00F, 5.00F, 8.00F, 6.00F, 8.00F), PartPose.offset(0.00F, 0.00F, 0.00F));
			PartDefinition engine_questionable_v8 = body.addOrReplaceChild("engine_questionable_v8", CubeListBuilder.create()
				.texOffs(90, 10).addBox(-5.00F, -7.00F, 5.00F, 10.00F, 7.00F, 8.00F)
				.texOffs(90, 10).addBox(-5.00F, -6.00F, 10.00F, 1.00F, 3.00F, 4.00F)
				.texOffs(90, 10).addBox(4.00F, -6.00F, 10.00F, 1.00F, 3.00F, 4.00F), PartPose.offset(0.00F, 0.00F, 0.00F));
			PartDefinition engine_diesel = body.addOrReplaceChild("engine_diesel", CubeListBuilder.create()
				.texOffs(90, 10).addBox(-4.00F, -8.00F, 5.00F, 8.00F, 8.00F, 9.00F), PartPose.offset(0.00F, 0.00F, 0.00F));
			PartDefinition booster_nos_tank = body.addOrReplaceChild("booster_nos_tank", CubeListBuilder.create()
				.texOffs(70, 40).addBox(2.00F, -4.00F, 8.00F, 4.00F, 4.00F, 8.00F), PartPose.offset(0.00F, 0.00F, 0.00F));
			PartDefinition booster_wicket_tank = body.addOrReplaceChild("booster_wicket_tank", CubeListBuilder.create()
				.texOffs(70, 55).addBox(2.00F, -5.00F, 8.00F, 5.00F, 5.00F, 8.00F), PartPose.offset(0.00F, 0.00F, 0.00F));
			PartDefinition accessory_spoiler = body.addOrReplaceChild("accessory_spoiler", CubeListBuilder.create()
				.texOffs(110, 40).addBox(-6.00F, -7.00F, 8.00F, 12.00F, 2.00F, 3.00F), PartPose.offset(0.00F, 0.00F, 0.00F));
			PartDefinition accessory_horn = body.addOrReplaceChild("accessory_horn", CubeListBuilder.create()
				.texOffs(110, 40).addBox(-2.00F, -5.00F, -10.00F, 4.00F, 3.00F, 3.00F), PartPose.offset(0.00F, 0.00F, 0.00F));
			PartDefinition accessory_bumper = body.addOrReplaceChild("accessory_bumper", CubeListBuilder.create()
				.texOffs(110, 40).addBox(-6.00F, -4.00F, -11.00F, 12.00F, 2.00F, 3.00F), PartPose.offset(0.00F, 0.00F, 0.00F));
			PartDefinition accessory_tight_turn_kit = body.addOrReplaceChild("accessory_tight_turn_kit", CubeListBuilder.create()
				.texOffs(110, 40).addBox(-4.00F, -2.00F, -8.00F, 8.00F, 1.00F, 4.00F), PartPose.offset(0.00F, 0.00F, 0.00F));
			PartDefinition accessory_wide_turn_kit = body.addOrReplaceChild("accessory_wide_turn_kit", CubeListBuilder.create()
				.texOffs(110, 40).addBox(-6.00F, -2.00F, -8.00F, 12.00F, 1.00F, 4.00F), PartPose.offset(0.00F, 0.00F, 0.00F));
		PartDefinition front_left_wheel = root.addOrReplaceChild("front_left_wheel", CubeListBuilder.create()
			.texOffs(40, 70).addBox(-1.50F, -4.00F, -4.00F, 3.00F, 8.00F, 8.00F)
			.texOffs(40, 70).addBox(-2.00F, -2.00F, -2.00F, 4.00F, 4.00F, 4.00F), PartPose.offset(9.00F, 0.00F, -7.00F));
		PartDefinition suspension_front_left = root.addOrReplaceChild("suspension_front_left", CubeListBuilder.create()
			.texOffs(110, 40).addBox(0.00F, -0.50F, -0.50F, 4.00F, 1.00F, 1.00F), PartPose.offset(5.00F, 2.00F, -7.00F));
		PartDefinition rear_left_wheel = root.addOrReplaceChild("rear_left_wheel", CubeListBuilder.create()
			.texOffs(40, 70).addBox(-1.50F, -4.00F, -4.00F, 3.00F, 8.00F, 8.00F)
			.texOffs(40, 70).addBox(-2.00F, -2.00F, -2.00F, 4.00F, 4.00F, 4.00F), PartPose.offset(9.00F, 0.00F, 7.00F));
		PartDefinition suspension_rear_left = root.addOrReplaceChild("suspension_rear_left", CubeListBuilder.create()
			.texOffs(110, 40).addBox(0.00F, -0.50F, -0.50F, 4.00F, 1.00F, 1.00F), PartPose.offset(5.00F, 2.00F, 7.00F));
		PartDefinition front_right_wheel = root.addOrReplaceChild("front_right_wheel", CubeListBuilder.create()
			.texOffs(40, 70).addBox(-1.50F, -4.00F, -4.00F, 3.00F, 8.00F, 8.00F)
			.texOffs(40, 70).addBox(-2.00F, -2.00F, -2.00F, 4.00F, 4.00F, 4.00F), PartPose.offset(-9.00F, 0.00F, -7.00F));
		PartDefinition suspension_front_right = root.addOrReplaceChild("suspension_front_right", CubeListBuilder.create()
			.texOffs(110, 40).addBox(-4.00F, -0.50F, -0.50F, 4.00F, 1.00F, 1.00F), PartPose.offset(-5.00F, 2.00F, -7.00F));
		PartDefinition rear_right_wheel = root.addOrReplaceChild("rear_right_wheel", CubeListBuilder.create()
			.texOffs(40, 70).addBox(-1.50F, -4.00F, -4.00F, 3.00F, 8.00F, 8.00F)
			.texOffs(40, 70).addBox(-2.00F, -2.00F, -2.00F, 4.00F, 4.00F, 4.00F), PartPose.offset(-9.00F, 0.00F, 7.00F));
		PartDefinition suspension_rear_right = root.addOrReplaceChild("suspension_rear_right", CubeListBuilder.create()
			.texOffs(110, 40).addBox(-4.00F, -0.50F, -0.50F, 4.00F, 1.00F, 1.00F), PartPose.offset(-5.00F, 2.00F, 7.00F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
