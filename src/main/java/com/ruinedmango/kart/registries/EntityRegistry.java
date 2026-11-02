package com.ruinedmango.kart.registries;

import com.ruinedmango.kart.KART;
import com.ruinedmango.kart.vehicle.KartEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EntityRegistry {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
			DeferredRegister.create(Registries.ENTITY_TYPE, KART.MODID);

	public static final DeferredHolder<EntityType<?>, EntityType<KartEntity>> KART_ENTITY =
			ENTITY_TYPES.register("kart", () -> EntityType.Builder.of(KartEntity::new, MobCategory.MISC)
					.sized(1.2F, 1.1F)
					.eyeHeight(0.65F)
					.passengerAttachments(0.65F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build(ResourceKey.create(
							Registries.ENTITY_TYPE,
							Identifier.fromNamespaceAndPath(KART.MODID, "kart"))));

	private EntityRegistry() {
	}

	public static void register(IEventBus modBus) {
		ENTITY_TYPES.register(modBus);
	}
}
