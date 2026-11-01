package com.ruinedmango.kart.vehicle;

import com.ruinedmango.kart.KART;
import com.ruinedmango.kart.component.ComponentDefinition;
import com.ruinedmango.kart.component.ComponentSlot;
import com.ruinedmango.kart.component.KartComponents;
import com.ruinedmango.kart.component.KartLoadout;
import com.ruinedmango.kart.menu.KartMenu;
import com.ruinedmango.kart.physics.KartPhysics;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class KartEntity extends Entity implements MenuProvider {
	private static final int COMPONENT_DATA_VERSION = 1;
	private static final EntityDataAccessor<Float> DATA_STEERING =
			SynchedEntityData.defineId(KartEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_WHEEL_ROTATION =
			SynchedEntityData.defineId(KartEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_DRIFT_STRENGTH =
			SynchedEntityData.defineId(KartEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_HEAT =
			SynchedEntityData.defineId(KartEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Boolean> DATA_BOOSTING =
			SynchedEntityData.defineId(KartEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> DATA_OVERHEATED =
			SynchedEntityData.defineId(KartEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> DATA_HAS_BOOSTER =
			SynchedEntityData.defineId(KartEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<String> DATA_COMPONENT_CONFIG =
			SynchedEntityData.defineId(KartEntity.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<Float> DATA_SUSPENSION =
			SynchedEntityData.defineId(KartEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_PITCH =
			SynchedEntityData.defineId(KartEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_ROLL =
			SynchedEntityData.defineId(KartEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_SUSPENSION_FL =
			SynchedEntityData.defineId(KartEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_SUSPENSION_FR =
			SynchedEntityData.defineId(KartEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_SUSPENSION_RL =
			SynchedEntityData.defineId(KartEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_SUSPENSION_RR =
			SynchedEntityData.defineId(KartEntity.class, EntityDataSerializers.FLOAT);

	private static final float MAX_HEAT = 100.0F;
	private static final float MAX_SUSPENSION = 0.35F;
	private static final float MAX_WHEEL_DROOP = 1.0F;
	private static final float MAX_WHEEL_COMPRESSION = 0.15F;
	private static final double WHEELBASE = 0.90D;
	private static final double TRACK_WIDTH = 1.10D;
	private static final float MAX_TERRAIN_TILT = 80.0F;

	private KartLoadout loadout = KartLoadout.defaultLoadout();
	private KartStats stats = KartStats.derive(this.loadout);
	private final InterpolationHandler interpolation = new InterpolationHandler(this, 3);
	private double wheelRotation;
	private float steering;
	private boolean drifting;
	private float driftStrength;
	private float forwardSpeed;
	private float lateralSpeed;
	private float slipAngle;
	private boolean lastHandbrake;
	private KartInput currentInput = KartInput.NEUTRAL;
	private int lastInputTick;
	private int lastInputSequence = -1;
	private @Nullable UUID lastInputPlayer;
	private @Nullable UUID lastRider;
	private float heat;
	private int overheatTicks;
	private int boostRequestTicks;
	private int groundGraceTicks;
	private int boostHoldTicks;
	private boolean boosting;
	private boolean overheated;
	private boolean lastBoosting;
	private float suspension;
	private float pitch;
	private float roll;
	private float suspensionFL;
	private float suspensionFR;
	private float suspensionRL;
	private float suspensionRR;
	private float pitchO;
	private float rollO;
	private float suspensionO;
	private float suspensionFLO;
	private float suspensionFRO;
	private float suspensionRLO;
	private float suspensionRRO;
	private double groundFL = Double.NaN;
	private double groundFR = Double.NaN;
	private double groundRL = Double.NaN;
	private double groundRR = Double.NaN;
	private String clientComponentConfig = "";
	private boolean clientComponentConfigApplied;

	public KartEntity(EntityType<? extends KartEntity> type, Level level) {
		super(type, level);
		this.syncComponentConfig();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(DATA_STEERING, 0.0F);
		builder.define(DATA_WHEEL_ROTATION, 0.0F);
		builder.define(DATA_DRIFT_STRENGTH, 0.0F);
		builder.define(DATA_HEAT, 0.0F);
		builder.define(DATA_BOOSTING, false);
		builder.define(DATA_OVERHEATED, false);
		builder.define(DATA_HAS_BOOSTER, false);
		builder.define(DATA_COMPONENT_CONFIG, "");
		builder.define(DATA_SUSPENSION, 0.0F);
		builder.define(DATA_PITCH, 0.0F);
		builder.define(DATA_ROLL, 0.0F);
		builder.define(DATA_SUSPENSION_FL, 0.0F);
		builder.define(DATA_SUSPENSION_FR, 0.0F);
		builder.define(DATA_SUSPENSION_RL, 0.0F);
		builder.define(DATA_SUSPENSION_RR, 0.0F);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		KartLoadout loaded = new KartLoadout();
		boolean hasSavedComponents = false;

		for (ComponentSlot slot : ComponentSlot.values()) {
			Optional<String> value = input.getString(slot.saveKey());
			if (value.isEmpty()) {
				continue;
			}

			hasSavedComponents = true;
			String raw = value.get();
			if (raw.isBlank()) {
				continue;
			}

			Identifier id = Identifier.tryParse(raw);
			if (id == null) {
				KART.LOGGER.warn("Invalid kart component id '{}' in slot {}", raw, slot);
				continue;
			}

			Optional<ComponentDefinition> definition = KartComponents.byId(id);
			if (definition.isEmpty()) {
				KART.LOGGER.warn("Unknown kart component '{}' in slot {}", id, slot);
				continue;
			}

			if (!slot.accepts(definition.get().type())) {
				KART.LOGGER.warn("Kart component '{}' cannot be installed in slot {}", id, slot);
				continue;
			}

			loaded.set(slot, id);
		}

		if (hasSavedComponents) {
			this.loadout = loaded;
		}
		this.recalculateStats();
		this.syncComponentConfig();
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		output.putInt("component_data_version", COMPONENT_DATA_VERSION);
		for (ComponentSlot slot : ComponentSlot.values()) {
			Identifier id = this.loadout.get(slot);
			output.putString(slot.saveKey(), id == null ? "" : id.toString());
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getFirstPassenger() instanceof Player rider) {
			rider.setYBodyRot(this.getYRot());
		}
		this.pitchO = this.pitch;
		this.rollO = this.roll;
		this.suspensionO = this.suspension;
		this.suspensionFLO = this.suspensionFL;
		this.suspensionFRO = this.suspensionFR;
		this.suspensionRLO = this.suspensionRL;
		this.suspensionRRO = this.suspensionRR;
		this.interpolation.interpolate();

		if (this.level().isClientSide()) {
			this.steering = this.entityData.get(DATA_STEERING);
			this.wheelRotation = this.entityData.get(DATA_WHEEL_ROTATION);
			this.driftStrength = this.entityData.get(DATA_DRIFT_STRENGTH);
			this.heat = this.entityData.get(DATA_HEAT);
			this.boosting = this.entityData.get(DATA_BOOSTING);
			this.overheated = this.entityData.get(DATA_OVERHEATED);
			this.suspension = Mth.lerp(0.25F, this.suspension, this.entityData.get(DATA_SUSPENSION));
			this.pitch = Mth.lerp(0.25F, this.pitch, this.entityData.get(DATA_PITCH));
			this.roll = Mth.lerp(0.25F, this.roll, this.entityData.get(DATA_ROLL));
			this.suspensionFL = Mth.lerp(0.45F, this.suspensionFL, this.entityData.get(DATA_SUSPENSION_FL));
			this.suspensionFR = Mth.lerp(0.45F, this.suspensionFR, this.entityData.get(DATA_SUSPENSION_FR));
			this.suspensionRL = Mth.lerp(0.45F, this.suspensionRL, this.entityData.get(DATA_SUSPENSION_RL));
			this.suspensionRR = Mth.lerp(0.45F, this.suspensionRR, this.entityData.get(DATA_SUSPENSION_RR));
			this.applyComponentConfig(this.entityData.get(DATA_COMPONENT_CONFIG));
			this.spawnDriftSmoke();
			this.spawnBoostParticles();
			return;
		}

		UUID rider = this.getFirstPassenger() instanceof Player player ? player.getUUID() : null;
		if (!Objects.equals(rider, this.lastRider)) {
			this.lastRider = rider;
			this.lastInputPlayer = null;
			this.lastInputSequence = -1;
			this.currentInput = KartInput.NEUTRAL;
		}
		KartInput input = this.readInput();
		if (input.handbrake() != this.lastHandbrake) {
			KART.LOGGER.debug("Kart handbrake {}", input.handbrake());
			this.lastHandbrake = input.handbrake();
		}
		this.updateBoost(input);
		if (this.boosting != this.lastBoosting) {
			KART.LOGGER.debug("Kart boosting {}", this.boosting);
			this.lastBoosting = this.boosting;
		}
		boolean wasOnGround = this.onGround();
		double verticalBefore = this.getDeltaMovement().y;
		KartPhysics.Result result = KartPhysics.step(this, this.stats, input, this.boosting);
		this.updateSuspension(wasOnGround, verticalBefore);
		this.updateTerrainContact();

		this.steering = input.steering();
		this.drifting = result.drifting();
		this.driftStrength = result.driftStrength();
		this.forwardSpeed = (float) result.forwardSpeed();
		this.lateralSpeed = (float) result.lateralSpeed();
		this.slipAngle = result.slipAngleDegrees();
		this.wheelRotation = wrapRadians(this.wheelRotation + result.wheelRotationDelta());

		this.entityData.set(DATA_STEERING, this.steering);
		this.entityData.set(DATA_WHEEL_ROTATION, (float) this.wheelRotation);
		this.entityData.set(DATA_DRIFT_STRENGTH, this.driftStrength);
		this.entityData.set(DATA_HEAT, this.heat);
		this.entityData.set(DATA_BOOSTING, this.boosting);
		this.entityData.set(DATA_OVERHEATED, this.overheated);
		this.entityData.set(DATA_HAS_BOOSTER, this.stats.boostForce() > 0.0F);
		this.entityData.set(DATA_SUSPENSION, this.suspension);
		this.entityData.set(DATA_PITCH, this.pitch);
		this.entityData.set(DATA_ROLL, this.roll);
		this.entityData.set(DATA_SUSPENSION_FL, this.suspensionFL);
		this.entityData.set(DATA_SUSPENSION_FR, this.suspensionFR);
		this.entityData.set(DATA_SUSPENSION_RL, this.suspensionRL);
		this.entityData.set(DATA_SUSPENSION_RR, this.suspensionRR);
	}

	private void spawnDriftSmoke() {
		if (this.driftStrength <= 0.25F || this.random.nextFloat() > 0.35F * this.driftStrength) {
			return;
		}

		Vec3 rear = KartPhysics.forward(this.getYRot()).scale(-0.8D);
		double x = this.getX() + rear.x + (this.random.nextDouble() - 0.5D) * 0.35D;
		double z = this.getZ() + rear.z + (this.random.nextDouble() - 0.5D) * 0.35D;
		this.level().addParticle(
				net.minecraft.core.particles.ParticleTypes.SMOKE,
				x,
				this.getY() + 0.2D,
				z,
				(this.random.nextDouble() - 0.5D) * 0.04D,
				0.02D,
				(this.random.nextDouble() - 0.5D) * 0.04D);
	}

	private KartInput readInput() {
		if (this.getFirstPassenger() instanceof ServerPlayer && this.tickCount - this.lastInputTick <= 5) {
			return this.currentInput;
		}
		return KartInput.NEUTRAL;
	}

	public void handleInput(ServerPlayer player, int sequence, KartInput input) {
		if (player.getVehicle() != this || player.distanceToSqr(this) > 64.0D) {
			return;
		}

		if (this.lastInputPlayer == null || !this.lastInputPlayer.equals(player.getUUID())) {
			this.lastInputPlayer = player.getUUID();
			this.lastInputSequence = -1;
		}

		if (sequence <= this.lastInputSequence) {
			return;
		}

		this.lastInputSequence = sequence;
		this.currentInput = new KartInput(
				Mth.clamp(input.throttle(), -1.0F, 1.0F),
				Mth.clamp(input.steering(), -1.0F, 1.0F),
				input.handbrake(),
				input.boost());
		this.lastInputTick = this.tickCount;
	}

	private void updateBoost(KartInput input) {
		if (this.stats.boostForce() <= 0.0F) {
			this.boosting = false;
			this.overheated = false;
			this.overheatTicks = 0;
			this.boostRequestTicks = 0;
			this.groundGraceTicks = 0;
			this.boostHoldTicks = 0;
			this.heat = 0.0F;
			return;
		}

		if (input.boost()) {
			this.boostRequestTicks = 3;
		} else if (this.boostRequestTicks > 0) {
			this.boostRequestTicks--;
		}

		if (this.onGround()) {
			this.groundGraceTicks = 5;
		} else if (this.groundGraceTicks > 0) {
			this.groundGraceTicks--;
		}

		if (this.overheatTicks > 0) {
			this.overheatTicks--;
			this.boosting = false;
			this.overheated = true;
			this.boostRequestTicks = 0;
			this.boostHoldTicks = 0;
			this.heat = Math.max(0.0F, this.heat - this.stats.boostCoolingPerTick());
			if (this.overheatTicks == 0) {
				this.overheated = false;
			}
			return;
		}

		this.overheated = false;
		boolean wantsBoost = this.boostRequestTicks > 0
				&& this.groundGraceTicks > 0
				&& !input.handbrake();

		if (wantsBoost) {
			this.boostHoldTicks = 3;
		} else if (this.boostHoldTicks > 0) {
			this.boostHoldTicks--;
		}

		if (this.boostHoldTicks > 0) {
			this.boosting = true;
			this.heat = Math.min(MAX_HEAT, this.heat + this.stats.boostHeatPerTick());
			if (this.heat >= MAX_HEAT) {
				this.heat = MAX_HEAT;
				this.boosting = false;
				this.overheated = true;
				this.overheatTicks = this.stats.boostOverheatTicks();
				this.boostHoldTicks = 0;
			}
		} else {
			this.boosting = false;
			this.heat = Math.max(0.0F, this.heat - this.stats.boostCoolingPerTick());
		}
	}

	private void spawnBoostParticles() {
		if (!this.boosting || this.random.nextFloat() > 0.55F) {
			return;
		}

		Vec3 rear = KartPhysics.forward(this.getYRot()).scale(-0.9D);
		double x = this.getX() + rear.x + (this.random.nextDouble() - 0.5D) * 0.2D;
		double z = this.getZ() + rear.z + (this.random.nextDouble() - 0.5D) * 0.2D;
		this.level().addParticle(
				net.minecraft.core.particles.ParticleTypes.FLAME,
				x,
				this.getY() + 0.25D,
				z,
				(this.random.nextDouble() - 0.5D) * 0.03D,
				0.03D,
				(this.random.nextDouble() - 0.5D) * 0.03D);
	}

	@Override
	public @Nullable LivingEntity getControllingPassenger() {
		return this.getFirstPassenger() instanceof Player player ? player : null;
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return passenger instanceof Player && this.getPassengers().isEmpty();
	}

	@Override
	public void onPassengerTurned(Entity passenger) {
		if (passenger instanceof Player player) {
			player.setYBodyRot(this.getYRot());
		}
	}

	@Override
	public Vec3 getPassengerRidingPosition(Entity passenger) {
		float yaw = this.getYRot();
		float yawRadians = yaw * Mth.DEG_TO_RAD;
		Quaternionf rotation = new Quaternionf().rotationY(yawRadians);
		rotation.mul(new Quaternionf().rotationX(-this.pitch * Mth.DEG_TO_RAD));
		rotation.mul(new Quaternionf().rotationZ(this.roll * Mth.DEG_TO_RAD));
		rotation.mul(new Quaternionf().rotationY(-yawRadians));
		Vector3f seat = new Vector3f(0.0F, 0.65F, 0.0F).rotate(rotation);
		return this.position().add(seat.x, seat.y, seat.z);
	}

	@Override
	public InterpolationHandler getInterpolation() {
		return this.interpolation;
	}

	@Override
	public boolean isClientAuthoritative() {
		return false;
	}

	@Override
	protected boolean isLocalClientAuthoritative() {
		return false;
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
		return false;
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
		if (player.isSecondaryUseActive()) {
			if (!this.level().isClientSide()) {
				player.openMenu(this);
			}
			return InteractionResult.SUCCESS;
		}

		if (!this.level().isClientSide() && this.getPassengers().isEmpty()) {
			player.startRiding(this);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("container.kart.modify");
	}

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
		return new KartMenu(containerId, inventory, this);
	}

	@Override
	public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
		buffer.writeVarInt(this.getId());
	}

	@Override
	public float maxUpStep() {
		return 1.05F;
	}

	public KartLoadout getLoadout() {
		return this.loadout;
	}

	public KartStats getStats() {
		return this.stats;
	}

	public float getSuspension() {
		return this.suspension;
	}

	public float getSuspension(float partialTick) {
		return Mth.lerp(partialTick, this.suspensionO, this.suspension);
	}

	public float getPitch() {
		return this.pitch;
	}

	public float getPitch(float partialTick) {
		return Mth.lerp(partialTick, this.pitchO, this.pitch);
	}

	public float getRoll() {
		return this.roll;
	}

	public float getRoll(float partialTick) {
		return Mth.lerp(partialTick, this.rollO, this.roll);
	}

	public float getSuspensionFL() {
		return this.suspensionFL;
	}

	public float getSuspensionFL(float partialTick) {
		return Mth.lerp(partialTick, this.suspensionFLO, this.suspensionFL);
	}

	public float getSuspensionFR() {
		return this.suspensionFR;
	}

	public float getSuspensionFR(float partialTick) {
		return Mth.lerp(partialTick, this.suspensionFRO, this.suspensionFR);
	}

	public float getSuspensionRL() {
		return this.suspensionRL;
	}

	public float getSuspensionRL(float partialTick) {
		return Mth.lerp(partialTick, this.suspensionRLO, this.suspensionRL);
	}

	public float getSuspensionRR() {
		return this.suspensionRR;
	}

	public float getSuspensionRR(float partialTick) {
		return Mth.lerp(partialTick, this.suspensionRRO, this.suspensionRR);
	}

	public boolean installComponent(ComponentSlot slot, ComponentDefinition definition) {
		if (!slot.accepts(definition.type())) {
			return false;
		}
		this.loadout.set(slot, definition.id());
		this.recalculateStats();
		this.syncComponentConfig();
		return true;
	}

	public boolean removeComponent(ComponentSlot slot) {
		if (this.loadout.get(slot) == null) {
			return false;
		}
		this.loadout.set(slot, null);
		this.recalculateStats();
		this.syncComponentConfig();
		return true;
	}

	private void syncComponentConfig() {
		if (!this.level().isClientSide()) {
			this.entityData.set(DATA_COMPONENT_CONFIG, encodeLoadout(this.loadout));
		}
	}

	private void applyComponentConfig(String raw) {
		if (this.clientComponentConfigApplied && raw.equals(this.clientComponentConfig)) {
			return;
		}

		this.clientComponentConfigApplied = true;
		this.clientComponentConfig = raw;
		this.loadout = decodeLoadout(raw);
		this.recalculateStats();
	}

	private void updateSuspension(boolean wasOnGround, double verticalBefore) {
		float target = 0.0F;
		if (!wasOnGround && this.onGround()) {
			target = Mth.clamp((float) (-verticalBefore) * 0.25F, 0.0F, MAX_SUSPENSION);
		}
		this.suspension = Mth.lerp(0.35F, this.suspension, target);
		if (this.suspension < 0.005F) {
			this.suspension = 0.0F;
		}
	}

	private void updateTerrainContact() {
		float targetPitch = 0.0F;
		float targetRoll = 0.0F;
		float targetFL = 0.0F;
		float targetFR = 0.0F;
		float targetRL = 0.0F;
		float targetRR = 0.0F;
		boolean grounded = this.onGround();
		boolean validContact = false;

		Vec3 forward = KartPhysics.forward(this.getYRot()).scale(WHEELBASE * 0.5D);
		Vec3 right = KartPhysics.right(this.getYRot()).scale(TRACK_WIDTH * 0.5D);
		double centerX = this.getX();
		double centerY = this.getY();
		double centerZ = this.getZ();

		this.groundFL = smoothGround(this.groundFL, this.sampleGround(
				centerX + forward.x - right.x, centerZ + forward.z - right.z, centerY));
		this.groundFR = smoothGround(this.groundFR, this.sampleGround(
				centerX + forward.x + right.x, centerZ + forward.z + right.z, centerY));
		this.groundRL = smoothGround(this.groundRL, this.sampleGround(
				centerX - forward.x - right.x, centerZ - forward.z - right.z, centerY));
		this.groundRR = smoothGround(this.groundRR, this.sampleGround(
				centerX - forward.x + right.x, centerZ - forward.z + right.z, centerY));

		if (!Double.isNaN(this.groundFL) && !Double.isNaN(this.groundFR)
				&& !Double.isNaN(this.groundRL) && !Double.isNaN(this.groundRR)) {
			validContact = true;
			double flY = this.groundFL;
			double frY = this.groundFR;
			double rlY = this.groundRL;
			double rrY = this.groundRR;
			double front = (flY + frY) * 0.5D;
			double rear = (rlY + rrY) * 0.5D;
			double rightSide = (frY + rrY) * 0.5D;
			double leftSide = (flY + rlY) * 0.5D;

			if (grounded) {
				targetPitch = (float) Math.toDegrees(Math.atan2(front - rear, WHEELBASE));
				targetRoll = (float) Math.toDegrees(Math.atan2(rightSide - leftSide, TRACK_WIDTH));
			}

			targetFL = Mth.clamp((float) (centerY - flY) * 0.9F, -MAX_WHEEL_COMPRESSION, MAX_WHEEL_DROOP);
			targetFR = Mth.clamp((float) (centerY - frY) * 0.9F, -MAX_WHEEL_COMPRESSION, MAX_WHEEL_DROOP);
			targetRL = Mth.clamp((float) (centerY - rlY) * 0.9F, -MAX_WHEEL_COMPRESSION, MAX_WHEEL_DROOP);
			targetRR = Mth.clamp((float) (centerY - rrY) * 0.9F, -MAX_WHEEL_COMPRESSION, MAX_WHEEL_DROOP);
		} else if (!grounded) {
			targetFL = MAX_WHEEL_DROOP;
			targetFR = MAX_WHEEL_DROOP;
			targetRL = MAX_WHEEL_DROOP;
			targetRR = MAX_WHEEL_DROOP;
		} else {
			targetFL = this.suspensionFL;
			targetFR = this.suspensionFR;
			targetRL = this.suspensionRL;
			targetRR = this.suspensionRR;
		}

		if (!validContact && grounded) {
			targetPitch = this.pitch;
			targetRoll = this.roll;
		}

		targetFL = limitChange(this.suspensionFL, targetFL, 0.12F);
		targetFR = limitChange(this.suspensionFR, targetFR, 0.12F);
		targetRL = limitChange(this.suspensionRL, targetRL, 0.12F);
		targetRR = limitChange(this.suspensionRR, targetRR, 0.12F);

		if (Math.abs(targetPitch - this.pitch) < 0.5F) {
			targetPitch = this.pitch;
		}
		if (Math.abs(targetRoll - this.roll) < 0.5F) {
			targetRoll = this.roll;
		}
		if (Math.abs(targetFL - this.suspensionFL) < 0.01F) {
			targetFL = this.suspensionFL;
		}
		if (Math.abs(targetFR - this.suspensionFR) < 0.01F) {
			targetFR = this.suspensionFR;
		}
		if (Math.abs(targetRL - this.suspensionRL) < 0.01F) {
			targetRL = this.suspensionRL;
		}
		if (Math.abs(targetRR - this.suspensionRR) < 0.01F) {
			targetRR = this.suspensionRR;
		}

		this.pitch = Mth.lerp(0.08F, this.pitch, Mth.clamp(targetPitch, -MAX_TERRAIN_TILT, MAX_TERRAIN_TILT));
		this.roll = Mth.lerp(0.08F, this.roll, Mth.clamp(targetRoll, -MAX_TERRAIN_TILT, MAX_TERRAIN_TILT));
		this.suspensionFL = Mth.lerp(0.35F, this.suspensionFL, targetFL);
		this.suspensionFR = Mth.lerp(0.35F, this.suspensionFR, targetFR);
		this.suspensionRL = Mth.lerp(0.35F, this.suspensionRL, targetRL);
		this.suspensionRR = Mth.lerp(0.35F, this.suspensionRR, targetRR);
	}

	private static double smoothGround(double previous, double sample) {
		if (Double.isNaN(sample)) {
			return previous;
		}
		if (Double.isNaN(previous)) {
			return sample;
		}
		return Mth.lerp(0.15F, previous, sample);
	}

	private static float limitChange(float current, float target, float maxStep) {
		return current + Mth.clamp(target - current, -maxStep, maxStep);
	}

	private double sampleGround(double x, double z, double baseY) {
		Vec3 from = new Vec3(x, baseY + 1.0D, z);
		Vec3 to = new Vec3(x, baseY - 3.0D, z);
		BlockHitResult hit = this.level().clip(new ClipContext(
				from,
				to,
				ClipContext.Block.COLLIDER,
				ClipContext.Fluid.NONE,
				this));
		return hit.getType() == HitResult.Type.MISS ? Double.NaN : hit.getLocation().y;
	}

	private static String encodeLoadout(KartLoadout loadout) {
		StringBuilder builder = new StringBuilder();
		for (ComponentSlot slot : ComponentSlot.values()) {
			Identifier id = loadout.get(slot);
			if (id == null) {
				continue;
			}
			if (!builder.isEmpty()) {
				builder.append(',');
			}
			builder.append(slot.serializedName()).append('=').append(id);
		}
		return builder.toString();
	}

	private static KartLoadout decodeLoadout(String raw) {
		KartLoadout loadout = new KartLoadout();
		if (raw == null || raw.isBlank()) {
			return loadout;
		}

		for (String token : raw.split(",")) {
			int separator = token.indexOf('=');
			if (separator <= 0 || separator >= token.length() - 1) {
				continue;
			}

			ComponentSlot slot = ComponentSlot.byName(token.substring(0, separator));
			Identifier id = Identifier.tryParse(token.substring(separator + 1));
			if (slot == null || id == null) {
				continue;
			}

			Optional<ComponentDefinition> definition = KartComponents.byId(id);
			if (definition.isPresent() && slot.accepts(definition.get().type())) {
				loadout.set(slot, id);
			}
		}
		return loadout;
	}

	private void recalculateStats() {
		this.stats = KartStats.derive(this.loadout);
		if (this.stats.boostForce() <= 0.0F) {
			this.heat = 0.0F;
			this.overheatTicks = 0;
			this.boosting = false;
			this.overheated = false;
		}
	}

	public float getSteering() {
		return this.steering;
	}

	public double getWheelRotation() {
		return this.wheelRotation;
	}

	public boolean isDrifting() {
		return this.drifting;
	}

	public float getDriftStrength() {
		return this.driftStrength;
	}

	public float getForwardSpeed() {
		return this.forwardSpeed;
	}

	public float getLateralSpeed() {
		return this.lateralSpeed;
	}

	public float getSlipAngle() {
		return this.slipAngle;
	}

	public float getHeat() {
		return this.heat;
	}

	public boolean isBoosting() {
		return this.boosting;
	}

	public boolean isOverheated() {
		return this.overheated;
	}

	public boolean hasBooster() {
		return this.entityData.get(DATA_HAS_BOOSTER);
	}

	private static double wrapRadians(double radians) {
		double twoPi = Math.PI * 2.0;
		double wrapped = radians % twoPi;
		return wrapped < 0.0 ? wrapped + twoPi : wrapped;
	}
}
