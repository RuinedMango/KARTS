package com.ruinedmango.kart.networking;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class KartNetwork {
	private KartNetwork() {
	}

	public static void registerPayloads(RegisterPayloadHandlersEvent event) {
		event.registrar("1").playToServer(
				KartInputPayload.TYPE,
				KartInputPayload.STREAM_CODEC,
				KartInputPayload::handle);
	}
}
