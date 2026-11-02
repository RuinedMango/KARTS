package com.ruinedmango.kart.networking;

import com.ruinedmango.kart.KART;
import com.ruinedmango.kart.vehicle.KartEntity;
import com.ruinedmango.kart.vehicle.KartInput;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record KartInputPayload(
		int entityId,
		int sequence,
		float throttle,
		float steering,
		boolean handbrake,
		boolean boost) implements CustomPacketPayload {

	public static final Type<KartInputPayload> TYPE =
			new Type<>(Identifier.fromNamespaceAndPath(KART.MODID, "input"));

	public static final StreamCodec<FriendlyByteBuf, KartInputPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			KartInputPayload::entityId,
			ByteBufCodecs.VAR_INT,
			KartInputPayload::sequence,
			ByteBufCodecs.FLOAT,
			KartInputPayload::throttle,
			ByteBufCodecs.FLOAT,
			KartInputPayload::steering,
			ByteBufCodecs.BYTE,
			KartInputPayload::flags,
			KartInputPayload::decode);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	private byte flags() {
		return (byte) ((this.handbrake ? 1 : 0) | (this.boost ? 2 : 0));
	}

	private static KartInputPayload decode(int entityId, int sequence, float throttle, float steering, byte flags) {
		return new KartInputPayload(
				entityId,
				sequence,
				throttle,
				steering,
				(flags & 1) != 0,
				(flags & 2) != 0);
	}

	public void handle(IPayloadContext context) {
		if (!(context.player() instanceof ServerPlayer player)) {
			return;
		}

		if (!(player.level().getEntity(this.entityId) instanceof KartEntity kart)) {
			return;
		}

		if (player.getVehicle() != kart || player.distanceToSqr(kart) > 64.0D) {
			return;
		}

		if (!Float.isFinite(this.throttle) || !Float.isFinite(this.steering)) {
			return;
		}

		kart.handleInput(player, this.sequence, new KartInput(
				Mth.clamp(this.throttle, -1.0F, 1.0F),
				Mth.clamp(this.steering, -1.0F, 1.0F),
				this.handbrake,
				this.boost));
	}
}
