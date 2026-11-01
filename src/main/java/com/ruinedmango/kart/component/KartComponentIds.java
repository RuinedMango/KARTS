package com.ruinedmango.kart.component;

import com.ruinedmango.kart.KART;

import net.minecraft.resources.Identifier;

public final class KartComponentIds {
	public static final Identifier SHOPPING_CART_FRAME = id("shopping_cart_frame");
	public static final Identifier BUDGET_GO_KART_FRAME = id("budget_go_kart_frame");
	public static final Identifier LAWNMOWER_FRAME = id("lawnmower_frame");

	public static final Identifier ELECTRIC_MOTOR = id("electric_motor");
	public static final Identifier LAWNMOWER_ENGINE = id("lawnmower_engine");
	public static final Identifier QUESTIONABLE_V8 = id("questionable_v8");
	public static final Identifier DIESEL_ENGINE = id("diesel_engine");

	public static final Identifier SHOPPING_CART_CASTERS = id("shopping_cart_casters");
	public static final Identifier STANDARD_KART_TIRES = id("standard_kart_tires");
	public static final Identifier OFFROAD_TIRES = id("offroad_tires");

	public static final Identifier NOS_TANK = id("nos_tank");
	public static final Identifier WICKET_NITRO_TANK = id("wicket_nitro");

	public static final Identifier SPOILER = id("spoiler");
	public static final Identifier HORN = id("horn");
	public static final Identifier BUMPER = id("bumper");
	public static final Identifier TIGHT_TURN_KIT = id("tight_turn_kit");
	public static final Identifier WIDE_TURN_KIT = id("wide_turn_kit");

	private KartComponentIds() {
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(KART.MODID, path);
	}
}
