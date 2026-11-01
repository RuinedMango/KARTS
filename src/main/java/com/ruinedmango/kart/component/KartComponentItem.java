package com.ruinedmango.kart.component;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.Locale;
import java.util.function.Consumer;

public class KartComponentItem extends Item {
	private final ComponentDefinition definition;

	public KartComponentItem(ComponentDefinition definition, Item.Properties properties) {
		super(properties);
		this.definition = definition;
	}

	public ComponentDefinition definition() {
		return this.definition;
	}

	@Override
	public Component getName(ItemStack stack) {
		return Component.translatable(this.definition.translationKey());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void appendHoverText(
			ItemStack stack,
			Item.TooltipContext context,
			TooltipDisplay display,
			Consumer<Component> builder,
			TooltipFlag flag) {
		ComponentStats stats = this.definition.stats();
		builder.accept(Component.literal("Type: " + this.definition.type().serializedName())
				.withStyle(ChatFormatting.GRAY));

		if (stats.mass() > 0.0F) {
			builder.accept(stat("Mass", stats.mass()));
		}
		if (stats.engineForce() > 0.0F) {
			builder.accept(stat("Engine force", stats.engineForce()));
			builder.accept(stat("Top speed", stats.maxForwardSpeed()));
		}
		if (stats.lateralGrip() > 0.0F) {
			builder.accept(stat("Grip", stats.lateralGrip()));
			builder.accept(stat("Steering", stats.steeringRate()));
		}
		if (stats.boostForce() > 0.0F) {
			builder.accept(stat("Boost force", stats.boostForce()));
			builder.accept(stat("Heat gain", stats.boostHeatPerTick()));
		}
		if (stats.steeringMultiplier() != 1.0F) {
			builder.accept(stat("Steering multiplier", stats.steeringMultiplier()));
		}
	}

	private static Component stat(String name, float value) {
		return Component.literal(String.format(Locale.ROOT, "%s: %.2f", name, value))
				.withStyle(ChatFormatting.DARK_GRAY);
	}
}
