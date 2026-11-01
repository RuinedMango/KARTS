package com.ruinedmango.kart.component.definitions;

import com.ruinedmango.kart.component.ComponentDefinition;
import com.ruinedmango.kart.component.ComponentStats;
import com.ruinedmango.kart.component.ComponentType;
import com.ruinedmango.kart.component.KartComponentIds;
import com.ruinedmango.kart.component.KartComponents;

public final class KartComponentDefinitions {
	private KartComponentDefinitions() {
	}

	public static void register() {
		register(KartComponentIds.SHOPPING_CART_FRAME, ComponentType.CHASSIS, "shopping_cart_frame",
				ComponentStats.chassis(80.0F, 0.90F, 1.15F));
		register(KartComponentIds.BUDGET_GO_KART_FRAME, ComponentType.CHASSIS, "budget_go_kart_frame",
				ComponentStats.chassis(110.0F, 1.00F, 1.00F));
		register(KartComponentIds.LAWNMOWER_FRAME, ComponentType.CHASSIS, "lawnmower_frame",
				ComponentStats.chassis(150.0F, 1.10F, 0.90F));

		register(KartComponentIds.ELECTRIC_MOTOR, ComponentType.ENGINE, "electric_motor",
				ComponentStats.engine(20.0F, 5.0F, 1.8F, 0.50F, 0.22F));
		register(KartComponentIds.LAWNMOWER_ENGINE, ComponentType.ENGINE, "lawnmower_engine",
				ComponentStats.engine(35.0F, 7.0F, 2.1F, 0.65F, 0.25F));
		register(KartComponentIds.QUESTIONABLE_V8, ComponentType.ENGINE, "questionable_v8",
				ComponentStats.engine(80.0F, 14.0F, 3.2F, 0.85F, 0.30F));
		register(KartComponentIds.DIESEL_ENGINE, ComponentType.ENGINE, "diesel_engine",
				ComponentStats.engine(180.0F, 6.0F, 2.0F, 2F, 0.2F));

		register(KartComponentIds.SHOPPING_CART_CASTERS, ComponentType.WHEELS, "shopping_cart_casters",
				ComponentStats.wheels(5.0F, 0.08F, 0.025F, 0.65F, 5.5F, 0.22F));
		register(KartComponentIds.STANDARD_KART_TIRES, ComponentType.WHEELS, "standard_kart_tires",
				ComponentStats.wheels(15.0F, 0.10F, 0.015F, 0.85F, 4.0F, 0.35F));
		register(KartComponentIds.OFFROAD_TIRES, ComponentType.WHEELS, "offroad_tires",
				ComponentStats.wheels(25.0F, 0.09F, 0.030F, 0.75F, 3.2F, 0.45F));

		register(KartComponentIds.NOS_TANK, ComponentType.BOOSTER, "nos_tank",
				ComponentStats.booster(15.0F, 0.09F, 3.0F, 1.2F, 40));
		register(KartComponentIds.WICKET_NITRO_TANK, ComponentType.BOOSTER, "wicket_tank",
				ComponentStats.booster(15.0F, 2F, 3.0F, 1.2F, 40));

		register(KartComponentIds.SPOILER, ComponentType.ACCESSORY, "spoiler",
				ComponentStats.accessory(5.0F, 1.05F, 0.98F));
		register(KartComponentIds.HORN, ComponentType.ACCESSORY, "horn",
				ComponentStats.accessory(2.0F, 1.0F, 1.0F));
		register(KartComponentIds.BUMPER, ComponentType.ACCESSORY, "bumper",
				ComponentStats.accessory(8.0F, 1.02F, 1.0F));
		register(KartComponentIds.TIGHT_TURN_KIT, ComponentType.ACCESSORY, "tight_turn_kit",
				ComponentStats.accessory(4.0F, 1.0F, 1.30F));
		register(KartComponentIds.WIDE_TURN_KIT, ComponentType.ACCESSORY, "wide_turn_kit",
				ComponentStats.accessory(4.0F, 1.02F, 0.78F));
	}

	private static void register(net.minecraft.resources.Identifier id, ComponentType type, String translationPath, ComponentStats stats) {
		KartComponents.register(new ComponentDefinition(
				id,
				type,
				"component.kart." + translationPath,
				stats));
	}
}
