package com.ruinedmango.kart.client;

import com.ruinedmango.kart.KART;
import com.ruinedmango.kart.registries.EntityRegistry;
import com.ruinedmango.kart.registries.MenuRegistry;
import com.ruinedmango.kart.vehicle.KartEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.Quaternionf;

@EventBusSubscriber(modid = KART.MODID, value = Dist.CLIENT)
public final class KartClientEvents {
	private KartClientEvents() {
	}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EntityRegistry.KART_ENTITY.get(), KartRenderer::new);
	}

	@SubscribeEvent
	public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(KartModel.LAYER_LOCATION, KartModel::createBodyLayer);
	}

	@SubscribeEvent
	public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
		KartCameraController.apply(event);
	}

	@SubscribeEvent
	public static void registerScreens(RegisterMenuScreensEvent event) {
		event.register(MenuRegistry.KART_MENU.get(), KartMenuScreen::new);
	}

	@SubscribeEvent
	public static void onPlayerRenderPre(RenderPlayerEvent.Pre<?> event) {
		event.getPoseStack().pushPose();

		AvatarRenderState state = event.getRenderState();
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) {
			return;
		}

		Entity entity = minecraft.level.getEntity(state.id);
		if (!(entity instanceof Player player) || !(player.getVehicle() instanceof KartEntity kart)) {
			return;
		}

		float partialTick = event.getPartialTick();
		float yawRadians = kart.getYRot(partialTick) * Mth.DEG_TO_RAD;
		Quaternionf tilt = new Quaternionf().rotationY(yawRadians);
		tilt.mul(new Quaternionf().rotationX(kart.getPitch(partialTick) * Mth.DEG_TO_RAD));
		tilt.mul(new Quaternionf().rotationZ(kart.getRoll(partialTick) * Mth.DEG_TO_RAD));
		tilt.mul(new Quaternionf().rotationY(-yawRadians));

		float seatPivot = 0.75F;
		event.getPoseStack().translate(0.0F, seatPivot, 0.0F);
		event.getPoseStack().mulPose(tilt);
		event.getPoseStack().translate(0.0F, -seatPivot, 0.0F);
	}

	@SubscribeEvent
	public static void onPlayerRenderPost(RenderPlayerEvent.Post<?> event) {
		event.getPoseStack().popPose();
	}
}
