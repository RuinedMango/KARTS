package com.ruinedmango.kart.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ruinedmango.kart.KART;
import com.ruinedmango.kart.component.ComponentSlot;
import com.ruinedmango.kart.vehicle.KartEntity;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public final class KartRenderer extends EntityRenderer<KartEntity, KartRenderState> {
	private static final Identifier TEXTURE =
			Identifier.fromNamespaceAndPath(KART.MODID, "textures/entity/kart.png");

	private final KartModel model;

	public KartRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new KartModel(context.bakeLayer(KartModel.LAYER_LOCATION));
		this.shadowRadius = 0.7F;
	}

	@Override
	public KartRenderState createRenderState() {
		return new KartRenderState();
	}

	@Override
	public void extractRenderState(KartEntity entity, KartRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.yRot = entity.getYRot(partialTicks);
		state.steering = entity.getSteering();
		state.wheelRotation = (float) entity.getWheelRotation();
		state.wheelRadius = entity.getStats().wheelRadius();
		state.suspension = entity.getSuspension(partialTicks);
		state.pitch = entity.getPitch(partialTicks);
		state.roll = entity.getRoll(partialTicks);
		state.suspensionFL = entity.getSuspensionFL(partialTicks);
		state.suspensionFR = entity.getSuspensionFR(partialTicks);
		state.suspensionRL = entity.getSuspensionRL(partialTicks);
		state.suspensionRR = entity.getSuspensionRR(partialTicks);
		state.chassis = entity.getLoadout().get(ComponentSlot.CHASSIS);
		state.engine = entity.getLoadout().get(ComponentSlot.ENGINE);
		state.wheels = entity.getLoadout().get(ComponentSlot.WHEELS);
		state.booster = entity.getLoadout().get(ComponentSlot.BOOSTER);
		state.accessory1 = entity.getLoadout().get(ComponentSlot.ACCESSORY_1);
		state.accessory2 = entity.getLoadout().get(ComponentSlot.ACCESSORY_2);
	}

	@Override
	public void submit(KartRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.translate(0.0F, 0.375F, 0.0F);
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.yRot));
		poseStack.mulPose(Axis.XP.rotationDegrees(state.pitch));
		poseStack.mulPose(Axis.ZP.rotationDegrees(state.roll));
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		collector.submitModel(
				this.model,
				state,
				poseStack,
				TEXTURE,
				state.lightCoords,
				OverlayTexture.NO_OVERLAY,
				state.outlineColor,
				null);
		poseStack.popPose();
		super.submit(state, poseStack, collector, camera);
	}
}
