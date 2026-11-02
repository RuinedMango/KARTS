package com.ruinedmango.kart.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ruinedmango.kart.component.ComponentDefinition;
import com.ruinedmango.kart.component.ComponentSlot;
import com.ruinedmango.kart.component.KartComponents;
import com.ruinedmango.kart.registries.EntityRegistry;
import com.ruinedmango.kart.vehicle.KartEntity;
import com.ruinedmango.kart.vehicle.KartStats;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.phys.Vec3;

import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

public final class KartCommands {
	private KartCommands() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("kart")
				.then(Commands.literal("spawn")
						.requires(KartCommands::isGamemaster)
						.executes(KartCommands::spawn))
				.then(Commands.literal("install")
						.requires(KartCommands::isGamemaster)
						.then(Commands.argument("slot", StringArgumentType.word())
								.suggests((context, builder) -> {
									for (ComponentSlot slot : ComponentSlot.values()) {
										builder.suggest(slot.serializedName());
									}
									return builder.buildFuture();
								})
								.then(Commands.argument("component", StringArgumentType.greedyString())
										.suggests((context, builder) -> {
											ComponentSlot slot = ComponentSlot.byName(StringArgumentType.getString(context, "slot"));
											Iterable<ComponentDefinition> candidates = slot == null
													? KartComponents.all()
													: KartComponents.byType(slot.type());
											for (ComponentDefinition definition : candidates) {
												builder.suggest(definition.id().toString());
											}
											return builder.buildFuture();
										})
										.executes(KartCommands::install))))
				.then(Commands.literal("remove")
						.requires(KartCommands::isGamemaster)
						.then(Commands.argument("slot", StringArgumentType.word())
								.suggests((context, builder) -> {
									for (ComponentSlot slot : ComponentSlot.values()) {
										builder.suggest(slot.serializedName());
									}
									return builder.buildFuture();
								})
								.executes(KartCommands::remove)))
				.then(Commands.literal("stats")
						.requires(KartCommands::isGamemaster)
						.executes(KartCommands::stats))
				.then(Commands.literal("telemetry")
						.requires(KartCommands::isGamemaster)
						.executes(KartCommands::telemetry))
				.then(Commands.literal("components")
						.requires(KartCommands::isGamemaster)
						.executes(KartCommands::components)));
	}

	private static boolean isGamemaster(CommandSourceStack source) {
		return source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
	}

	private static int spawn(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		CommandSourceStack source = context.getSource();
		ServerPlayer player = source.getPlayerOrException();
		ServerLevel level = source.getLevel();

		KartEntity kart = EntityRegistry.KART_ENTITY.get().create(level, EntitySpawnReason.COMMAND);
		if (kart == null) {
			source.sendFailure(Component.literal("Unable to create a kart"));
			return 0;
		}

		Vec3 position = player.position();
		kart.snapTo(position.x, position.y, position.z, player.getYRot(), 0.0F);
		level.addFreshEntity(kart);
		boolean mounted = player.startRiding(kart);

		source.sendSuccess(() -> Component.literal(mounted
				? "Spawned a kart and mounted the player"
				: "Spawned a kart"), false);
		return 1;
	}

	private static int install(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		CommandSourceStack source = context.getSource();
		KartEntity kart = riddenKart(context);
		if (kart == null) {
			return 0;
		}

		ComponentSlot slot = ComponentSlot.byName(StringArgumentType.getString(context, "slot"));
		if (slot == null) {
			source.sendFailure(Component.literal("Unknown slot. Use chassis, engine, wheels, booster, accessory_1, or accessory_2"));
			return 0;
		}

		Identifier id = Identifier.tryParse(StringArgumentType.getString(context, "component"));
		if (id == null) {
			source.sendFailure(Component.literal("Invalid component id"));
			return 0;
		}

		ComponentDefinition definition = KartComponents.byId(id).orElse(null);
		if (definition == null) {
			source.sendFailure(Component.literal("Unknown component: " + id));
			return 0;
		}

		if (!slot.accepts(definition.type())) {
			source.sendFailure(Component.literal(definition.id() + " cannot be installed in " + slot.serializedName()));
			return 0;
		}

		if (!kart.installComponent(slot, definition)) {
			source.sendFailure(Component.literal("Installation rejected"));
			return 0;
		}

		source.sendSuccess(() -> Component.literal("Installed " + definition.id() + " into " + slot.serializedName()), false);
		return 1;
	}

	private static int remove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		CommandSourceStack source = context.getSource();
		KartEntity kart = riddenKart(context);
		if (kart == null) {
			return 0;
		}

		ComponentSlot slot = ComponentSlot.byName(StringArgumentType.getString(context, "slot"));
		if (slot == null) {
			source.sendFailure(Component.literal("Unknown slot"));
			return 0;
		}

		if (!kart.removeComponent(slot)) {
			source.sendFailure(Component.literal("No component installed in " + slot.serializedName()));
			return 0;
		}

		source.sendSuccess(() -> Component.literal("Removed component from " + slot.serializedName()), false);
		return 1;
	}

	private static int stats(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		CommandSourceStack source = context.getSource();
		KartEntity kart = riddenKart(context);
		if (kart == null) {
			return 0;
		}

		KartStats stats = kart.getStats();
		source.sendSuccess(() -> Component.literal(String.format(
				Locale.ROOT,
				"mass=%.1f accel=%.3f reverse=%.3f top=%.2f grip=%.2f steer=%.2f radius=%.2f boost=%.3f heat=%.2f cool=%.2f overheat=%d",
				stats.mass(),
				stats.engineAcceleration(),
				stats.reverseAcceleration(),
				stats.maxForwardSpeed(),
				stats.lateralGrip(),
				stats.steeringRate(),
				stats.wheelRadius(),
				stats.boostForce(),
				stats.boostHeatPerTick(),
				stats.boostCoolingPerTick(),
				stats.boostOverheatTicks())), false);

		for (Map.Entry<ComponentSlot, Identifier> entry : kart.getLoadout().asMap().entrySet()) {
			source.sendSuccess(() -> Component.literal("  " + entry.getKey().serializedName() + " = " + entry.getValue()), false);
		}
		return 1;
	}

	private static int components(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		source.sendSuccess(() -> Component.literal("KART components:"), false);
		for (ComponentDefinition definition : KartComponents.all()) {
			source.sendSuccess(() -> Component.literal("  " + definition.id() + " [" + definition.type().serializedName() + "]"), false);
		}
		return 1;
	}

	private static int telemetry(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		CommandSourceStack source = context.getSource();
		KartEntity kart = riddenKart(context);
		if (kart == null) {
			return 0;
		}

		source.sendSuccess(() -> Component.literal(String.format(
				Locale.ROOT,
				"speed=%.3f forward=%.3f lateral=%.3f slip=%.1f drift=%.2f heat=%.1f pitch=%.1f roll=%.1f susp=%.2f/%.2f/%.2f/%.2f %s%s",
				kart.getDeltaMovement().horizontalDistance(),
				kart.getForwardSpeed(),
				kart.getLateralSpeed(),
				kart.getSlipAngle(),
				kart.getDriftStrength(),
				kart.getHeat(),
				kart.getPitch(),
				kart.getRoll(),
				kart.getSuspensionFL(),
				kart.getSuspensionFR(),
				kart.getSuspensionRL(),
				kart.getSuspensionRR(),
				kart.isBoosting() ? "BOOST " : "",
				kart.isOverheated() ? "OVERHEATED" : (kart.isDrifting() ? "DRIFT" : "grip"))), false);
		return 1;
	}

	private static @Nullable KartEntity riddenKart(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		if (player.getVehicle() instanceof KartEntity kart) {
			return kart;
		}
		context.getSource().sendFailure(Component.literal("Ride a kart first"));
		return null;
	}
}
